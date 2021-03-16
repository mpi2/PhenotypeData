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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.ri.entities.SmtpParameters;
import org.mousephenotype.cda.ri.services.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;


@ComponentScan({"org.mousephenotype.cda.ri"})
public class ApplicationSend implements CommandLineRunner {

    @Value("${paBaseUrl}")
    private String paBaseUrl;

    private final Logger         logger = LoggerFactory.getLogger(this.getClass());
    private       CoreService    coreService;
    private       SmtpParameters smtpParameters;

    private final String[] OPT_NO_DECORATION = {"n", "noDecoration"};
    private final String[] OPT_SEND = {"s", "send"};

    private final String OPT_NO_DECORATION_DESCRIPTION =
            "By default, decoration is added to each gene's state in the form of an asterisk ('*') to indicate that" +
            " the gene's state has changed. Specifying this option omits this decoration.";

    private final String OPT_SEND_DESCRIPTION =
            "Each qualifying e-mail is generated, but by default it is not sent (for safety reasons, so as to avoid" +
            " sending spam by mistake). Specify this flag when you want to generate and send each qualifying e-mail.";

    private final String[] OPT_HELP = {"h", "help"};

    private boolean help         = false;
    private boolean noDecoration = false;
    private boolean send         = false;

    public static void main(String[] args) {

        new SpringApplicationBuilder(ApplicationSend.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);
    }

    @Inject
    public ApplicationSend(CoreService coreService, SmtpParameters smtpParameters) {
        this.coreService = coreService;
        this.smtpParameters = smtpParameters;
    }


    public static final String USAGE = "Usage: [--help/-h] | [[--noDecoration/-n] [--send/s]]";

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        initialise(args);

        coreService.generateAndSend(paBaseUrl, noDecoration, send, smtpParameters);
    }


    // PRIVATE / PROTECTED METHODS


    private void initialise(String[] args) throws IOException {

        OptionParser parser  = new OptionParser();
        OptionSet    options = parseOptions(parser, args);

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

        parser.acceptsAll(Arrays.asList(OPT_NO_DECORATION), OPT_NO_DECORATION_DESCRIPTION)
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
        noDecoration = (options.has("noDecoration"));
        send = (options.has("send"));

        return options;
    }

    private String usage() {
        return USAGE;
    }
}