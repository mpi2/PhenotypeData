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

package org.mousephenotype.cda.support;

import org.mousephenotype.cda.utilities.UrlUtils;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * @author mrelac
 *
 * This class encapsulates the code and data necessary to represent access functionality
 * to a two-dimensional data grid composed of type <code>String</code>. Callers
 * pass in a <code>String[][]</code> containing the data store where the first
 * line is a heading. The class then serves up access to the data by row index
 * and either column index or column name, which must match exactly. No 
 * IndexOutOfBounds exception checking is done.
 */
public class GridMap {

    private final HashMap<String, Integer> colNameHash = new HashMap();
    private String[][] data;
    private final String target;
    private TestUtils testUtils = new TestUtils();
    private UrlUtils urlUtils = new UrlUtils();

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Creates a <code>GridMap</code> deep copy instance
     *
     * @param data the data store
     * @param target the target url of the page containing the data
     */
    public GridMap(String[][] data, String target) {
        if (data.length == 0) {
            this.data = new String[0][0];
        } else {
            this.data = new String[data.length][data[0].length];
            for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                for (int colIndex = 0; colIndex < data[0].length; colIndex++) {
                    this.data[rowIndex][colIndex] = data[rowIndex][colIndex];
                }
            }
        }

        this.target = target;

        int i = 0;
        for (String colHeading : data[0]) {
            colNameHash.put(colHeading, i);
        }
    }

    /**
     * Creates a <code>GridMap</code> deep copy instance
     *
     * @param dataList the data store
     * @param target the target URL of the page containing the data
     */
    public GridMap(List<List<String>> dataList, String target) {
        data = new String[0][0];
        try {
            data = new String[dataList.size()][dataList.get(0).size()];
            for (int rowIndex = 0; rowIndex <  dataList.size(); rowIndex++) {
                List<String> row = dataList.get(rowIndex);
                for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                    String cellValue = row.get(colIndex);
                    data[rowIndex][colIndex] = cellValue;
                }
            }

            for (String colHeading : data[0]) {
                colNameHash.put(colHeading, 0);
            }
        } catch (Exception e) {
            data = new String[0][0];
            logger.warn("Exception: " + e.getLocalizedMessage());
        }

        this.target = target;
    }


    // GETTERS AND SETTERS


    public String[][] getData() {
        return data;
    }

    public String[] getHeading() {
        if ((data != null) && (data.length > 0)) {
            return data[0];
        }

        return new String[0];
    }

    public String[][] getBody() {
        if ((data != null) && (data.length > 1)) {
            String[][] clone = new String[data.length - 1][data[0].length];
            for (int i = 0; i < data.length - 1; i++) {
                clone[i] = data[i + 1];
            }

            return clone;
        }

        return new String[0][0];
    }

    public String getCell(int rowIndex, int colIndex) {
        if (data != null)
            return data[rowIndex][colIndex];

        return "";
    }

    public String getCell(int rowIndex, String colName) {
        if ( ! colNameHash.containsKey(colName))
            throw new RuntimeException("ERROR: Column " + colName + " was not found.");

        return data[rowIndex][colNameHash.get(colName)];
    }

    public String getTarget() {
        return target;
    }

}
