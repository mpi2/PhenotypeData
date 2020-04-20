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

        String mpHpCsvPath = owlpath + "/mp-hp.csv";

        List<List<String>> list = MpHpCsvReader.readAll(mpHpCsvPath);

        String curie_hp = list.get(0).get(MpHpCsvReader.CURIE_HP_COLUMN);
        String curie_mp = list.get(0).get(MpHpCsvReader.CURIE_MP_COLUMN);

        // Test heading
        String msg = "Expected 'curie_x' but found '" + curie_hp + "'.";
        assertTrue(msg, curie_hp.toLowerCase().equals("curie_x"));
        msg = "Expected 'curie_y' but found '" + curie_mp + "'.";
        assertTrue(msg, curie_mp.toLowerCase().equals("curie_y"));

        // Test data rows
        for (int i = 1; i < list.size(); i++) {
            curie_hp = list.get(i).get(MpHpCsvReader.CURIE_HP_COLUMN);
            curie_mp = list.get(i).get(MpHpCsvReader.CURIE_MP_COLUMN);
            msg = "Expected value starting with 'HP:' but found '" + curie_hp + "'";
            assertTrue(msg, curie_hp.toUpperCase().startsWith("HP:"));

            msg = "Expected value starting with 'MP:' but found '" + curie_mp + "'";
            assertTrue(msg, curie_mp.toUpperCase().startsWith("MP:"));
        }
    }
}
