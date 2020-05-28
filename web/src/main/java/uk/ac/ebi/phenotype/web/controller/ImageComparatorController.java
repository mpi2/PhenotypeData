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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.Expression;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.AnatomyService;
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@Controller
public class ImageComparatorController {

	@NotNull @Autowired
	private ImageService imageService;

	@NotNull @Autowired
	private ExpressionService expressionService;

	@NotNull @Autowired
	private GeneService geneService;

	@NotNull @Autowired
	private AnatomyService anatomyService;

	
	@RequestMapping("/imageComparator")
	public String imageCompBrowser( @RequestParam(value = "acc", required=false)  String acc,
				@RequestParam(value = "parameter_stable_id", required=false)  String parameterStableId,
				@RequestParam(value = "parameter_association_value", required=false)  String parameterAssociationValue,
				@RequestParam(value = "anatomy_id", required=false)  String anatomyId,
				@RequestParam(value = "anatomy_term", required=false)  String anatomyTerm,
				@RequestParam(value = "gender", required=false) String gender,
				@RequestParam(value = "zygosity", required=false) String zygosity,
				@RequestParam(value="mediaType", required=false) String mediaType,
				@RequestParam(value="colony_id", required=false) String colonyId,
				@RequestParam(value="mp_id", required=false) String mpId,
				@RequestParam(value="force_frames", required=false, defaultValue="false") Boolean forceFrames,
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
		
		
		int numberOfControlsPerSex = 50;
		
		// int daysEitherSide = 30;// get a month either side
				SexType sexType=null;
				if(gender!=null && !gender.equals("all")){
					sexType = imageService.getSexTypesForFilter(gender);
				}
				
		if(mediaType!=null) System.out.println("mediaType= "+mediaType);
		// get experimental images
		// we will also want to call the getControls method and display side by
		// side
		if(StringUtils.isEmpty(anatomyId) && !StringUtils.isEmpty(anatomyTerm) && !anatomyTerm.contains("Unassigned")){
			try {
				AnatomyDTO anatomyDto = anatomyService.getTermByName(anatomyTerm);
				anatomyId=anatomyDto.getAnatomyId();
			} catch (Exception e) {
				//if anatomy id is not found it will be set to null and carry on.
				e.printStackTrace();
			}
		}
		
		if (parameterAssociationValue != null) {
			if (parameterAssociationValue.equalsIgnoreCase("all")) {

				parameterAssociationValue = null;
			}
		}
		
		System.out.println("acc="+acc+", parameterStableId="+ parameterStableId+", parameterAssociationValue"+ parameterAssociationValue+", anatomyId="+ anatomyId+", zygosity="+
				zygosity+", colonyId="+ colonyId+", mpId="+ mpId+", sex="+ sexType);
		List<ImageDTO> filteredMutants = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId,
				zygosity, colonyId, mpId, sexType);
		
		ImageDTO imgDoc =null;
		if (!filteredMutants.isEmpty()) {
			imgDoc = filteredMutants.get(0);
		}
		//this filters controls by the sex and things like procedure and phenotyping center - based on first image - this may not be a good idea - there maybe multiple phenotyping centers for a procedure which woudln't show???
		
				List<ImageDTO> controls=null;
								controls = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sexType, parameterStableId, anatomyId, parameterAssociationValue);
				
		

		this.addGeneToPage(acc, model);
		model.addAttribute("mediaType", mediaType);
		model.addAttribute("sexTypes",SexType.values());
		model.addAttribute("zygTypes",ZygosityType.values());
		model.addAttribute("mutants", filteredMutants);
		model.addAttribute("expression", Expression.values());
		//System.out.println("controls size=" + controls.size());
		model.addAttribute("controls", controls);
		
		boolean federated=this.isFederated(filteredMutants);
		boolean zStacked=this.isZStacked(filteredMutants);//for 3i stacked images we need the frames view to display the stacks - unless we can get the js viewports to display them easily
		if(forceFrames) {//flag to override anything else and show frames view - for debugging mainly or possibly to show z-stacks
			return "comparatorFrames";
		}
		if(mediaType!=null && mediaType.equals("pdf") || federated || zStacked){//we need iframes to load google pdf viewer so switch to this view for the pdfs or to work with JAX federated omero housed at JAX
			System.out.println("using frames based comparator");
			return "comparatorFrames";
		}
		return "comparator";//js viewport used to view images in this view.
	}

	
	
	/**
	 * just for testing for jax to see what area of their images are being shown
	 * @param model
	 * @param request
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@RequestMapping("/imageComparatorUrls")
	public String imageComparatorUrls(
				Model model, HttpServletRequest request)
			throws SolrServerException, IOException {
		String url1="";
		String url2="";
		
		System.out.println("query="+request.getQueryString());
		String[] urlList = request.getQueryString().split("url=");
		System.out.println(urlList);
		if(urlList.length>0){
			url1=urlList[1];
		}
		if(urlList.length>2){
			url2=urlList[2];
		}
		System.out.println("calling image imageComparatorUrls with "+url1+" url2= "+url2);
		if(url1.contains("omeroweb.jax.org")){
		model.addAttribute("url1", url1);
		}
		if(url2.contains("omeroweb.jax.org")){
		model.addAttribute("url2", url2);
		}
		return "comparatorUrls";//js viewport used to view images in this view.
	}
	
	private boolean isFederated(List<ImageDTO> filteredMutants) {
		for(ImageDTO image:filteredMutants){
			if(image.getImageLink()!=null && (image.getImageLink().contains("omero") || image.getImageLink().contains("NDPServe"))){//at the moment only use federated approach on omero served images - bad assumption here unwritten rule
				return true;
				
			}
		}
		return false;
	}
	
	private boolean isZStacked(List<ImageDTO> filteredMutants) {
		for(ImageDTO image:filteredMutants){
			if(image.getParameterStableId().equals("MGP_EEI_114_001")){//at the moment only use federated approach on omero served images - bad assumption here unwritten rule
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

	private void addGeneToPage(String acc, Model model)
			throws SolrServerException, IOException {
		GeneDTO gene = geneService.getGeneById(acc,GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL);//added for breadcrumb so people can go back to the gene page
		model.addAttribute("gene",gene);
	}
}