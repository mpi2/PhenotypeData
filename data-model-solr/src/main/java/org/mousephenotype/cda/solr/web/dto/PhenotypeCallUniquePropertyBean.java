package org.mousephenotype.cda.solr.web.dto;

import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.MarkerBean;
/**
 * Used to store parameters needed to reconstruct preQc and postQc graphs after collapsing rows in the gene page and phenotype pages
 * @author jwarren
 *
 */
public class PhenotypeCallUniquePropertyBean {
	private Integer project;
	private ImpressBaseDTO pipeline;
	private ImpressBaseDTO procedure;
	private ImpressBaseDTO parameter;
	private MarkerBean allele;
	private String gId;//phenoDCCs internal id that doesn't represent a gene id as they are different for the same mgi gene accession
	private String phenotypingCenter;

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	private String dataSource;

	public PhenotypeCallUniquePropertyBean(){
		
	}


	public PhenotypeCallUniquePropertyBean(Integer project, ImpressBaseDTO procedure, ImpressBaseDTO parameter,
			ImpressBaseDTO pipeline, MarkerBean allele) {
		super();
		this.project = project;
		this.pipeline = pipeline;
		this.procedure = procedure;
		this.parameter = parameter;
		this.allele = allele;
	}

	public Integer getProject() {
		return project;
	}

	public void setProject(Integer project) {
		this.project = project;
	}

	public ImpressBaseDTO getProcedure() {
		return procedure;
	}

	public void setProcedure(ImpressBaseDTO procedure) {
		this.procedure = procedure;
	}

	public ImpressBaseDTO getParameter() {
		return parameter;
	}

	public void setParameter(ImpressBaseDTO parameter) {
		this.parameter = parameter;
	}

	public ImpressBaseDTO getPipeline() {
		return pipeline;
	}

	public void setPipeline(ImpressBaseDTO pipeline) {
		this.pipeline = pipeline;
	}

	public MarkerBean getAllele() {
		return allele;
	}

	public void setAllele(MarkerBean allele) {
		this.allele = allele;
	}
	
	public String getPhenotypingCenter() {
		return phenotypingCenter;
	}


	public void setPhenotypingCenter(String phenotypingCenters) {
		this.phenotypingCenter = phenotypingCenters;
	}
	
	public String getgId() {
		return gId;
	}


	public void setgId(String gId) {
		this.gId = gId;
	}

	@Override
	public String toString() {
		return "PhenotypeCallUniquePropertyBean [project=" + project + ", procedure=" + procedure + ", parameter="
				+ parameter + ", pipeline=" + pipeline + ", allele=" + allele + ", phenotypingCenter="
				+ phenotypingCenter + " gid="+gId+"]";
	}


	

	
	

}
