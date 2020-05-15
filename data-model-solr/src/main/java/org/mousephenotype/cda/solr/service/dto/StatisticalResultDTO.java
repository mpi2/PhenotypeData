/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;
import org.mousephenotype.cda.enumerations.SignificantType;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@SolrDocument(solrCoreName = "statistical-result")
public class StatisticalResultDTO {



    public final static String DOCUMENT_ID = "doc_id";
    public final static String DB_ID = "db_id";
    public final static String DATA_TYPE = "data_type";

	public final static String MP_TERM_ID_OPTIONS = "mp_term_id_options";
	public final static String MP_TERM_NAME_OPTIONS = "mp_term_name_options";
    public final static String MP_TERM_ID = "mp_term_id";
    public final static String MP_TERM_NAME = "mp_term_name";
    public final static String TOP_LEVEL_MP_TERM_ID = "top_level_mp_term_id";
    public final static String TOP_LEVEL_MP_TERM_NAME = "top_level_mp_term_name";
    public final static String INTERMEDIATE_MP_TERM_ID = "intermediate_mp_term_id";
    public final static String INTERMEDIATE_MP_TERM_NAME = "intermediate_mp_term_name";

    public final static String MALE_MP_TERM_ID = "male_mp_term_id";
    public final static String MALE_MP_TERM_NAME = "male_mp_term_name";
    public final static String MALE_TOP_LEVEL_MP_TERM_ID = "male_top_level_mp_term_id";
    public final static String MALE_TOP_LEVEL_MP_TERM_NAME = "male_top_level_mp_term_name";
    public final static String MALE_INTERMEDIATE_MP_TERM_ID = "male_intermediate_mp_term_id";
    public final static String MALE_INTERMEDIATE_MP_TERM_NAME = "male_intermediate_mp_term_name";

    public final static String FEMALE_MP_TERM_ID = "female_mp_term_id";
    public final static String FEMALE_MP_TERM_NAME = "female_mp_term_name";
    public final static String FEMALE_TOP_LEVEL_MP_TERM_ID = "female_top_level_mp_term_id";
    public final static String FEMALE_TOP_LEVEL_MP_TERM_NAME = "female_top_level_mp_term_name";
    public final static String FEMALE_INTERMEDIATE_MP_TERM_ID = "female_intermediate_mp_term_id";
    public final static String FEMALE_INTERMEDIATE_MP_TERM_NAME = "female_intermediate_mp_term_name";

    public final static String RESOURCE_NAME = "resource_name";
    public final static String RESOURCE_FULLNAME = "resource_fullname";
    public final static String RESOURCE_ID = "resource_id";
    public final static String PROJECT_NAME = "project_name";
    public final static String PHENOTYPING_CENTER = "phenotyping_center";

    public final static String PIPELINE_STABLE_ID = "pipeline_stable_id";
    public final static String PIPELINE_STABLE_KEY = "pipeline_stable_key";
    public final static String PIPELINE_NAME = "pipeline_name";
    public final static String PIPELINE_ID = "pipeline_id";

    public final static String PROCEDURE_STABLE_ID = "procedure_stable_id";
    public final static String PROCEDURE_STABLE_KEY = "procedure_stable_key";
    public final static String PROCEDURE_NAME = "procedure_name";
    public final static String PROCEDURE_ID = "procedure_id";

    public final static String PARAMETER_STABLE_ID = "parameter_stable_id";
    public final static String PARAMETER_STABLE_KEY = "parameter_stable_key";
    public final static String PARAMETER_NAME = "parameter_name";
    public final static String PARAMETER_ID = "parameter_id";

    public final static String COLONY_ID = "colony_id";
    public final static String MARKER_SYMBOL = "marker_symbol";
    public final static String MARKER_ACCESSION_ID = "marker_accession_id";
    public final static String ALLELE_SYMBOL = "allele_symbol";
    public final static String ALLELE_NAME = "allele_name";
    public final static String ALLELE_ACCESSION_ID = "allele_accession_id";
    public final static String STRAIN_NAME = "strain_name";
    public final static String STRAIN_ACCESSION_ID = "strain_accession_id";
    public static final String GENETIC_BACKGROUND = "genetic_background";
    public final static String SEX = "sex";
    public final static String ZYGOSITY = "zygosity";
    public final static String PRODUCTION_CENTER = "production_center";

    public final static String CONTROL_SELECTION_METHOD = "control_selection_method";
    public final static String DEPENDENT_VARIABLE = "dependent_variable";
    public final static String METADATA_GROUP = "metadata_group";

    public final static String CONTROL_BIOLOGICAL_MODEL_ID = "control_biological_model_id";
    public final static String MUTANT_BIOLOGICAL_MODEL_ID = "mutant_biological_model_id";
    public final static String MALE_CONTROL_COUNT = "male_control_count";
    public final static String MALE_MUTANT_COUNT = "male_mutant_count";
    public final static String FEMALE_CONTROL_COUNT = "female_control_count";
    public final static String FEMALE_MUTANT_COUNT = "female_mutant_count";

    public final static String MALE_CONTROL_MEAN = "male_control_mean";
    public final static String MALE_MUTANT_MEAN = "male_mutant_mean";
    public final static String FEMALE_CONTROL_MEAN = "female_control_mean";
    public final static String FEMALE_MUTANT_MEAN = "female_mutant_mean";

	public final static String WORKFLOW = "workflow";
    public final static String STATISTICAL_METHOD = "statistical_method";
    public final static String STATUS = "status";
    public final static String ADDITIONAL_INFORMATION = "additional_information";
    public final static String RAW_OUTPUT = "raw_output";
    public final static String P_VALUE = "p_value";
    public final static String EFFECT_SIZE = "effect_size";

	public final static String GENOTYPE_PVALUE_LOW_VS_NORMAL_HIGH = "genotype_pvalue_low_vs_normal_high";
	public final static String GENOTYPE_PVALUE_LOW_NORMAL_VS_HIGH = "genotype_pvalue_low_normal_vs_high";
	public final static String GENOTYPE_EFFECT_SIZE_LOW_VS_NORMAL_HIGH = "genotype_effect_size_low_vs_normal_high";
	public final static String GENOTYPE_EFFECT_SIZE_LOW_NORMAL_VS_HIGH = "genotype_effect_size_low_normal_vs_high";
	public final static String FEMALE_PVALUE_LOW_VS_NORMAL_HIGH = "female_pvalue_low_vs_normal_high";
	public final static String FEMALE_PVALUE_LOW_NORMAL_VS_HIGH = "female_pvalue_low_normal_vs_high";
	public final static String FEMALE_EFFECT_SIZE_LOW_VS_NORMAL_HIGH = "female_effect_size_low_vs_normal_high";
	public final static String FEMALE_EFFECT_SIZE_LOW_NORMAL_VS_HIGH = "female_effect_size_low_normal_vs_high";
	public final static String MALE_PVALUE_LOW_VS_NORMAL_HIGH = "male_pvalue_low_vs_normal_high";
	public final static String MALE_PVALUE_LOW_NORMAL_VS_HIGH = "male_pvalue_low_normal_vs_high";
	public final static String MALE_EFFECT_SIZE_LOW_VS_NORMAL_HIGH = "male_effect_size_low_vs_normal_high";
	public final static String MALE_EFFECT_SIZE_LOW_NORMAL_VS_HIGH = "male_effect_size_low_normal_vs_high";

    public static final String LIFE_STAGE_ACC = "life_stage_acc";
    public static final String LIFE_STAGE_NAME = "life_stage_name";

	public final static String CATEGORIES = "categories";
    public final static String CATEGORICAL_P_VALUE = "categorical_p_value";
    public final static String CATEGORICAL_EFFECT_SIZE = "categorical_effect_size";

    public final static String BATCH_SIGNIFICANT = "batch_significant";
    public final static String VARIANCE_SIGNIFICANT = "variance_significant";
    public final static String NULL_TEST_P_VALUE = "null_test_p_value";
    public final static String GENOTYPE_EFFECT_P_VALUE = "genotype_effect_p_value";
    public final static String GENOTYPE_EFFECT_STDERR_ESTIMATE = "genotype_effect_stderr_estimate";
    public final static String GENOTYPE_EFFECT_PARAMETER_ESTIMATE = "genotype_effect_parameter_estimate";

    public final static String FEMALE_PERCENTAGE_CHANGE = "female_percentage_change";
    public final static String MALE_PERCENTAGE_CHANGE = "male_percentage_change";

    public final static String SEX_EFFECT_P_VALUE = "sex_effect_p_value";
    public final static String SEX_EFFECT_STDERR_ESTIMATE = "sex_effect_stderr_estimate";
    public final static String SEX_EFFECT_PARAMETER_ESTIMATE = "sex_effect_parameter_estimate";
    public final static String WEIGHT_EFFECT_P_VALUE = "weight_effect_p_value";
    public final static String WEIGHT_EFFECT_STDERR_ESTIMATE = "weight_effect_stderr_estimate";
    public final static String WEIGHT_EFFECT_PARAMETER_ESTIMATE = "weight_effect_parameter_estimate";

    public final static String GROUP_1_GENOTYPE = "group_1_genotype";
    public final static String GROUP_1_RESIDUALS_NORMALITY_TEST = "group_1_residuals_normality_test";
    public final static String GROUP_2_GENOTYPE = "group_2_genotype";
    public final static String GROUP_2_RESIDUALS_NORMALITY_TEST = "group_2_residuals_normality_test";
    public final static String BLUPS_TEST = "blups_test";
    public final static String ROTATED_RESIDUALS_TEST = "rotated_residuals_test";

    public final static String INTERCEPT_ESTIMATE = "intercept_estimate";
    public final static String INTERCEPT_ESTIMATE_STDERR_ESTIMATE = "intercept_estimate_stderr_estimate";
    public final static String INTERACTION_SIGNIFICANT = "interaction_significant";
    public final static String INTERACTION_EFFECT_P_VALUE = "interaction_effect_p_value";
    public final static String FEMALE_KO_EFFECT_P_VALUE = "female_ko_effect_p_value";
    public final static String FEMALE_KO_EFFECT_STDERR_ESTIMATE = "female_ko_effect_stderr_estimate";
    public final static String FEMALE_KO_PARAMETER_ESTIMATE = "female_ko_parameter_estimate";
    public final static String MALE_KO_EFFECT_P_VALUE = "male_ko_effect_p_value";
    public final static String MALE_KO_EFFECT_STDERR_ESTIMATE = "male_ko_effect_stderr_estimate";
    public final static String MALE_KO_PARAMETER_ESTIMATE = "male_ko_parameter_estimate";
    public final static String CLASSIFICATION_TAG = "classification_tag";

    public final static String EXTERNAL_DB_ID = "external_db_id";
    public final static String ORGANISATION_ID = "organisation_id";
    public final static String PHENOTYPING_CENTER_ID = "phenotyping_center_id";
    public final static String PROJECT_ID = "project_id";
    public final static String PHENOTYPE_SEX = "phenotype_sex";

    // Mapped anatomy terms from MP.
    public static final String ANATOMY_TERM_NAME = "anatomy_term_name";
    public static final String ANATOMY_TERM_ID = "anatomy_term_id";
    public static final String INTERMEDIATE_ANATOMY_TERM_ID = "intermediate_anatomy_term_id";
    public static final String INTERMEDIATE_ANATOMY_TERM_NAME = "intermediate_anatomy_term_name";
    public static final String TOP_LEVEL_ANATOMY_TERM_ID = "top_level_anatomy_term_id";
    public static final String TOP_LEVEL_ANATOMY_TERM_NAME = "top_level_anatomy_term_name";

	public static final String SIGNIFICANT = "significant";

	public SignificantType getSignificantType() {
        return SignificantType.valueOf(getClassificationTag());
    }

	@Id
    @Field(DOCUMENT_ID)
    private String docId;

    @Field(DB_ID)
    private Long dbId;

    @Field(ANATOMY_TERM_ID)
    List<String> anatomyTermId;

    @Field(ANATOMY_TERM_NAME)
    List<String> anatomyTermName;

    @Field(INTERMEDIATE_ANATOMY_TERM_ID)
    List<String> intermediateAnatomyTermId;

    @Field(INTERMEDIATE_ANATOMY_TERM_NAME)
    List<String> intermediateAnatomyTermName;

    @Field(TOP_LEVEL_ANATOMY_TERM_ID)
    List<String> topLevelAnatomyTermId;

    @Field(TOP_LEVEL_ANATOMY_TERM_NAME)
    List<String> topLevelAnatomyTermName;

    @Field(PHENOTYPE_SEX)
    private List<String> phenotypeSex;

    @Field(DATA_TYPE)
    private String dataType;

	@Field(MP_TERM_ID_OPTIONS)
	private List<String> mpTermIdOptions;

	@Field(MP_TERM_NAME_OPTIONS)
	private List<String> mpTermNameOptions;

	@Field(MP_TERM_ID)
	private String mpTermId;

	@Field(MP_TERM_NAME)
	private String mpTermName;

    @Field(TOP_LEVEL_MP_TERM_ID)
    private List<String> topLevelMpTermId;

    @Field(TOP_LEVEL_MP_TERM_NAME)
    private List<String> topLevelMpTermName;

    @Field(INTERMEDIATE_MP_TERM_ID)
    private List<String> intermediateMpTermId;

    @Field(INTERMEDIATE_MP_TERM_NAME)
    private List<String> intermediateMpTermName;

    @Field(MALE_MP_TERM_ID)
    private String maleMpTermId;

    @Field(MALE_MP_TERM_NAME)
    private String maleMpTermName;

    @Field(MALE_TOP_LEVEL_MP_TERM_ID)
    private List<String> maleTopLevelMpTermId;

    @Field(MALE_TOP_LEVEL_MP_TERM_NAME)
    private List<String> maleTopLevelMpTermName;

    @Field(MALE_INTERMEDIATE_MP_TERM_ID)
    private List<String> maleIntermediateMpTermId;

    @Field(MALE_INTERMEDIATE_MP_TERM_NAME)
    private List<String> maleIntermediateMpTermName;


    @Field(FEMALE_MP_TERM_ID)
    private String femaleMpTermId;

    @Field(FEMALE_MP_TERM_NAME)
    private String femaleMpTermName;

    @Field(FEMALE_TOP_LEVEL_MP_TERM_ID)
    private List<String> femaleTopLevelMpTermId;

    @Field(FEMALE_TOP_LEVEL_MP_TERM_NAME)
    private List<String> femaleTopLevelMpTermName;

    @Field(FEMALE_INTERMEDIATE_MP_TERM_ID)
    private List<String> femaleIntermediateMpTermId;

    @Field(FEMALE_INTERMEDIATE_MP_TERM_NAME)
    private List<String> femaleIntermediateMpTermName;


    @Field(LIFE_STAGE_ACC)
    String lifeStageAcc;

    @Field(LIFE_STAGE_NAME)
    String lifeStageName;


    @Field(RESOURCE_NAME)
    private String resourceName;

    @Field(RESOURCE_FULLNAME)
    private String resourceFullname;

    @Field(RESOURCE_ID)
    private Long resourceId;

    @Field(PROJECT_NAME)
    private String projectName;

    @Field(PHENOTYPING_CENTER)
    private String phenotypingCenter;

    @Field(PIPELINE_STABLE_ID)
    private String pipelineStableId;

    @Field(PIPELINE_STABLE_KEY)
    private Long pipelineStableKey;

    @Field(PIPELINE_NAME)
    private String pipelineName;

    @Field(PIPELINE_ID)
    private Long pipelineId;

    @Field(PROCEDURE_STABLE_ID)
    private String procedureStableId;

    @Field(PROCEDURE_STABLE_KEY)
    private Long procedureStableKey;

    @Field(PROCEDURE_NAME)
    private String procedureName;

    @Field(PROCEDURE_ID)
    private Long procedureId;

    @Field(PARAMETER_STABLE_ID)
    private String parameterStableId;

    @Field(PARAMETER_STABLE_KEY)
    private Long parameterStableKey;

    @Field(PARAMETER_NAME)
    private String parameterName;

    @Field(PARAMETER_ID)
    private Long parameterId;

    @Field(COLONY_ID)
    private String colonyId;

    @Field(MARKER_SYMBOL)
    private String markerSymbol;

    @Field(MARKER_ACCESSION_ID)
    private String markerAccessionId;

    @Field(ALLELE_SYMBOL)
    private String alleleSymbol;

    @Field(ALLELE_NAME)
    private String alleleName;

    @Field(ALLELE_ACCESSION_ID)
    private String alleleAccessionId;

    @Field(STRAIN_NAME)
    private String strainName;

    @Field(STRAIN_ACCESSION_ID)
    private String strainAccessionId;

    @Field(GENETIC_BACKGROUND)
    String geneticBackground;

    @Field(SEX)
    private String sex;

    @Field(ZYGOSITY)
    private String zygosity;

    @Field(CONTROL_SELECTION_METHOD)
    private String controlSelectionMethod;

    @Field(DEPENDENT_VARIABLE)
    private String dependentVariable;

    @Field(METADATA_GROUP)
    private String metadataGroup;

    @Field(CONTROL_BIOLOGICAL_MODEL_ID)
    private Long controlBiologicalModelId;

    @Field(MUTANT_BIOLOGICAL_MODEL_ID)
    private Long mutantBiologicalModelId;

    @Field(MALE_CONTROL_COUNT)
    private Integer maleControlCount;

    @Field(MALE_MUTANT_COUNT)
    private Integer maleMutantCount;

    @Field(FEMALE_CONTROL_COUNT)
    private Integer femaleControlCount;

    @Field(FEMALE_MUTANT_COUNT)
    private Integer femaleMutantCount;

    @Field(FEMALE_MUTANT_MEAN)
    private Double femaleMutantMean;

    @Field(FEMALE_CONTROL_MEAN)
    private Double femaleControlMean;

    @Field(MALE_MUTANT_MEAN)
    private Double maleMutantMean;

    @Field(MALE_CONTROL_MEAN)
    private Double maleControlMean;

	@Field(WORKFLOW)
	private String workflow;

	@Field(STATISTICAL_METHOD)
    private String statisticalMethod;

    @Field(STATUS)
    private String status;

    @Field(ADDITIONAL_INFORMATION)
    private String additionalInformation;

    @Field(RAW_OUTPUT)
    private String rawOutput;

    @Field(P_VALUE)
    private Double pValue;

    @Field(EFFECT_SIZE)
    private Double effectSize;

	@Field(GENOTYPE_PVALUE_LOW_VS_NORMAL_HIGH)
	private Double genotypePvalueLowVsNormalHigh;

	@Field(GENOTYPE_PVALUE_LOW_NORMAL_VS_HIGH)
	private Double genotypePvalueLowNormalVsHigh;

	@Field(GENOTYPE_EFFECT_SIZE_LOW_VS_NORMAL_HIGH)
	private Double genotypeEffectSizeLowVsNormalHigh;

	@Field(GENOTYPE_EFFECT_SIZE_LOW_NORMAL_VS_HIGH)
	private Double genotypeEffectSizeLowNormalVsHigh;

	@Field(FEMALE_PVALUE_LOW_VS_NORMAL_HIGH)
	private Double femalePvalueLowVsNormalHigh;

	@Field(FEMALE_PVALUE_LOW_NORMAL_VS_HIGH)
	private Double femalePvalueLowNormalVsHigh;

	@Field(FEMALE_EFFECT_SIZE_LOW_VS_NORMAL_HIGH)
	private Double femaleEffectSizeLowVsNormalHigh;

	@Field(FEMALE_EFFECT_SIZE_LOW_NORMAL_VS_HIGH)
	private Double femaleEffectSizeLowNormalVsHigh;

	@Field(MALE_PVALUE_LOW_VS_NORMAL_HIGH)
	private Double malePvalueLowVsNormalHigh;

	@Field(MALE_PVALUE_LOW_NORMAL_VS_HIGH)
	private Double malePvalueLowNormalVsHigh;

	@Field(MALE_EFFECT_SIZE_LOW_VS_NORMAL_HIGH)
	private Double maleEffectSizeLowVsNormalHigh;

	@Field(MALE_EFFECT_SIZE_LOW_NORMAL_VS_HIGH)
	private Double maleEffectSizeLowNormalVsHigh;

	@Field(CATEGORIES)
    private List<String> categories;

    @Field(CATEGORICAL_P_VALUE)
    private Double categoricalPValue;

    @Field(CATEGORICAL_EFFECT_SIZE)
    private Double categoricalEffectSize;

    @Field(BATCH_SIGNIFICANT)
    private Boolean batchSignificant;

    @Field(VARIANCE_SIGNIFICANT)
    private Boolean varianceSignificant;

    @Field(NULL_TEST_P_VALUE)
    private Double nullTestPValue;

    @Field(GENOTYPE_EFFECT_P_VALUE)
    private Double genotypeEffectPValue;

    @Field(GENOTYPE_EFFECT_STDERR_ESTIMATE)
    private Double genotypeEffectStderrEstimate;

    @Field(GENOTYPE_EFFECT_PARAMETER_ESTIMATE)
    private Double genotypeEffectParameterEstimate;

    @Field(FEMALE_PERCENTAGE_CHANGE)
    private String femalePercentageChange;

    @Field(MALE_PERCENTAGE_CHANGE)
    private String malePercentageChange;

    @Field(SEX_EFFECT_P_VALUE)
    private Double sexEffectPValue;

    @Field(SEX_EFFECT_STDERR_ESTIMATE)
    private Double sexEffectStderrEstimate;

    @Field(SEX_EFFECT_PARAMETER_ESTIMATE)
    private Double sexEffectParameterEstimate;

    @Field(WEIGHT_EFFECT_P_VALUE)
    private Double weightEffectPValue;

    @Field(WEIGHT_EFFECT_STDERR_ESTIMATE)
    private Double weightEffectStderrEstimate;

    @Field(WEIGHT_EFFECT_PARAMETER_ESTIMATE)
    private Double weightEffectParameterEstimate;

    @Field(GROUP_1_GENOTYPE)
    private String group1Genotype;

    @Field(GROUP_1_RESIDUALS_NORMALITY_TEST)
    private Double group1ResidualsNormalityTest;

    @Field(GROUP_2_GENOTYPE)
    private String group2Genotype;

    @Field(GROUP_2_RESIDUALS_NORMALITY_TEST)
    private Double group2ResidualsNormalityTest;

    @Field(BLUPS_TEST)
    private Double blupsTest;

    @Field(ROTATED_RESIDUALS_TEST)
    private Double rotatedResidualsTest;

    @Field(INTERCEPT_ESTIMATE)
    private Double interceptEstimate;

    @Field(INTERCEPT_ESTIMATE_STDERR_ESTIMATE)
    private Double interceptEstimateStderrEstimate;

    @Field(INTERACTION_SIGNIFICANT)
    private Boolean interactionSignificant;

    @Field(INTERACTION_EFFECT_P_VALUE)
    private Double interactionEffectPValue;

    @Field(FEMALE_KO_EFFECT_P_VALUE)
    private Double femaleKoEffectPValue;

    @Field(FEMALE_KO_EFFECT_STDERR_ESTIMATE)
    private Double femaleKoEffectStderrEstimate;

    @Field(FEMALE_KO_PARAMETER_ESTIMATE)
    private Double femaleKoParameterEstimate;

    @Field(MALE_KO_EFFECT_P_VALUE)
    private Double maleKoEffectPValue;

    @Field(MALE_KO_EFFECT_STDERR_ESTIMATE)
    private Double maleKoEffectStderrEstimate;

    @Field(MALE_KO_PARAMETER_ESTIMATE)
    private Double maleKoParameterEstimate;

    @Field(CLASSIFICATION_TAG)
    private String classificationTag;

    @Field(EXTERNAL_DB_ID)
    private Long externalDbId;

    @Field(ORGANISATION_ID)
    private Long organisationId;

    @Field(PHENOTYPING_CENTER_ID)
    private Long phenotypingCenterId;

    @Field(PROJECT_ID)
    private Long projectId;
    
    @Field(PRODUCTION_CENTER)
    private String productionCenter;

	@Field
	private Boolean significant;


    public String getDocId() {

        return docId;
    }

	public void setDocId(String docId) {

        this.docId = docId;
    }


    public Long getDbId() {

        return dbId;
    }


    public void setDbId(Long dbId) {

        this.dbId = dbId;
    }


    public String getDataType() {

        return dataType;
    }


    public void setDataType(String dataType) {

        this.dataType = dataType;
    }

	public List<String> getMpTermIdOptions() {
		return mpTermIdOptions;
	}

	public void setMpTermIdOptions(List<String> mpTermIdOptions) {
		this.mpTermIdOptions = mpTermIdOptions;
	}

    public void addMpTermIdOptions(Collection<String> mpTermIdOptions) {
        if (mpTermIdOptions != null) {
            if (this.mpTermIdOptions == null) {
                this.mpTermIdOptions = new ArrayList<>();
            }
            this.mpTermIdOptions.addAll(mpTermIdOptions);
        }
    }

    public void addMpTermIdOptions(String mpTermIdOption) {
        if (mpTermIdOption != null) {
            if (this.mpTermIdOptions == null) {
                this.mpTermIdOptions = new ArrayList<>();
            }
            this.mpTermIdOptions.add(mpTermIdOption);
        }
    }

	public List<String> getMpTermNameOptions() {
		return mpTermNameOptions;
	}

	public void setMpTermNameOptions(List<String> mpTermNameOptions) {
		this.mpTermNameOptions = mpTermNameOptions;
	}

    public void addMpTermNameOptions(Collection<String> mpTermNameOptions) {
        if (mpTermNameOptions != null) {
            if (this.mpTermNameOptions == null) {
                this.mpTermNameOptions = new ArrayList<>();
            }
            this.mpTermNameOptions.addAll(mpTermNameOptions);
        }
    }

    public void addMpTermNameOptions(String mpTermNameOptions) {
        if (mpTermNameOptions != null) {
            if (this.mpTermNameOptions == null) {
                this.mpTermNameOptions = new ArrayList<>();
            }
            this.mpTermNameOptions.add(mpTermNameOptions);
        }
    }

	public String getMpTermId() {

        return mpTermId;
    }


    public void setMpTermId(String mpTermId) {

        this.mpTermId = mpTermId;
    }


    public String getMpTermName() {

        return mpTermName;
    }


    public void setMpTermName(String mpTermName) {

        this.mpTermName = mpTermName;
    }


    public List<String> getTopLevelMpTermId() {

        return topLevelMpTermId;
    }


    public void setTopLevelMpTermId(List<String> topLevelMpTermId) {

        this.topLevelMpTermId = topLevelMpTermId;
    }

    public void addTopLevelMpTermId(Collection<String> topLevelMpTermId) {
        if (topLevelMpTermId!= null){
            if (this.topLevelMpTermId == null) {
                this.topLevelMpTermId = new ArrayList<>();
            }
            this.topLevelMpTermId.addAll(topLevelMpTermId);
        }
    }

    public List<String> getTopLevelMpTermName() {

        return topLevelMpTermName;
    }

    public void setTopLevelMpTermName(List<String> topLevelMpTermName) {

        this.topLevelMpTermName = topLevelMpTermName;
    }

    public void addTopLevelMpTermName(Collection<String> topLevelMpTermName) {
        if (topLevelMpTermName != null) {
            if (this.topLevelMpTermName == null) {
                this.topLevelMpTermName = new ArrayList<>();
            }
            this.topLevelMpTermName.addAll(topLevelMpTermName);
        }
    }


    public String getLifeStageAcc() {

        return lifeStageAcc;
    }

    public void setLifeStageAcc(String life_stage_acc) {

        this.lifeStageAcc = life_stage_acc;
    }

    public String setLifeStageName() {

        return lifeStageName;
    }

    public void setLifeStageName(String life_stage_name) {

        this.lifeStageName = life_stage_name;
    }

    public List<String> getIntermediateMpTermId() {

        return intermediateMpTermId;
    }


    public void setIntermediateMpTermId(List<String> intermediateMpTermId) {

        this.intermediateMpTermId = intermediateMpTermId;
    }

    public void addIntermediateMpTermId(Collection<String> intermediateMpTermId) {
        if ( intermediateMpTermId != null) {
            if (this.intermediateMpTermId == null) {
                this.intermediateMpTermId = new ArrayList<>();
            }
            this.intermediateMpTermId.addAll(intermediateMpTermId);
        }
    }

    public List<String> getIntermediateMpTermName() {

        return intermediateMpTermName;
    }


    public void setIntermediateMpTermName(List<String> intermediateMpTermName) {

        this.intermediateMpTermName = intermediateMpTermName;
    }

    public void addIntermediateMpTermName(Collection<String> intermediateMpTermName) {
        if ( intermediateMpTermName != null) {
            if (this.intermediateMpTermName == null) {
                this.intermediateMpTermName = new ArrayList<>();
            }
            this.intermediateMpTermName.addAll(intermediateMpTermName);
        }
    }

    public String getMaleMpTermId() {

        return maleMpTermId;
    }


    public void setMaleMpTermId(String maleMpTermId) {

        this.maleMpTermId = maleMpTermId;
    }


    public String getMaleMpTermName() {

        return maleMpTermName;
    }


    public void setMaleMpTermName(String maleMpTermName) {

        this.maleMpTermName = maleMpTermName;
    }


    public List<String> getMaleTopLevelMpTermId() {

        return maleTopLevelMpTermId;
    }


    public void setMaleTopLevelMpTermId(List<String> maleTopLevelMpTermId) {

        this.maleTopLevelMpTermId = maleTopLevelMpTermId;
    }

    public void addMaleTopLevelMpTermId(Collection<String> maleTopLevelMpTermId) {
        if (maleTopLevelMpTermId != null) {
            if (this.maleTopLevelMpTermId == null){
                this.maleTopLevelMpTermId = new ArrayList<>();
            }
            this.maleTopLevelMpTermId.addAll(maleTopLevelMpTermId);
        }
    }


    public List<String> getMaleTopLevelMpTermName() {

        return maleTopLevelMpTermName;
    }


    public void setMaleTopLevelMpTermName(List<String> maleTopLevelMpTermName) {

        this.maleTopLevelMpTermName = maleTopLevelMpTermName;
    }

    public void addMaleTopLevelMpTermName(Collection<String> maleTopLevelMpTermName) {
        if (maleTopLevelMpTermName != null) {
            if (this.maleTopLevelMpTermName == null){
                this.maleTopLevelMpTermName = new ArrayList<>();
            }
            this.maleTopLevelMpTermName.addAll(maleTopLevelMpTermName);
        }
    }



    public List<String> getMaleIntermediateMpTermId() {

        return maleIntermediateMpTermId;
    }


    public void setMaleIntermediateMpTermId(List<String> maleIntermediateMpTermId) {

        this.maleIntermediateMpTermId = maleIntermediateMpTermId;
    }

    public void addMaleIntermediateMpTermId(Collection<String> maleIntermediateMpTermId) {
        if (maleIntermediateMpTermId != null) {
            if (this.maleIntermediateMpTermId == null){
                this.maleIntermediateMpTermId = new ArrayList<>();
            }
            this.maleIntermediateMpTermId.addAll(maleIntermediateMpTermId);
        }
    }

    public List<String> getMaleIntermediateMpTermName() {

        return maleIntermediateMpTermName;
    }


    public void setMaleIntermediateMpTermName(List<String> maleIntermediateMpTermName) {

        this.maleIntermediateMpTermName = maleIntermediateMpTermName;
    }

    public void addMaleIntermediateMpTermName(Collection<String> maleIntermediateMpTermName) {

        if (maleIntermediateMpTermName != null) {
            if (this.maleIntermediateMpTermName == null){
                this.maleIntermediateMpTermName = new ArrayList<>();
            }
            this.maleIntermediateMpTermName.addAll(maleIntermediateMpTermName);
        }
    }

    public String getFemaleMpTermId() {

        return femaleMpTermId;
    }


    public void setFemaleMpTermId(String femaleMpTermId) {

        this.femaleMpTermId = femaleMpTermId;
    }


    public String getFemaleMpTermName() {

        return femaleMpTermName;
    }


    public void setFemaleMpTermName(String femaleMpTermName) {

        this.femaleMpTermName = femaleMpTermName;
    }


    public List<String> getFemaleTopLevelMpTermId() {

        return femaleTopLevelMpTermId;
    }


    public void addFemaleTopLevelMpTermId(Collection<String> femaleTopLevelMpTermId) {

        if (femaleTopLevelMpTermId != null) {
            if (this.femaleTopLevelMpTermId == null) {
                this.femaleTopLevelMpTermId = new ArrayList<>();
            }
            this.femaleTopLevelMpTermId.addAll(femaleTopLevelMpTermId);
        }
    }


    public List<String> getFemaleTopLevelMpTermName() {

        return femaleTopLevelMpTermName;
    }


    public void setFemaleTopLevelMpTermName(List<String> femaleTopLevelMpTermName) {

        this.femaleTopLevelMpTermName = femaleTopLevelMpTermName;
    }

    public void addFemaleTopLevelMpTermName(Collection<String> femaleTopLevelMpTermName) {

        if (femaleTopLevelMpTermName != null) {
            if (this.femaleTopLevelMpTermName == null) {
                this.femaleTopLevelMpTermName = new ArrayList<>();
            }
            this.femaleTopLevelMpTermName.addAll(femaleTopLevelMpTermName);
        }
    }

    public List<String> getFemaleIntermediateMpTermId() {

        return femaleIntermediateMpTermId;
    }


    public void setFemaleIntermediateMpTermId(List<String> femaleIntermediateMpTermId) {

        this.femaleIntermediateMpTermId = femaleIntermediateMpTermId;
    }

    public void addFemaleIntermediateMpTermId(Collection<String> femaleIntermediateMpTermId) {

        if (femaleIntermediateMpTermId != null) {
            if (this.femaleIntermediateMpTermId == null){
                this.femaleIntermediateMpTermId = new ArrayList<>();
            }
            this.femaleIntermediateMpTermId.addAll(femaleIntermediateMpTermId);
        }
    }

    public String getLifeStageName() {
        return lifeStageName;
    }

    public List<String> getFemaleIntermediateMpTermName() {

        return femaleIntermediateMpTermName;
    }


    public void setFemaleIntermediateMpTermName(List<String> femaleIntermediateMpTermName) {

        this.femaleIntermediateMpTermName = femaleIntermediateMpTermName;
    }

    public void addFemaleIntermediateMpTermName(Collection<String> femaleIntermediateMpTermName) {

        if (femaleIntermediateMpTermName != null) {
            if (this.femaleIntermediateMpTermName == null){
                this.femaleIntermediateMpTermName = new ArrayList<>();
            }
            this.femaleIntermediateMpTermName.addAll(femaleIntermediateMpTermName);
        }
    }


    public String getResourceName() {

        return resourceName;
    }


    public void setResourceName(String resourceName) {

        this.resourceName = resourceName;
    }


    public String getResourceFullname() {

        return resourceFullname;
    }


    public void setResourceFullname(String resourceFullname) {

        this.resourceFullname = resourceFullname;
    }


    public Long getResourceId() {

        return resourceId;
    }


    public void setResourceId(Long resourceId) {

        this.resourceId = resourceId;
    }


    public String getProjectName() {

        return projectName;
    }


    public void setProjectName(String projectName) {

        this.projectName = projectName;
    }


    public String getPhenotypingCenter() {

        return phenotypingCenter;
    }


    public void setPhenotypingCenter(String phenotypingCenter) {

        this.phenotypingCenter = phenotypingCenter;
    }


    public String getPipelineStableId() {

        return pipelineStableId;
    }


    public void setPipelineStableId(String pipelineStableId) {

        this.pipelineStableId = pipelineStableId;
    }


    public Long getPipelineStableKey() {

        return pipelineStableKey;
    }


    public void setPipelineStableKey(Long pipelineStableKey) {

        this.pipelineStableKey = pipelineStableKey;
    }


    public String getPipelineName() {

        return pipelineName;
    }


    public void setPipelineName(String pipelineName) {

        this.pipelineName = pipelineName;
    }


    public Long getPipelineId() {

        return pipelineId;
    }


    public void setPipelineId(Long pipelineId) {

        this.pipelineId = pipelineId;
    }


    public String getProcedureStableId() {

        return procedureStableId;
    }


    public void setProcedureStableId(String procedureStableId) {

        this.procedureStableId = procedureStableId;
    }


    public Long getProcedureStableKey() {

        return procedureStableKey;
    }


    public void setProcedureStableKey(Long procedureStableKey) {

        this.procedureStableKey = procedureStableKey;
    }


    public String getProcedureName() {

        return procedureName;
    }


    public void setProcedureName(String procedureName) {

        this.procedureName = procedureName;
    }


    public Long getProcedureId() {

        return procedureId;
    }


    public void setProcedureId(Long procedureId) {

        this.procedureId = procedureId;
    }


    public String getParameterStableId() {

        return parameterStableId;
    }


    public void setParameterStableId(String parameterStableId) {

        this.parameterStableId = parameterStableId;
    }


    public Long getParameterStableKey() {

        return parameterStableKey;
    }


    public void setParameterStableKey(Long parameterStableKey) {

        this.parameterStableKey = parameterStableKey;
    }


    public String getParameterName() {

        return parameterName;
    }


    public void setParameterName(String parameterName) {

        this.parameterName = parameterName;
    }


    public Long getParameterId() {

        return parameterId;
    }


    public void setParameterId(Long parameterId) {

        this.parameterId = parameterId;
    }


    public String getColonyId() {

        return colonyId;
    }


    public void setColonyId(String colonyId) {

        this.colonyId = colonyId;
    }


    public String getMarkerSymbol() {

        return markerSymbol;
    }


    public void setMarkerSymbol(String markerSymbol) {

        this.markerSymbol = markerSymbol;
    }


    public String getMarkerAccessionId() {

        return markerAccessionId;
    }


    public void setMarkerAccessionId(String markerAccessionId) {

        this.markerAccessionId = markerAccessionId;
    }


    public String getAlleleSymbol() {

        return alleleSymbol;
    }


    public void setAlleleSymbol(String alleleSymbol) {

        this.alleleSymbol = alleleSymbol;
    }


    public String getAlleleName() {

        return alleleName;
    }


    public void setAlleleName(String alleleName) {

        this.alleleName = alleleName;
    }


    public String getAlleleAccessionId() {

        return alleleAccessionId;
    }


    public void setAlleleAccessionId(String alleleAccessionId) {

        this.alleleAccessionId = alleleAccessionId;
    }


    public String getStrainName() {

        return strainName;
    }


    public void setStrainName(String strainName) {

        this.strainName = strainName;
    }


    public String getStrainAccessionId() {

        return strainAccessionId;
    }


    public void setStrainAccessionId(String strainAccessionId) {

        this.strainAccessionId = strainAccessionId;
    }


    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
    }

    public String getSex() {

        return sex;
    }


    public void setSex(String sex) {

        this.sex = sex;
    }


    public String getZygosity() {

        return zygosity;
    }


    public void setZygosity(String zygosity) {

        this.zygosity = zygosity;
    }


    public String getControlSelectionMethod() {

        return controlSelectionMethod;
    }


    public void setControlSelectionMethod(String controlSelectionMethod) {

        this.controlSelectionMethod = controlSelectionMethod;
    }


    public String getDependentVariable() {

        return dependentVariable;
    }


    public void setDependentVariable(String dependentVariable) {

        this.dependentVariable = dependentVariable;
    }


    public String getMetadataGroup() {

        return metadataGroup;
    }


    public void setMetadataGroup(String metadataGroup) {

        this.metadataGroup = metadataGroup;
    }


    public Long getControlBiologicalModelId() {

        return controlBiologicalModelId;
    }


    public void setControlBiologicalModelId(Long controlBiologicalModelId) {

        this.controlBiologicalModelId = controlBiologicalModelId;
    }


    public Long getMutantBiologicalModelId() {

        return mutantBiologicalModelId;
    }


    public void setMutantBiologicalModelId(Long mutantBiologicalModelId) {

        this.mutantBiologicalModelId = mutantBiologicalModelId;
    }


    public Integer getMaleControlCount() {

        return maleControlCount;
    }


    public void setMaleControlCount(Integer maleControlCount) {

        this.maleControlCount = maleControlCount;
    }


    public Integer getMaleMutantCount() {

        return maleMutantCount;
    }


    public void setMaleMutantCount(Integer maleMutantCount) {

        this.maleMutantCount = maleMutantCount;
    }


    public Integer getFemaleControlCount() {

        return femaleControlCount;
    }


    public void setFemaleControlCount(Integer femaleControlCount) {

        this.femaleControlCount = femaleControlCount;
    }


    public Integer getFemaleMutantCount() {

        return femaleMutantCount;
    }


    public void setFemaleMutantCount(Integer femaleMutantCount) {

        this.femaleMutantCount = femaleMutantCount;
    }


    public Double getFemaleMutantMean() {

        return femaleMutantMean;
    }


    public void setFemaleMutantMean(Double femaleMutantMean) {

        this.femaleMutantMean = femaleMutantMean;
    }


    public Double getFemaleControlMean() {

        return femaleControlMean;
    }


    public void setFemaleControlMean(Double femaleControlMean) {

        this.femaleControlMean = femaleControlMean;
    }


    public Double getMaleMutantMean() {

        return maleMutantMean;
    }


    public void setMaleMutantMean(Double maleMutantMean) {

        this.maleMutantMean = maleMutantMean;
    }


    public Double getMaleControlMean() {

        return maleControlMean;
    }


    public void setMaleControlMean(Double maleControlMean) {

        this.maleControlMean = maleControlMean;
    }

	public String getWorkflow() {
		return workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getStatisticalMethod() {

        return statisticalMethod;
    }


    public void setStatisticalMethod(String statisticalMethod) {

        this.statisticalMethod = statisticalMethod;
    }


    public String getStatus() {

        return status;
    }


    public void setStatus(String status) {

        this.status = status;
    }


    public String getAdditionalInformation() {

        return additionalInformation;
    }


    public void setAdditionalInformation(String additionalInformation) {

        this.additionalInformation = additionalInformation;
    }


    public String getRawOutput() {

        return rawOutput;
    }


    public void setRawOutput(String rawOutput) {

        this.rawOutput = rawOutput;
    }


    public Double getpValue() {

        return pValue;
    }


    public void setpValue(Double pValue) {

        this.pValue = pValue;
    }


    public Double getEffectSize() {

        return effectSize;
    }


    public void setEffectSize(Double effectSize) {

        this.effectSize = effectSize;
    }

	public Double getGenotypePvalueLowVsNormalHigh() {
		return genotypePvalueLowVsNormalHigh;
	}

	public void setGenotypePvalueLowVsNormalHigh(Double genotypePvalueLowVsNormalHigh) {
		this.genotypePvalueLowVsNormalHigh = genotypePvalueLowVsNormalHigh;
	}

	public Double getGenotypePvalueLowNormalVsHigh() {
		return genotypePvalueLowNormalVsHigh;
	}

	public void setGenotypePvalueLowNormalVsHigh(Double genotypePvalueLowNormalVsHigh) {
		this.genotypePvalueLowNormalVsHigh = genotypePvalueLowNormalVsHigh;
	}

	public Double getGenotypeEffectSizeLowVsNormalHigh() {
		return genotypeEffectSizeLowVsNormalHigh;
	}

	public void setGenotypeEffectSizeLowVsNormalHigh(Double genotypeEffectSizeLowVsNormalHigh) {
		this.genotypeEffectSizeLowVsNormalHigh = genotypeEffectSizeLowVsNormalHigh;
	}

	public Double getGenotypeEffectSizeLowNormalVsHigh() {
		return genotypeEffectSizeLowNormalVsHigh;
	}

	public void setGenotypeEffectSizeLowNormalVsHigh(Double genotypeEffectSizeLowNormalVsHigh) {
		this.genotypeEffectSizeLowNormalVsHigh = genotypeEffectSizeLowNormalVsHigh;
	}

	public Double getFemalePvalueLowVsNormalHigh() {
		return femalePvalueLowVsNormalHigh;
	}

	public void setFemalePvalueLowVsNormalHigh(Double femalePvalueLowVsNormalHigh) {
		this.femalePvalueLowVsNormalHigh = femalePvalueLowVsNormalHigh;
	}

	public Double getFemalePvalueLowNormalVsHigh() {
		return femalePvalueLowNormalVsHigh;
	}

	public void setFemalePvalueLowNormalVsHigh(Double femalePvalueLowNormalVsHigh) {
		this.femalePvalueLowNormalVsHigh = femalePvalueLowNormalVsHigh;
	}

	public Double getFemaleEffectSizeLowVsNormalHigh() {
		return femaleEffectSizeLowVsNormalHigh;
	}

	public void setFemaleEffectSizeLowVsNormalHigh(Double femaleEffectSizeLowVsNormalHigh) {
		this.femaleEffectSizeLowVsNormalHigh = femaleEffectSizeLowVsNormalHigh;
	}

	public Double getFemaleEffectSizeLowNormalVsHigh() {
		return femaleEffectSizeLowNormalVsHigh;
	}

	public void setFemaleEffectSizeLowNormalVsHigh(Double femaleEffectSizeLowNormalVsHigh) {
		this.femaleEffectSizeLowNormalVsHigh = femaleEffectSizeLowNormalVsHigh;
	}

	public Double getMalePvalueLowVsNormalHigh() {
		return malePvalueLowVsNormalHigh;
	}

	public void setMalePvalueLowVsNormalHigh(Double malePvalueLowVsNormalHigh) {
		this.malePvalueLowVsNormalHigh = malePvalueLowVsNormalHigh;
	}

	public Double getMalePvalueLowNormalVsHigh() {
		return malePvalueLowNormalVsHigh;
	}

	public void setMalePvalueLowNormalVsHigh(Double malePvalueLowNormalVsHigh) {
		this.malePvalueLowNormalVsHigh = malePvalueLowNormalVsHigh;
	}

	public Double getMaleEffectSizeLowVsNormalHigh() {
		return maleEffectSizeLowVsNormalHigh;
	}

	public void setMaleEffectSizeLowVsNormalHigh(Double maleEffectSizeLowVsNormalHigh) {
		this.maleEffectSizeLowVsNormalHigh = maleEffectSizeLowVsNormalHigh;
	}

	public Double getMaleEffectSizeLowNormalVsHigh() {
		return maleEffectSizeLowNormalVsHigh;
	}

	public void setMaleEffectSizeLowNormalVsHigh(Double maleEffectSizeLowNormalVsHigh) {
		this.maleEffectSizeLowNormalVsHigh = maleEffectSizeLowNormalVsHigh;
	}

	public List<String> getCategories() {

        return categories;
    }


    public void setCategories(List<String> categories) {

        this.categories = categories;
    }


    public Double getCategoricalPValue() {

        return categoricalPValue;
    }


    public void setCategoricalPValue(Double categoricalPValue) {

        this.categoricalPValue = categoricalPValue;
    }


    public Double getCategoricalEffectSize() {

        return categoricalEffectSize;
    }


    public void setCategoricalEffectSize(Double categoricalEffectSize) {

        this.categoricalEffectSize = categoricalEffectSize;
    }


    public Boolean getBatchSignificant() {

        return batchSignificant;
    }


    public void setBatchSignificant(Boolean batchSignificant) {

        this.batchSignificant = batchSignificant;
    }


    public Boolean getVarianceSignificant() {

        return varianceSignificant;
    }


    public void setVarianceSignificant(Boolean varianceSignificant) {

        this.varianceSignificant = varianceSignificant;
    }


    public Double getNullTestPValue() {

        return nullTestPValue;
    }


    public void setNullTestPValue(Double nullTestPValue) {

        this.nullTestPValue = nullTestPValue;
    }


    public Double getGenotypeEffectPValue() {

        return genotypeEffectPValue;
    }


    public void setGenotypeEffectPValue(Double genotypeEffectPValue) {

        this.genotypeEffectPValue = genotypeEffectPValue;
    }


    public Double getGenotypeEffectStderrEstimate() {

        return genotypeEffectStderrEstimate;
    }


    public void setGenotypeEffectStderrEstimate(Double genotypeEffectStderrEstimate) {

        this.genotypeEffectStderrEstimate = genotypeEffectStderrEstimate;
    }


    public Double getGenotypeEffectParameterEstimate() {

        return genotypeEffectParameterEstimate;
    }


    public void setGenotypeEffectParameterEstimate(Double genotypeEffectParameterEstimate) {

        this.genotypeEffectParameterEstimate = genotypeEffectParameterEstimate;
    }

    public String getFemalePercentageChange() {
        return femalePercentageChange;
    }

    public void setFemalePercentageChange(String femalePercentageChange) {
        this.femalePercentageChange = femalePercentageChange;
    }

    public String getMalePercentageChange() {
        return malePercentageChange;
    }

    public void setMalePercentageChange(String malePercentageChange) {
        this.malePercentageChange = malePercentageChange;
    }


    public Double getSexEffectPValue() {

        return sexEffectPValue;
    }


    public void setSexEffectPValue(Double sexEffectPValue) {

        this.sexEffectPValue = sexEffectPValue;
    }


    public Double getSexEffectStderrEstimate() {

        return sexEffectStderrEstimate;
    }


    public void setSexEffectStderrEstimate(Double sexEffectStderrEstimate) {

        this.sexEffectStderrEstimate = sexEffectStderrEstimate;
    }


    public Double getSexEffectParameterEstimate() {

        return sexEffectParameterEstimate;
    }


    public void setSexEffectParameterEstimate(Double sexEffectParameterEstimate) {

        this.sexEffectParameterEstimate = sexEffectParameterEstimate;
    }


    public Double getWeightEffectPValue() {

        return weightEffectPValue;
    }


    public void setWeightEffectPValue(Double weightEffectPValue) {

        this.weightEffectPValue = weightEffectPValue;
    }


    public Double getWeightEffectStderrEstimate() {

        return weightEffectStderrEstimate;
    }


    public void setWeightEffectStderrEstimate(Double weightEffectStderrEstimate) {

        this.weightEffectStderrEstimate = weightEffectStderrEstimate;
    }


    public Double getWeightEffectParameterEstimate() {

        return weightEffectParameterEstimate;
    }


    public void setWeightEffectParameterEstimate(Double weightEffectParameterEstimate) {

        this.weightEffectParameterEstimate = weightEffectParameterEstimate;
    }


    public String getGroup1Genotype() {

        return group1Genotype;
    }


    public void setGroup1Genotype(String group1Genotype) {

        this.group1Genotype = group1Genotype;
    }


    public Double getGroup1ResidualsNormalityTest() {

        return group1ResidualsNormalityTest;
    }


    public void setGroup1ResidualsNormalityTest(Double group1ResidualsNormalityTest) {

        this.group1ResidualsNormalityTest = group1ResidualsNormalityTest;
    }


    public String getGroup2Genotype() {

        return group2Genotype;
    }


    public void setGroup2Genotype(String group2Genotype) {

        this.group2Genotype = group2Genotype;
    }


    public Double getGroup2ResidualsNormalityTest() {

        return group2ResidualsNormalityTest;
    }


    public void setGroup2ResidualsNormalityTest(Double group2ResidualsNormalityTest) {

        this.group2ResidualsNormalityTest = group2ResidualsNormalityTest;
    }


    public Double getBlupsTest() {

        return blupsTest;
    }


    public void setBlupsTest(Double blupsTest) {

        this.blupsTest = blupsTest;
    }


    public Double getRotatedResidualsTest() {

        return rotatedResidualsTest;
    }


    public void setRotatedResidualsTest(Double rotatedResidualsTest) {

        this.rotatedResidualsTest = rotatedResidualsTest;
    }


    public Double getInterceptEstimate() {

        return interceptEstimate;
    }


    public void setInterceptEstimate(Double interceptEstimate) {

        this.interceptEstimate = interceptEstimate;
    }


    public Double getInterceptEstimateStderrEstimate() {

        return interceptEstimateStderrEstimate;
    }


    public void setInterceptEstimateStderrEstimate(Double interceptEstimateStderrEstimate) {

        this.interceptEstimateStderrEstimate = interceptEstimateStderrEstimate;
    }


    public Boolean getInteractionSignificant() {

        return interactionSignificant;
    }


    public void setInteractionSignificant(Boolean interactionSignificant) {

        this.interactionSignificant = interactionSignificant;
    }


    public Double getInteractionEffectPValue() {

        return interactionEffectPValue;
    }


    public void setInteractionEffectPValue(Double interactionEffectPValue) {

        this.interactionEffectPValue = interactionEffectPValue;
    }


    public Double getFemaleKoEffectPValue() {

        return femaleKoEffectPValue;
    }


    public void setFemaleKoEffectPValue(Double femaleKoEffectPValue) {

        this.femaleKoEffectPValue = femaleKoEffectPValue;
    }


    public Double getFemaleKoEffectStderrEstimate() {

        return femaleKoEffectStderrEstimate;
    }


    public void setFemaleKoEffectStderrEstimate(Double femaleKoEffectStderrEstimate) {

        this.femaleKoEffectStderrEstimate = femaleKoEffectStderrEstimate;
    }


    public Double getFemaleKoParameterEstimate() {

        return femaleKoParameterEstimate;
    }


    public void setFemaleKoParameterEstimate(Double femaleKoParameterEstimate) {

        this.femaleKoParameterEstimate = femaleKoParameterEstimate;
    }


    public Double getMaleKoEffectPValue() {

        return maleKoEffectPValue;
    }


    public void setMaleKoEffectPValue(Double maleKoEffectPValue) {

        this.maleKoEffectPValue = maleKoEffectPValue;
    }


    public Double getMaleKoEffectStderrEstimate() {

        return maleKoEffectStderrEstimate;
    }


    public void setMaleKoEffectStderrEstimate(Double maleKoEffectStderrEstimate) {

        this.maleKoEffectStderrEstimate = maleKoEffectStderrEstimate;
    }


    public Double getMaleKoParameterEstimate() {

        return maleKoParameterEstimate;
    }


    public void setMaleKoParameterEstimate(Double maleKoParameterEstimate) {

        this.maleKoParameterEstimate = maleKoParameterEstimate;
    }


    public String getClassificationTag() {

        return classificationTag;
    }


    public void setClassificationTag(String classificationTag) {

        this.classificationTag = classificationTag;
    }

    public Long getExternalDbId() {
        return externalDbId;
    }

    public void setExternalDbId(Long externalDbId) {
        this.externalDbId = externalDbId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Long getPhenotypingCenterId() {
        return phenotypingCenterId;
    }

    public void setPhenotypingCenterId(Long phenotypingCenterId) {
        this.phenotypingCenterId = phenotypingCenterId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<String> getPhenotypeSex() {
		return phenotypeSex;
	}

	public void setPhenotypeSex(List<String> phenotypeSex) {
		this.phenotypeSex = phenotypeSex;
	}

	public Boolean getSignificant() {
		return significant;
	}

	public void setSignificant(Boolean significant) {
		this.significant = significant;
	}

	public void addPhenotypeSex(String phenotypeSex) {

		if (this.phenotypeSex == null){
			this.phenotypeSex = new ArrayList<String>();
		}
		this.phenotypeSex.add(phenotypeSex);
	}


	public List<String> getAnatomyTermId() {
		return anatomyTermId;
	}

	public void addAnatomyTermId(String anatomyTermId){
		if (this.anatomyTermId == null){
			this.anatomyTermId = new ArrayList<>();
		}
		this.anatomyTermId.add(anatomyTermId);
	}

	public void setAnatomyTermId(List<String> anatomyTermId) {
		this.anatomyTermId = anatomyTermId;
	}

	public List<String> getAnatomyTermName() {
		return anatomyTermName;
	}

	public void setAnatomyTermName(List<String> anatomyTermName) {
		this.anatomyTermName = anatomyTermName;
	}

	public void addAnatomyTermName(String anatomyTermName){
		if (this.anatomyTermName == null){
			this.anatomyTermName = new ArrayList<>();
		}
		this.anatomyTermName.add(anatomyTermName);
	}


	public List<String> getIntermediateAnatomyTermId() {
		return intermediateAnatomyTermId;
	}

	public void setIntermediateAnatomyTermId(List<String> intermediateAnatomyTermId) {
		this.intermediateAnatomyTermId = intermediateAnatomyTermId;
	}

	public void addIntermediateAnatomyTermId(String intermediateAnatomyTermId){
		if (this.intermediateAnatomyTermId == null){
			this.intermediateAnatomyTermId = new ArrayList<>();
		}
		this.intermediateAnatomyTermId.add(intermediateAnatomyTermId);
	}

	public void addIntermediateAnatomyTermId(Collection<String> intermediateAnatomyTermIds){
		if (this.intermediateAnatomyTermId == null){
			this.intermediateAnatomyTermId = new ArrayList<>();
		}
		for (String term : intermediateAnatomyTermIds){
			if (!this.intermediateAnatomyTermId.contains(term)){
				this.intermediateAnatomyTermId.add(term);
			}
		}
	}

	public List<String> getIntermediateAnatomyTermName() {
		return intermediateAnatomyTermName;
	}

	public void setIntermediateAnatomyTermName(List<String> intermediateAnatomyTermName) {
		this.intermediateAnatomyTermName = intermediateAnatomyTermName;
	}

	public void addIntermediateAnatomyTermName(String intermediateAnatomyTermName){
		if (this.intermediateAnatomyTermName == null){
			this.intermediateAnatomyTermName = new ArrayList<>();
		}
		this.intermediateAnatomyTermName.add(intermediateAnatomyTermName);
	}

	public void addIntermediateAnatomyTermName(Collection<String> intermediateAnatomyTermName){
		if (this.intermediateAnatomyTermName == null){
			this.intermediateAnatomyTermName = new ArrayList<>();
		}
		for (String term : intermediateAnatomyTermName){
			if (!this.intermediateAnatomyTermName.contains(term)){
				this.intermediateAnatomyTermName.add(term);
			}
		}
	}

	public List<String> getTopLevelAnatomyTermId() {
		return topLevelAnatomyTermId;
	}

	public void setTopLevelAnatomyTermId(List<String> topLevelAnatomyTermId) {
		this.topLevelAnatomyTermId = topLevelAnatomyTermId;
	}

	public void addTopLevelAnatomyTermId(String topLevelAnatomyTermId){
		if (this.topLevelAnatomyTermId == null){
			this.topLevelAnatomyTermId = new ArrayList<>();
		}
		this.topLevelAnatomyTermId.add(topLevelAnatomyTermId);
	}

	public void addTopLevelAnatomyTermId(Collection<String> topLevelAnatomyTermId){
		if (topLevelAnatomyTermId != null) {
            if (this.topLevelAnatomyTermId == null) {
                this.topLevelAnatomyTermId = new ArrayList<>();
            }
            for (String term : topLevelAnatomyTermId) {
                if (!this.topLevelAnatomyTermId.contains(term)) {
                    this.topLevelAnatomyTermId.add(term);
                }
            }
        }
	}

	public List<String> getTopLevelAnatomyTermName() {
		return topLevelAnatomyTermName;
	}

	public void setTopLevelAnatomyTermName(List<String> topLevelAnatomyTermName) {
		this.topLevelAnatomyTermName = topLevelAnatomyTermName;
	}

	public void addTopLevelAnatomyTermName(String topLevelAnatomyTermName){
		if (this.topLevelAnatomyTermName == null){
			this.topLevelAnatomyTermName = new ArrayList<>();
		}
		this.topLevelAnatomyTermName.add(topLevelAnatomyTermName);
	}

	public void addTopLevelAnatomyTermName(Collection<String> topLevelAnatomyTermName){
	    if (topLevelAnatomyTermName != null) {
            if (this.topLevelAnatomyTermName == null) {
                this.topLevelAnatomyTermName = new ArrayList<>();
            }
            for (String term : topLevelAnatomyTermName) {
                if (!this.topLevelAnatomyTermName.contains(term)) {
                    this.topLevelAnatomyTermName.add(term);
                }
            }
        }
	}



    public void addTopLevelMpTermId(String topLevelMpTermId){
        if (this.topLevelMpTermId == null){
            this.topLevelMpTermId = new ArrayList<>();
        }
        this.topLevelMpTermId.add(topLevelMpTermId);
    }

    public void addTopLevelMpTermIds(Collection<String> topLevelMpTermIds){
        if (this.topLevelMpTermId == null){
            this.topLevelMpTermId = new ArrayList<>();
        }
        for (String term : topLevelMpTermIds){
            if (!this.topLevelMpTermId.contains(term)){
                this.topLevelMpTermId.add(term);
            }
        }
    }

    public void addTopLevelMpTermName(String topLevelMpTermName){
        if (this.topLevelMpTermName == null){
            this.topLevelMpTermName = new ArrayList<>();
        }
        if (!this.topLevelMpTermName.contains(topLevelMpTermName)) {
            this.topLevelMpTermName.add(topLevelMpTermName);
        }
    }

    public void addTopLevelMpTermNames(Collection<String> topLevelAnatomyTermNames){
        if (this.topLevelMpTermName == null){
            this.topLevelMpTermName = new ArrayList<>();
        }
        for (String term : topLevelAnatomyTermNames){
            if (!this.topLevelMpTermName.contains(term)){
                this.topLevelMpTermName.add(term);
            }
        }
    }
    

    public String getProductionCenter() {
    		return this.productionCenter;
    }


    public void setProductionCenter(String productionCenter) {

        this.productionCenter = productionCenter;
    }


    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StatisticalResultDTO that = (StatisticalResultDTO) o;

		if (getDocId() != null ? !getDocId().equals(that.getDocId()) : that.getDocId() != null) return false;
		if (getDbId() != null ? !getDbId().equals(that.getDbId()) : that.getDbId() != null) return false;
		if (getAnatomyTermId() != null ? !getAnatomyTermId().equals(that.getAnatomyTermId()) : that.getAnatomyTermId() != null)
			return false;
		if (getAnatomyTermName() != null ? !getAnatomyTermName().equals(that.getAnatomyTermName()) : that.getAnatomyTermName() != null)
			return false;
		if (getIntermediateAnatomyTermId() != null ? !getIntermediateAnatomyTermId().equals(that.getIntermediateAnatomyTermId()) : that.getIntermediateAnatomyTermId() != null)
			return false;
		if (getIntermediateAnatomyTermName() != null ? !getIntermediateAnatomyTermName().equals(that.getIntermediateAnatomyTermName()) : that.getIntermediateAnatomyTermName() != null)
			return false;
		if (getTopLevelAnatomyTermId() != null ? !getTopLevelAnatomyTermId().equals(that.getTopLevelAnatomyTermId()) : that.getTopLevelAnatomyTermId() != null)
			return false;
		if (getTopLevelAnatomyTermName() != null ? !getTopLevelAnatomyTermName().equals(that.getTopLevelAnatomyTermName()) : that.getTopLevelAnatomyTermName() != null)
			return false;
		if (getPhenotypeSex() != null ? !getPhenotypeSex().equals(that.getPhenotypeSex()) : that.getPhenotypeSex() != null)
			return false;
		if (getDataType() != null ? !getDataType().equals(that.getDataType()) : that.getDataType() != null)
			return false;
		if (getMpTermIdOptions() != null ? !getMpTermIdOptions().equals(that.getMpTermIdOptions()) : that.getMpTermIdOptions() != null)
			return false;
		if (getMpTermNameOptions() != null ? !getMpTermNameOptions().equals(that.getMpTermNameOptions()) : that.getMpTermNameOptions() != null)
			return false;
		if (getMpTermId() != null ? !getMpTermId().equals(that.getMpTermId()) : that.getMpTermId() != null)
			return false;
		if (getMpTermName() != null ? !getMpTermName().equals(that.getMpTermName()) : that.getMpTermName() != null)
			return false;
		if (getTopLevelMpTermId() != null ? !getTopLevelMpTermId().equals(that.getTopLevelMpTermId()) : that.getTopLevelMpTermId() != null)
			return false;
		if (getTopLevelMpTermName() != null ? !getTopLevelMpTermName().equals(that.getTopLevelMpTermName()) : that.getTopLevelMpTermName() != null)
			return false;
		if (getIntermediateMpTermId() != null ? !getIntermediateMpTermId().equals(that.getIntermediateMpTermId()) : that.getIntermediateMpTermId() != null)
			return false;
		if (getIntermediateMpTermName() != null ? !getIntermediateMpTermName().equals(that.getIntermediateMpTermName()) : that.getIntermediateMpTermName() != null)
			return false;
		if (getMaleMpTermId() != null ? !getMaleMpTermId().equals(that.getMaleMpTermId()) : that.getMaleMpTermId() != null)
			return false;
		if (getMaleMpTermName() != null ? !getMaleMpTermName().equals(that.getMaleMpTermName()) : that.getMaleMpTermName() != null)
			return false;
		if (getMaleTopLevelMpTermId() != null ? !getMaleTopLevelMpTermId().equals(that.getMaleTopLevelMpTermId()) : that.getMaleTopLevelMpTermId() != null)
			return false;
		if (getMaleTopLevelMpTermName() != null ? !getMaleTopLevelMpTermName().equals(that.getMaleTopLevelMpTermName()) : that.getMaleTopLevelMpTermName() != null)
			return false;
		if (getMaleIntermediateMpTermId() != null ? !getMaleIntermediateMpTermId().equals(that.getMaleIntermediateMpTermId()) : that.getMaleIntermediateMpTermId() != null)
			return false;
		if (getMaleIntermediateMpTermName() != null ? !getMaleIntermediateMpTermName().equals(that.getMaleIntermediateMpTermName()) : that.getMaleIntermediateMpTermName() != null)
			return false;
		if (getFemaleMpTermId() != null ? !getFemaleMpTermId().equals(that.getFemaleMpTermId()) : that.getFemaleMpTermId() != null)
			return false;
		if (getFemaleMpTermName() != null ? !getFemaleMpTermName().equals(that.getFemaleMpTermName()) : that.getFemaleMpTermName() != null)
			return false;
		if (getFemaleTopLevelMpTermId() != null ? !getFemaleTopLevelMpTermId().equals(that.getFemaleTopLevelMpTermId()) : that.getFemaleTopLevelMpTermId() != null)
			return false;
		if (getFemaleTopLevelMpTermName() != null ? !getFemaleTopLevelMpTermName().equals(that.getFemaleTopLevelMpTermName()) : that.getFemaleTopLevelMpTermName() != null)
			return false;
		if (getFemaleIntermediateMpTermId() != null ? !getFemaleIntermediateMpTermId().equals(that.getFemaleIntermediateMpTermId()) : that.getFemaleIntermediateMpTermId() != null)
			return false;
		if (getFemaleIntermediateMpTermName() != null ? !getFemaleIntermediateMpTermName().equals(that.getFemaleIntermediateMpTermName()) : that.getFemaleIntermediateMpTermName() != null)
			return false;
		if (getResourceName() != null ? !getResourceName().equals(that.getResourceName()) : that.getResourceName() != null)
			return false;
		if (getResourceFullname() != null ? !getResourceFullname().equals(that.getResourceFullname()) : that.getResourceFullname() != null)
			return false;
		if (getResourceId() != null ? !getResourceId().equals(that.getResourceId()) : that.getResourceId() != null)
			return false;
		if (getProjectName() != null ? !getProjectName().equals(that.getProjectName()) : that.getProjectName() != null)
			return false;
		if (getPhenotypingCenter() != null ? !getPhenotypingCenter().equals(that.getPhenotypingCenter()) : that.getPhenotypingCenter() != null)
			return false;
		if (getPipelineStableId() != null ? !getPipelineStableId().equals(that.getPipelineStableId()) : that.getPipelineStableId() != null)
			return false;
		if (getPipelineStableKey() != null ? !getPipelineStableKey().equals(that.getPipelineStableKey()) : that.getPipelineStableKey() != null)
			return false;
		if (getPipelineName() != null ? !getPipelineName().equals(that.getPipelineName()) : that.getPipelineName() != null)
			return false;
		if (getPipelineId() != null ? !getPipelineId().equals(that.getPipelineId()) : that.getPipelineId() != null)
			return false;
		if (getProcedureStableId() != null ? !getProcedureStableId().equals(that.getProcedureStableId()) : that.getProcedureStableId() != null)
			return false;
		if (getProcedureStableKey() != null ? !getProcedureStableKey().equals(that.getProcedureStableKey()) : that.getProcedureStableKey() != null)
			return false;
		if (getProcedureName() != null ? !getProcedureName().equals(that.getProcedureName()) : that.getProcedureName() != null)
			return false;
		if (getProcedureId() != null ? !getProcedureId().equals(that.getProcedureId()) : that.getProcedureId() != null)
			return false;
		if (getParameterStableId() != null ? !getParameterStableId().equals(that.getParameterStableId()) : that.getParameterStableId() != null)
			return false;
		if (getParameterStableKey() != null ? !getParameterStableKey().equals(that.getParameterStableKey()) : that.getParameterStableKey() != null)
			return false;
		if (getParameterName() != null ? !getParameterName().equals(that.getParameterName()) : that.getParameterName() != null)
			return false;
		if (getParameterId() != null ? !getParameterId().equals(that.getParameterId()) : that.getParameterId() != null)
			return false;
		if (getColonyId() != null ? !getColonyId().equals(that.getColonyId()) : that.getColonyId() != null)
			return false;
		if (getMarkerSymbol() != null ? !getMarkerSymbol().equals(that.getMarkerSymbol()) : that.getMarkerSymbol() != null)
			return false;
		if (getMarkerAccessionId() != null ? !getMarkerAccessionId().equals(that.getMarkerAccessionId()) : that.getMarkerAccessionId() != null)
			return false;
		if (getAlleleSymbol() != null ? !getAlleleSymbol().equals(that.getAlleleSymbol()) : that.getAlleleSymbol() != null)
			return false;
		if (getAlleleName() != null ? !getAlleleName().equals(that.getAlleleName()) : that.getAlleleName() != null)
			return false;
		if (getAlleleAccessionId() != null ? !getAlleleAccessionId().equals(that.getAlleleAccessionId()) : that.getAlleleAccessionId() != null)
			return false;
		if (getStrainName() != null ? !getStrainName().equals(that.getStrainName()) : that.getStrainName() != null)
			return false;
		if (getStrainAccessionId() != null ? !getStrainAccessionId().equals(that.getStrainAccessionId()) : that.getStrainAccessionId() != null)
			return false;
		if (getGeneticBackground() != null ? !getGeneticBackground().equals(that.getGeneticBackground()) : that.getGeneticBackground() != null)
			return false;
		if (getSex() != null ? !getSex().equals(that.getSex()) : that.getSex() != null) return false;
		if (getZygosity() != null ? !getZygosity().equals(that.getZygosity()) : that.getZygosity() != null)
			return false;
		if (getProductionCenter() != null ? !getProductionCenter().equals(that.getProductionCenter()) : that.getProductionCenter() != null)
			return false;
		if (getControlSelectionMethod() != null ? !getControlSelectionMethod().equals(that.getControlSelectionMethod()) : that.getControlSelectionMethod() != null)
			return false;
		if (getDependentVariable() != null ? !getDependentVariable().equals(that.getDependentVariable()) : that.getDependentVariable() != null)
			return false;
		if (getMetadataGroup() != null ? !getMetadataGroup().equals(that.getMetadataGroup()) : that.getMetadataGroup() != null)
			return false;
		if (getControlBiologicalModelId() != null ? !getControlBiologicalModelId().equals(that.getControlBiologicalModelId()) : that.getControlBiologicalModelId() != null)
			return false;
		if (getMutantBiologicalModelId() != null ? !getMutantBiologicalModelId().equals(that.getMutantBiologicalModelId()) : that.getMutantBiologicalModelId() != null)
			return false;
		if (getMaleControlCount() != null ? !getMaleControlCount().equals(that.getMaleControlCount()) : that.getMaleControlCount() != null)
			return false;
		if (getMaleMutantCount() != null ? !getMaleMutantCount().equals(that.getMaleMutantCount()) : that.getMaleMutantCount() != null)
			return false;
		if (getFemaleControlCount() != null ? !getFemaleControlCount().equals(that.getFemaleControlCount()) : that.getFemaleControlCount() != null)
			return false;
		if (getFemaleMutantCount() != null ? !getFemaleMutantCount().equals(that.getFemaleMutantCount()) : that.getFemaleMutantCount() != null)
			return false;
		if (getFemaleMutantMean() != null ? !getFemaleMutantMean().equals(that.getFemaleMutantMean()) : that.getFemaleMutantMean() != null)
			return false;
		if (getFemaleControlMean() != null ? !getFemaleControlMean().equals(that.getFemaleControlMean()) : that.getFemaleControlMean() != null)
			return false;
		if (getMaleMutantMean() != null ? !getMaleMutantMean().equals(that.getMaleMutantMean()) : that.getMaleMutantMean() != null)
			return false;
		if (getMaleControlMean() != null ? !getMaleControlMean().equals(that.getMaleControlMean()) : that.getMaleControlMean() != null)
			return false;
		if (getWorkflow() != null ? !getWorkflow().equals(that.getWorkflow()) : that.getWorkflow() != null)
			return false;
		if (getStatisticalMethod() != null ? !getStatisticalMethod().equals(that.getStatisticalMethod()) : that.getStatisticalMethod() != null)
			return false;
		if (getStatus() != null ? !getStatus().equals(that.getStatus()) : that.getStatus() != null) return false;
		if (getAdditionalInformation() != null ? !getAdditionalInformation().equals(that.getAdditionalInformation()) : that.getAdditionalInformation() != null)
			return false;
		if (getRawOutput() != null ? !getRawOutput().equals(that.getRawOutput()) : that.getRawOutput() != null)
			return false;
		if (getpValue() != null ? !getpValue().equals(that.getpValue()) : that.getpValue() != null) return false;
		if (getEffectSize() != null ? !getEffectSize().equals(that.getEffectSize()) : that.getEffectSize() != null)
			return false;
		if (getGenotypePvalueLowVsNormalHigh() != null ? !getGenotypePvalueLowVsNormalHigh().equals(that.getGenotypePvalueLowVsNormalHigh()) : that.getGenotypePvalueLowVsNormalHigh() != null)
			return false;
		if (getGenotypePvalueLowNormalVsHigh() != null ? !getGenotypePvalueLowNormalVsHigh().equals(that.getGenotypePvalueLowNormalVsHigh()) : that.getGenotypePvalueLowNormalVsHigh() != null)
			return false;
		if (getGenotypeEffectSizeLowVsNormalHigh() != null ? !getGenotypeEffectSizeLowVsNormalHigh().equals(that.getGenotypeEffectSizeLowVsNormalHigh()) : that.getGenotypeEffectSizeLowVsNormalHigh() != null)
			return false;
		if (getGenotypeEffectSizeLowNormalVsHigh() != null ? !getGenotypeEffectSizeLowNormalVsHigh().equals(that.getGenotypeEffectSizeLowNormalVsHigh()) : that.getGenotypeEffectSizeLowNormalVsHigh() != null)
			return false;
		if (getFemalePvalueLowVsNormalHigh() != null ? !getFemalePvalueLowVsNormalHigh().equals(that.getFemalePvalueLowVsNormalHigh()) : that.getFemalePvalueLowVsNormalHigh() != null)
			return false;
		if (getFemalePvalueLowNormalVsHigh() != null ? !getFemalePvalueLowNormalVsHigh().equals(that.getFemalePvalueLowNormalVsHigh()) : that.getFemalePvalueLowNormalVsHigh() != null)
			return false;
		if (getFemaleEffectSizeLowVsNormalHigh() != null ? !getFemaleEffectSizeLowVsNormalHigh().equals(that.getFemaleEffectSizeLowVsNormalHigh()) : that.getFemaleEffectSizeLowVsNormalHigh() != null)
			return false;
		if (getFemaleEffectSizeLowNormalVsHigh() != null ? !getFemaleEffectSizeLowNormalVsHigh().equals(that.getFemaleEffectSizeLowNormalVsHigh()) : that.getFemaleEffectSizeLowNormalVsHigh() != null)
			return false;
		if (getMalePvalueLowVsNormalHigh() != null ? !getMalePvalueLowVsNormalHigh().equals(that.getMalePvalueLowVsNormalHigh()) : that.getMalePvalueLowVsNormalHigh() != null)
			return false;
		if (getMalePvalueLowNormalVsHigh() != null ? !getMalePvalueLowNormalVsHigh().equals(that.getMalePvalueLowNormalVsHigh()) : that.getMalePvalueLowNormalVsHigh() != null)
			return false;
		if (getMaleEffectSizeLowVsNormalHigh() != null ? !getMaleEffectSizeLowVsNormalHigh().equals(that.getMaleEffectSizeLowVsNormalHigh()) : that.getMaleEffectSizeLowVsNormalHigh() != null)
			return false;
		if (getMaleEffectSizeLowNormalVsHigh() != null ? !getMaleEffectSizeLowNormalVsHigh().equals(that.getMaleEffectSizeLowNormalVsHigh()) : that.getMaleEffectSizeLowNormalVsHigh() != null)
			return false;
		if (getCategories() != null ? !getCategories().equals(that.getCategories()) : that.getCategories() != null)
			return false;
		if (getCategoricalPValue() != null ? !getCategoricalPValue().equals(that.getCategoricalPValue()) : that.getCategoricalPValue() != null)
			return false;
		if (getCategoricalEffectSize() != null ? !getCategoricalEffectSize().equals(that.getCategoricalEffectSize()) : that.getCategoricalEffectSize() != null)
			return false;
		if (getBatchSignificant() != null ? !getBatchSignificant().equals(that.getBatchSignificant()) : that.getBatchSignificant() != null)
			return false;
		if (getVarianceSignificant() != null ? !getVarianceSignificant().equals(that.getVarianceSignificant()) : that.getVarianceSignificant() != null)
			return false;
		if (getNullTestPValue() != null ? !getNullTestPValue().equals(that.getNullTestPValue()) : that.getNullTestPValue() != null)
			return false;
		if (getGenotypeEffectPValue() != null ? !getGenotypeEffectPValue().equals(that.getGenotypeEffectPValue()) : that.getGenotypeEffectPValue() != null)
			return false;
		if (getGenotypeEffectStderrEstimate() != null ? !getGenotypeEffectStderrEstimate().equals(that.getGenotypeEffectStderrEstimate()) : that.getGenotypeEffectStderrEstimate() != null)
			return false;
		if (getGenotypeEffectParameterEstimate() != null ? !getGenotypeEffectParameterEstimate().equals(that.getGenotypeEffectParameterEstimate()) : that.getGenotypeEffectParameterEstimate() != null)
			return false;
		if (getFemalePercentageChange() != null ? !getFemalePercentageChange().equals(that.getFemalePercentageChange()) : that.getFemalePercentageChange() != null)
			return false;
		if (getMalePercentageChange() != null ? !getMalePercentageChange().equals(that.getMalePercentageChange()) : that.getMalePercentageChange() != null)
			return false;
		if (getSexEffectPValue() != null ? !getSexEffectPValue().equals(that.getSexEffectPValue()) : that.getSexEffectPValue() != null)
			return false;
		if (getSexEffectStderrEstimate() != null ? !getSexEffectStderrEstimate().equals(that.getSexEffectStderrEstimate()) : that.getSexEffectStderrEstimate() != null)
			return false;
		if (getSexEffectParameterEstimate() != null ? !getSexEffectParameterEstimate().equals(that.getSexEffectParameterEstimate()) : that.getSexEffectParameterEstimate() != null)
			return false;
		if (getWeightEffectPValue() != null ? !getWeightEffectPValue().equals(that.getWeightEffectPValue()) : that.getWeightEffectPValue() != null)
			return false;
		if (getWeightEffectStderrEstimate() != null ? !getWeightEffectStderrEstimate().equals(that.getWeightEffectStderrEstimate()) : that.getWeightEffectStderrEstimate() != null)
			return false;
		if (getWeightEffectParameterEstimate() != null ? !getWeightEffectParameterEstimate().equals(that.getWeightEffectParameterEstimate()) : that.getWeightEffectParameterEstimate() != null)
			return false;
		if (getGroup1Genotype() != null ? !getGroup1Genotype().equals(that.getGroup1Genotype()) : that.getGroup1Genotype() != null)
			return false;
		if (getGroup1ResidualsNormalityTest() != null ? !getGroup1ResidualsNormalityTest().equals(that.getGroup1ResidualsNormalityTest()) : that.getGroup1ResidualsNormalityTest() != null)
			return false;
		if (getGroup2Genotype() != null ? !getGroup2Genotype().equals(that.getGroup2Genotype()) : that.getGroup2Genotype() != null)
			return false;
		if (getGroup2ResidualsNormalityTest() != null ? !getGroup2ResidualsNormalityTest().equals(that.getGroup2ResidualsNormalityTest()) : that.getGroup2ResidualsNormalityTest() != null)
			return false;
		if (getBlupsTest() != null ? !getBlupsTest().equals(that.getBlupsTest()) : that.getBlupsTest() != null)
			return false;
		if (getRotatedResidualsTest() != null ? !getRotatedResidualsTest().equals(that.getRotatedResidualsTest()) : that.getRotatedResidualsTest() != null)
			return false;
		if (getInterceptEstimate() != null ? !getInterceptEstimate().equals(that.getInterceptEstimate()) : that.getInterceptEstimate() != null)
			return false;
		if (getInterceptEstimateStderrEstimate() != null ? !getInterceptEstimateStderrEstimate().equals(that.getInterceptEstimateStderrEstimate()) : that.getInterceptEstimateStderrEstimate() != null)
			return false;
		if (getInteractionSignificant() != null ? !getInteractionSignificant().equals(that.getInteractionSignificant()) : that.getInteractionSignificant() != null)
			return false;
		if (getInteractionEffectPValue() != null ? !getInteractionEffectPValue().equals(that.getInteractionEffectPValue()) : that.getInteractionEffectPValue() != null)
			return false;
		if (getFemaleKoEffectPValue() != null ? !getFemaleKoEffectPValue().equals(that.getFemaleKoEffectPValue()) : that.getFemaleKoEffectPValue() != null)
			return false;
		if (getFemaleKoEffectStderrEstimate() != null ? !getFemaleKoEffectStderrEstimate().equals(that.getFemaleKoEffectStderrEstimate()) : that.getFemaleKoEffectStderrEstimate() != null)
			return false;
		if (getFemaleKoParameterEstimate() != null ? !getFemaleKoParameterEstimate().equals(that.getFemaleKoParameterEstimate()) : that.getFemaleKoParameterEstimate() != null)
			return false;
		if (getMaleKoEffectPValue() != null ? !getMaleKoEffectPValue().equals(that.getMaleKoEffectPValue()) : that.getMaleKoEffectPValue() != null)
			return false;
		if (getMaleKoEffectStderrEstimate() != null ? !getMaleKoEffectStderrEstimate().equals(that.getMaleKoEffectStderrEstimate()) : that.getMaleKoEffectStderrEstimate() != null)
			return false;
		if (getMaleKoParameterEstimate() != null ? !getMaleKoParameterEstimate().equals(that.getMaleKoParameterEstimate()) : that.getMaleKoParameterEstimate() != null)
			return false;
		if (getClassificationTag() != null ? !getClassificationTag().equals(that.getClassificationTag()) : that.getClassificationTag() != null)
			return false;
		if (getExternalDbId() != null ? !getExternalDbId().equals(that.getExternalDbId()) : that.getExternalDbId() != null)
			return false;
		if (getOrganisationId() != null ? !getOrganisationId().equals(that.getOrganisationId()) : that.getOrganisationId() != null)
			return false;
		if (getPhenotypingCenterId() != null ? !getPhenotypingCenterId().equals(that.getPhenotypingCenterId()) : that.getPhenotypingCenterId() != null)
			return false;
		if (getProjectId() != null ? !getProjectId().equals(that.getProjectId()) : that.getProjectId() != null)
			return false;
        if (lifeStageAcc != null ? !lifeStageAcc.equals(that.lifeStageAcc) : that.lifeStageAcc != null)
            return false;
        if (lifeStageName != null ? !lifeStageName.equals(that.lifeStageName) : that.lifeStageName != null)
            return false;
		return getSignificant() != null ? getSignificant().equals(that.getSignificant()) : that.getSignificant() == null;
	}

	@Override
	public int hashCode() {
		int result = getDocId() != null ? getDocId().hashCode() : 0;
		result = 31 * result + (getDbId() != null ? getDbId().hashCode() : 0);
		result = 31 * result + (getAnatomyTermId() != null ? getAnatomyTermId().hashCode() : 0);
		result = 31 * result + (getAnatomyTermName() != null ? getAnatomyTermName().hashCode() : 0);
		result = 31 * result + (getIntermediateAnatomyTermId() != null ? getIntermediateAnatomyTermId().hashCode() : 0);
		result = 31 * result + (getIntermediateAnatomyTermName() != null ? getIntermediateAnatomyTermName().hashCode() : 0);
		result = 31 * result + (getTopLevelAnatomyTermId() != null ? getTopLevelAnatomyTermId().hashCode() : 0);
		result = 31 * result + (getTopLevelAnatomyTermName() != null ? getTopLevelAnatomyTermName().hashCode() : 0);
		result = 31 * result + (getPhenotypeSex() != null ? getPhenotypeSex().hashCode() : 0);
		result = 31 * result + (getDataType() != null ? getDataType().hashCode() : 0);
		result = 31 * result + (getMpTermIdOptions() != null ? getMpTermIdOptions().hashCode() : 0);
		result = 31 * result + (getMpTermNameOptions() != null ? getMpTermNameOptions().hashCode() : 0);
		result = 31 * result + (getMpTermId() != null ? getMpTermId().hashCode() : 0);
		result = 31 * result + (getMpTermName() != null ? getMpTermName().hashCode() : 0);
		result = 31 * result + (getTopLevelMpTermId() != null ? getTopLevelMpTermId().hashCode() : 0);
		result = 31 * result + (getTopLevelMpTermName() != null ? getTopLevelMpTermName().hashCode() : 0);
		result = 31 * result + (getIntermediateMpTermId() != null ? getIntermediateMpTermId().hashCode() : 0);
		result = 31 * result + (getIntermediateMpTermName() != null ? getIntermediateMpTermName().hashCode() : 0);
		result = 31 * result + (getMaleMpTermId() != null ? getMaleMpTermId().hashCode() : 0);
		result = 31 * result + (getMaleMpTermName() != null ? getMaleMpTermName().hashCode() : 0);
		result = 31 * result + (getMaleTopLevelMpTermId() != null ? getMaleTopLevelMpTermId().hashCode() : 0);
		result = 31 * result + (getMaleTopLevelMpTermName() != null ? getMaleTopLevelMpTermName().hashCode() : 0);
		result = 31 * result + (getMaleIntermediateMpTermId() != null ? getMaleIntermediateMpTermId().hashCode() : 0);
		result = 31 * result + (getMaleIntermediateMpTermName() != null ? getMaleIntermediateMpTermName().hashCode() : 0);
		result = 31 * result + (getFemaleMpTermId() != null ? getFemaleMpTermId().hashCode() : 0);
		result = 31 * result + (getFemaleMpTermName() != null ? getFemaleMpTermName().hashCode() : 0);
		result = 31 * result + (getFemaleTopLevelMpTermId() != null ? getFemaleTopLevelMpTermId().hashCode() : 0);
		result = 31 * result + (getFemaleTopLevelMpTermName() != null ? getFemaleTopLevelMpTermName().hashCode() : 0);
		result = 31 * result + (getFemaleIntermediateMpTermId() != null ? getFemaleIntermediateMpTermId().hashCode() : 0);
		result = 31 * result + (getFemaleIntermediateMpTermName() != null ? getFemaleIntermediateMpTermName().hashCode() : 0);
		result = 31 * result + (getResourceName() != null ? getResourceName().hashCode() : 0);
		result = 31 * result + (getResourceFullname() != null ? getResourceFullname().hashCode() : 0);
		result = 31 * result + (getResourceId() != null ? getResourceId().hashCode() : 0);
		result = 31 * result + (getProjectName() != null ? getProjectName().hashCode() : 0);
		result = 31 * result + (getPhenotypingCenter() != null ? getPhenotypingCenter().hashCode() : 0);
		result = 31 * result + (getPipelineStableId() != null ? getPipelineStableId().hashCode() : 0);
		result = 31 * result + (getPipelineStableKey() != null ? getPipelineStableKey().hashCode() : 0);
		result = 31 * result + (getPipelineName() != null ? getPipelineName().hashCode() : 0);
		result = 31 * result + (getPipelineId() != null ? getPipelineId().hashCode() : 0);
		result = 31 * result + (getProcedureStableId() != null ? getProcedureStableId().hashCode() : 0);
		result = 31 * result + (getProcedureStableKey() != null ? getProcedureStableKey().hashCode() : 0);
		result = 31 * result + (getProcedureName() != null ? getProcedureName().hashCode() : 0);
		result = 31 * result + (getProcedureId() != null ? getProcedureId().hashCode() : 0);
		result = 31 * result + (getParameterStableId() != null ? getParameterStableId().hashCode() : 0);
		result = 31 * result + (getParameterStableKey() != null ? getParameterStableKey().hashCode() : 0);
		result = 31 * result + (getParameterName() != null ? getParameterName().hashCode() : 0);
		result = 31 * result + (getParameterId() != null ? getParameterId().hashCode() : 0);
		result = 31 * result + (getColonyId() != null ? getColonyId().hashCode() : 0);
		result = 31 * result + (getMarkerSymbol() != null ? getMarkerSymbol().hashCode() : 0);
		result = 31 * result + (getMarkerAccessionId() != null ? getMarkerAccessionId().hashCode() : 0);
		result = 31 * result + (getAlleleSymbol() != null ? getAlleleSymbol().hashCode() : 0);
		result = 31 * result + (getAlleleName() != null ? getAlleleName().hashCode() : 0);
		result = 31 * result + (getAlleleAccessionId() != null ? getAlleleAccessionId().hashCode() : 0);
		result = 31 * result + (getStrainName() != null ? getStrainName().hashCode() : 0);
		result = 31 * result + (getStrainAccessionId() != null ? getStrainAccessionId().hashCode() : 0);
		result = 31 * result + (getGeneticBackground() != null ? getGeneticBackground().hashCode() : 0);
		result = 31 * result + (getSex() != null ? getSex().hashCode() : 0);
		result = 31 * result + (getZygosity() != null ? getZygosity().hashCode() : 0);
		result = 31 * result + (getProductionCenter() != null ? getProductionCenter().hashCode() : 0 );
		result = 31 * result + (getControlSelectionMethod() != null ? getControlSelectionMethod().hashCode() : 0);
		result = 31 * result + (getDependentVariable() != null ? getDependentVariable().hashCode() : 0);
		result = 31 * result + (getMetadataGroup() != null ? getMetadataGroup().hashCode() : 0);
		result = 31 * result + (getControlBiologicalModelId() != null ? getControlBiologicalModelId().hashCode() : 0);
		result = 31 * result + (getMutantBiologicalModelId() != null ? getMutantBiologicalModelId().hashCode() : 0);
		result = 31 * result + (getMaleControlCount() != null ? getMaleControlCount().hashCode() : 0);
		result = 31 * result + (getMaleMutantCount() != null ? getMaleMutantCount().hashCode() : 0);
		result = 31 * result + (getFemaleControlCount() != null ? getFemaleControlCount().hashCode() : 0);
		result = 31 * result + (getFemaleMutantCount() != null ? getFemaleMutantCount().hashCode() : 0);
		result = 31 * result + (getFemaleMutantMean() != null ? getFemaleMutantMean().hashCode() : 0);
		result = 31 * result + (getFemaleControlMean() != null ? getFemaleControlMean().hashCode() : 0);
		result = 31 * result + (getMaleMutantMean() != null ? getMaleMutantMean().hashCode() : 0);
		result = 31 * result + (getMaleControlMean() != null ? getMaleControlMean().hashCode() : 0);
		result = 31 * result + (getWorkflow() != null ? getWorkflow().hashCode() : 0);
		result = 31 * result + (getStatisticalMethod() != null ? getStatisticalMethod().hashCode() : 0);
		result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
		result = 31 * result + (getAdditionalInformation() != null ? getAdditionalInformation().hashCode() : 0);
		result = 31 * result + (getRawOutput() != null ? getRawOutput().hashCode() : 0);
		result = 31 * result + (getpValue() != null ? getpValue().hashCode() : 0);
		result = 31 * result + (getEffectSize() != null ? getEffectSize().hashCode() : 0);
		result = 31 * result + (getGenotypePvalueLowVsNormalHigh() != null ? getGenotypePvalueLowVsNormalHigh().hashCode() : 0);
		result = 31 * result + (getGenotypePvalueLowNormalVsHigh() != null ? getGenotypePvalueLowNormalVsHigh().hashCode() : 0);
		result = 31 * result + (getGenotypeEffectSizeLowVsNormalHigh() != null ? getGenotypeEffectSizeLowVsNormalHigh().hashCode() : 0);
		result = 31 * result + (getGenotypeEffectSizeLowNormalVsHigh() != null ? getGenotypeEffectSizeLowNormalVsHigh().hashCode() : 0);
		result = 31 * result + (getFemalePvalueLowVsNormalHigh() != null ? getFemalePvalueLowVsNormalHigh().hashCode() : 0);
		result = 31 * result + (getFemalePvalueLowNormalVsHigh() != null ? getFemalePvalueLowNormalVsHigh().hashCode() : 0);
		result = 31 * result + (getFemaleEffectSizeLowVsNormalHigh() != null ? getFemaleEffectSizeLowVsNormalHigh().hashCode() : 0);
		result = 31 * result + (getFemaleEffectSizeLowNormalVsHigh() != null ? getFemaleEffectSizeLowNormalVsHigh().hashCode() : 0);
		result = 31 * result + (getMalePvalueLowVsNormalHigh() != null ? getMalePvalueLowVsNormalHigh().hashCode() : 0);
		result = 31 * result + (getMalePvalueLowNormalVsHigh() != null ? getMalePvalueLowNormalVsHigh().hashCode() : 0);
		result = 31 * result + (getMaleEffectSizeLowVsNormalHigh() != null ? getMaleEffectSizeLowVsNormalHigh().hashCode() : 0);
		result = 31 * result + (getMaleEffectSizeLowNormalVsHigh() != null ? getMaleEffectSizeLowNormalVsHigh().hashCode() : 0);
		result = 31 * result + (getCategories() != null ? getCategories().hashCode() : 0);
		result = 31 * result + (getCategoricalPValue() != null ? getCategoricalPValue().hashCode() : 0);
		result = 31 * result + (getCategoricalEffectSize() != null ? getCategoricalEffectSize().hashCode() : 0);
		result = 31 * result + (getBatchSignificant() != null ? getBatchSignificant().hashCode() : 0);
		result = 31 * result + (getVarianceSignificant() != null ? getVarianceSignificant().hashCode() : 0);
		result = 31 * result + (getNullTestPValue() != null ? getNullTestPValue().hashCode() : 0);
		result = 31 * result + (getGenotypeEffectPValue() != null ? getGenotypeEffectPValue().hashCode() : 0);
		result = 31 * result + (getGenotypeEffectStderrEstimate() != null ? getGenotypeEffectStderrEstimate().hashCode() : 0);
		result = 31 * result + (getGenotypeEffectParameterEstimate() != null ? getGenotypeEffectParameterEstimate().hashCode() : 0);
		result = 31 * result + (getFemalePercentageChange() != null ? getFemalePercentageChange().hashCode() : 0);
		result = 31 * result + (getMalePercentageChange() != null ? getMalePercentageChange().hashCode() : 0);
		result = 31 * result + (getSexEffectPValue() != null ? getSexEffectPValue().hashCode() : 0);
		result = 31 * result + (getSexEffectStderrEstimate() != null ? getSexEffectStderrEstimate().hashCode() : 0);
		result = 31 * result + (getSexEffectParameterEstimate() != null ? getSexEffectParameterEstimate().hashCode() : 0);
		result = 31 * result + (getWeightEffectPValue() != null ? getWeightEffectPValue().hashCode() : 0);
		result = 31 * result + (getWeightEffectStderrEstimate() != null ? getWeightEffectStderrEstimate().hashCode() : 0);
		result = 31 * result + (getWeightEffectParameterEstimate() != null ? getWeightEffectParameterEstimate().hashCode() : 0);
		result = 31 * result + (getGroup1Genotype() != null ? getGroup1Genotype().hashCode() : 0);
		result = 31 * result + (getGroup1ResidualsNormalityTest() != null ? getGroup1ResidualsNormalityTest().hashCode() : 0);
		result = 31 * result + (getGroup2Genotype() != null ? getGroup2Genotype().hashCode() : 0);
		result = 31 * result + (getGroup2ResidualsNormalityTest() != null ? getGroup2ResidualsNormalityTest().hashCode() : 0);
		result = 31 * result + (getBlupsTest() != null ? getBlupsTest().hashCode() : 0);
		result = 31 * result + (getRotatedResidualsTest() != null ? getRotatedResidualsTest().hashCode() : 0);
		result = 31 * result + (getInterceptEstimate() != null ? getInterceptEstimate().hashCode() : 0);
		result = 31 * result + (getInterceptEstimateStderrEstimate() != null ? getInterceptEstimateStderrEstimate().hashCode() : 0);
		result = 31 * result + (getInteractionSignificant() != null ? getInteractionSignificant().hashCode() : 0);
		result = 31 * result + (getInteractionEffectPValue() != null ? getInteractionEffectPValue().hashCode() : 0);
		result = 31 * result + (getFemaleKoEffectPValue() != null ? getFemaleKoEffectPValue().hashCode() : 0);
		result = 31 * result + (getFemaleKoEffectStderrEstimate() != null ? getFemaleKoEffectStderrEstimate().hashCode() : 0);
		result = 31 * result + (getFemaleKoParameterEstimate() != null ? getFemaleKoParameterEstimate().hashCode() : 0);
		result = 31 * result + (getMaleKoEffectPValue() != null ? getMaleKoEffectPValue().hashCode() : 0);
		result = 31 * result + (getMaleKoEffectStderrEstimate() != null ? getMaleKoEffectStderrEstimate().hashCode() : 0);
		result = 31 * result + (getMaleKoParameterEstimate() != null ? getMaleKoParameterEstimate().hashCode() : 0);
		result = 31 * result + (getClassificationTag() != null ? getClassificationTag().hashCode() : 0);
		result = 31 * result + (getExternalDbId() != null ? getExternalDbId().hashCode() : 0);
		result = 31 * result + (getOrganisationId() != null ? getOrganisationId().hashCode() : 0);
		result = 31 * result + (getPhenotypingCenterId() != null ? getPhenotypingCenterId().hashCode() : 0);
		result = 31 * result + (getProjectId() != null ? getProjectId().hashCode() : 0);
		result = 31 * result + (getSignificant() != null ? getSignificant().hashCode() : 0);
        result = 31 * result + (lifeStageAcc != null ? lifeStageAcc.hashCode() : 0);
        result = 31 * result + (lifeStageName != null ? lifeStageName.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "StatisticalResultDTO{" +
			"docId='" + docId + '\'' +
			", dbId=" + dbId +
			", anatomyTermId=" + anatomyTermId +
			", anatomyTermName=" + anatomyTermName +
			", intermediateAnatomyTermId=" + intermediateAnatomyTermId +
			", intermediateAnatomyTermName=" + intermediateAnatomyTermName +
			", topLevelAnatomyTermId=" + topLevelAnatomyTermId +
			", topLevelAnatomyTermName=" + topLevelAnatomyTermName +
			", phenotypeSex=" + phenotypeSex +
			", dataType='" + dataType + '\'' +
			", mpTermIdOptions=" + mpTermIdOptions +
			", mpTermNameOptions=" + mpTermNameOptions +
			", mpTermId='" + mpTermId + '\'' +
			", mpTermName='" + mpTermName + '\'' +
			", topLevelMpTermId=" + topLevelMpTermId +
			", topLevelMpTermName=" + topLevelMpTermName +
			", intermediateMpTermId=" + intermediateMpTermId +
			", intermediateMpTermName=" + intermediateMpTermName +
			", maleMpTermId='" + maleMpTermId + '\'' +
			", maleMpTermName='" + maleMpTermName + '\'' +
			", maleTopLevelMpTermId=" + maleTopLevelMpTermId +
			", maleTopLevelMpTermName=" + maleTopLevelMpTermName +
			", maleIntermediateMpTermId=" + maleIntermediateMpTermId +
			", maleIntermediateMpTermName=" + maleIntermediateMpTermName +
			", femaleMpTermId='" + femaleMpTermId + '\'' +
			", femaleMpTermName='" + femaleMpTermName + '\'' +
			", femaleTopLevelMpTermId=" + femaleTopLevelMpTermId +
			", femaleTopLevelMpTermName=" + femaleTopLevelMpTermName +
			", femaleIntermediateMpTermId=" + femaleIntermediateMpTermId +
			", femaleIntermediateMpTermName=" + femaleIntermediateMpTermName +
			", resourceName='" + resourceName + '\'' +
			", resourceFullname='" + resourceFullname + '\'' +
			", resourceId=" + resourceId +
			", projectName='" + projectName + '\'' +
			", phenotypingCenter='" + phenotypingCenter + '\'' +
			", pipelineStableId='" + pipelineStableId + '\'' +
			", pipelineStableKey=" + pipelineStableKey +
			", pipelineName='" + pipelineName + '\'' +
			", pipelineId=" + pipelineId +
			", procedureStableId='" + procedureStableId + '\'' +
			", procedureStableKey=" + procedureStableKey +
			", procedureName='" + procedureName + '\'' +
			", procedureId=" + procedureId +
			", parameterStableId='" + parameterStableId + '\'' +
			", parameterStableKey=" + parameterStableKey +
			", parameterName='" + parameterName + '\'' +
			", parameterId=" + parameterId +
			", colonyId='" + colonyId + '\'' +
			", markerSymbol='" + markerSymbol + '\'' +
			", markerAccessionId='" + markerAccessionId + '\'' +
			", alleleSymbol='" + alleleSymbol + '\'' +
			", alleleName='" + alleleName + '\'' +
			", alleleAccessionId='" + alleleAccessionId + '\'' +
			", strainName='" + strainName + '\'' +
			", strainAccessionId='" + strainAccessionId + '\'' +
			", geneticBackground='" + geneticBackground + '\'' +
			", sex='" + sex + '\'' +
			", zygosity='" + zygosity + '\'' +
			", productionCenter='" + productionCenter + '\'' +
			", controlSelectionMethod='" + controlSelectionMethod + '\'' +
			", dependentVariable='" + dependentVariable + '\'' +
			", metadataGroup='" + metadataGroup + '\'' +
			", controlBiologicalModelId=" + controlBiologicalModelId +
			", mutantBiologicalModelId=" + mutantBiologicalModelId +
			", maleControlCount=" + maleControlCount +
			", maleMutantCount=" + maleMutantCount +
			", femaleControlCount=" + femaleControlCount +
			", femaleMutantCount=" + femaleMutantCount +
			", femaleMutantMean=" + femaleMutantMean +
			", femaleControlMean=" + femaleControlMean +
			", maleMutantMean=" + maleMutantMean +
			", maleControlMean=" + maleControlMean +
			", workflow='" + workflow + '\'' +
			", statisticalMethod='" + statisticalMethod + '\'' +
			", status='" + status + '\'' +
			", additionalInformation='" + additionalInformation + '\'' +
			", rawOutput='" + rawOutput + '\'' +
			", pValue=" + pValue +
			", effectSize=" + effectSize +
			", genotypePvalueLowVsNormalHigh=" + genotypePvalueLowVsNormalHigh +
			", genotypePvalueLowNormalVsHigh=" + genotypePvalueLowNormalVsHigh +
			", genotypeEffectSizeLowVsNormalHigh=" + genotypeEffectSizeLowVsNormalHigh +
			", genotypeEffectSizeLowNormalVsHigh=" + genotypeEffectSizeLowNormalVsHigh +
			", femalePvalueLowVsNormalHigh=" + femalePvalueLowVsNormalHigh +
			", femalePvalueLowNormalVsHigh=" + femalePvalueLowNormalVsHigh +
			", femaleEffectSizeLowVsNormalHigh=" + femaleEffectSizeLowVsNormalHigh +
			", femaleEffectSizeLowNormalVsHigh=" + femaleEffectSizeLowNormalVsHigh +
			", malePvalueLowVsNormalHigh=" + malePvalueLowVsNormalHigh +
			", malePvalueLowNormalVsHigh=" + malePvalueLowNormalVsHigh +
			", maleEffectSizeLowVsNormalHigh=" + maleEffectSizeLowVsNormalHigh +
			", maleEffectSizeLowNormalVsHigh=" + maleEffectSizeLowNormalVsHigh +
			", categories=" + categories +
			", categoricalPValue=" + categoricalPValue +
			", categoricalEffectSize=" + categoricalEffectSize +
			", batchSignificant=" + batchSignificant +
			", varianceSignificant=" + varianceSignificant +
			", nullTestPValue=" + nullTestPValue +
			", genotypeEffectPValue=" + genotypeEffectPValue +
			", genotypeEffectStderrEstimate=" + genotypeEffectStderrEstimate +
			", genotypeEffectParameterEstimate=" + genotypeEffectParameterEstimate +
			", femalePercentageChange='" + femalePercentageChange + '\'' +
			", malePercentageChange='" + malePercentageChange + '\'' +
			", sexEffectPValue=" + sexEffectPValue +
			", sexEffectStderrEstimate=" + sexEffectStderrEstimate +
			", sexEffectParameterEstimate=" + sexEffectParameterEstimate +
			", weightEffectPValue=" + weightEffectPValue +
			", weightEffectStderrEstimate=" + weightEffectStderrEstimate +
			", weightEffectParameterEstimate=" + weightEffectParameterEstimate +
			", group1Genotype='" + group1Genotype + '\'' +
			", group1ResidualsNormalityTest=" + group1ResidualsNormalityTest +
			", group2Genotype='" + group2Genotype + '\'' +
			", group2ResidualsNormalityTest=" + group2ResidualsNormalityTest +
			", blupsTest=" + blupsTest +
			", rotatedResidualsTest=" + rotatedResidualsTest +
			", interceptEstimate=" + interceptEstimate +
			", interceptEstimateStderrEstimate=" + interceptEstimateStderrEstimate +
			", interactionSignificant=" + interactionSignificant +
			", interactionEffectPValue=" + interactionEffectPValue +
			", femaleKoEffectPValue=" + femaleKoEffectPValue +
			", femaleKoEffectStderrEstimate=" + femaleKoEffectStderrEstimate +
			", femaleKoParameterEstimate=" + femaleKoParameterEstimate +
			", maleKoEffectPValue=" + maleKoEffectPValue +
			", maleKoEffectStderrEstimate=" + maleKoEffectStderrEstimate +
			", maleKoParameterEstimate=" + maleKoParameterEstimate +
			", classificationTag='" + classificationTag + '\'' +
			", externalDbId=" + externalDbId +
			", organisationId=" + organisationId +
			", phenotypingCenterId=" + phenotypingCenterId +
			", projectId=" + projectId +
			", significant=" + significant +
            ", lifeStageAcc='" + lifeStageAcc + '\'' +
            ", lifeStageName='" + lifeStageName + '\'' +
			'}';
	}
}
