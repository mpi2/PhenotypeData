package org.mousephenotype.cda.web.config;

import org.mousephenotype.cda.db.AdminToolsDatasourceConfig;
import org.mousephenotype.cda.db.Komp2DatasourceConfig;
import org.mousephenotype.cda.web.SearchContext;
import org.mousephenotype.cda.web.WebContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ComponentScan("org.mousephenotype.cda")
@EnableAutoConfiguration
@Import({ WebContext.class, SearchContext.class, Komp2DatasourceConfig.class, AdminToolsDatasourceConfig.class})
@SpringBootApplication
public class PhentoypeDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhentoypeDataApplication.class, args);
    }
}
