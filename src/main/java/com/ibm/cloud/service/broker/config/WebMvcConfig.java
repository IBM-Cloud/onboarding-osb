/* *******************************************************************/
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/* © Copyright IBM Corp. 2021                                        */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* *******************************************************************/

package com.ibm.cloud.service.broker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;

@Configuration
class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    @Value("${cors.origin}")
    String corsOrigin;
  
    @Override
    public void addCorsMappings(CorsRegistry registry) {
      String[] allowedOrigins = corsOrigin.split(",");
      registry
        .addMapping("/**")
        .allowedOrigins(allowedOrigins)
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("Accept", "Content-Type", "Authorization", "x-xsrf-token")
        .exposedHeaders(HttpHeaders.AUTHORIZATION)
        .allowCredentials(true)
        .maxAge(500000);
    }

    @Bean
    public HttpFirewall defaultFireWall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

}
