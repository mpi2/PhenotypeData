package uk.ac.ebi;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.ErrorPageFilter;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import uk.ac.ebi.phenotype.web.util.DeploymentInterceptor;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ilinca on 01/03/2017.
 */
@Configuration
@ComponentScan(value = {"uk.ac.ebi",
        "org.mousephenotype.cda.solr",
        "org.mousephenotype.cda.utilities",
        "org.mousephenotype.cda.db",
        "uk.ac.ebi.phenotype.web.controller"},
        excludeFilters = @ComponentScan.Filter(value = org.mousephenotype.cda.annotations.ComponentScanNonParticipant.class, type = FilterType.ANNOTATION))
@PropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
@EnableNeo4jRepositories("uk.ac.ebi.phenotype.repository")
public class PhenotypeArchiveConfig {

    private static final Logger logger = LoggerFactory.getLogger(PhenotypeArchiveConfig.class);


    @NotNull
    @Value("${neo4jDbPath}")
    private String neo4jDbPath;

    @Value("${drupal_base_url}")
    private String drupalBaseUrl;

    @Value("${solr_url}")
    private String solrUrl;

    @Value("${base_url}")
    private String baseUrl;

    @Value("${internal_solr_url}")
    private String internalSolrUrl;

    @Value("${media_base_url}")
    private String mediaBaseUrl;

    @Value("${impc_media_base_url}")
    private String impcMediaBaseUrl;

    @Value("${monarch_url}")
    private String monarchUrl;

    @Value("${pdf_thumbnail_url}")
    private String pdfThumbnailUrl;

    @Value("${google_analytics}")
    private String googleAnalytics;

    @Value("${live_site}")
    private String liveSite;


    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    @Bean
    public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
    public DataSource komp2DataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.admintools")
    public DataSource admintoolsDataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(komp2DataSource());
        emf.setPackagesToScan(new String[]{"org.mousephenotype.cda.db.pojo", "org.mousephenotype.cda.db.dao"});

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);
        emf.setJpaProperties(buildHibernateProperties());

        return emf;
    }

    protected Properties buildHibernateProperties() {
        Properties hibernateProperties = new Properties();

        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        hibernateProperties.setProperty("hibernate.show_sql", "false");
        hibernateProperties.setProperty("hibernate.use_sql_comments", "false");
        hibernateProperties.setProperty("hibernate.format_sql", "false");
        hibernateProperties.setProperty("hibernate.generate_statistics", "false");
        hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");

        return hibernateProperties;
    }

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

    @Bean(name = "sessionFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(komp2DataSource());
        sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
        return sessionFactory;
    }

    @Bean(name = "globalConfiguration")
    public Map<String, String> getGlobalConfig() {
        Map<String, String> map = new HashMap<>();
        map.put("baseUrl", baseUrl);
        map.put("drupalBaseUrl", drupalBaseUrl);
        map.put("solrUrl", solrUrl);
        map.put("internalSolrUrl", internalSolrUrl);
        map.put("mediaBaseUrl", mediaBaseUrl);
        map.put("impcMediaBaseUrl", impcMediaBaseUrl);
        map.put("monarchUrl", monarchUrl);
        map.put("pdfThumbnailUrl", pdfThumbnailUrl);
        map.put("googleAnalytics", googleAnalytics);
        map.put("liveSite", liveSite);
        return map;
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver =
                new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Bean
    OpenSessionInViewInterceptor getOpenSessionInViewInterceptor(SessionFactory session){
        OpenSessionInViewInterceptor openSessionInViewInterceptor = new OpenSessionInViewInterceptor();
        openSessionInViewInterceptor.setSessionFactory(session);
        return openSessionInViewInterceptor;
    }

    @Bean
    public WebMvcConfigurerAdapter adapter() {

        return new WebMvcConfigurerAdapter() {

            @Autowired
            DeploymentInterceptor deploymentInterceptor;

//            @Autowired
//            OpenSessionInViewInterceptor openSessionInViewInterceptor;

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(deploymentInterceptor);
//                registry.addInterceptor(openSessionInViewInterceptor);
                super.addInterceptors(registry);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {

                logger.info("Adding WebMvc resources");
                registry.addResourceHandler("/css/**").addResourceLocations("/resources/css/");
                registry.addResourceHandler("/js/**").addResourceLocations("/resources/js/");
                registry.addResourceHandler("/img/**").addResourceLocations("/resources/img/");
                registry.addResourceHandler("/documentation/**").addResourceLocations("/resources/documentation/");
                registry.addResourceHandler("/dalliance/**").addResourceLocations("/resources/dalliance/");
                registry.addResourceHandler("/release_notes/**").addResourceLocations("/resources/release_notes/");
                registry.addResourceHandler("/image_compara/**").addResourceLocations("/resources/image_compara/");
                registry.addResourceHandler("/dist/**").addResourceLocations("/resources/js/anatomogram/dist/");
                registry.addResourceHandler("/fonts/**").addResourceLocations("/resources/fonts/");

                super.addResourceHandlers(registry);

            }

        };
    }

}
