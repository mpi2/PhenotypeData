package uk.ac.ebi.phenotype.web.controller;

import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrJ;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Class to handle the nagios web status monitoring pages
 *
 * @author jwarren
 *
 */
@Controller
public class WebStatusController {

	public static final Integer TIMEOUT_INTERVAL = 1;

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
	ProductService productService;

	@Autowired
	EucommToolsCreAllele2Service eucommToolsCreAllele2Service;

	List<WebStatus> nonEssentialWebStatusObjects;

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
		
		nonEssentialWebStatusObjects = new ArrayList<>();
		nonEssentialWebStatusObjects.add(omeroStatusService);//taken out the omero test as takes it from 100ms times to 1 second - put back in as render_birds_eye_view should be cached by omero!
		nonEssentialWebStatusObjects.add(allele2);
		nonEssentialWebStatusObjects.add(eucommCreProductService);
		nonEssentialWebStatusObjects.add(productService);
		nonEssentialWebStatusObjects.add(eucommToolsCreAllele2Service);
	}

	@RequestMapping("/webstatus")
	public String webStatus(Model model, HttpServletResponse response) {


		ExecutorService executor = Executors.newCachedThreadPool();

		boolean ok = true;

		// check our core solr instances are returning via the services
		List<WebStatusModel> webStatusModels = new ArrayList<>();
		for (WebStatus status : webStatusObjects) {

			Future<Long> future = null;

			String name = status.getServiceName();
			long number = 0;
			try {

				//				number = status.getWebStatus();

				// This block causes the method reference getWebStatus to be submitted to the executor
				// And then "get"ted from the future.  If the request is not complete in 2 seconds (more than enough time)
				// then timeout and throw an exception
				Callable<Long> task = status::getWebStatus;
				future = executor.submit(task);
				number = future.get(TIMEOUT_INTERVAL, TimeUnit.SECONDS);

			} catch (Exception e) {

				// Cancel the ongoing call
				if (future!=null) {future.cancel(true);}

				ok = false;
				e.printStackTrace();
			}

			WebStatusModel wModel = new WebStatusModel(name, number);
			webStatusModels.add(wModel);
		}

		model.addAttribute("webStatusModels", webStatusModels);

		// check the imits services
		List<WebStatusModel> nonEssentialWebStatusModels = new ArrayList<>();
		boolean nonEssentialOk=true;
		for (WebStatus status : nonEssentialWebStatusObjects) {

			Future<Long> future = null;
			String name = status.getServiceName();
			long number = 0;

			try {
				// number = status.getWebStatus();

				// This block causes the method reference getWebStatus to be submitted to the executor
				// And then "get"ted from the future.  If the request is not complete in 2 seconds (more than enough time)
				// then timeout and throw an exception
				Callable<Long> task = status::getWebStatus;
				future = executor.submit(task);
				number = future.get(TIMEOUT_INTERVAL, TimeUnit.SECONDS);

			} catch (Exception e) {

				// Cancel the ongoing call
				if (future!=null) {future.cancel(true);}

				// Do not change the website status for an unavailable non-critical resource
				nonEssentialOk=false;
				e.printStackTrace();
			}
			WebStatusModel wModel = new WebStatusModel(name, number);
			nonEssentialWebStatusModels.add(wModel);
		}

		model.addAttribute("nonEssentialWebStatusModels", nonEssentialWebStatusModels);
		if (ok) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		model.addAttribute("ok", ok);
		model.addAttribute("nonEssentialOk",nonEssentialOk);
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
