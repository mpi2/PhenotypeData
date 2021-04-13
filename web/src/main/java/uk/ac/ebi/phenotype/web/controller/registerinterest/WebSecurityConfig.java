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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Web security configuration.  This configures the login form for the registration of interest database
 * as well as CORS and Captcha when necessary.
 * */
@Configuration
@EnableWebSecurity
@ComponentScan("uk.ac.ebi.phenotype.web.controller.registerinterest")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private DataSource riDataSource;
    private CaptchaFilter captchaFilter;
    private int sessionTimeoutInMinutes;


    /**
     * Determine and return the correct baseUrl for the request.  The baseUrl is used to construct most of the URL
     * paths for the views and must be overridden depending on the context.  When the request is coming from a domain
     * that is not an ebi domain (containing "ebi.ac.uk"), then the request originated from the frontent and has been
     * proxied to the webapp.  In this case, all the portal functionality is mapped into the "/data" context path
     * and so all root-relative links should start with /data.  If the request is to a local-to-ebi resource, it's
     * safe to use the context path of the application.
     *
     * @param request the request object
     * @return True if the request is proxied, else False
     */
    private String getBaseUrl(HttpServletRequest request) {

        // default to the path where the application is deployed, e.g., /mi/impc/phenotype-archive
        String baseUrl = request.getContextPath();

        // If this webapp is being accessed behind a proxy, the x-forwarded-host header will be set, in which case,
        // override the default and use the agreed upon baseUrl path "/data".
        if (isProxied(request)) {
            String[] hosts = request.getHeader("x-forwarded-host").split(",");
            if (Arrays.stream(hosts).anyMatch(host -> !host.matches(".*ebi\\.ac\\.uk"))) {
                baseUrl = "/data";
            }
        }

        return baseUrl;

    }

    /**
     * Detect if this request has been proxied through a proxy server
     * @param request the request object
     * @return True if the request is proxied, else False
     */
    private Boolean isProxied(HttpServletRequest request) {
        Boolean isProxied = Boolean.FALSE;

        // If this webapp is being accessed behind a proxy, the "x-forwarded-host" header will be set
        if (request.getHeader("x-forwarded-host") != null) {
            String[] hosts = request.getHeader("x-forwarded-host").split(",");

            // If there is any host in the list that is _not_ local to ebi, then the request has been proxied
            if (Arrays.stream(hosts).anyMatch(host -> !host.matches(".*ebi\\.ac\\.uk"))) {
                isProxied = Boolean.TRUE;
            }
        }
        return isProxied;
    }


    private static final List<String> ALLOWED_CORS_ACCESS_URLS = Arrays.asList(
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
    public WebSecurityConfig(@Qualifier("riDataSource") DataSource riDataSource, CaptchaFilter captchaFilter,
                             int sessionTimeoutInMinutes) {
        this.riDataSource = riDataSource;
        this.captchaFilter = captchaFilter;
        this.sessionTimeoutInMinutes = sessionTimeoutInMinutes;
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        AllowFromStrategy strategy = httpServletRequest -> String.join(", ", ALLOWED_CORS_ACCESS_URLS);

        http
                .addFilterAfter(captchaFilter, CsrfFilter.class)

                .headers()

                .cacheControl().disable()
                .frameOptions().disable()

                // in spring-security-core 4.2.8 , the addHeaderWriter line commented out below is broken: specifying X-Frame-Options:ALLOW-FROM also incorrectly adds DENY to the same header so it reads ALLOW-FROM DENY.
                // see https://github.com/spring-projects/spring-security/issues/123
//                .addHeaderWriter(new XFrameOptionsHeaderWriter(new WhiteListedAllowFromStrategy(Arrays.asList("www.immunophenotype.org", "wwwdev.ebi.ac.uk"))))
//                .addHeaderWriter(new XFrameOptionsHeaderWriter(strategy))

                .and()

                .authorizeRequests()

                .antMatchers(HttpMethod.GET, "/authenticated/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/summary").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/registration/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/unregistration/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/toggle/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/account/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, "/account/**").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, "/**")
                .permitAll()

                .and()
                .exceptionHandling()
                .accessDeniedPage("/Access_Denied")

                .and()
                .formLogin()
                .loginPage("/rilogin")
                .failureHandler(new CustomAuthenticationFailureHandler())
                .successHandler(new CustomAuthenticationSuccessHandler())
                .usernameParameter("ssoId")
                .passwordParameter("password")

                // Ignore all csrf that isn't part of the login process.
                .and()
                .csrf()
                .ignoringAntMatchers("/dataTable_bq")
                .ignoringAntMatchers("/querybroker")
                .ignoringAntMatchers("/bqExport")
                .ignoringAntMatchers("/batchQuery")
                .ignoringAntMatchers("/alleleRefLogin");
    }


    /**
     * Success handler which overrides the default spring behaviour in order to include the baseUrl in the redirection
     * when appropriate (i.e., when behind a proxy)
     */
    public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

        private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication)
                throws IOException {
            String targetUrl = getBaseUrl(request) + "/summary";

            if (response.isCommitted()) {
                logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
                return;
            }

            int sessionTimeoutInMinutesBefore = request.getSession().getMaxInactiveInterval() / 60;
            final int SESSION_TIMEOUT_IN_SECONDS = sessionTimeoutInMinutes * 60;
            request.getSession().setMaxInactiveInterval(SESSION_TIMEOUT_IN_SECONDS);
            int sessionTimeoutInMinutesAfter = request.getSession().getMaxInactiveInterval() / 60;
            logger.info("Reset session timeout from {} minutes to {} minutes", sessionTimeoutInMinutesBefore, sessionTimeoutInMinutesAfter);

            response.sendRedirect(targetUrl);
            clearAuthenticationAttributes(request);
        }


        protected void clearAuthenticationAttributes(HttpServletRequest request) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return;
            }
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }



    /**
     * A Custom authentication failure class to overide the behaviour when a user authentication fails.  This is
     * needed in order to redirect correctly to the proper baseUrl depending on if the request was proxied or not.
     */
    public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

        private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

        @Override
        public void onAuthenticationFailure(
                HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException exception)
                throws IOException {

            logger.info("Exception occurred logging in", exception);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            String baseUrl = getBaseUrl(request);
            response.sendRedirect(baseUrl + "/rilogin?error=true");
        }
    }

    /**
     * Register a filter with spring that adds the CORS header Access-Control-Allow-Origin for domains which are allowed
     * to do cross origin AJAX requests to the portal.  This list is maintained in the class constant list
     * @link{WebSecurityConfig.ALLOWED_CORS_ACCESS_URLS}. If any of the domains in the list match, the header is
     * included in the response.
     * <p>
     * If the request has been proxied to the portal (e.g., by a loadbalancer), the true requesting hostname should be
     * presented as the first entry in the list of hosts provided in the X-Forwarded-Host header of the request,
     * otherwise use the Host header.
     * <p>
     * See the following for more information:
     * https://en.wikipedia.org/wiki/X-Forwarded-For
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-Host
     */
    @Component
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public class CorsFilter implements Filter {

        static final String X_FORWARDED_HOST = "x-forwarded-host";
        private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
            HttpServletResponse response = (HttpServletResponse) res;
            HttpServletRequest request = (HttpServletRequest) req;
            String requestHostname = request.getHeader("Host");

            logger.debug("Dumping headers for request:\n" +
                    Collections.list(request.getHeaderNames()).stream()
                            .map(k -> String.format("  Header '%s' = %s", k, request.getHeader(k)))
                            .collect(Collectors.joining("\n")));

            // If this request has ben proxied, match the original Host value (defined to be the first in the list
            // of x-forwarded-for header).  The implementation of the getHeader method matches by equalsIgnoreCase,
            // allowing the lowercase comparison.

            String host = null;

            if (request.getHeader(X_FORWARDED_HOST) != null) {
                List<String> hosts = Arrays
                        .stream(request.getHeader(X_FORWARDED_HOST).split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                if (hosts.stream().anyMatch(ALLOWED_CORS_ACCESS_URLS::contains)) {
                    host = hosts.get(0);
                }

            } else if (ALLOWED_CORS_ACCESS_URLS.stream().anyMatch(requestHostname::contains)) {
                host = ALLOWED_CORS_ACCESS_URLS.stream().filter(requestHostname::contains).findFirst().orElse("*");
            }

            if (host != null) {
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, host);
            }

            chain.doFilter(req, res);
        }

        public void init(FilterConfig filterConfig) {
        }

        public void destroy() {
        }

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

    /**
     * Add component to handle cross site filter attacks
     */
    @Component
    public class URLFilter implements Filter {

        @Override
        public void destroy() {
        }

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
     * Component to add the baseUrl and isProxied to every request
     */
    @Component
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public class DetectProxyFilter implements Filter {

        @Override
        public void destroy() {
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterchain)
                throws IOException, ServletException {

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            // Do not set baseUrl and isProxied attributes for assets
//            List<String> assets = Arrays.asList(".js", ".css", ".gif", ".png");
//            if (assets.stream().noneMatch(x -> request.getRequestURI().endsWith(x))) {
                request.setAttribute("baseUrl", getBaseUrl(request));
                request.setAttribute("isProxied", isProxied(request));
//            }

            filterchain.doFilter(request, response);
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
            if (value != null) {
                // ToDO :  Integrate OWASP ESAPI or AntiSamy library to avoid encoded attacks
                // value = ESAPI.encoder().canonicalize(value);
                // Avoid null characters
                value = value.replaceAll("\0", "");

                // Remove all sections that match a pattern
                for (Pattern scriptPattern : patterns) {
                    value = scriptPattern.matcher(value).replaceAll("");
                }
            }
            return value;
        }
    }
}