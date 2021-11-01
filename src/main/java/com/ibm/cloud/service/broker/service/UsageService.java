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

package com.ibm.cloud.service.broker.service;

import org.springframework.stereotype.Service;

import com.ibm.cloud.service.broker.model.MeteringPayload;

@Service
public interface UsageService {

    String sendUsageData(String resourceId, MeteringPayload meteringPayload) throws Exception;
}
