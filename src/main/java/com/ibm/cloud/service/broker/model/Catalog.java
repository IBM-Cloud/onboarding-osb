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

package com.ibm.cloud.service.broker.model;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Catalog {

    @NotEmpty
    @JsonProperty("services")
    private final List<ServiceDefinition> serviceDefinitions;

    public Catalog(List<ServiceDefinition> serviceDefinitions) {
        this.serviceDefinitions = serviceDefinitions;
    }

    public List<ServiceDefinition> getServiceDefinitions() {
        return serviceDefinitions;
    }

    @Override
    public String toString() {
        return "Catalog{" +
                "serviceDefinitions=" + serviceDefinitions +
                '}';
    }
}
