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

package org.mousephenotype.cda.exporter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ExporterTsv {

    /**
     * Exports a collection of data to a {@code tsb (tab-separated)} file.
     *
     * @param response {@link HttpServletResponse} instance required by the downstream implementation
     * @param filename The name of the download file that will be created
     * @param headings The headings. If null, no heading is written
     * @param data The data to be written
     * @throws IOException if {@code exportType} is not valid
     */
    public static void export(HttpServletResponse response, String filename, List<String> headings, List<List<String>> data)  throws IOException {

        PrintWriter output = response.getWriter();

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Content-disposition", "attachment; filename=" + filename + ".tsv");

        StringBuilder outputLine = new StringBuilder();

        if (headings != null) {
            outputLine
                    .append(String.join("\t", headings))
                    .append("\n");
        }
        if (data != null) {
            for (List<String> row : data) {
                outputLine
                        .append(String.join("\t", row))
                        .append("\n");
//                for (String cell : row) {
//                    if (!outputLine.toString().isEmpty()) {
//                        outputLine.append("\t");
//                    }
//                    outputLine.append(cell);
//                }
//                outputLine.append("\n");
            }
            output.println(outputLine);
        }

        output.flush();
        output.close();
    }
}