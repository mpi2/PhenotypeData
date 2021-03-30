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

package uk.ac.ebi.phenotype.web.exporter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class Exporter {

    /**
     * Exports a collection of data to a file.
     *
     * @param response {@link HttpServletResponse} instance required by the downstream implementation
     * @param exportType Type of export. See the ExportType class for valid types
     * @param filename The name of the download file that will be created
     * @param headings The headings. If null, no heading is written
     * @param data The data to be written
     * @throws IOException if {@code exportType} is not valid
     */
    public static void export(HttpServletResponse response, String exportType, String filename, List<String> headings, List<List<String>> data) throws IOException {

        if (exportType.toLowerCase().startsWith("xls")) {
            ExporterExcel.export(response, filename, headings, data);
        } else if (exportType.toLowerCase().startsWith("tsv")) {
            ExporterTsv.export(response, filename, headings, data);
        } else {
            throw new IOException("Unknown export filetype " + exportType);
        }
    }
}