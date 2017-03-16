package uk.ac.ebi.phenotype.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import java.util.Arrays;
import java.util.List;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication

@Configuration
@ComponentScan({ "uk.ac.ebi.phenotype" })
@EnableJpaRepositories(basePackages = "uk.ac.ebi.phenotype")
@EnableAutoConfiguration
@EnableNeo4jRepositories(basePackages = "uk.ac.ebi.phenotype.repository")
public class Application {

    private final static Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    GeneRepository geneRepository;

    public static final String geneSymbol = "Akt2";

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner demo(GeneRepository geneRepository) {
        return args -> {


             geneRepository.findByMarkerSymbol(geneSymbol).toString();
        };
    }

}
