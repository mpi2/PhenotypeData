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
import org.mousephenotype.cda.loads.common.config.DataSourcesConfigApp;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 03/09/16.
 */
@Configuration
@EnableAutoConfiguration(exclude = {
        JndiConnectionFactoryAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        Neo4jDataAutoConfiguration.class
})
public class LoadConfigBeans extends DataSourcesConfigApp implements InitializingBean {

    private NamedParameterJdbcTemplate    jdbcCda;
    private NamedParameterJdbcTemplate    jdbcDcc;
    private NamedParameterJdbcTemplate    jdbcDccEurophenome;
    private StepBuilderFactory            stepBuilderFactory;
    private Map<String, Allele>           allelesBySymbolMap;
    private Map<String, Integer>          cdaOrganisation_idMap;
    private Map<String, GenomicFeature>   genesByAccMap;
    private Map<String, PhenotypedColony> phenotypedColonyMap;
    private Map<String, Strain>           strainsByNameMap;


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

        this.allelesBySymbolMap = new ConcurrentHashMap<>(cdaSqlUtils().getAllelesBySymbol());
        this.cdaOrganisation_idMap = cdaSqlUtils().getCdaOrganisation_idsByDccCenterId();
        this.genesByAccMap = new ConcurrentHashMap<>(cdaSqlUtils().getGenes());
        this.phenotypedColonyMap = cdaSqlUtils().getPhenotypedColonies();
        this.strainsByNameMap = new ConcurrentHashMap<>(cdaSqlUtils().getStrainsByName());

        Assert.notNull(jdbcCda, "jdbcCda must be set");
        Assert.notNull(jdbcDcc, "jdbcDcc must be set");
        Assert.notNull(jdbcDccEurophenome, "jdbcDccEurophenome must be set");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must be set");
        Assert.notNull(allelesBySymbolMap, "allelesBySymbolMap must be set");
        Assert.notNull(cdaOrganisation_idMap, "cdaOrganisation_idMap must be set");
        Assert.notNull(genesByAccMap, "genesByAccMap must be set");
        Assert.notNull(phenotypedColonyMap, "phenotypedColonyMap must be set");
        Assert.notNull(strainsByNameMap, "strainsByNameMap must be set");
    }


    @Bean
    Map<String, Allele> getAllelesBySymbolMap() {
        return allelesBySymbolMap;
    }

    @Bean
    Map<String, Integer> cdaOrganisation_idMap() {
        return cdaOrganisation_idMap;
    }

    @Bean
    Map<String, GenomicFeature> genesByAccMap() {
        return genesByAccMap;
    }

    @Bean
    Map<String, PhenotypedColony> phenotypedColonyMap() {
        return phenotypedColonyMap;
    }

    @Bean
    Map<String, Strain> strainsByNameMap() {
        return strainsByNameMap;
    }
}