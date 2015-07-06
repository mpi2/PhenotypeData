package org.mousephenotype.cda.solr;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * <p>
 * This will also import the data specified in the sql/test-data.sql file.
 */

@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan("org.mousephenotype.cda")
@PropertySource("classpath:application.properties")
public class TestConfigSolr {


}
