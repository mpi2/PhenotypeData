/*
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * and Genome Research Limited
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenodigm.model;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author jj8
 */
public class DiseaseIdentifierTest {
    
    @Test
    public void testConstructor_super() {
        DiseaseIdentifier instance = new DiseaseIdentifier("OMIM", "101600");
        assertEquals("OMIM", instance.getDatabaseCode());
        assertEquals("101600", instance.getDatabaseAcc());
    }

    @Test
    public void testCompareTo() {
        
        DiseaseIdentifier decypherDisease1 = new DiseaseIdentifier("DECYPHER:1234");
        DiseaseIdentifier omimDisease1 = new DiseaseIdentifier("OMIM:1234");
        DiseaseIdentifier omimDisease2 = new DiseaseIdentifier("OMIM:1235");
        DiseaseIdentifier orphanetDisease1 = new DiseaseIdentifier("ORPHANET:1234");
        
        List<DiseaseIdentifier> diseaseList  = new ArrayList<>();
        
        diseaseList.add(orphanetDisease1);
        diseaseList.add(omimDisease1);
        diseaseList.add(decypherDisease1);
        diseaseList.add(omimDisease2);
        
        List<DiseaseIdentifier> expectedDiseaseList  = new ArrayList<>();
        
        expectedDiseaseList.add(decypherDisease1);        
        expectedDiseaseList.add(omimDisease1);
        expectedDiseaseList.add(omimDisease2);
        expectedDiseaseList.add(orphanetDisease1);
        
                
        Collections.sort(diseaseList);
        
        for (DiseaseIdentifier diseaseIdentifier : expectedDiseaseList) {
            System.out.println(diseaseIdentifier);
        }
        
        assertTrue(diseaseList.equals(expectedDiseaseList));
    }
    
    @Test
    public void testEquals() {
        DiseaseIdentifier decypherDisease1 = new DiseaseIdentifier("DECYPHER:1234");
        DiseaseIdentifier decypherDisease2 = new DiseaseIdentifier("DECYPHER:1234");
        
        assertTrue(decypherDisease1.equals(decypherDisease2));
        
        Set<DiseaseIdentifier> diseaseSet = new HashSet<>();
        diseaseSet.add(decypherDisease1);
        diseaseSet.add(decypherDisease2);
        
        assertEquals(1, diseaseSet.size());
    }
    
    @Test
    public void testGetOmimExternalUri() {
        DiseaseIdentifier diseaseIdentifier = new DiseaseIdentifier("OMIM:101600");
        String expected = "http://omim.org/entry/101600";
        String result = diseaseIdentifier.getExternalUri();
        System.out.println(result);
        assertEquals(expected, result);
    }
    
    @Test
    public void testGetDecipherExternalUri() {
        DiseaseIdentifier diseaseIdentifier = new DiseaseIdentifier("DECIPHER:18");
        String expected = "https://decipher.sanger.ac.uk/syndrome/18";
        String result = diseaseIdentifier.getExternalUri();
        System.out.println(result);
        assertEquals(expected, result);
    }
    
    @Test
    public void testGetOrphanetExternalUri() {
        DiseaseIdentifier diseaseIdentifier = new DiseaseIdentifier("ORPHANET:287");
        String expected = "http://www.orpha.net/consor/cgi-bin/OC_Exp.php?Lng=GB&Expert=287";
        String result = diseaseIdentifier.getExternalUri();
        System.out.println(result);
        assertEquals(expected, result);
    }
    
    @Test
    public void testGetUnknownExternalUri() {
        DiseaseIdentifier diseaseIdentifier = new DiseaseIdentifier("WIBBLE:287");
        String expected = "";
        String result = diseaseIdentifier.getExternalUri();
        System.out.println(result);
        assertEquals(expected, result);
    }
}
