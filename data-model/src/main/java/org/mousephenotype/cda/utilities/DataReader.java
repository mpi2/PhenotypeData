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

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 *
 * @author mrelac
 * 
 * This abstract class implements code common to derived classes, leaving 
 * stream-specific  customizations to the derived classes.
 */
public abstract class DataReader implements AutoCloseable {

    protected DataReaderFactory dataReaderFactory = new DataReaderFactory();
    protected URL url;

    public DataReader(URL url) throws IOException {
        this.url = url;
    }
    
    protected abstract void open() throws IOException;
    public abstract List<String> getLine() throws IOException;
    public abstract DataType getType();
    
    /**
     * @return  All rows of data from the stream created by 
     * invoking the url provided with the constructor. Supported stream formats
     * are defined in the public enum <code>DataReader.DataType</code>.
     * 
     * @throws Exception
     */
    public String[][] getData() throws Exception {
        return getData(null);
    }
    
    /**
     * @param maxRows the maximum number of download stream rows to return, including
     * any headings. To specify all rows, set <code>maxRows</code> to null.
     * @return <code>maxRows</code> rows of data (including headings) from the stream created by 
     * invoking the url provided with the constructor. Supported stream formats
     * are defined in the public enum <code>DataReader.DataType</code>.
     * 
     * @throws Exception
     */
    public String[][] getData(Integer maxRows) throws Exception {
        String message;

        if (maxRows == null)
            maxRows = lineCount();

        String[][] data = new String[maxRows][];

        if (maxRows == 0) {
            return data;
        }

        try {
            List<String> line;
            for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
                line = getLine();
                if (line == null)
                    break;

                data[rowIndex] = new String[line.size()];
                for (int colIndex = 0; colIndex < line.size(); colIndex++) {
                    data[rowIndex][colIndex] = line.get(colIndex);
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

        reset();
        return data;
    }

    /**
     * Get the url defining the input stream.
     */
     public URL getUrl() {
        return url;
    }

    /**
     * Set the url defining the input stream.
     *
     * @param url The url defining the input stream
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * @return the number of lines in this <code>DataReader</code> stream,
     * including headings.
     * @throws Exception
     */
    public int lineCount() throws Exception {
        String message;
        int lineCount = 0;

        try {
            while ((getLine()) != null) {
                lineCount++;
            }

            reset();
        } catch (Exception e) {
            throw new Exception(e);
        }

        return lineCount;
    }
    
    public enum DataType {
        TSV,
        XLS
    }


    // PRIVATE METHODS


    private void reset() {

        try {
            close();
        } catch (Exception e) {
            System.out.println("ERROR IN " + this.getClass().getSimpleName() + ".close(): " + e.getLocalizedMessage() + "\nURL: " + url);
        }

        try {
            open();
        } catch (Exception e) {
            System.out.println("ERROR IN " + this.getClass().getSimpleName() + ".open(): " + e.getLocalizedMessage() + "\nURL: " + url);
        }
    }
}