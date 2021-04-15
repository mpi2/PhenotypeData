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
import org.mousephenotype.cda.ri.entities.Contact;
import org.mousephenotype.cda.ri.entities.ResetCredentials;
import org.mousephenotype.cda.ri.enums.EmailFormat;
import org.mousephenotype.cda.ri.exceptions.InterestException;
import org.mousephenotype.cda.ri.pojo.SmtpParameters;
import org.mousephenotype.cda.ri.pojo.Summary;
import org.mousephenotype.cda.ri.services.MailService;
import org.mousephenotype.cda.ri.services.SummaryService;
import org.mousephenotype.cda.ri.utils.EmailUtils;
import org.mousephenotype.cda.utilities.DateUtils;
import org.mousephenotype.cda.utilities.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.phenotype.generic.util.SecurityUtils;

import javax.inject.Inject;
import javax.mail.Message;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class RegisterInterestController {

    private final int EMAIL_VALIDITY_TTL_MINUTES = 180;

    public final static String ANONYMOUS_USER = "anonymousUser";

    // Sleep intervals
    private final int INVALID_PASSWORD_SLEEP_SECONDS = 1;
    private final int SHORT_SLEEP_SECONDS            = 1;

    // Error messages
    public final static String ERR_ACCOUNT_NOT_DELETED    = "We were unable to delete your account. Please contact IMPC mouse informatics.";
    public final static String ERR_EMAIL_ADDRESS_MISMATCH = "The e-mail addresses do not match.";
    public final static String ERR_INVALID_CREDENTIALS    = "Invalid username and password.";
    public final static String ERR_INVALID_EMAIL_ADDRESS  = "The value provided is not a valid e-mail address. Please enter a valid e-mail address.";
    public final static String ERR_INVALID_TOKEN          = "Invalid token.";
    public final static String ERR_PASSWORD_MISMATCH      = "The passwords do not match.";
    public final static String ERR_SET_PASSWORD_FAILED    = "We were unable to set your passwor. Please contact IMPC mouse informatics.";
    public final static String ERR_SEND_MAIL_FAILED       = "Unable to send e-mail. Please contact IMPC mouse informatics.";


    // E-mail subject lines
    public final static String EMAIL_SUBJECT_NEW_ACCOUNT_OR_RESET_PASSWORD = "Request to create new account / reset your password";
    public final static String EMAIL_SUBJECT_RESET_PASSWORD                = "Request to reset your IMPC Register Interest password";

    // Title strings
    public final static String TITLE_INVALID_TOKEN                      = "Invalid token";
    public final static String TITLE_INVALID_CREDENTIALS                = "Invalid credentials";
    public final static String TITLE_PASSWORD_MISMATCH                  = "Password mismatch";
    public final static String TITLE_SET_PASSWORD_FAILED                = "Set password failed";
    public final static String TITLE_RESET_PASSWORD_REQUEST             = "Reset password";
    public final static String TITLE_NEW_ACCOUNT_RESET_PASSWORD_REQUEST = "Create new account / reset your password";
    public final static String TITLE_SEND_MAIL_FAILED                   = "E-mail server error.";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DateUtils  dateUtils  = new DateUtils();
    private EmailUtils emailUtils = new EmailUtils();

    private UrlUtils urlUtils = new UrlUtils();

    // Properties
    private PasswordEncoder passwordEncoder;
    private String          recaptchaPublic;
    private SmtpParameters  smtpParameters;
    private MailService     mailService;
    private SummaryService  summaryService;

    @Inject
    public RegisterInterestController(
        PasswordEncoder passwordEncoder,
        MailService mailService,
        String recaptchaPublic,
        SmtpParameters smtpParameters,
        SummaryService summaryService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.recaptchaPublic = recaptchaPublic;
        this.smtpParameters = smtpParameters;
        this.summaryService = summaryService;
    }

    @RequestMapping(value = "/rilogin", method = RequestMethod.GET)
    public String rilogin(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestParam(value = "target", required = false) String target,
        @RequestParam(value = "error", required = false) String error
    ) throws IOException {
        if ((target == null) || (target.trim().isEmpty())) {
            target = _getBaseUrl(request) + "/summary";
        }
        if (error != null) {
            _sleep(INVALID_PASSWORD_SLEEP_SECONDS);
        }
        // If user is already logged in, redirect them to the summary page.
        if (SecurityUtils.isLoggedIn()) {
            response.sendRedirect(_getBaseUrl(request) + "/summary");
            return null;
        }
        HttpSession session = request.getSession();
        session.setAttribute("recaptchaPublic", recaptchaPublic);
        session.setAttribute("target", target);

        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        return "loginPage";
    }

    @RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
    public String accessDenied(ModelMap model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Access is denied. Return to login page.
        model.addAttribute("title", TITLE_INVALID_CREDENTIALS);
        model.addAttribute("error", ERR_INVALID_CREDENTIALS);
        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        logger.info("/Access_Denied: No permission to access page for user {} with role {}", SecurityUtils.getPrincipal(), StringUtils.join(roles, ", "));
        _sleep(SHORT_SLEEP_SECONDS);
        return "loginPage";
    }


    // The logical endpoint name is /logout, but when /logout is used, this method never gets called. It appears like
    // some Spring magic is going on. Renaming the endpoint to /rilogout avoids Spring interference and gets properly called.
    @RequestMapping(value = "/rilogout", method = RequestMethod.GET)
    public void rilogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.debug("/logout: User {} logged out.", SecurityUtils.getPrincipal());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        response.sendRedirect(_getBaseUrl(request) + "/search");
    }

    // Call this endpoint from unauthenticated pages that want to force authentication (e.g. search, gene pages)
    @Secured("ROLE_USER")
    @RequestMapping(value = "/authenticated", method = RequestMethod.GET)
    public String authenticated(
        @RequestParam("target") String target, HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        response.sendRedirect(_getBaseUrl(request) + target);
        return null;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/update-gene-registration", method = RequestMethod.POST)
    public ResponseEntity updateRegistration(
        HttpServletRequest request,
        @RequestHeader(value = "asynch", defaultValue = "false") Boolean asynch,
        @RequestParam("geneAccessionId") String geneAccessionId,
        @RequestParam(value = "target", required = false) String target
    ) {
        // Redirect attempt to register for anonymousUser account to /search.
        if (SecurityUtils.getPrincipal().equalsIgnoreCase(ANONYMOUS_USER)) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", (target != null ? target : _getBaseUrl(request) + "/search"));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        String followStatus;
        if (summaryService.isRegisteredForGene(SecurityUtils.getPrincipal(), geneAccessionId)) {
            summaryService.unregisterGene(SecurityUtils.getPrincipal(), geneAccessionId);
            followStatus = "Not Following";
        } else {
            summaryService.registerGene(SecurityUtils.getPrincipal(), geneAccessionId);
            followStatus = "Following";
        }

        // If this is an AJAX request, return the updated value
        if (asynch) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> statusMap = new HashMap<>();
            statusMap.put(geneAccessionId, followStatus);
            return new ResponseEntity<>(statusMap, headers, HttpStatus.OK);
        }

        // Otherwise, redirect to the target url
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", (target != null ? target : _getBaseUrl(request) + "/summary"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/change-email-format", method = RequestMethod.POST)
    public ResponseEntity updateEmailFormat(
        HttpServletRequest request,
        @RequestHeader(value = "emailFormat") String emailFormat,
        @RequestHeader(value = "asynch", defaultValue = "false") Boolean asynch,
        @RequestParam(value = "target", required = false) String target
    ) {
        // Redirect attempt to register for anonymousUser account to /search.
        if (SecurityUtils.getPrincipal().equalsIgnoreCase(ANONYMOUS_USER)) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", (target != null ? target : _getBaseUrl(request) + "/search"));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        try {
            summaryService.changeEmailFormat(SecurityUtils.getPrincipal(), EmailFormat.valueOf(emailFormat));
        } catch (InterestException e) {
            logger.error("changeEmailFormat for user {} to {} failed", SecurityUtils.getPrincipal(), emailFormat);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", (target != null ? target : _getBaseUrl(request) + "/summary"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public String summary(ModelMap model, HttpServletResponse response) {
        Summary summary = summaryService.getSummaryByContact(summaryService.getContact(SecurityUtils.getPrincipal()));
        model.addAttribute("summary", summary);
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        return "ri_summaryPage";
    }

    @RequestMapping(value = "/resetPasswordRequest", method = RequestMethod.GET)
    public String resetPasswordRequest(HttpServletRequest request, ModelMap model) {
        HttpSession session = request.getSession();
        model.addAttribute("title", TITLE_RESET_PASSWORD_REQUEST);
        model.addAttribute("emailAddress", SecurityUtils.getPrincipal());
        return "ri_resetPasswordRequestPage";
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/resetPasswordConfirmation", method = RequestMethod.POST)
    public String resetPasswordeConfirmationPost(
        HttpServletRequest request,
        HttpServletResponse response,
        ModelMap model
    ) throws IOException, InterestException {
        HttpSession session      = request.getSession();
        String      emailAddress = SecurityUtils.getPrincipal();
        // Redirect attempt to reset password for anonymousUser account to /search.
        if (emailAddress.equalsIgnoreCase(ANONYMOUS_USER)) {
            response.sendRedirect(_getBaseUrl(request) + "/search");
            return null;
        }

        model.addAttribute("emailAddress", emailAddress);
        String  token         = _buildToken(emailAddress);
        String  tokenLink     = _buildTokenLink(request, TITLE_RESET_PASSWORD_REQUEST, token);
        String  subject       = EMAIL_SUBJECT_RESET_PASSWORD;
        boolean accountExists = summaryService.getContact(emailAddress) != null;
        String  body          = _generateResetPasswordEmail(tokenLink, accountExists);
        return _assembleAndSendEmail(TITLE_RESET_PASSWORD_REQUEST, emailAddress, subject, body, token, model);
    }

    @RequestMapping(value = "/newAccountRequest", method = RequestMethod.GET)
    public String newAccountRequest(HttpServletRequest request, ModelMap model) {
        HttpSession session = request.getSession();
        model.addAttribute("title", TITLE_NEW_ACCOUNT_RESET_PASSWORD_REQUEST);
        session.setAttribute("recaptchaPublic", recaptchaPublic);
        return "ri_collectEmailAddressPage";
    }

    @RequestMapping(value = "/sendNewAccountEmail", method = RequestMethod.POST)
    public String sendNewAccountEmail(HttpServletRequest request, HttpServletResponse response, ModelMap model,
                                      @RequestParam(value = "emailAddress", defaultValue = "") String emailAddress,
                                      @RequestParam(value = "repeatEmailAddress", defaultValue = "") String repeatEmailAddress
    ) throws InterestException, IOException {
        HttpSession session = request.getSession();
        model.addAttribute("emailAddress", emailAddress);
        model.addAttribute("repeatEmailAddress", repeatEmailAddress);
        session.setAttribute("recaptchaPublic", recaptchaPublic);

        // Validate e-mail addresses are identical.
        if (!emailAddress.equals(repeatEmailAddress)) {
            String urlParameters = _buildUrlParameters(ERR_EMAIL_ADDRESS_MISMATCH, emailAddress,
                repeatEmailAddress, TITLE_NEW_ACCOUNT_RESET_PASSWORD_REQUEST);
            response.sendRedirect(_getBaseUrl(request) + "/newAccountRequest" + urlParameters);
            return null;
        }
        // Validate that it looks like an e-mail address.
        if (!emailUtils.isValidEmailAddress(emailAddress)) {
            String urlParameters = _buildUrlParameters(ERR_INVALID_EMAIL_ADDRESS, emailAddress,
                repeatEmailAddress, TITLE_NEW_ACCOUNT_RESET_PASSWORD_REQUEST);
            response.sendRedirect(_getBaseUrl(request) + "/newAccountRequest" + urlParameters);
            return null;
        }

        String  token         = _buildToken(emailAddress);
        String  tokenLink     = _buildTokenLink(request, TITLE_NEW_ACCOUNT_RESET_PASSWORD_REQUEST, token);
        String  subject       = EMAIL_SUBJECT_NEW_ACCOUNT_OR_RESET_PASSWORD;
        boolean accountExists = summaryService.getContact(emailAddress) != null;
        String  body          = _generateNewAccountOrResetPasswordEmail(tokenLink, accountExists);
        return _assembleAndSendEmail(TITLE_NEW_ACCOUNT_RESET_PASSWORD_REQUEST, emailAddress, subject, body, token, model);
    }

    private String _assembleAndSendEmail(String title, String emailAddress, String subject, String body, String token, ModelMap model) throws InterestException {
        try {
            Message message = emailUtils.assembleEmail(subject, body, emailAddress, true, smtpParameters);
            if (message == null) {
                throw new InterestException("Skipping email '" + emailAddress + "'.");
            }
            final ResetCredentials existingCredentials = summaryService.getResetCredentialsByEmailAddress(emailAddress);

            // To prevent multiple emails being sent too quickly, randomly wait between 1 and 5 minutes (MAX_RESEND_WAIT_TIME) before
            // sending another email.
            final int MAX_RESEND_WAIT_TIME = 5;
            Integer randomTimeout = new Random().ints(1, (MAX_RESEND_WAIT_TIME + 1)).limit(1).findFirst().getAsInt();
            if (existingCredentials == null || dateUtils.isExpired(existingCredentials.getCreatedAt(), randomTimeout)) {
                logger.info("Register Interest Credential ({}) either does not exist or the timeout ({} minutes) has expired.  Sending new email.",
                    (existingCredentials == null ? "Null" : existingCredentials.getToken()), randomTimeout);
                // Insert request to reset_credentials table
                ResetCredentials resetCredentials = new ResetCredentials(emailAddress, token, new Date());
                summaryService.updateResetCredentials(resetCredentials);

                // Send e-mail
                emailUtils.sendEmail(message);
            } else {
                logger.info("Register Interest Credential ({}) timeout ({}) has not exprired. NOT sending new email.", existingCredentials.getToken(), randomTimeout);
                String error = "Currently unable to send e-mail. Please try again in " + MAX_RESEND_WAIT_TIME + " minutes.";
                model.addAttribute("title", title);
                model.addAttribute("error", error);
                return "ri_sendEmailStatusPage";
            }
        } catch (InterestException e) {
            model.addAttribute("title", TITLE_SEND_MAIL_FAILED);
            model.addAttribute("error", ERR_SEND_MAIL_FAILED);
            logger.error("Error trying to send e-mail to {}: {}", emailAddress, e.getLocalizedMessage());
            return "loginPage";
        }
        String status = "An e-mail with instructions has been sent to <i>" + emailAddress + "</i>. " +
            "The e-mail contains a link valid for " + EMAIL_VALIDITY_TTL_MINUTES / 60 + " hours. Any previous links are no longer valid.";
        model.addAttribute("title", title);
        model.addAttribute("status", status);
        return "ri_sendEmailStatusPage";
    }

    @RequestMapping(value = "/setPassword", method = RequestMethod.GET)
    public String setPasswordGet(
        ModelMap model,
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestParam("token") String token,
        @RequestParam(value = "action") String action) {

        // Look up email address from reset_credentials table. If not found or is expired, redirect to ri_expiredTokenPage.
        ResetCredentials resetCredentials = summaryService.getResetCredentialsByToken(token);
        if (resetCredentials == null) {
            logger.info("Token {} not found", token);
            _sleep(SHORT_SLEEP_SECONDS);
            return "ri_expiredTokenPage";
        }
        if (dateUtils.isExpired(resetCredentials.getCreatedAt(), EMAIL_VALIDITY_TTL_MINUTES)) {
            String emailAddress = resetCredentials.getAddress();
            logger.info("Token {} has expired", token);
            _sleep(SHORT_SLEEP_SECONDS);
            if ( ! emailAddress.equals(ANONYMOUS_USER)) {
                model.addAttribute("emailAddress", emailAddress);
            }
            return "ri_expiredTokenPage";
        }

        model.addAttribute("token", token);
        model.addAttribute("emailAddress", resetCredentials.getAddress());
        model.addAttribute("action", action);
        return "ri_setPasswordPage";
    }

    @RequestMapping(value = "/setPassword", method = RequestMethod.POST)
    public String setPasswordPost(
        ModelMap model, HttpServletRequest request,
        HttpServletResponse response,
        @RequestParam("token") String token,
        @RequestParam("newPassword") String newPassword,
        @RequestParam("repeatPassword") String repeatPassword,
        @RequestParam("action") String action
    ) throws IOException {

        model.addAttribute("token", token);
        model.addAttribute("action", action);

        // Look up email address from reset_credentials table
        ResetCredentials resetCredentials = summaryService.getResetCredentialsByToken(token);
        if (resetCredentials == null) {
            // Redirect attempt to delete anonymousUser account to /search.
            if (SecurityUtils.getPrincipal().equalsIgnoreCase(ANONYMOUS_USER)) {
                response.sendRedirect(_getBaseUrl(request) + "/search");
                return null;
            }
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);
            model.addAttribute("emailAddress", SecurityUtils.getPrincipal());
            logger.info("Token {} not found", token);
            _sleep(SHORT_SLEEP_SECONDS);
            return "ri_setPasswordPage";
        }
        String emailAddress = resetCredentials.getAddress();
        model.addAttribute("emailAddress", emailAddress);

        // Validate the new password. Return to ri_setPasswordPage if validation fails.
        String error = _validateNewPassword(newPassword, repeatPassword);
        if (!error.isEmpty()) {
            model.addAttribute("title", TITLE_PASSWORD_MISMATCH);
            model.addAttribute("error", ERR_PASSWORD_MISMATCH);
            logger.info("Password validation failed.");
            _sleep(SHORT_SLEEP_SECONDS);
            return "ri_setPasswordPage";
        }

        Contact contact = summaryService.getContact(emailAddress);
        try {
            // If the contact doesn't exist, create a new account; otherwise, just update the password.
            if (contact == null) {
                summaryService.createAccount(emailAddress, passwordEncoder.encode(newPassword));
                contact = summaryService.getContact(emailAddress);

                // Send welcome e-mail.
                boolean inHtml = true;
                mailService.generateAndSendWelcome(contact);
            } else {
                summaryService.updatePassword(emailAddress, passwordEncoder.encode(newPassword));
            }

            // Consume (remove) the reset_credential record.
            summaryService.deleteResetCredentialsByEmailAddress(emailAddress);

        } catch (InterestException e) {
            model.addAttribute("title", TITLE_SET_PASSWORD_FAILED);
            model.addAttribute("error", ERR_SET_PASSWORD_FAILED);
            logger.error("Error trying to set password for {}: {}", emailAddress, e);
            String errorMessage = ERR_SET_PASSWORD_FAILED;
            response.sendRedirect(_getBaseUrl(request) + "/rilogin?error=true&errorMessage=" + errorMessage);
            return null;
        }

        // Get the user's roles and mark the user as authenticated.
        Authentication authentication = new UsernamePasswordAuthenticationToken(emailAddress, null, contact.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.sendRedirect(_getBaseUrl(request) + "/summary");
        return null;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/accountDeleteRequest", method = RequestMethod.POST)
    public String accountDeleteRequestPost(ModelMap model) {
        model.addAttribute("emailAddress", SecurityUtils.getPrincipal());
        return "ri_deleteAccountRequestPage";
    }

    @RequestMapping(value = "/accountDeleteRequest", method = RequestMethod.GET)
    public String accountDeleteRequest(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
        // Redirect attempt to delete anonymousUser account to /search.
        if (SecurityUtils.getPrincipal().equalsIgnoreCase(ANONYMOUS_USER)) {
            response.sendRedirect(_getBaseUrl(request) + "/search");
            return null;
        }
        return accountDeleteRequestPost(model);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/accountDeleteConfirmation", method = RequestMethod.POST)
    public String accountDeleteConfirmationPost(
        HttpServletRequest request,
        HttpServletResponse response,
        ModelMap model
    ) throws IOException {
        // Redirect attempt to delete anonymousUser account to /search.
        if (SecurityUtils.getPrincipal().equalsIgnoreCase(ANONYMOUS_USER)) {
            response.sendRedirect(_getBaseUrl(request) + "/search");
            return null;
        }
        try {
            summaryService.deleteContact(SecurityUtils.getPrincipal());
        } catch (InterestException e) {
            logger.error("Unable to delete account for {}. Reason: {}", SecurityUtils.getPrincipal(), e.getLocalizedMessage());
            response.sendRedirect(_getBaseUrl(request) + "/summary?error=" + ERR_ACCOUNT_NOT_DELETED);
            return null;
        }

        // Log user out.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("deleteAccountDeleteUrl(): User {} logged out.", SecurityUtils.getPrincipal());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        model.addAttribute("showWhen", true);
        model.addAttribute("showLoginLink", true);
        model.addAttribute("status", "Your account has been deleted.");
        response.sendRedirect(_getBaseUrl(request) + "/search");
        return null;
    }

    @RequestMapping(value = "/accountDeleteConfirmation", method = RequestMethod.GET)
    public String accountDeleteConfirmation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(_getBaseUrl(request) + "/search");
        return null;
    }


    // PRIVATE METHODS


    /**
     * Build and return a unique hash token for this email address
     *
     * @param emailAddress contact email address
     * @return a unique hash token for this email address
     */
    private String _buildToken(String emailAddress) {

        String token;

        SecureRandom random       = new SecureRandom();
        String       randomDouble = Double.toString(random.nextDouble());

        token = DigestUtils.sha256Hex(randomDouble + emailAddress);

        return token;
    }

    private String _buildTokenLink(HttpServletRequest request, String action, String token) {
        String hostname  = request.getAttribute("mappedHostname").toString();
        String protocol  = (hostname.toLowerCase().contains("localhost")) ? "http:" : "https:";
        String tokenLink = protocol + hostname + _getBaseUrl(request) + "/setPassword?token=" + token + "&action=" + action;
        logger.debug("tokenLink = " + tokenLink);
        return tokenLink;
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


    // This method returns a snippet like this: ?error=true&errorMessage=Bad&emailAddress=x@y.z&repeatEmailAddress=y@z.zz&title=Create
    private String _buildUrlParameters(
        String errorMessage,
        String emailAddress,
        String repeatEmailAddress,
        String title) {
        return new StringBuilder()
            .append("?error=True")
            .append("&errorMessage=").append(errorMessage)
            .append("&emailAddress=").append(emailAddress)
            .append("&repeatEmailAddress=").append(repeatEmailAddress)
            .append("&title=").append(title).toString();
    }

    private String _generateResetPasswordEmail(String tokenLink, boolean accountExists) {
        String buttonText = (accountExists ? TITLE_RESET_PASSWORD_REQUEST : TITLE_NEW_ACCOUNT_RESET_PASSWORD_REQUEST);
        tokenLink = urlUtils.urlEncode(tokenLink);

        StringBuilder body = new StringBuilder()
            .append("<html>")
            .append(EMAIL_STYLE)
            .append("<table>")
            .append("<tr>")
            .append("<td>")
            .append("Dear colleague,")
            .append("<br />")
            .append("<br />")
            .append("This e-mail was sent in response to a request to reset your IMPC Register Interest password. ");

        if (accountExists) {
            body
                .append("<br />")
                .append("<br />")
                .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append("below to reset your IMPC Register Interest password.");
        } else {
            body
                .append("As no account exists yet for this e-mail address, the account will be created first. ")
                .append("<br />")
                .append("<br />")
                .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append("below to create your account and set your password.");
        }

        body
            .append("<br />")
            .append("<br />")
            .append("</td>")
            .append("</tr>")


            .append("<tr>")
            .append("<td>")
            .append("<a href=\"" + tokenLink + "\">" + buttonText + "</a>")
            .append("<br />")
            .append("<br />")
            .append("</td>")
            .append("</tr>")

            .append("<tr>")
            .append("<td>")
            .append("If you are having trouble seeing the link, please paste this text into your browser:")
            .append("<br />")
            .append("<br />")
            .append(tokenLink)
            .append("<br />")
            .append("<br />")
            .append("</td>")
            .append("</tr>")

            .append("</table>")
            .append("<br />")
            .append(mailService.generateEmailEpilogue(true))
            .append("</html>");

        return body.toString();
    }

    private String _generateNewAccountOrResetPasswordEmail(String tokenLink, boolean accountExists) {
        String buttonText = (accountExists ? TITLE_RESET_PASSWORD_REQUEST : TITLE_NEW_ACCOUNT_RESET_PASSWORD_REQUEST);
        tokenLink = urlUtils.urlEncode(tokenLink);

        StringBuilder body = new StringBuilder()
            .append("<html>")
            .append(EMAIL_STYLE)
            .append("<table>")
            .append("<tr>")
            .append("<td>")
            .append("Dear colleague,")
            .append("<br />")
            .append("<br />")
            .append("This e-mail was sent in response to a request to create a new IMPC Register Interest account / reset your password. ");

        if (accountExists) {
            body
                .append("As an account already exists for this e-mail address, no account will be created. You may use this link to reset your password.")
                .append("<br />")
                .append("<br />")
                .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append("below to reset your password.");
        } else {
            body
                .append("No account exists for this e-mail address, thus a new account will be created. ")
                .append("Your username will be your e-mail address.")
                .append("<br />")
                .append("<br />")
                .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                .append("below to create your IMPC Register Interest account and set your password.");
        }

        body
            .append("<br />")
            .append("<br />")
            .append("</td>")
            .append("</tr>")
            .append("<tr>")
            .append("<td>")
            .append("<a href=\"" + tokenLink + "\">" + buttonText + "</a>")
            .append("<br />")
            .append("<br />")
            .append("</td>")
            .append("</tr>")

            .append("<tr>")
            .append("<td>")
            .append("If you are having trouble seeing the link, please paste this text into your browser:")
            .append("<br />")
            .append("<br />")
            .append(tokenLink)
            .append("<br />")
            .append("<br />")
            .append("</td>")
            .append("</tr>")

            .append("</table>")
            .append("<br />")
            .append(mailService.generateEmailEpilogue(true))
            .append("</html>");

        return body.toString();
    }

    private void _sleep(int numSeconds) {
        try {
            Thread.sleep(numSeconds * 1000);

        } catch (InterruptedException e) {
        }
    }

    private String _validateNewPassword(String newPassword, String repeatPassword) {

        if (newPassword.isEmpty()) {
            return "Please specify a new password";
        }

        if (!newPassword.equals(repeatPassword)) {
            return "Passwords do not match";
        }

        return "";
    }

    private String _getBaseUrl(HttpServletRequest request) {
        logger.debug("Getting baseUrl for request {}, baseUrl is {}", request.getRequestURL().toString(), request.getAttribute("baseUrl").toString());
        return request.getAttribute("baseUrl").toString();
    }
}
