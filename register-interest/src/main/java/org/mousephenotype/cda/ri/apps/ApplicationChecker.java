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
public class ApplicationChecker implements CommandLineRunner {
    private final Logger      logger = LoggerFactory.getLogger(this.getClass());
    private       MailService mailService;

    private final String[] OPT_OUTPUT_DIR_NAME = {"o", "outputDirName"};

    private final String OPT_OUTPUT_DIR_DESCRIPTION =
        "If specified, all summary e-mail text will be written to this directory. Each summary filename is the contact's e-mail address.";

    private final String[] OPT_HELP = {"h", "help"};

    private boolean help = false;
    private String  outputDirName;

    public static void main(String[] args) {
        new SpringApplicationBuilder(ApplicationChecker.class)
            .web(WebApplicationType.NONE)
            .bannerMode(Banner.Mode.OFF)
            .logStartupInfo(false)
            .run(args);
    }

    @Inject
    public ApplicationChecker(MailService mailService) {
        this.mailService = mailService;
    }

    public static final String USAGE = "Usage: [--help/-h] | [--outputDirName/-o]";

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        initialise(args);
        mailService.checker(outputDirName);
    }


    // PRIVATE / PROTECTED METHODS


    private void initialise(String[] args) throws IOException {

        OptionParser parser = new OptionParser();
        parseOptions(parser, args);

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
        parser.acceptsAll(Arrays.asList(OPT_OUTPUT_DIR_NAME), OPT_OUTPUT_DIR_DESCRIPTION).withRequiredArg()
            .forHelp();
        try {
            options = parser.parse(args);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println(usage());
            System.exit(1);
        }

        help = (options.has("help"));
        if (options.has("outputDirName") && (options.hasArgument("outputDirName"))) {
            outputDirName = options.valueOf("outputDirName").toString();
        }

        return options;
    }

    private String usage() {
        return USAGE;
    }
}