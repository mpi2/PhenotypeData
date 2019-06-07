package uk.ac.ebi.phenotype.stats.model;

import javax.annotation.Resource;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Resource
public class Statistics  {
	
	@Id
	private String id;
	
	private String procedureStableId;
	
	public String getProcedureStableId() {
		return procedureStableId;
	}

	public void setProcedureStableId(String procedureStableId) {
		this.procedureStableId = procedureStableId;
	}

	private String parameterStableId;
	
	private String parameterStableName;
	
	private String pipelineStableId;
	
	public String getPipelineStableId() {
		return pipelineStableId;
	}

	public void setPipelineStableId(String pipelineStableId) {
		this.pipelineStableId = pipelineStableId;
	}

	private String allele;
	
	private String geneSymbol;
	
	private String phenotypingCenter;
	
	private String geneAccession;
	
	private String alleleAccession;
	
	private String metaDataGroup;
	
	public String getPhenotypingCenter() {
		return phenotypingCenter;
	}

	public void setPhenotypingCenter(String phenotypingCenter) {
		this.phenotypingCenter = phenotypingCenter;
	}

	public String getMetaDataGroup() {
		return metaDataGroup;
	}

	public void setMetaDataGroup(String metaDataGroup) {
		this.metaDataGroup = metaDataGroup;
	}

	private String zygosity;
	
	private String colonyId;
	
	private Integer impressParameterKey;
	
	private Integer impressProtocolKey;
	
	public String getColonyId() {
		return colonyId;
	}

	public void setColonyId(String colonyId) {
		this.colonyId = colonyId;
	}

	public Integer getImpressParameterKey() {
		return impressParameterKey;
	}

	public void setImpressParameterKey(int impressParameterKey) {
		this.impressParameterKey = impressParameterKey;
	}

	public Integer getImpressProtocolKey() {
		return impressProtocolKey;
	}

	public void setImpressProtocolKey(int impressProtocolKey) {
		this.impressProtocolKey = impressProtocolKey;
	}

	public String getZygosity() {
		return zygosity;
	}

	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}

	

	public String getGeneAccession() {
		return geneAccession;
	}

	public void setGeneAccession(String geneAccession) {
		this.geneAccession = geneAccession;
	}

	public String getAlleleAccession() {
		return alleleAccession;
	}

	public void setAlleleAccession(String alleleAccession) {
		this.alleleAccession = alleleAccession;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}



	public String getAllele() {
		return allele;
	}

	public void setAllele(String allele) {
		this.allele = allele;
	}

	public Statistics() {
		super();
	}

	public String getParameterStableName() {
		return parameterStableName;
	}

	public void setParameterStableName(String parameterStableName) {
		this.parameterStableName = parameterStableName;
	}

	public String getParameterStableId() {
		return parameterStableId;
	}

	public void setParameterStableId(String parameterStableId) {
		this.parameterStableId = parameterStableId;
	}

	public Statistics(Result result) {
		this.result=result;
	}
	
	Result result;
	
	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	private String headerInfo;
	public String getHeaderInfo() {
		return headerInfo;
	}

	public void setHeaderInfo(String headerInfo) {
		this.headerInfo = headerInfo;
	}

	//set the points objects before loading into mongo
	public void setPoints() {
		result.getDetails().setPoints();
		
	}

	@Override
	public String toString() {
		return "Stats [id=" + id + ", parameterStableId=" + parameterStableId + ", parameterStableName="
				+ parameterStableName + ", pipelineStableId=" + pipelineStableId + ", allele=" + allele
				+ ", geneSymbol=" + geneSymbol + ", phenotypingCenter=" + phenotypingCenter + ", geneAccession="
				+ geneAccession + ", alleleAccession=" + alleleAccession + ", metaDataGroup=" + metaDataGroup
				+ ", zygosity=" + zygosity + ", colonyId=" + colonyId + ", impressParameterKey=" + impressParameterKey
				+ ", impressProtocolKey=" + impressProtocolKey + ", result=" + result + ", headerInfo=" + headerInfo
				+ "]";
	}

	

	

}
