package org.mousephenotype.cda.web.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.repositories.solr.image.Image;
import org.mousephenotype.cda.repositories.solr.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Component
@Scope("prototype")
@EnableAutoConfiguration
@EnableSolrRepositories(basePackages = { "org.mousephenotype.cda.repositories.solr.image" }, multicoreSupport=true)
public class AnatomyController {

    @Autowired
    ImageService imageService;
    
    @RequestMapping(value = "/anatomy/{anatomy_id}", method = RequestMethod.GET)
   public String loadMaPage(@PathVariable String anatomy_id, Model model, HttpServletRequest request)
	 {
	
		// http://www.informatics.jax.org/searches/AMA.cgi?id=MA:0002950
		// right eye
//		Anatomy ma = JSONMAUtils.getMA(anatomy_id, config);
//
//		//get expression only images
//		JSONObject maAssociatedExpressionImagesResponse = JSONImageUtils.getAnatomyAssociatedExpressionImages(anatomy_id, config, numberOfImagesToDisplay);
//		JSONArray expressionImageDocs = maAssociatedExpressionImagesResponse.getJSONObject("response").getJSONArray("docs");
//		List<AnatomyPageTableRow> anatomyTable = is.getImagesForMA(anatomy_id, null, null, null, null);
//               
//		model.addAttribute("anatomy", ma);
//		model.addAttribute("expressionImages", expressionImageDocs);
//		model.addAttribute("anatomyTable", anatomyTable);
//        model.addAttribute("phenoFacets", getFacets(anatomy_id));
        System.out.println("loading ma page with id="+anatomy_id);
        List<Image>list=null;
             try {
                 
             
        list=imageService.findByMaId(anatomy_id);
         System.out.println("images size="+list.size());
                model.addAttribute("images", list);
        } catch (Exception e) {
            e.printStackTrace();
             }
       
		return "anatomy";
	}

  
}