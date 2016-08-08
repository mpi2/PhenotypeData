package org.mousephenotype.cda.indexers;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.service.OntologyBean;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigIndexers.class} )
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
@Transactional
public class ObservationIndexerTest {

    private ObservationIndexer observationIndexer;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("komp2DataSource")
    private DataSource ds;

    @Before
    public void setUp() throws Exception {
	    observationIndexer = new ObservationIndexer();
	    observationIndexer.setConnection(ds.getConnection());
    }

    @Test
    public void testGetOntologyParameterSubTerms() throws SQLException {

        Map<Integer, List<OntologyBean>> map = IndexerMap.getOntologyParameterSubTerms(ds.getConnection());

        boolean found = false;
        for (Integer mapId : map.keySet()) {
            List<OntologyBean> list = map.get(mapId);
            if (list.size() > 1) {

                System.out.println("Found an observation with more than one ontology term association");
                System.out.println("For map ID " + mapId + ": " + StringUtils.join( list.stream().map(OntologyBean::getName).collect(Collectors.toList() ), ", "));

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
//@Ignore
    public void testPopulateBiologicalDataMap() throws Exception {
        observationIndexer.initialise();

        observationIndexer.populateBiologicalDataMap();
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

    @Test
//@Ignore
    public void testPopulateLineBiologicalDataMap() throws Exception {
        observationIndexer.initialise();

        observationIndexer.populateLineBiologicalDataMap();
        Map<String, ObservationIndexer.BiologicalDataBean> bioDataMap = observationIndexer.getLineBiologicalData();
        Assert.assertTrue(bioDataMap.size() > 50);

        logger.info("Size of line level biological data map {}", bioDataMap.size());

    }


	@Test
	//@Ignore
	public void testPopulateWeightMap() throws Exception {
		observationIndexer.initialise();

		observationIndexer.populateWeightMap();
		Map<Integer, List<ObservationIndexer.WeightBean>> weightMap = observationIndexer.getWeightMap();

		ZonedDateTime dateOfExperiment = ZonedDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd").parse("2015-04-29").toInstant(), ZoneId.of("UTC"));

		System.out.println("Weight map for speciment 94369 is : " + weightMap.get(94369));
		System.out.println("Nearest weight to 2015-04-29 00:00:00 is " + observationIndexer.getNearestWeight(94369, dateOfExperiment) );
		Assert.assertTrue(weightMap.size() > 50);

		logger.info("Size of weight map {}", weightMap.size());

	}


	@Test
//@Ignore
    public void testImpressDataMaps() throws Exception {
        Map<Integer, ImpressBaseDTO> bioDataMap;
        Connection connection = ds.getConnection();

        // Pipelines
        bioDataMap = IndexerMap.getImpressPipelines(connection);
        Assert.assertTrue(bioDataMap.size() > 5);
        logger.info("Size of pipeline data map {}", bioDataMap.size());

        //Procedures
        bioDataMap = IndexerMap.getImpressProcedures(connection);
        Assert.assertTrue(bioDataMap.size() > 20);
        logger.info("Size of procedure data map {}", bioDataMap.size());

        //Parameters
        Map<Integer, ParameterDTO>  paramMap = IndexerMap.getImpressParameters(connection);
        Assert.assertTrue(paramMap.size() > 500);
        logger.info("Size of parameter data map {}", paramMap.size());

    }

    @Test
//@Ignore
    public void testDatasourceDataMaps() throws Exception {
        observationIndexer.initialise();

        observationIndexer.populateDatasourceDataMap();
        Map<Integer, ObservationIndexer.DatasourceBean> bioDataMap;

        // Project
        bioDataMap = observationIndexer.getProjectMap();
        Assert.assertTrue(bioDataMap.size() > 5);
        logger.info("Size of project data map {}", bioDataMap.size());

        //Datasource
        bioDataMap = observationIndexer.getDatasourceMap();
        Assert.assertTrue(bioDataMap.size() > 10);
        logger.info("Size of datasource data map {}", bioDataMap.size());

    }

    @Test
//@Ignore
    public void testpopulateCategoryNamesDataMap() throws Exception {
        observationIndexer.initialise();

        observationIndexer.populateCategoryNamesDataMap();
        Map<String, Map<String, String>> bioDataMap = observationIndexer.getTranslateCategoryNames();

        Assert.assertTrue(bioDataMap.size() > 5);
        logger.info("Size of translated category map {}", bioDataMap.size());

        Assert.assertTrue(bioDataMap.containsKey("M-G-P_008_001_020"));
        logger.info("Translated map contains key for M-G-P_008_001_020");

	    if (bioDataMap.get("M-G-P_008_001_020") != null && bioDataMap.get("M-G-P_008_001_020").get("0") != null) {
		    Assert.assertTrue(bioDataMap.get("M-G-P_008_001_020").get("0").equals("Present"));
		    logger.info("M-G-P_008_001_020 correctly mapped '0' to 'Present'");
	    } else {
		    logger.warn("M-G-P_008_001_020 not found in bioDataMap");
	    }

        Assert.assertTrue(bioDataMap.get("M-G-P_008_001_020").get("1").equals("Absent"));
        logger.info("M-G-P_008_001_020 correctly mapped '1' to 'Absent'");

        Assert.assertTrue(bioDataMap.get("ESLIM_008_001_014").get("0").equals("No response"));
        logger.info("ESLIM_008_001_014 correctly mapped '0' to 'No response'");

        Assert.assertTrue(bioDataMap.get("ESLIM_008_001_014").get("1").equals("Response to touch"));
        logger.info("ESLIM_008_001_014 correctly mapped '1' to 'Response to touch'");

        Assert.assertTrue(bioDataMap.get("ESLIM_008_001_014").get("2").equals("Flees prior to touch"));
        logger.info("ESLIM_008_001_014 correctly mapped '2' to 'Flees prior to touch'");

        Assert.assertTrue(bioDataMap.get("M-G-P_008_001_007").get("0").equals("Extended Freeze(over 5 seconds)"));
        logger.info("ESLIM_008_001_014 correctly mapped '0' to 'Extended Freeze(over 5 seconds)'");

        Assert.assertTrue(bioDataMap.get("M-G-P_008_001_007").get("1").equals("Brief freeze followed by movement"));
        logger.info("ESLIM_008_001_014 correctly mapped '1' to 'Brief freeze followed by movement'");

        Assert.assertTrue(bioDataMap.get("M-G-P_008_001_007").get("2").equals("Immediate movement"));
        logger.info("ESLIM_008_001_014 correctly mapped '2' to 'Immediate movement'");

	    Assert.assertTrue(bioDataMap.get("M-G-P_008_001_005").get("1").equals("Eyes Closed"));
	    logger.info("M-G-P_008_001_005 correctly mapped '1' to 'Eyes Closed'");

	    Assert.assertTrue(bioDataMap.get("M-G-P_008_001_005").get("0").equals("Eyes Open"));
	    logger.info("M-G-P_008_001_005 correctly mapped '0' to 'Eyes Open'");

    }

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

}
