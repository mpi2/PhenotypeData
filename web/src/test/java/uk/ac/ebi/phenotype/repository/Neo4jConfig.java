
package uk.ac.ebi.phenotype.repository;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import uk.ac.ebi.PhenotypeArchiveDatabaseConfig;

import javax.validation.constraints.NotNull;

/**
 * Created by jmason on 18/03/2017.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "uk.ac.ebi.phenotype.repository")
@PropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
@Import(PhenotypeArchiveDatabaseConfig.class)
public class Neo4jConfig {

    @NotNull
    @Value("${neo4jDbPath2}")
    private String neo4jDbPath2;

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();

        // To persist the database, uncomment this section
        //String pathToDb = Paths.get(".").toAbsolutePath().normalize().toString() + "/target//Users/ckchen/Documents/Neo4j/impc.neo4";
        String pathToDb = neo4jDbPath2;
        config
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
                .setURI("file://" + pathToDb);

        System.out.println(config);

        return config;
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(getConfiguration(), "uk.ac.ebi.phenotype.repository");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }

}


//--------------------------------------------
//package uk.ac.ebi.phenotype.repository;
//
//import org.neo4j.ogm.session.SessionFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
//import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import java.nio.file.Paths;
//
////import org.springframework.context.annotation.ComponentScan;
//
///**
// * Test configuration sets up the embedded Neo4J driver in memory mode
// */
//@Configuration
//@EnableTransactionManagement
////@ComponentScan(value = "uk.ac.ebi.phenotype.repository")
//@EnableNeo4jRepositories("uk.ac.ebi.phenotype.repository")
//public class Neo4jTestConfig {
//
//    @Bean
//    public org.neo4j.ogm.config.Configuration getConfiguration() {
//        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
//
//        // To persist the database, uncomment this section
//        String pathToDb = Paths.get(".").toAbsolutePath().normalize().toString() + "/target/test_graph.db";
//        config
//                .driverConfiguration()
//                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
//                .setURI("file://" + pathToDb);
//
//        System.out.println(config);
//
//        return config;
//    }
//
//    @Bean
//    public SessionFactory sessionFactory() {
//        return new SessionFactory(getConfiguration(), "uk.ac.ebi.phenotype.repository");
//    }
//
//    @Bean
//    public Neo4jTransactionManager transactionManager() {
//        return new Neo4jTransactionManager(sessionFactory());
//    }
//
//}