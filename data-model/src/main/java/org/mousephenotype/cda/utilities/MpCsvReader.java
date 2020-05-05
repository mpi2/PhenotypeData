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

package org.mousephenotype.cda.utilities;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MpCsvReader {

    private static Logger logger = LoggerFactory.getLogger(MpCsvReader.class);

    private static final char   defaultSeparator  = ',';      // Default from CSVWriter class
    private static final char   defaultQuotechar  = '"';      // Default from CSVWriter class
    private static final char   defaultEscapechar = '"';      // Default from CSVWriter class
    private static final String defaultLineEnd    = "\n";     // Default from CSVWriter class

    private String    fqFilename;
    private CSVReader reader;

    public MpCsvReader(String csvFilename) throws IOException {
        this(csvFilename, defaultSeparator);
    }

    public MpCsvReader(String csvFilename, char separator) throws IOException {
        fqFilename = expandFilename(csvFilename);
        FileReader fileReader = new FileReader(fqFilename);
        reader = new CSVReader(fileReader);
    }

    public MpCsvReader(CSVReader reader) {
        this.reader = reader;
    }

    public String getFqFilename() {
        return fqFilename;
    }


    // Returns null when no more input
    public String[] readAsArray() throws IOException {
        return reader.readNext();
    }

    // Returns null when no more input
    public List<String> read() throws IOException {
        String[] row = readAsArray();
        return (row == null ? null : Arrays.asList(row));
    }

    public void close() throws IOException {
        reader.close();
        fqFilename = null;
        reader = null;
    }

    private String expandFilename(String csvFilename) {
        Path path = Paths.get(csvFilename);
        File file = path.toFile();
        return file.getAbsolutePath();
    }
}
