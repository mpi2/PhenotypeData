package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.mousephenotype.cda.db.dao.OntologyTermDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrJ;
import org.mousephenotype.cda.solr.service.Allele2Service;
import org.mousephenotype.cda.solr.service.AlleleService;
import org.mousephenotype.cda.solr.service.AutoSuggestService;
import org.mousephenotype.cda.solr.service.DiseaseService;
import org.mousephenotype.cda.solr.service.EucommCreProductService;
import org.mousephenotype.cda.solr.service.EucommToolsCreAllele2Service;
import org.mousephenotype.cda.solr.service.EucommToolsProductService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.AnatomyService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.OmeroStatusService;
import org.mousephenotype.cda.solr.service.PhenodigmService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.PreQcService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;

/**
 * Class to handle the nagios web status monitoring pages
 *
 * @author jwarren
 *
 */
@Controller
public class WebStatusController {

	@Autowired
	ObservationService observationService;

	@Autowired
	private PostQcService postqcService;

	@Autowired
	private StatisticalResultService srService;

	@Autowired
	private PreQcService preqcService;

	@Autowired
	private AlleleService alleleService;

	@Autowired
	private ImagesSolrJ sangerImages;

	@Autowired
	ImageService impcImageService;

	@Autowired
	MpService mpService;

	@Autowired
	AnatomyService anatomyService;

	@Autowired
	ImpressService pipelineService;

	@Autowired
	private GeneService geneService;

	@Autowired
	DiseaseService diseaseService;

	@Autowired
	AutoSuggestService autoSuggestService;

	@Autowired
	private PhenotypePipelineDAO ppDAO;

	@Autowired
	PhenodigmService phenodigmService;

	@Autowired
	OmeroStatusService omeroStatusService;

	List<WebStatus> webStatusObjects;

	// imits solr services

	@Autowired
	Allele2Service allele2;

	@Autowired
	EucommCreProductService eucommCreProductService;

	@Autowired
	EucommToolsProductService eucommToolsProductService;

	@Autowired
	EucommToolsCreAllele2Service eucommToolsCreAllele2Service;

	List<WebStatus> imitsWebStatusObjects;

	@PostConstruct
	public void initialise() {

		// cores we need to test are at least this set:
		// experiment,genotype-phenotype,statistical-result,preqc,allele,images,impc_images,mp,ma,pipeline,gene,disease,autosuggest
		// System.out.println("calling webStatus initialisation method");
		webStatusObjects = new ArrayList<>();
		webStatusObjects.add(observationService);
		webStatusObjects.add(postqcService);
		webStatusObjects.add(srService);
		webStatusObjects.add(preqcService);
		webStatusObjects.add(alleleService);
		webStatusObjects.add(sangerImages);
		webStatusObjects.add(impcImageService);
		webStatusObjects.add(mpService);
		webStatusObjects.add(anatomyService);
		webStatusObjects.add(pipelineService);
		webStatusObjects.add(geneService);
		webStatusObjects.add(diseaseService);
		webStatusObjects.add(autoSuggestService);
		webStatusObjects.add(ppDAO);
		webStatusObjects.add(phenodigmService);
		//webStatusObjects.add(omeroStatusService);//taken out the omero test as takes it from 100ms times to 1 second!

		imitsWebStatusObjects = new ArrayList<>();
		imitsWebStatusObjects.add(allele2);
		imitsWebStatusObjects.add(eucommCreProductService);
		imitsWebStatusObjects.add(eucommToolsProductService);
		imitsWebStatusObjects.add(eucommToolsCreAllele2Service);
	}

	@RequestMapping("/webstatus")
	public String webStatus(Model model, HttpServletResponse response) {
		boolean ok = true;

		// check our core solr instances are returning via the services
		List<WebStatusModel> webStatusModels = new ArrayList<>();
		for (WebStatus status : webStatusObjects) {
			String name = status.getServiceName();
			long number = 0;
			try {
				number = status.getWebStatus();
			} catch (Exception e) {
				ok = false;
				e.printStackTrace();
			}
			WebStatusModel wModel = new WebStatusModel(name, number);
			webStatusModels.add(wModel);
		}

		model.addAttribute("webStatusModels", webStatusModels);

		// check the imits services
		List<WebStatusModel> imitsWebStatusModels = new ArrayList<>();
		for (WebStatus status : imitsWebStatusObjects) {
			String name = status.getServiceName();
			long number = 0;
			try {
				number = status.getWebStatus();
			} catch (Exception e) {

				// Do not change the status for unavailable non-critical resource

				e.printStackTrace();
			}
			WebStatusModel wModel = new WebStatusModel(name, number);
			imitsWebStatusModels.add(wModel);
		}

		model.addAttribute("imitsWebStatusModels", imitsWebStatusModels);
		if (ok) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		model.addAttribute("ok", ok);
		return "webStatus";
	}

	public class WebStatusModel {

		public String name;
		public long number;

		public WebStatusModel(String name, long number) {
			this.name = name;
			this.number = number;
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
