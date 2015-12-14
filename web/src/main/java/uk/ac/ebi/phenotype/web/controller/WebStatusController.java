package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.dao.GwasDAO;
import org.mousephenotype.cda.solr.generic.util.PhenotypeCallSummarySolr;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.PreQcService;
import org.mousephenotype.cda.solr.service.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;
import uk.ac.ebi.phenotype.service.UniprotService;

/**
 * Class to handle the nagios web status monitoring pages
 * 
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

	List<WebStatus> webStatusObjects;

	@PostConstruct
	public void initialise() {
		System.out.println("calling webStatus initialisation method");
		webStatusObjects = new ArrayList<>();
		webStatusObjects.add(observationService);
		webStatusObjects.add(preqcService);
		webStatusObjects.add(postqcService);
		//webStatusObjects.add(uniprotService);
		webStatusObjects.add(imageService);
		
		webStatusObjects.add(imageService);
	}

	@RequestMapping("/webstatus")
	public String webStatus(Model model) throws SolrServerException {
		System.out.println("calling webstatus controller");
		List<WebStatusModel> webStatusModels=new ArrayList<>();
		for (WebStatus status : webStatusObjects) {
			String name=status.getServiceName();
			long number = status.getWebStatus();
			WebStatusModel wModel=new WebStatusModel(name, number);
			webStatusModels.add(wModel);
		}

		model.addAttribute("webStatusModels", webStatusModels);
		return "webStatus";
	}
	
	public class WebStatusModel{

		public String name;
		public long number;
		
		 public WebStatusModel(String name, long number){
			 this.name=name;
			 this.number=number;
		 }
		 
		 public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public long getNumber() {
				return number;
			}

			public void setNumber(long number) {
				this.number = number;
			}
		 
	}

}
