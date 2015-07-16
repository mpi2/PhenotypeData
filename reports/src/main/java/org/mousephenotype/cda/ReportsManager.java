/*******************************************************************************
 *  Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.repositories.ObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrelac on 23/06/2015.
 */

@ComponentScan("org.mousephenotype.cda.reports")
@SpringBootApplication
public class ReportsManager implements CommandLineRunner {

    @Autowired(required = true)
    ObservationRepository observations;

    private static final Logger log = LoggerFactory.getLogger(ReportsManager.class);
    private static final String PROPERTIES_FILE_ARG = "propertiesFile";
    private static final String TARGET_DIRECTORY_ARG = "targetDirectory";
    private static final String REPORTS_ARG = "reports";

    private List<ReportType> reports = new ArrayList<>();
    private String targetDirectory;
    private PropertiesConfiguration props;

    public enum ReportType {
        dataOverview("dataOverview", "Data overview"),
        PROCEDURE_COMPLETENESS("procedureCompleteness", "Procedure completeness"),
        VIABILITY("viability", "Viability"),
        FERTILITY("fertility", "Fertility"),
        DISTRIBUTION_OF_PHENOTYPE_HITS("phenotypeHits", "Distribution of phenotype hits"),
        HITS_PER_LINE("hitsPerLine", "Hits per line"),
        HITS_PER_PARAMETER_AND_PROCEDURE("hitsPerParameter", "Hits per parameter and procedure"),
        SEXUAL_DIMORPHISM_NO_BODY_WEIGHT("sexualDimorphismNoBodyWeight", "Sexual dimorphism no body weight"),
        SEXUAL_DIMORPHISM_WITH_BODY_WEIGHT("sexualDimorphismWithBodyWeight", "Sexual dimorphism with body weight"),
        GO_ANNOTATIONS_TO_PHENOTYPED_IMPC_GENES_TOOL("goAnnotations", "GO annotations to phenotyped IMPC genes tool"),
        LACZ_EXPRESSION("laczExpression", "Lacz expression"),
        ALL_GENOTYPE_PHENOTYPE_DATA("allGPData", "All genotype-phenotype data"),
        BMD_STATS("bmdStats", "BMD stats (Bone Mineral Content, excluding skull)"),
        LPGTT_STATS_CONCENTRATION("lpgttConcentration", "lpGTT stats (Fasted blood glucose concentration)"),
        LPGTT_STATS_RESPONSE("lpgttResponse", "lpGTT stats (Area under the curve glucose response)"),
        PHENOTYPE_OVERVIEW_PER_GENE("phenotypeOverview", "Phenotype overview per gene"),
        ZYGOSITY("zygosity", "Zygosity");

        String tag;
        String description;

        private ReportType(String reportTag, String description) {
            this.tag = reportTag;
            this.description = description;
        }

        public String getName() {
            return this.tag;
        }

        public String getDescription() {
            return this.description;
        }

        @Override
        public String toString() {
            return getName();
        }

        public static String toStringAll() {
            StringBuilder sb = new StringBuilder();
            String formatString = "%-40.40s%s\n";
            sb.append(String.format(formatString, "Tag", "Description"));
            sb.append(String.format(formatString, "---", "-----------"));
            for (ReportType report : ReportType.values()) {
                sb.append(String.format(formatString, report.getName(), report.getDescription()));
            }

            return sb.toString();
        }

        /**
         * Returns the <code>ReportType</code> matching <code>tag</code>, if found.
         *
         * @param reportTag the report tag
         * @return the <code>ReportType</code> matching <code>tag</code>, if found.
         * @throws IllegalArgumentException if <code>tag</code> is not found.
         */
        public static ReportType fromTag(String reportTag) throws IllegalArgumentException {
            for (ReportType reportType : values()) {
                if (reportType.getName().equals(reportTag)) {
                    return reportType;
                }
            }

            throw new IllegalArgumentException("Unknown ReportType tag '" + reportTag + "'.");
        }
    }

    public static void main(String args[]) {
        SpringApplication.run(ReportsManager.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        PropertySource ps = new SimpleCommandLinePropertySource(args);
        List<String> errors = parseAndValidate(ps);
        if (!errors.isEmpty()) {
            for (String error : errors) {
                System.out.println(error);
            }
            System.out.println();
            usage();
            System.exit(1);
        }

        dumpInputParameters();

        for (ReportType reportType : reports) {

        }


        System.exit(0);
    }


    // PRIVATE METHODS


    private void usage() {
        System.out.println("Usage:");
        System.out.println(PROPERTIES_FILE_ARG + "=properties_file" + "  " +
                TARGET_DIRECTORY_ARG + "=target_directory" + "  " +
                REPORTS_ARG + "=report_tag_1[,report_tag_2[,...]]");
        System.out.println("\n");
        System.out.println("ReportType:");
        System.out.println(ReportType.toStringAll());
    }

    /**
     * Returns an empty list if parameters are valid; else returns a list of strings containing the error text.
     *
     * @param ps A valid <code>PropertySource</code> instance of this program's parsed arguments.
     * @return an empty list if paramters are valid; else returns a list of strings containing the error text.
     */
    private List<String> parseAndValidate(PropertySource ps) {
        List<String> retVal = new ArrayList<>();

        if (ps.containsProperty(PROPERTIES_FILE_ARG)) {
            try {
                props = new PropertiesConfiguration((String) ps.getProperty(PROPERTIES_FILE_ARG));
            } catch( ConfigurationException e) {
                retVal.add("Expected required properties file.");
            }
        } else {
            retVal.add("Expected required properties file.");
        }





//        Path path = Paths.get((String) ps.getProperty(PROPERTIES_FILE_ARG));
//        if (!Files.exists(path)) {
//            retVal.add("Properties file '" + ps.getProperty(PROPERTIES_FILE_ARG) + "' does not exist or is not readable.");
//        }

        if (ps.containsProperty(TARGET_DIRECTORY_ARG)) {
            targetDirectory = (String) ps.getProperty(TARGET_DIRECTORY_ARG);
        } else {
            retVal.add("Expected required target directory.");
        }
        Path path = Paths.get((String) ps.getProperty(TARGET_DIRECTORY_ARG));
        if (!Files.exists(path)) {
            retVal.add("Target directory " + path.toAbsolutePath() + " does not exist.");
        }
        if (!Files.isWritable(path)) {
            retVal.add("Target directory " + path.toAbsolutePath() + " is not writeable.");
        }

        if (ps.containsProperty(REPORTS_ARG)) {
            String reportsCsl = (String) ps.getProperty(REPORTS_ARG);
            String[] reportTags = reportsCsl.split(",");
            for (String reportTag : reportTags) {                                                                       // Validate the tag.
                try {
                    ReportType reportType = ReportType.fromTag(reportTag);
                    reports.add(reportType);
                } catch (IllegalArgumentException | NullPointerException e) {
                    retVal.add("Unknown report tag '" + reportTag + "'.");
                }
            }
        } else {
            retVal.add("Expected at least one report.");
        }

        if (reports.isEmpty()) {
            retVal.add("Expected at least one report.");
        }

        return retVal;
    }

    private void dumpInputParameters() {
        System.out.println("\nProperties file:  " + props.getURL());
        System.out.println("Target directory: " + targetDirectory);
        System.out.println("Reports:          " + StringUtils.join(reports, ",") + "\n");
    }
}