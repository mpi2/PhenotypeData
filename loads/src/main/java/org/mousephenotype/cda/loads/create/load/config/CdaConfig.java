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

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.common.config.DataSourceCdaConfig;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by mrelac on 03/09/16.
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration(exclude = {
        JndiConnectionFactoryAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
})
public class CdaConfig extends DataSourceCdaConfig {

    @Value("${datasource.dcc.jdbc-url}")
    private String dccUrl;

    @Value("${datasource.dcc.username}")
    private String dccUsername;

    @Value("${datasource.dcc.password}")
    private String dccPassword;

    
    // dcc database
    @Bean
    public DataSource dccDataSource() {
        return SqlUtils.getConfiguredDatasource(dccUrl, dccUsername, dccPassword);
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcDcc() {
        return new NamedParameterJdbcTemplate(dccDataSource());
    }

    @Bean
    public DccSqlUtils dccSqlUtils() {
        return new DccSqlUtils(jdbcDcc());
    }
}