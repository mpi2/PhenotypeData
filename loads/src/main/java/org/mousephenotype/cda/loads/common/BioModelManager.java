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

    private static Map<String, Allele>           allelesBySymbolMap;
    private static Map<BioModelKey, Long>        bioModelPkMap;          // key is BioModelKey key. Value is biological model primary key.
    private static Map<String, GenomicFeature>   genesByAccMap;
    private static Map<String, PhenotypedColony> phenotypedColonyMap;
    private static Map<String, Strain>           strainsByNameOrMgiAccessionIdMap;
    private static Map<String, OntologyTerm>     ontologyTermMap;


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
    public synchronized Long getBiologicalModelPk(BioModelKey bioModelKey) {

        return bioModelPkMap.get(bioModelKey);
    }

    public synchronized Long insertMutantIfMissing(SpecimenExtended specimenExtended, String zygosity,
                                                  Long dbId, Long biologicalSamplePk) throws DataLoadException
    {

        Specimen specimen = specimenExtended.getSpecimen();

        BioModelKey key = createMutantKey(specimenExtended.getDatasourceShortName(), specimen.getStrainID(), specimen.getColonyID(), zygosity);
        Long biologicalModelPk = getBiologicalModelPk(key);
        if (biologicalModelPk == null) {
            biologicalModelPk = insert(dbId, biologicalSamplePk, specimenExtended);
        } else {
            // Insert the relationship between biological_model and biological_sample
            cdaSqlUtils.insertBiologicalModelSample(biologicalModelPk, biologicalSamplePk);
        }

        return biologicalModelPk;
    }

    public synchronized Long insertControlIfMissing(SpecimenExtended specimenExtended, Long dbId, Long biologicalSamplePk) throws DataLoadException
    {

        Specimen specimen = specimenExtended.getSpecimen();

        BioModelKey key               = createControlKey(specimenExtended.getDatasourceShortName(), specimen.getStrainID());
        Long     biologicalModelPk = getBiologicalModelPk(key);
        if (biologicalModelPk == null) {
            biologicalModelPk = insert(dbId, biologicalSamplePk, specimenExtended);
        } else {
            // Insert the relationship between biological_model and biological_sample
            cdaSqlUtils.insertBiologicalModelSample(biologicalModelPk, biologicalSamplePk);
        }

        return biologicalModelPk;
    }


    public synchronized long insertLineIfMissing(String zygosity, long dbId, long phenotypingCenterPk, DccExperimentDTO lineExperiment) throws DataLoadException
    {
        BioModelKey key               = createMutantKey(lineExperiment.getDatasourceShortName(), lineExperiment.getSpecimenStrainId(), lineExperiment.getColonyId(), zygosity);
        Long     biologicalModelPk = getBiologicalModelPk(key);

        if (biologicalModelPk == null) {
            biologicalModelPk = insert(dbId, lineExperiment);
        }

        return biologicalModelPk;
    }


    /**
     * Create a biological model key for mutants using the specified components.
     * @param datasourceShortName
     * @param colonyId The colony id (name)
     * @param zygosity
     * @return
     * @throws DataLoadException
     */
    public synchronized BioModelKey createMutantKey(
            String datasourceShortName, String backgroundStrain, String colonyId, String zygosity) throws DataLoadException
    {
        String      message;
        BioModelKey key;

        GenomicFeature gene;
        Allele         allele;
        Strain         strain;

        PhenotypedColony colony = phenotypedColonyMap.get(colonyId);
        if (colony == null) {
            message = "colony must not be null";
            logger.error(message);
            throw new DataLoadException(message);
        }

        gene = getGene(colony);
        allele = getAllele(colony, gene);
        strain = strainsByNameOrMgiAccessionIdMap.get(backgroundStrain);

        if (strain == null) {
            Strain newStrain = StrainMapper.createBackgroundStrain(backgroundStrain);
            cdaSqlUtils.insertStrain(newStrain);
            strainsByNameOrMgiAccessionIdMap.put(backgroundStrain, newStrain);
            strain = newStrain;
        }

        key = new BioModelKey(datasourceShortName, strain.getId().getAccession(), gene.getId().getAccession(), allele.getId().getAccession(), zygosity);

        return key;
    }

    /**
     * Create a biological model key for controls using the specified components.
     * @param datasourceShortName
     * @param strainName
     * @return
     * @throws DataLoadException
     */
    public synchronized BioModelKey createControlKey(String datasourceShortName, String strainName) throws DataLoadException
    {

        BioModelKey key;
        Strain      strain;
        String      zygosity = ZygosityType.homozygote.getName();

        strain = strainsByNameOrMgiAccessionIdMap.get(strainName);

        key = new BioModelKey(datasourceShortName, strain.getId().getAccession(), null, null, zygosity);

        return key;
    }


    // GETTERS

    public Map<String, Allele> getAllelesBySymbolMap() {
        return allelesBySymbolMap;
    }

    public Map<BioModelKey, Long> getBioModelPkMap() {
        return bioModelPkMap;
    }

    public Map<String, GenomicFeature> getGenesByAccMap() {
        return genesByAccMap;
    }

    public Map<String, PhenotypedColony> getPhenotypedColonyMap() {
        return phenotypedColonyMap;
    }

    public Map<String, Strain> getStrainsByNameOrMgiAccessionIdMap() {
        return strainsByNameOrMgiAccessionIdMap;
    }

    public static Map<String, OntologyTerm> getOntologyTermMap() {
        return ontologyTermMap;
    }

    // PRIVATE METHODS


    private synchronized void initialise() throws DataLoadException {

        // Initialise maps
        allelesBySymbolMap = new ConcurrentHashMapAllowNull<>(cdaSqlUtils.getAllelesBySymbol());
        genesByAccMap = new ConcurrentHashMapAllowNull<>(cdaSqlUtils.getGenesByAcc());
        phenotypedColonyMap = new ConcurrentHashMapAllowNull<>(cdaSqlUtils.getPhenotypedColonies());
        strainsByNameOrMgiAccessionIdMap = new ConcurrentHashMapAllowNull<>(cdaSqlUtils.getStrainsByNameOrMgiAccessionIdMap());
        bioModelPkMap = new ConcurrentHashMapAllowNull<>(cdaSqlUtils.getBiologicalModelPksMapByBioModelKey());
        ontologyTermMap = new ConcurrentHashMapAllowNull<>(cdaSqlUtils.getOntologyTermsByName());
    }


    /**
     * Insert a sample-level {@link SpecimenExtended} control or mutant biological model
     *
     * @param dbId             the dbId of the specimen whose biological model is to be inserted
     * @param biologicalSamplePk the biologicalSample primary key
     * @param specimenExtended the specimen whose biological model is to be inserted
     * @return the biological_model primary key of the newly inserted record.
     * @throws DataLoadException
     */
    private Long insert(Long dbId, Long biologicalSamplePk, SpecimenExtended specimenExtended) throws DataLoadException {

        Long   biologicalModelPk;
        String datasourceShortName = specimenExtended.getDatasourceShortName();
        String zygosity;

        Specimen        specimen        = specimenExtended.getSpecimen();
        ExperimentGroup experimentGroup = (specimen.isIsBaseline() ? ExperimentGroup.CONTROL : ExperimentGroup.MUTANT);

        if (experimentGroup == ExperimentGroup.CONTROL) {

            biologicalModelPk = insertControl(dbId, biologicalSamplePk, datasourceShortName, specimen.getStrainID());

        } else {

            zygosity = LoadUtils.getSpecimenLevelMutantZygosity(specimen.getZygosity().value());
            biologicalModelPk = insertMutant(dbId, biologicalSamplePk, datasourceShortName, specimen.getStrainID(), specimen.getColonyID(), zygosity);
        }

        return biologicalModelPk;
    }


    /**
     * Insert a line-level {@link DccExperimentDTO} mutant biological model
     *
     * @param dbId                the dbId of the line experiment whose biological model is to be inserted
     * @param lineExperiment      the line experiment whose biological model is to be inserted
     *
     * @return the biological_model primary key of the newly inserted record.
     *
     * @throws DataLoadException
     */
    private long insert(long dbId, DccExperimentDTO lineExperiment) throws DataLoadException {

        String datasourceShortName = lineExperiment.getDatasourceShortName();

        // MUTANTS. Use iMits colony info.
        List<SimpleParameter> simpleParameterList = dccSqlUtils.getSimpleParameters(lineExperiment.getDcc_procedure_pk());
        String zygosity = LoadUtils.getLineLevelZygosity(simpleParameterList);

        return insertMutant(dbId, null, datasourceShortName, lineExperiment.getSpecimenStrainId(), lineExperiment.getColonyId(), zygosity);
    }


    private long insertMutant(long dbId, Long biologicalSamplePk, String datasourceShortName,
                             String backgroundStrain, String colonyId, String zygosity) throws DataLoadException {

        long   biologicalModelPk;
        String message;

        PhenotypedColony colony = phenotypedColonyMap.get(colonyId);
        if (colony == null) {
            message = "Missing colonyId " + colonyId;
            logger.error(message);
            throw new DataLoadException(message);
        }

        String allelicComposition = StrainMapper.createAllelicComposition(zygosity, colony.getAlleleSymbol(), colony.getGene().getSymbol(), LoadUtils.SampleGroup.EXPERIMENTAL.value());

        GenomicFeature gene = getGene(colony);
        AccDbId geneAcc = new AccDbId(gene.getId().getAccession(), gene.getId().getDatabaseId());

        Allele allele = getAllele(colony, gene);
        AccDbId alleleAcc = new AccDbId(allele.getId().getAccession(), allele.getId().getDatabaseId());

        Strain strain = strainsByNameOrMgiAccessionIdMap.get(backgroundStrain);
        AccDbId strainAcc = new AccDbId(strain.getId().getAccession(), strain.getId().getDatabaseId());

        String geneticBackground = strain.getGeneticBackground();
        BioModelInsertDTOMutant mutantDto = new BioModelInsertDTOMutant(dbId, biologicalSamplePk, allelicComposition, geneticBackground, zygosity, geneAcc, alleleAcc, strainAcc);

        BioModelKey mutantKey = createMutantKey(datasourceShortName, backgroundStrain, colony.getColonyName(), zygosity);
        Long existingModelPk = bioModelPkMap.get(mutantKey);
        biologicalModelPk = cdaSqlUtils.insertBiologicalModelImpc(mutantDto, existingModelPk);

        if(existingModelPk == null) {
            bioModelPkMap.put(mutantKey, biologicalModelPk);
        }

        return biologicalModelPk;
    }

    private Long insertControl(Long dbId, Long biologicalSamplePk, String datasourceShortName, String strainName) throws DataLoadException {

        String allelicComposition = "";
        String zygosity           = ZygosityType.homozygote.getName();
        Long   biologicalModelPk;

        Strain strain = strainsByNameOrMgiAccessionIdMap.get(strainName);
        AccDbId strainAcc = new AccDbId(strain.getId().getAccession(), strain.getId().getDatabaseId());

        String geneticBackground = strain.getGeneticBackground();
        BioModelInsertDTOControl controlDto = new BioModelInsertDTOControl(dbId, biologicalSamplePk, allelicComposition, geneticBackground, zygosity, strainAcc);

        BioModelKey controlKey = createControlKey(datasourceShortName, strainName);
        Long existingModelPk = bioModelPkMap.get(controlKey);

        biologicalModelPk = cdaSqlUtils.insertBiologicalModelImpc(controlDto, existingModelPk);

        if(existingModelPk == null) {
            bioModelPkMap.put(controlKey, biologicalModelPk);
        }

        return biologicalModelPk;
    }

    private GenomicFeature getGene(PhenotypedColony colony) throws DataLoadException {
        String message;
        GenomicFeature gene = genesByAccMap.get(colony.getGene().getId().getAccession());
        if (gene == null) {
            message = "Unknown gene '" + colony.getGene() + "'";
            logger.error(message);
            throw new DataLoadException(message);
        }

        return gene;
    }

    private Allele getAllele(PhenotypedColony colony, GenomicFeature gene) throws DataLoadException {
        Allele allele = allelesBySymbolMap.get(colony.getAlleleSymbol());
        if (allele == null) {
            OntologyTerm targetedTerm = ontologyTermMap.get(CdaSqlUtils.ONTOLOGY_TERM_TARGETED);
            allele = cdaSqlUtils.createAlleleFromSymbol(colony.getAlleleSymbol(), gene, targetedTerm);
            cdaSqlUtils.insertAllele(allele);
            allelesBySymbolMap.put(allele.getSymbol(), allele);
        }

        return allele;
    }
}