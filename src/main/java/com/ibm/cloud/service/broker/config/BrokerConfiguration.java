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

package com.ibm.cloud.service.broker.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.service.broker.model.Catalog;
import com.ibm.cloud.service.broker.model.ServiceDefinition;

@Configuration
public class BrokerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerConfiguration.class);

    @Bean
    public Catalog getCatalog() throws IOException {
        // load catalog from catalog.json
        try {
            JsonNode catalogJson = loadCatalog();
            ObjectMapper mapper = new ObjectMapper();
            LOGGER.info("Catalog loaded from catalog.json: {}", 
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(catalogJson));
            ServiceDefinition service = mapper.treeToValue(catalogJson, ServiceDefinition.class);//(catalogJson, ServiceDefinition.class);
            Catalog catalog = new Catalog(Arrays.asList(service));
            LOGGER.info("Catalog loaded from catalog.json: {}", mapper.writeValueAsString(catalog));
            //LOGGER.info("Catalog loaded from catalog.json: {}", catalog.toString());
            return catalog;
        } catch (IOException ioe) {
            LOGGER.error("Error while loading catalog data from data/catalog.json: ", ioe);
            throw ioe;
        }
    }

    private JsonNode loadCatalog() throws IOException {
        InputStream stream=Thread.currentThread().getContextClassLoader().getResourceAsStream("data/catalog.json");
        return new ObjectMapper().readValue(stream, JsonNode.class);
    }

}
