package uk.ac.ebi;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.ErrorPageFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.CacheControl;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import uk.ac.ebi.phenotype.web.util.DeploymentInterceptor;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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



//@PropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
//@PropertySource("${configServerUrl}")
@PropertySource("http://ves-ebi-d9.ebi.ac.uk:8989/pa/dev")

@EnableScheduling
public class PhenotypeArchiveConfig {

    private static final Logger logger = LoggerFactory.getLogger(PhenotypeArchiveConfig.class);


//    @Value("${drupal_base_url}")
    private String drupalBaseUrl;

//    @Value("${solr_url}")
    private String solrUrl;

//    @Value("${base_url}")
    private String baseUrl;

//    @Value("${internal_solr_url}")
    private String internalSolrUrl;

//    @Value("${media_base_url}")
    private String mediaBaseUrl;

//    @Value("${impc_media_base_url}")
    private String impcMediaBaseUrl;

//    @Value("${monarch_url}")
    private String monarchUrl;

//    @Value("${pdf_thumbnail_url}")
    private String pdfThumbnailUrl;

//    @Value("${google_analytics}")
    private String googleAnalytics;

//    @Value("${live_site}")
    private String liveSite;

    @NotNull
//    @Value("${paBaseUrl}")
    private String paBaseUrl;


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
        map.put("paBaseUrl", paBaseUrl);
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
                registry.addResourceHandler("/css/**").addResourceLocations("/resources/css/").setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                registry.addResourceHandler("/js/**").addResourceLocations("/resources/js/").setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                registry.addResourceHandler("/img/**").addResourceLocations("/resources/img/").setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                registry.addResourceHandler("/documentation/**").addResourceLocations("/resources/documentation/").setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                registry.addResourceHandler("/dalliance/**").addResourceLocations("/resources/dalliance/");
                registry.addResourceHandler("/release_notes/**").addResourceLocations("/resources/release_notes/").setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                registry.addResourceHandler("/image_compara/**").addResourceLocations("/resources/image_compara/");
                registry.addResourceHandler("/dist/**").addResourceLocations("/resources/js/anatomogram/dist/");
                registry.addResourceHandler("/fonts/**").addResourceLocations("/resources/fonts/").setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

                super.addResourceHandlers(registry);

            }

        };
    }

}
