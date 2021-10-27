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

package com.ibm.cloud.service.broker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.service.broker.model.Catalog;
import com.ibm.cloud.service.broker.model.InstanceDto;
import com.ibm.cloud.service.broker.model.Plan;
import com.ibm.cloud.service.broker.service.BrokerService;
import com.ibm.cloud.service.broker.util.BrokerUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/")
public class BrokerControler {

  @Autowired
  BrokerService brokerService;

  @Autowired
  Catalog catalog;

  @Value("${app.build.number}")
  private String buildNumber;

  private static final Logger LOGGER = LoggerFactory.getLogger(
    BrokerControler.class
  );

  /**
   * Import Catalog from RMC. Not invoked by IBM Cloud.
   * @param httpServletRequest http servlet request
   * @return
   * @throws Exception in case of error
   */
  @PutMapping(path = "v2/catalog") // TODO: path can be changed and separate access can be given for this api if needed.
  public ResponseEntity<String> importCatalog(
    final HttpServletRequest httpServletRequest,
    @RequestParam("file") MultipartFile file
  )
    throws Exception {
    LOGGER.info("PUT / request headers: " + headersString(httpServletRequest));
    return brokerService.importCatalog(file);
  }

  /**
   * Get catalog /v2/catalog , returns service and plans.
   * @param httpServletRequest request
   * @return catalog
   * @throws Exception in case of error.
   */
  @GetMapping(path = "v2/catalog", produces = "application/json")
  public ResponseEntity<String> catalog(
    final HttpServletRequest httpServletRequest
  )
    throws Exception { //JSONException, URISyntaxException
    LOGGER.info(
      "Request received: GET /v2/catalog request headers: " +
      headersString(httpServletRequest)
    );
    ObjectMapper mapper = new ObjectMapper();
    String result = mapper.writeValueAsString(catalog);
    LOGGER.info("Request completed: GET /v2/catalog");
    return ResponseEntity.ok(result);
  }

  /**
   * Create service instance.
   * @param httpServletRequest request
   * @param instanceId instance id
   * @param acceptsIncomplete accepts_incomplete
   * @param json input json
   * @return response
   * @throws Exception in case of error
   */
  @PutMapping("v2/service_instances/{instanceId}")
  public ResponseEntity<String> provision(
    final HttpServletRequest httpServletRequest,
    @PathVariable("instanceId") final String instanceId,
    @RequestParam(
      required = false,
      name = "accepts_incomplete"
    ) final boolean acceptsIncomplete,
    @RequestBody final JsonNode json
  )
    throws Exception {
    LOGGER.info(
      "Create Service Instance request received: PUT /v2/service_instances/" +
      instanceId +
      "  ?accepts_incomplete=" +
      acceptsIncomplete +
      "  request body: " +
      json
    );
    // LOGGER.debug(" request headers: " + headersString(httpServletRequest));

    ResponseEntity<String> response = brokerService.provision(
      instanceId,
      json,
      BrokerUtil.getIamId(httpServletRequest),
      BrokerUtil.getHeaderValue(
        httpServletRequest,
        BrokerUtil.BLUEMIX_REGION_HEADER
      )
    );
    LOGGER.info(
      "Create Service Instance Response status: {}, body: {}",
      response.getStatusCode(),
      response.getBody()
    );
    return response;
  }

  /**
   * IBM Cloud Enablement Extension: enable service instance
   * @throws IOException
   */
  @PutMapping("bluemix_v1/service_instances/{instanceId}")
  public ResponseEntity<String> updateState(
    final HttpServletRequest httpServletRequest,
    @PathVariable("instanceId") final String instanceId,
    @RequestBody final JsonNode json
  )
    throws IOException {
    LOGGER.info(
      "Update instance state request received: PUT /bluemix_v1/service_instances/" +
      instanceId +
      "request body: " +
      json
    );

    ResponseEntity<String> response = brokerService.updateState(
      instanceId,
      json,
      BrokerUtil.getIamId(httpServletRequest)
    );

    LOGGER.info(
      "Update instance state response status: {}, body: {}",
      response.getStatusCode(),
      response.getBody()
    );
    return response;
  }

  /**
   * IBM Cloud Enablement Extension: service instance state inquiry.
   * @throws IOException
   */
  @GetMapping("bluemix_v1/service_instances/{instanceId}")
  public ResponseEntity<String> state(
    final HttpServletRequest httpServletRequest,
    @PathVariable("instanceId") final String instanceId
  )
    throws IOException {
    LOGGER.info(
      "Get instance state request received: GET /bluemix_v1/service_instances/" +
      instanceId
    );
    // LOGGER.debug(" request headers: " + headersString(httpServletRequest));
    //validateAuthorization(httpServletRequest);

    // TODO - Do your actual work here
    ResponseEntity<String> response = brokerService.getState(
      instanceId,
      BrokerUtil.getIamId(httpServletRequest)
    );
    LOGGER.info(
      "Get instance state response status: {}, body: {}",
      response.getStatusCode(),
      response.getBody()
    );
    return response;
  }

  private String headersString(final HttpServletRequest request) {
    final Map<String, List<String>> headerMap = new TreeMap<String, List<String>>();

    final Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      final List<String> headerValueList = new ArrayList<String>();

      final String headerName = headerNames.nextElement();

      final Enumeration<String> headerValues = request.getHeaders(headerName);
      while (headerValues.hasMoreElements()) {
        headerValueList.add(headerValues.nextElement());
      }

      headerMap.put(headerName, headerValueList);
    }

    return headerMap.toString();
  }

  @PutMapping("v2/service_instances/{instanceId}/service_bindings/{bindingId}")
  public ResponseEntity<String> bind(
    final HttpServletRequest httpServletRequest,
    @PathVariable("instanceId") final String instanceId,
    @PathVariable("bindingId") final String bindingId,
    final JsonNode json
  )
    throws Exception {
    return ResponseEntity.status(HttpStatus.OK).body("");
  }

  @DeleteMapping(
    "v2/service_instances/{instanceId}/service_bindings/{bindingId}"
  )
  public ResponseEntity<String> unbind(
    final HttpServletRequest httpServletRequest,
    @PathVariable("instanceId") final String instanceId,
    @PathVariable("bindingId") final String bindingId,
    @RequestParam("plan_id") final String planId,
    @RequestParam("service_id") final String serviceId
  )
    throws Exception {
    LOGGER.info(
      "DELETE /v2/service_instances/" +
      instanceId +
      "/service_bindings/" +
      bindingId +
      "?plan_id=" +
      planId +
      "&service_id=" +
      serviceId
    );

    // TODO - Do your actual work here

    return ResponseEntity.ok("");
  }

  /**
   * Deprovision/Delete given service instance.
   * @param httpServletRequest http request
   * @param instanceId the instance id
   * @param acceptsIncomplete accept_incomplete
   * @param planId plan id
   * @param serviceId service id
   * @return response
   * @throws Exception if any error
   */
  @DeleteMapping("v2/service_instances/{instanceId}")
  public ResponseEntity<String> deprovision(
    final HttpServletRequest httpServletRequest,
    @PathVariable("instanceId") final String instanceId,
    @RequestParam("accepts_incomplete") final boolean acceptsIncomplete,
    @RequestParam("plan_id") final String planId,
    @RequestParam("service_id") final String serviceId
  )
    throws Exception {
    LOGGER.info(
      "Deprovision Service Instance request received: DELETE /v2/service_instances/" +
      instanceId +
      "  ?accepts_incomplete=" +
      acceptsIncomplete +
      " &plan_id=" +
      planId +
      " &service_id=" +
      serviceId
    );
    ResponseEntity<String> response = brokerService.deprovision(
      instanceId,
      planId,
      serviceId,
      BrokerUtil.getIamId(httpServletRequest)
    );
    LOGGER.info(
      "Deprovision Service Instance Response status: {}, body: {}",
      response.getStatusCode(),
      response.getBody()
    );
    return response;
  }

  @PatchMapping("v2/service_instances/{instanceId}")
  public ResponseEntity<String> update(
    final HttpServletRequest httpServletRequest,
    @PathVariable("instanceId") final String instanceId,
    @RequestParam("accepts_incomplete") final boolean acceptsIncomplete,
    @RequestBody final JsonNode json
  )
    throws Exception {
    LOGGER.info(
      "Update Service Instance request received: PATCH /v2/service_instances/" +
      instanceId +
      " ?accepts_incomplete=" +
      acceptsIncomplete +
      " request body: " +
      json
    );

    return ResponseEntity.status(HttpStatus.OK).body("");
  }

  @GetMapping("v2/service_instances/{instanceId}/last_operation")
  public ResponseEntity<String> fetch(
    final HttpServletRequest httpServletRequest,
    @PathVariable("instanceId") final String instanceId,
    @RequestParam(required = false, name = "operation") final String operation,
    @RequestParam("plan_id") final String planId,
    @RequestParam("service_id") final String serviceId
  )
    throws Exception {
    LOGGER.info(
      "Get last_operation request received: GET /v2/service_instances/" +
      instanceId +
      "?operation=" +
      operation +
      "&plan_id=" +
      planId +
      "&service_id=" +
      serviceId
    );

    ResponseEntity<String> response = brokerService.lastOperation(
      instanceId,
      BrokerUtil.getIamId(httpServletRequest)
    );

    LOGGER.info(
      "last_operation Response status: {}, body: {}",
      response.getStatusCode(),
      response.getBody()
    );
    return response;
  }

  @GetMapping("provision_status")
  public ResponseEntity<String> getProvisionStatus() {
    return ResponseEntity
      .status(HttpStatus.OK)
      .body("Successfully Provisioned the Instance !!");
  }
}
