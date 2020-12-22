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
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@SolrDocument(collection = "genotype-phenotype")
public class GenotypePhenotypeDTO {

    public static final String ID = "doc_id";
    public static final String ANATOMY_TERM_NAME = "anatomy_term_name";
    public static final String ANATOMY_TERM_ID = "anatomy_term_id";
    public static final String ALLELE_NAME = "allele_name";
    public static final String ALLELE_SYMBOL = "allele_symbol";
    public static final String ALLELE_ACCESSION_ID = "allele_accession_id";
    public static final String ALT_MP_TERM_ID = "alt_mp_term_id";
    public static final String ASSERTION_TYPE = "assertion_type";
    public static final String ASSERTION_TYPE_ID = "assertion_type_id";
    public static final String COLONY_ID = "colony_id";
    public static final String EFFECT_SIZE = "effect_size";
    public static final String EXTERNAL_ID = "external_id";
    public static final String GENETIC_BACKGROUND = "genetic_background";
    public static final String GID = "preqc_gid"; // preqc only
    public static final String INTERMEDIATE_MP_TERM_ID = "intermediate_mp_term_id";
    public static final String INTERMEDIATE_MP_TERM_NAME = "intermediate_mp_term_name";
    public static final String INTERMEDIATE_ANATOMY_TERM_ID = "intermediate_anatomy_term_id";
    public static final String INTERMEDIATE_ANATOMY_TERM_NAME = "intermediate_anatomy_term_name";
    public static final String LIFE_STAGE_ACC = "life_stage_acc";
    public static final String LIFE_STAGE_NAME = "life_stage_name";
    public static final String MPATH_TERM_ID = "mpath_term_id";
    public static final String MPATH_TERM_NAME = "mpath_term_name";
    public static final String MARKER_SYMBOL = "marker_symbol";
    public static final String MARKER_ACCESSION_ID = "marker_accession_id";
    public static final String MP_TERM_ID = "mp_term_id";
    public static final String MP_TERM_NAME = "mp_term_name";
    public static final String ONTOLOGY_DB_ID = "ontology_db_id";
    public static final String PHENOTYPING_CENTER = "phenotyping_center";
    public static final String PROJECT_EXTERNAL_ID = "project_external_id";
    public static final String PROJECT_NAME = "project_name";
    public static final String PROJECT_FULLNAME = "project_fullname";
    public static final String PIPELINE_NAME = "pipeline_name";
    public static final String PIPELINE_STABLE_ID = "pipeline_stable_id";
    public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key";
    public static final String PROCEDURE_NAME = "procedure_name";
    public static final String PROCEDURE_STABLE_ID = "procedure_stable_id";
    public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key";
    public static final String PARAMETER_NAME = "parameter_name";
    public static final String PARAMETER_STABLE_ID = "parameter_stable_id";
    public static final String PARAMETER_STABLE_KEY = "parameter_stable_key";
    public static final String PERCENTAGE_CHANGE = "percentage_change";
    public static final String P_VALUE = "p_value";
    public static final String RESOURCE_NAME = "resource_name";
    public static final String RESOURCE_FULLNAME = "resource_fullname";
    public static final String SEX = "sex";
    public static final String STATISTICAL_METHOD = "statistical_method";
    public static final String STRAIN_NAME = "strain_name";
    public static final String STRAIN_ACCESSION_ID = "strain_accession_id";
    public static final String TOP_LEVEL_ANATOMY_TERM_ID = "top_level_anatomy_term_id";
    public static final String TOP_LEVEL_ANATOMY_TERM_NAME = "top_level_anatomy_term_name";
    public static final String TOP_LEVEL_MP_TERM_ID = "top_level_mp_term_id";
    public static final String TOP_LEVEL_MP_TERM_NAME = "top_level_mp_term_name";
    public static final String ZYGOSITY = "zygosity";


    @Id
    @Field(ID)
    String id;

    @Field(ONTOLOGY_DB_ID)
    Long ontologyDbId;

    @Field(ASSERTION_TYPE)
    String assertionType;

    @Field(ASSERTION_TYPE_ID)
    String assertionTypeId;

    @Field(MPATH_TERM_ID)
    String mpathTermId;

    @Field(MPATH_TERM_NAME)
    String mpathTermName;

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

    @Field(MP_TERM_ID)
    String mpTermId;

    @Field(MP_TERM_NAME)
    String mpTermName;

    @Field(ALT_MP_TERM_ID)
    List<String> altMpTermId;

    @Field(TOP_LEVEL_MP_TERM_ID)
    List<String> topLevelMpTermId;

    @Field(TOP_LEVEL_MP_TERM_NAME)
    List<String> topLevelMpTermName;

    @Field(INTERMEDIATE_MP_TERM_ID)
    List<String> intermediateMpTermId;

    @Field(INTERMEDIATE_MP_TERM_NAME)
    List<String> intermediateMpTermName;

    @Field(MARKER_SYMBOL)
    String markerSymbol;

    @Field(MARKER_ACCESSION_ID)
    String markerAccessionId;

    @Field(COLONY_ID)
    String colonyId;

    @Field(ALLELE_NAME)
    String alleleName;

    @Field(ALLELE_SYMBOL)
    String alleleSymbol;

    @Field(ALLELE_ACCESSION_ID)
    String alleleAccessionId;

    @Field(STRAIN_NAME)
    String strainName;

    @Field(STRAIN_ACCESSION_ID)
    String strainAccessionId;

    @Field(GENETIC_BACKGROUND)
    String geneticBackground;

    @Field(PHENOTYPING_CENTER)
    String phenotypingCenter;

    @Field(PROJECT_EXTERNAL_ID)
    String projectExternalId;

    @Field(PROJECT_NAME)
    List<String> projectName;

    @Field(PROJECT_FULLNAME)
    String projectFullname;

    @Field(RESOURCE_NAME)
    String resourceName;

    @Field(RESOURCE_FULLNAME)
    String resourceFullname;

    @Field(SEX)
    String sex;

    @Field(ZYGOSITY)
    String zygosity;

    @Field(PIPELINE_NAME)
    String pipelineName;

    @Field(PIPELINE_STABLE_ID)
    String pipelineStableId;

    @Field(PIPELINE_STABLE_KEY)
    String pipelineStableKey;

    @Field(PROCEDURE_NAME)
    String procedureName;

    @Field(PROCEDURE_STABLE_ID)
   List<String> procedureStableId;

    @Field(PROCEDURE_STABLE_KEY)
    List<String> procedureStableKey;

    @Field(PARAMETER_NAME)
    String parameterName;

    @Field(PARAMETER_STABLE_ID)
    String parameterStableId;

    @Field(PARAMETER_STABLE_KEY)
    List<String> parameterStableKey;

    @Field(STATISTICAL_METHOD)
    String statisticalMethod;

    @Field(PERCENTAGE_CHANGE)
    String percentageChange;

    @Field(P_VALUE)
    Double p_value;

    @Field(EFFECT_SIZE)
    Double effectSize;

    @Field(EXTERNAL_ID)
    String externalId;

    @Field(LIFE_STAGE_ACC)
    String lifeStageAcc;

    @Field(LIFE_STAGE_NAME)
    String lifeStageName;

    public void addAnatomyTermId(String anatomyTermId){
        if (this.anatomyTermId == null){
            this.anatomyTermId = new ArrayList<>();
        }
        this.anatomyTermId.add(anatomyTermId);
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

    public void addTopLevelAnatomyTermId(String topLevelAnatomyTermId){
        if (this.topLevelAnatomyTermId == null){
            this.topLevelAnatomyTermId = new ArrayList<>();
        }
        this.topLevelAnatomyTermId.add(topLevelAnatomyTermId);
    }

    public void addTopLevelAnatomyTermId(Collection<String> topLevelAnatomyTermId){
        if (this.topLevelAnatomyTermId == null){
            this.topLevelAnatomyTermId = new ArrayList<>();
        }
        for (String term : topLevelAnatomyTermId){
            if (!this.topLevelAnatomyTermId.contains(term)){
                this.topLevelAnatomyTermId.add(term);
            }
        }
    }

    public void addTopLevelAnatomyTermName(String topLevelAnatomyTermName){
        if (this.topLevelAnatomyTermName == null){
            this.topLevelAnatomyTermName = new ArrayList<>();
        }
        this.topLevelAnatomyTermName.add(topLevelAnatomyTermName);
    }

    public void addTopLevelAnatomyTermName(Collection<String> topLevelAnatomyTermName){
        if (this.topLevelAnatomyTermName == null){
            this.topLevelAnatomyTermName = new ArrayList<>();
        }
        for (String term : topLevelAnatomyTermName){
            if (!this.topLevelAnatomyTermName.contains(term)){
                this.topLevelAnatomyTermName.add(term);
            }
        }
    }

    public String getAssertionTypeID() {

        return assertionTypeId;
    }


}
