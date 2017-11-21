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

package org.mousephenotype.cda.loads.create.load;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.PhenotypedColony;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.common.*;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StageUnit;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Embryo;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Mouse;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads the specimens from a database with a dcc schema into the cda database.
 *
 * Created by mrelac on 15/11/2017.
 *
 */
@ComponentScan
public class LoadSpecimens implements CommandLineRunner {


    private CdaSqlUtils cdaSqlUtils;
    private CommonUtils commonUtils = new CommonUtils();
    private DccSqlUtils dccSqlUtils;

    private NamedParameterJdbcTemplate jdbcCda;

    private Set<String> missingColonyIds = new HashSet<>();
    private Set<String> missingCenters   = new HashSet<>();
    private Set<String> unexpectedStage  = new HashSet<>();

    private final Logger               logger  = LoggerFactory.getLogger(this.getClass());
    private       Map<String, Integer> written = new HashMap<>();

    private OntologyTerm developmentalStageMouse;
    private OntologyTerm sampleTypeMouseEmbryoStage;
    private OntologyTerm sampleTypePostnatalMouse;

    private Map<String, PhenotypedColony> phenotypedColonyMap;

    private int efoDbId;

    private Map<String, Integer>      cdaDb_idMap = new ConcurrentHashMap<>();
    private BioModelManager           bioModelManager;

    public LoadSpecimens(
            NamedParameterJdbcTemplate jdbcCda,
            CdaSqlUtils cdaSqlUtils,
            DccSqlUtils dccSqlUtils
    ) {
        this.jdbcCda = jdbcCda;
        this.cdaSqlUtils = cdaSqlUtils;
        this.dccSqlUtils = dccSqlUtils;

        written.put("biologicalSample", 0);
        written.put("liveSample", 0);
        written.put("controlSample", 0);
        written.put("experimentalSample", 0);
    }


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(LoadSpecimens.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }


    @Override
    public void run(String... strings) throws Exception {

        Assert.notNull(jdbcCda, "jdbcCda must not be null");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must not be null");
        Assert.notNull(dccSqlUtils, "dccSqlUtils must not be null");

        bioModelManager = new BioModelManager(cdaSqlUtils, dccSqlUtils);
        cdaDb_idMap = cdaSqlUtils.getCdaDb_idsByDccDatasourceShortName();
        phenotypedColonyMap = bioModelManager.getPhenotypedColonyMap();

        Assert.notNull(bioModelManager, "bioModelManager must not be null");
        Assert.notNull(cdaDb_idMap, "cdaDb_idMap must not be null");
        Assert.notNull(phenotypedColonyMap, "phenotypedColonyMap must not be null");

        developmentalStageMouse = cdaSqlUtils.getOntologyTermByName("postnatal");
        sampleTypeMouseEmbryoStage = cdaSqlUtils.getOntologyTermByName("mouse embryo stage");
        sampleTypePostnatalMouse = cdaSqlUtils.getOntologyTerm("MA:0002405");                // postnatal mouse
        efoDbId = cdaDb_idMap.get("EFO");

        Assert.notNull(developmentalStageMouse, "developmentalStageMouse must not be null");
        Assert.notNull(sampleTypeMouseEmbryoStage, "xsampleTypeMouseEmbryoStagex must not be null");
        Assert.notNull(sampleTypePostnatalMouse, "sampleTypePostnatalMouse must not be null");
        Assert.notNull(efoDbId, "efoDbId must not be null");

        long startStep = new Date().getTime();


        String message = "**** LOADING " + dccSqlUtils.getDbName() + " SPECIMENS ****";
        logger.info(StringUtils.repeat("*", message.length()));
        logger.info(message);
        logger.info(StringUtils.repeat("*", message.length()));

        List<SpecimenExtended> specimens = dccSqlUtils.getSpecimens();
        Map<String, Integer>   counts;

        for (SpecimenExtended specimenExtended : specimens) {

            Specimen specimen    = specimenExtended.getSpecimen();
            String   sampleGroup = (specimen.isIsBaseline()) ? "control" : "experimental";
            boolean  isControl   = (sampleGroup.equals("control"));
            int dbId;
            String phenotypingCenter;
            Integer phenotypingCenterPk;
            Integer productionCenterPk;

            PhenotypedColony colony = phenotypedColonyMap.get(specimen.getColonyID());
            if (colony == null) {
                if ( ! DccSqlUtils.knownBadColonyIds.contains(specimen.getColonyID())) {
                    missingColonyIds.add(specimen.getColonyID());
                }

                continue;
            }

            /**
             * Some dcc europhenome colonies were incorrectly associated with EuroPhenome. Imits has the authoritative mapping
             * between colonyId and project and, for these incorrect colonies, overrides the dbId to reflect the real owner
             * of the data, MGP.
             */
            RunStatus           status;
            EuroPhenomeRemapper remapper = new EuroPhenomeRemapper(specimenExtended);
            if (remapper.needsRemapping()) {
                status = remapper.remap(phenotypedColonyMap, cdaDb_idMap);
                if (status.hasErrors()) {
                    missingColonyIds.addAll(status.getErrorMessages());
                    continue;
                }
            }

            dbId = cdaDb_idMap.get(specimenExtended.getDatasourceShortName());
            phenotypingCenter = LoadUtils.mappedExternalCenterNames.get(specimen.getPhenotypingCentre().value());
            phenotypingCenterPk = colony.getPhenotypingCentre().getId();
            if (phenotypingCenterPk == null) {
                missingCenters.add("Missing phenotyping center '" + specimen.getPhenotypingCentre().value() + "'");

                continue;
            }
            productionCenterPk = (colony.getProductionCentre() == null
                    ? phenotypingCenterPk
                    : colony.getProductionCentre().getId());

            if (isControl) {
                counts = insertSampleControlSpecimen(specimenExtended, dbId, phenotypingCenterPk, productionCenterPk);
                written.put("controlSample", written.get("controlSample") + 1);
            } else {
                counts = insertSampleExperimentalSpecimen(specimenExtended, dbId, phenotypingCenter, phenotypingCenterPk, productionCenterPk);
                written.put("experimentalSample", written.get("experimentalSample") + 1);
            }

            written.put("biologicalSample", written.get("biologicalSample") + counts.get("biologicalSample"));
            written.put("liveSample", written.get("liveSample") + counts.get("liveSample"));
        }

        for (String missingColonyId : missingColonyIds) {
            logger.warn("Skipping missing phenotyped_colony information for dcc-supplied colony '" + missingColonyId + "'");
        }

        for (String stage : unexpectedStage) {
            logger.info("Unexpected value for embryonic DCP stage: " + stage);
        }

        for (String missingCenter : missingCenters) {
            logger.warn(missingCenter);
        }

        logger.info("Wrote {} new biological samples", written.get("biologicalSample"));
        logger.info("Wrote {} new live samples", written.get("liveSample"));
        logger.info("Processed {} experimental samples", written.get("experimentalSample"));
        logger.info("Processed {} control samples", written.get("controlSample"));
        logger.info("Processed {} total samples", written.get("experimentalSample") + written.get("controlSample"));

        logger.debug("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
    }

    private Map<String, Integer> insertSampleExperimentalSpecimen(SpecimenExtended specimenExtended, int dbId,
                                                                  String phenotypingCenter,
                                                                  int phenotypingCenterPk,
                                                                  int productionCenterPk) throws DataLoadException {
        Specimen specimen = specimenExtended.getSpecimen();

        Map<String, Integer> counts = new HashMap<>();
        counts.put("biologicalSample", 0);
        counts.put("liveSample", 0);

        String       message;
        String       zygosity    = cdaSqlUtils.getSpecimenLevelMutantZygosity(specimen.getZygosity().value());
        String       sampleGroup = (specimen.isIsBaseline()) ? "control" : "experimental";
        Date         dateOfBirth;
        OntologyTerm developmentalStage;
        String       externalId  = specimen.getSpecimenID();
        String       litterId    = specimen.getLitterId();
        OntologyTerm sampleType;

        if (specimen instanceof Mouse) {
            dateOfBirth = ((Mouse) specimen).getDOB().getTime();
            developmentalStage = developmentalStageMouse;
            sampleType = sampleTypePostnatalMouse;

        } else if (specimen instanceof Embryo) {
            dateOfBirth = null;
            String    stage     = ((Embryo) specimen).getStage().replaceAll("E", "");
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
        // NOTE: For biological_sample and live_sample, avoid using the hibernate DTOs, as they add a lot of overhead and confusion to an otherwise simple schema.schema

        // biological_sample
        Map<String, Integer> results = cdaSqlUtils.insertBiologicalSample(externalId, dbId, sampleType, sampleGroup, phenotypingCenterPk, productionCenterPk);
        counts.put("biologicalSample", counts.get("biologicalSample") + results.get("count"));
        int biologicalSampleId = results.get("biologicalSamplePk");

        // live_sample
        int liveSampleId = cdaSqlUtils.insertLiveSample(biologicalSampleId, specimen.getColonyID(), dateOfBirth, developmentalStage, litterId, sex, zygosity);
        if (liveSampleId > 0) {
            counts.put("liveSample", counts.get("liveSample") + 1);
        }

        return counts;
    }

    private Map<String, Integer> insertSampleControlSpecimen(SpecimenExtended specimenExtended,
                                                             int dbId,
                                                             int phenotypingCenterPk,
                                                             int productionCenterPk) throws DataLoadException {

        Specimen specimen = specimenExtended.getSpecimen();

        int          biologicalSamplePk;
        Date         dateOfBirth;
        OntologyTerm developmentalStage;
        String       externalId;
        String       litterId;
        String       message;
        String       sampleGroup;
        OntologyTerm sampleType;
        String       sex;
        String       zygosity;

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

        // Get the various components needed for inserting into biological_model and friends, biological_sample, and live_sample.
        zygosity = ZygosityType.homozygote.getName();

        externalId = specimen.getSpecimenID();
        sampleType = (specimen instanceof Mouse ? sampleTypePostnatalMouse : sampleTypeMouseEmbryoStage);
        sampleGroup = "control";

        if (specimen instanceof Mouse) {
            dateOfBirth = ((Mouse) specimen).getDOB().getTime();
            developmentalStage = developmentalStageMouse;

        } else if (specimen instanceof Embryo) {
            dateOfBirth = null;
            String    stage     = ((Embryo) specimen).getStage().replaceAll("E", "");
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
        Map<String, Integer> results = cdaSqlUtils.insertBiologicalSample(externalId, dbId, sampleType, sampleGroup, phenotypingCenterPk, productionCenterPk);
        counts.put("biologicalSample", counts.get("biologicalSample") + results.get("count"));
        biologicalSamplePk = results.get("biologicalSamplePk");

        // live_sample
        int liveSampleId = cdaSqlUtils.insertLiveSample(biologicalSamplePk, specimen.getColonyID(), dateOfBirth, developmentalStage, litterId, sex, zygosity);
        if (liveSampleId > 0) {
            counts.put("liveSample", counts.get("liveSample") + 1);
        }

        // biological model and friends
        bioModelManager.insert(dbId, specimenExtended);

        return counts;
    }


    // PRIVATE METHODS


    /**
     * Returns the {@link OntologyTerm} associated with the given stage and stageUnit, creating it first if it does
     * not yet exist in the database.
     *
     * @param stage     the stage from impress
     * @param stageUnit the stage unit applicable to stage
     * @return the term associated with the correct stage
     * @throws DataLoadException if {@code stage} is not a floating point number
     */
    private OntologyTerm selectOrInsertStageTerm(String stage, StageUnit stageUnit) throws DataLoadException {
        String       termName = null;
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

                if ( ! expectedDpc.contains(stage)) {
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