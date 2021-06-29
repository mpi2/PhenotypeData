package utilities;

import org.junit.jupiter.api.Test;
import org.mousephenotype.cda.utilities.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Created by ilinca on 06/01/2017.
 */
public class CommonUtilsTest {

    @Test
    public void testGetBitMask() {

        // 32 bit is enough
        List<Boolean> truthValues = new ArrayList<>();
        truthValues.add(true);
        truthValues.add(false);
        truthValues.add(true);
        truthValues.add(false);
        truthValues.add(false);
        truthValues.add(true);

        Double mask = CommonUtils.getBitMask(truthValues).get(0);

        assert (mask != null);
        assert (mask == 37 );

        // more than 32 bits needed
        for (int i = 0; i < 26; i++){
            truthValues.add(false);
        }
        truthValues.add(true);
        truthValues.add(false);
        truthValues.add(true);

        assert (CommonUtils.getBitMask(truthValues).get(0) == 37);
        assert (CommonUtils.getBitMask(truthValues).get(1) == 10);

    }

    @Test
    public void parseImpressStatusTest() {
        CommonUtils commonUtils = new CommonUtils();

        String[] retVal;

        retVal = commonUtils.parseImpressStatus(null);
        assertArrayEquals(new String[]{null, null}, retVal);

        retVal = commonUtils.parseImpressStatus("");
        assertArrayEquals(new String[]{null, null}, retVal);

        retVal = commonUtils.parseImpressStatus(" ");
        assertArrayEquals(new String[]{null, null}, retVal);

        retVal = commonUtils.parseImpressStatus(":");
        assertArrayEquals(new String[]{null, null}, retVal);

        retVal = commonUtils.parseImpressStatus("?");
        assertArrayEquals(new String[]{null, null}, retVal);

        retVal = commonUtils.parseImpressStatus("|");
        assertArrayEquals(new String[]{"|", null}, retVal);

        retVal = commonUtils.parseImpressStatus("IMPC_PARAMSC_005");
        assertArrayEquals(new String[]{"IMPC_PARAMSC_005", null}, retVal);

        retVal = commonUtils.parseImpressStatus("IMPC_PARAMSC_005?");
        assertArrayEquals(new String[]{"IMPC_PARAMSC_005", null}, retVal);

        retVal = commonUtils.parseImpressStatus("Parameter not measured - Sample clotting");
        assertArrayEquals(new String[]{"Parameter not measured - Sample clotting", null}, retVal);

        retVal = commonUtils.parseImpressStatus("IMPC_PARAMSC_005?Parameter not measured - Sample clotting");
        assertArrayEquals(new String[]{"IMPC_PARAMSC_005", "Parameter not measured - Sample clotting"}, retVal);

        retVal = commonUtils.parseImpressStatus("IMPC_PARAMSC_005:    Parameter not measured - Sample clotting  ");
        assertArrayEquals(new String[]{"IMPC_PARAMSC_005", "Parameter not measured - Sample clotting"}, retVal);
    }
}