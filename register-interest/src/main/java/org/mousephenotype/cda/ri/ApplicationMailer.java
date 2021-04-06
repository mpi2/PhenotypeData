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

package org.mousephenotype.cda.ri;

import org.mousephenotype.cda.ri.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import javax.inject.Inject;


@ComponentScan({"org.mousephenotype.cda.ri"})
public class ApplicationMailer implements CommandLineRunner {

    private final Logger      logger = LoggerFactory.getLogger(this.getClass());
    private       MailService mailService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(ApplicationMailer.class)
            .web(WebApplicationType.NONE)
            .bannerMode(Banner.Mode.OFF)
            .logStartupInfo(false)
            .run(args);
    }

    @Inject
    public ApplicationMailer(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) {
        mailService.mailer();
    }
}