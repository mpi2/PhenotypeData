package org.mousephenotype.cda.loads.annotations;


import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.dao.GwasDAO;
import org.mousephenotype.cda.db.dao.OntologyTermDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.dao.ReferenceDAO;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.mousephenotype.cda.db.dao"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                GwasDAO.class,
                ReferenceDAO.class})}
)
@PropertySource("file:${user.home}/configfiles/${profile:local}/datarelease.properties")
public class OntologyAnnotationGeneratorTestConfig {

    @Value("${datasource.komp2.url}")
    String cdabaseUrl;

    @Value("${datasource.komp2.username}")
    protected String cdabaseUsername;

    @Value("${datasource.komp2.password}")
    protected String cdabasePassword;


    // komp2 database
    @Bean(name="komp2DataSource")
    public DataSource komp2DataSource() {
        return SqlUtils.getConfiguredDatasource(cdabaseUrl, cdabaseUsername, cdabasePassword);
    }

    @Bean
    @PersistenceContext(name = "komp2Context")
    public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(komp2DataSource())
                .packages("org.mousephenotype.cda.db")
                .persistenceUnit("komp2")
                .build();
    }


    @Bean(name = "komp2TxManager")
    protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        tm.setDataSource(komp2DataSource());
        return tm;
    }

    @Bean(name = "sessionFactoryHibernate")
    public SessionFactory getSessionFactory() {

        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

        return sessionBuilder.buildSessionFactory();
    }

    @Autowired
    OntologyTermDAO ontologyTermDAO;

    @Autowired
    PhenotypePipelineDAO ppDAO;

    @Bean
    public MpTermService mpTermService() {
        return new MpTermService(ontologyTermDAO, ppDAO);
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda());
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCda() {
        return new NamedParameterJdbcTemplate(komp2DataSource());
    }

}