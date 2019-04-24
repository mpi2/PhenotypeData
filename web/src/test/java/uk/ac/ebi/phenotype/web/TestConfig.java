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

package uk.ac.ebi.phenotype.web;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.phenotype.service.UniprotService;

import javax.sql.DataSource;

/**
 * This class acts as a spring bootstrap. No code requiring spring should be placed in this class, as, at this
 * point, spring is not yet initialised.
 * <p/>
 * Created by mrelac on 29/06/2015.
 */

/**
 * IMPORTANT NOTE: In order to run the tests, you must specify the "profile", a directory under the /configfiles
 * resource directory, which must contain a test.properties file. e.g. mvn test -Dprofile=dev
 *
 * Examples: /Users/mrelac/configfiles/beta/test.properties,
 *           /Users/mrelac/configfiles/dev/test.properties,
 *           /net/isilonP/public/rw/homes/tc_mi01/configfiles/beta/test.properties
 *           /net/isilonP/public/rw/homes/tc_mi01/configfiles/dev/test.properties
 */

// NOTE: Don't use @TestPropertySource. Why? See: http://stackoverflow.com/questions/28418071/how-to-override-config-value-from-propertysource-used-in-a-configurationproper

@Configuration
@EnableAutoConfiguration
//@ComponentScan(value = {"org.mousephenotype", "uk.ac.ebi.phenotype"},
//	excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ComponentScanNonParticipant.class)
//)
public class TestConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Value("${datasource.komp2.url}")
//    private String datasourceKomp2Url;
//
//    @Value("${solr.host}")
//    private String solrHost;
//
//    @Value("${cms_base_url}")
//    private String cmsBaseUrl;
//
//
//	@Value("${base_url}")
//	private String baseUrl;
//
//    @Value("${internal_solr_url}")
//    private String internalSolrUrl;
//
//    @PostConstruct
//    public void initialise() {
//        logger.info("dataSource.komp2.url: " + datasourceKomp2Url);
//        logger.info("solr.host:            " + solrHost);
//        logger.info("baseUrl:              " + baseUrl);
//        logger.info("internalSolrUrl:      " + internalSolrUrl);
//    }
//
//	@Bean
//	public PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurer() {
//		return new PropertyPlaceholderConfigurer();
//	}

//    @Bean (name="globalConfiguration")
//    public Map <String, String> globalConfiguration(){
//
//    	Map <String, String> gc = new HashMap<>();
//    	gc.put("baseUrl", "${baseUrl}");
//    	gc.put("cmsBaseUrl", "${cmsBaseUrl}");
//    	gc.put("solrUrl", "${solrUrl}");
//    	gc.put("internalSolrUrl", "${internalSolrUrl}");
//    	gc.put("mediaBaseUrl", "${mediaBaseUrl}");
//    	gc.put("impcMediaBaseUrl", "${impcMediaBaseUrl}");
//    	gc.put("pdfThumbnailUrl", "${pdfThumbnailUrl}");
//    	gc.put("googleAnalytics", "${googleAnalytics}");
//    	gc.put("liveSite", "${liveSite}");
//
//    	return gc;
//    }

//	@Bean
//	@Primary
//	@PersistenceContext(name="komp2Context")
//	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//		emf.setDataSource(komp2DataSource());
//		emf.setPackagesToScan("org.mousephenotype.cda.db.pojo", "org.mousephenotype.cda.db.entity");
//
//		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		emf.setJpaVendorAdapter(vendorAdapter);
//
//		return emf;
//	}

//	@Bean(name = "sessionFactory")
//	public SessionFactory sessionFactory() {
//		SessionFactory sessionFactory = new SessionFactory();
////		sessionFactory.setDataSource(komp2DataSource());
////		sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
//		return sessionFactory;
//	}

//	@Bean(name = "komp2TxManager")
//    @Primary
//	public PlatformTransactionManager txManager() {
//		return new DataSourceTransactionManager(komp2DataSource());
//	}


//	@Bean
//	@ConfigurationProperties(prefix = "datasource.admintools")
//	public DataSource admintoolsDataSource() {
//		return DataSourceBuilder.create().build();
//	}





	//////////////
	// DATASOURCES
	//////////////

	@Value("${datasource.komp2.jdbc-url}")
	private String komp2Url;

	@Value("${datasource.komp2.username}")
	private String username;

	@Value("${datasource.komp2.password}")
	private String password;

	@Bean
//	@Primary
	@ConfigurationProperties("datasource.komp2")
	public DataSource komp2DataSource() {

		DataSource komp2DataSource = SqlUtils.getConfiguredDatasource(komp2Url, username, password);

		return komp2DataSource;
	}


	@Bean
	public UniprotService uniprotService() {
		return new UniprotService();
	}




	///////
	// DAOs
	///////

//	@Bean
//	public ObservationDAO observationDAO() {
//		return new ObservationDAOImpl(sessionFactory());
//	}
}