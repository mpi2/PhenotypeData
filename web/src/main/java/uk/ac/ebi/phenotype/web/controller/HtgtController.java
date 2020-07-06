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

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.bean.ExpressionImagesBean;
import org.mousephenotype.cda.solr.generic.util.PhenotypeCallSummarySolr;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.web.dto.*;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenodigm2.Disease;
import uk.ac.ebi.phenodigm2.DiseaseModelAssociation;
import uk.ac.ebi.phenodigm2.GeneDiseaseAssociation;
import uk.ac.ebi.phenodigm2.WebDao;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.generic.util.RegisterInterestUtils;
import uk.ac.ebi.phenotype.generic.util.SolrIndex2;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryBySex;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryType;
import uk.ac.ebi.phenotype.service.PharosService;
import uk.ac.ebi.phenotype.web.util.FileExportUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HtgtController {

    private final Logger LOGGER = LoggerFactory.getLogger(HtgtController.class);
    private static final int numberOfImagesToDisplay = 5;


    private final GeneService              geneService;
    private final StatisticalResultService statisticalResultService;
    private final OrderService             orderService;
    private final ImpressService           impressService;

    @Resource(name = "globalConfiguration")
    Map<String, String> config;

    private String cmsBaseUrl;

    private PharosService pharosService;

    @Inject
    public HtgtController(
                          ImageService imageService,
                          GeneService geneService,
                          ImpressService impressService,
                          OrderService orderService,
                          @Named("statistical-result-service") StatisticalResultService statisticalResultService) {
        this.geneService = geneService;
        this.impressService = impressService;
        this.orderService = orderService;
        this.statisticalResultService = statisticalResultService;
    }

    @PostConstruct
    private void postConstruct() {

        cmsBaseUrl = config.get("cmsBaseUrl");
        pharosService = new PharosService();
    }

    HttpProxy proxy = new HttpProxy();


    /**
     * Runs when the request missing an accession ID. This redirects to the
     * search page which defaults to showing all genes in the list
     */
    @RequestMapping("/htgt")
    public String rootForward() {
        return "redirect:/search";
    }

    /**
     * Prints out the request object
     */


    @RequestMapping("/htgt/{design_id}")
    public String genes(@PathVariable String design_id,
                        @RequestParam(value = "heatmap", required = false, defaultValue = "false") Boolean showHeatmap,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        RedirectAttributes attributes)
            throws URISyntaxException, GenomicFeatureNotFoundException, IOException, SQLException, SolrServerException {

        String debug = request.getParameter("debug");
        boolean d = debug != null && debug.equals("true");
        if (d) {
            model.addAttribute("debug", "true");
        }


            processGeneRequest(design_id, model, request);

        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        return "htgt";
    }




    private void processGeneRequest(String acc, Model model, HttpServletRequest request)
            throws GenomicFeatureNotFoundException, URISyntaxException, IOException, SQLException, SolrServerException {
        int numberOfTopLevelMpTermsWithStatisticalResult = 0;
        GeneDTO gene = geneService.getGeneById(acc);

        if (gene == null) {
            LOGGER.warn("Gene object from solr for " + acc + " can't be found.");
            throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
        }

        model.addAttribute("gene",gene);

    }



}
