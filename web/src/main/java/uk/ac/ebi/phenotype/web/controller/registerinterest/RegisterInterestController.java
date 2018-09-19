/*******************************************************************************
 * Copyright 2018 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package uk.ac.ebi.phenotype.web.controller.registerinterest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.ri.core.entities.Contact;
import org.mousephenotype.cda.ri.core.entities.ResetCredentials;
import org.mousephenotype.cda.ri.core.entities.Summary;
import org.mousephenotype.cda.ri.core.exceptions.InterestException;
import org.mousephenotype.cda.ri.core.services.CoreService;
import org.mousephenotype.cda.ri.core.utils.DateUtils;
import org.mousephenotype.cda.ri.core.utils.EmailUtils;
import org.mousephenotype.cda.ri.core.utils.SecurityUtils;
import org.mousephenotype.cda.ri.core.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.mail.Message;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class RegisterInterestController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    private final int PASSWORD_CHANGE_TTL_MINUTES = 10;

    // Sleep intervals
    private final int INVALID_PASSWORD_SLEEP_SECONDS = 1;
    private final int SHORT_SLEEP_SECONDS            = 1;

    // Error messages
    public final static String ERR_ACCOUNT_LOCKED         = "Your account is locked.";
    public final static String ERR_ACCOUNT_NOT_DELETED    = "We were unable to delete your account. Please contact IMPC mouse informatics.";
    public final static String ERR_EMAIL_ADDRESS_MISMATCH = "The e-mail addresses do not match.";
    public final static String ERR_INVALID_EMAIL_ADDRESS  = "The value provided is not a valid e-mail address. Please enter a valid e-mail address.";
    public final static String ERR_INVALID_TOKEN          = "Invalid token.";
    public final static String ERR_PASSWORD_MISMATCH      = "The passwords do not match.";
    public final static String ERR_REGISTER_GENE_FAILED   = "The gene could not be registered.";
    public final static String ERR_UNREGISTER_GENE_FAILED = "The gene could not be unregistered.";
    public final static String ERR_CHANGE_PASSWORD_FAILED = "The password could not be changed.";
    public final static String ERR_SEND_MAIL_FAILED       = "The e-mail send failed.";
    public final static String ERR_INVALID_CREDENTIALS    = "Invalid username and password.";

    // Info messages
    public final static String INFO_ACCOUNT_CREATED    = "Your account has been created.";
    public static final String INFO_ALREADY_REGISTERED = "You are already registered for this gene.";
    public final static String INFO_CHANGE_PASSWORD    = "Send an e-mail to the address below to change the password.";
    public static final String INFO_NOT_REGISTERED     = "You are not registered for this gene.";
    public final static String INFO_PASSWORD_EXPIRED   = "Your password is expired. Please use the <i>change password</i> link below to change your password.";
    public final static String INFO_PASSWORD_CHANGED   = "Your password has been changed.";


    // Title strings
    public final static String TITLE_ACCOUNT_CREATED            = "Account created";
    public final static String TITLE_ACCOUNT_LOCKED             = "Account locked";
    public final static String TITLE_ACCOUNT_NOT_DELETED        = "Account not deleted";
    public final static String TITLE_INVALID_TOKEN              = "Invalid token";
    public final static String TITLE_INVALID_EMAIL_ADDRESS      = "Invalid e-mail address";
    public final static String TITLE_PASSWORD_EXPIRED           = "Password expired";
    public final static String TITLE_INVALID_CREDENTIALS        = "Invalid credentials";
    public final static String TITLE_PASSWORD_MISMATCH          = "Password mismatch";
    public final static String TITLE_EMAIL_ADDRESS_MISMATCH     = "e-mail address mismatch";
    public final static String TITLE_CHANGE_PASSWORD_FAILED     = "Change password failed";
    public final static String TITLE_CHANGE_PASSWORD_REQUEST    = "Change password";
    public final static String TITLE_CHANGE_PASSWORD_EMAIL_SENT = "Change password e-mail sent";
    public final static String TITLE_PASSWORD_CHANGED           = "Password Changed";
    public final static String TITLE_REGISTER_GENE_FAILED       = "Gene registration failed.";
    public final static String TITLE_UNREGISTER_GENE_FAILED     = "Gene unregistration failed.";
    public final static String TITLE_SEND_MAIL_FAILED           = "Mail server is down";

    private final Logger logger        = LoggerFactory.getLogger(this.getClass());
    private       CoreService      coreService;
    private       DateUtils        dateUtils     = new DateUtils();
    private       EmailUtils       emailUtils    = new EmailUtils();
    private       SecurityUtils    securityUtils = new SecurityUtils();

    // Properties
    private String          drupalBaseUrl;
    private String          paBaseUrl;
    private PasswordEncoder passwordEncoder;
    private SqlUtils        sqlUtils;
    private String          smtpFrom;
    private String          smtpHost;
    private int             smtpPort;
    private String          smtpReplyto;


    @Inject
    public RegisterInterestController(
            String paBaseUrl,
            String drupalBaseUrl,
            PasswordEncoder passwordEncoder,
            SqlUtils sqlUtils,
            String smtpFrom,
            String smtpHost,
            int smtpPort,
            String smtpReplyto,
            CoreService coreService
    ) {
        this.paBaseUrl = paBaseUrl;
        this.drupalBaseUrl = drupalBaseUrl;
        this.passwordEncoder = passwordEncoder;
        this.sqlUtils = sqlUtils;
        this.smtpFrom = smtpFrom;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpReplyto = smtpReplyto;
        this.coreService = coreService;
    }


    @RequestMapping(value = "/rilogin", method = RequestMethod.GET)
    public String rilogin(
            HttpServletRequest request
    ) {
        String error = request.getParameter("error");

        if (error != null) {
            sleep(INVALID_PASSWORD_SLEEP_SECONDS);
        }

        HttpSession session = request.getSession();
        session.setAttribute("paBaseUrl", paBaseUrl);
        session.setAttribute("drupalBaseUrl", drupalBaseUrl);

        return "loginPage";
    }


    @RequestMapping(value = "/rilogin", method = RequestMethod.POST)
    public String riloginPost(
            HttpServletRequest request
    ) {
        String error = request.getParameter("error");

        if (error != null) {
            sleep(INVALID_PASSWORD_SLEEP_SECONDS);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("paBaseUrl", paBaseUrl);
        session.setAttribute("drupalBaseUrl", drupalBaseUrl);

        return "redirect: " + paBaseUrl + "/summary";
    }


    @RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
    public String accessDenied(ModelMap model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Access is denied. Return to ri_errorPage page.
        model.addAttribute("title", TITLE_INVALID_CREDENTIALS);
        model.addAttribute("error", ERR_INVALID_CREDENTIALS);

        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        logger.info("/Access_Denied: No permission to access page for user {} with role {}", securityUtils.getPrincipal(), StringUtils.join(roles, ", "));

        sleep(SHORT_SLEEP_SECONDS);
// FIXME Is this correct? return or redirect? errorPage or loginPage?
        return "ri_errorPage";
    }


    // The logical endpoint name is /logout, but when /logout is used, this method never gets called. It appears like
    // some Spring magic is going on. Renaming the endpoint to /rilogout avoids Spring interference and gets properly called.
    @RequestMapping(value = "/rilogout", method = RequestMethod.GET)
    public String rilogout(HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("/logout: User {} logged out.", securityUtils.getPrincipal());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:" + paBaseUrl + "/summary";
    }

    // Call this endpoint from unauthenticated pages that want to force authentication (e.g. search, gene pages)
    @Secured("ROLE_USER")
    @RequestMapping(value = "/authenticated", method = RequestMethod.GET)
    public String authenticated(
            @RequestParam("target") String target
    ) {
        return "redirect:" + target;
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/registration/gene/{geneAccessionId}", method = RequestMethod.GET)
    public String registrationGene(
            @PathVariable("geneAccessionId") String geneAccessionId,
            @RequestParam("target") String target) throws InterestException
    {
        sqlUtils.registerGene(securityUtils.getPrincipal(), geneAccessionId);

        return "redirect:" + target;
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/unregistration/gene/{geneAccessionId}", method = RequestMethod.GET)
    public String unregistrationGene(
            @PathVariable("geneAccessionId") String geneAccessionId,
            @RequestParam("target") String target) throws InterestException
    {
        sqlUtils.unregisterGene(securityUtils.getPrincipal(), geneAccessionId);

        return "redirect:" + target;
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public String summary(ModelMap model, HttpServletRequest request) {
        Contact contact = sqlUtils.getContact(securityUtils.getPrincipal());
        if (contact == null) {
            model.addAttribute("title", TITLE_INVALID_CREDENTIALS);
            model.addAttribute("error", ERR_INVALID_CREDENTIALS);

            // contact is null. Get roles from authentication.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<String>   roles          = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            logger.info("summary: Unable to get principal for user {} with role {}", securityUtils.getPrincipal(), StringUtils.join(roles, ", "));

            sleep(SHORT_SLEEP_SECONDS);

            return "ri_errorPage";
        }

        Summary summary = sqlUtils.getSummary(securityUtils.getPrincipal());

        model.addAttribute("summary", summary);
        model.addAttribute("paBaseUrl", request.getSession().getAttribute("paBaseUrl"));

        return "ri_summaryPage";
    }


    @RequestMapping(value = "/changePasswordRequest", method = RequestMethod.GET)
    public String changePasswordRequest(ModelMap model) {
        model.addAttribute("title", TITLE_CHANGE_PASSWORD_REQUEST);
        model.addAttribute("status", INFO_CHANGE_PASSWORD);

        return "ri_changePasswordRequestPage";
    }


    @RequestMapping(value = "/changePasswordEmail", method = RequestMethod.POST)
    public String changePasswordEmail(
            ModelMap model,
            @RequestParam(value = "emailAddress", defaultValue = "") String emailAddress,
            @RequestParam(value = "repeatEmailAddress", defaultValue = "") String repeatEmailAddress
    ) {
        // Validate e-mail addresses are identical.
        if (!emailAddress.equals(repeatEmailAddress)) {
            model.addAttribute("emailAddress", emailAddress);
            model.addAttribute("title", TITLE_EMAIL_ADDRESS_MISMATCH);
            model.addAttribute("error", ERR_EMAIL_ADDRESS_MISMATCH);

            return "ri_changePasswordRequestPage";
        }

        // Validate that it looks like an e-mail address.
        if (!emailUtils.isValidEmailAddress(emailAddress)) {
            model.addAttribute("title", TITLE_INVALID_EMAIL_ADDRESS);
            model.addAttribute("error", ERR_INVALID_EMAIL_ADDRESS);

            return "ri_changePasswordRequestPage";
        }

        // Generate and assemble email with password change
        String token     = buildToken(emailAddress);
        String tokenLink = paBaseUrl + "/changePasswordResponse?token=" + token;
        logger.debug("tokenLink = " + tokenLink);

        String  body;
        String  subject;
        Contact contact = sqlUtils.getContact(emailAddress);
        if (contact == null) {
            body = generateCreateAccountEmail(tokenLink);
            subject = "Your request to create new IMPC Register Interest account";
        } else {
            body = generateChangePasswordEmail(tokenLink);
            subject = "Your request to change your IMPC Register Interest password";
        }
        Message message = emailUtils.assembleEmail(smtpHost, smtpPort, smtpFrom, smtpReplyto, subject, body, emailAddress, true);

        // Insert request to reset_credentials table
        ResetCredentials resetCredentials = new ResetCredentials(emailAddress, token, new Date());
        sqlUtils.updateResetCredentials(resetCredentials);

        // Send e-mail
        try {

            emailUtils.sendEmail(message);

        } catch (InterestException e) {

            model.addAttribute("title", TITLE_SEND_MAIL_FAILED);
            model.addAttribute("error", ERR_SEND_MAIL_FAILED);
            logger.error("Error trying to send password change e-mail to {}: {}", emailAddress, e.getLocalizedMessage());
            return "ri_errorPage";
        }

        String status = "An e-mail containing a change password link has been sent to <i>" + emailAddress + "</i>.\n" +
                "Any previous links are no longer valid. This link is valid for " + PASSWORD_CHANGE_TTL_MINUTES + " minutes.";
        model.addAttribute("emailAddress", emailAddress);
        model.addAttribute("title", TITLE_CHANGE_PASSWORD_EMAIL_SENT);
        model.addAttribute("status", status);
        model.addAttribute("showWhen", true);

        logger.info("Sent Change Password email to {}", emailAddress);

        return "ri_statusPage";
    }


    @RequestMapping(value = "/changePasswordResponse", method = RequestMethod.GET)
    public String changePasswordResponseGetUrl(ModelMap model, HttpServletRequest request) {

        // Parse out query string for token value.
        String token = SecurityUtils.getTokenFromQueryString(request.getQueryString());

        // Look up email address from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, return to ri_errorPage page.
        if (resetCredentials == null) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} not found", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "ri_errorPage";
        }

        // If token has expired, return to ri_errorPage page.
        if (dateUtils.isExpired(resetCredentials.getCreatedAt(), PASSWORD_CHANGE_TTL_MINUTES)) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} has expired", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "ri_errorPage";
        }

        model.addAttribute("token", token);

        return "ri_changePasswordResponsePage";
    }


    @RequestMapping(value = "/changePasswordResponse", method = RequestMethod.POST)
    public String changePasswordResponsePostUrl(
            ModelMap model, HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("repeatPassword") String repeatPassword
    ) {
        String title;
        String status;
        model.addAttribute("token", token);

        // Validate the new password. Return to ri_changePasswordResponsePage if validation fails.
        String error = validateNewPassword(newPassword, repeatPassword);
        if ( ! error.isEmpty()) {
            model.addAttribute("title", TITLE_PASSWORD_MISMATCH);
            model.addAttribute("error", ERR_PASSWORD_MISMATCH);

            logger.info("Token {} not found", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "ri_changePasswordResponsePage";
        }

        // Look up email address from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, return to ri_errorPage page.
        if (resetCredentials == null) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} not found", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "ri_errorPage";
        }

        String emailAddress = resetCredentials.getAddress();

        Contact contact = sqlUtils.getContact(emailAddress);
        try {

            // If the contact doesn't exist, create a new account; otherwise, just update the password.
            if (contact == null) {
                sqlUtils.createAccount(emailAddress, passwordEncoder.encode(newPassword));
                contact = sqlUtils.getContact(emailAddress);
                title = TITLE_ACCOUNT_CREATED;
                status = INFO_ACCOUNT_CREATED;

                // Send welcome e-mail.
                coreService.generateAndSendWelcome(emailAddress);

            } else {
                sqlUtils.updatePassword(emailAddress, passwordEncoder.encode(newPassword));
                title = TITLE_PASSWORD_CHANGED;
                status = INFO_PASSWORD_CHANGED;
            }

            // Consume (remove) the reset_credential record.
            sqlUtils.deleteResetCredentialsByEmailAddress(emailAddress);

        } catch (InterestException e) {

            model.addAttribute("title", TITLE_CHANGE_PASSWORD_FAILED);
            model.addAttribute("error", ERR_CHANGE_PASSWORD_FAILED);
            logger.error("Error trying to change password for {}: {}", emailAddress, e.getLocalizedMessage());

            return "ri_errorPage";
        }

        logger.info("Password successfully changed for {}", emailAddress);

        // Get the user's roles and mark the user as authenticated.
        Authentication authentication = new UsernamePasswordAuthenticationToken(emailAddress, null, contact.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("title", title);
        model.addAttribute("status", status);
        model.addAttribute("emailAddress", emailAddress);

        return "ri_statusPage";
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String accountGetUrl(ModelMap model) {
        model.addAttribute("emailAddress", securityUtils.getPrincipal());

        return "ri_deleteAccountConfirmationPage";
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public String accountDeleteUrl(
            HttpServletRequest request,
            HttpServletResponse response,
            ModelMap model
    ) {
        try {

            sqlUtils.deleteContact(securityUtils.getPrincipal());

        } catch (InterestException e) {

            logger.error(e.getLocalizedMessage());
            model.addAttribute("title", TITLE_ACCOUNT_NOT_DELETED);
            model.addAttribute("error", ERR_ACCOUNT_NOT_DELETED);

            return "ri_errorPage";
        }

        // Log user out.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("deleteAccountDeleteUrl(): User {} logged out.", securityUtils.getPrincipal());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:" + paBaseUrl + "/login?deleted";
    }


    // PRIVATE METHODS


    /**
     * Build and return a unique hash token for this email address
     *
     * @param emailAddress contact email address
     * @return a unique hash token for this email address
     */
    private String buildToken(String emailAddress) {

        String token;

        SecureRandom random       = new SecureRandom();
        String       randomDouble = Double.toString(random.nextDouble());

        token = DigestUtils.sha256Hex(randomDouble + emailAddress);

        return token;
    }

    private final String EMAIL_STYLE = new StringBuilder()
            .append("<style>")
            .append(".button {")
            .append("background-color: #286090;")
            .append("border-color: #204d74;")
            .append("border-radius: 5px;")
            .append("text-align: center;")
            .append("padding: 10px;")
            .append("}")
            .append(".button a {")
            .append("color: #ffffff;")
            .append("display: block;")
            .append("font-size: 14px;")
            .append("text-decoration: none;")
            .append("}")
            .append("</style>").toString();

    private String generateChangePasswordEmail(String tokenLink) {

        StringBuilder body = new StringBuilder()
                .append("<html>")
                .append(EMAIL_STYLE)
                .append("<table>")
                .append("<tr>")
                .append("<td>")
                .append("Dear colleague,")
                .append("<br />")
                .append("<br />")
                .append("This e-mail was sent in response to a request to change your IMPC Register Interest password. ")
                .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append("below to change your IMPC Register Interest password.")
                .append("<br />")
                .append("<br />")
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td class=\"button\">")
                .append("<a href=\"" + tokenLink + "\">Reset password</a>")
                .append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</html>");

        return body.toString();
    }

    private String generateCreateAccountEmail(String tokenLink) {

        StringBuilder body = new StringBuilder()
                .append("<html>")
                .append(EMAIL_STYLE)
                .append("<table>")
                .append("<tr>")
                .append("<td>")
                .append("Dear colleague,")
                .append("<br />")
                .append("<br />")
                .append("This e-mail was sent in response to a request to create a new IMPC Register Interest account. ")
                .append("Your username will be your e-mail address.")
                .append("<br />")
                .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append("below to set your IMPC Register Interest password and create your account.")
                .append("<br />")
                .append("<br />")
                .append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td class=\"button\">")
                .append("<a href=\"" + tokenLink + "\">Create account</a>")
                .append("</td>")
                .append("</tr>")
                .append("</table>")
                .append("</html>");

        return body.toString();
    }

    private void sleep(int numSeconds) {
        try {
            Thread.sleep(numSeconds * 1000);

        } catch (InterruptedException e) {
        }
    }

    private String validateNewPassword(String newPassword, String repeatPassword) {

        if (newPassword.isEmpty()) {
            return "Please specify a new password";
        }

        if (!newPassword.equals(repeatPassword)) {
            return "Passwords do not match";
        }

        return "";
    }
}