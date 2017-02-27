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

package org.mousephenotype.cda.loads.common;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile}/datarelease.properties")
@EnableAutoConfiguration(exclude = {
        JndiConnectionFactoryAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class})
/**
 * This configuration class holds configuration information shared by the data load create process.
 *
 * Created by mrelac on 18/08/2016.
 */
public class DataSourcesConfigApp {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${datasource.cdabase.url}")
    String cdabaseUrl;

    @Value("${datasource.cda.url}")
    String cdaUrl;

    @Value("${datasource.dcc.url}")
    String dccUrl;

    @Value("${datasource.dccEurophenomeFinal.url}")
    String dccEurophenomeFinalUrl;

    @Value("${datasource.cdabase.username}")
    String username;

    @Value("${datasource.cdabase.password}")
    String password;

    @Bean(name = "cdabaseDataSource", destroyMethod = "close")
    public DataSource cdabaseDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(cdabaseUrl)
                .username(username)
                .password(password)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);


        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using cdasource database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

// 2017-02-14 (mrelac) The data source below consistently causes a DataLoadException. Using the BasicDataSource above works, and it completes in 7 minutes, as opposed to over an hour!
// DataLoadException: org.springframework.jdbc.CannotGetJdbcConnectionException: Could not get JDBC Connection; nested exception is com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: Could not create connection to database server. Attempted reconnect 3 times. Giving up.


//    @Bean(name = "cdabaseDataSource")
//    public DataSource cdabaseDataSource() {
//
//        DataSource ds = DataSourceBuilder
//                .create()
//                .url(cdabaseUrl)
//                .username(username)
//                .password(password)
//                .type(DriverManagerDataSource.class)
//                .driverClassName("com.mysql.jdbc.Driver").build();
//
//        try {
//            logger.info("Using cdasource database {}", ds.getConnection().getCatalog());
//
//        } catch (Exception e) { }
//
//        return ds;
//    }

    @Bean(name = "jdbcCdabase")
    public NamedParameterJdbcTemplate jdbcCdabase() {
        return new NamedParameterJdbcTemplate(cdabaseDataSource());
    }



    @Bean(name = "cdaDataSource", destroyMethod = "close")
    @Primary
    public DataSource cdaDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(cdaUrl)
                .username(username)
                .password(password)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);


        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);


        try {
            logger.info("Using cda database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

//    @Bean(name = "cdaDataSource", destroyMethod = "close")
//    @Bean(name = "cdaDataSource")
//    @Primary
//    public DataSource cdaDataSource() {
//
//        DataSource ds = DataSourceBuilder
//                .create()
//                .url(cdaUrl)
//                .username(username)
//                .password(password)
//                .type(DriverManagerDataSource.class)
//                .driverClassName("com.mysql.jdbc.Driver").build();
//
//        try {
//            logger.info("Using cda database {}", ds.getConnection().getCatalog());
//
//        } catch (Exception e) { }
//
//        return ds;
//    }

    @Bean(name = "jdbcCda")
    public NamedParameterJdbcTemplate jdbcCda() {
        return new NamedParameterJdbcTemplate(cdaDataSource());
    }



    @Bean(name = "dccDataSource", destroyMethod = "close")
    public DataSource dccDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(dccUrl)
                .username(username)
                .password(password)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();

        ((BasicDataSource) ds).setInitialSize(4);

        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using dcc database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

    // 2017-02-26 (mrelac) The data source below consistently causes a DataLoadException.
    // DataLoadException: org.springframework.jdbc.CannotGetJdbcConnectionException: Could not get JDBC Connection; nested exception is com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: Could not create connection to database server. Attempted reconnect 3 times. Giving up.


//    @Bean(name = "dccDataSource", destroyMethod = "close")
//    @Bean(name = "dccDataSource")
//    public DataSource dccDataSource() {
//
//        DataSource ds = DataSourceBuilder
//                .create()
//                .url(dccUrl)
//                .username(username)
//                .password(password)
//                .type(DriverManagerDataSource.class)
//                .driverClassName("com.mysql.jdbc.Driver").build();
//
//        try {
//            logger.info("Using dcc database {}", ds.getConnection().getCatalog());
//
//        } catch (Exception e) { }
//
//        return ds;
//    }

    @Bean(name = "jdbcDcc")
    public NamedParameterJdbcTemplate jdbcDcc() {
        return new NamedParameterJdbcTemplate(dccDataSource());
    }

//    @Bean(name = "dccEurophenomeDataSource", destroyMethod = "close")
//    public DataSource dccEurophenomeDataSource() {
//
//        DataSource ds = DataSourceBuilder
//                .create()
//                .url(dccEurophenomeFinalUrl)
//                .username(username)
//                .password(password)
//                .type(BasicDataSource.class)
//                .driverClassName("com.mysql.jdbc.Driver").build();
//        ((BasicDataSource) ds).setInitialSize(4);
//
//
//        ((BasicDataSource) ds).setLogAbandoned(false);
//        ((BasicDataSource) ds).setRemoveAbandoned(false);
//
//        try {
//            logger.info("Using dcc europhenome database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());
//
//        } catch (Exception e) { }
//
//        return ds;
//    }

//    @Bean(name = "dccEurophenomeDataSource", destroyMethod = "close")
    @Bean(name = "dccEurophenomeDataSource")
    public DataSource dccEurophenomeDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(dccEurophenomeFinalUrl)
                .username(username)
                .password(password)
                .type(DriverManagerDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();

        try {
            logger.info("Using dcc europhenome database {}", ds.getConnection().getCatalog());

        } catch (Exception e) { }

        return ds;
    }

    @Bean(name = "jdbcDccEurophenome")
    public NamedParameterJdbcTemplate jdbcDccEurophenome() {
        return new NamedParameterJdbcTemplate(dccEurophenomeDataSource());
    }
}