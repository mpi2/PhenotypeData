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
import org.mousephenotype.cda.loads.cdaloader.steps.itemreaders.OntologyItemReader;
import org.mousephenotype.cda.loads.cdaloader.steps.itemwriters.ResourceFileDbItemWriter;
import org.mousephenotype.cda.loads.cdaloader.steps.tasklets.RecreateAndLoadDbTables;
import org.mousephenotype.cda.loads.cdaloader.support.ResourceFileOntology;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public RecreateAndLoadDbTables recreateAndLoadDbTables() {
        return new RecreateAndLoadDbTables();
    }

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

//    @Autowired
//    @Qualifier("resourceFileDbItemWriter")
//    public ResourceFileDbItemWriter resourceFileDbItemWriter;


    @Bean(name = "resourceFileOntologyMa")
//    @StepScope
    public ResourceFileOntology resourceFileOntologyMa() throws CdaLoaderException {
        ResourceFileOntology resourceFileOntology = new ResourceFileOntology();
        String sourceUrl = "http://purl.obolibrary.org/obo/ma.owl";
        String filename = owlpath + "/ma.owl";
        int dbId = 8;
        String prefix = "MA";
        resourceFileOntology.initialise(sourceUrl, filename, dbId, prefix);

        return resourceFileOntology;
    }

    @Bean(name = "resourceFileOntologyMp")
//    @StepScope
    public ResourceFileOntology resourceFileOntologyMp() throws CdaLoaderException {
        ResourceFileOntology resourceFileOntology = new ResourceFileOntology();
        String sourceUrl = "ftp://ftp.informatics.jax.org/pub/reports/mp.owl";
        String filename = owlpath + "/mp.owl";
        int dbId = 5;
        String prefix = "MP";
        resourceFileOntology.initialise(sourceUrl, filename, dbId, prefix);

        return resourceFileOntology;
    }

    @Bean(name = "resourceFileDbItemWriter")
    @StepScope
    public ResourceFileDbItemWriter resourceFileDbItemWriter() {
        ResourceFileDbItemWriter writer = new ResourceFileDbItemWriter();

        return writer;
    }

    @Bean
    public Step loadMaStep() throws CdaLoaderException {

        String filename = owlpath + "/ma.owl";
        int dbId = 8;
        String prefix = "MA";

        OntologyItemReader ontologyReader = new OntologyItemReader();
        ontologyReader.initialise(filename, dbId, prefix);

        return stepBuilderFactory.get("loadMaStep")
                .chunk(10)
                .reader(ontologyReader)
                .writer(resourceFileDbItemWriter())
                .build();
    }

    @Bean
    public Step loadMpStep () throws CdaLoaderException {

        String filename = owlpath + "/mp.owl";
        int dbId = 5;
        String prefix = "MP";

        OntologyItemReader ontologyReader = new OntologyItemReader();
        ontologyReader.initialise(filename, dbId, prefix);

        return stepBuilderFactory.get("loadMpStep")
                .chunk(10)
                .reader(ontologyReader)
                .writer(resourceFileDbItemWriter())
                .build();
    }
}