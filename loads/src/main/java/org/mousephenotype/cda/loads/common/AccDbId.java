/*******************************************************************************
 * Copyright © 2017 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.common;

/**
 * 2017-10=05 (mrelac) This class wraps an accession id and a db id, used by gene, allele, strains, phenotype objects.
 */
public class AccDbId {
    private String acc;
    private Integer dbId;

    public AccDbId(String acc, Integer dbId) {
        this.acc = acc;
        this.dbId = dbId;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccDbId accDbId = (AccDbId) o;

        if (!acc.equals(accDbId.acc)) return false;
        return dbId.equals(accDbId.dbId);
    }

    @Override
    public int hashCode() {
        int result = acc.hashCode();
        result = 31 * result + dbId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AccDbId{" +
                "acc='" + acc + '\'' +
                ", dbId=" + dbId +
                '}';
    }
}