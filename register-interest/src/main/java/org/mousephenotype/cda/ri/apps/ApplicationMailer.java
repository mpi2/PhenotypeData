/*******************************************************************************
 * Copyright © 2021 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.ri.apps;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.ri.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;


@ComponentScan({"org.mousephenotype.cda.ri.apps", "org.mousephenotype.cda.ri.services"})
public class ApplicationMailer implements CommandLineRunner {
    private final Logger      logger = LoggerFactory.getLogger(this.getClass());
    private       MailService mailService;

    private final String[] OPT_SEND = {"s", "send"};

    private final String OPT_SEND_DESCRIPTION =
        "Each qualifying e-mail is generated, but by default it is not sent (for safety reasons, so as to avoid" +
            " sending spam by mistake). Specify this flag when you want to generate and send each qualifying e-mail.";

    private final String[] OPT_HELP = {"h", "help"};

    private boolean help = false;
    private boolean send = false;

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

    public static final String USAGE = "Usage: [--help/-h] | [--send/s]";

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        initialise(args);
        mailService.mailer(send);
    }


    // PRIVATE / PROTECTED METHODS


    private void initialise(String[] args) throws IOException {

        OptionParser parser  = new OptionParser();
        parser.allowsUnrecognizedOptions();
        parseOptions(parser, args);

        logger.info("Program Arguments: " + StringUtils.join(args, ", "));

        if (help) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }
    }

    protected OptionSet parseOptions(OptionParser parser, String[] args) {
        OptionSet options = null;
        parser.allowsUnrecognizedOptions();
        parser.acceptsAll(Arrays.asList(OPT_HELP), "Display help/usage information\t" + USAGE)
            .forHelp();
        parser.acceptsAll(Arrays.asList(OPT_SEND), OPT_SEND_DESCRIPTION)
            .forHelp();
        try {
            options = parser.parse(args);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println(usage());
            System.exit(1);
        }
        help = (options.has("help"));
        send = (options.has("send"));
        return options;
    }

    private String usage() {
        return USAGE;
    }
}