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

package uk.ac.ebi.phenotype.web.controller.registerinterest;

import org.mousephenotype.cda.ri.core.services.CoreService;
import org.mousephenotype.cda.ri.core.services.GenerateService;
import org.mousephenotype.cda.ri.core.services.SendService;
import org.mousephenotype.cda.ri.core.utils.SqlUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

@Configuration
//@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")

//@PropertySource("${configServerUrl}")
//@PropertySource("http://ves-ebi-d9.ebi.ac.uk:8989/pa/dev")

@EnableAutoConfiguration
public class RegisterInterestConfig {

    // Database properties

    @NotNull
    @Value("${datasource.ri.password}")
    private String dbPassword;

    @NotNull
    @Value("${datasource.ri.username}")
    private String dbUsername;

    @NotNull
    @Value("${datasource.ri.url}")
    private String riDbUrl;

    @Bean
    public NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils(jdbc());
    }

    @Bean
    @Qualifier("riDataSource")
    public DataSource riDataSource() {
        return SqlUtils.getConfiguredDatasource(riDbUrl, dbUsername, dbPassword);
    }


    // e-mail server properties
    @NotNull
    @Value("${mail.smtp.from}")
    private String smtpFrom;

    @NotNull
    @Value("${mail.smtp.host}")
    private String smtpHost;

    @NotNull
    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @NotNull
    @Value("${mail.smtp.replyto}")
    private String smtpReplyto;

    @NotNull
    @Value("${paBaseUrl}")
    String paBaseUrl;

    @NotNull
    @Value("${drupal_base_url}")
    private String drupalBaseUrl;

    @NotNull
    @Value("${recaptcha.public}")
    private String recaptchaPublic;


    @Bean
    public String smtpFrom() {
        return smtpFrom;
    }

    @Bean
    public String smtpHost() {
        return smtpHost;
    }

    @Bean
    public int smtpPort() {
        return smtpPort;
    }

    @Bean
    public String smtpReplyto() {
        return smtpReplyto;
    }

    @Bean
    public String paBaseUrl() {
        return paBaseUrl;
    }


    @Bean
    public String drupalBaseUrl() {
        return drupalBaseUrl;
    }

    @Bean
    public String recaptchaPublic() {
        return recaptchaPublic;
    }

    @Bean
    public CoreService coreService() {
        return new CoreService(new GenerateService(paBaseUrl, sqlUtils()), new SendService(sqlUtils(), smtpHost, smtpPort, smtpFrom, smtpReplyto));
    }
}