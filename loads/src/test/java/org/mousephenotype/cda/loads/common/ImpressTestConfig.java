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

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by mrelac on 16/04/2018.
 */

@Configuration
public class ImpressTestConfig {

	@Value("${datasource.impress.current.jdbc-url}")
	protected String impressCurrentUrl;

	@Value("${datasource.impress.current.username}")
	protected String impressCurrentUsername;

	@Value("${datasource.impress.current.password}")
	protected String impressCurrentPassword;

	@Value("${datasource.impress.previous.jdbc-url}")
	protected String impressPreviousUrl;

	@Value("${datasource.impress.previous.username}")
	protected String impressPreviousUsername;

	@Value("${datasource.impress.previous.password}")
	protected String impressPreviousPassword;



	@Bean
	public DataSource impressCurrentDataSource() {
		return SqlUtils.getConfiguredDatasource(impressCurrentUrl, impressCurrentUsername, impressCurrentPassword);
	}

	@Bean
	public NamedParameterJdbcTemplate jdbcImpressCurrent() {
		return new NamedParameterJdbcTemplate(impressCurrentDataSource());
	}

	@Bean
	public CdaSqlUtils impressCurrentSqlUtils() {
		return new CdaSqlUtils(jdbcImpressCurrent());
	}

	@Bean
	public DataSource impressPreviousDataSource() {
		return SqlUtils.getConfiguredDatasource(impressPreviousUrl, impressPreviousUsername, impressPreviousPassword);
	}

	@Bean
	public NamedParameterJdbcTemplate jdbcImpressPrevious() {
		return new NamedParameterJdbcTemplate(impressPreviousDataSource());
	}

	@Bean
	public CdaSqlUtils impressPreviousSqlUtils() {
		return new CdaSqlUtils(jdbcImpressPrevious());
	}
}