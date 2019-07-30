package org.mousephenotype.cda.loads.statistics.load;

import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import static org.mousephenotype.cda.db.utilities.SqlUtils.getConfiguredDatasource;

@Configuration
@EnableJpaRepositories(basePackages = "org.mousephenotype.cda.db.repositories")
@ComponentScan(basePackages = {"org.mousephenotype.cda.loads.statistics.load", "org.mousephenotype.cda.db.dao"})
public class StatisticalResultLoaderConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    private OntologyTermRepository ontologyTermRepository;
    private ParameterRepository    parameterRepository;

    public StatisticalResultLoaderConfig(
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull ParameterRepository parameterRepository)
    {
        this.ontologyTermRepository = ontologyTermRepository;
        this.parameterRepository = parameterRepository;
    }


    @Value("${datasource.komp2.jdbc-url}")
    String komp2Url;

    @Value("${datasource.komp2.username}")
    String komp2Username;

    @Value("${datasource.komp2.password}")
    String komp2Password;

//    @Bean
//    @Primary
//    @PersistenceContext(name = "komp2Context")
//    public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(komp2DataSource())
//                .packages("org.mousephenotype.cda.db")
//                .persistenceUnit("komp2")
//                .build();
//    }
//
//    @Bean(name = "sessionFactoryHibernate")
//    public LocalSessionFactoryBean sessionFactoryHibernate() {
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        sessionFactory.setDataSource(komp2DataSource());
//        sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
//        return sessionFactory;
//    }
//
//    @Bean(name = "sessionFactoryHibernate")
//    @Primary
//    public SessionFactory getSessionFactory() {
//
//        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
//        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
//        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");
//
//        return sessionBuilder.buildSessionFactory();
//    }

    @Bean(name = "komp2DataSource")
    @Primary
    public DataSource komp2DataSource() {
        return getConfiguredDatasource(komp2Url, komp2Username, komp2Password);
    }

    @Bean(name="threeIFile")
    public Resource threeIFile() {
        return new ClassPathResource("data/flow_results_EBIexport_180119.csv");
    }

    @Bean
    public MpTermService mpTermService() {
        return new MpTermService(ontologyTermRepository, parameterRepository);
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcKomp2() {
        return new NamedParameterJdbcTemplate(komp2DataSource());
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcKomp2());
    }
}