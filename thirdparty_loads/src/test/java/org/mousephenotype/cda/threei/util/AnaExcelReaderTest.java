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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

/**
 * Unit test for AnaExcelReader.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class AnaExcelReaderTest {


    private final int N_ROWS_TO_PROCESS = 20;
    private final int[] N_ROWS_PER_MOUSE = {4, 4, 1, 3, 3, 1, 2, 2};
    private final int N_MICE_TO_PROCESS = N_ROWS_PER_MOUSE.length;
    private AnaExcelReader reader;

    @Before
    public void AnaExcelReaderTestSeup() throws IOException {
        Resource resource = new ClassPathResource("threei/tempAna.xlsx");
        reader = new AnaExcelReader(resource.getFile().getAbsolutePath());
    }


    /**
     * Rigourous Test :-)
     */
    @Test
    public void testAnaExcelReader() {
        assertTrue(true);
    }

    // Expecting 28 columns
    @Test
    public void testNumberOfColumns() {
        //assertThat( reader.getNumberOfColumns()).isEqualTo(29);
        assertTrue(reader.getNumberOfColumns() == 29);
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

    @Test
    public void testNumberOfRowsFromReader() {
        reader.reset();
        while (reader.hasNext()) {
            reader.getRow();
        }

        int nRows = reader.getNumberOfRowsRead();
        System.out.println("Number of rows read (according to reader) = " + nRows);
        assertTrue(N_ROWS_TO_PROCESS == nRows);
    }

    @Test
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

        for (int j = 0; j < N_MICE_TO_PROCESS; j++) {
            System.out.println("Number of rows processed = " + nRowsPerMouse[j] + ". Expected number of rows = " + N_ROWS_PER_MOUSE[j]);
            assertTrue(N_ROWS_PER_MOUSE[j] == nRowsPerMouse[j]);
        }
    }

}
