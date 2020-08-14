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

import lombok.*;
import org.apache.solr.client.solrj.beans.Field;
import org.mousephenotype.cda.enumerations.SignificantType;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Data
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

	// Required for the webapp to display the correct classification tag
	public SignificantType getSignificantType() {
        return SignificantType.getValue(getClassificationTag(), significant);
    }

	@Id
    @Field(DOCUMENT_ID)
    private String docId;

//    @Field(DB_ID)
//    private Long dbId;

    @Field(ANATOMY_TERM_ID)
    List<String> anatomyTermId;

    @Field(ANATOMY_TERM_NAME)
    List<String> anatomyTermName;

    @Field(INTERMEDIATE_ANATOMY_TERM_ID)
    List<String> intermediateAnatomyTermId;

    @Field(INTERMEDIATE_ANATOMY_TERM_NAME)
    List<String> intermediateAnatomyTermName;

//    @Field(TOP_LEVEL_ANATOMY_TERM_ID)
//    List<String> topLevelAnatomyTermId;
//
//    @Field(TOP_LEVEL_ANATOMY_TERM_NAME)
//    List<String> topLevelAnatomyTermName;

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

//    @Field(RESOURCE_ID)
//    private Long resourceId;

    @Field(PROJECT_NAME)
    private List<String> projectName;

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
    private List<String> procedureStableId;

    @Field(PROCEDURE_STABLE_KEY)
    private List<Long> procedureStableKey;

    @Field(PROCEDURE_NAME)
    private String procedureName;

    @Field(PROCEDURE_ID)
    private List<Long>procedureId;

    @Field(PARAMETER_STABLE_ID)
    private String parameterStableId;

    @Field(PARAMETER_STABLE_KEY)
    private List<Long> parameterStableKey;

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

    @Field(P_VALUE)
    private Double pValue;

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


    public void addTopLevelMpTermId(Collection<String> topLevelMpTermId) {
        if (topLevelMpTermId!= null){
            if (this.topLevelMpTermId == null) {
                this.topLevelMpTermId = new ArrayList<>();
            }
            this.topLevelMpTermId.addAll(topLevelMpTermId);
        }
    }

    public void addTopLevelMpTermName(Collection<String> topLevelMpTermName) {
        if (topLevelMpTermName != null) {
            if (this.topLevelMpTermName == null) {
                this.topLevelMpTermName = new ArrayList<>();
            }
            this.topLevelMpTermName.addAll(topLevelMpTermName);
        }
    }


    public String setLifeStageName() {

        return lifeStageName;
    }


    public void addIntermediateMpTermId(Collection<String> intermediateMpTermId) {
        if ( intermediateMpTermId != null) {
            if (this.intermediateMpTermId == null) {
                this.intermediateMpTermId = new ArrayList<>();
            }
            this.intermediateMpTermId.addAll(intermediateMpTermId);
        }
    }


    public void addIntermediateMpTermName(Collection<String> intermediateMpTermName) {
        if ( intermediateMpTermName != null) {
            if (this.intermediateMpTermName == null) {
                this.intermediateMpTermName = new ArrayList<>();
            }
            this.intermediateMpTermName.addAll(intermediateMpTermName);
        }
    }


    public void addMaleTopLevelMpTermId(Collection<String> maleTopLevelMpTermId) {
        if (maleTopLevelMpTermId != null) {
            if (this.maleTopLevelMpTermId == null){
                this.maleTopLevelMpTermId = new ArrayList<>();
            }
            this.maleTopLevelMpTermId.addAll(maleTopLevelMpTermId);
        }
    }


    public void addMaleTopLevelMpTermName(Collection<String> maleTopLevelMpTermName) {
        if (maleTopLevelMpTermName != null) {
            if (this.maleTopLevelMpTermName == null){
                this.maleTopLevelMpTermName = new ArrayList<>();
            }
            this.maleTopLevelMpTermName.addAll(maleTopLevelMpTermName);
        }
    }


    public void addMaleIntermediateMpTermId(Collection<String> maleIntermediateMpTermId) {
        if (maleIntermediateMpTermId != null) {
            if (this.maleIntermediateMpTermId == null){
                this.maleIntermediateMpTermId = new ArrayList<>();
            }
            this.maleIntermediateMpTermId.addAll(maleIntermediateMpTermId);
        }
    }


    public void addMaleIntermediateMpTermName(Collection<String> maleIntermediateMpTermName) {

        if (maleIntermediateMpTermName != null) {
            if (this.maleIntermediateMpTermName == null){
                this.maleIntermediateMpTermName = new ArrayList<>();
            }
            this.maleIntermediateMpTermName.addAll(maleIntermediateMpTermName);
        }
    }


    public void addFemaleTopLevelMpTermId(Collection<String> femaleTopLevelMpTermId) {

        if (femaleTopLevelMpTermId != null) {
            if (this.femaleTopLevelMpTermId == null) {
                this.femaleTopLevelMpTermId = new ArrayList<>();
            }
            this.femaleTopLevelMpTermId.addAll(femaleTopLevelMpTermId);
        }
    }


    public void addFemaleTopLevelMpTermName(Collection<String> femaleTopLevelMpTermName) {

        if (femaleTopLevelMpTermName != null) {
            if (this.femaleTopLevelMpTermName == null) {
                this.femaleTopLevelMpTermName = new ArrayList<>();
            }
            this.femaleTopLevelMpTermName.addAll(femaleTopLevelMpTermName);
        }
    }


    public void addFemaleIntermediateMpTermId(Collection<String> femaleIntermediateMpTermId) {

        if (femaleIntermediateMpTermId != null) {
            if (this.femaleIntermediateMpTermId == null){
                this.femaleIntermediateMpTermId = new ArrayList<>();
            }
            this.femaleIntermediateMpTermId.addAll(femaleIntermediateMpTermId);
        }
    }


    public void addFemaleIntermediateMpTermName(Collection<String> femaleIntermediateMpTermName) {

        if (femaleIntermediateMpTermName != null) {
            if (this.femaleIntermediateMpTermName == null){
                this.femaleIntermediateMpTermName = new ArrayList<>();
            }
            this.femaleIntermediateMpTermName.addAll(femaleIntermediateMpTermName);
        }
    }


    public void addAnatomyTermName(String anatomyTermName){
		if (this.anatomyTermName == null){
			this.anatomyTermName = new ArrayList<>();
		}
		this.anatomyTermName.add(anatomyTermName);
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


}
