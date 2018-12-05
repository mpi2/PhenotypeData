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

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.activation.MimetypesFileTypeMap;
import java.sql.SQLException;
import java.util.*;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.Integer;
import java.math.BigInteger;

/**
 * Created by kolab on 12/11/2018 - based on CreateSlmAgsExperimentXml
 * <p/>
 * This class encapsulates the code to append details of FCS images to 
 * ExperimentXml files as specified in the JIRA ticket:
 *      https://www.ebi.ac.uk/panda/jira/browse/MPII-3156
 */
public class AddFcsImagesToXml extends Create3iXmls implements CommandLineRunner {

    private String imageDir;
    private String xmlPath;
    private String assay;
    private String outFilename;

    private String fileType = "image/png";
    private String indirBase = "file:///nfs/komp2/web/images/3i/headline_images/";
    private Map <String, List<String> > seriesMediaParameterIDs = 
            new HashMap();

    private Map <String, List<ImageDetails> > imageDetails = new HashMap();

    
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(AddFcsImagesToXml.class);
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

        // path to directory to process
        parser.accepts("imagedir").withRequiredArg().ofType(String.class);

        // path to xmlfile
        parser.accepts("xmlpath").withOptionalArg().ofType(String.class);

        // Assay
        parser.accepts("assay").withOptionalArg().ofType(String.class);

        // Path to save output XML
        parser.accepts("outfilename").withOptionalArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        if ( ! options.has("imagedir")) {
            String message = "Missing required command-line parameter 'imagedir'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        imageDir = (String) options.valuesOf("imagedir").get(0);

        if ( ! options.has("xmlpath")) {
            String message = "Missing required command-line parameter 'xmlpath'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        xmlPath = (String) options.valuesOf("xmlpath").get(0);

        if ( ! options.has("assay")) {
            String message = "Missing required command-line parameter 'assay'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        assay = (String) options.valuesOf("assay").get(0);



        if ( ! options.has("outfilename")) {
            outFilename = "experiment_with_fcs.impc.xml";
        } else {
            outFilename = (String) options.valuesOf("outfilename").get(0);
        }

        logger.debug("Loading XML file {}", xmlPath);

        // Add parameters for each assay type and their procedures
        // The procedures are used to check the correct XML file is
        // being processed
        seriesMediaParameterIDs.put("mln", Arrays.asList("MGP_MLN_206_001","MGP_MLN_001"));
        seriesMediaParameterIDs.put("imm", Arrays.asList("MGP_IMM_233_001","MGP_IMM_001"));
        seriesMediaParameterIDs.put("bmi", Arrays.asList("MGP_BMI_045_001","MGP_BMI_001"));
    }

    private void run() throws DataLoadException {

        // Read in XML file
        List<CentreProcedure> centreProcedures;

        try {
            centreProcedures = XMLUtils.unmarshal(AddFcsImagesToXml.CONTEXT_PATH, CentreProcedureSet.class, xmlPath).getCentre();
        } catch (Exception e) {
            throw new DataLoadException(e);
        }

        if (centreProcedures.size() == 0) {
            logger.warn("experiment file {} is empty.", xmlPath);
            return;
        }

        logger.info("There are {} center procedure sets in experiment file {}", centreProcedures.size(), xmlPath);

        // Check XML file contains the right procedures
        //  - it is sufficient to find one instance of the expected
        //    procedure
        Boolean xmlValidForProcedure = false;
        String currentProcedure = seriesMediaParameterIDs.get(assay).get(1);
        CentreProcedure centreProcedure = centreProcedures.get(0);
        for (Experiment experiment : centreProcedure.getExperiment()) {
            String procedureName = experiment.getProcedure().getProcedureID();
            if (procedureName.contains(currentProcedure)) {
                xmlValidForProcedure = true;
                break;
            }
        }
        if (!xmlValidForProcedure) {
            logger.error("Could not find any elements with procedure {} for assay {} - exiting",
                currentProcedure, assay);
            return;
        }
        
        // Get details of png files from imagedir
        File dir = new File(imageDir);
        Pattern parameter_matcher = Pattern.compile("([A-Z]{3,3}_[A-Z]{3,3}_\\d{3,3}_\\d{3,3})");
        Pattern number_matcher = Pattern.compile("(\\d{6,9})");
        String[] fnames = dir.list();
        
        if (fnames == null) {
            logger.error("Could not get any files from {}", imageDir);
            return;
        }

        for (String fname : fnames) {

            Matcher matcher = number_matcher.matcher(fname);
            if (!matcher.find()){
                logger.warn("Could not find mnumber in {} - not processing",fname);
                continue;
            }
            // Next three lines get mnumber as string with no leading zeros
            String mnumber = matcher.group();
            Integer number = Integer.parseInt(mnumber);
            mnumber = number.toString();

            matcher = parameter_matcher.matcher(fname);
            if (!matcher.find()){
                logger.warn("Could not find parameterID in " + fname + " - not processing");
                continue;
            }
            ImageDetails i = new ImageDetails();
            i.parameterID = matcher.group(); //matcher.group();
            i.uri = this.indirBase + fname;
            i.fileType = this.fileType;

            if (this.imageDetails.containsKey(mnumber)) {
                i.incrementValue = this.imageDetails.get(mnumber).size()+1;
                this.imageDetails.get(mnumber).add(i);
            } else {
                i.incrementValue = 1;
                List<ImageDetails> tempImageDetails = new ArrayList();
                tempImageDetails.add(i);
                this.imageDetails.put(mnumber, tempImageDetails);
            }
        }

        // Create required elements for each image. Would have liked to
        // use a lambda function (this.imageDetails.forEach) to iterate
        // through the FCS imageDetails, but I'm not familiar with the
        // Experiment object, so will use the more straightforward but
        // less efficient option of iterating through the Experiments
        String seriesMediaParameterID = this.seriesMediaParameterIDs.get(assay).get(0);
        for (Experiment experiment : centreProcedure.getExperiment()){
            String mnumber = experiment.getSpecimenID().get(0);
            if (this.imageDetails.containsKey(mnumber)) {
                List<SeriesMediaParameterValue> smpvList = 
                    new ArrayList<SeriesMediaParameterValue>();
                for (ImageDetails imgDetail : imageDetails.get(mnumber)) {
                    SeriesMediaParameterValue smpv = 
                        new SeriesMediaParameterValue();
                    smpv.setIncrementValue(""+imgDetail.incrementValue);
                    smpv.setURI(imgDetail.uri);
                    smpv.setFileType(imgDetail.fileType);

                    List<ParameterAssociation> smpaList = 
                        new ArrayList<ParameterAssociation>();
                    ParameterAssociation smpa = new ParameterAssociation();
                    smpa.setParameterID(imgDetail.parameterID);
                    smpa.setSequenceID(BigInteger.valueOf(imgDetail.incrementValue));
                    smpaList.add(smpa);

                    smpv.setParameterAssociation(smpaList);
                    smpvList.add(smpv);
                }



                SeriesMediaParameter seriesMediaParameter = 
                    new SeriesMediaParameter();
                seriesMediaParameter.setParameterID(seriesMediaParameterID);
                seriesMediaParameter.setValue(smpvList);
                Procedure procedure = experiment.getProcedure();
                List<SeriesMediaParameter> seriesMediaParameters = 
                    procedure.getSeriesMediaParameter();
                if (seriesMediaParameters == null) {
                    seriesMediaParameters = new ArrayList<SeriesMediaParameter>();
                }
                seriesMediaParameters.add(seriesMediaParameter);
                procedure.setSeriesMediaParameter(seriesMediaParameters);

            } else {
                System.out.println("NO FCS images for " + mnumber);
            }
        }
        try {
            // Save to file
            CentreProcedureSet centreProcedureSet = new CentreProcedureSet();
            centreProcedureSet.setCentre(centreProcedures);
            XMLUtils.marshall(AddFcsImagesToXml.CONTEXT_PATH, centreProcedureSet, outFilename);
            logger.info("marshalled centreExperiment to {}", outFilename);
        } catch (Exception e) {
            logger.error("Problem marshalling centreExperiment to {}", outFilename);
            throw new DataLoadException(e);
        }
    }

    private class ImageDetails {
        public int incrementValue;
        public String uri;
        public String parameterID;
        public String fileType;
    }
}
