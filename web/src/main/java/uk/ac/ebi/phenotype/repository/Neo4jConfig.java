package uk.ac.ebi.phenotype.repository;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import uk.ac.ebi.PhenotypeArchiveDatabaseConfig;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.nio.file.Paths;

/**
 * Created by jmason on 18/03/2017.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "uk.ac.ebi.phenotype.repository")
@PropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
@Import(PhenotypeArchiveDatabaseConfig.class)
public class Neo4jConfig {

    @NotNull
    @Value("${neo4jDbPath}")
    private String neo4jDbPath;

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();

        // To persist the database, uncomment this section
        //String pathToDb = Paths.get(".").toAbsolutePath().normalize().toString() + "/target//Users/ckchen/Documents/Neo4j/impc.neo4";
        String pathToDb = neo4jDbPath;
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
