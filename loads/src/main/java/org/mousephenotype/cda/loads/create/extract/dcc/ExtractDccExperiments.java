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
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.common.SpecimenExtended;
import org.mousephenotype.cda.loads.create.extract.dcc.config.ExtractDccConfigBeans;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
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
import java.util.*;

/**
 * Created by mrelac on 09/02/2016.
 * <p/>
 * This class encapsulates the code and data necessary to load the specified target database with the source dcc
 * experiment files currently found at /usr/local/komp2/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the profile containing the application.properties, the source file, and the database name.
 */
@Import( { ExtractDccConfigBeans.class })
public class ExtractDccExperiments implements CommandLineRunner {

    private String datasourceShortName;
    private String dbname;
    private String filename;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, Long> specimenIdPhenotypingCenterMap = new HashMap<>();         // key = specimenId_phenotypingCenter. Value = specimenPk.

    // These procedures are always meant to be skipped. Additional skipped parameters passed in on the command line are
    // appended to these.
   	private Set<String> skipProcedures = new HashSet<>(Arrays.asList(
   		"SLM_SLM", "SLM_AGS", "TRC_TRC", "DSS_DSS", "MGP_ANA", "MGP_BCI", "MGP_BMI", "MGP_EEI", "MGP_MLN", "MGP_PBI", "MGP_IMM"));

    // Required by the Harwell DCC export utilities
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

    @NotNull
    @Autowired
    private DataSource dcc;

    @NotNull
    @Autowired
    private DccSqlUtils dccSqlUtils;


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

    private void initialize(String[] args) throws DataLoadException {
        List<SpecimenExtended> specimens = dccSqlUtils.getSpecimens();
        for (SpecimenExtended specimenExtended : specimens) {
            Specimen specimen = specimenExtended.getSpecimen();
            String key = specimen.getSpecimenID() + "_" + specimen.getPhenotypingCentre().value();
            specimenIdPhenotypingCenterMap.put(key, specimen.getHjid());
        }

        OptionParser parser = new OptionParser();

        // parameter to indicate experiment table creation
        parser.accepts("create");

        // parameter to indicate the data source short name (e.g. EuroPhenome, IMPC, 3I, etc)
        parser.accepts("datasourceShortName").withRequiredArg().ofType(String.class);

        // parameter to indicate the name of the file to process
        parser.accepts("filename").withRequiredArg().ofType(String.class);

        // parameter to indicate profile (subdirectory of configfiles containing application.properties)
        parser.accepts("profile").withRequiredArg().ofType(String.class);

        // parameter to indicate procedures to be skipped in experiment files.
        parser.accepts("skip-procedures").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        if ( ! options.has("datasourceShortName")) {
            String message = "Missing required command-line paraemter 'datasourceShortName'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        datasourceShortName = (String) options.valuesOf("datasourceShortName").get(0);
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

        if (options.has("skip-procedures")) {
            String       commaListSkipProcs = (String) options.valuesOf("skip-procedures").get(0);
            List<String> skipProcs          = Arrays.asList(commaListSkipProcs.split(","));
            skipProcedures.addAll(skipProcs);

            logger.info("Skipping procedures {}", StringUtils.join(skipProcedures, ", "));
        }

        logger.debug("Loading experiment file {}", filename);
    }

    private void run() throws DataLoadException {
        int                   totalExperiments       = 0;
        int                   totalExperimentsFailed = 0;
        int                   totalLines             = 0;
        int                   totalLinesFailed       = 0;
        List<CentreProcedure> centerProcedures;

        try {
            centerProcedures = XMLUtils.unmarshal(ExtractDccExperiments.CONTEXT_PATH, CentreProcedureSet.class, filename).getCentre();
        } catch (Exception e) {
            throw new DataLoadException(e);
        }

        if (centerProcedures.size() == 0) {
            logger.error("{} failed to unmarshall", filename);
            throw new DataLoadException(filename + " failed to unmarshall.", new XMLloadingException());
        }

        logger.debug("There are {} center procedure sets in experiment file {}", centerProcedures.size(), filename);

        for (CentreProcedure centerProcedure : centerProcedures) {
            logger.debug("Parsing experiments for center {}", centerProcedure.getCentreID());

            // Load experiment info.
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

            // Load line info.
            for (Line line : centerProcedure.getLine()) {

                try {
                    // Get centerPk
                    long centerPk = dccSqlUtils.getCenterPk(centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
                    if (centerPk < 1) {
                        logger.warn("UNKNOWN CENTER,PIPELINE,PROJECT: '" + centerProcedure.getCentreID().value() + ","
                                            + centerProcedure.getPipeline() + "," + centerProcedure.getProject() + "'. INSERTING...");
                        centerPk = dccSqlUtils.insertCenter(centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
                    }

                    insertLine(line, datasourceShortName, centerProcedure, centerPk);
                    totalLines++;
                } catch (Exception e) {
                    logger.error("ERROR IMPORTING LINE. CENTER: {}. LINE: {}. EXPERIMENT SKIPPED. ERROR:\n{}" , centerProcedure.getCentreID(), line, e.getLocalizedMessage());
                    totalLinesFailed++;
                }
            }
        }

        if (totalExperimentsFailed > 0) {
            logger.warn("Inserted {} experiments ({} failed).", totalExperiments, totalExperimentsFailed);
        } else {
            logger.debug("Inserted {} experiments ({} failed).", totalExperiments, totalExperimentsFailed);
        }

        if (totalLinesFailed > 0) {
            logger.warn("Inserted {} lines ({} failed).", totalLines, totalLinesFailed);
        } else {
            logger.debug("Inserted {} lines ({} failed).", totalLines, totalLinesFailed);
        }
    }

    @Transactional
    private void insertExperiment(Experiment experiment, CentreProcedure centerProcedure, long centerPk) throws DataLoadException {

        Long procedurePk, center_procedurePk;

        // procedure
        String procedureName = experiment.getProcedure().getProcedureID();

        // Skip any lines whose procedure group has been marked to be skipped.
        String procedureGroup = getProcedureGroup(procedureName);
        if (skipProcedures.contains(procedureGroup)) {
            logger.info("Skipped excluded Procedure ID {}", procedureName);
            return;
        }

        Procedure procedure = dccSqlUtils.getProcedure(experiment.getProcedure().getProcedureID());
        if (procedure == null) {
            procedure = dccSqlUtils.insertProcedure(experiment.getProcedure().getProcedureID());
        }
        procedurePk = procedure.getHjid();

        if (experiment.getProcedure().getProcedureMetadata() != null) {
            for (ProcedureMetadata procedureMetadata : experiment.getProcedure().getProcedureMetadata()) {
                long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata).getHjid();
                dccSqlUtils.insertProcedure_procedureMetadata(procedure.getHjid(), procedureMetadataPk);
            }
        }

        // center_procedure
        center_procedurePk = dccSqlUtils.selectOrInsertCenter_procedure(centerPk, procedurePk);

        // experiment
        long experimentPk = dccSqlUtils.selectOrInsertExperiment(experiment, center_procedurePk).getHjid();

        // specimens
        for (String specimenId : experiment.getSpecimenID()) {
            Long specimenPk;

            // get specimenPk
            String key = specimenId + "_" + centerProcedure.getCentreID().value();
            specimenPk = specimenIdPhenotypingCenterMap.get(key);
            if (specimenPk == null) {
                logger.warn("UNKNOWN SPECIMEN,CENTER: '" + specimenId + "," + centerProcedure.getCentreID().value() + "'. SKIPPING...");
                return;
            }

            // experiment_specimen
            dccSqlUtils.selectOrInsertExperiment_specimen(experimentPk, specimenPk);
        }

        // housing
        if (centerProcedure.getHousing() != null) {
            dccSqlUtils.insertHousing(centerProcedure.getHousing(), center_procedurePk);
        }

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
                            long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata).getHjid();
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
                                        long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata).getHjid();
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
                                long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata).getHjid();
                                dccSqlUtils.insertSeriesMediaParameterValue_procedureMetadata(seriesMediaParameterValuePk, procedureMetadataPk);
                            }
                        }
                    }
                }
            }
        }
    }

    @Transactional
    private void insertLine(Line line, String datasourceShortName, CentreProcedure centerProcedure, long centerPk) throws DataLoadException {

        Long procedurePk, center_procedurePk;

        final String colonyId = line.getColonyID();

        // procedure
        String procedureName = line.getProcedure().getProcedureID();

        // Skip any lines whose procedure group has been marked to be skipped.
        String procedureGroup = getProcedureGroup(procedureName);
        if (skipProcedures.contains(procedureGroup)) {
            String lineID = String.format("%s-%s", procedureName, colonyId);
            logger.info("Skipped Line ID {} as it contains excluded procedure ID {}", lineID , procedureName);
            return;
        }

        Procedure procedure = dccSqlUtils.getProcedure(procedureName);
        if (procedure == null) {
            procedure = dccSqlUtils.insertProcedure(line.getProcedure().getProcedureID());

        }
        procedurePk = procedure.getHjid();

        if (line.getProcedure().getProcedureMetadata() != null) {
            for (ProcedureMetadata procedureMetadata : line.getProcedure().getProcedureMetadata()) {
                long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata).getHjid();
                dccSqlUtils.insertProcedure_procedureMetadata(procedure.getHjid(), procedureMetadataPk);
            }
        }

        // center_procedure
        center_procedurePk = dccSqlUtils.selectOrInsertCenter_procedure(centerPk, procedurePk);

        // housing
        if (centerProcedure.getHousing() != null) {
            dccSqlUtils.insertHousing(centerProcedure.getHousing(), center_procedurePk);
        }

        long linePk = dccSqlUtils.getLinePk(line.getColonyID(), center_procedurePk);
        if (linePk == 0) {
            linePk = dccSqlUtils.insertLine(line, datasourceShortName, center_procedurePk);
        }

        if (line.getStatusCode() != null) {
            // line_statuscode
            for (StatusCode statuscode : line.getStatusCode()) {
                statuscode = dccSqlUtils.selectOrInsertStatuscode(statuscode);
                dccSqlUtils.insertLine_statuscode(linePk, statuscode.getHjid());
            }
        }

        // line_statuscode
        if (line.getStatusCode() != null) {
            for (StatusCode statuscode : line.getStatusCode()) {
                statuscode = dccSqlUtils.selectOrInsertStatuscode(statuscode);
                dccSqlUtils.insertLine_statuscode(linePk, statuscode.getHjid());
            }
        }
        // simpleParameter
        if (line.getProcedure().getSimpleParameter() != null) {
            for (SimpleParameter simpleParameter : line.getProcedure().getSimpleParameter()) {
                dccSqlUtils.insertSimpleParameter(simpleParameter, procedurePk);
            }
        }

        // ontologyParameter and ontologyParameterTerm
        if (line.getProcedure().getOntologyParameter() != null) {
            for (OntologyParameter ontologyParameter : line.getProcedure().getOntologyParameter()) {
                ontologyParameter = dccSqlUtils.insertOntologyParameter(ontologyParameter, procedurePk);
                for (String term : ontologyParameter.getTerm()) {
                    dccSqlUtils.insertOntologyParameterTerm(term, ontologyParameter.getHjid());
                }
            }
        }

        // seriesParameter
        if (line.getProcedure().getSeriesParameter() != null) {
            for (SeriesParameter seriesParameter : line.getProcedure().getSeriesParameter()) {
                seriesParameter = dccSqlUtils.insertSeriesParameter(seriesParameter, procedurePk);

                // seriesParameterValue
                for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
                    dccSqlUtils.insertSeriesParameterValue(seriesParameterValue, seriesParameter.getHjid());
                }
            }
        }
        // mediaParameter
        if (line.getProcedure().getMediaParameter() != null) {
            for (MediaParameter mediaParameter : line.getProcedure().getMediaParameter()) {
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
                            long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata).getHjid();
                            dccSqlUtils.insertMediaParameter_procedureMetadata(mediaParameter.getHjid(), procedureMetadataPk);
                        }
                    }
                }
            }

            // mediaSampleParameter
            if (line.getProcedure().getMediaSampleParameter() != null) {
                for (MediaSampleParameter mediaSampleParameter : line.getProcedure().getMediaSampleParameter()) {
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
                                        long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata).getHjid();
                                        dccSqlUtils.insertMediaFile_procedureMetadata(mediaFilePk, procedureMetadataPk);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // seriesMediaParameter
            if (line.getProcedure().getSeriesMediaParameter() != null) {
                for (SeriesMediaParameter seriesMediaParameter : line.getProcedure().getSeriesMediaParameter()) {
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
                                long procedureMetadataPk = dccSqlUtils.selectOrInsertProcedureMetadata(procedureMetadata).getHjid();
                                dccSqlUtils.insertSeriesMediaParameterValue_procedureMetadata(seriesMediaParameterValuePk, procedureMetadataPk);
                            }
                        }
                    }
                }
            }
        }
    }

    public String getProcedureGroup(String procedureName) {
   		String procedureGroup = procedureName.substring(0, procedureName.lastIndexOf('_'));
   		return procedureGroup;
   	}
}