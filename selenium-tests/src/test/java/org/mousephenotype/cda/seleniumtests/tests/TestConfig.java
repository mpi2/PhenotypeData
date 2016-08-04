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

package org.mousephenotype.cda.seleniumtests.tests;

/**
 * This class acts as a spring bootstrap. No code requiring spring should be placed in this class, as, at this
 * point, spring is not yet initialised.
 *
 * Created by mrelac on 29/06/2015.
 */

import org.mousephenotype.cda.seleniumtests.exception.TestException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * IMPORTANT NOTE: In order to run the tests, you must specify the "profile", a directory under the /configfiles
 * resource directory, which must contain a test.properties file.
 *
 * Examples: /Users/mrelac/configfiles/beta/test.properties,
 *           /Users/mrelac/configfiles/dev/test.properties,
 *           /net/isilonP/public/rw/homes/tc_mi01/configfiles/beta/test.properties
 *           /net/isilonP/public/rw/homes/tc_mi01/configfiles/dev/test.properties
 */

// NOTE: Don't use @TestPropertySource. Why? See: http://stackoverflow.com/questions/28418071/how-to-override-config-value-from-propertysource-used-in-a-configurationproper

@Configuration
@ComponentScan({"org.mousephenotype.cda"})
@PropertySource("file:${user.home}/configfiles/${profile}/test.properties")
@EnableAutoConfiguration
public class TestConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${datasource.komp2.url}")
    private String datasourceKomp2Url;


    @Value("${solr.host}")
    private String solrHost;

    @Value("${baseUrl}")
    private String baseUrl;

    @Value("${internalSolrUrl}")
    private String internalSolrUrl;

	@Value("${seleniumUrl}")
 	private String seleniumUrl;

	@Value("${browserName}")
 	private String browserName;

    private boolean isPostConstruct = false;

    @PostConstruct
    private void initialise() {
        isPostConstruct = true;
    }

    private void logParameters(RemoteWebDriver privateDriver) throws TestException {
        logger.info("dataSource.komp2.url: " + datasourceKomp2Url);
        logger.info("solr.host:            " + solrHost);
        logger.info("baseUrl:              " + baseUrl);
        logger.info("internalSolrUrl:      " + internalSolrUrl);
		logger.info("seleniumUrl:          " + seleniumUrl);

        logger.info("browserName:          " + privateDriver.getCapabilities().getBrowserName());
        logger.info("version:              " + privateDriver.getCapabilities().getVersion());
        logger.info("platform:             " + privateDriver.getCapabilities().getPlatform().name());
    }

	@Bean
	@Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
	public DataSource komp2DataSource() {
        DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}

	@Bean
	@Primary
	@PersistenceContext(name="komp2Context")
	public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder){
		return builder
			.dataSource(komp2DataSource())
			.packages("org.mousephenotype.cda.db")
			.persistenceUnit("komp2")
			.build();
	}

	@Bean(name = "sessionFactory")
	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
		HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
		factory.setEntityManagerFactory(emf);
		return factory;
	}

	@Bean(name = "komp2TxManager")
    @Primary
	public PlatformTransactionManager txManager() {
		return new DataSourceTransactionManager(komp2DataSource());
	}

    @Bean(name = "driver")
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public RemoteWebDriver driver() throws TestException {
        RemoteWebDriver retVal = null;

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

        try {
            retVal = new RemoteWebDriver(new URL(seleniumUrl), desiredCapabilities);
            if (isPostConstruct) {
                logParameters(retVal);
            }
        } catch (MalformedURLException e) {
            throw new TestException("Unable to get driver from wrapper. Reason: " + e.getLocalizedMessage());
        }

        return retVal;
    }

	@Bean
	@ConfigurationProperties(prefix = "datasource.admintools")
	public DataSource admintoolsDataSource() {
		return DataSourceBuilder.create().build();
	}
}
