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
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimen;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.CentreSpecimenSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.Specimen;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 09/02/2016.
 * <p/>
 * This is a special executable utility class that reads all of the specimens in the special file provided and inserts them into
 * a table  in the dcc database called dcc_specimens_aggregated. The special file is provided to this class by the command-line
 * parameter --filename=xxx, where xxx is the fully-qualified name of the file containing the datasourceShortName and directories
 * containing specimen xml files to be loaded. Each directory should point to the specimen xml files. The datasourceShortName is
 * specified in the file by enclosing it in brackets (see example below). The first line of the file should be the bracketed
 * datasourceShortName, followed by all of the specimen directories to be loaded, followed by the next bracketed datasourceShortName
 * and its specimen xml directories.
 *
 * 'filename' used to specify samples on my local computer:
 *
 *     [EuroPhenome]
 *     /Users/mrelac/tmp/phenotype_data/6.0/europhenome/2013-05-20
 *     [3i]
 *     /Users/mrelac/tmp/phenotype_data/6.0/3i
 *     [IMPC]
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/BCM
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/GMC
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/H
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/ICS
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/J
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/KMPC
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/NING
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/RBRC
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/TCP
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/UCD
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/WTSI
 *     /Users/mrelac/tmp/phenotype_data/6.0/impc/H

 */
@ComponentScan
public class DccSpecimenAggregator implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String dbname;

    private ArrayList<Pair> pairs = new ArrayList<>();

    // Required by the Harwell DCC export utilities
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

    private DataSource  dccDataSource;
    private DccSqlUtils dccSqlUtils;

    @Autowired
    NamedParameterJdbcTemplate jdbcDcc;

    public DccSpecimenAggregator(
            DataSource dccDataSource,
            DccSqlUtils dccSqlUtils
    ) {
        this.dccDataSource = dccDataSource;
        this.dccSqlUtils = dccSqlUtils;
    }


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DccSpecimenAggregator.class);
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

        OptionParser parser = new OptionParser();

        parser.allowsUnrecognizedOptions();
        /*
         * Accepts a single file containing alternating rows of:
         *   datasourceShortName
         *   directory containing specimen files (files may be mixed. This code will find all files in each directory with 'specimen' in the name)
         */
        parser.accepts("filename").withRequiredArg().ofType(String.class);

        // parameter to indicate profile (subdirectory of configfiles containing application.properties). The dcc database defined in this file is where specimens will be created/written.
        // NOTE: 'profile' is not required by this code, but it is passed in on the command line and consumed by the XxxConfig classes.
        parser.accepts("profile").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        if ( ! options.has("filename")) {
            String message = "Missing required command-line parameter 'filename'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        String filesToProcess = (String) options.valuesOf("filename").get(0);

        try (BufferedReader br = new BufferedReader(new FileReader(filesToProcess))) {

            String line;
            String datasourceShortName = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("[")) {
                    datasourceShortName = line.replace("[", "").replace("]", "");
                } else {
                    pairs.addAll(getSpecimenFiles(datasourceShortName, line));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            dbname = dccDataSource.getConnection().getCatalog();
        } catch (SQLException e) {
            dbname = "Unknown";
        }

        doDdl();        // Create the table.
    }

    private List<Pair> getSpecimenFiles(String datasourceShortName, String line) {
        List<Pair> results = new ArrayList<>();

        Path path = Paths.get(line);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*specimen*")) {

            for (Path entry : stream) {
                results.add(new Pair(datasourceShortName, entry.toAbsolutePath().toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    private void run() throws DataLoadException {
        int                  totalSpecimens        = 0;
        int                  totalSpecimenFailures = 0;
        List<CentreSpecimen> centerSpecimens;


        for (Pair pair : pairs) {

            String datasourceShortName = pair.datasourceShortName;
            String filename = pair.filename;

            try {
                centerSpecimens = XMLUtils.unmarshal(DccSpecimenAggregator.CONTEXT_PATH, CentreSpecimenSet.class, filename).getCentre();
            } catch (Exception e) {
                throw new DataLoadException(e);
            }

            if (centerSpecimens.size() == 0) {
                logger.error("{} failed to unmarshall", filename);
                throw new DataLoadException(filename + " failed to unmarshall.", new XMLloadingException());
            }

            logger.debug("There are {} center specimen sets in specimen file {}", centerSpecimens.size(), filename);

            for (CentreSpecimen centerSpecimen : centerSpecimens) {
                logger.debug("Parsing specimens for center {}", centerSpecimen.getCentreID());

                for (Specimen specimen : centerSpecimen.getMouseOrEmbryo()) {
                    try {
                        insertSpecimen(specimen, datasourceShortName, filename);
                        totalSpecimens++;
                    } catch (Exception e) {
                        logger.error("ERROR IMPORTING SPECIMEN FROM FILE {}. specimenID: {}. datasourceShortName: {}. centreID: {}. SPECIMEN SKIPPED. ERROR:\n{}",
                                     filename, specimen.getSpecimenID(), datasourceShortName, centerSpecimen.getCentreID(), e.getLocalizedMessage());
                        totalSpecimenFailures++;
                    }
                }
            }
        }


        logger.info("Added " + totalSpecimens + " to the database with " + totalSpecimenFailures + " failures");
    }

    public void insertSpecimen(Specimen specimen, String datasourceShortName, String filename) throws DataLoadException {

        // Correct the europhenome specimen colony id
        if (datasourceShortName.equals("EuroPhenome")) {
            String colonyIdBefore = specimen.getColonyID();
            specimen.setColonyID(dccSqlUtils.correctEurophenomeColonyId(specimen.getColonyID()));
            String colonyIdAfter = specimen.getColonyID();
            if ( ! colonyIdBefore.equals(colonyIdAfter)) {
//                System.out.println("Modified EuroPhenome specimen colonyId from '" + colonyIdBefore + "' to '" + colonyIdAfter + "'");
            }
        }

        // specimen
        // For EuroPhenome specimens only, the dcc appends the center name to the specimen id. We remove it here
        // because 1)it is not useful, and 2)moreso, it would be confusing when the centers tried to look up their specimens
        // and couldn't find them because the specimen ids had the trailing center
        if ((datasourceShortName.equals(CdaSqlUtils.EUROPHENOME)) && (specimen.getSpecimenID() != null)) {
            String truncatedSpecimen = specimen.getSpecimenID();
            int    idx               = -1;
            if (truncatedSpecimen.endsWith("_MRC_Harwell")) {
                idx = truncatedSpecimen.lastIndexOf(("_MRC_Harwell"));
            } else {
                idx = truncatedSpecimen.lastIndexOf("_");
            }

            if (idx >= 0) {
                truncatedSpecimen = truncatedSpecimen.substring(0, idx);
                specimen.setSpecimenID(truncatedSpecimen);
            }
        }

        insertSpecimenDetail(specimen, datasourceShortName, filename);
    }

    private void doDdl() {
        String ddls[] = new String[] {
        "DROP TABLE IF EXISTS dcc_specimens_aggregated;",
        "CREATE TABLE dcc_specimens_aggregated ( " +
             "  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY," +
             "  colonyId VARCHAR(255) DEFAULT NULL," +
             "  datasourceShortName VARCHAR(40) NOT NULL," +
             "  gender VARCHAR(255) NOT NULL," +
             "  isBaseline tinyint(1) NOT NULL," +
             "  litterId VARCHAR(255) NOT NULL," +
             "  phenotypingCenter VARCHAR(255) NOT NULL," +
             "  pipeline VARCHAR(255) NOT NULL," +
             "  productionCenter VARCHAR(255) DEFAULT NULL," +
             "  project VARCHAR(255) NOT NULL," +
             "  specimenId VARCHAR(255) NOT NULL," +
             "  strainId VARCHAR(255) DEFAULT NULL," +
             "  zygosity VARCHAR(255) NOT NULL," +
             "  statuscode_pk INT UNSIGNED DEFAULT NULL," +
             "  filename VARCHAR(1024) DEFAULT NULL," +
             "  KEY colonyIdIndex (colonyId)," +
             "  KEY datasourceShortNameIndex (datasourceShortName)," +
             "  KEY isBaselineIndex (isBaseline)," +
             "  KEY phenotypingCenterIndex (phenotypingCenter)," +
             "  KEY pipelineIndex (pipeline)," +
             "  KEY productionCenterIndex (productionCenter)," +
             "  KEY projectIndex (project)," +
             "  KEY specimenIdIndex (specimenId)," +
             "  KEY strainIdIndex (strainId)," +
             "  KEY zygosityIndex (zygosity)," +
             "  KEY filenameIndex (filename)" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;"};
        Map<String, Object> parameterMap = new HashMap<>();

        for (String ddl : ddls) {
            jdbcDcc.update(ddl, parameterMap);
        }
    }


    private void insertSpecimenDetail(Specimen specimen, String datasourceShortName, String filename) {
        final String insert = "INSERT INTO dcc_specimens_aggregated (" +
                "colonyId, datasourceShortName, gender, isBaseline, litterId, phenotypingCenter, pipeline, productionCenter, project, specimenId, strainId, zygosity, statuscode_pk, filename) VALUES " +
                "(:colonyId, :datasourceShortName, :gender, :isBaseline, :litterId, :phenotypingCenter, :pipeline, :productionCenter, :project, :specimenId, :strainId, :zygosity, :statuscodePk, :filename);";

        Map<String, Object> parameterMap = new HashMap();

        parameterMap.put("colonyId", (specimen.getColonyID() == null ? null : specimen.getColonyID().trim()));
        parameterMap.put("datasourceShortName", datasourceShortName);
        parameterMap.put("gender", (specimen.getGender() == null ? null : specimen.getGender().value()));
        parameterMap.put("isBaseline", specimen.isIsBaseline() ? 1 : 0);
        parameterMap.put("litterId", specimen.getLitterId());
        parameterMap.put("phenotypingCenter", (specimen.getPhenotypingCentre() == null ? null : specimen.getPhenotypingCentre().value()));
        parameterMap.put("pipeline", specimen.getPipeline());
        parameterMap.put("productionCenter", (specimen.getProductionCentre() == null ? null : specimen.getProductionCentre().value()));
        parameterMap.put("project", specimen.getProject());
        parameterMap.put("specimenId", specimen.getSpecimenID());
        parameterMap.put("strainId", specimen.getStrainID());
        parameterMap.put("zygosity", specimen.getZygosity() == null ? null : specimen.getZygosity().value());
        parameterMap.put("statuscodePk", specimen.getStatusCode());
        parameterMap.put("filename", filename);

        KeyHolder          keyholder       = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
        int                count           = jdbcDcc.update(insert, parameterSource, keyholder);
        if (count > 0) {
            long pk = keyholder.getKey().longValue();
            specimen.setHjid(pk);
        }
    }

    private class Pair {
        String datasourceShortName;
        String filename;

        public Pair(String datasourceShortName, String filename) {
            this.datasourceShortName = datasourceShortName;
            this.filename = filename;
        }
    }
}