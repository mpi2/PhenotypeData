/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.utilities;

import com.opencsv.CSVReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MpCsvWriterTest {

    static String             TEST_FILENAME         = "CsvUtilsTestFile.csv";
    static String[]           EXPECTED_LINE_1_ARRAY = {"Line 1, col 1", "Line 1, col 2", "Line 1, col 3"};
    static String[]           EXPECTED_LINE_2_ARRAY = {"Line 2, col 1", "Line 2, col 2", "Line 2, col 3"};
    static List<String>       EXPECTED_LINE_1       = Arrays.asList(EXPECTED_LINE_1_ARRAY);
    static List<String>       EXPECTED_LINE_2       = Arrays.asList(EXPECTED_LINE_2_ARRAY);
    static List<List<String>> EXPECTED_ALL          = new ArrayList<>();

    static {
        EXPECTED_ALL.add(EXPECTED_LINE_1);
        EXPECTED_ALL.add(EXPECTED_LINE_2);
    }

    @Before
    public void initialise() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
    }

    @After
    public void cleanup() throws IOException {
            System.out.println("  Trying to delete file");
            System.out.println("  File exists? " + Files.exists(Paths.get(TEST_FILENAME)));
            System.out.println("  File name: " + Paths.get(TEST_FILENAME).toAbsolutePath());
            Files.deleteIfExists(Paths.get(TEST_FILENAME));
            System.out.println("  Is file deleted? " + !Files.exists(Paths.get(TEST_FILENAME)));
            System.out.println("  File exists? " + Files.exists(Paths.get(TEST_FILENAME)));
            assertTrue(!Files.exists(Paths.get(TEST_FILENAME)));

    }


    @Test
    public void createEmptyCsvFile() throws IOException {
        System.out.println("Starting createEmptyCsvFile()");

        assertTrue( ! Files.exists(Paths.get(TEST_FILENAME)));
        try (MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, false)) {
            writer.flush();
            System.out.println("   FQ filename:" + writer.getFqFilename());
        }

        assertTrue(Files.exists(Paths.get(TEST_FILENAME)));
        System.out.println("Ending createEmptyCsvFile()");

    }

    @Test
    public void writeAndFlushOneRow() throws IOException {
        System.out.println("Starting writeAndFlushOneRow()");

        assertTrue(!Files.exists(Paths.get(TEST_FILENAME)));

        try (MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, false)) {
            List<String> actual;

            writer.write(EXPECTED_LINE_1);
            writer.flush();
            actual = read();
            assertArrayEquals(EXPECTED_LINE_1.toArray(), actual.toArray());

        }

       System.out.println("Ending writeAndFlushOneRow()");

    }

    @Test
    public void writeAndFlushTwoRowsWithFlushAfterFirst() throws IOException {
        System.out.println("Starting writeAndFlushTwoRowsWithFlushAfterFirst()");
        assertTrue( ! Files.exists(Paths.get(TEST_FILENAME)));

        try( MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, true)) {
            List<String> actual;

            writer.write(EXPECTED_LINE_1);
            writer.flush();
            actual = read();
            assertArrayEquals(EXPECTED_LINE_1.toArray(), actual.toArray());

            writer.write(EXPECTED_LINE_2);
            writer.flush();
            List<List<String>> actualAll = readAll();
            assertEquals(EXPECTED_ALL.size(), actualAll.size());
            for (int i = 0; i < actualAll.size(); i++) {
                assertArrayEquals(EXPECTED_ALL.get(i).toArray(), actualAll.get(i).toArray());
            }
        }
    }

    @Test
    public void writeAndFlushTwoRowsWithFlushAtEnd() throws IOException {
        System.out.println("Starting writeAndFlushTwoRowsWithFlushAtEnd()");
        assertTrue( ! Files.exists(Paths.get(TEST_FILENAME)));

        List<List<String>> actualAll;

        try(MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, true)) {
            List<String> actual;

            writer.write(EXPECTED_LINE_1);

            // File doesn't get flushed until after it's closed.
            actualAll = readAll();
            assertEquals(0, actualAll.size());

            writer.write(EXPECTED_LINE_2);

            actualAll = readAll();
            assertEquals(0, actualAll.size());

            writer.flush();

            actualAll = readAll();

            assertEquals(EXPECTED_ALL.size(), actualAll.size());
            for (int i = 0; i < actualAll.size(); i++) {
                assertArrayEquals(EXPECTED_ALL.get(i).toArray(), actualAll.get(i).toArray());
            }
        }
        System.out.println("End writeAndFlushTwoRowsWithFlushAtEnd()");

    }

    @Test
    public void close() throws IOException {
        System.out.println("Starting close()");

        assertTrue( ! Files.exists(Paths.get(TEST_FILENAME)));
        MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, false);
        writer.close();

        assertNull(writer.getFqFilename());
        try {
            writer.write(EXPECTED_LINE_1);
            fail("Expected Null Pointer Exception because file is closed.");
        } catch (NullPointerException e) {;
        }
    }

    @Test
    public void getFqFilename() throws IOException {
        System.out.println("Starting getFqFilename()");

        assertTrue( ! Files.exists(Paths.get(TEST_FILENAME)));
        try (MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, false)) {
            String actualFqFilename = writer.getFqFilename();
            assertTrue(actualFqFilename.endsWith(TEST_FILENAME) && actualFqFilename.contains(File.separator));
        }
    }


    // PRIVATE METHODS

    private List<String> read() throws IOException {
        try (FileReader fr = new FileReader(TEST_FILENAME); CSVReader r = new CSVReader(fr)) {
            return Arrays.asList(r.readNext());
        }
    }

    private List<List<String>> readAll() throws IOException {
        try (FileReader fr = new FileReader(TEST_FILENAME); CSVReader r = new CSVReader(fr)) {
            List<String[]> results = r.readAll();
            return results.stream().map(Arrays::asList).collect(Collectors.toList());
        }
    }
}
