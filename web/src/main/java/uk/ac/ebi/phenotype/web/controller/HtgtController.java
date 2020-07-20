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


    @RequestMapping("/designs/{design_id}")
    public String genes(@PathVariable int design_id,
                        @RequestParam(required = false, value = "accession") String acc,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        RedirectAttributes attributes)
            throws URISyntaxException,IOException, SQLException, SolrServerException {

        GeneDTO gene=null;
        if(acc!=null && !acc.isEmpty()) {
            gene = geneService.getGeneById(acc);
            System.out.println("gene ikmc id="+ gene);
        }
        if(gene!=null){
            model.addAttribute("gene", gene);
        }
        processHtgtRequest(design_id, model, request);

        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        return "htgt";
    }




    private void processHtgtRequest(int designId, Model model, HttpServletRequest request)
    {
        int numberOfTopLevelMpTermsWithStatisticalResult = 0;
        //GeneDTO gene = geneService.getGeneById(designId);
        List<Design> designs=htgtService.getDesigns(designId);
        System.out.println("calling process htgt");
        model.addAttribute("designs", designs);
        model.addAttribute("designId", designId);
    }



}
