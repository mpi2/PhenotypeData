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
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.*;

/**
 * IMPC P-Value report.
 *
 * Created by mrelac on 24/07/2015.
 */
@SpringBootApplication
@Component
public class ImpcPValuesReport extends AbstractReport {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatisticalResultService statisticalResultService;

    public ImpcPValuesReport() {
        super();
    }

    public static void main(String args[]) {
        SpringApplication.run(ImpcPValuesReport.class, args);
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass().getSuperclass()));
    }

    @Override
    public void run(String[] args) throws ReportException {
        initialise(args);

        long start = System.currentTimeMillis();

        List<StatisticalResultDTO> resultDtoList = new ArrayList<>();

        try {
            resultDtoList = statisticalResultService.getImpcPvalues();
        } catch (SolrServerException e) {
            throw new ReportException("Exception in statisticalResultService.getImpcValues(): " + e.getLocalizedMessage());
        }

        log.info(String.format(" Getting %s rows [%s]", resultDtoList.size(), System.currentTimeMillis() - start));

        Map<RowKey, Map<String, Double>> matrixValues = new HashMap<>();
        Set<String> allParameters = new HashSet<>();

        Integer i = 0 ;
        for (StatisticalResultDTO result : resultDtoList) {

            if ( i % 100000 == 0) {
                log.info(String.format(" %s records processed [%s]", i, System.currentTimeMillis() - start));
            }
            i++;
            String parameter = result.getParameterName() + "(" + result.getParameterStableId() + ")";
            Double pvalue = result.getpValue();
            RowKey rowKey = new RowKey(result);

            if ( ! matrixValues.containsKey(rowKey)) {
                matrixValues.put(rowKey, new HashMap<String, Double>());
            }

            if ( ! matrixValues.get(rowKey).containsKey(parameter)) {

                matrixValues.get(rowKey).put(parameter, pvalue);

            } else if (pvalue < matrixValues.get(rowKey).get(parameter)) {
                matrixValues.get(rowKey).put(parameter, pvalue);

            }
            allParameters.add(parameter.replace("\r\n", " ").replace("\n", " "));
        }

        log.info(String.format(" Found %s rows", matrixValues.keySet().size()));

        List<String> sortedParameters = new ArrayList<>(allParameters);
        Collections.sort(sortedParameters);
        sortedParameters = Collections.unmodifiableList(sortedParameters);

        log.info(" Parameter: " + org.apache.commons.lang3.StringUtils.join(sortedParameters, "\n Paramter: "));

        List<String> header = new ArrayList<>();
        header.addAll(Arrays.asList("Genotype", "ColonyId", "Gene", "Center"));
        header.addAll(sortedParameters);

        csvWriter.writeNext(header);

        i=0;
        for (RowKey rowKey : matrixValues.keySet()) {

            if (i%100==0) {
                log.info(String.format(" %s rowKey records processed [%s]", i, commonUtils.msToHms(System.currentTimeMillis() - start)));
            }
            i++;

            List<String> row = new ArrayList<>();
            row.add(rowKey.genotype);
            row.add(rowKey.colonyId);
            row.add(rowKey.markerSymbol);
            row.add(rowKey.center);

            for (String param : sortedParameters) {
                if (matrixValues.get(rowKey).containsKey(param)) {
                    row.add(matrixValues.get(rowKey).get(param).toString());
                } else {
                    row.add("");
                }
            }

            csvWriter.writeNext(row);
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. %s rowKey records processed [%s]", i, System.currentTimeMillis() - start));
    }

    private class RowKey {
        String genotype;
        String colonyId;
        String markerSymbol;
        String center;

        public RowKey(StatisticalResultDTO result) {
            this.genotype = result.getAlleleSymbol() + "-" + result.getZygosity();
            this.colonyId = result.getColonyId();
            this.markerSymbol = result.getMarkerSymbol();
            this.center = result.getPhenotypingCenter();
        }

        @Override
        public String toString() {
            return "RowKey{" +
                    "genotype='" + genotype + '\'' +
                    ", colonyId='" + colonyId + '\'' +
                    ", markerSymbol='" + markerSymbol + '\'' +
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
            if (markerSymbol != null ? !markerSymbol.equals(rowKey.markerSymbol) : rowKey.markerSymbol != null)
                return false;
            return !(center != null ? !center.equals(rowKey.center) : rowKey.center != null);

        }

        @Override
        public int hashCode() {
            int result = genotype != null ? genotype.hashCode() : 0;
            result = 31 * result + (colonyId != null ? colonyId.hashCode() : 0);
            result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
            result = 31 * result + (center != null ? center.hashCode() : 0);
            return result;
        }
    }
}
