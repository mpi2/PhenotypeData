/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this targetFile except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.reports.support;

import com.opencsv.CSVWriter;

import java.io.Writer;
import java.util.List;

/**
 * Created by mrelac on 24/07/2015.
 */
// TODO - These should be migrated to data-model and integrated with MpCsvWriter. Or better yet, deleted when ETL is permanent.
@Deprecated
public class MpCSVWriter extends CSVWriter {

    public MpCSVWriter(Writer writer) {
        super(writer);
    }

    @Deprecated  // Use data-model MpCsvWriter(String csvFilename, boolean append, char separator)
    public MpCSVWriter(Writer writer, char separator) {
        super(writer, separator);
    }

    public MpCSVWriter(Writer writer, char separator, char quotechar) {
        super(writer, separator, quotechar);
    }

    public MpCSVWriter(Writer writer, char separator, char quotechar, char escapechar) {
        super(writer, separator, quotechar, escapechar);
    }

    public MpCSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
        super(writer, separator, quotechar, escapechar, lineEnd);
    }

    public MpCSVWriter(Writer writer, char separator, char quotechar, String lineEnd) {
        super(writer, separator, quotechar, lineEnd);
    }

    @Deprecated  // Use data-model MpCsvWriter write()
    public void writeRow(List<String> nextLine) {
        writeNext(nextLine.toArray(new String[0]));
    }

    @Deprecated  // Use data-model MpCsvWriter writeRowsMulti()
    public void writeAllMulti(List<List<String[]>> allLines) {
        for (List<String[]> row : allLines) {
            writeAll(row);
            writeNext(new String[0]);
        }
    }

    @Deprecated  // Use data-model MpCsvWriter writeRows()
    public void writeRows(List<List<String>> data) {
        for (List<String> row : data) {
            writeRow(row);
        }
    }
}