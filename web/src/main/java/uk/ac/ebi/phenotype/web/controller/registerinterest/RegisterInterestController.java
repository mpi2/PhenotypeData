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
import org.mousephenotype.cda.ri.core.services.GenerateService;
import org.mousephenotype.cda.ri.core.utils.DateUtils;
import org.mousephenotype.cda.ri.core.utils.EmailUtils;
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
import uk.ac.ebi.phenotype.generic.util.SecurityUtils;

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

    private final int EMAIL_VALIDITY_TTL_MINUTES = 180;

    // Sleep intervals
    private final int INVALID_PASSWORD_SLEEP_SECONDS = 1;
    private final int SHORT_SLEEP_SECONDS            = 1;

    // Error messages
    public final static String ERR_ACCOUNT_LOCKED         = "Your account is locked.";
    public final static String ERR_ACCOUNT_NOT_DELETED    = "We were unable to delete your account. Please contact IMPC mouse informatics.";
    public final static String ERR_CREATE_ACCOUNT_FAILED  = "We were unable to create your account. Please contact IMPC mouse informatics.";
    public final static String ERR_EMAIL_ADDRESS_MISMATCH = "The e-mail addresses do not match.";
    public final static String ERR_EXPIRED_TOKEN          = "Expired token.";
    public final static String ERR_INVALID_CREDENTIALS    = "Invalid username and password.";
    public final static String ERR_INVALID_EMAIL_ADDRESS  = "The value provided is not a valid e-mail address. Please enter a valid e-mail address.";
    public final static String ERR_INVALID_TOKEN          = "Invalid token.";
    public final static String ERR_PASSWORD_MISMATCH      = "The passwords do not match.";
    public final static String ERR_REGISTER_GENE_FAILED   = "The gene could not be registered. Please contact IMPC mouse informatics.";
    public final static String ERR_RESET_PASSWORD_FAILED  = "We were unable to reset your password. Please contact IMPC mouse informatics.";
    public final static String ERR_SEND_MAIL_FAILED       = "Unable to send e-mail. Please contact IMPC mouse informatics.";
    public final static String ERR_UNREGISTER_GENE_FAILED = "The gene could not be unregistered. Please contact IMPC mouse informatics.";


    // E-mail subject lines
    public final static String EMAIL_SUBJECT_NEW_ACCOUNT    = "Request to create a new IMPC Register Interest account";
    public final static String EMAIL_SUBJECT_RESET_PASSWORD = "Request to reset your IMPC Register Interest password";


    // Info messages
    public final static String INFO_ACCOUNT_CREATED    = "Your account has been created.";
    public static final String INFO_ALREADY_REGISTERED = "You are already registered for this gene.";
    public final static String INFO_RESET_PASSWORD     = "Send an e-mail to the address below to reset your password.";
    public final static String INFO_NEW_ACCOUNT        = "Send an e-mail to the address below to create a new account.";
    public static final String INFO_NOT_REGISTERED     = "You are not registered for this gene.";
    public final static String INFO_PASSWORD_EXPIRED   = "Your password is expired. Please use the <i>reset password</i> link below to reset your password.";
    public final static String INFO_PASSWORD_RESET     = "Your password has been reset.";


    // Title strings
    public final static String TITLE_NEW_ACCOUNT_CREATED       = "Account created";
    public final static String TITLE_ACCOUNT_LOCKED            = "Account locked";
    public final static String TITLE_ACCOUNT_NOT_DELETED       = "Account not deleted";
    public final static String TITLE_INVALID_TOKEN             = "Invalid token";
    public final static String TITLE_INVALID_EMAIL_ADDRESS     = "Invalid e-mail address";
    public final static String TITLE_PASSWORD_EXPIRED          = "Password expired";
    public final static String TITLE_INVALID_CREDENTIALS       = "Invalid credentials";
    public final static String TITLE_PASSWORD_MISMATCH         = "Password mismatch";
    public final static String TITLE_EMAIL_ADDRESS_MISMATCH    = "e-mail address mismatch";
    public final static String TITLE_RESET_PASSWORD_FAILED     = "Reset password failed";
    public final static String TITLE_RESET_PASSWORD_REQUEST    = "Reset password";
    public final static String TITLE_EMAIL_SENT = "E-mail sent";
    public final static String TITLE_NEW_ACCOUNT_REQUEST       = "Create new account";
    public final static String TITLE_PASSWORD_RESET            = "Password Reset";
    public final static String TITLE_REGISTER_GENE_FAILED      = "Gene registration failed.";
    public final static String TITLE_UNREGISTER_GENE_FAILED    = "Gene unregistration failed.";
    public final static String TITLE_SEND_MAIL_FAILED          = "E-mail server error.";

    private final Logger logger        = LoggerFactory.getLogger(this.getClass());
    private       CoreService      coreService;
    private       DateUtils        dateUtils     = new DateUtils();
    private       EmailUtils       emailUtils    = new EmailUtils();

    // Properties
    private String          drupalBaseUrl;
    private String          paBaseUrl;
    private PasswordEncoder passwordEncoder;
    private SqlUtils        sqlUtils;
    private String          smtpFrom;
    private String          smtpHost;
    private int             smtpPort;
    private String          smtpReplyto;


    private enum ActionType {
        NEW_ACCOUNT,
        RESET_PASSWORD
    }


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
        
        // If user is already logged in, redirect them to the summary page.
        if (SecurityUtils.isLoggedIn()) {
            return "redirect: " + paBaseUrl + "/summary";
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

        HttpSession session = request.getSession();
        session.setAttribute("paBaseUrl", paBaseUrl);
        session.setAttribute("drupalBaseUrl", drupalBaseUrl);

        return "redirect: " + paBaseUrl + "/summary";
    }


    @RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
    public String accessDenied(ModelMap model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Access is denied. Return to login page.
        model.addAttribute("title", TITLE_INVALID_CREDENTIALS);
        model.addAttribute("error", ERR_INVALID_CREDENTIALS);

        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        logger.info("/Access_Denied: No permission to access page for user {} with role {}", SecurityUtils.getPrincipal(), StringUtils.join(roles, ", "));

        sleep(SHORT_SLEEP_SECONDS);

        return "loginPage";
    }


    // The logical endpoint name is /logout, but when /logout is used, this method never gets called. It appears like
    // some Spring magic is going on. Renaming the endpoint to /rilogout avoids Spring interference and gets properly called.
    @RequestMapping(value = "/rilogout", method = RequestMethod.GET)
    public String rilogout(HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("/logout: User {} logged out.", SecurityUtils.getPrincipal());
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
    @RequestMapping(value = "/registration/gene/{geneAccessionId}", method = RequestMethod.POST)
    public String registrationGene(
            HttpServletRequest request,
            @PathVariable("geneAccessionId") String geneAccessionId) throws InterestException
    {
        String target = request.getParameter("target");
        if (target == null) {
            target = paBaseUrl + "/summary";
        }
        sqlUtils.registerGene(SecurityUtils.getPrincipal(), geneAccessionId);

        return "redirect:" + target;
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/unregistration/gene/{geneAccessionId}", method = RequestMethod.POST)
    public String unregistrationGene(
            HttpServletRequest request,
            @PathVariable("geneAccessionId") String geneAccessionId) throws InterestException
    {
        String target = request.getParameter("target");
        if (target == null) {
            target = paBaseUrl + "/summary";
        }
        sqlUtils.unregisterGene(SecurityUtils.getPrincipal(), geneAccessionId);

        return "redirect:" + target;
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public String summary(ModelMap model, HttpServletRequest request) {

        Summary summary = sqlUtils.getSummary(SecurityUtils.getPrincipal());

        model.addAttribute("summary", summary);
        model.addAttribute("paBaseUrl", paBaseUrl);

        return "ri_summaryPage";
    }


    @RequestMapping(value = "/resetPasswordRequest", method = RequestMethod.GET)
    public String resetPasswordRequest(ModelMap model) {
        model.addAttribute("title", TITLE_RESET_PASSWORD_REQUEST);

        return "ri_collectEmailAddressPage";
    }

    @RequestMapping(value = "/newAccountRequest", method = RequestMethod.GET)
    public String newAccountRequest(ModelMap model) {
        model.addAttribute("title", TITLE_NEW_ACCOUNT_REQUEST);

        return "ri_collectEmailAddressPage";
    }


    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    public String sendEmail(ModelMap model,
        @RequestParam(value = "emailAddress", defaultValue = "") String emailAddress,
        @RequestParam(value = "repeatEmailAddress", defaultValue = "") String repeatEmailAddress,
        @RequestParam("action") String action
    ) {
        model.addAttribute("emailAddress", emailAddress);
        model.addAttribute("repeatEmailAddress", repeatEmailAddress);

        ActionType actionType = (action.equals("Reset password") ? ActionType.RESET_PASSWORD : ActionType.NEW_ACCOUNT);

        String body;
        String subject;
        String title;

        switch (actionType) {
            case NEW_ACCOUNT:
                title = TITLE_NEW_ACCOUNT_REQUEST;
                subject = EMAIL_SUBJECT_NEW_ACCOUNT;
                break;

            default:
                title = TITLE_RESET_PASSWORD_REQUEST;
                subject = EMAIL_SUBJECT_RESET_PASSWORD;
                break;
        }


        // Validate e-mail addresses are identical.
        if ( ! emailAddress.equals(repeatEmailAddress)) {
            model.addAttribute("emailAddress", emailAddress);
            model.addAttribute("title", title);
            model.addAttribute("error", ERR_EMAIL_ADDRESS_MISMATCH);

            return "ri_collectEmailAddressPage";
        }

        // Validate that it looks like an e-mail address.
        if ( ! emailUtils.isValidEmailAddress(emailAddress)) {
            model.addAttribute("title", title);
            model.addAttribute("error", ERR_INVALID_EMAIL_ADDRESS);

            return "ri_collectEmailAddressPage";
        }

        // Generate and assemble email
        String token     = buildToken(emailAddress);
        String tokenLink = paBaseUrl + "/setPassword?token=" + token + "&action=" + action;
        logger.debug("tokenLink = " + tokenLink);

        Contact contact       = sqlUtils.getContact(emailAddress);
        boolean accountExists = (contact != null);
        if (actionType == ActionType.NEW_ACCOUNT) {
            body = generateNewAccountEmail(tokenLink, accountExists);
        } else {
            body = generateResetPasswordEmail(tokenLink, accountExists);
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
            logger.error("Error trying to send e-mail to {}: {}", emailAddress, e.getLocalizedMessage());

            return "loginPage";
        }

        String status = "An e-mail with instructions has been sent to <i>" + emailAddress + "</i>. " +
                "The e-mail contains a link valid for " + EMAIL_VALIDITY_TTL_MINUTES / 60 + " hours. Any previous links are no longer valid.";

        model.addAttribute("title", title);
        model.addAttribute("status", status);
        model.addAttribute("repeatEmailAddress", emailAddress);
        model.addAttribute("hideButton", true);

        return "ri_collectEmailAddressPage";
    }


    @RequestMapping(value = "/setPassword", method = RequestMethod.GET)
    public String setPasswordGet(
            ModelMap model,
            HttpServletRequest request,
            @RequestParam("token") String token) {

        // Look up email address from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, redirect to login page.
        if (resetCredentials == null) {

            logger.info("Token {} not found", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "redirect: rilogin?error=true&errorMessage=" + ERR_INVALID_TOKEN;
        }

        // If token has expired, redirect to login page.
        if (dateUtils.isExpired(resetCredentials.getCreatedAt(), EMAIL_VALIDITY_TTL_MINUTES)) {

            logger.info("Token {} has expired", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "redirect: rilogin?error=true&errorMessage=" + ERR_EXPIRED_TOKEN;
        }

        model.addAttribute("token", token);
        model.addAttribute("emailAddress", resetCredentials.getAddress());

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
    ) {

        ActionType actionType = (action.equals("Reset password") ? ActionType.RESET_PASSWORD : ActionType.NEW_ACCOUNT);

        model.addAttribute("token", token);

        // Validate the new password. Return to ri_setPasswordPage if validation fails.
        String error = validateNewPassword(newPassword, repeatPassword);
        if ( ! error.isEmpty()) {
            model.addAttribute("title", TITLE_PASSWORD_MISMATCH);
            model.addAttribute("error", ERR_PASSWORD_MISMATCH);

            logger.info("Password validation failed.");

            sleep(SHORT_SLEEP_SECONDS);

            return "ri_setPasswordPage";
        }

        // Look up email address from reset_credentials table
        ResetCredentials resetCredentials = sqlUtils.getResetCredentials(token);

        // If not found, return to ri_setPasswordPage page.
        if (resetCredentials == null) {
            model.addAttribute("title", TITLE_INVALID_TOKEN);
            model.addAttribute("error", ERR_INVALID_TOKEN);

            logger.info("Token {} not found", token);

            sleep(SHORT_SLEEP_SECONDS);

            return "ri_setPasswordPage";
        }

        String emailAddress = resetCredentials.getAddress();

        Contact contact = sqlUtils.getContact(emailAddress);
        try {

            // If the contact doesn't exist, create a new account; otherwise, just update the password.
            if (contact == null) {
                sqlUtils.createAccount(emailAddress, passwordEncoder.encode(newPassword));
                contact = sqlUtils.getContact(emailAddress);

                // Send welcome e-mail.
                coreService.generateAndSendWelcome(emailAddress);

            } else {
                sqlUtils.updatePassword(emailAddress, passwordEncoder.encode(newPassword));
            }

            // Consume (remove) the reset_credential record.
            sqlUtils.deleteResetCredentialsByEmailAddress(emailAddress);

        } catch (InterestException e) {

            model.addAttribute("title", TITLE_RESET_PASSWORD_FAILED);
            model.addAttribute("error", ERR_RESET_PASSWORD_FAILED);
            logger.error("Error trying to [re]set password for {}: {}", emailAddress, e.getLocalizedMessage());

            String errorMessage = (actionType == ActionType.NEW_ACCOUNT ? ERR_CREATE_ACCOUNT_FAILED : ERR_RESET_PASSWORD_FAILED);

            return "redirect: rilogin?error=true&errorMessage=" + errorMessage;
        }

        // Get the user's roles and mark the user as authenticated.
        Authentication authentication = new UsernamePasswordAuthenticationToken(emailAddress, null, contact.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect: " + paBaseUrl + "/summary";
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/accountDeleteRequest", method = RequestMethod.POST)
    public String accountDeleteRequest(ModelMap model) {
        model.addAttribute("emailAddress", SecurityUtils.getPrincipal());

        return "ri_deleteAccountConfirmationPage";
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/accountDeleteConfirmation", method = RequestMethod.POST)
    public String accountDeleteConfirmation(
            HttpServletRequest request,
            HttpServletResponse response,
            ModelMap model
    ) {
        try {

            sqlUtils.deleteContact(SecurityUtils.getPrincipal());

        } catch (InterestException e) {

            logger.error("Unable to delete account for {}. Reason: {}", SecurityUtils.getPrincipal(), e.getLocalizedMessage());

            return "redirect: " + paBaseUrl + "/summary?error=" + ERR_ACCOUNT_NOT_DELETED;
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

        return "loginPage";
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

    private String generateResetPasswordEmail(String tokenLink, boolean accountExists) {
        String buttonText = (accountExists ? TITLE_RESET_PASSWORD_REQUEST : TITLE_NEW_ACCOUNT_REQUEST);

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
                .append(GenerateService.getEmailEpilogue(true))
                .append("</html>");

        return body.toString();
    }

    private String generateNewAccountEmail(String tokenLink, boolean accountExists) {
        String buttonText = (accountExists ? TITLE_RESET_PASSWORD_REQUEST : TITLE_NEW_ACCOUNT_REQUEST);

        StringBuilder body = new StringBuilder()
                .append("<html>")
                .append(EMAIL_STYLE)
                .append("<table>")
                .append("<tr>")
                .append("<td>")
                .append("Dear colleague,")
                .append("<br />")
                .append("<br />")
                .append("This e-mail was sent in response to a request to create a new IMPC Register Interest account. ");

        if (accountExists) {
            body
                    .append("As an account already exists for this e-mail address, no account will be created. You may use this link to reset your password.")
                    .append("<br />")
                    .append("<br />")
                    .append("If you made no such request, please ignore this e-mail; otherwise, please click on the link ")
                    .append("below to reset your password.");
        } else {
            body
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
                .append(GenerateService.getEmailEpilogue(true))
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