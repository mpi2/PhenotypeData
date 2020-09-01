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
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.phenotype.chart.ColorCodingPalette;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;

import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;


@Controller
public class PhenomeStatsController {

    private final GenotypePhenotypeService genotypePhenotypeService;
    private final PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();

    public PhenomeStatsController(
            @NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService
    ) {
        this.genotypePhenotypeService = genotypePhenotypeService;
    }

    @RequestMapping(value = "/phenome", method = RequestMethod.GET)
    public String getGraph(
            @RequestParam(required = true, value = "pipeline_stable_id") String pipelineStableId,
            @RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
            Model model) throws SolrServerException, IOException, URISyntaxException, JSONException {

        PhenotypeFacetResult results = genotypePhenotypeService.getPhenotypeFacetResultByPhenotypingCenterAndPipeline(phenotypingCenter, pipelineStableId);

        ColorCodingPalette colorCoding = new ColorCodingPalette();

        colorCoding.generatePhenotypeCallSummaryColorsNew(
                results.getPhenotypeCallSummaries(),
                ColorCodingPalette.NB_COLOR_MAX,
                1,
                Constants.P_VALUE_THRESHOLD);

        // generate a chart
        String chart = phenomeChartProvider.generatePhenomeChartByPhenotype(
                results.getPhenotypeCallSummaries(),
                phenotypingCenter,
                Constants.P_VALUE_THRESHOLD);

        model.addAttribute("phenotypeCalls", results.getPhenotypeCallSummaries());
        model.addAttribute("palette", colorCoding.getPalette());
        model.addAttribute("chart", chart);

        return null;
    }
}