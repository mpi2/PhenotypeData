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
import org.mousephenotype.cda.reports.support.FileUtils;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.reports.support.ReportsManagerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.List;

/**
 * Created by mrelac on 23/06/2015.
 */

@SpringBootApplication
public class ReportsManager implements CommandLineRunner {

    @Autowired
    BmdStatsReport bmdStatsReport;

    @Autowired
    BmdStatsGlucoseConcentrationReport bmdStatsGlucoseConcentrationReport;

    @Autowired
    BmdStatsGlucoseResponseReport bmdStatsGlucoseResponseReport;

    @Autowired
    DataOverviewReport dataOverviewReport;

    @Autowired
    FertilityReport fertilityReport;

    @Autowired
    protected HitsPerLineReport hitsPerLineReport;

    @Autowired
    protected HitsPerParameterAndProcedureReport hitsPerParameterAndProcedureReport;

    @Autowired
    protected ImpcPValueReport impcPValueReport;

    @Autowired
    protected LacZExpressionReport lacZExpressionReport;

    @Autowired
    protected PhenotypeHitsReport phenotypeHitsReport;

    @Autowired
    protected PhenotypeOverviewPerGeneReport phenotypeOverviewPerGeneReport;

    @Autowired
    ProcedureCompletenessReport procedureCompletenessReport;

    @Autowired
    SexualDimorphismNoBodyWeightReport sexualDimorphismNoBodyWeightReport;

    @Autowired
    SexualDimorphismWithBodyWeightReport sexualDimorphismWithBodyWeightReport;

    @Autowired
    ViabilityReport viabilityReport;

    @Autowired
    ZygosityReport zygosityReport;


    private ReportsManagerParser parser = new ReportsManagerParser();





//    @Autowired(required = true)
//    ObservationRepository observations;
//
//    @Autowired
//    @Deprecated
//    ReportsService reportsService;

//    @Autowired
//    PhenotypeCenterService phenotypeCenterService;

//    @Autowired
//    SexualDimorphismDAO sexualDimorphismDAO;

//@Autowired
//ReportGenerator reportGenerator;

//    @NotNull
//    @Value("${drupalBaseUrl}")
//    protected String drupalBaseUrl;

    FileUtils fileUtils = new FileUtils();

    private static final Logger log = LoggerFactory.getLogger(ReportsManager.class);


//    private static final String PROPERTIES_FILE_ARG = "propertiesFile";
//    private static final String TARGET_DIRECTORY_ARG = "targetDirectory";
//    private static final String REPORTS_ARG = "reports";
//    private static final String HELP_ARG = "help";

//    private List<ReportType> reports = new ArrayList<>();
//    private String targetDirectory;
//    private PropertiesConfiguration props;
//    private boolean showHelp;

    public enum ReportType {
        BMD_STATS("bmdStats", "BMD stats (Bone Mineral Content, excluding skull) report"),
        BMD_STATS_GLUCOSE_CONCENTRATION("bmdStatsGlucoseConcentration", "lpGTT stats (Fasted blood glucose concentration) report"),
        BMD_STATS_GLUCOSE_RESPONSE("bmdStatsGlucoseResponse", "lpGTT stats (Area under the curve glucose response) report"),
        DATA_OVERVIEW("dataOverview", "Data overview report"),
        PHENOTYPE_HITS("phenotypeHits", "Distribution of phenotype hits report"),
        FERTILITY("fertility", "Fertility report"),
        HITS_PER_LINE("hitsPerLineReport", "Hits per line report"),
        HITS_PER_PARAMETER_AND_PROCEDURE("hitsPerParameterAndProcedure", "Hits per parameter and procedure report"),
        IMPC_P_VALUES("impcPvalues", "IMPC p-values report"),
        LACZ_EXPRESSION("laczExpression", "Lacz expression report"),
        PHENOTYPE_OVERVIEW_PER_GENE("phenotypeOverviewPerGene", "Phenotype overview per gene report"),
        PROCEDURE_COMPLETENESS("procedureCompleteness", "Procedure completeness report"),
        SEXUAL_DIMORPHISM_NO_BODY_WEIGHT("sexualDimorphismNoBodyWeight", "Sexual dimorphism no body weight report"),
        SEXUAL_DIMORPHISM_WITH_BODY_WEIGHT("sexualDimorphismWithBodyWeight", "Sexual dimorphism with body weight report"),
        VIABILITY("viability", "Viability report"),
        ZYGOSITY("zygosity", "Zygosity report");

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

                    case IMPC_P_VALUES:
                        impcPValueReport.run(args);
                        file = impcPValueReport.targetFile;
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
                }

                String fqFilename = (file != null ? file.getAbsolutePath() : "<unknown>");
                log.info("Created report '" + reportType + "' in " + fqFilename + ".");

            } catch (ReportException e) {
                log.error("FAILED to create report '" + reportType + " in " + parser.getTargetDirectory() + ". Reason: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        if (parser.showHelp()) {
            usage();
        }

        System.exit(0);
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
            System.out.println(String.format("%-50.50s %-30.30s", commands[i], defaults[i]));
        }
        System.out.println();
        System.out.println("Reports:");
        System.out.println(ReportType.toStringAll());
    }

//    /**
//     * Returns an empty list if parameters are valid; else returns a list of strings containing the error text.
//     *
//     * @param ps A valid <code>PropertySource</code> instance of this program's parsed arguments.
//     * @return an empty list if paramters are valid; else returns a list of strings containing the error text.
//     */
//    private List<String> parseAndValidate(PropertySource ps) {
//        List<String> retVal = new ArrayList<>();
//
//        showHelp = (ps.containsProperty(HELP) ? true : false);
//
//        if (ps.containsProperty(PROPERTIES_FILE_ARG)) {
//            try {
//                props = new PropertiesConfiguration((String) ps.getProperty(PROPERTIES_FILE_ARG));
//            } catch( ConfigurationException e) {
//                retVal.add("Expected required properties targetFile.");
//            }
//        } else {
//            retVal.add("Expected required properties targetFile.");
//        }
//
//        if (ps.containsProperty(TARGET_DIRECTORY_ARG)) {
//            targetDirectory = (String) ps.getProperty(TARGET_DIRECTORY_ARG);
//        } else {
//            retVal.add("Expected required target directory.");
//        }
//        Path path = Paths.get((String) ps.getProperty(TARGET_DIRECTORY_ARG));
//        if (!Files.exists(path)) {
//            retVal.add("Target directory " + path.toAbsolutePath() + " does not exist.");
//        }
//        if ( ! Files.isWritable(path)) {
//            retVal.add("Target directory " + path.toAbsolutePath() + " is not writeable.");
//        }
//
//        if (ps.containsProperty(REPORTS_ARG)) {
//            String reportsCsl = (String) ps.getProperty(REPORTS_ARG);
//            String[] reportTags = reportsCsl.split(",");
//            for (String reportTag : reportTags) {                                                                       // Validate the tag.
//                if (reportTag.isEmpty()) {
//                    reports.addAll(Arrays.asList(ReportType.values()));
//                } else {
//                    try {
//                        ReportType reportType = ReportType.fromTag(reportTag);
//                        reports.add(reportType);
//                    } catch (IllegalArgumentException | NullPointerException e) {
//                        retVal.add("Unknown report tag '" + reportTag + "'.");
//                    }
//                }
//            }
//        } else {
//            reports.addAll(Arrays.asList(ReportType.values()));
//        }
//
//        return retVal;
//    }

    private void logInputParameters() {
        log.info("Reports:          " + StringUtils.join(parser.getReports(), ","));
        log.info("Target directory: " + parser.getTargetDirectory());
        log.info("Report format:    " + parser.getReportFormat());
        log.info("Properties targetFile:  " + (parser.getApplicationProperties() == null ? "<omitted>" : parser.getApplicationProperties().getURL().toString()));
        log.info("Prefix:           " + (parser.getPrefix() == null                ? "<omitted>" : parser.getPrefix()));
    }

//    public Map<String, List<String>> parse(String[] args) {
//        PropertySource ps = new SimpleCommandLinePropertySource(args);
//        Map<String, List<String>> propertyMap = new HashMap<>();
//
//        if (ps.containsProperty(HELP_ARG)) {
//            propertyMap.put(HELP_ARG, new ArrayList<>());
//        }
//
//        if (ps.containsProperty(REPORTS_ARG)) {
//            propertyMap.put(REPORTS_ARG, Arrays.asList(ps.getProperty(REPORTS_ARG).toString().split(",")));
//        }
//
//        return propertyMap;
//    }
//
//    public List<String> validate(Map<String, List<String>> propertyMap) {
//        List<String> retVal = new ArrayList<>();
//
//        showHelp = (propertyMap.containsKey(HELP_ARG) ? true : false);
//
//
//        if (propertyMap.containsKey(REPORTS_ARG)) {
//            List<String> reportNames = propertyMap.get(REPORTS_ARG);
//            for (String reportName : reportNames) {
//                if (reportName.isEmpty()) {
//                    reports.addAll(Arrays.asList(ReportType.values()));
//                } else {
//                    try {
//                        ReportType reportType = ReportType.fromTag(reportName);
//                        reports.add(reportType);
//                    } catch (IllegalArgumentException | NullPointerException e) {
//                        retVal.add("Unknown report name '" + reportName + "'.");
//                    }
//                }
//            }
//        } else {
//            reports.addAll(Arrays.asList(ReportType.values()));
//        }
//
//        return retVal;
//    }
}