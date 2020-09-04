/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this targetFile except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.reports;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.beans.Introspector;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GeneAndMPTermAssociation report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class GeneAndMPTermAssociation extends AbstractReport {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, String> geneAccessionIdByGeneSymbol = new HashMap<>();

    @Autowired
    GeneService geneService;

    @Autowired
    @Qualifier("genotype-phenotype-service")
    GenotypePhenotypeService genotypePhenotypeService;

    @Autowired
    ObservationService observationService;

    @NotNull
    @Value("${cms_base_url}")
    protected String cmsBaseUrl;

    public GeneAndMPTermAssociation() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    Map<String, String> loadGeneAccessionIdByGeneSymbolMap() throws ReportException {
        try {
            List<GeneDTO> dto = geneService.getAllGeneDTOs();
            return dto.stream().collect(Collectors.toMap(GeneDTO::getMarkerSymbol, GeneDTO::getMgiAccessionId));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportException(e);
        }
    }

    public void run(String[] args) throws ReportException {
        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ZygosityReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);
        long start = System.currentTimeMillis();
        geneAccessionIdByGeneSymbol = loadGeneAccessionIdByGeneSymbolMap();
        int count = 0;

        List<String[]> result = new ArrayList<>();
        Map<Pair<String, ZygosityType>, Set<String>> mps = new TreeMap<>();
        Map<GeneCenterZygosity, List<String>> data = new HashMap<>();
        Map<GeneCenterZygosity, List<String>> viabilityData = new HashMap<>();
        String[] phenotypeZygosityHeading = {"MP Term", "Zygosity", "# Genes", "Gene Symbol (Gene Accession Id)"};
        result.add(phenotypeZygosityHeading);

        try {
            // Get the list of phenotype calls
            List<GenotypePhenotypeDTO> gps = genotypePhenotypeService.getAllGenotypePhenotypes(resources);
            for (GenotypePhenotypeDTO gp : gps) {
                // Exclude Viability calls from the counts of genes by zygosity
                if (gp.getParameterStableId().contains("VIA")) {
                    continue;
                }
                // Exclude LacZ calls
                if (gp.getParameterStableId().contains("ALZ")) {
                    continue;
                }

                final String symbol = gp.getMarkerSymbol();
                final ZygosityType zygosity = ZygosityType.valueOf(gp.getZygosity());
                final List<String> topLevelMpTermName = gp.getTopLevelMpTermName();

                if (topLevelMpTermName == null) continue;

                // Collect top level MP term information
                for (String mp : topLevelMpTermName) {
                    Pair<String, ZygosityType> k = new ImmutablePair<>(mp, zygosity);
                    if (!mps.containsKey(k)) {
                        mps.put(k, new HashSet<>());
                    }
                    mps.get(k).add(symbol);
                }

                // Collect gene center zygosity -> mp term
                GeneCenterZygosity g = new GeneCenterZygosity();
                g.setGeneSymbol(symbol);
                g.setZygosity(ZygosityType.valueOf(gp.getZygosity()));
                g.setPhenotypeCenter(gp.getPhenotypingCenter());
                if (!data.containsKey(g)) {
                    data.put(g, new ArrayList<String>());
                }

                data.get(g).add(gp.getMpTermName());
            }

            for (Pair<String, ZygosityType> k : mps.keySet()) {
                final String mpSymbol = k.getLeft();
                final String zygosity = k.getRight().getName();

                Set<String> mpsSet = new HashSet<>();
                for (String gene : mps.get(k)) {
                    String acc = geneAccessionIdByGeneSymbol.get(gene);
                    mpsSet.add(gene + (acc == null ? "" : "(" + acc + ")"));
                }

                String[] row = {mpSymbol, zygosity, Integer.toString(mps.get(k).size()), StringUtils.join(mpsSet, "|")};
                result.add(row);
            }

            result.add(Constants.EMPTY_ROW);
            String[] geneZygosityHeading = {"Gene Symbol", "Gene Accession Id", "Phenotyping Center", "Viability", "Hom", "Het", "Hemi", "Gene Page URL"};
            result.add(geneZygosityHeading);
            count += result.size();
            csvWriter.writeRowsOfArray(result);
            result.clear();

            // Get the viability data from the experiment core directly
            for (ObservationDTO obs : observationService.getObservationsByParameterStableId("IMPC_VIA_001_001")) {
                // Skip records that are not for the resources we are interested in
                if ( ! resources.contains(obs.getDataSourceName())) {
                    continue;
                }

                String symbol = obs.getGeneSymbol();
                GeneCenterZygosity g = new GeneCenterZygosity();
                g.setGeneSymbol(symbol);
                g.setZygosity(ZygosityType.valueOf(obs.getZygosity()));
                g.setPhenotypeCenter(obs.getPhenotypingCenter());
                if (!viabilityData.containsKey(g)) {
                    viabilityData.put(g, new ArrayList<String>());
                }

                viabilityData.get(g).add(obs.getCategory());
            }

            Set<String> geneSymbols = new HashSet<>();
            for (GeneCenterZygosity g : data.keySet()) {
                geneSymbols.add(g.getGeneSymbol());
            }

            Set<String> centers = new HashSet<>();
            for (GeneCenterZygosity g : viabilityData.keySet()) {
                geneSymbols.add(g.getGeneSymbol());
                centers.add(g.getPhenotypeCenter());
            }

            for (String geneSymbol : geneSymbols) {
                for (String center : centers) {
                    boolean include = false;
                    List<String> via = new ArrayList<>();
                    GeneCenterZygosity candidate = new GeneCenterZygosity();
                    candidate.setPhenotypeCenter(center);
                    candidate.setGeneSymbol(geneSymbol);

                    candidate.setZygosity(ZygosityType.homozygote);
                    String homCount = (data.get(candidate) != null) ? Integer.toString(data.get(candidate).size()) : "";
                    if (viabilityData.containsKey(candidate)) via.addAll(viabilityData.get(candidate));
                    include = (viabilityData.containsKey(candidate) || data.containsKey(candidate)) ? true : include;

                    candidate.setZygosity(ZygosityType.heterozygote);
                    String hetCount = (data.get(candidate) != null) ? Integer.toString(data.get(candidate).size()) : "";
                    if (viabilityData.containsKey(candidate)) via.addAll(viabilityData.get(candidate));
                    include = (viabilityData.containsKey(candidate) || data.containsKey(candidate)) ? true : include;

                    candidate.setZygosity(ZygosityType.hemizygote);
                    String hemiCount = (data.get(candidate) != null) ? Integer.toString(data.get(candidate).size()) : "";
                    if (viabilityData.containsKey(candidate)) via.addAll(viabilityData.get(candidate));
                    include = (viabilityData.containsKey(candidate) || data.containsKey(candidate)) ? true : include;

                    String geneLink = "";
                    String acc = geneAccessionIdByGeneSymbol.get(geneSymbol);
                    acc = (acc == null ? "" : acc.trim());
                    if ( ! acc.isEmpty()) {
                        geneLink = cmsBaseUrl + "/data/genes/" + acc;
                        if (geneLink.startsWith("//")) {
                            geneLink = "http:" + geneLink;
                        }
                    }

                    String[] row = {geneSymbol, acc, center, StringUtils.join(via, ": "), homCount, hetCount, hemiCount, geneLink};
                    if (include)
                        result.add(row);
                }
            }

            result = result
                .stream()
                .sorted(Comparator.comparing((String[] l) -> l[0]))
                .collect(Collectors.toList());

            csvWriter.writeRowsOfArray(result);
            count += result.size();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        csvWriter.closeQuietly();

        log.info(String.format(
            "Finished. %s rows written in %s",
            count, commonUtils.msToHms(System.currentTimeMillis() - start)));
    }


    // PRIVATE METHODS


    private class GeneCenterZygosity {
        private String geneSymbol;
        private String phenotypeCenter;
        private ZygosityType zygosity;

        public String getGeneSymbol() {
            return geneSymbol;
        }

        public void setGeneSymbol(String geneSymbol) {
            this.geneSymbol = geneSymbol;
        }

        public String getPhenotypeCenter() {
            return phenotypeCenter;
        }

        public void setPhenotypeCenter(String phenotypeCenter) {
            this.phenotypeCenter = phenotypeCenter;
        }

        public ZygosityType getZygosity() {
            return zygosity;
        }

        public void setZygosity(ZygosityType zygosity) {
            this.zygosity = zygosity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof GeneCenterZygosity)) {
                return false;
            }
            GeneCenterZygosity that = (GeneCenterZygosity) o;
            if (geneSymbol != null ? !geneSymbol.equals(that.geneSymbol) : that.geneSymbol != null) {
                return false;
            }
            if (phenotypeCenter != null ? !phenotypeCenter.equals(that.phenotypeCenter) : that.phenotypeCenter != null) {
                return false;
            }
            return zygosity == that.zygosity;
        }

        @Override
        public int hashCode() {
            int result = geneSymbol != null ? geneSymbol.hashCode() : 0;
            result = 31 * result + (phenotypeCenter != null ? phenotypeCenter.hashCode() : 0);
            result = 31 * result + (zygosity != null ? zygosity.hashCode() : 0);
            return result;
        }
    }
}
