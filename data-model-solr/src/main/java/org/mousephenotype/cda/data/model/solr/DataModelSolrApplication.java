package org.mousephenotype.cda.data.model.solr;

import org.mousephenotype.cda.data.model.solr.repositories.config.SearchContext;
import org.mousephenotype.cda.data.model.solr.repositories.config.WebContext;
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
public class DataModelSolrApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataModelSolrApplication.class, args);
    }
}
