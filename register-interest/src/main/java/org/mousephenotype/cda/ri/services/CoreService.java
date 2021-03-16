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

import org.mousephenotype.cda.ri.entities.GeneSent;
import org.mousephenotype.cda.ri.entities.SmtpParameters;
import org.mousephenotype.cda.ri.entities.Summary;
import org.mousephenotype.cda.ri.entities.SummaryWithDecoration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service
public class CoreService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private GenerateService generateService;
    private SendService sendService;

    @Inject
    public CoreService(GenerateService generateService, SendService sendService) {
        this.generateService = generateService;
        this.sendService = sendService;
    }

    public static void sleep(int numSeconds) {
        try {
            Thread.sleep(numSeconds * 1000);
        } catch (InterruptedException e) {

        }
    }

    public void generateAndSendWelcome(String emailAddress, SmtpParameters smtpParameters) {

        boolean inHtml = true;
        String welcomeText = generateService.getWelcomeContent(inHtml);
        sendService.sendWelcome(emailAddress, SendService.DEFAULT_WELCOME_SUBJECT, welcomeText, inHtml, smtpParameters);
    }


    public void generateAndSend(String paBaseUrl, boolean noDecoration, boolean send, SmtpParameters smtpParameters) {

        int     count    = 0;
        boolean inHtml   = true;

        logger.info("BEGIN generateAndSend noDecoration = {}. send = {}. SmtpParameters = {}.", noDecoration, send, smtpParameters);

        Map<String, Summary> summaries = generateService.getAllSummariesByEmailAddress();
        for (Summary summary : summaries.values()) {

            Map<String, GeneSent> genesSentByGeneAccessionId = generateService.getGeneSentStatusByGeneAccessionId(summary.getEmailAddress());
            Summary decoratedSummary = new SummaryWithDecoration(summary, genesSentByGeneAccessionId);
            if ( ! ((SummaryWithDecoration) decoratedSummary).isDecorated()) {
                continue;
            }

            if ( ! noDecoration) {
                summary = decoratedSummary;
            }

            String content = generateService.getSummaryContent(paBaseUrl, summary, true);

            if (send) {

                sendService.sendSummary(summary, SendService.DEFAULT_SUMMARY_SUBJECT, content, inHtml, smtpParameters);
            }

            count++;
            logger.info("{} : {}", count, summary.getEmailAddress());

            // Pause so we don't exceed 100 e-mails per hour.
            if (send) {
                sleep(36);
            }
        }

        logger.info("END generateAndSend. Processed {} summaries.", count);
    }
}