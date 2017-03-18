package uk.ac.ebi.phenotype.repository;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Paths;

/**
 * Created by jmason on 18/03/2017.
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "uk.ac.ebi.phenotype.repository")
@PropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
public class Neo4jConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
    public DataSource komp2DataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.admintools")
    public DataSource admintoolsDataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();

        // To persist the database, uncomment this section
        String pathToDb = Paths.get(".").toAbsolutePath().normalize().toString() + "/target/load_impc_graph.db";
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
