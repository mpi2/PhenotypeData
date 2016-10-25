package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * @author ilinca
 * @since 2016/01/?
 */
@Controller
public class EmbryoController {
	
	@Autowired
	private ObservationService os;

	@Autowired
	private GeneService gs;
	
	@RequestMapping(value = "/embryo", method = RequestMethod.GET)
	public String loadPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
	throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

		return "redirect:/landing/embryo";
	}
	
	@RequestMapping(value = "/embryo/vignettes", method = RequestMethod.GET)
	public String loadVignettes(Model model, HttpServletRequest request, RedirectAttributes attributes)
	throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

		
		return "embryoVignettes";
	}

	
}
