package org.mousephenotype.cda.indexers.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class MpHpCsvReaderTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void readAll() throws IOException {
        Resource r = context.getResource("upheno_mapping_mp_hp_v2.csv");

        List<List<String>> list = MpHpCsvReader.readAll(r.getFile().getPath());

        String msg = "Expected 'curie_x' but found '" + list.get(0).get(5) + "'.";
        assertTrue(msg, list.get(0).get(5).toLowerCase().equals("curie_x"));
        msg = "Expected 'curie_y' but found '" + list.get(0).get(6) + "'.";
        assertTrue(msg, list.get(0).get(6).toLowerCase().equals("curie_y"));

        String curie_x;
        String curie_y;
        for (int i = 1; i < list.size(); i++) {
            curie_x = list.get(i).get(5);
            curie_y = list.get(i).get(6);
            msg = "Expected value starting with 'HP:' but found '" + curie_x + "'";
            assertTrue(msg, curie_x.toUpperCase().startsWith("HP:"));

            msg = "Expected value starting with 'MP:' but found '" + curie_y + "'";
            assertTrue(msg, curie_y.toUpperCase().startsWith("MP:"));
        }
    }
}
