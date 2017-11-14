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

import org.mousephenotype.cda.loads.common.config.DataSourcesConfigApp;
import org.mousephenotype.cda.loads.create.extract.dcc.ExtractDccExperiments;
import org.mousephenotype.cda.loads.create.extract.dcc.ExtractDccSpecimens;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

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
public class TestConfig extends DataSourcesConfigApp {


    // cda_base database
    @Bean
    public DataSource cdabaseDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("cda_base_test")
                .addScripts("sql/h2/cdabase/schema.sql")
                .build();
    }


    // cda database
    @Bean
    public DataSource cdaDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("cda_test")
                .addScripts("sql/h2/cdabase/schema.sql")
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

    @Bean
    public ExtractDccSpecimens extractDccSpecimens() {
        return new ExtractDccSpecimens(dccDataSource(), dccSqlUtils());
    }

    @Bean
    public ExtractDccExperiments extractDccExperiments() {
        return new ExtractDccExperiments(dccDataSource(), dccSqlUtils());
    }
}