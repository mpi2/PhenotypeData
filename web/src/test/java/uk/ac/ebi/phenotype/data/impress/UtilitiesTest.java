package uk.ac.ebi.phenotype.data.impress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.OntologyTermDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.impress.Utilities;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.StageUnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.phenotype.TestConfig;

import java.util.Arrays;
import java.util.List;



@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/applicationTest.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
@Transactional
public class UtilitiesTest {

    @Autowired
    PhenotypePipelineDAO pDAO;

    @Autowired
    OntologyTermDAO ontologyTermDAO;

    @Autowired
    Utilities impressUtilities;


    @Test
    public void testCheckTypeParameterString() {
        Parameter p = pDAO.getParameterByStableId("ESLIM_003_001_006");
        String value= "2.092";
        ObservationType oType = impressUtilities.checkType(p, value);

        System.out.println(oType);
        assert(oType.equals(ObservationType.time_series));
    }


    @Test
    public void testCheckStageConversion() throws Exception {

        List<String> goodStages = Arrays.asList("9.5", "12.5", "20");
        List<StageUnitType> goodStageUnits = Arrays.asList(StageUnitType.DPC, StageUnitType.DPC, StageUnitType.THEILER);
        List<String> goodTerms = Arrays.asList( "embryonic day 9.5", "embryonic day 12.5", "TS20,embryo");

        List<String> badStages = Arrays.asList( "9.5", "a", "30");
        List<StageUnitType> badStageUnits = Arrays.asList(StageUnitType.THEILER, StageUnitType.THEILER, StageUnitType.DPC);

        for (int i = 0; i<goodStages.size(); i++) {
            String stage = goodStages.get(i);
            StageUnitType stageUnit = goodStageUnits.get(i);

            System.out.println("Testing :" + stage + " " + stageUnit.getStageUnitName());

            // Need a method to convert impress input to representative EFO term
            OntologyTerm term = impressUtilities.getStageTerm(stage, stageUnit);
            org.junit.Assert.assertTrue(term.getName().equals(goodTerms.get(i)));
        }

        for (int i = 0; i<badStages.size(); i++) {
            String stage = badStages.get(i);
            StageUnitType stageUnit = badStageUnits.get(i);

            System.out.println("Testing bad case:" + stage + " " + stageUnit.getStageUnitName());

            // Need a method to convert impress input to represnetative EFO term
            OntologyTerm term = impressUtilities.getStageTerm(stage, stageUnit);
            org.junit.Assert.assertTrue(term==null);
        }


    }

}
