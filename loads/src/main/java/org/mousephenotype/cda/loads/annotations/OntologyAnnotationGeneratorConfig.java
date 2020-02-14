package org.mousephenotype.cda.loads.annotations;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "org.mousephenotype.cda.db.repositories")
@ComponentScan("org.mousephenotype.cda.db")
public class OntologyAnnotationGeneratorConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

}