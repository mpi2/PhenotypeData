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
import uk.ac.ebi.phenodigm.model.GeneIdentifier;

/**
 *
 * @author jj8
 */
public class GeneAssociationSummaryTest {
    
    GeneAssociationSummary instance;
    GeneIdentifier mouseGeneIdentifier;
    GeneIdentifier humanGeneIdentifier;
    AssociationSummary summary;        

    public GeneAssociationSummaryTest() {
        mouseGeneIdentifier = new GeneIdentifier("Fgfr2", "MGI:1234");
        humanGeneIdentifier = new GeneIdentifier("FGFR2", "HGNC:46785");
        summary = new AssociationSummary(true, true, true, 89.00, 78.01);
        instance = new GeneAssociationSummary(humanGeneIdentifier, mouseGeneIdentifier, summary);
    }

    /**
     * Test of getHumanGeneIdentifier method, of class GeneAssociationSummary.
     */
    @Test
    public void testGetHumanGeneIdentifier() {
        GeneIdentifier expResult = humanGeneIdentifier;
        GeneIdentifier result = instance.getHgncGeneIdentifier();
        assertEquals(expResult, result);
    }
    /**
     * Test of getModelGeneIdentifier method, of class GeneAssociationSummary.
     */
    @Test
    public void testGetMouseGeneIdentifier() {
        GeneIdentifier expResult = mouseGeneIdentifier;
        GeneIdentifier result = instance.getModelGeneIdentifier();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAssociationSummary method, of class GeneAssociationSummary.
     */
    @Test
    public void testGetAssociationSummary() {
        AssociationSummary expResult = summary;
        AssociationSummary result = instance.getAssociationSummary();
        assertEquals(expResult, result);

    }

    /**
     * Test of toString method, of class GeneAssociationSummary.
     */
    @Test
    public void testToString() {
        String expResult = "GeneAssociationSummary{FGFR2{HGNC:46785} Fgfr2{MGI:1234} AssociationSummary{associatedInHuman=true, hasLiteratureEvidence=true, inLocus=true, locus=null, bestMgiScore=89.0, bestImpcScore=78.01}}";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
}
