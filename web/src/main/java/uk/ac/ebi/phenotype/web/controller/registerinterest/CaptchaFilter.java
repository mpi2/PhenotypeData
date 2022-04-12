package uk.ac.ebi.phenotype.web.controller.registerinterest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.ac.ebi.phenotype.web.util.CaptchaHttpProxy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    private final Logger           log              = LoggerFactory.getLogger(this.getClass().getCanonicalName());
    private       CaptchaHttpProxy captchaHttpProxy = new CaptchaHttpProxy();

    // See https://www.google.com/recaptcha for setup
    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${recaptcha.url}")
    private String recaptchaUrl;

    @Value("${recaptcha.response.param}")
    private String recaptchaResponseParam;

    @Value("${base_url}")
    private String baseUrl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (
            ("POST".equalsIgnoreCase(request.getMethod())) &&
                (
                    request.getServletPath().contains("rilogin") ||
                    request.getServletPath().contains("sendNewAccountEmail")
                )
            ) {

            System.out.println("\n");
            System.out.println("Request path " + request.getRequestURI());
            System.out.println("Request method " + request.getMethod());

            log.info("URL = " + request.getRequestURL());

            if ( ! validateRecaptcha(request)) {
                String target = request.getHeader("referer");
                if ((target == null) || ! (target.startsWith(baseUrl))) {
                    target = baseUrl + "/rilogin";
                }
                if (target.endsWith("sendNewAccountEmail")) {
                    // sendNewAccountEmail is a POST and will throw a 405 if redirected, as there is no GET. Remap to New account.
                    target = baseUrl + "/newAccountRequest";
                }
                target += "?error=true";
                response.sendRedirect(target);
                return;
            }

        }

        chain.doFilter(request, response);
    }

    /**
     * Contact the google recaptcha service and validate the user is a human
     *
     * @param request the request
     * @return true if the user is not a bot
     * @throws IOException when the server fails to respond appropriately
     */
    private boolean validateRecaptcha(HttpServletRequest request) {

        boolean success = false;

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("secret", recaptchaSecret));
        params.add(new BasicNameValuePair("response", request.getParameter(recaptchaResponseParam)));

        String body = "";

        try {
            body = captchaHttpProxy.getContent(recaptchaUrl, params);
            if (body != null) {
                JSONObject recaptchaResponse = new JSONObject(body);
                success = recaptchaResponse.getBoolean("success");
            }

        } catch (IOException | JSONException e) {

            log.info("Exception from recaptcha service", e);
        }

        log.debug("Response from google recaptcha service: " + body);

        return success;
    }
}