package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAOImpl;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.*;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigSolr.class} )
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
public class ExpressionServiceTest {


	@Autowired
	private ExpressionService expressionService;

	@Autowired
	ExperimentService experimentService;


	@Test
	public void getLacDataForAnatomogram() {

		expressionService.initialiseAbnormalOntologyMaps();
		String geneAccession = "MGI:1922730";
		try {
			List<Count> parameterCounts = expressionService.getLaczCategoricalParametersForGene(geneAccession);
			List<AnatomogramDataBean> beans = expressionService.getAnatomogramDataBeans(parameterCounts);
			for (AnatomogramDataBean bean : beans) {
				System.out.println("AnatomogramDataBean" + bean);
			}


			Map<String, Long> anatomogramDataBeans = expressionService.getLacSelectedTopLevelMaCountsForAnatomogram(beans);
			for (String topMa : anatomogramDataBeans.keySet()) {
				System.out.println("topMa=" + topMa + " total count " + anatomogramDataBeans.get(topMa));
			}

		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void getDataForAnatomyPage() throws SolrServerException {
		expressionService.getFacets("MA:0000004");
	}


}
