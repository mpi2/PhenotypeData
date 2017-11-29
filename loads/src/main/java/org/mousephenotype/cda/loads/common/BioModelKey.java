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
 * 2017-10-09 (mrelac)
 *
 * This class encapsulates the components of a biological model lookup key, which are:
 * <ul>
 *     <li>datasourceShortName</li>
 *     <li>strainAccessionId</li>
 *     <li>geneAccessionId (will be null/empty for controls)</li>
 *     <li>alleleAccessionId (will be null/empty for controls)</li>
 *     <li>zygosity</li>
 * </ul>
 */

public class BioModelKey {

    private String datasourceShortName;
    private String strainAccessionId;
    private String zygosity;
    private String geneAccessionId;
    private String alleleAccessionId;


    /**
     * Creates a {@link BioModelKey} instance from the given parameters that uniquely identifies a biological model.
     *
     * NOTE: This method is intended to be called by the {@link BioModelManager} and not by any other load code. The
     * {@link BioModelManager} performs critical strain remapping transformations. Callers wishing to create a
     * {@link BioModelKey} should use the {@link BioModelManager} {@code createMutantKey}() and {@code createControlKey}()
     * methods instead, which have a much simpler interface and perform the required strain remappings.
     *
     * @param datasourceShortName
     * @param strainAccessionId
     * @param geneAccessionId
     * @param alleleAccessionId
     * @param zygosity
     */
    public BioModelKey(String datasourceShortName, String strainAccessionId, String geneAccessionId, String alleleAccessionId, String zygosity) {
        this.datasourceShortName = datasourceShortName;
        this.strainAccessionId = strainAccessionId;
        this.geneAccessionId = (geneAccessionId == null ? "" : geneAccessionId);
        this.alleleAccessionId = (alleleAccessionId == null ? "" : alleleAccessionId);
        this.zygosity = zygosity;
    }

    @Override
    public boolean equals(Object o) {

        return (o.toString().equals(this.toString()));
    }

    @Override
    public int hashCode() {
        int result = this.toString().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return datasourceShortName + "_" + strainAccessionId + "_" + geneAccessionId + "_" + alleleAccessionId + "_" + zygosity;
    }
}