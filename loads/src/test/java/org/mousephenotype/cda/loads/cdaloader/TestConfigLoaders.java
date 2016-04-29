package org.mousephenotype.cda.loads.cdaloader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * <p>
 * This will also import the data specified in the sql/test-data.sql file.
 */

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan({"org.mousephenotype.loads", "org.mousephenotype.cda.loads", "org.mousephenotype.cda.loads.cdaloader"})
public class TestConfigLoaders {

	@Value("http:${solrUrl}")
	String solrBaseUrl;

}
