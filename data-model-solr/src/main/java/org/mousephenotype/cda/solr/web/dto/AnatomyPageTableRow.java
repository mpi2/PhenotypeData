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

import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.MarkerBean;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.EvidenceLink.IconType;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class AnatomyPageTableRow extends DataTableRow{


	String expression;
    List<OntologyTerm> anatomy;
    String anatomyLinks;
    int numberOfImages = 0;


	public void  addIncrementToNumberOfImages(){
    	numberOfImages ++;
    }

    public AnatomyPageTableRow(ObservationDTO observation, String anatomyId, String baseUrl, String expressionValue) {

    	super();
        List<String> sex = new ArrayList<>();
        sex.add(observation.getSex());
        MarkerBean gene = new MarkerBean();
        gene.setSymbol(observation.getGeneSymbol());
        gene.setAccessionId(observation.getGeneAccession());
        MarkerBean allele = new MarkerBean();
        allele.setSymbol(observation.getAlleleSymbol());
        allele.setSymbol(observation.getAlleleAccession());
        this.setGene(gene);
        this.setAllele(allele);
        this.setSexes(sex);
        this.setDataSourceName(observation.getDataSourceName());
        this.setZygosity(observation.getZygosity() != null ? ZygosityType.valueOf(observation.getZygosity()) : ZygosityType.not_applicable);
        ImpressBaseDTO proc = new ImpressBaseDTO();
        proc.setName(observation.getProcedureName());
        proc.setStableId(observation.getProcedureStableId());
        ImpressBaseDTO param = new ImpressBaseDTO();
        param.setName(observation.getParameterName());
        param.setStableId(observation.getParameterStableId());
        this.setProcedure(proc);
        this.setParameter(param);
        this.setPhenotypingCenter(observation.getPhenotypingCenter());

        List<OntologyTerm> anatomyTerms = new ArrayList<>();
        
		if (observation instanceof ImageDTO) {
			ImageDTO imageDTO = (ImageDTO) observation;
			for (int i = 0; i < imageDTO.getAnatomyId().size(); i++) {
				if (imageDTO.getExpression(observation.getAnatomyId().get(i)).equalsIgnoreCase(expressionValue)) {
					OntologyTerm anatomy = new OntologyTerm();
					anatomy.setId(new DatasourceEntityId(observation.getAnatomyId().get(i), -1));
					anatomy.setName(observation.getAnatomyTerm().get(i));
					anatomyTerms.add(anatomy);
				}
			}
		} else {//else currently this should be a normal observation and so will have categorical parameter data

			for (int i = 0; i < observation.getAnatomyId().size(); i++) {
				if (observation.getCategory().equalsIgnoreCase(expressionValue)) {
					OntologyTerm anatomy = new OntologyTerm();
					anatomy.setId(new DatasourceEntityId(observation.getAnatomyId().get(i), -1));
					anatomy.setName(observation.getAnatomyTerm().get(i));
					anatomyTerms.add(anatomy);
				}
			}

		}

        this.setExpression(expressionValue);
        this.setAnatomy(anatomyTerms);

		this.setEvidenceLink(buildImageUrl(baseUrl, anatomyId, expressionValue));
        this.setAnatomyLinks(getAnatomyWithLinks(baseUrl));
        if(expressionValue.equals("ambiguous")){
			System.out.println("odd expression value for row="+this);
		}
    }
    
   


    public String getAnatomyWithLinks(String baseUrl){
    	
    	StringBuilder links = new StringBuilder();
    	
    	if (anatomy != null && anatomy.size() > 0){
	    	links = new StringBuilder("<a href=\"" + baseUrl + "/anatomy/");
	    	for (int i = 0; i < anatomy.size(); i++){
	    		links.append(anatomy.get(i).getId().getAccession()).append("\">").append(anatomy.get(i).getName()).append("</a>");
	    		if (i != anatomy.size()-1 ){
	    			links.append(", <a href=\"").append(baseUrl).append("/anatomy/");
	    		}
	    	}
    	}

    	return links.toString();
    }


    public EvidenceLink buildImageUrl(String baseUrl, String anatomyId, String expressionValue){
    	//http://localhost:8080/phenotype-archive/imageCompara?anatomy_id:%22EMAPA:16105%22&gene_symbol:Ap4e1&parameter_name:%22LacZ%20images%20wholemount%22&parameter_association_value:%22ambiguous%22
		//System.out.println("building evidence link");
    	String url = baseUrl + "/imageComparator?";
        url +=ImageDTO.ANATOMY_ID + "=" + anatomyId;
       

    	if (getGene().getSymbol()!= null){
    		url += "&acc="+ this.getGene().getAccessionId();
    	} else {
    		url += "&" + ImageDTO.BIOLOGICAL_SAMPLE_GROUP + "=control";
    	}
    	if (getParameter() != null){
    		url += "&" + ImageDTO.PARAMETER_STABLE_ID + "=" + getParameter().getStableId();
    	}
    	if ( expressionValue != null){
    		url += "&" + ImageDTO.PARAMETER_ASSOCIATION_VALUE + "=" + expressionValue;
    	}
    	if( zygosity!=null){
    		url+= "&" +ImageDTO.ZYGOSITY+"="+zygosity;
    	}
    	if( phenotypingCenter!=null){
    		url+= "&" +ImageDTO.PHENOTYPING_CENTER+"="+phenotypingCenter;
    	}

    	EvidenceLink link = new EvidenceLink();
    	//System.out.println("setting url in link "+url);
    	link.setUrl(url);
    	link.setIconType(IconType.IMAGE);
    	link.setAlt("Images");

    	return link;
    }

	@Override
	public int compareTo(DataTableRow o) {

		return 0;
	}

	public String getExpression() {

		return expression;
	}

	public void setExpression(String expression) {

		this.expression = expression;
	}

	public String getKey(){
		//return getAllele().getSymbol() + getZygosity().name() +  getExpression();
		return getGene().getSymbol() + getZygosity().name() +  getExpression();
	}

	public boolean equals(AnatomyPageTableRow obj) {
	    return this.getKey().equalsIgnoreCase(obj.getKey());
	}

	public int getNumberOfImages() {

		return numberOfImages;
	}

	public void setNumberOfImages(int numberOfImages) {

		this.numberOfImages = numberOfImages;
	}


	public void addSex(String sex){

		if (!sexes.contains(sex)){
			sexes.add(sex);
		}
	}

	public List<OntologyTerm> getAnatomy() {

		return anatomy;
	}


	public void setAnatomy(List<OntologyTerm> anatomy) {

		this.anatomy = anatomy;
	}


	public String getAnatomyLinks() {

		return anatomyLinks;
	}


	public void setAnatomyLinks(String anatomyLinks) {

		this.anatomyLinks = anatomyLinks;
	}


	@Override
	public String toString() {
		return "AnatomyPageTableRow [expression=" + expression + ", anatomy=" + anatomy + ", anatomyLinks="
				+ anatomyLinks + ", numberOfImages=" + numberOfImages + "]";
	}


	public String toTabbedString() {
		String tab="\t";
		String geneAccession;
		if(this.getGene().getAccessionId()==null) {
			geneAccession="control";
		}else {
			geneAccession=this.getGene().getAccessionId();
		}
		String imageUrl="";
		if(numberOfImages>0) {
			imageUrl="https://www.mousephenotype.org";
			if(this.getEvidenceLink()!=null && this.getEvidenceLink().getUrl()!=null) {
			imageUrl+=this.getEvidenceLink().getUrl();
			}
		}
		StringJoiner anatomyStringJoiner = new StringJoiner(",");
		String anatomyString="";
		if(anatomy!=null) {
			for( OntologyTerm ana: anatomy) {
				anatomyStringJoiner.add(ana.getName());
			}
			anatomyString=anatomyStringJoiner.toString();
		}
		StringJoiner anatomyLinkStringJoiner = new StringJoiner(",");
		String anatomyLinkString="";
		if(anatomy!=null) {
			for( OntologyTerm ana: anatomy) {
				anatomyLinkStringJoiner.add("http://www.mousephenotype.org/anatomy/"+ana.getId().getAccession());
			}
			anatomyLinkString=anatomyLinkStringJoiner.toString();
		}
		return this.getGene().getSymbol()+tab+this.getAllele().getSymbol()+tab+geneAccession+tab+expression +tab+ anatomyString + tab+ anatomyLinkString +tab+ this.getZygosity().getShortName()+tab+this.getSexes()+tab
				+ this.getParameter().getName() + tab+ this.getPhenotypingCenter()+tab+numberOfImages+tab+ imageUrl;
	}
	
	
	public static String getTabbedHeader(){
    	return "Gene Symbol\tMGI Accession\tMGI Allele\tExpression\tAnatomy\tAnatomy URLs\tZygosity\tSex\tProcedure Name\tPhenotyping Center\tNumber of Images\tImage Link";
    }
	
}
