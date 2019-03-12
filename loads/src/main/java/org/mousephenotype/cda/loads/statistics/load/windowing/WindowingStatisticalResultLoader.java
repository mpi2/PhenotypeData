package org.mousephenotype.cda.loads.statistics.load.windowing;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.json.JSONException;
import org.json.JSONObject;
import org.mousephenotype.cda.db.statistics.LightweightResult;
import org.mousephenotype.cda.db.statistics.LineStatisticalResult;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.loads.statistics.load.StatisticalResultLoader;
import org.mousephenotype.cda.loads.statistics.load.StatisticalResultLoaderConfig;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@Import(value = {StatisticalResultLoaderConfig.class})
public class WindowingStatisticalResultLoader extends StatisticalResultLoader implements CommandLineRunner {

    final private Logger logger = LoggerFactory.getLogger(getClass());


    public WindowingStatisticalResultLoader() { }

    @Inject
    public WindowingStatisticalResultLoader(@Named("komp2DataSource") DataSource komp2DataSource, MpTermService mpTermService) {
        Assert.notNull(komp2DataSource, "Komp2 datasource cannot be null");
        Assert.notNull(mpTermService, "mpTermService cannot be null");
        this.komp2DataSource = komp2DataSource;
        this.mpTermService = mpTermService;
    }

    public static void main(String[] args) {
        SpringApplication.run(StatisticalResultLoader.class, args);
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
        boolean directory = Files.isDirectory(Paths.get(fileLocation));
        boolean regularFile = Files.isRegularFile(Paths.get(fileLocation));

        initializeMaps();


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

                saveResult(counts, result);
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

    }


    /**
     * Process a string from a results file into a LineStatisticalResult object
     *
     * field list
     *
     *
     *
     * @param data a line from a statistical results file
     * @throws JSONException sometimes
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

        if ("TESTED".equals(result.getStatus())) {
            // Result was processed successfully by PhenStat, load the result object

            // Always set status to Success with successfully processed
            result.setStatus("Success");


            // Vector output results from PhenStat start at field 19
            int i = 19;

            result.setBatchIncluded(getBooleanField(fields[i++]));
            result.setResidualVariancesHomogeneity(getBooleanField(fields[i++]));
            result.setGenotypeContribution(getDoubleField(fields[i++]));
            result.setGenotypeEstimate(getStringField(fields[i++]));
            result.setGenotypeStandardError(getDoubleField(fields[i++]));
            result.setGenotypePVal(getStringField(fields[i++]));
            result.setGenotypePercentageChange(getStringField(fields[i++]));
            result.setSexEstimate(getDoubleField(fields[i++]));
            result.setSexStandardError(getDoubleField(fields[i++]));
            result.setSexPVal(getDoubleField(fields[i++]));
            result.setWeightEstimate(getDoubleField(fields[i++]));
            result.setWeightStandardError(getDoubleField(fields[i++]));
            result.setWeightPVal(getDoubleField(fields[i++]));
            result.setGroup1Genotype(getStringField(fields[i++]));
            result.setGroup1ResidualsNormalityTest(getDoubleField(fields[i++]));
            result.setGroup2Genotype(getStringField(fields[i++]));
            result.setGroup2ResidualsNormalityTest(getDoubleField(fields[i++]));
            result.setBlupsTest(getDoubleField(fields[i++]));
            result.setRotatedResidualsNormalityTest(getDoubleField(fields[i++]));
            result.setInterceptEstimate(getDoubleField(fields[i++]));
            result.setInterceptStandardError(getDoubleField(fields[i++]));
            result.setInteractionIncluded(getBooleanField(fields[i++]));
            result.setInteractionPVal(getDoubleField(fields[i++]));
            result.setSexFvKOEstimate(getDoubleField(fields[i++]));
            result.setSexFvKOStandardError(getDoubleField(fields[i++]));
            result.setSexFvKOPVal(getDoubleField(fields[i++]));
            result.setSexMvKOEstimate(getDoubleField(fields[i++]));
            result.setSexMvKOStandardError(getDoubleField(fields[i++]));
            result.setSexMvKOPVal(getDoubleField(fields[i++]));
            result.setClassificationTag(getStringField(fields[i++]));
            result.setAdditionalInformation(getStringField(fields[i++]));

            logger.debug("Last iteration left index i at: ", i);
        } else {
            // Result was not processed by PhenStat

            result.setBatchIncluded(null);
            result.setResidualVariancesHomogeneity(null);
            result.setGenotypeContribution(null);
            result.setGenotypeEstimate(null);
            result.setGenotypeStandardError(null);
            result.setGenotypePVal(null);
            result.setGenotypePercentageChange(null);
            result.setSexEstimate(null);
            result.setSexStandardError(null);
            result.setSexPVal(null);
            result.setWeightEstimate(null);
            result.setWeightStandardError(null);
            result.setWeightPVal(null);
            result.setGroup1Genotype(null);
            result.setGroup1ResidualsNormalityTest(null);
            result.setGroup2Genotype(null);
            result.setGroup2ResidualsNormalityTest(null);
            result.setBlupsTest(null);
            result.setRotatedResidualsNormalityTest(null);
            result.setInterceptEstimate(null);
            result.setInterceptStandardError(null);
            result.setInteractionIncluded(null);
            result.setInteractionPVal(null);
            result.setSexFvKOEstimate(null);
            result.setSexFvKOStandardError(null);
            result.setSexFvKOPVal(null);
            result.setSexMvKOEstimate(null);
            result.setSexMvKOStandardError(null);
            result.setSexMvKOPVal(null);
            result.setClassificationTag(null);
            result.setAdditionalInformation("Orig data: " + data);
        }


        return result;
    }


}
