/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.seleniumtests.support;

/**
 * This class encapsulates the code and data necessary to represent the phenotype page top section procedure elements.
 *
 * Created by mrelac on 05/04/2016.
 */
public class PhenotypeProcedure {

    private String hrefPath;
    private String name;

    public PhenotypeProcedure() {

    }

    public PhenotypeProcedure(String name, String href) {
        setName(name);
        setHrefPath(href);
    }

    public void setHrefPath(String href) {
        String tmp = href.replace("//", "");       // Remove any "//".
            int idx = tmp.indexOf("/");

            this.hrefPath = tmp.substring(idx);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhenotypeProcedure that = (PhenotypeProcedure) o;

        if (hrefPath != null ? ! hrefPath.equals(that.hrefPath) : that.hrefPath != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = hrefPath != null ? hrefPath.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PhenotypeProcedure{" +
                "name='" + name + '\'' +
                ", hrefPath='" + hrefPath + '\'' +
                '}';
    }
}