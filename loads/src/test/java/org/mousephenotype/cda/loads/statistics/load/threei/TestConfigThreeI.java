package org.mousephenotype.cda.loads.statistics.load.threei;


import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.dao.OntologyTermDAO;
import org.mousephenotype.cda.db.dao.OntologyTermDAOImpl;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAOImpl;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan
public class TestConfigThreeI {


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

    // komp2 database
    @Value("${datasource.komp2.jdbc-url}")
    protected String komp2Url;

    @Value("${datasource.komp2.username}")
    protected String username;

    @Value("${datasource.komp2.password}")
    protected String password;

    @Bean
    public DataSource komp2DataSource() {
        return SqlUtils.getConfiguredDatasource(komp2Url, username, password);
    }

    @Bean(name = "sessionFactoryHibernate")
    public SessionFactory sessionFactory() {

        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

        return sessionBuilder.buildSessionFactory();
    }

    @Bean(name = "komp2TxManager")
    @Primary
    protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        tm.setDataSource(komp2DataSource());
        return tm;
    }


    ///////
    // DAOs
    ///////

    @Bean
    public OntologyTermDAO ontologyTermDAO() {
        return new OntologyTermDAOImpl(sessionFactory());
    }

    @Bean
    public PhenotypePipelineDAO pipelineDAO() {
        return new PhenotypePipelineDAOImpl(sessionFactory());
    }


    ///////////
    // SERVICES
    ///////////

    @Bean
    public MpTermService mpTermService() {
        return new MpTermService(ontologyTermDAO(), pipelineDAO());
    }


    ////////////////
    // MISCELLANEOUS
    ////////////////

    @Bean(name = "threeIFile")
    public Resource threeIFile() {
        return new ClassPathResource("data/threei_test_data.csv");
    }
}