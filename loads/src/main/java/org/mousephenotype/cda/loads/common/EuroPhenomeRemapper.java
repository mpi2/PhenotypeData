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
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;

import java.util.Map;

/**
 * Some dcc europhenome colonies were incorrectly associated with EuroPhenome. Imits has the authoritative mapping
 * between colonyId and project and, for these incorrect colonies, overrides the dbId to reflect the real owner
 * of the data, MGP.
 */
public class EuroPhenomeRemapper {

    private SpecimenExtended specimenExtended;
    private DccExperimentDTO dccExperiment;

    public EuroPhenomeRemapper(SpecimenExtended specimenExtended) {
        this.specimenExtended = specimenExtended;
    }

    public EuroPhenomeRemapper(DccExperimentDTO dccExperiment) {
        this.dccExperiment = dccExperiment;
    }

    public boolean needsRemapping() {

        // Ignore mice with colonyId starting with 'baseline', as these specimens are not in imits.
        if (specimenExtended != null) {
            if ((specimenExtended.getDatasourceShortName().equals(CdaSqlUtils.EUROPHENOME) &&
                    ( ! specimenExtended.getSpecimen().getColonyID().toLowerCase().startsWith("baseline")))) {

                return true;
            }
        } else {
            if ((dccExperiment.getDatasourceShortName().equals(CdaSqlUtils.EUROPHENOME) &&
                    ( ! dccExperiment.getColonyId().toLowerCase().startsWith("baseline")))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Remap special-case europhenome ownership. This method may change the project and the datasourceShortName of the
     * {@link DccExperimentDTO} or {@link SpecimenExtended} passed into the EuroPhenomeRemapper constructor call.
     * @param phenotypedColonyMap
     * @param cdaDb_idMap
     * @return {@link RunStatus}
     */
    public RunStatus remap(Map<String, PhenotypedColony> phenotypedColonyMap, Map<String, Integer> cdaDb_idMap) {

        RunStatus status = new RunStatus();
        PhenotypedColony phenotypedColony;
        String errMsg;

        if (specimenExtended != null) {
            Specimen specimen = specimenExtended.getSpecimen();
            phenotypedColony = phenotypedColonyMap.get(specimen.getColonyID());
            if ((phenotypedColony == null) || (phenotypedColony.getColonyName() == null)) {
                errMsg = "Unable to get phenotypedColony for samples for colonyId {} to apply special MGP" +
                        " remap rule for EuroPhenome. Rule NOT applied.";
                status.addError(errMsg);

                return status;
            }

            if (phenotypedColony.getPhenotypingConsortium().getName().equals(CdaSqlUtils.MGP)) {
                specimen.setProject(CdaSqlUtils.MGP);
                specimenExtended.setDatasourceShortName(CdaSqlUtils.MGP);
            }
        } else {
            phenotypedColony = phenotypedColonyMap.get(dccExperiment.getColonyId());
            if ((phenotypedColony == null) || (phenotypedColony.getColonyName() == null)) {
                errMsg = "Unable to get phenotypedColony for experiments for colonyId {} to apply special MGP" +
                        " remap rule for EuroPhenome. Rule NOT applied.";
                status.addError(errMsg);

                return status;
            }

            if (phenotypedColony.getPhenotypingConsortium().getName().equals(CdaSqlUtils.MGP)) {
                dccExperiment.setProject(CdaSqlUtils.MGP);
                dccExperiment.setDatasourceShortName(CdaSqlUtils.MGP);
            }
        }

        return status;
    }

    public SpecimenExtended getSpecimenExtended() {
        return specimenExtended;
    }

    public DccExperimentDTO getDccExperiment() {
        return dccExperiment;
    }
}