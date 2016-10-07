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

package org.mousephenotype.cda.loads.create.load.steps;

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.common.LoadUtils;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.BiologicalModelAggregator;
import org.mousephenotype.cda.loads.create.load.support.EuroPhenomeStrainMapper;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StageUnit;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Embryo;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Mouse;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Loads the experiments from a database with a dcc schema into the cda database.
 *
 * Created by mrelac on 31/08/2016.
 *
 */
public class ExperimentLoader implements Step, Tasklet, InitializingBean {

    private Map<String, Allele>     allelesBySymbol         = new HashMap<>();
    private CommonUtils             commonUtils             = new CommonUtils();
    private EuroPhenomeStrainMapper euroPhenomeStrainMapper;

    private CdaSqlUtils                cdaSqlUtils;
    private DccSqlUtils                dccSqlUtils;
    private NamedParameterJdbcTemplate jdbcCda;
    private LoadUtils                  loadUtils = new LoadUtils();

    private Set<String> missingColonyIds = new HashSet<>();
    private Set<String> unexpectedStage = new HashSet<>();

    private final Logger         logger      = LoggerFactory.getLogger(this.getClass());
    private StepBuilderFactory   stepBuilderFactory;
    private Map<String, Integer> written     = new HashMap<>();

    private OntologyTerm developmentalStageMouse;
    private OntologyTerm sampleTypeMouseEmbryoStage;
    private OntologyTerm sampleTypeWholeOrganism;

    private String externalDbShortName;
    private int externalDbId;
    private int efoDbId;


    public ExperimentLoader(NamedParameterJdbcTemplate jdbcCda, StepBuilderFactory stepBuilderFactory,
                            CdaSqlUtils cdaSqlUtils, DccSqlUtils dccSqlUtils, String externalDbShortName) {
        this.jdbcCda = jdbcCda;
        this.stepBuilderFactory = stepBuilderFactory;
        this.cdaSqlUtils = cdaSqlUtils;
        this.dccSqlUtils = dccSqlUtils;
        this.externalDbShortName = externalDbShortName;

        written.put("biologicalModel", 0);
        written.put("biologicalSample", 0);
        written.put("liveSample", 0);
        written.put("controlSample", 0);
        written.put("experimentalSample", 0);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        developmentalStageMouse = cdaSqlUtils.getOntologyTermByName("postnatal");
        sampleTypeMouseEmbryoStage = cdaSqlUtils.getOntologyTermByName("mouse embryo stage");
        sampleTypeWholeOrganism = cdaSqlUtils.getOntologyTermByName("whole organism");
        allelesBySymbol = cdaSqlUtils.getAllelesBySymbol();
        this.euroPhenomeStrainMapper = new EuroPhenomeStrainMapper(cdaSqlUtils);
        setExternalDb(externalDbShortName);
        this.efoDbId = cdaSqlUtils.getExternalDbId("EFO");

        Assert.notNull(developmentalStageMouse, "developmentalStageMouse must be set");
        Assert.notNull(sampleTypeMouseEmbryoStage, "xsampleTypeMouseEmbryoStagex must be set");
        Assert.notNull(sampleTypeWholeOrganism, "sampleTypeWholeOrganism must be set");
        Assert.notNull(allelesBySymbol, "allelesBySymbol must be set");
        Assert.notNull(jdbcCda, "jdbcCda must be set");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must be set");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must be set");
        Assert.notNull(dccSqlUtils, "dccSqlUtils must be set");
        Assert.notNull(externalDbId, "externalDb short_name (e.g. IMPC, Ensembl, etc.) must be set");
        Assert.notNull(efoDbId, "efoDbId must be set");
    }

    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "experimentLoaderStep";
    }

    /**
     * @return true if a step that is already marked as complete can be started again.
     */
    @Override
    public boolean isAllowStartIfComplete() {
        return false;
    }

    /**
     * @return the number of times a job can be started with the same identifier.
     */
    @Override
    public int getStartLimit() {
        return 10;
    }

    /**
     * Process the step and assign progress and status meta information to the {@link StepExecution} provided. The
     * {@link Step} is responsible for setting the meta information and also saving it if required by the
     * implementation.<br>
     * <p/>
     * It is not safe to re-use an instance of {@link Step} to process multiple concurrent executions.
     *
     * @param stepExecution an entity representing the step to be executed
     * @throws JobInterruptedException if the step is interrupted externally
     */
    @Override
    public void execute(StepExecution stepExecution) throws JobInterruptedException {
        stepBuilderFactory.get("experimentLoaderStep")
                .tasklet(this)
                .build()
                .execute(stepExecution);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();

        List<org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment> dccExperiments = dccSqlUtils.getExperiments();
        Map<String, Integer> counts;

        for (org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment dccExperiment : dccExperiments) {
            counts = insertExperiment(dccExperiment);






//            String sampleGroup = (experiment.isIsBaseline()) ? "control" : "experimental";
//            boolean isControl = (sampleGroup.equals("control"));
//
//            if (isControl) {
//                counts = insertSampleControlSpecimen(experiment);
//                written.put("controlSample", written.get("controlSample") + 1);
//            } else {
//                counts = insertSampleExperimentalSpecimen(experiment);
//                written.put("experimentalSample", written.get("experimentalSample") + 1);
//            }
//
//            written.put("biologicalModel", written.get("biologicalModel") + counts.get("biologicalModel"));
//            written.put("biologicalSample", written.get("biologicalSample") + counts.get("biologicalSample"));
//            written.put("liveSample", written.get("liveSample") + counts.get("liveSample"));
        }

        Iterator<String> missingColonyIdsIt = missingColonyIds.iterator();
        while (missingColonyIdsIt.hasNext()) {
            String colonyId = missingColonyIdsIt.next();
            logger.error("Missing phenotyped_colony information for dcc-supplied colony " + colonyId + ". Skipping...");
        }

//        Iterator<String> unexpectedStageIt = unexpectedStage.iterator();
//        while (unexpectedStageIt.hasNext()) {
//            String stage = unexpectedStageIt.next();
//            logger.info("Unexpected value for embryonic DCP stage: " + stage);
//        }
//
//        logger.info("Wrote {} new biological models", written.get("biologicalModel"));
//        logger.info("Wrote {} new biological samples", written.get("biologicalSample"));
//        logger.info("Wrote {} new live samples", written.get("liveSample"));
//        logger.info("Processed {} experimental samples", written.get("experimentalSample"));
//        logger.info("Processed {} control samples", written.get("controlSample"));
//        logger.info("Processed {} total samples", written.get("experimentalSample") + written.get("controlSample"));

        logger.debug("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
        contribution.setExitStatus(ExitStatus.COMPLETED);
        chunkContext.setComplete();

        return RepeatStatus.FINISHED;
    }


    public RepeatStatus execute2(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();

        List<Specimen> specimens = dccSqlUtils.getSpecimens();
        Map<String, Integer> counts;

        for (Specimen specimen : specimens) {
            String sampleGroup = (specimen.isIsBaseline()) ? "control" : "experimental";
            boolean isControl = (sampleGroup.equals("control"));

            if (isControl) {
                counts = insertSampleControlSpecimen(specimen);
                written.put("controlSample", written.get("controlSample") + 1);
            } else {
                counts = insertSampleExperimentalSpecimen(specimen);
                written.put("experimentalSample", written.get("experimentalSample") + 1);
            }

            written.put("biologicalModel", written.get("biologicalModel") + counts.get("biologicalModel"));
            written.put("biologicalSample", written.get("biologicalSample") + counts.get("biologicalSample"));
            written.put("liveSample", written.get("liveSample") + counts.get("liveSample"));
        }

        Iterator<String> missingColonyIdsIt = missingColonyIds.iterator();
        while (missingColonyIdsIt.hasNext()) {
            String colonyId = missingColonyIdsIt.next();
            logger.error("Missing phenotyped_colony information for dcc-supplied colony " + colonyId + ". Skipping...");
        }

        Iterator<String> unexpectedStageIt = unexpectedStage.iterator();
        while (unexpectedStageIt.hasNext()) {
            String stage = unexpectedStageIt.next();
            logger.info("Unexpected value for embryonic DCP stage: " + stage);
        }

        logger.info("Wrote {} new biological models", written.get("biologicalModel"));
        logger.info("Wrote {} new biological samples", written.get("biologicalSample"));
        logger.info("Wrote {} new live samples", written.get("liveSample"));
        logger.info("Processed {} experimental samples", written.get("experimentalSample"));
        logger.info("Processed {} control samples", written.get("controlSample"));
        logger.info("Processed {} total samples", written.get("experimentalSample") + written.get("controlSample"));

        logger.debug("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
        contribution.setExitStatus(ExitStatus.COMPLETED);
        chunkContext.setComplete();

        return RepeatStatus.FINISHED;
    }

    @Transactional
    private Map<String, Integer> insertExperiment(org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment dccExperiment) {
        Map<String, Integer> results = new HashMap<>();

        Experiment experiment = createExperiment(dccExperiment);

        return results;
    }

    private Experiment createExperiment(org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment dccExperiment) {
        Experiment experiment = new Experiment();

//        String originalColonyId = dcc.get
//        cdaSqlUtils.getPhenotypedColony(dccExperiment)
//        experiment.setColonyId(dccExperiment.);
//
//
//
//        dccSqlUtils.getSpecimens().iterator().next().
////        dccExperiment.
//        PhenotypedColony colony = cdaSqlUtils.getPhenotypedColony(dccExperiment.)
//
//        experiment.setColonyId(cdaSqlUtils.getCol);experiment.set

        return experiment;
    }

    @Transactional
    private Map<String, Integer> insertSampleExperimentalSpecimen(Specimen specimen) throws DataLoadException {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("biologicalModel", 0);
        counts.put("biologicalSample", 0);
        counts.put("liveSample", 0);
        String message;

        GenomicFeature gene;
        String backgroundStrainName;
        Strain backgroundStrain;

        // Query iMits first for specimen information. iMits is more up-to-date than the dcc.
        // NOTE: A certain set of EuroPhenome colonies that have duplicated work on a cell line must have their colony
        //       ids filtered in order to match the iMits colony to which they belong.
        PhenotypedColony colony = cdaSqlUtils.getPhenotypedColony(specimen.getColonyID());
        if (colony == null) {
            // Try looking up the colony id after removing the characters after the trailing underscore.
            String colonyId = specimen.getColonyID();
            int lastUnderscoreIndex = colonyId.lastIndexOf("_");
            if (lastUnderscoreIndex >= 0) {
                String truncatedColonyId = specimen.getColonyID().substring(0, lastUnderscoreIndex);
                colony = cdaSqlUtils.getPhenotypedColony(truncatedColonyId);
                if (colony == null) {
                    missingColonyIds.add(truncatedColonyId);
                }
            }

            return counts;
        }

        // Get the allele by symbol.
        Allele allele = allelesBySymbol.get(colony.getAlleleSymbol());
        if (allele == null) {
            try {
                allele = cdaSqlUtils.createAndInsertAllele(colony.getAlleleSymbol());

            } catch (DataLoadException e) {
                message = "Missing allele information for dcc-supplied colony " + specimen.getColonyID() + ". Skipping...";
                logger.error(message);
                throw new DataLoadException(message, e);
            }
        }

        // Get the gene. Mark as error and skip if no gene.
        gene = colony.getGene();
        if (gene == null) {
            message = "Missing gene information for dcc-supplied colony " + specimen.getColonyID() + " for allele " + allele.toString() + ". Skipping...";
            logger.error(message);
            throw new DataLoadException(message);
        }

        // Get the background strain from iMits. EuroPhenome background strains require manual curation/remapping and
        // may be comprised of multiple strains separated by semicolons. Treat any background strains with semicolons
        // as a single strain; do not split them into separate strains.
        // Recap:
        //  - Get background strain from iMits
        //  - Filter the iMits background strain name through the EuroPhenomeStrainMapper
        //  - If the filtered background strain does not exist, create it and add it to the strain table.
        try {
            backgroundStrainName = euroPhenomeStrainMapper.filterEuroPhenomeGeneticBackground(colony.getBackgroundStrainName());
            backgroundStrain = cdaSqlUtils.getStrainByName(backgroundStrainName);
            if (backgroundStrain == null) {
                backgroundStrain = cdaSqlUtils.createAndInsertStrain(backgroundStrainName);
            }

        } catch (DataLoadException e) {

            message = "Insert strain " + colony.getBackgroundStrainName() + " for dcc-supplied colony " + specimen.getColonyID() + " failed. Reason: " + e.getLocalizedMessage() + ". Skipping...";
            logger.error(message);
            throw new DataLoadException(message, e);
        }

        // Get the various components needed for inserting into biological_model, biological_sample, live_sample, and biological_model_sample.
        String colonyId = specimen.getColonyID();
        Date dateOfBirth;
        OntologyTerm developmentalStage;
        String externalId = specimen.getSpecimenID();
        String litterId = specimen.getLitterId();
        OntologyTerm sampleType;

        if (specimen instanceof Mouse) {
            dateOfBirth = ((Mouse) specimen).getDOB().getTime();
            developmentalStage = developmentalStageMouse;
            sampleType = sampleTypeWholeOrganism;

        } else if (specimen instanceof Embryo) {
            dateOfBirth = null;
            String stage = ((Embryo) specimen).getStage().replaceAll("E", "");
            StageUnit stageUnit = ((Embryo) specimen).getStageUnit();
//            developmentalStage = selectOrInsertStageTerm(stage, stageUnit);
//            if (developmentalStage == null) {
//                message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "': Unknown developmental stage '" + stage + "'. Skipping...";
//                logger.error(message);
//                throw new DataLoadException(message);
//            }
            sampleType = sampleTypeMouseEmbryoStage;

        } else {
            message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "': Expected specimen sample class 'Mouse' or 'Embryo' but found '" + specimen.getClass().getCanonicalName() + "'. Skipping...";
            logger.error(message);
            throw new DataLoadException(message);
        }

        String sampleGroup = (specimen.isIsBaseline()) ? "control" : "experimental";

        int phenotypingCenterId = colony.getPhenotypingCentre().getId();

        int productionCenterId = colony.getProductionCentre().getId();

        String sex = specimen.getGender().value();
        try {
            // Remap sex values to consistent values (or throw an exception if there is no match)
            sex = SexType.getByDisplayName(sex).getName();

        } catch (IllegalArgumentException e) {
            message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "' has unknown sex value '" + sex + "'. Skipping...";
            logger.error(message);
            throw new DataLoadException(message);
        }

        String zygosity = null;
        switch (specimen.getZygosity().value()) {
            case "wild type":
            case "homozygous":
                zygosity = ZygosityType.homozygote.getName();
                break;
            case "heterozygous":
                zygosity = ZygosityType.heterozygote.getName();
                break;
            case "hemizygous":
                zygosity = ZygosityType.hemizygote.getName();
                break;

            default:
                message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "': unexpected zygosity '" + specimen.getZygosity().value() + "'. Skipping...";
                logger.error(message);
                throw new DataLoadException(message);
        }


        // Do the table  INSERTs.
        // NOTE: For biological_model, biological_sample, and live_sample, avoid using the hibernate DTOs, as they add a lot of overhead and confusion to an otherwise simple schema.


        // Get the biological model. Create one if it is not found.
        BiologicalModel biologicalModel = cdaSqlUtils.getBiologicalModelByJoins(colony.getGene().getId().getAccession(), allele.getSymbol(), backgroundStrainName);
        if (biologicalModel == null) {
            String allelicComposition = euroPhenomeStrainMapper.createAllelicComposition(zygosity, allele.getSymbol(), gene.getSymbol(), sampleGroup);
            BiologicalModelAggregator biologicalModelAggregator = new BiologicalModelAggregator(
                    allelicComposition,
                    allele.getSymbol(),
                    backgroundStrainName,
                    zygosity,
                    allele.getId().getAccession(),
                    colony.getGene().getId().getAccession(),
                    backgroundStrain.getId().getAccession());
            List<BiologicalModelAggregator> biologicalModelAggregators = new ArrayList<>();
            biologicalModelAggregators.add(biologicalModelAggregator);

            cdaSqlUtils.insertBiologicalModel(biologicalModelAggregators);

            biologicalModel = cdaSqlUtils.getBiologicalModel(allelicComposition, backgroundStrainName);
            if (biologicalModel != null) {
                counts.put("biologicalModel", counts.get("biologicalModel") + 1);
            }
        }

        int biologicalModelId = biologicalModel.getId();

        // biological_sample
        Map<String, Integer> results = cdaSqlUtils.insertBiologicalSample(externalId, externalDbId, sampleType, sampleGroup, phenotypingCenterId, productionCenterId);
        counts.put("biologicalSample", counts.get("biologicalSample") + results.get("count"));
        int biologicalSampleId = results.get("biologicalSampleId");

        // live_sample
//        int liveSampleId = cdaSqlUtils.insertLiveSample(biologicalSampleId, colonyId, dateOfBirth, developmentalStage, litterId, sex, zygosity);
//        if (liveSampleId > 0) {
//            counts.put("liveSample", counts.get("liveSample") + 1);
//        }

        // biological_model_sample
        int biologicalModelSampleId = cdaSqlUtils.insertBiologicalModelSample(biologicalModelId, biologicalSampleId);

        return counts;
    }

    private Strain getBackgroundStrain(Specimen specimen) throws DataLoadException {
        Strain backgroundStrain;
        String backgroundStrainName;
        String message;

        // specimen.strainId can contain an MGI strain accession id in the form "MGI:", or a strain name like C57BL/6N.
        if (specimen.getStrainID().toLowerCase().startsWith("mgi:")) {
            backgroundStrain = cdaSqlUtils.getStrain(specimen.getStrainID());
            if (backgroundStrain == null) {
                throw new DataLoadException("No strain table entry found for strain accession id '" + specimen.getStrainID() + "'");
            }
            backgroundStrainName = backgroundStrain.getName();

        } else {
                backgroundStrainName = specimen.getStrainID();
        }

        try {
            backgroundStrainName = euroPhenomeStrainMapper.filterEuroPhenomeGeneticBackground(backgroundStrainName);
            backgroundStrain = cdaSqlUtils.getStrainByName(backgroundStrainName);
            if (backgroundStrain == null) {
                backgroundStrain = cdaSqlUtils.createAndInsertStrain(backgroundStrainName);
            }

        } catch (DataLoadException e) {

            message = "Insert strain " + specimen.getStrainID() + " failed. Skipping...";
            logger.error(message);
            throw new DataLoadException(message, e);
        }

        return backgroundStrain;
    }

    @Transactional
    private Map<String, Integer> insertSampleControlSpecimen(Specimen specimen) throws DataLoadException {
        String allelicComposition;
        Strain backgroundStrain;
        int biologicalSampleId;
        String colonyId;
        Date dateOfBirth;
        OntologyTerm developmentalStage;
        String externalId;
        String litterId;
        String message;
        int phenotypingCenterId;
        Integer productionCenterId ;        // This int value is optional.
        String sampleGroup;
        OntologyTerm sampleType;
        String sex;
        String zygosity;

        Map<String, Integer> counts = new HashMap<>();
        counts.put("biologicalModel", 0);
        counts.put("biologicalSample", 0);
        counts.put("liveSample", 0);

        // NEED FOR biological_model:
        //      allelicComposition
        //      zygosity
        //      backgroundStrainName
        //      backgroundStrain.getId().getAccession());

        // NEED FOR biological_sample:
        //      externalId
        //      externalDbId
        //      sampleType
        //      sampleGroup
        //      phenotypingCenterId
        //      productionCenterId

        // NEED FOR live_sample:
        //      biologicalSampleId
        //      colonyId
        //      dateOfBirth
        //      developmentalStage
        //      litterId
        //      sex
        //      zygosity

        // Get the various components needed for inserting into biological_model, biological_sample, live_sample, and biological_model_sample.
        allelicComposition = "";
        zygosity = ZygosityType.homozygote.getName();
        backgroundStrain = getBackgroundStrain(specimen);

        externalId = specimen.getSpecimenID();
        sampleType = (specimen instanceof Mouse ? sampleTypeWholeOrganism : sampleTypeMouseEmbryoStage);
        sampleGroup = "control";

        try {
            phenotypingCenterId = loadUtils.translateILAR(jdbcCda, specimen.getPhenotypingCentre().value()).getId();
        } catch (Exception e) {
            logger.error("Exception: unknown phenotyping center '{}' for specimenId {}, colonyId {}. Skipping...", specimen.getPhenotypingCentre().value(), specimen.getSpecimenID(), specimen.getColonyID());
            return counts;
        }

        try {
            productionCenterId = (specimen.getProductionCentre() != null ? loadUtils.translateILAR(jdbcCda, specimen.getProductionCentre().value()).getId() : null);
        } catch (Exception e) {
            logger.error("Exception: unknown production center '{}' for specimenId {}, colonyId {}. Skipping...", specimen.getProductionCentre().value(), specimen.getSpecimenID(), specimen.getColonyID());
            return counts;
        }

        colonyId = specimen.getColonyID();
        if (specimen instanceof Mouse) {
            dateOfBirth = ((Mouse) specimen).getDOB().getTime();
            developmentalStage = developmentalStageMouse;

        } else if (specimen instanceof Embryo) {
            dateOfBirth = null;
            String stage = ((Embryo) specimen).getStage().replaceAll("E", "");
            StageUnit stageUnit = ((Embryo) specimen).getStageUnit();
//            developmentalStage = selectOrInsertStageTerm(stage, stageUnit);
//            if (developmentalStage == null) {
//                message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "': Unknown developmental stage '" + stage + "'. Skipping...";
//                logger.error(message);
//                throw new DataLoadException(message);
//            }
        } else {
            message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "': Expected specimen sample class 'Mouse' or 'Embryo' but found '" + specimen.getClass().getCanonicalName() + "'. Skipping...";
            logger.error(message);
            throw new DataLoadException(message);
        }
        litterId = specimen.getLitterId();
        sex = specimen.getGender().value();
        try {
            // Remap sex values to consistent values (or throw an exception if there is no match)
            sex = SexType.getByDisplayName(sex).getName();
        } catch (IllegalArgumentException e) {

            message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "' has unknown sex value '" + sex + "'. Skipping...";
            logger.error(message);
            throw new DataLoadException(message);
        }


        // Do the table  INSERTs.
        // NOTE: For biological_model, biological_sample, and live_sample, avoid using the hibernate DTOs, as they add a lot of overhead and confusion to an otherwise simple schema.


        // Get the biological model. Create one if it is not found.
        BiologicalModel biologicalModel = cdaSqlUtils.getBiologicalModel(allelicComposition, backgroundStrain.getName());
        if (biologicalModel == null) {
            BiologicalModelAggregator biologicalModelAggregator = new BiologicalModelAggregator(
                    allelicComposition,
                    backgroundStrain.getName(),
                    zygosity,
                    backgroundStrain.getId().getAccession());
            List<BiologicalModelAggregator> biologicalModelAggregators = new ArrayList<>();
            biologicalModelAggregators.add(biologicalModelAggregator);

            cdaSqlUtils.insertBiologicalModel(biologicalModelAggregators);

            biologicalModel = cdaSqlUtils.getBiologicalModel(allelicComposition, backgroundStrain.getName());
            if (biologicalModel == null) {
                throw new DataLoadException("Inserted biological model (" + allelicComposition + ", " + backgroundStrain.getName() + ") but INSERT failed.");
            }

            counts.put("biologicalModel", counts.get("biologicalModel") + 1);
        }

        int biologicalModelId = biologicalModel.getId();

        // biological_sample
        Map<String, Integer> results = cdaSqlUtils.insertBiologicalSample(externalId, externalDbId, sampleType, sampleGroup, phenotypingCenterId, productionCenterId);

        counts.put("biologicalSample", counts.get("biologicalSample") + results.get("count"));
        biologicalSampleId = results.get("biologicalSampleId");

        // live_sample
//        int liveSampleId = cdaSqlUtils.insertLiveSample(biologicalSampleId, colonyId, dateOfBirth, developmentalStage, litterId, sex, zygosity);
//        if (liveSampleId > 0) {
//            counts.put("liveSample", counts.get("liveSample") + 1);
//        }

        // biological_model_sample
        int biologicalModelSampleId = cdaSqlUtils.insertBiologicalModelSample(biologicalModelId, biologicalSampleId);

        return counts;
    }

    public String getExternalDb() {
        return externalDbShortName;
    }

    public void setExternalDb(String externalDbShortName) {
        this.externalDbShortName = externalDbShortName;
        this.externalDbId = cdaSqlUtils.getExternalDbId(externalDbShortName);
    }


    // PRIVATE METHODS



}