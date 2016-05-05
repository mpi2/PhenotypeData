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

package org.mousephenotype.cda.loads.cdaloader.config;

import org.mousephenotype.cda.loads.cdaloader.ResourceFileOntology;
import org.mousephenotype.cda.loads.cdaloader.exception.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.step.DownloadResourceFiles;
import org.mousephenotype.cda.loads.cdaloader.step.RecreateAndLoadDbTables;
import org.springframework.batch.core.configuration.annotation.StepScope;
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

    @Bean(name = "downloadResourceFiles")
//    @StepScope
    public DownloadResourceFiles downloadResourceFiles() {
        return new DownloadResourceFiles();
    }

    @Bean(name = "recreateAndLoadDbTables")
    @StepScope
    public RecreateAndLoadDbTables recreateAndLoadDbTables() {
        return new RecreateAndLoadDbTables();
    }

    @Bean(name = "resourceFileOntologyMa")
//    @StepScope
    public ResourceFileOntology resourceFileOntologyMa() throws CdaLoaderException {
        ResourceFileOntology resourceFileOntology = new ResourceFileOntology();
        String sourceFile = owlpath + "/ma.owl";
        int dbId = 8;
        String prefix = "MA:";
        resourceFileOntology.initialise(sourceFile, dbId, prefix);

        return resourceFileOntology;
    }
}