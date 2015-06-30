package org.mousephenotype.cda;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;


/**
 * Created by jmason on 29/06/2015.
 */
@Configuration
@ComponentScan("org.mousephenotype.cda")
public class TestConfig {

	@Bean
	@Qualifier("komp2DataSource")
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
//			.addScript("classpath:sql/schema.sql")
//			.addScript("classpath:sql/test-data.sql")
			.build();
	}

	@Bean
	@Qualifier("admintoolsDataSource")
	public DataSource admintoolsDataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
				//			.addScript("classpath:sql/schema.sql")
				//			.addScript("classpath:sql/test-data.sql")
			.build();
	}


}
