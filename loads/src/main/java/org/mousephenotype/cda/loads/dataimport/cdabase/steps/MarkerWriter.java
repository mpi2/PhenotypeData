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

import org.mousephenotype.cda.db.utilities.SqlUtils;
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
public class MarkerWriter implements ItemWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("cdabaseLoaderUtils")
    private CdabaseSqlUtils cdabaseSqlUtils;

    private SqlUtils             sqlUtils = new SqlUtils();
    private Map<String, Integer> written  = new HashMap<>();

    public MarkerWriter() {

        written.put("genes", 0);
        written.put("synonyms", 0);
        written.put("xrefs", 0);
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
        Map<String, Integer> counts = cdabaseSqlUtils.insertGenes(items);
        written.put("genes", written.get("genes") + counts.get("genes"));
        written.put("synonyms", written.get("synonyms") + counts.get("synonyms"));
        written.put("xrefs", written.get("xrefs") + counts.get("xrefs"));
    }

    public int getWrittenGenes() {
        return written.get("genes");
    }

    public int getWrittenSynonyms() {
        return written.get("synonyms");
    }

    public int getWrittenXrefs() {
        return written.get("xrefs");
    }
}