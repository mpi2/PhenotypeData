package org.mousephenotype.cda.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@Scope("prototype")
//@EnableSolrRepositories(basePackages = { "org.mousephenotype.cda.repositories.solr.image" }, multicoreSupport=true)
public class AnatomyController {

    //@Autowired
    //ImageService imageService;
    
    @RequestMapping("/anatomy")
   public String anatomy(Map<String, Object> model) {

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
       
        try {
            //list = imageService.findByMaId(anatomy_id);
            //System.out.println("images size=" + list.size());
            model.put("images", new ArrayList<>());
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "anatomy";
   
    }
}