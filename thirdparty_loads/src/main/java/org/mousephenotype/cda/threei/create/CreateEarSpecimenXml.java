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
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;


//import org.mousephenotype.cda.loads.create.extract.dcc.TestSpecimen;
import org.mousephenotype.cda.threei.util.ExcelReader;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.*;

import java.sql.Date;

/**
 * Created by kolab on 04/10/2017 - based on ExtractDccSpecimens.
 * <p/>
 * This class encapsulates the code and data necessary to convert an XLS (Excel) report provided by Sanger/KCL to the XML format required by ExtractDccSpecimens and ExtractDccExperiments load the specified target database with the source dcc
 * specimen files currently found at /usr/local/komp2/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the profile containing the application.properties, the source file, and the database name.
 */
public class CreateEarSpecimenXml extends Create3iXmls implements CommandLineRunner {

    private String outFilename;
    private String inFilename;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(CreateEarSpecimenXml.class);
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
            outFilename = "specimen.impc.xml";
        } else {
            outFilename = (String) options.valuesOf("outfilename").get(0);
        }

        logger.debug("Loading report file {}", inFilename);
    }

    private void run() throws DataLoadException {
        int                  totalSpecimens        = 0;
        int                  totalSpecimenFailures = 0;

        // Get data from Excel file. Current file should have four
        // rows for each mouse.
        ExcelReader reader = new ExcelReader(inFilename);

        List<Specimen> specimens = new ArrayList<Specimen>();

        // Get column headings
        ArrayList<String> columnHeadings = reader.getColumnHeadings();
        int nColumns = reader.getNumberOfColumns();

        // Get row details
        reader.reset();
        while (reader.hasNext()) {
            ArrayList<String[]> rowsForMouse = reader.getRowsForMouse();
            Specimen specimen = null;
            Mouse mouse = new Mouse();
            int nRowsForMouse = rowsForMouse.size();
            if (nRowsForMouse > 0) {
                String[] row = rowsForMouse.get(0);
                String specimenId = "";
                try {
                    specimenId = integerFormat(row[2]);
                    if (specimenId == "") {
                        logger.warn("No SpecimenID for record at row " + 
                            reader.getNumberOfRowsRead() + 
                            "record not processed");
                        continue;
                    }
                    GregorianCalendar dob = gcDate(row[5]);
                    if (null == dob) {
                        logger.warn("Date of birth for mouse with specimen ID " + 
                            specimenId +
                            " could not be formatted to a GregorianCalendar date." +
                            " Record not processed.");
                            continue;
                    }
                    mouse.setDOB(dob);
                    specimen = mouse;

                    specimen.setSpecimenID(specimenId);
                    specimen.setColonyID(row[1]);

                    if (row[8].toLowerCase().equals("true")) {
                        specimen.setIsBaseline(true);
                    } else {
                        specimen.setIsBaseline(false);
                    } // What if it is not set to true or false?

                    // STRAIN_MGI_ID is in col 28 (28-1=27) of ANA EAR sheet,
                    // but col 29 for DSS (called MGI_ID in DSS)
                    specimen.setStrainID(row[27]);
                    //specimen.setStrainID(row[28]);
                    specimen.setGender(Gender.fromValue(row[3].toLowerCase()));

                    String zygosity = row[9].toLowerCase();
                    if (zygosity.equals("wildtype")) {
                        zygosity = "wild type";
                    }
                    specimen.setZygosity(Zygosity.fromValue(zygosity));
                    specimen.setLitterId(integerFormat(row[10]));
                    specimen.setPipeline(row[26]);
                    specimen.setProductionCentre(CentreILARcode.fromValue("Wtsi"));
                    specimen.setPhenotypingCentre(CentreILARcode.fromValue("Wtsi"));
                    specimen.setProject("ToBeLoadedFromIMITS");
                } catch (Exception e) {
                    String message = "Could not process record with " +
                        " specimenID " + specimenId + ". Error message: " +
                        e.getMessage();
                    logger.warn(message);
                    continue;
                }
            }
            specimens.add(specimen);

        }

        // Write out specimen
        CentreSpecimen centreSpecimen = new CentreSpecimen();
        centreSpecimen.setMouseOrEmbryo(specimens);
        List<CentreSpecimen> centreSpecimens = new ArrayList<CentreSpecimen>();
        centreSpecimen.setCentreID(CentreILARcode.fromValue("Wtsi"));
        centreSpecimens.add(centreSpecimen);
        CentreSpecimenSet centreSpecimenSet = new CentreSpecimenSet();
        centreSpecimenSet.setCentre(centreSpecimens);

        try {
            // Save to file
            XMLUtils.marshall(CreateEarSpecimenXml.CONTEXT_PATH, centreSpecimenSet, outFilename);
            logger.info("marshalled centreSpecimen to {}", outFilename);
        } catch (Exception e) {
            logger.error("Problem marshalling centreSpecimen to {}", outFilename);
            throw new DataLoadException(e);
        }

    }
}
