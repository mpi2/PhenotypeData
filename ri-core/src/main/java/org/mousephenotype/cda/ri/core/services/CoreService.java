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

import org.mousephenotype.cda.ri.core.entities.GeneSent;
import org.mousephenotype.cda.ri.core.entities.Summary;
import org.mousephenotype.cda.ri.core.entities.SummaryWithDecoration;
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

    public void generateAndSendWelcome(String emailAddress) {

        boolean inHtml = true;
        String welcomeText = generateService.getWelcomeContent(inHtml);
        sendService.sendWelcome(emailAddress, SendService.DEFAULT_WELCOME_SUBJECT, welcomeText, inHtml);
    }


    public void generateAndSendAll() {

        int     count    = 0;
        boolean inHtml   = true;

        logger.info("BEGIN generateAndSendAll");

        Map<String, Summary> summaries = generateService.getAllSummariesByEmailAddress();
        for (Summary summary : summaries.values()) {

            String content = generateService.getSummaryContent(summary, inHtml);
            sendService.sendSummary(summary, SendService.DEFAULT_SUMMARY_SUBJECT, content, inHtml);
            count++;

            // Pause for 36 seconds so we don't exceed 100 e-mails per hour.
            sleep(36);
        }

        logger.info("END generateAndSendAll. Processed {} summaries.", count);
    }

    public void generateAndSendDecorated() {

        int count = 0;

        logger.info("BEGIN generateAndSendDecorated");

        Map<String, Summary> summaries = generateService.getAllSummariesByEmailAddress();
        for (Summary summaryWithoutDecoration : summaries.values()) {

            Map<String, GeneSent> genesSentByGeneAccessionId = generateService.getGeneSentStatusByGeneAccessionId(summaryWithoutDecoration.getEmailAddress());
            SummaryWithDecoration summaryWithDecoration = new SummaryWithDecoration(summaryWithoutDecoration, genesSentByGeneAccessionId);
            if (summaryWithDecoration.isDecorated()) {

                // At least one gene status component is decorated. Send an e-mail.

                String content = generateService.getSummaryContent(summaryWithDecoration, true);
                sendService.sendSummary(summaryWithDecoration, SendService.DEFAULT_SUMMARY_SUBJECT, content, true);
                count++;

                // Pause for 36 seconds so we don't exceed 100 e-mails per hour.
                sleep(36);
            }
        }

        logger.info("END generateAndSendDecorated. Processed {} summaries.", count);
    }
}