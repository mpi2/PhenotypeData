package org.mousephenotype.cda.loads.statistics.load.threei;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.PhenotypedColony;
import org.mousephenotype.cda.db.statistics.*;
import org.mousephenotype.cda.enumerations.*;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.config.DataSourceCdaConfig;
import org.mousephenotype.cda.loads.common.config.DataSourceCdabaseConfig;
import org.mousephenotype.cda.loads.common.config.DataSourceDccConfig;
import org.mousephenotype.cda.loads.statistics.load.impc.StatisticalResultFailed;
import org.mousephenotype.cda.loads.statistics.load.impc.StatisticalResultLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@ComponentScan(basePackages = {"org.mousephenotype.cda.loads.statistics.load", "org.mousephenotype.cda.loads.common"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                        DataSourceCdabaseConfig.class,
                        DataSourceCdaConfig.class,
                        DataSourceDccConfig.class,
                        CdaSqlUtils.class})})
public class ThreeIStatisticalResultLoader extends StatisticalResultLoader implements CommandLineRunner {

    final private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, PhenotypedColony> phenotypedColonies;
    private Set<String> missingColonies;
    private Set<String> missingProcedures;

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

        missingProcedures = new HashSet<>();
        missingColonies = new HashSet<>();
    }


    /**
     * Process a string from a results file into a LineStatisticalResult object
     *
     * @param data a line from a statistical results file
     */
    LineStatisticalResult getResult(ThreeIFileResult data) {

        LineStatisticalResult result = new LineStatisticalResult();

        if (missingColonies.contains(data.getColony_Prefixes())) {
            return null;
        }

        PhenotypedColony colony = phenotypedColonies.get(data.getColony_Prefixes());

        if (colony == null) {
            missingColonies.add(data.getColony_Prefixes());
            logger.info("Cannot find colony ID for line {}. Skipping", data);
            return null;
        }

        if (parameterTypeMap.get(data.getParameter_Id()) == null) {
            logger.info("Cannot find parameter type for line {}. Skipping", data);
            return null;
        }

        String dataSource = "3i";
        String project = "3i";
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
        result.setSuggestedMpTerm(data.getAnnotation_Calls());

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

        // 20180315 Per Lucie
        // - For flow data, use 70 for control count per sex
        //    - This is for Spleen (IMM), MLN, and bone marrow () screens
        //
        // - ANA is "all" controls:
        //     - Females: 569
        //     - Males: 545
        //
        // - Ear is "all" controls:
        //   - Females: 362
        //   - Males: 366
        //
        // - Blood is "all" controls, but values vary by parameter

        switch (data.getProcedure_Id()) {

            case "MGP_ANA_001":
                result.setCountControlMale(545);
                result.setCountControlFemale(569);
                break;

            case "MGP_EEI_001":
                result.setCountControlMale(366);
                result.setCountControlFemale(362);
                break;

            case "MGP_IMM_001":
            case "MGP_MLN_001":
            case "MGP_BMI_001":
                result.setCountControlMale(70);
                result.setCountControlFemale(70);
                break;

            case "MGP_PBI_001":

                /*
                    2018-03-21
                    Per Lucie Abeler-Dörner, blood needs to be called by IMPC for the time being

                    "
                    Is the global reference range now part of the IMPC statistical pipeline?
                    If yes, blood should go through the analysis independent of 3i data.
                    If the reference range is not available, it will have to go through
                    the mixed model as it did before.
                    "

                    So, for now, we do not process the blood hits supplied in the file

                 */

                if ( ! missingProcedures.contains(data.getProcedure_Id()))
                {
                    logger.warn("Cannot find control counts for procedure {}", data.getProcedure_Id());
                    missingProcedures.add(data.getProcedure_Id());

                }

                return null;

            // List supplied by Ania 20180315
            // Blood parameter control counts vary by parameter

//                switch(data.getParameter_Name()) {
//                    case "Total_T_Cell_Percentage":
//                        result.setCountControlMale(832);
//                        result.setCountControlFemale(839);
//                        break;
//                    case "Alpha_Beta_T_Cell_Percentage":
//                        result.setCountControlMale(833);
//                        result.setCountControlFemale(842);
//                        break;
//                    case "Cd4_Alpha_Beta_T_Cell_Percentage":
//                        result.setCountControlMale(828);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Cd8_Alpha_Beta_T_Cell_Percentage":
//                        result.setCountControlMale(832);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Gamma_Delta_T_Cell_Percentage":
//                        result.setCountControlMale(832);
//                        result.setCountControlFemale(839);
//                        break;
//                    case "NKT_Cell_Percentage":
//                        result.setCountControlMale(833);
//                        result.setCountControlFemale(842);
//                        break;
//                    case "Nk_Cell_Percentage":
//                        result.setCountControlMale(825);
//                        result.setCountControlFemale(837);
//                        break;
//                    case "Cd4_Cd25_Alpha_Beta_Reg_Percent":
//                        result.setCountControlMale(825);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Cd4_Cd44_Cd62l_Alpha_Beta_Percent":
//                        result.setCountControlMale(825);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Cd8_Cd44_Cd62l_Alpha_Beta_Percent":
//                        result.setCountControlMale(830);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Klrg1_Mature_Nk_Cell_Percentage":
//                        result.setCountControlMale(800);
//                        result.setCountControlFemale(812);
//                        break;
//                    case "Cd4_Klrg1_Alpha_Beta_T_Cell_Percentage":
//                        result.setCountControlMale(804);
//                        result.setCountControlFemale(819);
//                        break;
//                    case "Cd8_Klrg1_Alpha_Beta_T_Cell_Percentage":
//                        result.setCountControlMale(807);
//                        result.setCountControlFemale(819);
//                        break;
//                    case "B_Cell_Percentage":
//                        result.setCountControlMale(830);
//                        result.setCountControlFemale(843);
//                        break;
//                    case "Igd_Mature_B_Cell_Percentage":
//                        result.setCountControlMale(829);
//                        result.setCountControlFemale(842);
//                        break;
//                    case "Monocyte_Percentage":
//                        result.setCountControlMale(826);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Ly6c_Pos_Monocyte_Percentage":
//                        result.setCountControlMale(826);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Ly6c_Neg_Monocyte_Percentage":
//                        result.setCountControlMale(826);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Neutrophil_Percentage":
//                        result.setCountControlMale(830);
//                        result.setCountControlFemale(842);
//                        break;
//                    case "Eosinophil_Percentage":
//                        result.setCountControlMale(830);
//                        result.setCountControlFemale(842);
//                        break;
//                    case "Total_T_Cell_Number":
//                        result.setCountControlMale(832);
//                        result.setCountControlFemale(839);
//                        break;
//                    case "Alpha_Beta_T_Cell_Number":
//                        result.setCountControlMale(833);
//                        result.setCountControlFemale(842);
//                        break;
//                    case "Cd4_Alpha_Beta_T_Cell_Number":
//                        result.setCountControlMale(828);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Cd8_Alpha_Beta_T_Cell_Number":
//                        result.setCountControlMale(832);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Gamma_Delta_T_Cell_Number":
//                        result.setCountControlMale(832);
//                        result.setCountControlFemale(839);
//                        break;
//                    case "NKT_Cell_Number":
//                        result.setCountControlMale(833);
//                        result.setCountControlFemale(842);
//                        break;
//                    case "Nk_Cell_Number":
//                        result.setCountControlMale(825);
//                        result.setCountControlFemale(837);
//                        break;
//                    case "Cd4_Cd25_Alpha_Beta_Reg_Number":
//                        result.setCountControlMale(825);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Cd4_Cd44_Cd62l_Alpha_Beta_Number":
//                        result.setCountControlMale(826);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Cd8_Cd44_Cd62l_Alpha_Beta_Number":
//                        result.setCountControlMale(830);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Klrg1_Mature_Nk_Cell_Number":
//                        result.setCountControlMale(800);
//                        result.setCountControlFemale(812);
//                        break;
//                    case "Cd4_Klrg1_Alpha_Beta_T_Cell_Number":
//                        result.setCountControlMale(803);
//                        result.setCountControlFemale(819);
//                        break;
//                    case "Cd8_Klrg1_Alpha_Beta_T_Cell_Number":
//                        result.setCountControlMale(807);
//                        result.setCountControlFemale(819);
//                        break;
//                    case "B_Cell_Number":
//                        result.setCountControlMale(830);
//                        result.setCountControlFemale(843);
//                        break;
//                    case "Igd_Mature_B_Cell_Number":
//                        result.setCountControlMale(829);
//                        result.setCountControlFemale(842);
//                        break;
//                    case "Monocyte_Number":
//                        result.setCountControlMale(826);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Ly6c_Pos_Monocyte_Number":
//                        result.setCountControlMale(826);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Ly6c_Neg_Monocyte_Number":
//                        result.setCountControlMale(826);
//                        result.setCountControlFemale(841);
//                        break;
//                    case "Neutrophil_Number":
//                        result.setCountControlMale(830);
//                        result.setCountControlFemale(842);
//                        break;
//                    case "Eosinophil_Number":
//                        result.setCountControlMale(830);
//                        result.setCountControlFemale(842);
//                        break;
//                    default:
//                        result.setCountControlMale(null);
//                        result.setCountControlFemale(null);
//                        break;
//                }
//                break;

            // In the case where we cannot find the procedure in this list
            // Set the control counts to null
            default:
                result.setCountControlMale(null);
                result.setCountControlFemale(null);
                break;
        }

        result.setCountMutantMale(getIntegerField(data.getSamples()));
        result.setCountMutantFemale(getIntegerField(data.getSamples()));

        // Set opposite sex to null when approrpriate
        if (data.getGender().equals("Female")) {
            result.setCountMutantMale(null);
            result.setCountControlMale(null);
        } else if (data.getGender().equals("Male")) {
            result.setCountMutantFemale(null);
            result.setCountControlFemale(null);
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
        } else if (data.getCall_Type().toLowerCase().equals("not performed or applicable")) {
            result.setStatus("Not processed");
            result.setStatisticalMethod("Manual");
            result.setCode("Data analysis not performed or not applicable");
        }


        result.setGenotypePVal(null);
        result.setGenotypeEstimate(null);

        switch (data.getCall_Type()) {
            case "Significant":

                switch (data.getGender()) {
                    case "Male":
                        result.setSexMvKOPVal(0.0);
                        result.setSexMvKOEstimate(1.0);
                        break;
                    case "Female":
                        result.setSexFvKOPVal(0.0);
                        result.setSexFvKOEstimate(1.0);
                        break;
                    default:
                        result.setGenotypePVal("0");
                        result.setGenotypeEstimate("1");
                        break;
                }
                break;
            case "Not Significant":
                result.setGenotypePVal("1");
                result.setGenotypeEstimate("0");
                break;
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

        LightweightResult result = null;

        try {
            if (data == null) {
                return null;
            }

            NameIdDTO center = nameIdDtoByOrganisationName.get(data.getCenter());
            NameIdDTO pipeline = nameIdDtoByPipelineStableId.get(data.getPipeline());

            NameIdDTO procedure = nameIdDtoByProcedureStableId.get(data.getProcedure()); // Procedure group e.g. IMPC_CAL
            NameIdDTO parameter = nameIdDtoByParameterStableId.get(data.getDependentVariable());

            // result contains a "statistical result" that has the
            // ability to produce a PreparedStatement ready for database insertion

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
                temp.setNullTestSignificance(pValue);
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


            ControlStrategy strategy = ControlStrategy.manual;

            result.setMetadataGroup(data.getMetadataGroup());
            result.setStatisticalMethod(data.getStatisticalMethod().isEmpty() ? "-" : data.getStatisticalMethod());

            result.setDataSourceId(datasourceDbIdByDatasourceShortName.get(data.getDataSourceName()));
            result.setProjectId(projectIdByProjectName.get(data.getProjectName()));

            result.setOrganisationId(center.getDbId());
            result.setOrganisationName(center.getName());

            result.setPipelineId(pipeline.getDbId());
            result.setPipelineStableId(pipeline.getStableId());

            result.setProcedureId(procedure.getDbId());
            result.setProcedureGroup(data.getProcedure());
            result.setProcedureName(procedure.getName());

            result.setParameterId(parameter.getDbId());
            result.setParameterName(parameter.getName());
            result.setParameterStableId(parameter.getStableId());
            result.setDependentVariable(parameter.getStableId());

            result.setColonyId(data.getColonyId());
            result.setStrain(data.getStrain());
            result.setZygosity(data.getZygosity());

            result.setZygosity(data.getZygosity());

            result.setControlSelectionMethod(strategy);

            //TODO: Wire the biomodels to the SR
            Long bioModelId = bioModelMap.get(data.getColonyId()).get(ZygosityType.valueOf(data.getZygosity()));
            result.setExperimentalId(bioModelId);

            result.setControlId(null);


            // Lookup from colony ID
            result.setAlleleAccessionId(colonyAlleleMap.get(data.getColonyId()));

            result.setMaleControlCount(data.getCountControlMale());
            result.setMaleMutantCount(data.getCountMutantMale());
            result.setFemaleControlCount(data.getCountControlFemale());
            result.setFemaleMutantCount(data.getCountMutantFemale());

            Set<String> sexes = new HashSet<>();
            if (data.getCountMutantMale() != null) {
                sexes.add("male");
            }
            if (data.getCountMutantFemale() != null) {
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

            result.setStatus(data.getStatus().equalsIgnoreCase("Success") ? data.getStatus() : data.getStatus() + " - " + data.getCode());

            if (data.getStatus().equalsIgnoreCase("Success")) {
                if (data.getSuggestedMpTerm().startsWith("MP:")) {
                    result.setMpAcc(data.getSuggestedMpTerm());

                } else {
                    setMpTerm(result);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot load data for 3I input: {}", data, e);
        }

        return result;
    }


    private void processFile(Resource threeIFile) {

        try {
            Map<String, Integer> counts = new HashMap<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(threeIFile.getInputStream()));
            String allData = reader.lines().map(String::trim).distinct().collect(Collectors.joining("\n"));

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
                    counts.put(result.getParameterName(), counts.getOrDefault(result.getParameterName(), 0) + 1);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            logger.info("  ADDED " + counts.get("Manual") + " statistical results for Method: Manual");
            counts.remove("Manual");

            for (String parameterName : counts.keySet()) {
                logger.info("     " + counts.get(parameterName) + " for Parameter: " + parameterName);
            }

            if ( ! missingProcedures.isEmpty()) {
                logger.warn("Cound not find data for procedures: {}", StringUtils.join(missingColonies, ", "));
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
        this.populateBioModelMap();
        populateControlBioModelMap();
        populateParameterTypeMap();

        // Update the parameter type map if any of the 3I parameters are missing data in the database
        parameterTypeMap.putIfAbsent("MGP_MLN_111_001", ObservationType.unidimensional);
        parameterTypeMap.putIfAbsent("MGP_MLN_026_001", ObservationType.unidimensional);


        // process the 3I file provided by Ania@3I consortium 2018-01-19
        processFile(threeIFile);

        if (missingColonies.size() > 0) {
            logger.warn("Colonies in the ThreeI file but missing in the database {}", StringUtils.join(missingColonies, ", "));
        }

    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ThreeIStatisticalResultLoader.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }

    protected void populateBioModelMap() throws SQLException {
        Map<String, Map<ZygosityType, Long>> map = bioModelMap;

        String query = "SELECT DISTINCT colony_name AS colony_id, bm.zygosity, bm.id as biological_model_id, strain.name " +
                "FROM phenotyped_colony pc " +
                "INNER JOIN strain ON strain.name=pc.background_strain_name " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.strain_acc=strain.acc " +
                "INNER JOIN allele ON allele.symbol=pc.allele_symbol " +
                "INNER JOIN biological_model_allele bma ON bma.allele_acc=allele.acc " +
                "INNER JOIN biological_model_genomic_feature bmgf ON bmgf.gf_acc=pc.gf_acc " +
                "INNER JOIN biological_model bm ON bm.id=bmgf.biological_model_id AND bm.id=bma.biological_model_id AND bm.id=bmstrain.biological_model_id  " +
                " " ;

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {

                String colonyId = r.getString("colony_id");
                ZygosityType zyg = ZygosityType.valueOf(r.getString("zygosity"));
                Long modelId = r.getLong("biological_model_id");
                String strain = r.getString("name");

                strainNameByBiologicalModelId.put(modelId, strain);

                map.putIfAbsent(colonyId, new HashMap<>());
                map.get(colonyId).put(zyg, modelId);
            }
        }

        logger.info(" Mapped {} biological model entries", map.size());
    }
}