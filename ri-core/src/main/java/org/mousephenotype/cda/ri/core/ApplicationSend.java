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

package org.mousephenotype.cda.ri.core;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.ri.core.services.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;


@ComponentScan
public class ApplicationSend implements CommandLineRunner {

    private final Logger       logger        = LoggerFactory.getLogger(this.getClass());
    private       CoreService  coreService;

    private final String[] OPT_SUPPRESS = {"s", "suppress"};
    private final String[] OPT_HELP = {"h", "help"};

    private boolean help    = false;
    private boolean suppress = false;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ApplicationSend.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Inject
    public ApplicationSend(CoreService coreService) {
        this.coreService = coreService;
    }


    public static final String USAGE = "Usage: [--help/-h] | [--suppress/-s]";

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {

        initialise(args);

        if (suppress) {

            logger.info("Generate and send gene status to all contacts.");
            coreService.generateAndSendAll();

        } else {

            logger.info("Generate and send gene status to contacts with gene status CHANGED since last e-mail.");
            coreService.generateAndSendDecorated();

        }
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

        parser.acceptsAll(Arrays.asList(OPT_HELP), "Display help/usage information\t" + USAGE)
                .forHelp();

        parser.acceptsAll(Arrays.asList(OPT_SUPPRESS), "Suppress the decoration indicating whether or not a gene's status has changed since the last e-mail sent")
                .forHelp();

        try {

            options = parser.parse(args);

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());
            System.out.println(usage());
            System.exit(1);
        }

        help = (options.has("help"));
        suppress = (options.has("suppress"));

        return options;
    }

    private String usage() {
        return USAGE;
    }
}