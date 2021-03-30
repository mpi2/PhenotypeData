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

import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExporterExcel {

    /**
     * Exports a collection of data to an {@code excel} file.
     *
     * @param response {@link HttpServletResponse} instance required by the downstream implementation
     * @param filename The name of the download file that will be created
     * @param headings The headings. If null, no heading is written
     * @param data The data to be written
     * @throws IOException if {@code exportType} is not valid
     */
    public static void export(HttpServletResponse response, String filename, List<String> headings, List<List<String>> data) throws IOException {

        boolean isWorkbookEmpty = true;

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");

        int      nextRow  = 0;
        Workbook workbook = WorkbookFactory.create(true);
        Sheet    sheet    = workbook.createSheet("Sheet 1");
        Row      row;

        if (headings != null) {
            row = sheet.createRow(nextRow++);
            writeRow(row, headings);
            isWorkbookEmpty = false;
        }
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                row = sheet.createRow(nextRow++);
                writeRow(row, data.get(i));
            }
            isWorkbookEmpty = false;
        }

        if ( ! isWorkbookEmpty) {
            workbook.write(response.getOutputStream());
        }
    }

    private static void writeRow(Row row, List<String> data) {
        for (int i = 0; i < data.size(); i++) {
            Cell cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(data.get(i));
        }
    }
}