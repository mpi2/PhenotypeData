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
package org.mousephenotype.cda.solr.web.dto;




import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.MarkerBean;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 * Abstract representation of a single row in a gene or phenotype page
 * "phenotypes" HTML table. This class is a repository for common code but does
 * not (and must not) contain compareTo methods as they differ by [gene or
 * phenotype] page. Making the class abstract forces users to instantiate
 * a subclass that extends this class, such as GenePageTableRow or
 * PhenotypePageTableRow. Should more flavours of the "phenotypes" HTML table
 * be needed, simply extend this class and write a compareTo method.
 *
 * This class used to be called PhenotypeRow serving gene and phenotype pages
 * but was broken out into this abstract class and two concrete classes to
 * architect correct "phenotypes" HTML page row ordering.
 */
public abstract class DataTableRow implements Comparable<DataTableRow> {

    private Map<String, String> config;
    protected BasicBean phenotypeTerm;
    protected MarkerBean gene;
    protected MarkerBean allele;
    protected List<String> sexes;
    protected ZygosityType zygosity;
    protected int projectId;
    protected String phenotypingCenter;
    protected ImpressBaseDTO procedure;
    protected ImpressBaseDTO parameter;
    protected String dataSourceName;//to hold the name of the origin of the data e.g. Europhenome or WTSI Mouse Genetics Project
    protected String graphUrl;
    protected ImpressBaseDTO pipeline;
    protected Double pValue;
    protected boolean isPreQc;
    protected String gid;

    public DataTableRow() { }


    public DataTableRow(PhenotypeCallSummary pcs, String baseUrl, Map<String, String> config) {

	    this.config = config;
        List<String> sex = new ArrayList<String>();
        sex.add(pcs.getSex().toString());
        this.setGid(pcs.getgId());
        this.setPreQc(pcs.isPreQC());
 ////       this.setGene(pcs.getGene());
///        this.setAllele(pcs.getAllele());
        this.setSexes(sex);
//TODO DELETE THIS WHOLE METHOD//////    this.setPhenotypeTerm(pcs.getPhenotypeTerm());
  //      this.setPipeline(pcs.getPipeline());
		// zygosity representation depends on source of information
        // we need to know what the data source is so we can generate appropriate link on the page

        this.pValue = pcs.getpValue();
        this.setDataSourceName(pcs.getDatasource().getName());

        this.setZygosity(pcs.getZygosity());
        if (pcs.getExternalId() != null) {
            this.setProjectId(pcs.getExternalId());
        }

 //       this.setProcedure(pcs.getProcedure());
  /////      this.setParameter(pcs.getParameter());
        this.setPhenotypingCenter(pcs.getPhenotypingCenter());

        this.setGraphUrl(baseUrl);

    }

    public DataTableRow(PhenotypeCallSummaryDTO pcs, String baseUrl, Map<String, String> config) {

	    this.config = config;
        List<String> sex = new ArrayList<String>();
        sex.add(pcs.getSex().toString());
        this.setGid(pcs.getgId());
        this.setPreQc(pcs.isPreQC());
        this.setGene(pcs.getGene());
        this.setAllele(pcs.getAllele());
        this.setSexes(sex);
        this.setPhenotypeTerm(pcs.getPhenotypeTerm());
        this.setPipeline(pcs.getPipeline());
        this.pValue = pcs.getpValue();
        this.setDataSourceName(pcs.getDatasource().getName());
        this.setZygosity(pcs.getZygosity());
        this.setProcedure(pcs.getProcedure());
        this.setParameter(pcs.getParameter());
        this.setPhenotypingCenter(pcs.getPhenotypingCenter());

        if (pcs.getProject() != null && pcs.getProject().getId() != null) {
            this.setProjectId(new Integer(pcs.getProject().getId()));
        }
       
        this.setGraphUrl(baseUrl);

    }
    
    @Override
    public abstract int compareTo(DataTableRow o);


    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

	/**
	 * @return the gid
	 */
	public String getGid() {

		return gid;
	}


	/**
	 * @param gid the gid to set
	 */
	public void setGid(String gid) {

		this.gid = gid;
	}


	/**
	 * @return the pValue
	 */
	public Double getpValue() {

		return pValue;
	}

	/**
	 * @param pValue the pValue to set
	 */
	public void setpValue(Double pValue) {

		this.pValue = pValue;
	}

	/**
	 * @return the isPreQc
	 */
	public boolean isPreQc() {

		return isPreQc;
	}

	/**
	 * @param isPreQc the isPreQc to set
	 */
	public void setPreQc(boolean isPreQc) {

		this.isPreQc = isPreQc;
	}

	public void setPValue(Double pValue) {
        this.pValue = pValue;
    }

    public Double getPrValue() {
        return this.pValue;
    }

    public String getPrValueAsString() {
        BigDecimal bd = new BigDecimal(this.pValue);
        bd = bd.round(new MathContext(3));
        double rounded = bd.doubleValue();
        String result = Double.toString(rounded);
        return result;
    }

    public ImpressBaseDTO getPipeline() {
        return pipeline;
    }

    public void setPipeline(ImpressBaseDTO pipeline) {
        this.pipeline = pipeline;
    }

    public String getGraphUrl() {
        return graphUrl;
    }

    public void setGraphUrl(String graphBaseUrl) {
        this.graphUrl = buildGraphUrl(graphBaseUrl);
    }

    public String buildGraphUrl(String baseUrl) {
    	String url= baseUrl;
    	if (!isPreQc){
    		url = getChartPageUrlPostQc(baseUrl, gene.getAccessionId(), allele.getAccessionId(), null, zygosity, parameter.getStableId(),
    		pipeline.getStableId(), phenotypingCenter);
        } else {
		    // Need to use the drupal base url because phenoview is not mapped under the /data url
		    url = config.get("drupalBaseUrl");
		    url += "/../phenoview/?gid=" + gid;

            if (parameter != null) {
                url += "&qeid=" + parameter.getStableId();
            }
        }
        return url;
    }

    public String getPhenotypingCenter() {
        return this.phenotypingCenter;
    }

    public void setPhenotypingCenter(String phenotypingCenter) {
        this.phenotypingCenter = phenotypingCenter;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public BasicBean getPhenotypeTerm() {
        return phenotypeTerm;
    }

    public void setPhenotypeTerm(BasicBean term) {
        this.phenotypeTerm = term;
    }

    public MarkerBean getAllele() {
        return allele;
    }

    public void setAllele(MarkerBean allele) {
        this.allele = allele;
    }

    public List<String> getSexes() {
        return sexes;
    }

    public void setSexes(List<String> sex) {
        this.sexes = sex;
    }

    public ZygosityType getZygosity() {
        return zygosity;
    }

    public void setZygosity(ZygosityType zygosityType) {
        this.zygosity = zygosityType;
    }

    /**
     * @return the projectId
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public MarkerBean getGene() {
        return gene;
    }

    public void setGene(MarkerBean gene) {
        this.gene = gene;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allele == null) ? 0 : allele.hashCode());
        result = prime * result + ((dataSourceName == null) ? 0 : dataSourceName.hashCode());
        result = prime * result + ((phenotypeTerm == null) ? 0 : phenotypeTerm.hashCode());
        result = prime * result + ((phenotypingCenter == null) ? 0 : phenotypingCenter.hashCode());
        result = prime * result + ((zygosity == null) ? 0 : zygosity.hashCode());
        if (gene != null) {
            result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
            result = prime * result + ((procedure == null) ? 0 : procedure.hashCode());
        }
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
        	System.out.println("a");
            return false;
        }
        DataTableRow other = (DataTableRow) obj;
        if (allele == null) {
            if (other.allele != null) {

            	System.out.println("b");
                return false;
            }
        } else if ( ! allele.equals(other.allele)) {

        	System.out.println("c");
            return false;
        }
        if (dataSourceName == null) {
            if (other.dataSourceName != null) {

            	System.out.println("d");
                return false;
            }
        } else if ( ! dataSourceName.equals(other.dataSourceName)) {

        	System.out.println("e");
            return false;
        }
        if (parameter == null) {
            if (other.parameter != null) {

            	System.out.println("f");
                return false;
            }
        } else if ( ! parameter.equals(other.parameter)) {

        	System.out.println("g");
            return false;
        }
        if (procedure == null) {
            if (other.procedure != null) {

            	System.out.println("h");
                return false;
            }
        } else if ( ! procedure.equals(other.procedure)) {

        	System.out.println("i");
            return false;
        }
        if (gene == null) {
            if (other.gene != null) {

            	System.out.println("j");
                return false;
            }
        } else if ( ! gene.equals(other.gene)) {

        	System.out.println("k");
            return false;
        }
        if (phenotypingCenter == null) {
            if (other.phenotypingCenter != null) {

            	System.out.println("l");
                return false;
            }
        } else if ( ! phenotypingCenter.equals(other.phenotypingCenter)) {

        	System.out.println("m");
            return false;
        }
        if (phenotypeTerm == null) {
            if (other.phenotypeTerm != null) {

            	System.out.println("n");
                return false;
            }
        } else if ( ! phenotypeTerm.equals(other.phenotypeTerm)) {

        	System.out.println("o");
            return false;
        }
        if (zygosity != other.zygosity) {

        	System.out.println("p");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PhenotypeRow [phenotypeTerm=" + phenotypeTerm
                + ", gene=" + gene + ", allele=" + allele + ", sexes=" + sexes
                + ", zygosity=" + zygosity
                + ", projectId=" + projectId + ", procedure=" + procedure
                + ", parameter=" + parameter + ", dataSourceName="
                + dataSourceName + ", phenotypingCenter=" + phenotypingCenter + "]";
    }


    public String toTabbedString(String targetPage) {

        String res = "";

        if (targetPage.equalsIgnoreCase("gene")) {
            res = getPhenotypeTerm().getName() + "\t"
                    + getAllele().getSymbol() + "\t"
                    + getZygosity() + "\t"
                    + getSexes().get(0) + "\t"
                    + getProcedure().getName() + " | " + getParameter().getName() + "\t"
                    + getPhenotypingCenter() + "\t"
                    + getDataSourceName() + "\t"
                    + getPrValueAsString() + "\t"
                    + getGraphUrl();
        } else if (targetPage.equalsIgnoreCase("phenotype")) {
            res = getGene().getSymbol() + "\t"
                    + getAllele().getName() + "\t"
                    + getZygosity() + "\t"
                    + getSexes().get(0) + "\t"
                    + getPhenotypeTerm().getName() + "\t"
                    + getProcedure().getName() + " | " + getParameter().getName() + "\t"
                    + getPhenotypingCenter() + "\t"
                    + getDataSourceName() + "\t"
                    + getPrValueAsString() + "\t"
                    + getGraphUrl();
        }
        return res;
    }

    public static String getChartPageUrlPostQc(String baseUrl, String geneAcc, String alleleAcc, String metadataGroup, ZygosityType zygosity, String parameterStableId, String pipelineStableId, String phenotypingCenter) {
        String url = baseUrl;
        url += "/charts?accession=" + geneAcc;
        url += "&allele_accession_id=" + alleleAcc;
        if (metadataGroup != null) {
            url += "&metadata_group=" + metadataGroup;
        }
        if (zygosity != null) {
            url += "&zygosity=" + zygosity.name();
        }
        if (parameterStableId != null) {
            url += "&parameter_stable_id=" + parameterStableId;
        }
        if (pipelineStableId != null) {
            url += "&pipeline_stable_id=" + pipelineStableId;
        }
        if (phenotypingCenter != null) {
            url += "&phenotyping_center=" + phenotypingCenter;
        }
        return url;
    }

}
