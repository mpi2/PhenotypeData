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

import org.mousephenotype.cda.ri.entities.SmtpParameters;
import org.mousephenotype.cda.ri.entities.Summary;
import org.mousephenotype.cda.ri.utils.EmailUtils;
import org.mousephenotype.cda.ri.utils.RiSqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

@Service
public class SendService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String DEFAULT_WELCOME_SUBJECT = "Welcome to the IMPC gene registration system";
    public static final String DEFAULT_SUMMARY_SUBJECT = "Complete list of IMPC genes for which you have registered interest";

    private RiSqlUtils riSqlUtils;
    private EmailUtils emailUtils = new EmailUtils();


    @Inject
    public SendService(
            RiSqlUtils riSqlUtils
    ) {
        this.riSqlUtils = riSqlUtils;
    }


    public void sendSummary(Summary summary, String subject, String content, boolean inHtml, SmtpParameters smtpParameters) {
        Message message;

        message = emailUtils.assembleEmail(subject, content, summary.getEmailAddress(), inHtml, smtpParameters);

        try {

            if (message == null) {
                return;
            }

            Transport.send(message);
            riSqlUtils.updateGeneSent(summary);

        } catch (MessagingException e) {

            logger.warn("Exception sending summary e-mail to {}. Skipping. Reason: {}", summary.getEmailAddress(), e.getLocalizedMessage());
            return;
        }

    }


    public void sendWelcome(String emailAddress, String subject, String welcomeText, boolean inHtml, SmtpParameters smtpParameters) {


        Message message = emailUtils.assembleEmail(subject, welcomeText, emailAddress, inHtml, smtpParameters);
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
}