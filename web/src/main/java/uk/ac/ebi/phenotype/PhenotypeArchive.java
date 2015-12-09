package uk.ac.ebi.phenotype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
@SpringBootApplication
public class PhenotypeArchive extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(PhenotypeArchive.class);

    public static void main(String[] args) {
        SpringApplication.run(PhenotypeArchive.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PhenotypeArchive.class);
    }


    @Bean //TODO: might be better as a  @Config WebMvcConfig extends WebMvcConfigurerAdapter class
    public WebMvcConfigurerAdapter adapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                logger.info("Adding interceptors");
                //TODO: could this be where to add the util/DeploymentInterceptor ?
                super.addInterceptors(registry);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                logger.info("Adding WebMvc resources");
//                registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
            }


        };
    }

}
