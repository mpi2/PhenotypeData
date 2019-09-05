package uk.ac.ebi;

import org.mousephenotype.cda.constants.Constants;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.ac.ebi.phenotype.util.SolrUtilsWeb;
import uk.ac.ebi.phenotype.web.util.DeploymentInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by ilinca on 01/03/2017.
 */

@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@ComponentScan(value = {"uk.ac.ebi",
        "org.mousephenotype.cda.solr",
        "org.mousephenotype.cda.utilities",
        "org.mousephenotype.cda.db",
        "uk.ac.ebi.phenotype.web.controller"},
        excludeFilters = @ComponentScan.Filter(value = org.mousephenotype.cda.annotations.ComponentScanNonParticipant.class, type = FilterType.ANNOTATION))
@EnableScheduling
public class PhenotypeArchiveConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(PhenotypeArchiveConfig.class);


    @Value("${cms_base_url}")
    private String cmsBaseUrl;

    @Value("${solr_url}")
    private String solrUrl;

    // FIXME - Is this needed post-springBoot_2.1.7_DATA_REPOSITORIES? (remove commented-out usage in getGlobalConfig() below if not needed.)
//    @Value("${statistics_url}")
//    private String statisticsUrl;

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

    @Value("${google_analytics}")
    private String googleAnalytics;

    @Value("${live_site}")
    private String liveSite;

    @Value("${paBaseUrl}")
    private String paBaseUrl;


    @Bean(name = "globalConfiguration")
    public Map<String, String> getGlobalConfig() {
        Map<String, String> map = new HashMap<>();
        map.put("baseUrl", baseUrl);
        map.put("cmsBaseUrl", cmsBaseUrl);
        map.put("solrUrl", solrUrl);
        map.put("internalSolrUrl", internalSolrUrl);
        map.put("mediaBaseUrl", mediaBaseUrl);
        map.put("impcMediaBaseUrl", impcMediaBaseUrl);
        map.put("monarchUrl", monarchUrl);
        map.put("pdfThumbnailUrl", Constants.PDF_THUMBNAIL_RELATIVE_URL);
        map.put("googleAnalytics", googleAnalytics);
        map.put("liveSite", liveSite);
        map.put("paBaseUrl", paBaseUrl);
//        map.put("statisticsUrl", statisticsUrl);
        return map;
    }

    @Autowired
    DeploymentInterceptor deploymentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deploymentInterceptor);
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
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/views/", ".jsp");
    }

    @Bean
    public SolrUtilsWeb solrUtilsWeb() {
        return new SolrUtilsWeb(solrIndexWeb());
    }

    @Bean
    public SolrIndex solrIndexWeb() {
        return new SolrIndex();
    }
}