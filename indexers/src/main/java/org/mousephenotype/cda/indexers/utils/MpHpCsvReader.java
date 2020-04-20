/*******************************************************************************
 * Copyright © 2020 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.indexers.utils;

import au.com.bytecode.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class reads a csv file provided by Monarch/Nico Matentzoglu that contains mp to hp
 * mappings. This functionality replaces the older OntologyParser mp-hp.owl method, which
 * required constant maintenance.
 */
public class MpHpCsvReader {

    public static final int COLUMN_COUNT    = 7;            // mp-hp.csv Column count
    public static final int CURIE_HP_COLUMN = 5;            // HP column offset (0-relative)
    public static final int CURIE_MP_COLUMN = 6;            // MP column offset (0-relative)

    public static final String MP_HP_CSV_FILENAME = "mp-hp.csv";

    public static List<List<String>> readAll(String fqFilename) throws IOException {
        List<List<String>> data = new ArrayList<>();

        Path           path           = Paths.get(fqFilename);
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        CSVReader      csvReader      = new CSVReader(bufferedReader, ',', '"');

        int lineNumber = 1;

        String[] line;
        while ((line = csvReader.readNext()) != null) {
            if (line.length != COLUMN_COUNT) {
                throw new IOException("Line " + lineNumber + ": Expected " + COLUMN_COUNT + " columns but found " + line.length);
            }
            data.add(Arrays.asList(line));

            lineNumber++;
        }

        return data;
    }
}
