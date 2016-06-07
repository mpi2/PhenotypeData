package org.mousephenotype.cda.indexers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author jmason
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigIndexers.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile}/test.properties"})
@Transactional
public class StatisticalResultIndexerTest {

	@Autowired
	DataSource komp2DataSource;

	@Autowired
	StatisticalResultsIndexer statisticalResultIndexer;

	@PostConstruct
	void postConstruct() {
		try {

			statisticalResultIndexer.setConnection(komp2DataSource.getConnection());
			statisticalResultIndexer.setPipelineMap(IndexerMap.getImpressPipelines(komp2DataSource.getConnection()));
			statisticalResultIndexer.setProcedureMap(IndexerMap.getImpressProcedures(komp2DataSource.getConnection()));
			statisticalResultIndexer.setParameterMap(IndexerMap.getImpressParameters(komp2DataSource.getConnection()));
			statisticalResultIndexer.populateBiologicalDataMap();
			statisticalResultIndexer.populateResourceDataMap();
			statisticalResultIndexer.populateSexesMap();
			statisticalResultIndexer.populateParameterMpTermMap();
			statisticalResultIndexer.populateEmbryoSignificanceMap();

		} catch (SQLException e) {
			e.printStackTrace();
			assert (statisticalResultIndexer.getConnection() != null);
		}
	}

	@Test
	public void getEmbryoResults() throws Exception {

		List<StatisticalResultDTO> results = statisticalResultIndexer.getEmbryoResults().call();
		assert (results.size() > 100);
	}

	@Test
	public void resultsUniqueIds() throws Exception {

		List<String> ids = new ArrayList<>();
		List<Callable<List<StatisticalResultDTO>>> resultGenerators = Arrays.asList(
			statisticalResultIndexer.getViabilityResults(),
			statisticalResultIndexer.getFertilityResults(),
			statisticalResultIndexer.getReferenceRangePlusResults(),
			statisticalResultIndexer.getUnidimensionalResults(),
			statisticalResultIndexer.getCategoricalResults(),
			statisticalResultIndexer.getEmbryoViabilityResults(),
			statisticalResultIndexer.getEmbryoResults()
		);

		for (Callable<List<StatisticalResultDTO>> r : resultGenerators) {

			System.out.println("Getting ids from " + r.getClass());
			ids.addAll(r.call().stream().map(StatisticalResultDTO::getDocId).collect(Collectors.toList()));

		}

		Set<String> uniques = new HashSet<>();
		Set<String> diff = ids
			.stream()
			.filter(e -> !uniques.add(e))
			.collect(Collectors.toSet());

		assert (diff.isEmpty());
		System.out.println("All generated IDs unique");
	}

}
