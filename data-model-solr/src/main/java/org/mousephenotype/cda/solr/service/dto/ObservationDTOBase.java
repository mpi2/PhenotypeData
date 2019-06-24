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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ObservationDTOBase {

	public final static String ID = "id";
	public final static String DATASOURCE_ID = "datasource_id";
	public final static String DATASOURCE_NAME = "datasource_name";
	public final static String PROJECT_ID = "project_id";
	public final static String PROJECT_NAME = "project_name";
	public final static String PHENOTYPING_CENTER = "phenotyping_center";
	public final static String PHENOTYPING_CENTER_ID = "phenotyping_center_id";
	public final static String PRODUCTION_CENTER = "production_center";
	public final static String PRODUCTION_CENTER_ID = "production_center_id";


	public final static String SPECIMEN_PROJECT_NAME = "specimen_project_name";
	public final static String SPECIMEN_PROJECT_ID = "specimen_project_id";


	public final static String LITTER_ID = "litter_id";
	public final static String GENE_ACCESSION_ID = "gene_accession_id";
	public final static String GENE_SYMBOL = "gene_symbol";
	public final static String ALLELE_ACCESSION_ID = "allele_accession_id";
	public final static String ALLELE_SYMBOL = "allele_symbol";
	public final static String ZYGOSITY = "zygosity";
	public final static String SEX = "sex";
	public final static String BIOLOGICAL_MODEL_ID = "biological_model_id";
	public final static String BIOLOGICAL_SAMPLE_ID = "biological_sample_id";
	public final static String BIOLOGICAL_SAMPLE_GROUP = "biological_sample_group";
	public final static String STRAIN_ACCESSION_ID = "strain_accession_id";
	public final static String STRAIN_NAME = "strain_name";
	public final static String GENETIC_BACKGROUND = "genetic_background";
	public final static String ALLELIC_COMPOSITION = "allelic_composition";
	public final static String PIPELINE_NAME = "pipeline_name";
	public final static String PIPELINE_ID = "pipeline_id";
	public final static String PIPELINE_STABLE_ID = "pipeline_stable_id";
	public final static String PROCEDURE_ID = "procedure_id";
	public final static String PROCEDURE_NAME = "procedure_name";
	public final static String PROCEDURE_STABLE_ID = "procedure_stable_id";
	public final static String PROCEDURE_GROUP = "procedure_group";
	public final static String PARAMETER_ID = "parameter_id";
	public final static String PARAMETER_NAME = "parameter_name";
	public final static String PARAMETER_STABLE_ID = "parameter_stable_id";
	public final static String DATA_TYPE = "data_type";
	public final static String EXPERIMENT_ID = "experiment_id";
	public final static String EXPERIMENT_SOURCE_ID = "experiment_source_id";
	public final static String OBSERVATION_TYPE = "observation_type";
	public final static String COLONY_ID = "colony_id";
	public final static String POPULATION_ID = "population_id";
	public final static String EXTERNAL_SAMPLE_ID = "external_sample_id";
	public final static String DATA_POINT = "data_point";
	public final static String ORDER_INDEX = "order_index";
	public final static String DIMENSION = "dimension";
	public final static String TIME_POINT = "time_point";
	public final static String DISCRETE_POINT = "discrete_point";
	public final static String CATEGORY = "category";
	public final static String VALUE = "value";
	public final static String METADATA = "metadata";
	public final static String METADATA_GROUP = "metadata_group";
	public final static String DOWNLOAD_FILE_PATH = "download_file_path";
	public final static String FILE_TYPE = "file_type";
	public final static String PARAMETER_ASSOCIATION_STABLE_ID = "parameter_association_stable_id";
	public final static String PARAMETER_ASSOCIATION_SEQUENCE_ID = "parameter_association_sequence_id";
	public final static String PARAMETER_ASSOCIATION_DIM_ID = "parameter_association_dim_id";
	public final static String PARAMETER_ASSOCIATION_NAME = "parameter_association_name";
	public final static String PARAMETER_ASSOCIATION_VALUE = "parameter_association_value";
	public final static String WEIGHT_PARAMETER_STABLE_ID = "weight_parameter_stable_id";
	public final static String WEIGHT_DAYS_OLD = "weight_days_old";
	public final static String WEIGHT = "weight";
	public static final String DEVELOPMENTAL_STAGE_ACCESSION = "developmental_stage_acc";
	public static final String DEVELOPMENTAL_STAGE_NAME = "developmental_stage_name";

	public final static String DATE_OF_BIRTH = "date_of_birth";
	public final static String DATE_OF_EXPERIMENT = "date_of_experiment";
	public final static String WEIGHT_DATE = "weight_date";
	public static final String TEXT_VALUE = "text_value";
	public static final String SUB_TERM_NAME = "sub_term_name";
	public static final String SUB_TERM_ID = "sub_term_id";
	public static final String SUB_TERM_DESCRIPTION = "sub_term_description";
	public static final String SEQUENCE_ID = "sequence_id";

	public static final String ANATOMY_ID = "anatomy_id";
	public static final String ANATOMY_TERM = "anatomy_term";
	public static final String ANATOMY_TERM_SYNONYM = "anatomy_term_synonym";
	public static final String ANATOMY_ID_TERM = "anatomy_id_term";
	public static final String TOP_LEVEL_ANATOMY_ID = "top_level_anatomy_id";
	public static final String TOP_LEVEL_ANATOMY_TERM = "top_level_anatomy_term";
	public static final String TOP_LEVEL_ANATOMY_TERM_SYNONYM = "top_level_anatomy_term_synonym";
	public static final String SELECTED_TOP_LEVEL_ANATOMY_ID = "selected_top_level_anatomy_id";
	public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM = "selected_top_level_anatomy_term";
	public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM = "selected_top_level_anatomy_term_synonym";
	public static final String INTERMEDIATE_ANATOMY_ID = "intermediate_anatomy_id";
	public static final String INTERMEDIATE_ANATOMY_TERM = "intermediate_anatomy_term";
	public static final String INTERMEDIATE_ANATOMY_TERM_SYNONYM = "intermediate_anatomy_term_synonym";
	
	public static final String AGE_IN_DAYS= "age_in_days";
	public static final String AGE_IN_WEEKS= "age_in_weeks";


	@Field(ID)
    protected String id;

    @Field(DATASOURCE_ID)
    protected Integer dataSourceId;

    @Field(DATASOURCE_NAME)
    protected String dataSourceName;

    @Field(PROJECT_ID)
    protected Integer projectId;

    @Field(PROJECT_NAME)
    protected String projectName;

    @Field(PIPELINE_NAME)
    protected String pipelineName;

    @Field(PIPELINE_STABLE_ID)
    protected String pipelineStableId;

    @Field(PROCEDURE_STABLE_ID)
    protected String procedureStableId;

	@Field(PROCEDURE_GROUP)
	protected String procedureGroup;

    @Field(PARAMETER_STABLE_ID)
    protected String parameterStableId;

    @Field(DATA_TYPE)
    protected String dataType;

    @Field(PIPELINE_ID)
    protected Integer pipelineId;

    @Field(PROCEDURE_ID)
    protected Integer procedureId;

    @Field(PARAMETER_ID)
    protected Integer parameterId;

    @Field(STRAIN_ACCESSION_ID)
    protected String strainAccessionId;

    @Field(STRAIN_NAME)
    protected String strainName;

	@Field(GENETIC_BACKGROUND)
	protected String geneticBackground;

	@Field(ALLELIC_COMPOSITION)
	protected String allelicComposition;

	@Field(EXPERIMENT_SOURCE_ID)
    protected String experimentSourceId;

    @Field(GENE_SYMBOL)
    protected String geneSymbol;

    @Field(GENE_ACCESSION_ID)
    protected String geneAccession;

    @Field(EXPERIMENT_ID)
    protected Integer experimentId;

    @Field(PHENOTYPING_CENTER_ID)
    protected Integer phenotypingCenterId;

    @Field(PHENOTYPING_CENTER)
    protected String phenotypingCenter;




	@Field(SPECIMEN_PROJECT_ID)
	protected Integer specimenProjectId;

	@Field(SPECIMEN_PROJECT_NAME)
	protected String specimenProjectName;




	@Field(PRODUCTION_CENTER_ID)
	protected Integer productionCenterId;

	@Field(PRODUCTION_CENTER)
	protected String productionCenter;

	@Field(LITTER_ID)
	protected String litterId;

	@Field(OBSERVATION_TYPE)
    protected String observationType;

    @Field(COLONY_ID)
    protected String colonyId;

    @Field(BIOLOGICAL_SAMPLE_ID)
    protected Integer biologicalSampleId;

    @Field(BIOLOGICAL_MODEL_ID)
    protected Integer biologicalModelId;

    @Field(ZYGOSITY)
    protected String zygosity;

    @Field(SEX)
    protected String sex;

    @Field(BIOLOGICAL_SAMPLE_GROUP)
    protected String group;

    @Field(CATEGORY)
    protected String category;

    @Field(DATA_POINT)
    protected Float dataPoint;

    @Field(ORDER_INDEX)
    protected Integer orderIndex;

    @Field(DIMENSION)
    protected String dimension;

    @Field(TIME_POINT)
    protected String timePoint;

    @Field(DISCRETE_POINT)
    protected Float discretePoint;

    @Field(EXTERNAL_SAMPLE_ID)
    protected String externalSampleId;

    @Field(PARAMETER_NAME)
    protected String parameterName;

    @Field(PROCEDURE_NAME)
    protected String procedureName;

    @Field(METADATA_GROUP)
    protected String metadataGroup;

    @Field(METADATA)
    protected List<String> metadata;

    @Field(ALLELE_ACCESSION_ID)
    protected String alleleAccession;

    @Field(ALLELE_SYMBOL)
    protected String alleleSymbol;

	@Field(DOWNLOAD_FILE_PATH)
	protected String downloadFilePath;

	@Field(FILE_TYPE)
	protected String fileType;

    @Field(PARAMETER_ASSOCIATION_STABLE_ID)
    protected List<String> parameterAssociationStableId;

    @Field(PARAMETER_ASSOCIATION_SEQUENCE_ID)
    protected List<String> parameterAssociationSequenceId;

    @Field(PARAMETER_ASSOCIATION_DIM_ID)
    protected List<String> parameterAssociationDimId;

    @Field(PARAMETER_ASSOCIATION_NAME)
	protected List<String> parameterAssociationName;

    @Field(PARAMETER_ASSOCIATION_VALUE)
	protected List<String> parameterAssociationValue;

    @Field(WEIGHT_PARAMETER_STABLE_ID)
    protected String weightParameterStableId;

    @Field(WEIGHT_DAYS_OLD)
    protected Integer weightDaysOld;

    @Field(WEIGHT)
    protected Float weight;

    @Field(DEVELOPMENTAL_STAGE_ACCESSION)
	protected String developmentalStageAcc;

    @Field(DEVELOPMENTAL_STAGE_NAME)
   	protected String developmentalStageName;

    @Field(TEXT_VALUE)
	private String textValue;

	@Field(SUB_TERM_NAME)
	private List<String> subTermName;

	@Field(SUB_TERM_ID)
	private List<String> subTermId;

	@Field(SUB_TERM_DESCRIPTION)
	private List<String> subTermDescription;

	@Field(SEQUENCE_ID)
	private Integer sequenceId;

	@Field(ANATOMY_ID)
	List<String> anatomyId;

	@Field(ANATOMY_TERM)
	List<String> anatomyTerm;

	@Field(ANATOMY_ID_TERM)
	List<String> anatomyIdTerm;

	@Field(ANATOMY_TERM_SYNONYM)
	List<String> anatomyTermSynonym;

	@Field(INTERMEDIATE_ANATOMY_ID)
	List<String> intermediateAnatomyId;

	@Field(INTERMEDIATE_ANATOMY_TERM)
	List<String> intermediateAnatomyTerm;

	@Field(INTERMEDIATE_ANATOMY_TERM_SYNONYM)
	List<String> intermediateAnatomyTermSynonym;

	@Field(TOP_LEVEL_ANATOMY_ID)
	List<String> topLevelAnatomyId;

	@Field(TOP_LEVEL_ANATOMY_TERM)
	List<String> topLevelAnatomyTerm;

	@Field(TOP_LEVEL_ANATOMY_TERM_SYNONYM)
	List<String> topLevelAnatomyTermSynonym;

	@Field(SELECTED_TOP_LEVEL_ANATOMY_ID)
	List<String> selectedTopLevelAnatomyId;

	@Field(SELECTED_TOP_LEVEL_ANATOMY_TERM)
	List<String> selectedTopLevelAnatomyTerm;

	@Field(SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM)
	List<String> selectedTopLevelAnatomyTermSynonym;
	
	@Field(AGE_IN_DAYS)
	private Integer ageInDays;
	
	@Field(AGE_IN_WEEKS)
	private Integer ageInWeeks;
	


	public Integer getAgeInDays() {
		return ageInDays;
	}

	public void setAgeInDays(Integer ageInDays) {
		this.ageInDays = ageInDays;
	}

	public Integer getAgeInWeeks() {
		return ageInWeeks;
	}

	public void setAgeInWeeks(Integer ageInWeeks) {
		this.ageInWeeks = ageInWeeks;
	}

	public List<String> getAnatomyId() {
		return anatomyId;
	}

	public void setAnatomyId(List<String> anatomyId) {
		this.anatomyId = anatomyId;
	}

	public void addAnatomyId(List<String> anatomyId) {
		if (this.anatomyId == null) { this.anatomyId = new ArrayList<>();}
		this.anatomyId.addAll(anatomyId);
	}

	public List<String> getAnatomyTerm() {
		return anatomyTerm;
	}

	public void setAnatomyTerm(List<String> anatomyTerm) {
		this.anatomyTerm = anatomyTerm;
	}

	public void addAnatomyTerm(List<String> anatomyTerm) {

		if (this.anatomyTerm == null){
			this.anatomyTerm = new ArrayList<>();
		}

		this.anatomyTerm.addAll(anatomyTerm);
	}

	public List<String> getAnatomyIdTerm() {
		return anatomyIdTerm;
	}

	public void setAnatomyIdTerm(List<String> anatomyIdTerm) {
		this.anatomyIdTerm = anatomyIdTerm;
	}
	public void addAnatomyIdTerm(String anatomyIdTerm) {
		if (this.anatomyIdTerm == null){
			this.anatomyIdTerm = new ArrayList<>();
		}
		this.anatomyIdTerm.add(anatomyIdTerm);
	}

	public List<String> getAnatomyTermSynonym() {
		return anatomyTermSynonym;
	}

	public void setAnatomyTermSynonym(List<String> anatomyTermSynonym) {
		this.anatomyTermSynonym = anatomyTermSynonym;
	}

	public void addAnatomyTermSynonym(String anatomyTermSynonym) {
		if (this.anatomyTermSynonym == null){
			this.anatomyTermSynonym = new ArrayList<>();
		}
		this.anatomyTermSynonym.add(anatomyTermSynonym);
	}
	public void addAnatomyTermSynonym(Collection<String> anatomyTermSynonym, boolean uniqueOnly) {
		if (this.anatomyTermSynonym == null){
			this.anatomyTermSynonym = new ArrayList<>();
		}
		if (!uniqueOnly) {
			this.anatomyTermSynonym.addAll(anatomyTermSynonym);
		} else {
			this.anatomyTermSynonym = addUnique(this.anatomyTermSynonym, anatomyTermSynonym);
		}
	}
	public List<String> getIntermediateAnatomyId() {
		return intermediateAnatomyId;
	}

	public void setIntermediateAnatomyId(List<String> intermediateAnatomyId) {
		this.intermediateAnatomyId = intermediateAnatomyId;
	}

	public void addIntermediateAnatomyId(Collection<String> intermediateAnatomyId) {

		if (intermediateAnatomyId != null) {
			if (this.intermediateAnatomyId == null) {
				this.intermediateAnatomyId = new ArrayList<>();
			}

			this.intermediateAnatomyId.addAll(intermediateAnatomyId);
		}
	}

	private List<String> addUnique(List<String> toList, Collection<String> fromList){

		for (String o : fromList){
			if (!toList.contains(o)){
				toList.add(o);
			}
		}
		return toList;
	}

	public List<String> getIntermediateAnatomyTerm() {
		return intermediateAnatomyTerm;
	}

	public void setIntermediateAnatomyTerm(List<String> intermediateAnatomyTerm) {
		this.intermediateAnatomyTerm = intermediateAnatomyTerm;
	}

	public void addIntermediateAnatomyTerm(Collection<String> intermediateAnatomyTerm) {
		if (intermediateAnatomyTerm != null) {
			if (this.intermediateAnatomyTerm == null) {
				this.intermediateAnatomyTerm = new ArrayList<>();
			}
			this.intermediateAnatomyTerm.addAll(intermediateAnatomyTerm);
		}
	}

	public void addTopLevelAnatomyTerm(String topLevelAnatomyTerm) {
		if (this.topLevelAnatomyTerm == null){
			this.topLevelAnatomyTerm = new ArrayList<>();
		}
		this.topLevelAnatomyTerm.add(topLevelAnatomyTerm);
	}

	public void addTopLevelAnatomyId(String topLevelAnatomyId) {
		if (this.topLevelAnatomyId == null){
			this.topLevelAnatomyId = new ArrayList<>();
		}
		this.topLevelAnatomyId.add(topLevelAnatomyId);
	}

	public void addTopLevelAnatomySynonym(List<String> topLevelAnatomyTermSynonym, boolean uniqueOnly) {
		if (this.topLevelAnatomyTermSynonym == null){
			this.topLevelAnatomyTermSynonym = new ArrayList<>();
		}
		if (!uniqueOnly) {
			this.topLevelAnatomyTermSynonym.addAll(topLevelAnatomyTermSynonym);
		} else {
			this.topLevelAnatomyTermSynonym = addUnique(this.topLevelAnatomyTermSynonym, topLevelAnatomyTermSynonym);
		}
	}

	public List<String> getIntermediateAnatomyTermSynonym() {
		return intermediateAnatomyTermSynonym;
	}

	public void setIntermediateAnatomyTermSynonym(List<String> intermediateAnatomyTermSynonym) {
		this.intermediateAnatomyTermSynonym = intermediateAnatomyTermSynonym;
	}

	public void addIntermediateAnatomyTermSynonym(Collection<String> intermediateAnatomyTermSynonym, boolean uniqueOnly) {
		if (this.intermediateAnatomyTermSynonym == null){
			this.intermediateAnatomyTermSynonym = new ArrayList<>();
		}
		if (!uniqueOnly) {
			this.intermediateAnatomyTermSynonym.addAll(intermediateAnatomyTermSynonym);
		} else {
			this.intermediateAnatomyTermSynonym = addUnique(this.intermediateAnatomyTermSynonym, intermediateAnatomyTermSynonym);
		}
	}

	public List<String> getTopLevelAnatomyId() {
		return topLevelAnatomyId;
	}

	public void setTopLevelAnatomyId(List<String> topLevelAnatomyId) {
		this.topLevelAnatomyId = topLevelAnatomyId;
	}

	public List<String> getTopLevelAnatomyTerm() {
		return topLevelAnatomyTerm;
	}

	public void setTopLevelAnatomyTerm(List<String> topLevelAnatomyTerm) {
		this.topLevelAnatomyTerm = topLevelAnatomyTerm;
	}

	public List<String> getTopLevelAnatomyTermSynonym() {
		return topLevelAnatomyTermSynonym;
	}

	public void setTopLevelAnatomyTermSynonym(List<String> topLevelAnatomyTermSynonym) {
		this.topLevelAnatomyTermSynonym = topLevelAnatomyTermSynonym;
	}

	public List<String> getSelectedTopLevelAnatomyId() {
		return selectedTopLevelAnatomyId;
	}

	public void setSelectedTopLevelAnatomyId(List<String> selectedTopLevelAnatomyId) {
		this.selectedTopLevelAnatomyId = selectedTopLevelAnatomyId;
	}

	public List<String> getSelectedTopLevelAnatomyTerm() {
		return selectedTopLevelAnatomyTerm;
	}

	public void setSelectedTopLevelAnatomyTerm(List<String> selectedTopLevelAnatomyTerm) {
		this.selectedTopLevelAnatomyTerm = selectedTopLevelAnatomyTerm;
	}

	public List<String> getSelectedTopLevelAnatomyTermSynonym() {
		return selectedTopLevelAnatomyTermSynonym;
	}

	public void setSelectedTopLevelAnatomyTermSynonym(List<String> selectedTopLevelAnatomyTermSynonym) {
		this.selectedTopLevelAnatomyTermSynonym = selectedTopLevelAnatomyTermSynonym;
	}

	public void addSelectedTopLevelAnatomyTerm(Collection<String> selectedTopLevelAnatomyTerm, boolean uniqueOnly) {
		this.selectedTopLevelAnatomyTerm = add(this.selectedTopLevelAnatomyTerm, selectedTopLevelAnatomyTerm, uniqueOnly);
	}

	public void addSelectedTopLevelAnatomyId(Collection<String> selectedTopLevelAnatomyId, boolean uniqueOnly) {

		this.selectedTopLevelAnatomyId = add(this.selectedTopLevelAnatomyId, selectedTopLevelAnatomyId, uniqueOnly);
	}

	public void addSelectedTopLevelAnatomySynonyms(Collection<String> selectedTopLevelAnatomyTermSynonym, boolean uniqueOnly) {
		this.selectedTopLevelAnatomyTermSynonym = add(this.selectedTopLevelAnatomyTermSynonym, selectedTopLevelAnatomyTermSynonym, uniqueOnly);
	}

	public Integer getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(Integer sequenceId) {
		this.sequenceId = sequenceId;
	}

	public List<String> getSubTermName() {
		return subTermName;
	}

	public void setSubTermName(List<String> subTermName) {
		this.subTermName = subTermName;
	}

	public List<String> getSubTermId() {
		return subTermId;
	}

	public void addSubTermId(String id){
		if(this.subTermId==null){
			this.subTermId=new ArrayList<String>();
		}
		this.subTermId.add(id);
	}

	public void addSubTermName(String name){
		if(this.subTermName==null){
			this.subTermName=new ArrayList<String>();
		}
		this.subTermName.add(name);
	}

	public void addSubTermDescription(String description){
		if(this.subTermDescription==null){
			this.subTermDescription=new ArrayList<String>();
		}
		this.subTermDescription.add(description);
	}

	public void setSubTermId(List<String> subTermId) {
		this.subTermId = subTermId;
	}

	public List<String> getSubTermDescription() {
		return subTermDescription;
	}

	public void setSubTermDescription(List<String> subTermDescription) {
		this.subTermDescription = subTermDescription;
	}

	public void setTextValue(String textValue) {
		this.textValue=textValue;

	}

	public String getTextValue() {
		return textValue;
	}


	public List<String> getParameterAssociationValue() {
		return parameterAssociationValue;
	}


	public void setParameterAssociationValue(List<String> parameterAssociationValue) {
		this.parameterAssociationValue = parameterAssociationValue;
	}


	public List<String> getParameterAssociationName() {

		return parameterAssociationName;
	}


	public void setParameterAssociationName(List<String> parameterAssociationName) {

		this.parameterAssociationName = parameterAssociationName;
	}

	public void addParameterAssociationStableId(String id) {
        if(parameterAssociationStableId == null) {
            parameterAssociationStableId = new ArrayList<>();
        }
        parameterAssociationStableId.add(id);
    }

    public void addParameterAssociationName(String paramAssociationName) {

    	if(parameterAssociationName == null) {
    		parameterAssociationName = new ArrayList<String>();
        }
    	parameterAssociationName.add(paramAssociationName);

	}

    public void addParameterAssociationSequenceId(String id) {
        if(parameterAssociationSequenceId == null) {
            parameterAssociationSequenceId = new ArrayList<>();
        }
        parameterAssociationSequenceId.add(id);
    }

    public void addParameterAssociationDimId(String id) {
        if(parameterAssociationDimId == null) {
            parameterAssociationDimId = new ArrayList<>();
        }
        parameterAssociationDimId.add(id);
    }

    public HashSet<String> getDistinctParameterAssociationsValue(){
    	return new HashSet(parameterAssociationValue);
    }

    /**
     * helper methods
     *
     * @throws SQLException
     */


    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getProcedureName() {
        return this.procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getExternalSampleId() {
        return externalSampleId;
    }

    public void setExternalSampleId(String externalSampleId) {
        this.externalSampleId = externalSampleId;
    }


    public boolean isControl() {
        return this.group.equals("control");
    }

    public boolean isMutant() {
        return this.group.equals("experimental");
    }

    /**
     *
     * @return key uniquely identifying the group in which the ObservationDTO
     *         object is analysed. A concatenation of phenotyping center,
     *         strainAccessionId, allele, parameter, pipeline, zygosity, sex, metadata
     */

    public String getKey() {
        return "[allele: " + this.getAlleleAccession()
                + " , strainAccessionId :" + this.getStrain()
                + " , phenotyping center :" + this.getPhenotypingCenter()
                + " , parameter :" + this.getParameterStableId()
                + " , pipeline :" + this.getPipelineStableId()
                + " , zygosity :" + this.getZygosity()
                + " , metadata :" + this.getMetadataGroup()
                + " ]";
    }

	/**
	 * end helper methods
	 */
	@Override
	public String toString() {
		return "ObservationDTOBase{" +
				"id=" + id +
				", dataSourceId=" + dataSourceId +
				", dataSourceName='" + dataSourceName + '\'' +
				", projectId=" + projectId +
				", projectName='" + projectName + '\'' +
				", pipelineName='" + pipelineName + '\'' +
				", pipelineStableId='" + pipelineStableId + '\'' +
				", procedureStableId='" + procedureStableId + '\'' +
				", procedureGroup='" + procedureGroup + '\'' +
				", parameterStableId='" + parameterStableId + '\'' +
				", pipelineId=" + pipelineId +
				", procedureId=" + procedureId +
				", parameterId=" + parameterId +
				", strainAccessionId='" + strainAccessionId + '\'' +
				", strainName='" + strainName + '\'' +
				", geneticBackground='" + geneticBackground + '\'' +
				", experimentSourceId='" + experimentSourceId + '\'' +
				", geneSymbol='" + geneSymbol + '\'' +
				", geneAccession='" + geneAccession + '\'' +
				", experimentId=" + experimentId +
				", phenotypingCenterId=" + phenotypingCenterId +
				", phenotypingCenter='" + phenotypingCenter + '\'' +
				", specimenProjectName='" + specimenProjectName + '\'' +
				", specimenProjectId='" + specimenProjectId + '\'' +
				", productionCenterId=" + productionCenterId +
				", productionCenter='" + productionCenter + '\'' +
				", litterId='" + litterId + '\'' +
				", observationType='" + observationType + '\'' +
				", colonyId='" + colonyId + '\'' +
				", biologicalSampleId=" + biologicalSampleId +
				", biologicalModelId=" + biologicalModelId +
				", zygosity='" + zygosity + '\'' +
				", sex='" + sex + '\'' +
				", group='" + group + '\'' +
				", category='" + category + '\'' +
				", dataPoint=" + dataPoint +
				", orderIndex=" + orderIndex +
				", dimension='" + dimension + '\'' +
				", timePoint='" + timePoint + '\'' +
				", discretePoint=" + discretePoint +
				", externalSampleId='" + externalSampleId + '\'' +
				", parameterName='" + parameterName + '\'' +
				", procedureName='" + procedureName + '\'' +
				", metadataGroup='" + metadataGroup + '\'' +
				", metadata=" + metadata +
				", alleleAccession='" + alleleAccession + '\'' +
				", alleleSymbol='" + alleleSymbol + '\'' +
				", downloadFilePath='" + downloadFilePath + '\'' +
				", fileType='" + fileType + '\'' +
				", parameterAssociationStableId=" + parameterAssociationStableId +
				", parameterAssociationSequenceId=" + parameterAssociationSequenceId +
				", parameterAssociationDimId=" + parameterAssociationDimId +
				", parameterAssociationName=" + parameterAssociationName +
				", parameterAssociationValue=" + parameterAssociationValue +
				", weightParameterStableId='" + weightParameterStableId + '\'' +
				", weightDaysOld=" + weightDaysOld +
				", weight=" + weight +
				", developmentalStageAcc='" + developmentalStageAcc + '\'' +
				", developmentalStageName='" + developmentalStageName + '\'' +
				", textValue='" + textValue + '\'' +
				", subTermName=" + subTermName +
				", subTermId=" + subTermId +
				", subTermDescription=" + subTermDescription +
				", sequenceId=" + sequenceId +
				", anatomyId=" + anatomyId +
				", anatomyTerm=" + anatomyTerm +
				", anatomyIdTerm=" + anatomyIdTerm +
				", anatomyTermSynonym=" + anatomyTermSynonym +
				", intermediateAnatomyId=" + intermediateAnatomyId +
				", intermediateAnatomyTerm=" + intermediateAnatomyTerm +
				", intermediateAnatomyTermSynonym=" + intermediateAnatomyTermSynonym +
				", topLevelAnatomyId=" + topLevelAnatomyId +
				", topLevelAnatomyTerm=" + topLevelAnatomyTerm +
				", topLevelAnatomyTermSynonym=" + topLevelAnatomyTermSynonym +
				", selectedTopLevelAnatomyId=" + selectedTopLevelAnatomyId +
				", selectedTopLevelAnatomyTerm=" + selectedTopLevelAnatomyTerm +
				", selectedTopLevelAnatomyTermSynonym=" + selectedTopLevelAnatomyTermSynonym +
				'}';
	}

	/**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the pipelineName
     */
    public String getPipelineName() {
        return pipelineName;
    }

    /**
     * @param pipelineName
     *            the pipelineName to set
     */
    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    /**
     * @return the pipelineStableId
     */
    public String getPipelineStableId() {
        return pipelineStableId;
    }

    /**
     * @param pipelineStableId
     *            the pipelineStableId to set
     */
    public void setPipelineStableId(String pipelineStableId) {
        this.pipelineStableId = pipelineStableId;
    }

    /**
     * @return the procedureStableId
     */
    public String getProcedureStableId() {
        return procedureStableId;
    }

    /**
     * @param procedureStableId
     *            the procedureStableId to set
     */
    public void setProcedureStableId(String procedureStableId) {
        this.procedureStableId = procedureStableId;
    }


	public String getProcedureGroup() {

		return procedureGroup;
	}


	public void setProcedureGroup(String procedureGroup) {

		this.procedureGroup = procedureGroup;
	}


	/**
     * @return the parameterStableId
     */
    public String getParameterStableId() {
        return parameterStableId;
    }

    /**
     * @param parameterStableId
     *            the parameterStableId to set
     */
    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
     * @return the pipelineId
     */
    public Integer getPipelineId() {
        return pipelineId;
    }

    /**
     * @param pipelineId
     *            the pipelineId to set
     */
    public void setPipelineId(Integer pipelineId) {
        this.pipelineId = pipelineId;
    }

    /**
     * @return the procedureId
     */
    public Integer getProcedureId() {
        return procedureId;
    }

    /**
     * @param procedureId
     *            the procedureId to set
     */
    public void setProcedureId(Integer procedureId) {
        this.procedureId = procedureId;
    }

    /**
     * @return the parameterId
     */
    public Integer getParameterId() {
        return parameterId;
    }

    /**
     * @param parameterId
     *            the parameterId to set
     */
    public void setParameterId(Integer parameterId) {
        this.parameterId = parameterId;
    }

    /**
     * @return the strainAccessionId
     */
    public String getStrain() {
        return strainAccessionId;
    }

    /**
     * @param strainAccessionId
     *            the strainAccessionId to set
     */
    public void setStrain(String strainAccessionId) {
        this.strainAccessionId = strainAccessionId;
    }

    /**
     * @return the experimentSourceId
     */
    public String getExperimentSourceId() {
        return experimentSourceId;
    }

    /**
     * @param experimentSourceId
     *            the experimentSourceId to set
     */
    public void setExperimentSourceId(String experimentSourceId) {
        this.experimentSourceId = experimentSourceId;
    }

    /**
     * @return the geneSymbol
     */
    public String getGeneSymbol() {
        return geneSymbol;
    }

    /**
     * @param geneSymbol
     *            the geneSymbol to set
     */
    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    /**
     * @return the geneAccession
     */
    public String getGeneAccession() {
        return geneAccession;
    }

    /**
     * @param geneAccession
     *            the geneAccession to set
     */
    public void setGeneAccession(String geneAccession) {
        this.geneAccession = geneAccession;
    }

    /**
     * @return the experimentId
     */
    public Integer getExperimentId() {
        return experimentId;
    }

    /**
     * @param experimentId
     *            the experimentId to set
     */
    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }

    /**
     * @return the organisationId
     */
    public Integer getPhenotypingCenterId() {
        return phenotypingCenterId;
    }

	public void setPhenotypingCenterId(Integer phenotypingCenterId) {
		this.phenotypingCenterId = phenotypingCenterId;
	}

	public Integer getProductionCenterId() {
		return productionCenterId;
	}

	public void setProductionCenterId(Integer productionCenterId) {
		this.productionCenterId = productionCenterId;
	}

	public String getProductionCenter() {
		return productionCenter;
	}

	public void setProductionCenter(String productionCenter) {
		this.productionCenter = productionCenter;
	}

	public String getLitterId() {
		return litterId;
	}

	public void setLitterId(String litterId) {
		this.litterId = litterId;
	}

	/**
     * @return the observationType
     */
    public String getObservationType() {
        return observationType;
    }

    /**
     * @param observationType
     *            the observationType to set
     */
    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    /**
     * @return the organisation
     */
    public String getPhenotypingCenter() {
        return phenotypingCenter;
    }

    /**
     * @param phenotypingCenter
     *            the organisation to set
     */
    public void setPhenotypingCenter(String phenotypingCenter) {
        this.phenotypingCenter = phenotypingCenter;
    }


	public Integer getSpecimenProjectId() {
		return specimenProjectId;
	}

	public void setSpecimenProjectId(Integer specimenProjectId) {
		this.specimenProjectId = specimenProjectId;
	}

	public String getSpecimenProjectName() {
		return specimenProjectName;
	}

	public void setSpecimenProjectName(String specimenProjectName) {
		this.specimenProjectName = specimenProjectName;
	}

	/**
     * @return the colonyId
     */
    public String getColonyId() {
        return colonyId;
    }

    /**
     * @param colonyId
     *            the colonyId to set
     */
    public void setColonyId(String colonyId) {
        this.colonyId = colonyId;
    }


    /**
     * @return the biologicalSampleId
     */
    public Integer getBiologicalSampleId() {
        return biologicalSampleId;
    }

    /**
     * @param biologicalSampleId
     *            the biologicalSampleId to set
     */
    public void setBiologicalSampleId(Integer biologicalSampleId) {
        this.biologicalSampleId = biologicalSampleId;
    }

    /**
     * @return the biologicalModelId
     */
    public Integer getBiologicalModelId() {
        return biologicalModelId;
    }

    /**
     * @param biologicalModelId
     *            the biologicalModelId to set
     */
    public void setBiologicalModelId(Integer biologicalModelId) {
        this.biologicalModelId = biologicalModelId;
    }

    /**
     * @return the zygosity
     */
    public String getZygosity() {
        return zygosity;
    }

    /**
     * @param zygosity
     *            the zygosity to set
     */
    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    /**
     * @return the sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex
     *            the sex to set
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     *            the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the dataPoint
     */
    public Float getDataPoint() {
        return dataPoint;
    }

    /**
     * @param dataPoint
     *            the dataPoint to set
     */
    public void setDataPoint(Float dataPoint) {
        this.dataPoint = dataPoint;
    }

    /**
     * @return the orderIndex
     */
    public Integer getOrderIndex() {
        return orderIndex;
    }

    /**
     * @param orderIndex
     *            the orderIndex to set
     */
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    /**
     * @return the dimension
     */
    public String getDimension() {
        return dimension;
    }

    /**
     * @param dimension
     *            the dimension to set
     */
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    /**
     * @return the timePoint
     */
    public String getTimePoint() {
        return timePoint;
    }

    /**
     * @param timePoint
     *            the timePoint to set
     */
    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    /**
     * @return the discretePoint
     */
    public Float getDiscretePoint() {
        return discretePoint;
    }

    /**
     * @param discretePoint
     *            the discretePoint to set
     */
    public void setDiscretePoint(Float discretePoint) {
        this.discretePoint = discretePoint;
    }

    /**
     * @return the metadataGroup
     */
    public String getMetadataGroup() {
        return metadataGroup;
    }

    /**
     * @param metadataGroup
     *            the metadataGroup to set
     */
    public void setMetadataGroup(String metadataGroup) {
        this.metadataGroup = metadataGroup;
    }

    /**
     * @return the metadata
     */
    public List<String> getMetadata() {
        return metadata;
    }

    public String getStrainAccessionId() {
		return strainAccessionId;
	}

	public void setStrainAccessionId(String strainAccessionId) {
		this.strainAccessionId = strainAccessionId;
	}

	public String getStrainName() {
		return strainName;
	}

	public void setStrainName(String strainName) {
		this.strainName = strainName;
	}

    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
    }

	public String getAllelicComposition() {
		return allelicComposition;
	}

	public void setAllelicComposition(String allelicComposition) {
		this.allelicComposition = allelicComposition;
	}

	public String getAlleleSymbol() {
		return alleleSymbol;
	}

	public void setAlleleSymbol(String alleleSymbol) {
		this.alleleSymbol = alleleSymbol;
	}

	/**
     * @param metadata
     *            the metadata to set
     */
    public void setMetadata(List<String> metadata) {
        this.metadata = metadata;
    }

    public String getAlleleAccession() {
        return this.alleleAccession;
    }

    public void setAlleleAccession(String alleleAccession) {
        this.alleleAccession = alleleAccession;
    }

    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDownloadFilePath() {

		return downloadFilePath;
	}

	public void setDownloadFilePath(String downloadFilePath) {

		this.downloadFilePath = downloadFilePath;
	}


    public List<String> getParameterAssociationStableId() {

        return parameterAssociationStableId;
    }


    public void setParameterAssociationStableId(List<String> parameterAssociationStableId) {

        this.parameterAssociationStableId = parameterAssociationStableId;
    }


    public List<String> getParameterAssociationSequenceId() {

        return parameterAssociationSequenceId;
    }


    public void setParameterAssociationSequenceId(List<String> parameterAssociationSequenceId) {

        this.parameterAssociationSequenceId = parameterAssociationSequenceId;
    }


    public List<String> getParameterAssociationDimId() {

        return parameterAssociationDimId;
    }


    public void setParameterAssociationDimId(List<String> parameterAssociationDimId) {

        this.parameterAssociationDimId = parameterAssociationDimId;
    }


    public String getFileType() {
		return fileType;
	}


	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	public void addParameterAssociationValue(String parameterAssValue) {
		if(this.parameterAssociationValue==null){
			parameterAssociationValue=new ArrayList<>();
		}
		parameterAssociationValue.add(parameterAssValue);

	}


    public String getWeightParameterStableId() {

        return weightParameterStableId;
    }


    public void setWeightParameterStableId(String weightParameterStableId) {

        this.weightParameterStableId = weightParameterStableId;
    }


    public Integer getWeightDaysOld() {

        return weightDaysOld;
    }


    public void setWeightDaysOld(Integer weightDaysOld) {

        this.weightDaysOld = weightDaysOld;
    }


    public Float getWeight() {

        return weight;
    }


    public void setWeight(Float weight) {

        this.weight = weight;
    }


	public void setDevelopmentStageAcc(String developmentalStageAcc) {
		this.developmentalStageAcc=developmentalStageAcc;

	}


	public void setDevelopmentStageName(String developmentalStageName) {
		this.developmentalStageName=developmentalStageName;

	}


	public String getDevelopmentalStageAcc() {
		return developmentalStageAcc;
	}

	public void setDevelopmentalStageAcc(String developmentalStageAcc) {
		this.developmentalStageAcc = developmentalStageAcc;
	}

	public String getDevelopmentalStageName() {
		return developmentalStageName;
	}

	public void setDevelopmentalStageName(String developmentalStageName) {
		this.developmentalStageName = developmentalStageName;
	}

	protected List<String> add(List<String> to, String what, Boolean uniqueOnly){
		if (to == null){
			to = new ArrayList<>();
		}
		if (!uniqueOnly || !to.contains(what)) {
			to.add(what);
		}
		return to;
	}

	protected List<String> add (List<String> to, Collection<String> what, Boolean uniqueOnly){
		if (to == null){
			to = new ArrayList<>();
		}
		if ( uniqueOnly ) {
			addUnique(to, what);
		} else {
			to.addAll(what);
		}
		return to;
	}
}
