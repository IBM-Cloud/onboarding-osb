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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.service.broker.model.Catalog;
import com.ibm.cloud.service.broker.model.Plan;
import com.ibm.cloud.service.broker.model.ServiceDefinition;

public class CatalogUtil {

    public static final String COSTS = "costs";
    public static final String METERING_UNIT = "meteringUnit";
    public static final String CREDIT_PLAN = "virtualcoursecredit-subscription-plan";
    public static final String BASE_PLAN = "computinglab-base-plan";
    public static final String PREMIUM_PLAN = "computinglab-premium-plan";
    public static final String LITE_PLAN = "lite";

    public static Plan getPlan(Catalog catalog, String serviceId, String planId) {
        if (serviceId == null || planId == null) {
            return null;
        }
        for (ServiceDefinition service: catalog.getServiceDefinitions()) {
            if (serviceId.equals(service.getId())) {
                for(Plan plan: service.getPlans()) {
                    if (planId.equals(plan.getId())) {
                        return plan;
                    }
                }
            }
        }
        return null;
    }

    public static Map<String, List<String>> getMeteringUnits(Catalog catalog) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> catalogMeteringUnits = new HashMap<>();
        catalog.getServiceDefinitions().get(0).getPlans().forEach(plan -> {
            JsonNode planCosts = mapper.valueToTree(plan.getMetadata().get(COSTS));
            if (planCosts.isArray()) {
                List<String> planMeteringUnits = new ArrayList<>();
                planCosts.forEach(cost -> {
                    if (cost.has(METERING_UNIT)) {
                        planMeteringUnits.add(cost.get(METERING_UNIT).asText());
                    }
                });
                catalogMeteringUnits.put(plan.getId(), planMeteringUnits);
            }
        });
        return catalogMeteringUnits;
    }

}
