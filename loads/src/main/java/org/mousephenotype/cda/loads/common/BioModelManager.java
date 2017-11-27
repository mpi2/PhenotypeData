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

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.PhenotypedColony;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.create.load.support.StrainMapper;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.web.ExperimentGroup;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * This class encapsulates the code and data to query and insert into the biological model tables. Typical usage is to
 * instantiate, then use the instance to query and insert:
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

    private CdaSqlUtils  cdaSqlUtils;
    private DccSqlUtils  dccSqlUtils;
    private StrainMapper strainMapper;

    private Map<String, Allele>           allelesBySymbolMap;
    private Map<BioModelKey, Integer>     bioModelPkMap;          // key is BioModelKey key. Value is biological model primary key.
    private Map<String, GenomicFeature>   genesByAccMap;
    private Map<String, PhenotypedColony> phenotypedColonyMap;
    private Map<String, Strain>           strainsByNameMap;


    public BioModelManager(CdaSqlUtils cdaSqlUtils, DccSqlUtils dccSqlUtils) throws DataLoadException {
        this.cdaSqlUtils = cdaSqlUtils;
        this.dccSqlUtils = dccSqlUtils;
        initialise();
    }

    /**
     * Returns the biological model primary key if found; null otherwise.
     *
     * @param bioModelKey The biological model key that uniquely identifies a biological model. Use the static method
     * BioModelKey.make() to make the BioModelKey.
     *
     * @return the biological model primary key if found; null otherwise.
     */
    public Integer getBiologicalModelPk(BioModelKey bioModelKey) {

        return bioModelPkMap.get(bioModelKey);
    }

    /**
     * @param dbId             the dbId of the specimen whose biological model is to be inserted
     * @param biologicalSamplePk the biologicalSample primary key
     * @param phenotypingCenterPk the phenotyping center primary key
     * @param specimenExtended the specimen whose biological model is to be inserted
     * @return the biological_model primary key of the newly inserted record.
     * @throws DataLoadException
     */
    public int insert(int dbId, int biologicalSamplePk, int phenotypingCenterPk, SpecimenExtended specimenExtended) throws DataLoadException {

        int    biologicalModelPk;
        String datasourceShortName = specimenExtended.getDatasourceShortName();
        String zygosity;

        Specimen        specimen        = specimenExtended.getSpecimen();
        ExperimentGroup experimentGroup = (specimen.isIsBaseline() ? ExperimentGroup.CONTROL : ExperimentGroup.MUTANT);

        if (experimentGroup == ExperimentGroup.CONTROL) {

            biologicalModelPk = insertControl(dbId, biologicalSamplePk, phenotypingCenterPk, datasourceShortName, specimen.getSpecimenID(), specimen.getStrainID());

        } else {

            zygosity = cdaSqlUtils.getSpecimenLevelMutantZygosity(specimen.getZygosity().value());
            biologicalModelPk = insertMutant(dbId, biologicalSamplePk, phenotypingCenterPk, datasourceShortName, specimen.getColonyID(), specimen.getSpecimenID(), zygosity);
        }

        return biologicalModelPk;
    }


    /**
     * @param dbId                the dbId of the line experiment whose biological model is to be inserted
     * @param biologicalSamplePk the biological sample primary key (will be null in the case of line-level experiment biological model inserts)
     * @param phenotypingCenterPk the phenotyping center primary key
     * @param lineExperiment      the line experiment whose biological model is to be inserted
     *
     * @return the biological_model primary key of the newly inserted record.
     *
     * @throws DataLoadException
     */
    public int insert(int dbId, Integer biologicalSamplePk, int phenotypingCenterPk, DccExperimentDTO lineExperiment) throws DataLoadException {

        String datasourceShortName = lineExperiment.getDatasourceShortName();

        // MUTANTS. Use iMits colony info.
        List<SimpleParameter> simpleParameterList = dccSqlUtils.getSimpleParameters(lineExperiment.getDcc_procedure_pk());
        String zygosity = LoadUtils.getLineLevelZygosity(simpleParameterList);

        return insertMutant(dbId, biologicalSamplePk, phenotypingCenterPk, datasourceShortName, lineExperiment.getColonyId(), lineExperiment.getSpecimenId(), zygosity);
    }


    // GETTERS

    public Map<String, Allele> getAllelesBySymbolMap() {
        return allelesBySymbolMap;
    }

    public Map<BioModelKey, Integer> getBioModelPkMap() {
        return bioModelPkMap;
    }

    public Map<String, GenomicFeature> getGenesByAccMap() {
        return genesByAccMap;
    }

    public Map<String, PhenotypedColony> getPhenotypedColonyMap() {
        return phenotypedColonyMap;
    }

    public Map<String, Strain> getStrainsByNameMap() {
        return strainsByNameMap;
    }

    public StrainMapper getStrainMapper() {
        return strainMapper;
    }

    // PRIVATE METHODS


    private void initialise() throws DataLoadException {

        // Initialise maps
        allelesBySymbolMap = cdaSqlUtils.getAllelesBySymbol();
        genesByAccMap = cdaSqlUtils.getGenesByAcc();
        phenotypedColonyMap = cdaSqlUtils.getPhenotypedColonies();
        strainsByNameMap = cdaSqlUtils.getStrainsByName();
        strainMapper = new StrainMapper(cdaSqlUtils);
        bioModelPkMap = cdaSqlUtils.getBiologicalModelPksMapByBioModelKey();
    }

    private int insertMutant(int dbId, Integer biologicalSamplePk, int phenotypingCenterPk, String datasourceShortName,
                             String colonyId, String specimenId, String zygosity) throws DataLoadException {

        int    biologicalModelPk;
        String message;

        PhenotypedColony colony = phenotypedColonyMap.get(colonyId);
        if (colony == null) {
            message = "Missing colonyId " + colonyId;
            logger.error(message);
            throw new DataLoadException(message);
        }

        String allelicComposition = strainMapper.createAllelicComposition(zygosity, colony.getAlleleSymbol(), colony.getGene().getSymbol(), LoadUtils.SampleGroup.EXPERIMENTAL.value());


        // GENE part
        GenomicFeature gene = genesByAccMap.get(colony.getGene().getId().getAccession());
        if (gene == null) {
            message = "Unknown gene '" + colony.getGene() + "'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        AccDbId geneAcc = new AccDbId(gene.getId().getAccession(), gene.getId().getDatabaseId());

        // ALLELE part
        Allele allele = allelesBySymbolMap.get(colony.getAlleleSymbol());
        if (allele == null) {
            allele = cdaSqlUtils.createAlleleFromSymbol(colony.getAlleleSymbol(), gene);
            cdaSqlUtils.insertAllele(allele);
            allelesBySymbolMap.put(allele.getSymbol(), allele);
        }
        AccDbId alleleAcc = new AccDbId(allele.getId().getAccession(), allele.getId().getDatabaseId());

        // STRAIN part
        String remappedBackgroundStrain = strainMapper.parseMultipleBackgroundStrainNames(colony.getBackgroundStrain());
        Strain strain                   = strainsByNameMap.get(remappedBackgroundStrain);
        if (strain == null) {
            strain = strainMapper.createBackgroundStrain(remappedBackgroundStrain);
            cdaSqlUtils.insertStrain(strain);
            strainsByNameMap.put(strain.getName(), strain);
        }
        AccDbId strainAcc = new AccDbId(strain.getId().getAccession(), strain.getId().getDatabaseId());

        String geneticBackground = strain.getGeneticBackground();
        BioModelInsertDTOMutant mutantDto = new BioModelInsertDTOMutant(dbId, biologicalSamplePk, allelicComposition, geneticBackground, zygosity, geneAcc, alleleAcc, strainAcc);
        biologicalModelPk = cdaSqlUtils.insertBiologicalModelImpc(mutantDto);

        BioModelKey mutantKey = BioModelKey.make(specimenId, phenotypingCenterPk, datasourceShortName, zygosity);
        bioModelPkMap.put(mutantKey, biologicalModelPk);

        return biologicalModelPk;
    }

    private int insertControl(int dbId, int biologicalSamplePk, int phenotypingCenterPk, String datasourceShortName, String specimenId, String specimenStrainId) throws DataLoadException {

        String allelicComposition = "";
        String zygosity           = ZygosityType.homozygote.getName();
        int    biologicalModelPk;

        // STRAIN part
        String remappedBackgroundStrain = strainMapper.parseMultipleBackgroundStrainNames(specimenStrainId);
        Strain strain                   = strainsByNameMap.get(remappedBackgroundStrain);
        if (strain == null) {
            strain = strainMapper.createBackgroundStrain(remappedBackgroundStrain);
            cdaSqlUtils.insertStrain(strain);
            strainsByNameMap.put(strain.getName(), strain);
        }
        AccDbId strainAcc = new AccDbId(strain.getId().getAccession(), strain.getId().getDatabaseId());

        String geneticBackground = strain.getGeneticBackground();
        BioModelInsertDTOControl controlDto = new BioModelInsertDTOControl(dbId, biologicalSamplePk, allelicComposition, geneticBackground, zygosity, strainAcc);
        biologicalModelPk = cdaSqlUtils.insertBiologicalModelImpc(controlDto);

        BioModelKey controlKey = BioModelKey.make(specimenId, phenotypingCenterPk, datasourceShortName, zygosity);
        bioModelPkMap.put(controlKey, biologicalModelPk);

        return biologicalModelPk;
    }
}