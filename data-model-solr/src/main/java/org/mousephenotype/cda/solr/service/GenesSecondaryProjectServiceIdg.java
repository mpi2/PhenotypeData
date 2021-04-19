/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.pojo.GenesSecondaryProject;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.EssentialGeneDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.mousephenotype.cda.solr.web.dto.HeatMapCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenesSecondaryProjectServiceIdg {

    private static final Logger logger = LoggerFactory.getLogger(GenesSecondaryProjectServiceIdg.class);

    private GeneService geneService;
    private EssentialGeneService essentialGeneService;
    private MpService mpService;
    private CacheManager cacheManager;

    @Inject
    public GenesSecondaryProjectServiceIdg(
            @NotNull GeneService geneService,
            @NotNull EssentialGeneService essentialGeneService,
            @NotNull MpService mpService,
            @NotNull CacheManager cacheManager) {
        this.geneService = geneService;
        this.essentialGeneService = essentialGeneService;
        this.mpService = mpService;
        this.cacheManager = cacheManager;
    }

    /**
     * Caching policy definitions
     * <p>
     * Flush the cache every so often.  This is triggered by Spring on a schedule of every fixedDelay = 86400000 ms (24
     * hours)
     */
    @Scheduled(fixedDelay = 86400000)
    public void resetIdgCaches() {
        List<String> caches = Arrays.asList(
                "topLevelPhenotypesGeneRows",
                "geneRowCache");
        caches.forEach(cache -> Objects.requireNonNull(cacheManager.getCache(cache)).clear());
        logger.info("Refreshing IDG caches");
    }

    public Set<GenesSecondaryProject> getAllBySecondaryProjectId() throws IOException, SolrServerException {
        Set<GenesSecondaryProject> infos = new HashSet<>();
        List<EssentialGeneDTO> geneList = essentialGeneService.getAllIdgGeneList().stream()
                .filter(x -> x.getMgiAccession() != null)
                .collect(Collectors.toList());

        for (EssentialGeneDTO gene : geneList) {
            GenesSecondaryProject info = new GenesSecondaryProject();
            info.setGroupLabel(gene.getIdgFamily());
            info.setMgiGeneAccessionId(gene.getMgiAccession());
            info.setHumanGeneSymbol(gene.getHumanGeneSymbol());
            infos.add(info);
        }

        return infos;
    }

    public Set<GenesSecondaryProject> getAllBySecondaryProjectIdAndGroupLabel(String groupLabel)
            throws IOException, SolrServerException {
        return getAllBySecondaryProjectId().stream()
                .filter(x -> x.getGroupLabel().equalsIgnoreCase(groupLabel))
                .collect(Collectors.toSet());
    }


//    @Cacheable("topLevelPhenotypesGeneRows")
    public List<GeneRowForHeatMap> getGeneRowsForHeatMap(String baseUrl) throws SolrServerException, IOException {

        List<BasicBean> parameters = this.getXAxisForHeatMap();

        // get a list of mgi geneAccessionIds for the project - which will be the row headers
        Set<GenesSecondaryProject> projectBeans = this.getAllBySecondaryProjectId();

        Set<String> accessions = projectBeans
                .stream()
                .map(GenesSecondaryProject::getMgiGeneAccessionId)
                .collect(Collectors.toSet());


        List<GeneDTO> geneToMouseStatus = geneService.getProductionStatusForGeneSet(accessions);
        Map<String, GeneRowForHeatMap> rows = getSecondaryProjectMapForGeneList(geneToMouseStatus, parameters, baseUrl, projectBeans);
        List<GeneRowForHeatMap> geneRows = new ArrayList<>(rows.values());
        Collections.sort(geneRows);
        return geneRows;
    }


    /**
     * No need to expire this cache as the top level phenotype terms will not change
     * @return List of top level terms wrapped in BasicBean objects
     */
    @Cacheable("topLevelPhenotypesXAxis")
    public List<BasicBean> getXAxisForHeatMap() throws IOException, SolrServerException {
        Set<BasicBean> topLevelPhenotypes = mpService.getAllTopLevelPhenotypesAsBasicBeans();
        return new ArrayList<>(topLevelPhenotypes);
    }


//    @Cacheable("geneRowCache")
    public HashMap<String, GeneRowForHeatMap> getSecondaryProjectMapForGeneList(
            List<GeneDTO> genes,
            List<BasicBean> topLevelMps,
            String baseUrl,
            Set<GenesSecondaryProject> projectBeans) {

        HashMap<String, GeneRowForHeatMap> geneRowMap = new HashMap<>(); // <geneAcc, row>

        Map<String, String> accessionToGroupLabelMap = projectBeans
                .stream()
                .collect(Collectors.toMap(GenesSecondaryProject::getMgiGeneAccessionId, GenesSecondaryProject::getGroupLabel,
                        (groupLabel1, groupLabel2) -> {
                            if (groupLabel1.equalsIgnoreCase(groupLabel2)) {
                                return groupLabel1;
                            } else {
                                return groupLabel1 + " " + groupLabel2;
                            }
                        }));

        Map<String, String> accessionToHumanSymbol = projectBeans.stream().collect(Collectors.toMap(GenesSecondaryProject::getMgiGeneAccessionId, GenesSecondaryProject::getHumanGeneSymbol,
                (humanGene1, humanGene2) -> {
                    if (humanGene1.equals(humanGene2)) {
                        return humanGene1;
                    } else {
                        return humanGene1 + " " + humanGene2;
                    }
                }));


        for (GeneDTO gene : genes) {

            // Fill row with default values for all mp top levels
            GeneRowForHeatMap row = new GeneRowForHeatMap(gene.getMgiAccessionId(), gene.getMarkerSymbol(), topLevelMps);

            // get a data structure with the gene accession, parameter associated with a value or status ie. not phenotyped, not significant
            String accession = gene.getMgiAccessionId();
            String humanSymbol = accessionToHumanSymbol.get(accession);
            List<String> humanSymbols = new ArrayList<>();
            humanSymbols.add(humanSymbol);
            row.setHumanSymbol(humanSymbols);

            // Mouse production status
            Map<String, String> prod = GeneService.getStatusFromDTO(gene, baseUrl+"/genes/"+gene.getMgiAccessionId());
            String prodStatusIcons = prod.get("productionIcons") + prod.get("phenotypingIcons");
            prodStatusIcons = prodStatusIcons.equals("") ? "-" : prodStatusIcons;
            row.setMiceProduced(prodStatusIcons);
            if (row.getMiceProduced().isEmpty()) {
                for (HeatMapCell cell : row.getXAxisToCellMap().values()) {
                    cell.addStatus(HeatMapCell.THREE_I_NO_DATA); // set all the cells to No Data Available
                }
            }
            if (accessionToGroupLabelMap.containsKey(accession)) {
                row.setGroupLabel(accessionToGroupLabelMap.get(accession));
            }

            // The term might have been annotated to "mammalian phenotype" which doesn't have an icon in the grid.  Skip it
            for (BasicBean bean : topLevelMps) {
                String mp = bean.getName();
                HeatMapCell cell = row.getXAxisToCellMap().containsKey(mp) ? row.getXAxisToCellMap().get(mp) : new HeatMapCell(mp, HeatMapCell.THREE_I_NO_DATA);
                if (gene.getSignificantTopLevelMpTerms() != null && gene.getSignificantTopLevelMpTerms().contains(mp)) {
                    cell.addStatus(HeatMapCell.THREE_I_DEVIANCE_SIGNIFICANT);
                } else if (gene.getNotSignificantTopLevelMpTerms() != null && gene.getNotSignificantTopLevelMpTerms().contains(mp)) {
                    cell.addStatus(HeatMapCell.THREE_I_DATA_ANALYSED_NOT_SIGNIFICANT);
                }
                row.add(cell);
            }

            geneRowMap.put(gene.getMgiAccessionId(), row);

        }


        return geneRowMap;
    }

}
