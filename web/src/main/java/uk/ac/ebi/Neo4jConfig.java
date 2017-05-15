package uk.ac.ebi;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

import javax.validation.constraints.NotNull;

/**
 * Created by jmason on 18/03/2017.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "org.mousephenotype.cda.neo4j.repository")
@PropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
@Import(PhenotypeArchiveDatabaseConfig.class)
public class Neo4jConfig {

    @NotNull
    @Value("${neo4jDbPath}")
    private String neo4jDbPath;

    @NotNull
    @Value("${datasource.neo4j.user}")
    private String neoUser;
    @NotNull
    @Value("${datasource.neo4j.pass}")
    private String neoPass;
    @NotNull
    @Value("${datasource.neo4j.server}")
    private String neoServer;
    @NotNull
    @Value("${datasource.neo4j.port}")
    private String neoPort;


    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();

        System.out.println(neoUser + " " + neoPass);

        // TODO: Change to the server neo4j config

        // To persist the database, uncomment this section
        //String pathToDb = Paths.get(".").toAbsolutePath().normalize().toString() + "/target//Users/ckchen/Documents/Neo4j/impc.neo4";
        String pathToDb = neo4jDbPath;
        config
//                .driverConfiguration()
//                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver")
//                .setURI("file://" + pathToDb);
//
                .driverConfiguration()
                .setDriverClassName("org.neo4j.ogm.drivers.http.driver.HttpDriver")
                .setURI("http://" + neoUser + ":" + neoPass + "@" + neoServer + ":" + neoPort);

                // not working
//                .driverConfiguration()
//                .setDriverClassName("org.neo4j.ogm.drivers.bolt.driver.BoltDriver")
//                .setURI("bolt://" + neoUser + ":" + neoPass + "@" + neoServer) // .setURI("bolt://neo4j:password@localhost")
//                .setConnectionPoolSize(150);

        System.out.println(config);

        return config;
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(getConfiguration(), "org.mousephenotype.cda.neo4j");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }

}
