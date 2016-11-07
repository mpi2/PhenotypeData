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

package org.mousephenotype.cda.loads.common;

import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;

/**
 * Created by mrelac on 07/11/2016.
 *
 * This class adds instance variables to the DCC Specimen for components supplied by us and not supplied by the DCC
 * that we need in the Dcc schema. Example: datasourceShortName.
 */


public class SpecimenExtended {
    private Specimen specimen;
    private String   datasourceShortName;

    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    public String getDatasourceShortName() {
        return datasourceShortName;
    }

    public void setDatasourceShortName(String datasourceShortName) {
        this.datasourceShortName = datasourceShortName;
    }
}
