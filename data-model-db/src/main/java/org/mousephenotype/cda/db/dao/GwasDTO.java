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

package org.mousephenotype.cda.db.dao;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author ckchen
 * 
 * This class encapsulates the code and data necessary to represent GWAS data
 * of an IMPC gene
 * 
 */
public class GwasDTO {
	
	public static final String MGI_GENE_ID = "mgi_gene_id";
	public static final String MGI_GENE_ID_CI = "mgi_gene_id_ci";
	
	public static final String MGI_GENE_SYMBOL = "mgi_gene_symbol";
	public static final String MGI_GENE_SYMBOL_CI = "mgi_gene_symbol_ci";
	
	public static final String MGI_ALLELE_ID = "mgi_allele_id";
	public static final String MGI_ALLELE_ID_CI = "mgi_allele_id_ci";
	
	public static final String MGI_ALLELE_NAME = "mgi_allele_name";
	public static final String MGI_ALLELE_NAME_CI = "mgi_allele_name_ci";
	
	public static final String PHENO_MAPPING_CATEGORY = "pheno_mapping_category";
	
	public static final String MP_TERM_ID = "mp_term_id";
	public static final String MP_TERM_ID_CI = "mp_term_id_ci";
	public static final String MP_TERM_ID_URL = "mp_term_id_url";
	
	public static final String MP_TERM_NAME = "mp_term_name";
	public static final String MP_TERM_NAME_CI = "mp_term_name_ci";
	
	public static final String MOUSE_GENDER = "mouse_gender";
	
	public static final String PVALUE = "p_value";
	
	public static final String DISEASE_TRAIT = "disease_trait";
	public static final String DISEASE_TRAIT_CI = "disease_trait_ci";
	public static final String DISEASE_TRAIT_ID_URL = "disease_trait_id_url";

	public static final String REPORTED_GENE = "reported_gene";
	public static final String REPORTED_GENE_CI = "reported_gene_ci";
	
	public static final String MAPPED_GENE = "mapped_gene";
	public static final String MAPPED_GENE_CI = "mapped_gene_ci";
	
	public static final String UPSTREAM_GENE = "upstream_gene";
	public static final String UPSTREAM_GENE_CI = "upstream_gene_ci";
	
	public static final String DOWNSTREAM_GENE = "downstream_gene";
	public static final String DOWNSTREAM_GENE_CI = "downstream_gene_ci";
	
	public static final String SNP_ID = "snp_id";
	public static final String SNP_ID_CI = "snp_id_ci";
    
	@Field(MGI_GENE_ID)
	private String mgi_gene_id;
	
	@Field(MGI_GENE_ID_CI)
	private String mgi_gene_id_ci;

	@Field(MGI_GENE_SYMBOL)
	private String mgi_gene_symbol;
	
	@Field(MGI_GENE_SYMBOL_CI)
	private String mgi_gene_symbol_ci;

	@Field(MGI_ALLELE_ID)
	private String mgi_allele_id;
	
	@Field(MGI_ALLELE_ID_CI)
	private String mgi_allele_id_ci;

	@Field(MGI_ALLELE_NAME)
	private String mgi_allele_name;
	
	@Field(MGI_ALLELE_NAME_CI)
	private String mgi_allele_name_ci;
	
	@Field(PHENO_MAPPING_CATEGORY)
	private String pheno_mapping_category;
	
	@Field(MP_TERM_ID)
	private String mp_term_id;
	
	@Field(MP_TERM_ID_CI)
	private String mp_term_id_ci;
	
	@Field(MP_TERM_ID_URL)
	private String mp_term_id_url;
	
	@Field(MP_TERM_NAME)
	private String mp_term_name;
	
	@Field(MP_TERM_NAME_CI)
	private String mp_term_name_ci;
	
	@Field(MOUSE_GENDER)
	private String mouse_gender;
	
	@Field(PVALUE)
	private float p_value;
	
	@Field(DISEASE_TRAIT)
	private String disease_trait;
	
	@Field(DISEASE_TRAIT_CI)
	private String disease_trait_ci;
	
	@Field(DISEASE_TRAIT_ID_URL)
	private String disease_trait_id_url;
	
	@Field(REPORTED_GENE)
	private String reported_gene;
	
	@Field(REPORTED_GENE_CI)
	private String reported_gene_ci;
	
	@Field(MAPPED_GENE)
	private String mapped_gene;
	
	@Field(MAPPED_GENE_CI)
	private String mapped_geneCi;
	
	@Field(UPSTREAM_GENE)
	private String upstream_gene;
	
	@Field(UPSTREAM_GENE_CI)
	private String upstream_gene_ci;
	
	@Field(DOWNSTREAM_GENE)
	private String downstream_gene;
	
	@Field(DOWNSTREAM_GENE_CI)
	private String downstream_gene_ci;
	
	@Field(SNP_ID)
	private String snp_id;
	
	@Field(SNP_ID_CI)
	private String snp_id_ci;

	public String getMgiGeneId() {
		return mgi_gene_id;
	}

	public void setMgiGeneId(String mgi_gene_id) {
		this.mgi_gene_id = mgi_gene_id;
		this.mgi_gene_id_ci = mgi_gene_id;
	}
	
	public String getMgiGeneSymbol() {
		return mgi_gene_symbol;
	}

	public void setMgiGeneSymbol(String mgi_gene_symbol) {
		this.mgi_gene_symbol = mgi_gene_symbol;
		this.mgi_gene_symbol_ci = mgi_gene_symbol;
	}

	public String getMgiAlleleId() {
		return mgi_allele_id;
	}

	public void setMgiAlleleId(String mgi_allele_id) {
		this.mgi_allele_id = mgi_allele_id;
		this.mgi_allele_id_ci = mgi_allele_id;
	}

	public String getMgiAlleleName() {
		return mgi_allele_name;
	}

	public void setMgiAlleleName(String mgi_allele_name) {
		this.mgi_allele_name = mgi_allele_name;
		this.mgi_allele_name_ci = mgi_allele_name;
	}

	public String getPhenoMappingCategory() {
		return pheno_mapping_category;
	}

	public void setPhenoMappingCategory(String pheno_mapping_category) {
		this.pheno_mapping_category = pheno_mapping_category;
	}

	public String getMpTermId() {
		return mp_term_id;
	}
	
	public void setMpTermId(String mp_term_id) {
		this.mp_term_id = mp_term_id;
		this.mp_term_id_ci = mp_term_id;
	}

	public String getMpTermIdUrl() {
		return mp_term_id_url;
	}
	
	public void setMpTermIdUrl(String mp_term_id_url) {
		this.mp_term_id_url = mp_term_id_url;
	}
	
	public String getMpTermName() {
		return mp_term_name;
	}

	public void setMpTermName(String mp_term_name) {
		this.mp_term_name = mp_term_name;
		this.mp_term_name_ci = mp_term_name;
	}

	public String getMouseGender() {
		return mouse_gender;
	}

	public void setMouseGender(String mouse_gender) {
		this.mouse_gender = mouse_gender;
	}

	public float getPvalue() {
		return p_value;
	}

	public void setPvalue(float p_value) {
		this.p_value = p_value;
	}

	public String getDiseaseTrait() {
		return disease_trait;
	}

	public void setDiseaseTrait(String disease_trait) {
		this.disease_trait = disease_trait;
		this.disease_trait_ci = disease_trait;
	}

	public String getDiseaseTraitIdUrl() {
		return disease_trait_id_url;
	}

	public void setDiseaseTraitIdUrl(String disease_trait_id_url) {
		this.disease_trait_id_url = disease_trait_id_url;
	}
	
	public String getReportedGene() {
		return reported_gene;
	}

	public void setReportedGene(String reported_gene) {
		this.reported_gene = reported_gene;
		this.reported_gene = reported_gene;
	}

	public String getMappedGene() {
		return mapped_gene;
	}

	public void setMappedGene(String mapped_gene) {
		this.mapped_gene = mapped_gene;
		this.mapped_geneCi = mapped_gene;
	}

	public String getUpstreamGene() {
		return upstream_gene;
	}

	public void setUpstreamGene(String upstream_gene) {
		this.upstream_gene = upstream_gene;
		this.upstream_gene_ci = upstream_gene;
	}

	public String getDownstreamGene() {
		return downstream_gene;
	}

	public void setDownstreamGene(String downstream_gene) {
		this.downstream_gene = downstream_gene;
		this.downstream_gene_ci = downstream_gene;
	}

	public String getSnpId() {
		return snp_id;
	}

	public void setSnpId(String snp_id) {
		this.snp_id = snp_id;
		this.snp_id_ci = snp_id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((disease_trait == null) ? 0 : disease_trait.hashCode());
		result = prime * result + ((disease_trait_ci == null) ? 0 : disease_trait_ci.hashCode());
		result = prime * result + ((disease_trait_id_url == null) ? 0 : disease_trait_id_url.hashCode());
		result = prime * result + ((downstream_gene == null) ? 0 : downstream_gene.hashCode());
		result = prime * result + ((downstream_gene_ci == null) ? 0 : downstream_gene_ci.hashCode());
		result = prime * result + ((mapped_gene == null) ? 0 : mapped_gene.hashCode());
		result = prime * result + ((mapped_geneCi == null) ? 0 : mapped_geneCi.hashCode());
		result = prime * result + ((mgi_allele_id == null) ? 0 : mgi_allele_id.hashCode());
		result = prime * result + ((mgi_allele_id_ci == null) ? 0 : mgi_allele_id_ci.hashCode());
		result = prime * result + ((mgi_allele_name == null) ? 0 : mgi_allele_name.hashCode());
		result = prime * result + ((mgi_allele_name_ci == null) ? 0 : mgi_allele_name_ci.hashCode());
		result = prime * result + ((mgi_gene_id == null) ? 0 : mgi_gene_id.hashCode());
		result = prime * result + ((mgi_gene_id_ci == null) ? 0 : mgi_gene_id_ci.hashCode());
		result = prime * result + ((mgi_gene_symbol == null) ? 0 : mgi_gene_symbol.hashCode());
		result = prime * result + ((mgi_gene_symbol_ci == null) ? 0 : mgi_gene_symbol_ci.hashCode());
		result = prime * result + ((mouse_gender == null) ? 0 : mouse_gender.hashCode());
		result = prime * result + ((mp_term_id == null) ? 0 : mp_term_id.hashCode());
		result = prime * result + ((mp_term_id_ci == null) ? 0 : mp_term_id_ci.hashCode());
		result = prime * result + ((mp_term_id_url == null) ? 0 : mp_term_id_url.hashCode());
		result = prime * result + ((mp_term_name == null) ? 0 : mp_term_name.hashCode());
		result = prime * result + ((mp_term_name_ci == null) ? 0 : mp_term_name_ci.hashCode());
		result = prime * result + Float.floatToIntBits(p_value);
		result = prime * result + ((pheno_mapping_category == null) ? 0 : pheno_mapping_category.hashCode());
		result = prime * result + ((reported_gene == null) ? 0 : reported_gene.hashCode());
		result = prime * result + ((reported_gene_ci == null) ? 0 : reported_gene_ci.hashCode());
		result = prime * result + ((snp_id == null) ? 0 : snp_id.hashCode());
		result = prime * result + ((snp_id_ci == null) ? 0 : snp_id_ci.hashCode());
		result = prime * result + ((upstream_gene == null) ? 0 : upstream_gene.hashCode());
		result = prime * result + ((upstream_gene_ci == null) ? 0 : upstream_gene_ci.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GwasDTO other = (GwasDTO) obj;
		if (disease_trait == null) {
			if (other.disease_trait != null)
				return false;
		} else if (!disease_trait.equals(other.disease_trait))
			return false;
		if (disease_trait_ci == null) {
			if (other.disease_trait_ci != null)
				return false;
		} else if (!disease_trait_ci.equals(other.disease_trait_ci))
			return false;
		if (disease_trait_id_url == null) {
			if (other.disease_trait_id_url != null)
				return false;
		} else if (!disease_trait_id_url.equals(other.disease_trait_id_url))
			return false;
		if (downstream_gene == null) {
			if (other.downstream_gene != null)
				return false;
		} else if (!downstream_gene.equals(other.downstream_gene))
			return false;
		if (downstream_gene_ci == null) {
			if (other.downstream_gene_ci != null)
				return false;
		} else if (!downstream_gene_ci.equals(other.downstream_gene_ci))
			return false;
		if (mapped_gene == null) {
			if (other.mapped_gene != null)
				return false;
		} else if (!mapped_gene.equals(other.mapped_gene))
			return false;
		if (mapped_geneCi == null) {
			if (other.mapped_geneCi != null)
				return false;
		} else if (!mapped_geneCi.equals(other.mapped_geneCi))
			return false;
		if (mgi_allele_id == null) {
			if (other.mgi_allele_id != null)
				return false;
		} else if (!mgi_allele_id.equals(other.mgi_allele_id))
			return false;
		if (mgi_allele_id_ci == null) {
			if (other.mgi_allele_id_ci != null)
				return false;
		} else if (!mgi_allele_id_ci.equals(other.mgi_allele_id_ci))
			return false;
		if (mgi_allele_name == null) {
			if (other.mgi_allele_name != null)
				return false;
		} else if (!mgi_allele_name.equals(other.mgi_allele_name))
			return false;
		if (mgi_allele_name_ci == null) {
			if (other.mgi_allele_name_ci != null)
				return false;
		} else if (!mgi_allele_name_ci.equals(other.mgi_allele_name_ci))
			return false;
		if (mgi_gene_id == null) {
			if (other.mgi_gene_id != null)
				return false;
		} else if (!mgi_gene_id.equals(other.mgi_gene_id))
			return false;
		if (mgi_gene_id_ci == null) {
			if (other.mgi_gene_id_ci != null)
				return false;
		} else if (!mgi_gene_id_ci.equals(other.mgi_gene_id_ci))
			return false;
		if (mgi_gene_symbol == null) {
			if (other.mgi_gene_symbol != null)
				return false;
		} else if (!mgi_gene_symbol.equals(other.mgi_gene_symbol))
			return false;
		if (mgi_gene_symbol_ci == null) {
			if (other.mgi_gene_symbol_ci != null)
				return false;
		} else if (!mgi_gene_symbol_ci.equals(other.mgi_gene_symbol_ci))
			return false;
		if (mouse_gender == null) {
			if (other.mouse_gender != null)
				return false;
		} else if (!mouse_gender.equals(other.mouse_gender))
			return false;
		if (mp_term_id == null) {
			if (other.mp_term_id != null)
				return false;
		} else if (!mp_term_id.equals(other.mp_term_id))
			return false;
		if (mp_term_id_ci == null) {
			if (other.mp_term_id_ci != null)
				return false;
		} else if (!mp_term_id_ci.equals(other.mp_term_id_ci))
			return false;
		if (mp_term_id_url == null) {
			if (other.mp_term_id_url != null)
				return false;
		} else if (!mp_term_id_url.equals(other.mp_term_id_url))
			return false;
		if (mp_term_name == null) {
			if (other.mp_term_name != null)
				return false;
		} else if (!mp_term_name.equals(other.mp_term_name))
			return false;
		if (mp_term_name_ci == null) {
			if (other.mp_term_name_ci != null)
				return false;
		} else if (!mp_term_name_ci.equals(other.mp_term_name_ci))
			return false;
		if (Float.floatToIntBits(p_value) != Float.floatToIntBits(other.p_value))
			return false;
		if (pheno_mapping_category == null) {
			if (other.pheno_mapping_category != null)
				return false;
		} else if (!pheno_mapping_category.equals(other.pheno_mapping_category))
			return false;
		if (reported_gene == null) {
			if (other.reported_gene != null)
				return false;
		} else if (!reported_gene.equals(other.reported_gene))
			return false;
		if (reported_gene_ci == null) {
			if (other.reported_gene_ci != null)
				return false;
		} else if (!reported_gene_ci.equals(other.reported_gene_ci))
			return false;
		if (snp_id == null) {
			if (other.snp_id != null)
				return false;
		} else if (!snp_id.equals(other.snp_id))
			return false;
		if (snp_id_ci == null) {
			if (other.snp_id_ci != null)
				return false;
		} else if (!snp_id_ci.equals(other.snp_id_ci))
			return false;
		if (upstream_gene == null) {
			if (other.upstream_gene != null)
				return false;
		} else if (!upstream_gene.equals(other.upstream_gene))
			return false;
		if (upstream_gene_ci == null) {
			if (other.upstream_gene_ci != null)
				return false;
		} else if (!upstream_gene_ci.equals(other.upstream_gene_ci))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GwasDTO [mgi_gene_id=" + mgi_gene_id + ", mgi_gene_id_ci=" + mgi_gene_id_ci + ", mgi_gene_symbol="
				+ mgi_gene_symbol + ", mgi_gene_symbol_ci=" + mgi_gene_symbol_ci + ", mgi_allele_id=" + mgi_allele_id
				+ ", mgi_allele_id_ci=" + mgi_allele_id_ci + ", mgi_allele_name=" + mgi_allele_name
				+ ", mgi_allele_name_ci=" + mgi_allele_name_ci + ", pheno_mapping_category=" + pheno_mapping_category
				+ ", mp_term_id=" + mp_term_id + ", mp_term_id_ci=" + mp_term_id_ci + ", mp_term_id_url="
				+ mp_term_id_url + ", mp_term_name=" + mp_term_name + ", mp_term_name_ci=" + mp_term_name_ci
				+ ", mouse_gender=" + mouse_gender + ", p_value=" + p_value + ", disease_trait=" + disease_trait
				+ ", disease_trait_ci=" + disease_trait_ci + ", disease_trait_id_url=" + disease_trait_id_url
				+ ", reported_gene=" + reported_gene + ", reported_gene_ci=" + reported_gene_ci + ", mapped_gene="
				+ mapped_gene + ", mapped_geneCi=" + mapped_geneCi + ", upstream_gene=" + upstream_gene
				+ ", upstream_gene_ci=" + upstream_gene_ci + ", downstream_gene=" + downstream_gene
				+ ", downstream_gene_ci=" + downstream_gene_ci + ", snp_id=" + snp_id + ", snp_id_ci=" + snp_id_ci
				+ "]";
	}

	
}
