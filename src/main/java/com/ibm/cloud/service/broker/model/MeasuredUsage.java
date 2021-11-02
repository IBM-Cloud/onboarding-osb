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
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasuredUsage {

    private String measure;
    private long quantity;

    public String getMeasure() {
        return measure;
    }
    
    public void setMeasure(String measure) {
        this.measure = measure;
    }
    
    public long getQuantity() {
        return quantity;
    }
    
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "MeasuredUsage{" +
                "measure='" + measure + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
