package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExperimentDetails {
	
	@JsonProperty("status")
	private String status;//"Successful",
	@JsonProperty("procedure_group")
	private String procedureGroup;//: "IMPC_HEM",
	@JsonProperty("procedure_stable_id")
	private String procedureStableId;//: "IMPC_HEM_002",
	@JsonProperty("procedure_name")
	private String procedureName;//: "Hematology",
	@JsonProperty("parameter_stable_id")
	private String parameterStableId;//: "IMPC_HEM_001_001",
	@JsonProperty("parameter_name")
	private String parameterName;//: "White blood cell count",
	@JsonProperty("phenotyping_center")
	private String phenotypingCenter;//: "TCP",
	@JsonProperty("allele_symbol")
	private String alleleSymbol;//: "Tox3<tm1b(KOMP)Mbp>",
	@JsonProperty("gene_symbol")
	private String geneSymbol;//"Tox3",
	@JsonProperty("gene_accession_id")
	private String geneAccessionId;//: "MGI:3039593",
	@JsonProperty("pipeline_name")
	private String pipelineName;//: "TCP Pipeline",
	@JsonProperty("pipeline_stable_id")
	private String pipelineStableId;//: "TCP_001",
	@JsonProperty("strain_accession_id")
	private String strainAccessionId;//: "MGI:2683688",
	@JsonProperty("metadata_group")
	private String metadataGroup;//: "6d8b9f2ca6828de31a76ca43d1da2771",
	@JsonProperty("zygosity")
	private String zygosity;//: "heterozygote",
	@JsonProperty("colony_id")
	private String colonyId;//: "BL2283",
    //"reserved": null
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProcedureGroup() {
		return procedureGroup;
	}
	public void setProcedureGroup(String procedureGroup) {
		this.procedureGroup = procedureGroup;
	}
	public String getProcedureStableId() {
		return procedureStableId;
	}
	public void setProcedureStableId(String procedureStableId) {
		this.procedureStableId = procedureStableId;
	}
	public String getProcedureName() {
		return procedureName;
	}
	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}
	public String getParameterStableId() {
		return parameterStableId;
	}
	public void setParameterStableId(String parameterStableId) {
		this.parameterStableId = parameterStableId;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public String getPhenotypingCenter() {
		return phenotypingCenter;
	}
	public void setPhenotypingCenter(String phenotypingCenter) {
		this.phenotypingCenter = phenotypingCenter;
	}
	public String getAlleleSymbol() {
		return alleleSymbol;
	}
	public void setAlleleSymbol(String alleleSymbol) {
		this.alleleSymbol = alleleSymbol;
	}
	public String getGeneSymbol() {
		return geneSymbol;
	}
	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}
	public String getGeneAccessionId() {
		return geneAccessionId;
	}
	public void setGeneAccessionId(String geneAccessionId) {
		this.geneAccessionId = geneAccessionId;
	}
	public String getPipelineName() {
		return pipelineName;
	}
	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}
	public String getPipelineStableId() {
		return pipelineStableId;
	}
	public void setPipelineStableId(String pipelineStableId) {
		this.pipelineStableId = pipelineStableId;
	}
	public String getStrainAccessionId() {
		return strainAccessionId;
	}
	public void setStrainAccessionId(String strainAccessionId) {
		this.strainAccessionId = strainAccessionId;
	}
	public String getMetadataGroup() {
		return metadataGroup;
	}
	public void setMetadataGroup(String metadataGroup) {
		this.metadataGroup = metadataGroup;
	}
	public String getZygosity() {
		return zygosity;
	}
	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}
	public String getColonyId() {
		return colonyId;
	}
	public void setColonyId(String colonyId) {
		this.colonyId = colonyId;
	}
  

}
