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

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.create.load.support.StrainMapper;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.web.ExperimentGroup;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class encapsulates the code and data to query and insert into the biological model tables. Typical usage is to
 * instantiate, then use the instance to query and insert.:
 * <ul>
 * <li>biological_model</li>
 * <li>biological_model_allele</li>
 * <li>biological_model_genomic_feature</li>
 * <li>biological_model_phenotype</li>
 * <li>biological_model_sample</li>
 * <li>biological_model_strain</li>
 * </ul>
 */
public class BioModelManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<BioModelKey, Integer> bioModelMap = new ConcurrentHashMap<>();          // key is composite key. Value is biological model primary key.
    private CdaSqlUtils               cdaSqlUtils;
    private DccSqlUtils               dccSqlUtils;
    private StrainMapper              strainMapper;

    private Map<String, Allele>           allelesBySymbolMap;
    private Map<String, GenomicFeature>   genesByAccMap;
    private Map<String, PhenotypedColony> phenotypedColonyMap;
    private Map<String, Strain>           strainsByNameMap;

    private Set<String> missingColonyIds = new HashSet<>();


    public BioModelManager(CdaSqlUtils cdaSqlUtils, DccSqlUtils dccSqlUtils) {
        this.cdaSqlUtils = cdaSqlUtils;
        this.dccSqlUtils = dccSqlUtils;
    }

    /**
     * Returns the biological model primary key if found; null otherwise
     *
     * @param bioModelKey
     * @return
     */
    public Integer getBiologicalModelPk(BioModelKey bioModelKey) {
        if (bioModelMap == null) {
            this.bioModelMap = cdaSqlUtils.getBioModelPks();
        }

        return bioModelMap.get(bioModelKey);
    }

    /**
     * @param dbId             the dbId of the specimen whose biological model is to be inserted
     * @param specimenExtended the specimen whose biological model is to be inserted
     * @return the biological_model primary key of the newly inserted record.
     * @throws DataLoadException
     */
    public int insert(int dbId, SpecimenExtended specimenExtended) throws DataLoadException {

        Integer biologicalModelPk = null;

        if (allelesBySymbolMap == null) {
            initialise();
        }

        Specimen        specimen        = specimenExtended.getSpecimen();
        ExperimentGroup experimentGroup = (specimen.isIsBaseline() ? ExperimentGroup.CONTROL : ExperimentGroup.MUTANT);

        // Check the bioModelMap for the biological model. If it is not found, create it.
        if (experimentGroup == ExperimentGroup.CONTROL) {

            // CONTROLS
            BioModelKeyControl controlKey = createBioModelControlKey(dbId, specimen.getStrainID());

            synchronized (this) {
                biologicalModelPk = bioModelMap.get(controlKey);
                if (biologicalModelPk == null) {
                    biologicalModelPk = createBiologicalModelControl(controlKey);
                    if (biologicalModelPk == null) {
                        throw new DataLoadException();
                    } else {
                        bioModelMap.put(controlKey, biologicalModelPk);
                    }
                }
            }
        } else {

            // MUTANTS. Get and use iMits colony info.
            BioModelKeyMutant mutantKey = createBioModelMutantKey(dbId, specimen.getZygosity().value(), specimen.getColonyID());

            synchronized (this) {
                biologicalModelPk = bioModelMap.get(mutantKey);
                if (biologicalModelPk == null) {
                    biologicalModelPk = createBiologicalModelMutant(mutantKey);
                    if (biologicalModelPk == null) {
                        throw new DataLoadException();
                    } else {
                        bioModelMap.put(mutantKey, biologicalModelPk);
                    }
                }
            }
        }

        return biologicalModelPk;
    }


    /**
     * @param dbId           the dbId of the line experiment whose biological model is to be inserted
     * @param lineExperiment the line experiment whose biological model is to be inserted
     * @return the biological_model primary key of the newly inserted record.
     * @throws DataLoadException
     */
    public int insert(Integer dbId, DccExperimentDTO lineExperiment) throws DataLoadException {

        Integer biologicalModelPk = null;

        if (allelesBySymbolMap == null) {
            initialise();
        }

        // Check the bioModelMap for the biological model. If it is not found, create it.

        // MUTANTS. Get and use iMits colony info.
        BioModelKeyMutant mutantKey = createBioModelKeyForLine(dbId, lineExperiment);

        synchronized (this) {
            biologicalModelPk = bioModelMap.get(mutantKey);
            if (biologicalModelPk == null) {
                biologicalModelPk = createBiologicalModelMutant(mutantKey);
                if (biologicalModelPk == null) {
                    throw new DataLoadException();
                } else {
                    bioModelMap.put(mutantKey, biologicalModelPk);
                }
            }
        }

        return biologicalModelPk;
    }

    public synchronized BioModelKeyMutant createBioModelKeyForLine(int dbId, DccExperimentDTO dccExperiment) throws DataLoadException {
        String message;
        String zygosity;
        String allelicComposition;
        String geneticBackground;

        List<SimpleParameter> simpleParameterList = dccSqlUtils.getSimpleParameters(dccExperiment.getDcc_procedure_pk());
        zygosity = LoadUtils.getLineLevelZygosity(simpleParameterList);

        // Get and use iMits colony info.
        PhenotypedColony colony = phenotypedColonyMap.get(dccExperiment.getColonyId());
        if ((colony == null) || (colony.getColonyName() == null)) {
            message = "Missing colonyId " + dccExperiment.getColonyId();
            missingColonyIds.add(message);
            return null;
        }
        allelicComposition = strainMapper.createAllelicComposition(zygosity, colony.getAlleleSymbol(), colony.getGene().getSymbol(), LoadUtils.SampleGroup.EXPERIMENTAL.value());
        geneticBackground = colony.getBackgroundStrain();

        return new BioModelKeyMutant(dbId, allelicComposition, geneticBackground, zygosity, colony);
    }

    public synchronized BioModelKey createBioModelKey(int dbId, boolean isControl, String strainId, String zygosity, String colonyId) throws DataLoadException {

        if (isControl) {
            // CONTROL
            return createBioModelControlKey(dbId, strainId);

        } else {
            // MUTANT
            return createBioModelMutantKey(dbId, zygosity, colonyId);
        }
    }

    public Set<String> getMissingColonyIds() {
        return missingColonyIds;
    }

    public Map<String, PhenotypedColony> getPhenotypedColonyMap() throws DataLoadException {
        if (phenotypedColonyMap == null) {
            initialise();
        }

        return phenotypedColonyMap;
    }


    // PRIVATE METHODS


    private void initialise() throws DataLoadException {

        // Initialise maps
        allelesBySymbolMap = cdaSqlUtils.getAllelesBySymbol();
        genesByAccMap = cdaSqlUtils.getGenesByAcc();
        phenotypedColonyMap = cdaSqlUtils.getPhenotypedColonies();
        strainsByNameMap = cdaSqlUtils.getStrainsByName();
        strainMapper = new StrainMapper(cdaSqlUtils);
    }

    private synchronized Integer createBiologicalModelControl(BioModelKeyControl controlKey) throws DataLoadException {

        String  message;
        Integer biologicalModelPk;

        DatasourceEntityId       dsId       = controlKey.getBackgroundStrain().getId();
        AccDbId                  strain     = new AccDbId(dsId.getAccession(), dsId.getDatabaseId());
        BioModelInsertDTOControl controlDto = new BioModelInsertDTOControl(controlKey.getDbId(), controlKey.getAllelicComposition(), controlKey.getGeneticBackground(), controlKey.getZygosity(), strain);

        biologicalModelPk = cdaSqlUtils.insertBiologicalModelImpc(controlDto);
        if (biologicalModelPk == null) {
            message = "BiologicalModel creation failed. BioModelInsertDTOControl: " + controlDto;
            logger.error(message);
            throw new DataLoadException(message);
        }

        return biologicalModelPk;
    }

    private synchronized Integer createBiologicalModelMutant(BioModelKeyMutant mutantKey) throws DataLoadException {
        String           message;
        Integer          biologicalModelPk;
        PhenotypedColony colony = mutantKey.getColony();

        GenomicFeature gf = genesByAccMap.get(colony.getGene().getId().getAccession());
        if (gf == null) {
            message = "Unknown gene '" + colony.getGene() + "'";
            logger.error(message);
            return null;
        }
        AccDbId gene = new AccDbId(gf.getId().getAccession(), gf.getId().getDatabaseId());

        Allele a = allelesBySymbolMap.get(colony.getAlleleSymbol());
        if (a == null) {
            a = cdaSqlUtils.createAndInsertAllele(colony.getAlleleSymbol(), colony.getGene());
            if (a == null) {
                message = "Couldn't create allele '" + colony.getAlleleSymbol() + "'";
                logger.error(message);
                throw new DataLoadException(message);
            } else {
                allelesBySymbolMap.put(a.getSymbol(), a);
            }
        }
        AccDbId allele = new AccDbId(a.getId().getAccession(), a.getId().getDatabaseId());

        Strain s = strainsByNameMap.get(colony.getBackgroundStrain());
        if (s == null) {
            s = strainMapper.createBackgroundStrain(colony.getBackgroundStrain());
            cdaSqlUtils.insertStrain(s);
            if (s == null) {
                message = "Couldn't create strain '" + colony.getBackgroundStrain() + "'";
                logger.error(message);
                throw new DataLoadException(message);
            } else {
                strainsByNameMap.put(s.getName(), s);
            }
        }
        AccDbId strain = new AccDbId(s.getId().getAccession(), s.getId().getDatabaseId());

        BioModelInsertDTOMutant mutantDto = new BioModelInsertDTOMutant(mutantKey.getDbId(), mutantKey.getAllelicComposition(), mutantKey.getGeneticBackground(), mutantKey.getZygosity(), gene, allele, strain);

        biologicalModelPk = cdaSqlUtils.insertBiologicalModelImpc(mutantDto);
        if (biologicalModelPk == null) {
            message = "BiologicalModel creation failed. BioModelInsertDTOMutant: " + mutantDto;
            logger.error(message);
            throw new DataLoadException(message);
        }

        return biologicalModelPk;
    }

    private synchronized BioModelKeyControl createBioModelControlKey(int dbId, String specimenStrainId) throws DataLoadException {
        String zygosity           = ZygosityType.homozygote.getName();
        String allelicComposition = "";
        Strain backgroundStrain   = cdaSqlUtils.getBackgroundStrain(specimenStrainId);
        String geneticBackground  = strainMapper.parseMultipleBackgroundStrainNames(backgroundStrain.getName());

        return new BioModelKeyControl(dbId, allelicComposition, geneticBackground, zygosity, backgroundStrain);
    }

    private synchronized BioModelKeyMutant createBioModelMutantKey(int dbId, String zygosity, String colonyId) throws DataLoadException {
        String message;
        String allelicComposition;
        String geneticBackground;

        PhenotypedColony colony = phenotypedColonyMap.get(colonyId);

        if ((colony == null) || (colony.getColonyName() == null)) {
            message = "Missing colonyId " + colonyId;
            missingColonyIds.add(message);
            return null;
        }
        allelicComposition = strainMapper.createAllelicComposition(zygosity, colony.getAlleleSymbol(), colony.getGene().getSymbol(), LoadUtils.SampleGroup.EXPERIMENTAL.value());
        geneticBackground = colony.getBackgroundStrain();

        return new BioModelKeyMutant(dbId, allelicComposition, geneticBackground, zygosity, colony);
    }
}