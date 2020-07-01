package uk.ac.ebi.phenotype.web.controller;

import org.mousephenotype.cda.solr.repositories.image.ImagesSolrJ;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Monitor the various critical endpoints required by the portal.  If any of these are unresponsive
 * the page will signal to that this app is unhealthy and any downstream consequences can occur (i.e., removal
 * from loadbalancer, automatic restart, etc.)
 */
@Controller
public class WebStatusController {

    private final Logger logger = LoggerFactory.getLogger(WebStatusController.class);

    public static final Integer TIMEOUT_INTERVAL = 3;

    List<WebStatus> webStatusObjects;

    private final ObservationService observationService;
    private final GenotypePhenotypeService genotypePhenotypeService;
    private final StatisticalResultService statisticalResultService;
    private final AlleleService alleleService;
    private final ImagesSolrJ sangerImages;
    private final GeneService geneService;
    private final ImageService impcImageService;
    private final MpService mpService;
//    private final AnatomyService anatomyService;
    private final ImpressService pipelineService;
    private final PhenodigmService phenodigmService;

    // imits solr services

    final Allele2Service allele2;
    final ProductService productService;

    List<WebStatus> nonEssentialWebStatusObjects;
    private Model savedModel = null;
    private Integer cacheHit = 0;
    private Integer cacheMiss = 0;

    public WebStatusController(
            @NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService,
            @NotNull @Named("statistical-result-service") StatisticalResultService statisticalResultService,
            ObservationService observationService,
            ProductService productService,
            AlleleService alleleService,
            ImagesSolrJ sangerImages,
            ImageService impcImageService,
            MpService mpService,
//            AnatomyService anatomyService,
            ImpressService pipelineService,
            GeneService geneService,
            Allele2Service allele2,
            PhenodigmService phenodigmService
    ) {
        this.statisticalResultService = statisticalResultService;
        this.observationService = observationService;
        this.productService = productService;
        this.genotypePhenotypeService = genotypePhenotypeService;
        this.alleleService = alleleService;
        this.sangerImages = sangerImages;
        this.impcImageService = impcImageService;
        this.mpService = mpService;
//        this.anatomyService = anatomyService;
        this.pipelineService = pipelineService;
        this.geneService = geneService;
        this.allele2 = allele2;
        this.phenodigmService = phenodigmService;
    }

    @PostConstruct
    public void initialise() {

        // cores we need to test are at least this set:
        // experiment,genotype-phenotype,statistical-result,preqc,allele,images,impc_images,mp,ma,pipeline,gene,disease,autosuggest
        // System.out.println("calling webStatus initialisation method");
        webStatusObjects = new ArrayList<>();
        webStatusObjects.add(observationService);
        webStatusObjects.add(genotypePhenotypeService);
        webStatusObjects.add(statisticalResultService);
        webStatusObjects.add(alleleService);
        webStatusObjects.add(sangerImages);
        webStatusObjects.add(impcImageService);
        webStatusObjects.add(mpService);
//        webStatusObjects.add(anatomyService);
        webStatusObjects.add(pipelineService);
        webStatusObjects.add(geneService);
        webStatusObjects.add(phenodigmService);

        nonEssentialWebStatusObjects = new ArrayList<>();
        nonEssentialWebStatusObjects.add(allele2);
        nonEssentialWebStatusObjects.add(productService);
    }

    @RequestMapping("/clearCache")
    public synchronized ModelAndView clearCache() {
        savedModel = null;
        return new ModelAndView("redirect:webstatus");


    }

    @RequestMapping("/webstatus")
    public synchronized String webStatus(Model model, HttpServletResponse response) {

        if (savedModel != null && savedModel.containsAttribute("webStatusModels") && Math.random() < 0.95) {
            cacheHit += 1;

            model.addAllAttributes(savedModel.asMap());

            model.addAttribute("hits", cacheHit);
            model.addAttribute("misses", cacheMiss);
            model.addAttribute("ratio", cacheHit+cacheMiss>0 ? ((float)cacheHit / (cacheHit+cacheMiss)): "Not available yet");

            return "webStatus";
        }

        logger.debug("Updating webstatus model values");
        cacheMiss += 1;

        ExecutorService executor = Executors.newCachedThreadPool();

        boolean ok = true;

        // check our core solr instances are returning via the services
        List<WebStatusModel> webStatusModels = new ArrayList<>();
        for (WebStatus status : webStatusObjects) {

            Future<Long> future = null;

            String name = status.getServiceName();
            long number = 0;
            try {

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

                // Do not cache the model if it is not ok.  This will force the status check
                // to occur on subsequent page loads.
                savedModel = null;

                logger.error("Essential service {} is not available", name);
                e.printStackTrace();
                break;
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
                // And then "get"ted from the future.  If the request is not complete in TIMEOUT_INTERVAL
                // seconds (more than enough time) then timeout and throw an exception
                Callable<Long> task = status::getWebStatus;
                future = executor.submit(task);
                number = future.get(TIMEOUT_INTERVAL, TimeUnit.SECONDS);

            } catch (Exception e) {

                // Cancel the ongoing call
                if (future!=null) {future.cancel(true);}

                // Do not change the website status for an unavailable non-critical resource
                nonEssentialOk=false;
                logger.error("Non essential service {} is not available", name);
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
        model.addAttribute("hits", cacheHit);
        model.addAttribute("misses", cacheMiss);
        model.addAttribute("ratio", cacheHit+cacheMiss>0 ? ((float)cacheHit / (cacheHit+cacheMiss)): "Not available yet");

        // If everything is ok, cache the model to be used later
        if (ok) {
            savedModel = model;
        }


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