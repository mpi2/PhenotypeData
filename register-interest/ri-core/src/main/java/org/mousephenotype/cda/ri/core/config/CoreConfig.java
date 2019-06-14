/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.ri.core.config;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.ri.core.services.CoreService;
import org.mousephenotype.cda.ri.core.services.GenerateService;
import org.mousephenotype.cda.ri.core.services.SendService;
import org.mousephenotype.cda.ri.core.utils.RiSqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
@EnableAutoConfiguration
public class CoreConfig {

    @Value("${paBaseUrl}")
    private String paBaseUrl;

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @Value("${mail.smtp.from}")
    private String smtpFrom;

    @Value("${mail.smtp.replyto}")
    private String smtpReplyto;


    @Bean
    public String paBaseUrl() {
        return paBaseUrl;
    }

    @Bean
    public NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public RiSqlUtils riSqlUtils() {
        return new RiSqlUtils(jdbc());
    }

    @Bean
    public CoreService coreService() {
        return new CoreService(generateService(), sendService());
    }

    @Bean
    public GenerateService generateService() {
        return new GenerateService(paBaseUrl, riSqlUtils());
    }

    @Bean
    public SendService sendService() {
        return new SendService(riSqlUtils(), smtpHost, smtpPort, smtpFrom, smtpReplyto);
    }

    @Value("${datasource.ri.url}")
    String riUrl;

    @Value("${datasource.ri.username}")
    String username;

    @Value("${datasource.ri.password}")
    String password;

    @Bean
    public DataSource riDataSource() {
        return SqlUtils.getConfiguredDatasource(riUrl, username, password);
    }
}