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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class DiseaseModelAssociationTest {
    
    DiseaseModelAssociation first;
    DiseaseModelAssociation second;
    DiseaseModelAssociation third;

    @Before
    public void setUp() {
        first = new DiseaseModelAssociation();
        first.setDiseaseIdentifier(new DiseaseIdentifier("OMIM:00001"));
        MouseModel firstModel = new MouseModel();
        firstModel.setMgiGeneId("MGI:0001");
        firstModel.setMgiModelId(11111);
        firstModel.setPhenotypeTerms(new ArrayList<>());
        first.setMouseModel(firstModel);
                
        second = new DiseaseModelAssociation();
        second.setDiseaseIdentifier(new DiseaseIdentifier("OMIM:00002"));
        MouseModel secondModel = new MouseModel();
        secondModel.setMgiGeneId("MGI:0002");
        secondModel.setMgiModelId(22222);
        secondModel.setPhenotypeTerms(new ArrayList<>());
        second.setMouseModel(secondModel);
        
        third = new DiseaseModelAssociation();
        third.setDiseaseIdentifier(new DiseaseIdentifier("OMIM:00003"));
        MouseModel thirdModel = new MouseModel();
        thirdModel.setMgiGeneId("MGI:0003");
        thirdModel.setMgiModelId(33333);
        thirdModel.setPhenotypeTerms(new ArrayList<>());
      third.setMouseModel(thirdModel);
    }
    
    @Test
    public void testGetSetDiseaseIdentifier() {
        DiseaseIdentifier expected = new DiseaseIdentifier("OMIM:1234");
        DiseaseModelAssociation diseaseAssociation = new DiseaseModelAssociation();
        diseaseAssociation.setDiseaseIdentifier(expected);
        assertEquals(expected, diseaseAssociation.getDiseaseIdentifier());
    }
    
    @Test
    public void testCompareToDifferentValues() {
        
        first.setDiseaseToModelScore(90.12);
        second.setDiseaseToModelScore(89.99);
        
        List<DiseaseModelAssociation> associationsList = new ArrayList<>();
        associationsList.add(second);
        associationsList.add(first);
        
        Collections.sort(associationsList);
        
        DiseaseModelAssociation firstElement = associationsList.get(0);
        assertEquals(first, firstElement);     
        
    }
    
    @Test
    public void testDiseaseToGeneComparator() {

        first.setDiseaseToModelScore(90.12);
        second.setDiseaseToModelScore(89.99);
        third.setDiseaseToModelScore(56.99);

        List<DiseaseModelAssociation> associationsList = new ArrayList<>();
        associationsList.add(second);
        associationsList.add(first);
        associationsList.add(third);
        
        Collections.sort(associationsList, DiseaseModelAssociation.DiseaseToGeneScoreComparator);
        System.out.println("testDiseaseToGeneComparator");
        for (DiseaseModelAssociation diseaseAssociation : associationsList) {
            System.out.println(diseaseAssociation);
        }
        DiseaseModelAssociation firstElement = associationsList.get(0);
        assertEquals(first, firstElement);       
    }
    
    @Test
    public void testGeneToDiseaseComparator() {
        
        first.setModelToDiseaseScore(90.12);
        second.setModelToDiseaseScore(89.99);
        third.setModelToDiseaseScore(89.99);
        
        List<DiseaseModelAssociation> associationsList = new ArrayList<>();
        associationsList.add(second);
        associationsList.add(first);
        associationsList.add(third);

        Collections.sort(associationsList, DiseaseModelAssociation.GeneToDiseaseScoreComparator);
        System.out.println("testGeneToDiseaseComparator");
        for (DiseaseModelAssociation diseaseAssociation : associationsList) {
            System.out.println(diseaseAssociation);
        }
        DiseaseModelAssociation firstElement = associationsList.get(0);
        assertEquals(first, firstElement);
    }
    
    @Test
    public void testCompareToSameDiseaseToModelScores() {
        
        first.setDiseaseToModelScore(90.12);       
        second.setDiseaseToModelScore(90.12);
        
        List<DiseaseModelAssociation> associationsList = new ArrayList<>();
        associationsList.add(second);
        associationsList.add(first);
        
        Collections.sort(associationsList);
        
        DiseaseModelAssociation firstElement = associationsList.get(0);
        assertEquals(first, firstElement);
    }
        
    @Test
    public void testCompareToSameModelToDiseaseScores() {
        first.setModelToDiseaseScore(90.12);
        second.setModelToDiseaseScore(90.12);
                
        List<DiseaseModelAssociation> associationsList = new ArrayList<>();
        associationsList.add(second);
        associationsList.add(first);
        
        Collections.sort(associationsList);
        
        DiseaseModelAssociation firstElement = associationsList.get(0);
        assertEquals(first, firstElement);
    }
    
    @Test
    public void testToString() {
        System.out.println("testToString");
        String expected = "DiseaseAssociation{OMIM:00001 MGI:0001_11111 hasLiteratureEvidence: false Scores: [m2d=0.0, d2m=0.0] PhenotypeMatches: null MouseModelPhenotypes: []}";
        assertEquals(expected, first.toString());
    }
}