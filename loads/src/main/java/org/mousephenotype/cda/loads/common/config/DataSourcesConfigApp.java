/*******************************************************************************
 * Copyright © 2016 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.common.config;

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import javax.sql.DataSource;

@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile}/datarelease.properties")
@EnableAutoConfiguration(exclude = {
        JndiConnectionFactoryAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        Neo4jDataAutoConfiguration.class
        })
@Lazy
/**
 * This configuration class holds configuration information shared by the data load create process.
 *
 * Created by mrelac on 18/08/2016.
 */
public class DataSourcesConfigApp {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    final private Integer INITIAL_POOL_CONNECTIONS = 1;

    @Value("${datasource.cda.url}")
    String cdaUrl;

    @Value("${datasource.cda.username}")
    String cdaUsername;

    @Value("${datasource.cda.password}")
    String cdaPassword;

    @Value("${datasource.dccEurophenomeFinal.url}")
    String dccEurophenomeFinalUrl;

    @Value("${datasource.dccEurophenomeFinal.username}")
    String dccEurophenomeFinalUsername;

    @Value("${datasource.dccEurophenomeFinal.password}")
    String dccEurophenomeFinalPassword;

    @Value("${datasource.cdabase.url}")
    String cdabaseUrl;

    @Value("${datasource.cdabase.username}")
    String cdabaseUsername;

    @Value("${datasource.cdabase.password}")
    String cdabasePassword;

    @Value("${datasource.dcc.url}")
    String dccUrl;

    @Value("${datasource.dcc.username}")
    String dccUsername;

    @Value("${datasource.dcc.password}")
    String dccPassword;



    private DataSource getConfiguredDatasource(String url, String username, String password) {
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setInitialSize(INITIAL_POOL_CONNECTIONS);
        ds.setMaxActive(50);
        ds.setMinIdle(INITIAL_POOL_CONNECTIONS);
        ds.setMaxIdle(INITIAL_POOL_CONNECTIONS);
        ds.setTestOnBorrow(true);
        ds.setValidationQuery("SELECT 1");
        ds.setValidationInterval(5000);
        ds.setMaxAge(30000);
        ds.setMaxWait(35000);
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(5000);
        ds.setMinEvictableIdleTimeMillis(5000);
        ds.setValidationInterval(30000);
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(10000); // 10 seconds before abandoning a query

        try {
            logger.info("Using cdasource database {} with initial pool size {}. URL: {}", ds.getConnection().getCatalog(), ds.getInitialSize(), url);

        } catch (Exception e) {

            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

        return ds;
    }


    @Bean(name = "sessionFactoryHibernate")
    @Primary
    public SessionFactory getSessionFactory() {

        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(cdabaseDataSource());
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

        return sessionBuilder.buildSessionFactory();
    }



    // cda_base database
    @Bean
    public DataSource cdabaseDataSource() {
        return getConfiguredDatasource(cdabaseUrl, cdabaseUsername, cdabasePassword);
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCdabase() {
        return new NamedParameterJdbcTemplate(cdabaseDataSource());
    }

    @Bean
    public CdaSqlUtils cdabaseSqlUtils() {
        return new CdaSqlUtils(jdbcCdabase());
    }



    // cda database
    @Bean
    @Primary
    public DataSource cdaDataSource() {
        return getConfiguredDatasource(cdaUrl, cdaUsername, cdaPassword);
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCda() {
        return new NamedParameterJdbcTemplate(cdaDataSource());
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda());
    }


    // dcc database
    @Bean
    public DataSource dccDataSource() {
        return getConfiguredDatasource(dccUrl, dccUsername, dccPassword);
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcDcc() {
        return new NamedParameterJdbcTemplate(dccDataSource());
    }

    @Bean
    public DccSqlUtils dccSqlUtils() {
        return new DccSqlUtils(jdbcDcc());
    }



    // dcc_europhenome database
    @Bean
    public DataSource dccEurophenomeDataSource() {
        return getConfiguredDatasource(dccEurophenomeFinalUrl, dccEurophenomeFinalUsername, dccEurophenomeFinalPassword);
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcDccEurophenome() {
        return new NamedParameterJdbcTemplate(dccEurophenomeDataSource());
    }

    @Bean
    public DccSqlUtils dccEurophenomeSqlUtils() {
        return new DccSqlUtils(jdbcDccEurophenome());
    }
}