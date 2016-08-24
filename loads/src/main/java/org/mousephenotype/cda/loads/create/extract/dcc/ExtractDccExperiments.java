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

package org.mousephenotype.cda.loads.create.extract.dcc;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.mousephenotype.cda.loads.create.extract.dcc.config.ExtractDccConfigBeans;
import org.mousephenotype.cda.loads.create.extract.dcc.support.ExtractDccSqlUtils;
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
import java.sql.SQLException;
import java.util.List;

/**
 * Created by mrelac on 09/02/2016.
 * <p/>
 * This class encapsulates the code and data necessary to load the specified target database with the source dcc
 * experiment files currently found at /usr/local/komp2/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the profile containing the application.properties, the source file, and the database name.
 */
@Import( { ExtractDccConfigBeans.class })
public class ExtractDccExperiments implements CommandLineRunner {

    private String dbname;
    private String filename;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    // Required by the Harwell DCC export utilities
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

    @NotNull
    @Autowired

    private DataSource dcc;

    @NotNull
    @Autowired
    private ExtractDccSqlUtils extractDccSqlUtils;


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ExtractDccExperiments.class);
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

        // parameter to indicate the name of the file to process
        parser.accepts("filename").withRequiredArg().ofType(String.class);

        // parameter to indicate profile (subdirectory of configfiles containing application.properties)
        parser.accepts("profile").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        filename = (String) options.valuesOf("filename").get(0);

        if (options.has("create")) {
            try {
                dbname = dcc.getConnection().getCatalog();
            } catch (SQLException e) {
                dbname = "Unknown";
            }

            logger.info("Dropping and creating dcc experiment tables for database {} - begin", dbname);
            org.springframework.core.io.Resource r = new ClassPathResource("scripts/dcc/createExperiment.sql");
            ResourceDatabasePopulator            p = new ResourceDatabasePopulator(r);
            p.execute(dcc);
            logger.info("Dropping and creating dcc experiment tables for database {} - complete", dbname);
        }

        logger.debug("Loading experiment file {}", filename);
    }

    private void run() throws DataImportException {
        int                   totalExperiments       = 0;
        int                   totalExperimentsFailed = 0;
        List<CentreProcedure> centerProcedures;

        try {
            centerProcedures = XMLUtils.unmarshal(ExtractDccExperiments.CONTEXT_PATH, CentreProcedureSet.class, filename).getCentre();
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
                    long centerPk = extractDccSqlUtils.getCenterPk(centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
                    if (centerPk < 1) {
                        logger.warn("UNKNOWN CENTER,PIPELINE,PROJECT: '" + centerProcedure.getCentreID().value() + ","
                                            + centerProcedure.getPipeline() + "," + centerProcedure.getProject() + "'. INSERTING...");
                        centerPk = extractDccSqlUtils.insertCenter(centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
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
            Specimen specimen = extractDccSqlUtils.getSpecimen(specimenId, centerProcedure.getCentreID().value());
            if (specimen == null) {
                logger.warn("UNKNOWN SPECIMEN,CENTER: '" + specimenId + "," + centerProcedure.getCentreID().value() + "'. SKIPPING...");
                return;
            }
            specimenPk = specimen.getHjid();

            // procedure
            Procedure procedure = extractDccSqlUtils.getProcedure(experiment.getProcedure().getProcedureID());
            if (procedure == null) {
                procedure = extractDccSqlUtils.insertProcedure(experiment.getProcedure().getProcedureID());

                if (experiment.getProcedure().getProcedureMetadata() != null) {
                    for (ProcedureMetadata procedureMetadata : experiment.getProcedure().getProcedureMetadata()) {
                        long procedureMetadataPk = extractDccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata.getParameterID(), null).getHjid();
                        extractDccSqlUtils.insertProcedure_procedureMetadata(procedure.getHjid(), procedureMetadataPk);
                    }
                }
            }
            procedurePk = procedure.getHjid();

            // center_procedure
            center_procedurePk = extractDccSqlUtils.selectOrInsertCenter_procedure(centerPk, procedurePk);

            // housing
            if (centerProcedure.getHousing() != null) {
                extractDccSqlUtils.insertHousing(centerProcedure.getHousing(), center_procedurePk);
            }

            // line
            if (centerProcedure.getLine() != null) {
                for (Line line : centerProcedure.getLine()) {
                    long linePk = extractDccSqlUtils.getLinePk(line.getColonyID(), center_procedurePk);
                    if (linePk == 0) {
                        linePk = extractDccSqlUtils.insertLine(line, center_procedurePk);
                    }

                    if (line.getStatusCode() != null) {
                        // line_statuscode
                        for (StatusCode statuscode : line.getStatusCode()) {
                            statuscode = extractDccSqlUtils.selectOrInsertStatuscode(statuscode);
                            extractDccSqlUtils.insertLine_statuscode(linePk, statuscode.getHjid());
                        }
                    }
                }
            }

            // experiment
            long experimentPk = extractDccSqlUtils.selectOrInsertExperiment(experiment, center_procedurePk).getHjid();

            // experiment_specimen
            extractDccSqlUtils.selectOrInsertExperiment_specimen(experimentPk, specimenPk);

            // experiment_statuscode
            if (experiment.getStatusCode() != null) {
                for (StatusCode statuscode : experiment.getStatusCode()) {
                    statuscode = extractDccSqlUtils.selectOrInsertStatuscode(statuscode);
                    extractDccSqlUtils.selectOrInsertExperiment_statuscode(experimentPk, statuscode.getHjid());
                }
            }
            // simpleParameter
            if (experiment.getProcedure().getSimpleParameter() != null) {
                for (SimpleParameter simpleParameter : experiment.getProcedure().getSimpleParameter()) {
                    extractDccSqlUtils.insertSimpleParameter(simpleParameter, procedurePk);
                }
            }

            // ontologyParameter and ontologyParameterTerm
            if (experiment.getProcedure().getOntologyParameter() != null) {
                for (OntologyParameter ontologyParameter : experiment.getProcedure().getOntologyParameter()) {
                    ontologyParameter = extractDccSqlUtils.insertOntologyParameter(ontologyParameter, procedurePk);
                    for (String term : ontologyParameter.getTerm()) {
                        extractDccSqlUtils.insertOntologyParameterTerm(term, ontologyParameter.getHjid());
                    }
                }
            }

            // seriesParameter
            if (experiment.getProcedure().getSeriesParameter() != null) {
                for (SeriesParameter seriesParameter : experiment.getProcedure().getSeriesParameter()) {
                    seriesParameter = extractDccSqlUtils.insertSeriesParameter(seriesParameter, procedurePk);

                    // seriesParameterValue
                    for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                        extractDccSqlUtils.insertSeriesParameterValue(seriesParameterValue, seriesParameter.getHjid());
                    }
                }
            }
            // mediaParameter
            if (experiment.getProcedure().getMediaParameter() != null) {
                for (MediaParameter mediaParameter : experiment.getProcedure().getMediaParameter()) {
                    mediaParameter = extractDccSqlUtils.insertMediaParameter(mediaParameter, procedurePk);

                    // mediaParameter_parameterAssociation
                    if (mediaParameter.getParameterAssociation() != null) {
                        for (ParameterAssociation parameterAssociation : mediaParameter.getParameterAssociation()) {
                            long parameterAssociationPk = extractDccSqlUtils.selectOrInsertParameterAssociation(parameterAssociation).getHjid();
                            extractDccSqlUtils.insertMediaParameter_parameterAssociation(mediaParameter.getHjid(), parameterAssociationPk);
                        }

                        // mediaParameter_procedureMetadata
                        if (mediaParameter.getProcedureMetadata() != null) {
                            for (ProcedureMetadata procedureMetadata : mediaParameter.getProcedureMetadata()) {
                                long procedureMetadataPk = extractDccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata.getParameterID(), null).getHjid();
                                extractDccSqlUtils.insertMediaParameter_procedureMetadata(mediaParameter.getHjid(), procedureMetadataPk);
                            }
                        }
                    }
                }

                // mediaSampleParameter
                if (experiment.getProcedure().getMediaSampleParameter() != null) {
                    for (MediaSampleParameter mediaSampleParameter : experiment.getProcedure().getMediaSampleParameter()) {
                        mediaSampleParameter = extractDccSqlUtils.insertMediaSampleParameter(mediaSampleParameter, procedurePk);
                        long mediaSampleParameterPk = mediaSampleParameter.getHjid();

                        // mediaSample
                        for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
                            mediaSample = extractDccSqlUtils.insertMediaSample(mediaSample, mediaSampleParameterPk);
                            long mediaSamplePk = mediaSample.getHjid();

                            // mediaSection
                            for (MediaSection mediaSection : mediaSample.getMediaSection()) {
                                mediaSection = extractDccSqlUtils.insertMediaSection(mediaSection, mediaSamplePk);
                                long mediaSectionPk = mediaSection.getHjid();

                                // mediaFile
                                for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                                    mediaFile = extractDccSqlUtils.insertMediaFile(mediaFile, mediaSectionPk);
                                    long mediaFilePk = mediaFile.getHjid();

                                    // mediaFile_parameterAssociation
                                    if (mediaFile.getParameterAssociation() != null) {
                                        for (ParameterAssociation parameterAssociation : mediaFile.getParameterAssociation()) {
                                            long parameterAssociationPk = extractDccSqlUtils.selectOrInsertParameterAssociation(parameterAssociation).getHjid();
                                            extractDccSqlUtils.insertMediaFile_parameterAssociation(mediaFilePk, parameterAssociationPk);
                                        }
                                    }

                                    // mediaFile_procedureMetadata
                                    if (mediaFile.getProcedureMetadata() != null) {
                                        for (ProcedureMetadata procedureMetadata : mediaFile.getProcedureMetadata()) {
                                            long procedureMetadataPk = extractDccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata.getParameterID(), null).getHjid();
                                            extractDccSqlUtils.insertMediaFile_procedureMetadata(mediaFilePk, procedureMetadataPk);
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
                        seriesMediaParameter = extractDccSqlUtils.insertSeriesMediaParameter(seriesMediaParameter, procedurePk);
                        long seriesMediaParameterPk = seriesMediaParameter.getHjid();

                        // seriesMediaParameterValue
                        for (SeriesMediaParameterValue seriesMediaParameterValue : seriesMediaParameter.getValue()) {
                            seriesMediaParameterValue = extractDccSqlUtils.insertSeriesMediaParameterValue(seriesMediaParameterValue, seriesMediaParameterPk);
                            ;
                            long seriesMediaParameterValuePk = seriesMediaParameterValue.getHjid();

                            // seriesMediaParameterValue_parameterAssociation
                            if ((seriesMediaParameterValue.getParameterAssociation() != null) && (!seriesMediaParameterValue.getParameterAssociation().isEmpty())) {
                                for (ParameterAssociation parameterAssociation : seriesMediaParameterValue.getParameterAssociation()) {
                                    long parameterAssociationPk = extractDccSqlUtils.selectOrInsertParameterAssociation(parameterAssociation).getHjid();
                                    extractDccSqlUtils.insertSeriesMediaParameterValue_parameterAssociation(seriesMediaParameterValuePk, parameterAssociationPk);
                                }
                            }

                            // seriesMediaParameterValue_procedureMetadata
                            if ((seriesMediaParameterValue.getProcedureMetadata() != null) && (!seriesMediaParameterValue.getProcedureMetadata().isEmpty())) {
                                for (ProcedureMetadata procedureMetadata : seriesMediaParameterValue.getProcedureMetadata()) {
                                    long procedureMetadataPk = extractDccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata.getParameterID(), null).getHjid();
                                    extractDccSqlUtils.insertSeriesMediaParameterValue_procedureMetadata(seriesMediaParameterValuePk, procedureMetadataPk);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}