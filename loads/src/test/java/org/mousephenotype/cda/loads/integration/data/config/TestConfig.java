/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.integration.data.config;

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.PhenotypedColony;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.loads.common.config.DataSourcesConfigApp;
import org.mousephenotype.cda.loads.create.extract.dcc.ExtractDccExperiments;
import org.mousephenotype.cda.loads.create.extract.dcc.ExtractDccSpecimens;
import org.mousephenotype.cda.loads.create.load.LoadExperiments;
import org.mousephenotype.cda.loads.create.load.LoadSpecimens;
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
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 02/05/2017.
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
public class TestConfig extends DataSourcesConfigApp implements InitializingBean {

    private NamedParameterJdbcTemplate    jdbcCda;
    private NamedParameterJdbcTemplate    jdbcDcc;
    private NamedParameterJdbcTemplate    jdbcDccEurophenome;
    private StepBuilderFactory            stepBuilderFactory;
    private Map<String, Allele>           allelesBySymbolMap;
    private Map<String, Integer>          cdaOrganisation_idMap;
    private Map<String, GenomicFeature>   genesByAccMap;
    private Map<String, PhenotypedColony> phenotypedColonyMap;
    private Map<String, Strain>           strainsByNameMap;


    @Lazy
    @Inject
    public TestConfig(
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








    // cda_base database
    @Bean
    public DataSource cdabaseDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("cda_base_test")
                .addScripts("sql/h2/cda/schema.sql")
                .build();
    }


    // cda database
    @Bean
    public DataSource cdaDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("cda_test")
                .addScripts("sql/h2/cda/schema.sql",
                            "sql/h2/impress/impressSchema.sql",
                            "sql/h2/dataIntegrationTest-data.sql")
                .build();
    }


    // dcc database
    @Bean
    public DataSource dccDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("dcc_test")
                .addScripts("sql/h2/dcc/createSpecimen.sql", "sql/h2/dcc/createExperiment.sql")
                .build();
    }



    // dcc_europhenome database
    @Bean
    public DataSource dccEurophenomeDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("dcc_europhenome_test")
//                .addScripts("sql/h2/dcc/createSpecimen.sql", "sql/h2/dcc/createExperiment.sql")
                .build();
    }

    @Bean
    public ExtractDccSpecimens extractDccSpecimens() {
        return new ExtractDccSpecimens(dccDataSource(), dccSqlUtils());
    }

    @Bean
    public ExtractDccExperiments extractDccExperiments() {
        return new ExtractDccExperiments(dccDataSource(), dccSqlUtils());
    }

    @Bean
    public LoadSpecimens loadSamples() {
        return new LoadSpecimens(jdbcCda, cdaSqlUtils(), dccSqlUtils(), allelesBySymbolMap, cdaOrganisation_idMap, phenotypedColonyMap);
    }

    @Bean
    public LoadExperiments loadExperiments() {
        return new LoadExperiments(jdbcCda, cdaSqlUtils(), dccSqlUtils(), allelesBySymbolMap, cdaOrganisation_idMap, genesByAccMap, phenotypedColonyMap, strainsByNameMap);
    }
}