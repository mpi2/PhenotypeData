/** *****************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ****************************************************************************** */
package uk.ac.ebi.phenotype.web.controller;

import org.mousephenotype.cda.solr.generic.util.PhenotypeCallSummarySolr;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenodigm2.WebDao;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.generic.util.RegisterInterestUtils;
import uk.ac.ebi.phenotype.generic.util.SolrIndex2;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;
import uk.ac.ebi.phenotype.service.PharosService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Controller
public class EssentialGenesController {

    private final Logger LOGGER = LoggerFactory.getLogger(EssentialGenesController.class);
    private static final int numberOfImagesToDisplay = 5;

    private final PhenotypeSummaryDAO phenSummary;
    private final ImagesSolrDao            imagesSolrDao;
    private final PhenotypeCallSummarySolr phenotypeCallSummaryService;
    private final ObservationService       observationService;
    private final SolrIndex                solrIndex;
    private final SolrIndex2               solrIndex2;
    private final ImageService             imageService;
    private final ExpressionService        expressionService;
    private final GeneService              geneService;
    private final StatisticalResultService statisticalResultService;
    private final GenotypePhenotypeService genotypePhenotypeService;
    private final OrderService             orderService;
    private final ImpressService           impressService;
    private final WebDao                   phenoDigm2Dao;
    private final RegisterInterestUtils    riUtils;

    @Resource(name = "globalConfiguration")
    Map<String, String> config;

    private String cmsBaseUrl;

    private PharosService pharosService;

    @Inject
    public EssentialGenesController(PhenotypeCallSummarySolr phenotypeCallSummaryService, PhenotypeSummaryDAO phenSummary, ImagesSolrDao imagesSolrDao, ObservationService observationService, SolrIndex solrIndex, SolrIndex2 solrIndex2, WebDao phenoDigm2Dao, ImageService imageService, ExpressionService expressionService, RegisterInterestUtils riUtils, GeneService geneService, ImpressService impressService, GenotypePhenotypeService genotypePhenotypeService, OrderService orderService, StatisticalResultService statisticalResultService) {
        this.phenotypeCallSummaryService = phenotypeCallSummaryService;
        this.phenSummary = phenSummary;
        this.imagesSolrDao = imagesSolrDao;
        this.observationService = observationService;
        this.solrIndex = solrIndex;
        this.solrIndex2 = solrIndex2;
        this.phenoDigm2Dao = phenoDigm2Dao;
        this.imageService = imageService;
        this.expressionService = expressionService;
        this.riUtils = riUtils;
        this.geneService = geneService;
        this.impressService = impressService;
        this.genotypePhenotypeService = genotypePhenotypeService;
        this.orderService = orderService;
        this.statisticalResultService = statisticalResultService;
    }

    @PostConstruct
    private void postConstruct() {

        cmsBaseUrl = config.get("cmsBaseUrl");
        pharosService = new PharosService();
    }




    @RequestMapping("/essential_genes")
    public String essentialGenes( Model model)
    {
System.out.println("calling essential genes");
        return "essential_genes";
    }

}
