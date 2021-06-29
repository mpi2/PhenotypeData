package utilities;

import org.junit.jupiter.api.Test;
import org.mousephenotype.cda.dto.LifeStage;
import org.mousephenotype.cda.utilities.LifeStageMapper;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LifeStageMapperTest {

    @Test
    public void testLifeStageMapper() {
        final Map<String, LifeStage> lifeStages = new HashMap<>();
        lifeStages.put("IMPC_EVL_001_001", LifeStage.E9_5);
        lifeStages.put("IMPC_DXA_001_001", LifeStage.EARLY_ADULT);
        lifeStages.put("IMPCLA_DXA_001_001", LifeStage.LATE_ADULT);
        lifeStages.forEach((param, expectedStage) -> assertEquals(LifeStageMapper.getLifeStage(param), expectedStage));

        final Map<String, LifeStage> badLifeStages = new HashMap<>();
        lifeStages.put("IMPC_EVO_001_001", LifeStage.E12_5);
        lifeStages.put("IMPC_DXA_001_001", LifeStage.LATE_ADULT);
        badLifeStages.forEach((param, bad) -> assertNotEquals(LifeStageMapper.getLifeStage(param), bad));

    }
}