package org.mousephenotype.cda.loads.statistics.load;


import org.hibernate.SessionFactory;
import org.springframework.util.Assert;
import org.mousephenotype.cda.db.dao.GwasDAO;
import org.mousephenotype.cda.db.dao.OntologyTermDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.dao.ReferenceDAO;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.statistics.load.threei.TestConfigThreeI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
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


    @Bean
    @PersistenceContext(name = "komp2Context")
    public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(cdaDataSource())
                .packages("org.mousephenotype.cda.db")
                .persistenceUnit("komp2")
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
    public SessionFactory getSessionFactory() {

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
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda());
    }


    @Bean
    public NamedParameterJdbcTemplate jdbcCda() {
        return new NamedParameterJdbcTemplate(cdaDataSource());
    }

}