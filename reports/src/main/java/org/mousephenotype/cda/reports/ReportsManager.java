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
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import java.io.File;
import java.util.List;

/**
 * Class to kick off reports from the command-line.
 *
 * Example command used to build the 3.1 reports:
 *  java -Dprofile=reports3.1 -jar /Users/mrelac/workspace/PhenotypeData/reports/target/reports-1.0.0-exec.jar --targetDirectory=/Users/mrelac/reports/3.1
 *
 * NOTE: The ReportsManager is invoked by the data load process; thus datarelease.properties is included in the PropertySource below.
 *
 * Created by mrelac on 23/06/2015.
 */

@PropertySource({
        "file:${user.home}/configfiles/${profile}/application.properties",
        "file:${user.home}/configfiles/${profile}/datarelease.properties"
})
@SpringBootApplication
public class ReportsManager implements CommandLineRunner {


    @Autowired
    private LaczExpressionReport lacZExpressionReport;

    @Autowired
    private BmdStatsGlucoseConcentrationReport bmdStatsGlucoseConcentrationReport;

    @Autowired
    private BmdStatsGlucoseResponseReport bmdStatsGlucoseResponseReport;

    @Autowired
    private DataOverviewReport dataOverviewReport;

    @Autowired
    private BmdStatsReport bmdStatsReport;

    @Autowired
    private FertilityReport fertilityReport;

    @Autowired
    private HitsPerLineReport hitsPerLineReport;

    @Autowired
    private HitsPerParameterAndProcedureReport hitsPerParameterAndProcedureReport;

    @Autowired
    private ImpcGafReport impcGafReport;

    @Autowired
    private ImpcPValuesReport impcPValuesReport;

    @Autowired
    private ExtractValidateCdabaseReport extractValidateCdabaseReport;

    @Autowired
    private ExtractValidateDccReport extractValidateDccReport;

    @Autowired
    private ExtractValidateImpressReport extractValidateImpressReport;

    @Autowired
    private LoadValidateCdaExperimentReport loadValidateCdaExperimentReport;

    @Autowired
    private LoadValidateCdaReport loadValidateCdaReport;

    @Autowired
    private MetabolismCalorimetryReport metabolismCalorimetryReport;

    @Autowired
    private MetabolismCBCReport metabolismCBCReport;

    @Autowired
    private MetabolismDEXAReport metabolismDEXAReport;

    @Autowired
    private MetabolismIPGTTReport metabolismIPGTTReport;

    @Autowired
    private PhenotypeHitsReport phenotypeHitsReport;

    @Autowired
    private PhenotypeOverviewPerGeneReport phenotypeOverviewPerGeneReport;

    @Autowired
    private ProcedureCompletenessReport procedureCompletenessReport;

    @Autowired
    private SexualDimorphismNoBodyWeightReport sexualDimorphismNoBodyWeightReport;

    @Autowired
    private SexualDimorphismWithBodyWeightReport sexualDimorphismWithBodyWeightReport;

    @Autowired
    private ViabilityReport viabilityReport;

    @Autowired
    private ZygosityReport zygosityReport;

    @Autowired
    private ExpressionMpOverlapReport laczMpReport;

    @Autowired
    private IDRReport idrReport;


    private ReportsManagerParser parser = new ReportsManagerParser();
    private static final Logger log = LoggerFactory.getLogger(ReportsManager.class);
    public enum ReportType {
        BMD_STATS("bmdStats", "BMD stats (Bone Mineral Content, excluding skull) report"),
        BMD_STATS_GLUCOSE_CONCENTRATION("bmdStatsGlucoseConcentration", "lpGTT stats (Fasted blood glucose concentration) report"),
        BMD_STATS_GLUCOSE_RESPONSE("bmdStatsGlucoseResponse", "lpGTT stats (Area under the curve glucose response) report"),
        DATA_OVERVIEW("dataOverview", "Data overview report"),
        PHENOTYPE_HITS("phenotypeHits", "Distribution of phenotype hits report"),
        FERTILITY("fertility", "Fertility report"),
        HITS_PER_LINE("hitsPerLine", "Hits per line report"),
        HITS_PER_PARAMETER_AND_PROCEDURE("hitsPerParameterAndProcedure", "Hits per parameter and procedure report"),
        IMPC_GAF("impcGaf", "IMPC GAF report"),
        IMPC_P_VALUES("impcPValues", "IMPC p-values report"),
        EXTRACT_VALIDATE_CDA_BASE("extractValidateCdabase", "Extract Validate cda_base report"),
        EXTRACT_VALIDATE_DCC("extractValidateDcc", "Extract Validate DCC report"),
        EXTRACT_VALIDATE_IMPRESS("extractValidateImpress", "Extract Validate Impress report"),
        LACZ_EXPRESSION("laczExpression", "Lacz expression report"),
        LOAD_VALIDATE_CDA("loadValidateCda", "Load Validate cda report"),
        LOAD_VALIDATE_CDA_EXPERIMENT("loadValidateCdaExperiment", "Load Validate cda experiment report"),
        METABOLISM_CALORIMETRY("metabolismCalorimetry", "Metabolism calorimetry"),
        METABOLISM_CBC("metabolismCBC", "Metabolism CBC"),
        METABOLISM_DEXA("metabolismDEXA", "Metabolism DEXA"),
        METABOLISM_IPGTT("metabolismIPGTT", "Metabolism IPGTT"),
        PHENOTYPE_OVERVIEW_PER_GENE("phenotypeOverviewPerGene", "Phenotype overview per gene report"),
        PROCEDURE_COMPLETENESS("procedureCompleteness", "Procedure completeness report"),
        SEXUAL_DIMORPHISM_NO_BODY_WEIGHT("sexualDimorphismNoBodyWeight", "Sexual dimorphism no body weight report"),
        SEXUAL_DIMORPHISM_WITH_BODY_WEIGHT("sexualDimorphismWithBodyWeight", "Sexual dimorphism with body weight report"),
        VIABILITY("viability", "Viability report"),
        LACZMPOVERLAP("laczmpoverlap", "Lacz and MP call overlap report"),
        ZYGOSITY("zygosity", "Zygosity report"),
        EBIDCC("ebidcc", "EBI-DCC call comparison report"),
        IDR("idr", "IDR report");

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

        SpringApplication.run(ReportsManager.class, args);
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

                    case BMD_STATS:
                        bmdStatsReport.run(args);
                        file = bmdStatsReport.targetFile;
                        break;

                    case BMD_STATS_GLUCOSE_CONCENTRATION:
                        bmdStatsGlucoseConcentrationReport.run(args);
                        file = bmdStatsGlucoseConcentrationReport.targetFile;

                    case BMD_STATS_GLUCOSE_RESPONSE:
                        bmdStatsGlucoseResponseReport.run(args);
                        file = bmdStatsGlucoseResponseReport.targetFile;

                    case DATA_OVERVIEW:
                        dataOverviewReport.run(args);
                        file = dataOverviewReport.targetFile;
                        break;

                    case PHENOTYPE_HITS:
                        phenotypeHitsReport.run(args);
                        file = phenotypeHitsReport.targetFile;
                        break;

                    case FERTILITY:
                        fertilityReport.run(args);
                        file = fertilityReport.targetFile;
                        break;

                    case HITS_PER_LINE:
                        hitsPerLineReport.run(args);
                        file = hitsPerLineReport.targetFile;
                        break;

                    case HITS_PER_PARAMETER_AND_PROCEDURE:
                        hitsPerParameterAndProcedureReport.run(args);
                        file = hitsPerParameterAndProcedureReport.targetFile;
                        break;

                    case LACZ_EXPRESSION:
                        lacZExpressionReport.run(args);
                        file = lacZExpressionReport.targetFile;
                        break;

                    case METABOLISM_CALORIMETRY:
                        metabolismCalorimetryReport.run(args);
                        file = metabolismCalorimetryReport.targetFile;
                        break;

                    case METABOLISM_CBC:
                        metabolismCBCReport.run(args);
                        file = metabolismCBCReport.targetFile;
                        break;

                    case METABOLISM_DEXA:
                        metabolismDEXAReport.run(args);
                        file = metabolismDEXAReport.targetFile;
                        break;

                    case METABOLISM_IPGTT:
                        metabolismIPGTTReport.run(args);
                        file = metabolismIPGTTReport.targetFile;
                        break;

                    case IMPC_GAF:
                        impcGafReport.run(args);
                        file = impcGafReport.targetFile;
                        break;

                    case IMPC_P_VALUES:
                        impcPValuesReport.run(args);
                        file = impcPValuesReport.targetFile;
                        break;

                    case EXTRACT_VALIDATE_CDA_BASE:
                        extractValidateCdabaseReport.run(args);
                        file = extractValidateCdabaseReport.targetFile;
                        break;

                    case EXTRACT_VALIDATE_DCC:
                        extractValidateDccReport.run(args);
                        file = extractValidateDccReport.targetFile;
                        break;

                    case EXTRACT_VALIDATE_IMPRESS:
                        extractValidateImpressReport.run(args);
                        file = extractValidateImpressReport.targetFile;
                        break;

                    case LOAD_VALIDATE_CDA_EXPERIMENT:
                        loadValidateCdaExperimentReport.run(args);
                        file = loadValidateCdaExperimentReport.targetFile;
                        break;

                    case LOAD_VALIDATE_CDA:
                        loadValidateCdaReport.run(args);
                        file = loadValidateCdaReport.targetFile;
                        break;

                    case PHENOTYPE_OVERVIEW_PER_GENE:
                        phenotypeOverviewPerGeneReport.run(args);
                        file = phenotypeOverviewPerGeneReport.targetFile;
                        break;

                    case PROCEDURE_COMPLETENESS:
                        procedureCompletenessReport.run(args);
                        file = procedureCompletenessReport.targetFile;
                        break;

                    case SEXUAL_DIMORPHISM_NO_BODY_WEIGHT:
                        sexualDimorphismNoBodyWeightReport.run(args);
                        file = sexualDimorphismNoBodyWeightReport.targetFile;
                        break;

                    case SEXUAL_DIMORPHISM_WITH_BODY_WEIGHT:
                        sexualDimorphismWithBodyWeightReport.run(args);
                        file = sexualDimorphismWithBodyWeightReport.targetFile;
                        break;

                    case VIABILITY:
                        viabilityReport.run(args);
                        file = viabilityReport.targetFile;
                        break;

                    case ZYGOSITY:
                        zygosityReport.run(args);
                        file = zygosityReport.targetFile;
                        break;

//                    case IDR:
//                        idrReport.run(args);
//                        file = idrReport.targetFile;
//                        break;
//                    case LACZMPOVERLAP: // This is not ready for users, used internally only, at the moment.
//                    	laczMpReport.run(args);
//                    	file = laczMpReport.targetFile;
//                    	break;
                   
                }

                String fqFilename = (file != null ? file.getAbsolutePath() : "<unknown>");
                log.info("Created report '" + reportType + "' in " + fqFilename);

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
            , "   [--" + ReportsManagerParser.PROPERTIES_FILE_ARG  + "=properties_file]"
            , "   [--" + ReportsManagerParser.PREFIX_ARG           + "=prefix]"
            , "   [--" + ReportsManagerParser.HELP_ARG             + "]"
        };
        String[] defaults = {
              "Default is all reports"
            , "Default is " + ReportsManagerParser.DEFAULT_TARGET_DIRECTORY
            , "Default is " + ReportsManagerParser.DEFAULT_REPORT_FORMAT
            , "Default is " + ReportsManagerParser.DEFAULT_PROPERTIES_FILE
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