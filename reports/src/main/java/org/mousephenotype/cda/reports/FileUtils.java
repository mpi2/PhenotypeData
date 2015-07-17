/*******************************************************************************
 *  Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.reports;

import au.com.bytecode.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

/**
 * This class contains generic file utilities required by the reports module.
 *
 * Created by mrelac on 16/07/2015.
 */
@Component
public class FileUtils {

    /**
     * Create and write CSV file <code>filename</code> to directory <code>targetFileDir</code>
     * from source <code>content</code>.
     * @param targetFileDir target directory
     * @param filename target filename
     * @param content content to be written
     * @return a <code>File</code> instance representing the csv file just created
     * @throws IOException
     */
    public File createCSV(String targetFileDir, String filename, List<String[]> content) throws IOException {
        File file = new File(Paths.get(targetFileDir, filename).toAbsolutePath().toString());

        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        csvWriter.writeAll(content);
        csvWriter.close();

        return file;
    }

    /**
     * Create and write CSV file <code>filename</code> to directory <code>targetFileDir</code>
     * from source <code>content</code>.
     * @param targetFileDir target directory
     * @param filename target filename
     * @param content content to be written
     * @return a <code>File</code> instance representing the csv file just created
     * @throws IOException
     */
    public File createCSVMulti(String targetFileDir, String filename, List<List<String[]>> content) throws IOException {
        File file = new File(Paths.get(targetFileDir, filename).toAbsolutePath().toString());

        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        for (List<String[]> row : content) {
            csvWriter.writeAll(row);
            csvWriter.writeNext(new String[0]);
        }

        csvWriter.close();

        return file;
    }
}