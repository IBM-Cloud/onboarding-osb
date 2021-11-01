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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.cloud.service.broker.model.MeteringPayload;
import com.ibm.cloud.service.broker.service.UsageService;

@RestController
@RequestMapping(path = "/")
public class UsageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsageController.class);
    
    @Autowired
    UsageService usageService;
    
    
    @PostMapping(path="metering/{resourceId}/usage", produces = "application/json")
    public ResponseEntity<String> usage(@PathVariable("resourceId") String resourceId, @RequestBody MeteringPayload meteringPayload)
           throws Exception {
        LOGGER.info("Request received: POST /usage request with reourceId: {} payload: {}", resourceId, meteringPayload.toString());
        String resp = usageService.sendUsageData(resourceId, meteringPayload);
        return ResponseEntity.ok(resp);
    }

}
