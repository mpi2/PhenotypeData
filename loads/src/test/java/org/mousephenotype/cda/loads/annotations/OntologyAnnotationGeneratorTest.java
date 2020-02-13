/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.annotations;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.db.repositories.PipelineRepository;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.db.statistics.ResultDTO;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mousephenotype.cda.loads.annotations.OntologyAnnotationGenerator.SIGNIFICANCE_THRESHOLD;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =  OntologyAnnotationGeneratorTestConfig.class)
@Transactional
public class OntologyAnnotationGeneratorTest {

    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private PipelineRepository pipelineRepository;

    @Autowired
    private MpTermService mpTermService;

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Autowired
    private DataSource komp2DataSource;

    private Connection connection;
    private OntologyAnnotationGenerator mpGenerator;

    @Before
    public void setUp() throws SQLException {

        connection = komp2DataSource.getConnection();
        connection.setAutoCommit(false);

        mpGenerator = new OntologyAnnotationGenerator(komp2DataSource, mpTermService, ontologyTermRepository, parameterRepository);
    }


    @Test
    public void testGetAllOptionsForParameter() throws Exception {
        Parameter parameter = parameterRepository.getFirstByStableId("IMPC_XRY_001_001");

        Map<String, OntologyTerm> results =  mpTermService.getAllOptionsForParameter(connection, ontologyTermRepository, parameter, PhenotypeAnnotationType.abnormal);

        // Should only have one result
        for(String key : results.keySet()) {
            assert(key.equals("Absent"));
            assert(results.get(key).equals("MP:0003456"));
        }
    }


    @Test
    public void testGetAnnotationTypeMap() throws Exception {
        Parameter parameter = parameterRepository.getFirstByStableId("IMPC_HEM_001_001");

        MultiKeyMap annotationsMap = mpTermService.getAnnotationTypeMap(parameter.getStableId(), connection, ontologyTermRepository, parameterRepository);

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
        Parameter parameter = parameterRepository.getFirstByStableId("MGP_PBI_036_001");

        MultiKeyMap annotationsMap = mpTermService.getAnnotationTypeMap(parameter.getStableId(), connection, ontologyTermRepository, parameterRepository);
        Set<OntologyTerm> annotationsFlatMap = new HashSet();
        for (Object k : annotationsMap.keySet()) {
//            String g = (String)k;
            annotationsFlatMap.add((OntologyTerm)annotationsMap.get(k));
        }

        // populate possible ontology associations
        Map<String, List<OntologyTerm>> shouldHave = new HashMap<>();
        for (ParameterOntologyAnnotation pa : parameter.getAnnotations()) {
            if(!shouldHave.containsKey(parameter.getStableId())) {
                shouldHave.put(parameter.getStableId(), new ArrayList<OntologyTerm>());
            }

            shouldHave.get(parameter.getStableId()).add(pa.getOntologyTerm());
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
        Parameter parameter = parameterRepository.getFirstByStableId("M-G-P_014_001_001");

        MultiKeyMap annotationsMap = mpTermService.getAnnotationTypeMap(parameter.getStableId(), connection, ontologyTermRepository, parameterRepository);

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

        List<Pipeline> pipelines = new ArrayList<>();
        pipelineRepository.findAll().forEach(pipelines::add);
        Collections.shuffle(pipelines);
        for (Pipeline pipeline : pipelines) {

            logger.info("Testing pipeline: {}", pipeline);
            List<Procedure> procedures = new ArrayList<>(pipeline.getProcedures());
            Collections.shuffle(procedures);
            for (Procedure procedure : procedures) {
                logger.info("Testing procedure: {}", procedure);
                for (Parameter parameter : procedure.getParameters()) {

                    logger.info("Testing parameter: {}", parameter);
                    MultiKeyMap annotationsMap = mpTermService.getAnnotationTypeMap(parameter.getStableId(), connection, ontologyTermRepository, parameterRepository);
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
        Parameter parameter = parameterRepository.getFirstByStableId("IMPC_HEM_001_001");

        Pipeline pipeline = pipelineRepository.getByStableId("MGP_001");
        List<ResultDTO> results = mpGenerator.getUnidimensionalResults(connection);
        assert(results.size()>100);
        for ( ResultDTO result : results) {
            if(result.getGeneAcc().equals("MGI:1926116")) {// Gene: Fam175b
                if(result.getParameterId().equals(parameter.getId())) { //Parameter: IMPC_HEM_001_001
                    if(result.getPipelineId().equals(pipeline.getId())) {
                        System.out.println("============= FOUND =============");
                        System.out.println(result);

                        OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), result, null, connection, 1.0f, true);
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

        final int EXPECTED_RRPLUS_RESULTS_COUNT = 500;
        List<ResultDTO> results = mpGenerator.getRRPlusResults(connection);

        Assert.assertTrue("Expected at least " + EXPECTED_RRPLUS_RESULTS_COUNT + " results but found " + results.size(), results.size() >= EXPECTED_RRPLUS_RESULTS_COUNT);
//        for (ResultDTO result : results) {
//            System.out.println(result);
//        }
    }
    @Ignore
    @Test
    public void testProcessRRPlusParameters() throws SQLException {

        mpGenerator.processRRPlusParameters(komp2DataSource.getConnection());
    }


    @Test
    public void testGetEmbryonicResults() throws SQLException {

        List<ResultDTO> results = mpGenerator.getEmbryonicResults(connection);

        for (ResultDTO result : results) {
            assertTrue(result.getNullTestPvalue()==null);
        }

        System.out.println(" Total results: " + results.size());
    }


    @Ignore
    @Test
    public void testInfertilityAssociations() throws SQLException {

        List<ResultDTO> lineResults = mpGenerator.getLineResults(connection);

        for (ResultDTO result : lineResults) {

            Parameter p = parameterRepository.findById(result.getParameterId()).get();

            parameterRepository.findById(result.getParameterId()).get();

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

        Parameter mp = parameterRepository.getFirstByStableId("IMPC_FER_001_001"); //fale fertility
        Parameter fp = parameterRepository.getFirstByStableId("IMPC_FER_019_001"); //female fertility
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
            embLineParams.add(parameterRepository.getFirstByStableId(parm));
        }

        assert(lineResults.size()>100);
        for (ResultDTO result : lineResults) {

            assert(embLineParams.contains(parameterRepository.findById(result.getParameterId()).get()));
        }
    }


    @Test
    public void testCategoricalResults() throws SQLException {

        Long parameterId = parameterRepository.getFirstByStableId("M-G-P_026_001_029").getId();
        List<ResultDTO> categoriocalResults = mpGenerator.getCategoricalResults(connection);

        assert(categoriocalResults.size()>100);

        Boolean foundCenpjMGPParameter = Boolean.FALSE;

        for (ResultDTO result : categoriocalResults) {

            if ( ! result.getGeneAcc().equals("MGI:2684927")) {
                continue;
            }

            logger.info("Found parameter {} for {} (id: {}). Looking for {}", parameterRepository.findById(result.getParameterId()).get(), result.getGeneAcc(), result.getParameterId(), parameterId);
            if (result.getParameterId().equals(parameterId)) {
                foundCenpjMGPParameter = Boolean.TRUE;

                // process this result to see if we get a term
                if (result.getNullTestPvalue() < 0.0001) {

                    // Effect is significant, find out which term to associate

                    Parameter parameter = parameterRepository.findById(result.getParameterId()).get();

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
    public void testSignificantCategoricalResultVps4aLensOpacity() throws  Exception {

        Long parameterId = parameterRepository.getFirstByStableId("IMPC_EYE_017_001").getId();
        List<ResultDTO> categoricalResults = mpGenerator.getCategoricalResults(connection);
        ResultDTO toSave = null;

        assert(categoricalResults.size()>100);
        Boolean foundParameter = Boolean.FALSE;

        for (ResultDTO result : categoricalResults) {

            if ( ! result.getGeneAcc().equals("MGI:1890520")) {
                continue;
            }

            toSave = result;

            logger.info("Found parameter {} for {} (id: {}). Looking for {}", parameterRepository.findById(result.getParameterId()).get(), result.getGeneAcc(), result.getParameterId(), parameterId);
            if (result.getParameterId().equals(parameterId)) {
                foundParameter = Boolean.TRUE;

                // process this result to see if we get a term
                if (result.getNullTestPvalue() < 0.0001) {

                    // Effect is significant, find out which term to associate

                    Parameter parameter = parameterRepository.findById(result.getParameterId()).get();

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

        // Don't actually save the result to the database
        mpGenerator.SAVE_RESULTS = Boolean.FALSE;

        // Ensure the result exists
        assertNotNull (toSave);

        // Attempt to "save" the result
        mpGenerator.initializeSexSpecificMap(connection);
        mpGenerator.initializeInsertPhenotypeCallSummaryStatement(connection);
        mpGenerator.initializeInsertCategoricalStatResultPhenotypeCallSummaryStatement(connection);
        mpGenerator.initializeInsertUnidimensionalStatResultPhenotypeCallSummaryStatement(connection);
        mpGenerator.initializeInsertRRPlusStatResultPhenotypeCallSummaryStatement(connection);

        mpGenerator.saveCategoricalResult(connection, toSave);
    }

    @Test
    public void testCategoricalResultBmp4EyelidClosure() throws SQLException {

        Long parameterId = parameterRepository.getFirstByStableId("IMPC_EYE_005_001").getId();
        List<ResultDTO> categoriocalResults = mpGenerator.getCategoricalResults(connection);

        assert(categoriocalResults.size()>100);

        Boolean foundParameter = Boolean.FALSE;

        for (ResultDTO result : categoriocalResults) {

            if ( ! result.getGeneAcc().equals("MGI:88180")) {
                continue;
            }

            logger.info("Found parameter {} for {} (id: {}). Looking for {}", parameterRepository.findById(result.getParameterId()).get(), result.getGeneAcc(), result.getParameterId(), parameterId);
            if (result.getParameterId().equals(parameterId)) {
                foundParameter = Boolean.TRUE;

                if (result.getNullTestPvalue() < 0.0001) {

                    // Effect is significant, find out which term to associate

                    Parameter parameter = parameterRepository.findById(result.getParameterId()).get();
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


    @Ignore
    @Test
    public void testSexuallyDimorphicCategoricalResult() throws SQLException {

        Long parameterId = parameterRepository.getFirstByStableId("IMPC_EYE_092_001").getId();
        List<ResultDTO> categoriocalResults = mpGenerator.getCategoricalResults(connection);

        assert(categoriocalResults.size()>100);

        Boolean foundParameter = Boolean.FALSE;

        for (ResultDTO result : categoriocalResults) {

            if ( ! result.getGeneAcc().equals("MGI:88555")) {
                continue;
            }

            logger.info("Found parameter {} for {} (id: {}). Looking for {}", parameterRepository.findById(result.getParameterId()).get(), result.getGeneAcc(), result.getParameterId(), parameterId);
            if (result.getParameterId().equals(parameterId)) {
                foundParameter = Boolean.TRUE;

                // process this result to see if we get a term
                if (result.getNullTestPvalue() < 0.0001) {

                    // Effect is significant, find out which term to associate

                    Parameter parameter = parameterRepository.findById(result.getParameterId()).get();

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