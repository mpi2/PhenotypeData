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

import org.apache.solr.client.solrj.SolrServerException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Controller
public class ImageComparatorController {

	@Autowired
	ImageService imageService;

	@Autowired
	ExpressionService expressionService;

	@Autowired
	GeneService geneService;

	
	@RequestMapping("/imageComparator")
	public String imageCompBrowser( @RequestParam(value = "acc")  String acc,
				@RequestParam(value = "parameter_stable_id", required=false)  String parameterStableId,
				@RequestParam(value = "parameter_association_value", required=false)  String parameterAssociationValue,
				@RequestParam(value = "anatomy_id", required=false)  String anatomyId,
				@RequestParam(value = "gender", required=false) String gender,
				@RequestParam(value = "zygosity", required=false) String zygosity,
				@RequestParam(value="mediaType", required=false) String mediaType,
				@RequestParam(value="colony_id", required=false) String colonyId,
				@RequestParam(value="mp_id", required=false) String mpId,
				Model model, HttpServletRequest request)
			throws SolrServerException, IOException {
		System.out.println("calling image imageComparator");
		
		// good example url with control and experimental images
		// http://localhost:8080/phenotype-archive/imagePicker/MGI:2669829/IMPC_EYE_050_001
		//changed to http://localhost:8080/phenotype-archive/imageComparator?acc=MGI:2669829&parameter_stable_id=IMPC_EYE_050_001
		//in anatomy pages we have links like this that need to be supported
		//http://localhost:8080/phenotype-archive/impcImages/images?q=*:*&defType=edismax&wt=json&fq=(anatomy_id:%22EMAPA:16105%22%20OR%20selected_top_level_anatomy_id:%22EMAPA:16105%22%20OR%20intermediate_anatomy_id:%22EMAPA:16105%22)%20%20AND%20gene_symbol:Ap4e1%20AND%20parameter_name:%22LacZ%20images%20wholemount%22%20AND%20parameter_association_value:%22ambiguous%22&title=gene%20Ap4e1%20with%20ambiguous%20in%20heart
		//http://localhost:8080/phenotype-archive/imageCompara?anatomy_id:%22EMAPA:16105%22&gene_symbol:Ap4e1&parameter_name:%22LacZ%20images%20wholemount%22&parameter_association_value:%22ambiguous%22
		//example link from gene page phenotype table that should work through this method with mpId and colonyId
		//http://localhost:8080/phenotype-archive/imageComparator?acc=MGI:1913955&mpId=MP:0000572&colony_id=RIKEN_EPD0296_2_A10
		
		if(mediaType!=null) System.out.println("mediaType= "+mediaType);
		// get experimental images
		// we will also want to call the getControls method and display side by
		// side
		List<ImageDTO> mutants=new ArrayList<>();
		QueryResponse responseExperimental = imageService
				.getImagesForGeneByParameter(acc, parameterStableId,"experimental", Integer.MAX_VALUE, 
						null, null, null, anatomyId, parameterAssociationValue, mpId, colonyId);
		ImageDTO imgDoc =null;
		if (responseExperimental != null && responseExperimental.getResults().size()>0) {
			//mutants=responseExperimental.getResults();
			mutants=responseExperimental.getBeans(ImageDTO.class);
			
			imgDoc = mutants.get(0);
		}
		
		int numberOfControlsPerSex = 5;
		// int daysEitherSide = 30;// get a month either side
		SexType sexType=null;
		if(gender!=null && !gender.equals("all")){
			sexType = getSexTypesForFilter(gender);
		}
		
		
		//this filters controls by the sex and things like procedure and phenotyping center - based on first image - this may not be a good idea - there maybe multiple phenotyping centers for a procedure which woudln't show???
		List<ImageDTO> controls=null;
		if(imgDoc!=null){
		controls = filterControlsBySexAndOthers(imgDoc, numberOfControlsPerSex, sexType);
		}
		List<ImageDTO> filteredMutants = filterMutantsBySex(mutants, imgDoc, sexType);
		
		List<ZygosityType> zygosityTypes=null;
		if(zygosity!=null && !zygosity.equals("all")){
			zygosityTypes=getZygosityTypesForFilter(zygosity);
			//only filter mutants by zygosity as all controls are homs.
			filteredMutants=filterImagesByZygosity(filteredMutants, zygosityTypes);
		}
		
		boolean federated=this.isFederated(filteredMutants);
		

		
		

		this.addGeneToPage(acc, model);
		model.addAttribute("mediaType", mediaType);
		model.addAttribute("sexTypes",SexType.values());
		model.addAttribute("zygTypes",ZygosityType.values());
		model.addAttribute("mutants", filteredMutants);
		//System.out.println("controls size=" + controls.size());
		model.addAttribute("controls", controls);
		if(mediaType!=null && mediaType.equals("pdf") || federated){//we need iframes to load google pdf viewer so switch to this view for the pdfs.
			System.out.println("using frames based comparator to pdfs");
			return "comparatorFrames";
		}
		return "comparator";//js viewport used to view images in this view.
	}
	
	private boolean isFederated(List<ImageDTO> filteredMutants) {
		for(ImageDTO image:filteredMutants){
			if(image.getImageLink()!=null && image.getImageLink().contains("omero") ){//at the moment only use federated approach on omero served images - bad assumption here unwritten rule
				return true;
				
			}
		}
		return false;
	}

	@RequestMapping("/overlap")
	public String overlap(@RequestParam String acc,@RequestParam String id1, @RequestParam String id2, Model model) throws SolrServerException, IOException{
		
		this.addGeneToPage(acc, model);
		return "overlap";
	}
	
	@RequestMapping("/imageComparatorTest")
	public String imageCompBrowser(){
		return "comparatorBasicTest";
		
	}
	
	
	private List<ImageDTO> filterImagesByZygosity(List<ImageDTO> filteredMutants, List<ZygosityType> zygosityTypes) {
		List<ImageDTO> filteredImages=new ArrayList<>();
		if(zygosityTypes==null || (zygosityTypes.get(0).getName().equals("not_applicable"))){//just return don't filter if not applicable default is found
			return filteredMutants;
		}
		for(ZygosityType zygosityType:zygosityTypes){
			for(ImageDTO image:filteredMutants){
				if(image.getZygosity().equals(zygosityType.getName())){
					filteredImages.add(image);
				}
			}
		}
		return filteredImages;
	}

	private List filterMutantsBySex(List<ImageDTO> mutants, ImageDTO imgDoc, SexType sexType) {
		if(sexType==null){
			return mutants;
		}
		
		List<ImageDTO> filteredMutants = new ArrayList<>();
		
		if (imgDoc != null) {
			
				for(ImageDTO mutant:mutants){
					if(mutant.getSex().equals(sexType.getName())){
						filteredMutants.add(mutant);
					}
				}
				
			
		}
		return filteredMutants;
	}

	private List<ImageDTO> filterControlsBySexAndOthers(ImageDTO imgDoc, int numberOfControlsPerSex,
			SexType sex) throws SolrServerException, IOException {
		if(sex==null){
			return imageService.getControls(numberOfControlsPerSex, null, imgDoc, null);
		}
		List<ImageDTO> controls = new ArrayList<>();
		Set<ImageDTO> uniqueControls=new HashSet<>();
		if (imgDoc != null) {
			
				List<ImageDTO> controlsTemp = imageService.getControls(numberOfControlsPerSex, sex, imgDoc, null);
				uniqueControls.addAll(controlsTemp);
			
		}
		
		
			controls.addAll(uniqueControls);//add a unique set only so we don't have duplicate omero ids!!!
		
		return controls;
	}

	private SexType getSexTypesForFilter(String gender) {
		SexType chosenSex=null;
		for(SexType sex:SexType.values()){
			if(sex.name().equals(gender)){
				chosenSex=sex;
				return sex;
			}
		}
		return chosenSex;
	}
	
	private List<ZygosityType> getZygosityTypesForFilter(String zygosity) {
		List<ZygosityType> zygosityTypes=new ArrayList<>();
		if(zygosity.equals("homozygote")){
			zygosityTypes.add(ZygosityType.homozygote);
		}else
		if(zygosity.equals("heterozygote")){
			zygosityTypes.add(ZygosityType.heterozygote);
		}else if(zygosity.equals("hemizygote")){
				zygosityTypes.add(ZygosityType.hemizygote);
		}else
		if(zygosity.equals("not_applicable") || zygosity.equals("notapplicable")){
			zygosityTypes.addAll(Arrays.asList(ZygosityType.values()));
			
		}else
		if(zygosity.equals("all")){
			zygosityTypes.addAll(Arrays.asList(ZygosityType.values()));
			
		}
		return zygosityTypes;
	}

	private void addGeneToPage(String acc, Model model)
			throws SolrServerException, IOException {
		GeneDTO gene = geneService.getGeneById(acc,GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL);//added for breadcrumb so people can go back to the gene page
		model.addAttribute("gene",gene);
	}

	
}