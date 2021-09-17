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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;


@Component
public class MessageSourceUtil {
    @Autowired
    private MessageSource messageSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSourceUtil.class);
    /**
     * Get proper text for given property substituting params using properties file.
     *
     * @param property property to be searched in properties file.
     * @param params   params that can be substituted in the returned text.
     * @return text corresponding to property after substituting passed params.
     */
    public String getText(String property, String... params) {
        try {
            return messageSource.getMessage(property, params, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            LOGGER.error("Could not find message for property: " + property, e);
        }
        return property;
    }
}
