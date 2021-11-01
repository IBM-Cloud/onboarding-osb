/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                 */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2020                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* **************************************************************** */

package com.ibm.cloud.service.broker.exception;


public class BaseException extends RuntimeException {
    private final String code;
    private final String[] params;

    public BaseException(String code, String... params) {
        super(code);
        this.code = code;
        this.params = params;
    }

	public String getCode() {
		return code;
	}

	public String[] getParams() {
		return params;
	}
    
}
