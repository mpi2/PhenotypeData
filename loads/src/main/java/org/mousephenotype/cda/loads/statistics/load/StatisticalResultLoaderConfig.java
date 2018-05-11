package org.mousephenotype.cda.loads.statistics.load;

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.dao.GwasDAO;
import org.mousephenotype.cda.db.dao.ReferenceDAO;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import static org.mousephenotype.cda.db.utilities.SqlUtils.getConfiguredDatasource;

@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile:dev}/datarelease.properties")
@ComponentScan(basePackages = {"org.mousephenotype.cda.loads.statistics.load", "org.mousephenotype.cda.db.dao"},
        excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {GwasDAO.class, ReferenceDAO.class})})
public class StatisticalResultLoaderConfig implements ApplicationContextAware {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext context;

    @Value("${datasource.komp2.url}")
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

    @Bean(name="threeIFile")
    public Resource threeIFile() {
        return context.getResource("classpath:data/flow_results_EBIexport_180119.csv");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
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