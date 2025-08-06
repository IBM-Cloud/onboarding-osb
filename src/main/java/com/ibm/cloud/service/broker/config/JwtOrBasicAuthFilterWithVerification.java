/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                 */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2021                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* **************************************************************** */

package com.ibm.cloud.service.broker.config;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtOrBasicAuthFilterWithVerification extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtOrBasicAuthFilterWithVerification.class);

    private final List<String> allowedIds;

    private final JSONArray publicKeys;

    // Constructor to initialize allowed IDs and load public keys
    public JwtOrBasicAuthFilterWithVerification(List<String> allowedIds) {
        this.allowedIds = allowedIds;
        this.publicKeys = loadPublicKeys();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        // Retrieve the Authorization header from the request
        String authHeader = request.getHeader("Authorization");
        LOGGER.debug("authHeader = " + authHeader);

        // Check if the Authorization header contains a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // Extract the JWT token from the Authorization header
                String token = authHeader.substring(7);

                // Split the JWT token into its three parts: header, payload, and signature
                String[] parts = token.split("\\.");
                if (parts.length != 3) {
                    LOGGER.debug("Invalid JWT structure");
                    throw new IllegalArgumentException("Invalid JWT structure");
                }

                // Decode and parse the JWT header
                String header = parts[0];
                String headerStr = new String(Base64.getDecoder().decode(header), StandardCharsets.UTF_8);
                JSONObject headerObj = new JSONObject(headerStr);

                // Retrieve the public key using the "kid" and "alg" from the header
                JSONObject publicKeyJson = getPublicKey(headerObj.getString("kid"), headerObj.getString("alg"));
                PublicKey publicKey = getPublicKey(publicKeyJson);

                // Extract the payload and signature from the JWT
                String payload = parts[1];
                String signature = parts[2];

                // Reconstruct the signed data (header + payload)
                String signedData = header + "." + payload;

                // Verify the JWT signature using the public key
                if (!verifySignature(signedData, signature, publicKey)) {
                    LOGGER.debug("Invalid JWT signature");
                    throw new SecurityException("Invalid JWT signature");
                } else {
                    LOGGER.debug("JWT signature verified successfully");
                }

                // Decode and parse the JWT payload
                String payloadJson = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                JSONObject payloadObj = new JSONObject(payloadJson);

                // Extract the "id" field from the payload
                String jwtId = payloadObj.optString("id");
                LOGGER.debug("jwtId = " + jwtId);

                // Check if the extracted ID is in the list of allowed IDs
                if (!allowedIds.contains(jwtId)) {
                    LOGGER.debug("JWT ID does not match any of the expected CRNs List: " + allowedIds);
                    throw new SecurityException("Invalid JWT");
                }

                // Set the authentication context with the extracted ID
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    jwtId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception ex) {
                // Handle any exceptions during JWT validation
                LOGGER.debug("JWT validation failed:" + ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"JWT validation failed: " + ex.getMessage() + "\"}");
                return;
            }
        }

        // If no JWT, let httpBasic handle it
        filterChain.doFilter(request, response);
    }

    /**
     * Verifies the signature of the given data using the provided public key.
     *
     * @param data The data to verify.
     * @param signatureBase64Url The signature in Base64 URL-encoded format.
     * @param publicKey The public key to use for verification.
     * @return true if the signature is valid, false otherwise.
     * @throws Exception if an error occurs during signature verification.
     */
    private boolean verifySignature(String data, String signatureBase64Url, PublicKey publicKey) throws Exception {
        // Decode the Base64 URL-encoded signature
        byte[] signatureBytes = Base64.getUrlDecoder().decode(signatureBase64Url);

        // Initialize the signature verifier with the public key
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);

        // Update the verifier with the data
        verifier.update(data.getBytes(StandardCharsets.UTF_8));

        // Verify the signature
        return verifier.verify(signatureBytes);
    }

    /**
     * Loads the public keys from the IAM service.
     *
     * @return A JSONArray containing the public keys.
     */
    private JSONArray loadPublicKeys() {
        try {
            LOGGER.debug("Loading public keys from IAM");
            String url = "https://iam.cloud.ibm.com/identity/keys";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String jsonString = response.body();
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray keysArray = jsonObject.getJSONArray("keys");
            if (keysArray.length() == 0) {
                throw new IllegalStateException("No public keys found in the response");
            }
            return keysArray;
        } catch (Exception e) {
            LOGGER.error("Failed to load public keys from IAM: " + e.getMessage(), e);
            throw new RuntimeException("Failed to load public keys from IAM", e);
        }
    }

    /**
     * Retrieves the public key JSON object based on the key ID (kid) and algorithm (alg).
     *
     * @param kid The key ID.
     * @param alg The algorithm.
     * @return The matching public key JSON object.
     */
    private JSONObject getPublicKey(String kid, String alg) {
        LOGGER.debug("Searching for public key with kid: " + kid + " and alg: " + alg);
        for (int i = 0; i < publicKeys.length(); i++) {
            JSONObject key = publicKeys.getJSONObject(i);
            if (key.getString("kid").equals(kid) && key.getString("alg").equals(alg)) {
                LOGGER.debug("Found matching public key: " + key);
                // Return the first matching key
                return key;
            }
        }
        throw new IllegalArgumentException("No matching public key found for kid: " + kid + " and alg: " + alg);
    }

    /**
     * Converts a JSON Web Key (JWK) to a PublicKey object.
     *
     * @param jwk The JSON Web Key.
     * @return The corresponding PublicKey object.
     * @throws Exception if an error occurs during key conversion.
     */
    public PublicKey getPublicKey(JSONObject jwk) throws Exception {

        if (!"RSA".equals(jwk.getString("kty"))) {
            throw new IllegalArgumentException("Only RSA keys are supported in this example.");
        }

        // Base64URL decode 'n' and 'e'
        Base64.Decoder urlDecoder = Base64.getUrlDecoder();
        BigInteger modulus = new BigInteger(1, urlDecoder.decode(jwk.getString("n")));
        BigInteger exponent = new BigInteger(1, urlDecoder.decode(jwk.getString("e")));

        // Build PublicKey
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return publicKey;
    }
}
