package org.mousephenotype.cda.loads.statistics.load.impc;

import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

@Configuration
@EnableAutoConfiguration
@ComponentScan("org.mousephenotype.cda.db")
@EnableJpaRepositories(basePackages = "org.mousephenotype.cda.db.repositories")
public class StatisticalResultLoaderConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataSource             komp2DataSource;
    private OntologyTermRepository ontologyTermRepository;
    private ParameterRepository    parameterRepository;

    @Inject
    public StatisticalResultLoaderConfig(
            @NotNull DataSource komp2DataSource,
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull ParameterRepository parameterRepository)
    {
        this.komp2DataSource = komp2DataSource;
        this.ontologyTermRepository = ontologyTermRepository;
        this.parameterRepository = parameterRepository;
    }

    @Bean(name="threeIFile")
    public Resource threeIFile() {
        return new ClassPathResource("data/flow_results_EBIexport_180119.csv");
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcKomp2() {
        return new NamedParameterJdbcTemplate(komp2DataSource);
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcKomp2());
    }
}