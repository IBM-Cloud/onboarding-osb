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

package com.ibm.cloud.service.broker.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BrokerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerUtil.class);
    
    public static final String PLATFORM = "platform";
    public static final String IBM_CLOUD = "ibmcloud";
    public static final String LITE_PLAN = "lite";
    public static final String ORGINATING_IDENTITY_HEADER = "x-broker-api-originating-identity";
    public static final String BLUEMIX_REGION_HEADER = "x-bluemix-region";
    
    public static final String DESCRIPTION = "description";
    public static final String ERROR = "error";
    public static final String DASHBOARD_URL  = "dashboard_url";
    
    public static final String IBM_IUID_PREFIX = "IBMid-";
    
    // test monitor instance
    public static final String TEST_INSTANCE_ID = "crnTest111";
    
    public static String getIamId(HttpServletRequest request) throws IOException {
        final String originatingIdentity = request.getHeader(ORGINATING_IDENTITY_HEADER);
        if (originatingIdentity != null)
        {
            final String[] strings = originatingIdentity.split(" ");
            String xx = new String(Base64.getDecoder().decode(strings[1]));
            JsonNode iam = new ObjectMapper().readTree(xx);
            if (iam != null && iam.has("iam_id")) {
                return iam.get("iam_id").asText();
            }
        }
        return null;
    }

    public static String getHeaderValue(HttpServletRequest request, String headerName) {
        return request.getHeader(headerName);
    }

    public static Object getParameter(Map<String, Object> parameters, String paramName) {
        if (parameters != null) {
            return parameters.get(paramName);
        }
        return null;
    }

    public static String loadTextFile(String filename) throws IOException {
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            byte[] bdata = FileCopyUtils.copyToByteArray(stream);
            return new String(bdata, StandardCharsets.UTF_8);

        } catch (IOException e) {
            LOGGER.error("IOException while loading file: {}", filename, e);
        }

        return null;
    }

    public static String headersString(final HttpServletRequest request)
    {
        final Map<String, List<String>> headerMap = new TreeMap<String, List<String>>();

        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            final List<String> headerValueList = new ArrayList<String>();

            final String headerName = headerNames.nextElement();

            final Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements())
            {
                headerValueList.add(headerValues.nextElement());
            }

            headerMap.put(headerName,
                          headerValueList);
        }

        return headerMap.toString();
    }
    
}
