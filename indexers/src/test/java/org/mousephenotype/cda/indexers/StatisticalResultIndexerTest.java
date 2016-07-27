package org.mousephenotype.cda.indexers;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.service.AbstractGenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
@Transactional
public class StatisticalResultIndexerTest implements ApplicationContextAware {

	@Autowired
	DataSource komp2DataSource;

	StatisticalResultsIndexer statisticalResultIndexer;

	ApplicationContext applicationContext;

	@PostConstruct
	void postConstruct() {


		try {

			// Use Spring to wire up the dependencies
			statisticalResultIndexer = StatisticalResultsIndexer.class.newInstance();
			applicationContext.getAutowireCapableBeanFactory().autowireBean(statisticalResultIndexer);

			statisticalResultIndexer.setConnection(komp2DataSource.getConnection());
			statisticalResultIndexer.setPipelineMap(IndexerMap.getImpressPipelines(komp2DataSource.getConnection()));
			statisticalResultIndexer.setProcedureMap(IndexerMap.getImpressProcedures(komp2DataSource.getConnection()));
			statisticalResultIndexer.setParameterMap(IndexerMap.getImpressParameters(komp2DataSource.getConnection()));
			statisticalResultIndexer.populateBiologicalDataMap();
			statisticalResultIndexer.populateResourceDataMap();
			statisticalResultIndexer.populateSexesMap();
			statisticalResultIndexer.populateParameterMpTermMap();
			statisticalResultIndexer.populateEmbryoSignificanceMap();

		} catch (IllegalAccessException | InstantiationException | SQLException e) {
			e.printStackTrace();
			assert (statisticalResultIndexer.getConnection() != null);
		}
	}

	@Test
	public void getRrPlusResults() throws Exception {

		List<StatisticalResultDTO> results = statisticalResultIndexer.getReferenceRangePlusResults().call();
		assert (results.size() > 100);

		for (StatisticalResultDTO result : results) {

			// Every document that has an MP term must also have at least one top level MP term
			if (result.getMpTermId() != null) {
				assert(result.getTopLevelMpTermId() != null);
			}
		}
	}


	@Test
	public void getEmbryoResults() throws Exception {

		List<StatisticalResultDTO> results = statisticalResultIndexer.getEmbryoResults().call();
		assert (results.size() > 100);
	}

	@Test
	public void getSignificanceField() throws Exception {


		// Results that have a p-value
		List<Callable<List<StatisticalResultDTO>>> resultGenerators = Arrays.asList(
			statisticalResultIndexer.getReferenceRangePlusResults(),
			statisticalResultIndexer.getUnidimensionalResults(),
			statisticalResultIndexer.getCategoricalResults()
		);

		for (Callable<List<StatisticalResultDTO>> r : resultGenerators) {

			System.out.println("Assessing result of type " + r.getClass().getSimpleName());
			List<StatisticalResultDTO> results = r.call();
			for (StatisticalResultDTO result : results) {

				// PhenStat results
				if (result.getStatus().equals("Success") && result.getNullTestPValue()!=null) {
					if (result.getNullTestPValue() <= AbstractGenotypePhenotypeService.P_VALUE_THRESHOLD) {
						assert (result.getSignificant());
					} else {
						assert ( ! result.getSignificant());
					}
				}

				// Wilcoxon and fisher's exact results
				if (result.getStatus().equals("Success") && result.getNullTestPValue()==null) {
					if (result.getpValue() <= AbstractGenotypePhenotypeService.P_VALUE_THRESHOLD) {
						assert (result.getSignificant());
					} else {
						assert( ! result.getSignificant());
					}
				}

				// Assert that failed results have a null signficance
				if ( ! result.getStatus().equals("Success")) {
					assert (result.getSignificant() == null);
				}

			}
		}

		// Results that do not have a p-value
		resultGenerators = Arrays.asList(
			statisticalResultIndexer.getViabilityResults(),
			statisticalResultIndexer.getFertilityResults(),
			statisticalResultIndexer.getEmbryoViabilityResults(),
			statisticalResultIndexer.getEmbryoResults()
		);

		for (Callable<List<StatisticalResultDTO>> r : resultGenerators) {

			System.out.println("Assessing result of type " + r.getClass().getSimpleName());
			List<StatisticalResultDTO> results = r.call();
			for (StatisticalResultDTO result : results) {

				if (result.getStatus().equals("Success")) {
					assert(result.getSignificant());
				}
			}
		}


	}

	@Ignore
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
