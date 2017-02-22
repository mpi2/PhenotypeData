/*******************************************************************************
 *  Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.reports.support;

import java.util.List;

/**
 * Created by mrelac on 20/02/2017.
 *
 * This class encapsulates the code and data necessary to represent differences between cda-schema'd experiment detail rows.
 */

public class ExperimentDetail {
    private List<String> headings;
    private List<String>  row1;
    private List<String>  row2;
    private List<Integer> colIndexDifference;

    public List<String> getHeadings() {
        return headings;
    }

    public void setHeadings(List<String> headings) {
        this.headings = headings;
    }

    public List<String> getRow1() {
        return row1;
    }

    public void setRow1(List<String> row1) {
        this.row1 = row1;
    }

    public List<String> getRow2() {
        return row2;
    }

    public void setRow2(List<String> row2) {
        this.row2 = row2;
    }

    public List<Integer> getColIndexDifference() {
        return colIndexDifference;
    }

    public void setColIndexDifference(List<Integer> colIndexDifference) {
        this.colIndexDifference = colIndexDifference;
    }
}