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
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * IMPC P-Value report.
 *
 * Created by mrelac on 24/07/2015.
 */

// Not in public report set as of 13-Aug-2020
@Component
@Deprecated
public class ImpcPhenotypesProcedures extends AbstractReport {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatisticalResultService statisticalResultService;

    public ImpcPhenotypesProcedures() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ImpcPhenotypesProceduresReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<StatisticalResultDTO> resultDtoList;

        try {
            resultDtoList = statisticalResultService.getImpcPvaluesAndMpTerms();
        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception in statisticalResultService.getImpcPvaluesAndMpTerms(): " + e.getLocalizedMessage());
        }

        log.info(String.format(" Getting %s rows [%s]", resultDtoList.size(), System.currentTimeMillis() - start));

        Map<RowKey, Map<String, String>> matrixValues = new HashMap<>();
        Map<RowKey, Set<String>> matrixProceduresSet = new HashMap<>();

        Integer i = 0 ;
        for (StatisticalResultDTO result : resultDtoList) {

            i++;

            String parameter = result.getParameterName() + "(" + result.getParameterStableId() + ")";
            String mpTermName = result.getMpTermName();
            RowKey rowKey = new RowKey(result);

            if ( ! matrixProceduresSet.containsKey(rowKey)) {
                matrixProceduresSet.put(rowKey, new HashSet<>());
            }
            matrixProceduresSet.get(rowKey).addAll(result.getProcedureStableId());

            if ( ! matrixValues.containsKey(rowKey)) {
                matrixValues.put(rowKey, new HashMap<>());
            }

            if (!matrixValues.get(rowKey).containsKey(parameter)) {
                matrixValues.get(rowKey).put(parameter, mpTermName);

            } else if (mpTermName != null && matrixValues.get(rowKey).get(parameter) != null ) {
                matrixValues.get(rowKey).put(parameter, mpTermName);

            } else {
                matrixValues.get(rowKey).putIfAbsent(parameter, null);

            }

        }

        log.info(String.format("%s records processed [%s]", i, commonUtils.msToHms(System.currentTimeMillis() - start)));

        List<String> header = new ArrayList<>();
        header.addAll(Arrays.asList("Genotype", "Colony Id", "Gene Symbol", "MGI Gene Id", ", Zygosity", "Center", "Significant", "Procedures Complete"));

        csvWriter.write(header);

        i=0;
        start = System.currentTimeMillis();
        for (RowKey rowKey : matrixValues.keySet()) {

            i++;

            List<String> row = new ArrayList<>();
            row.add(rowKey.genotype);
            row.add(rowKey.colonyId);
            row.add(rowKey.geneSymbol);
            row.add(rowKey.mgiAccessionId);
            row.add(rowKey.zygosity);
            row.add(rowKey.center);

            // Add a pipe | separated list of significant MP terms
            Set<String> mpTerms = new TreeSet<>();
            if (matrixValues.get(rowKey)!=null) {
              mpTerms = matrixValues.get(rowKey).values()
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }
            row.add(String.join("|", mpTerms));

            // Add count of procedures for this rowKey has been tested
            row.add(String.valueOf(matrixProceduresSet.get(rowKey).size()));

            csvWriter.write(row);
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. %s rowKey records processed [%s]", i, commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    private class RowKey {
        String genotype;
        String colonyId;
        String geneSymbol;
        String mgiAccessionId;
        String zygosity;
        String center;

        public RowKey(StatisticalResultDTO result) {
            this.genotype = result.getAlleleSymbol();
            this.colonyId = result.getColonyId();
            this.geneSymbol = result.getMarkerSymbol();
            this.mgiAccessionId = result.getMarkerAccessionId();
            this.zygosity = result.getZygosity();
            this.center = result.getPhenotypingCenter();
        }

        @Override
        public String toString() {
            return "RowKey{" +
                    "genotype='" + genotype + '\'' +
                    ", colonyId='" + colonyId + '\'' +
                    ", geneSymbol='" + geneSymbol + '\'' +
                    ", mgiAccessionId='" + mgiAccessionId + '\'' +
                    ", zygosity='" + zygosity + '\'' +
                    ", center='" + center + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RowKey rowKey = (RowKey) o;
            return Objects.equals(genotype, rowKey.genotype) &&
                    Objects.equals(colonyId, rowKey.colonyId) &&
                    Objects.equals(geneSymbol, rowKey.geneSymbol) &&
                    Objects.equals(mgiAccessionId, rowKey.mgiAccessionId) &&
                    Objects.equals(zygosity, rowKey.zygosity) &&
                    Objects.equals(center, rowKey.center);
        }

        @Override
        public int hashCode() {

            return Objects.hash(genotype, colonyId, geneSymbol, mgiAccessionId, zygosity, center);
        }
    }
}
