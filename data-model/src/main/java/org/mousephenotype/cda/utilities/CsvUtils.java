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

public class CsvUtils {

    private static Logger logger = LoggerFactory.getLogger(CsvUtils.class);

    private static String     fqFilename;
    private        FileWriter fileWriter;
    private        CSVWriter  writer;

    // TODO - Migrate MpCsvWriter here and add CsvReader methods.

    public CsvUtils() { }

    public CsvUtils(String csvFilename) throws IOException {
        open(csvFilename, true);
    }
    public void open(String csvFilename, boolean append) throws IOException {

        if (fqFilename != null) {
            throw new IOException("File is already open");
        }
        try {
            fileWriter = getFileWriter(csvFilename, append);
            writer = new CSVWriter(fileWriter);

        } catch (IOException e) {
            logger.error("can't open file {}. Reason: {}", fqFilename, e.getLocalizedMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void write(String... row) {
        writer.writeNext(row);
    }

    public void write(List<String> row) {
        writer.writeNext(row.toArray(new String[0]));
    }

    public void close() throws IOException {
        try {
            if (fileWriter != null) {
                fileWriter.close();
                fileWriter = null;
            }

        } catch (IOException e) {
            logger.error("can't close file {}. Reason: {}", fqFilename, e.getLocalizedMessage());
            e.printStackTrace();
            throw e;
        }
        fqFilename = null;
        fileWriter = null;
        writer = null;
    }

    // PRIVATE METHODS

    private FileWriter getFileWriter(String csvFilename,  boolean append)
            throws IOException
    {
        Path path = Paths.get(csvFilename);
        File file = path.toFile();
        fqFilename = file.getAbsolutePath();
        logger.info("{} file {} for writing", append ? "Opening" : "Creating", fqFilename);

        return new FileWriter(csvFilename, append);
    }

    // STATIC METHODS THAT FLUSH ON EVERY WRITE.

    public static void deleteAndRecreateFile(String csvFilename) throws IOException {

        CsvUtils u = new CsvUtils();
        try {
            FileWriter fileWriter = u.getFileWriter(csvFilename, false);
            u.close();

        } catch (IOException e) {
            logger.error("can't open file/close {}. Reason: {}", fqFilename, e.getLocalizedMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void writeAndFlushRow(String csvFilename, List<String> row)
            throws IOException
    {
        CsvUtils u = new CsvUtils();
        u.open(csvFilename, true);
        u.write(row);
        u.close();
    }
}
