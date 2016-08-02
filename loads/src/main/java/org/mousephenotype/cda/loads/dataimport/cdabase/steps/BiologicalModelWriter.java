/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.dataimport.cdabase.steps;

import org.mousephenotype.cda.loads.dataimport.cdabase.support.CdabaseSqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 26/04/16.
 */
public class BiologicalModelWriter implements ItemWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, Integer> written = new HashMap<>();

    @Autowired
    @Qualifier("cdabaseLoaderUtils")
    private CdabaseSqlUtils cdabaseSqlUtils;

    public BiologicalModelWriter() {

        written.put("bioModels", 0);
        written.put("bioModelAlleles", 0);
        written.put("bioModelGenomicFeatures", 0);
        written.put("bioModelPhenotypes", 0);
    }


    /**
     * Process the supplied data element. Will not be called with any null items
     * in normal operation.
     *
     * @param items items to be written
     * @throws Exception if there are errors. The framework will catch the
     *                   exception and convert or rethrow it as appropriate.
     */
    @Override
    public void write(List items) throws Exception {
        Map<String, Integer>      counts   = cdabaseSqlUtils.insertBioModel(items);
        written.put("bioModels", written.get("bioModels") + counts.get("bioModels"));
        written.put("bioModelAlleles", written.get("bioModelAlleles") + counts.get("bioModelAlleles"));
        written.put("bioModelGenomicFeatures", written.get("bioModelGenomicFeatures") + counts.get("bioModelGenomicFeatures"));
        written.put("bioModelPhenotypes", written.get("bioModelPhenotypes") + counts.get("bioModelPhenotypes"));
    }

    public int getWrittenBioModels() {
        return written.get("bioModels");
    }
    public int getWrittenBioModelAlleles() {
        return written.get("bioModelAlleles");
    }
    public int getWrittenBioModelGenomicFeatures() {
        return written.get("bioModelGenomicFeatures");
    }
    public int getWrittenBioModelPhenotypes() {
        return written.get("bioModelPhenotypes");
    }
}