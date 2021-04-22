/*******************************************************************************
 *  Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this targetFile except in compliance
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

package org.mousephenotype.cda.reports;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.reports.support.ReportsManagerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.util.List;

/**
 * Class to kick off reports from the command-line.
 *
 * Example command used to build the 3.1 reports:
 *  java -Dprofile=reports3.1 -jar /Users/mrelac/workspace/PhenotypeData/reports/target/reports-1.0.0-exec.jar --targetDirectory=/Users/mrelac/reports/3.1
 * Created by mrelac on 23/06/2015.
 */

@EnableCaching
@ComponentScan({"org.mousephenotype.cda.reports"})
public class ReportsManager implements CommandLineRunner {


    @Autowired
    private LaczExpression lacZExpression;

    @Autowired
    private GlucoseConcentration glucoseConcentration;

    @Autowired
    private GlucoseResponse glucoseResponse;

    @Autowired
    private DataOverview dataOverview;

    @Autowired
    private BoneMineralDensity boneMineralDensity;

    @Autowired
    private Fertility fertility;

    @Autowired
    private PhenotypeHitsPerLine phenotypeHitsPerLine;

    @Autowired
    private PhenotypeHitsPerParameterAndProcedure phenotypeHitsPerParameterAndProcedure;

    @Autowired
    private ImpcGafReport impcGafReport;

    @Autowired
    private ImpcPValues impcPValues;

    @Autowired
    private MetabolismCalorimetry metabolismCalorimetry;

    @Autowired
    private MetabolismCBC metabolismCBC;

    @Autowired
    private MetabolismDEXA metabolismDEXA;

    @Autowired
    private MetabolismIPGTT metabolismIPGTT;

    @Autowired
    private PhenotypeHitsPerTopLevelMPTerm phenotypeHitsPerTopLevelMPTerm;

    @Autowired
    private PhenotypeHitsPerGene phenotypeHitsPerGene;

    @Autowired
    private Viability viability;

    @Autowired
    private GeneAndMPTermAssociation geneAndMPTermAssociation;


    private ReportsManagerParser parser = new ReportsManagerParser();
    private static final Logger log = LoggerFactory.getLogger(ReportsManager.class);
    public enum ReportType {
//        BONE_MINERAL_DENSITY("boneMineralDensity", "Bone Mineral Density statistics (Bone Mineral Content, excluding skull)"),
        DATA_OVERVIEW("dataOverview", "Data overview"),
        FERTILITY("fertility", "Fertility"),
        GENE_AND_MP_TERM_ASSOCIATION("geneAndMPTermAssociation", "GeneAndMPTermAssociation"),
//        GLUCOSE_CONCENTRATION("glucoseConcentration", "lpGTT stats (Fasted blood glucose concentration)"),
//        GLUCOSE_RESPONSE("glucoseResponse", "lpGTT stats (Area under the curve glucose response)"),
//        IMPC_GAF("impcGaf", "IMPC GAF"),
//        IMPC_P_VALUES("impcPValues", "IMPC p-values"),
        LACZ_EXPRESSION("laczExpression", "Lacz expression"),
//        METABOLISM_CALORIMETRY("metabolismCalorimetry", "Metabolism calorimetry"),
//        METABOLISM_CBC("metabolismCBC", "Metabolism CBC"),
//        METABOLISM_DEXA("metabolismDEXA", "Metabolism DEXA"),
//        METABOLISM_IPGTT("metabolismIPGTT", "Metabolism IPGTT"),
        PHENOTYPE_HITS_PER_GENE("phenotypeHitsPerGene", "Phenotype hits per gene"),
        PHENOTYPE_HITS_PER_LINE("phenotypeHitsPerLine", "Phenotype hits per line"),
        PHENOTYPE_HITS_PER_PARAMETER_AND_PROCEDURE("phenotypeHitsPerParameterAndProcedure", "Hits per parameter and procedure"),
        PHENOTYPE_HITS_PER_TOP_LEVEL_MP_TERM("phenotypeHitsPerTopLevelMPTerm", "Distribution of phenotype hits"),
//        PROCEDURE_COMPLETENESS_ALL("procedureCompletenessAll", "Procedure completeness for All data sources"),
//        PROCEDURE_COMPLETENESS_IMPC("procedureCompletenessImpc", "Procedure completeness for IMPC data source"),
        VIABILITY("viability", "Viability");

        String tag;
        String description;

        ReportType(String reportTag, String description) {
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
            sb.append(String.format(formatString, "Name", "Description"));
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

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ReportsManager.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }

    @Override
    public void run(String... args) throws Exception {
        File file = null;

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            for (String error : errors) {
                System.out.println(error);
            }
            System.out.println();
            usage();
            System.exit(1);
        }

        if (parser.showHelp()) {
            usage();
            System.exit(0);
        }

        logInputParameters();

        int systemExitCode = 0;
        for (ReportType reportType : parser.getReports()) {
            try {
                switch (reportType) {

//                    case BONE_MINERAL_DENSITY:
//                        boneMineralDensity.run(args);
//                        file = boneMineralDensity.targetFile;
//                        break;
//
//                    case GLUCOSE_CONCENTRATION:
//                        glucoseConcentration.run(args);
//                        file = glucoseConcentration.targetFile;
//                        break;
//
//                    case GLUCOSE_RESPONSE:
//                        glucoseResponse.run(args);
//                        file = glucoseResponse.targetFile;
//                        break;

                    case DATA_OVERVIEW:
                        dataOverview.run(args);
                        file = dataOverview.targetFile;
                        break;

                    case FERTILITY:
                        fertility.run(args);
                        file = fertility.targetFile;
                        break;

                    case GENE_AND_MP_TERM_ASSOCIATION:
                        geneAndMPTermAssociation.run(args);
                        file = geneAndMPTermAssociation.targetFile;
                        break;

//                    case IMPC_P_VALUES:
//                        impcPValues.run(args);
//                        file = impcPValues.targetFile;
//                        break;

                    case LACZ_EXPRESSION:
                        lacZExpression.run(args);
                        file = lacZExpression.targetFile;
                        break;

                    case PHENOTYPE_HITS_PER_GENE:
                        phenotypeHitsPerGene.run(args);
                        file = phenotypeHitsPerGene.targetFile;
                        break;

                    case PHENOTYPE_HITS_PER_LINE:
                        phenotypeHitsPerLine.run(args);
                        file = phenotypeHitsPerLine.targetFile;
                        break;

                    case PHENOTYPE_HITS_PER_PARAMETER_AND_PROCEDURE:
                        phenotypeHitsPerParameterAndProcedure.run(args);
                        file = phenotypeHitsPerParameterAndProcedure.targetFile;
                        break;

                    case PHENOTYPE_HITS_PER_TOP_LEVEL_MP_TERM:
                        phenotypeHitsPerTopLevelMPTerm.run(args);
                        file = phenotypeHitsPerTopLevelMPTerm.targetFile;
                        break;

//                    case METABOLISM_CALORIMETRY:
//                        metabolismCalorimetry.run(args);
//                        file = metabolismCalorimetry.targetFile;
//                        break;
//
//                    case METABOLISM_CBC:
//                        metabolismCBC.run(args);
//                        file = metabolismCBC.targetFile;
//                        break;
//
//                    case METABOLISM_DEXA:
//                        metabolismDEXA.run(args);
//                        file = metabolismDEXA.targetFile;
//                        break;
//
//                    case METABOLISM_IPGTT:
//                        metabolismIPGTT.run(args);
//                        file = metabolismIPGTT.targetFile;
//                        break;

//                    case IMPC_GAF:
//                        impcGafReport.run(args);
//                        file = impcGafReport.targetFile;
//                        break;

                    case VIABILITY:
                        viability.run(args);
                        file = viability.targetFile;
                        break;
                }

                String fqFilename = (file != null ? file.getAbsolutePath() : "<unknown>");
                log.info("Created report '" + reportType + "' in " + fqFilename + "\n");

            } catch (Exception e) {

                log.error("FAILED to create report '" + reportType + " in " + parser.getTargetDirectory() + ". Reason: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        if (parser.showHelp()) {
            usage();
        }

        System.exit(systemExitCode);
    }


    // PRIVATE METHODS


    private void usage() {
        String[] commands = {
              "[[[[--" + ReportsManagerParser.REPORTS_ARG          + "]=report1],report2], ...]"
            , "   [--" + ReportsManagerParser.TARGET_DIRECTORY_ARG + "=target_directory]"
            , "   [--" + ReportsManagerParser.REPORT_FORMAT_ARG    + "={csv | tsv}]"
            , "   [--" + ReportsManagerParser.PREFIX_ARG           + "=prefix]"
            , "   [--" + ReportsManagerParser.HELP_ARG             + "]"
        };
        String[] defaults = {
              "Default is all reports"
            , "Default is " + ReportsManagerParser.DEFAULT_TARGET_DIRECTORY
            , "Default is " + ReportsManagerParser.DEFAULT_REPORT_FORMAT
            , "Default is none"
            , ""
        };
        System.out.println("Usage:");
        for (int i = 0; i < commands.length; i++) {
            System.out.println(String.format("%-50.50s %-80.80s", commands[i], defaults[i]));
        }
        System.out.println();
        System.out.println("Reports:");
        System.out.println(ReportType.toStringAll());
    }

    private void logInputParameters() {
        log.info("Reports:          " + StringUtils.join(parser.getReports(), ","));
        log.info("Target directory: " + parser.getTargetDirectory());
        log.info("Report format:    " + (parser.getReportFormat() == null ? "<omitted>" : parser.getReportFormat()));
        log.info("Properties targetFile:  " + (parser.getApplicationProperties() == null ? "<omitted>" : parser.getApplicationProperties().getURL().toString()));
        log.info("Prefix:           " + (parser.getPrefix() == null ? "<omitted>" : parser.getPrefix()));
    }
}