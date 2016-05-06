/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.phenodigm.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for Class MouseModel.
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class MouseModelTest {
    
    private MouseModel mgiHomozygote;
    private MouseModel mgiHeterozygote;
    private MouseModel miRna;
    
    @Before
    public void setUp() {
        mgiHomozygote = new MouseModel();
        mgiHomozygote.setMgiGeneId("MGI:1234");
        mgiHomozygote.setMgiModelId(4321);
        mgiHomozygote.setSource("MGI");
        mgiHomozygote.setGeneticBackground("involves: 129S6/SvEvTac * C57BL/6");
        mgiHomozygote.setAllelicComposition("Fgf9<tm1Dor>/Fgf9<tm1Dor>");
        mgiHomozygote.setAlleleIds("MGI:2135961");
        mgiHomozygote.setAllelicCompositionLink("<a href=\"http://informatics.jax.org/accession/MGI:2135961\">Fgf9<sup>tm1Dor</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:2135961\">Fgf9<sup>tm1Dor</sup></a>");
        
        mgiHeterozygote = new MouseModel();
        mgiHeterozygote.setMgiGeneId("MGI:1233");
        mgiHeterozygote.setMgiModelId(888);
        mgiHeterozygote.setSource("IMPC");
        mgiHeterozygote.setGeneticBackground("involves: 129S4/SvJaeSor * C57BL/6J");
        mgiHeterozygote.setAllelicComposition("Pitx2<tm1Jfm>/Pitx2<tm2Jfm>");
        mgiHeterozygote.setAlleleIds("MGI:2136269|MGI:2136268");
        mgiHeterozygote.setAllelicCompositionLink("<a href=\"http://informatics.jax.org/accession/MGI:2136268\">Pitx2<sup>tm1Jfm</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:2136269\">Pitx2<sup>tm2Jfm</sup></a>");

        miRna = new MouseModel();
        miRna.setMgiGeneId("MGI:1233");
        miRna.setMgiModelId(888);
        miRna.setSource("MGI");
        miRna.setGeneticBackground("C57BL/6N");
        miRna.setAllelicComposition("Mir141<sup>tm1(mirKO)Wtsi</sup>/+");
        miRna.setAlleleIds("MGI:2676826");
        miRna.setAllelicCompositionLink("<a href=\"http://informatics.jax.org/accession/MGI:2676826\">Mir141<sup>tm1(mirKO)Wtsi</a>/<a href=\"http://informatics.jax.org/accession/MGI:2136269\">Pitx2<sup>tm2Jfm</sup></a>");
    }

    /**
     * Test of hashCode method, of class MouseModel.
     */
    @Test
    public void testHashCode() {
        Set<MouseModel> modelSet = new HashSet<MouseModel>();
        modelSet.add(mgiHomozygote);
        modelSet.add(mgiHeterozygote);
        
        assertEquals(2, modelSet.size());
        
    }

    /**
     * Test of equals method, of class MouseModel.
     */
    @Test
    public void testEquals() {
        MouseModel other = mgiHeterozygote;
        MouseModel instance = mgiHomozygote;
        boolean expResult = false;
        boolean result = instance.equals(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of compareTo method, of class MouseModel.
     */
    @Test
    public void testCompareTo() {
        MouseModel other = mgiHeterozygote;
        MouseModel instance = mgiHomozygote;
        Set<MouseModel> modelSet = new TreeSet<MouseModel>();
        modelSet.add(other);
        modelSet.add(other);
        modelSet.add(instance);
        
        assertEquals(2, modelSet.size());
        
        List<MouseModel> sortedList = new ArrayList<MouseModel>();
        sortedList.addAll(modelSet);
        //models are sorted according to their modelId only
        assertEquals(mgiHeterozygote, sortedList.get(0));
    }

    /**
     * Test of toString method, of class MouseModel.
     */
    @Test
    public void testGetSource() {
        MouseModel instance = mgiHomozygote;
        String expResult = "MGI";
        String result = instance.getSource();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getAllelicCompositionLink method, of class MouseModel.
     */
    @Test
    public void testGetAllelicCompositionLink() {
        MouseModel instance = mgiHomozygote;
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:2135961\">Fgf9<sup>tm1Dor</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:2135961\">Fgf9<sup>tm1Dor</sup></a>";
        String result = instance.getAllelicCompositionLink();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getAllelicCompositionLink method, of class MouseModel.
     */
    @Test
    public void testGetAllelicCompositionLinkMultipleAlleles() {
        MouseModel instance = mgiHeterozygote;
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:2136268\">Pitx2<sup>tm1Jfm</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:2136269\">Pitx2<sup>tm2Jfm</sup></a>";
        String result = instance.getAllelicCompositionLink();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of toString method, of class MouseModel.
     */
    @Test
    public void testToString() {
        MouseModel instance = mgiHomozygote;
        String expResult = "MouseModel{MGI:1234_4321 (MGI) Fgf9<tm1Dor>/Fgf9<tm1Dor> involves: 129S6/SvEvTac * C57BL/6 MGI:2135961}";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
}
