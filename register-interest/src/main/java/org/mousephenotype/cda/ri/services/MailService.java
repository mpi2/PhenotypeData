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
import java.util.ArrayList;
import java.util.List;
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
    public void mailer() {
        int count = 0;
        logger.info("BEGIN mailer. SmtpParameters = {}.", smtpParameters);
        List<Summary> summaries = getAndUpdateChangedSummaries();
        for (Summary summary : summaries) {
            generateAndSendSummary(summary);
            riSqlUtils.updateGeneSent(summary);
            count++;
            logger.info("{} : {}", count, summary.getEmailAddress());
            _sleep(36);     // Pause so we don't exceed 100 e-mails per hour.
        }
        logger.info("END mailer. Processed {} summaries.", count);
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
        logger.info("BEGIN checker. outfileName = {}.", outputDirName);
        List<Summary> summaries = getAndUpdateChangedSummaries();
        if (!summaries.isEmpty()) {
            System.out.println("Would send updated status summary to these contacts:");
        } else {
            System.out.println("There are no updated status summaries for any contacts.");
        }
        for (Summary summary : summaries) {
            String content = generateSummary(summary);
            if (outputDir != null) {
                Path           path   = Paths.get(outputDirName, summary.getEmailAddress());
                BufferedWriter writer = new BufferedWriter(new FileWriter(path.toAbsolutePath().toString()));
                writer.append(content);
                writer.close();
            }
            count++;
            logger.info("{} : {}", count, summary.getEmailAddress());
        }
        logger.info("END checker. Processed {} summaries.{}", count,
            outputDir == null ? "" : " Output files written to " + outputDir.getAbsolutePath() + ".");
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

    public String generateSummary(Summary summary) {
        return _generateSummaryContent(summary);
    }

    public void generateAndSendSummary(Summary summary) {
        String content = generateSummary(summary);
        _sendSummary(summary, DEFAULT_SUMMARY_SUBJECT, content);
    }

    public String generateWelcome(boolean inHtml) {
        return _getWelcomeContent(inHtml);
    }

    public void generateAndSendWelcome(Contact contact) {
        _sendWelcome(contact, DEFAULT_WELCOME_SUBJECT, generateWelcome(contact.isInHtml()));
    }

    // A Summary is considered 'changed' if any of its SummaryDetails for any registered genes is different.
    public List<Summary> getAndUpdateChangedSummaries() {
        // For each contact
        //    Get a map of this contact's last SummaryDetail list indexed by geneAccessionId (lastSdByAcc)
        //    For each currentSd (this contact's current SummaryDetail list)
        //      Look up currentSd in lastSdByAcc
        //      If not found, treat currentSd as unchanged
        //      Else compare summaryDetails using SummaryDetail method that marks each changed summary. If any
        //        summary details have changed, add summary to changedSummaries.
        List<Summary> changedSummaries = new ArrayList<>();
        riSqlUtils.getContacts()
            .stream()
            .forEach((Contact c) -> {
                Summary s = getAndUpdateSummaryIfChanged(c);
                if (s != null) {
                    changedSummaries.add(s);
                }
            });
        return changedSummaries;
    }

    /**
     * @param contact
     * @return summary with changed SummaryDetail instances marked as changed. If there were no changes,
     * a null Summary is returned.
     */
    public Summary getAndUpdateSummaryIfChanged(Contact contact) {
        Map<String, SummaryDetail> lastSdByAcc = riSqlUtils.getGeneSentByEmailAddress(contact.getEmailAddress())
            .stream()
            .map(GeneSent::toSummaryDetail)
            .collect(Collectors.toMap(SummaryDetail::getGeneAccessionId, Function.identity()));

        final boolean[] changed = {false};
        Summary         summary = summaryService.getSummaryByContact(contact);
        summary.getDetails()
            .stream()
            .forEach(currentSd -> {
                SummaryDetail lastSd = lastSdByAcc.get(currentSd.getGeneAccessionId());
                if (lastSd != null) {
                    if (currentSd.markSdDifferences(lastSd)) {
                        changed[0] = true;
                    }
                }
            });
        return summary;
    }


    // PRIVATE METHODS


    // Summary content
    private String _generateSummaryContent(Summary summary) {
        if (summary == null) {
            return "No summary was found for that contact";
        }
        return new StringBuilder()
            .append(_generateSummaryPrefaceContent(summary.isInHtml()))
            .append(SummaryDetailTable.build(summary.getDetails(), summary.isInHtml()))
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

    private void _sendSummary(Summary summary, String subject, String content) {
        Message message;
        message = emailUtils.assembleEmail(subject, content, summary.getEmailAddress(),
            summary.isInHtml(), smtpParameters);
        try {
            if (message == null) {
                return;
            }
            Transport.send(message);
        } catch (MessagingException e) {
            logger.warn("Exception sending summary e-mail to {}. Skipping. Reason: {}", summary.getEmailAddress(), e.getLocalizedMessage());
            return;
        }
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