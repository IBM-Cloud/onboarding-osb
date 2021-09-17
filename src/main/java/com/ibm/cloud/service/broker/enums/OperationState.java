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

package com.ibm.cloud.service.broker.enums;

public enum OperationState {
    SUCCEEDED("succeeded"), FAILED("failed"), IN_PROGRESS("in progress");
    
    private final String state;

    OperationState(String state) {
        this.state = state;
    }
    
    public String getValue() {
        return state;
    }
    
    @Override
    public String toString() {
        return state;
    }
}
