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

package com.ibm.cloud.service.broker.db;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "SERVICE_INSTANCE")
public class ServiceInstance implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "INSTANCE_ID")
    private String instanceId;
    
    private String name;
    
    @Column(name = "IAM_ID")
    private String iamId;

    @Column(name = "PLAN_ID")
    private String planId;
    
    @Column(name = "SERVICE_ID")
    private String serviceId;
    
    private String status;
    
    private boolean enabled;
    
    private String region;
    
    @Column(length = 1024)
    private String context;
    
    private String parameters;
    
    @Column(name = "CREATE_DATE")
    private Instant createDate;
    
    @Column(name = "UPDATE_DATE")
    private Instant updateDate;

    public String getInstanceId() {
        return instanceId;
    }
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIamId() {
        return iamId;
    }
    
    public void setIamId(String iamId) {
        this.iamId = iamId;
    }
    
        public String getPlanId() {
        return planId;
    }
    
    public void setPlanId(String planId) {
        this.planId = planId;
    }
    
    public String getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public Instant getCreateDate() {
        return createDate;
    }
    
    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }
    
    public Instant getUpdateDate() {
        return updateDate;
    }
    
    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServiceInstance [instanceId=").append(instanceId).append(", name=").append(name).append(", iam_id=").append(iamId).append(", serviceId=").append(serviceId)
        .append(", planId=").append(planId).append(", status=").append(status).append(", enabled=").append(enabled).append(", region=").append(region).append(", context=").append(context)
        .append(", parameters=").append(parameters).append(", craeteDate=")
        .append(createDate).append(", updateDate=").append(updateDate).append("]");
        return builder.toString();
    }

}
