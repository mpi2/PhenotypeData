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
import org.mousephenotype.cda.constants.Constants;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hits Per Line report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class HitsPerLineReport extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GenotypePhenotypeService genotypePhenotypeService;

    @Autowired
    StatisticalResultService statisticalResultService;

    public HitsPerLineReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("HitsPerLineReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        // TODO refactor to pivot facet on zygosity, colony_id (this order) => 1 call instead of 2
        //Columns:		parameter name | parameter stable id | number of significant hits
        List<List<String[]>> result = new ArrayList<>();
        try {
            List<String[]> zygosityTable = new ArrayList<>();
            String[] headerParams  ={"# Hits", "# Colonies With This Many HOM Hits", "# Colonies With This Many HET Hits", "# Colonies With This Many Calls"};
            zygosityTable.add(headerParams);

            Map<String, Long> homsMap = genotypePhenotypeService.getHitsDistributionBySomethingNoIds(GenotypePhenotypeDTO.COLONY_ID, resources, ZygosityType.homozygote, 1, Constants.P_VALUE_THRESHOLD);
            Map<String, Long> hetsMap = genotypePhenotypeService.getHitsDistributionBySomethingNoIds(GenotypePhenotypeDTO.COLONY_ID, resources, ZygosityType.heterozygote, 1, Constants.P_VALUE_THRESHOLD);
            Map<String, Long> allMap = genotypePhenotypeService.getHitsDistributionBySomethingNoIds(GenotypePhenotypeDTO.COLONY_ID, resources, null, 1, Constants.P_VALUE_THRESHOLD);

            Map<String, Long> homsNoHits = statisticalResultService.getColoniesNoMPHit(resources, ZygosityType.homozygote);
            Map<String, Long> hetsNoHits = statisticalResultService.getColoniesNoMPHit(resources, ZygosityType.heterozygote);
            Map<String, Long> allNoHits = statisticalResultService.getColoniesNoMPHit(resources, null);

            HashMap<Long, Integer> homRes = new HashMap<>();
            HashMap<Long, Integer> hetRes = new HashMap<>();
            HashMap<Long, Integer> allRes = new HashMap<>();

            long maxHitsPerColony = 0;

            for (String colony: homsMap.keySet()){
                if (homsNoHits.containsKey(colony)){
                    homsNoHits.remove(colony);
                }
                if (allNoHits.containsKey(colony)){
                    allNoHits.remove(colony);
                }
                long count = homsMap.get(colony);
                if (homRes.containsKey(count)){
                    homRes.put(count, homRes.get(count) + 1);
                } else {
                    homRes.put(count, 1);
                    if (count > maxHitsPerColony){
                        maxHitsPerColony = count;
                    }
                }
            }
            for (String colony: hetsMap.keySet()){
                if (hetsNoHits.containsKey(colony)){
                    hetsNoHits.remove(colony);
                }
                if (allNoHits.containsKey(colony)){
                    allNoHits.remove(colony);
                }
                long count = hetsMap.get(colony);
                if (hetRes.containsKey(count)){
                    hetRes.put(count, hetRes.get(count) + 1);
                } else {
                    hetRes.put(count, 1);
                    if (count > maxHitsPerColony){
                        maxHitsPerColony = count;
                    }
                }
            }
            int tempI = 0;
            for (String colony: allMap.keySet()){
                if (allNoHits.containsKey(colony)){
                    allNoHits.remove(colony);
                }
                long count = allMap.get(colony);
                if (allRes.containsKey(count)){
                    tempI++;
                    allRes.put(count, allRes.get(count) + 1);
                } else {
                    allRes.put(count, 1);
                    tempI++;
                    if (count > maxHitsPerColony){
                        maxHitsPerColony = count;
                    }
                }
            }

            homRes.put(Long.parseLong("0"), homsNoHits.size());
            hetRes.put(Long.parseLong("0"), hetsNoHits.size());
            allRes.put(Long.parseLong("0"), allNoHits.size());

            long iterator = 0;

            while (iterator <= maxHitsPerColony){
                String[] row = {Long.toString(iterator), Long.toString(homRes.containsKey(iterator) ? homRes.get(iterator) : 0),
                        Long.toString(hetRes.containsKey(iterator) ? hetRes.get(iterator) : 0), Long.toString(allRes.containsKey(iterator) ? allRes.get(iterator) : 0)};
                zygosityTable.add(row);
                iterator += 1;
            }

            result.add(zygosityTable);
            csvWriter.writeAllMulti(result);

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}