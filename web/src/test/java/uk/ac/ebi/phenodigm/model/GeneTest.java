/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.phenodigm.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jj8
 */
public class GeneTest {

    @Test
    public void testHashCodeHashSetBehaviour() {
        Gene first = new Gene(new GeneIdentifier("Fgfr2", "MGI:12354"), null);
        Gene second = new Gene(new GeneIdentifier("Fgfr1", "MGI:12353"), null);
        Gene third = new Gene(new GeneIdentifier("Fgfr3", "MGI:12355"), new GeneIdentifier("FGFR3", "HGNC:12355"));
        
        Set<Gene> geneSet = new HashSet<Gene>();
        geneSet.add(first);
        geneSet.add(second);
        geneSet.add(third);
        
        assertEquals(3, geneSet.size());
    }
    
    
    @Test
    public void testEqualsCodeTreeSetBehaviour() {
        Gene first = new Gene(new GeneIdentifier("Fgfr2", "MGI:12354"), null);
        Gene second = new Gene(new GeneIdentifier("Fgfr1", "MGI:12353"), null);
        Gene third = new Gene(new GeneIdentifier("Fgfr3", "MGI:12355"), new GeneIdentifier("FGFR3", "HGNC:12355"));
        
        Set<Gene> geneSet = new TreeSet<Gene>();
        geneSet.add(first);
        geneSet.add(second);
        geneSet.add(third);
        
        assertEquals(3, geneSet.size());
              
    }
    
    @Test
    public void testHashCodeHashMapBehaviour() {
        GeneIdentifier one = new GeneIdentifier("Fgfr2", "MGI:12354");
        GeneIdentifier two = new GeneIdentifier("Fgfr1", "MGI:12353");
        GeneIdentifier three = new GeneIdentifier("Fgfr3", "MGI:12355");
                
        Gene first = new Gene(one, null);
        Gene second = new Gene(two, null);
        Gene third = new Gene(three, new GeneIdentifier("FGFR3", "HGNC:12355"));
        
        Map<GeneIdentifier, Gene> geneMap = new HashMap<GeneIdentifier, Gene>();
        geneMap.put(one, first);
        geneMap.put(two, second);
        geneMap.put(three, third);
        
        assertEquals(3, geneMap.size());
        
        Gene expected = new Gene(three, new GeneIdentifier("FGFR3", "HGNC:12355"));
        Gene got = geneMap.get(three);
        
        assertEquals(expected, got);
    }
    
    @Test
    public void testEqualsIsEquals() {
        Gene first = new Gene(new GeneIdentifier("Fgfr2", "MGI:12354"), null);
        Gene second = new Gene(new GeneIdentifier("Fgfr2", "MGI:12354"), null);
        assertEquals(first, second);
    
    }
    
    @Test
    public void testEqualsNotEquals() {
        Gene first = new Gene(new GeneIdentifier("Fgfr2", "MGI:12354"), null);
        Gene second = new Gene(new GeneIdentifier("Fgfr1", "MGI:12353"), null);
        boolean result = first.equals(second);
        assertFalse(result);
    }
    
    /**
     * Test of toString method, of class Gene.
     */
    @Test
    public void testToStringNullHumanGeneIdentifier() {
        Gene instance = new Gene(new GeneIdentifier("Fgfr2", "MGI:12354"), null);
        String expResult = "Gene{ Fgfr2{MGI:12354} - null }";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of toString method, of class Gene.
     */
    @Test
    public void testToStringWithHumanGeneIdentifier() {
        Gene instance = new Gene(new GeneIdentifier("Fgfr2", "MGI:12354"), new GeneIdentifier("FGFR2", "HGNC:12354"));
        String expResult = "Gene{ Fgfr2{MGI:12354} - FGFR2{HGNC:12354} }";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
}
