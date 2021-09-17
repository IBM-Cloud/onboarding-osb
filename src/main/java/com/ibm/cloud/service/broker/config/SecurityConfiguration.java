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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${service.broker.user}")
    private String username;

    @Value("${service.broker.password}")
    private String pass;

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
         http
             .csrf().disable()
             .authorizeRequests().antMatchers("/**").authenticated()
             .and()
             .httpBasic();
         //  Remove this line when h2 console not needed
            http.headers().frameOptions().disable();
        
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
