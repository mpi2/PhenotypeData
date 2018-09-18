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

package org.mousephenotype.cda.threei.create;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
//import org.mousephenotype.cda.loads.create.extract.dcc.config.ExtractDccConfigBeans;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;


//import org.mousephenotype.cda.loads.create.extract.dcc.TestSpecimen;
import org.mousephenotype.cda.threei.util.ExcelReader;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.activation.MimetypesFileTypeMap;
import java.sql.SQLException;
import java.util.*;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;


/**
 * Created by kolab on 30/07/2018 - based on CreateCtlExperimentXml.
 * <p/>
 * This class encapsulates the code and data necessary to convert an XLS (Excel) report provided by Sanger/KCL to the XML format required by ExtractDccSpecimens and ExtractDccExperiments load the specified target database with the source dcc
 * specimen files currently found at /usr/local/komp2/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the profile containing the application.properties, the source file, and the database name.
 */
public class CreateSlmExperimentXml extends Create3iXmls implements CommandLineRunner {

    private String outFilename;
    private String inFilename;

    @Value("${n_slm_rows_per_mouse}")
    private int nRowsPerMouse;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(CreateSlmExperimentXml.class);
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

        OptionParser parser = new OptionParser();

        // parameter to indicate the name of the file to process
        parser.accepts("infilename").withRequiredArg().ofType(String.class);

        // parameter to indicate directory to save specimen and experiment files
        parser.accepts("outfilename").withOptionalArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        if ( ! options.has("infilename")) {
            String message = "Missing required command-line paraemter 'infilename'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        inFilename = (String) options.valuesOf("infilename").get(0);

        if ( ! options.has("outfilename")) {
            outFilename = "experiment.impc.xml";
        } else {
            outFilename = (String) options.valuesOf("outfilename").get(0);
        }

        logger.debug("Loading report file {}", inFilename);
    }

    private void run() throws DataLoadException {
        int                  totalSpecimens        = 0;
        int                  totalSpecimenFailures = 0;

        // Get Ear data from Excel file. Current file should have 113
        // rows for each mouse.
        ExcelReader reader = new ExcelReader(inFilename);

        List<Experiment> experiments = new ArrayList<Experiment>();

        // Get column headings
        ArrayList<String> columnHeadings = reader.getColumnHeadings();
        // For debug purposes print out column names
        int counter=0;
        for (String columnHeading: columnHeadings) {
            logger.info("Column " + counter + " = " + columnHeading);
            counter++;
        }
        int nColumns = reader.getNumberOfColumns();

        // Get row details
        reader.reset();
        while (reader.hasNext()) {
            ArrayList<String[]> rowsForMouse = reader.getRowsForMouse();
            int nRows = rowsForMouse.size();
            String[] row = rowsForMouse.get(0);
            String exptId = integerFormat(row[15]);
            if (exptId == "") {
                logger.warn("No ExperimentID for record at row " + 
                    reader.getNumberOfRowsRead() + 
                    "record not processed");
                continue;
            }
            
            if (nRows != nRowsPerMouse ) {
                logger.warn("Records with ExperimentID have " + nRows +
                    " rows. " + nRowsPerMouse + " rows expected");
            }

            GregorianCalendar exptDate = gcDate(row[12]);
            if (null == exptDate) {
                logger.warn("Experiment date for expt with ID " + exptId +
                    " could not be formatted to a GregorianCalendar date." +
                    " Record not processed.");
                    continue;
            }

            Experiment experiment = new Experiment();
            experiment.setDateOfExperiment(exptDate);
            experiment.setExperimentID("3i_" + exptId);
            List<String> specimenIds = new ArrayList<String>();
            specimenIds.add(integerFormat(row[2]));
            experiment.setSpecimenID(specimenIds);
            Procedure procedure = new Procedure();
            procedure.setProcedureID(row[25]);
           
            List<SimpleParameter> simpleParameters = new ArrayList<SimpleParameter>();
            List<ProcedureMetadata> procedureMetadatas = new ArrayList<ProcedureMetadata>();
            List<SeriesParameter> seriesParameters = new ArrayList<SeriesParameter>();
            List<SeriesParameterValue> spvList = new ArrayList<SeriesParameterValue>();

            for (String[] rowResult : rowsForMouse) {
                String parameterImpressId = rowResult[24];
                String value = rowResult[20].equals("NonStringNonNumericValue") ? "" : rowResult[20];
                // Treat parameter value pairs depending on type
                // I have gone into IMPReSS to determine this
                // but ideally this should be specified in a configuration
                // file
                //
                if (parameterImpressId.equals("SLM_SLM_001_001"))
                {
                    // procedureMetadata
                    ProcedureMetadata procedureMetadata = 
                        new ProcedureMetadata();
                    procedureMetadata.setParameterID(parameterImpressId);
                    procedureMetadata.setValue(value);
                    procedureMetadatas.add(procedureMetadata);
                } else {

                    // Deal with simpleparameters and their units
                    SimpleParameter simpleParameter = new SimpleParameter();
                    simpleParameter.setParameterID(parameterImpressId);
                    simpleParameter.setValue(value);
                    switch (parameterImpressId) {
                        case "SLM_SLM_003_001":
                            // Change the format of the date to:
                            // YYYY-MM-DDThh:mm:ss+time_diff
                            simpleParameter.setValue(toZonedDate(value));
                            simpleParameter.setUnit("days post infection");
                            break;
                        case "SLM_SLM_004_001": case "SLM_SLM_008_001":
                            simpleParameter.setUnit("g");
                            break;
                        case "SLM_SLM_005_001": case "SLM_SLM_009_001":
                        case "SLM_SLM_012_001":
                            simpleParameter.setUnit("Counts per 20 micro litres");
                            break;
                    }
                    simpleParameters.add(simpleParameter);
                }
            }

            //Experiments
            procedure.setSimpleParameter(simpleParameters);
            procedure.setProcedureMetadata(procedureMetadatas);
            experiment.setProcedure(procedure);
            experiments.add(experiment);

        }
        
        // Write out experiment
        CentreProcedure centreProcedure = new CentreProcedure();
        // Need to check project and pipeline are correct with Jeremy
        centreProcedure.setProject("BaSH");
        centreProcedure.setPipeline("SLM_001");
        centreProcedure.setCentreID(CentreILARcode.fromValue("Wtsi"));
        centreProcedure.setExperiment(experiments);

        List<CentreProcedure> centreProcedures = new ArrayList<CentreProcedure>();
        centreProcedures.add(centreProcedure);
        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        centreProcedureSet.setCentre(centreProcedures);
        
        try {
            // Save to file
            XMLUtils.marshall(CreateSlmExperimentXml.CONTEXT_PATH, centreProcedureSet, outFilename);
            logger.info("marshalled centreExperiment to {}", outFilename);
        } catch (Exception e) {
            logger.error("Problem marshalling centreExperiment to {}", outFilename);
            throw new DataLoadException(e);
        }
    }

    // Convert a date to a zoned date
    private String toZonedDate(String d) {
        try {
            DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss XXX");
            DateTimeFormatter outFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            ZonedDateTime dateVal = ZonedDateTime.parse(d + " 00:00:00 +01:00:00",inFormatter);
            return dateVal.format(outFormatter);
        } catch (Exception e) {
           logger.error("Problem converting " + d + " to zoned date" + " using unconverted value");
           return d;
        }
    }
}
