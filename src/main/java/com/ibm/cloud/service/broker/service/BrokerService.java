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

package com.ibm.cloud.service.broker.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.cloud.service.broker.model.InstanceDto;

public interface BrokerService {

    
    ResponseEntity<String> provision(String instanceId, JsonNode json, String iamId, String region) throws Exception;
    
    ResponseEntity<String> deprovision(String instanceId, String planId, String serviceId, String iamId) throws Exception;
    
    ResponseEntity<String> update(String instanceId, JsonNode json, String iamId, String region) throws Exception;
    
    ResponseEntity<String> lastOperation(String instanceId, String iamId) throws JsonProcessingException;
    
    ResponseEntity<String> importCatalog(MultipartFile file) throws JsonParseException, JsonMappingException, IOException;
    
    ResponseEntity<String> updateState(String instanceId, JsonNode json, String iamId) throws JsonProcessingException;
    
    ResponseEntity<String> getState(String instanceId, String iamId) throws JsonProcessingException;
    
    List<InstanceDto> getServiceInstances() throws Exception;
}
