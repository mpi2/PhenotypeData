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

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MpCsvWriter implements AutoCloseable {

    private static Logger logger = LoggerFactory.getLogger(MpCsvWriter.class);

    private static final char   defaultSeparator  = ',';      // Default from CSVWriter class
    private static final char   defaultQuotechar  = '"';      // Default from CSVWriter class
    private static final char   defaultEscapechar = '"';      // Default from CSVWriter class
    private static final String defaultLineEnd    = "\n";     // Default from CSVWriter class

    private String     fqFilename;
    private CSVWriter  writer;

    public MpCsvWriter(String csvFilename, boolean append) throws IOException {
        this(csvFilename, append, defaultSeparator);
    }

    public MpCsvWriter(String csvFilename, boolean append, char separator) throws IOException {
        fqFilename = expandFilename(csvFilename);
        FileWriter fileWriter = new FileWriter(fqFilename, append);
//        writer = new CSVWriter(fileWriter);

        writer = new CSVWriter(fileWriter, separator, defaultQuotechar, defaultEscapechar, defaultLineEnd);
    }

    public MpCsvWriter(CSVWriter writer) {
        this.writer = writer;
    }

    public String getFqFilename() {
        return fqFilename;
    }

    public void write(String... row) {
        writer.writeNext(row);
    }

    public void write(List<String> row) {
        write(row.toArray(new String[0]));
    }

    public void writeRowsOfArray(List<String[]> data) {
        data
            .stream()
            .forEach(row -> write(row));
    }
    public void writeRows(List<List<String>> data) {
        data
            .stream()
            .forEach(row -> write(row));
    }

    public void writeRowsMulti(List<List<String[]>> data) {
        data
            .stream()
            .forEach(chunk -> {
                chunk
                    .stream()
                    .forEach(row -> write(row));
                });
    }

    public void writeRowsMulti2(List<List<List<String>>> data) {
        data
            .stream()
            .forEach(chunk -> {
                chunk
                    .stream()
                    .forEach(row -> write(row));
            });
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void flushQuietly() {
        writer.flushQuietly();
    }

    public void close() throws IOException {
        writer.close();
        fqFilename = null;
        writer = null;
    }

    public void closeQuietly() {
        try {
            writer.close();
        } catch(IOException e) {
            logger.error("Close failed: {}", e.getLocalizedMessage());
        }
        fqFilename = null;
        writer = null;
    }

    private String expandFilename(String csvFilename) {
        Path path = Paths.get(csvFilename);
        File file = path.toFile();
        return file.getAbsolutePath();
    }
}
