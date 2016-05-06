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
package uk.ac.ebi.phenodigm.dao;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.ebi.phenodigm.model.MouseModel;

/**
 * Tests for class ExternalLinkBuilder
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class ExternalLinkBuilderTest {
    
    //MGI models
    MouseModel mgiHomozygote;
    MouseModel mgiHeterozygote;
    //IMPC/MGP models
    MouseModel impcHomozygote;
    MouseModel impcHeterozygote;
    //TODO: Test making Alleles
    @Before
    public void setUp() {
        mgiHomozygote = new MouseModel();
        mgiHomozygote.setMgiGeneId("MGI:2135961");
        mgiHomozygote.setMgiModelId(4321);
        mgiHomozygote.setSource("MGI");
        mgiHomozygote.setAllelicComposition("Fgf9<tm1Dor>/Fgf9<tm1Dor>");
        mgiHomozygote.setGeneticBackground("involves: 129S6/SvEvTac * C57BL/6");
        mgiHomozygote.setAlleleIds("MGI:2135961");
        mgiHomozygote.setAllelicCompositionLink("<a href=\"http://informatics.jax.org/accession/MGI:2135961\">Fgf9<sup>tm1Dor</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:2135961\">Fgf9<sup>tm1Dor</sup></a>");
        
        mgiHeterozygote = new MouseModel();
        mgiHeterozygote.setMgiGeneId("MGI:1233");
        mgiHeterozygote.setMgiModelId(888);
        mgiHeterozygote.setSource("MGI");
        mgiHeterozygote.setAllelicComposition("Pitx2<tm1Jfm>/Pitx2<tm2Jfm>");
        mgiHeterozygote.setGeneticBackground("involves: 129S4/SvJaeSor * C57BL/6J");
        mgiHeterozygote.setAlleleIds("MGI:2136268|MGI:2136269");
        mgiHeterozygote.setAllelicCompositionLink("<a href=\"http://informatics.jax.org/accession/MGI:2136268\">Pitx2<sup>tm1Jfm</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:2136269\">Pitx2<sup>tm2Jfm</sup></a>");
    
        impcHomozygote = new MouseModel();
        impcHomozygote.setMgiGeneId("MGI:2148742");
        impcHomozygote.setMgiModelId(27887);
        impcHomozygote.setSource("IMPC");
        impcHomozygote.setAllelicComposition("Cldn16<tm1a(KOMP)Wtsi>/Cldn16<tm1a(KOMP)Wtsi>");
        impcHomozygote.setGeneticBackground("MAPG");
        impcHomozygote.setAlleleIds(null);
        impcHomozygote.setAllelicCompositionLink("<a href=\"http://www.mousephenotype.org/data/genes/MGI:2148742\">Cldn16<sup>tm1a(KOMP)Wtsi</sup></a>/<a href=\"http://www.mousephenotype.org/data/genes/MGI:2148742\">Cldn16<sup>tm1a(KOMP)Wtsi</sup></a>");
        
        impcHeterozygote = new MouseModel();
        impcHeterozygote.setMgiGeneId("MGI:97874");
        impcHeterozygote.setMgiModelId(3773);
        impcHeterozygote.setSource("IMPC");
        impcHeterozygote.setAllelicComposition("Rb1<tm1Brd>/Rb1<+>");
        impcHeterozygote.setGeneticBackground("involves: 129S7/SvEvBrd * C57BL/6");
        impcHeterozygote.setAlleleIds(null);
        impcHeterozygote.setAllelicCompositionLink("<a href=\"http://www.mousephenotype.org/data/genes/MGI:97874\">Rb1<sup>tm1Brd</sup></a>/<a href=\"http://www.mousephenotype.org/data/genes/MGI:97874\">Rb1<sup>+</sup></a>");
        
    
    }

    /**
     * Test of formatAllelicCompositionHtml method, of class ExternalLinkBuilder.
     */
    @Test
    public void testFormatAllelicCompositionHtmlSingleAllele() {
        System.out.println("formatAllelicCompositionHtmlSingleAllele");
        String expResult = "Pitx2<sup>tm1Jfm</sup>";
        String result = ExternalLinkBuilder.formatAllelicCompositionHtml("Pitx2<tm1Jfm>");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of formatAllelicCompositionHtml method, of class ExternalLinkBuilder.
     */
    @Test
    public void testFormatAllelicCompositionHtmlTwoAllele() {
        String expResult = "Pitx2<sup>tm1Jfm</sup>/Pitx2<sup>tm2Jfm</sup>";
        String result = ExternalLinkBuilder.formatAllelicCompositionHtml("Pitx2<tm1Jfm>/Pitx2<tm2Jfm>");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkMgiHomozygote() {
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:2135961\">Fgf9<sup>tm1Dor</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:2135961\">Fgf9<sup>tm1Dor</sup></a>";
        String result = ExternalLinkBuilder.buildLink(mgiHomozygote);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkMgiHeterozygote() {
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:2136268\">Pitx2<sup>tm1Jfm</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:2136269\">Pitx2<sup>tm2Jfm</sup></a>";
        String result = ExternalLinkBuilder.buildLink(mgiHeterozygote);
        assertEquals(expResult, result);
    }
    
        
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkImpcHomozygote() {
        String expResult = "<a href=\"http://www.mousephenotype.org/data/genes/MGI:2148742\">Cldn16<sup>tm1a(KOMP)Wtsi</sup></a>/<a href=\"http://www.mousephenotype.org/data/genes/MGI:2148742\">Cldn16<sup>tm1a(KOMP)Wtsi</sup></a>";
        String result = ExternalLinkBuilder.buildLink(impcHomozygote);
        assertEquals(expResult, result);
    }

    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkImpcHeterozygoteNoAlelleId() {
        String expResult = "<a href=\"http://www.mousephenotype.org/data/genes/MGI:97874\">Rb1<sup>tm1Brd</sup></a>/<a href=\"http://www.mousephenotype.org/data/genes/MGI:97874\">Rb1<sup>+</sup></a>";
        String result = ExternalLinkBuilder.buildLink(impcHeterozygote);
        assertEquals(expResult, result);
    }

    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkMgiHeterozygoteNoAlelleId() {
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:97874\">Rb1<sup>tm1Brd</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:97874\">Rb1<sup>+</sup></a>";
        impcHeterozygote.setSource("MGI");
        String result = ExternalLinkBuilder.buildLink(impcHeterozygote);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkMgiHeterozygoteAlelleIdWithWildType() {
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:1857339\">Rb1<sup>tm1Brd</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:97874\">Rb1<sup>+</sup></a>";
        impcHeterozygote.setSource("MGI");
        impcHeterozygote.setAlleleIds("MGI:1857339");
        String result = ExternalLinkBuilder.buildLink(impcHeterozygote);
        assertEquals(expResult, result);
    }

    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkStrings() {
        String geneId = "MGI:101898";
        String source = "MGI";
        String allelicComposition = "Cd247/Pou2f1<tm1Tks>/Cd247/Pou2f1<tm1Tks>";
        String alleleIds = "MGI:2447062";
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:2447062\">Cd247/Pou2f1<sup>tm1Tks</sup></a>/<a href=\"http://informatics.jax.org/accession/MGI:2447062\">Cd247/Pou2f1<sup>tm1Tks</sup></a>";
        String result = ExternalLinkBuilder.buildLink(geneId, source, allelicComposition, alleleIds);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkStringsIMPCHetWildType() {
        String geneId = "MGI:2148742";
        String source = "IMPC";
        String allelicComposition = "Cldn16<tm1a(KOMP)Wtsi>/+";
        String alleleIds = null;
        String expResult = "<a href=\"http://www.mousephenotype.org/data/genes/MGI:2148742\">Cldn16<sup>tm1a(KOMP)Wtsi</sup></a>/<a href=\"http://www.mousephenotype.org/data/genes/MGI:2148742\">+</a>";
        String result = ExternalLinkBuilder.buildLink(geneId, source, allelicComposition, alleleIds);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkStringsMgpSourceAlsoProducesImpcLink() {
        String geneId = "MGI:2148742";
        String source = "MGP";
        String allelicComposition = "Cldn16<tm1a(KOMP)Wtsi>/+";
        String alleleIds = null;
        String expResult = "<a href=\"http://www.mousephenotype.org/data/genes/MGI:2148742\">Cldn16<sup>tm1a(KOMP)Wtsi</sup></a>/<a href=\"http://www.mousephenotype.org/data/genes/MGI:2148742\">+</a>";
        String result = ExternalLinkBuilder.buildLink(geneId, source, allelicComposition, alleleIds);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkStringsMt() {
        String geneId = "MGI:102492";
        String source = "MGI";
        String allelicComposition = "mt-Rnr2<m1Dwa>";
        String alleleIds = "MGI:3783752";
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:3783752\">mt-Rnr2<sup>m1Dwa</sup></a>";
        String result = ExternalLinkBuilder.buildLink(geneId, source, allelicComposition, alleleIds);
        assertEquals(expResult, result);
    }

    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testBuildLinkStringsMtNoAlleleId() {
        String geneId = "MGI:102492";
        String source = "MGI";
        String allelicComposition = "mt-Rnr2<m1Dwa>";
        //bound to happen somewhere
        String alleleIds = null;
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:102492\">mt-Rnr2<sup>m1Dwa</sup></a>";
        String result = ExternalLinkBuilder.buildLink(geneId, source, allelicComposition, alleleIds);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testAlleleBuildLinkStringNoAlleleId() {
        String geneId = "MGI:102492";
        String source = "MGI";
        String allele = "mt-Rnr2<m1Dwa>";
        //bound to happen somewhere
        String alleleId = null;
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:102492\">mt-Rnr2<sup>m1Dwa</sup></a>";

        if (alleleId == null || alleleId.isEmpty()) {
            alleleId = geneId;
        }

        String urlPattern = ExternalLinkBuilder.MouseHrefTags.getHrefTag(source);

        String result = String.format(urlPattern, alleleId, ExternalLinkBuilder.formatAllelicCompositionHtml(allele));
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testAlleleBuildLinkString() {
        String geneId = "MGI:102492";
        String source = "MGI";
        String allele = "mt-Rnr2<m1Dwa>";
        //bound to happen somewhere
        String alleleId = "MGI:3783752";
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:3783752\">mt-Rnr2<sup>m1Dwa</sup></a>";

        if (alleleId == null || alleleId.isEmpty()) {
            alleleId = geneId;
        }

        String urlPattern = ExternalLinkBuilder.MouseHrefTags.getHrefTag(source);

        String result = String.format(urlPattern, alleleId, ExternalLinkBuilder.formatAllelicCompositionHtml(allele));
        assertEquals(expResult, result);
    }
    
    /**
     * Test of buildLink method, of class ExternalLinkBuilder.
     */
    @Test
    public void testSpontaneousMutationBuildLinkString() {
        String geneId = "MGI:97743";
        String source = "MGI";
        String allelicComposition = "Poo/Poo<+>";
        //bound to happen somewhere
        String alleleId = "MGI:3040786";
        String expResult = "<a href=\"http://informatics.jax.org/accession/MGI:3040786\">Poo</a>/<a href=\"http://informatics.jax.org/accession/MGI:97743\">Poo<sup>+</sup></a>";
        String result = ExternalLinkBuilder.buildLink(geneId, source, allelicComposition, alleleId);
        assertEquals(expResult, result);
    }
     
 
}
