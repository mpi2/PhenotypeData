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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.PhenotypedColony;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.common.SpecimenExtended;
import org.mousephenotype.cda.loads.create.load.support.StrainMapper;
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
import org.springframework.util.Assert;

import java.util.*;

/**
 * Loads the specimens from a database with a dcc schema into the cda database.
 *
 * Created by mrelac on 31/08/2016.
 *
 */
public class SampleLoader implements Step, Tasklet, InitializingBean {


    private CdaSqlUtils                cdaSqlUtils;
    private CommonUtils                commonUtils = new CommonUtils();
    private DccSqlUtils                dccSqlUtils;
    private StrainMapper strainMapper;
    private NamedParameterJdbcTemplate jdbcCda;

    private Set<String> missingColonyIds         = new HashSet<>();
    private Set<String> missingBackgroundStrains = new HashSet<>();
    private Set<String> unexpectedStage          = new HashSet<>();

    private final Logger         logger      = LoggerFactory.getLogger(this.getClass());
    private StepBuilderFactory   stepBuilderFactory;
    private Map<String, Integer> written     = new HashMap<>();

    private OntologyTerm developmentalStageMouse;
    private OntologyTerm sampleTypeMouseEmbryoStage;
    private OntologyTerm sampleTypePostnatalMouse;

    private Map<String, Allele>           allelesBySymbolMap = new HashMap<>();
    private Map<String, Integer>          cdaOrganisation_idMap;
    private Map<String, PhenotypedColony> phenotypedColonyMap;

    private int efoDbId;


    public SampleLoader(NamedParameterJdbcTemplate jdbcCda,
                        StepBuilderFactory stepBuilderFactory,
                        CdaSqlUtils cdaSqlUtils,
                        DccSqlUtils dccSqlUtils,
                        Map<String, Allele> allelesBySymbolMap,
                        Map<String, Integer> cdaOrganisation_idMap,
                        Map<String, PhenotypedColony> phenotypedColonyMap
                        ) {
        this.jdbcCda = jdbcCda;
        this.stepBuilderFactory = stepBuilderFactory;
        this.cdaSqlUtils = cdaSqlUtils;
        this.dccSqlUtils = dccSqlUtils;
        this.cdaOrganisation_idMap = cdaOrganisation_idMap;
        this.phenotypedColonyMap = phenotypedColonyMap;
        this.allelesBySymbolMap = allelesBySymbolMap;

        written.put("biologicalSample", 0);
        written.put("liveSample", 0);
        written.put("controlSample", 0);
        written.put("experimentalSample", 0);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        developmentalStageMouse = cdaSqlUtils.getOntologyTermByName("postnatal");
        sampleTypeMouseEmbryoStage = cdaSqlUtils.getOntologyTermByName("mouse embryo stage");
        sampleTypePostnatalMouse = cdaSqlUtils.getOntologyTerm("MA:0002405");                // postnatal mouse
        this.efoDbId = cdaSqlUtils.getExternalDbId("EFO");

        Assert.notNull(cdaOrganisation_idMap, "cdaOrganisation_idMap must be set");
        Assert.notNull(phenotypedColonyMap, "phenotypedColonyMap must be set");
        Assert.notNull(allelesBySymbolMap, "allelesBySymbolMap must be set");

        Assert.notNull(developmentalStageMouse, "developmentalStageMouse must be set");
        Assert.notNull(sampleTypeMouseEmbryoStage, "xsampleTypeMouseEmbryoStagex must be set");
        Assert.notNull(sampleTypePostnatalMouse, "sampleTypePostnatalMouse must be set");
        Assert.notNull(jdbcCda, "jdbcCda must be set");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must be set");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must be set");
        Assert.notNull(dccSqlUtils, "dccSqlUtils must be set");
        Assert.notNull(efoDbId, "efoDbId must be set");
    }

    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "sampleLoaderStep";
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
        stepBuilderFactory.get("sampleLoaderStep")
                .tasklet(this)
                .build()
                .execute(stepExecution);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();

        String message = "**** LOADING " + dccSqlUtils.getDbName() + " SPECIMENS ****";
        logger.info(StringUtils.repeat("*", message.length()));
        logger.info(message);
        logger.info(StringUtils.repeat("*", message.length()));

        List<SpecimenExtended> specimens = dccSqlUtils.getSpecimens();
        Map<String, Integer>   counts;

        for (SpecimenExtended specimenExtended : specimens) {

            Specimen specimen = specimenExtended.getSpecimen();
            String sampleGroup = (specimen.isIsBaseline()) ? "control" : "experimental";
            boolean isControl = (sampleGroup.equals("control"));

            // NOTE: control specimens don't have unique colony ids, so don't use iMits as they won't be found.

            if (isControl) {
                counts = insertSampleControlSpecimen(specimenExtended);
                written.put("controlSample", written.get("controlSample") + 1);
            } else {
                counts = insertSampleExperimentalSpecimen(specimenExtended);
                written.put("experimentalSample", written.get("experimentalSample") + 1);
            }

            written.put("biologicalSample", written.get("biologicalSample") + counts.get("biologicalSample"));
            written.put("liveSample", written.get("liveSample") + counts.get("liveSample"));
        }

        Iterator<String> missingColonyIdsIt = missingColonyIds.iterator();
        while (missingColonyIdsIt.hasNext()) {
            String colonyId = missingColonyIdsIt.next();
            logger.warn("Skipping missing phenotyped_colony information for dcc-supplied colony '" + colonyId + "'");
        }

        Iterator<String> missingBackgroundStrainsIt = missingBackgroundStrains.iterator();
        while (missingBackgroundStrainsIt.hasNext()) {
            String colonyId = missingBackgroundStrainsIt.next();
            logger.warn("Skipping colonyId " + colonyId + " because background strain is missing");
        }

        Iterator<String> unexpectedStageIt = unexpectedStage.iterator();
        while (unexpectedStageIt.hasNext()) {
            String stage = unexpectedStageIt.next();
            logger.info("Unexpected value for embryonic DCP stage: " + stage);
        }

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

    public Map<String, Integer> insertSampleExperimentalSpecimen(SpecimenExtended specimenExtended) throws DataLoadException {
        Specimen specimen = specimenExtended.getSpecimen();

        int externalDbId = cdaSqlUtils.getExternalDbId(specimenExtended.getDatasourceShortName());

        Map<String, Integer> counts = new HashMap<>();
        counts.put("biologicalSample", 0);
        counts.put("liveSample", 0);

        String message;

        int phenotypingCenterId;
        int productionCenterId;
        PhenotypedColony phenotypedColony = phenotypedColonyMap.get(specimen.getColonyID());
        if (phenotypedColony == null) {
            if ( ! DccSqlUtils.knownBadColonyIds.contains(specimen.getColonyID())) {
                missingColonyIds.add(specimen.getColonyID());
            }
            return counts;
        }

        // EuroPhenome specimens with project 'MGP' in the imits list must get their biological_sample db_id set to the key for MGP.
        if (specimenExtended.getDatasourceShortName().equals(CdaSqlUtils.EUROPHENOME)) {
            if (phenotypedColony.getPhenotypingConsortium().getName().equals(CdaSqlUtils.MGP)) {
                specimenExtended.setDatasourceShortName(CdaSqlUtils.MGP);
                externalDbId = cdaSqlUtils.getExternalDbId(specimenExtended.getDatasourceShortName());                  // Remap MGP projects to MGP for EuroPhenome loads.
            }
        }

        phenotypingCenterId = phenotypedColony.getPhenotypingCentre().getId();
        productionCenterId = phenotypedColony.getProductionCentre().getId();
        String zygosity = cdaSqlUtils.getSpecimenLevelMutantZygosity(specimen.getZygosity().value());
        String sampleGroup = (specimen.isIsBaseline()) ? "control" : "experimental";


        Date dateOfBirth;
        OntologyTerm developmentalStage;
        String externalId = specimen.getSpecimenID();
        String litterId = specimen.getLitterId();
        OntologyTerm sampleType;

        if (specimen instanceof Mouse) {
            dateOfBirth = ((Mouse) specimen).getDOB().getTime();
            developmentalStage = developmentalStageMouse;
            sampleType = sampleTypePostnatalMouse;

        } else if (specimen instanceof Embryo) {
            dateOfBirth = null;
            String stage = ((Embryo) specimen).getStage().replaceAll("E", "");
            StageUnit stageUnit = ((Embryo) specimen).getStageUnit();
            developmentalStage = selectOrInsertStageTerm(stage, stageUnit);
            if (developmentalStage == null) {
                message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "': Unknown developmental stage '" + stage + "'. Skipping...";
                logger.error(message);
                throw new DataLoadException(message);
            }
            sampleType = sampleTypeMouseEmbryoStage;

        } else {
            message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "': Expected specimen sample class 'Mouse' or 'Embryo' but found '" + specimen.getClass().getCanonicalName() + "'. Skipping...";
            logger.error(message);
            throw new DataLoadException(message);
        }

        String sex = specimen.getGender().value();
        try {
            // Remap sex values to consistent values (or throw an exception if there is no match)
            sex = SexType.getByDisplayName(sex).getName();

        } catch (IllegalArgumentException e) {
            message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "' has unknown sex value '" + sex + "'. Skipping...";
            logger.error(message);
            throw new DataLoadException(message);
        }

        // Do the table  INSERTs.
        // NOTE: For biological_sample and live_sample, avoid using the hibernate DTOs, as they add a lot of overhead and confusion to an otherwise simple schema.

        // biological_sample
        Map<String, Integer> results = cdaSqlUtils.insertBiologicalSample(externalId, externalDbId, sampleType, sampleGroup, phenotypingCenterId, productionCenterId);
        counts.put("biologicalSample", counts.get("biologicalSample") + results.get("count"));
        int biologicalSampleId = results.get("biologicalSampleId");

        // live_sample
        int liveSampleId = cdaSqlUtils.insertLiveSample(biologicalSampleId, specimen.getColonyID(), dateOfBirth, developmentalStage, litterId, sex, zygosity);
        if (liveSampleId > 0) {
            counts.put("liveSample", counts.get("liveSample") + 1);
        }

        return counts;
    }

    public Map<String, Integer> insertSampleControlSpecimen(SpecimenExtended specimenExtended) throws DataLoadException {

        Specimen specimen = specimenExtended.getSpecimen();

        int externalDbId = cdaSqlUtils.getExternalDbId(specimenExtended.getDatasourceShortName());

        int biologicalSampleId;
        Date dateOfBirth;
        OntologyTerm developmentalStage;
        String externalId;
        String litterId;
        String message;
        int phenotypingCenterId;        // This is always supplied by the dcc.
        int productionCenterId;         // If this is missing, use the productionCenterId.
        String sampleGroup;
        OntologyTerm sampleType;
        String sex;
        String zygosity;

        Map<String, Integer> counts = new HashMap<>();
        counts.put("biologicalSample", 0);
        counts.put("liveSample", 0);

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

        // Get the various components needed for inserting into biological_sample and live_sample.
        zygosity = ZygosityType.homozygote.getName();

        externalId = specimen.getSpecimenID();
        sampleType = (specimen instanceof Mouse ? sampleTypePostnatalMouse : sampleTypeMouseEmbryoStage);
        sampleGroup = "control";

        phenotypingCenterId = cdaOrganisation_idMap.get(specimen.getPhenotypingCentre().value());
        try {
            productionCenterId = cdaOrganisation_idMap.get(specimen.getProductionCentre().value());     // production center may be null.
        } catch (Exception e) {
            productionCenterId = phenotypingCenterId;
        }

        if (specimen instanceof Mouse) {
            dateOfBirth = ((Mouse) specimen).getDOB().getTime();
            developmentalStage = developmentalStageMouse;

        } else if (specimen instanceof Embryo) {
            dateOfBirth = null;
            String stage = ((Embryo) specimen).getStage().replaceAll("E", "");
            StageUnit stageUnit = ((Embryo) specimen).getStageUnit();
            developmentalStage = selectOrInsertStageTerm(stage, stageUnit);
            if (developmentalStage == null) {
                message = "Specimen ID '" + specimen.getSpecimenID() + "', colony ID '" + specimen.getColonyID() + "': Unknown developmental stage '" + stage + "'. Skipping...";
                logger.error(message);
                throw new DataLoadException(message);
            }

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
        // NOTE: For biological_sample, and live_sample, avoid using the hibernate DTOs, as they add a lot of overhead and confusion to an otherwise simple schema.

        // biological_sample
        Map<String, Integer> results = cdaSqlUtils.insertBiologicalSample(externalId, externalDbId, sampleType, sampleGroup, phenotypingCenterId, productionCenterId);

        counts.put("biologicalSample", counts.get("biologicalSample") + results.get("count"));
        biologicalSampleId = results.get("biologicalSampleId");

        // live_sample
        int liveSampleId = cdaSqlUtils.insertLiveSample(biologicalSampleId, specimen.getColonyID(), dateOfBirth, developmentalStage, litterId, sex, zygosity);
        if (liveSampleId > 0) {
            counts.put("liveSample", counts.get("liveSample") + 1);
        }

        return counts;
    }


    // PRIVATE METHODS


    /**
     * Returns the {@link OntologyTerm} associated with the given stage and stageUnit, creating it first if it does
     * not yet exist in the database.
   	 *
   	 * @param stage the stage from impress
   	 * @param stageUnit the stage unit applicable to stage
   	 * @return the term associated with the correct stage
     * @throws DataLoadException if {@code stage} is not a floating point number
   	 */
   	public OntologyTerm selectOrInsertStageTerm(String stage, StageUnit stageUnit) throws DataLoadException {
   		String termName = null;
   		OntologyTerm term;

        final Set<String> expectedDpc = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("9.5", "12.5", "13.5", "14.5", "15.5", "18.5")));


   		switch (stageUnit) {

   			case DPC:
                Double dStage = commonUtils.tryParseDouble(stage);
                if (dStage == null) {
                    throw new DataLoadException("Stage '" + stage + "' is not a floating point number");
                }

   				// Mouse gestation is between 0 and 20 days, so plus 4 to be safe, else reject
                if (dStage < 0) {
                    throw new DataLoadException("Stage '" + stage + "' is less than 0");
                } else if (dStage > 24) {
                    throw new DataLoadException("Stage '" + stage + "' is greater than the maximum Mouse gestation of 24 days");
   				}

   				termName = String.format("embryonic day %s", stage);

   				if( ! expectedDpc.contains(stage)) {
   					unexpectedStage.add(stage);
   				}
   				break;

   			case THEILER:
                Integer iStage = commonUtils.tryParseInt(stage);
                if (iStage == null) {
                    throw new DataLoadException("Stage '" + stage + "' is not an integer");
                }

   				// Only allow a stage term that makes sense
                if (iStage < 0) {
                    throw new DataLoadException("Stage '" + stage + "' is less than 0");
                } else if (iStage > 28) {
                    throw new DataLoadException("Stage '" + stage + "' is greater than the maximum value of 28");
   				}

   	            termName = String.format("TS%s,embryo", stage);
   				break;

   			default:
   				throw new DataLoadException("Unknown stageUnit '" + stageUnit.value() + "'");
   		}

        if ((termName == null) || (termName.trim().isEmpty())) {
            throw new DataLoadException("termName is null or empty");
        }

        try {
            term = cdaSqlUtils.getOntologyTermByName(termName);
        } catch (Exception e) {
            term = null;
        }

        if (term == null) {
            String termAcc = "NULL-" + DigestUtils.md5Hex(termName).substring(0, 9).toUpperCase();
            term = new OntologyTerm();
            term.setId(new DatasourceEntityId(termAcc, efoDbId));
            term.setDescription(termName);
            term.setName(termName);
            List<OntologyTerm> terms = new ArrayList<>();
            terms.add(term);
            Map<String, Integer> counts = cdaSqlUtils.insertOntologyTerm(terms);
            if (counts.get("terms") == 0) {
                logger.error("Tried to create new embryonic stage term '" + term.getName() + "' but database save failed");
            } else {
                logger.info("Created new embryonic stage term '" + term.getName() + "'");
            }
        }

   		return term;
   	}
}