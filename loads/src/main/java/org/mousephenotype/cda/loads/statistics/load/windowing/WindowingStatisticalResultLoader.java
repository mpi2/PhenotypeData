package org.mousephenotype.cda.loads.statistics.load.windowing;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.statistics.*;
import org.mousephenotype.cda.enumerations.*;
import org.mousephenotype.cda.loads.statistics.load.StatisticalResultFailed;
import org.mousephenotype.cda.loads.statistics.load.StatisticalResultLoaderConfig;
import org.mousephenotype.cda.solr.service.BasicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication
@Import(value = {StatisticalResultLoaderConfig.class})
public class WindowingStatisticalResultLoader extends BasicService implements CommandLineRunner {

    final private Logger logger = LoggerFactory.getLogger(getClass());

    protected DataSource komp2DataSource;
    protected MpTermService mpTermService;

    protected Map<String, NameIdDTO> organisationMap = new HashMap<>();
    protected Map<String, NameIdDTO> pipelineMap = new HashMap<>();
    protected Map<String, NameIdDTO> procedureMap = new HashMap<>();
    protected Map<String, NameIdDTO> parameterMap = new HashMap<>();

    protected Map<String, Integer> datasourceMap = new HashMap<>();
    protected Map<String, Integer> projectMap = new HashMap<>();
    protected Map<String, String> colonyAlleleMap = new HashMap<>();
    protected Map<String, Map<String, String>> colonyProcedureMap = new HashMap<>();
    protected Map<String, Map<ZygosityType, Integer>> bioModelMap = new HashMap<>();
    protected Map<Integer, String> bioModelStrainMap = new HashMap<>();
    protected Map<String, Integer> controlBioModelMap = new HashMap<>();
    public Map<String, ObservationType> parameterTypeMap = new HashMap<>();


    public WindowingStatisticalResultLoader() { }

    @Inject
    public WindowingStatisticalResultLoader(@Named("komp2DataSource") DataSource komp2DataSource, MpTermService mpTermService) {
        Assert.notNull(komp2DataSource, "Komp2 datasource cannot be null");
        Assert.notNull(mpTermService, "mpTermService cannot be null");
        this.komp2DataSource = komp2DataSource;
        this.mpTermService = mpTermService;
    }


    @Override
    public void run(String... strings) throws Exception {

        logger.info("Starting windowing statistical result loader");

        // parameter to indicate the location of the result file(s)
        OptionParser parser = new OptionParser();
        parser.accepts("location").withRequiredArg().ofType(String.class).isRequired();
        OptionSet options = parser.parse(strings);
        if ( ! options.hasArgument("location") ) {
            logger.error("location argument missing");
            return;
        }
        String fileLocation = (String) options.valuesOf("location").get(0);

        // If the location is a single file, parse it
        boolean regularFile = Files.isRegularFile(Paths.get(fileLocation));
        boolean directory = Files.isDirectory(Paths.get(fileLocation));

        // Populate lookups
        populateOrganisationMap();
        populatePipelineMap();
        populateProcedureMap();
        populateColonyProcedureMap();
        populateParameterMap();
        populateDatasourceMap();
        populateProjectMap();
        populateColonyAlleleMap();
        populateBioModelMap();
        populateControlBioModelMap();
        populateParameterTypeMap();

        if (regularFile) {

            // process the file
            processFile(fileLocation);

        } else if (directory) {

            // process all regular files in the directory that end in "result" and have "tsv" in the filename
            Files
                    .walk(Paths.get(fileLocation))
                    .filter(p -> p.toString().toLowerCase().contains("successful"))
                    .filter(p -> p.toString().endsWith(".tsv"))
                    .filter(p -> Files.isRegularFile(p.toAbsolutePath()))
                    .parallel()
                    .forEach(p -> {
                        logger.info("Processing file: " + p.toAbsolutePath().toString());
                        processFile(p.toAbsolutePath().toString());
                    });

        } else {
            logger.warn("File " + fileLocation + " is not a regular file or a directory");
        }
    }


    private void processFile(String loc) {

        try {
            Map<String, Integer> counts = new HashMap<>();

            AtomicInteger c = new AtomicInteger();
            Files.lines(Paths.get(loc)).forEach(line -> {

                LightweightResult result;
                try {
                    result = getBaseResult(getResult(line));
                } catch (Exception e) {
                    System.out.println("Error processing row: " + line.substring(0,300)+"...\n");
                    c.getAndIncrement();
                    if(c.get()<100 || Math.random()<0.001) {
                        // Print the first 100 stacktraces and then sample at 1 per 1000
                        String fullStackTrace = org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(e);
                        System.out.println("Stacktrace: " + fullStackTrace);
                    }
                    return;
                }

                if (result == null) {
                    // Skipping record
                    System.out.println("Skipping row: " + line.substring(0,300)+"...\n");
                    return;
                }

                try (Connection connection = komp2DataSource.getConnection()) {
                    PreparedStatement p = result.getSaveResultStatement(connection);
                    p.executeUpdate();

                    counts.put(result.getStatisticalMethod(), counts.getOrDefault(result.getStatisticalMethod(), 0) + 1);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("There were " + c.get() + " exceptions thrown");

            for(String method : counts.keySet()) {
                logger.info("  ADDED " + counts.get(method) + " statistical results for method: " + method);
            }

        } catch (Exception e) {
            logger.error("Could not process file: " + loc, e);
        }
    }


    enum HEADERS {

        STATUS ("Status"),
        PROCEDURE_GROUP ("Procedure group"),
        PROCEDURE_STABLE_ID ("Procedure Stable Id"),
        PROCEDURE_NAME ("Procedure Name"),
        PARAMETER_STABLE_ID ("Parameter Stable Id"),
        PARAMETER_NAME ("Parameter Name"),
        PHENOTYPING_CENTER ("Phenotyping Center"),
        ALLELE_SYMBOL ("Allele Symbol"),
        GENE_SYMBOL ("Gene Symbol"),
        GENE_ACCESSION_ID ("Gene Accession ID"),
        PIPELINE_NAME ("Pipeline Name"),
        PIPELINE_STABLE_ID ("Pipeline Stable ID"),
        BACKGROUND_STRAIN_ACCESSION_ID ("Background Strain Accession Id"),
        METADATA_GROUP ("Metadata Group"),
        ZYGOSITY ("Zygosity"),
        COLONY_ID ("Colony Id"),
        OTHER ("Other"),
        ADDITIONAL_DATA ("Additional Data");

        private final String identifier;

        public String toString() {
            return identifier;
        }

        HEADERS(String identifier) {
            this.identifier = identifier;
        }

        public static String getEnumNameForValue(Object value){
            HEADERS[] values = HEADERS.values();
            String enumValue = null;
            for(HEADERS eachValue : values) {
                enumValue = eachValue.toString();

                if (enumValue.equalsIgnoreCase(value.toString())) {
                    return eachValue.name();
                }
            }
            return enumValue;
        }

    }


    /**
     * Process a string from a results file into a LineStatisticalResult object
     *
     * field list
     *
     *
     *
     * @param data a line from a statistical results file
     * @throws IOException
     */
    LineStatisticalResult getResult(String data) throws JSONException {

        LineStatisticalResult result = new LineStatisticalResult();
        List<String> dataFields = Arrays.asList(data.trim().split("\t"));

        Map<HEADERS, String> values = new HashMap<>();

        values.put(HEADERS.STATUS, dataFields.get(0));
        values.put(HEADERS.PROCEDURE_GROUP, dataFields.get(1));
        values.put(HEADERS.PROCEDURE_STABLE_ID, dataFields.get(2));
        values.put(HEADERS.PROCEDURE_NAME, dataFields.get(3));
        values.put(HEADERS.PARAMETER_STABLE_ID, dataFields.get(4));
        values.put(HEADERS.PARAMETER_NAME, dataFields.get(5));
        values.put(HEADERS.PHENOTYPING_CENTER, dataFields.get(6));
        values.put(HEADERS.ALLELE_SYMBOL, dataFields.get(7));
        values.put(HEADERS.GENE_SYMBOL, dataFields.get(8));
        values.put(HEADERS.GENE_ACCESSION_ID, dataFields.get(9));
        values.put(HEADERS.PIPELINE_NAME, dataFields.get(10));

        if (dataFields.get(11).contains("_")) {
            List<String> pipelinePieces = Arrays.asList(dataFields.get(11).split("_"));

            String pipelineStableId = dataFields.get(11);

            if ( pipelinePieces.size() > 2) {
                pipelineStableId = pipelinePieces.get(pipelinePieces.size()-2) + "_" + pipelinePieces.get(pipelinePieces.size()-1);
            }
            values.put(HEADERS.PIPELINE_STABLE_ID, pipelineStableId);
        } else {
            values.put(HEADERS.PIPELINE_STABLE_ID, dataFields.get(11));
        }

        values.put(HEADERS.BACKGROUND_STRAIN_ACCESSION_ID, dataFields.get(12));
        values.put(HEADERS.METADATA_GROUP, dataFields.get(13));
        values.put(HEADERS.ZYGOSITY, dataFields.get(14));
        values.put(HEADERS.COLONY_ID, dataFields.get(15));
        values.put(HEADERS.OTHER, dataFields.get(16));
        values.put(HEADERS.ADDITIONAL_DATA, dataFields.get(17));

        JSONObject json = new JSONObject(values.get(HEADERS.ADDITIONAL_DATA));



        String dataSource = "IMPC";
        String project    = "IMPC";
        String center     = values.get(HEADERS.PHENOTYPING_CENTER);
        String pipeline   = values.get(HEADERS.PIPELINE_STABLE_ID);
        String procedure  = values.get(HEADERS.PROCEDURE_GROUP);
        String strain     = values.get(HEADERS.BACKGROUND_STRAIN_ACCESSION_ID);

        // Center Phenomin is ICS
        if (center.equals("Phenomin")) {
            center = "ICS";
        }

        // Strain in the filename does not include a ":"
        if (strain.contains("MGI") && !strain.contains(":")) {
            strain = strain.replaceAll("MGI", "MGI:");
        }

        result.setDataSource(dataSource);
        result.setProject(project);
        result.setCenter(center);
        result.setPipeline(pipeline);
        result.setProcedure(procedure);
        result.setStrain(strain);




        //
        // SET THE FIELDS UP ACCORDING TO EXPECTED DOWNSTREAM PROCESSING
        String [] fields = new String[50];

        /*
     0	metadata_group
     1	zygosity
     2	colony_id
     3	depvar
     4	status
     5	code
     6	count cm
     7	count cf
     8	count mm
     9	count mf
     10	mean cm
     11	mean cf
     12	mean mm
     13	mean mf
     14	control_strategy
     15	workflow
     16	weight available
     17	Method
     18	Dependent variable
     19	Batch included
     20	Residual variances homogeneity
     21	Genotype contribution
     22	Genotype estimate
     23	Genotype standard error
     24	Genotype p-Val
     25	Genotype percentage change
     26	Sex estimate
     27	Sex standard error
     28	Sex p-val
     29	Weight estimate
     30	Weight standard error
     31	Weight p-val
     32	Gp1 genotype
     33	Gp1 Residuals normality test
     34	Gp2 genotype
     35	Gp2 Residuals normality test
     36	Blups test
     37	Rotated residuals normality test
     38	Intercept estimate
     39	Intercept standard error
     40	Interaction included
     41	Interaction p-val
     42	Sex FvKO estimate
     43	Sex FvKO standard error
     44	Sex FvKO p-val
     45	Sex MvKO estimate
     46	Sex MvKO standard error
     47	Sex MvKO p-val
     48	Classification tag
     49	Additional information
         */

        fields[0] = values.get(HEADERS.METADATA_GROUP); // metadata_group
        fields[1] = values.get(HEADERS.ZYGOSITY); // zygosity
        fields[2] = values.get(HEADERS.COLONY_ID); // colony_id
        fields[3] = values.get(HEADERS.PARAMETER_STABLE_ID); // depvar
        fields[4] = "TESTED"; // status
        fields[5] = "OK"; // code

        JSONObject normalResult = json.getJSONObject("result").getJSONObject("vectoroutput").getJSONObject("normal_result");
        JSONObject normalAdditionalInformation = normalResult.getJSONObject("additional_information");
        JSONObject normalSummaryStatistics = normalAdditionalInformation.getJSONObject("summary_statistics");

        JSONObject jsonResult = normalResult;
        JSONObject additionalInformation = normalAdditionalInformation;
        JSONObject summaryStatistics = normalSummaryStatistics;

        final List<String> keysFromWindowedResult = new ArrayList<>();
        for (Iterator s = json.getJSONObject("result").getJSONObject("vectoroutput").getJSONObject("windowed_result").keys(); s.hasNext(); ) {
            String key = (String) s.next();
            keysFromWindowedResult.add(key);
        }
        if (keysFromWindowedResult.size() > 0) {
            JSONObject windowedResult = json.getJSONObject("result").getJSONObject("vectoroutput").getJSONObject("windowed_result");
            JSONObject windowedAdditionalInformation = windowedResult.getJSONObject("additional_information");
            JSONObject windowedSummaryStatistics = windowedAdditionalInformation.getJSONObject("summary_statistics");

            jsonResult = windowedResult;
            additionalInformation = windowedAdditionalInformation;
            summaryStatistics = windowedSummaryStatistics;
        }

        JSONObject details = json.getJSONObject("result").getJSONObject("details");

        Integer maleControls = null;
        Integer femaleControls = null;
        Integer maleMutants = null;
        Integer femaleMutants = null;
        final List<String> categoriesFromJson = new ArrayList<>();
        for (Iterator s = summaryStatistics.keys(); s.hasNext(); ) {

            final String category = (String) s.next();

            categoriesFromJson.add(category);

            if (category.startsWith("Male_control")) {
                maleControls = (maleControls == null) ?
                        Integer.parseInt(summaryStatistics.getJSONObject(category).getString("count")) :
                        maleControls + Integer.parseInt(summaryStatistics.getJSONObject(category).getString("count"));
            } else if (category.startsWith("Female_control")) {
                femaleControls = (femaleControls == null) ?
                        Integer.parseInt(summaryStatistics.getJSONObject(category).getString("count")) :
                        femaleControls + Integer.parseInt(summaryStatistics.getJSONObject(category).getString("count"));
            } else if (category.startsWith("Male_experimental")) {
                maleMutants = (maleMutants == null) ?
                        Integer.parseInt(summaryStatistics.getJSONObject(category).getString("count")) :
                        maleMutants + Integer.parseInt(summaryStatistics.getJSONObject(category).getString("count"));
            } else if (category.startsWith("Female_experimental")) {
                femaleMutants = (femaleMutants == null) ?
                        Integer.parseInt(summaryStatistics.getJSONObject(category).getString("count")) :
                        femaleMutants + Integer.parseInt(summaryStatistics.getJSONObject(category).getString("count"));
            }
        }

        fields[6] = maleControls == null ? "" : maleControls.toString(); // count cm
        fields[7] = femaleControls == null ? "" : femaleControls.toString(); // count cf
        fields[8] = maleMutants == null ? "" : maleMutants.toString(); // count mm
        fields[9] = femaleMutants == null ? "" : femaleMutants.toString(); // count mf

        String method = jsonResult.getString("method");

        if (method.toLowerCase().startsWith("reference range")) {
            fields[10] = ""; // mean cm
            fields[11] = ""; // mean cf
            fields[12] = ""; // mean mm
            fields[13] = ""; // mean mf
        } else {
            fields[10] = categoriesFromJson.contains("Male_control") ? summaryStatistics.getJSONObject("Male_control").getString("mean") : ""; // mean cm
            fields[11] = categoriesFromJson.contains("Female_control") ? summaryStatistics.getJSONObject("Female_control").getString("mean") : ""; // mean cf
            fields[12] = categoriesFromJson.contains("Male_experimental") ? summaryStatistics.getJSONObject("Male_experimental").getString("mean") : ""; // mean mm
            fields[13] = categoriesFromJson.contains("Female_experimental") ? summaryStatistics.getJSONObject("Female_experimental").getString("mean") : ""; // mean mf
        }

        fields[14] = details.getString("concurrent_control_selection"); // control_strategy
        fields[15] = additionalInformation.getString("multibatch_in_analysis"); // workflow

        List<Integer> weights = new ArrayList<>();
        for (int i=0; i< details.getJSONArray("original_body_weight").length(); i++) {
            String t = details.getJSONArray("original_body_weight").getString(i);
            if ( ! t.equals("null")) {
                weights.add(details.getJSONArray("original_body_weight").getInt(i));
                break;
            }
        }

        fields[16] = weights.size() > 0 ? "true" : "false"; // weight available


        final List<String> keysFromAddtionalInformation = new ArrayList<>();
        for (Iterator s = additionalInformation.keys(); s.hasNext(); ) {
            String key = (String) s.next();
            keysFromAddtionalInformation.add(key);
        }

        if (keysFromAddtionalInformation.contains("formula")) {
            method = method +" (model: "
                    + additionalInformation.getString("formula").replaceAll("data_point", values.get(HEADERS.PARAMETER_STABLE_ID))
                    + ")";
        }

        fields[17] = method; // Method
        fields[18] = values.get(HEADERS.PARAMETER_STABLE_ID); // Dependent variable


        fields[19] = jsonResult.getString("batch_included"); // Batch included
        fields[20] = jsonResult.getString("residual_variances_homogeneity"); // Residual variances homogeneity
        fields[21] = jsonResult.getString("genotype_contribution"); // Genotype contribution
        fields[22] = jsonResult.getString("genotype_estimate"); // Genotype estimate
        fields[23] = jsonResult.getString("genotype_standard_error"); // Genotype standard error
        fields[24] = jsonResult.getString("genotype_p_val"); // Genotype p-Val
        fields[25] = jsonResult.getString("genotype_percentage_change"); // Genotype percentage change
        fields[26] = jsonResult.getString("sex_estimate"); // Sex estimate
        fields[27] = jsonResult.getString("sex_standard_error"); // Sex standard error
        fields[28] = jsonResult.getString("sex_p_val"); // Sex p-val
        fields[29] = jsonResult.getString("weight_estimate"); // Weight estimate
        fields[30] = jsonResult.getString("weight_standard_error"); // Weight standard error
        fields[31] = jsonResult.getString("weight_p_val"); // Weight p-val
        fields[32] = jsonResult.getString("gp1_genotype"); // Gp1 genotype
        fields[33] = jsonResult.getString("gp1_residuals_normality_test"); // Gp1 Residuals normality test
        fields[34] = jsonResult.getString("gp2_genotype"); // Gp2 genotype
        fields[35] = jsonResult.getString("gp2_residuals_normality_test"); // Gp2 Residuals normality test
        fields[36] = jsonResult.getString("blups_test"); // Blups test
        fields[37] = jsonResult.getString("rotated_residuals_normality_test"); // Rotated residuals normality test
        fields[38] = jsonResult.getString("intercept_estimate"); // Intercept estimate
        fields[39] = jsonResult.getString("intercept_standard_error"); // Intercept standard error
        fields[40] = jsonResult.getString("interaction_included"); // Interaction included
        fields[41] = jsonResult.getString("interaction_p_val"); // Interaction p-val
        fields[42] = jsonResult.getString("sex_fvko_estimate"); // Sex FvKO estimate
        fields[43] = jsonResult.getString("sex_fvko_standard_error"); // Sex FvKO standard error
        fields[44] = jsonResult.getString("sex_fvko_p_val"); // Sex FvKO p-val
        fields[45] = jsonResult.getString("sex_mvko_estimate"); // Sex MvKO estimate
        fields[46] = jsonResult.getString("sex_mvko_standard_error"); // Sex MvKO standard error
        fields[47] = jsonResult.getString("sex_mvko_p_val"); // Sex MvKO p-val
        fields[48] = jsonResult.getString("classification_tag"); // Classification tag
        fields[49] = additionalInformation.toString(); // Additional information


        //
        // DONE POPULATING THE fields ARRAY
        //


        //
        // START BUILDING THE INTERNAL STATS RESULT OBJECT
        //


        result.setMetadataGroup( getStringField(fields[0]) );
        result.setZygosity( getStringField(fields[1]) );
        result.setColonyId( getStringField(fields[2]) );

        // May need to change the output from R columns that remap some of the eye categories
        // It relabels the column to parameterStableId_MAPPED
        String depVar = getStringField(fields[3]).replaceAll("_MAPPED", "");
        if (depVar.contains(".")) {
            depVar = depVar.replaceAll("\\.", "-");
        }
        result.setDependentVariable( depVar );

        result.setStatus( fields[4] );
        result.setCode( getStringField(fields[5]) );
        result.setCountControlMale( getIntegerField(fields[6]) );
        result.setCountControlFemale( getIntegerField(fields[7]) );
        result.setCountMutantMale( getIntegerField(fields[8]) );
        result.setCountMutantFemale( getIntegerField(fields[9]) );
        result.setMaleControlMean ( getDoubleField(fields[10]) );
        result.setFemaleControlMean ( getDoubleField(fields[11]) );
        result.setMaleMutantMean ( getDoubleField(fields[12]) );
        result.setFemaleMutantMean ( getDoubleField(fields[13]) );
        result.setControlSelection( getStringField(fields[14]) );
        result.setWorkflow( getStringField(fields[15]) );
        result.setWeightAvailable( getStringField(fields[16]) );

        try {
            result.setStatisticalMethod(getStringField(fields[17]));
        } catch (ArrayIndexOutOfBoundsException e) {
            result.setStatisticalMethod("-");
        }

        // fields[18] is a duplicate of fields[3]

        switch(result.getStatus()) {
            case "TESTED":
                // Result was processed successfully by PhenStat, load the result object

                // Always set status to Success with successfully processed
                result.setStatus( "Success" );


                // Vector output results from PhenStat start at field 19
                int i = 19;

                result.setBatchIncluded( getBooleanField(fields[i++]) );
                result.setResidualVariancesHomogeneity( getBooleanField(fields[i++]) );
                result.setGenotypeContribution( getDoubleField(fields[i++]) );
                result.setGenotypeEstimate( getStringField(fields[i++]) );
                result.setGenotypeStandardError( getDoubleField(fields[i++]) );
                result.setGenotypePVal( getStringField(fields[i++]) );
                result.setGenotypePercentageChange( getStringField(fields[i++]) );
                result.setSexEstimate( getDoubleField(fields[i++]) );
                result.setSexStandardError( getDoubleField(fields[i++]) );
                result.setSexPVal( getDoubleField(fields[i++]) );
                result.setWeightEstimate( getDoubleField(fields[i++]) );
                result.setWeightStandardError( getDoubleField(fields[i++]) );
                result.setWeightPVal( getDoubleField(fields[i++]) );
                result.setGroup1Genotype( getStringField(fields[i++]) );
                result.setGroup1ResidualsNormalityTest( getDoubleField(fields[i++]) );
                result.setGroup2Genotype( getStringField(fields[i++]) );
                result.setGroup2ResidualsNormalityTest( getDoubleField(fields[i++]) );
                result.setBlupsTest( getDoubleField(fields[i++]) );
                result.setRotatedResidualsNormalityTest( getDoubleField(fields[i++]) );
                result.setInterceptEstimate( getDoubleField(fields[i++]) );
                result.setInterceptStandardError( getDoubleField(fields[i++]) );
                result.setInteractionIncluded( getBooleanField(fields[i++]) );
                result.setInteractionPVal( getDoubleField(fields[i++]) );
                result.setSexFvKOEstimate( getDoubleField(fields[i++]) );
                result.setSexFvKOStandardError( getDoubleField(fields[i++]) );
                result.setSexFvKOPVal( getDoubleField(fields[i++]) );
                result.setSexMvKOEstimate( getDoubleField(fields[i++]) );
                result.setSexMvKOStandardError( getDoubleField(fields[i++]) );
                result.setSexMvKOPVal( getDoubleField(fields[i++]) );
                result.setClassificationTag( getStringField(fields[i++]) );
                result.setAdditionalInformation( getStringField(fields[i++]) );

                logger.debug("Last iteration left index i at: ", i);

                break;

            default:
                // Result was not processed by PhenStat

                result.setBatchIncluded( null );
                result.setResidualVariancesHomogeneity( null );
                result.setGenotypeContribution( null );
                result.setGenotypeEstimate( null );
                result.setGenotypeStandardError( null );
                result.setGenotypePVal( null );
                result.setGenotypePercentageChange( null );
                result.setSexEstimate( null );
                result.setSexStandardError( null );
                result.setSexPVal( null );
                result.setWeightEstimate( null );
                result.setWeightStandardError( null );
                result.setWeightPVal( null );
                result.setGroup1Genotype( null );
                result.setGroup1ResidualsNormalityTest( null );
                result.setGroup2Genotype( null );
                result.setGroup2ResidualsNormalityTest( null );
                result.setBlupsTest( null );
                result.setRotatedResidualsNormalityTest( null );
                result.setInterceptEstimate( null );
                result.setInterceptStandardError( null );
                result.setInteractionIncluded( null );
                result.setInteractionPVal( null );
                result.setSexFvKOEstimate( null );
                result.setSexFvKOStandardError( null );
                result.setSexFvKOPVal( null );
                result.setSexMvKOEstimate( null );
                result.setSexMvKOStandardError( null );
                result.setSexMvKOPVal( null );
                result.setClassificationTag( null );
                result.setAdditionalInformation( "Orig data: "+  data );
                break;
        }


        return result;
    }


    protected class NameIdDTO {

        int dbId;
        String name;
        String stableId;

        public NameIdDTO(int dbId, String name) {
            this.dbId = dbId;
            this.name = name;
        }

        public NameIdDTO(int dbId, String name, String stableId) {
            this.dbId = dbId;
            this.name = name;
            this.stableId = stableId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDbId() {
            return dbId;
        }

        public void setDbId(int dbId) {
            this.dbId = dbId;
        }

        public String getStableId() {
            return stableId;
        }

        public void setStableId(String stableId) {
            this.stableId = stableId;
        }
    }


    /**
     * Generate the data set to process
     *
     * @param data    The data result from a stats analysis
     * @return result object partially populated with basic information
     */
    public LightweightResult getBaseResult(LineStatisticalResult data) {

        if (data == null) {
            return null;
        }

        NameIdDTO center = organisationMap.get(data.getCenter());
        NameIdDTO pipeline = pipelineMap.get(data.getPipeline());

        // Get actual colony procedure
        // key is [procedure_group][colonyID] => procedure_stable_id that has data for this combination
        String actualProc = null;

        if (colonyProcedureMap.containsKey(data.getProcedure()) &&
                colonyProcedureMap.get(data.getProcedure()).containsKey(data.getColonyId())) {
            actualProc = colonyProcedureMap.get(data.getProcedure()).get(data.getColonyId());
        }

        if (actualProc == null) {
            logger.warn("Cannot find procedure for colony {}, parameter {}", data.getColonyId(), data.getDependentVariable());
            return null;
        }

        NameIdDTO procedure = procedureMap.get(actualProc!=null ? actualProc : procedureMap.get(data.getProcedure()));

        NameIdDTO parameter = parameterMap.get(data.getDependentVariable());

        // result contains a "statistical result" that has the
        // ability to produce a PreparedStatement ready for database insertion

        LightweightResult result;
        StatisticalResult statsResult;

        if (parameterTypeMap.get(data.getDependentVariable()) == ObservationType.categorical) {

            Double effectSize = getDoubleField(data.getGenotypeEstimate());
            Double pValue = getDoubleField(data.getGenotypePVal());
            Double malePValue = data.getSexMvKOPVal();
            Double maleEffectSize = data.getSexMvKOEstimate();
            Double femalePValue = data.getSexFvKOPVal();
            Double femaleEffectSize = data.getSexFvKOEstimate();
            String classificationTag = data.getClassificationTag();

            // Categorical result
            result = new LightweightCategoricalResult();
            ((LightweightCategoricalResult)result).setCategoryA("normal");
            ((LightweightCategoricalResult) result).setpValue( pValue );
            ((LightweightCategoricalResult) result).setEffectSize( effectSize );

            ((LightweightCategoricalResult) result).setMalePValue( malePValue );
            ((LightweightCategoricalResult) result).setMaleEffectSize( maleEffectSize );
            ((LightweightCategoricalResult) result).setFemalePValue( femalePValue );
            ((LightweightCategoricalResult) result).setFemaleEffectSize( femaleEffectSize );
            ((LightweightCategoricalResult) result).setClassificationTag( classificationTag );

            StatisticalResultCategorical temp = new StatisticalResultCategorical();
            temp.setpValue( pValue );
            temp.setEffectSize( effectSize );
            temp.setMalePValue( malePValue );
            temp.setMaleEffectSize( maleEffectSize );
            temp.setFemalePValue( femalePValue );
            temp.setFemaleEffectSize( femaleEffectSize );
            temp.setClassificationTag( classificationTag );
            statsResult = temp;

        } else if (parameterTypeMap.get(data.getDependentVariable()) == ObservationType.unidimensional && data.getStatisticalMethod().contains("Mixed Model framework")) {

            Double effectSize = getDoubleField(data.getGenotypeEstimate());
            Double pValue = getDoubleField(data.getGenotypePVal());

            // Unidimensional result
            result = new LightweightUnidimensionalResult();
            ((LightweightUnidimensionalResult)result).setFemaleControlMean(data.getFemaleControlMean());
            ((LightweightUnidimensionalResult)result).setFemaleExperimentalMean(data.getFemaleMutantMean());
            ((LightweightUnidimensionalResult)result).setMaleControlMean(data.getMaleControlMean());
            ((LightweightUnidimensionalResult)result).setMaleExperimentalMean(data.getMaleMutantMean());


            StatisticalResultMixedModel temp = new StatisticalResultMixedModel();
            temp.setBatchSignificance(data.getBatchIncluded());
            temp.setVarianceSignificance(data.getResidualVariancesHomogeneity());
            temp.setNullTestSignificance(data.getGenotypeContribution());
            temp.setGenotypeParameterEstimate( effectSize );
            temp.setGenotypeStandardErrorEstimate(data.getGenotypeStandardError());
            temp.setGenotypeEffectPValue( pValue );
            temp.setGenotypePercentageChange(data.getGenotypePercentageChange());
            temp.setGenderParameterEstimate(data.getSexEstimate());
            temp.setGenderStandardErrorEstimate(data.getSexStandardError());
            temp.setGenderEffectPValue(data.getSexPVal());
            temp.setWeightParameterEstimate(data.getWeightEstimate());
            temp.setWeightStandardErrorEstimate(data.getWeightStandardError());
            temp.setWeightEffectPValue(data.getWeightPVal());
            temp.setGp1Genotype(data.getGroup1Genotype());
            temp.setGp1ResidualsNormalityTest(data.getGroup1ResidualsNormalityTest());
            temp.setGp2Genotype(data.getGroup2Genotype());
            temp.setGp2ResidualsNormalityTest(data.getGroup2ResidualsNormalityTest());
            temp.setBlupsTest(data.getBlupsTest());
            temp.setRotatedResidualsNormalityTest(data.getRotatedResidualsNormalityTest());
            temp.setInterceptEstimate(data.getInterceptEstimate());
            temp.setInterceptEstimateStandardError(data.getInterceptStandardError());
            temp.setInteractionSignificance(data.getInteractionIncluded());
            temp.setInteractionEffectPValue(data.getInteractionPVal());
            temp.setGenderFemaleKoEstimate(data.getSexFvKOEstimate());
            temp.setGenderFemaleKoStandardErrorEstimate(data.getSexFvKOStandardError());
            temp.setGenderFemaleKoPValue(data.getSexFvKOPVal());
            temp.setGenderMaleKoEstimate(data.getSexMvKOEstimate());
            temp.setGenderMaleKoStandardErrorEstimate(data.getSexMvKOStandardError());
            temp.setGenderMaleKoPValue(data.getSexMvKOPVal());
            temp.setClassificationTag(data.getClassificationTag());

            statsResult = temp;
        } else if (parameterTypeMap.get(data.getDependentVariable()) == ObservationType.unidimensional && data.getStatisticalMethod().contains("Reference Ranges Plus framework")) {

            // Reference Range result
            result = new LightweightUnidimensionalResult();
            ((LightweightUnidimensionalResult)result).setFemaleControlMean(data.getFemaleControlMean());
            ((LightweightUnidimensionalResult)result).setFemaleExperimentalMean(data.getFemaleMutantMean());
            ((LightweightUnidimensionalResult)result).setMaleControlMean(data.getMaleControlMean());
            ((LightweightUnidimensionalResult)result).setMaleExperimentalMean(data.getMaleMutantMean());

            StatisticalResultReferenceRangePlus temp = new StatisticalResultReferenceRangePlus();
            temp.setBatchSignificance(data.getBatchIncluded());
            temp.setVarianceSignificance(data.getResidualVariancesHomogeneity());
            temp.setNullTestSignificance(data.getGenotypeContribution());
            temp.setGenotypeParameterEstimate(data.getGenotypeEstimate());
            temp.setGenotypeStandardErrorEstimate(data.getGenotypeStandardError());
            temp.setGenotypeEffectPValue(data.getGenotypePVal());
            temp.setGenotypePercentageChange(data.getGenotypePercentageChange());
            temp.setGenderParameterEstimate(data.getSexEstimate() != null ? data.getSexEstimate().toString() : null);
            temp.setGenderStandardErrorEstimate(data.getSexStandardError());
            temp.setGenderEffectPValue(data.getSexPVal() != null ? data.getSexPVal().toString() : null);
            temp.setWeightParameterEstimate(data.getWeightEstimate());
            temp.setWeightStandardErrorEstimate(data.getWeightStandardError());
            temp.setWeightEffectPValue(data.getWeightPVal());
            temp.setGp1Genotype(data.getGroup1Genotype());
            temp.setGp1ResidualsNormalityTest(data.getGroup1ResidualsNormalityTest());
            temp.setGp2Genotype(data.getGroup2Genotype());
            temp.setGp2ResidualsNormalityTest(data.getGroup2ResidualsNormalityTest());
            temp.setBlupsTest(data.getBlupsTest());
            temp.setRotatedResidualsNormalityTest(data.getRotatedResidualsNormalityTest());
            temp.setInterceptEstimate(data.getInterceptEstimate());
            temp.setInterceptEstimateStandardError(data.getInterceptStandardError());
            temp.setInteractionSignificance(data.getInteractionIncluded());
            temp.setInteractionEffectPValue(data.getInteractionPVal());
            temp.setGenderFemaleKoEstimate(data.getSexFvKOEstimate() != null ? data.getSexFvKOEstimate().toString() : null);
            temp.setGenderFemaleKoStandardErrorEstimate(data.getSexFvKOStandardError());
            temp.setGenderFemaleKoPValue(data.getSexFvKOPVal() != null ? data.getSexFvKOPVal().toString() : null);
            temp.setGenderMaleKoEstimate(data.getSexMvKOEstimate() != null ? data.getSexMvKOEstimate().toString() : null);
            temp.setGenderMaleKoStandardErrorEstimate(data.getSexMvKOStandardError());
            temp.setGenderMaleKoPValue(data.getSexMvKOPVal() != null ? data.getSexMvKOPVal().toString() : null);
            temp.setClassificationTag(data.getClassificationTag());

            statsResult = temp;

        } else {
            // Unknown method or failed to process.
            if (StringUtils.isNotEmpty(data.getStatisticalMethod())) {
                logger.debug("Unknown statistical method '" + data.getStatisticalMethod() + "'");
            }

            result = new LightweightUnidimensionalResult();
            StatisticalResultFailed temp = new StatisticalResultFailed();
            temp.setStatisticalMethod("Not processed");
            result.setStatus(data.getStatus() + "-" + data.getCode());
            statsResult = temp;
        }

        result.setStatisticalResult(statsResult);


        ControlStrategy strategy = ControlStrategy.NA;
        try {
            strategy = ControlStrategy.valueOf(data.getControlSelection());
        } catch (IllegalArgumentException e) {
            // It's ok, stats failed to process so control strat stays as NA
        }

        result.setMetadataGroup(data.getMetadataGroup());
        result.setStatisticalMethod(data.getStatisticalMethod().isEmpty() ? "-" : data.getStatisticalMethod());

        result.setDataSourceId(datasourceMap.get(data.getDataSourceName()));
        result.setProjectId(projectMap.get(data.getProjectName()));

        result.setOrganisationId(center.getDbId());
        result.setOrganisationName(center.getName());

        result.setPipelineId(pipeline.getDbId());
        result.setPipelineStableId(pipeline.getStableId());

        result.setProcedureId(procedure.getDbId());
        result.setProcedureGroup(data.getProcedure());

        result.setParameterId(parameter.getDbId());
        result.setParameterStableId(parameter.getStableId());
        result.setDependentVariable(parameter.getStableId());

        result.setColonyId(data.getColonyId());
        result.setStrain(data.getStrain());
        result.setZygosity(data.getZygosity());

        result.setZygosity(data.getZygosity());

        result.setControlSelectionMethod(strategy);

        Integer bioModelId = bioModelMap.get(data.getColonyId()).get(ZygosityType.valueOf(data.getZygosity()));
        result.setExperimentalId(bioModelId);

        Integer controlBioModelId = controlBioModelMap.get(bioModelStrainMap.get(bioModelId));
        result.setControlId(controlBioModelId);


        // Lookup from colony ID
        result.setAlleleAccessionId(colonyAlleleMap.get(data.getColonyId()));

        result.setMaleControlCount(data.getCountControlMale());
        result.setMaleMutantCount(data.getCountMutantMale());
        result.setFemaleControlCount(data.getCountControlFemale());
        result.setFemaleMutantCount(data.getCountMutantFemale());

        Set<String> sexes = new HashSet<>();
        if (data.getCountMutantMale() != null && data.getCountMutantMale()>3) {
            sexes.add("male");
        }
        if (data.getCountMutantFemale() != null && data.getCountMutantFemale()>3) {
            sexes.add("female");
        }

        // Set the sex(es) of the result set
        result.setSex(SexType.both.getName());
        if (sexes.size() == 1) {
            result.setSex(new ArrayList<>(sexes).get(0));
        }

        // Set workflow if it has been provided
        if (Arrays.stream(BatchClassification.values())
                .map(Enum::toString)
                .collect(Collectors.toList())
                .contains(data.getWorkflow())) {

            BatchClassification batches = BatchClassification.valueOf(data.getWorkflow());
            result.setWorkflow(batches);
        } else  {
            result.setWorkflow(BatchClassification.unknown);
        }

        result.setWeightAvailable(data.getWeightAvailable() != null && data.getWeightAvailable().equals("TRUE"));

        result.setCalculationTimeNanos(0L);
        result.setAdditionalInformation(data.getAdditionalInformation());

        result.setStatus(data.getStatus()=="Success" ? data.getStatus() : data.getStatus() + " - " + data.getCode());

        if (data.getStatus()=="Success") {
            setMpTerm(result);
        }

        return result;
    }

    /**
     * Set the appropriate MP term(s) on the result object
     * <p>
     * Could set differnet terms for male and female
     *
     * @param result the result object to populate
     * @throws SQLException
     */
    public void setMpTerm(LightweightResult result) {

        ResultDTO resultWrapper = new ResultDTO(result);

        try (Connection connection = komp2DataSource.getConnection()) {

            String parameterId = result.getParameterStableId();

            OntologyTerm mpTerm = mpTermService.getMPTerm(parameterId, resultWrapper, null, connection, 1.0f, false);

            if (mpTerm != null) {
                result.setMpAcc(mpTerm.getId().getAccession());

            } else {

                OntologyTerm femaleMpTerm = null;
                OntologyTerm maleMpTerm = null;

                if (resultWrapper.getFemaleEffectSize() != null) {
                    femaleMpTerm = mpTermService.getMPTerm(parameterId, resultWrapper, SexType.female, connection, 1.0f, false);
                }
                if (resultWrapper.getMaleEffectSize() != null) {
                    maleMpTerm = mpTermService.getMPTerm(parameterId, resultWrapper, SexType.male, connection, 1.0f, false);
                }

                if (femaleMpTerm != null && result instanceof LightweightUnidimensionalResult) {
                    ((LightweightUnidimensionalResult) result).setFemaleMpAcc(femaleMpTerm.getId().getAccession());
                }
                if (maleMpTerm != null && result instanceof LightweightUnidimensionalResult) {
                    ((LightweightUnidimensionalResult) result).setMaleMpAcc(maleMpTerm.getId().getAccession());
                }

                if (femaleMpTerm != null && maleMpTerm != null && maleMpTerm.equals(femaleMpTerm)) {

                    // Both female and male terms are defined and the same
                    result.setMpAcc(femaleMpTerm.getId().getAccession());

                } else {

                    // Sexual dimorphism, get the abnormal term for this parameter
                    mpTerm = mpTermService.getAbnormalMPTerm(parameterId, resultWrapper, connection, 1.0f);

                    if (mpTerm != null) {
                        result.setMpAcc(mpTerm.getId().getAccession());
                    }

                }
            }
        } catch (SQLException e) {
            logger.warn("Cannot retrieve MP term for result", result);
        }
    }


    //
    // MAIN METHOD
    //

    public static void main(String[] args) {
        SpringApplication.run(WindowingStatisticalResultLoader.class, args);
    }


    //
    // POPULATE CACHES
    //

    protected void populateParameterTypeMap() throws SQLException {
        Map<String, ObservationType> map = parameterTypeMap;

        String query= "SELECT DISTINCT * FROM phenotype_parameter_type";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {

                String parameterId = r.getString("parameter_stable_id");
                String obsType=r.getString("observation_type");

                map.put(parameterId, ObservationType.valueOf(obsType));
            }
        }

        logger.info(" Mapped {} parameter type entries", map.size());
    }

    protected void populateControlBioModelMap() throws SQLException {
        Map<String, Integer> map = controlBioModelMap;

        String query = "SELECT  * FROM biological_model bm " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bm.id " +
                "INNER JOIN strain ON strain.acc=bmstrain.strain_acc " +
                "WHERE allelic_composition = '' " ;

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {

                String background = r.getString("name");
                Integer modelId = r.getInt("biological_model_id");

                map.put(background, modelId);
            }
        }

        logger.info(" Mapped {} biological model entries", map.size());
    }

    protected void populateBioModelMap() throws SQLException {
        Map<String, Map<ZygosityType, Integer>> map = bioModelMap;

        String query = "SELECT DISTINCT colony_id, ls.zygosity, bm.id as biological_model_id, strain.name " +
                "FROM live_sample ls " +
                "INNER JOIN biological_sample bs ON ls.id=bs.id " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=ls.id " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " +
                "INNER JOIN strain ON strain.acc=bmstrain.strain_acc " +
                "INNER JOIN biological_model bm ON (bm.id=bms.biological_model_id AND bm.zygosity=ls.zygosity) " +
                "WHERE bs.sample_group = 'experimental' " ;

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {

                String colonyId = r.getString("colony_id");
                ZygosityType zyg = ZygosityType.valueOf(r.getString("zygosity"));
                Integer modelId = r.getInt("biological_model_id");
                String strain = r.getString("name");

                bioModelStrainMap.put(modelId, strain);

                map.putIfAbsent(colonyId, new HashMap<>());
                map.get(colonyId).put(zyg, modelId);
            }
        }

        logger.info(" Mapped {} biological model entries", map.size());
    }

    protected void populateColonyAlleleMap() throws SQLException {
        Map<String, String> map = colonyAlleleMap;

        String query = "SELECT DISTINCT colony_id, allele_acc " +
                "FROM live_sample ls " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=ls.id " +
                "INNER JOIN biological_model_allele bma ON bma.biological_model_id=bms.biological_model_id " +
                "INNER JOIN allele a ON a.acc=bma.allele_acc " ;

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                map.put(r.getString("colony_id"), r.getString("allele_acc"));
            }
        }

        logger.info(" Mapped {} colony/allele entries", map.size());
    }

    protected void populateDatasourceMap() throws SQLException {
        Map<String, Integer> map = datasourceMap;

        String query = "SELECT * FROM external_db";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                map.put(r.getString("short_name"), r.getInt("id"));
            }
        }

        logger.info(" Mapped {} datasource entries", map.size());
    }

    protected void populateProjectMap() throws SQLException {
        Map<String, Integer> map = projectMap;

        String query = "SELECT * FROM project";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                map.put(r.getString("name"), r.getInt("id"));
            }
        }

        logger.info(" Mapped {} project entries", map.size());
    }



    protected void populateOrganisationMap() throws SQLException {
        Map<String, NameIdDTO> map = organisationMap;

        // Populate the organisation map with this query
        String query = "SELECT * FROM organisation";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                map.put(r.getString("name"), new NameIdDTO(r.getInt("id"), r.getString("name")));
            }
        }

        logger.info(" Mapped {} organisation entries", map.size());
    }

    protected void populatePipelineMap() throws SQLException {
        Map<String, NameIdDTO> map = pipelineMap;

        String query = "SELECT * FROM phenotype_pipeline";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                map.put(r.getString("stable_id"), new NameIdDTO(r.getInt("id"), r.getString("name"), r.getString("stable_id")));
            }
        }

        logger.info(" Mapped {} pipeline entries", map.size());
    }

    /**
     * Lookup procedure by colony
     *
     * @throws SQLException
     */
    protected void populateColonyProcedureMap() throws SQLException {
        Map<String, Map<String, String>> map = colonyProcedureMap;

        // procedure_colony_id created by Derived Parameter job
        // Query to generate is:
        // CREATE TABLE procedure_colony_id AS
        //   SELECT DISTINCT procedure_stable_id, ls.colony_id
        //   FROM specimen_life_stage slf
        //   INNER JOIN live_sample ls ON ls.id=slf.biological_sample_id
        String query = "SELECT * FROM procedure_colony_id" ;

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {

                String procGroup = r.getString("procedure_stable_id");
                procGroup = StringUtils.join(ArrayUtils.subarray(procGroup.split("_"), 0, 2), "_");

                map.putIfAbsent(procGroup, new HashMap<>());
                map.get(procGroup).put(r.getString("colony_id"), r.getString("procedure_stable_id"));

            }
        }

        logger.info(" Mapped {} colony * parameter => procedure entries", map.size());
    }
    /**
     * Lookup procedure by procedure group (split by "_" chop the last element)
     * @throws SQLException
     */
    protected void populateProcedureMap() throws SQLException {
        Map<String, NameIdDTO> map = procedureMap;

        // Order by ID en
        String query = "SELECT * FROM phenotype_procedure";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                String procGroup = r.getString("stable_id");
                map.put(procGroup, new NameIdDTO(r.getInt("id"), r.getString("name"), r.getString("stable_id")));
            }
        }

        logger.info(" Mapped {} procedure entries", map.size());
    }

    protected void populateParameterMap() throws SQLException {
        Map<String, NameIdDTO> map = parameterMap;

        String query = "SELECT * FROM phenotype_parameter";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                map.put(r.getString("stable_id"), new NameIdDTO(r.getInt("id"), r.getString("name"), r.getString("stable_id")));
            }
        }

        logger.info(" Mapped {} parameter entries", map.size());
    }



    //
    // SUPPORTING METHODS
    //


    protected boolean isValidInt(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Categorical results represent genotype effect as a percent (i.e. 75%)
     * This method Convert things with a "%" to a double value between 0 and 1
     * and a regular number to a double.
     *
     * @param str the string to convert to a number
     * @return A double or null if str does not represent a number
     */
    protected Double getDoubleField(String str) {

        if (str == null) {
            return null;
        }

        Double retVal;
        if (str.contains("%")) {
            String n = str.replaceAll("%", "");
            retVal = (NumberUtils.isNumber(n)) ? Double.parseDouble(n) : null;
            if (retVal != null) {
                // Normalize to percent between 0.0 and 1.0
                retVal = retVal / 100.0;
            }
        } else {
            retVal = (NumberUtils.isNumber(str)) ? Double.parseDouble(str) : null;
        }
        return retVal;
    }

    protected Integer getIntegerField(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length == 0) {
            return null;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return null;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return null;
            }
        }

        return Integer.parseInt(str);
    }

    /** Convert "NA" to empty string
     *
     * @param str the string to convert
     * @return the converted string
     */
    protected String getStringField(String str) {

        if (str==null || str.isEmpty() || str.equals("NA")) {
            return "";
        }

        return str;
    }


    /**
     * Will return true only when str is TRUE or true
     */
    Boolean getBooleanField(String str) {
        return Boolean.parseBoolean(str);
    }



}
