package uk.ac.ebi.phenotype.web.controller;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilinca on 05/01/2017.
 */
public class ControllerUtilsTest {

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

        Double mask = ControllerUtils.getBitMask(truthValues).get(0);

        assert (mask != null);
        assert (mask == 37 );

        // more than 32 bits needed
        for (int i = 0; i < 26; i++){
            truthValues.add(false);
        }
        truthValues.add(true);
        truthValues.add(false);
        truthValues.add(true);

        assert (ControllerUtils.getBitMask(truthValues).get(0) == 37);
        assert (ControllerUtils.getBitMask(truthValues).get(1) == 5);

    }

}
