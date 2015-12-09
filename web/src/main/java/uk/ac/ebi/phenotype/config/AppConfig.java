package uk.ac.ebi.phenotype.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmWebDao;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmWebDaoSolrImpl;

import javax.annotation.PostConstruct;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
@Configuration
@ComponentScan({"org.mousephenotype", "uk.ac.ebi.phenotype"})
@EnableTransactionManagement
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    Environment env;

    @Value("${datasource.komp2.url}")
    private String datasourceKomp2Url;

    @Value("${phenodigm.solrserver}")
    private String phenodigmSolrserver;

    @Value("${solr.host}")
    private String solrHost;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${internalSolrUrl}")
    private String internalSolrUrl;

    @PostConstruct
    public void logConnectionProperties() {
        logger.info("dataSource.komp2.url: " + datasourceKomp2Url);
        logger.info("phenodigm.solrserver: " + phenodigmSolrserver);
        logger.info("solr.host:            " + solrHost);
        logger.info("baseUrl:              " + baseUrl);
        logger.info("internalSolrUrl:      " + internalSolrUrl);
    }

    @Bean (name="globalConfiguration")
    public Map<String, String> globalConfiguration(){

        Map <String, String> gc = new HashMap<>();
        gc.put("baseUrl", "${baseUrl}");
        gc.put("drupalBaseUrl", "${drupalBaseUrl}");
        gc.put("solrUrl", "${solrUrl}");
        gc.put("internalSolrUrl", "${internalSolrUrl}");
        gc.put("mediaBaseUrl", "${mediaBaseUrl}");
        gc.put("impcMediaBaseUrl", "${impcMediaBaseUrl}");
        gc.put("pdfThumbnailUrl", "${pdfThumbnailUrl}");
        gc.put("googleAnalytics", "${googleAnalytics}");
        gc.put("liveSite", "${liveSite}");

        return gc;
    }

    @Bean
    public PhenoDigmWebDao phenoDigmWebDao() {
        return new PhenoDigmWebDaoSolrImpl();
    }

    @Bean(name = "komp2DataSource")
    @Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
    public DataSource komp2DataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setMaximumPoolSize(10);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl(env.getProperty("datasource.komp2.url"));
        dataSource.setUsername(env.getProperty("datasource.komp2.username"));
        dataSource.setPassword(env.getProperty("datasource.komp2.password"));
        dataSource.setPoolName("komp2");
        return dataSource;
//        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    @PersistenceContext(name="komp2Context")
    public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder){
        return builder
                .dataSource(komp2DataSource())
                .packages("org.mousephenotype.cda.db")
                .persistenceUnit("komp2")
                .build();
    }

//    @Bean(name = "sessionFactory")
//    public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
//        HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
//        factory.setEntityManagerFactory(emf);
//        return factory;
//    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(komp2DataSource());
        sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
        return sessionFactory;
    }

    @Bean(name = "komp2TxManager")
    @Primary
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(komp2DataSource());
    }


    @Bean(name = "admintoolsDataSource")
    @ConfigurationProperties(prefix = "datasource.admintools")
    public DataSource admintoolsDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setMaximumPoolSize(10);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl(env.getProperty("datasource.admintools.url"));
        dataSource.setUsername(env.getProperty("datasource.admintools.username"));
        dataSource.setPassword(env.getProperty("datasource.admintools.password"));
        dataSource.setPoolName("admin tools");
        return dataSource;
    }

}
