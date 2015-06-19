package org.mousephenotype.cda;

import org.mousephenotype.cda.web.config.SearchContext;
import org.mousephenotype.cda.web.config.WebContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@Import({ WebContext.class, SearchContext.class })
@SpringBootApplication
public class PhentoypeDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhentoypeDataApplication.class, args);
    }
}
