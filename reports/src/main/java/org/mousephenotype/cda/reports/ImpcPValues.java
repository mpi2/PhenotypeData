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

/**
 * IMPC P-Value report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class ImpcPValues extends AbstractReport {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatisticalResultService statisticalResultService;

    public ImpcPValues() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ImpcPValuesReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<StatisticalResultDTO> resultDtoList = new ArrayList<>();

        try {
            resultDtoList = statisticalResultService.getImpcPvalues();
        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception in statisticalResultService.getImpcValues(): " + e.getLocalizedMessage());
        }

        log.info(String.format(" Getting %s rows [%s]", resultDtoList.size(), System.currentTimeMillis() - start));

        Map<RowKey, Map<String, Double>> matrixValues = new HashMap<>();
        Set<String> allParameters = new HashSet<>();

        Integer i = 0 ;
        for (StatisticalResultDTO result : resultDtoList) {

            i++;

            String parameter = result.getParameterName() + "(" + result.getParameterStableId() + ")";
            Double pvalue = result.getPValue();
            RowKey rowKey = new RowKey(result);

            if ( ! matrixValues.containsKey(rowKey)) {
                matrixValues.put(rowKey, new HashMap<>());
            }

            if (!matrixValues.get(rowKey).containsKey(parameter)) {
                matrixValues.get(rowKey).put(parameter, pvalue);

            } else if (pvalue != null && matrixValues.get(rowKey).get(parameter) != null && pvalue < matrixValues.get(rowKey).get(parameter)) {
                matrixValues.get(rowKey).put(parameter, pvalue);

            } else {
                matrixValues.get(rowKey).putIfAbsent(parameter, null);

            }

            allParameters.add(parameter.replace("\r\n", " ").replace("\n", " "));
        }

        log.info(String.format("%s records processed [%s]", i, commonUtils.msToHms(System.currentTimeMillis() - start)));

        List<String> sortedParameters = new ArrayList<>(allParameters);
        Collections.sort(sortedParameters);
        sortedParameters = Collections.unmodifiableList(sortedParameters);

        log.trace(" Parameter: " + org.apache.commons.lang3.StringUtils.join(sortedParameters, "\n Parameter: "));

        List<String> header = new ArrayList<>();
        header.addAll(Arrays.asList("Genotype", "Colony Id", "Gene Symbol", "MGI Gene Id", "Center"));
        header.addAll(sortedParameters);

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
            row.add(rowKey.center);

            for (String param : sortedParameters) {
                if (matrixValues.get(rowKey).containsKey(param)) {
                    String value = (matrixValues.get(rowKey).get(param) == null ? "" : matrixValues.get(rowKey).get(param).toString());
                    row.add(value);
                } else {
                    row.add("");
                }
            }

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
        String center;

        public RowKey(StatisticalResultDTO result) {
            this.genotype = result.getAlleleSymbol() + "-" + result.getZygosity();
            this.colonyId = result.getColonyId();
            this.geneSymbol = result.getMarkerSymbol();
            this.mgiAccessionId = result.getMarkerAccessionId();
            this.center = result.getPhenotypingCenter();
        }

        @Override
        public String toString() {
            return "RowKey{" +
                    "genotype='" + genotype + '\'' +
                    ", colonyId='" + colonyId + '\'' +
                    ", geneSymbol='" + geneSymbol + '\'' +
                    ", mgiAccessionId='" + mgiAccessionId + '\'' +
                    ", center='" + center + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RowKey rowKey = (RowKey) o;

            if (genotype != null ? !genotype.equals(rowKey.genotype) : rowKey.genotype != null) return false;
            if (colonyId != null ? !colonyId.equals(rowKey.colonyId) : rowKey.colonyId != null) return false;
            if (geneSymbol != null ? !geneSymbol.equals(rowKey.geneSymbol) : rowKey.geneSymbol != null)
                return false;
            return !(center != null ? !center.equals(rowKey.center) : rowKey.center != null);

        }

        @Override
        public int hashCode() {
            int result = genotype != null ? genotype.hashCode() : 0;
            result = 31 * result + (colonyId != null ? colonyId.hashCode() : 0);
            result = 31 * result + (geneSymbol != null ? geneSymbol.hashCode() : 0);
            result = 31 * result + (mgiAccessionId != null ? mgiAccessionId.hashCode() : 0);
            result = 31 * result + (center != null ? center.hashCode() : 0);
            return result;
        }
    }
}
