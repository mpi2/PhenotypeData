/*******************************************************************************
 *  Copyright © 2018 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.ri.services;

import org.junit.Before;
import org.junit.Test;
import org.mousephenotype.cda.ri.entities.Contact;
import org.mousephenotype.cda.ri.pojo.Summary;
import org.mousephenotype.cda.ri.pojo.SummaryDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;

/**
 * Updated by mrelac on 06/12/2018.
 */

@SpringBootTest
public class SummaryServiceTest extends BaseTest {

    public static final String user1 = "user1@ebi.ac.uk";
    public static final String user2 = "user2@ebi.ac.uk";
    public static final String user3 = "user3@ebi.ac.uk";
    public static final String user4 = "user4@ebi.ac.uk";


    @Autowired
    private SummaryService summaryService;


    @Before
    public void setUp() {
    }

    @Test
    public void getContacts() {
        List<String> expected = Arrays.asList(user1, user2, user3, user4);
        List<Contact> actual = summaryService.getContacts();
        assertEquals("Expected " + expected.size() + " contacts. Found " + actual.size(), actual.size(), expected.size());

        List<Contact> l;
        l = actual.stream().filter(c -> c.getEmailAddress().equalsIgnoreCase(user1)).collect(Collectors.toList());
        assertFalse("Expected to find " + user1, l.isEmpty());
        assertTrue("Expected inHtml true for " + user1, l.get(0).isInHtml());

        l = actual.stream().filter(c -> c.getEmailAddress().equalsIgnoreCase(user2)).collect(Collectors.toList());
        assertFalse("Expected to find " + user2, l.isEmpty());
        assertFalse("Expected inHtml false for " + user2, l.get(0).isInHtml());

        l = actual.stream().filter(c -> c.getEmailAddress().equalsIgnoreCase(user3)).collect(Collectors.toList());
        assertFalse("Expected to find " + user3, l.isEmpty());
        assertTrue("Expected inHtml true for " + user3, l.get(0).isInHtml());

        l = actual
            .stream().filter(c -> c.getEmailAddress().equalsIgnoreCase(user4)).collect(Collectors.toList());
        assertFalse("Expected to find " + user4, l.isEmpty());
        assertFalse("Expected inHtml false for " + user4, l.get(0).isInHtml());

    }

    @Test
    public void getContact() {
        Contact actual = summaryService.getContact(user4);
        assertEquals(user4, actual.getEmailAddress());
        assertFalse("Expected inHtml true for " + user4, actual.isInHtml());
    }

    @Test
    public void getGeneAccessionIds() {
        List<String> expected = Arrays.asList("MGI:103576", "MGI:1919199", "MGI:2443658");
        List<String> actual   = summaryService.getGeneAccessionIds(user1);
        assertEquals("Expected " + expected.size() + " gene accession ids. Found " + actual.size(), actual.size(), expected.size());
        assertTrue("Expected to find " + "MGI:103576", actual.contains("MGI:103576"));
        assertTrue("Expected to find " + "MGI:1919199", actual.contains("MGI:1919199"));
        assertTrue("Expected to find " + "MGI:2443658", actual.contains("MGI:2443658"));
    }

    // The data for this test comes from the gene core, and there are thousands of entries. Since the data is dynamic,
    // we can't test for specific SummaryDetail values by geneAccessionId. However, we can test for minimum count and
    // spot-check some known gene accession ids.
    @Test
    public void getSummaryDetailsByAcc() {
        int                        expectedCount = 22000;
        Map<String, SummaryDetail> actual        = summaryService.getSummaryDetailsByAcc();
        assertTrue("Expected at least " + expectedCount + " gene accession ids. Found " + actual.size(), actual.size() > expectedCount);
        assertTrue("Expected to find " + "MGI:103576", actual.containsKey("MGI:103576"));
        assertTrue("Expected to find " + "MGI:1919199", actual.containsKey("MGI:1919199"));
        assertTrue("Expected to find " + "MGI:2443658", actual.containsKey("MGI:2443658"));
    }

    // This test queries the gene core for current status. As such we can only test the email address , SummaryDetail
    // counts, and SummaryDetail gene accession ids returned.
    @Test
    public void getSummaryByEmailAddress() {
        Summary actual = summaryService.getSummaryByContact(summaryService.getContact(user1));
        assertEquals(user1, actual.getEmailAddress());
        assertEquals(3, actual.getDetails().size());
        assertTrue(actual.getDetails().stream().anyMatch(sd -> sd.getGeneAccessionId().equalsIgnoreCase("MGI:103576")));
        assertTrue(actual.getDetails().stream().anyMatch(sd -> sd.getGeneAccessionId().equalsIgnoreCase("MGI:1919199")));
        assertTrue(actual.getDetails().stream().anyMatch(sd -> sd.getGeneAccessionId().equalsIgnoreCase("MGI:2443658")));
    }

    @Test
    public void isRegisteredForGeneFalse() {
        boolean actual = summaryService.isRegisteredForGene(user1, "MGI:000000");
        assertFalse("Expected user1 NOT to be registered for MGI:000000", actual);

    }

    @Test
    public void isRegisteredForGeneTrue() {
        boolean actual = summaryService.isRegisteredForGene(user1, "MGI:103576");
        assertTrue("Expected user1 to be registered for MGI:103576", actual);
    }

    @Test
    public void integrationTest_registerGene_and_unregister_gene() {
        Summary summary;
        summary = summaryService.getSummaryByContact(summaryService.getContact(user1));
        assertEquals("Expected user1 to be registered for 3 genes but found " + summary.getDetails().size(), 3, summary.getDetails().size());

        assertFalse(summaryService.isRegisteredForGene(user1, "MGI:88081"));
        summaryService.registerGene(user1, "MGI:88081");
        summary = summaryService.getSummaryByContact(summaryService.getContact(user1));
        assertEquals("Expected user1 to be registered for 4 genes but found " + summary.getDetails().size(), 4, summary.getDetails().size());
        assertTrue("Expected user1 to be registered for MGI:88081 but they weren't", summaryService.isRegisteredForGene(user1, "MGI:88081"));

        summaryService.unregisterGene(user1, "MGI:88081");
        assertFalse(summaryService.isRegisteredForGene(user1, "MGI:88081"));
        summary = summaryService.getSummaryByContact(summaryService.getContact(user1));
        assertEquals("Expected user1 to be registered for 3 genes but found " + summary.getDetails().size(), 3, summary.getDetails().size());
    }
}
