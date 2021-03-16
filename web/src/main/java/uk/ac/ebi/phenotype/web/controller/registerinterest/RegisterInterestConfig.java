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

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.ri.entities.SmtpParameters;
import org.mousephenotype.cda.ri.services.CoreService;
import org.mousephenotype.cda.ri.services.GenerateService;
import org.mousephenotype.cda.ri.services.SendService;
import org.mousephenotype.cda.ri.utils.RiSqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration
public class RegisterInterestConfig {

    // Database properties

    @Value("${datasource.ri.jdbc-url}")
    private String riDbUrl;

    @Value("${datasource.ri.username}")
    private String dbUsername;

    @Value("${datasource.ri.password}")
    private String dbPassword;

    @Bean
    public NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public RiSqlUtils riSqlUtils() {
        return new RiSqlUtils(jdbc());
    }

    @Bean
    public DataSource riDataSource() {
        return SqlUtils.getConfiguredDatasource(riDbUrl, dbUsername, dbPassword);
    }


    // e-mail server properties
    @Value("${mail.smtp.from}")
    private String smtpFrom;

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private Integer smtpPort;

    @Value("${mail.smtp.replyto}")
    private String smtpReplyto;

    @Value("${cms_base_url}")
    private String cmsBaseUrl;

    @Value("${recaptcha.public}")
    private String recaptchaPublic;

    @Value("${sessionTimeoutInMinutes}")
    private Integer sessionTimeoutInMinutes;


    @Bean
    public String smtpFrom() {
        return smtpFrom;
    }

    @Bean
    public SmtpParameters mailServerParameters() {
        return new SmtpParameters(smtpHost, smtpPort, smtpFrom, smtpReplyto);
    }


    @Bean
    public String cmsBaseUrl() {
        return cmsBaseUrl;
    }

    @Bean
    public String recaptchaPublic() {
        return recaptchaPublic;
    }

    @Bean Integer sessionTimeoutInMinutes() {
        return sessionTimeoutInMinutes;
    }

    @Bean
    public CoreService coreService() {
        return new CoreService(new GenerateService(riSqlUtils()), new SendService(riSqlUtils()));
    }
}