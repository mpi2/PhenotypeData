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
import org.mousephenotype.cda.solr.service.Design;
import org.mousephenotype.cda.solr.service.GeneService;
import uk.ac.ebi.phenotype.service.HtgtService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class HtgtController {

    private final Logger logger = LoggerFactory.getLogger(HtgtController.class);


    private final GeneService geneService;
    private final HtgtService htgtService;


    @Inject
    public HtgtController(
            GeneService geneService,
            HtgtService htgtService) {
        this.geneService = geneService;
        this.htgtService = htgtService;
    }

    /**
     * Runs when the request missing an accession ID. This redirects to the
     * search page which defaults to showing all genes in the list
     */
    @RequestMapping("/htgt")
    public String rootForward() {
        return "redirect:/search";
    }

    @RequestMapping("/designs/{design_id}")
    public String genes(@PathVariable Integer design_id,
                        @RequestParam(required = false, value = "accession") String acc,
                        Model model,
                        HttpServletRequest request)
            throws IOException, SolrServerException {

        GeneDTO gene = null;
        if (acc != null && !acc.isEmpty()) {
            gene = geneService.getGeneById(acc);
            logger.debug("gene ikmc id=" + gene);
        }
        if (gene != null) {
            model.addAttribute("gene", gene);
        }
        List<Design> designs = htgtService.getDesigns(design_id);
        model.addAttribute("designs", designs);
        model.addAttribute("designId", design_id);

        return "htgt";
    }
}
