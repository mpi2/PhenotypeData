package org.mousephenotype.cda.loads.statistics.load;


import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.dao.*;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.statistics.load.threei.TestConfigThreeI;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.mousephenotype.cda.db.dao", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                StatisticalResultLoaderConfig.class,
                TestConfigThreeI.class,
                GwasDAO.class,
                ReferenceDAO.class})}
)
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

    @Bean(name = "komp2TxManager")
    protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        tm.setDataSource(cdaDataSource());
        return tm;
    }

    @Bean(name = "sessionFactoryHibernate")
    public SessionFactory sessionFactory() {

        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(cdaDataSource());
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

        return sessionBuilder.buildSessionFactory();
    }

    @Bean
    @Inject
    public MpTermService mpTermService(OntologyTermDAO ontologyTermDAO, PhenotypePipelineDAO phenotypePipelineDAO) {
        Assert.notNull(ontologyTermDAO, "ontologyTermDAO cannot be null");
        Assert.notNull(phenotypePipelineDAO, "phenotypePipelineDAO cannot be null");
        return new MpTermService(ontologyTermDAO, phenotypePipelineDAO);
    }

    @Bean
    public PhenotypePipelineDAO phenotypePipelineDAO() {
        return new PhenotypePipelineDAOImpl(sessionFactory());
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