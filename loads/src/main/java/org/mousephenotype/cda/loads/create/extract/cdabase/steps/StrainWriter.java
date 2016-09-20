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

package org.mousephenotype.cda.loads.create.extract.cdabase.steps;

import org.mousephenotype.cda.loads.common.CdaSqlUtils;
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

// For more info on ScopedProxyMode, see https://shekhargulati.com/2010/10/30/spring-scoped-proxy-beans-an-alternative-to-method-injection/
public class StrainWriter implements ItemWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("cdabaseSqlUtils")
    private CdaSqlUtils cdaSqlUtils;
    private Map<String, Integer> written = new HashMap<>();

        public StrainWriter() {

            written.put("strains", 0);
            written.put("synonyms", 0);
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
        Map<String, Integer> counts = cdaSqlUtils.insertStrains(items);
        written.put("strains", written.get("strains") + counts.get("strains"));
        written.put("synonyms", written.get("synonyms") + counts.get("synonyms"));
    }

    public int getWrittenStrains() {
        return written.get("strains");
    }

    public int getWrittenSynonyms() {
        return written.get("synonyms");
    }
}