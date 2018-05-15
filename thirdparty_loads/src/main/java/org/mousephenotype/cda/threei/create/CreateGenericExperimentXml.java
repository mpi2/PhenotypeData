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
import org.mousephenotype.cda.loads.create.extract.dcc.config.ExtractDccConfigBeans;
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

/**
 * Created by kolab on 22/01/2018 - based on CreateAnaExperimentXml.
 * <p/>
 * This class encapsulates the code and data necessary to convert an XLS (Excel) report provided by Sanger/KCL to the XML format required by ExtractDccSpecimens and ExtractDccExperiments load the specified target database with the source dcc
 * specimen files currently found at /usr/local/komp2/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the profile containing the application.properties, the source file, and the database name.
 */
public class CreateGenericExperimentXml extends Create3iXmls implements CommandLineRunner {

    private String outFilename;
    private String inFilename;

    @Value("${n_ear_rows_per_mouse}")
    private int nRowsPerMouse;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(CreateGenericExperimentXml.class);
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
            List<SeriesMediaParameter> seriesMediaParameters = new ArrayList<SeriesMediaParameter>();

            // Since there are multiple rows for each mouse, with each row 
            // same values for location of images, we only want to
            // attempt image assignment for the first row encountered
            boolean imageAssignAttempted = false;
            boolean imageAssigned = false;

            for (String[] rowResult : rowsForMouse) {
                String parameterImpressId = rowResult[24];
                String value = rowResult[20].equals("NonStringNonNumericValue") ? "" : rowResult[20];
                // Treat parameter value pairs depending on type
                // I have gone into IMPReSS to determine this
                // but ideally this should be specified in a configuration
                // file
                switch (parameterImpressId) {
                    // ProcedureMetadata
                    case "MGP_BCI_013_001" : case "MGP_BCI_014_001" : case "MGP_BCI_015_001" :
                        ProcedureMetadata procedureMetadata = 
                            new ProcedureMetadata();
                        procedureMetadata.setParameterID(parameterImpressId);
                        procedureMetadata.setValue(value);
                        procedureMetadatas.add(procedureMetadata);
                        break;
                    
                    // SimpleParameters
                    default:
                        SimpleParameter simpleParameter = new SimpleParameter();
                        simpleParameter.setParameterID(parameterImpressId);
                        simpleParameter.setValue(value);
                        simpleParameters.add(simpleParameter);
                }


                if (imageAssignAttempted == false && nColumns >= 32) {
                    // Ear images are in columns 29-31
                    ArrayList<String> imagePath = new ArrayList<String>();
                    for (int im_no=29; im_no<=31; im_no++) {
                        String tempPath = rowResult[im_no].equals("NonStringNonNumericValue") ? "" : rowResult[im_no];
                        if (tempPath != null && tempPath.length() > 0 && 
                            !tempPath.equals("#N/A")) {
                            imagePath.add(tempPath);
                        }
                    }
                    if (imagePath.size() == 0) {
                        continue;
                    }
                    MimetypesFileTypeMap mimetypes = new MimetypesFileTypeMap();
                    ArrayList<SeriesMediaParameterValue> smpvList = 
                        new ArrayList<SeriesMediaParameterValue>();
                    for (int im_no=0; im_no<imagePath.size(); im_no++) { 
                        SeriesMediaParameterValue smpv = 
                            new SeriesMediaParameterValue();
                        smpv.setIncrementValue(""+(im_no+1));
                        smpv.setURI(imagePath.get(im_no));
                        //smpv.setFileType(mimetypes.getContentType(imagePath.get(im_no)));
                        smpv.setFileType("application/octet-stream");
                        smpvList.add(smpv);
                    } 

                    SeriesMediaParameter seriesMediaParameter = 
                        new SeriesMediaParameter();
                    seriesMediaParameter.setParameterID("MGP_EEI_114_001");
                    if (smpvList.size() > 0) {
                        seriesMediaParameter.setValue(smpvList);
                        seriesMediaParameters.add(seriesMediaParameter);
                        imageAssigned = true;
                    }
                    imageAssignAttempted = true;
                }
            }
            //experiment.setSequenceID("3I_" + row[0]);

            //Experiments
            procedure.setSimpleParameter(simpleParameters);
            procedure.setProcedureMetadata(procedureMetadatas);
            if (imageAssigned) {
                procedure.setSeriesMediaParameter(seriesMediaParameters);
            }
            experiment.setProcedure(procedure);
            experiments.add(experiment);

        }
        
        // Write out experiment
        CentreProcedure centreProcedure = new CentreProcedure();
        centreProcedure.setProject("ToBeLoadedFromIMITS");
        centreProcedure.setPipeline("MGP_001");
        centreProcedure.setCentreID(CentreILARcode.fromValue("Wtsi"));
        centreProcedure.setExperiment(experiments);

        List<CentreProcedure> centreProcedures = new ArrayList<CentreProcedure>();
        centreProcedures.add(centreProcedure);
        CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
        centreProcedureSet.setCentre(centreProcedures);
        
        try {
            // Save to file
            XMLUtils.marshall(CreateGenericExperimentXml.CONTEXT_PATH, centreProcedureSet, outFilename);
            logger.info("marshalled centreExperiment to {}", outFilename);
        } catch (Exception e) {
            logger.error("Problem marshalling centreExperiment to {}", outFilename);
            throw new DataLoadException(e);
        }
    }
}
