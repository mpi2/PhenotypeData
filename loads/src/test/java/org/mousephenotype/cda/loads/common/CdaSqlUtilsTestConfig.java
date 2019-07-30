/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Created by mrelac on 27/09/16.
 */
@Configuration
public class CdaSqlUtilsTestConfig {

	@Bean(name = "cdaSqlUtils")
 	public CdaSqlUtils cdaSqlUtils() {
     return new CdaSqlUtils(jdbcCdaBase());
 }

	@Bean(name = "cdaBaseDataSourceH2")
	@Primary
	public DataSource cdaBaseDataSourceH2() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                                            .ignoreFailedDrops(true)
                                            .setName("cda_base_test")
                                            .build();
	}

	@Bean
	public NamedParameterJdbcTemplate jdbcCdaBase() {
		return new NamedParameterJdbcTemplate(cdaBaseDataSourceH2());
	}


//	@Bean
//	public LocalContainerEntityManagerFactoryBean entityManagerFactoryDcc1() {
//		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//		em.setDataSource(cdaBaseDataSourceH2());
//		em.setPackagesToScan("org.mousephenotype.cda.db.entity", "org.mousephenotype.cda.db.pojo");
//
//		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		em.setJpaVendorAdapter(vendorAdapter);
//
//		return em;
//	}
}