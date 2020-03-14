package uk.ac.ebi.phenotype.web.controller;

import org.mousephenotype.cda.solr.repositories.image.ImagesSolrJ;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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

    private final Logger logger = LoggerFactory.getLogger(WebStatusController.class);

    public static final Integer TIMEOUT_INTERVAL = 2;

    @Autowired
    ObservationService observationService;

    @Autowired
    private GenotypePhenotypeService genotypePhenotypeService;

    @Autowired
    private StatisticalResultService statisticalResultService;

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
    PhenodigmService phenodigmService;

    List<WebStatus> webStatusObjects;

    // imits solr services

    @Autowired
    Allele2Service allele2;

    @Autowired
    ProductService productService;

    List<WebStatus> nonEssentialWebStatusObjects;
    private Model savedModel = null;
    private Integer cacheHit = 0;
    private Integer cacheMiss = 0;

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
        webStatusObjects.add(anatomyService);
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