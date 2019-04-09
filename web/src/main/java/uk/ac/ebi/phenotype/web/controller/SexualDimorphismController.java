package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by ilinca on 13/09/2016.
 */
 @Controller
public class SexualDimorphismController {

    @RequestMapping("/sexual-dimorphism")
    public String allImages(  HttpServletRequest request, Model model, RedirectAttributes attributes)
    throws SolrServerException, IOException {

        //model.addAttribute("manuscriptLink", ));
        return "landing_sexual_dimorphism";
    }

}
