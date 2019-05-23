package org.mousephenotype.cda.loads.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 27/09/16.
 */
@RunWith(SpringRunner.class)
@Import(CdaSqlUtilsTestConfig.class)
@Sql(scripts = {"/sql/h2/cda/schema.sql", "/sql/h2/impress/impressSchema.sql"})
public class CdaSqlUtilsTest {

    @Autowired
    NamedParameterJdbcTemplate jdbc;

    @Autowired
    CdaSqlUtils cdaSqlUtils;

    @Before
    public void beforeTests() {


        List<String> q = new ArrayList<>();
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (1,'ESLIM_001',6,'EUMODIC Pipeline 1','EUMODIC Pipeline 1',1,0,1,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (2,'ESLIM_002',6,'EUMODIC Pipeline 2','EUMODIC Pipeline 2',1,0,2,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (3,'GMC_001',6,'GMC Pipeline','GMC Pipeline',1,0,3,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (4,'M-G-P_001',6,'MGP Pipeline','MGP Pipeline',1,0,4,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (5,'HAROLD_001',6,'Aging ENU Screen Pipeline 1','Aging ENU Screen Pipeline 1',1,0,5,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (6,'ESLIM_003',6,'EUMODIC Pipeline 3','EUMODIC Pipeline 3',1,0,6,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (7,'IMPC_001',6,'IMPC Pipeline','The IMPC Pipeline is a core set of Procedures and Parameters to be collected by all phenotyping centers.',1,1,7,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (8,'HRWL_001',6,'Harwell','Harwell extra parameters',1,0,8,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (9,'TCP_001',6,'TCP Pipeline','Toronto Centre for Phenogenomics''s (TCP) pipeline for extra parameters and procedures',1,1,9,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (10,'ICS_001',6,'ICS','ICS''s pipeline for extra procedures and parameters',1,0,10,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (11,'HAS_001',6,'Harwell Ageing Screen','',1,0,11,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (12,'JAX_001',6,'JAX Pipeline','',1,0,12,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (13,'UCD_001',6,'UCD Pipeline','',1,0,13,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (14,'HMGU_001',6,'German Mouse Clinic','German Mouse Clinic Pipeline for IMPC Project _ Rotarod as an additional center-specific Procedure',1,0,14,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (15,'MGP_001',6,'MGP Select Pipeline','',1,0,15,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (16,'BCM_001',6,'BCM Pipeline','',1,1,16,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (17,'SLM_001',6,'Salmonella Challenge',' and IgG2a.',1,0,17,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (18,'TRC_001',6,'Trichuris challenge',' infection.',1,0,18,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (19,'DSS_001',6,'DSS challenge',' a blinded manner.',1,0,19,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (20,'RBRCLA_001',6,'Riken late adult','Pipeline for Riken''s late adult procedures',1,0,28,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (21,'UCDLA_001',6,'UCD late adult','UCD late adult',1,0,32,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (22,'TCPLA_001',6,'TCP late adult','TCP late adult',1,0,33,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (23,'ICSLA_001',6,'ICS late adult','ICS late adult',1,0,34,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (24,'BCMLA_001',6,'BCM late adult','Baylor late adult',1,0,35,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (25,'KMPCLA_001',6,'KMPC late adult','KMPC late adult',1,0,36,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (26,'NINGLA_001',6,'Nanjing late adult','Nanjing late adult',1,0,37,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (27,'HRWLIP_001',6,'Harwell interval pipeline','Pipeline for Harwell procedures between the early adult and late adult pipelines',1,0,38,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (28,'JAXIP_001',6,'JAX interval pipeline','Pipeline for procedures between the early adult and late adult pipelines',1,0,39,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (29,'CCPIP_001',6,'CCP Interval Pipeline','CCP Interval Pipeline; phenotyping between the Early Adult (EA) and Late Adult (LA) pipelines.',1,1,41,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (30,'CCPLA_001',6,'CCP Late Adult','CCP Late Adult pipeline',1,0,42,0)");
        q.add("INSERT INTO phenotype_pipeline (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated) VALUES (31,'HMGULA_001',6,'GMC late adult','Pipeline for GMC''s late adult procedures',1,0,43,0)");

        for (String q1 : q) {
            jdbc.update(q1, (Map)null);
        }

    }

    @Test
    public void getCdaPipeline_idsByDccPipelineTest() {


        final Map<String, Integer> cdaPipeline_idsByDccPipeline = cdaSqlUtils.getCdaPipeline_idsByDccPipeline();

        for (Map.Entry e : cdaPipeline_idsByDccPipeline.entrySet()) {
            System.out.println("key: " +e.getKey() + ", Value: " + e.getValue());
        }

        final Integer hrwlip_001 = cdaPipeline_idsByDccPipeline.get("HRWLIP_001");
        assert (hrwlip_001 != null);
        assert (hrwlip_001 == 27);

    }

// FIXME This test needs to be rewritten to use the new PhenotypeParameterOntologyAssociationUpdater.
//    @Test
//    public void testGetLatestOntologyTerm() throws Exception {
//        System.out.println("testGetLatestOntologyTerm");
//        OntologyTerm             latestTerm;
//
//        RunStatus status = new RunStatus();
//        System.out.println("exists-isObsolete-hasReplacement");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0001", status);
//        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0010"));
//        System.out.println("PASS\n");
//
//        System.out.println("exists-isObsolete-noReplacement-hasConsiderId");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0002", status);
//        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0011"));
//        System.out.println("PASS\n");
//
//        System.out.println("exists-isObsolete-noReplacement-noConsiderId");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0003", status);
//        Assert.assertNull(latestTerm);
//        System.out.println("PASS\n");
//
//        System.out.println("exists-notObsolete");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0004", status);
//        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0004"));
//        System.out.println("PASS\n");
//
//        System.out.println("exists-notObsolete-hasReplacement");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0005", status);
//        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0005"));
//        System.out.println("PASS\n");
//
//        System.out.println("notExists-hasAlternate");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:9999", status);
//        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0012"));
//        System.out.println("PASS\n");
//
//        System.out.println("notExists-noAlternate");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:8888", status);
//        Assert.assertNull(latestTerm);
//        System.out.println("PASS\n");
//
//        System.out.println("exists-notObsolete-hasConsiderId");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0010", status);
//        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0010"));
//        System.out.println("PASS\n");
//
//        System.out.println("exists-notObsolete-hasAlternateId");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0012", status);
//        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0012"));
//        System.out.println("PASS\n");
//
//        System.out.println("exists-notObsolete-hasReplacement-hasConsiderId-hasAlternateId");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0014", status);
//        Assert.assertTrue(latestTerm.getId().getAccession().equals("MP:0014"));
//        System.out.println("PASS\n");
//
//        System.out.println("replacement-isObsolete");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0016", status);
//        Assert.assertNull(latestTerm);
//        System.out.println("PASS\n");
//
//        System.out.println("replacement-hasObsoleteConsiderId");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0017", status);
//        Assert.assertNull(latestTerm);
//        System.out.println("PASS\n");
//
//        System.out.println("replacement-multipleConsiderIds");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:0018", status);
//        Assert.assertNull(latestTerm);
//        System.out.println("PASS\n");
//
//        System.out.println("notExists-hasObsoleteAlternateId");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:7777", status);
//        Assert.assertNull(latestTerm);
//        System.out.println("PASS\n");
//
//        System.out.println("notExists-multipleAlternateIds");
//        latestTerm = cdaSqlUtils.getLatestOntologyTerm("MP:6666", status);
//        Assert.assertNull(latestTerm);
//        System.out.println("PASS\n");
//
//        System.out.println("-------------- Anomalies (sorted) --------------");
//        List<String> anomalyReasons = new ArrayList<>();
//        for (String reason  : status.getErrorMessages()) {
//            anomalyReasons.add(reason);
//        }
//        for (String reason  : status.getWarningMessages()) {
//            anomalyReasons.add(reason);
//        }
//        Collections.sort(anomalyReasons);
//        for (String anomalyReason : anomalyReasons) {
//            System.out.println(anomalyReason);
//        }
//        System.out.println();
//    }

//    @Test
//    public void testCheckAndUpdateOntologyTerms() throws Exception {
//        System.out.println("testCheckAndUpdateOntologyTerms");
//        List<String> ontologyTermAccessionIds = Arrays.asList(new String[] {
//                "MP:0001", "MP:0002", "MP:0003", "MP:0004", "MP:0005", "MP:0999", "MP:0888", "MP:0010", "MP:0012", "MP:0014", "MP:0016", "MP:0017", "MP:0018", "MP:0777", "MP:0666"
//        });
//
//        Set<OntologyTermAnomaly> writtenAnomalies = cdaSqlUtils.checkAndUpdateOntologyTerms(jdbc, ontologyTermAccessionIds, "phenotype_parameter_ontology_annotation", "ontology_acc");
//
//        Set<OntologyTermAnomaly> readAnomalies = cdaSqlUtils.getOntologyTermAnomalies();
//        Assert.assertTrue("written anomaly list size: " + writtenAnomalies.size() + ". read anomaly list size: " + readAnomalies.size(), writtenAnomalies.size() == readAnomalies.size());
//        writtenAnomalies.removeAll(readAnomalies);
//        if ( ! writtenAnomalies.isEmpty()) {
//            System.out.println("Expected empty writtenAnomalies. Dumping writtenAnomalies:");
//            for (OntologyTermAnomaly anomaly : writtenAnomalies) {
//                System.out.println("\t" + anomaly.toString());
//            }
//            Assert.fail();
//        }
//
//        List<String> anomalyReasons = new ArrayList<>();
//        for (OntologyTermAnomaly anomaly  : readAnomalies) {
//            anomalyReasons.add(anomaly.getReason());
//        }
//        Collections.sort(anomalyReasons);
//        for (String anomalyReason : anomalyReasons) {
//            System.out.println(anomalyReason);
//        }
//
//        System.out.println();
//    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource cdaDataSource;

    @Before
    public void before() throws SQLException {
        Resource r = context.getResource("sql/CdaSqlUtilsTest.sql");
        ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), r);
    }

    @Test
    public void testGetStrain() throws DataLoadException {
        Strain strain = cdaSqlUtils.getStrainsByNameOrMgiAccessionIdMap().get("C57BL/6J");
        assert strain.getId().getAccession().equals("MGI:3028467");
    }
}