package org.mousephenotype.cda.utilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MpCsvReaderTest {

    static String             TEST_FILENAME         = "CsvUtilsTestFile.csv";
    static String[]           EXPECTED_LINE_1_ARRAY = {"Line 1, col 1", "Line 1, col 2", "Line 1, col 3"};
    static String[]           EXPECTED_LINE_2_ARRAY = {"Line 2, col 1", "Line 2, col 2", "Line 2, col 3"};
    static List<String>       EXPECTED_LINE_1       = Arrays.asList(EXPECTED_LINE_1_ARRAY);
    static List<String>       EXPECTED_LINE_2       = Arrays.asList(EXPECTED_LINE_2_ARRAY);
    static List<List<String>> EXPECTED_ALL          = new ArrayList<>();
    static List<String[]>     EXPECTED_ALL_ARRAY    = new ArrayList<>();

    static {
        EXPECTED_ALL.add(EXPECTED_LINE_1);
        EXPECTED_ALL.add(EXPECTED_LINE_2);
        EXPECTED_ALL_ARRAY.add(EXPECTED_LINE_1_ARRAY);
        EXPECTED_ALL_ARRAY.add(EXPECTED_LINE_2_ARRAY);
    }

    @Before
    public void initialise() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
    }

    @After
    public void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILENAME));
    }

    @Test
    public void readAsArray() throws IOException {
        MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, false);

        writer.write(EXPECTED_LINE_1);
        writer.write(EXPECTED_LINE_2);

        MpCsvReader reader = new MpCsvReader(TEST_FILENAME);
        int i = 0;
        while (true) {
            String[] row = reader.readAsArray();
            if (row == null)
                break;
            assertArrayEquals(EXPECTED_ALL_ARRAY.get(i), row);
        }
    }

    @Test
    public void read() throws IOException {
        MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, false);

        writer.write(EXPECTED_LINE_1);
        writer.write(EXPECTED_LINE_2);

        MpCsvReader reader = new MpCsvReader(TEST_FILENAME);
        int i = 0;
        while (true) {
            List<String> row = reader.read();
            if (row == null)
                break;
            String[] expected = EXPECTED_ALL_ARRAY.get(i);
            String[] actual = row.toArray(new String[0]);
            assertArrayEquals(expected, actual);
        }
    }

    @Test
    public void close() throws IOException {

        assertTrue( ! Files.exists(Paths.get(TEST_FILENAME)));
        MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, false);
        writer.close();

        MpCsvReader reader = new MpCsvReader(TEST_FILENAME);
        String actualFqFilename = reader.getFqFilename();
        reader.close();

        assertNull(reader.getFqFilename());
        try {
            assertNull(reader.read());
            fail("Expected Null Pointer Exception because file is closed.");
        } catch (NullPointerException e) {;
        }
    }

    @Test
    public void getFqFilename() throws IOException {

        assertTrue( ! Files.exists(Paths.get(TEST_FILENAME)));
        MpCsvWriter writer = new MpCsvWriter(TEST_FILENAME, false);
        writer.close();

        MpCsvReader reader = new MpCsvReader(TEST_FILENAME);
        String actualFqFilename = reader.getFqFilename();
        assertTrue(actualFqFilename.startsWith(File.separator));
    }
}