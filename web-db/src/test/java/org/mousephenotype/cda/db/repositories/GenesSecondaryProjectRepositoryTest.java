package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.GenesSecondaryProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class GenesSecondaryProjectRepositoryTest {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    GenesSecondaryProjectRepository genesSecondaryProjectRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "h2/schema.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void getAllBySecondaryProjectId() {
        Set<GenesSecondaryProject> genes = genesSecondaryProjectRepository.getAllBySecondaryProjectId("idg");
        assertEquals(genes.size(), 359);
        Map<String, GenesSecondaryProject> genesMap = new HashMap<>(400);
        for (GenesSecondaryProject gene : genes) {
            genesMap.put(gene.getMgiGeneAccessionId(), gene);
        }
        assertTrue(genesMap.size() == 359);

        GenesSecondaryProject expectedGene = new GenesSecondaryProject("MGI:2685341", "idg", "GPCRs");
        GenesSecondaryProject actualGene = genesMap.get("MGI:2685341");
        assertEquals(expectedGene, actualGene);
    }

    @Test
    public void getAllBySecondaryProjectIdAndGroupLabel() {
        Set<GenesSecondaryProject> genes = genesSecondaryProjectRepository.getAllBySecondaryProjectIdAndGroupLabel("idg", "Kinases");
        assertEquals(genes.size(), 127);
        Map<String, GenesSecondaryProject> genesMap = new HashMap<>(200);
        for (GenesSecondaryProject gene : genes) {
            genesMap.put(gene.getMgiGeneAccessionId(), gene);
        }
        assertTrue(genesMap.size() == 127);

        GenesSecondaryProject expectedGene = new GenesSecondaryProject("MGI:2443413", "idg", "Kinases");
        GenesSecondaryProject actualGene = genesMap.get("MGI:2443413");
        assertEquals(expectedGene, actualGene);
    }

    @Test
    public void getAllBySecondaryProjectIdAllIdg() {
        Set<GenesSecondaryProject> genes = genesSecondaryProjectRepository.getAllBySecondaryProjectId("idg");
        assertTrue(genes.size() > 127);
        Map<String, GenesSecondaryProject> genesMap = new HashMap<>(200);
        for (GenesSecondaryProject gene : genes) {
            genesMap.put(gene.getMgiGeneAccessionId(), gene);
        }
        assertTrue(genesMap.size() > 127);

        GenesSecondaryProject expectedGene = new GenesSecondaryProject("MGI:2443413", "idg", "Kinases");
        GenesSecondaryProject actualGene = genesMap.get("MGI:2443413");
        assertEquals(expectedGene, actualGene);
    }
}