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

import java.util.HashSet;
import java.util.Set;

public class BioModelInsertDTOControl extends BioModelInsertDTO {

    protected Set<AccDbId> strains = new HashSet<>();


    /**
     * Constructs a control DTO for inserting biological models and their requisite components into the database
     *
     * @param dbId required
     * @param allelicComposition required
     * @param geneticBackground required
     * @param zygosity required
     * @param strain (required)
     */
    public BioModelInsertDTOControl(int dbId, String allelicComposition, String geneticBackground, String zygosity, AccDbId strain) {
        super(dbId, allelicComposition, geneticBackground, zygosity);
        this.strains.add(strain);
    }


    public Set<AccDbId> getStrains() {
        return strains;
    }

    public void setStrains(Set<AccDbId> strains) {
        this.strains = strains;
    }

    @Override
    public String toString() {
        return "BioModelInsertDTOControl{" +
                "dbId=" + dbId +
                ", allelicComposition='" + allelicComposition + '\'' +
                ", geneticBackground='" + geneticBackground + '\'' +
                ", zygosity='" + zygosity + '\'' +
                '}';
    }
}
