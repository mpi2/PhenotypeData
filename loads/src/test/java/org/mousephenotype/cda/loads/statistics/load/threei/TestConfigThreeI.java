package org.mousephenotype.cda.loads.statistics.load.threei;


import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@EnableTransactionManagement
@ComponentScan
public class TestConfigThreeI {


    private OntologyTermRepository ontologyTermRepository;
    private ParameterRepository    parameterRepository;

    @Inject
    public TestConfigThreeI(
           @NotNull OntologyTermRepository ontologyTermRepository,
           @NotNull ParameterRepository    parameterRepository)
    {
        this.ontologyTermRepository = ontologyTermRepository;
        this.parameterRepository = parameterRepository;
    }


    //////////////
    // DATASOURCES
    //////////////

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