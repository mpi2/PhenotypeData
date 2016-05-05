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

package org.mousephenotype.cda.loads.cdaloader.configs;

import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.steps.itemwriters.ResourceFileItemWriter;
import org.mousephenotype.cda.loads.cdaloader.steps.tasklets.RecreateAndLoadDbTables;
import org.mousephenotype.cda.loads.cdaloader.support.ResourceFileOntology;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.validation.constraints.NotNull;

/**
 * Created by mrelac on 03/05/16.
 */
@Configuration
public class ConfigBeans {

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;

    @Bean(name = "recreateAndLoadDbTables")
    @StepScope
    public RecreateAndLoadDbTables recreateAndLoadDbTables() {
        return new RecreateAndLoadDbTables();
    }

    @Bean(name = "resourceFileOntologyMa")
    @StepScope
    public ResourceFileOntology resourceFileOntologyMa() throws CdaLoaderException {
        ResourceFileOntology resourceFileOntology = new ResourceFileOntology();
        String sourceUrl = "http://purl.obolibrary.org/obo/ma.owl";
        String filename = owlpath + "/ma.owl";
        int dbId = 8;
        String prefix = "MA";
        resourceFileOntology.initialise(sourceUrl, filename, dbId, prefix);

        return resourceFileOntology;
    }

    @Bean(name = "resourceFileItemWriter")
    @StepScope
    public ResourceFileItemWriter resourceFileItemWriter() {
        ResourceFileItemWriter writer = new ResourceFileItemWriter();
        writer.setLineAggregator(new PassThroughLineAggregator<>());
        String sourcePath = "/tmp/loadOntologies.log";
        writer.setResource(new FileSystemResource(sourcePath));

        return writer;
    }
}