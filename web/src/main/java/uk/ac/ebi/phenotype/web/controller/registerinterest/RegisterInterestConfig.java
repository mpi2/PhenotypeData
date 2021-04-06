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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "org.mousephenotype.cda.solr",
        "org.mousephenotype.cda.ri.config",
        "org.mousephenotype.cda.ri.services"})
public class RegisterInterestConfig {

    @Value("${cms_base_url}")
    private String cmsBaseUrl;

    @Value("${recaptcha.public}")
    private String recaptchaPublic;

    @Value("${sessionTimeoutInMinutes}")
    private Integer sessionTimeoutInMinutes;

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
}