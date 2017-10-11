package uk.ac.ebi.phenotype.threeI.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ThreeIHomeController {

	
	@RequestMapping(value = "/threeI/*", method = RequestMethod.GET)
	public String loadMaPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
			throws SolrServerException, IOException, URISyntaxException, JSONException {
		System.out.println("hello world called");
		return "home";
	}
}
