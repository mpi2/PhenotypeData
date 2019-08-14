package uk.ac.ebi;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration
@ComponentScan("org.mousephenotype.cda.db")
public class PhenotypeArchiveDatabaseConfig {

}