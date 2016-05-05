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

package org.mousephenotype.cda.loads.cdaloader;

import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.loads.cdaloader.exception.CdaLoaderException;
import org.springframework.batch.item.ItemReader;

/**
 * Created by mrelac on 04/05/16.
 */
public class ResourceFileOntology extends ResourceFile {

    private OwlItemReader reader = null;
    private String prefix;

    public void initialise(String sourceFile, int dbId, String prefix) throws CdaLoaderException {
        initialise(sourceFile, dbId);
        this.prefix = prefix;

        reader = new OwlItemReader();
        reader.initialise(sourceFile, dbId, prefix);
    }

    @Override
    public void download() {

    }

    @Override
    public ItemReader<OntologyTerm> getItemReader() throws CdaLoaderException {
        return reader;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}