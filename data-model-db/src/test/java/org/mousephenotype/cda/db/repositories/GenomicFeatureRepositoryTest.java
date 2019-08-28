package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class GenomicFeatureRepositoryTest {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    GenomicFeatureRepository genomicFeatureRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql",
                "sql/h2/repositories/GenomicFeatureRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void getById_Accession() {
        OntologyTerm biotypeOt = new OntologyTerm("1", 2L);
        OntologyTerm subtypeOt = new OntologyTerm("2", 2L);
        CoordinateSystem coordinateSystem = new CoordinateSystem();
        coordinateSystem.setId(1);
        SequenceRegion sequenceRegion = new SequenceRegion();
        sequenceRegion.setId(5);
        GenomicFeature expected = new GenomicFeature(
                new DatasourceEntityId("MGI:1915120", 3L),
                "Enoph1", "enolase-phosphatase 1", null,null,
                biotypeOt, subtypeOt,
                sequenceRegion, 100039985, 100068760, 1, "48.46", "active");

        GenomicFeature actual = genomicFeatureRepository.getById_AccessionAndExternalDbShortName("MGI:1915120", "MGI");
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSymbol(), actual.getSymbol());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getBiotype().getId(), actual.getBiotype().getId());
        assertEquals(expected.getSubtype().getId(), actual.getSubtype().getId());
        assertEquals(expected.getSequenceRegion().getId(), actual.getSequenceRegion().getId());
        assertEquals(expected.getStart(), actual.getStart());
        assertEquals(expected.getEnd(), actual.getEnd());
        assertEquals(expected.getStrand(), actual.getStrand());
        assertEquals(expected.getcMposition(), actual.getcMposition());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void getById_AccessionAndExternalDbShortName() {
        OntologyTerm biotypeOt = new OntologyTerm("36", 2L);
        OntologyTerm subtypeOt = new OntologyTerm("36", 2L);
        CoordinateSystem coordinateSystem = new CoordinateSystem();
        coordinateSystem.setId(1);
        SequenceRegion sequenceRegion = new SequenceRegion();
        sequenceRegion.setId(7);
        GenomicFeature expected = new GenomicFeature(
                new DatasourceEntityId("MGI:3643847", 3L),
                "Zfp419", "zinc finger protein 419", null,null,
                biotypeOt, subtypeOt,
                sequenceRegion, 9058365, 9062173, 1, "5.32", "active");

        GenomicFeature actual = genomicFeatureRepository.getById_Accession("MGI:3643847");
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSymbol(), actual.getSymbol());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getBiotype().getId(), actual.getBiotype().getId());
        assertEquals(expected.getSubtype().getId(), actual.getSubtype().getId());
        assertEquals(expected.getSequenceRegion().getId(), actual.getSequenceRegion().getId());
        assertEquals(expected.getStart(), actual.getStart());
        assertEquals(expected.getEnd(), actual.getEnd());
        assertEquals(expected.getStrand(), actual.getStrand());
        assertEquals(expected.getcMposition(), actual.getcMposition());
        assertEquals(expected.getStatus(), actual.getStatus());
    }
}