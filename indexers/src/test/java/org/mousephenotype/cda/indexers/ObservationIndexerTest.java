package org.mousephenotype.cda.indexers;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.WeightMap;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.service.OntologyBean;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * These tests take forever to run and consume more memory than is available on developer's machines. Ignore them.
 */
@RunWith(SpringRunner.class)
//@ComponentScan
@SpringBootTest(classes = {ObservationIndexerTestConfig.class})
public class ObservationIndexerTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Connection connection;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    private ObservationIndexer observationIndexer;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "classpath:sql/h2/schema.sql",
                "classpath:sql/h2/ImpressSchema.sql",
                "classpath:sql/h2/H2ReplaceDateDiff.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }








    @Test
    public void testGetOntologyParameterSubTerms() throws SQLException {

        Map<Long, List<OntologyBean>> map = IndexerMap.getOntologyParameterSubTerms(connection);

        boolean found = false;
        for (Long mapId : map.keySet()) {
            List<OntologyBean> list = map.get(mapId);
            if (list.size() > 1) {

                System.out.println("Found an observation with more than one ontology term association");
                System.out.println("For map ID " + mapId + ": " + StringUtils.join(list.stream().map(OntologyBean::getName).collect(Collectors.toList() ), ", "));

                list.stream().forEach(x -> {
                    System.out.println(x);
                });

                for (OntologyBean x : list) {
                    System.out.println(x);
                }

                found = true;
                break;
            }
        }

        assert (found == true);

    }

    @Test
    public void testPopulateBiologicalDataMap() throws Exception {
        observationIndexer.initialise();

        observationIndexer.populateBiologicalDataMap(connection);
        Map<String, ObservationIndexer.BiologicalDataBean> bioDataMap = observationIndexer.getBiologicalData();

        Assert.assertTrue(bioDataMap.size() > 1000);
	    logger.info("Size of biological data map {}", bioDataMap.size());

	    for (ObservationIndexer.BiologicalDataBean biologicalDataBean : bioDataMap.values()) {

	    	if ( ! biologicalDataBean.sampleGroup.equals("control")) {
			    Assert.assertTrue(! StringUtils.isEmpty(biologicalDataBean.alleleAccession));
			    Assert.assertTrue(! StringUtils.isEmpty(biologicalDataBean.geneticBackground));
		    }
	    }
    }


//     Ignore this test as it takes too long to run.
    @Test
    @Ignore
    public void testPopulateLineBiologicalDataMap() throws Exception {

        Resource cdaResource        = context.getResource("classpath:sql/h2/indexers/ObservationIndexerTest-experiment01-data.sql");
        Resource experimentResource = context.getResource("classpath:xml/ObservationIndexerTest-experiment01-data.xml");
        ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), cdaResource);

        observationIndexer.initialise();

        observationIndexer.populateLineBiologicalDataMap(connection);
        Map<String, ObservationIndexer.BiologicalDataBean> bioDataMap = observationIndexer.getLineBiologicalData();
        Assert.assertTrue("Expected bioDataMap size > 50. Actual size: " + bioDataMap.size(), bioDataMap.size() > 50);
    }


    // Ignore this test as it takes too long to run.
	@Test
	@Ignore
	public void testWeightMap() throws Exception {

        // Instantiate WeightMap here as it takes upwards of 8 minutes to load.
        WeightMap weightMap = new WeightMap(komp2DataSource);

        logger.info("Size of weight map {}", weightMap.size());
        Assert.assertTrue(weightMap.size() > 50);


        ZonedDateTime dateOfExperiment = ZonedDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd").parse("2015-04-29").toInstant(), ZoneId.of("UTC"));
		System.out.println("Weight map for specimen 94369 is : " + weightMap.get(94369L));
        System.out.println("Nearest weight to 2015-04-29 00:00:00 is " + weightMap.getNearestWeight(94369L, dateOfExperiment) );


		Long testDbId = null;
		try (PreparedStatement s = connection.prepareStatement("SELECT * FROM biological_sample where external_id = 'B6NTAC-USA/680.8f_5016035'") ) {
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                testDbId = rs.getLong("id");
            }
        }

        dateOfExperiment = ZonedDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd").parse("2012-07-17").toInstant(), ZoneId.of("UTC"));
        System.out.println("Weight map for specimen 'B6NTAC-USA/680.8f_5016035' (DB ID is " + testDbId+ ") is : " + weightMap.get(testDbId));
        System.out.println("Nearest weight to 2012-07-17 00:00:00 is " + weightMap.getNearestWeight(testDbId, dateOfExperiment) );
        
        Set<Float> weights = new HashSet<>();
        for (int i = 0; i< 1000; i++) {
            final WeightMap.BodyWeight nearestWeight = weightMap.getNearestWeight(testDbId, dateOfExperiment);
            weights.add(nearestWeight.getWeight());
        }

        Assert.assertEquals("Error Multiple weights found for same date", 1, weights.size());


	}


	@Test
    public void testImpressDataMaps() throws Exception {
        Map<Long, ImpressBaseDTO> bioDataMap;

        // Pipelines
        bioDataMap = IndexerMap.getImpressPipelines(connection);
        Assert.assertTrue(bioDataMap.size() > 5);
        logger.info("Size of pipeline data map {}", bioDataMap.size());

        //Procedures
        bioDataMap = IndexerMap.getImpressProcedures(connection);
        Assert.assertTrue(bioDataMap.size() > 20);
        logger.info("Size of procedure data map {}", bioDataMap.size());

        //Parameters
        Map<Long, ParameterDTO>  paramMap = IndexerMap.getImpressParameters(connection);
        Assert.assertTrue(paramMap.size() > 500);
        logger.info("Size of parameter data map {}", paramMap.size());

    }

//    @Test
//    public void testDatasourceDataMaps() throws Exception {
//        observationIndexer.initialise();
//
//        observationIndexer.populateDatasourceDataMap(connection);
//        Map<Long, ObservationIndexer.DatasourceBean> bioDataMap;
//
//        // Project
//        bioDataMap = observationIndexer.getProjectMap();
//        Assert.assertTrue(bioDataMap.size() > 5);
//        logger.info("Size of project data map {}", bioDataMap.size());
//
//        //Datasource
//        bioDataMap = observationIndexer.getDatasourceMap();
//        Assert.assertTrue(bioDataMap.size() > 10);
//        logger.info("Size of datasource data map {}", bioDataMap.size());
//
//    }

//    @Test
//    public void testpopulateCategoryNamesDataMap() throws Exception {
//        observationIndexer.initialise();
//
//        observationIndexer.populateCategoryNamesDataMap(connection);
//        Map<String, Map<String, String>> bioDataMap = observationIndexer.getTranslateCategoryNames();
//
//        Assert.assertTrue(bioDataMap.size() > 5);
//        logger.info("Size of translated category map {}", bioDataMap.size());
//
//        Assert.assertTrue(bioDataMap.containsKey("M-G-P_008_001_020"));
//        logger.info("Translated map contains key for M-G-P_008_001_020");
//
//	    if (bioDataMap.get("M-G-P_008_001_020") != null && bioDataMap.get("M-G-P_008_001_020").get("0") != null) {
//		    Assert.assertTrue(bioDataMap.get("M-G-P_008_001_020").get("0").equals("Present"));
//		    logger.info("M-G-P_008_001_020 correctly mapped '0' to 'Present'");
//	    } else {
//		    logger.warn("M-G-P_008_001_020 not found in bioDataMap");
//	    }
//
//        Assert.assertTrue(bioDataMap.get("M-G-P_008_001_020").get("1").equals("Absent"));
//        logger.info("M-G-P_008_001_020 correctly mapped '1' to 'Absent'");
//
//        Assert.assertTrue(bioDataMap.get("ESLIM_008_001_014").get("0").equals("No response"));
//        logger.info("ESLIM_008_001_014 correctly mapped '0' to 'No response'");
//
//        Assert.assertTrue(bioDataMap.get("ESLIM_008_001_014").get("1").equals("Response to touch"));
//        logger.info("ESLIM_008_001_014 correctly mapped '1' to 'Response to touch'");
//
//        Assert.assertTrue(bioDataMap.get("ESLIM_008_001_014").get("2").equals("Flees prior to touch"));
//        logger.info("ESLIM_008_001_014 correctly mapped '2' to 'Flees prior to touch'");
//
//        Assert.assertTrue(bioDataMap.get("M-G-P_008_001_007").get("0").equals("Extended Freeze(over 5 seconds)"));
//        logger.info("ESLIM_008_001_014 correctly mapped '0' to 'Extended Freeze(over 5 seconds)'");
//
//        Assert.assertTrue(bioDataMap.get("M-G-P_008_001_007").get("1").equals("Brief freeze followed by movement"));
//        logger.info("ESLIM_008_001_014 correctly mapped '1' to 'Brief freeze followed by movement'");
//
//        Assert.assertTrue(bioDataMap.get("M-G-P_008_001_007").get("2").equals("Immediate movement"));
//        logger.info("ESLIM_008_001_014 correctly mapped '2' to 'Immediate movement'");
//
//	    Assert.assertTrue(bioDataMap.get("M-G-P_008_001_005").get("1").equals("Eyes Closed"));
//	    logger.info("M-G-P_008_001_005 correctly mapped '1' to 'Eyes Closed'");
//
//	    Assert.assertTrue(bioDataMap.get("M-G-P_008_001_005").get("0").equals("Eyes Open"));
//	    logger.info("M-G-P_008_001_005 correctly mapped '0' to 'Eyes Open'");
//
//    }

	@Test
	public void testDate() throws ParseException {
		String dateString = "2015-06-11";
		Date now = new Date();
		Date d = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		System.out.println(d);

		now = new Date();
		Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		System.out.println(d2);

		System.out.println(TimeZone.getDefault().getDisplayName());
	}

	@Test
    public void specimenProjectIsLoaded() {

    }
}