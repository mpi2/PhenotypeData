package org.mousephenotype.cda.loads.statistics.load.threei;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.db.pojo.PhenotypedColony;
import org.mousephenotype.cda.enumerations.*;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.statistics.load.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@ComponentScan(basePackages = {"org.mousephenotype.cda.loads.statistics.load", "org.mousephenotype.cda.loads.common"})
public class ThreeIStatisticalResultLoader extends StatisticalResultLoader implements CommandLineRunner {

    final private Logger logger = LoggerFactory.getLogger(getClass());

    Map<String, PhenotypedColony> phenotypedColonies;

    private Resource threeIFile;

    @Inject
    public ThreeIStatisticalResultLoader(@Named("komp2DataSource") DataSource komp2DataSource,
                                         MpTermService mpTermService,
                                         CdaSqlUtils cdaSqlUtils,
                                         Resource threeIFile) {
        Assert.notNull(komp2DataSource, "Komp2 datasource cannot be null");
        Assert.notNull(mpTermService, "mpTermService cannot be null");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils cannot be null");

        this.komp2DataSource = komp2DataSource;
        this.mpTermService = mpTermService;

        this.phenotypedColonies = cdaSqlUtils.getPhenotypedColonies();
        this.threeIFile = threeIFile;

    }


    /**
     * Process a string from a results file into a LineStatisticalResult object
     *
     * @param data a line from a statistical results file
     * @throws IOException
     */
    LineStatisticalResult getResult(ThreeIFileResult data) {

        LineStatisticalResult result = new LineStatisticalResult();

        PhenotypedColony colony = phenotypedColonies.get(data.getColony_Prefixes());

        if (colony == null) {
            logger.info("Cannot find colony ID for line {}. Skipping", data);
            return null;
        }

        if (parameterTypeMap.get(data.getParameter_Id()) == null) {
            logger.info("Cannot find parameter type for line {}. Skipping", data);
            return null;
        }

        String dataSource = "3I";
        String project = "3I";
        String center = "WTSI";
        String pipeline = "MGP_001";
        String procedure = data.getProcedure_Id();
        String strain = colony.getBackgroundStrain();

        result.setDataSource(dataSource);
        result.setProject(project);
        result.setCenter(center);
        result.setPipeline(pipeline);
        result.setProcedure(procedure);
        result.setStrain(strain);

        result.setMetadataGroup("");

        String zygosity;
        switch (data.getGenotype()) {
            case "Het":
                zygosity = ZygosityType.heterozygote.getName();
                break;
            case "Hom":
                zygosity = ZygosityType.homozygote.getName();
                break;
            case "Hemi":
                zygosity = ZygosityType.hemizygote.getName();
                break;
            default:
                logger.info("Cannot determine zygosity for line {}. Skipping", data);

                // Short circuit since can't determine zygosity
                return null;
        }

        result.setZygosity(zygosity);
        result.setColonyId(colony.getColonyName());
        result.setDependentVariable(data.getParameter_Id());


        result.setCountControlMale(null);
        result.setCountControlFemale(null);
        result.setCountMutantMale(-1);
        result.setCountMutantFemale(-1);

        if (data.getGender().equals("Female")) {
            result.setCountMutantFemale(getIntegerField(data.getSamples()));
        } else if (data.getGender().equals("Male")) {
            result.setCountMutantMale(getIntegerField(data.getSamples()));
        }

        result.setMaleControlMean(null);
        result.setFemaleControlMean(null);
        result.setMaleMutantMean(null);
        result.setFemaleMutantMean(null);

        result.setControlSelection("Manual");
        result.setWorkflow("Manual");
        result.setWeightAvailable("false");

        result.setStatisticalMethod("Manual");
        result.setStatus("Success");
        result.setCode("OK");

        if (data.getCall_Type().toLowerCase().equals("pending")) {
            result.setStatus("Not processed");
            result.setStatisticalMethod("Manual");
            result.setCode("Data analysis pending");
        }


        result.setGenotypePVal(null);
        result.setGenotypeEstimate(null);

        if (data.getCall_Type().equals("Significant")) {
            result.setGenotypePVal("0");
            result.setGenotypeEstimate("1");
        }

        result.setBatchIncluded(null);
        result.setResidualVariancesHomogeneity(null);
        result.setGenotypeContribution(null);
        result.setGenotypeStandardError(null);
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

        result.setClassificationTag("Analysis performed by 3I consortium");
        result.setAdditionalInformation(null);

        return result;
    }


    /**
     * Generate the data set to process
     *
     * @param data The data result from a stats analysis
     * @return result object partially populated with basic information
     */
    public LightweightResult getBaseResult(LineStatisticalResult data) {

        if (data == null) {
            return null;
        }

        StatisticalResultLoader.NameIdDTO center = organisationMap.get(data.getCenter());
        StatisticalResultLoader.NameIdDTO pipeline = pipelineMap.get(data.getPipeline());
        StatisticalResultLoader.NameIdDTO procedure = procedureMap.get(data.getProcedure()); // Procedure group e.g. IMPC_CAL
        StatisticalResultLoader.NameIdDTO parameter = parameterMap.get(data.getDependentVariable());

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
            ((LightweightCategoricalResult) result).setCategoryA("normal");
            ((LightweightCategoricalResult) result).setpValue(pValue);
            ((LightweightCategoricalResult) result).setEffectSize(effectSize);

            ((LightweightCategoricalResult) result).setMalePValue(malePValue);
            ((LightweightCategoricalResult) result).setMaleEffectSize(maleEffectSize);
            ((LightweightCategoricalResult) result).setFemalePValue(femalePValue);
            ((LightweightCategoricalResult) result).setFemaleEffectSize(femaleEffectSize);
            ((LightweightCategoricalResult) result).setClassificationTag(classificationTag);

            StatisticalResultCategorical temp = new StatisticalResultCategorical();
            temp.setpValue(pValue);
            temp.setEffectSize(effectSize);
            temp.setMalePValue(malePValue);
            temp.setMaleEffectSize(maleEffectSize);
            temp.setFemalePValue(femalePValue);
            temp.setFemaleEffectSize(femaleEffectSize);
            temp.setClassificationTag(classificationTag);
            statsResult = temp;

        } else if (parameterTypeMap.get(data.getDependentVariable()) == ObservationType.unidimensional) {

            Double effectSize = getDoubleField(data.getGenotypeEstimate());
            Double pValue = getDoubleField(data.getGenotypePVal());

            // Unidimensional result
            result = new LightweightUnidimensionalResult();
            ((LightweightUnidimensionalResult) result).setFemaleControlMean(data.getFemaleControlMean());
            ((LightweightUnidimensionalResult) result).setFemaleExperimentalMean(data.getFemaleMutantMean());
            ((LightweightUnidimensionalResult) result).setMaleControlMean(data.getMaleControlMean());
            ((LightweightUnidimensionalResult) result).setMaleExperimentalMean(data.getMaleMutantMean());


            StatisticalResultMixedModel temp = new StatisticalResultMixedModel();
            temp.setBatchSignificance(data.getBatchIncluded());
            temp.setVarianceSignificance(data.getResidualVariancesHomogeneity());
            temp.setNullTestSignificance(data.getGenotypeContribution());
            temp.setGenotypeParameterEstimate(effectSize);
            temp.setGenotypeStandardErrorEstimate(data.getGenotypeStandardError());
            temp.setGenotypeEffectPValue(pValue);
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

        //TODO: Wire the biomodels to the SR
//        Integer bioModelId = bioModelMap.get(data.getColonyId()).get(ZygosityType.valueOf(data.getZygosity()));
//        result.setExperimentalId(bioModelId);

        result.setControlId(null);


        // Lookup from colony ID
        result.setAlleleAccessionId(colonyAlleleMap.get(data.getColonyId()));

        result.setMaleControlCount(data.getCountControlMale());
        result.setMaleMutantCount(data.getCountMutantMale());
        result.setFemaleControlCount(data.getCountControlFemale());
        result.setFemaleMutantCount(data.getCountMutantFemale());

        Set<String> sexes = new HashSet<>();
        if (data.getCountMutantMale() > 3) {
            sexes.add("male");
        }
        if (data.getCountMutantFemale() > 3) {
            sexes.add("female");
        }

        // Set the sex(es) of the result set
        result.setSex(SexType.both.getName());
        if (sexes.size() == 1) {
            result.setSex(new ArrayList<>(sexes).get(0));
        }

        result.setWorkflow(BatchClassification.unknown);

        result.setWeightAvailable(false);

        result.setCalculationTimeNanos(0L);

        result.setStatus(data.getStatus() == "Success" ? data.getStatus() : data.getStatus() + " - " + data.getCode());

        if (data.getStatus() == "Success") {
            setMpTerm(result);
        }

        return result;
    }


    private void processFile(Resource threeIFile) {

        try {
            Map<String, Integer> counts = new HashMap<>();

            String loc = threeIFile.getFile().getAbsolutePath();

            String allData = Files.readAllLines(Paths.get(loc)).stream().map(String::trim).collect(Collectors.joining("\n"));

            CsvMapper mapper = new CsvMapper();

            // schema from 'ThreeIFileResult' definition
            CsvSchema schema = mapper.schemaFor(ThreeIFileResult.class).withHeader();
            MappingIterator<ThreeIFileResult> it = mapper.readerFor(ThreeIFileResult.class).with(schema).readValues(allData);
            List<ThreeIFileResult> all = it.readAll();


            for (ThreeIFileResult data : all) {

                LightweightResult result = getBaseResult(getResult(data));

                if (result == null) {
                    // Skipping record
                    continue;
                }

                try (Connection connection = komp2DataSource.getConnection()) {

                    PreparedStatement p = result.getSaveResultStatement(connection);
                    p.executeUpdate();

                    counts.put(result.getStatisticalMethod(), counts.getOrDefault(result.getStatisticalMethod(), 0) + 1);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            for (String method : counts.keySet()) {
                logger.info("  ADDED " + counts.get(method) + " statistical results for method: " + method);
            }

        } catch (Exception e) {
            logger.error("Could not process 3I input file", e);
        }


    }

    @Override
    public void run(String... strings) throws Exception {

        logger.info("Starting 3I statistical result loader");

        // Populate lookups
        populateOrganisationMap();
        populatePipelineMap();
        populateProcedureMap();
        populateParameterMap();
        populateDatasourceMap();
        populateProjectMap();
        populateColonyAlleleMap();
        populateBioModelMap();
        populateControlBioModelMap();
        populateParameterTypeMap();

        // Update the parameter type map if any of the 3I parameters are imssing data in the database
        parameterTypeMap.putIfAbsent("MGP_MLN_111_001", ObservationType.unidimensional);
        parameterTypeMap.putIfAbsent("MGP_MLN_026_001", ObservationType.unidimensional);


        // process the 3I file provided by Ania@3I consortium 2018-01-19
        processFile(threeIFile);


    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ThreeIStatisticalResultLoader.class, args);
    }


}
