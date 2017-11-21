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

import org.mousephenotype.cda.db.pojo.PhenotypedColony;

public class BioModelKeyMutant extends BioModelKey {

    private PhenotypedColony colony;        // Used for experiment-level and line-level mutant specimens only.

    public BioModelKeyMutant(Integer dbId, String allelicComposition, String geneticBackground, String zygosity, /*int phenotypingCenterPk,*/ PhenotypedColony colony) {
        super(dbId, allelicComposition, geneticBackground, zygosity);
        this.colony = colony;
    }

    public PhenotypedColony getColony() {
        return colony;
    }

    public void setColony(PhenotypedColony colony) {
        this.colony = colony;
    }

    @Override
    public String toString() {
        return "BioModelKeyMutant{" +
                "colony=" + colony +
                '}';
    }


    /**
     * Equality is determined by the components in {@link BioModelKey}. The {@link BioModelKeyControl} and
     * {@link BioModelKeyMutant} extended class components are ignored for purposes of equality/hash.
     */

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
