package uk.ac.ebi.phenotype.web.controller.registerinterest;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CaptchaFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

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

            if (! validateRecaptcha(req)) {
                res.sendRedirect(paBaseUrl + "/rilogin?error=true");
                return;
            }

        }

        chain.doFilter(req, res);
    }

    /**
     * Contact the google recaptcha service and validate the user is a human
     *
     * @param req the request
     * @return true if the user is not a bot
     * @throws IOException when the
     */
    private boolean validateRecaptcha(ServletRequest req) {

        boolean success = false;

        try {

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(recaptchaUrl);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("secret", recaptchaSecret));
            params.add(new BasicNameValuePair("response", req.getParameter(recaptchaResponseParam)));
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse response = client.execute(httpPost);

            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(response);
            log.info("Response from google recaptcha service: " + body);

            JSONObject recaptchaResponse = new JSONObject(body);
            success = recaptchaResponse.getBoolean("success");
        } catch (NullPointerException | JSONException  | IOException  e) {
            log.info("Exception from recaptcha service" , e);
        }

        return success;
    }

    @Override
    public void destroy() {

    }
}