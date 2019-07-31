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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.writers.frameoptions.AllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @NotNull
    @Value("${paBaseUrl}")
    private String paBaseUrl;

    private static final List<String> ALLOWED_CORS_ACCESS_URLS = Arrays.asList(
            "mousephenotypedev.org",
            "www.mousephenotype.org",
            "web.mousephenotype.org",
            "beta.mousephenotype.org",
            "dev.mousephenotype.org",
            "staging.mousephenotype.org",
            "test.mousephenotype.org",
            "www.immunophenotype.org",
            "www.ebi.ac.uk",
            "wwwdev.ebi.ac.uk"
    );

    // Must use qualifier to get ri database; otherwise, komp2 is served up.
    @Inject
    public WebSecurityConfig(@Qualifier("riDataSource") DataSource riDataSource) {
        this.riDataSource = riDataSource;
    }

    @Autowired
    private CaptchaFilter captchaFilter;

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ALLOWED_CORS_ACCESS_URLS);
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        AllowFromStrategy strategy = httpServletRequest -> String.join(", ", ALLOWED_CORS_ACCESS_URLS);

        http
                .addFilterAfter(captchaFilter, CsrfFilter.class)

                .headers()

                .cacheControl().disable()
                .frameOptions().disable()

                // in spring-security-core 4.2.8 , the addHeaderWriter line commented out below is broken: specifying X-Frame-Options:ALLOW-FROM also incorrectly adds DENY to the same header so it reads ALLOW-FROM DENY.
                // see https://github.com/spring-projects/spring-security/issues/123
//                .addHeaderWriter(new XFrameOptionsHeaderWriter(new WhiteListedAllowFromStrategy(Arrays.asList("www.immunophenotype.org", "wwwdev.ebi.ac.uk"))))
                .addHeaderWriter(new XFrameOptionsHeaderWriter(strategy))

                .and()

                .cors().configurationSource(corsConfigurationSource())

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

                //.successHandler(new RiSavedRequestAwareAuthenticationSuccessHandler())
                .usernameParameter("ssoId")
                .passwordParameter("password")

                // Ignore all csrf that isn't part of the login process.
                .and()
                .csrf()
                .ignoringAntMatchers("/dataTable_bq")
                .ignoringAntMatchers("/dataTableAlleleRefPost")
                .ignoringAntMatchers("/fetchAlleleRefPmidData")
                .ignoringAntMatchers("/querybroker")
                .ignoringAntMatchers("/bqExport")
                .ignoringAntMatchers("/batchQuery")
                .ignoringAntMatchers("/alleleRefLogin")
                .ignoringAntMatchers("/addpmid")
                .ignoringAntMatchers("/addpmidAllele")
                .ignoringAntMatchers("/gwaslookup");
        //.and().sessionManagement().invalidSessionStrategy(new RiSimpleRedirectInvalidSessionStrategy(paBaseUrl + "/search/gene?kw=*"))
        ;
    }

    /**
     * Register a filter with spring that adds the CORS header Access-Control-Allow-Origin for domains which
     * are allowed to do cross origin AJAX requests to the portal.  This list is maintained in the class constant
     * list @link{WebSecurityConfig.ALLOWED_CORS_ACCESS_URLS}.  If any of the domains in the list match, the header
     * is included in the response.
     *
     * If the request has been proxied to the portal (e.g., by a loadbalancer), the true requesting hostname should
     * be presented as the first entry in the list of hosts provided in the X-Forwarded-Host header of the request,
     * otherwise use the Host header.
     *
     * See the following for more information:
     * https://en.wikipedia.org/wiki/X-Forwarded-For
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-Host
     *
     */
    @Component
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public class CorsFilter implements Filter {

        private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
            HttpServletResponse response = (HttpServletResponse) res;
            HttpServletRequest request = (HttpServletRequest) req;
            String requestHostname = request.getHeader("Host");

            logger.debug("Dumping headers for request:\n" +
                Collections.list(request.getHeaderNames()).stream()
                    .map(k -> String.format("  Header '%s' = %s", k, request.getHeader(k)))
                    .collect(Collectors.joining("\n")));

            // If this request has ben proxied, lookup the original Host value (defined to be the first in the list
            // of x-forwarded-for header).  The implementation of the getHeader method matches by equalsIgnoreCase,
            // allowing the lowercase comparison.

            String host = null;

            if (ALLOWED_CORS_ACCESS_URLS.stream().anyMatch(requestHostname::contains)) {

                host = ALLOWED_CORS_ACCESS_URLS.stream().filter(requestHostname::contains).findFirst().orElse("*");

            } else if (request.getHeader("x-forwarded-host") != null) {
                List<String> hosts = Arrays
                        .stream(request.getHeader("x-forwarded-host").split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                if (ALLOWED_CORS_ACCESS_URLS.stream().anyMatch(hosts::contains)) {
                    host = hosts.get(0);
                }
            }

            if (host != null) {
                response.setHeader("Access-Control-Allow-Origin", host);
            }

            chain.doFilter(req, res);
        }

        public void init(FilterConfig filterConfig) { }
        public void destroy() { }

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

    public class RiSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

        private final Logger       logger       = LoggerFactory.getLogger(this.getClass().getCanonicalName());
        private       RequestCache requestCache = new HttpSessionRequestCache();

        public RiSavedRequestAwareAuthenticationSuccessHandler() {
        }

        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
            logger.info("============RiSavedRequest: Authentication Success!");

            // Set the session maximum inactive interval. The interval parameter is in seconds.
            int MAX_INACTIVE_INTERVAL_IN_HOURS = 26;
            request.getSession().setMaxInactiveInterval(MAX_INACTIVE_INTERVAL_IN_HOURS * 3600);

            SavedRequest savedRequest = this.requestCache.getRequest(request, response);
            if (savedRequest == null) {
                logger.info("==============RiSavedRequest: savedRequest is null.");
                super.onAuthenticationSuccess(request, response, authentication);
            } else {
                String targetUrlParameter = this.getTargetUrlParameter();
                if (!this.isAlwaysUseDefaultTargetUrl() && (targetUrlParameter == null || !StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
                    this.clearAuthenticationAttributes(request);
                    String targetUrl = savedRequest.getRedirectUrl();
                    logger.info("==================Redirecting to DefaultSavedRequest Url: " + targetUrl);
                    this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
                } else {
                    logger.info("====================RiSavedRequest: removing request.");
                    this.requestCache.removeRequest(request, response);
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            }
        }

        public void setRequestCache(RequestCache requestCache) {
            this.requestCache = requestCache;
        }
    }


    public final class RiSimpleRedirectInvalidSessionStrategy implements InvalidSessionStrategy {
        private final Log              logger           = LogFactory.getLog(this.getClass());
        private final String           destinationUrl;
        private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        private       boolean          createNewSession = true;

        public RiSimpleRedirectInvalidSessionStrategy(String invalidSessionUrl) {
            Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
            this.destinationUrl = invalidSessionUrl;
        }

        public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
            logger.info("============================Invalid session detected");
            String referer = request.getHeader("referer");
            if ((referer != null) && ( ! referer.startsWith(paBaseUrl))) {
                referer = null;                         // Don't use referer if it doesn't start with known, safe url.
            }

            String target = (referer == null ? this.destinationUrl : referer);
            logger.info("===========================Starting new session (if required) and redirecting to '" + target + "'");
            if (this.createNewSession) {
                request.getSession();
            }

            this.redirectStrategy.sendRedirect(request, response, target);
        }

        public void setCreateNewSession(boolean createNewSession) {
            this.createNewSession = createNewSession;
        }
    }

        /**
     * Add component to handle cross site filter attacks
     */
    @Component
    public class URLFilter implements Filter {

        @Override
        public void destroy() {}


        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterchain)
                throws IOException, ServletException {

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            filterchain.doFilter(new XSSFilter(request), response);

        }

        @Override
        public void init(FilterConfig filterconfig) {

        }

    }

    /**
     * Filter to detect and filter out XSS payloads from input strings
     */
    public class XSSFilter extends HttpServletRequestWrapper {

        private Pattern[] patterns = new Pattern[]{
                // Script fragments
                Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
                // src='...'
                Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // lonely script tags
                Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
                Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

                Pattern.compile("<(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // eval(...)
                Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // expression(...)
                Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
                // javascript:...
                Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
                // vbscript:...
                Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
                // onload(...)=...
                Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
        };

        XSSFilter(HttpServletRequest servletRequest) {
            super(servletRequest);
        }

        @Override
        public String[] getParameterValues(String parameter) {
            String[] values = super.getParameterValues(parameter);

            if (values == null) {
                return null;
            }

            int count = values.length;
            String[] encodedValues = new String[count];
            for (int i = 0; i < count; i++) {
                encodedValues[i] = stripXSS(values[i]);
            }

            return encodedValues;
        }

        @Override
        public String getParameter(String parameter) {
            String value = super.getParameter(parameter);

            return stripXSS(value);
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return stripXSS(value);
        }

        private String stripXSS(String value) {
            //System.out.println("in strip XSS method");
            if (value != null) {
                // ToDO :  Integrate OWASP ESAPI or AntiSamy library to avoid encoded attacks
                // value = ESAPI.encoder().canonicalize(value);
                //System.out.println("replacing value in strip XSS method");
                // Avoid null characters
                value = value.replaceAll("\0", "");

                // Remove all sections that match a pattern
                for (Pattern scriptPattern : patterns){
                    value = scriptPattern.matcher(value).replaceAll("");
                }
            }
            return value;
        }
    }
}