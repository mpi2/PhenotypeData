package org.mousephenotype.cda.loads.annotations;

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.dao.ReferenceDAO;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.config.DataSourceCdaConfig;
import org.mousephenotype.cda.loads.common.config.DataSourceCdabaseConfig;
import org.mousephenotype.cda.loads.common.config.DataSourceDccConfig;
import org.mousephenotype.cda.loads.statistics.generate.StatisticalDatasetGeneratorConfig;
import org.mousephenotype.cda.loads.statistics.load.StatisticalResultLoaderConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import static org.mousephenotype.cda.db.utilities.SqlUtils.getConfiguredDatasource;

@Configuration
@ComponentScan(basePackages = {"org.mousephenotype.cda.loads.annotations", "org.mousephenotype.cda.loads.statistics.load", "org.mousephenotype.cda.db"},
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        MpTermService.class
                })
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                        DataSourceCdabaseConfig.class,
                        DataSourceCdaConfig.class,
                        DataSourceDccConfig.class,
                        StatisticalResultLoaderConfig.class,
                        StatisticalDatasetGeneratorConfig.class,
                        ReferenceDAO.class,
                        CdaSqlUtils.class})})
public class OntologyAnnotationGeneratorConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${datasource.komp2.jdbc-url}")
    String komp2Url;

    @Value("${datasource.komp2.username}")
    String komp2Username;

    @Value("${datasource.komp2.password}")
    String komp2Password;


    @Bean
    @Primary
    @PersistenceContext(name = "komp2Context")
    public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(komp2DataSource())
                .packages("org.mousephenotype.cda.db")
                .persistenceUnit("komp2")
                .build();
    }

    @Bean(name = "sessionFactoryHibernate")
    public LocalSessionFactoryBean sessionFactoryHibernate() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(komp2DataSource());
        sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
        return sessionFactory;
    }

    @Bean(name = "sessionFactoryHibernate")
    @Primary
    public SessionFactory getSessionFactory() {

        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

        return sessionBuilder.buildSessionFactory();
    }

    @Bean(name = "komp2DataSource")
    @Primary
    public DataSource komp2DataSource() {
        return getConfiguredDatasource(komp2Url, komp2Username, komp2Password);
    }
}