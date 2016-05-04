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
package uk.ac.ebi.phenodigm.web;

import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.ebi.phenodigm.model.DiseaseIdentifier;

/**
 *
 * @author jj8
 */
public class DiseaseAssociationSummaryTest {
    
    private final DiseaseAssociationSummary instance;
    private final DiseaseIdentifier diseaseIdentifier;
    private final String diseaseTerm;
    private final AssociationSummary summary;
    
    public DiseaseAssociationSummaryTest() {
        diseaseIdentifier = new DiseaseIdentifier("OMIM:12345");
        diseaseTerm = "Wibble-wobble syndrome";
        summary = new AssociationSummary(true, true, true, 89.34, 87.65);
        instance = new DiseaseAssociationSummary(diseaseIdentifier, diseaseTerm, summary);
    }

    /**
     * Test of getDiseaseIdentifier method, of class DiseaseAssociationSummary.
     */
    @Test
    public void testGetDiseaseIdentifier() {
        System.out.println("getDiseaseIdentifier");
        DiseaseIdentifier expResult = diseaseIdentifier;
        DiseaseIdentifier result = instance.getDiseaseIdentifier();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDiseaseTerm method, of class DiseaseAssociationSummary.
     */
    @Test
    public void testGetDiseaseTerm() {
        System.out.println("getDiseaseTerm");
        String expResult = diseaseTerm;
        String result = instance.getDiseaseTerm();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAssociationSummary method, of class DiseaseAssociationSummary.
     */
    @Test
    public void testGetAssociationSummary() {
        System.out.println("getAssociationSummary");
        AssociationSummary expResult = summary;
        AssociationSummary result = instance.getAssociationSummary();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class DiseaseAssociationSummary.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String expResult = "DiseaseAssociationSummary{OMIM:12345 AssociationSummary{associatedInHuman=true, hasLiteratureEvidence=true, inLocus=true, locus=null, bestMgiScore=89.34, bestImpcScore=87.65}}";
        String result = instance.toString();
        System.out.println(result);
        assertEquals(expResult, result);
    }
    
}
