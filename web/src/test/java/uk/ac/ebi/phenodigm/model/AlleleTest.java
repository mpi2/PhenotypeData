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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class AlleleTest {
    
    private Allele instance;
    
    private final String source = "SOURCE";
    private final String geneId = "MGI:99999";
    private final String alleleSymbol = "Aa<Tm1.23>";
    private final String alleleId = "MGI:12345";

    
    @Before
    public void setUp() {
        instance = new Allele(source, geneId, alleleSymbol, alleleId);
    }
    
    @Test
    public void testGetSetGeneId() {
        assertEquals(geneId, instance.getGeneId());
    }

    @Test
    public void testGetSetAlleleSymbol() {
        assertEquals(alleleSymbol, instance.getAlleleSymbol());
    }

    @Test
    public void testGetSetAlleleId() {
        assertEquals(alleleId, instance.getAlleleId());
    }

    @Test
    public void testHashCode() {
    }

    @Test
    public void testEquals() {
    }

    @Test
    public void testToString() {
        String expected = "Allele{source=" + source + ", geneId=" + geneId + ", alleleId=" + alleleId + ", alleleSymbol=" + alleleSymbol + '}';
        assertEquals(expected, instance.toString());
    }
    
}
