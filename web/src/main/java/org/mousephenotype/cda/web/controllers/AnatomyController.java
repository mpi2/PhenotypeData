package org.mousephenotype.cda.web.controllers;

import org.mousephenotype.cda.solr.repositories.image.Image;
import org.mousephenotype.cda.solr.repositories.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
@Component
@Scope("prototype")
@EnableAutoConfiguration
@EnableSolrRepositories(basePackages = { "org.mousephenotype.cda.repositories.solr.image" }, multicoreSupport=true)
public class AnatomyController {

    @Autowired
    ImageService imageService;
    
   @RequestMapping(value = "/anatomy", method = RequestMethod.GET)
   public void anatomy(Model model, HttpServletRequest request) {

        // http://www.informatics.jax.org/searches/AMA.cgi?id=MA:0002950
        // right eye
//		Anatomy ma = JSONMAUtils.getMA(anatomy_id, config);
//
//		//get expression only images
//		JSONObject maAssociatedExpressionImagesResponse = JSONImageUtils.getAnatomyAssociatedExpressionImages(anatomy_id, config, numberOfImagesToDisplay);
//		JSONArray expressionImageDocs = maAssociatedExpressionImagesResponse.getJSONObject("response").getJSONArray("docs");
//		List<AnatomyPageTableRow> anatomyTable = imageService.getImagesForMA(anatomy_id, null, null, null, null);
//               
//		model.addAttribute("anatomy", ma);
//		model.addAttribute("expressionImages", expressionImageDocs);
//		model.addAttribute("anatomyTable", anatomyTable);
//        model.addAttribute("phenoFacets", getFacets(anatomy_id));
	   String anatomy_id="MA:0000191";
        System.out.println("loading ma page with id=" + anatomy_id);
        List<Image> list;
        try {
            list = imageService.findByMaId(anatomy_id);
            System.out.println("images size=" + list.size());
            model.addAttribute("images", list);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        
   
    }
}