package org.mousephenotype.cda.loads.common;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.utilities.RunStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * Created by mrelac on 27/09/16.
 */
@RunWith(SpringRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@Import(CdaSqlUtilsTestConfig.class)
public class CdaSqlUtilsTest {

    @Autowired
    NamedParameterJdbcTemplate jdbc;

    @Autowired
    CdaSqlUtils cdaSqlUtils;


    @Test
    public void testGetLatestOntologyTerm() throws Exception {
        System.out.println("testGetLatestOntologyTerm");
        OntologyTerm             latestTerm;

        RunStatus status = new RunStatus();
        System.out.println("exists-isObsolete-hasReplacement");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0001", status);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0010"));
        System.out.println("PASS\n");

        System.out.println("exists-isObsolete-noReplacement-hasConsiderId");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0002", status);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0011"));
        System.out.println("PASS\n");

        System.out.println("exists-isObsolete-noReplacement-noConsiderId");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0003", status);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0004", status);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0004"));
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasReplacement");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0005", status);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0005"));
        System.out.println("PASS\n");

        System.out.println("notExists-hasAlternate");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:9999", status);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0012"));
        System.out.println("PASS\n");

        System.out.println("notExists-noAlternate");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:8888", status);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasConsiderId");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0010", status);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0010"));
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasAlternateId");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0012", status);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0012"));
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasReplacement-hasConsiderId-hasAlternateId");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0014", status);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0014"));
        System.out.println("PASS\n");

        System.out.println("replacement-isObsolete");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0016", status);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("replacement-hasObsoleteConsiderId");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0017", status);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("replacement-multipleConsiderIds");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0018", status);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("notExists-hasObsoleteAlternateId");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:7777", status);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("notExists-multipleAlternateIds");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:6666", status);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("-------------- Anomalies (sorted) --------------");
        List<String> anomalyReasons = new ArrayList<>();
        for (String reason  : status.getErrorMessages()) {
            anomalyReasons.add(reason);
        }
        for (String reason  : status.getWarningMessages()) {
            anomalyReasons.add(reason);
        }
        Collections.sort(anomalyReasons);
        for (String anomalyReason : anomalyReasons) {
            System.out.println(anomalyReason);
        }
        System.out.println();
    }

    @Test
    public void testCheckAndUpdateOntologyTerms() throws Exception {
        System.out.println("testCheckAndUpdateOntologyTerms");
        List<String> ontologyTermAccessionIds = Arrays.asList(new String[] {
                "MP:0001", "MP:0002", "MP:0003", "MP:0004", "MP:0005", "MP:0999", "MP:0888", "MP:0010", "MP:0012", "MP:0014", "MP:0016", "MP:0017", "MP:0018", "MP:0777", "MP:0666"
        });

        Set<OntologyTermAnomaly> writtenAnomalies = cdaSqlUtils.checkAndUpdateOntologyTerms(jdbc, ontologyTermAccessionIds, "phenotype_parameter_ontology_annotation", "ontology_acc");

        Set<OntologyTermAnomaly> readAnomalies = cdaSqlUtils.getOntologyTermAnomalies();
        Assert.assertTrue("written anomaly list size: " + writtenAnomalies.size() + ". read anomaly list size: " + readAnomalies.size(), writtenAnomalies.size() == readAnomalies.size());
        writtenAnomalies.removeAll(readAnomalies);
        if ( ! writtenAnomalies.isEmpty()) {
            System.out.println("Expected empty writtenAnomalies. Dumping writtenAnomalies:");
            for (OntologyTermAnomaly anomaly : writtenAnomalies) {
                System.out.println("\t" + anomaly.toString());
            }
            Assert.fail();
        }

        List<String> anomalyReasons = new ArrayList<>();
        for (OntologyTermAnomaly anomaly  : readAnomalies) {
            anomalyReasons.add(anomaly.getReason());
        }
        Collections.sort(anomalyReasons);
        for (String anomalyReason : anomalyReasons) {
            System.out.println(anomalyReason);
        }

        System.out.println();
    }
}