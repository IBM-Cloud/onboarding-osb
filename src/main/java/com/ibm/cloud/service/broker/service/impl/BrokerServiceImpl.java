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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.service.broker.db.ServiceInstance;
import com.ibm.cloud.service.broker.db.ServiceInstanceRepository;
import com.ibm.cloud.service.broker.enums.OperationState;
import com.ibm.cloud.service.broker.enums.ServiceInstatanceStatus;
import com.ibm.cloud.service.broker.model.Catalog;
import com.ibm.cloud.service.broker.model.CreateServiceInstanceRequest;
import com.ibm.cloud.service.broker.model.Plan;
import com.ibm.cloud.service.broker.model.ServiceDefinition;
import com.ibm.cloud.service.broker.model.UpdateStateRequest;
import com.ibm.cloud.service.broker.response.ServiceInstanceStateResponse;
import com.ibm.cloud.service.broker.service.BrokerService;
import com.ibm.cloud.service.broker.util.BrokerUtil;
import com.ibm.cloud.service.broker.util.CatalogUtil;


@Service
public class BrokerServiceImpl implements BrokerService {
    
    @Value("${dashboard.url}")
    private String dashboardUrl;
    
    @Autowired
    Catalog catalog;
    
    @Autowired
    ApplicationContext applicationContext;
    
    @Autowired
    ServiceInstanceRepository serviceInstanceRepository;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServiceImpl.class);
    
    private static final String INSTANCE_STATE = "state";
    
    private static final String PROVISION_STATUS_API = "/provision_status/";

    @Override
    public ResponseEntity<String> provision(String instanceId, JsonNode json, String iamId, String region) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        HttpStatus status = HttpStatus.CREATED;
        Map<String, Object> response = new HashMap<>();

        CreateServiceInstanceRequest createServiceRequest = mapper.treeToValue(json, CreateServiceInstanceRequest.class);
        createServiceRequest.setInstanceId(instanceId);

        if (createServiceRequest.getContext() != null && BrokerUtil.IBM_CLOUD.equals(createServiceRequest.getContext().getPlatform())) {
            Plan plan = CatalogUtil.getPlan(catalog, createServiceRequest.getServiceId(), createServiceRequest.getPlanId());
            if (plan != null) {
                ServiceInstance serviceInstance = getServiceInstanceEntity(createServiceRequest, iamId, region);
                serviceInstanceRepository.save(serviceInstance);
                LOGGER.info("Service Instance created: instanceId: {} status: {} planId: {}", instanceId,
                        serviceInstance.getStatus(), plan.getId());
                response.put(BrokerUtil.DASHBOARD_URL, dashboardUrl + PROVISION_STATUS_API + instanceId);
            } else {
                LOGGER.error("Plan id:{} does not belong to this service: {}", createServiceRequest.getPlanId(), createServiceRequest.getServiceId());
                return ResponseEntity.status(422).body("Invalid plan id: " + createServiceRequest.getPlanId());
            }

        } else {
            LOGGER.error("Unidentified platform: {}", createServiceRequest.getContext().getPlatform());
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            response.put("description", "Invalid platform: " + createServiceRequest.getContext().getPlatform());
            response.put(BrokerUtil.ERROR, "Invalid platform: " + createServiceRequest.getContext().getPlatform());
            return ResponseEntity.status(422).body("Invalid platform: " + createServiceRequest.getContext().getPlatform());
        }
        String strResponse = mapper.writeValueAsString(response);

        return ResponseEntity.status(status).body(strResponse);
    }

    

    @Override
    public ResponseEntity<String> deprovision(String instanceId, String planId, String serviceId, String iamId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> response = new HashMap<>();
        response.put("description", "Deprovisioned");
        String strResponse = mapper.writeValueAsString(response);
        return ResponseEntity.status(HttpStatus.OK).body(strResponse);
    }

    @Override
    public ResponseEntity<String> update(String instanceId, JsonNode json, String iamId, String region) throws Exception {
        // check if service supports plan update 

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @Override
    public ResponseEntity<String> lastOperation(String instanceId, String iamId) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        response.put(INSTANCE_STATE, OperationState.SUCCEEDED.getValue());
        String strResponse = mapper.writeValueAsString(response);
        return ResponseEntity.status(HttpStatus.OK).body(strResponse);
    }

    @Override
    public ResponseEntity<String> importCatalog(MultipartFile file) throws JsonParseException, JsonMappingException, IOException {

        Reader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

        ObjectMapper mapper = new ObjectMapper();

        JsonNode catalogJson = mapper.readValue(reader, JsonNode.class);
        ServiceDefinition service = mapper.treeToValue(catalogJson, ServiceDefinition.class);
        Catalog importedCatalog = new Catalog(Arrays.asList(service));

        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) context.getBeanFactory();
        registry.destroySingleton("getCatalog"); //destroys the bean object
        registry.registerSingleton("getCatalog", importedCatalog); //add to singleton beans cache
        LOGGER.info("Imported catalog: {}", importedCatalog);
        return ResponseEntity.ok(mapper.writeValueAsString(catalogJson));
    }

    @Override
    public ResponseEntity<String> updateState(String instanceId, JsonNode json, String iamId) throws JsonProcessingException {
        // get service instance for given id
        //ServiceInstance serviceInstance = validateInstanceId(instanceId);
        ObjectMapper mapper = new ObjectMapper();
        UpdateStateRequest updateStateRequest = mapper.treeToValue(json, UpdateStateRequest.class);
        /*if (serviceInstance != null) {
            // disable instance
            if (!updateStateRequest.getEnabled()) {
                
                if (ServiceInstatanceStatus.ACTIVE.name().equals(serviceInstance.getStatus())
                        && serviceInstance.getEnabled()) {
                    serviceInstance.setEnabled(false);
                    serviceInstance.setStatus(ServiceInstatanceStatus.DISABLED.name());
                    serviceInstance.setUpdateDate(Instant.now());
                    serviceInstanceRepository.save(serviceInstance);
                }
            } else {
                // enable instance
                if (ServiceInstatanceStatus.DISABLED.name().equals(serviceInstance.getStatus())
                        && !serviceInstance.getEnabled()) {
                    serviceInstance.setEnabled(true);
                    serviceInstance.setStatus(ServiceInstatanceStatus.ACTIVE.name());
                    serviceInstance.setUpdateDate(Instant.now());
                    serviceInstanceRepository.save(serviceInstance);
                }
            }
            //TODO: create workflow for this state change
            ServiceInstanceStateResponse response = mapResponse(serviceInstance);
            return ResponseEntity.ok(mapper.writeValueAsString(response));
        }
        LOGGER.error("Update service instance state failed as Service inatsnce not found with id: {}", instanceId);
        *///return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Service inatsnce not found with id: " + instanceId);
        //return temp response as success
        ServiceInstanceStateResponse resp = new ServiceInstanceStateResponse();
        resp.setActive(updateStateRequest.getEnabled());
        resp.setEnabled(updateStateRequest.getEnabled());

        return ResponseEntity.ok(mapper.writeValueAsString(resp));
    }

    @Override
    public ResponseEntity<String> getState(String instanceId, String iamId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        // get service instance for given id
        /*ServiceInstance serviceInstance = validateInstanceId(instanceId);
        
        if (serviceInstance != null) {
            ServiceInstanceStateResponse response = mapResponse(serviceInstance);
            return ResponseEntity.ok(mapper.writeValueAsString(response));
        }
        LOGGER.error("Get service instance state failed as Service inatsnce not found with id: {}", instanceId);
        *///return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Service inatsnce not found with id: " + instanceId);
        //return temp response as success
        ServiceInstanceStateResponse resp = new ServiceInstanceStateResponse();
        resp.setActive(false);
        resp.setEnabled(false);

        return ResponseEntity.ok(mapper.writeValueAsString(resp));
    }


    /*private ServiceInstance validateInstanceId(String instanceId) {
        
        List<ServiceInstance> instanceList = serviceInstanceRepository.findByInstanceIdAndStatusNotIn(instanceId,
                Arrays.asList(ServiceInstatanceStatus.DEPROVISIONED.name()));

        if (instanceList != null && !instanceList.isEmpty()) {
            return instanceList.get(0);
        }
        return null;
    }*/

    /*private ServiceInstanceStateResponse mapResponse(ServiceInstance serviceInstance) {
        ServiceInstanceStateResponse response = new ServiceInstanceStateResponse();
        response.setActive(ServiceInstatanceStatus.ACTIVE.name().equals(serviceInstance.getStatus()) ? true : false);
        response.setEnabled(serviceInstance.getEnabled()==null ? response.getActive() : serviceInstance.getEnabled());
        response.setLastActive(serviceInstance.getUpdateDate().getEpochSecond());
        return response;
    }*/

    private ServiceInstance getServiceInstanceEntity(CreateServiceInstanceRequest request, String iamId, String region) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ServiceInstance instance = new ServiceInstance();
        instance.setInstanceId(request.getInstanceId());
        instance.setName((request.getContext() != null)?request.getContext().getName():null);
        instance.setServiceId(request.getServiceId());
        instance.setPlanId(request.getPlanId());
        instance.setIamId(iamId);
        instance.setRegion(region);
        instance.setContext(mapper.writeValueAsString(request.getContext()));
        instance.setParameters(mapper.writeValueAsString(request.getParameters()));
        instance.setStatus(ServiceInstatanceStatus.ACTIVE.name());
        instance.setEnabled(true);
        
        instance.setCreateDate(Instant.now());
        instance.setUpdateDate(Instant.now());
        //TODO: isMetered
        return instance;
    }

    // display service instances on broker home page in html table format
    public StringBuilder getServiceInstances() throws Exception {
        List<ServiceInstance> instances = serviceInstanceRepository.findAll();
        StringBuilder instanceTable = new StringBuilder();
        if (!CollectionUtils.isEmpty(instances)) {
            instanceTable.append("<br/><br/><b>Instance Details:</b>")
            .append("<style>table, th, td {border: 1px solid black;} th, td {  padding: 10px;  }</style>")
            .append("<table border=1><tr><th>#</th><th>Instance Id</th><th>Status</th><th>Created on</th></tr>");
            int i = 0;
            for (ServiceInstance instance: instances) {
                instanceTable.append("<tr><td>").append(++i).append("</td>");
                instanceTable.append("<td>").append(instance.getInstanceId()).append("</td>");
                instanceTable.append("<td>").append(instance.getStatus()).append("</td>");
                instanceTable.append("<td>").append(instance.getCreateDate()).append("</td></tr>");
            }
            instanceTable.append("</table>");
        }
        return instanceTable;
    }

}
