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
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.service.PharosService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

@Controller
public class HtgtController {

    private final Logger LOGGER = LoggerFactory.getLogger(HtgtController.class);
    private static final int numberOfImagesToDisplay = 5;


    private final GeneService              geneService;
    private final OrderService             orderService;
    private final HtgtService htgtService;

    @Resource(name = "globalConfiguration")
    Map<String, String> config;

    private String cmsBaseUrl;

    private PharosService pharosService;

    @Inject
    public HtgtController(
            GeneService geneService,
            OrderService orderService,
            @Named("statistical-result-service") StatisticalResultService statisticalResultService, HtgtService htgtService) {
        this.geneService = geneService;
        this.orderService = orderService;
        this.htgtService = htgtService;
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


            processHtgtRequest(design_id, model, request);

        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        return "htgt";
    }




    private void processHtgtRequest(String designId, Model model, HttpServletRequest request)
            throws GenomicFeatureNotFoundException, URISyntaxException, IOException, SQLException, SolrServerException {
        int numberOfTopLevelMpTermsWithStatisticalResult = 0;
        GeneDTO gene = geneService.getGeneById(designId);
        String designs=htgtService.getDesigns(designId);
        System.out.println("calling process htgt");
        if (gene == null) {
            LOGGER.warn("Gene object from solr for " + designId + " can't be found.");
            throw new GenomicFeatureNotFoundException("Gene " + designId + " can't be found.", designId);
        }

        model.addAttribute("gene",gene);
    model.addAttribute("designs", designs);
    }



}
