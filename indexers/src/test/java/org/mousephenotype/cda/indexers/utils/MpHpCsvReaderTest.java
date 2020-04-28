package org.mousephenotype.cda.indexers.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {UtilsTestConfig.class})
public class MpHpCsvReaderTest {

    @Autowired
    private String owlpath;

    @Test
    public void readAll() throws IOException {

        String mpHpCsvPath = owlpath + "/" + MpHpCsvReader.MP_HP_CSV_FILENAME;

        List<List<String>> list = MpHpCsvReader.readAll(mpHpCsvPath);


        // TEST ONLY THOSE COLUMNS REQUIRED FOR MP-HP MAPPING.
        String hpTermName = list.get(0).get(MpHpCsvReader.HP_NAME_COL_OFFSET);
        String mpId = list.get(0).get(MpHpCsvReader.MP_ID_COL_OFFSET);

        // Test heading string
        String msg;
        msg = "Expected 'label_x' but found '" + hpTermName + "'.";
        assertTrue(msg, hpTermName.toLowerCase().equals("label_x"));

        msg = "Expected 'curie_y' but found '" + mpId + "'.";
        assertTrue(msg, mpId.toLowerCase().equals("curie_y"));

        // Test data rows
        for (int i = 1; i < list.size(); i++) {
            hpTermName = list.get(i).get(MpHpCsvReader.HP_NAME_COL_OFFSET);
            mpId = list.get(i).get(MpHpCsvReader.MP_ID_COL_OFFSET);
            msg = "Expected value ending in ' (HPO)' but found '" + hpTermName + "'";
            assertTrue(msg, hpTermName.toUpperCase().contains(" (HPO)"));

            msg = "Expected value starting with 'MP:' but found '" + mpId + "'";
            assertTrue(msg, mpId.toUpperCase().startsWith("MP:"));
        }
    }
}
