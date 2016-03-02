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

import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Gender;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Zygosity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.sql.*;
import java.util.GregorianCalendar;

/**
 * Created by mrelac on 02/03/2016.
 */
public class LoaderUtils {

    private static final Logger logger = LoggerFactory.getLogger(LoaderUtils.class);

    public static String dumpSpecimen(Connection connection, long centerPk, long specimenPk) {
        String retVal = "";

        String query =
                "SELECT\n"
                        + "  cs.pk AS cs_pk\n"
                        + ", c.pk AS c_pk\n"
                        + ", s.pk AS s_pk\n"
                        + ", s.statuscode_fk AS s_statuscode_fk\n"
                        + ", c.centerId\n"
                        + ", c.pipeline\n"
                        + ", c.project\n"
                        + ", s.colonyId\n"
                        + ", s.gender\n"
                        + ", s.isBaseline\n"
                        + ", s.litterId\n"
                        + ", s.phenotypingCenter\n"
                        + ", s.pipeline\n"
                        + ", s.productionCenter\n"
                        + ", s.specimenId\n"
                        + ", s.strainId\n"
                        + ", s.zygosity\n"
                        + ", sc.dateOfStatuscode\n"
                        + ", sc.value\n"
                        + ", m.DOB\n"
                        + ", e.stage\n"
                        + ", e.stageUnit\n"
                        + "FROM center c\n"
                        + "JOIN center_specimen cs ON cs.center_fk = c.pk\n"
                        + "JOIN specimen s ON cs.specimen_fk = s.pk\n"
                        + "LEFT OUTER JOIN mouse m ON m.specimen_fk = cs.specimen_fk\n"
                        + "LEFT OUTER JOIN embryo e ON e.specimen_fk = cs.specimen_fk\n"
                        + "LEFT OUTER JOIN statuscode sc ON sc.pk = s.statuscode_fk\n"
                        + "WHERE c.pk = ? AND s.pk = ?;";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, centerPk);
            ps.setLong(2, specimenPk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                retVal += "{"
                        + "cs.pk=" + rs.getLong("cs_pk")
                        + ",c.pk=" + rs.getLong("c_pk")
                        + ",s.pk=" + rs.getLong("s_pk")
                        + ",s.statuscode_fk=" + (rs.getLong("s_statuscode_fk") == 0 ? "<null>" : rs.getLong("s_statuscode_fk"))
                        + ",centerId=" + rs.getString("c.centerId")
                        + ",pipeline=" + rs.getString("c.pipeline")
                        + ",project=" + rs.getString("c.project")
                        + ",colonyId=" + rs.getString("s.colonyId")
                        + ",gender=" + rs.getString("s.gender")
                        + ",isBaseline=" + rs.getInt("s.isBaseline")
                        + ",litterId=" + rs.getString("s.litterId")
                        + ",phenotypingCenter=" + rs.getString("s.phenotypingCenter")
                        + ",productionCenter=" + (rs.getString("s.productionCenter") == null ? "<null>" : rs.getString("s.productionCenter"))
                        + ",specimenId=" + rs.getString("s.specimenId")
                        + ",strainId=" + rs.getString("s.strainId")
                        + ",zygosity=" + rs.getString("s.zygosity");
                if (rs.getLong("s_statuscode_fk") != 0) {
                    retVal += ",sc.dateOfStatuscode=" + (rs.getDate("sc.dateOfStatuscode") == null ? "<null>" : rs.getDate("sc.dateOfStatuscode"))
                            + ",sc.value=" + rs.getString("sc.value");
                }
                retVal += (rs.getDate("m.DOB") == null ? " (EMBRYO)" : " (MOUSE)");
            }

        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

        return retVal;
    }

    public static ApplicationContext loadApplicationContext(String context) {
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
            logger.error("Unable to load context '" + context + "' from file or classpath. Exiting...");
        }

        return appContext;
    }

    public static void truncateSpecimenTables(Connection connection) throws SQLException {
        String query;
        PreparedStatement ps;

        String[] tables = new String[]{
                "center"
                , "center_specimen"
                , "embryo"
                , "genotype"
                , "mouse"
                , "parentalStrain"
                , "relatedSpecimen"
                , "specimen"
                , "statuscode"
        };

        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
        ps.execute();
        for (String tableName : tables) {
            query = "TRUNCATE " + tableName + ";";

            try {
                ps = connection.prepareStatement(query);
                ps.execute();
            } catch (SQLException e) {
                logger.error("Unable to truncate table " + tableName);
                throw e;
            }
        }
        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1");
        ps.execute();
    }

    /**
     * Looks for the <code>center</code> table primary key for the given centerId, pipeline, and project. Retuns the
     * key if found; null otherwise.
     *
     * @param connection A valid database connection
     * @param centerId   The center id
     * @param pipeline   The pipeline
     * @param project    The Project
     * @return The center primary key if found; null otherwise.
     */
    public static Long getCenterPk(Connection connection, String centerId, String pipeline, String project) {
        Long centerPk = null;

        String query = "";
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM center WHERE centerId = ? AND pipeline = ? AND project = ?;");
            ps.setString(1, centerId);
            ps.setString(2, pipeline);
            ps.setString(3, project);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                centerPk = rs.getLong("pk");
            }
        } catch (SQLException e) {

        }

        return centerPk;
    }

    /**
     * Looks for the <code>specimen</code> for the given specimenId, centerId, pipeline, and project.
     * Retuns the <code>Specimen</code> instance if found; null otherwise.
     *
     * @param connection A valid database connection
     * @param specimenId The specimen id
     * @param centerId   The center id
     * @param pipeline   The pipeline
     * @param project    The Project
     * @return The <code>Specimen</code> instance if found; null otherwise.
     * <p/>
     * <i>NOTE: The primary key value is returned in Hjid.</i>
     */
    public static Specimen getSpecimen(Connection connection, String specimenId, String centerId, String pipeline, String project) {
        SpecimenCDA specimen = new SpecimenCDA();

        String query = "";
        PreparedStatement ps;
        ResultSet rs;
        try {
            query =
                    "SELECT *\n"
                            + "FROM specimen s\n"
                            + "JOIN center_specimen cs ON cs.specimen_fk =  s.pk\n"
                            + "JOIN center           c ON  c.pk          = cs.center_fk\n"
                            + "WHERE s.specimenId = ? AND c.centerId = ? AND c.pipeline = ? AND c.project = ?;";

            ps = connection.prepareStatement(query);
            ps.setString(1, specimenId);
            ps.setString(2, centerId);
            ps.setString(3, pipeline);
            ps.setString(4, project);
            rs = ps.executeQuery();
            if (rs.next()) {
                specimen.setHjid(rs.getLong("s.pk"));
                String colonyId = rs.getString("colonyId");
                if (colonyId != null) {
                    specimen.setColonyID(colonyId);
                }
                specimen.setGender(Gender.fromValue(rs.getString("gender")));
                specimen.setIsBaseline((rs.getInt("isBaseline") == 1 ? true : false));
                specimen.setLitterId(rs.getString("litterId"));
                specimen.setPhenotypingCentre(CentreILARcode.fromValue(rs.getString("phenotypingCenter")));
                specimen.setPipeline(rs.getString("pipeline"));
                String productionCenter = rs.getString("productionCenter");
                if (productionCenter != null) {
                    specimen.setProductionCentre(CentreILARcode.fromValue(productionCenter));
                }
                specimen.setProject(rs.getString("project"));
                specimen.setSpecimenID(rs.getString("specimenId"));
                String strainId = rs.getString("strainId");
                if (strainId != null) {
                    specimen.setStrainID(strainId);
                }
                specimen.setZygosity(Zygosity.fromValue(rs.getString("zygosity")));
                specimen.setStatusCode(getStatuscode(connection, specimen.getHjid()));
            }

        } catch (SQLException e) {

        }

        return specimen;
    }

    /**
     * Returns the Specimen <code>StatusCode</code>, if it exists; null otherwise.
     *
     * @param connection  A valid database connection
     * @param specimen_pk The specimen primary key
     * @return The <code>StatusCode</code> matching specimen_pk, if it exists; null otherwise.
     * <p/>
     * <i>NOTE: The primary key value is returned in Hjid.</i>
     */
    public static StatusCode getStatuscode(Connection connection, long specimen_pk) {
        StatusCode statuscode = new StatusCode();
        String query =
                "SELECT sc.pk, sc.dateOfStatuscode, sc.value\n"
                        + "FROM specimen s"
                        + "LEFT OUTER JOIN statuscode sc ON sc.pk = s.statuscode_fk"
                        + "where s.pk = ?;";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = connection.prepareStatement(query);
            ps.setLong(1, specimen_pk);
            rs = ps.executeQuery();
            if (rs.next()) {
                Long pk = rs.getLong("sc.pk");
                if (pk > 0) {
                    statuscode.setHjid(pk);
                    Date dateOfStatuscode = rs.getDate("sc.dateOfStatuscode");
                    if (dateOfStatuscode != null) {
                        GregorianCalendar gc = new GregorianCalendar();
                        gc.setTime(dateOfStatuscode);
                        statuscode.setDate(gc);
                        statuscode.setValue(rs.getString("sc.value"));
                    }
                }
            }

        } catch (SQLException e) {

        }

        return statuscode;
    }
}