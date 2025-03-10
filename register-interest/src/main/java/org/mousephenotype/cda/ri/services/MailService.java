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

package org.mousephenotype.cda.ri.services;

import org.mousephenotype.cda.ri.entities.Contact;
import org.mousephenotype.cda.ri.entities.GeneSent;
import org.mousephenotype.cda.ri.exceptions.InterestException;
import org.mousephenotype.cda.ri.pojo.SmtpParameters;
import org.mousephenotype.cda.ri.pojo.Summary;
import org.mousephenotype.cda.ri.pojo.SummaryDetail;
import org.mousephenotype.cda.ri.pojo.SummaryDetailTable;
import org.mousephenotype.cda.ri.utils.EmailUtils;
import org.mousephenotype.cda.ri.utils.RiSqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String DEFAULT_WELCOME_SUBJECT = "Welcome to the IMPC gene registration system";
    public static final String DEFAULT_SUMMARY_SUBJECT = "Complete list of IMPC genes for which you have registered interest";
    public static final String PRIVACY_POLICY_LINK     = "https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices";
    public static final String MOUSEINFORMATICS_EMAIL  = "mouse-helpdesk@ebi.ac.uk";

    private EmailUtils     emailUtils = new EmailUtils();
    private String         paBaseUrl;
    private SummaryService summaryService;
    private RiSqlUtils     riSqlUtils;
    private SmtpParameters smtpParameters;

    private Map<String, String> messagingExceptionByEmailAddress = new HashMap<>();

    @Inject
    public MailService(SummaryService summaryService, RiSqlUtils riSqlUtils, String paBaseUrl,
                       SmtpParameters smtpParameters) {
        this.summaryService = summaryService;
        this.riSqlUtils = riSqlUtils;
        this.paBaseUrl = paBaseUrl;
        this.smtpParameters = smtpParameters;
    }

    /**
     * This is the main entry point for e-mail updates to contacts with changed gene statuses.
     * This generate-and-send operation is meant to be idempotent such that if this method is
     * terminated before all summaries have been sent, previously sent summaries will not be re-sent.
     */
    public void mailer(boolean doSend) {
        int sentCount = 0;
        int expectedCount = 0;

        Map<String, String> changedSummaryContentByEmailAddress = _getChangedSummaryContentByEmailAddress();
        logger.info("BEGIN mailer. SmtpParameters = {}", smtpParameters);
        logger.info("  Expecting to send {} e-mails.", changedSummaryContentByEmailAddress.size());
        for (Map.Entry<String, String> entry : changedSummaryContentByEmailAddress.entrySet()) {
            boolean wasSent;
            Summary summary = summaryService.getSummaryByContact(summaryService.getContact(entry.getKey()));
            expectedCount++;
            if (doSend) {
                wasSent = _sendSummary(summary, DEFAULT_SUMMARY_SUBJECT, entry.getValue());
                if (wasSent) {
                    riSqlUtils.updateGeneSent(summary);
                    sentCount++;
                    logger.info("{} : {}", sentCount, summary.getEmailAddress());
                    _sleep(36);
                }
            }
        }
        if (doSend) {
            logger.info("END mailer. Processed and sent {} e-mails.", sentCount);
            if ( ! messagingExceptionByEmailAddress.isEmpty()) {
               logger.info("There were {} messaging exceptions. emailAddress: reason:",
                   messagingExceptionByEmailAddress.size());
               messagingExceptionByEmailAddress
                   .entrySet()
                   .stream()
                   .forEach(entry -> logger.info("  {}: {}", entry.getKey(), entry.getValue()));
            }
        } else {
            logger.info("END mailer. Would have processed {} e-mails but sent none as the 'send' flag was not specified.", expectedCount);
        }
    }

    /**
     * Generate a list of all changed summary e-mails. if 'outputDirName' is not null, write all summary email text to
     * this directory, each summary file name is the contact's email address.
     * <i>NOTE:</i> if 'outputDirName' is specified, it must either:
     * <ul>
     *     <li>not yet exist and be writeable (it will then be created)</li>
     *     <li>exist but be empty (if it is not empty, an exception is thrown)</li>
     * </ul>, or if it already exists and be empty, or an exception is thrown
     * <p>
     * No e-mails are sent and gene_sent is not updated.
     *
     * @param outputDirName if not null, write all summary email text files, each named <i>emailAddress</i>, here.
     */
    public void checker(String outputDirName) throws Exception {
        File outputDir = null;
        if (outputDirName != null) {
            Path outputDirPath = Paths.get(outputDirName);
            if (Files.exists(outputDirPath)) {
                if (!Files.isDirectory(outputDirPath)) {
                    logger.error("Requested output directory {} already exists and is not a directory.", outputDirPath.toAbsolutePath());
                    throw new InterestException();
                }
                if (!_isEmpty(outputDirPath)) {
                    logger.error("Requested output directory {} is not empty.", outputDirPath.toAbsolutePath());
                    throw new InterestException();
                }
            } else {
                Files.createDirectories(outputDirPath);
            }
            outputDir = new File(outputDirName);
            if (!outputDir.canWrite()) {
                logger.error("Unable to write to requested output directory {}.", outputDirPath.toAbsolutePath());
                throw new InterestException();
            }
        }
        int count = 0;
        logger.info("BEGIN checker. outputDirName = {}", outputDirName);

        Map<String, String> changedSummaryContentByEmailAddress = _getChangedSummaryContentByEmailAddress();
        if ( ! changedSummaryContentByEmailAddress.isEmpty()) {
            System.out.println("Would send updated status summary to these contacts:");
        } else {
            System.out.println("There are no updated status summaries for any contacts.");
        }
        for (Map.Entry<String, String> entry : changedSummaryContentByEmailAddress.entrySet()) {
            if (outputDir != null) {
                Path           path   = Paths.get(outputDirName, entry.getKey());
                BufferedWriter writer = new BufferedWriter(new FileWriter(path.toAbsolutePath().toString()));
                writer.append(entry.getValue());
                writer.close();
            }
            count++;
            logger.info("{} : {}", count, entry.getKey());
        }

        logger.info("END checker. Processed {} summaries.{}", count,
            outputDir == null ? "" : " Output files written to " + outputDir.getAbsolutePath() + ".");
    }

    // For each contact/summary
    //   get map of last SummaryDetail indexed by geneAccessionId
    //   For each currentSd
    //     if currentSd is found in lastSdsByAcc map
    //       if currentSd != lastSd
    //         generate content
    //         add to contentMapByEmailAddress
    //         Continue to next summary
    //   return contentMapByEmailAddress
    private Map<String, String> _getChangedSummaryContentByEmailAddress() {
        Map<String, String> summaryContentByEmailAddress = new HashMap<>();
        riSqlUtils.getContacts()
            .stream()
            .forEach((Contact c) -> {
                Summary                    current      = summaryService.getSummaryByContact(c);
                Map<String, SummaryDetail> lastSdsByAcc = getLastSdsByAcc(c.getEmailAddress());
                for (SummaryDetail currentSd : current.getDetails()) {
                    SummaryDetail lastSd = lastSdsByAcc.get(currentSd.getGeneAccessionId());
                    if ( ! currentSd.equals(lastSd)) {
                        summaryContentByEmailAddress.put(current.getEmailAddress(), _generateSummaryContent(current, lastSdsByAcc));
                        break;
                    }
                }
            });

        return summaryContentByEmailAddress;
    }

    public static final String generateEmailEpilogue(boolean inHtml) {
        StringBuilder body = new StringBuilder();
        body
            .append("You may review our e-mail list privacy policy at:")
            .append(inHtml ? "<br /><br />" : "\n\n")
            .append(inHtml ? SummaryDetailTable.buildHtmlCell("div", PRIVACY_POLICY_LINK, PRIVACY_POLICY_LINK) : PRIVACY_POLICY_LINK + "\n")
            .append(inHtml ? "<br />" : "\n")
            .append("For further information / enquiries please write to ")
            .append(inHtml ? "<a href=\"mailto: " + MOUSEINFORMATICS_EMAIL + "\">" + MOUSEINFORMATICS_EMAIL + "</a>" : MOUSEINFORMATICS_EMAIL)
            .append(".")
            .append(inHtml ? "<br /><br />" : "\n\n")
            .append("Best Regards,\n")
            .append(inHtml ? "<br /><br />" : "\n\n")
            .append("The IMPC team");

        return body.toString();
    }

    public String generateSummary(Summary summary, Map<String, SummaryDetail> lastSdsByAcc) {
        return _generateSummaryContent(summary, lastSdsByAcc);
    }

    // NOTE: This method is used by MailServiceTest methods that send real e-mails. Those tests are commented out
    //       but if uncommented they call the method below (and they send real e-mails).
    public void generateAndSendSummary(Summary summary, Map<String, SummaryDetail> lastSdsByAcc) {
        String content = generateSummary(summary, lastSdsByAcc);
        _sendSummary(summary, DEFAULT_SUMMARY_SUBJECT, content);
    }

    public String generateWelcome(boolean inHtml) {
        return _getWelcomeContent(inHtml);
    }

    public void generateAndSendWelcome(Contact contact) {
        _sendWelcome(contact, DEFAULT_WELCOME_SUBJECT, generateWelcome(contact.isInHtml()));
    }


    // PROTECTED METHODS


    protected Map<String, SummaryDetail> getLastSdsByAcc(String emailAddress) {
        Map<String, SummaryDetail> lastSdsByAcc = riSqlUtils.getGeneSentByEmailAddress(emailAddress)
            .stream()
            .map(GeneSent::toSummaryDetail)
            .collect(Collectors.toMap(SummaryDetail::getGeneAccessionId, Function.identity()));
        return lastSdsByAcc;
    }


    // PRIVATE METHODS


    // Summary content
    private String _generateSummaryContent(Summary summary, Map<String, SummaryDetail> lastSdsByAcc) {
        if (summary == null) {
            return "No summary was found for that contact";
        }
        return new StringBuilder()
            .append(_generateSummaryPrefaceContent(summary.isInHtml()))
            .append(SummaryDetailTable.build(summary.getDetails(), lastSdsByAcc, summary.isInHtml()))
            .append(summary.isInHtml() ? "<br />" : "\n")
            .append("* Indicates a status has changed since the last e-mail sent to you.")
            .append(summary.isInHtml() ? "<br /><br />" : "\n\n")
            .append(generateEmailEpilogue(summary.isInHtml()))
            .toString();
    }

    private final String _generateSummaryPrefaceContent(boolean inHtml) {
        StringBuilder sb = new StringBuilder();
        sb
            .append("Dear colleague,\n")

            .append(inHtml ? "<br /><br />" : "\n\n")

            .append("Below please find a summary of the IMPC genes for which you have registered interest.\n")

            .append(inHtml ? "<br /><br />" : "\n\n")

            .append("You have previously joined the IMPC ")
            .append(inHtml ? "<i>" : "'")
            .append("Register Interest")
            .append(inHtml ? "</i>" : "'")
            .append(" ")
            .append("list, which records your email address and genes for which you would like updates on mouse knockout, production, and phenotyping.\n")

            .append(inHtml ? "<br /><br />" : "\n\n")

            .append("You may manage the list of genes for which you have registered interest by visiting the IMPC ")
            .append(inHtml ? "<a href=\"" + paBaseUrl + "/summary" + "\">" : "'")
            .append("summary")
            .append(inHtml ? "</a>" : "'")
            .append(" page at " + paBaseUrl + "/summary.\n")

            .append(inHtml ? "<br /><br />" : "\n\n")
        ;

        return sb.toString();
    }

    // Return true if e-mail was sent; false if it was not
    private boolean _sendSummary(Summary summary, String subject, String content) {
        Message message;
        message = emailUtils.assembleEmail(subject, content, summary.getEmailAddress(),
            summary.isInHtml(), smtpParameters);
        try {
            if (message == null) {
                return false;
            }
            Transport.send(message);
        } catch (MessagingException e) {
            messagingExceptionByEmailAddress.put(summary.getEmailAddress(), e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    // Welcome

    /**
     * @param inHtml boolean: returns html-wrapped string if true; plain text otherwise
     * @return An HTML string containing the static welcome text sent to newly registered contacts.
     */
    private static final String _getWelcomeContent(boolean inHtml) {
        StringBuilder sb = new StringBuilder();
        sb
            .append("Dear colleague,")

            .append(inHtml ? "<br /><br />" : "\n\n")

            .append("Thank you for registering your interest in genes with the IMPC. As a benefit of having registered, ")
            .append("you will receive an e-mail notification whenever the status of the gene(s) for which you have registered ")
            .append("interest changes.\n")

            .append("\n")

            .append("You may register or unregister for any genes of your choice by either:\n")

            .append(inHtml ? "<ul>" : "")

            .append(inHtml ? "<li>" : "\t* ")
            .append("clicking the link in the ")
            .append(inHtml ? "<b>" : "")
            .append("Search")
            .append(inHtml ? "</b>" : "")
            .append(" ")
            .append("page's ")
            .append(inHtml ? "<i>" : "")
            .append("Register")
            .append(inHtml ? "</i>" : "")
            .append(" ")
            .append("column corresponding to the gene of interest, or ")
            .append(inHtml ? "</li>" : "\n")

            .append(inHtml ? "<li>" : "\t* ")
            .append("clicking on the button on the ")
            .append(inHtml ? "<b>" : "")
            .append("Gene")
            .append(inHtml ? "</b>" : "")
            .append(" ")
            .append("page just below the gene name")
            .append(inHtml ? "</li>" : "\n")

            .append(inHtml ? "</ul>" : "")

            .append("\n")

            .append("Clicking the ")
            .append(inHtml ? "<i>" : "")
            .append("My genes")
            .append(inHtml ? "</i>" : "")
            .append(" ")
            .append("link on the ")
            .append(inHtml ? "<b>" : "")
            .append("Search")
            .append(inHtml ? "</b>" : "")
            .append(" ")
            .append("page will take you to a page showing the genes for which you have already registered. On this page you may:")

            .append(inHtml ? "<ul>" : "")

            .append(inHtml ? "<li>" : "\t* ")
            .append("unregister by clicking the ")
            .append(inHtml ? "<i>" : "")
            .append("Unregister")
            .append(inHtml ? "</i>" : "")
            .append(" ")
            .append("button")
            .append(inHtml ? "</li>" : "\n")

            .append(inHtml ? "<li>" : "\t* ")
            .append("reset your password by clicking the ")
            .append(inHtml ? "<i>" : "")
            .append("Reset password")
            .append(inHtml ? "</i>" : "")
            .append(" ")
            .append("button")
            .append(inHtml ? "</li>" : "\n")

            .append(inHtml ? "<li>" : "\t* ")
            .append("delete your account by clicking the")
            .append(inHtml ? "<i>" : "")
            .append("Delete account")
            .append(inHtml ? "</i>" : "")
            .append(" ")
            .append("button ")
            .append(inHtml ? "<b><i>" : "")
            .append("Warning: deleting your account will delete all genes for which you have registered interest, as well ")
            .append("as any history. This action is permanent and cannot be undone, so please use with caution.")
            .append(inHtml ? "</i></b>" : "")

            .append(inHtml ? "</ul>\n" : "\n")
            .append("\n")

            .append(generateEmailEpilogue(inHtml))
        ;

        return sb.toString();
    }

    private boolean _isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                return !entries.findFirst().isPresent();
            }
        }
        return false;
    }

    private void _sendWelcome(Contact contact, String subject, String welcomeText) {
        Message message = emailUtils.assembleEmail(subject, welcomeText, contact.getEmailAddress(),
            contact.isInHtml(), smtpParameters);
        String recipient = null;
        try {
            if (message == null) {
                return;
            }
            recipient = message.getRecipients(Message.RecipientType.TO)[0].toString();
            Transport.send(message);
        } catch (MessagingException e) {
            logger.warn("SEND of welcome message to " + recipient + " failed. Skipping... Reason: " + e.getLocalizedMessage());
        }
    }


    // Other
    private static void _sleep(int numSeconds) {
        try {
            Thread.sleep(numSeconds * 1000);
        } catch (InterruptedException e) {

        }
    }
}