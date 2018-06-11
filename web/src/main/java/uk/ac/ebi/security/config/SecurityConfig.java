/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package uk.ac.ebi.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.validation.constraints.NotNull;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @NotNull
    @Value("${username_user}")
    String username_user;

    @NotNull
    @Value("${username_admin}")
    String username_admin;

    @NotNull
    @Value("${pw_user}")
    String pw_user;

    @NotNull
    @Value("${pw_admin}")
    String pw_admin;

    @NotNull
    @Value("${role_user}")
    String role_user;

    @NotNull
    @Value("${role_admin}")
    String role_admin;

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(username_user).password(pw_user).roles(role_user, role_admin);
        auth.inMemoryAuthentication().withUser(username_admin).password(pw_admin).roles(role_admin);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/registerInterestSummary/**").access("hasRole('" + role_user + "') or hasRole('" + role_admin + "')")
                .and().formLogin().loginPage("/login")
                .usernameParameter("ssoId").passwordParameter("password")
                .and().csrf()
                .and().exceptionHandling().accessDeniedPage("/Access_Denied");
    }
}