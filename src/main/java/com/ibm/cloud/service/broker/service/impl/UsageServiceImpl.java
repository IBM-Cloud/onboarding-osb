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

package com.ibm.cloud.service.broker.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.service.broker.db.ServiceInstanceRepository;
import com.ibm.cloud.service.broker.exception.BadRequestException;
import com.ibm.cloud.service.broker.exception.NotFoundException;
import com.ibm.cloud.service.broker.exception.ServerException;
import com.ibm.cloud.service.broker.model.MeteringPayload;
import com.ibm.cloud.service.broker.service.UsageService;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UsageServiceImpl implements UsageService {

  @Value("${usage.endpoint}")
  private String usageEndpoint;

  @Value("${iam.endpoint}")
  private String iamEndpoint;

  @Value("${metering.api.key}")
  private String apiKey;

  private static final Logger LOGGER = LoggerFactory.getLogger(
    UsageServiceImpl.class
  );

  private static final String USAGE_API_PATH =
    "/v4/metering/resources/{resource_id}/usage";

  // iam access token
  private static final String IAM_IDENTITY_TOKEN_PATH = "/identity/token";
  private static final String IAM_GRANT_TYPE =
    "grant_type=urn%3Aibm%3Aparams%3Aoauth%3Agrant-type%3Aapikey&apikey=";

  @Autowired
  ServiceInstanceRepository serviceInstanceRepository;

  /**
   * send usage data, impementation based on service used.
   * @throws Exception
   */
  @Override
  public String sendUsageData(
    String resourceId,
    MeteringPayload meteringPayload
  )
    throws Exception {
    if (meteringPayload.getStart() == 0) {
      Instant instant = Instant.now();
      meteringPayload.setStart(
        instant.minus(1L, ChronoUnit.HOURS).toEpochMilli()
      );
    }
    if (meteringPayload.getEnd() == 0) {
      Instant instant = Instant.now();
      meteringPayload.setEnd(instant.toEpochMilli());
    }
    String iamAccessToken = getIamAccessToken();
    String usageApiUrl = usageEndpoint.concat(
      USAGE_API_PATH.replace("{resource_id}", resourceId)
    );
    ObjectMapper mapper = new ObjectMapper();
    List<MeteringPayload> meteringPayloadList = new ArrayList<>();
    meteringPayloadList.add(meteringPayload);
    return sendUsageData(
      usageApiUrl,
      iamAccessToken,
      mapper.writeValueAsString(meteringPayloadList),
      MediaType.APPLICATION_JSON,
      mapper
    );
  }

  private String sendUsageData(
    String usageApiUrl,
    String iamAccessToken,
    String data,
    MediaType mediaType,
    ObjectMapper mapper
  )
    throws IOException {
    LOGGER.info("Sending USAGE data: {}", data);
    ResponseEntity<JsonNode> response = invokeUsageEndpoint(
      usageApiUrl,
      "Bearer ".concat(iamAccessToken),
      data,
      mediaType,
      HttpMethod.POST
    );
    LOGGER.info("Usage Metering response: {}", response.getBody());
    if (response.getStatusCode() == HttpStatus.ACCEPTED) {
      JsonNode responseJson = response.getBody().get("resources");
      if (responseJson.isArray()) {
        responseJson.forEach(
          resp -> {
            if (resp.has("status") && 201 != resp.get("status").asInt()) {
              try {
                LOGGER.error(
                  "ALERT: Error response from Metering Usage API: {}",
                  mapper.writeValueAsString(resp)
                );
              } catch (JsonProcessingException e) {
                LOGGER.error("Error while logging response from Usage API", e);
              }
            }
          }
        );
      }
      return responseJson.toString();
    } else {
      LOGGER.error(
        "Error while sending USAGE data: response status code: {} response body: {}",
        response.getStatusCode(),
        response.getBody()
      );
      return response.getBody().toString();
    }
  }

  /**
   * call to submit usage data, impementation based on service used.
   * @throws Exception
   */
  private ResponseEntity<JsonNode> invokeUsageEndpoint(
    String endpoint,
    String auth,
    String data,
    MediaType mediaType,
    HttpMethod httpMethod
  ) {
    HttpHeaders headers = new HttpHeaders();
    if (auth != null) {
      headers.add("Authorization", auth);
    }
    headers.setContentType(mediaType);

    HttpEntity<String> entity = new HttpEntity<>(data, headers);
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(
      endpoint
    );
    LOGGER.info("Invoking POST API: {}", uriBuilder.toUriString());
    LOGGER.info("with body and headers: {}", entity);
    ResponseEntity<JsonNode> response = null;
    RestTemplate restTemplate = getRestTemplate();
    try {
      response =
        restTemplate.exchange(
          uriBuilder.toUriString(),
          httpMethod,
          entity,
          new ParameterizedTypeReference<JsonNode>() {}
        );
    } catch (HttpStatusCodeException e) {
      LOGGER.error(
        "HttpStatusCodeException while invoking API url: {}  Exception: ",
        uriBuilder.toUriString(),
        e
      );
      handleException(e, endpoint);
    }
    if (response != null) {
      LOGGER.info(
        "API response code: {}  response body: {}",
        response.getStatusCode(),
        response.getBody()
      );
      checkIfValidResponse(response.getStatusCode(), endpoint);
    }
    return response;
  }

  /**
   * verify response after usage call.
   * @throws Exception
   */
  private void checkIfValidResponse(HttpStatus httpStatus, String endpointUri) {
    switch (httpStatus) {
      case CREATED:
        // do nothing as it's valid
        break;
      case OK:
        // do nothing as it's valid
        break;
      case ACCEPTED:
        // do nothing as it's valid
        break;
      case BAD_REQUEST:
        throw new BadRequestException("E_BAD_REQUEST " + endpointUri);
      default:
        throw new ServerException("E_SERVER_ERROR " + endpointUri);
    }
  }

  private void handleException(HttpStatusCodeException e, String endpointUri) {
    if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
      throw new NotFoundException("E_NOT_FOUND " + endpointUri);
    } else if (e.getStatusCode().is4xxClientError()) {
      throw new BadRequestException("E_BAD_REQ " + endpointUri);
    }
    LOGGER.error(
      "ALERT: REST Invocation error in a non 4xx : {}",
      e.getMessage()
    );
  }

  RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  /**
   * IAM login
   * @throws Exception
   */
  private String getIamAccessToken() throws IOException {
    ResponseEntity<JsonNode> response = invokeUsageEndpoint(
      iamEndpoint.concat(IAM_IDENTITY_TOKEN_PATH),
      null,
      IAM_GRANT_TYPE.concat(apiKey),
      MediaType.APPLICATION_FORM_URLENCODED,
      HttpMethod.POST
    );
    //JsonNode responseNode = new ObjectMapper().readTree(response.getBody());

    if (response.getBody() != null && response.getBody().has("access_token")) {
      return response.getBody().get("access_token").asText();
    }
    return null;
  }
}
