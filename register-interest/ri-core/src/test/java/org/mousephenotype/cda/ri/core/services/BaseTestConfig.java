/*******************************************************************************
 *  Copyright © 2018 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.ri.core.services;

import org.mousephenotype.cda.ri.core.entities.SmtpParameters;
import org.mousephenotype.cda.ri.core.utils.RiSqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Created by mrelac on 16/08/2018
 */
@Configuration
@ComponentScan(basePackages = { "org.mousephenotype.cda.ri.core" })
public class BaseTestConfig {

    public static final String PA_BASE_URL = "https://dev.mousephenotype.org/data";

    @Bean
    public DataSource riDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("ri")
                .build();
    }

    @Bean
    protected RiSqlUtils riSqlUtils() {
        return new RiSqlUtils(jdbc());
    }

    @Bean
    protected NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public String paBaseUrl() {
        return PA_BASE_URL;
    }


    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @Value("${mail.smtp.from}")
    private String smtpFrom;

    @Value("${mail.smtp.replyto}")
    private String smtpReplyto;

    @Bean
    public SmtpParameters smtpParameters() {
        return new SmtpParameters(smtpHost, smtpPort, smtpFrom, smtpReplyto);
    }
}