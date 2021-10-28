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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.cloud.service.broker.model.InstanceDto;
import com.ibm.cloud.service.broker.service.SupportInfoService;
import com.ibm.cloud.service.broker.util.BrokerUtil;

@RestController
@RequestMapping(path = "/support")
public class SupportInfoController {

    @Autowired
    SupportInfoService supportInfoService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SupportInfoController.class);

    @GetMapping(path="/instances", produces = "application/json")
    public ResponseEntity<List<InstanceDto>> getInstances( final HttpServletRequest httpServletRequest)
           throws Exception {
        LOGGER.info("Request received: GET /support/instances request headers: " + BrokerUtil.headersString(httpServletRequest));
        List<InstanceDto> instances = supportInfoService.getServiceInstances();
        LOGGER.info("Request completed: GET /support/instances");
        return ResponseEntity.ok(instances);
    }


    @GetMapping(path="/metadata", produces = "application/json")
    public ResponseEntity<String> getMetadata( final HttpServletRequest httpServletRequest)
           throws Exception {
        LOGGER.info("Request received: GET /support/metadata request headers: " + BrokerUtil.headersString(httpServletRequest));
        String meta = supportInfoService.getMetadata();
        LOGGER.info("Request completed: GET /support/metadata");
        return ResponseEntity.ok(meta);
    }

}
