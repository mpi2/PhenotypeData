package uk.ac.ebi.phenotype.web.controller.registerinterest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.ac.ebi.phenotype.web.util.CaptchaHttpProxy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    private final Logger           log              = LoggerFactory.getLogger(this.getClass().getCanonicalName());
    private       CaptchaHttpProxy captchaHttpProxy = new CaptchaHttpProxy();

    // See https://www.google.com/recaptcha for setup
    @NotNull
    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @NotNull
    @Value("${recaptcha.url}")
    private String recaptchaUrl;

    @NotNull
    @Value("${recaptcha.response.param}")
    private String recaptchaResponseParam;

    @NotNull
    @Value("${paBaseUrl}")
    private String paBaseUrl;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        if (
            ("POST".equalsIgnoreCase(req.getMethod())) &&
                (
                    req.getServletPath().contains("rilogin") ||
                    req.getServletPath().contains("sendEmail")
                )
            ) {

            System.out.println("\n");
            System.out.println("Request path " + req.getRequestURI());
            System.out.println("Request method " + req.getMethod());

            log.info("URL = " + req.getRequestURL());

            if ( ! validateRecaptcha(req)) {
                String target = req.getHeader("referer");
                if ((target == null) || ! (target.startsWith(paBaseUrl))) {
                    target = paBaseUrl + "/rilogin";
                }
                target += "?error=true";
                res.sendRedirect(target);
                return;
            }

        }

        chain.doFilter(req, res);
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

        log.info("Response from google recaptcha service: " + body);

        return success;
    }
}