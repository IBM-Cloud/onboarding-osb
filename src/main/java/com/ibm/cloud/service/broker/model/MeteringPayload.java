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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeteringPayload {

    private String planId;
    private String resourceInstanceId;
    private long start;
    private long end;
    private String region;
    private List<MeasuredUsage> measuredUsage;
    
    public String getPlanId() {
        return planId;
    }
    
    public void setPlanId(String planId) {
        this.planId = planId;
    }
    
    public String getResourceInstanceId() {
        return resourceInstanceId;
    }
    
    public void setResourceInstanceId(String instanceId) {
        this.resourceInstanceId = instanceId;
    }
    
    public long getStart() {
        return start;
    }
    
    public void setStart(long startTime) {
        this.start = startTime;
    }
    
    public long getEnd() {
        return end;
    }
    
    public void setEnd(long endTime) {
        this.end = endTime;
    }
    
    public List<MeasuredUsage> getMeasuredUsage() {
        return measuredUsage;
    }
    
    public void setMeasuredUsage(List<MeasuredUsage> measuredUsage) {
        this.measuredUsage = measuredUsage;
    }
    
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "MeteringPayload{" +
                "planId='" + planId + '\'' +
                ", instanceId='" + resourceInstanceId + '\'' +
                ", startTime='" + start + '\'' +
                ", endTime=" + end +
                ", MeasuredUsage=" + measuredUsage +
                '}';
    }
}
