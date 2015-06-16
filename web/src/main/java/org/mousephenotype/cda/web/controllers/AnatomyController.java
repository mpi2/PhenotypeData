package org.mousephenotype.cda.web.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@EnableAutoConfiguration
public class AnatomyController {

    @Autowired
    ImageService is;
    
    @RequestMapping(value = "/anatomy/{anatomy_id}", method = RequestMethod.GET)
    @ResponseBody
   public String loadMaPage(@PathVariable String anatomy_id, Model model, HttpServletRequest request, RedirectAttributes attributes)
	throws SolrServerException, IOException, URISyntaxException {
	
		// http://www.informatics.jax.org/searches/AMA.cgi?id=MA:0002950
		// right eye
		Anatomy ma = JSONMAUtils.getMA(anatomy_id, config);

		//get expression only images
		JSONObject maAssociatedExpressionImagesResponse = JSONImageUtils.getAnatomyAssociatedExpressionImages(anatomy_id, config, numberOfImagesToDisplay);
		JSONArray expressionImageDocs = maAssociatedExpressionImagesResponse.getJSONObject("response").getJSONArray("docs");
		List<AnatomyPageTableRow> anatomyTable = is.getImagesForMA(anatomy_id, null, null, null, null);
               
		model.addAttribute("anatomy", ma);
		model.addAttribute("expressionImages", expressionImageDocs);
		model.addAttribute("anatomyTable", anatomyTable);
        model.addAttribute("phenoFacets", getFacets(anatomy_id));
		return "anatomy";
	}

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AnatomyController.class, args);
    }
}