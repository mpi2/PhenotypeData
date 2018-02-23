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
        "file:${user.home}/configfiles/${profile}/datarelease.properties"
})
public class AppConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    

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

    
    
    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils();
    }



    // CDABASE
    @Bean
    public DataSource cdabasePreviousDataSource() {
        return SqlUtils.getConfiguredDatasource(cdabasePreviousUrl, cdabasePreviousUsername, cdabasePreviousPassword);
    }
    @Bean
    public NamedParameterJdbcTemplate jdbcCdabasePrevious() {
        return new NamedParameterJdbcTemplate(cdabasePreviousDataSource());
    }
    @Bean
    public DataSource cdabaseCurrentDataSource() {
        return SqlUtils.getConfiguredDatasource(cdabaseCurrentUrl, cdabaseCurrentUsername, cdabaseCurrentPassword);
    }
    @Bean
    public NamedParameterJdbcTemplate jdbcCdabaseCurrent() {
        return new NamedParameterJdbcTemplate(cdabaseCurrentDataSource());
    }


    // CDA
    @Bean
    public DataSource cdaPreviousDataSource() {
        return SqlUtils.getConfiguredDatasource(cdaPreviousUrl, cdaPreviousUsername, cdaPreviousPassword);
    }
    @Bean
    public NamedParameterJdbcTemplate jdbcCdaPrevious() {
        return new NamedParameterJdbcTemplate(cdaPreviousDataSource());
    }
    public DataSource cdaCurrentDataSource() {
        return SqlUtils.getConfiguredDatasource(cdaCurrentUrl, cdaCurrentUsername, cdaCurrentPassword);
    }
    @Bean
    public NamedParameterJdbcTemplate jdbcCdaCurrent() {
        return new NamedParameterJdbcTemplate(cdaCurrentDataSource());
    }


    // DCC
    @Bean
    public DataSource dccPreviousDataSource() {
        return SqlUtils.getConfiguredDatasource(dccPreviousUrl, dccPreviousUsername, dccPreviousPassword);
    }
    @Bean
    public NamedParameterJdbcTemplate jdbcDccPrevious() {
        return new NamedParameterJdbcTemplate(dccPreviousDataSource());
    }
    public DataSource dccCurrentDataSource() {
        return SqlUtils.getConfiguredDatasource(dccCurrentUrl, dccCurrentUsername, dccCurrentPassword);
    }
    @Bean
    public NamedParameterJdbcTemplate jdbcDccCurrent() {
        return new NamedParameterJdbcTemplate(dccCurrentDataSource());
    }
    

    
    // IMPRESS
    @Bean
    public DataSource impressPreviousDataSource() {
        return SqlUtils.getConfiguredDatasource(impressPreviousUrl, impressPreviousUsername, impressPreviousPassword);
    }
    @Bean
    public NamedParameterJdbcTemplate jdbcImpressPrevious() {
        return new NamedParameterJdbcTemplate(impressPreviousDataSource());
    }
    @Bean
    public DataSource impressCurrentDataSource() {
        return SqlUtils.getConfiguredDatasource(impressCurrentUrl, impressCurrentUsername, impressCurrentPassword);
    }
    @Bean
    public NamedParameterJdbcTemplate jdbcImpressCurrent() {
        return new NamedParameterJdbcTemplate(impressCurrentDataSource());
    }
}