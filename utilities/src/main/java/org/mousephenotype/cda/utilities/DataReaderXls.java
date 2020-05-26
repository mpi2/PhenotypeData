/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.cda.utilities;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mrelac
 * 
 * This <code>DataReader</code> implementation handles Microsoft Excel streams.
 */
public class DataReaderXls extends DataReader {
    
    private Iterator<Row> rowIterator = null;
    Workbook workbook;

    /**
     * Instantiates a new <code>DataReader</code> that knows how to serve up
     * Microsoft Excel XLS streams
     *
     * @param url The url defining the input stream
     */
    public DataReaderXls(URL url) throws IOException {
        super(url);
        open();
    }

    /**
     * Opens the stream defined by the url used in the constructor.
     */
    @Override
    protected void open() {
        try (InputStream inputStream = url.openStream()) {
            workbook = WorkbookFactory.create(inputStream);
        } catch (Exception e) {
            System.out.println("Error opening workbook. Will treat as empty workbook. Exception: " + e.getLocalizedMessage());
            return;
        }
        Sheet sheet = workbook.getSheetAt(0);
        rowIterator = sheet.rowIterator();
    }
    
    /**
     * Closes the stream defined by the url used in the constructor.
     */
    @Override
    public void close()  {
        // nothing to do.
    }
    
    /**
     * Returns the next line as a <code>List</code> of <code>String</code> if 
     * there is still data; null otherwise
     * @return  the next line as a <code>List</code> of <code>String</code> if 
     * there is still data; null otherwise
     * 
     */
    @Override
    public List<String> getLine()  {
        if (rowIterator == null) {
            return null;
        }

        List<String> line = new ArrayList<>();
        if ( ! rowIterator.hasNext())
            return null;
        
        Row row = rowIterator.next();
        
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String cellValue = cell.getStringCellValue();
            line.add(cellValue);
        }
        return line;
    }
    
    /**
     * Returns the data type
     * @return the data type
     */
    @Override
    public DataType getType() {
        return DataType.XLS;
    }
}