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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Context {

    private String name;
    private String crn;
    private String platform;
    
    @JsonProperty("account_id")
    private String accountId;
    
    @JsonProperty("resource_group_crn")
    private String resourceGroupCrn;
    
    @JsonProperty("target_crn")
    private String targetCrn;
    
    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAccountId() {
        return accountId;
    }
    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    public String getResourceGroupCrn() {
        return resourceGroupCrn;
    }
    
    public void setResourceGroupCrn(String resourceGroupCrn) {
        this.resourceGroupCrn = resourceGroupCrn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public String getTargetCrn() {
        return targetCrn;
    }

    public void setTargetCrn(String targetCrn) {
        this.targetCrn = targetCrn;
    }
    
}
