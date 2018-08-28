package org.mousephenotype.cda.loads.annotations;


import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.DatasourceDAO;
import org.mousephenotype.cda.db.dao.OntologyTermDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.dao.ProjectDAO;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.db.statistics.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mousephenotype.cda.loads.annotations.OntologyAnnotationGenerator.SIGNIFICANCE_THRESHOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OntologyAnnotationGeneratorTestConfig.class)
@Transactional
public class OntologyAnnotationGeneratorTest {

    private static final Logger logger = LoggerFactory.getLogger(OntologyAnnotationGeneratorTest.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    PhenotypePipelineDAO pDAO;

    @Autowired
    MpTermService mpTermService;

    @Autowired
    OntologyTermDAO termDAO;

    @Autowired
    OntologyTermDAO ontologyTermDAO;

    @Autowired
    PhenotypePipelineDAO phenotypePipelineDAO;

    @Autowired
    DatasourceDAO datasourceDAO;

    @Autowired
    ProjectDAO projectDAO;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    Connection connection;
    OntologyAnnotationGenerator mpGenerator;

    Integer dataSourceId;
    Integer projectId;

    @Before
    public void setUp() throws SQLException {

        dataSourceId = datasourceDAO.getDatasourceByShortName("IMPC").getId();
        projectId = projectDAO.getProjectByName("IMPC").getId();

        connection = komp2DataSource.getConnection();
        connection.setAutoCommit(false);

        mpGenerator = new OntologyAnnotationGenerator(mpTermService, ontologyTermDAO, komp2DataSource, phenotypePipelineDAO);

    }


    @Test
    public void testGetAllOptionsForParameter() throws Exception {
        Parameter p = pDAO.getParameterByStableId("IMPC_XRY_001_001");
        Map<String, OntologyTerm> results =  mpTermService.getAllOptionsForParameter(connection, termDAO, p, PhenotypeAnnotationType.abnormal);

        // Should only have one result
        for(String key : results.keySet()) {
            assert(key.equals("Absent"));
            assert(results.get(key).equals("MP:0003456"));
        }
    }

    @Test
    public void testGetAnnotationTypeMap() throws Exception {
        Parameter p = pDAO.getParameterByStableId("IMPC_HEM_001_001");

        MultiKeyMap annotationsMap = mpTermService.getAnnotationTypeMap(p.getStableId(), connection, termDAO);

        Map<String, OntologyTerm> shouldHave = new HashMap<>();

        for(String key : shouldHave.keySet()) {
            logger.warn("Checking key: " + key);
            logger.warn("  Should have value: " + shouldHave.get(key));
            assert(annotationsMap.containsKey(key));
            assert(annotationsMap.containsValue(shouldHave.get(key)));
        }

    }


    @Test
    public void testGetAnnotationTypeMap3iParameter() throws Exception {
        Parameter p = pDAO.getParameterByStableId("MGP_PBI_036_001");
        System.out.println(pDAO);

        MultiKeyMap annotationsMap = mpTermService.getAnnotationTypeMap(p.getStableId(), connection, termDAO);
        Set<OntologyTerm> annotationsFlatMap = new HashSet();
        for (Object k : annotationsMap.keySet()) {
//            String g = (String)k;
            annotationsFlatMap.add((OntologyTerm)annotationsMap.get(k));
        }

        // populate possible ontology associations
        Map<String, List<OntologyTerm>> shouldHave = new HashMap<>();
        for (ParameterOntologyAnnotation pa : p.getAnnotations()) {
            if(!shouldHave.containsKey(p.getStableId())) {
                shouldHave.put(p.getStableId(), new ArrayList<OntologyTerm>());
            }

            shouldHave.get(p.getStableId()).add(pa.getOntologyTerm());
        }

        for(String key : shouldHave.keySet()) {
            logger.warn("Checking key: " + key);
            logger.warn("  Should have value: " + shouldHave.get(key));
            for (OntologyTerm term : shouldHave.get(key)) {
                assert (annotationsFlatMap.contains(term));
            }
        }

    }


    @Test
    public void testGetAnnotationTypeMapNoNormal() throws Exception {
        Parameter p = pDAO.getParameterByStableId("M-G-P_014_001_001");

        MultiKeyMap annotationsMap = mpTermService.getAnnotationTypeMap(p.getStableId(), connection, termDAO);

        Map<String, OntologyTerm> shouldHave = new HashMap<>();

        for(String key : shouldHave.keySet()) {
            logger.warn("Checking key: " + key);
            logger.warn("  Should have value: " + shouldHave.get(key));
            assert(annotationsMap.containsKey(key));
            assert(annotationsMap.containsValue(shouldHave.get(key)));
        }

    }

    @Test
    public void testManyAnnotationTypeMaps() throws Exception {

        List<Pipeline> pipelines =pDAO.getAllPhenotypePipelines();
        Collections.shuffle(pipelines);
        for (Pipeline pipeline : pipelines) {

            logger.info("Testing pipeline: {}", pipeline);
            List<Procedure> procedures = new ArrayList<>(pipeline.getProcedures());
            Collections.shuffle(procedures);
            for (Procedure procedure : procedures) {

                logger.info("Testing procedure: {}", procedure);
                for (Parameter parameter : procedure.getParameters()) {

                    logger.info("Testing parameter: {}", parameter);
                    MultiKeyMap annotationsMap = mpTermService.getAnnotationTypeMap(parameter.getStableId(), connection, termDAO);
                    System.out.println(annotationsMap);

                    Map<PhenotypeAnnotationType, OntologyTerm> shouldHave = new HashMap<>();
                    for (ParameterOntologyAnnotation pa : parameter.getAnnotations()) {
                        shouldHave.put((pa.getType()), pa.getOntologyTerm());
                    }
                    System.out.println("should have is:" + shouldHave);

                    for(PhenotypeAnnotationType key : shouldHave.keySet()) {
                        logger.warn("Checking key: " + key);
                        logger.warn("  Should have value: " + shouldHave.get(key));
                        assert(annotationsMap.containsKey(key, "", ""));
                        assert(annotationsMap.get(key, "", "").equals(shouldHave.get(key)));
                    }

                }
                break;
            }
            break;
        }


    }



    @Test
    public void testGetUnidimensionalResultsAndTerm_IMPC_HEM_001_001() throws SQLException {
        Parameter p = pDAO.getParameterByStableId("IMPC_HEM_001_001");
        Pipeline pipe = pDAO.getPhenotypePipelineByStableId("MGP_001");
        List<ResultDTO> results = mpGenerator.getUnidimensionalResults(connection);
        assert(results.size()>100);
        for ( ResultDTO result : results) {
            if(result.getGeneAcc().equals("MGI:1926116")) {// Gene: Fam175b
                if(result.getParameterId().equals(p.getId())) { //Parameter: IMPC_HEM_001_001
                    if(result.getPipelineId().equals(pipe.getId())) {
                        System.out.println("============= FOUND =============");
                        System.out.println(result);

                        OntologyTerm term = mpTermService.getMPTerm(p.getStableId(), result, null, connection, 1.0f, true);
                        System.out.println(" Found term: " + term);

                        assert(term!=null);

                        break;
                    }
                }
            }
        }
    }

    @Test
    public void testGetRRPlusResults() throws SQLException {

        List<ResultDTO> results = mpGenerator.getRRPlusResults(connection);

        for (ResultDTO result : results) {
            System.out.println(result);
        }

    }

    @Test
    public void testProcessRRPlusParameters() throws SQLException {

        mpGenerator.processRRPlusParameters(komp2DataSource.getConnection(), pDAO);

    }


    @Test
    public void testGetEmbryonicResults() throws SQLException {

        List<ResultDTO> results = mpGenerator.getEmbryonicResults(connection);

        for (ResultDTO result : results) {
            assertTrue(result.getNullTestPvalue()==null);
        }

        System.out.println(" Total results: " + results.size());

    }


    @Test
    public void testInfertilityAssociations() throws SQLException {

        List<ResultDTO> lineResults = mpGenerator.getLineResults(connection);

        for (ResultDTO result : lineResults) {

            Parameter p = pDAO.getParameterById(result.getParameterId());

            if (p.getStableId().contains("FER")) {

                // For male fertility results
                if (p.getStableId().equals("IMPC_FER_001_001")) {
                    // Female pvalues should be null
                    assert (result.getFemalePvalue()==null);
                } else {
                    // For female results, male pvalues should be null
                    assert (result.getMalePvalue()==null);

                }
            }
        }

        mpGenerator.processLineParameters(lineResults);

        Parameter mp = pDAO.getParameterByStableId("IMPC_FER_001_001"); //male fertility
        Parameter fp = pDAO.getParameterByStableId("IMPC_FER_019_001"); //female fertility
        String query = "SELECT * FROM phenotype_call_summary WHERE parameter_id IN (" + mp.getId() + ", " + fp.getId() + ")";
        int checked = 0;
        try (Connection cnx = komp2DataSource.getConnection(); PreparedStatement statement = cnx.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // look at the FER parameters for Males and assert that the females are not there.

                // NO "male infertile" female associations expected
                if (resultSet.getString("sex").equals(SexType.female.getName())) {
                    assertFalse (resultSet.getString("mp_acc").equals("MP:0001925"));
                    assertTrue (resultSet.getString("mp_acc").equals("MP:0001926"));
                }

                // NO "female infertile" male associations expected
                if (resultSet.getString("sex").equals(SexType.male.getName())) {
                    assertFalse (resultSet.getString("mp_acc").equals("MP:0001926"));
                    assertTrue (resultSet.getString("mp_acc").equals("MP:0001925"));
                }

                checked += 1;
            }
        }
        logger.info("Checked {} gene to parameter associations for correct sex to infertile MP term association", checked);
    }

    @Test
    public void testGetEmbryonicLineResults() throws SQLException {
        List<ResultDTO> lineResults = mpGenerator.getEmbryonicLineResults(connection);

        Set<Parameter> embLineParams = new HashSet<>();
        for(String parm : Arrays.asList("IMPC_EVL_001_001", "IMPC_EVM_001_001", "IMPC_EVO_001_001", "IMPC_EVP_001_001")) {
            embLineParams.add(pDAO.getParameterByStableId(parm));
        }

        assert(lineResults.size()>100);
        for (ResultDTO result : lineResults) {

            assert(embLineParams.contains(pDAO.getParameterById(result.getParameterId())));

        }

    }


    @Test
    public void testCategoricalResults() throws SQLException {

        Integer parameterId = pDAO.getParameterByStableId("M-G-P_026_001_029").getId();
        List<ResultDTO> categoriocalResults = mpGenerator.getCategoricalResults(connection);

        assert(categoriocalResults.size()>100);

        Boolean foundCenpjMGPParameter = Boolean.FALSE;

        for (ResultDTO result : categoriocalResults) {

            if ( ! result.getGeneAcc().equals("MGI:2684927")) {
                continue;
            }

            logger.info("Found parameter {} for {} (id: {}). Looking for {}", pDAO.getParameterById(result.getParameterId()), result.getGeneAcc(), result.getParameterId(), parameterId);
            if (result.getParameterId().equals(parameterId)) {
                foundCenpjMGPParameter = Boolean.TRUE;

                // process this result to see if we get a term
                if (result.getNullTestPvalue() < 0.0001) {

                    // Effect is significant, find out which term to associate

                    Parameter parameter = pDAO.getParameterById(result.getParameterId());

                    // Check the female specific term
                    if (result.getFemalePvalue() != null && result.getFemalePvalue() <= SIGNIFICANCE_THRESHOLD) {

                        result.setSex(SexType.female);
                        OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), result, SexType.female, connection, SIGNIFICANCE_THRESHOLD, true);

                        logger.info("  Got term {} ", term);
                        assert (term != null);
                    }

                    // Check the male specific term
                    if (result.getMalePvalue() != null && result.getMalePvalue() <= SIGNIFICANCE_THRESHOLD) {

                        result.setSex(SexType.male);
                        OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), result, SexType.male, connection, SIGNIFICANCE_THRESHOLD, true);

                        logger.info("  Got term {} ", term);
                        assert (term != null);
                    }
                }
                break;
            }

        }

        assert(foundCenpjMGPParameter);

    }



    @Test
    public void testCategoricalResultBmp4EyelidClosure() throws SQLException {

        Integer parameterId = pDAO.getParameterByStableId("IMPC_EYE_005_001").getId();
        List<ResultDTO> categoriocalResults = mpGenerator.getCategoricalResults(connection);

        assert(categoriocalResults.size()>100);

        Boolean foundParameter = Boolean.FALSE;

        for (ResultDTO result : categoriocalResults) {

            if ( ! result.getGeneAcc().equals("MGI:88180")) {
                continue;
            }

            logger.info("Found parameter {} for {} (id: {}). Looking for {}", pDAO.getParameterById(result.getParameterId()), result.getGeneAcc(), result.getParameterId(), parameterId);
            if (result.getParameterId().equals(parameterId)) {
                foundParameter = Boolean.TRUE;

                if (result.getNullTestPvalue() < 0.0001) {

                    // Effect is significant, find out which term to associate

                    Parameter parameter = pDAO.getParameterById(result.getParameterId());
                    OntologyTerm term=null;

                    // Check the female specific term
                    if (result.getFemalePvalue() != null && result.getFemalePvalue() <= SIGNIFICANCE_THRESHOLD) {

                        result.setSex(SexType.female);
                        term = mpTermService.getMPTerm(parameter.getStableId(), result, SexType.female, connection, SIGNIFICANCE_THRESHOLD, true);

                        logger.info("  Got term {} ", term);
                        assert (term != null);
                    }

                    // Check the male specific term
                    if (result.getMalePvalue() != null && result.getMalePvalue() <= SIGNIFICANCE_THRESHOLD) {

                        result.setSex(SexType.male);
                        term = mpTermService.getMPTerm(parameter.getStableId(), result, SexType.male, connection, SIGNIFICANCE_THRESHOLD, true);

                        logger.info("  Got term {} ", term);
                        assert (term != null);
                    }

                    // Check the general term
                    if (term == null) {
                        result.setSex(SexType.both);
                        term = mpTermService.getMPTerm(parameter.getStableId(), result, null, connection, SIGNIFICANCE_THRESHOLD, true);

                        logger.info("  Got term {} ", term);
                        assert (term != null);
                    }
                }
                break;
            }

        }

        assert(foundParameter);

    }


    @Test
    public void testSexuallyDimorphicCategoricalResult() throws SQLException {

        Integer parameterId = pDAO.getParameterByStableId("IMPC_EYE_092_001").getId();
        List<ResultDTO> categoriocalResults = mpGenerator.getCategoricalResults(connection);

        assert(categoriocalResults.size()>100);

        Boolean foundParameter = Boolean.FALSE;

        for (ResultDTO result : categoriocalResults) {

            if ( ! result.getGeneAcc().equals("MGI:88555")) {
                continue;
            }

            logger.info("Found parameter {} for {} (id: {}). Looking for {}", pDAO.getParameterById(result.getParameterId()), result.getGeneAcc(), result.getParameterId(), parameterId);
            if (result.getParameterId().equals(parameterId)) {
                foundParameter = Boolean.TRUE;

                // process this result to see if we get a term
                if (result.getNullTestPvalue() < 0.0001) {

                    // Effect is significant, find out which term to associate

                    Parameter parameter = pDAO.getParameterById(result.getParameterId());

                    // Check the female specific term
                    if (result.getFemalePvalue() != null && result.getFemalePvalue() <= SIGNIFICANCE_THRESHOLD) {

                        result.setSex(SexType.female);
                        OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), result, SexType.female, connection, SIGNIFICANCE_THRESHOLD, true);

                        logger.info("  Got term {} ", term);
                        assert (term != null);
                    }

                    // Check the male specific term
                    if (result.getMalePvalue() != null && result.getMalePvalue() <= SIGNIFICANCE_THRESHOLD) {

                        result.setSex(SexType.male);
                        OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), result, SexType.male, connection, SIGNIFICANCE_THRESHOLD, true);

                        logger.info("  Got term {} ", term);
                        assert (term != null);
                    }
                }
                break;
            }

        }

        assert(foundParameter);

    }


    @Test
    public void testGetLineEmapOntologyResults() throws SQLException {

        List<ResultDTO> lineResults = mpGenerator.getLineOntologyResults(connection, "emap");

        assert(lineResults.size()>100);

        for (ResultDTO result : lineResults) {
            assertTrue(result.getZygosity() != null);
        }

    }

    @Test
    public void testGetLineOntologyResults() throws SQLException {

        List<ResultDTO> lineResults = mpGenerator.getLineOntologyResults(connection, "mpath");

        assert(lineResults.size()>100);

        for (ResultDTO result : lineResults) {
            assertTrue(result.getZygosity() != null);
        }


    }


    @Test
    public void testGetGrossPathologyOntologyResults() throws SQLException {

        List<ResultDTO> lineResults = mpGenerator.getLineOntologyResults(connection, "mp");

        assert(lineResults.size()>100);

        for (ResultDTO result : lineResults) {

            assertTrue(result.getZygosity() != null);

            if (result.getMpTerm().equals("") && result.getGeneAcc().equals("")){
                assertTrue(result.getZygosity().equals(ZygosityType.heterozygote));
            }
        }


    }


}