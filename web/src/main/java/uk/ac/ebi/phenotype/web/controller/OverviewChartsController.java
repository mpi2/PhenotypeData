/*******************************************************************************
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
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class OverviewChartsController {


    private final GenotypePhenotypeService genotypePhenotypeService;

    @Inject
    public OverviewChartsController(
            @NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService
    ) {
        this.genotypePhenotypeService = genotypePhenotypeService;
    }


    @RequestMapping(value = "/chordDiagram", method = RequestMethod.GET)
    public String getGraph(
            @RequestParam(required = false, value = "phenotype_name") List<String> phenotypeName,
            Model model) {

        model.addAttribute("phenotypeName", (phenotypeName != null) ? new JSONArray(phenotypeName.stream().distinct().collect(Collectors.toList())) : null);
        return "chordDiagram";

    }


    @ResponseBody
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/chordDiagram.json", method = RequestMethod.GET)
    public String getMatrix(
            @RequestParam(required = false, value = "phenotype_name") List<String> phenotypeName,
            @RequestParam(required = false, value = "idg") Boolean idg,
            @RequestParam(required = false, value = "idgClass") String idgClass) {

        return genotypePhenotypeService.getPleiotropyMatrix(phenotypeName, idg, idgClass).toString();

    }


    @ResponseBody
    @RequestMapping(value = "/chordDiagram.csv", method = RequestMethod.GET)
    public String getChordDiagramDownload(
            @RequestParam(required = false, value = "phenotype_name") List<String> phenotypeName,
            @RequestParam(required = false, value = "idg") Boolean idg,
            @RequestParam(required = false, value = "idgClass") String idgClass
    ) {

        try {
            return genotypePhenotypeService.getPleiotropyDownload(phenotypeName, idg, idgClass);
        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
        }
        return "";
    }

}