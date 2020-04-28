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
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CsvUtilsTest {

    static String TEST_FILENAME = "CsvUtilsTestFile.csv";
    static String[] EXPECTED_LINE_1_ARRAY = {"Line 1, col 1", "Line 1, col 2", "Line 1, col 3"};
    static String[] EXPECTED_LINE_2_ARRAY = {"Line 2, col 1", "Line 2, col 2", "Line 2, col 3"};
    static List<String> EXPECTED_LINE_1 = Arrays.asList(EXPECTED_LINE_1_ARRAY);
    static List<String> EXPECTED_LINE_2 = Arrays.asList(EXPECTED_LINE_2_ARRAY);
    static List<List<String>> EXPECTED_ALL = new ArrayList<>();
    static {
        EXPECTED_ALL.add(EXPECTED_LINE_1);
        EXPECTED_ALL.add(EXPECTED_LINE_2);
    }

    @Before
    public void init() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
    }

    @Test
    public void writeAndFlushOneRow() throws IOException {
        try {
            CsvUtils     utils = new CsvUtils();
            List<String> actual;

            utils.deleteAndRecreateFile(TEST_FILENAME);
            utils.writeAndFlushRow(TEST_FILENAME, EXPECTED_LINE_1);
            actual = read();
            assertArrayEquals(EXPECTED_LINE_1.toArray(), actual.toArray());
        } finally {
            Files.deleteIfExists(Paths.get(TEST_FILENAME));
        }
    }

    @Test
    public void writeAndFlushTwoRowsWithFlushAfterFirst() throws IOException {
        try {
            CsvUtils     utils = new CsvUtils();
            List<String> actual;

            utils.deleteAndRecreateFile(TEST_FILENAME);
            utils.writeAndFlushRow(TEST_FILENAME, EXPECTED_LINE_1);
            actual = read();
            assertArrayEquals(EXPECTED_LINE_1.toArray(), actual.toArray());

            utils.writeAndFlushRow(TEST_FILENAME, EXPECTED_LINE_2);

            List<List<String>> actualAll = readAll();
            assertEquals(EXPECTED_ALL.size(), actualAll.size());
            for (int i = 0; i < actualAll.size(); i++) {
                assertArrayEquals(EXPECTED_ALL.get(i).toArray(), actualAll.get(i).toArray());
            }
        } finally {
            Files.deleteIfExists(Paths.get(TEST_FILENAME));
        }
    }

    @Test
    public void writeAndFlushTwoRowsWithFlushAtEnd() throws IOException {
        try {
            CsvUtils     utils = new CsvUtils();
            List<String> actual;
            List<List<String>> actualAll;

            utils.deleteAndRecreateFile(TEST_FILENAME);

            utils.open(TEST_FILENAME, true);
            utils.write(EXPECTED_LINE_1);

            // File doesn't get flushed until after it's closed.
            actualAll = readAll();
            assertEquals(0, actualAll.size());

            utils.write(EXPECTED_LINE_2);

            actualAll = readAll();
            assertEquals(0, actualAll.size());

            utils.close();

            actualAll = readAll();
            assertEquals(EXPECTED_ALL.size(), actualAll.size());
            for (int i = 0; i < actualAll.size(); i++) {
                assertArrayEquals(EXPECTED_ALL.get(i).toArray(), actualAll.get(i).toArray());
            }
        } finally {
            Files.deleteIfExists(Paths.get(TEST_FILENAME));
        }
    }


    // PRIVATE METHODS

    private List<String> read() throws IOException {
        FileReader fr = new FileReader(TEST_FILENAME);
        CSVReader  r  = new CSVReader(fr);
        return Arrays.asList(r.readNext());
    }
    private List<List<String>> readAll() throws IOException {
        FileReader fr = new FileReader(TEST_FILENAME);
        CSVReader r = new CSVReader(fr);
        List<String[]> results = r.readAll();
        return results.stream().map(row ->
            Arrays.asList(row)).collect(Collectors.toList());
    }
}