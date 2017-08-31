package org.mousephenotype.cda.loads.statistics.load;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.mousephenotype.cda.enumerations.BatchClassification;
import org.mousephenotype.cda.enumerations.ControlStrategy;
import org.mousephenotype.cda.enumerations.SexType;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@Import(value = {StatisticalResultLoaderConfig.class})
public class StatisticalResultLoader extends BasicService implements CommandLineRunner {



    final private Logger logger = LoggerFactory.getLogger(getClass());
    final private DataSource komp2DataSource;


    Map<String, NameIdDTO> organisationMap = new HashMap<>();
    Map<String, NameIdDTO> pipelineMap = new HashMap<>();
    Map<String, NameIdDTO> procedureMap = new HashMap<>();
    Map<String, NameIdDTO> parameterMap = new HashMap<>();


    void populateOrganisationMap() throws SQLException {
        Map map = organisationMap;

        // Populate the organisation map with this query
        String query = "SELECT * FROM organisation";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                map.put(r.getString("stable_id"), new NameIdDTO(r.getInt("id"), r.getString("name")));
            }
        }

        logger.info(" Mapped {} organisation entries", map.size());
    }

    void populatePipelineMap() throws SQLException {
        Map map = pipelineMap;

        String query = "SELECT * FROM phenotype_pipeline";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                map.put(r.getString("stable_id"), new NameIdDTO(r.getInt("id"), r.getString("name"), r.getString("stable_id")));
            }
        }

        logger.info(" Mapped {} pipeline entries", map.size());
    }

    void populateProcedureMap() throws SQLException {
        Map map = procedureMap;

        String query = "SELECT * FROM phenotype_procedure ORDER BY id";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                String procGroup = r.getString("stable_id");
                map.put(procGroup, new NameIdDTO(r.getInt("id"), r.getString("name"), r.getString("stable_id")));
            }
        }

        logger.info(" Mapped {} procedure entries", map.size());
    }

    void populateParameterMap() throws SQLException {
        Map map = parameterMap;

        String query = "SELECT * FROM phenotype_parameter";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {
                map.put(r.getString("stable_id"), new NameIdDTO(r.getInt("id"), r.getString("name"), r.getString("stable_id")));
            }
        }

        logger.info(" Mapped {} parameter entries", map.size());
    }



    private String fileLocation;

    @Inject
    public StatisticalResultLoader(@Named("komp2DataSource") DataSource komp2DataSource) {
        Assert.notNull(komp2DataSource, "Komp2 datasource cannot be null");
        this.komp2DataSource = komp2DataSource;
    }


    private boolean isValid(String check) {
        if (check.isEmpty()) {
            return false;
        }
        if (check.equals("NA")) {
            return false;
        }
        return true;
    }

    private boolean isValidInt(String str) {
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
     * Process a string from a results file into a LineStatisticalResult object
     *
     * field list
     0 metadata_group
     1 zygosity
     2 colony_id
     3 depvar
     4 status
     5 code
     6 count cm
     7 count cf
     8 count mm
     9 count mf
     10 Method
     11 Dependent variable
     12 Batch included
     13 Residual variances homogeneity
     14 Genotype contribution
     15 Genotype estimate
     16 Genotype standard error
     17 Genotype p-Val
     18 Genotype percentage change
     19 Sex estimate
     20 Sex standard error
     21 Sex p-val
     22 Weight estimate
     23 Weight standard error
     24 Weight p-val
     25 Gp1 genotype
     26 Gp1 Residuals normality test
     27 Gp2 genotype
     28 Gp2 Residuals normality test
     29 Blups test
     30 Rotated residuals normality test
     31 Intercept estimate
     32 Intercept standard error
     33 Interaction included
     34 Interaction p-val
     35 Sex FvKO estimate
     36 Sex FvKO standard error
     37 Sex FvKO p-val
     38 Sex MvKO estimate
     39 Sex MvKO standard error
     40 Sex MvKO p-val
     41 Classification tag
     42 Additional information

     * @param data a line from a statistical results file
     * @throws IOException
     */
    LineStatisticalResult getResult(String data) {

        LineStatisticalResult result = new LineStatisticalResult();

        String [] fields = data.replace(System.getProperty("line.separator"), "").split("\t", -1);
        System.out.println(org.apache.commons.lang.StringUtils.join(fields, ","));
        StatusCode status = StatusCode.valueOf(fields[4]);


        result.setMetadataGroup( (isValid(fields[0])) ? fields[0] : "");
        result.setZygosity( (isValid(fields[1])) ? fields[1] : "");
        result.setColonyId( (isValid(fields[2])) ? fields[2] : "");
        result.setDependentVariable( (isValid(fields[3])) ? fields[3] : "");
        result.setStatus( status.name() );
        result.setCode( (isValid(fields[5])) ? fields[5] : "");
        result.setCountControlMale( (isValidInt(fields[6])) ? Integer.parseInt(fields[6]) : null);
        result.setCountControlFemale( (isValidInt(fields[7])) ? Integer.parseInt(fields[7]) : null);
        result.setCountMutantMale( (isValidInt(fields[8])) ? Integer.parseInt(fields[8]) : null);
        result.setCountMutantFemale( (isValidInt(fields[9])) ? Integer.parseInt(fields[9]) : null);
        result.setStatisticalMethod( (isValid(fields[10])) ? fields[10] : "");

        // fields[11] is a duplicate of fields[3]


        if (fields.length < 40) {
            return result;
        }

        switch(status) {
            case TESTED:
                // Result was processed successfully by PhenStat, load the result object

                result.setBatchIncluded( (isValid(fields[12])) ? fields[12] : "");
                result.setResidualVariancesHomogeneity( (isValid(fields[13])) ? fields[13] : "");
                result.setGenotypeContribution( (isValid(fields[14])) ? fields[14] : "");
                result.setGenotypeEstimate( (isValid(fields[15])) ? fields[15] : "");
                result.setGenotypeStandardError( (isValid(fields[16])) ? fields[16] : "");
                result.setGenotypePVal( (isValid(fields[17])) ? fields[17] : "");
                result.setGenotypePercentageChange( (isValid(fields[18])) ? fields[18] : "");
                result.setSexEstimate( (isValid(fields[19])) ? fields[19] : "");
                result.setSexStandardError( (isValid(fields[20])) ? fields[20] : "");
                result.setSexPVal( (isValid(fields[21])) ? fields[21] : "");
                result.setWeightEstimate( (isValid(fields[22])) ? fields[22] : "");
                result.setWeightStandardError( (isValid(fields[23])) ? fields[23] : "");
                result.setWeightPVal( (isValid(fields[24])) ? fields[24] : "");
                result.setGroup1Genotype( (isValid(fields[25])) ? fields[25] : "");
                result.setGroup1ResidualsNormalityTest( (isValid(fields[26])) ? fields[26] : "");
                result.setGroup2Genotype( (isValid(fields[27])) ? fields[27] : "");
                result.setGroup2ResidualsNormalityTest( (isValid(fields[28])) ? fields[28] : "");
                result.setBlupsTest( (isValid(fields[29])) ? fields[29] : "");
                result.setRotatedResidualsNormalityTest( (isValid(fields[30])) ? fields[30] : "");
                result.setInterceptEstimate( (isValid(fields[31])) ? fields[31] : "");
                result.setInterceptStandardError( (isValid(fields[32])) ? fields[32] : "");
                result.setInteractionIncluded( (isValid(fields[33])) ? fields[33] : "");
                result.setInteractionPVal( (isValid(fields[34])) ? fields[34] : "");
                result.setSexFvKOEstimate( (isValid(fields[35])) ? fields[35] : "");
                result.setSexFvKOStandardError( (isValid(fields[36])) ? fields[36] : "");
                result.setSexFvKOPVal( (isValid(fields[37])) ? fields[37] : "");
                result.setSexMvKOEstimate( (isValid(fields[38])) ? fields[38] : "");
                result.setSexMvKOStandardError( (isValid(fields[39])) ? fields[39] : "");
                result.setSexMvKOPVal( (isValid(fields[40])) ? fields[40] : "");
                result.setClassificationTag( (isValid(fields[41])) ? fields[41] : "");
                result.setAdditionalInformation( (isValid(fields[42])) ? fields[42] : "");

                break;
            case FAILED:
                // Result failed to be processed by PhenStat
                break;
            default:
                result = null;
                break;
        }

        return result;
    }


    private enum StatusCode {
            TESTED,
            FAILED;
    }

    private class NameIdDTO {

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
    public LightweightUnidimensionalResult getBaseResult(LineStatisticalResult data) {

        NameIdDTO center = organisationMap.get(data.getCenter());
        NameIdDTO pipeline = pipelineMap.get(data.getPipeline());
        NameIdDTO procedure = pipelineMap.get(data.getProcedure()); // Procedure group e.g. IMPC_CAL
        NameIdDTO parameter = parameterMap.get(data.getDependentVariable());
        ControlStrategy strategy = ControlStrategy.valueOf(data.getControlSelection());


        // result contains a "statistical result" that has the
        // ability to produce a PreparedStatement ready for database insertion
        LightweightUnidimensionalResult result = new LightweightUnidimensionalResult();

        result.setMetadataGroup(data.getMetadataGroup());

        // TODO: how do we get these?
//        result.setDataSourceId(wrapper.getDataSourceId());
//        result.setProjectId(wrapper.getProjectId());

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

        result.setExperimentalZygosity(data.getZygosity());

        result.setControlSelectionMethod(strategy);

        // TODO: Lookup from specimen?
//        result.setControlId(data.getControlBiologicalModelId());
//        result.setExperimentalId(data.getMutantBiologicalModelId());
//        result.setAlleleAccessionId(data.getAlleleAccessionId());

        result.setMaleControlCount(data.getCountControlMale());
        result.setMaleMutantCount(data.getCountMutantMale());
        result.setFemaleControlCount(data.getCountControlFemale());
        result.setFemaleMutantCount(data.getCountMutantFemale());

        result.setFemaleControlMean(data.getFemaleControlMean());
        result.setFemaleExperimentalMean(data.getFemaleMutantMean());
        result.setMaleControlMean(data.getMaleControlMean());
        result.setMaleExperimentalMean(data.getMaleMutantMean());

        Set<String> sexes = new HashSet<>();
        if (data.getCountMutantMale()>3) {
            sexes.add("male");
        }
        if (data.getCountMutantFemale()>3) {
            sexes.add("female");
        }

        // Set the sex(es) of the result set
        result.setSex(SexType.both.getName());
        if (sexes.size() == 1) {
            result.setSex(new ArrayList<String>(sexes).get(0));
        }

        BatchClassification batches = BatchClassification.valueOf(data.getWorkflow());
        result.setWorkflow(batches);
//        result.setWeightAvailable(data.we);

        result.setCalculationTimeNanos(0L);

        return result;
    }


    private void processFile(String loc) throws IOException {

        for (String line : Files.readAllLines(Paths.get(loc))) {

//            LineStatisticalResult result = getResult(line);
            LightweightUnidimensionalResult result = getBaseResult(getResult(line));

        }

    }

    @Override
    public void run(String... strings) throws Exception {

        logger.info("Starting statistical result loader");

        // Populate lookups
        populateOrganisationMap();
        populatePipelineMap();
        populateProcedureMap();
        populateParameterMap();


        // parameter to indicate the location of the result file(s)
        OptionParser parser = new OptionParser();
        parser.accepts("location").withRequiredArg().ofType(String.class);
        OptionSet options = parser.parse(strings);
        fileLocation = (String) options.valuesOf("location").get(0);

        // If the location is a single file, parse it
        boolean regularFile = Files.isRegularFile(Paths.get(fileLocation));
        boolean directory = Files.isDirectory(Paths.get(fileLocation));

        if (regularFile) {

            // process the file
            processFile(fileLocation);

        } else if (directory) {

            // process all files in the directoy
            try (Stream<Path> paths = Files.walk(Paths.get(fileLocation), 1)) {
                for (Path path : paths.collect(Collectors.toList())) {
                    if (path.endsWith("result")) {
                        processFile(path.toString());
                    }
                }
            }
        }


    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StatisticalResultLoader.class, args);
    }



}
