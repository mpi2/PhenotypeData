package org.mousephenotype.cda.indexers.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {UtilsTestConfig.class})
public class MpToHpTermsTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private   String owlpath;

    @Test
    public void mpToHpTermsTest() throws IndexerException {

        String mpHpCsvPath = owlpath + "/" + MpHpCsvReader.MP_HP_CSV_FILENAME;

        Map<String, Set<String>> terms = IndexerMap.getMpToHpTerms(mpHpCsvPath);

        // Spot-check some terms:
        /*
            MP:0001967: 'Hearing impairment'
            MP:0002001: 'Blindness'
            MP:0003342: 'Supernumerary spleens', 'Accessory spleen', 'Polysplenia'
            MP:0030505: 'Impacted tooth', 'Multiple impacted teeth'
            MP:0012674: 'Myelin outfoldings', 'Myelin tomacula'
            MP:0001560: 'Abnormal circulating insulin level', 'Abnormal insulin level'
        */

        assertTrue(terms.get("MP:0001967").contains("Hearing impairment"));

        assertTrue(terms.get("MP:0002001").contains("Blindness"));

        assertTrue(terms.get("MP:0003342").contains("Supernumerary spleens")); // Contains 3 HP terms
        assertTrue(terms.get("MP:0003342").contains("Accessory spleen"));
        assertTrue(terms.get("MP:0003342").contains("Polysplenia"));

        assertTrue(terms.get("MP:0030505").contains("Impacted tooth"));
        assertTrue(terms.get("MP:0030505").contains("Multiple impacted teeth"));

        assertTrue(terms.get("MP:0012674").contains("Myelin outfoldings"));
        assertTrue(terms.get("MP:0012674").contains("Myelin tomacula"));

        assertTrue(terms.get("MP:0001560").contains("Abnormal circulating insulin level"));
        assertTrue(terms.get("MP:0001560").contains("Abnormal insulin level"));
    }

    @Test
    public void printAllMappings() throws IndexerException {

        String mpHpCsvPath = owlpath + "/" + MpHpCsvReader.MP_HP_CSV_FILENAME;

        Map<String, Set<String>> terms = IndexerMap.getMpToHpTerms(mpHpCsvPath);

        System.out.println();
        System.out.println("mpIds with multiple hpTerms are separated by :: (double colons).");
        System.out.println("There are " + terms.size() + " mpId -> hpTerm mappings:");
        terms
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(x -> {
                    System.out.println(x.getKey() + ": " + StringUtils.join(x.getValue(), "::"));
            });
    }
}
