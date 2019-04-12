/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.db.owl;


import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

@Configuration
public class OntologyParserTestConfig {

    @Value("${datasource.komp2.jdbc-url}")
    private String komp2Url;

    @NotNull
    @Value("${datasource.komp2.username}")
    private String username;

    @NotNull
    @Value("${datasource.komp2.password}")
    private String password;

    @Bean
    @Primary
    @ConfigurationProperties("datasource.komp2")
    public DataSource dataSource() {

        DataSource dataSource = SqlUtils.getConfiguredDatasource(komp2Url, username, password);

        return dataSource;
    }
}