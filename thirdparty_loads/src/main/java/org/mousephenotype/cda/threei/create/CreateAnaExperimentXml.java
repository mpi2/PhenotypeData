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
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
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
import org.mousephenotype.cda.threei.util.AnaExcelReader;

import java.util.*;
import javax.activation.MimetypesFileTypeMap;

/**
 * Created by kolab on 04/10/2017 - based on DccSpecimenExtractor.
 * <p/>
 * This class encapsulates the code and data necessary to convert an XLS (Excel) report provided by Sanger/KCL to the XML format required by DccSpecimenExtractor and DccExperimentExtractor load the specified target database with the source dcc
 * specimen files currently found at /usr/local/komp2/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the profile containing the application.properties, the source file, and the database name.
 */
public class CreateAnaExperimentXml extends Create3iXmls implements CommandLineRunner {

    private String outFilename;
    private String inFilename;

    @Value("${n_ana_rows_per_mouse}")
    private int nRowsPerMouse;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(CreateAnaExperimentXml.class);
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
            String message = "Missing required command-line parameter 'infilename'";
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

        // Get ANA data from Excel file. Current file should have four
        // rows for each mouse.
        AnaExcelReader reader = new AnaExcelReader(inFilename);

        List<Experiment> experiments = new ArrayList<Experiment>();

        // Get column headings
        ArrayList<String> columnHeadings = reader.getColumnHeadings();
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
                String value = rowResult[20];

                if (parameterImpressId.equals("MGP_ANA_001_001") ||
                    parameterImpressId.equals("MGP_ANA_002_001")) {

                    SimpleParameter simpleParameter = new SimpleParameter();
                    simpleParameter.setParameterID(parameterImpressId);
                    simpleParameter.setValue(value);
                    simpleParameters.add(simpleParameter);
                } else if (parameterImpressId.equals("MGP_ANA_003_001") ||
                           parameterImpressId.equals("MGP_ANA_004_001")) {

                    ProcedureMetadata procedureMetadata = new ProcedureMetadata();
                    procedureMetadata.setParameterID(parameterImpressId);
                    procedureMetadata.setValue(value);
                    procedureMetadatas.add(procedureMetadata);
                    
                }

                String imagePath = rowResult[28].equals("NonStringNonNumericValue") ? "" : rowResult[28];
                MimetypesFileTypeMap mimetypes = new MimetypesFileTypeMap();
                if (imageAssignAttempted == false) {
                    ArrayList<SeriesMediaParameterValue> smpvList = 
                        new ArrayList<SeriesMediaParameterValue>();
                    SeriesMediaParameterValue smpv = 
                        new SeriesMediaParameterValue();
                    if (imagePath != null && imagePath.length() > 0 && 
                         !imagePath.equals("#N/A")) {
                        
                        smpv.setIncrementValue("1");
                        smpv.setURI(imagePath);
                        smpv.setFileType(mimetypes.getContentType(imagePath));
                        smpvList.add(smpv);
                    } 

                    SeriesMediaParameter seriesMediaParameter = 
                        new SeriesMediaParameter();
                    seriesMediaParameter.setParameterID("MGP_ANA_005_001");
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
            XMLUtils.marshall(CreateAnaExperimentXml.CONTEXT_PATH, centreProcedureSet, outFilename);
            logger.info("marshalled centreExperiment to {}", outFilename);
        } catch (Exception e) {
            logger.error("Problem marshalling centreExperiment to {}", outFilename);
            throw new DataLoadException(e);
        }
    }
}
