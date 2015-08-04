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

package org.mousephenotype.cda.seleniumtests.support;

import org.mousephenotype.cda.seleniumtests.exception.TestException;
import org.springframework.beans.factory.annotation.Autowired;

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
public abstract class DataReader {
    
    protected URL url;

    @Autowired
    DataReaderFactory dataReaderFactory;
    
    public DataReader() {

    }
    
    public abstract void open() throws IOException;
    public abstract void close() throws IOException;
    public abstract List<String> getLine() throws IOException;
    public abstract DataType getType();
    
    /**
     * @return  All rows of data from the stream created by 
     * invoking the url provided with the constructor. Supported stream formats
     * are defined in the public enum <code>DataReader.DataType</code>.
     * 
     * @throws TestException
     */
    public String[][] getData() throws TestException {
        return getData(null);
    }
    
    /**
     * @param maxRows the maximum number of download stream rows to return, including
     * any headings. To specify all rows, set <code>maxRows</code> to null.
     * @return <code>maxRows</code> rows of data (including headings) from the stream created by 
     * invoking the url provided with the constructor. Supported stream formats
     * are defined in the public enum <code>DataReader.DataType</code>.
     * 
     * @throws TestException
     */
    public String[][] getData(Integer maxRows) throws TestException {
        String message;

        if (maxRows == null)
            maxRows = lineCount();
        
        String[][] data = new String[maxRows][];
        DataReader dataReader = null;
        try {
            dataReader = dataReaderFactory.create(url);
 //System.out.println("After create()");
            dataReader.open();
 //System.out.println("After open()");
            List<String> line;
            for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
                line = dataReader.getLine();
                if (line == null)
                    break;
                
                data[rowIndex] = new String[line.size()];
                for (int colIndex = 0; colIndex < line.size(); colIndex++) {
                    data[rowIndex][colIndex] = line.get(colIndex);
                }
            }
        } catch (IOException e) {
            message = "EXCEPTION: " + e.getLocalizedMessage() + "\nURL: " + url;
            throw new TestException(message);
        } finally {
            try {
                if (dataReader != null)
                    dataReader.close();
            } catch (IOException e) {
                message = "EXCEPTION. dataReader.close() failed. Reason: " + e.getLocalizedMessage() + "\nURL: " + url;
                throw new TestException(message);
            }
        }
        
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
     * @throws TestException
     */
    public int lineCount() throws TestException {
        String message;
        int lineCount = 0;
        DataReader dataReader = null;
        try {
            dataReader = dataReaderFactory.create(url);
            dataReader.open();
            
            while ((dataReader.getLine()) != null) {
                lineCount++;
            }
        } catch (IOException e) {
            message = "EXCEPTION in lineCount(): " + e.getLocalizedMessage() + "\nURL: " + url;
            throw new TestException(message);
        } finally {
            try {
                if (dataReader != null)
                    dataReader.close();
            } catch (IOException e) {
                message = "EXCEPTION in lineCount(). dataReader.close() failed. Reason: " + e.getLocalizedMessage() + "\nURL: " + url;
                throw new TestException(message);
            }
        }
        
        return lineCount;
    }
    
    public enum DataType {
        TSV,
        XLS
    }
}