package org.mousephenotype.cda.loads.legacy.dccimport.imits;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "phenotype_attempt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PhenotypeAttemptBean {
	private String colony_name;
	private String cre_excision_required;
	private String id;
	private Boolean is_active;
	private String mi_plan_id;
	private String mouse_allele_type;
	private String number_of_cre_matings_successful;
	private Boolean phenotyping_complete;
	private String phenotyping_experiments_started;
	private Boolean phenotyping_started;
	private Boolean rederivation_complete;
	private Boolean rederivation_started;
	private Boolean report_to_public;
	private Boolean tat_cre;
	private String distribution_centres_formatted_display;
	private String status_name;

	@XmlElement
	private Map<String, String> status_dates = new HashMap<String, String>();

	private String marker_symbol;
	private String mouse_allele_symbol_superscript;
	private String mouse_allele_symbol;
	private String allele_symbol;
	private String mi_attempt_colony_background_strain_name;
	private String mi_attempt_colony_background_mgi_strain_accession_id;
	private String mi_attempt_colony_background_mgi_strain_name;
	private String colony_background_strain_mgi_accession;
	private String colony_background_strain_mgi_name;
	private String mgi_accession_id;
	private String consortium_name;
	private String production_centre_name;
	private String mi_attempt_colony_name;
	private String deleter_strain_name;
	private String [] distribution_centres_attributes;
	private String colony_background_strain_name;
	private String qc_southern_blot_result;
	private String qc_five_prime_lr_pcr_result;
	private String qc_five_prime_cassette_integrity_result;
	private String qc_tv_backbone_assay_result;
	private String qc_neo_count_qpcr_result;
	private String qc_lacz_count_qpcr_result;
	private String qc_neo_sr_pcr_result;
	private String qc_loa_qpcr_result;
	private String qc_homozygous_loa_sr_pcr_result;
	private String qc_lacz_sr_pcr_result;
	private String qc_mutant_specific_sr_pcr_result;
	private String qc_loxp_confirmation_result;
	private String qc_three_prime_lr_pcr_result;
	private String qc_critical_region_qpcr_result;
	private String qc_loxp_srpcr_result;
	private String qc_loxp_srpcr_and_sequencing_result;
	public String getColony_name() {
		return colony_name;
	}
	public void setColony_name(String colony_name) {
		this.colony_name = colony_name;
	}
	public String getCre_excision_required() {
		return cre_excision_required;
	}
	public void setCre_excision_required(String cre_excision_required) {
		this.cre_excision_required = cre_excision_required;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Boolean getIs_active() {
		return is_active;
	}
	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}
	public String getMi_plan_id() {
		return mi_plan_id;
	}
	public void setMi_plan_id(String mi_plan_id) {
		this.mi_plan_id = mi_plan_id;
	}
	public String getMouse_allele_type() {
		return mouse_allele_type;
	}
	public void setMouse_allele_type(String mouse_allele_type) {
		this.mouse_allele_type = mouse_allele_type;
	}
	public String getNumber_of_cre_matings_successful() {
		return number_of_cre_matings_successful;
	}
	public void setNumber_of_cre_matings_successful(
			String number_of_cre_matings_successful) {
		this.number_of_cre_matings_successful = number_of_cre_matings_successful;
	}
	public Boolean getPhenotyping_complete() {
		return phenotyping_complete;
	}
	public void setPhenotyping_complete(Boolean phenotyping_complete) {
		this.phenotyping_complete = phenotyping_complete;
	}
	public String getPhenotyping_experiments_started() {
		return phenotyping_experiments_started;
	}
	public void setPhenotyping_experiments_started(
			String phenotyping_experiments_started) {
		this.phenotyping_experiments_started = phenotyping_experiments_started;
	}
	public Boolean getPhenotyping_started() {
		return phenotyping_started;
	}
	public void setPhenotyping_started(Boolean phenotyping_started) {
		this.phenotyping_started = phenotyping_started;
	}
	public Boolean getRederivation_complete() {
		return rederivation_complete;
	}
	public void setRederivation_complete(Boolean rederivation_complete) {
		this.rederivation_complete = rederivation_complete;
	}
	public Boolean getRederivation_started() {
		return rederivation_started;
	}
	public void setRederivation_started(Boolean rederivation_started) {
		this.rederivation_started = rederivation_started;
	}
	public Boolean getReport_to_public() {
		return report_to_public;
	}
	public void setReport_to_public(Boolean report_to_public) {
		this.report_to_public = report_to_public;
	}
	public Boolean getTat_cre() {
		return tat_cre;
	}
	public void setTat_cre(Boolean tat_cre) {
		this.tat_cre = tat_cre;
	}
	public String getDistribution_centres_formatted_display() {
		return distribution_centres_formatted_display;
	}
	public void setDistribution_centres_formatted_display(
			String distribution_centres_formatted_display) {
		this.distribution_centres_formatted_display = distribution_centres_formatted_display;
	}
	public String getStatus_name() {
		return status_name;
	}
	public void setStatus_name(String status_name) {
		this.status_name = status_name;
	}
	public Map<String, String> getStatus_dates() {
		return status_dates;
	}
	public void setStatus_dates(Map<String, String> status_dates) {
		this.status_dates = status_dates;
	}
	public String getMarker_symbol() {
		return marker_symbol;
	}
	public void setMarker_symbol(String marker_symbol) {
		this.marker_symbol = marker_symbol;
	}
	public String getMouse_allele_symbol_superscript() {
		return mouse_allele_symbol_superscript;
	}
	public void setMouse_allele_symbol_superscript(
			String mouse_allele_symbol_superscript) {
		this.mouse_allele_symbol_superscript = mouse_allele_symbol_superscript;
	}
	public String getMouse_allele_symbol() {
		return mouse_allele_symbol;
	}
	public void setMouse_allele_symbol(String mouse_allele_symbol) {
		this.mouse_allele_symbol = mouse_allele_symbol;
	}
	public String getAllele_symbol() {
		return allele_symbol;
	}
	public void setAllele_symbol(String allele_symbol) {
		this.allele_symbol = allele_symbol;
	}
	public String getMi_attempt_colony_background_strain_name() {
		return mi_attempt_colony_background_strain_name;
	}
	public void setMi_attempt_colony_background_strain_name(
			String mi_attempt_colony_background_strain_name) {
		this.mi_attempt_colony_background_strain_name = mi_attempt_colony_background_strain_name;
	}
	public String getMi_attempt_colony_background_mgi_strain_accession_id() {
		return mi_attempt_colony_background_mgi_strain_accession_id;
	}
	public void setMi_attempt_colony_background_mgi_strain_accession_id(
			String mi_attempt_colony_background_mgi_strain_accession_id) {
		this.mi_attempt_colony_background_mgi_strain_accession_id = mi_attempt_colony_background_mgi_strain_accession_id;
	}
	public String getMi_attempt_colony_background_mgi_strain_name() {
		return mi_attempt_colony_background_mgi_strain_name;
	}
	public void setMi_attempt_colony_background_mgi_strain_name(
			String mi_attempt_colony_background_mgi_strain_name) {
		this.mi_attempt_colony_background_mgi_strain_name = mi_attempt_colony_background_mgi_strain_name;
	}
	public String getColony_background_strain_mgi_accession() {
		return colony_background_strain_mgi_accession;
	}
	public void setColony_background_strain_mgi_accession(
			String colony_background_strain_mgi_accession) {
		this.colony_background_strain_mgi_accession = colony_background_strain_mgi_accession;
	}
	public String getColony_background_strain_mgi_name() {
		return colony_background_strain_mgi_name;
	}
	public void setColony_background_strain_mgi_name(
			String colony_background_strain_mgi_name) {
		this.colony_background_strain_mgi_name = colony_background_strain_mgi_name;
	}
	public String getMgi_accession_id() {
		return mgi_accession_id;
	}
	public void setMgi_accession_id(String mgi_accession_id) {
		this.mgi_accession_id = mgi_accession_id;
	}
	public String getConsortium_name() {
		return consortium_name;
	}
	public void setConsortium_name(String consortium_name) {
		this.consortium_name = consortium_name;
	}
	public String getProduction_centre_name() {
		return production_centre_name;
	}
	public void setProduction_centre_name(String production_centre_name) {
		this.production_centre_name = production_centre_name;
	}
	public String getMi_attempt_colony_name() {
		return mi_attempt_colony_name;
	}
	public void setMi_attempt_colony_name(String mi_attempt_colony_name) {
		this.mi_attempt_colony_name = mi_attempt_colony_name;
	}
	public String getDeleter_strain_name() {
		return deleter_strain_name;
	}
	public void setDeleter_strain_name(String deleter_strain_name) {
		this.deleter_strain_name = deleter_strain_name;
	}
	public String[] getDistribution_centres_attributes() {
		return distribution_centres_attributes;
	}
	public void setDistribution_centres_attributes(
			String[] distribution_centres_attributes) {
		this.distribution_centres_attributes = distribution_centres_attributes;
	}
	public String getColony_background_strain_name() {
		return colony_background_strain_name;
	}
	public void setColony_background_strain_name(
			String colony_background_strain_name) {
		this.colony_background_strain_name = colony_background_strain_name;
	}
	public String getQc_southern_blot_result() {
		return qc_southern_blot_result;
	}
	public void setQc_southern_blot_result(String qc_southern_blot_result) {
		this.qc_southern_blot_result = qc_southern_blot_result;
	}
	public String getQc_five_prime_lr_pcr_result() {
		return qc_five_prime_lr_pcr_result;
	}
	public void setQc_five_prime_lr_pcr_result(String qc_five_prime_lr_pcr_result) {
		this.qc_five_prime_lr_pcr_result = qc_five_prime_lr_pcr_result;
	}
	public String getQc_five_prime_cassette_integrity_result() {
		return qc_five_prime_cassette_integrity_result;
	}
	public void setQc_five_prime_cassette_integrity_result(
			String qc_five_prime_cassette_integrity_result) {
		this.qc_five_prime_cassette_integrity_result = qc_five_prime_cassette_integrity_result;
	}
	public String getQc_tv_backbone_assay_result() {
		return qc_tv_backbone_assay_result;
	}
	public void setQc_tv_backbone_assay_result(String qc_tv_backbone_assay_result) {
		this.qc_tv_backbone_assay_result = qc_tv_backbone_assay_result;
	}
	public String getQc_neo_count_qpcr_result() {
		return qc_neo_count_qpcr_result;
	}
	public void setQc_neo_count_qpcr_result(String qc_neo_count_qpcr_result) {
		this.qc_neo_count_qpcr_result = qc_neo_count_qpcr_result;
	}
	public String getQc_lacz_count_qpcr_result() {
		return qc_lacz_count_qpcr_result;
	}
	public void setQc_lacz_count_qpcr_result(String qc_lacz_count_qpcr_result) {
		this.qc_lacz_count_qpcr_result = qc_lacz_count_qpcr_result;
	}
	public String getQc_neo_sr_pcr_result() {
		return qc_neo_sr_pcr_result;
	}
	public void setQc_neo_sr_pcr_result(String qc_neo_sr_pcr_result) {
		this.qc_neo_sr_pcr_result = qc_neo_sr_pcr_result;
	}
	public String getQc_loa_qpcr_result() {
		return qc_loa_qpcr_result;
	}
	public void setQc_loa_qpcr_result(String qc_loa_qpcr_result) {
		this.qc_loa_qpcr_result = qc_loa_qpcr_result;
	}
	public String getQc_homozygous_loa_sr_pcr_result() {
		return qc_homozygous_loa_sr_pcr_result;
	}
	public void setQc_homozygous_loa_sr_pcr_result(
			String qc_homozygous_loa_sr_pcr_result) {
		this.qc_homozygous_loa_sr_pcr_result = qc_homozygous_loa_sr_pcr_result;
	}
	public String getQc_lacz_sr_pcr_result() {
		return qc_lacz_sr_pcr_result;
	}
	public void setQc_lacz_sr_pcr_result(String qc_lacz_sr_pcr_result) {
		this.qc_lacz_sr_pcr_result = qc_lacz_sr_pcr_result;
	}
	public String getQc_mutant_specific_sr_pcr_result() {
		return qc_mutant_specific_sr_pcr_result;
	}
	public void setQc_mutant_specific_sr_pcr_result(
			String qc_mutant_specific_sr_pcr_result) {
		this.qc_mutant_specific_sr_pcr_result = qc_mutant_specific_sr_pcr_result;
	}
	public String getQc_loxp_confirmation_result() {
		return qc_loxp_confirmation_result;
	}
	public void setQc_loxp_confirmation_result(String qc_loxp_confirmation_result) {
		this.qc_loxp_confirmation_result = qc_loxp_confirmation_result;
	}
	public String getQc_three_prime_lr_pcr_result() {
		return qc_three_prime_lr_pcr_result;
	}
	public void setQc_three_prime_lr_pcr_result(String qc_three_prime_lr_pcr_result) {
		this.qc_three_prime_lr_pcr_result = qc_three_prime_lr_pcr_result;
	}
	public String getQc_critical_region_qpcr_result() {
		return qc_critical_region_qpcr_result;
	}
	public void setQc_critical_region_qpcr_result(
			String qc_critical_region_qpcr_result) {
		this.qc_critical_region_qpcr_result = qc_critical_region_qpcr_result;
	}
	public String getQc_loxp_srpcr_result() {
		return qc_loxp_srpcr_result;
	}
	public void setQc_loxp_srpcr_result(String qc_loxp_srpcr_result) {
		this.qc_loxp_srpcr_result = qc_loxp_srpcr_result;
	}
	public String getQc_loxp_srpcr_and_sequencing_result() {
		return qc_loxp_srpcr_and_sequencing_result;
	}
	public void setQc_loxp_srpcr_and_sequencing_result(
			String qc_loxp_srpcr_and_sequencing_result) {
		this.qc_loxp_srpcr_and_sequencing_result = qc_loxp_srpcr_and_sequencing_result;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((allele_symbol == null) ? 0 : allele_symbol.hashCode());
		result = prime
				* result
				+ ((colony_background_strain_mgi_accession == null) ? 0
						: colony_background_strain_mgi_accession.hashCode());
		result = prime
				* result
				+ ((colony_background_strain_mgi_name == null) ? 0
						: colony_background_strain_mgi_name.hashCode());
		result = prime
				* result
				+ ((colony_background_strain_name == null) ? 0
						: colony_background_strain_name.hashCode());
		result = prime * result
				+ ((colony_name == null) ? 0 : colony_name.hashCode());
		result = prime * result
				+ ((consortium_name == null) ? 0 : consortium_name.hashCode());
		result = prime
				* result
				+ ((cre_excision_required == null) ? 0 : cre_excision_required
						.hashCode());
		result = prime
				* result
				+ ((deleter_strain_name == null) ? 0 : deleter_strain_name
						.hashCode());
		result = prime * result
				+ Arrays.hashCode(distribution_centres_attributes);
		result = prime
				* result
				+ ((distribution_centres_formatted_display == null) ? 0
						: distribution_centres_formatted_display.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((is_active == null) ? 0 : is_active.hashCode());
		result = prime * result
				+ ((marker_symbol == null) ? 0 : marker_symbol.hashCode());
		result = prime
				* result
				+ ((mgi_accession_id == null) ? 0 : mgi_accession_id.hashCode());
		result = prime
				* result
				+ ((mi_attempt_colony_background_mgi_strain_accession_id == null) ? 0
						: mi_attempt_colony_background_mgi_strain_accession_id
								.hashCode());
		result = prime
				* result
				+ ((mi_attempt_colony_background_mgi_strain_name == null) ? 0
						: mi_attempt_colony_background_mgi_strain_name
								.hashCode());
		result = prime
				* result
				+ ((mi_attempt_colony_background_strain_name == null) ? 0
						: mi_attempt_colony_background_strain_name.hashCode());
		result = prime
				* result
				+ ((mi_attempt_colony_name == null) ? 0
						: mi_attempt_colony_name.hashCode());
		result = prime * result
				+ ((mi_plan_id == null) ? 0 : mi_plan_id.hashCode());
		result = prime
				* result
				+ ((mouse_allele_symbol == null) ? 0 : mouse_allele_symbol
						.hashCode());
		result = prime
				* result
				+ ((mouse_allele_symbol_superscript == null) ? 0
						: mouse_allele_symbol_superscript.hashCode());
		result = prime
				* result
				+ ((mouse_allele_type == null) ? 0 : mouse_allele_type
						.hashCode());
		result = prime
				* result
				+ ((number_of_cre_matings_successful == null) ? 0
						: number_of_cre_matings_successful.hashCode());
		result = prime
				* result
				+ ((phenotyping_complete == null) ? 0 : phenotyping_complete
						.hashCode());
		result = prime
				* result
				+ ((phenotyping_experiments_started == null) ? 0
						: phenotyping_experiments_started.hashCode());
		result = prime
				* result
				+ ((phenotyping_started == null) ? 0 : phenotyping_started
						.hashCode());
		result = prime
				* result
				+ ((production_centre_name == null) ? 0
						: production_centre_name.hashCode());
		result = prime
				* result
				+ ((qc_critical_region_qpcr_result == null) ? 0
						: qc_critical_region_qpcr_result.hashCode());
		result = prime
				* result
				+ ((qc_five_prime_cassette_integrity_result == null) ? 0
						: qc_five_prime_cassette_integrity_result.hashCode());
		result = prime
				* result
				+ ((qc_five_prime_lr_pcr_result == null) ? 0
						: qc_five_prime_lr_pcr_result.hashCode());
		result = prime
				* result
				+ ((qc_homozygous_loa_sr_pcr_result == null) ? 0
						: qc_homozygous_loa_sr_pcr_result.hashCode());
		result = prime
				* result
				+ ((qc_lacz_count_qpcr_result == null) ? 0
						: qc_lacz_count_qpcr_result.hashCode());
		result = prime
				* result
				+ ((qc_lacz_sr_pcr_result == null) ? 0 : qc_lacz_sr_pcr_result
						.hashCode());
		result = prime
				* result
				+ ((qc_loa_qpcr_result == null) ? 0 : qc_loa_qpcr_result
						.hashCode());
		result = prime
				* result
				+ ((qc_loxp_confirmation_result == null) ? 0
						: qc_loxp_confirmation_result.hashCode());
		result = prime
				* result
				+ ((qc_loxp_srpcr_and_sequencing_result == null) ? 0
						: qc_loxp_srpcr_and_sequencing_result.hashCode());
		result = prime
				* result
				+ ((qc_loxp_srpcr_result == null) ? 0 : qc_loxp_srpcr_result
						.hashCode());
		result = prime
				* result
				+ ((qc_mutant_specific_sr_pcr_result == null) ? 0
						: qc_mutant_specific_sr_pcr_result.hashCode());
		result = prime
				* result
				+ ((qc_neo_count_qpcr_result == null) ? 0
						: qc_neo_count_qpcr_result.hashCode());
		result = prime
				* result
				+ ((qc_neo_sr_pcr_result == null) ? 0 : qc_neo_sr_pcr_result
						.hashCode());
		result = prime
				* result
				+ ((qc_southern_blot_result == null) ? 0
						: qc_southern_blot_result.hashCode());
		result = prime
				* result
				+ ((qc_three_prime_lr_pcr_result == null) ? 0
						: qc_three_prime_lr_pcr_result.hashCode());
		result = prime
				* result
				+ ((qc_tv_backbone_assay_result == null) ? 0
						: qc_tv_backbone_assay_result.hashCode());
		result = prime
				* result
				+ ((rederivation_complete == null) ? 0 : rederivation_complete
						.hashCode());
		result = prime
				* result
				+ ((rederivation_started == null) ? 0 : rederivation_started
						.hashCode());
		result = prime
				* result
				+ ((report_to_public == null) ? 0 : report_to_public.hashCode());
		result = prime * result
				+ ((status_dates == null) ? 0 : status_dates.hashCode());
		result = prime * result
				+ ((status_name == null) ? 0 : status_name.hashCode());
		result = prime * result + ((tat_cre == null) ? 0 : tat_cre.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PhenotypeAttemptBean other = (PhenotypeAttemptBean) obj;
		if (allele_symbol == null) {
			if (other.allele_symbol != null) {
				return false;
			}
		} else if (!allele_symbol.equals(other.allele_symbol)) {
			return false;
		}
		if (colony_background_strain_mgi_accession == null) {
			if (other.colony_background_strain_mgi_accession != null) {
				return false;
			}
		} else if (!colony_background_strain_mgi_accession
				.equals(other.colony_background_strain_mgi_accession)) {
			return false;
		}
		if (colony_background_strain_mgi_name == null) {
			if (other.colony_background_strain_mgi_name != null) {
				return false;
			}
		} else if (!colony_background_strain_mgi_name
				.equals(other.colony_background_strain_mgi_name)) {
			return false;
		}
		if (colony_background_strain_name == null) {
			if (other.colony_background_strain_name != null) {
				return false;
			}
		} else if (!colony_background_strain_name
				.equals(other.colony_background_strain_name)) {
			return false;
		}
		if (colony_name == null) {
			if (other.colony_name != null) {
				return false;
			}
		} else if (!colony_name.equals(other.colony_name)) {
			return false;
		}
		if (consortium_name == null) {
			if (other.consortium_name != null) {
				return false;
			}
		} else if (!consortium_name.equals(other.consortium_name)) {
			return false;
		}
		if (cre_excision_required == null) {
			if (other.cre_excision_required != null) {
				return false;
			}
		} else if (!cre_excision_required.equals(other.cre_excision_required)) {
			return false;
		}
		if (deleter_strain_name == null) {
			if (other.deleter_strain_name != null) {
				return false;
			}
		} else if (!deleter_strain_name.equals(other.deleter_strain_name)) {
			return false;
		}
		if (!Arrays.equals(distribution_centres_attributes,
				other.distribution_centres_attributes)) {
			return false;
		}
		if (distribution_centres_formatted_display == null) {
			if (other.distribution_centres_formatted_display != null) {
				return false;
			}
		} else if (!distribution_centres_formatted_display
				.equals(other.distribution_centres_formatted_display)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (is_active == null) {
			if (other.is_active != null) {
				return false;
			}
		} else if (!is_active.equals(other.is_active)) {
			return false;
		}
		if (marker_symbol == null) {
			if (other.marker_symbol != null) {
				return false;
			}
		} else if (!marker_symbol.equals(other.marker_symbol)) {
			return false;
		}
		if (mgi_accession_id == null) {
			if (other.mgi_accession_id != null) {
				return false;
			}
		} else if (!mgi_accession_id.equals(other.mgi_accession_id)) {
			return false;
		}
		if (mi_attempt_colony_background_mgi_strain_accession_id == null) {
			if (other.mi_attempt_colony_background_mgi_strain_accession_id != null) {
				return false;
			}
		} else if (!mi_attempt_colony_background_mgi_strain_accession_id
				.equals(other.mi_attempt_colony_background_mgi_strain_accession_id)) {
			return false;
		}
		if (mi_attempt_colony_background_mgi_strain_name == null) {
			if (other.mi_attempt_colony_background_mgi_strain_name != null) {
				return false;
			}
		} else if (!mi_attempt_colony_background_mgi_strain_name
				.equals(other.mi_attempt_colony_background_mgi_strain_name)) {
			return false;
		}
		if (mi_attempt_colony_background_strain_name == null) {
			if (other.mi_attempt_colony_background_strain_name != null) {
				return false;
			}
		} else if (!mi_attempt_colony_background_strain_name
				.equals(other.mi_attempt_colony_background_strain_name)) {
			return false;
		}
		if (mi_attempt_colony_name == null) {
			if (other.mi_attempt_colony_name != null) {
				return false;
			}
		} else if (!mi_attempt_colony_name.equals(other.mi_attempt_colony_name)) {
			return false;
		}
		if (mi_plan_id == null) {
			if (other.mi_plan_id != null) {
				return false;
			}
		} else if (!mi_plan_id.equals(other.mi_plan_id)) {
			return false;
		}
		if (mouse_allele_symbol == null) {
			if (other.mouse_allele_symbol != null) {
				return false;
			}
		} else if (!mouse_allele_symbol.equals(other.mouse_allele_symbol)) {
			return false;
		}
		if (mouse_allele_symbol_superscript == null) {
			if (other.mouse_allele_symbol_superscript != null) {
				return false;
			}
		} else if (!mouse_allele_symbol_superscript
				.equals(other.mouse_allele_symbol_superscript)) {
			return false;
		}
		if (mouse_allele_type == null) {
			if (other.mouse_allele_type != null) {
				return false;
			}
		} else if (!mouse_allele_type.equals(other.mouse_allele_type)) {
			return false;
		}
		if (number_of_cre_matings_successful == null) {
			if (other.number_of_cre_matings_successful != null) {
				return false;
			}
		} else if (!number_of_cre_matings_successful
				.equals(other.number_of_cre_matings_successful)) {
			return false;
		}
		if (phenotyping_complete == null) {
			if (other.phenotyping_complete != null) {
				return false;
			}
		} else if (!phenotyping_complete.equals(other.phenotyping_complete)) {
			return false;
		}
		if (phenotyping_experiments_started == null) {
			if (other.phenotyping_experiments_started != null) {
				return false;
			}
		} else if (!phenotyping_experiments_started
				.equals(other.phenotyping_experiments_started)) {
			return false;
		}
		if (phenotyping_started == null) {
			if (other.phenotyping_started != null) {
				return false;
			}
		} else if (!phenotyping_started.equals(other.phenotyping_started)) {
			return false;
		}
		if (production_centre_name == null) {
			if (other.production_centre_name != null) {
				return false;
			}
		} else if (!production_centre_name.equals(other.production_centre_name)) {
			return false;
		}
		if (qc_critical_region_qpcr_result == null) {
			if (other.qc_critical_region_qpcr_result != null) {
				return false;
			}
		} else if (!qc_critical_region_qpcr_result
				.equals(other.qc_critical_region_qpcr_result)) {
			return false;
		}
		if (qc_five_prime_cassette_integrity_result == null) {
			if (other.qc_five_prime_cassette_integrity_result != null) {
				return false;
			}
		} else if (!qc_five_prime_cassette_integrity_result
				.equals(other.qc_five_prime_cassette_integrity_result)) {
			return false;
		}
		if (qc_five_prime_lr_pcr_result == null) {
			if (other.qc_five_prime_lr_pcr_result != null) {
				return false;
			}
		} else if (!qc_five_prime_lr_pcr_result
				.equals(other.qc_five_prime_lr_pcr_result)) {
			return false;
		}
		if (qc_homozygous_loa_sr_pcr_result == null) {
			if (other.qc_homozygous_loa_sr_pcr_result != null) {
				return false;
			}
		} else if (!qc_homozygous_loa_sr_pcr_result
				.equals(other.qc_homozygous_loa_sr_pcr_result)) {
			return false;
		}
		if (qc_lacz_count_qpcr_result == null) {
			if (other.qc_lacz_count_qpcr_result != null) {
				return false;
			}
		} else if (!qc_lacz_count_qpcr_result
				.equals(other.qc_lacz_count_qpcr_result)) {
			return false;
		}
		if (qc_lacz_sr_pcr_result == null) {
			if (other.qc_lacz_sr_pcr_result != null) {
				return false;
			}
		} else if (!qc_lacz_sr_pcr_result.equals(other.qc_lacz_sr_pcr_result)) {
			return false;
		}
		if (qc_loa_qpcr_result == null) {
			if (other.qc_loa_qpcr_result != null) {
				return false;
			}
		} else if (!qc_loa_qpcr_result.equals(other.qc_loa_qpcr_result)) {
			return false;
		}
		if (qc_loxp_confirmation_result == null) {
			if (other.qc_loxp_confirmation_result != null) {
				return false;
			}
		} else if (!qc_loxp_confirmation_result
				.equals(other.qc_loxp_confirmation_result)) {
			return false;
		}
		if (qc_loxp_srpcr_and_sequencing_result == null) {
			if (other.qc_loxp_srpcr_and_sequencing_result != null) {
				return false;
			}
		} else if (!qc_loxp_srpcr_and_sequencing_result
				.equals(other.qc_loxp_srpcr_and_sequencing_result)) {
			return false;
		}
		if (qc_loxp_srpcr_result == null) {
			if (other.qc_loxp_srpcr_result != null) {
				return false;
			}
		} else if (!qc_loxp_srpcr_result.equals(other.qc_loxp_srpcr_result)) {
			return false;
		}
		if (qc_mutant_specific_sr_pcr_result == null) {
			if (other.qc_mutant_specific_sr_pcr_result != null) {
				return false;
			}
		} else if (!qc_mutant_specific_sr_pcr_result
				.equals(other.qc_mutant_specific_sr_pcr_result)) {
			return false;
		}
		if (qc_neo_count_qpcr_result == null) {
			if (other.qc_neo_count_qpcr_result != null) {
				return false;
			}
		} else if (!qc_neo_count_qpcr_result
				.equals(other.qc_neo_count_qpcr_result)) {
			return false;
		}
		if (qc_neo_sr_pcr_result == null) {
			if (other.qc_neo_sr_pcr_result != null) {
				return false;
			}
		} else if (!qc_neo_sr_pcr_result.equals(other.qc_neo_sr_pcr_result)) {
			return false;
		}
		if (qc_southern_blot_result == null) {
			if (other.qc_southern_blot_result != null) {
				return false;
			}
		} else if (!qc_southern_blot_result
				.equals(other.qc_southern_blot_result)) {
			return false;
		}
		if (qc_three_prime_lr_pcr_result == null) {
			if (other.qc_three_prime_lr_pcr_result != null) {
				return false;
			}
		} else if (!qc_three_prime_lr_pcr_result
				.equals(other.qc_three_prime_lr_pcr_result)) {
			return false;
		}
		if (qc_tv_backbone_assay_result == null) {
			if (other.qc_tv_backbone_assay_result != null) {
				return false;
			}
		} else if (!qc_tv_backbone_assay_result
				.equals(other.qc_tv_backbone_assay_result)) {
			return false;
		}
		if (rederivation_complete == null) {
			if (other.rederivation_complete != null) {
				return false;
			}
		} else if (!rederivation_complete.equals(other.rederivation_complete)) {
			return false;
		}
		if (rederivation_started == null) {
			if (other.rederivation_started != null) {
				return false;
			}
		} else if (!rederivation_started.equals(other.rederivation_started)) {
			return false;
		}
		if (report_to_public == null) {
			if (other.report_to_public != null) {
				return false;
			}
		} else if (!report_to_public.equals(other.report_to_public)) {
			return false;
		}
		if (status_dates == null) {
			if (other.status_dates != null) {
				return false;
			}
		} else if (!status_dates.equals(other.status_dates)) {
			return false;
		}
		if (status_name == null) {
			if (other.status_name != null) {
				return false;
			}
		} else if (!status_name.equals(other.status_name)) {
			return false;
		}
		if (tat_cre == null) {
			if (other.tat_cre != null) {
				return false;
			}
		} else if (!tat_cre.equals(other.tat_cre)) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		return "PhenotypeAttemptBean [colony_name=" + colony_name
				+ ", cre_excision_required=" + cre_excision_required + ", id="
				+ id + ", is_active=" + is_active + ", mi_plan_id="
				+ mi_plan_id + ", mouse_allele_type=" + mouse_allele_type
				+ ", number_of_cre_matings_successful="
				+ number_of_cre_matings_successful + ", phenotyping_complete="
				+ phenotyping_complete + ", phenotyping_experiments_started="
				+ phenotyping_experiments_started + ", phenotyping_started="
				+ phenotyping_started + ", rederivation_complete="
				+ rederivation_complete + ", rederivation_started="
				+ rederivation_started + ", report_to_public="
				+ report_to_public + ", tat_cre=" + tat_cre
				+ ", distribution_centres_formatted_display="
				+ distribution_centres_formatted_display + ", status_name="
				+ status_name + ", status_dates=" + status_dates
				+ ", marker_symbol=" + marker_symbol
				+ ", mouse_allele_symbol_superscript="
				+ mouse_allele_symbol_superscript + ", mouse_allele_symbol="
				+ mouse_allele_symbol + ", allele_symbol=" + allele_symbol
				+ ", mi_attempt_colony_background_strain_name="
				+ mi_attempt_colony_background_strain_name
				+ ", mi_attempt_colony_background_mgi_strain_accession_id="
				+ mi_attempt_colony_background_mgi_strain_accession_id
				+ ", mi_attempt_colony_background_mgi_strain_name="
				+ mi_attempt_colony_background_mgi_strain_name
				+ ", colony_background_strain_mgi_accession="
				+ colony_background_strain_mgi_accession
				+ ", colony_background_strain_mgi_name="
				+ colony_background_strain_mgi_name + ", mgi_accession_id="
				+ mgi_accession_id + ", consortium_name=" + consortium_name
				+ ", production_centre_name=" + production_centre_name
				+ ", mi_attempt_colony_name=" + mi_attempt_colony_name
				+ ", deleter_strain_name=" + deleter_strain_name
				+ ", distribution_centres_attributes="
				+ Arrays.toString(distribution_centres_attributes)
				+ ", colony_background_strain_name="
				+ colony_background_strain_name + ", qc_southern_blot_result="
				+ qc_southern_blot_result + ", qc_five_prime_lr_pcr_result="
				+ qc_five_prime_lr_pcr_result
				+ ", qc_five_prime_cassette_integrity_result="
				+ qc_five_prime_cassette_integrity_result
				+ ", qc_tv_backbone_assay_result="
				+ qc_tv_backbone_assay_result + ", qc_neo_count_qpcr_result="
				+ qc_neo_count_qpcr_result + ", qc_lacz_count_qpcr_result="
				+ qc_lacz_count_qpcr_result + ", qc_neo_sr_pcr_result="
				+ qc_neo_sr_pcr_result + ", qc_loa_qpcr_result="
				+ qc_loa_qpcr_result + ", qc_homozygous_loa_sr_pcr_result="
				+ qc_homozygous_loa_sr_pcr_result + ", qc_lacz_sr_pcr_result="
				+ qc_lacz_sr_pcr_result + ", qc_mutant_specific_sr_pcr_result="
				+ qc_mutant_specific_sr_pcr_result
				+ ", qc_loxp_confirmation_result="
				+ qc_loxp_confirmation_result
				+ ", qc_three_prime_lr_pcr_result="
				+ qc_three_prime_lr_pcr_result
				+ ", qc_critical_region_qpcr_result="
				+ qc_critical_region_qpcr_result + ", qc_loxp_srpcr_result="
				+ qc_loxp_srpcr_result
				+ ", qc_loxp_srpcr_and_sequencing_result="
				+ qc_loxp_srpcr_and_sequencing_result + "]";
	}

}
