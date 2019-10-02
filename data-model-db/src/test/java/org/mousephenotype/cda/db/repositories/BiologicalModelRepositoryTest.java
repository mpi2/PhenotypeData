package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.BiologicalModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class BiologicalModelRepositoryTest {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    BiologicalModelRepository biologicalModelRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql",
                "sql/h2/repositories/BiologicalModelRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void getBiologicalModelByDatasource_IdAndAllelicCompositionAndGeneticBackgroundAndZygosity() {

        List<BiologicalModel> expectedList = Arrays.asList(
                new BiologicalModel(46L, "Cacna2d2<du-td>/Cacna2d2<du-td>", "involves: DBA/2J", null),
                new BiologicalModel(2L, "Apoa1<tm1Unc>/Apoa1<tm1Unc>", "involves: 129P2/OlaHsd", null),
                new BiologicalModel(37883L, "Tbc1d2<tm1(NCOM)Cmhd>/Tbc1d2<tm1(NCOM)Cmhd>", "involves: C57BL/6N", "homozygote"),
                new BiologicalModel(7L, "Supt4a<tm1b(EUCOMM)Hmgu>/Supt4a<tm1b(EUCOMM)Hmgu>", "C57BL/6N-Supt4a<tm1b(EUCOMM)Hmgu>/Tcp", null),
                new BiologicalModel(37877L, "", "involves: C3H/HeH", "homozygote"),
                new BiologicalModel(12L, "Cd83<lcd4>/Cd83<lcd4>", "involves: C57BL/6J", null));

        List<BiologicalModel> actualList = new ArrayList<>();
        for (BiologicalModel bm : expectedList) {
            actualList.add(biologicalModelRepository
                                   .getBiologicalModelByIdAndAllelicCompositionAndGeneticBackgroundAndZygosity(bm.getId(), bm.getAllelicComposition(), bm.getGeneticBackground(), bm.getZygosity()));
        }

        assertTrue(actualList.size() == 6);

        for (BiologicalModel expected : expectedList) {
            assertTrue(actualList.contains(expected));
        }
    }
}