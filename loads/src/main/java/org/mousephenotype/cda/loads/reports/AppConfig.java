package org.mousephenotype.cda.loads.reports;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    

    // CDABASE
    @Value("${datasource.cdabase.compare.previous.jdbc-url}")
    String cdabasePreviousUrl;

    @Value("${datasource.cdabase.compare.previous.username}")
    String cdabasePreviousUsername;

    @Value("${datasource.cdabase.compare.previous.password}")
    String cdabasePreviousPassword;

    @Value("${datasource.cdabase.compare.current.jdbc-url}")
    String cdabaseCurrentUrl;

    @Value("${datasource.cdabase.compare.current.username}")
    String cdabaseCurrentUsername;

    @Value("${datasource.cdabase.compare.current.password}")
    String cdabaseCurrentPassword;


    // CDA
    @Value("${datasource.cda.compare.previous.jdbc-url}")
    String cdaPreviousUrl;

    @Value("${datasource.cda.compare.previous.username}")
    String cdaPreviousUsername;

    @Value("${datasource.cda.compare.previous.password}")
    String cdaPreviousPassword;

    @Value("${datasource.cda.compare.current.jdbc-url}")
    String cdaCurrentUrl;

    @Value("${datasource.cda.compare.current.username}")
    String cdaCurrentUsername;

    @Value("${datasource.cda.compare.current.password}")
    String cdaCurrentPassword;


    // DCC
    @Value("${datasource.dcc.compare.previous.jdbc-url}")
    String dccPreviousUrl;

    @Value("${datasource.dcc.compare.previous.username}")
    String dccPreviousUsername;

    @Value("${datasource.dcc.compare.previous.password}")
    String dccPreviousPassword;

    @Value("${datasource.dcc.compare.current.jdbc-url}")
    String dccCurrentUrl;

    @Value("${datasource.dcc.compare.current.username}")
    String dccCurrentUsername;

    @Value("${datasource.dcc.compare.current.password}")
    String dccCurrentPassword;


    
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
    @Bean
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
    @Bean
    public DataSource dccCurrentDataSource() {
        return SqlUtils.getConfiguredDatasource(dccCurrentUrl, dccCurrentUsername, dccCurrentPassword);
    }
    @Bean
    public NamedParameterJdbcTemplate jdbcDccCurrent() {
        return new NamedParameterJdbcTemplate(dccCurrentDataSource());
    }
}