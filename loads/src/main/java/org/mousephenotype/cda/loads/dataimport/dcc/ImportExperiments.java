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
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
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
 * experiment files currently found at /usr/local/komp2/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the profile containing the application.properties, the source file, and the database name.
 */
@Import(DccConfigApp.class)
public class ImportExperiments implements CommandLineRunner {

    private String filename;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    // Required by the Harwell DCC export utilities
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

    @NotNull
    @Autowired
    private DataSource dccDataSource;

    @NotNull
    @Autowired
    private DccSqlUtils dccSqlUtils;


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ImportExperiments.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        initialize(args);
        run();
    }

    private void initialize(String[] args) {

        OptionParser parser = new OptionParser();

        // parameter to indicate experiment table creation
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

            logger.info("Dropping and creating dcc experiment tables - begin");
            org.springframework.core.io.Resource r = new ClassPathResource("scripts/dcc/createExperiment.sql");
            ResourceDatabasePopulator            p = new ResourceDatabasePopulator(r);
            p.execute(dccDataSource);
            logger.info("Dropping and creating dcc experiment tables - complete");
        }

        logger.debug("Loading experiment file {}", filename);
    }

    private void run() throws DataImportException {
        int                   totalExperiments       = 0;
        int                   totalExperimentsFailed = 0;
        List<CentreProcedure> centerProcedures;

        try {
            centerProcedures = XMLUtils.unmarshal(ImportExperiments.CONTEXT_PATH, CentreProcedureSet.class, filename).getCentre();
        } catch (Exception e) {
            throw new DataImportException(e);
        }

        if (centerProcedures.size() == 0) {
            logger.error("{} failed to unmarshall", filename);
            throw new DataImportException(filename + " failed to unmarshall.", new XMLloadingException());
        }

        logger.debug("There are {} center procedure sets in experiment file {}", centerProcedures.size(), filename);

        for (CentreProcedure centerProcedure : centerProcedures) {
            logger.debug("Parsing experiments for center {}", centerProcedure.getCentreID());

            for (Experiment experiment : centerProcedure.getExperiment()) {

                try {
                    // Get centerPk
                    long centerPk = dccSqlUtils.getCenterPk(centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
                    if (centerPk < 1) {
                        logger.warn("UNKNOWN CENTER,PIPELINE,PROJECT: '" + centerProcedure.getCentreID().value() + ","
                                            + centerProcedure.getPipeline() + "," + centerProcedure.getProject() + "'. INSERTING...");
                        centerPk = dccSqlUtils.insertCenter(centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
                    }

                    insertExperiment(experiment, centerProcedure, centerPk);
                    totalExperiments++;
                } catch (Exception e) {
                    logger.error("ERROR IMPORTING EXPERIMENT. CENTER: {}. EXPERIMENT: {}. EXPERIMENT SKIPPED. ERROR:\n{}" , centerProcedure.getCentreID(), experiment, e.getLocalizedMessage());
                    totalExperimentsFailed++;
                }
            }
        }

        if (totalExperimentsFailed > 0) {
            logger.warn("Inserted {} experiments ({} failed).", totalExperiments, totalExperimentsFailed);
        } else {
            logger.debug("Inserted {} experiments ({} failed).", totalExperiments, totalExperimentsFailed);
        }
    }

    @Transactional
    private void insertExperiment(Experiment experiment, CentreProcedure centerProcedure, long centerPk) throws DataImportException {

        Long specimenPk, procedurePk, center_procedurePk;

        for (String specimenId : experiment.getSpecimenID()) {

            // get specimenPk
            Specimen specimen = dccSqlUtils.getSpecimen(specimenId, centerProcedure.getCentreID().value());
            if (specimen == null) {
                logger.warn("UNKNOWN SPECIMEN,CENTER: '" + specimenId + "," + centerProcedure.getCentreID().value() + "'. SKIPPING...");
                return;
            }
            specimenPk = specimen.getHjid();

            // procedure
            Procedure procedure = dccSqlUtils.getProcedure(experiment.getProcedure().getProcedureID());
            if (procedure == null) {
                procedure = dccSqlUtils.insertProcedure(experiment.getProcedure().getProcedureID());

                if (experiment.getProcedure().getProcedureMetadata() != null) {
                    for (ProcedureMetadata procedureMetadata : experiment.getProcedure().getProcedureMetadata()) {
                        long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata.getParameterID(), null).getHjid();
                        dccSqlUtils.insertProcedure_procedureMetadata(procedure.getHjid(), procedureMetadataPk);
                    }
                }
            }
            procedurePk = procedure.getHjid();

            // center_procedure
            center_procedurePk = dccSqlUtils.selectOrInsertCenter_procedure(centerPk, procedurePk);

            // housing
            if (centerProcedure.getHousing() != null) {
                dccSqlUtils.insertHousing(centerProcedure.getHousing(), center_procedurePk);
            }

            // line
            if (centerProcedure.getLine() != null) {
                for (Line line : centerProcedure.getLine()) {
                    long linePk = dccSqlUtils.getLinePk(line.getColonyID(), center_procedurePk);
                    if (linePk == 0) {
                        linePk = dccSqlUtils.insertLine(line, center_procedurePk);
                    }

                    if (line.getStatusCode() != null) {
                        // line_statuscode
                        for (StatusCode statuscode : line.getStatusCode()) {
                            statuscode = dccSqlUtils.selectOrInsertStatuscode(statuscode);
                            dccSqlUtils.insertLine_statuscode(linePk, statuscode.getHjid());
                        }
                    }
                }
            }

            // experiment
            long experimentPk = dccSqlUtils.selectOrInsertExperiment(experiment, center_procedurePk).getHjid();

            // experiment_specimen
            dccSqlUtils.selectOrInsertExperiment_specimen(experimentPk, specimenPk);

            // experiment_statuscode
            if (experiment.getStatusCode() != null) {
                for (StatusCode statuscode : experiment.getStatusCode()) {
                    statuscode = dccSqlUtils.selectOrInsertStatuscode(statuscode);
                    dccSqlUtils.selectOrInsertExperiment_statuscode(experimentPk, statuscode.getHjid());
                }
            }
            // simpleParameter
            if (experiment.getProcedure().getSimpleParameter() != null) {
                for (SimpleParameter simpleParameter : experiment.getProcedure().getSimpleParameter()) {
                    dccSqlUtils.insertSimpleParameter(simpleParameter, procedurePk);
                }
            }

            // ontologyParameter and ontologyParameterTerm
            if (experiment.getProcedure().getOntologyParameter() != null) {
                for (OntologyParameter ontologyParameter : experiment.getProcedure().getOntologyParameter()) {
                    ontologyParameter = dccSqlUtils.insertOntologyParameter(ontologyParameter, procedurePk);
                    for (String term : ontologyParameter.getTerm()) {
                        dccSqlUtils.insertOntologyParameterTerm(term, ontologyParameter.getHjid());
                    }
                }
            }

            // seriesParameter
            if (experiment.getProcedure().getSeriesParameter() != null) {
                for (SeriesParameter seriesParameter : experiment.getProcedure().getSeriesParameter()) {
                    seriesParameter = dccSqlUtils.insertSeriesParameter(seriesParameter, procedurePk);

                    // seriesParameterValue
                    for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                        dccSqlUtils.insertSeriesParameterValue(seriesParameterValue, seriesParameter.getHjid());
                    }
                }
            }
            // mediaParameter
            if (experiment.getProcedure().getMediaParameter() != null) {
                for (MediaParameter mediaParameter : experiment.getProcedure().getMediaParameter()) {
                    mediaParameter = dccSqlUtils.insertMediaParameter(mediaParameter, procedurePk);

                    // mediaParameter_parameterAssociation
                    if (mediaParameter.getParameterAssociation() != null) {
                        for (ParameterAssociation parameterAssociation : mediaParameter.getParameterAssociation()) {
                            long parameterAssociationPk = dccSqlUtils.selectOrInsertParameterAssociation(parameterAssociation).getHjid();
                            dccSqlUtils.insertMediaParameter_parameterAssociation(mediaParameter.getHjid(), parameterAssociationPk);
                        }

                        // mediaParameter_procedureMetadata
                        if (mediaParameter.getProcedureMetadata() != null) {
                            for (ProcedureMetadata procedureMetadata : mediaParameter.getProcedureMetadata()) {
                                long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata.getParameterID(), null).getHjid();
                                dccSqlUtils.insertMediaParameter_procedureMetadata(mediaParameter.getHjid(), procedureMetadataPk);
                            }
                        }
                    }
                }

                // mediaSampleParameter
                if (experiment.getProcedure().getMediaSampleParameter() != null) {
                    for (MediaSampleParameter mediaSampleParameter : experiment.getProcedure().getMediaSampleParameter()) {
                        mediaSampleParameter = dccSqlUtils.insertMediaSampleParameter(mediaSampleParameter, procedurePk);
                        long mediaSampleParameterPk = mediaSampleParameter.getHjid();

                        // mediaSample
                        for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
                            mediaSample = dccSqlUtils.insertMediaSample(mediaSample, mediaSampleParameterPk);
                            long mediaSamplePk = mediaSample.getHjid();

                            // mediaSection
                            for (MediaSection mediaSection : mediaSample.getMediaSection()) {
                                mediaSection = dccSqlUtils.insertMediaSection(mediaSection, mediaSamplePk);
                                long mediaSectionPk = mediaSection.getHjid();

                                // mediaFile
                                for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                                    mediaFile = dccSqlUtils.insertMediaFile(mediaFile, mediaSectionPk);
                                    long mediaFilePk = mediaFile.getHjid();

                                    // mediaFile_parameterAssociation
                                    if (mediaFile.getParameterAssociation() != null) {
                                        for (ParameterAssociation parameterAssociation : mediaFile.getParameterAssociation()) {
                                            long parameterAssociationPk = dccSqlUtils.selectOrInsertParameterAssociation(parameterAssociation).getHjid();
                                            dccSqlUtils.insertMediaFile_parameterAssociation(mediaFilePk, parameterAssociationPk);
                                        }
                                    }

                                    // mediaFile_procedureMetadata
                                    if (mediaFile.getProcedureMetadata() != null) {
                                        for (ProcedureMetadata procedureMetadata : mediaFile.getProcedureMetadata()) {
                                            long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata.getParameterID(), null).getHjid();
                                            dccSqlUtils.insertMediaFile_procedureMetadata(mediaFilePk, procedureMetadataPk);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // seriesMediaParameter
                if (experiment.getProcedure().getSeriesMediaParameter() != null) {
                    for (SeriesMediaParameter seriesMediaParameter : experiment.getProcedure().getSeriesMediaParameter()) {
                        seriesMediaParameter = dccSqlUtils.insertSeriesMediaParameter(seriesMediaParameter, procedurePk);
                        long seriesMediaParameterPk = seriesMediaParameter.getHjid();

                        // seriesMediaParameterValue
                        for (SeriesMediaParameterValue seriesMediaParameterValue : seriesMediaParameter.getValue()) {
                            seriesMediaParameterValue = dccSqlUtils.insertSeriesMediaParameterValue(seriesMediaParameterValue, seriesMediaParameterPk);
                            ;
                            long seriesMediaParameterValuePk = seriesMediaParameterValue.getHjid();

                            // seriesMediaParameterValue_parameterAssociation
                            if ((seriesMediaParameterValue.getParameterAssociation() != null) && (!seriesMediaParameterValue.getParameterAssociation().isEmpty())) {
                                for (ParameterAssociation parameterAssociation : seriesMediaParameterValue.getParameterAssociation()) {
                                    long parameterAssociationPk = dccSqlUtils.selectOrInsertParameterAssociation(parameterAssociation).getHjid();
                                    dccSqlUtils.insertSeriesMediaParameterValue_parameterAssociation(seriesMediaParameterValuePk, parameterAssociationPk);
                                }
                            }

                            // seriesMediaParameterValue_procedureMetadata
                            if ((seriesMediaParameterValue.getProcedureMetadata() != null) && (!seriesMediaParameterValue.getProcedureMetadata().isEmpty())) {
                                for (ProcedureMetadata procedureMetadata : seriesMediaParameterValue.getProcedureMetadata()) {
                                    long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata.getParameterID(), null).getHjid();
                                    dccSqlUtils.insertSeriesMediaParameterValue_procedureMetadata(seriesMediaParameterValuePk, procedureMetadataPk);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}