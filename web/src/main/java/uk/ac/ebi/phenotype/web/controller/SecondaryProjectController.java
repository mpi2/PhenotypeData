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
import org.mousephenotype.cda.db.pojo.GenesSecondaryProject;
import org.mousephenotype.cda.solr.service.EssentialGeneService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.GenesSecondaryProjectServiceIdg;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.phenotype.chart.PieChartCreator;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.util.PublicationFetcher;
import uk.ac.ebi.phenotype.web.dao.ReferenceService;
import uk.ac.ebi.phenotype.web.dto.AlleleRef;
import uk.ac.ebi.phenotype.web.dto.Publication;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.mousephenotype.cda.common.Constants.IDG_PUBLICATION_LIST;

@Controller
public class SecondaryProjectController {

    private final Logger logger = LoggerFactory.getLogger(SecondaryProjectController.class);

    private GeneService geneService;
    private UnidimensionalChartAndTableProvider chartProvider;
    private GenesSecondaryProjectServiceIdg idg;
    private ReferenceService referenceService;

    public SecondaryProjectController(
            @NotNull GeneService geneService,
            @NotNull ReferenceService referenceService,
            @NotNull UnidimensionalChartAndTableProvider chartProvider,
            @NotNull GenesSecondaryProjectServiceIdg idg) {
        this.geneService = geneService;
        this.referenceService = referenceService;
        this.chartProvider = chartProvider;
        this.idg = idg;
    }

    @RequestMapping(value = "/secondaryproject/idg", method = RequestMethod.GET)
    public String idgProjectPage(HttpServletRequest request,
                                           Model model)
            throws IOException, SolrServerException {

        // for IDG now we are going to get the list of gene accessions from the essential genes core

        Set<GenesSecondaryProject> secondaryProjects = idg.getAllBySecondaryProjectId();
        Set<String> accessions = secondaryProjects
                .stream()
                .map(GenesSecondaryProject::getMgiGeneAccessionId)
                .collect(Collectors.toSet());
        List<GeneDTO> geneDTOList = geneService.getGenesByMgiIds(new ArrayList<>(accessions));
        Set<String> alleleSymbols = new HashSet<>();
        geneDTOList.stream().filter(g -> g.getAlleleName() != null).forEach(g -> g.getAlleleName().forEach(aName -> alleleSymbols.add(g.getMarkerSymbol() + "<" + aName + ">")));
        Set<String> symbols = geneDTOList
                .stream()
                .map(GeneDTO::getMarkerSymbol)
                .collect(Collectors.toSet());

        Map<String, Long> geneStatus = geneService.getStatusCount(accessions, GeneDTO.ES_CELL_PRODUCTION_STATUS);
        Map<String, Long> mouseStatus = geneService.getStatusCount(accessions, GeneDTO.MOUSE_PRODUCTION_STATUS);
        Map<String, Long> phenoStatus = geneService.getStatusCount(accessions, GeneDTO.PHENOTYPE_STATUS);
        Map<String, Long> combinedData = new HashMap<>();
        combinedData.putAll(geneStatus);
        combinedData.putAll(mouseStatus);
        combinedData.putAll(phenoStatus);

        List<String> colorsForPie = new ArrayList<>();

        String nonSigColor = "'rgb(194, 194, 194)'";
        String sigColor = "'rgb(247, 157, 70)'";
        colorsForPie.add(sigColor);
        colorsForPie.add(nonSigColor);

        Map<String, Integer> totalLabelToNumber = new LinkedHashMap<>();
        Integer total = accessions.size();
        Integer withoutData = Math.toIntExact(total - phenoStatus.values().stream().mapToInt(Long::intValue).sum());
        Integer withData = total - withoutData;
        totalLabelToNumber.put("IDG gene with IMPC Data", withData);
        totalLabelToNumber.put("IDG gene with no IMPC Data", withoutData);

        String idgOrthologPie = PieChartCreator.getPieChartForColorList(totalLabelToNumber, "idgOrthologPie", "IDG genes IMPC data status", "", colorsForPie);
        model.addAttribute("idgOrthologPie", idgOrthologPie);
        model.addAttribute("idgChartTable", chartProvider.getStatusColumnChart(combinedData, "IDG genes IMPC production status", "idgChart", colorsForPie));
        model.addAttribute("idgGeneCount", accessions.size());
        model.addAttribute("idgPercent", String.format("%.1f", ((withData / Float.valueOf(total)) * 100)));

        // Include data for supporting the table views of data available
        String baseUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();
        List<GeneRowForHeatMap> geneRows = idg.getGeneRowsForHeatMap(baseUrl).stream().map(x -> {
            GeneRowForHeatMap n = new GeneRowForHeatMap(x.getAccession());
            n.setMiceProduced(x.getMiceProduced().replaceAll("badge", "btn"));
            n.setGroupLabel(x.getGroupLabel());
            n.setSymbol(x.getSymbol());
            n.setHumanSymbol(x.getHumanSymbol());
            n.setLowestPValue(x.getLowestPValue());
            n.setXAxisToCellMap(x.getXAxisToCellMap());
            return n;
            }).collect(Collectors.toList());

        // Ion channel specific information
        final List<GeneRowForHeatMap> ionChannelRows = geneRows.stream()
                .filter(x -> x.getGroupLabel().equals(EssentialGeneService.ION_CHANNEL))
                .collect(Collectors.toList());
        model.addAttribute("ION_CHANNEL", EssentialGeneService.ION_CHANNEL);
        model.addAttribute("ionChannelRows", ionChannelRows);
        model.addAttribute("ionChannelEsCellsProduced", ionChannelRows.stream().filter(x -> x.getMiceProduced().contains("ES Cells")).count());
        model.addAttribute("ionChannelMiceProduced", ionChannelRows.stream().filter(x -> x.getMiceProduced().contains("Mice")).count());
        model.addAttribute("ionChannelPhenotypesProduced", ionChannelRows.stream().filter(x -> x.getMiceProduced().contains("Phenotype data")).count());

        // Kinase specific
        final List<GeneRowForHeatMap> kinaseRows = geneRows.stream().filter(x -> x.getGroupLabel().equals(EssentialGeneService.KINASE)).collect(Collectors.toList());
        model.addAttribute("KINASE", EssentialGeneService.KINASE);
        model.addAttribute("kinaseRows", kinaseRows);
        model.addAttribute("kinaseEsCellsProduced", kinaseRows.stream().filter(x -> x.getMiceProduced().contains("ES Cells")).count());
        model.addAttribute("kinaseMiceProduced", kinaseRows.stream().filter(x -> x.getMiceProduced().contains("Mice")).count());
        model.addAttribute("kinasePhenotypesProduced", kinaseRows.stream().filter(x -> x.getMiceProduced().contains("Phenotype data")).count());

        // GPCRC specific
        final List<GeneRowForHeatMap> gpcrRows = geneRows.stream().filter(x -> x.getGroupLabel().equals(EssentialGeneService.GPCR)).collect(Collectors.toList());
        model.addAttribute("GPCR", EssentialGeneService.GPCR);
        model.addAttribute("gpcrRows", gpcrRows);
        model.addAttribute("gpcrEsCellsProduced", gpcrRows.stream().filter(x -> x.getMiceProduced().contains("ES Cells")).count());
        model.addAttribute("gpcrMiceProduced", gpcrRows.stream().filter(x -> x.getMiceProduced().contains("Mice")).count());
        model.addAttribute("gpcrPhenotypesProduced", gpcrRows.stream().filter(x -> x.getMiceProduced().contains("Phenotype data")).count());

        /*
        PUBLICATIONS
         */

        // Filter out publications not directly related to this gene
        final List<Publication> publications = referenceService.getReviewedByPmidList(new ArrayList<>(IDG_PUBLICATION_LIST));
        model.addAttribute("publications", publications);

        return "idg";


    }

}
