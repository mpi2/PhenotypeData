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

import org.mousephenotype.cda.db.pojo.Strain;

public class BioModelKeyControl extends BioModelKey {

    private Strain backgroundStrain;        // Used for experiment-level control specimens only.

    public BioModelKeyControl(Integer dbId, String allelicComposition, String geneticBackground, String zygosity, int phenotypingCenterPk, Strain backgroundStrain) {
        super(dbId, allelicComposition, geneticBackground, zygosity, phenotypingCenterPk);
        this.backgroundStrain = backgroundStrain;
    }

    public Strain getBackgroundStrain() {
        return backgroundStrain;
    }

    public void setBackgroundStrain(Strain backgroundStrain) {
        this.backgroundStrain = backgroundStrain;
    }

    @Override
    public String toString() {
        return "BioModelKeyControl{" +
                "backgroundStrain=" + backgroundStrain +
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
