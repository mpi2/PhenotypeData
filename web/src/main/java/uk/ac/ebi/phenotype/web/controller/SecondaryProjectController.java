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

import org.mousephenotype.cda.db.pojo.GenesSecondaryProject;
import org.mousephenotype.cda.solr.service.EssentialGeneService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.GenesSecondaryProjectServiceIdg;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.phenotype.chart.PieChartCreator;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.web.dao.GenesSecondaryProjectService;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SecondaryProjectController {

    private final Logger logger = LoggerFactory.getLogger(SecondaryProjectController.class);

    private GeneService geneService;
    private UnidimensionalChartAndTableProvider chartProvider;
    private GenesSecondaryProjectServiceIdg idg;

    public SecondaryProjectController(
            @NotNull GeneService geneService,
            //@NotNull EssentialGeneService essentialGeneService,
            @NotNull UnidimensionalChartAndTableProvider chartProvider,
            @NotNull @Qualifier("idg") GenesSecondaryProjectServiceIdg idg) {
        this.geneService = geneService;
        this.chartProvider = chartProvider;
        this.idg = idg;
    }

    @RequestMapping(value = "/secondaryproject/{id}", method = RequestMethod.GET)
    public String loadSecondaryProjectPage(@PathVariable String id, Model model) {


                //for IDG now we are going to get the list of gene accessions from the essential genes core

                Set<GenesSecondaryProject> secondaryProjects = idg.getAllBySecondaryProjectId();
                Set<String> accessions = secondaryProjects
                        .stream()
                        .map(GenesSecondaryProject::getMgiGeneAccessionId)
                        .collect(Collectors.toSet());

                Map<String, Long> geneStatus = geneService.getStatusCount(accessions, GeneDTO.LATEST_ES_CELL_STATUS);
                Map<String, Long> mouseStatus = geneService.getStatusCount(accessions, GeneDTO.LATEST_MOUSE_STATUS);
                Map<String, Long> phenoStatus = geneService.getStatusCount(accessions, GeneDTO.LATEST_PHENOTYPE_STATUS);
                Map<String, Long> combinedData = new HashMap<>();
                combinedData.putAll(geneStatus);
                combinedData.putAll(mouseStatus);
                combinedData.putAll(phenoStatus);
                
                List<String> colorsForPie = new ArrayList<>();
                
                String nonSigColor="'rgb(194, 194, 194)'";
                String sigColor="'rgb(247, 157, 70)'";
                colorsForPie.add(sigColor);
                colorsForPie.add(nonSigColor);

                Map<String, Integer> totalLabelToNumber = new LinkedHashMap<>();
                Integer total = accessions.size();
                Integer withoutData = Math.toIntExact(total - phenoStatus.values().stream().mapToInt(Long::intValue).sum());
                Integer withData = total - withoutData;
                totalLabelToNumber.put("IDG gene with IMPC Data", withData);
                totalLabelToNumber.put("IDG gene with no IMPC Data", withoutData);
                
                String idgOrthologPie = PieChartCreator.getPieChartForColorList(totalLabelToNumber, "idgOrthologPie", "IDG genes IMPC data status", "",colorsForPie);
                model.addAttribute("idgOrthologPie", idgOrthologPie);
                model.addAttribute("idgChartTable", chartProvider.getStatusColumnChart(combinedData, "IDG genes IMPC production status", "idgChart", colorsForPie));
                model.addAttribute("idgGeneCount", accessions.size());
                model.addAttribute("idgPercent", String.format("%.1f", ((withData / Float.valueOf(total))*100)));


            return "idg";



    }

}
