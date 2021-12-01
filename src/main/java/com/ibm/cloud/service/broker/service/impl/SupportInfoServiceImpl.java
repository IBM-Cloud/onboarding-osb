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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.service.broker.db.ServiceInstance;
import com.ibm.cloud.service.broker.db.ServiceInstanceRepository;
import com.ibm.cloud.service.broker.model.InstanceDto;
import com.ibm.cloud.service.broker.service.SupportInfoService;

@Service
public class SupportInfoServiceImpl implements SupportInfoService {

    private static final String BUILD_NUMBER = "BUILD_NUMBER";
    private static final String IAM_ENDPOINT = "IAM_ENDPOINT";
    private static final String USAGE_ENDPOINT = "USAGE_ENDPOINT";
    private static final String IS_METERING_API_KEY_SET = "IS_METERING_APIKEY_SET";
    private static final String BROKER_URL = "BROKER_URL";
    private static final String PC_URL = "PC_URL";


    @Value("${app.build.number}")
    private String buildNumber;
    @Value("${iam.endpoint}")
    private String iamEndpoint;
    @Value("${usage.endpoint}")
    private String usageEndpoint;
    @Value("${metering.api.key}")
    private String metringApikey;
    @Value("${broker.url}")
    private String brokerUrl;
    @Value("${partner.center.url}")
    private String partnerCenterUrl;


    @Autowired
    ServiceInstanceRepository serviceInstanceRepository;

    @Override
    public List<InstanceDto> getServiceInstances() throws Exception {
        List<ServiceInstance> instances = serviceInstanceRepository.findAll();
        List<InstanceDto> instanceDtos = null;
        if (instances != null && !instances.isEmpty()) {
            instanceDtos = new ArrayList<InstanceDto>();
            for (ServiceInstance instance: instances) {
                instanceDtos.add(mapToInstanceDto(instance));
            }
        }
        return instanceDtos;
    }

    @Override
    public String getMetadata() throws Exception {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put(BUILD_NUMBER, buildNumber);
        metadata.put(IAM_ENDPOINT, iamEndpoint);
        metadata.put(USAGE_ENDPOINT, usageEndpoint);
        metadata.put(BROKER_URL, brokerUrl);
        metadata.put(PC_URL, partnerCenterUrl);
        metadata.put(IS_METERING_API_KEY_SET, (!"".equals(metringApikey)) ? true : false);
        return new ObjectMapper().writeValueAsString(metadata);
    }

    private InstanceDto mapToInstanceDto(ServiceInstance ins) {
        InstanceDto dto = new InstanceDto();
        dto.setInstanceId(ins.getInstanceId());
        dto.setName(ins.getName());
        dto.setPlanId(ins.getPlanId());
        dto.setStatus(ins.getStatus());
        dto.setRegion(ins.getRegion());
        dto.setUpdateDate(ins.getUpdateDate());
        return dto;
    }

}
