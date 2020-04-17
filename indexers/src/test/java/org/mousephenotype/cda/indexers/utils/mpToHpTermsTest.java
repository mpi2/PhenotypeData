package org.mousephenotype.cda.indexers.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {UtilsTestConfig.class})
public class mpToHpTermsTest {

    @Autowired
    private String owlpath;

    @Autowired
    private ApplicationContext context;

    @Test
    public void getMpToHpTermsTest() throws IndexerException {

        String mpHpCsvPath = owlpath + "/mp-hp.csv";

        Map<String, List<String>> terms = IndexerMap.getMpToHpTerms(mpHpCsvPath);

        // Spot-check some terms:
        /*
            MP:0001967: HP:0000365  (deafness)
            MP:0002001: HP:0000618 (blindness)
            MP:0003342: HP:0001747, HP:0001748, HP:0009799
            MP:0030505: HP:0001571, HP:0011079
            MP:0012674: HP:0004336, HP:0030175
            MP:0001560: HP:0040214, HP:0040215
        */

        assertTrue(terms.get("MP:0001967").contains("HP:0000365")); // deafness

        assertTrue(terms.get("MP:0002001").contains("HP:0000618")); // blindness

        assertTrue(terms.get("MP:0003342").contains("HP:0001747")); // Contains 3 HP terms
        assertTrue(terms.get("MP:0003342").contains("HP:0001748"));
        assertTrue(terms.get("MP:0003342").contains("HP:0009799"));

        assertTrue(terms.get("MP:0030505").contains("HP:0001571")); // Contains 2 HP terms
        assertTrue(terms.get("MP:0030505").contains("HP:0011079"));

        assertTrue(terms.get("MP:0012674").contains("HP:0004336")); // Contains 2 HP terms
        assertTrue(terms.get("MP:0012674").contains("HP:0030175"));

        assertTrue(terms.get("MP:0001560").contains("HP:0040214")); // Contains 2 HP terms
        assertTrue(terms.get("MP:0001560").contains("HP:0040215"));

        // Uncomment to see MP terms mapped to multiple HP terms.
//        terms
//            .entrySet()
//            .stream()
//            .forEach(x -> {
//                if (x.getValue().size() > 1)
//                    System.out.println(x.getKey() + ": " + StringUtils.join(x.getValue(), ", "));
//            });
    }
}
