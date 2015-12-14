package uk.ac.ebi.phenotype.web.controller;

import org.mousephenotype.cda.db.dao.GwasDAO;
import org.mousephenotype.cda.solr.generic.util.PhenotypeCallSummarySolr;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.PreQcService;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.ebi.phenotype.generic.util.SolrIndex2;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;
import uk.ac.ebi.phenotype.service.UniprotService;

/**
 * Class to handle the nagios web status monitoring pages
 * @author jwarren
 *
 */
@Controller
public class WebStatusController {

	@Autowired
	private PhenotypeSummaryDAO phenSummary;

	@Autowired
	private ImagesSolrDao imagesSolrDao;

	@Autowired
	private PhenotypeCallSummarySolr phenoDAO;

	@Autowired
	private GwasDAO gwasDao;

	@Autowired
	ObservationService observationService;

	@Autowired
	ImageService imageService;

	@Autowired
	ExpressionService expressionService;

	@Autowired
	private GeneService geneService;

	@Autowired
	private PreQcService preqcService;

	@Autowired
	private PostQcService postqcService;

	@Autowired
	private UniprotService uniprotService;
	
	@RequestMapping("/webstatus")
	public String webStatus(){
		System.out.println("calling webstatus controller");
		
		
		
		return "webStatus";
	}
	
}
