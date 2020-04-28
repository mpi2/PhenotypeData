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

import com.opencsv.CSVReader;

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

    public static final String MP_HP_CSV_FILENAME = "mp-hp.csv";

    public static final int HP_OBO_URL_COL_OFFSET        = 0;     // Column A - HP obo term URL
    public static final int MP_OBO_URL_COL_OFFSET        = 1;     // Column B - HP obo term URL
    public static final int HP_NAME_COL_OFFSET           = 2;     // Column C - hp term name with ' (HPO)' appended
    public static final int MP_NAME_COL_OFFSET           = 3;     // Column D - mp term name with ' (MPO)' appended
    public static final int MATCHING_CATEGORY_COL_OFFSET = 4;     // Column E - 'matching_category'
    public static final int HP_ID_COL_OFFSET             = 5;     // Column F - HP term
    public static final int MP_ID_COL_OFFSET             = 6;     // Column G - MP term
    public static final int COLUMN_COUNT                 = 7;     // mp-hp.csv Column count

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
