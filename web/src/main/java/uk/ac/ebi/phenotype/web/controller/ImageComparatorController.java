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
package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import Glacier2.CannotCreateSessionException;
//import Glacier2.PermissionDeniedException;

@Controller
public class ImageComparatorController {

	@Autowired
	ImageService imageService;

	@Autowired
	ExpressionService expressionService;

	@Autowired
	GeneService geneService;

	
	@RequestMapping("/imageComparator/{acc}/{parameter_stable_id}")
	public String imageCompBrowser(@PathVariable String acc,
			@PathVariable String parameter_stable_id,
			@RequestParam(value = "gender", required=false) String gender, @RequestParam(value = "zygosity", defaultValue="not_applicable") String zygosity, @RequestParam(value="mediaType", required=false) String mediaType, Model model, HttpServletRequest request)
			throws SolrServerException {
		System.out.println("calling image imageComparator");
		if(gender!=null)System.out.println("sex in controller="+gender);
		if(zygosity!=null)System.out.println("zygParam="+zygosity);
		
		// good example url with control and experimental images
		// http://localhost:8080/phenotype-archive/imagePicker/MGI:2669829/IMPC_EYE_050_001
		
		if(mediaType!=null) System.out.println("mediaType= "+mediaType);
		// get experimental images
		// we will also want to call the getControls method and display side by
		// side
		SolrDocumentList mutants = new SolrDocumentList();
		QueryResponse responseExperimental = imageService
				.getImagesForGeneByParameter(acc, parameter_stable_id,
						"experimental", 10000, null, null, null);
		SolrDocument imgDoc =null;
		if (responseExperimental != null && responseExperimental.getResults().size()>0) {
			mutants=responseExperimental.getResults();
			System.out.println("list size=" + mutants.size());
			imgDoc = mutants.get(0);
		}
		
		int numberOfControlsPerSex = 5;
		// int daysEitherSide = 30;// get a month either side
		List<SexType> sexTypes=null;
		if(gender!=null){
			sexTypes = getSexTypesForFilter(gender);
		}
		
		
		//this filters controls by the sex and things like procedure and phenotyping center - based on first image - this may not be a good idea - there maybe multiple phenotyping centers for a procedure which woudln't show???
		SolrDocumentList controls = filterControlsBySexAndOthers(imgDoc, numberOfControlsPerSex, sexTypes);
		SolrDocumentList filteredMutants = filterMutantsBySex(mutants, imgDoc, sexTypes);
		
		List<ZygosityType> zygosityTypes=getZygosityTypesForFilter(zygosity);
		
		//only filter mutants by zygosity as all controls are homs.
		filteredMutants=filterImagesByZygosity(filteredMutants, zygosityTypes);
		
		

		this.addGeneToPage(acc, model);
		model.addAttribute("mediaType", mediaType);
		System.out.println("mutants size=" + filteredMutants.size());
		model.addAttribute("mutants", filteredMutants);
		System.out.println("controls size=" + controls.size());
		model.addAttribute("controls", controls);
		if(mediaType!=null && mediaType.equals("pdf")){//we need iframes to load google pdf viewer so switch to this view for the pdfs.
			return "comparatorFrames";
		}
		return "comparator";//js viewport used to view images in this view.
	}
	
	@RequestMapping("/imageComparatorTest")
	public String imageCompBrowser(){
		return "comparatorBasicTest";
		
	}
	
	
	private SolrDocumentList filterImagesByZygosity(SolrDocumentList imageDocs, List<ZygosityType> zygosityTypes) {
		SolrDocumentList filteredImages=new SolrDocumentList();
		if(zygosityTypes.get(0).getName().equals("not_applicable")){//just return don't filter if not applicable default is found
			return imageDocs;
		}
		for(ZygosityType zygosityType:zygosityTypes){
			for(SolrDocument control:imageDocs){
				if(control.get(ImageDTO.ZYGOSITY).equals(zygosityType.getName())){
					filteredImages.add(control);
				}
			}
		}
		return filteredImages;
	}

	private SolrDocumentList filterMutantsBySex(SolrDocumentList mutants, SolrDocument imgDoc, List<SexType> sexTypes) {
		if(sexTypes==null || sexTypes.isEmpty()){
			return mutants;
		}
		
		SolrDocumentList filteredMutants = new SolrDocumentList();
		
		if (imgDoc != null) {
			for (SexType sex : sexTypes) {
				for(SolrDocument mutant:mutants){
					if(mutant.get("sex").equals(sex.getName())){
						filteredMutants.add(mutant);
					}
				}
				
			}
		}
		return filteredMutants;
	}

	private SolrDocumentList filterControlsBySexAndOthers(SolrDocument imgDoc, int numberOfControlsPerSex,
			List<SexType> sexTypes) throws SolrServerException {
		if(sexTypes==null){
			return imageService.getControls(numberOfControlsPerSex, null, imgDoc, null);
		}
		SolrDocumentList controls = new SolrDocumentList();
		Set<SolrDocument> uniqueControls=new HashSet<>();
		if (imgDoc != null) {
			for (SexType sex : sexTypes) {
				System.out.println("sex in controls="+sex);
				SolrDocumentList controlsTemp = imageService.getControls(numberOfControlsPerSex, sex, imgDoc, null);
				
				uniqueControls.addAll(controlsTemp);
			}
		}
		
		
			controls.addAll(uniqueControls);//add a unique set only so we don't have duplicate omero ids!!!
		
		return controls;
	}

	private List<SexType> getSexTypesForFilter(String gender) {
		List<SexType> sexTypes=new ArrayList<>();
		if(gender.equals("male")){
			sexTypes.add(SexType.male);
		}else
		if(gender.equals("female")){
			sexTypes.add(SexType.female);
		}else
		if(gender.equals("both")){
			sexTypes.add(SexType.male);
			sexTypes.add(SexType.female);
		}else if(gender.equals("not applicable") || gender.equals("no data")){
			return Arrays.asList(SexType.values());
		}
		return sexTypes;
	}
	
	private List<ZygosityType> getZygosityTypesForFilter(String zygosity) {
		List<ZygosityType> zygosityTypes=new ArrayList<>();
		if(zygosity.equals("homozygote")){
			zygosityTypes.add(ZygosityType.homozygote);
		}else
		if(zygosity.equals("heterozygote")){
			zygosityTypes.add(ZygosityType.heterozygote);
		}else
		if(zygosity.equals("not_applicable")){
			zygosityTypes.addAll(Arrays.asList(ZygosityType.values()));
			
		}
		return zygosityTypes;
	}

	private void addGeneToPage(String acc, Model model)
			throws SolrServerException {
		GeneDTO gene = geneService.getGeneById(acc,GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL);//added for breadcrumb so people can go back to the gene page
		System.out.println("gene in picker="+gene);
		model.addAttribute("gene",gene);
	}

	
}