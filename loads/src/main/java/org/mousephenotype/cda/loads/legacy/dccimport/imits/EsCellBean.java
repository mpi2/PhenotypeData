package org.mousephenotype.cda.loads.legacy.dccimport.imits;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "es_cell")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EsCellBean {

	private String allele_id;
	private String allele_symbol_superscript_template;
	private String allele_type;
	private String comment;
	private String contact;
	private String created_at;
	private String id;
	private String ikmc_project_id;
	private String legacy_id;
	private String mgi_allele_id;
	private String mgi_allele_symbol_superscript;
	private String mutation_subtype;
	private String name;
	private String parental_cell_line;
	private String pipeline_id;
	private String production_centre_auto_update;
	private String production_qc_five_prime_screen;
	private String production_qc_loss_of_allele;
	private String production_qc_loxp_screen;
	private String production_qc_three_prime_screen;
	private String production_qc_vector_integrity;
	private String report_to_public;
	private String strain;
	private String targeting_vector_id;
	private String updated_at;
	private String user_qc_chr1;
	private String user_qc_chr11;
	private String user_qc_chr8;
	private String user_qc_chry;
	private String user_qc_comment;
	private String user_qc_five_prime_cassette_integrity;
	private String user_qc_five_prime_lr_pcr;
	private String user_qc_karyotype;
	private String user_qc_karyotype_pcr;
	private String user_qc_karyotype_spread;
	private String user_qc_lacz_qpcr;
	private String user_qc_lacz_sr_pcr;
	private String user_qc_loss_of_wt_allele;
	private String user_qc_loxp_confirmation;
	private String user_qc_loxp_srpcr_and_sequencing;
	private String user_qc_map_test;
	private String user_qc_mouse_clinic_id;
	private String user_qc_mutant_specific_sr_pcr;
	private String user_qc_neo_count_qpcr;
	private String user_qc_neo_sr_pcr;
	private String user_qc_southern_blot;
	private String user_qc_three_prime_lr_pcr;
	private String user_qc_tv_backbone_assay;

	@XmlAnyElement
	private Map<String, String> distribution_qcs = new HashMap<String, String>();

	public String getAllele_id() {
		return allele_id;
	}

	public void setAllele_id(String allele_id) {
		this.allele_id = allele_id;
	}

	public String getAllele_symbol_superscript_template() {
		return allele_symbol_superscript_template;
	}

	public void setAllele_symbol_superscript_template(String allele_symbol_superscript_template) {
		this.allele_symbol_superscript_template = allele_symbol_superscript_template;
	}

	public String getAllele_type() {
		return allele_type;
	}

	public void setAllele_type(String allele_type) {
		this.allele_type = allele_type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIkmc_project_id() {
		return ikmc_project_id;
	}

	public void setIkmc_project_id(String ikmc_project_id) {
		this.ikmc_project_id = ikmc_project_id;
	}

	public String getLegacy_id() {
		return legacy_id;
	}

	public void setLegacy_id(String legacy_id) {
		this.legacy_id = legacy_id;
	}

	public String getMgi_allele_id() {
		return mgi_allele_id;
	}

	public void setMgi_allele_id(String mgi_allele_id) {
		this.mgi_allele_id = mgi_allele_id;
	}

	public String getMgi_allele_symbol_superscript() {
		return mgi_allele_symbol_superscript;
	}

	public void setMgi_allele_symbol_superscript(String mgi_allele_symbol_superscript) {
		this.mgi_allele_symbol_superscript = mgi_allele_symbol_superscript;
	}

	public String getMutation_subtype() {
		return mutation_subtype;
	}

	public void setMutation_subtype(String mutation_subtype) {
		this.mutation_subtype = mutation_subtype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParental_cell_line() {
		return parental_cell_line;
	}

	public void setParental_cell_line(String parental_cell_line) {
		this.parental_cell_line = parental_cell_line;
	}

	public String getPipeline_id() {
		return pipeline_id;
	}

	public void setPipeline_id(String pipeline_id) {
		this.pipeline_id = pipeline_id;
	}

	public String getProduction_centre_auto_update() {
		return production_centre_auto_update;
	}

	public void setProduction_centre_auto_update(String production_centre_auto_update) {
		this.production_centre_auto_update = production_centre_auto_update;
	}

	public String getProduction_qc_five_prime_screen() {
		return production_qc_five_prime_screen;
	}

	public void setProduction_qc_five_prime_screen(String production_qc_five_prime_screen) {
		this.production_qc_five_prime_screen = production_qc_five_prime_screen;
	}

	public String getProduction_qc_loss_of_allele() {
		return production_qc_loss_of_allele;
	}

	public void setProduction_qc_loss_of_allele(String production_qc_loss_of_allele) {
		this.production_qc_loss_of_allele = production_qc_loss_of_allele;
	}

	public String getProduction_qc_loxp_screen() {
		return production_qc_loxp_screen;
	}

	public void setProduction_qc_loxp_screen(String production_qc_loxp_screen) {
		this.production_qc_loxp_screen = production_qc_loxp_screen;
	}

	public String getProduction_qc_three_prime_screen() {
		return production_qc_three_prime_screen;
	}

	public void setProduction_qc_three_prime_screen(String production_qc_three_prime_screen) {
		this.production_qc_three_prime_screen = production_qc_three_prime_screen;
	}

	public String getProduction_qc_vector_integrity() {
		return production_qc_vector_integrity;
	}

	public void setProduction_qc_vector_integrity(String production_qc_vector_integrity) {
		this.production_qc_vector_integrity = production_qc_vector_integrity;
	}

	public String getReport_to_public() {
		return report_to_public;
	}

	public void setReport_to_public(String report_to_public) {
		this.report_to_public = report_to_public;
	}

	public String getStrain() {
		return strain;
	}

	public void setStrain(String strain) {
		this.strain = strain;
	}

	public String getTargeting_vector_id() {
		return targeting_vector_id;
	}

	public void setTargeting_vector_id(String targeting_vector_id) {
		this.targeting_vector_id = targeting_vector_id;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getUser_qc_chr1() {
		return user_qc_chr1;
	}

	public void setUser_qc_chr1(String user_qc_chr1) {
		this.user_qc_chr1 = user_qc_chr1;
	}

	public String getUser_qc_chr11() {
		return user_qc_chr11;
	}

	public void setUser_qc_chr11(String user_qc_chr11) {
		this.user_qc_chr11 = user_qc_chr11;
	}

	public String getUser_qc_chr8() {
		return user_qc_chr8;
	}

	public void setUser_qc_chr8(String user_qc_chr8) {
		this.user_qc_chr8 = user_qc_chr8;
	}

	public String getUser_qc_chry() {
		return user_qc_chry;
	}

	public void setUser_qc_chry(String user_qc_chry) {
		this.user_qc_chry = user_qc_chry;
	}

	public String getUser_qc_comment() {
		return user_qc_comment;
	}

	public void setUser_qc_comment(String user_qc_comment) {
		this.user_qc_comment = user_qc_comment;
	}

	public String getUser_qc_five_prime_cassette_integrity() {
		return user_qc_five_prime_cassette_integrity;
	}

	public void setUser_qc_five_prime_cassette_integrity(String user_qc_five_prime_cassette_integrity) {
		this.user_qc_five_prime_cassette_integrity = user_qc_five_prime_cassette_integrity;
	}

	public String getUser_qc_five_prime_lr_pcr() {
		return user_qc_five_prime_lr_pcr;
	}

	public void setUser_qc_five_prime_lr_pcr(String user_qc_five_prime_lr_pcr) {
		this.user_qc_five_prime_lr_pcr = user_qc_five_prime_lr_pcr;
	}

	public String getUser_qc_karyotype() {
		return user_qc_karyotype;
	}

	public void setUser_qc_karyotype(String user_qc_karyotype) {
		this.user_qc_karyotype = user_qc_karyotype;
	}

	public String getUser_qc_karyotype_pcr() {
		return user_qc_karyotype_pcr;
	}

	public void setUser_qc_karyotype_pcr(String user_qc_karyotype_pcr) {
		this.user_qc_karyotype_pcr = user_qc_karyotype_pcr;
	}

	public String getUser_qc_karyotype_spread() {
		return user_qc_karyotype_spread;
	}

	public void setUser_qc_karyotype_spread(String user_qc_karyotype_spread) {
		this.user_qc_karyotype_spread = user_qc_karyotype_spread;
	}

	public String getUser_qc_lacz_qpcr() {
		return user_qc_lacz_qpcr;
	}

	public void setUser_qc_lacz_qpcr(String user_qc_lacz_qpcr) {
		this.user_qc_lacz_qpcr = user_qc_lacz_qpcr;
	}

	public String getUser_qc_lacz_sr_pcr() {
		return user_qc_lacz_sr_pcr;
	}

	public void setUser_qc_lacz_sr_pcr(String user_qc_lacz_sr_pcr) {
		this.user_qc_lacz_sr_pcr = user_qc_lacz_sr_pcr;
	}

	public String getUser_qc_loss_of_wt_allele() {
		return user_qc_loss_of_wt_allele;
	}

	public void setUser_qc_loss_of_wt_allele(String user_qc_loss_of_wt_allele) {
		this.user_qc_loss_of_wt_allele = user_qc_loss_of_wt_allele;
	}

	public String getUser_qc_loxp_confirmation() {
		return user_qc_loxp_confirmation;
	}

	public void setUser_qc_loxp_confirmation(String user_qc_loxp_confirmation) {
		this.user_qc_loxp_confirmation = user_qc_loxp_confirmation;
	}

	public String getUser_qc_loxp_srpcr_and_sequencing() {
		return user_qc_loxp_srpcr_and_sequencing;
	}

	public void setUser_qc_loxp_srpcr_and_sequencing(String user_qc_loxp_srpcr_and_sequencing) {
		this.user_qc_loxp_srpcr_and_sequencing = user_qc_loxp_srpcr_and_sequencing;
	}

	public String getUser_qc_map_test() {
		return user_qc_map_test;
	}

	public void setUser_qc_map_test(String user_qc_map_test) {
		this.user_qc_map_test = user_qc_map_test;
	}

	public String getUser_qc_mouse_clinic_id() {
		return user_qc_mouse_clinic_id;
	}

	public void setUser_qc_mouse_clinic_id(String user_qc_mouse_clinic_id) {
		this.user_qc_mouse_clinic_id = user_qc_mouse_clinic_id;
	}

	public String getUser_qc_mutant_specific_sr_pcr() {
		return user_qc_mutant_specific_sr_pcr;
	}

	public void setUser_qc_mutant_specific_sr_pcr(String user_qc_mutant_specific_sr_pcr) {
		this.user_qc_mutant_specific_sr_pcr = user_qc_mutant_specific_sr_pcr;
	}

	public String getUser_qc_neo_count_qpcr() {
		return user_qc_neo_count_qpcr;
	}

	public void setUser_qc_neo_count_qpcr(String user_qc_neo_count_qpcr) {
		this.user_qc_neo_count_qpcr = user_qc_neo_count_qpcr;
	}

	public String getUser_qc_neo_sr_pcr() {
		return user_qc_neo_sr_pcr;
	}

	public void setUser_qc_neo_sr_pcr(String user_qc_neo_sr_pcr) {
		this.user_qc_neo_sr_pcr = user_qc_neo_sr_pcr;
	}

	public String getUser_qc_southern_blot() {
		return user_qc_southern_blot;
	}

	public void setUser_qc_southern_blot(String user_qc_southern_blot) {
		this.user_qc_southern_blot = user_qc_southern_blot;
	}

	public String getUser_qc_three_prime_lr_pcr() {
		return user_qc_three_prime_lr_pcr;
	}

	public void setUser_qc_three_prime_lr_pcr(String user_qc_three_prime_lr_pcr) {
		this.user_qc_three_prime_lr_pcr = user_qc_three_prime_lr_pcr;
	}

	public String getUser_qc_tv_backbone_assay() {
		return user_qc_tv_backbone_assay;
	}

	public void setUser_qc_tv_backbone_assay(String user_qc_tv_backbone_assay) {
		this.user_qc_tv_backbone_assay = user_qc_tv_backbone_assay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allele_id == null) ? 0 : allele_id.hashCode());
		result = prime * result + ((allele_symbol_superscript_template == null) ? 0 : allele_symbol_superscript_template.hashCode());
		result = prime * result + ((allele_type == null) ? 0 : allele_type.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		result = prime * result + ((created_at == null) ? 0 : created_at.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ikmc_project_id == null) ? 0 : ikmc_project_id.hashCode());
		result = prime * result + ((legacy_id == null) ? 0 : legacy_id.hashCode());
		result = prime * result + ((mgi_allele_id == null) ? 0 : mgi_allele_id.hashCode());
		result = prime * result + ((mgi_allele_symbol_superscript == null) ? 0 : mgi_allele_symbol_superscript.hashCode());
		result = prime * result + ((mutation_subtype == null) ? 0 : mutation_subtype.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parental_cell_line == null) ? 0 : parental_cell_line.hashCode());
		result = prime * result + ((pipeline_id == null) ? 0 : pipeline_id.hashCode());
		result = prime * result + ((production_centre_auto_update == null) ? 0 : production_centre_auto_update.hashCode());
		result = prime * result + ((production_qc_five_prime_screen == null) ? 0 : production_qc_five_prime_screen.hashCode());
		result = prime * result + ((production_qc_loss_of_allele == null) ? 0 : production_qc_loss_of_allele.hashCode());
		result = prime * result + ((production_qc_loxp_screen == null) ? 0 : production_qc_loxp_screen.hashCode());
		result = prime * result + ((production_qc_three_prime_screen == null) ? 0 : production_qc_three_prime_screen.hashCode());
		result = prime * result + ((production_qc_vector_integrity == null) ? 0 : production_qc_vector_integrity.hashCode());
		result = prime * result + ((report_to_public == null) ? 0 : report_to_public.hashCode());
		result = prime * result + ((strain == null) ? 0 : strain.hashCode());
		result = prime * result + ((targeting_vector_id == null) ? 0 : targeting_vector_id.hashCode());
		result = prime * result + ((updated_at == null) ? 0 : updated_at.hashCode());
		result = prime * result + ((user_qc_chr1 == null) ? 0 : user_qc_chr1.hashCode());
		result = prime * result + ((user_qc_chr11 == null) ? 0 : user_qc_chr11.hashCode());
		result = prime * result + ((user_qc_chr8 == null) ? 0 : user_qc_chr8.hashCode());
		result = prime * result + ((user_qc_chry == null) ? 0 : user_qc_chry.hashCode());
		result = prime * result + ((user_qc_comment == null) ? 0 : user_qc_comment.hashCode());
		result = prime * result + ((user_qc_five_prime_cassette_integrity == null) ? 0 : user_qc_five_prime_cassette_integrity.hashCode());
		result = prime * result + ((user_qc_five_prime_lr_pcr == null) ? 0 : user_qc_five_prime_lr_pcr.hashCode());
		result = prime * result + ((user_qc_karyotype == null) ? 0 : user_qc_karyotype.hashCode());
		result = prime * result + ((user_qc_karyotype_pcr == null) ? 0 : user_qc_karyotype_pcr.hashCode());
		result = prime * result + ((user_qc_karyotype_spread == null) ? 0 : user_qc_karyotype_spread.hashCode());
		result = prime * result + ((user_qc_lacz_qpcr == null) ? 0 : user_qc_lacz_qpcr.hashCode());
		result = prime * result + ((user_qc_lacz_sr_pcr == null) ? 0 : user_qc_lacz_sr_pcr.hashCode());
		result = prime * result + ((user_qc_loss_of_wt_allele == null) ? 0 : user_qc_loss_of_wt_allele.hashCode());
		result = prime * result + ((user_qc_loxp_confirmation == null) ? 0 : user_qc_loxp_confirmation.hashCode());
		result = prime * result + ((user_qc_loxp_srpcr_and_sequencing == null) ? 0 : user_qc_loxp_srpcr_and_sequencing.hashCode());
		result = prime * result + ((user_qc_map_test == null) ? 0 : user_qc_map_test.hashCode());
		result = prime * result + ((user_qc_mouse_clinic_id == null) ? 0 : user_qc_mouse_clinic_id.hashCode());
		result = prime * result + ((user_qc_mutant_specific_sr_pcr == null) ? 0 : user_qc_mutant_specific_sr_pcr.hashCode());
		result = prime * result + ((user_qc_neo_count_qpcr == null) ? 0 : user_qc_neo_count_qpcr.hashCode());
		result = prime * result + ((user_qc_neo_sr_pcr == null) ? 0 : user_qc_neo_sr_pcr.hashCode());
		result = prime * result + ((user_qc_southern_blot == null) ? 0 : user_qc_southern_blot.hashCode());
		result = prime * result + ((user_qc_three_prime_lr_pcr == null) ? 0 : user_qc_three_prime_lr_pcr.hashCode());
		result = prime * result + ((user_qc_tv_backbone_assay == null) ? 0 : user_qc_tv_backbone_assay.hashCode());
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
		EsCellBean other = (EsCellBean) obj;
		if (allele_id == null) {
			if (other.allele_id != null) {
				return false;
			}
		} else if (!allele_id.equals(other.allele_id)) {
			return false;
		}
		if (allele_symbol_superscript_template == null) {
			if (other.allele_symbol_superscript_template != null) {
				return false;
			}
		} else if (!allele_symbol_superscript_template.equals(other.allele_symbol_superscript_template)) {
			return false;
		}
		if (allele_type == null) {
			if (other.allele_type != null) {
				return false;
			}
		} else if (!allele_type.equals(other.allele_type)) {
			return false;
		}
		if (comment == null) {
			if (other.comment != null) {
				return false;
			}
		} else if (!comment.equals(other.comment)) {
			return false;
		}
		if (contact == null) {
			if (other.contact != null) {
				return false;
			}
		} else if (!contact.equals(other.contact)) {
			return false;
		}
		if (created_at == null) {
			if (other.created_at != null) {
				return false;
			}
		} else if (!created_at.equals(other.created_at)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (ikmc_project_id == null) {
			if (other.ikmc_project_id != null) {
				return false;
			}
		} else if (!ikmc_project_id.equals(other.ikmc_project_id)) {
			return false;
		}
		if (legacy_id == null) {
			if (other.legacy_id != null) {
				return false;
			}
		} else if (!legacy_id.equals(other.legacy_id)) {
			return false;
		}
		if (mgi_allele_id == null) {
			if (other.mgi_allele_id != null) {
				return false;
			}
		} else if (!mgi_allele_id.equals(other.mgi_allele_id)) {
			return false;
		}
		if (mgi_allele_symbol_superscript == null) {
			if (other.mgi_allele_symbol_superscript != null) {
				return false;
			}
		} else if (!mgi_allele_symbol_superscript.equals(other.mgi_allele_symbol_superscript)) {
			return false;
		}
		if (mutation_subtype == null) {
			if (other.mutation_subtype != null) {
				return false;
			}
		} else if (!mutation_subtype.equals(other.mutation_subtype)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (parental_cell_line == null) {
			if (other.parental_cell_line != null) {
				return false;
			}
		} else if (!parental_cell_line.equals(other.parental_cell_line)) {
			return false;
		}
		if (pipeline_id == null) {
			if (other.pipeline_id != null) {
				return false;
			}
		} else if (!pipeline_id.equals(other.pipeline_id)) {
			return false;
		}
		if (production_centre_auto_update == null) {
			if (other.production_centre_auto_update != null) {
				return false;
			}
		} else if (!production_centre_auto_update.equals(other.production_centre_auto_update)) {
			return false;
		}
		if (production_qc_five_prime_screen == null) {
			if (other.production_qc_five_prime_screen != null) {
				return false;
			}
		} else if (!production_qc_five_prime_screen.equals(other.production_qc_five_prime_screen)) {
			return false;
		}
		if (production_qc_loss_of_allele == null) {
			if (other.production_qc_loss_of_allele != null) {
				return false;
			}
		} else if (!production_qc_loss_of_allele.equals(other.production_qc_loss_of_allele)) {
			return false;
		}
		if (production_qc_loxp_screen == null) {
			if (other.production_qc_loxp_screen != null) {
				return false;
			}
		} else if (!production_qc_loxp_screen.equals(other.production_qc_loxp_screen)) {
			return false;
		}
		if (production_qc_three_prime_screen == null) {
			if (other.production_qc_three_prime_screen != null) {
				return false;
			}
		} else if (!production_qc_three_prime_screen.equals(other.production_qc_three_prime_screen)) {
			return false;
		}
		if (production_qc_vector_integrity == null) {
			if (other.production_qc_vector_integrity != null) {
				return false;
			}
		} else if (!production_qc_vector_integrity.equals(other.production_qc_vector_integrity)) {
			return false;
		}
		if (report_to_public == null) {
			if (other.report_to_public != null) {
				return false;
			}
		} else if (!report_to_public.equals(other.report_to_public)) {
			return false;
		}
		if (strain == null) {
			if (other.strain != null) {
				return false;
			}
		} else if (!strain.equals(other.strain)) {
			return false;
		}
		if (targeting_vector_id == null) {
			if (other.targeting_vector_id != null) {
				return false;
			}
		} else if (!targeting_vector_id.equals(other.targeting_vector_id)) {
			return false;
		}
		if (updated_at == null) {
			if (other.updated_at != null) {
				return false;
			}
		} else if (!updated_at.equals(other.updated_at)) {
			return false;
		}
		if (user_qc_chr1 == null) {
			if (other.user_qc_chr1 != null) {
				return false;
			}
		} else if (!user_qc_chr1.equals(other.user_qc_chr1)) {
			return false;
		}
		if (user_qc_chr11 == null) {
			if (other.user_qc_chr11 != null) {
				return false;
			}
		} else if (!user_qc_chr11.equals(other.user_qc_chr11)) {
			return false;
		}
		if (user_qc_chr8 == null) {
			if (other.user_qc_chr8 != null) {
				return false;
			}
		} else if (!user_qc_chr8.equals(other.user_qc_chr8)) {
			return false;
		}
		if (user_qc_chry == null) {
			if (other.user_qc_chry != null) {
				return false;
			}
		} else if (!user_qc_chry.equals(other.user_qc_chry)) {
			return false;
		}
		if (user_qc_comment == null) {
			if (other.user_qc_comment != null) {
				return false;
			}
		} else if (!user_qc_comment.equals(other.user_qc_comment)) {
			return false;
		}
		if (user_qc_five_prime_cassette_integrity == null) {
			if (other.user_qc_five_prime_cassette_integrity != null) {
				return false;
			}
		} else if (!user_qc_five_prime_cassette_integrity.equals(other.user_qc_five_prime_cassette_integrity)) {
			return false;
		}
		if (user_qc_five_prime_lr_pcr == null) {
			if (other.user_qc_five_prime_lr_pcr != null) {
				return false;
			}
		} else if (!user_qc_five_prime_lr_pcr.equals(other.user_qc_five_prime_lr_pcr)) {
			return false;
		}
		if (user_qc_karyotype == null) {
			if (other.user_qc_karyotype != null) {
				return false;
			}
		} else if (!user_qc_karyotype.equals(other.user_qc_karyotype)) {
			return false;
		}
		if (user_qc_karyotype_pcr == null) {
			if (other.user_qc_karyotype_pcr != null) {
				return false;
			}
		} else if (!user_qc_karyotype_pcr.equals(other.user_qc_karyotype_pcr)) {
			return false;
		}
		if (user_qc_karyotype_spread == null) {
			if (other.user_qc_karyotype_spread != null) {
				return false;
			}
		} else if (!user_qc_karyotype_spread.equals(other.user_qc_karyotype_spread)) {
			return false;
		}
		if (user_qc_lacz_qpcr == null) {
			if (other.user_qc_lacz_qpcr != null) {
				return false;
			}
		} else if (!user_qc_lacz_qpcr.equals(other.user_qc_lacz_qpcr)) {
			return false;
		}
		if (user_qc_lacz_sr_pcr == null) {
			if (other.user_qc_lacz_sr_pcr != null) {
				return false;
			}
		} else if (!user_qc_lacz_sr_pcr.equals(other.user_qc_lacz_sr_pcr)) {
			return false;
		}
		if (user_qc_loss_of_wt_allele == null) {
			if (other.user_qc_loss_of_wt_allele != null) {
				return false;
			}
		} else if (!user_qc_loss_of_wt_allele.equals(other.user_qc_loss_of_wt_allele)) {
			return false;
		}
		if (user_qc_loxp_confirmation == null) {
			if (other.user_qc_loxp_confirmation != null) {
				return false;
			}
		} else if (!user_qc_loxp_confirmation.equals(other.user_qc_loxp_confirmation)) {
			return false;
		}
		if (user_qc_loxp_srpcr_and_sequencing == null) {
			if (other.user_qc_loxp_srpcr_and_sequencing != null) {
				return false;
			}
		} else if (!user_qc_loxp_srpcr_and_sequencing.equals(other.user_qc_loxp_srpcr_and_sequencing)) {
			return false;
		}
		if (user_qc_map_test == null) {
			if (other.user_qc_map_test != null) {
				return false;
			}
		} else if (!user_qc_map_test.equals(other.user_qc_map_test)) {
			return false;
		}
		if (user_qc_mouse_clinic_id == null) {
			if (other.user_qc_mouse_clinic_id != null) {
				return false;
			}
		} else if (!user_qc_mouse_clinic_id.equals(other.user_qc_mouse_clinic_id)) {
			return false;
		}
		if (user_qc_mutant_specific_sr_pcr == null) {
			if (other.user_qc_mutant_specific_sr_pcr != null) {
				return false;
			}
		} else if (!user_qc_mutant_specific_sr_pcr.equals(other.user_qc_mutant_specific_sr_pcr)) {
			return false;
		}
		if (user_qc_neo_count_qpcr == null) {
			if (other.user_qc_neo_count_qpcr != null) {
				return false;
			}
		} else if (!user_qc_neo_count_qpcr.equals(other.user_qc_neo_count_qpcr)) {
			return false;
		}
		if (user_qc_neo_sr_pcr == null) {
			if (other.user_qc_neo_sr_pcr != null) {
				return false;
			}
		} else if (!user_qc_neo_sr_pcr.equals(other.user_qc_neo_sr_pcr)) {
			return false;
		}
		if (user_qc_southern_blot == null) {
			if (other.user_qc_southern_blot != null) {
				return false;
			}
		} else if (!user_qc_southern_blot.equals(other.user_qc_southern_blot)) {
			return false;
		}
		if (user_qc_three_prime_lr_pcr == null) {
			if (other.user_qc_three_prime_lr_pcr != null) {
				return false;
			}
		} else if (!user_qc_three_prime_lr_pcr.equals(other.user_qc_three_prime_lr_pcr)) {
			return false;
		}
		if (user_qc_tv_backbone_assay == null) {
			if (other.user_qc_tv_backbone_assay != null) {
				return false;
			}
		} else if (!user_qc_tv_backbone_assay.equals(other.user_qc_tv_backbone_assay)) {
			return false;
		}
		return true;
	}

	public Map<String, String> getDistribution_qcs() {
		return distribution_qcs;
	}

	public void setDistribution_qcs(Map<String, String> distribution_qcs) {
		this.distribution_qcs = distribution_qcs;
	}

}
