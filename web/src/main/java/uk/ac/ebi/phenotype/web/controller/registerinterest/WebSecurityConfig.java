/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package uk.ac.ebi.phenotype.web.controller.registerinterest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.frameoptions.StaticAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;

/**
 * Created by mrelac on 12/06/2017.
 *
 * Design of sample login screen taken from http://websystique.com/spring-security/spring-security-4-hibernate-annotation-example/
 */
@Configuration
@EnableWebSecurity
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
@ComponentScan("uk.ac.ebi.phenotype.web.controller.registerinterest")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private DataSource riDataSource;

    // Must use qualifier to get ri database; otherwise, komp2 is served up.
    @Inject
    public WebSecurityConfig(@Qualifier("riDataSource") DataSource riDataSource) {
        this.riDataSource = riDataSource;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        http

            .headers()
                // spring-security-core 4.2.8 is broken: specifying X-Frame-Options:ALLOW-FROM also incorrectly adds DENY to the same header so it reads ALLOW-FROM DENY.
                // see https://github.com/spring-projects/spring-security/issues/123
//                .addHeaderWriter(new XFrameOptionsHeaderWriter(new WhiteListedAllowFromStrategy(Arrays.asList("www.immunophenotype.org", "wwwdev.ebi.ac.uk"))))

                .addHeaderWriter(new XFrameOptionsHeaderWriter(new StaticAllowFromStrategy(new URI("http://www.immunophenotype.org"))))
                .addHeaderWriter(new XFrameOptionsHeaderWriter(new StaticAllowFromStrategy(new URI("http://wwwdev.ebi.ac.uk"))))
//                .frameOptions().disable()

            .and()

            .authorizeRequests()

                    .antMatchers(HttpMethod.GET, "/authenticated/**").access("hasRole('USER') or hasRole('ADMIN')")
                    .antMatchers(HttpMethod.GET, "/summary").access("hasRole('USER') or hasRole('ADMIN')")
                    .antMatchers(HttpMethod.GET, "/registration/**").access("hasRole('USER') or hasRole('ADMIN')")
                    .antMatchers(HttpMethod.GET, "/unregistration/**").access("hasRole('USER') or hasRole('ADMIN')")
                    .antMatchers(HttpMethod.GET, "/account").access("hasRole('USER') or hasRole('ADMIN')")
                    .antMatchers(HttpMethod.POST, "/account").access("hasRole('USER') or hasRole('ADMIN')")
                    .antMatchers(HttpMethod.GET,"/**")
                        .permitAll()

                .and()
                    .exceptionHandling()
                        .accessDeniedPage("/Access_Denied")

                .and()
                    .formLogin()
                        .loginPage("/rilogin")
                        .failureUrl("/rilogin?error")
                        .successHandler(new RiSavedRequestAwareAuthenticationSuccessHandler())
                        .usernameParameter("ssoId")
                        .passwordParameter("password")
        ;
    }

    @Autowired
    public void configureGlobalSecurityJdbc(AuthenticationManagerBuilder auth) throws Exception {

        auth
                .userDetailsService(userDetailsService())
                    .passwordEncoder(bcryptPasswordEncoder())

                .and()
                    .jdbcAuthentication()
                    .dataSource(riDataSource)
                    .rolePrefix("ROLE_")
                    .usersByUsernameQuery("SELECT address AS username, password, 'true' AS enabled FROM contact WHERE address = ?")
                    .authoritiesByUsernameQuery("SELECT c.address AS username, cr.role FROM contact c JOIN contact_role cr ON cr.contact_pk = c.pk WHERE c.address = ?")
        ;
    }

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public class RiSavedRequestAwareAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
        protected final Log logger = LogFactory.getLog(this.getClass());
        private RequestCache requestCache = new HttpSessionRequestCache();

        public RiSavedRequestAwareAuthenticationSuccessHandler() {
        }

        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
            logger.info("RiSavedRequest: Authentication Success!");
            SavedRequest savedRequest = this.requestCache.getRequest(request, response);
            if (savedRequest == null) {
                logger.info("RiSavedRequest: savedRequest is null.");
                super.onAuthenticationSuccess(request, response, authentication);
            } else {
                String targetUrlParameter = this.getTargetUrlParameter();
                if (!this.isAlwaysUseDefaultTargetUrl() && (targetUrlParameter == null || !StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
                    this.clearAuthenticationAttributes(request);
                    String targetUrl = savedRequest.getRedirectUrl();
                    this.logger.info("Redirecting to DefaultSavedRequest Url: " + targetUrl);
                    this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
                } else {
                    logger.info("RiSavedRequest: removing request.");
                    this.requestCache.removeRequest(request, response);
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            }
        }

        public void setRequestCache(RequestCache requestCache) {
            this.requestCache = requestCache;
        }
    }
}