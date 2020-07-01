package org.mousephenotype.cda.loads.statistics.load.impc;


import org.mousephenotype.cda.db.PrimaryDataSource;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "org.mousephenotype.cda.db.repositories")
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.mousephenotype.cda.loads.statistics.load.impc", "org.mousephenotype.cda.db"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                        PrimaryDataSource.class,
                        StatisticalResultLoaderConfig.class,
                        StatisticalResultLoader.class
                })})
public class StatisticalResultLoaderTestConfig {

    // cda database
    @Bean
    public DataSource cdaDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("cda_test")
                .build();
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda());
    }


    @Bean
    public NamedParameterJdbcTemplate jdbcCda() {
        return new NamedParameterJdbcTemplate(cdaDataSource());
    }
}