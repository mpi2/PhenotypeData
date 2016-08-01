package org.mousephenotype.cda.loads.legacy.dccimport.imits;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;


@XmlRootElement(name = "microinjection_attempt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MicroInjectionAttemptBean {

	private String colony_name;
	private String comments;
	private String date_chimeras_mated;
	private String genotyping_comment;
	private String id;
	private String is_active;
	private String is_released_from_genotyping;
	private String mi_date;
	private String mi_plan_id;
	private String mouse_allele_type;
	private String number_of_cct_offspring;
	private String number_of_chimera_matings_attempted;
	private String number_of_chimera_matings_successful;
	private String number_of_chimeras_with_0_to_9_percent_glt;
	private String number_of_chimeras_with_100_percent_glt;
	private String number_of_chimeras_with_10_to_49_percent_glt;
	private String number_of_chimeras_with_50_to_99_percent_glt;
	private String number_of_chimeras_with_glt_from_cct;
	private String number_of_chimeras_with_glt_from_genotyping;
	private String number_of_het_offspring;
	private String number_of_live_glt_offspring;
	private String number_of_males_with_0_to_39_percent_chimerism;
	private String number_of_males_with_100_percent_chimerism;
	private String number_of_males_with_40_to_79_percent_chimerism;
	private String number_of_males_with_80_to_99_percent_chimerism;
	private String number_surrogates_receiving;
	private String report_to_public;
	private String total_blasts_injected;
	private String total_chimeras;
	private String total_f1_mice_from_matings;
	private String total_female_chimeras;
	private String total_male_chimeras;
	private String total_pups_born;
	private String total_transferred;
	private String[] distribution_centres_formatted_display;
	private String es_cell_marker_symbol;
    private String mgi_accession_id;

	@XmlAnyElement
	private String es_cell_allele_symbol;
	private String status_name;

	@XmlElement
	private Map<String,String> status_dates = new HashMap<String, String>();

	//private String status_dates": {
	//       "Micro-injection in progress": "2009-02-06",
	//      "Chimeras obtained": "2009-03-24",
	//     "Genotype confirmed": "2009-10-21",
	//    "Micro-injection aborted": "2012-07-18"
	// },
	private String mouse_allele_symbol_superscript;
	private String mouse_allele_symbol;
	private String phenotype_attempts_count;
	private String pipeline_name;
	private String allele_symbol;
	private String es_cell_name;
	private String consortium_name;
	private String production_centre_name;
	private String blast_strain_name;
	private String colony_background_strain_name;
    private String colony_background_strain_mgi_accession;
	private String test_cross_strain_name;
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

	@XmlElement
	private Map<String,String> distribution_centres_attributes = new HashMap<String, String>();

	//   "distribution_centres_attributes": [
	//      {
	//         "distribution_network": null,
	//        "end_date": null,
	//       "id": 307,
	//      "is_distributed_by_emma": false,
	//     "start_date": null,
	//         "deposited_material_name": "Frozen embryos",
	//         "centre_name": "Monterotondo",
	//         "_destroy": false
	//      }
	//  ]


	private String assay_type;
	private String blast_strain_mgi_accession;
	private String blast_strain_mgi_name;
	private String cassette_transmission_verified;
	private String cassette_transmission_verified_auto_complete;

	@XmlElement
	private Map<String, String> colonies_attributes = new HashMap<>();

	private String colony_background_strain_mgi_name;
	private String crsp_no_founder_pups;
	private String crsp_num_founders_selected_for_breading;
	private String crsp_total_embryos_injected;
	private String crsp_total_embryos_survived;
	private String crsp_total_num_mutant_founders;
	private String crsp_total_transfered;
	private String experimental;
	private String external_ref;
	private String founder_num_assays;
	private String founder_num_positive_results;
	private String genotype_confirmed_allele_symbols;
	private String genotype_confirmed_distribution_centres;
	private String genotyped_confirmed_colony_names;
	private String genotyped_confirmed_colony_phenotype_attempts_count;
	private String marker_symbol;
	private String mi_plan_mutagenesis_via_crispr_cas9;

	@XmlElement
	private Map<String, String> mutagenesis_factor_attributes = new HashMap<>();

	private String mutagenesis_factor_external_ref;
	private String mutagenesis_factor_id;
	private String real_allele_id;
	private String test_cross_strain_mgi_accession;
	private String test_cross_strain_mgi_name;


	public String getColony_name() {
		return colony_name;
	}

	public void setColony_name(String colony_name) {
		this.colony_name = colony_name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDate_chimeras_mated() {
		return date_chimeras_mated;
	}

	public void setDate_chimeras_mated(String date_chimeras_mated) {
		this.date_chimeras_mated = date_chimeras_mated;
	}

	public String getGenotyping_comment() {
		return genotyping_comment;
	}

	public void setGenotyping_comment(String genotyping_comment) {
		this.genotyping_comment = genotyping_comment;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIs_active() {
		return is_active;
	}

	public void setIs_active(String is_active) {
		this.is_active = is_active;
	}

	public String getIs_released_from_genotyping() {
		return is_released_from_genotyping;
	}

	public void setIs_released_from_genotyping(String is_released_from_genotyping) {
		this.is_released_from_genotyping = is_released_from_genotyping;
	}

	public String getMi_date() {
		return mi_date;
	}

	public void setMi_date(String mi_date) {
		this.mi_date = mi_date;
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

	public String getNumber_of_cct_offspring() {
		return number_of_cct_offspring;
	}

	public void setNumber_of_cct_offspring(String number_of_cct_offspring) {
		this.number_of_cct_offspring = number_of_cct_offspring;
	}

	public String getNumber_of_chimera_matings_attempted() {
		return number_of_chimera_matings_attempted;
	}

	public void setNumber_of_chimera_matings_attempted(String number_of_chimera_matings_attempted) {
		this.number_of_chimera_matings_attempted = number_of_chimera_matings_attempted;
	}

	public String getNumber_of_chimera_matings_successful() {
		return number_of_chimera_matings_successful;
	}

	public void setNumber_of_chimera_matings_successful(String number_of_chimera_matings_successful) {
		this.number_of_chimera_matings_successful = number_of_chimera_matings_successful;
	}

	public String getNumber_of_chimeras_with_0_to_9_percent_glt() {
		return number_of_chimeras_with_0_to_9_percent_glt;
	}

	public void setNumber_of_chimeras_with_0_to_9_percent_glt(String number_of_chimeras_with_0_to_9_percent_glt) {
		this.number_of_chimeras_with_0_to_9_percent_glt = number_of_chimeras_with_0_to_9_percent_glt;
	}

	public String getNumber_of_chimeras_with_100_percent_glt() {
		return number_of_chimeras_with_100_percent_glt;
	}

	public void setNumber_of_chimeras_with_100_percent_glt(String number_of_chimeras_with_100_percent_glt) {
		this.number_of_chimeras_with_100_percent_glt = number_of_chimeras_with_100_percent_glt;
	}

	public String getNumber_of_chimeras_with_10_to_49_percent_glt() {
		return number_of_chimeras_with_10_to_49_percent_glt;
	}

	public void setNumber_of_chimeras_with_10_to_49_percent_glt(String number_of_chimeras_with_10_to_49_percent_glt) {
		this.number_of_chimeras_with_10_to_49_percent_glt = number_of_chimeras_with_10_to_49_percent_glt;
	}

	public String getNumber_of_chimeras_with_50_to_99_percent_glt() {
		return number_of_chimeras_with_50_to_99_percent_glt;
	}

	public void setNumber_of_chimeras_with_50_to_99_percent_glt(String number_of_chimeras_with_50_to_99_percent_glt) {
		this.number_of_chimeras_with_50_to_99_percent_glt = number_of_chimeras_with_50_to_99_percent_glt;
	}

	public String getNumber_of_chimeras_with_glt_from_cct() {
		return number_of_chimeras_with_glt_from_cct;
	}

	public void setNumber_of_chimeras_with_glt_from_cct(String number_of_chimeras_with_glt_from_cct) {
		this.number_of_chimeras_with_glt_from_cct = number_of_chimeras_with_glt_from_cct;
	}

	public String getNumber_of_chimeras_with_glt_from_genotyping() {
		return number_of_chimeras_with_glt_from_genotyping;
	}

	public void setNumber_of_chimeras_with_glt_from_genotyping(String number_of_chimeras_with_glt_from_genotyping) {
		this.number_of_chimeras_with_glt_from_genotyping = number_of_chimeras_with_glt_from_genotyping;
	}

	public String getNumber_of_het_offspring() {
		return number_of_het_offspring;
	}

	public void setNumber_of_het_offspring(String number_of_het_offspring) {
		this.number_of_het_offspring = number_of_het_offspring;
	}

	public String getNumber_of_live_glt_offspring() {
		return number_of_live_glt_offspring;
	}

	public void setNumber_of_live_glt_offspring(String number_of_live_glt_offspring) {
		this.number_of_live_glt_offspring = number_of_live_glt_offspring;
	}

	public String getNumber_of_males_with_0_to_39_percent_chimerism() {
		return number_of_males_with_0_to_39_percent_chimerism;
	}

	public void setNumber_of_males_with_0_to_39_percent_chimerism(String number_of_males_with_0_to_39_percent_chimerism) {
		this.number_of_males_with_0_to_39_percent_chimerism = number_of_males_with_0_to_39_percent_chimerism;
	}

	public String getNumber_of_males_with_100_percent_chimerism() {
		return number_of_males_with_100_percent_chimerism;
	}

	public void setNumber_of_males_with_100_percent_chimerism(String number_of_males_with_100_percent_chimerism) {
		this.number_of_males_with_100_percent_chimerism = number_of_males_with_100_percent_chimerism;
	}

	public String getNumber_of_males_with_40_to_79_percent_chimerism() {
		return number_of_males_with_40_to_79_percent_chimerism;
	}

	public void setNumber_of_males_with_40_to_79_percent_chimerism(String number_of_males_with_40_to_79_percent_chimerism) {
		this.number_of_males_with_40_to_79_percent_chimerism = number_of_males_with_40_to_79_percent_chimerism;
	}

	public String getNumber_of_males_with_80_to_99_percent_chimerism() {
		return number_of_males_with_80_to_99_percent_chimerism;
	}

	public void setNumber_of_males_with_80_to_99_percent_chimerism(String number_of_males_with_80_to_99_percent_chimerism) {
		this.number_of_males_with_80_to_99_percent_chimerism = number_of_males_with_80_to_99_percent_chimerism;
	}

	public String getNumber_surrogates_receiving() {
		return number_surrogates_receiving;
	}

	public void setNumber_surrogates_receiving(String number_surrogates_receiving) {
		this.number_surrogates_receiving = number_surrogates_receiving;
	}

	public String getReport_to_public() {
		return report_to_public;
	}

	public void setReport_to_public(String report_to_public) {
		this.report_to_public = report_to_public;
	}

	public String getTotal_blasts_injected() {
		return total_blasts_injected;
	}

	public void setTotal_blasts_injected(String total_blasts_injected) {
		this.total_blasts_injected = total_blasts_injected;
	}

	public String getTotal_chimeras() {
		return total_chimeras;
	}

	public void setTotal_chimeras(String total_chimeras) {
		this.total_chimeras = total_chimeras;
	}

	public String getTotal_f1_mice_from_matings() {
		return total_f1_mice_from_matings;
	}

	public void setTotal_f1_mice_from_matings(String total_f1_mice_from_matings) {
		this.total_f1_mice_from_matings = total_f1_mice_from_matings;
	}

	public String getTotal_female_chimeras() {
		return total_female_chimeras;
	}

	public void setTotal_female_chimeras(String total_female_chimeras) {
		this.total_female_chimeras = total_female_chimeras;
	}

	public String getTotal_male_chimeras() {
		return total_male_chimeras;
	}

	public void setTotal_male_chimeras(String total_male_chimeras) {
		this.total_male_chimeras = total_male_chimeras;
	}

	public String getTotal_pups_born() {
		return total_pups_born;
	}

	public void setTotal_pups_born(String total_pups_born) {
		this.total_pups_born = total_pups_born;
	}

	public String getTotal_transferred() {
		return total_transferred;
	}

	public void setTotal_transferred(String total_transferred) {
		this.total_transferred = total_transferred;
	}

	public String[] getDistribution_centres_formatted_display() {
		return distribution_centres_formatted_display;
	}

	public void setDistribution_centres_formatted_display(String[] distribution_centres_formatted_display) {
		this.distribution_centres_formatted_display = distribution_centres_formatted_display;
	}

	public String getEs_cell_marker_symbol() {
		return es_cell_marker_symbol;
	}

	public void setEs_cell_marker_symbol(String es_cell_marker_symbol) {
		this.es_cell_marker_symbol = es_cell_marker_symbol;
	}

	public String getMgi_accession_id() {
		return mgi_accession_id;
	}

	public void setMgi_accession_id(String mgi_accession_id) {
		this.mgi_accession_id = mgi_accession_id;
	}

	public String getEs_cell_allele_symbol() {
		return es_cell_allele_symbol;
	}

	public void setEs_cell_allele_symbol(String es_cell_allele_symbol) {
		this.es_cell_allele_symbol = es_cell_allele_symbol;
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

	public String getMouse_allele_symbol_superscript() {
		return mouse_allele_symbol_superscript;
	}

	public void setMouse_allele_symbol_superscript(String mouse_allele_symbol_superscript) {
		this.mouse_allele_symbol_superscript = mouse_allele_symbol_superscript;
	}

	public String getMouse_allele_symbol() {
		return mouse_allele_symbol;
	}

	public void setMouse_allele_symbol(String mouse_allele_symbol) {
		this.mouse_allele_symbol = mouse_allele_symbol;
	}

	public String getPhenotype_attempts_count() {
		return phenotype_attempts_count;
	}

	public void setPhenotype_attempts_count(String phenotype_attempts_count) {
		this.phenotype_attempts_count = phenotype_attempts_count;
	}

	public String getPipeline_name() {
		return pipeline_name;
	}

	public void setPipeline_name(String pipeline_name) {
		this.pipeline_name = pipeline_name;
	}

	public String getAllele_symbol() {
		return allele_symbol;
	}

	public void setAllele_symbol(String allele_symbol) {
		this.allele_symbol = allele_symbol;
	}

	public String getEs_cell_name() {
		return es_cell_name;
	}

	public void setEs_cell_name(String es_cell_name) {
		this.es_cell_name = es_cell_name;
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

	public String getBlast_strain_name() {
		return blast_strain_name;
	}

	public void setBlast_strain_name(String blast_strain_name) {
		this.blast_strain_name = blast_strain_name;
	}

	public String getColony_background_strain_name() {
		return colony_background_strain_name;
	}

	public void setColony_background_strain_name(String colony_background_strain_name) {
		this.colony_background_strain_name = colony_background_strain_name;
	}

	public String getColony_background_strain_mgi_accession() {
		return colony_background_strain_mgi_accession;
	}

	public void setColony_background_strain_mgi_accession(String colony_background_strain_mgi_accession) {
		this.colony_background_strain_mgi_accession = colony_background_strain_mgi_accession;
	}

	public String getTest_cross_strain_name() {
		return test_cross_strain_name;
	}

	public void setTest_cross_strain_name(String test_cross_strain_name) {
		this.test_cross_strain_name = test_cross_strain_name;
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

	public void setQc_five_prime_cassette_integrity_result(String qc_five_prime_cassette_integrity_result) {
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

	public void setQc_homozygous_loa_sr_pcr_result(String qc_homozygous_loa_sr_pcr_result) {
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

	public void setQc_mutant_specific_sr_pcr_result(String qc_mutant_specific_sr_pcr_result) {
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

	public void setQc_critical_region_qpcr_result(String qc_critical_region_qpcr_result) {
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

	public void setQc_loxp_srpcr_and_sequencing_result(String qc_loxp_srpcr_and_sequencing_result) {
		this.qc_loxp_srpcr_and_sequencing_result = qc_loxp_srpcr_and_sequencing_result;
	}

	public Map<String, String> getDistribution_centres_attributes() {
		return distribution_centres_attributes;
	}

	public void setDistribution_centres_attributes(Map<String, String> distribution_centres_attributes) {
		this.distribution_centres_attributes = distribution_centres_attributes;
	}

	public String getAssay_type() {
		return assay_type;
	}

	public void setAssay_type(String assay_type) {
		this.assay_type = assay_type;
	}

	public String getBlast_strain_mgi_accession() {
		return blast_strain_mgi_accession;
	}

	public void setBlast_strain_mgi_accession(String blast_strain_mgi_accession) {
		this.blast_strain_mgi_accession = blast_strain_mgi_accession;
	}

	public String getBlast_strain_mgi_name() {
		return blast_strain_mgi_name;
	}

	public void setBlast_strain_mgi_name(String blast_strain_mgi_name) {
		this.blast_strain_mgi_name = blast_strain_mgi_name;
	}

	public String getCassette_transmission_verified() {
		return cassette_transmission_verified;
	}

	public void setCassette_transmission_verified(String cassette_transmission_verified) {
		this.cassette_transmission_verified = cassette_transmission_verified;
	}

	public String getCassette_transmission_verified_auto_complete() {
		return cassette_transmission_verified_auto_complete;
	}

	public void setCassette_transmission_verified_auto_complete(String cassette_transmission_verified_auto_complete) {
		this.cassette_transmission_verified_auto_complete = cassette_transmission_verified_auto_complete;
	}

	public Map<String, String> getColonies_attributes() {
		return colonies_attributes;
	}

	public void setColonies_attributes(Map<String, String> colonies_attributes) {
		this.colonies_attributes = colonies_attributes;
	}

	public String getColony_background_strain_mgi_name() {
		return colony_background_strain_mgi_name;
	}

	public void setColony_background_strain_mgi_name(String colony_background_strain_mgi_name) {
		this.colony_background_strain_mgi_name = colony_background_strain_mgi_name;
	}

	public String getCrsp_no_founder_pups() {
		return crsp_no_founder_pups;
	}

	public void setCrsp_no_founder_pups(String crsp_no_founder_pups) {
		this.crsp_no_founder_pups = crsp_no_founder_pups;
	}

	public String getCrsp_num_founders_selected_for_breading() {
		return crsp_num_founders_selected_for_breading;
	}

	public void setCrsp_num_founders_selected_for_breading(String crsp_num_founders_selected_for_breading) {
		this.crsp_num_founders_selected_for_breading = crsp_num_founders_selected_for_breading;
	}

	public String getCrsp_total_embryos_injected() {
		return crsp_total_embryos_injected;
	}

	public void setCrsp_total_embryos_injected(String crsp_total_embryos_injected) {
		this.crsp_total_embryos_injected = crsp_total_embryos_injected;
	}

	public String getCrsp_total_embryos_survived() {
		return crsp_total_embryos_survived;
	}

	public void setCrsp_total_embryos_survived(String crsp_total_embryos_survived) {
		this.crsp_total_embryos_survived = crsp_total_embryos_survived;
	}

	public String getCrsp_total_num_mutant_founders() {
		return crsp_total_num_mutant_founders;
	}

	public void setCrsp_total_num_mutant_founders(String crsp_total_num_mutant_founders) {
		this.crsp_total_num_mutant_founders = crsp_total_num_mutant_founders;
	}

	public String getCrsp_total_transfered() {
		return crsp_total_transfered;
	}

	public void setCrsp_total_transfered(String crsp_total_transfered) {
		this.crsp_total_transfered = crsp_total_transfered;
	}

	public String getExperimental() {
		return experimental;
	}

	public void setExperimental(String experimental) {
		this.experimental = experimental;
	}

	public String getExternal_ref() {
		return external_ref;
	}

	public void setExternal_ref(String external_ref) {
		this.external_ref = external_ref;
	}

	public String getFounder_num_assays() {
		return founder_num_assays;
	}

	public void setFounder_num_assays(String founder_num_assays) {
		this.founder_num_assays = founder_num_assays;
	}

	public String getFounder_num_positive_results() {
		return founder_num_positive_results;
	}

	public void setFounder_num_positive_results(String founder_num_positive_results) {
		this.founder_num_positive_results = founder_num_positive_results;
	}

	public String getGenotype_confirmed_allele_symbols() {
		return genotype_confirmed_allele_symbols;
	}

	public void setGenotype_confirmed_allele_symbols(String genotype_confirmed_allele_symbols) {
		this.genotype_confirmed_allele_symbols = genotype_confirmed_allele_symbols;
	}

	public String getGenotype_confirmed_distribution_centres() {
		return genotype_confirmed_distribution_centres;
	}

	public void setGenotype_confirmed_distribution_centres(String genotype_confirmed_distribution_centres) {
		this.genotype_confirmed_distribution_centres = genotype_confirmed_distribution_centres;
	}

	public String getGenotyped_confirmed_colony_names() {
		return genotyped_confirmed_colony_names;
	}

	public void setGenotyped_confirmed_colony_names(String genotyped_confirmed_colony_names) {
		this.genotyped_confirmed_colony_names = genotyped_confirmed_colony_names;
	}

	public String getGenotyped_confirmed_colony_phenotype_attempts_count() {
		return genotyped_confirmed_colony_phenotype_attempts_count;
	}

	public void setGenotyped_confirmed_colony_phenotype_attempts_count(String genotyped_confirmed_colony_phenotype_attempts_count) {
		this.genotyped_confirmed_colony_phenotype_attempts_count = genotyped_confirmed_colony_phenotype_attempts_count;
	}

	public String getMarker_symbol() {
		return marker_symbol;
	}

	public void setMarker_symbol(String marker_symbol) {
		this.marker_symbol = marker_symbol;
	}

	public String getMi_plan_mutagenesis_via_crispr_cas9() {
		return mi_plan_mutagenesis_via_crispr_cas9;
	}

	public void setMi_plan_mutagenesis_via_crispr_cas9(String mi_plan_mutagenesis_via_crispr_cas9) {
		this.mi_plan_mutagenesis_via_crispr_cas9 = mi_plan_mutagenesis_via_crispr_cas9;
	}

	public Map<String, String> getMutagenesis_factor_attributes() {
		return mutagenesis_factor_attributes;
	}

	public void setMutagenesis_factor_attributes(Map<String, String> mutagenesis_factor_attributes) {
		this.mutagenesis_factor_attributes = mutagenesis_factor_attributes;
	}

	public String getMutagenesis_factor_external_ref() {
		return mutagenesis_factor_external_ref;
	}

	public void setMutagenesis_factor_external_ref(String mutagenesis_factor_external_ref) {
		this.mutagenesis_factor_external_ref = mutagenesis_factor_external_ref;
	}

	public String getMutagenesis_factor_id() {
		return mutagenesis_factor_id;
	}

	public void setMutagenesis_factor_id(String mutagenesis_factor_id) {
		this.mutagenesis_factor_id = mutagenesis_factor_id;
	}

	public String getReal_allele_id() {
		return real_allele_id;
	}

	public void setReal_allele_id(String real_allele_id) {
		this.real_allele_id = real_allele_id;
	}

	public String getTest_cross_strain_mgi_accession() {
		return test_cross_strain_mgi_accession;
	}

	public void setTest_cross_strain_mgi_accession(String test_cross_strain_mgi_accession) {
		this.test_cross_strain_mgi_accession = test_cross_strain_mgi_accession;
	}

	public String getTest_cross_strain_mgi_name() {
		return test_cross_strain_mgi_name;
	}

	public void setTest_cross_strain_mgi_name(String test_cross_strain_mgi_name) {
		this.test_cross_strain_mgi_name = test_cross_strain_mgi_name;
	}
}
