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

package org.mousephenotype.cda.loads.dataimport.dcc;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.mousephenotype.cda.loads.dataimport.dcc.configs.DccConfigApp;
import org.mousephenotype.cda.loads.dataimport.dcc.support.DccSqlUtils;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by mrelac on 09/02/2016.
 * <p/>
 * This class encapsulates the code and data necessary to load the specified target database with the source dcc
 * specimen files currently found at /usr/local/komp2/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the profile containing the application.properties, the source file, and the database name.
 */
@Import(DccConfigApp.class)
public class ImportSpecimens implements CommandLineRunner {

    private String filename;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    // Required by the Harwell DCC export utilities
    public static final  String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

    @NotNull
    @Autowired
    private DataSource dccDataSource;

    @NotNull
    @Autowired
    private DccSqlUtils dccSqlUtils;


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ImportSpecimens.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        initialize(args);
        run();
    }

    private void initialize(String[] args) {

        OptionParser parser = new OptionParser();

        // parameter to indicate specimen table creation
        parser.accepts("create");

        // parameter to indicate the database name
        parser.accepts("dbname").withRequiredArg().ofType(String.class);

        // parameter to indicate the name of the file to process
        parser.accepts("filename").withRequiredArg().ofType(String.class);

        // parameter to indicate profile (subdirectory of configfiles containing application.properties)
        parser.accepts("profile").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        filename = (String) options.valuesOf("filename").get(0);

        if (options.has("create")) {
            logger.info("Dropping and creating dcc specimen tables - begin");
            org.springframework.core.io.Resource r = new ClassPathResource("scripts/dcc/createSpecimen.sql");
            ResourceDatabasePopulator            p = new ResourceDatabasePopulator(r);
            p.execute(dccDataSource);
            logger.info("Dropping and creating dcc specimen tables - complete");
        }

        logger.info("Loading specimen file {}", filename);
    }

    private void run() throws DataImportException {
        int                  totalSpecimens        = 0;
        int                  totalSpecimenFailures = 0;
        List<CentreSpecimen> centerSpecimens;

        try {
            centerSpecimens = XMLUtils.unmarshal(ImportSpecimens.CONTEXT_PATH, CentreSpecimenSet.class, filename).getCentre();
        } catch (Exception e) {
            throw new DataImportException(e);
        }

        if (centerSpecimens.size() == 0) {
            logger.error("{} failed to unmarshall", filename);
            throw new DataImportException(filename + " failed to unmarshall.", new XMLloadingException());
        }

        logger.debug("There are {} center specimen sets in specimen file {}", centerSpecimens.size(), filename);

        for (CentreSpecimen centerSpecimen : centerSpecimens) {
            logger.debug("Parsing specimens for center {}", centerSpecimen.getCentreID());

            for (Specimen specimen : centerSpecimen.getMouseOrEmbryo()) {
                try {
                    insertSpecimen(specimen, centerSpecimen);
                    totalSpecimens++;
                } catch (Exception e) {
                    logger.error("ERROR IMPORTING SPECIMEN. CENTER: {}. SPECIMEN: {}. SPECIMEN SKIPPED. ERROR:\n{}", centerSpecimen.getCentreID(), specimen, e.getLocalizedMessage());
                    totalSpecimenFailures++;
                }
            }
        }

        // Update the relatedSpecimen.specimen_mine_pk column.
        int relatedSpecimenUpdateCount = dccSqlUtils.updateRelatedSpecimenMinePk();

        if (totalSpecimenFailures > 0) {
            logger.warn("Inserted {} specimens ({} failed). Updated {} related specimens", totalSpecimens, totalSpecimenFailures, relatedSpecimenUpdateCount);
        } else {
            logger.debug("Inserted {} specimens ({} failed). Updated {} related specimens", totalSpecimens, totalSpecimenFailures, relatedSpecimenUpdateCount);
        }
    }

    @Transactional
    private void insertSpecimen(Specimen specimen, CentreSpecimen centerSpecimen) throws DataImportException {

        Long specimenPk;

        // center
        long centerPk = dccSqlUtils.getCenterPk(centerSpecimen.getCentreID().value(), specimen.getPipeline(), specimen.getProject());
        if (centerPk < 1) {
            centerPk = dccSqlUtils.insertCenter(centerSpecimen.getCentreID().value(), specimen.getPipeline(), specimen.getProject());
        }

        // statuscode
        if (specimen.getStatusCode() != null) {
            dccSqlUtils.selectOrInsertStatuscode(specimen.getStatusCode().getValue(), specimen.getStatusCode().getDate());
        }

        // specimen
        Specimen existingSpecimen = dccSqlUtils.getSpecimen(specimen.getSpecimenID(), centerSpecimen.getCentreID().value());
        if (existingSpecimen != null) {
            specimenPk = existingSpecimen.getHjid();
            // Validate that this specimen's info matches the existing one in the database.
            if ( ! specimen.getPipeline().equals(existingSpecimen.getPipeline())) {
                throw new DataImportException("pipeline mismatch (pk " + specimenPk + "). Existing pipeline: '" + specimen.getPipeline() + "'. This pipeline: '" + existingSpecimen.getPipeline() + "'.");
            }
            if ( ! specimen.getGender().value().equals(existingSpecimen.getGender().value())) {
                throw new DataImportException("project mismatch (pk " + specimenPk + "). Existing project: '" + specimen.getProject() + "'. This project: '" + existingSpecimen.getProject() + "'.");
            }
            if ( ! specimen.getGender().value().equals(existingSpecimen.getGender().value())) {
                throw new DataImportException("gender mismatch (pk " + specimenPk + "). Existing gender: '" + specimen.getGender().value() + "'. This gender: '" + existingSpecimen.getGender().value() + "'.");
            }
            if ( specimen.isIsBaseline() != existingSpecimen.isIsBaseline()) {
                throw new DataImportException("isBaseline mismatch (pk " + specimenPk + "). Existing isBaseline: '" + (specimen.isIsBaseline() ? 1 : 0) + "'. This isBaseline: '" + existingSpecimen.isIsBaseline() + "'.");
            }
            if ( ! specimen.getLitterId().equals(existingSpecimen.getLitterId())) {
                throw new DataImportException("litterId mismatch. (pk " + specimenPk + "). Existing litterId: '" + specimen.getLitterId() + "'. This litterId: '" + existingSpecimen.getLitterId() + "'.");
            }
            if ( ! specimen.getPhenotypingCentre().value().equals(existingSpecimen.getPhenotypingCentre().value())) {
                throw new DataImportException("phenotypingCenter mismatch. (pk " + specimenPk + "). Existing phenotypingCenter: '" + specimen.getPhenotypingCentre().value()
                        + "'. This phenotypingCenter: '" + existingSpecimen.getPhenotypingCentre() + "'.");
            }
            if (specimen.getProductionCentre() == null) {
                if (existingSpecimen.getProductionCentre() != null) {
                    throw new DataImportException("productionCenter mismatch. (pk " + specimenPk + "). Existing productionCenter is null. this productionCenter: '" + existingSpecimen.getProductionCentre());
                }
            } else {
                if ( ! specimen.getProductionCentre().value().equals(existingSpecimen.getProductionCentre().value())) {
                    throw new DataImportException("productionCenter mismatch. (pk " + specimenPk + "). Existing productionCenter: '" + specimen.getProductionCentre().value()
                            + "'. This productionCenter: '" + existingSpecimen.getProductionCentre().value() + "'.");
                }
            }
            if ( ! specimen.getStrainID().equals(existingSpecimen.getStrainID())) {
                throw new DataImportException("strainId mismatch. (pk " + specimenPk + "). Existing strainId: '" + specimen.getStrainID() + "'. This strainId: '" + existingSpecimen.getStrainID() + "'.");
            }
            if ( ! specimen.getZygosity().value().equals(existingSpecimen.getZygosity().value())) {
                throw new DataImportException("zygosity mismatch. (pk " + specimenPk + "). Existing zygosity: '" + specimen.getZygosity().value() + "'. This zygosity: '" + existingSpecimen.getZygosity().value() + "'.");
            }
        } else {
            specimen = dccSqlUtils.insertSpecimen(specimen);
            specimenPk = specimen.getHjid();
        }

        // embryo or mouse
        if (specimen instanceof Embryo) {
            dccSqlUtils.insertEmbryo((Embryo) specimen, specimenPk);
        } else  if (specimen instanceof Mouse) {
            dccSqlUtils.insertMouse((Mouse) specimen, specimenPk);
        } else {
            throw new DataImportException("Unknown specimen type '" + specimen.getClass().getSimpleName());
        }

        // genotype
        for (Genotype genotype : specimen.getGenotype()) {
            dccSqlUtils.insertGenotype(genotype, specimenPk);
        }

        // parentalStrain
        for (ParentalStrain parentalStrain : specimen.getParentalStrain()) {
            dccSqlUtils.insertParentalStrain(parentalStrain, specimenPk);
        }

        // chromosomalAlteration
        if ( ! specimen.getChromosomalAlteration().isEmpty()) {
            throw new DataImportException("chromosomalAlteration is not yet supported. Records found!");
        }

        // center_specimen
        dccSqlUtils.insertCenter_specimen(centerPk, specimenPk);

        // relatedSpecimen NOTE: 'specimen_mine_pk cannot be loaded until ALL of the specimen files have been loaded,
        // as the related specimens are not guaranteed to be defined in the same specimen file (and, in fact, are not).
        for (RelatedSpecimen relatedSpecimen : specimen.getRelatedSpecimen()) {
            dccSqlUtils.insertRelatedSpecimen(relatedSpecimen, specimenPk);
        }
    }
}