package org.mousephenotype.cda.loads.statistics.load;


import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.statistics.load.threei.TestConfigThreeI;
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
import javax.validation.constraints.NotNull;

@Configuration
@EnableJpaRepositories(basePackages = "org.mousephenotype.cda.db.repositories")
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                StatisticalResultLoaderConfig.class,
                TestConfigThreeI.class})}
)
public class StatisticalResultLoaderTestConfig {

    private OntologyTermRepository ontologyTermRepository;
    private ParameterRepository    parameterRepository;

    public StatisticalResultLoaderTestConfig(
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull ParameterRepository parameterRepository)
    {
        this.ontologyTermRepository = ontologyTermRepository;
        this.parameterRepository = parameterRepository;
    }


    // cda database
    @Bean
    public DataSource cdaDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("cda_test")
                .build();
    }

//    @Bean(name = "komp2TxManager")
//    protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
//        JpaTransactionManager tm = new JpaTransactionManager();
//        tm.setEntityManagerFactory(emf);
//        tm.setDataSource(cdaDataSource());
//        return tm;
//    }
//
//    @Bean(name = "sessionFactoryHibernate")
//    public SessionFactory sessionFactory() {
//
//        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(cdaDataSource());
//        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
//        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");
//
//        return sessionBuilder.buildSessionFactory();
//    }

    @Bean
    public MpTermService mpTermService() {
        return new MpTermService(ontologyTermRepository, parameterRepository);
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