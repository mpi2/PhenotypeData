package org.mousephenotype.cda.loads.reports;

import org.apache.commons.dbcp.BasicDataSource;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource({
        "file:${user.home}/configfiles/${profile:release}/datarelease.properties"
})
public class AppConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    
    // IMPRESS
    
    @Value("${datasource.impress.compare.previous.url}")
    String impressPreviousUrl;

    @Value("${datasource.impress.compare.previous.username}")
    String impressPreviousUsername;

    @Value("${datasource.impress.compare.previous.password}")
    String impressPreviousPassword;

    @Value("${datasource.impress.compare.current.url}")
    String impressCurrentUrl;

    @Value("${datasource.impress.compare.current.username}")
    String impressCurrentUsername;

    @Value("${datasource.impress.compare.current.password}")
    String impressCurrentPassword;

    
    // CDA
    
    @Value("${datasource.cda.compare.previous.url}")
    String cdaPreviousUrl;

    @Value("${datasource.cda.compare.previous.username}")
    String cdaPreviousUsername;

    @Value("${datasource.cda.compare.previous.password}")
    String cdaPreviousPassword;

    @Value("${datasource.cda.compare.current.url}")
    String cdaCurrentUrl;

    @Value("${datasource.cda.compare.current.username}")
    String cdaCurrentUsername;

    @Value("${datasource.cda.compare.current.password}")
    String cdaCurrentPassword;

    
    // CDABASE
    
    @Value("${datasource.cdabase.compare.previous.url}")
    String cdabasePreviousUrl;

    @Value("${datasource.cdabase.compare.previous.username}")
    String cdabasePreviousUsername;

    @Value("${datasource.cdabase.compare.previous.password}")
    String cdabasePreviousPassword;

    @Value("${datasource.cdabase.compare.current.url}")
    String cdabaseCurrentUrl;

    @Value("${datasource.cdabase.compare.current.username}")
    String cdabaseCurrentUsername;

    @Value("${datasource.cdabase.compare.current.password}")
    String cdabaseCurrentPassword;

    
    // DCC

    @Value("${datasource.dcc.compare.previous.url}")
    String dccPreviousUrl;

    @Value("${datasource.dcc.compare.previous.username}")
    String dccPreviousUsername;

    @Value("${datasource.dcc.compare.previous.password}")
    String dccPreviousPassword;

    @Value("${datasource.dcc.compare.current.url}")
    String dccCurrentUrl;

    @Value("${datasource.dcc.compare.current.username}")
    String dccCurrentUsername;

    @Value("${datasource.dcc.compare.current.password}")
    String dccCurrentPassword;
    
    

    // IMPRESS

    @Bean(destroyMethod = "close")
    public DataSource impressPreviousDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(impressPreviousUrl)
                .username(impressPreviousUsername)
                .password(impressPreviousPassword)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);
        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using impressPreviousDataSource database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcImpressPrevious() {
        return new NamedParameterJdbcTemplate(impressPreviousDataSource());
    }

    @Bean(destroyMethod = "close")
    public DataSource impressCurrentDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(impressCurrentUrl)
                .username(impressCurrentUsername)
                .password(impressCurrentPassword)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);
        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using impressCurrentDataSource database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcImpressCurrent() {
        return new NamedParameterJdbcTemplate(impressCurrentDataSource());
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils();
    }


    // CDA

    @Bean(destroyMethod = "close")
    public DataSource cdaPreviousDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(cdaPreviousUrl)
                .username(cdaPreviousUsername)
                .password(cdaPreviousPassword)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);
        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using cdaPreviousDataSource database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCdaPrevious() {
        return new NamedParameterJdbcTemplate(cdaPreviousDataSource());
    }

    @Bean(destroyMethod = "close")
    public DataSource cdaCurrentDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(cdaCurrentUrl)
                .username(cdaCurrentUsername)
                .password(cdaCurrentPassword)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);


        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using cdaCurrentDataSource database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCdaCurrent() {
        return new NamedParameterJdbcTemplate(cdaCurrentDataSource());
    }


    // CDABASE

    @Bean(destroyMethod = "close")
    public DataSource cdabasePreviousDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(cdabasePreviousUrl)
                .username(cdabasePreviousUsername)
                .password(cdabasePreviousPassword)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);


        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using cdabasePreviousDataSource database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCdabasePrevious() {
        return new NamedParameterJdbcTemplate(cdabasePreviousDataSource());
    }
    
    @Bean(destroyMethod = "close")
    public DataSource cdabaseCurrentDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(cdabaseCurrentUrl)
                .username(cdabaseCurrentUsername)
                .password(cdabaseCurrentPassword)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);


        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using cdabaseCurrentDataSource database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCdabaseCurrent() {
        return new NamedParameterJdbcTemplate(cdabaseCurrentDataSource());
    }


    // DCC

    @Bean(destroyMethod = "close")
    public DataSource dccPreviousDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(dccPreviousUrl)
                .username(dccPreviousUsername)
                .password(dccPreviousPassword)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);


        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using dccPreviousDataSource database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcDccPrevious() {
        return new NamedParameterJdbcTemplate(dccPreviousDataSource());
    }

    @Bean(destroyMethod = "close")
    public DataSource dccCurrentDataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(dccCurrentUrl)
                .username(dccCurrentUsername)
                .password(dccCurrentPassword)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();
        ((BasicDataSource) ds).setInitialSize(4);


        ((BasicDataSource) ds).setLogAbandoned(false);
        ((BasicDataSource) ds).setRemoveAbandoned(false);

        try {
            logger.info("Using dccCurrentDataSource database {} with initial pool size {}", ds.getConnection().getCatalog(), ((BasicDataSource) ds).getInitialSize());

        } catch (Exception e) { }

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcDccCurrent() {
        return new NamedParameterJdbcTemplate(dccCurrentDataSource());
    }
}