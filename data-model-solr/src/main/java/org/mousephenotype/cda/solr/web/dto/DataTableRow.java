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


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.pojo.PhenotypeCallSummary;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.MarkerBean;


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
    protected String lifeStageName;
    protected String lifeStageAcc;
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
    protected String colonyId;

    public DataTableRow() { }       

    public DataTableRow(PhenotypeCallSummary pcs, String baseUrl, Map<String, String> config) throws UnsupportedEncodingException {

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

    public DataTableRow(PhenotypeCallSummaryDTO pcs, String baseUrl, Map<String, String> config, ImageService imageService) throws UnsupportedEncodingException, SolrServerException {

	    this.config = config;
        List<String> sex = new ArrayList<String>();
        sex.add(pcs.getSex().toString());
        this.setGid(pcs.getgId());
        this.setPreQc(pcs.isPreQC());
        this.setGene(pcs.getGene());
        this.setAllele(pcs.getAllele());
        this.setSexes(sex);
        this.setLifeStageName(pcs.getLifeStageName());
        this.setPhenotypeTerm(pcs.getPhenotypeTerm());
        this.setPipeline(pcs.getPipeline());
        this.pValue = pcs.getpValue();
        this.setDataSourceName(pcs.getDatasource().getName());
        this.setZygosity(pcs.getZygosity());
        this.setProcedure(pcs.getProcedure());
        this.setParameter(pcs.getParameter());
        this.setPhenotypingCenter(pcs.getPhenotypingCenter());
        this.setColonyId(pcs.getColonyId());

        if (pcs.getProject() != null && pcs.getProject().getId() != null) {
            this.setProjectId(new Integer(pcs.getProject().getId()));
        }

        if (pcs.getPhenotypeTerm().getId().startsWith("MPATH:") && ! imageService.hasImages(pcs.getGene().getAccessionId(), pcs.getProcedure().getName(), pcs.getColonyId())){
			this.graphUrl = "";
        }
        else {
            this.setGraphUrl(baseUrl);
        }

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
     * @return the colony id
     */
    public String getColonyId() {

        return colonyId;
    }


    /**
     * @param colonyId the colonyId to set
     */
    public void setColonyId(String colonyId) {

        this.colonyId = colonyId;
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

    public void setGraphUrl(String graphBaseUrl) throws UnsupportedEncodingException {
        this.graphUrl = buildGraphUrl(graphBaseUrl);
    }

    public String buildGraphUrl(String baseUrl) throws UnsupportedEncodingException {
    	String url= baseUrl;

        if (!isPreQc){
            if ( procedure.getName().startsWith("Histopathology") ){
                url = getMpathImagesUrlPostQc(baseUrl, gene.getAccessionId(), gene.getSymbol(), procedure.getName(), this.colonyId);
                System.out.println("URL: " + url);


            }
            else {
                url = getChartPageUrlPostQc(baseUrl, gene.getAccessionId(), allele.getAccessionId(), null, zygosity, parameter.getStableId(),
                        pipeline.getStableId(), phenotypingCenter);
            }
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

    public String getLifeStageName() {
        return lifeStageName;
    }

    public void setLifeStageName(String lifeStageName) {
        this.lifeStageName = lifeStageName;
    }

    public String getLifeStageAcc() {
        return lifeStageAcc;
    }

    public void setLifeStageAcc(String lifeStageAcc) {
        this.lifeStageAcc = lifeStageAcc;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataTableRow that = (DataTableRow) o;

        if (projectId != that.projectId) return false;
        if (isPreQc != that.isPreQc) return false;
        if (config != null ? !config.equals(that.config) : that.config != null) return false;
        if (phenotypeTerm != null ? !phenotypeTerm.equals(that.phenotypeTerm) : that.phenotypeTerm != null)
            return false;
        if (gene != null ? !gene.equals(that.gene) : that.gene != null) return false;
        if (allele != null ? !allele.equals(that.allele) : that.allele != null) return false;
        if (sexes != null ? !sexes.equals(that.sexes) : that.sexes != null) return false;
        if (zygosity != that.zygosity) return false;
        if (lifeStageName != null ? !lifeStageName.equals(that.lifeStageName) : that.lifeStageName != null)
            return false;
        if (lifeStageAcc != null ? !lifeStageAcc.equals(that.lifeStageAcc) : that.lifeStageAcc != null) return false;
        if (phenotypingCenter != null ? !phenotypingCenter.equals(that.phenotypingCenter) : that.phenotypingCenter != null)
            return false;
        if (procedure != null ? !procedure.equals(that.procedure) : that.procedure != null) return false;
        if (parameter != null ? !parameter.equals(that.parameter) : that.parameter != null) return false;
        if (dataSourceName != null ? !dataSourceName.equals(that.dataSourceName) : that.dataSourceName != null)
            return false;
        if (graphUrl != null ? !graphUrl.equals(that.graphUrl) : that.graphUrl != null) return false;
        if (pipeline != null ? !pipeline.equals(that.pipeline) : that.pipeline != null) return false;
        if (pValue != null ? !pValue.equals(that.pValue) : that.pValue != null) return false;
        return !(gid != null ? !gid.equals(that.gid) : that.gid != null);

    }

    @Override
    public int hashCode() {
        int result = config != null ? config.hashCode() : 0;
        result = 31 * result + (phenotypeTerm != null ? phenotypeTerm.hashCode() : 0);
        result = 31 * result + (gene != null ? gene.hashCode() : 0);
        result = 31 * result + (allele != null ? allele.hashCode() : 0);
        result = 31 * result + (sexes != null ? sexes.hashCode() : 0);
        result = 31 * result + (zygosity != null ? zygosity.hashCode() : 0);
        result = 31 * result + (lifeStageName != null ? lifeStageName.hashCode() : 0);
        result = 31 * result + (lifeStageAcc != null ? lifeStageAcc.hashCode() : 0);
        result = 31 * result + projectId;
        result = 31 * result + (phenotypingCenter != null ? phenotypingCenter.hashCode() : 0);
        result = 31 * result + (procedure != null ? procedure.hashCode() : 0);
        result = 31 * result + (parameter != null ? parameter.hashCode() : 0);
        result = 31 * result + (dataSourceName != null ? dataSourceName.hashCode() : 0);
        result = 31 * result + (graphUrl != null ? graphUrl.hashCode() : 0);
        result = 31 * result + (pipeline != null ? pipeline.hashCode() : 0);
        result = 31 * result + (pValue != null ? pValue.hashCode() : 0);
        result = 31 * result + (isPreQc ? 1 : 0);
        result = 31 * result + (gid != null ? gid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PhenotypeRow [phenotypeTerm=" + phenotypeTerm
                + ", gene=" + gene + ", allele=" + allele + ", sexes=" + sexes
                + ", zygosity=" + zygosity
                + ", lifeStageName=" + lifeStageName
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
                    + getLifeStageName() + "\t"
                    + getProcedure().getName() + " | " + getParameter().getName() + "\t"
                    + getPhenotypingCenter() + " | " + getDataSourceName() + "\t"
                    + getPrValueAsString() + "\t"
                    + getGraphUrl();
        } else if (targetPage.equalsIgnoreCase("phenotype")) {
            res = getGene().getSymbol() + "\t"
                    + getAllele().getName() + "\t"
                    + getZygosity() + "\t"
                    + getSexes().get(0) + "\t"
                    + getLifeStageName() + "\t"
                    + getPhenotypeTerm().getName() + "\t"
                    + getProcedure().getName() + " | " + getParameter().getName() + "\t"
                    + getPhenotypingCenter() + " | " + getDataSourceName() + "\t"
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

    public static String getMpathImagesUrlPostQc(String baseUrl, String geneAcc, String geneSymbol, String procedureName, String colonyId) throws UnsupportedEncodingException {
       //images?q=*:*&defType=edismax&wt=json&fq=(gene_accession_id=:"" AND colony_id:"" AND parameter_stable_id:"XXX")&title=gene null in brain
        String url = baseUrl + "/impcImages/images?";
        String params = "q=*&defType=edismax&wt=json&fq=(";
        params += "gene_accession_id:" + URLEncoder.encode("\"" + geneAcc + "\"", "UTF-8");
        params += " AND procedure_name:" + URLEncoder.encode(procedureName, "UTF-8");
        params += " AND colony_id:" + colonyId + ")";
        params += "&title=gene " + URLEncoder.encode(geneSymbol + " in " + procedureName, "UTF-8");
       // params = URLEncoder.encode(params, "UTF-8");
        url += params;

        return url;
    }


}
