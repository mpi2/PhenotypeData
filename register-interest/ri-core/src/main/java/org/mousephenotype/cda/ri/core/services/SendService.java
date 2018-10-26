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

package org.mousephenotype.cda.ri.core.services;

import org.mousephenotype.cda.ri.core.entities.Summary;
import org.mousephenotype.cda.ri.core.utils.EmailUtils;
import org.mousephenotype.cda.ri.core.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.validation.constraints.NotNull;

@Service
public class SendService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String DEFAULT_WELCOME_SUBJECT = "Welcome to the IMPC gene registration system";
    public static final String DEFAULT_SUMMARY_SUBJECT = "Complete list of IMPC genes for which you have registered interest";

    private SqlUtils   sqlUtils;
    private EmailUtils emailUtils = new EmailUtils();

    @NotNull
    @Value("${mail.smtp.host}")
    private String smtpHost;

    @NotNull
    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @NotNull
    @Value("${mail.smtp.from}")
    private String smtpFrom;

    @NotNull
    @Value("${mail.smtp.replyto}")
    private String smtpReplyto;


    @Inject
    public SendService(
            SqlUtils sqlUtils,
            String smtpHost,
            Integer smtpPort,
            String smtpFrom,
            String smtpReplyto
    ) {
        this.sqlUtils = sqlUtils;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpFrom = smtpFrom;
        this.smtpReplyto = smtpReplyto;
    }


    public void sendSummary(Summary summary, String subject, String content, boolean inHtml) {
        Message message;

        message = emailUtils.assembleEmail(smtpHost, smtpPort, smtpFrom, smtpReplyto, subject, content, summary.getEmailAddress(), inHtml);

        try {

            Transport.send(message);
            sqlUtils.updateGeneSent(summary);

        } catch (MessagingException e) {

            logger.warn("Exception sending summary e-mail to {}. Skipping. Reason: {}", summary.getEmailAddress(), e.getLocalizedMessage());
            return;
        }

    }


    public void sendWelcome(String emailAddress, String subject, String welcomeText, boolean inHtml) {


        Message message = emailUtils.assembleEmail(smtpHost, smtpPort, smtpFrom, smtpReplyto, subject, welcomeText, emailAddress, inHtml);
        String recipient = null;

        try {
            recipient = message.getRecipients(Message.RecipientType.TO)[0].toString();
            Transport.send(message);

        } catch (MessagingException e) {

            logger.warn("SEND of welcome message to " + recipient + " failed. Skipping... Reason: " + e.getLocalizedMessage());
        }
    }
}