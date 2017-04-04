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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by ckchen on 25/05/2016.
 */
public class AnatomyDTO {
    public static final String DATA_TYPE = "dataType";
    public static final String ANATOMY_ID = "anatomy_id";
    public static final String ANATOMY_TERM = "anatomy_term";
    public static final String ANATOMY_TERM_SYNONYM = "anatomy_term_synonym";
    public static final String ANATOMY_NODE_ID = "anatomy_node_id";
    public static final String ALT_ANATOMY_ID = "alt_anatomy_id";
    public static final String STAGE = "stage";

    public static final String TOP_LEVEL_ANATOMY_ID = "top_level_anatomy_id";
    public static final String TOP_LEVEL_ANATOMY_TERM = "top_level_anatomy_term";
    public static final String TOP_LEVEL_ANATOMY_TERM_SYNONYM = "top_level_anatomy_term_synonym";

    public static final String SELECTED_TOP_LEVEL_ANATOMY_ID = "selected_top_level_anatomy_id";
    public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM = "selected_top_level_anatomy_term";
    public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM = "selected_top_level_anatomy_term_synonym";
    public static final String SELECTED_TOP_LEVEL_ANATOMY_IDTERM = "selected_top_level_anatomy_idTerm";

    public static final String INTERMEDIATE_ANATOMY_ID = "intermediate_anatomy_id";
    public static final String INTERMEDIATE_ANATOMY_TERM = "intermediate_anatomy_term";
    public static final String INTERMEDIATE_ANATOMY_TERM_SYNONYM = "intermediate_anatomy_term_synonym";

    public static final String PARENT_ANATOMY_ID = "parent_anatomy_id";
    public static final String PARENT_ANATOMY_TERM = "parent_anatomy_term";

    public static final String CHILD_ANATOMY_ID = "child_anatomy_id";
    public static final String CHILD_ANATOMY_TERM = "child_anatomy_term";

    public static final String UBERON_ID = "uberon_id";
    public static final String ALL_AE_MAPPED_UBERON_ID = "all_ae_mapped_uberon_id";
    public static final String EFO_ID = "efo_id";
    public static final String ALL_AE_MAPPED_EFO_ID = "all_ae_mapped_efo_id";

    // MA-MP mapping
    public static final String MP_ID = "mp_id";
    public static final String MP_TERM = "mp_term";
    public static final String MP_TERM_SYNONYM = "mp_term_synonym";

    public static final String TOP_LEVEL_MP_ID = "top_level_mp_id";
    public static final String TOP_LEVEL_MP_TERM = "top_level_mp_term";
    public static final String TOP_LEVEL_MP_TERM_SYNONYM = "top_level_mp_term_synonym";

    public static final String INTERMEDIATE_MP_ID = "intermediate_mp_id";
    public static final String INTERMEDIATE_MP_TERM = "intermediate_mp_term";
    public static final String INTERMEDIATE_MP_TERM_SYNONYM = "intermediate_mp_term_synonym";

    public static final String CHILD_MP_ID = "child_mp_id";
    public static final String CHILD_MP_TERM = "child_mp_term";
    public static final String CHILD_MP_TERM_SYNONYM = "child_mp_term_synonym";
    
    //OntologyBrowser
	public static final String SEARCH_TERM_JSON = MpDTO.SEARCH_TERM_JSON;
	public static final String CHILDREN_JSON = MpDTO.CHILDREN_JSON;
	public static final String SCROLL_NODE = MpDTO.SCROLL_NODE;


    @Field(DATA_TYPE)
    private String dataType;

    @Field(ANATOMY_ID)
    private String anatomyId;

    @Field(ANATOMY_TERM)
    private String anatomyTerm;
    
    @Field(ANATOMY_TERM_SYNONYM)
    private List<String> anatomyTermSynonym;
    
    @Field(ALT_ANATOMY_ID)
    private List<String> altAnatomyIds;
    
    @Field(ANATOMY_NODE_ID)
    private List<Integer> anatomyNodeId;

    @Field(STAGE)
    private String stage;

    @Field(TOP_LEVEL_ANATOMY_ID)
    private List<String> topLevelAnatomyId;

    @Field(TOP_LEVEL_ANATOMY_TERM)
    private List<String> topLevelAnatomyTerm;
    
    @Field(TOP_LEVEL_ANATOMY_TERM_SYNONYM)
    private List<String> topLevelAnatomyTermSynonym;

    @Field(SELECTED_TOP_LEVEL_ANATOMY_ID)
    private List<String> selectedTopLevelAnatomyId;

    @Field(SELECTED_TOP_LEVEL_ANATOMY_TERM)
    private List<String> selectedTopLevelAnatomyTerm;

    @Field(SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM)
    private List<String> selectedTopLevelAnatomyTermSynonym;

    @Field(SELECTED_TOP_LEVEL_ANATOMY_IDTERM)
    private List<String> selectedTopLevelAnatomyIdTerm;

    @Field(INTERMEDIATE_ANATOMY_ID)
    private List<String> intermediateAnatomyId;

    @Field(INTERMEDIATE_ANATOMY_TERM)
    private List<String> intermediateAnatomyTerm;

    @Field(INTERMEDIATE_ANATOMY_TERM_SYNONYM)
    private List<String> intermediateAnatomyTermSynonym;
    
    @Field(PARENT_ANATOMY_ID)
    private List<String> parentAnatomyId;

    @Field(PARENT_ANATOMY_TERM)
    private List<String> parentAnatomyTerm;

    @Field(CHILD_ANATOMY_ID)
    private List<String> childAnatomyId;

    @Field(CHILD_ANATOMY_TERM)
    private List<String> childAnatomyTerm;

    // MA to MP mapping
    @Field(MP_ID)
    private List<String> mpId;

    @Field(MP_TERM)
    private List<String> mpTerm;

    @Field(MP_TERM_SYNONYM)
    private List<String> mpTermSynonym;

    @Field(TOP_LEVEL_MP_ID)
    private List<String> topLevelMpId;

    @Field(TOP_LEVEL_MP_TERM)
    private List<String> topLevelMpTerm;

    @Field(TOP_LEVEL_MP_TERM_SYNONYM)
    private List<String> topLevelMpTermSynonym;

    @Field(CHILD_MP_ID)
    private List<String> childMpId;

    @Field(CHILD_MP_TERM)
    private List<String> childMpTerm;

    @Field(CHILD_MP_TERM_SYNONYM)
    private List<String> childMpTermSynonym;

    // anatomogram stuff
    @Field(UBERON_ID)
    private List<String> uberonIds;

    @Field(ALL_AE_MAPPED_UBERON_ID)
    private List<String> all_ae_mapped_uberonIds;

    @Field(EFO_ID)
    private List<String> efoIds;

    @Field(ALL_AE_MAPPED_EFO_ID)
    private List<String> all_ae_mapped_efoIds;


    // ontology browser
    @Field(SEARCH_TERM_JSON)
    private String searchTermJson;

    @Field(CHILDREN_JSON)
    private String childrenJson;

    @Field(SCROLL_NODE)
    private String scrollNode;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getAnatomyId() {
        return anatomyId;
    }

    public void setAnatomyId(String anatomyId) {
        this.anatomyId = anatomyId;
    }

    public String getAnatomyTerm() {
        return anatomyTerm;
    }

    public void setAnatomyTerm(String anatomyTerm) {
        this.anatomyTerm = anatomyTerm;
    }

    public List<String> getAnatomyTermSynonym() {
        return anatomyTermSynonym;
    }

    public void setAnatomyTermSynonym(Collection<String> anatomyTermSynonym) {
        if (anatomyTermSynonym != null) {
            this.anatomyTermSynonym = new ArrayList<>();
            this.anatomyTermSynonym.addAll(anatomyTermSynonym);
        }
    }

    public List<String> getAltAnatomyIds() {
        return altAnatomyIds;
    }

    public void setAltAnatomyIds(Collection<String> altAnatomyIds) {
        if (altAnatomyIds != null) {
            this.altAnatomyIds = new ArrayList<>();
            this.altAnatomyIds.addAll(altAnatomyIds);
        }
    }

    public List<Integer> getAnatomyNodeId() {
        return anatomyNodeId;
    }

    public void setAnatomyNodeId(Collection<Integer> anatomyNodeId) {
        if (anatomyNodeId != null){
            this.anatomyNodeId = new ArrayList<>();
            this.anatomyNodeId.addAll(anatomyNodeId);
        }
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public List<String> getTopLevelAnatomyId() {
        return topLevelAnatomyId;
    }

    public void setTopLevelAnatomyId(Collection<String> topLevelAnatomyId) {
        if (topLevelAnatomyId != null){
            this.topLevelAnatomyId = new ArrayList<>();
            this.topLevelAnatomyId.addAll(topLevelAnatomyId);
        }
    }

    public List<String> getTopLevelAnatomyTerm() {
        return topLevelAnatomyTerm;
    }

    public void setTopLevelAnatomyTerm(Collection<String> topLevelAnatomyTerm) {
        if (topLevelAnatomyTerm != null){
            this.topLevelAnatomyTerm = new ArrayList<>();
            this.topLevelAnatomyTerm.addAll(topLevelAnatomyTerm);
        }
    }

    public List<String> getTopLevelAnatomyTermSynonym() {
        return topLevelAnatomyTermSynonym;
    }

    public void setTopLevelAnatomyTermSynonym(Collection<String> topLevelAnatomyTermSynonym) {
        if (topLevelAnatomyTermSynonym != null){
            this.topLevelAnatomyTermSynonym = new ArrayList<>();
            this.topLevelAnatomyTermSynonym.addAll(topLevelAnatomyTermSynonym);
        }
    }

    public List<String> getSelectedTopLevelAnatomyId() {
        return selectedTopLevelAnatomyId;
    }

    public void setSelectedTopLevelAnatomyId(Collection<String> selectedTopLevelAnatomyId) {
        if (selectedTopLevelAnatomyId != null){
            this.selectedTopLevelAnatomyId = new ArrayList<>();
            this.selectedTopLevelAnatomyId.addAll(selectedTopLevelAnatomyId);
        }
    }

    public List<String> getSelectedTopLevelAnatomyTerm() {
        return selectedTopLevelAnatomyTerm;
    }

    public void setSelectedTopLevelAnatomyTerm(Collection<String> selectedTopLevelAnatomyTerm) {
        if (selectedTopLevelAnatomyTerm != null){
            this.selectedTopLevelAnatomyTerm = new ArrayList<>();
            this.selectedTopLevelAnatomyTerm.addAll(selectedTopLevelAnatomyTerm);
        }
    }

    public List<String> getSelectedTopLevelAnatomyTermSynonym() {
        return selectedTopLevelAnatomyTermSynonym;
    }

    public void setSelectedTopLevelAnatomyTermSynonym(Collection<String> selectedTopLevelAnatomyTermSynonym) {
        if (selectedTopLevelAnatomyTermSynonym != null){
            this.selectedTopLevelAnatomyTermSynonym = new ArrayList<>();
            this.selectedTopLevelAnatomyTermSynonym.addAll(selectedTopLevelAnatomyTermSynonym);
        }
    }

    public List<String> getSelectedTopLevelAnatomyIdTerm() {
        return selectedTopLevelAnatomyIdTerm;
    }

    public void setSelectedTopLevelAnatomyIdTerm(List<String> selectedTopLevelAnatomyIdTerm) {
        this.selectedTopLevelAnatomyIdTerm = selectedTopLevelAnatomyIdTerm;
    }

    public List<String> getIntermediateAnatomyId() {
        return intermediateAnatomyId;
    }

    public void setIntermediateAnatomyId(Collection<String> intermediateAnatomyId) {
        if(intermediateAnatomyId!=null) {
            this.intermediateAnatomyId = new ArrayList<>();
            this.intermediateAnatomyId.addAll(intermediateAnatomyId);
        }
    }

    public List<String> getIntermediateAnatomyTerm() {
        return intermediateAnatomyTerm;
    }

    public void setIntermediateAnatomyTerm(Collection<String> intermediateAnatomyTerm) {
        if(intermediateAnatomyTerm != null) {
            this.intermediateAnatomyTerm = new ArrayList<>();
            this.intermediateAnatomyTerm.addAll(intermediateAnatomyTerm);
        }
    }

    public List<String> getIntermediateAnatomyTermSynonym() {
        return intermediateAnatomyTermSynonym;
    }

    public void setIntermediateAnatomyTermSynonym(Collection<String> intermediateAnatomyTermSynonym) {
        if(intermediateAnatomyTermSynonym != null) {
            this.intermediateAnatomyTermSynonym = new ArrayList<>();
            this.intermediateAnatomyTermSynonym.addAll(intermediateAnatomyTermSynonym);
        }
    }

    public List<String> getParentAnatomyId() {
        return parentAnatomyId;
    }

    public void setParentAnatomyId(Collection<String> parentAnatomyId) {
        if (parentAnatomyId != null){
            this.parentAnatomyId = new ArrayList<>();
            this.parentAnatomyId.addAll(parentAnatomyId);
        }
    }

    public List<String> getParentAnatomyTerm() {
        return parentAnatomyTerm;
    }

    public void setParentAnatomyTerm(Collection<String> parentAnatomyTerm) {
        if (parentAnatomyTerm != null){
            this.parentAnatomyTerm = new ArrayList<>();
            this.parentAnatomyTerm.addAll(parentAnatomyTerm);
        }
    }

    public List<String> getChildAnatomyId() {
        return childAnatomyId;
    }

    public void setChildAnatomyId(Collection<String> childAnatomyId) {
        if (childAnatomyId != null){
            this.childAnatomyId = new ArrayList<>();
            this.childAnatomyId.addAll(childAnatomyId);
        }
    }

    public List<String> getChildAnatomyTerm() {
        return childAnatomyTerm;
    }

    public void setChildAnatomyTerm(Collection<String> childAnatomyTerm) {
        if (childAnatomyTerm != null){
            this.childAnatomyTerm = new ArrayList<>();
            this.childAnatomyTerm.addAll(childAnatomyTerm);
        }
    }

    public List<String> getMpId() {
        return mpId;
    }

    public void setMpId(List<String> mpId) {
        this.mpId = mpId;
    }

    public List<String> getMpTerm() {
        return mpTerm;
    }

    public void setMpTerm(List<String> mpTerm) {
        this.mpTerm = mpTerm;
    }

    public List<String> getMpTermSynonym() {
        return mpTermSynonym;
    }

    public void setMpTermSynonym(List<String> mpTermSynonym) {
        this.mpTermSynonym = mpTermSynonym;
    }

    public List<String> getTopLevelMpId() {
        return topLevelMpId;
    }

    public void setTopLevelMpId(List<String> topLevelMpId) {
        this.topLevelMpId = topLevelMpId;
    }

    public List<String> getTopLevelMpTerm() {
        return topLevelMpTerm;
    }

    public void setTopLevelMpTerm(List<String> topLevelMpTerm) {
        this.topLevelMpTerm = topLevelMpTerm;
    }

    public List<String> getTopLevelMpTermSynonym() {
        return topLevelMpTermSynonym;
    }

    public void setTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {
        this.topLevelMpTermSynonym = topLevelMpTermSynonym;
    }

    public List<String> getChildMpId() {
        return childMpId;
    }

    public void setChildMpId(List<String> childMpId) {
        this.childMpId = childMpId;
    }

    public List<String> getChildMpTerm() {
        return childMpTerm;
    }

    public void setChildMpTerm(List<String> childMpTerm) {
        this.childMpTerm = childMpTerm;
    }

    public List<String> getChildMpTermSynonym() {
        return childMpTermSynonym;
    }

    public void setChildMpTermSynonym(List<String> childMpTermSynonym) {
        this.childMpTermSynonym = childMpTermSynonym;
    }

    public List<String> getUberonIds() {
        return uberonIds;
    }

    public void setUberonIds(List<String> uberonIds) {
        this.uberonIds = uberonIds;
    }

    public List<String> getAll_ae_mapped_uberonIds() {
        return all_ae_mapped_uberonIds;
    }

    public void setAll_ae_mapped_uberonIds(List<String> all_ae_mapped_uberonIds) {
        this.all_ae_mapped_uberonIds = all_ae_mapped_uberonIds;
    }

    public List<String> getEfoIds() {
        return efoIds;
    }

    public void setEfoIds(List<String> efoIds) {
        this.efoIds = efoIds;
    }

    public List<String> getAll_ae_mapped_efoIds() {
        return all_ae_mapped_efoIds;
    }

    public void setAll_ae_mapped_efoIds(List<String> all_ae_mapped_efoIds) {
        this.all_ae_mapped_efoIds = all_ae_mapped_efoIds;
    }

    public String getSearchTermJson() {
        return searchTermJson;
    }

    public void setSearchTermJson(String searchTermJson) {
        this.searchTermJson = searchTermJson;
    }

    public String getChildrenJson() {
        return childrenJson;
    }

    public void setChildrenJson(String childrenJson) {
        this.childrenJson = childrenJson;
    }

    public String getScrollNode() {
        return scrollNode;
    }

    public void setScrollNode(String scrollNode) {
        this.scrollNode = scrollNode;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnatomyDTO that = (AnatomyDTO) o;

        if (dataType != null ? !dataType.equals(that.dataType) : that.dataType != null) return false;
        if (anatomyId != null ? !anatomyId.equals(that.anatomyId) : that.anatomyId != null) return false;
        if (anatomyTerm != null ? !anatomyTerm.equals(that.anatomyTerm) : that.anatomyTerm != null) return false;
        if (anatomyTermSynonym != null ? !anatomyTermSynonym.equals(that.anatomyTermSynonym) : that.anatomyTermSynonym != null)
            return false;
        if (altAnatomyIds != null ? !altAnatomyIds.equals(that.altAnatomyIds) : that.altAnatomyIds != null)
            return false;
        if (anatomyNodeId != null ? !anatomyNodeId.equals(that.anatomyNodeId) : that.anatomyNodeId != null)
            return false;
        if (stage != null ? !stage.equals(that.stage) : that.stage != null) return false;
        if (topLevelAnatomyId != null ? !topLevelAnatomyId.equals(that.topLevelAnatomyId) : that.topLevelAnatomyId != null)
            return false;
        if (topLevelAnatomyTerm != null ? !topLevelAnatomyTerm.equals(that.topLevelAnatomyTerm) : that.topLevelAnatomyTerm != null)
            return false;
        if (topLevelAnatomyTermSynonym != null ? !topLevelAnatomyTermSynonym.equals(that.topLevelAnatomyTermSynonym) : that.topLevelAnatomyTermSynonym != null)
            return false;
        if (selectedTopLevelAnatomyId != null ? !selectedTopLevelAnatomyId.equals(that.selectedTopLevelAnatomyId) : that.selectedTopLevelAnatomyId != null)
            return false;
        if (selectedTopLevelAnatomyTerm != null ? !selectedTopLevelAnatomyTerm.equals(that.selectedTopLevelAnatomyTerm) : that.selectedTopLevelAnatomyTerm != null)
            return false;
        if (selectedTopLevelAnatomyTermSynonym != null ? !selectedTopLevelAnatomyTermSynonym.equals(that.selectedTopLevelAnatomyTermSynonym) : that.selectedTopLevelAnatomyTermSynonym != null)
            return false;
        if (selectedTopLevelAnatomyIdTerm != null ? !selectedTopLevelAnatomyIdTerm.equals(that.selectedTopLevelAnatomyIdTerm) : that.selectedTopLevelAnatomyIdTerm != null)
            return false;
        if (intermediateAnatomyId != null ? !intermediateAnatomyId.equals(that.intermediateAnatomyId) : that.intermediateAnatomyId != null)
            return false;
        if (intermediateAnatomyTerm != null ? !intermediateAnatomyTerm.equals(that.intermediateAnatomyTerm) : that.intermediateAnatomyTerm != null)
            return false;
        if (intermediateAnatomyTermSynonym != null ? !intermediateAnatomyTermSynonym.equals(that.intermediateAnatomyTermSynonym) : that.intermediateAnatomyTermSynonym != null)
            return false;
        if (parentAnatomyId != null ? !parentAnatomyId.equals(that.parentAnatomyId) : that.parentAnatomyId != null)
            return false;
        if (parentAnatomyTerm != null ? !parentAnatomyTerm.equals(that.parentAnatomyTerm) : that.parentAnatomyTerm != null)
            return false;
        if (childAnatomyId != null ? !childAnatomyId.equals(that.childAnatomyId) : that.childAnatomyId != null)
            return false;
        if (childAnatomyTerm != null ? !childAnatomyTerm.equals(that.childAnatomyTerm) : that.childAnatomyTerm != null)
            return false;
        if (mpId != null ? !mpId.equals(that.mpId) : that.mpId != null) return false;
        if (mpTerm != null ? !mpTerm.equals(that.mpTerm) : that.mpTerm != null) return false;
        if (mpTermSynonym != null ? !mpTermSynonym.equals(that.mpTermSynonym) : that.mpTermSynonym != null)
            return false;
        if (topLevelMpId != null ? !topLevelMpId.equals(that.topLevelMpId) : that.topLevelMpId != null) return false;
        if (topLevelMpTerm != null ? !topLevelMpTerm.equals(that.topLevelMpTerm) : that.topLevelMpTerm != null)
            return false;
        if (topLevelMpTermSynonym != null ? !topLevelMpTermSynonym.equals(that.topLevelMpTermSynonym) : that.topLevelMpTermSynonym != null)
            return false;
        if (childMpId != null ? !childMpId.equals(that.childMpId) : that.childMpId != null) return false;
        if (childMpTerm != null ? !childMpTerm.equals(that.childMpTerm) : that.childMpTerm != null) return false;
        if (childMpTermSynonym != null ? !childMpTermSynonym.equals(that.childMpTermSynonym) : that.childMpTermSynonym != null)
            return false;
        if (uberonIds != null ? !uberonIds.equals(that.uberonIds) : that.uberonIds != null) return false;
        if (all_ae_mapped_uberonIds != null ? !all_ae_mapped_uberonIds.equals(that.all_ae_mapped_uberonIds) : that.all_ae_mapped_uberonIds != null)
            return false;
        if (efoIds != null ? !efoIds.equals(that.efoIds) : that.efoIds != null) return false;
        if (all_ae_mapped_efoIds != null ? !all_ae_mapped_efoIds.equals(that.all_ae_mapped_efoIds) : that.all_ae_mapped_efoIds != null)
            return false;
        if (searchTermJson != null ? !searchTermJson.equals(that.searchTermJson) : that.searchTermJson != null)
            return false;
        if (childrenJson != null ? !childrenJson.equals(that.childrenJson) : that.childrenJson != null) return false;
        return !(scrollNode != null ? !scrollNode.equals(that.scrollNode) : that.scrollNode != null);

    }

    @Override
    public int hashCode() {
        int result = dataType != null ? dataType.hashCode() : 0;
        result = 31 * result + (anatomyId != null ? anatomyId.hashCode() : 0);
        result = 31 * result + (anatomyTerm != null ? anatomyTerm.hashCode() : 0);
        result = 31 * result + (anatomyTermSynonym != null ? anatomyTermSynonym.hashCode() : 0);
        result = 31 * result + (altAnatomyIds != null ? altAnatomyIds.hashCode() : 0);
        result = 31 * result + (anatomyNodeId != null ? anatomyNodeId.hashCode() : 0);
        result = 31 * result + (stage != null ? stage.hashCode() : 0);
        result = 31 * result + (topLevelAnatomyId != null ? topLevelAnatomyId.hashCode() : 0);
        result = 31 * result + (topLevelAnatomyTerm != null ? topLevelAnatomyTerm.hashCode() : 0);
        result = 31 * result + (topLevelAnatomyTermSynonym != null ? topLevelAnatomyTermSynonym.hashCode() : 0);
        result = 31 * result + (selectedTopLevelAnatomyId != null ? selectedTopLevelAnatomyId.hashCode() : 0);
        result = 31 * result + (selectedTopLevelAnatomyTerm != null ? selectedTopLevelAnatomyTerm.hashCode() : 0);
        result = 31 * result + (selectedTopLevelAnatomyTermSynonym != null ? selectedTopLevelAnatomyTermSynonym.hashCode() : 0);
        result = 31 * result + (selectedTopLevelAnatomyIdTerm != null ? selectedTopLevelAnatomyIdTerm.hashCode() : 0);
        result = 31 * result + (intermediateAnatomyId != null ? intermediateAnatomyId.hashCode() : 0);
        result = 31 * result + (intermediateAnatomyTerm != null ? intermediateAnatomyTerm.hashCode() : 0);
        result = 31 * result + (intermediateAnatomyTermSynonym != null ? intermediateAnatomyTermSynonym.hashCode() : 0);
        result = 31 * result + (parentAnatomyId != null ? parentAnatomyId.hashCode() : 0);
        result = 31 * result + (parentAnatomyTerm != null ? parentAnatomyTerm.hashCode() : 0);
        result = 31 * result + (childAnatomyId != null ? childAnatomyId.hashCode() : 0);
        result = 31 * result + (childAnatomyTerm != null ? childAnatomyTerm.hashCode() : 0);
        result = 31 * result + (mpId != null ? mpId.hashCode() : 0);
        result = 31 * result + (mpTerm != null ? mpTerm.hashCode() : 0);
        result = 31 * result + (mpTermSynonym != null ? mpTermSynonym.hashCode() : 0);
        result = 31 * result + (topLevelMpId != null ? topLevelMpId.hashCode() : 0);
        result = 31 * result + (topLevelMpTerm != null ? topLevelMpTerm.hashCode() : 0);
        result = 31 * result + (topLevelMpTermSynonym != null ? topLevelMpTermSynonym.hashCode() : 0);
        result = 31 * result + (childMpId != null ? childMpId.hashCode() : 0);
        result = 31 * result + (childMpTerm != null ? childMpTerm.hashCode() : 0);
        result = 31 * result + (childMpTermSynonym != null ? childMpTermSynonym.hashCode() : 0);
        result = 31 * result + (uberonIds != null ? uberonIds.hashCode() : 0);
        result = 31 * result + (all_ae_mapped_uberonIds != null ? all_ae_mapped_uberonIds.hashCode() : 0);
        result = 31 * result + (efoIds != null ? efoIds.hashCode() : 0);
        result = 31 * result + (all_ae_mapped_efoIds != null ? all_ae_mapped_efoIds.hashCode() : 0);
        result = 31 * result + (searchTermJson != null ? searchTermJson.hashCode() : 0);
        result = 31 * result + (childrenJson != null ? childrenJson.hashCode() : 0);
        result = 31 * result + (scrollNode != null ? scrollNode.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "AnatomyDTO{" +
                "dataType='" + dataType + '\'' +
                ", anatomyId='" + anatomyId + '\'' +
                ", anatomyTerm='" + anatomyTerm + '\'' +
                ", anatomyTermSynonym=" + anatomyTermSynonym +
                ", altAnatomyIds=" + altAnatomyIds +
                ", anatomyNodeId=" + anatomyNodeId +
                ", stage='" + stage + '\'' +
                ", topLevelAnatomyId=" + topLevelAnatomyId +
                ", topLevelAnatomyTerm=" + topLevelAnatomyTerm +
                ", topLevelAnatomyTermSynonym=" + topLevelAnatomyTermSynonym +
                ", selectedTopLevelAnatomyId=" + selectedTopLevelAnatomyId +
                ", selectedTopLevelAnatomyTerm=" + selectedTopLevelAnatomyTerm +
                ", selectedTopLevelAnatomyTermSynonym=" + selectedTopLevelAnatomyTermSynonym +
                ", selectedTopLevelAnatomyIdTerm=" + selectedTopLevelAnatomyIdTerm +
                ", intermediateAnatomyId=" + intermediateAnatomyId +
                ", intermediateAnatomyTerm=" + intermediateAnatomyTerm +
                ", intermediateAnatomyTermSynonym=" + intermediateAnatomyTermSynonym +
                ", parentAnatomyId=" + parentAnatomyId +
                ", parentAnatomyTerm=" + parentAnatomyTerm +
                ", childAnatomyId=" + childAnatomyId +
                ", childAnatomyTerm=" + childAnatomyTerm +
                ", mpId=" + mpId +
                ", mpTerm=" + mpTerm +
                ", mpTermSynonym=" + mpTermSynonym +
                ", topLevelMpId=" + topLevelMpId +
                ", topLevelMpTerm=" + topLevelMpTerm +
                ", topLevelMpTermSynonym=" + topLevelMpTermSynonym +
                ", childMpId=" + childMpId +
                ", childMpTerm=" + childMpTerm +
                ", childMpTermSynonym=" + childMpTermSynonym +
                ", uberonIds=" + uberonIds +
                ", all_ae_mapped_uberonIds=" + all_ae_mapped_uberonIds +
                ", efoIds=" + efoIds +
                ", all_ae_mapped_efoIds=" + all_ae_mapped_efoIds +
                ", searchTermJson='" + searchTermJson + '\'' +
                ", childrenJson='" + childrenJson + '\'' +
                ", scrollNode='" + scrollNode + '\'' +
                '}';
    }
}
