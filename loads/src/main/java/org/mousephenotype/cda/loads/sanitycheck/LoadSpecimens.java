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

package org.mousephenotype.cda.loads.sanitycheck;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.List;

/**
 * Created by mrelac on 09/02/2016.
 * <p/>
 * This class encapsulates the code and data necessary to load the specified target database with the source dcc report
 * files currently found on ebi-00x at /nfs/komp2/web/phenotype_data/impc. This class is meant to be an executable jar
 * whose parameters describe the source location and the target database.
 */
@Component
public class LoadSpecimens {
    /**
     * Load specimen data that was encoded using the IMPC XML format
     */

    // Required by the Harwell DCC export utilities
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    private static final Logger logger = LoggerFactory.getLogger(LoadSpecimens.class);

    @NotNull
    @Autowired
    @Qualifier("komp2url")
    protected String komp2Url;

    @NotNull
    @Autowired
    @Qualifier("username")
    protected String username;

    @NotNull
    @Autowired
    @Qualifier("password")
    protected String password;

    private String context;
    private String filename;
    private String dbrootname;
    private boolean force = false;

    protected ApplicationContext applicationContext;

    private Connection connection;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, XMLloadingException, KeyManagementException, SQLException, JAXBException {

        // Wire up spring support for this application
        LoadSpecimens main = new LoadSpecimens();
        main.initialize(args);
        main.run();

        logger.info("Process finished.  Exiting.");

    }

    private void initialize(String[] args)
            throws IOException, SQLException, KeyManagementException, NoSuchAlgorithmException {

        OptionParser parser = new OptionParser();

        // parameter to indicate the name of the file to process
        parser.accepts("filename").withRequiredArg().ofType(String.class);

        // parameter to indicate the application context xml file.
        parser.accepts("context").withRequiredArg().ofType(String.class);

        // parameter to indicate the base directory (the one in the format "yyyy-mm-dd".
        // Typically mounted on /nfs/komp2/web/phenotype_data/impc.
        parser.accepts("dbrootname").withRequiredArg().ofType(String.class);

        // parameter to indicate whether or not to create the tables.
        parser.accepts("createtables").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);
        dbrootname = (String) options.valuesOf("dbrootname").get(0);

        if (options.has("createtables")) {
            String forceString = (String) options.valuesOf("createtables").get(0).toString().toLowerCase().trim();
            if ((forceString.equals("true") || (forceString.equals("1")))) {
                force = true;
            }
        }

        context = (String) options.valuesOf("context").get(0);
        filename = (String) options.valuesOf("filename").get(0);
        logger.info("Loading specimens file {}", filename);

        applicationContext = loadApplicationContext((String)options.valuesOf("context").get(0));
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

        // Wire up spring support for this application
        ApplicationContext applicationContext;
        String context = (String) options.valuesOf("context").get(0);
        logger.info("Using application context file {}", context);
        if (new File(context).exists()) {
            applicationContext = new FileSystemXmlApplicationContext("file:" + context);
        } else {
            applicationContext = new ClassPathXmlApplicationContext(context);
        }

        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

        String dbName = "dccimport2_" + dbrootname.replaceAll("-", "_");
        String dbUrl = komp2Url.replace("komp2", dbName);

        connection = DriverManager.getConnection(dbUrl, username, password);
        System.out.println("connection = " + connection);
    }

    private void run() throws JAXBException, XMLloadingException, IOException, SQLException, KeyManagementException, NoSuchAlgorithmException {

        List<CentreSpecimen> centers = XMLUtils.unmarshal(LoadSpecimens.CONTEXT_PATH, CentreSpecimenSet.class, filename).getCentre();

        if (centers.size() == 0) {
            logger.error("{} failed to unmarshall", filename);
            throw new XMLloadingException(filename + " failed to unserialize.");
        }

        logger.info("Specimen files has {} specimen sets", centers.size());

        PreparedStatement ps;
        String query;
        for (CentreSpecimen center : centers) {
            logger.info("Parsing centre {}", center.getCentreID());
            connection.setAutoCommit(false);    // BEGIN TRANSACTION

            // Insert into CENTRESPECIMEN
            query = "INSERT INTO CENTRESPECIMEN ("
                    + "HJID, CENTREID)"
                    + " VALUES (?, ?)";
            ps = connection.prepareStatement(query);
            ps.setLong(1, center.getHjid());
            ps.setString(2, center.getCentreID().value());
            ps.execute();

//            for (Specimen specimen : center.getMouseOrEmbryo()) {
//                query = "INSERT INTO SPECIMEN ("
//                + "HJID, COLONYID, GENDER, ISBASELINE, LITTERID, PHENOTYPINGCENTRE, PIPELINE,"
//                + " PRODUCTIONCENTRE, PROJECT, SPECIMENID, STRAINID, ZYGOSITY"
//                + ") VALUES ("
//                        + "? ? ? ? ? ? ? ? ? ? ? ? ? ?)";
//                ps = connection.prepareStatement(query);
//                ps.setLong(1, specimen.getHjid());
//                ps.setString(2, specimen.getColonyID());
//                ps.setString(3, specimen.getGender().value());
//                ps.setInt(4, specimen.isIsBaseline() ? 1 : 0);
//                ps.setString(5, specimen.getLitterId());
//                ps.setString(6, specimen.getPhenotypingCentre().value());
//                ps.setString(7, specimen.getPipeline());
//                ps.setString(8, specimen.getProductionCentre().value());
//                ps.setString(9, specimen.getProject());
//                ps.setString(10, specimen.getSpecimenID());
//                ps.setString(11, specimen.getStrainID());
//                ps.setString(12, specimen.getZygosity().value());
//            }

            connection.commit();
        }

        connection.close();
    }

    protected ApplicationContext loadApplicationContext(String context) {
        ApplicationContext appContext;

        // Try context as a file resource.
        File file = new File(context);
        if (file.exists()) {
            // Try context as a file resource
            appContext = new FileSystemXmlApplicationContext("file:" + context);
        } else {
            // Try context as a class path resource
            appContext = new ClassPathXmlApplicationContext(context);
        }

        if (appContext == null) {
            logger.error("Unable to load context '" + context  + "' from file or classpath. Exiting...");
        }

        return appContext;
    }
}
