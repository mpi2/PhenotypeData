package org.mousephenotype.cda.loads.common;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.AlternateId;
import org.mousephenotype.cda.db.pojo.ConsiderId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mrelac on 27/09/16.
 */
@RunWith(SpringRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@Import(TestConfig.class)
public class CdaSqlUtilsTest {

    @Autowired
    NamedParameterJdbcTemplate jdbc;

    @Autowired
    CdaSqlUtils cdaSqlUtils;


    @Test
    public void testGetLatestOntologyTerm() throws Exception {
        OntologyTerm originalTerm;
        OntologyTerm latestTerm;
        boolean isObsolete;
        String replacementAcc;
        AlternateId  alternateId;
        ConsiderId   considerId;

        System.out.println("exists-isObsolete-hasReplacement");
        isObsolete = true;
        replacementAcc = "MP:0010";
        considerId = null;
        alternateId = null;
        originalTerm = createOntologyTerm("MP:0001", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0010"));
        System.out.println("PASS\n");

        System.out.println("exists-isObsolete-noReplacement-hasConsiderId");
        isObsolete = true;
        replacementAcc = null;
        considerId = new ConsiderId("MP:0002", "MP:0011");
        alternateId = null;
        originalTerm = createOntologyTerm("MP:0002", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0011"));
        System.out.println("PASS\n");

        System.out.println("exists-isObsolete-noReplacement-noConsiderId");
        isObsolete = true;
        replacementAcc = null;
        considerId = null;
        alternateId = null;
        originalTerm = createOntologyTerm("MP:0003", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete");
        isObsolete = false;
        replacementAcc = null;
        considerId = null;
        alternateId = null;
        originalTerm = createOntologyTerm("MP:0004", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0004"));
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasReplacement");
        isObsolete = false;
        replacementAcc = "MP:0010";
        considerId = null;
        alternateId = null;
        originalTerm = createOntologyTerm("MP:0005", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0005"));
        System.out.println("PASS\n");

        System.out.println("notExists-hasAlternate");
        isObsolete = false;
        replacementAcc = null;
        considerId = null;
        alternateId = new AlternateId("MP:0012", "MP:9999");
        originalTerm = createOntologyTerm("MP:9999", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0012"));
        System.out.println("PASS\n");

        System.out.println("notExists-noAlternate");
        isObsolete = false;
        replacementAcc = null;
        considerId = null;
        alternateId = null;
        originalTerm = createOntologyTerm("MP:8888", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasConsiderId");
        isObsolete = false;
        replacementAcc = null;
        considerId = new ConsiderId("MP:0300", "MP:0010");
        alternateId = null;
        originalTerm = createOntologyTerm("MP:0010", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0010"));
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasConsiderId2");
        isObsolete = false;
        replacementAcc = null;
        considerId = new ConsiderId("MP:0011", "MP:0300");
        alternateId = null;
        originalTerm = createOntologyTerm("MP:0011", isObsolete, replacementAcc, considerId, alternateId);
        System.out.println("exists-notObsolete-hasConsiderId2");
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0011"));
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasAlternateId");
        isObsolete = false;
        replacementAcc = null;
        considerId = null;
        alternateId = new AlternateId("MP:0400", "MP:0012");
        originalTerm = createOntologyTerm("MP:0012", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0012"));
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasAlternateId2");
        isObsolete = false;
        replacementAcc = null;
        considerId = null;
        alternateId = new AlternateId("MP:0013", "MP:0400");
        originalTerm = createOntologyTerm("MP:0013", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0013"));
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasReplacement-hasConsiderId-hasAlternateId");
        isObsolete = false;
        replacementAcc = "MP:0011";
        considerId = new ConsiderId("MP:0400", "MP:0014");
        alternateId = new AlternateId("MP:0500", "MP:0014");
        originalTerm = createOntologyTerm("MP:0014", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0014"));
        System.out.println("PASS\n");

        System.out.println("exists-notObsolete-hasReplacement-hasConsiderId-hasAlternateId2");
        isObsolete = false;
        replacementAcc = "MP:0012";
        considerId = new ConsiderId("MP:0015", "MP:0300");
        alternateId = new AlternateId("MP:0015", "MP:0400");
        originalTerm = createOntologyTerm("MP:0015", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0015"));
        System.out.println("PASS\n");

        System.out.println("replacement-isObsolete");
        isObsolete = true;
        replacementAcc = "MP:0001";
        considerId = null;
        alternateId = null;
        originalTerm = createOntologyTerm("MP:0016", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("replacement-hasObsoleteConsiderId");
        isObsolete = true;
        replacementAcc = null;
        considerId = new ConsiderId("MP:0017", "MP:0002");
        alternateId = new AlternateId("MP:0004", "MP:0017");
        originalTerm = createOntologyTerm("MP:0017", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("replacement-multipleConsiderIds");
        isObsolete = true;
        replacementAcc = null;
        considerId = new ConsiderId("MP:0018", "MP:0001");
        alternateId = null;
        originalTerm = createOntologyTerm("MP:0018", isObsolete, replacementAcc, considerId, alternateId);
        originalTerm.getConsiderIds().add(new ConsiderId("MP:0018", "MP:0002"));
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("notExists-hasObsoleteAlternateId");
        isObsolete = false;
        replacementAcc = null;
        considerId = new ConsiderId("MP:0018", "MP:0001");
        alternateId = new AlternateId("MP:0001", "MP:7777");
        originalTerm = createOntologyTerm("MP:7777", isObsolete, replacementAcc, considerId, alternateId);
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        System.out.println("notExists-multipleAlternateIds");
        isObsolete = false;
        replacementAcc = null;
        considerId = new ConsiderId("MP:0018", "MP:0001");
        alternateId = new AlternateId("MP:5555", "MP:6666");
        originalTerm = createOntologyTerm("MP:6666", isObsolete, replacementAcc, considerId, alternateId);
        originalTerm.getAlternateIds().add(new AlternateId("MP:4444", "MP:6666"));
        latestTerm = cdaSqlUtils.getLatestOntologyTerm(originalTerm);
        Assert.assertNull(latestTerm);
        System.out.println("PASS\n");

        List<List<String>> ontologyTermCorrections = cdaSqlUtils.getOntologyTermLookups();
        for (List<String> row : ontologyTermCorrections) {
            System.out.println(String.format("%-20.20s%-20.20s%-50s", row.get(0), row.get(1), row.get(2)));
        }
    }

    private OntologyTerm createOntologyTerm(String acc, boolean isObsolete, String replacementAcc, ConsiderId considerId, AlternateId alternateId) {
        Set<AlternateId> altIds = null;
        if (alternateId != null) {
            altIds = new HashSet<>();
            altIds.add(alternateId);
        }
        Set<ConsiderId> considerIds = null;
        if (considerId != null) {
            considerIds = new HashSet<>();
            considerIds.add(considerId);
        }

        OntologyTerm term = new OntologyTerm(acc, 1);
        term.setIsObsolete(isObsolete);
        term.setReplacementAcc(replacementAcc);
        term.setAlternateIds(altIds);
        term.setConsiderIds(considerIds);

        return term;
    }
}