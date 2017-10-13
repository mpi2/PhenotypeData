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

package org.mousephenotype.cda.loads.create.load.config;

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.PhenotypedColony;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.config.DataSourcesConfigApp;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.create.load.steps.ExperimentLoader;
import org.mousephenotype.cda.loads.create.load.steps.ImpressUpdater;
import org.mousephenotype.cda.loads.create.load.steps.SampleLoader;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 03/09/16.
 */
@Configuration
@Import(DataSourcesConfigApp.class)
public class LoadConfigBeans implements InitializingBean {

    private NamedParameterJdbcTemplate    jdbcCda;
    private NamedParameterJdbcTemplate    jdbcDcc;
    private NamedParameterJdbcTemplate    jdbcDccEurophenome;
    private StepBuilderFactory            stepBuilderFactory;
    private Map<String, GenomicFeature>   genesByAccMap;
    private Map<String, Allele>           allelesBySymbolMap;
    private Map<String, Strain>           strainsByNameMap;
    private Map<String, Integer>          cdaOrganisation_idMap;
    private Map<String, PhenotypedColony> phenotypedColonyMap;


    @Inject
    @Lazy
    public LoadConfigBeans(
            NamedParameterJdbcTemplate jdbcCda,
            NamedParameterJdbcTemplate jdbcDcc,
            NamedParameterJdbcTemplate jdbcDccEurophenome,
            StepBuilderFactory stepBuilderFactory
    ) {
        this.jdbcCda = jdbcCda;
        this.jdbcDcc = jdbcDcc;
        this.jdbcDccEurophenome = jdbcDccEurophenome;
        this.stepBuilderFactory = stepBuilderFactory;
    }



    @Override
    public void afterPropertiesSet() throws Exception {

        this.genesByAccMap = new ConcurrentHashMap<>(cdaSqlUtils().getGenes());
        this.allelesBySymbolMap = new ConcurrentHashMap<>(cdaSqlUtils().getAllelesBySymbol());
        this.strainsByNameMap = new ConcurrentHashMap<>(cdaSqlUtils().getStrainsByName());
        this.cdaOrganisation_idMap = cdaSqlUtils().getCdaOrganisation_idsByDccCenterId();
        this.phenotypedColonyMap = cdaSqlUtils().getPhenotypedColonies();

        Assert.notNull(jdbcCda, "jdbcCda must be set");
        Assert.notNull(jdbcDcc, "jdbcDcc must be set");
        Assert.notNull(jdbcDccEurophenome, "jdbcDccEurophenome must be set");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must be set");
        Assert.notNull(genesByAccMap, "genesByAccMap must be set");
        Assert.notNull(allelesBySymbolMap, "allelesBySymbolMap must be set");
        Assert.notNull(strainsByNameMap, "strainsByNameMap must be set");
        Assert.notNull(cdaOrganisation_idMap, "cdaOrganisation_idMap must be set");
        Assert.notNull(phenotypedColonyMap, "phenotypedColonyMap must be set");
    }


    @Bean
    public List<SampleLoader> sampleLoaders() {
        List<SampleLoader> sampleLoaders = new ArrayList<>();

        sampleLoaders.add(new SampleLoader(jdbcCda, stepBuilderFactory, cdaSqlUtils(), dccSqlUtils(), allelesBySymbolMap, cdaOrganisation_idMap, phenotypedColonyMap));
        sampleLoaders.add(new SampleLoader(jdbcCda, stepBuilderFactory, cdaSqlUtils(), dccEurophenomeSqlUtils(), allelesBySymbolMap, cdaOrganisation_idMap, phenotypedColonyMap));

        return sampleLoaders;
    }

    @Bean
    public SampleLoader sampleDccLoader() {
        return new SampleLoader(jdbcCda, stepBuilderFactory, cdaSqlUtils(), dccSqlUtils(), allelesBySymbolMap, cdaOrganisation_idMap, phenotypedColonyMap);
    }

    @Bean
    public ExperimentLoader experimentDccLoader() {
        return new ExperimentLoader(jdbcCda, stepBuilderFactory, cdaSqlUtils(), dccSqlUtils(), genesByAccMap, allelesBySymbolMap, strainsByNameMap, phenotypedColonyMap, cdaOrganisation_idMap);
    }

    @Bean
    public SampleLoader sampleDccEurophenomeLoader() {
        return new SampleLoader(jdbcCda, stepBuilderFactory, cdaSqlUtils(), dccEurophenomeSqlUtils(), allelesBySymbolMap, cdaOrganisation_idMap, phenotypedColonyMap);
    }

    @Bean
    public ExperimentLoader experimentDccEurophenomeLoader() {
        return new ExperimentLoader(jdbcCda, stepBuilderFactory, cdaSqlUtils(), dccEurophenomeSqlUtils(), genesByAccMap, allelesBySymbolMap, strainsByNameMap, phenotypedColonyMap, cdaOrganisation_idMap);
    }

    @Bean
    public ImpressUpdater impressUpdater() {

        ImpressUpdater impressUpdater = new ImpressUpdater(jdbcCda, stepBuilderFactory, cdaSqlUtils());

        return impressUpdater;
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda);
    }

    @Bean
    public DccSqlUtils dccSqlUtils() {
        return new DccSqlUtils(jdbcDcc);
    }

    @Bean
    public DccSqlUtils dccEurophenomeSqlUtils() {
        return new DccSqlUtils(jdbcDccEurophenome);
    }
}