package org.mousephenotype.cda.loads.legacy.dccimport.imits;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "allele")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlleleBean {

	private String id;
	private String gene_id;
	private String assembly;
	private String chromosome;
	private String strand;
	private String homology_arm_start;
	private String homology_arm_end;
	private String loxp_start;
	private String loxp_end;
	private String cassette_start;
	private String cassette_end;
	private String cassette;
	private String backbone;
	private String subtype_description;
	private String floxed_start_exon;
	private String floxed_end_exon;
	private String project_design_id;
	private String reporter;
	private String mutation_method_id;
	private String mutation_type_id;
	private String mutation_subtype_id;
	private String cassette_type;
	private String created_at;
	private String updated_at;
	private String intron;
	private String type;
	private String marker_symbol;


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGene_id() {
		return gene_id;
	}
	public void setGene_id(String gene_id) {
		this.gene_id = gene_id;
	}
	public String getAssembly() {
		return assembly;
	}
	public void setAssembly(String assembly) {
		this.assembly = assembly;
	}
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public String getHomology_arm_start() {
		return homology_arm_start;
	}
	public void setHomology_arm_start(String homology_arm_start) {
		this.homology_arm_start = homology_arm_start;
	}
	public String getHomology_arm_end() {
		return homology_arm_end;
	}
	public void setHomology_arm_end(String homology_arm_end) {
		this.homology_arm_end = homology_arm_end;
	}
	public String getLoxp_start() {
		return loxp_start;
	}
	public void setLoxp_start(String loxp_start) {
		this.loxp_start = loxp_start;
	}
	public String getLoxp_end() {
		return loxp_end;
	}
	public void setLoxp_end(String loxp_end) {
		this.loxp_end = loxp_end;
	}
	public String getCassette_start() {
		return cassette_start;
	}
	public void setCassette_start(String cassette_start) {
		this.cassette_start = cassette_start;
	}
	public String getCassette_end() {
		return cassette_end;
	}
	public void setCassette_end(String cassette_end) {
		this.cassette_end = cassette_end;
	}
	public String getCassette() {
		return cassette;
	}
	public void setCassette(String cassette) {
		this.cassette = cassette;
	}
	public String getBackbone() {
		return backbone;
	}
	public void setBackbone(String backbone) {
		this.backbone = backbone;
	}
	public String getSubtype_description() {
		return subtype_description;
	}
	public void setSubtype_description(String subtype_description) {
		this.subtype_description = subtype_description;
	}
	public String getFloxed_start_exon() {
		return floxed_start_exon;
	}
	public void setFloxed_start_exon(String floxed_start_exon) {
		this.floxed_start_exon = floxed_start_exon;
	}
	public String getFloxed_end_exon() {
		return floxed_end_exon;
	}
	public void setFloxed_end_exon(String floxed_end_exon) {
		this.floxed_end_exon = floxed_end_exon;
	}
	public String getProject_design_id() {
		return project_design_id;
	}
	public void setProject_design_id(String project_design_id) {
		this.project_design_id = project_design_id;
	}
	public String getReporter() {
		return reporter;
	}
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}
	public String getMutation_method_id() {
		return mutation_method_id;
	}
	public void setMutation_method_id(String mutation_method_id) {
		this.mutation_method_id = mutation_method_id;
	}
	public String getMutation_type_id() {
		return mutation_type_id;
	}
	public void setMutation_type_id(String mutation_type_id) {
		this.mutation_type_id = mutation_type_id;
	}
	public String getMutation_subtype_id() {
		return mutation_subtype_id;
	}
	public void setMutation_subtype_id(String mutation_subtype_id) {
		this.mutation_subtype_id = mutation_subtype_id;
	}
	public String getCassette_type() {
		return cassette_type;
	}
	public void setCassette_type(String cassette_type) {
		this.cassette_type = cassette_type;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getIntron() {
		return intron;
	}
	public void setIntron(String intron) {
		this.intron = intron;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((assembly == null) ? 0 : assembly.hashCode());
		result = prime * result
				+ ((backbone == null) ? 0 : backbone.hashCode());
		result = prime * result
				+ ((cassette == null) ? 0 : cassette.hashCode());
		result = prime * result
				+ ((cassette_end == null) ? 0 : cassette_end.hashCode());
		result = prime * result
				+ ((cassette_start == null) ? 0 : cassette_start.hashCode());
		result = prime * result
				+ ((cassette_type == null) ? 0 : cassette_type.hashCode());
		result = prime * result
				+ ((chromosome == null) ? 0 : chromosome.hashCode());
		result = prime * result
				+ ((created_at == null) ? 0 : created_at.hashCode());
		result = prime * result
				+ ((floxed_end_exon == null) ? 0 : floxed_end_exon.hashCode());
		result = prime
				* result
				+ ((floxed_start_exon == null) ? 0 : floxed_start_exon
						.hashCode());
		result = prime * result + ((gene_id == null) ? 0 : gene_id.hashCode());
		result = prime
				* result
				+ ((homology_arm_end == null) ? 0 : homology_arm_end.hashCode());
		result = prime
				* result
				+ ((homology_arm_start == null) ? 0 : homology_arm_start
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((intron == null) ? 0 : intron.hashCode());
		result = prime * result
				+ ((loxp_end == null) ? 0 : loxp_end.hashCode());
		result = prime * result
				+ ((loxp_start == null) ? 0 : loxp_start.hashCode());
		result = prime
				* result
				+ ((mutation_method_id == null) ? 0 : mutation_method_id
						.hashCode());
		result = prime
				* result
				+ ((mutation_subtype_id == null) ? 0 : mutation_subtype_id
						.hashCode());
		result = prime
				* result
				+ ((mutation_type_id == null) ? 0 : mutation_type_id.hashCode());
		result = prime
				* result
				+ ((project_design_id == null) ? 0 : project_design_id
						.hashCode());
		result = prime * result
				+ ((reporter == null) ? 0 : reporter.hashCode());
		result = prime * result + ((strand == null) ? 0 : strand.hashCode());
		result = prime
				* result
				+ ((subtype_description == null) ? 0 : subtype_description
						.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((updated_at == null) ? 0 : updated_at.hashCode());
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
		AlleleBean other = (AlleleBean) obj;
		if (assembly == null) {
			if (other.assembly != null) {
				return false;
			}
		} else if (!assembly.equals(other.assembly)) {
			return false;
		}
		if (backbone == null) {
			if (other.backbone != null) {
				return false;
			}
		} else if (!backbone.equals(other.backbone)) {
			return false;
		}
		if (cassette == null) {
			if (other.cassette != null) {
				return false;
			}
		} else if (!cassette.equals(other.cassette)) {
			return false;
		}
		if (cassette_end == null) {
			if (other.cassette_end != null) {
				return false;
			}
		} else if (!cassette_end.equals(other.cassette_end)) {
			return false;
		}
		if (cassette_start == null) {
			if (other.cassette_start != null) {
				return false;
			}
		} else if (!cassette_start.equals(other.cassette_start)) {
			return false;
		}
		if (cassette_type == null) {
			if (other.cassette_type != null) {
				return false;
			}
		} else if (!cassette_type.equals(other.cassette_type)) {
			return false;
		}
		if (chromosome == null) {
			if (other.chromosome != null) {
				return false;
			}
		} else if (!chromosome.equals(other.chromosome)) {
			return false;
		}
		if (created_at == null) {
			if (other.created_at != null) {
				return false;
			}
		} else if (!created_at.equals(other.created_at)) {
			return false;
		}
		if (floxed_end_exon == null) {
			if (other.floxed_end_exon != null) {
				return false;
			}
		} else if (!floxed_end_exon.equals(other.floxed_end_exon)) {
			return false;
		}
		if (floxed_start_exon == null) {
			if (other.floxed_start_exon != null) {
				return false;
			}
		} else if (!floxed_start_exon.equals(other.floxed_start_exon)) {
			return false;
		}
		if (gene_id == null) {
			if (other.gene_id != null) {
				return false;
			}
		} else if (!gene_id.equals(other.gene_id)) {
			return false;
		}
		if (homology_arm_end == null) {
			if (other.homology_arm_end != null) {
				return false;
			}
		} else if (!homology_arm_end.equals(other.homology_arm_end)) {
			return false;
		}
		if (homology_arm_start == null) {
			if (other.homology_arm_start != null) {
				return false;
			}
		} else if (!homology_arm_start.equals(other.homology_arm_start)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (intron == null) {
			if (other.intron != null) {
				return false;
			}
		} else if (!intron.equals(other.intron)) {
			return false;
		}
		if (loxp_end == null) {
			if (other.loxp_end != null) {
				return false;
			}
		} else if (!loxp_end.equals(other.loxp_end)) {
			return false;
		}
		if (loxp_start == null) {
			if (other.loxp_start != null) {
				return false;
			}
		} else if (!loxp_start.equals(other.loxp_start)) {
			return false;
		}
		if (mutation_method_id == null) {
			if (other.mutation_method_id != null) {
				return false;
			}
		} else if (!mutation_method_id.equals(other.mutation_method_id)) {
			return false;
		}
		if (mutation_subtype_id == null) {
			if (other.mutation_subtype_id != null) {
				return false;
			}
		} else if (!mutation_subtype_id.equals(other.mutation_subtype_id)) {
			return false;
		}
		if (mutation_type_id == null) {
			if (other.mutation_type_id != null) {
				return false;
			}
		} else if (!mutation_type_id.equals(other.mutation_type_id)) {
			return false;
		}
		if (project_design_id == null) {
			if (other.project_design_id != null) {
				return false;
			}
		} else if (!project_design_id.equals(other.project_design_id)) {
			return false;
		}
		if (reporter == null) {
			if (other.reporter != null) {
				return false;
			}
		} else if (!reporter.equals(other.reporter)) {
			return false;
		}
		if (strand == null) {
			if (other.strand != null) {
				return false;
			}
		} else if (!strand.equals(other.strand)) {
			return false;
		}
		if (subtype_description == null) {
			if (other.subtype_description != null) {
				return false;
			}
		} else if (!subtype_description.equals(other.subtype_description)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (updated_at == null) {
			if (other.updated_at != null) {
				return false;
			}
		} else if (!updated_at.equals(other.updated_at)) {
			return false;
		}
		return true;
	}
	public String getMarker_symbol() {
		return marker_symbol;
	}
	public void setMarker_symbol(String marker_symbol) {
		this.marker_symbol = marker_symbol;
	}


}
