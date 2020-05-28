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

package org.mousephenotype.cda.dto;

import java.util.Objects;

public class UniqueDatatypeAndStatisticalMethod {
    String datatype;
    String statisticalMethod;

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getStatisticalMethod() {
        return statisticalMethod;
    }

    public void setStatisticalMethod(String statisticalMethod) {
        this.statisticalMethod = statisticalMethod;
    }

    public UniqueDatatypeAndStatisticalMethod(String datatype, String statisticalMethod) {
        this.datatype = datatype;
        this.statisticalMethod = statisticalMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniqueDatatypeAndStatisticalMethod that = (UniqueDatatypeAndStatisticalMethod) o;
        return datatype.equals(that.datatype) &&
                statisticalMethod.equals(that.statisticalMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datatype, statisticalMethod);
    }

    @Override
    public String toString() {
        return "UniqueDatatypeAndStatisticalMethod{" +
                "datatype='" + datatype + '\'' +
                ", statisticalMethod='" + statisticalMethod + '\'' +
                '}';
    }
}
