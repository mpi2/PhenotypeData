/*******************************************************************************
 *  Copyright (c) 2013 - 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.selenium.config;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.selenium.exception.TestException;
import org.mousephenotype.cda.solr.service.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;


@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@ComponentScan
public class TestConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${datasource.komp2.jdbc-url}")
    private String datasourceKomp2Url;

    @Value("${base_url}")
    private String baseUrl;

    @Value("${internal_solr_url}")
    private String internalSolrUrl;

	@Value("${seleniumUrl}")
 	private String seleniumUrl;

	@Value("${browserName}")
 	private String browserName;


    @PostConstruct
    private void initialise() throws TestException {
        logParameters();
    }

    private void logParameters()  throws TestException {
        logger.info("dataSource.komp2.jdbc-url: " + datasourceKomp2Url);
        logger.info("baseUrl:                   " + baseUrl);
        logger.info("internalSolrUrl:           " + internalSolrUrl);
		logger.info("seleniumUrl:               " + seleniumUrl);

        logger.info("browserName:          " + desiredCapabilities().getBrowserName());
        logger.info("version:              " + desiredCapabilities().getVersion());
        logger.info("platform:             " + desiredCapabilities().getPlatform().name());
    }


    @Bean
    public DesiredCapabilities desiredCapabilities() throws TestException {

        DesiredCapabilities desiredCapabilities;

        switch (browserName.toLowerCase()) {
            case "chrome":
                desiredCapabilities = DesiredCapabilities.chrome();
                break;

            case "edge":
                desiredCapabilities = DesiredCapabilities.edge();
                break;

            case "firefox":
                desiredCapabilities = DesiredCapabilities.firefox();
                break;

            case "internetexplorer":
            case "internet explorer":
            case "ie":
            case "iexplorer":
                desiredCapabilities = DesiredCapabilities.internetExplorer();
                break;

            case "opera":
                desiredCapabilities = DesiredCapabilities.operaBlink();
                break;

            case "safari":
                desiredCapabilities = DesiredCapabilities.safari();
                break;

            default:
                throw new TestException("Unknown browserName '" + browserName + "'");
        }
        return desiredCapabilities;
    }


    //////////////
    // DATASOURCES
    //////////////

    @Value("${datasource.komp2.jdbc-url}")
    protected String cdabaseUrl;

    @Value("${datasource.komp2.username}")
    protected String cdabaseUsername;

    @Value("${datasource.komp2.password}")
    protected String cdabasePassword;

    @Bean
    public DataSource komp2DataSource() {
        return SqlUtils.getConfiguredDatasource(cdabaseUrl, cdabaseUsername, cdabasePassword);
    }


    // Read only solr servers

    @Bean
    public HttpSolrClient experimentCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/experiment").build();
    }

    // gene
    @Bean
    public HttpSolrClient geneCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/gene").build();
    }

    // genotype-phenotype
    @Bean
    public HttpSolrClient genotypePhenotypeCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/genotype-phenotype").build();
    }

    // mp
	@Bean
    public HttpSolrClient mpCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/mp").build(); }

    // pipeline
    @Bean
    public HttpSolrClient pipelineCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/pipeline").build();
    }


    ///////////
    // SERVICES
    ///////////

    @Bean
    public GeneService geneService() {
        return new GeneService(geneCore(), impressService());
    }

    @Bean
    public ImpressService impressService() {
        return new ImpressService(pipelineCore());
    }

    @Bean
    public MpService mpService() {
        return new MpService(mpCore());
    }

    @Bean
    public ObservationService observationService() {
        return new ObservationService(experimentCore());
    }


    ////////////////
    // Miscellaneous
    ////////////////

//    @Bean(name = "sessionFactoryHibernate")
//    public SessionFactory sessionFactory() {
//
//        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
//        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
//        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");
//
//        return sessionBuilder.buildSessionFactory();
//    }
}
