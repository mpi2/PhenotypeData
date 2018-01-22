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
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.common.SpecimenExtended;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by mrelac on 09/02/2016.
 * <p/>
 * This class encapsulates the code and data necessary to load the specified target database with the source dcc
 * experiment files currently found at /usr/local/komp2/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the profile containing the application.properties, the source file, and the database name.
 */
@ComponentScan
public class DccExperimentExtractor implements CommandLineRunner {

    private String datasourceShortName;
    private String dbname;
    private String filename;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, Long> specimenIdPhenotypingCenterMap = new HashMap<>();         // key = specimenId_phenotypingCenter. Value = specimenPk.

    // These procedures are always meant to be skipped. Additional skipped parameters passed in on the command line are
    // appended to these.
   	private Set<String> skipProcedures = new HashSet<>(Arrays.asList("SLM_SLM", "SLM_AGS", "TRC_TRC", "DSS_DSS", "MGP_ANA", "MGP_BCI", "MGP_BMI", "MGP_EEI", "MGP_MLN", "MGP_PBI", "MGP_IMM"));

    // Load all 3i data
   	private Set<String> skipThreeIProcedures = new HashSet<>();

    // Required by the Harwell DCC export utilities
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

    private DataSource dccDataSource;
    private DccSqlUtils dccSqlUtils;


    public DccExperimentExtractor(
            DataSource dccDataSource,
            DccSqlUtils dccSqlUtils
    ) {
        this.dccDataSource = dccDataSource;
        this.dccSqlUtils = dccSqlUtils;
    }


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DccExperimentExtractor.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws DataLoadException {

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
            String message = "Missing required command-line parameter 'datasourceShortName'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        datasourceShortName = (String) options.valuesOf("datasourceShortName").get(0);
        filename = (String) options.valuesOf("filename").get(0);

        // If we're loading 3I data, don't filter out the 3I procedures
        if (datasourceShortName.toLowerCase().equals("3i")) {
            skipProcedures = skipThreeIProcedures;
        }

        if (options.has("create")) {
            try {
                dbname = dccDataSource.getConnection().getCatalog();
            } catch (SQLException e) {
                dbname = "Unknown";
            }

            logger.info("Dropping and creating dcc experiment tables for database {} - begin", dbname);
            org.springframework.core.io.Resource r = new ClassPathResource("scripts/dcc/createExperiment.sql");
            ResourceDatabasePopulator            p = new ResourceDatabasePopulator(r);
            p.execute(dccDataSource);
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
            centerProcedures = XMLUtils.unmarshal(DccExperimentExtractor.CONTEXT_PATH, CentreProcedureSet.class, filename).getCentre();
        } catch (Exception e) {
            throw new DataLoadException(e);
        }

        if (centerProcedures.size() == 0) {
            logger.info("experiment file {} is empty.", filename);
            return;
        }

        logger.info("There are {} center procedure sets in experiment file {}", centerProcedures.size(), filename);

        for (CentreProcedure centerProcedure : centerProcedures) {
            logger.info("Parsing experiments for center {}", centerProcedure.getCentreID().value());

            // Load experiment info.
            for (Experiment experiment : centerProcedure.getExperiment()) {

                long centerPk = 0;
                try {
                    // Get centerPk
                    centerPk = dccSqlUtils.getCenterPk(centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
                    if (centerPk < 1) {
                        String center = "<null>";
                        String pipeline = "<null>";
                        String project = "<null>";
                        try { center = centerProcedure.getCentreID().value(); } catch (Exception e) { }
                        try { pipeline = centerProcedure.getPipeline(); } catch (Exception e) { }
                        try { project = centerProcedure.getProject(); } catch (Exception e) { }
                        logger.warn("UNKNOWN CENTER,PIPELINE,PROJECT: '{},{},{}. INSERTING ...", center, pipeline, project);
                        centerPk = dccSqlUtils.insertCenter(centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
                    }

                    insertExperiment(experiment, datasourceShortName, centerProcedure, centerPk);
                    totalExperiments++;

                } catch (Exception e) {
                    logger.error("ERROR IMPORTING EXPERIMENT FROM FILE {}. experimentID: '{}'. datasourceShortName: {}. cenreID: {}, centerPk: {}. EXPERIMENT SKIPPED. ERROR:\n{}" ,
                                 filename, experiment.getExperimentID(), datasourceShortName, centerProcedure.getCentreID().value(), centerPk, e.getLocalizedMessage());
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
            logger.info("Inserted {} experiments from file {}.", totalExperiments, filename);
        }

        if (totalLinesFailed > 0) {
            logger.warn("Inserted {} lines ({} failed).", totalLines, totalLinesFailed);
        } else {
            logger.info("Inserted {} line level experiments from file {}.", totalLines, filename);
        }

    }

    @Transactional
    public void insertExperiment(Experiment experiment, String datasourceShortName, CentreProcedure centerProcedure, long centerPk) throws DataLoadException {

        Long procedurePk, center_procedurePk;

        String procedureName = experiment.getProcedure().getProcedureID();

        // Skip any lines whose procedure group has been marked to be skipped.
        String procedureGroup = getProcedureGroup(procedureName);
        if (skipProcedures.contains(procedureGroup)) {
            logger.info("Skipped excluded Procedure ID {}", procedureName);
            return;
        }

        // procedure
        procedurePk = dccSqlUtils.insertProcedure(experiment.getProcedure().getProcedureID());

        if (experiment.getProcedure().getProcedureMetadata() != null) {
            for (ProcedureMetadata procedureMetadata : experiment.getProcedure().getProcedureMetadata()) {
                insertProcedureMetadata(procedureMetadata, procedurePk);
            }
        }

        // center_procedure
        center_procedurePk = dccSqlUtils.selectOrInsertCenter_procedure(centerPk, procedurePk);

        // experiment
        long experimentPk = dccSqlUtils.insertExperiment(experiment, datasourceShortName, center_procedurePk);
        if (experimentPk == 0) {
            logger.warn("SELECT/INSERT experimentId " + experiment.getExperimentID() + " failed. SKIPPING.");
            return;
        }

        // housing
        if (centerProcedure.getHousing() != null) {
            dccSqlUtils.insertHousing(centerProcedure.getHousing(), center_procedurePk);
        }

        // specimens

        // For EuroPhenome specimens only, the dcc appends the center name to the specimen id. We remove it here
        // because 1)it is not useful, 2)moreso, it would be confusing when the centers tried to look up their specimens
        // and couldn't find them because the specimen ids had the trailing center, and 3)we remove it in the specimen extractor.
        if (datasourceShortName.equals(CdaSqlUtils.EUROPHENOME)) {
            List<String> specimenIds = experiment.getSpecimenID();
            List<String> truncatedSpecimenIds = new ArrayList<>();
            for (String specimenId : specimenIds) {
                if (specimenId != null) {
                    String truncatedSpecimen = specimenId;
                    int    idx               = -1;
                    if (truncatedSpecimen.endsWith("_MRC_Harwell")) {
                        idx = truncatedSpecimen.lastIndexOf(("_MRC_Harwell"));
                    } else {
                        idx = truncatedSpecimen.lastIndexOf("_");
                    }

                    if (idx >= 0) {
                        truncatedSpecimen = truncatedSpecimen.substring(0, idx);
                        specimenId = truncatedSpecimen;
                    }
                    truncatedSpecimenIds.add(specimenId);
                }
            }

            experiment.setSpecimenID(truncatedSpecimenIds);
        }

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
            dccSqlUtils.insertExperiment_specimen(experimentPk, specimenPk);
        }

        // experiment_statuscode
        if (experiment.getStatusCode() != null) {
            for (StatusCode statuscode : experiment.getStatusCode()) {
                statuscode = dccSqlUtils.selectOrInsertStatuscode(statuscode);
                dccSqlUtils.insertExperiment_statuscode(experimentPk, statuscode.getHjid());
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
                insertOntology(ontologyParameter, procedurePk, filename);
            }
        }

        // seriesParameter
        if (experiment.getProcedure().getSeriesParameter() != null) {
            for (SeriesParameter seriesParameter : experiment.getProcedure().getSeriesParameter()) {
                insertSeriesParameter(seriesParameter, procedurePk);
            }
        }
        // mediaParameter
        if (experiment.getProcedure().getMediaParameter() != null) {
            for (MediaParameter mediaParameter : experiment.getProcedure().getMediaParameter()) {
                insertMediaParameter(mediaParameter, procedurePk);
            }

            // mediaSampleParameter
            if (experiment.getProcedure().getMediaSampleParameter() != null) {
                for (MediaSampleParameter mediaSampleParameter : experiment.getProcedure().getMediaSampleParameter()) {
                    insertMediaSampleParameter(mediaSampleParameter, procedurePk);
                }
            }

            // seriesMediaParameter
            if (experiment.getProcedure().getSeriesMediaParameter() != null) {
                for (SeriesMediaParameter seriesMediaParameter : experiment.getProcedure().getSeriesMediaParameter()) {
                    insertSeriesMediaParameter(seriesMediaParameter, procedurePk);
                }
            }
        }
    }

    @Transactional
    public void insertLine(Line line, String datasourceShortName, CentreProcedure centerProcedure, long centerPk) throws DataLoadException {

        Long procedurePk, center_procedurePk;

        // Correct the europhenome specimen colony id
        if (datasourceShortName.equals("EuroPhenome")) {
            line.setColonyID(dccSqlUtils.correctEurophenomeColonyId(line.getColonyID()));
        }

        String procedureName = line.getProcedure().getProcedureID();

        // Skip any lines whose procedure group has been marked to be skipped.
        String procedureGroup = getProcedureGroup(procedureName);
        if (skipProcedures.contains(procedureGroup)) {
            String lineID = String.format("%s-%s", procedureName, line.getColonyID());
            logger.info("Skipped Line ID {} as it contains excluded procedure ID {}", lineID , procedureName);
            return;
        }

        // procedure
        procedurePk = dccSqlUtils.insertProcedure(line.getProcedure().getProcedureID());

        // procedureMetadata
        if (line.getProcedure().getProcedureMetadata() != null) {
            for (ProcedureMetadata procedureMetadata : line.getProcedure().getProcedureMetadata()) {
                insertProcedureMetadata(procedureMetadata, procedurePk);
            }
        }

        // center_procedure
        center_procedurePk = dccSqlUtils.selectOrInsertCenter_procedure(centerPk, procedurePk);

        // housing
        if (centerProcedure.getHousing() != null) {
            dccSqlUtils.insertHousing(centerProcedure.getHousing(), center_procedurePk);
        }

        // line
        long linePk = dccSqlUtils.getLinePk(line.getColonyID(), center_procedurePk);
        if (linePk == 0) {
            linePk = dccSqlUtils.insertLine(line, datasourceShortName, center_procedurePk);
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
                insertOntology(ontologyParameter, procedurePk, filename);
            }
        }

        // seriesParameter and seriesParameterValue
        if (line.getProcedure().getSeriesParameter() != null) {
            for (SeriesParameter seriesParameter : line.getProcedure().getSeriesParameter()) {
                insertSeriesParameter(seriesParameter, procedurePk);
            }
        }
        // mediaParameter
        if (line.getProcedure().getMediaParameter() != null) {
            for (MediaParameter mediaParameter : line.getProcedure().getMediaParameter()) {
                insertMediaParameter(mediaParameter, procedurePk);
            }

            // mediaSampleParameter
            if (line.getProcedure().getMediaSampleParameter() != null) {
                for (MediaSampleParameter mediaSampleParameter : line.getProcedure().getMediaSampleParameter()) {
                    insertMediaSampleParameter(mediaSampleParameter, procedurePk);
                }
            }

            // seriesMediaParameter
            if (line.getProcedure().getSeriesMediaParameter() != null) {
                for (SeriesMediaParameter seriesMediaParameter : line.getProcedure().getSeriesMediaParameter()) {
                    insertSeriesMediaParameter(seriesMediaParameter, procedurePk);
                }
            }
        }
    }


    // PRIVATE METHODS


    private String getProcedureGroup(String procedureName) {
   		String procedureGroup = procedureName.substring(0, procedureName.lastIndexOf('_'));
   		return procedureGroup;
   	}

    private void insertProcedureMetadata(ProcedureMetadata procedureMetadata, long procedurePk) {
        long procedureMetadataPk = dccSqlUtils.insertProcedureMetadata(procedureMetadata);
        dccSqlUtils.insertProcedure_procedureMetadata(procedurePk, procedureMetadataPk);
    }

    private void insertOntology(OntologyParameter ontologyParameter, long procedurePk, String filename) {
        long ontologyParameterPk = dccSqlUtils.insertOntologyParameter(ontologyParameter, procedurePk);
        for (String term : ontologyParameter.getTerm()) {
            dccSqlUtils.insertOntologyParameterTerm(term, ontologyParameterPk, ontologyParameter, filename);
        }
    }

    private void insertSeriesParameter(SeriesParameter seriesParameter, long procedurePk) {
        long seriesParameterPk = dccSqlUtils.insertSeriesParameter(seriesParameter, procedurePk);

        // seriesParameterValue
        for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {
            dccSqlUtils.insertSeriesParameterValue(seriesParameterValue, seriesParameterPk);
        }
    }

    private void insertMediaParameter(MediaParameter mediaParameter, long procedurePk) {
        long mediaParameterPk = dccSqlUtils.insertMediaParameter(mediaParameter, procedurePk);

        // mediaParameter_parameterAssociation
        if (mediaParameter.getParameterAssociation() != null) {
            for (ParameterAssociation parameterAssociation : mediaParameter.getParameterAssociation()) {
                long parameterAssociationPk = dccSqlUtils.insertParameterAssociation(parameterAssociation);
                dccSqlUtils.insertMediaParameter_parameterAssociation(mediaParameterPk, parameterAssociationPk);
            }

            // mediaParameter_procedureMetadata
            if (mediaParameter.getProcedureMetadata() != null) {
                for (ProcedureMetadata procedureMetadata : mediaParameter.getProcedureMetadata()) {
                    long procedureMetadataPk = dccSqlUtils.insertProcedureMetadata(procedureMetadata);
                    dccSqlUtils.insertMediaParameter_procedureMetadata(mediaParameterPk, procedureMetadataPk);
                }
            }
        }
    }

    private void insertMediaSampleParameter(MediaSampleParameter mediaSampleParameter, long procedurePk) {
        long mediaSampleParameterPk = dccSqlUtils.insertMediaSampleParameter(mediaSampleParameter, procedurePk);

        // mediaSample
        for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
            long mediaSamplePk = dccSqlUtils.insertMediaSample(mediaSample, mediaSampleParameterPk);

            // mediaSection
            for (MediaSection mediaSection : mediaSample.getMediaSection()) {
                long mediaSectionPk = dccSqlUtils.insertMediaSection(mediaSection, mediaSamplePk);

                // mediaFile
                for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                    long mediaFilePk = dccSqlUtils.insertMediaFile(mediaFile, mediaSectionPk);

                    // mediaFile_parameterAssociation
                    if (mediaFile.getParameterAssociation() != null) {
                        for (ParameterAssociation parameterAssociation : mediaFile.getParameterAssociation()) {
                            long parameterAssociationPk = dccSqlUtils.insertParameterAssociation(parameterAssociation);
                            dccSqlUtils.insertMediaFile_parameterAssociation(mediaFilePk, parameterAssociationPk);
                        }
                    }

                    // mediaFile_procedureMetadata
                    if (mediaFile.getProcedureMetadata() != null) {
                        for (ProcedureMetadata procedureMetadata : mediaFile.getProcedureMetadata()) {
                            long procedureMetadataPk = dccSqlUtils.insertProcedureMetadata(procedureMetadata);
                            dccSqlUtils.insertMediaFile_procedureMetadata(mediaFilePk, procedureMetadataPk);
                        }
                    }
                }
            }
        }
    }

    private void insertSeriesMediaParameter(SeriesMediaParameter seriesMediaParameter, long procedurePk) {
        long seriesMediaParameterPk = dccSqlUtils.insertSeriesMediaParameter(seriesMediaParameter, procedurePk);

        // seriesMediaParameterValue
        for (SeriesMediaParameterValue seriesMediaParameterValue : seriesMediaParameter.getValue()) {
            long seriesMediaParameterValuePk = dccSqlUtils.insertSeriesMediaParameterValue(seriesMediaParameterValue, seriesMediaParameterPk);

            // seriesMediaParameterValue_parameterAssociation
            if ((seriesMediaParameterValue.getParameterAssociation() != null) && (!seriesMediaParameterValue.getParameterAssociation().isEmpty())) {
                for (ParameterAssociation parameterAssociation : seriesMediaParameterValue.getParameterAssociation()) {
                    long parameterAssociationPk = dccSqlUtils.insertParameterAssociation(parameterAssociation);
                    dccSqlUtils.insertSeriesMediaParameterValue_parameterAssociation(seriesMediaParameterValuePk, parameterAssociationPk);
                }
            }

            // seriesMediaParameterValue_procedureMetadata
            if ((seriesMediaParameterValue.getProcedureMetadata() != null) && (!seriesMediaParameterValue.getProcedureMetadata().isEmpty())) {
                for (ProcedureMetadata procedureMetadata : seriesMediaParameterValue.getProcedureMetadata()) {
                    long procedureMetadataPk = dccSqlUtils.insertProcedureMetadata(procedureMetadata);
                    dccSqlUtils.insertSeriesMediaParameterValue_procedureMetadata(seriesMediaParameterValuePk, procedureMetadataPk);
                }
            }
        }
    }
}