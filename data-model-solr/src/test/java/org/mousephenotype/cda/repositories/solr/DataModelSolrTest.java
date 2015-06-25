package org.mousephenotype.cda.repositories.solr;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class DataModelSolrTest {

    public static void main(String[] args) {
        SpringApplication.run(DataModelSolrTest.class, args);
    }
}
