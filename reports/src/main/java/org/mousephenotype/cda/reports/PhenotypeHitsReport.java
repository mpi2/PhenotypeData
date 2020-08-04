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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Phenotype Hits report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class PhenotypeHitsReport extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StatisticalResultService statisticalResultService;

    public PhenotypeHitsReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("PhenotypeHitsReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<List<String[]>> result = new ArrayList<>();

        Float pVal = (float) 0.0001;
        TreeMap<String, Long> significant = statisticalResultService.getDistributionOfAnnotationsByMPTopLevel(resources, pVal);
        TreeMap<String, Long> all = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        all.putAll(statisticalResultService.getDistributionOfAnnotationsByMPTopLevel(resources, null));

        List<String[]> table = new ArrayList<>();
        String[] header = new String[4];
        header[0] = "Top Level MP Term";
        header[1] = "No. Significant Calls";
        header[2] = "No. Not Significant Calls";
        header[3] = "% Significant Calls";
        table.add(header);

        for (String mp : all.keySet()){
            if (!mp.equalsIgnoreCase("reproductive system phenotype")){ // line data is not in statistical result core yet
                String[] row = new String[4];
                row[0] = mp;
                Long sign = (long) 0;
                if (significant.containsKey(mp)){
                    sign = significant.get(mp);
                }
                row[1] = sign.toString();
                long notSignificant = all.get(mp) - sign;
                row[2] = Long.toString(notSignificant);
                float percentage =  100 * ((float)sign / (float)all.get(mp));
                row[3] = (Float.toString(percentage));
                table.add(row);
            }
        }

        result.add(new ArrayList<>(table));

        table = new ArrayList<>();
        String[] headerLines = new String[4];
        headerLines[0] = "Top Level MP Term";
        headerLines[1] = "Lines Associated";
        headerLines[2] = "Lines Tested";
        headerLines[3] = "% Lines Associated";
        table.add(headerLines);

        try {
            Map<String, List<String>> genesSignificantMp = statisticalResultService.getDistributionOfLinesByMPTopLevel(resources, pVal);
            TreeMap<String, List<String>> genesAllMp = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            genesAllMp.putAll(statisticalResultService.getDistributionOfLinesByMPTopLevel(resources, null));

            for (String mp : genesAllMp.keySet()){
                if (!mp.equalsIgnoreCase("reproductive system phenotype")){
                    String[] row = new String[4];
                    row[0] = mp;
                    int sign = 0;
                    if (genesSignificantMp.containsKey(mp)){
                        sign = genesSignificantMp.get(mp).size();
                    }
                    row[1] = Integer.toString(sign);
                    row[2] = Integer.toString(genesAllMp.get(mp).size());
                    float percentage =  100 * ((float)sign / (float)genesAllMp.get(mp).size());
                    row[3] = (Float.toString(percentage));
                    table.add(row);
                }
            }
        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        result.add(new ArrayList<>(table));

        table = new ArrayList<>();
        String[] headerGenes = new String[4];
        headerGenes[0] = "Top Level MP Term";
        headerGenes[1] = "Genes Associated";
        headerGenes[2] = "Genes Tested";
        headerGenes[3] = "% Associated";
        table.add(headerGenes);

        try {
            Map<String, List<String>> genesSignificantMp = statisticalResultService.getDistributionOfGenesByMPTopLevel(resources, pVal);
            TreeMap<String, List<String>> genesAllMp = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            genesAllMp.putAll(statisticalResultService.getDistributionOfGenesByMPTopLevel(resources, null));

            for (String mp : genesAllMp.keySet()){
                if (!mp.equalsIgnoreCase("reproductive system phenotype")){
                    String[] row = new String[4];
                    row[0] = mp;
                    int sign = 0;
                    if (genesSignificantMp.containsKey(mp)){
                        sign = genesSignificantMp.get(mp).size();
                    }
                    row[1] = Integer.toString(sign);
                    row[2] = Integer.toString(genesAllMp.get(mp).size());
                    float percentage =  100 * ((float)sign / (float)genesAllMp.get(mp).size());
                    row[3] = (Float.toString(percentage));
                    table.add(row);
                }
            }

            result.add(new ArrayList<>(table));
            csvWriter.writeRowsMulti(result);

        } catch (SolrServerException | IOException  e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}