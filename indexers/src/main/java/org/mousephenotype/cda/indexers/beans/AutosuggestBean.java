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
package org.mousephenotype.cda.indexers.beans;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;


/**
 * SolrJ class to support loading the autosuggest core
 */
public class AutosuggestBean {

	public static final String AUTOSUGGEST_ID = "autosuggest_id";
	public static final String DOCTYPE = "docType";
	public static final String MGI_ACCESSION_ID = "mgi_accession_id";
	public static final String MGI_ALLELE_ACCESSION_ID = "allele_accession_id";
	public static final String ALLELE_MGI_ACCESSION_ID = "allele_mgi_accession_id";
	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String MARKER_NAME = "marker_name";
	public static final String MARKER_SYNONYM = "marker_synonym";
	public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";
	public static final String HP_ID = "hp_id";
	public static final String HP_TERM = "hp_term";
	public static final String HP_SYNONYM = "hp_synonym";
	public static final String HP_TERM_SYNONYM = "hp_term_synonym";
	public static final String HPMP_ID = "hpmp_id";
	public static final String HPMP_TERM = "hpmp_term";

	public static final String MP_ID = "mp_id";
	public static final String MP_TERM = "mp_term";
	public static final String MP_TERM_SYNONYM = "mp_term_synonym";
	public static final String MP_NARROW_SYNONYM = "mp_narrow_synonym";
	public static final String ALT_MP_ID = "alt_mp_id";
	public static final String CHILD_MP_ID = "child_mp_id";
	public static final String CHILD_MP_TERM = "child_mp_term";
	public static final String CHILD_MP_TERM_SYNONYM = "child_mp_term_synonym";
	public static final String PARENT_MP_ID = "parent_mp_id";
	public static final String PARENT_MP_TERM = "parent_mp_term";
	public static final String PARENT_MP_TERM_SYNONYM = "parent_mp_term_synonym";
	public static final String INTERMEDIATE_MP_ID = "intermediate_mp_id";
	public static final String INTERMEDIATE_MP_TERM = "intermediate_mp_term";
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = "intermediate_mp_term_synonym";
	public static final String TOP_LEVEL_MP_ID = "top_level_mp_id";
	public static final String TOP_LEVEL_MP_TERM = "top_level_mp_term";
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = "top_level_mp_term_synonym";

	// generic anatomy fields
	public static final String ANATOMY_ID = "anatomy_id";
	public static final String ANATOMY_TERM = "anatomy_term";
	public static final String ANATOMY_TERM_SYNONYM = "anatomy_term_synonym";
	public static final String ALT_ANATOMY_ID = "alt_anatomy_id";
	public static final String CHILD_ANATOMY_ID = "child_anatomy_id";
	public static final String CHILD_ANATOMY_TERM = "child_anatomy_term";
	public static final String CHILD_ANATOMY_TERM_SYNONYM = "child_anatomy_term_synonym";
	public static final String PARENT_ANATOMY_ID = "parent_anatomy_id";
	public static final String PARENT_ANATOMY_TERM = "parent_anatomy_term";
	public static final String PARENT_ANATOMY_TERM_SYNONYM = "parent_anatomy_term_synonym";
	public static final String INTERMEDIATE_ANATOMY_ID = "intermediate_anatomy_id";
	public static final String INTERMEDIATE_ANATOMY_TERM = "intermediate_anatomy_term";
	public static final String INTERMEDIATE_ANATOMY_TERM_SYNONYM = "intermediate_anatomy_term_synonym";
	public static final String TOP_LEVEL_ANATOMY_ID = "top_level_anatomy_id";
	public static final String TOP_LEVEL_ANATOMY_TERM = "top_level_anatomy_term";
	public static final String TOP_LEVEL_ANATOMY_TERM_SYNONYM = "top_level_anatomy_term_synonym";
	public static final String SELECTED_TOP_LEVEL_ANATOMY_ID = "selected_top_level_anatomy_id";
	public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM = "selected_top_level_anatomy_term";
	public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM = "selected_top_level_anatomy_term_synonym";

	public static final String PARAMETER_NAME = "parameter_name";

	public static final String DISEASE_ID = "disease_id";
	public static final String DISEASE_TERM = "disease_term";
	public static final String DISEASE_ALTS = "disease_alts";

	public static final String IKMC_PROJECT = "ikmc_project";
	public static final String ALLELE_NAME = "allele_name";
	public static final String GENE_ALLELE = "gene_allele";

	public static final String AUTO_SUGGEST = "auto_suggest";

	public static final String GWAS_MGI_GENE_ID = "gwas_mgi_gene_id";
	public static final String GWAS_MGI_GENE_SYMBOL = "gwas_mgi_gene_symbol";
	public static final String GWAS_MGI_ALLELE_ID = "gwas_mgi_allele_id";
	public static final String GWAS_MGI_ALLELE_NAME = "gwas_mgi_allele_name";
	
	public static final String GWAS_MP_TERM_ID = "gwas_mp_term_id";
	public static final String GWAS_MP_TERM_NAME = "gwas_mp_term_name";
	
	public static final String GWAS_DISEASE_TRAIT = "gwas_disease_trait";
	public static final String GWAS_REPORTED_GENE = "gwas_reported_gene";
	public static final String GWAS_MAPPED_GENE = "gwas_mapped_gene";
	public static final String GWAS_UPSTREAM_GENE = "gwas_upstream_gene";
	public static final String GWAS_DOWNSTREAM_GENE = "gwas_downstream_gene";
	public static final String GWAS_SNP_ID = "gwas_snp_id";

	@Field(AUTOSUGGEST_ID)
	private String autosuggestId;

	@Field(DOCTYPE)
	private String docType;

	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionId;

	@Field(MGI_ALLELE_ACCESSION_ID)
	private List<String> mgiAlleleAccessionIds;

	@Field(ALLELE_MGI_ACCESSION_ID)
	private String alleleMgiAccessionId;

	@Field(MARKER_SYMBOL)
	private String markerSymbol;

	@Field(MARKER_NAME)
	private String markerName;

	@Field(MARKER_SYNONYM)
	private String markerSynonym;

	@Field(HUMAN_GENE_SYMBOL)
	private String humanGeneSymbol;

	@Field(PARAMETER_NAME)
	private String parameterName;

	@Field(HP_ID)
	private String hpId;

	@Field(HP_TERM)
	private String hpTerm;

	@Field(HP_SYNONYM)
	private String hpSynonym;

	@Field(HP_TERM_SYNONYM)
	private String hpTermSynonym;

	@Field(HPMP_ID)
	private String hpmpId;

	@Field(HPMP_TERM)
	private String hpmpTerm;

	@Field(ALT_MP_ID)
	private String altMpId;

	@Field(MP_ID)
	private String mpId;

	@Field(MP_TERM)
	private String mpTerm;

	@Field(MP_TERM_SYNONYM)
	private String mpTermSynonym;

	@Field(MP_NARROW_SYNONYM)
	private String mpNarrowSynonym;

	@Field(CHILD_MP_ID)
	private String childMpId;

	@Field(CHILD_MP_TERM)
	private String childMpTerm;

	@Field(CHILD_MP_TERM_SYNONYM)
	private String childMpTermSynonym;

	@Field(PARENT_MP_ID)
	private String parentMpId;

	@Field(PARENT_MP_TERM)
	private String parentMpTerm;

	@Field(PARENT_MP_TERM_SYNONYM)
	private String parentMpTermSynonym;

	@Field(INTERMEDIATE_MP_ID)
	private String intermediateMpId;

	@Field(INTERMEDIATE_MP_TERM)
	private String intermediateMpTerm;

	@Field(INTERMEDIATE_MP_TERM_SYNONYM)
	private String intermediateMpTermSynonym;

	@Field(TOP_LEVEL_MP_ID)
	private String topLevelMpId;

	@Field(TOP_LEVEL_MP_TERM)
	private String topLevelMpTerm;

	@Field(TOP_LEVEL_MP_TERM_SYNONYM)
	private String topLevelMpTermSynonym;

	// generic anatomy fields
	@Field(ANATOMY_ID)
	private String anatomyId;

	@Field(ALT_ANATOMY_ID)
	private String altAnatomyId;

	@Field(ANATOMY_TERM)
	private String anatomyTerm;

	@Field(ANATOMY_TERM_SYNONYM)
	private String anatomyTermSynonym;

	@Field(CHILD_ANATOMY_ID)
	private String childAnatomyId;

	@Field(CHILD_ANATOMY_TERM)
	private String childAnatomyTerm;

	@Field(CHILD_ANATOMY_TERM_SYNONYM)
	private String childAnatomyTermSynonym;

	@Field(PARENT_ANATOMY_ID)
	private String parentAnatomyId;

	@Field(PARENT_ANATOMY_TERM)
	private String parentAnatomyTerm;

	@Field(PARENT_ANATOMY_TERM_SYNONYM)
	private String parentAnatomyTermSynonym;

	@Field(INTERMEDIATE_ANATOMY_ID)
	private String intermediateAnatomyId;

	@Field(INTERMEDIATE_ANATOMY_TERM)
	private String intermediateAnatomyTerm;

	@Field(INTERMEDIATE_ANATOMY_TERM_SYNONYM)
	private String intermediateAnatomyTermSynonym;

	@Field(TOP_LEVEL_ANATOMY_ID)
	private String topLevelAnatomyId;

	@Field(TOP_LEVEL_ANATOMY_TERM)
	private String topLevelAnatomyTerm;

	@Field(TOP_LEVEL_ANATOMY_TERM_SYNONYM)
	private String topLevelAnatomyTermSynonym;

	@Field(SELECTED_TOP_LEVEL_ANATOMY_ID)
	private String selectedTopLevelAnatomyId;

	@Field(SELECTED_TOP_LEVEL_ANATOMY_TERM)
	private String selectedTopLevelAnatomyTerm;

	@Field(SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM)
	private String selectedTopLevelAnatomyTermSynonym;

	@Field(DISEASE_ID)
	private String diseaseId;

	@Field(DISEASE_TERM)
	private String diseaseTerm;

	@Field(DISEASE_ALTS)
	private String diseaseAlts;

	@Field(IKMC_PROJECT)
	private String ikmcProject;
	@Field(ALLELE_NAME)
	private String alleleName;
	@Field(GENE_ALLELE)
	private String geneAllele;

	@Field(AUTO_SUGGEST)
	private List<String> autosuggest;

	@Field(GWAS_MGI_GENE_ID)
	private String gwasMgiGeneId;

	@Field(GWAS_MGI_GENE_SYMBOL)
	private String gwasMgiGeneSymbol;

	@Field(GWAS_MGI_ALLELE_ID)
	private String gwasMgiAlleleId;

	@Field(GWAS_MGI_ALLELE_NAME)
	private String gwasMgiAlleleName;
	
	@Field(GWAS_MP_TERM_ID)
	private String gwasMpTermId;
	
	@Field(GWAS_MP_TERM_NAME)
	private String gwasMpTermName;
	
	@Field(GWAS_DISEASE_TRAIT)
	private String gwasDiseaseTrait;
	
	@Field(GWAS_REPORTED_GENE)
	private String gwasReportedGene;
	
	@Field(GWAS_MAPPED_GENE)
	private String gwasMappedGene;
	
	@Field(GWAS_UPSTREAM_GENE)
	private String gwasUpstreamGene;
	
	@Field(GWAS_DOWNSTREAM_GENE)
	private String gwasDownstreamGene;
	
	@Field(GWAS_SNP_ID)
	private String gwasSnpId;


	public String getAutosuggestId() {
		return autosuggestId;
	}

	public void setAutosuggestId(String allele2Id) {
		this.autosuggestId = autosuggestId;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getMgiAccessionId() {
		return mgiAccessionId;
	}

	public void setMgiAccessionId(String mgiAccessionId) {
		this.mgiAccessionId = mgiAccessionId;
	}

	public List<String> getMgiAlleleAccessionIds() {
		return mgiAlleleAccessionIds;
	}

	public void setMgiAlleleAccessionIds(List<String> mgiAlleleAccessionIds) {
		this.mgiAlleleAccessionIds = mgiAlleleAccessionIds;
	}

	public String getAlleleMgiAccessionId() {
		return alleleMgiAccessionId;
	}

	public void setAlleleMgiAccessionId(String alleleMgiAccessionId) {
		this.alleleMgiAccessionId = alleleMgiAccessionId;
	}

	public String getMarkerSymbol() {
		return markerSymbol;
	}

	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	public String getMarkerName() {
		return markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	public String getMarkerSynonym() {
		return markerSynonym;
	}

	public void setMarkerSynonym(String markerSynonym) {
		this.markerSynonym = markerSynonym;
	}

	public String getHumanGeneSymbol() {
		return humanGeneSymbol;
	}

	public void setHumanGeneSymbol(String humanGeneSymbol) {
		this.humanGeneSymbol = humanGeneSymbol;
	}

	public String getHpId() {
		return hpId;
	}

	public void setHpId(String hpId) {
		this.hpId = hpId;
	}

	public String getHpTerm() {
		return hpTerm;
	}

	public void setHpTerm(String hpTerm) {
		this.hpTerm = hpTerm;
	}

	public String getHpSynonym() {
		return hpSynonym;
	}

	public void setHpSynonym(String hpSynonym) {
		this.hpSynonym = hpSynonym;
	}

	public String getHpTermSynonym() {
		return hpTermSynonym;
	}

	public void setHpTermSynonym(String hpTermSynonym) {
		this.hpTermSynonym = hpTermSynonym;
	}

	public String getHpmpId() {
		return hpmpId;
	}

	public void setHpmpId(String hpmpId) {
		this.hpmpId = hpmpId;
	}

	public String getHpmpTerm() {
		return hpmpTerm;
	}

	public void setHpmpTerm(String hpmpTerm) {
		this.hpmpTerm = hpmpTerm;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getAltMpId() {
		return altMpId;
	}

	public void setAltMpId(String altMpId) {
		this.altMpId = altMpId;
	}

	public String getMpId() {
		return mpId;
	}

	public void setMpId(String mpId) {
		this.mpId = mpId;
	}

	public String getMpTerm() {
		return mpTerm;
	}

	public void setMpTerm(String mpTerm) {
		this.mpTerm = mpTerm;
	}

	public String getMpTermSynonym() {
		return mpTermSynonym;
	}

	public void setMpTermSynonym(String mpTermSynonym) {
		this.mpTermSynonym = mpTermSynonym;
	}

	public String getMpNarrowSynonym() {
		return mpNarrowSynonym;
	}

	public void setMpNarrowSynonym(String mpNarrowSynonym) {
		this.mpNarrowSynonym = mpNarrowSynonym;
	}

	public String getChildMpId() {
		return childMpId;
	}

	public void setChildMpId(String childMpId) {
		this.childMpId = childMpId;
	}

	public String getChildMpTerm() {
		return childMpTerm;
	}

	public void setChildMpTerm(String childMpTerm) {
		this.childMpTerm = childMpTerm;
	}

	public String getChildMpTermSynonym() {
		return childMpTermSynonym;
	}

	public void setChildMpTermSynonym(String childMpTermSynonym) {
		this.childMpTermSynonym = childMpTermSynonym;
	}

	public String getParentMpId() {
		return parentMpId;
	}

	public void setParentMpId(String parentMpId) {
		this.parentMpId = parentMpId;
	}

	public String getParentMpTerm() {
		return parentMpTerm;
	}

	public void setParentMpTerm(String parentMpTerm) {
		this.parentMpTerm = parentMpTerm;
	}

	public String getParentMpTermSynonym() {
		return parentMpTermSynonym;
	}

	public void setParentMpTermSynonym(String parentMpTermSynonym) {
		this.parentMpTermSynonym = parentMpTermSynonym;
	}

	public String getIntermediateMpId() {
		return intermediateMpId;
	}

	public void setIntermediateMpId(String intermediateMpId) {
		this.intermediateMpId = intermediateMpId;
	}

	public String getIntermediateMpTerm() {
		return intermediateMpTerm;
	}

	public void setIntermediateMpTerm(String intermediateMpTerm) {
		this.intermediateMpTerm = intermediateMpTerm;
	}

	public String getIntermediateMpTermSynonym() {
		return intermediateMpTermSynonym;
	}

	public void setIntermediateMpTermSynonym(String intermediateMpTermSynonym) {
		this.intermediateMpTermSynonym = intermediateMpTermSynonym;
	}

	public String getTopLevelMpId() {
		return topLevelMpId;
	}

	public void setTopLevelMpId(String topLevelMpId) {
		this.topLevelMpId = topLevelMpId;
	}

	public String getTopLevelMpTerm() {
		return topLevelMpTerm;
	}

	public void setTopLevelMpTerm(String topLevelMpTerm) {
		this.topLevelMpTerm = topLevelMpTerm;
	}

	public String getTopLevelMpTermSynonym() {
		return topLevelMpTermSynonym;
	}

	public void setTopLevelMpTermSynonym(String topLevelMpTermSynonym) {
		this.topLevelMpTermSynonym = topLevelMpTermSynonym;
	}

	public String getAnatomyId() {
		return anatomyId;
	}

	public void setAnatomyId(String anatomyId) {
		this.anatomyId = anatomyId;
	}

	public String getAltAnatomyId() {
		return altAnatomyId;
	}

	public void setAltAnatomyId(String altAnatomyId) {
		this.altAnatomyId = altAnatomyId;
	}

	public String getAnatomyTerm() {
		return anatomyTerm;
	}

	public void setAnatomyTerm(String anatomyTerm) {
		this.anatomyTerm = anatomyTerm;
	}

	public String getAnatomyTermSynonym() {
		return anatomyTermSynonym;
	}

	public void setAnatomyTermSynonym(String anatomyTermSynonym) {
		this.anatomyTermSynonym = anatomyTermSynonym;
	}

	public String getChildAnatomyId() {
		return childAnatomyId;
	}

	public void setChildAnatomyId(String childAnatomyId) {
		this.childAnatomyId = childAnatomyId;
	}

	public String getChildAnatomyTerm() {
		return childAnatomyTerm;
	}

	public void setChildAnatomyTerm(String childAnatomyTerm) {
		this.childAnatomyTerm = childAnatomyTerm;
	}

	public String getChildAnatomyTermSynonym() {
		return childAnatomyTermSynonym;
	}

	public void setChildAnatomyTermSynonym(String childAnatomyTermSynonym) {
		this.childAnatomyTermSynonym = childAnatomyTermSynonym;
	}

	public String getParentAnatomyId() {
		return parentAnatomyId;
	}

	public void setParentAnatomyId(String parentAnatomyId) {
		this.parentAnatomyId = parentAnatomyId;
	}

	public String getParentAnatomyTerm() {
		return parentAnatomyTerm;
	}

	public void setParentAnatomyTerm(String parentAnatomyTerm) {
		this.parentAnatomyTerm = parentAnatomyTerm;
	}

	public String getParentAnatomyTermSynonym() {
		return parentAnatomyTermSynonym;
	}

	public void setParentAnatomyTermSynonym(String parentAnatomyTermSynonym) {
		this.parentAnatomyTermSynonym = parentAnatomyTermSynonym;
	}

	public String getIntermediateAnatomyId() {
		return intermediateAnatomyId;
	}

	public void setIntermediateAnatomyId(String intermediateAnatomyId) {
		this.intermediateAnatomyId = intermediateAnatomyId;
	}

	public String getIntermediateAnatomyTerm() {
		return intermediateAnatomyTerm;
	}

	public void setIntermediateAnatomyTerm(String intermediateAnatomyTerm) {
		this.intermediateAnatomyTerm = intermediateAnatomyTerm;
	}

	public String getIntermediateAnatomyTermSynonym() {
		return intermediateAnatomyTermSynonym;
	}

	public void setIntermediateAnatomyTermSynonym(String intermediateAnatomyTermSynonym) {
		this.intermediateAnatomyTermSynonym = intermediateAnatomyTermSynonym;
	}

	public String getTopLevelAnatomyId() {
		return topLevelAnatomyId;
	}

	public void setTopLevelAnatomyId(String topLevelAnatomyId) {
		this.topLevelAnatomyId = topLevelAnatomyId;
	}

	public String getTopLevelAnatomyTerm() {
		return topLevelAnatomyTerm;
	}

	public void setTopLevelAnatomyTerm(String topLevelAnatomyTerm) {
		this.topLevelAnatomyTerm = topLevelAnatomyTerm;
	}

	public String getTopLevelAnatomyTermSynonym() {
		return topLevelAnatomyTermSynonym;
	}

	public void setTopLevelAnatomyTermSynonym(String topLevelAnatomyTermSynonym) {
		this.topLevelAnatomyTermSynonym = topLevelAnatomyTermSynonym;
	}

	public String getSelectedTopLevelAnatomyId() {
		return selectedTopLevelAnatomyId;
	}

	public void setSelectedTopLevelAnatomyId(String selectedTopLevelAnatomyId) {
		this.selectedTopLevelAnatomyId = selectedTopLevelAnatomyId;
	}

	public String getSelectedTopLevelAnatomyTerm() {
		return selectedTopLevelAnatomyTerm;
	}

	public void setSelectedTopLevelAnatomyTerm(String selectedTopLevelAnatomyTerm) {
		this.selectedTopLevelAnatomyTerm = selectedTopLevelAnatomyTerm;
	}

	public String getSelectedTopLevelAnatomyTermSynonym() {
		return selectedTopLevelAnatomyTermSynonym;
	}

	public void setSelectedTopLevelAnatomyTermSynonym(String selectedTopLevelAnatomyTermSynonym) {
		this.selectedTopLevelAnatomyTermSynonym = selectedTopLevelAnatomyTermSynonym;
	}

	public String getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(String diseaseId) {
		this.diseaseId = diseaseId;
	}

	public String getDiseaseTerm() {
		return diseaseTerm;
	}

	public void setDiseaseTerm(String diseaseTerm) {
		this.diseaseTerm = diseaseTerm;
	}

	public String getDiseaseAlts() {
		return diseaseAlts;
	}

	public void setDiseaseAlts(String diseaseAlts) {
		this.diseaseAlts = diseaseAlts;
	}

	public String getIkmcProject() {
		return ikmcProject;
	}

	public void setIkmcProject(String ikmcProject) {
		this.ikmcProject = ikmcProject;
	}

	public String getAlleleName() {
		return alleleName;
	}

	public void setAlleleName(String alleleName) {
		this.alleleName = alleleName;
	}

	public String getGeneAllele() {
		return geneAllele;
	}

	public void setGeneAllele(String geneAllele) {
		this.geneAllele = geneAllele;
	}

	public List<String> getAutosuggest() {
		return autosuggest;
	}

	public void setAutosuggest(List<String> autosuggest) {
		this.autosuggest = autosuggest;
	}

	public String getGwasMgiGeneId() {
		return gwasMgiGeneId;
	}

	public void setGwasMgiGeneId(String gwasMgiGeneId) {
		this.gwasMgiGeneId = gwasMgiGeneId;
	}

	public String getGwasMgiGeneSymbol() {
		return gwasMgiGeneSymbol;
	}

	public void setGwasMgiGeneSymbol(String gwasMgiGeneSymbol) {
		this.gwasMgiGeneSymbol = gwasMgiGeneSymbol;
	}

	public String getGwasMgiAlleleId() {
		return gwasMgiAlleleId;
	}

	public void setGwasMgiAlleleId(String gwasMgiAlleleId) {
		this.gwasMgiAlleleId = gwasMgiAlleleId;
	}

	public String getGwasMgiAlleleName() {
		return gwasMgiAlleleName;
	}

	public void setGwasMgiAlleleName(String gwasMgiAlleleName) {
		this.gwasMgiAlleleName = gwasMgiAlleleName;
	}

	public String getGwasMpTermId() {
		return gwasMpTermId;
	}

	public void setGwasMpTermId(String gwasMpTermId) {
		this.gwasMpTermId = gwasMpTermId;
	}

	public String getGwasMpTermName() {
		return gwasMpTermName;
	}

	public void setGwasMpTermName(String gwasMpTermName) {
		this.gwasMpTermName = gwasMpTermName;
	}

	public String getGwasDiseaseTrait() {
		return gwasDiseaseTrait;
	}

	public void setGwasDiseaseTrait(String gwasDiseaseTrait) {
		this.gwasDiseaseTrait = gwasDiseaseTrait;
	}

	public String getGwasReportedGene() {
		return gwasReportedGene;
	}

	public void setGwasReportedGene(String gwasReportedGene) {
		this.gwasReportedGene = gwasReportedGene;
	}

	public String getGwasMappedGene() {
		return gwasMappedGene;
	}

	public void setGwasMappedGene(String gwasMappedGene) {
		this.gwasMappedGene = gwasMappedGene;
	}

	public String getGwasUpstreamGene() {
		return gwasUpstreamGene;
	}

	public void setGwasUpstreamGene(String gwasUpstreamGene) {
		this.gwasUpstreamGene = gwasUpstreamGene;
	}

	public String getGwasDownstreamGene() {
		return gwasDownstreamGene;
	}

	public void setGwasDownstreamGene(String gwasDownstreamGene) {
		this.gwasDownstreamGene = gwasDownstreamGene;
	}

	public String getGwasSnpId() {
		return gwasSnpId;
	}

	public void setGwasSnpId(String gwasSnpId) {
		this.gwasSnpId = gwasSnpId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AutosuggestBean that = (AutosuggestBean) o;

        if (autosuggestId != null ? !autosuggestId.equals(that.autosuggestId) : that.autosuggestId != null) return false;
		if (docType != null ? !docType.equals(that.docType) : that.docType != null) return false;
		if (mgiAccessionId != null ? !mgiAccessionId.equals(that.mgiAccessionId) : that.mgiAccessionId != null)
			return false;
		if (mgiAlleleAccessionIds != null ? !mgiAlleleAccessionIds.equals(that.mgiAlleleAccessionIds) : that.mgiAlleleAccessionIds != null)
			return false;
		if (alleleMgiAccessionId != null ? !alleleMgiAccessionId.equals(that.alleleMgiAccessionId) : that.alleleMgiAccessionId != null)
			return false;
		if (markerSymbol != null ? !markerSymbol.equals(that.markerSymbol) : that.markerSymbol != null) return false;
		if (markerName != null ? !markerName.equals(that.markerName) : that.markerName != null) return false;
		if (markerSynonym != null ? !markerSynonym.equals(that.markerSynonym) : that.markerSynonym != null)
			return false;
		if (humanGeneSymbol != null ? !humanGeneSymbol.equals(that.humanGeneSymbol) : that.humanGeneSymbol != null)
			return false;
		if (parameterName != null ? !parameterName.equals(that.parameterName) : that.parameterName != null)
			return false;
		if (hpId != null ? !hpId.equals(that.hpId) : that.hpId != null) return false;
		if (hpTerm != null ? !hpTerm.equals(that.hpTerm) : that.hpTerm != null) return false;
		if (hpSynonym != null ? !hpSynonym.equals(that.hpSynonym) : that.hpSynonym != null) return false;
		if (hpTermSynonym != null ? !hpTermSynonym.equals(that.hpTermSynonym) : that.hpTermSynonym != null)
			return false;
		if (hpmpId != null ? !hpmpId.equals(that.hpmpId) : that.hpmpId != null) return false;
		if (hpmpTerm != null ? !hpmpTerm.equals(that.hpmpTerm) : that.hpmpTerm != null) return false;
		if (altMpId != null ? !altMpId.equals(that.altMpId) : that.altMpId != null) return false;
		if (mpId != null ? !mpId.equals(that.mpId) : that.mpId != null) return false;
		if (mpTerm != null ? !mpTerm.equals(that.mpTerm) : that.mpTerm != null) return false;
		if (mpTermSynonym != null ? !mpTermSynonym.equals(that.mpTermSynonym) : that.mpTermSynonym != null)
			return false;
		if (mpNarrowSynonym != null ? !mpNarrowSynonym.equals(that.mpNarrowSynonym) : that.mpNarrowSynonym != null)
			return false;
		if (childMpId != null ? !childMpId.equals(that.childMpId) : that.childMpId != null) return false;
		if (childMpTerm != null ? !childMpTerm.equals(that.childMpTerm) : that.childMpTerm != null) return false;
		if (childMpTermSynonym != null ? !childMpTermSynonym.equals(that.childMpTermSynonym) : that.childMpTermSynonym != null)
			return false;
		if (parentMpId != null ? !parentMpId.equals(that.parentMpId) : that.parentMpId != null) return false;
		if (parentMpTerm != null ? !parentMpTerm.equals(that.parentMpTerm) : that.parentMpTerm != null) return false;
		if (parentMpTermSynonym != null ? !parentMpTermSynonym.equals(that.parentMpTermSynonym) : that.parentMpTermSynonym != null)
			return false;
		if (intermediateMpId != null ? !intermediateMpId.equals(that.intermediateMpId) : that.intermediateMpId != null)
			return false;
		if (intermediateMpTerm != null ? !intermediateMpTerm.equals(that.intermediateMpTerm) : that.intermediateMpTerm != null)
			return false;
		if (intermediateMpTermSynonym != null ? !intermediateMpTermSynonym.equals(that.intermediateMpTermSynonym) : that.intermediateMpTermSynonym != null)
			return false;
		if (topLevelMpId != null ? !topLevelMpId.equals(that.topLevelMpId) : that.topLevelMpId != null) return false;
		if (topLevelMpTerm != null ? !topLevelMpTerm.equals(that.topLevelMpTerm) : that.topLevelMpTerm != null)
			return false;
		if (topLevelMpTermSynonym != null ? !topLevelMpTermSynonym.equals(that.topLevelMpTermSynonym) : that.topLevelMpTermSynonym != null)
			return false;
		if (anatomyId != null ? !anatomyId.equals(that.anatomyId) : that.anatomyId != null) return false;
		if (altAnatomyId != null ? !altAnatomyId.equals(that.altAnatomyId) : that.altAnatomyId != null) return false;
		if (anatomyTerm != null ? !anatomyTerm.equals(that.anatomyTerm) : that.anatomyTerm != null) return false;
		if (anatomyTermSynonym != null ? !anatomyTermSynonym.equals(that.anatomyTermSynonym) : that.anatomyTermSynonym != null)
			return false;
		if (childAnatomyId != null ? !childAnatomyId.equals(that.childAnatomyId) : that.childAnatomyId != null)
			return false;
		if (childAnatomyTerm != null ? !childAnatomyTerm.equals(that.childAnatomyTerm) : that.childAnatomyTerm != null)
			return false;
		if (childAnatomyTermSynonym != null ? !childAnatomyTermSynonym.equals(that.childAnatomyTermSynonym) : that.childAnatomyTermSynonym != null)
			return false;
		if (parentAnatomyId != null ? !parentAnatomyId.equals(that.parentAnatomyId) : that.parentAnatomyId != null)
			return false;
		if (parentAnatomyTerm != null ? !parentAnatomyTerm.equals(that.parentAnatomyTerm) : that.parentAnatomyTerm != null)
			return false;
		if (parentAnatomyTermSynonym != null ? !parentAnatomyTermSynonym.equals(that.parentAnatomyTermSynonym) : that.parentAnatomyTermSynonym != null)
			return false;
		if (intermediateAnatomyId != null ? !intermediateAnatomyId.equals(that.intermediateAnatomyId) : that.intermediateAnatomyId != null)
			return false;
		if (intermediateAnatomyTerm != null ? !intermediateAnatomyTerm.equals(that.intermediateAnatomyTerm) : that.intermediateAnatomyTerm != null)
			return false;
		if (intermediateAnatomyTermSynonym != null ? !intermediateAnatomyTermSynonym.equals(that.intermediateAnatomyTermSynonym) : that.intermediateAnatomyTermSynonym != null)
			return false;
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
		if (diseaseId != null ? !diseaseId.equals(that.diseaseId) : that.diseaseId != null) return false;
		if (diseaseTerm != null ? !diseaseTerm.equals(that.diseaseTerm) : that.diseaseTerm != null) return false;
		if (diseaseAlts != null ? !diseaseAlts.equals(that.diseaseAlts) : that.diseaseAlts != null) return false;
		if (ikmcProject != null ? !ikmcProject.equals(that.ikmcProject) : that.ikmcProject != null) return false;
		if (alleleName != null ? !alleleName.equals(that.alleleName) : that.alleleName != null) return false;
		if (geneAllele != null ? !geneAllele.equals(that.geneAllele) : that.geneAllele != null) return false;
		if (autosuggest != null ? !autosuggest.equals(that.autosuggest) : that.autosuggest != null) return false;
		if (gwasMgiGeneId != null ? !gwasMgiGeneId.equals(that.gwasMgiGeneId) : that.gwasMgiGeneId != null)
			return false;
		if (gwasMgiGeneSymbol != null ? !gwasMgiGeneSymbol.equals(that.gwasMgiGeneSymbol) : that.gwasMgiGeneSymbol != null)
			return false;
		if (gwasMgiAlleleId != null ? !gwasMgiAlleleId.equals(that.gwasMgiAlleleId) : that.gwasMgiAlleleId != null)
			return false;
		if (gwasMgiAlleleName != null ? !gwasMgiAlleleName.equals(that.gwasMgiAlleleName) : that.gwasMgiAlleleName != null)
			return false;
		if (gwasMpTermId != null ? !gwasMpTermId.equals(that.gwasMpTermId) : that.gwasMpTermId != null) return false;
		if (gwasMpTermName != null ? !gwasMpTermName.equals(that.gwasMpTermName) : that.gwasMpTermName != null)
			return false;
		if (gwasDiseaseTrait != null ? !gwasDiseaseTrait.equals(that.gwasDiseaseTrait) : that.gwasDiseaseTrait != null)
			return false;
		if (gwasReportedGene != null ? !gwasReportedGene.equals(that.gwasReportedGene) : that.gwasReportedGene != null)
			return false;
		if (gwasMappedGene != null ? !gwasMappedGene.equals(that.gwasMappedGene) : that.gwasMappedGene != null)
			return false;
		if (gwasUpstreamGene != null ? !gwasUpstreamGene.equals(that.gwasUpstreamGene) : that.gwasUpstreamGene != null)
			return false;
		if (gwasDownstreamGene != null ? !gwasDownstreamGene.equals(that.gwasDownstreamGene) : that.gwasDownstreamGene != null)
			return false;
		return gwasSnpId != null ? gwasSnpId.equals(that.gwasSnpId) : that.gwasSnpId == null;
	}

	@Override
	public int hashCode() {
		int result = docType != null ? docType.hashCode() : 0;
        result = 31 * result + (autosuggestId != null ? autosuggestId.hashCode() : 0);
		result = 31 * result + (mgiAccessionId != null ? mgiAccessionId.hashCode() : 0);
		result = 31 * result + (mgiAlleleAccessionIds != null ? mgiAlleleAccessionIds.hashCode() : 0);
		result = 31 * result + (alleleMgiAccessionId != null ? alleleMgiAccessionId.hashCode() : 0);
		result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
		result = 31 * result + (markerName != null ? markerName.hashCode() : 0);
		result = 31 * result + (markerSynonym != null ? markerSynonym.hashCode() : 0);
		result = 31 * result + (humanGeneSymbol != null ? humanGeneSymbol.hashCode() : 0);
		result = 31 * result + (parameterName != null ? parameterName.hashCode() : 0);
		result = 31 * result + (hpId != null ? hpId.hashCode() : 0);
		result = 31 * result + (hpTerm != null ? hpTerm.hashCode() : 0);
		result = 31 * result + (hpSynonym != null ? hpSynonym.hashCode() : 0);
		result = 31 * result + (hpTermSynonym != null ? hpTermSynonym.hashCode() : 0);
		result = 31 * result + (hpmpId != null ? hpmpId.hashCode() : 0);
		result = 31 * result + (hpmpTerm != null ? hpmpTerm.hashCode() : 0);
		result = 31 * result + (altMpId != null ? altMpId.hashCode() : 0);
		result = 31 * result + (mpId != null ? mpId.hashCode() : 0);
		result = 31 * result + (mpTerm != null ? mpTerm.hashCode() : 0);
		result = 31 * result + (mpTermSynonym != null ? mpTermSynonym.hashCode() : 0);
		result = 31 * result + (mpNarrowSynonym != null ? mpNarrowSynonym.hashCode() : 0);
		result = 31 * result + (childMpId != null ? childMpId.hashCode() : 0);
		result = 31 * result + (childMpTerm != null ? childMpTerm.hashCode() : 0);
		result = 31 * result + (childMpTermSynonym != null ? childMpTermSynonym.hashCode() : 0);
		result = 31 * result + (parentMpId != null ? parentMpId.hashCode() : 0);
		result = 31 * result + (parentMpTerm != null ? parentMpTerm.hashCode() : 0);
		result = 31 * result + (parentMpTermSynonym != null ? parentMpTermSynonym.hashCode() : 0);
		result = 31 * result + (intermediateMpId != null ? intermediateMpId.hashCode() : 0);
		result = 31 * result + (intermediateMpTerm != null ? intermediateMpTerm.hashCode() : 0);
		result = 31 * result + (intermediateMpTermSynonym != null ? intermediateMpTermSynonym.hashCode() : 0);
		result = 31 * result + (topLevelMpId != null ? topLevelMpId.hashCode() : 0);
		result = 31 * result + (topLevelMpTerm != null ? topLevelMpTerm.hashCode() : 0);
		result = 31 * result + (topLevelMpTermSynonym != null ? topLevelMpTermSynonym.hashCode() : 0);
		result = 31 * result + (anatomyId != null ? anatomyId.hashCode() : 0);
		result = 31 * result + (altAnatomyId != null ? altAnatomyId.hashCode() : 0);
		result = 31 * result + (anatomyTerm != null ? anatomyTerm.hashCode() : 0);
		result = 31 * result + (anatomyTermSynonym != null ? anatomyTermSynonym.hashCode() : 0);
		result = 31 * result + (childAnatomyId != null ? childAnatomyId.hashCode() : 0);
		result = 31 * result + (childAnatomyTerm != null ? childAnatomyTerm.hashCode() : 0);
		result = 31 * result + (childAnatomyTermSynonym != null ? childAnatomyTermSynonym.hashCode() : 0);
		result = 31 * result + (parentAnatomyId != null ? parentAnatomyId.hashCode() : 0);
		result = 31 * result + (parentAnatomyTerm != null ? parentAnatomyTerm.hashCode() : 0);
		result = 31 * result + (parentAnatomyTermSynonym != null ? parentAnatomyTermSynonym.hashCode() : 0);
		result = 31 * result + (intermediateAnatomyId != null ? intermediateAnatomyId.hashCode() : 0);
		result = 31 * result + (intermediateAnatomyTerm != null ? intermediateAnatomyTerm.hashCode() : 0);
		result = 31 * result + (intermediateAnatomyTermSynonym != null ? intermediateAnatomyTermSynonym.hashCode() : 0);
		result = 31 * result + (topLevelAnatomyId != null ? topLevelAnatomyId.hashCode() : 0);
		result = 31 * result + (topLevelAnatomyTerm != null ? topLevelAnatomyTerm.hashCode() : 0);
		result = 31 * result + (topLevelAnatomyTermSynonym != null ? topLevelAnatomyTermSynonym.hashCode() : 0);
		result = 31 * result + (selectedTopLevelAnatomyId != null ? selectedTopLevelAnatomyId.hashCode() : 0);
		result = 31 * result + (selectedTopLevelAnatomyTerm != null ? selectedTopLevelAnatomyTerm.hashCode() : 0);
		result = 31 * result + (selectedTopLevelAnatomyTermSynonym != null ? selectedTopLevelAnatomyTermSynonym.hashCode() : 0);
		result = 31 * result + (diseaseId != null ? diseaseId.hashCode() : 0);
		result = 31 * result + (diseaseTerm != null ? diseaseTerm.hashCode() : 0);
		result = 31 * result + (diseaseAlts != null ? diseaseAlts.hashCode() : 0);
		result = 31 * result + (ikmcProject != null ? ikmcProject.hashCode() : 0);
		result = 31 * result + (alleleName != null ? alleleName.hashCode() : 0);
		result = 31 * result + (geneAllele != null ? geneAllele.hashCode() : 0);
		result = 31 * result + (autosuggest != null ? autosuggest.hashCode() : 0);
		result = 31 * result + (gwasMgiGeneId != null ? gwasMgiGeneId.hashCode() : 0);
		result = 31 * result + (gwasMgiGeneSymbol != null ? gwasMgiGeneSymbol.hashCode() : 0);
		result = 31 * result + (gwasMgiAlleleId != null ? gwasMgiAlleleId.hashCode() : 0);
		result = 31 * result + (gwasMgiAlleleName != null ? gwasMgiAlleleName.hashCode() : 0);
		result = 31 * result + (gwasMpTermId != null ? gwasMpTermId.hashCode() : 0);
		result = 31 * result + (gwasMpTermName != null ? gwasMpTermName.hashCode() : 0);
		result = 31 * result + (gwasDiseaseTrait != null ? gwasDiseaseTrait.hashCode() : 0);
		result = 31 * result + (gwasReportedGene != null ? gwasReportedGene.hashCode() : 0);
		result = 31 * result + (gwasMappedGene != null ? gwasMappedGene.hashCode() : 0);
		result = 31 * result + (gwasUpstreamGene != null ? gwasUpstreamGene.hashCode() : 0);
		result = 31 * result + (gwasDownstreamGene != null ? gwasDownstreamGene.hashCode() : 0);
		result = 31 * result + (gwasSnpId != null ? gwasSnpId.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "AutosuggestBean{" +
				"docType='" + docType + '\'' +
                ", autosuggestId='" + autosuggestId + '\'' +
				", mgiAccessionId='" + mgiAccessionId + '\'' +
				", mgiAlleleAccessionIds=" + mgiAlleleAccessionIds +
				", alleleMgiAccessionId='" + alleleMgiAccessionId + '\'' +
				", markerSymbol='" + markerSymbol + '\'' +
				", markerName='" + markerName + '\'' +
				", markerSynonym='" + markerSynonym + '\'' +
				", humanGeneSymbol='" + humanGeneSymbol + '\'' +
				", parameterName='" + parameterName + '\'' +
				", hpId='" + hpId + '\'' +
				", hpTerm='" + hpTerm + '\'' +
				", hpSynonym='" + hpSynonym + '\'' +
				", hpTermSynonym='" + hpTermSynonym + '\'' +
				", hpmpId='" + hpmpId + '\'' +
				", hpmpTerm='" + hpmpTerm + '\'' +
				", altMpId='" + altMpId + '\'' +
				", mpId='" + mpId + '\'' +
				", mpTerm='" + mpTerm + '\'' +
				", mpTermSynonym='" + mpTermSynonym + '\'' +
				", mpNarrowSynonym='" + mpNarrowSynonym + '\'' +
				", childMpId='" + childMpId + '\'' +
				", childMpTerm='" + childMpTerm + '\'' +
				", childMpTermSynonym='" + childMpTermSynonym + '\'' +
				", parentMpId='" + parentMpId + '\'' +
				", parentMpTerm='" + parentMpTerm + '\'' +
				", parentMpTermSynonym='" + parentMpTermSynonym + '\'' +
				", intermediateMpId='" + intermediateMpId + '\'' +
				", intermediateMpTerm='" + intermediateMpTerm + '\'' +
				", intermediateMpTermSynonym='" + intermediateMpTermSynonym + '\'' +
				", topLevelMpId='" + topLevelMpId + '\'' +
				", topLevelMpTerm='" + topLevelMpTerm + '\'' +
				", topLevelMpTermSynonym='" + topLevelMpTermSynonym + '\'' +
				", anatomyId='" + anatomyId + '\'' +
				", altAnatomyId='" + altAnatomyId + '\'' +
				", anatomyTerm='" + anatomyTerm + '\'' +
				", anatomyTermSynonym='" + anatomyTermSynonym + '\'' +
				", childAnatomyId='" + childAnatomyId + '\'' +
				", childAnatomyTerm='" + childAnatomyTerm + '\'' +
				", childAnatomyTermSynonym='" + childAnatomyTermSynonym + '\'' +
				", parentAnatomyId='" + parentAnatomyId + '\'' +
				", parentAnatomyTerm='" + parentAnatomyTerm + '\'' +
				", parentAnatomyTermSynonym='" + parentAnatomyTermSynonym + '\'' +
				", intermediateAnatomyId='" + intermediateAnatomyId + '\'' +
				", intermediateAnatomyTerm='" + intermediateAnatomyTerm + '\'' +
				", intermediateAnatomyTermSynonym='" + intermediateAnatomyTermSynonym + '\'' +
				", topLevelAnatomyId='" + topLevelAnatomyId + '\'' +
				", topLevelAnatomyTerm='" + topLevelAnatomyTerm + '\'' +
				", topLevelAnatomyTermSynonym='" + topLevelAnatomyTermSynonym + '\'' +
				", selectedTopLevelAnatomyId='" + selectedTopLevelAnatomyId + '\'' +
				", selectedTopLevelAnatomyTerm='" + selectedTopLevelAnatomyTerm + '\'' +
				", selectedTopLevelAnatomyTermSynonym='" + selectedTopLevelAnatomyTermSynonym + '\'' +
				", diseaseId='" + diseaseId + '\'' +
				", diseaseTerm='" + diseaseTerm + '\'' +
				", diseaseAlts='" + diseaseAlts + '\'' +
				", ikmcProject='" + ikmcProject + '\'' +
				", alleleName='" + alleleName + '\'' +
				", geneAllele='" + geneAllele + '\'' +
				", autosuggest=" + autosuggest +
				", gwasMgiGeneId='" + gwasMgiGeneId + '\'' +
				", gwasMgiGeneSymbol='" + gwasMgiGeneSymbol + '\'' +
				", gwasMgiAlleleId='" + gwasMgiAlleleId + '\'' +
				", gwasMgiAlleleName='" + gwasMgiAlleleName + '\'' +
				", gwasMpTermId='" + gwasMpTermId + '\'' +
				", gwasMpTermName='" + gwasMpTermName + '\'' +
				", gwasDiseaseTrait='" + gwasDiseaseTrait + '\'' +
				", gwasReportedGene='" + gwasReportedGene + '\'' +
				", gwasMappedGene='" + gwasMappedGene + '\'' +
				", gwasUpstreamGene='" + gwasUpstreamGene + '\'' +
				", gwasDownstreamGene='" + gwasDownstreamGene + '\'' +
				", gwasSnpId='" + gwasSnpId + '\'' +
				'}';
	}
}
