package org.mousephenotype.cda.indexers;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.config.TestConfigIndexers;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.solr.service.AbstractGenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * @author jmason
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfigIndexers.class})
public class StatisticalResultsIndexerTest implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(StatisticalResultsIndexerTest.class);

    @Autowired
    private DataSource komp2DataSource;

    private StatisticalResultsIndexer statisticalResultIndexer;

    private ApplicationContext applicationContext;

    @Autowired
    private OntologyParserFactory ontologyParserFactory;

    @PostConstruct
    void postConstruct() {

        System.out.println("Doing post construct");

        try {

            // Use Spring to wire up the dependencies
            statisticalResultIndexer = StatisticalResultsIndexer.class.newInstance();
            applicationContext.getAutowireCapableBeanFactory().autowireBean(statisticalResultIndexer);

            statisticalResultIndexer.setPipelineMap(IndexerMap.getImpressPipelines(komp2DataSource.getConnection()));
            statisticalResultIndexer.setProcedureMap(IndexerMap.getImpressProcedures(komp2DataSource.getConnection()));
            statisticalResultIndexer.setParameterMap(IndexerMap.getImpressParameters(komp2DataSource.getConnection()));
            statisticalResultIndexer.populateBiologicalDataMap();
            statisticalResultIndexer.populateResourceDataMap();
            statisticalResultIndexer.populateSexesMap();
            statisticalResultIndexer.populateParameterMpTermMap();
            statisticalResultIndexer.populateEmbryoSignificanceMap();

            statisticalResultIndexer.setOntologyParserFactory(ontologyParserFactory);
            statisticalResultIndexer.setMpParser(ontologyParserFactory.getMpParser());
            statisticalResultIndexer.setMpMaParser(ontologyParserFactory.getMpParser());
            statisticalResultIndexer.setMaParser(ontologyParserFactory.getMpParser());
            statisticalResultIndexer.setSAVE(Boolean.FALSE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test @Ignore
    public void getEmbryoSignificantCalls() {
        
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        Map<String, String> embryoSignificantResults = statisticalResultIndexer.getEmbryoSignificantResults();

        Set<String> parameters = embryoSignificantResults
                .keySet()
                .stream()
                .map(x -> x.substring(0, StringUtils.ordinalIndexOf(x, "_", 4)))
                .collect(Collectors.toSet());

        assert (parameters.contains("IMPC_EVM_001_001"));
        assert (parameters.contains("IMPC_EVL_001_001"));

        Map<String, Integer> parameterGroups = new HashMap<>();

        for (String parameter : parameters) {
            String group = parameter.substring(0, StringUtils.ordinalIndexOf(parameter, "_", 2));
            if (!parameterGroups.containsKey(group)) {
                parameterGroups.put(group, 0);
            }
            parameterGroups.put(group, parameterGroups.get(group) + 1);
        }

        System.out.println("Parameter group: Count of parameters");
        for (String group : parameterGroups.keySet()) {
            System.out.println("  " + group + ": " + parameterGroups.get(group));
        }

    }


    @Test @Ignore
    public void getRrPlusResults() {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        List<StatisticalResultDTO> results = statisticalResultIndexer.getReferenceRangePlusResults().call();
        assert (results.size() > 100);

        for (StatisticalResultDTO result : results) {

            // Every document that has an MP term must also have at least one top level MP term
            if (result.getMpTermId() != null) {
                assert (result.getTopLevelMpTermId() != null);
            }
        }
    }


    @Test @Ignore
    public void getEmbryoResults() {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        List<StatisticalResultDTO> results = statisticalResultIndexer.getEmbryoResults().call();
        assert (results.size() > 100);
    }

    @Test @Ignore
    public void getEmbryoViabilityResults() {
        
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");
        
        List<StatisticalResultDTO> results = statisticalResultIndexer.getEmbryoViabilityResults().call();
        assert (results.size() > 100);
    }


    @Test @Ignore
    public void getGrossPathologyResults() {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        List<StatisticalResultDTO> results = statisticalResultIndexer.getGrossPathologyResults().call();
        assert (results.size() > 100);

        List<String> ids = new ArrayList<>();
        ids.addAll(results.stream().map(StatisticalResultDTO::getDocId).collect(Collectors.toList()));

        Set<String> uniques = new HashSet<>();
        Set<String> diff = ids
                .stream()
                .filter(e -> !uniques.add(e))
                .collect(Collectors.toSet());

        if (!diff.isEmpty()) {
            System.out.println("Diff is : " + StringUtils.join(diff, ", "));
            List<String> diffList = new ArrayList<>(diff);
            List<StatisticalResultDTO> duplicated = results.stream().filter(p -> p.getDocId().equals(diffList.get(0))).collect(Collectors.toList());

            System.out.println(duplicated);
        }

        assert (diff.isEmpty());
        System.out.println("All generated IDs unique");
    }


    @Test @Ignore
    public void getBothCategoricalAndUnidimResults() {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");


        List<Callable<List<StatisticalResultDTO>>> resultGenerators = Arrays.asList(
                statisticalResultIndexer.getCategoricalResults(),
                statisticalResultIndexer.getUnidimensionalResults()
        );

        ExecutorService pool = Executors.newFixedThreadPool(4);
        List<Future<List<StatisticalResultDTO>>> producers = new ArrayList<>();

        for (Callable<List<StatisticalResultDTO>> r : resultGenerators) {

            Future<List<StatisticalResultDTO>> future = pool.submit(r);
            producers.add(future);

        }

        int count = 0;
        for (Future<List<StatisticalResultDTO>> future : producers) {

            List<StatisticalResultDTO> beans;

            try {
                beans = future.get();

                if (beans != null && beans.size() > 0) {
                    count += beans.size();
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }

        // Stop threadpool
        pool.shutdown();

        assert (count > 1000000);

    }


    @Test @Ignore
    public void getCategoricalResults() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        StatisticalResultsIndexer.CategoricalResults r = statisticalResultIndexer.getCategoricalResults();

        System.out.println("Assessing result of type " + r.getClass().getSimpleName());
        List<StatisticalResultDTO> results = r.call();

        assert (results.size() > 100000);

    }


    @Test @Ignore
    public void getUnidmensionalResultsAllHaveBiologicalModel() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        StatisticalResultsIndexer.UnidimensionalResults r = statisticalResultIndexer.getUnidimensionalResults();

        System.out.println("Assessing result of type " + r.getClass().getSimpleName());
        List<StatisticalResultDTO> results = r.call();

        Optional<StatisticalResultDTO> missing = results.stream()
                .filter(x -> x.getMutantBiologicalModelId() == null || x.getMutantBiologicalModelId() == 0)
                .findFirst();
        missing.ifPresent(x -> {
            System.out.println("Unidimensional stats result");
            System.out.println("Found first instance with no biological model for: " + x);
        });

        assert (results.size() > 100000);

    }

    @Test @Ignore
    public void getCategoricalResultsAllHaveBiologicalModel() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        StatisticalResultsIndexer.CategoricalResults r = statisticalResultIndexer.getCategoricalResults();

        System.out.println("Assessing result of type " + r.getClass().getSimpleName());
        List<StatisticalResultDTO> results = r.call();

        results.forEach(x -> {
            if (x.getMutantBiologicalModelId() == null || x.getMutantBiologicalModelId() == 0) {
                System.out.println("Cannot find biological model for Categorical stats result: " + x);
            }
        });

        assert (results.size() > 100000);

    }


    @Test @Ignore
    public void getCategoricalResultsForAkt2() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        StatisticalResultsIndexer.CategoricalResults r = statisticalResultIndexer.getCategoricalResults();

        System.out.println("Assessing result of type " + r.getClass().getSimpleName());
        List<StatisticalResultDTO> results = r.call();

        final Set<String> topLevelMpTermIds = results.stream()
                .filter(x -> x.getColonyId().equals("clone 1"))
                .map(StatisticalResultDTO::getTopLevelMpTermId)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toSet());

        Set<String> shouldHave = new HashSet<>();
        shouldHave.add("MP:0005378");
        shouldHave.add("MP:0005376");
        shouldHave.add("MP:0005375");
        shouldHave.add("MP:0005386");
        shouldHave.add("MP:0003631");
        shouldHave.add("MP:0005390");
        shouldHave.add("MP:0005387");
        shouldHave.add("MP:0005397");

        logger.info("Should have set contains: {}", StringUtils.join(shouldHave, ", "));
        logger.info("Does   have set contains: {}", StringUtils.join(topLevelMpTermIds, ", "));

        // Sets should be equal
        assertTrue(shouldHave.containsAll(topLevelMpTermIds));
        assertTrue(topLevelMpTermIds.containsAll(shouldHave));

    }

    @Test @Ignore
    public void getUnidimResults() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        StatisticalResultsIndexer.UnidimensionalResults r = statisticalResultIndexer.getUnidimensionalResults();

        System.out.println("Assessing result of type " + r.getClass().getSimpleName());
        List<StatisticalResultDTO> results = r.call();

        assert (results.size() > 100000);

    }

    @Test @Ignore
    public void getUnidimResultsWithStage() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        StatisticalResultsIndexer.UnidimensionalResults r = statisticalResultIndexer.getUnidimensionalResults();

        List<StatisticalResultDTO> results = r.call();
        for (StatisticalResultDTO result : results) {

            try {
                assert (result.getLifeStageAcc() != null);
            } catch (AssertionError e) {
                System.out.println("  Warning: Life stage is not set for doc: \n" + result);
            }

        }

        assert (results.size() > 100000);

    }

    @Test @Ignore
    public void getIMMResultsForMpExistenceTest() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        StatisticalResultsIndexer.ReferenceRangePlusResults r = statisticalResultIndexer.getReferenceRangePlusResults();

        System.out.println("Assessing result of type " + r.getClass().getSimpleName());
        List<StatisticalResultDTO> results = r.call();

        StatisticalResultDTO theOneImLookingFor = null;

        for (StatisticalResultDTO result : results) {

            if (result.getColonyId().equals("JR24517") && result.getParameterStableId().equals("IMPC_IMM_006_001")) {
                theOneImLookingFor = result;
            }

        }

        System.out.println(theOneImLookingFor);

        assert theOneImLookingFor != null;
        assert theOneImLookingFor.getMpTermId() != null;
        assert results.size() > 1000;

    }


    @Test @Ignore
    public void testRrPlusResultsForTopLevelMpExistence() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

        StatisticalResultsIndexer.ReferenceRangePlusResults r = statisticalResultIndexer.getReferenceRangePlusResults();

        System.out.println("Assessing result of type " + r.getClass().getSimpleName());
        List<StatisticalResultDTO> results = r.call();

        List<StatisticalResultDTO> missing = new ArrayList<>();

        for (StatisticalResultDTO result : results) {

            if (result.getTopLevelMpTermId() == null) {
                missing.add(result);
            }

            if (Math.random()<0.0001) {
                System.out.println("Random result");
                System.out.println(String.format("doc id: %s, colony:%s, top level ids: [%s], top level names: [%s]", result.getDocId(), result.getColonyId(), StringUtils.join(result.getTopLevelMpTermId(), ","), StringUtils.join(result.getTopLevelMpTermName(), ",")));
            }

        }

        System.out.println("Missing top level terms " + missing.size());

        assert missing.size() < 1;

    }


    @Test @Ignore
    public void getSignificanceField() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");


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
                if (result.getStatus().equals("Success") && result.getNullTestPValue() != null) {
                    if (result.getNullTestPValue() <= AbstractGenotypePhenotypeService.P_VALUE_THRESHOLD) {
                        assert (result.getSignificant());
                    } else {
                        assert (!result.getSignificant());
                    }
                }

                // Wilcoxon and fisher's exact results
                if (result.getStatus().equals("Success") && result.getNullTestPValue() == null) {
                    if (result.getpValue() <= AbstractGenotypePhenotypeService.P_VALUE_THRESHOLD) {
                        assert (result.getSignificant());
                    } else {
                        assert (!result.getSignificant());
                    }
                }

                // Assert that failed results have a null signficance
                if (!result.getStatus().equals("Success")) {
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
                    assert (result.getSignificant());
                }
            }
        }


    }

    @Test @Ignore
    public void resultsUniqueIds() throws Exception {

        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("\n-------------------  " + testName + "-------------------");

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