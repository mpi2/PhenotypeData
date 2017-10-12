/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.threei.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for AnaExcelReader.
 */
public class AnaExcelReaderTest 
    extends TestCase
{
    private final int N_ROWS_TO_PROCESS = 20;
    private final int[] N_ROWS_PER_MOUSE = {4,4,1,3,3,1,2,2};
    private final int N_MICE_TO_PROCESS = N_ROWS_PER_MOUSE.length;
    private AnaExcelReader reader;
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AnaExcelReaderTest( String testName )
    {
        super( testName );
        
        // The excel file is based on the format provided by Mark Griffiths
        // on 04/10/2017.
        // ToDo: Speak to Jeremy about including this from resources with
        // spring boot
        reader = new AnaExcelReader("/home/kola/Downloads/tempAna.xlsx");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AnaExcelReaderTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testAnaExcelReader()
    {
        assertTrue( true );
    }

    // Expecting 28 columns
    public void testNumberOfColumns() {
        //assertThat( reader.getNumberOfColumns()).isEqualTo(28);
        assertTrue( reader.getNumberOfColumns() == 28 );
    }

    // Test Number of rows read
    // In the test file there are twenty rows -
    //    2 x 4 rows per mouse
    //    2 x 3 rows per mouse
    //    2 x 2 rows per mouse
    //    2 x 1 row per mouse
    /*
    public void testNumberOfRowsDirect() {
        // Get all records
        // Check number
        int nRows = 0;
        String[] row = null;
        reader.reset();
        while(reader.hasNext()) {
            row = reader.getRow();   
            nRows++;
        }
        
        System.out.println("Number of rows read = " + nRows);
        assertTrue( N_ROWS_TO_PROCESS == nRows );
    }
    */

    public void testNumberOfRowsFromReader() {
        reader.reset(); 
        while(reader.hasNext()) {
            reader.getRow();
        }

        int nRows = reader.getNumberOfRowsRead();
        System.out.println("Number of rows read (according to reader) = " + nRows);
        assertTrue( N_ROWS_TO_PROCESS == nRows);
    }

    public void testNumberOfMiceProcessed() {
        int[] nRowsPerMouse = new int[N_MICE_TO_PROCESS];
        int i = 0;
        while (reader.hasNext()) {
            nRowsPerMouse[i] = reader.getRowsForMouse().size();
            i++;
        }
        int nMiceProcessed = reader.getNumberOfMiceProcessed();

        System.out.println("Number of mice processed = " + nMiceProcessed);
        assertTrue(N_MICE_TO_PROCESS == nMiceProcessed);

        for (int j=0; j<N_MICE_TO_PROCESS; j++) {
            System.out.println("Number of rows processed = " + nRowsPerMouse[j] +  ". Expected number of rows = " + N_ROWS_PER_MOUSE[j]);
            assertTrue(N_ROWS_PER_MOUSE[j] == nRowsPerMouse[j]);
        }
    }
}
