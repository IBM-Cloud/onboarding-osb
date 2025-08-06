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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;


@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${service.broker.allowedIds}")
    private List<String> allowedIds;

    @Value("${service.broker.user}")
    private String username;

    @Value("${service.broker.password}")
    private String pass;
    
    private static final String[] OPEN_URLS = {"/provision_status"};
    private static final String AUTH_URL = "/**";

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
             .cors().and().csrf().disable()
             .authorizeRequests()
                 .antMatchers(OPEN_URLS).permitAll()
                 .anyRequest().authenticated()
             .and()
                 .httpBasic()
             .and()
                 .addFilterBefore(new JwtOrBasicAuthFilterWithVerification(allowedIds), UsernamePasswordAuthenticationFilter.class);

         //  Remove this line when h2 console not needed
         http.headers().httpStrictTransportSecurity().maxAgeInSeconds(31536000).includeSubDomains(true).preload(true).and()
            .contentSecurityPolicy("frame-ancestors 'self' *.ibm.com http://localhost:8080").and()
            .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN).and().contentTypeOptions().and().frameOptions()
            .sameOrigin();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        String passEnc = "{noop}" + pass;
        auth.inMemoryAuthentication()
            .withUser(username)
            .password(passEnc)
            .roles("");
    }
    

}
