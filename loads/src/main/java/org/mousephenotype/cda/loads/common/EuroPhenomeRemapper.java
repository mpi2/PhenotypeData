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
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;

import java.util.Map;

/**
 * Some dcc europhenome colonies were incorrectly associated with EuroPhenome. Imits has the authoritative mapping
 * between colonyId and project and, for these incorrect colonies, overrides the dbId and phenotyping center to
 * reflect the real owner of the data, MGP.
 */
public class EuroPhenomeRemapper {

    private SpecimenExtended specimenExtended;
    private DccExperimentDTO dccExperiment;
    private Map<String, PhenotypedColony> colonyMap;

    public EuroPhenomeRemapper(SpecimenExtended specimenExtended, Map<String, PhenotypedColony> colonyMap) {
        this.specimenExtended = specimenExtended;
        this.colonyMap = colonyMap;
    }

    public EuroPhenomeRemapper(DccExperimentDTO dccExperiment, Map<String, PhenotypedColony> colonyMap) {
        this.dccExperiment = dccExperiment;
        this.colonyMap = colonyMap;
    }



    public boolean needsRemapping() {

        PhenotypedColony colony;

        // Ignore mice with colonyId starting with 'baseline', as these specimens are not in imits.
        if (specimenExtended != null) {
            Specimen specimen = specimenExtended.getSpecimen();
            colony = colonyMap.get(specimen.getColonyID());

            if ((specimenExtended.getDatasourceShortName().equals(CdaSqlUtils.EUROPHENOME))
                    && ( ! specimen.getColonyID().toLowerCase().startsWith("baseline"))
                    && (colony != null)
                    && (colony.getPhenotypingConsortium().getName().equals(CdaSqlUtils.MGP))) {

                return true;
            }
        } else {

            colony = colonyMap.get(dccExperiment.getColonyId());

            if ((dccExperiment.getDatasourceShortName().equals(CdaSqlUtils.EUROPHENOME))
                    && ( ! dccExperiment.getColonyId().toLowerCase().startsWith("baseline"))
                    && (colony != null)
                    && (colony.getPhenotypingConsortium().getName().equals(CdaSqlUtils.MGP))) {

                return true;
            }
        }

        return false;
    }

    /**
     * Remap special-case europhenome ownership. This method changes project and the datasourceShortName of the
     * {@link DccExperimentDTO} or {@link SpecimenExtended} passed into the EuroPhenomeRemapper constructor call,
     * so callers will want to wait to initialise dbId, phenotypingCenter, and phenotypingCenterPk until after
     * this call has completed.
     */
    public void remap() {

        if (specimenExtended != null) {
            specimenExtended.getSpecimen().setProject(CdaSqlUtils.MGP);
            specimenExtended.setDatasourceShortName(CdaSqlUtils.MGP);
        } else {
            dccExperiment.setProject(CdaSqlUtils.MGP);
            dccExperiment.setDatasourceShortName(CdaSqlUtils.MGP);
        }
    }
}