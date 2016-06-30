package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Created by jmason on 30/06/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigIndexers.class} )
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class PhenodigmIndexerTest {

	private PhenodigmIndexer phenodigmIndexer;

	@Autowired
	@Qualifier("phenodigmIndexing")
	private SolrServer phenodigmIndexing;

	@Autowired
	@Qualifier("phenodigmDataSource")
	private DataSource phenodigmDataSource;

	@Before
	public void setUp() throws Exception {
		phenodigmIndexer = new PhenodigmIndexer(phenodigmIndexing, phenodigmDataSource);
	}


	@Test
	public void getDiseasePhenotypeMap() throws Exception {

		Map<String, Set<String>> diseasePhenotypeMap = phenodigmIndexer.getDiseasePhenotypeMap();

		assertThat(diseasePhenotypeMap.size(), greaterThan(50));

		System.out.println(diseasePhenotypeMap);
	}

}
