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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.generic.util.JSONImageUtils;
import org.mousephenotype.cda.solr.service.AnatomyService;
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.OntologyBean;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.web.dto.AnatomyPageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class AnatomyController {

	private final Logger log = LoggerFactory.getLogger(AnatomyController.class);

	@Autowired
	ImageService is;

	@Autowired
	AnatomyService anatomyService;
	
	@Autowired
	ExpressionService expressionService;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;


	private static final int numberOfImagesToDisplay=5;



	/**
	 * Phenotype controller loads information required for displaying the
	 * phenotype page or, in the case of an error, redirects to the error page
	 *
	 * @param anatomy_id
	 *            the Mammalian anatomy id of the tissue to display
	 * @return the name of the view to render, or redirect to search page on
	 *         error
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws SolrServerException
	 *
	 */
	@RequestMapping(value = "/anatomy/{anatomy}", method = RequestMethod.GET)
	public String loadMaPage(@PathVariable String anatomy, Model model, HttpServletRequest request, RedirectAttributes attributes)
	throws SolrServerException, IOException, URISyntaxException {

		AnatomyDTO anatomyTerm = anatomyService.getTerm(anatomy);

		//get expression only images
		JSONObject maAssociatedExpressionImagesResponse = JSONImageUtils.getAnatomyAssociatedExpressionImages(anatomy, config, numberOfImagesToDisplay);
		JSONArray expressionImageDocs = maAssociatedExpressionImagesResponse.getJSONObject("response").getJSONArray("docs");
		List<AnatomyPageTableRow> anatomyTable = is.getImagesForAnatomy(anatomy, null, null, null, null, request.getAttribute("baseUrl").toString());
		List<AnatomyPageTableRow> anatomyExpTable=expressionService.getExpressionForGenesOnAnatomyPage(anatomy);
		model.addAttribute("anatomy", anatomyTerm);
		model.addAttribute("expressionImages", expressionImageDocs);
		model.addAttribute("anatomyTable", anatomyTable);
        model.addAttribute("phenoFacets", getFacets(anatomy));

        // Stuff for parent-child display
        model.addAttribute("hasChildren", (anatomyTerm.getChildAnatomyId() != null && anatomyTerm.getChildAnatomyId().size() > 0) ? true : false);
        model.addAttribute("hasParents", (anatomyTerm.getParentAnatomyId() != null && anatomyTerm.getParentAnatomyId().size() > 0) ? true : false);

        return "anatomy";

	}

	 /**
     * @author ilinca
     * @since 2016/05/03
     * @param anatomyId
     * @param type
     * @param model
     * @return
     * @throws SolrServerException
     * @throws IOException
     * @throws URISyntaxException
     */
    @RequestMapping(value="/anatomyTree/json/{anatomyId}", method=RequestMethod.GET)
    public @ResponseBody String getParentChildren( @PathVariable String anatomyId, @RequestParam(value = "type", required = true) String type, Model model)
    throws SolrServerException, IOException, URISyntaxException {

    	if (type.equals("parents")){

	    	JSONObject data = new JSONObject();
	    	data.element("id", anatomyId);
	    	JSONArray nodes = new JSONArray();

	    	for (OntologyBean term : anatomyService.getParents(anatomyId)){
	    		nodes.add(term.toJson());
	    	}

	    	data.element("children", nodes);
			return data.toString();

    	} else if (type.equals("children")){

    		JSONObject data = new JSONObject();
        	data.element("id", anatomyId);
        	JSONArray nodes = new JSONArray();

        	for (OntologyBean term : anatomyService.getChildren(anatomyId)){
	    		nodes.add(term.toJson());
	    	}

        	data.element("children", nodes);
    		return data.toString();
    	}
    	return "";
    }


    @RequestMapping(value = "/anatomyFrag/{anatomyId}", method = RequestMethod.GET)
	public String loadAnatomyTable(	@PathVariable String anatomyId,
								@RequestParam(required = false, value = "anatomy_term") List<String> anatomyTerms,
								@RequestParam(required = false, value = "parameter_association_value") List<String> parameterAssociationValue,
								@RequestParam(required = false, value = "phenotyping_center") List<String> phenotypingCenter,
								@RequestParam(required = false, value = "procedure_name") List<String> procedureName,
								Model model,
								HttpServletRequest request,
								RedirectAttributes attributes)
	throws SolrServerException, IOException, URISyntaxException {
//this method doesn't get used anywhere???
		List<AnatomyPageTableRow> anatomyTable = is.getImagesForAnatomy(anatomyId, anatomyTerms, phenotypingCenter, procedureName, parameterAssociationValue, request.getAttribute("baseUrl").toString());
		//List<AnatomyPageTableRow> anatomyExpTable=expressionService.getExpressionForGenesOnAnatomyPage(anatomyId);
		
		model.addAttribute("anatomyTable", anatomyTable);

		return "anatomyFrag";
	}

    private Map<String, Map<String, Long>> getFacets (String anatomyId){

    	Map<String, Map<String, Long>> phenoFacets = new HashMap<>();
    	Map<String, Map<String, Long>> temp = new HashMap<>();
		try {
			temp = is.getFacets(anatomyId);
			phenoFacets.put(ImageDTO.ANATOMY_TERM, temp.get(ImageDTO.ANATOMY_TERM));
			phenoFacets.put(ImageDTO.PARAMETER_ASSOCIATION_VALUE, temp.get(ImageDTO.PARAMETER_ASSOCIATION_VALUE));
			phenoFacets.put(ImageDTO.PHENOTYPING_CENTER, temp.get(ImageDTO.PHENOTYPING_CENTER));
			phenoFacets.put(ImageDTO.PROCEDURE_NAME, temp.get(ImageDTO.PROCEDURE_NAME));
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
        return phenoFacets;
    }


	@ExceptionHandler(Exception.class)
	public ModelAndView handleGenericException(Exception exception) {
		exception.printStackTrace();
		ModelAndView mv = new ModelAndView("identifierError");
		mv.addObject("errorMessage", exception.getMessage());
		mv.addObject("acc", "This");
		mv.addObject("type", "mouse anatomy");
		mv.addObject("exampleURI", "/anatomy/MA:0002950");
		return mv;
	}

}
