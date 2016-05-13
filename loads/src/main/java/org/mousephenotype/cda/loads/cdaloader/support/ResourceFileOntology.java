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

package org.mousephenotype.cda.loads.cdaloader.support;

import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.steps.itemreaders.OntologyItemReader;
import org.mousephenotype.cda.loads.cdaloader.steps.itemwriters.ResourceFileDbItemWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by mrelac on 04/05/16.
 */
public class ResourceFileOntology extends ResourceFile {

    @Autowired
    @Qualifier("resourceFileDbItemWriter")
    public ResourceFileDbItemWriter ontologyWriter;
//    public FlatFileItemWriter ontologyWriter;

    private OntologyItemReader ontologyReader = null;

    /**
     * Initialise a new <code>ResourceFileOntology</code> instance
     * @param sourceUrl the resource source url
     * @param filename the fully qualified filename where the resource will be downloaded to and from which the <code>
     *                 ItemReader</code> will read
     * @param dbId the ontology identifier (from the komp2 table 'external_db')
     * @param prefix the <code>OntologyParser</code> OWL prefix (e.g. PATO, MA, MP, etc.)
     */
    public void initialise(String sourceUrl, String filename, int dbId, String prefix) throws CdaLoaderException {
        super.initialise(sourceUrl, filename);

        ontologyReader = new OntologyItemReader();
        ontologyReader.initialise(filename, dbId, prefix);
    }

    @Override
    @StepScope
    public Step getLoadStep() throws CdaLoaderException {

        return stepBuilderFactory.get("loadOntologyStep")
                .chunk(10)
                .reader(ontologyReader)
                .writer(ontologyWriter)
                .build();
    }
}