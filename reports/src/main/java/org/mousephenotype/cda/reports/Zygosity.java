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
 * Zygosity report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class Zygosity extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

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

    public static final String[] EMPTY_ROW = new String[]{""};

    public Zygosity() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ZygosityReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<String[]> result = new ArrayList<>();
        Map<Pair<String, ZygosityType>, Set<String>> mps = new TreeMap<>();

        Map<GeneCenterZygosity, List<String>> data = new HashMap<>();
        Map<GeneCenterZygosity, List<String>> viabilityData = new HashMap<>();

        String[] headerParams = {"MP Term", "Zygosity", "# Genes", "Gene Symbol (Gene Accession Id)"};
        result.add(headerParams);

        int mpTermCount = 0;
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
                        mps.put(k, new HashSet<String>());
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
                    GeneDTO geneDTO = geneService.getGeneByGeneSymbolWithLimitedFields(gene);

                    if ((geneDTO != null) && (geneDTO.getMgiAccessionId() != null) && ( ! geneDTO.getMgiAccessionId().isEmpty())) {
                        mpsSet.add(gene + "(" + geneDTO.getMgiAccessionId() + ")");
                    } else {
                        mpsSet.add(gene);
                    }
                }

                String[] row = {mpSymbol, zygosity, Integer.toString(mps.get(k).size()), StringUtils.join(mpsSet, "|")};
                result.add(row);
                mpTermCount++;
            }

            result.add(EMPTY_ROW);

            // Get the viability data from the experiment core directly
            for (ObservationDTO obs : observationService.getObservationsByParameterStableId("IMPC_VIA_001_001")) {


                // Skip records that are not for the resources we are interested in
                if (!resources.contains(obs.getDataSourceName())) {
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

            String[] resetHeaderParams = {"Gene Symbol", "Gene Accession Id", "Phenotyping Center", "Viability", "Hom", "Het", "Hemi", "Gene Page URL"};
            result.add(resetHeaderParams);

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

                GeneDTO gene = geneService.getGeneByGeneSymbolWithLimitedFields(geneSymbol);

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
                    if (gene != null) {
                        geneLink = cmsBaseUrl + "/data/genes/" + gene.getMgiAccessionId();
                        if (geneLink.startsWith("//")) {
                            geneLink = "http:" + geneLink;
                        }
                    }

                    String mgiGeneId = ((gene != null) && (gene.getMgiAccessionId() != null) ? gene.getMgiAccessionId() : "");
                    String[] row = {geneSymbol, mgiGeneId, center, StringUtils.join(via, ": "), homCount, hetCount, hemiCount, geneLink};
                    if (include)
                        result.add(row);
                }
            }

            result = result
                .stream()
                .sorted(Comparator.comparing((String[] l) -> l[0]))
                .collect(Collectors.toList());

            csvWriter.writeRowsOfArray(result);

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format(
            "Finished. %s MP term rows written and %s Gene rows written in %s",
            mpTermCount, result.size(), commonUtils.msToHms(System.currentTimeMillis() - start)));
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
