package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;

@Controller
public class EmbryoController {
	 @RequestMapping(value = "/embryo", method = RequestMethod.GET)
	    public String loadMpPage( Model model, HttpServletRequest request, RedirectAttributes attributes)
	    throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {
		 	
		 	System.out.println("Call controller");
	        return "embryo";
	        
	    }
}
