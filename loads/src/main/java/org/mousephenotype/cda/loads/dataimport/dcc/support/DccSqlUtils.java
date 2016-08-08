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

package org.mousephenotype.cda.loads.dataimport.dcc.support;

import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Gender;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Dimension;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ParameterAssociation;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Zygosity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by mrelac on 02/03/2016.
 */
public class DccSqlUtils {

    private static final Logger logger = LoggerFactory.getLogger(DccSqlUtils.class);

    public static String dumpSpecimen(Connection connection, long centerPk, long specimenPk) {
        String retVal = "";

        String query =
                "SELECT\n"
                        + "  cs.pk AS cs_pk\n"
                        + ", c.pk AS c_pk\n"
                        + ", s.pk AS s_pk\n"
                        + ", s.statuscode_pk AS s_statuscode_pk\n"
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
                        + "JOIN center_specimen cs ON cs.center_pk = c.pk\n"
                        + "JOIN specimen s ON cs.specimen_pk = s.pk\n"
                        + "LEFT OUTER JOIN mouse m ON m.specimen_pk = cs.specimen_pk\n"
                        + "LEFT OUTER JOIN embryo e ON e.specimen_pk = cs.specimen_pk\n"
                        + "LEFT OUTER JOIN statuscode sc ON sc.pk = s.statuscode_pk\n"
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
                        + ",s.statuscode_pk=" + (rs.getLong("s_statuscode_pk") == 0 ? "<null>" : rs.getLong("s_statuscode_pk"))
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
                if (rs.getLong("s_statuscode_pk") != 0) {
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

    public static void truncateExperimentTables(Connection connection) throws SQLException {
        String query;
        PreparedStatement ps;

        String[] tables = new String[] {
                  "experiment"
                , "experiment_statuscode"
                , "experiment_specimen"
                , "housing"
                , "line"
                , "procedure_"
                , "center_procedure"
                , "line_statuscode"
                , "simpleParameter"
                , "ontologyParameter"
                , "seriesParameter"
                , "mediaParameter"
                , "ontologyParameterTerm"
                , "seriesParameterValue"
                , "mediaParameter_parameterAssociation"
                , "mediaParameter_procedureMetadata"
                , "parameterAssociation"
                , "procedureMetadata"
                , "dimension"
                , "mediaSampleParameter"
                , "mediaSample"
                , "mediaSection"
                , "mediaFile"
                , "mediaFile_parameterAssociation"
                , "mediaFile_procedureMetadata"
                , "seriesMediaParameter"
                , "procedure_procedureMetadata"
                , "seriesMediaParameterValue"
                , "seriesMediaParameterValue_parameterAssociation"
                , "seriesMediaParameterValue_procedureMetadata"
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

    public static void truncateSpecimenTables(Connection connection) throws SQLException {
        String query;
        PreparedStatement ps;

        String[] tables = new String[] {
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
     * key if found; 0 otherwise.
     *
     * @param connection A valid database connection
     * @param centerId   The center id
     * @param pipeline   The pipeline
     * @param project    The Project
     * @return The center primary key if found; 0 otherwise.
     */
    public static long getCenterPk(Connection connection, String centerId, String pipeline, String project) {
        long centerPk = 0L;

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
     * Returns the <code>ParameterAssociation</code> matching <code>parameterId</code> and <code>sequenceId</code>,
     * if found; null otherwise.
     *
     * @param connection  A valid database connection
     * @param parameterId The parameterId value to search for
     * @param sequenceId An optional sequence Id to search for. May be null.
     * @return The <code>ParameterAssociation</code> matching <code>parameterId</code> and, if not null,
     * <code>sequenceId</code>, if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     * <i>NOTE: The <code>Dimension</code> collection, if any, is returned as well.</i>
     */
    public static ParameterAssociation getParameterAssociation(Connection connection, String parameterId, Integer sequenceId) {
        ParameterAssociation parameterAssociation = null;

        if (parameterId == null)
            return parameterAssociation;
        String query;

        if (sequenceId == null) {
            query = "SELECT * FROM parameterAssociation WHERE parameterId = ?";
        } else {
            query = "SELECT * FROM parameterAssociation WHERE parameterId = ? AND sequenceId = ?";
        }

        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, parameterId);
            if (sequenceId != null) {
                ps.setInt(2, sequenceId);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                parameterAssociation = new ParameterAssociation();
                parameterAssociation.setHjid(rs.getLong("pk"));
                parameterAssociation.setParameterID(parameterId);
                parameterAssociation.setSequenceID(BigInteger.valueOf(rs.getLong("sequenceId")));

                query = "SELECT * FROM dimension WHERE parameterAssociation_pk = ?";
                ps = connection.prepareStatement(query);
                ps.setLong(1, parameterAssociation.getHjid());
                rs = ps.executeQuery();
                List<Dimension> dimensionList = new ArrayList<>();
                parameterAssociation.setDim(dimensionList);
                while (rs.next()) {
                    Dimension dimension = new Dimension();
                    dimension.setHjid(rs.getLong("pk"));
                    dimension.setId(rs.getString("id"));
                    dimension.setOrigin(rs.getString("origin"));
                    dimension.setUnit(rs.getString("unit"));
                    BigDecimal value = rs.getBigDecimal("value");
                    dimension.setValue( (rs.wasNull() ? null : value));
                    dimensionList.add(dimension);
                }
            }

        } catch (SQLException e) {

        }

        return parameterAssociation;
    }

    /**
     * Returns the <code>ProcedureMetadata</code> matching <code>parameterId</code> and <code>sequenceId</code>,
     * if found; null otherwise.
     *
     * @param connection  A valid database connection
     * @param parameterId The parameterId value to search for
     * @param sequenceId An optional sequence Id to search for. May be null.
     * @return The <code>ProcedureMetadata</code> matching <code>parameterId</code> and, if not null, <code>sequenceId</code>,
     * if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public static ProcedureMetadata getProcedureMetadata(Connection connection, String parameterId, Integer sequenceId) {
        ProcedureMetadata procedureMetadata = null;

        if (parameterId == null)
            return procedureMetadata;

        String query;

        if (sequenceId == null) {
            query = "SELECT * FROM procedureMetadata WHERE parameterId = ?";
        } else {
            query = "SELECT * FROM procedureMetadata WHERE parameterId = ? AND sequenceId = ?";
        }

        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, parameterId);
            if (sequenceId != null) {
                ps.setInt(2, sequenceId);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                procedureMetadata = new ProcedureMetadata();
                procedureMetadata.setHjid(rs.getLong("pk"));
                procedureMetadata.setParameterID(parameterId);
                procedureMetadata.setParameterStatus(rs.getString("parameterStatus"));
                procedureMetadata.setSequenceID(BigInteger.valueOf(rs.getLong("sequenceId")));
                procedureMetadata.setValue(rs.getString("value"));
            }

        } catch (SQLException e) {

        }

        return procedureMetadata;
    }

    /**
     * Looks for the <code>specimen</code> for the given specimenId, centerId, pipeline, and project.
     * Retuns the <code>Specimen</code> instance if found; null otherwise.
     *
     * @param connection A valid database connection
     * @param specimenId The specimen id
     * @param centerId   The center id
     * @return The <code>Specimen</code> instance if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public static Specimen getSpecimen(Connection connection, String specimenId, String centerId) {
        SpecimenCda specimen = null;

        String query = "";
        PreparedStatement ps;
        ResultSet rs;
        try {
            query =
                    "SELECT *\n"
                    + "FROM specimen s\n"
                    + "JOIN center_specimen cs ON cs.specimen_pk =  s.pk\n"
                    + "JOIN center           c ON  c.pk          = cs.center_pk\n"
                    + "WHERE s.specimenId = ? AND c.centerId = ?";

            ps = connection.prepareStatement(query);
            ps.setString(1, specimenId);
            ps.setString(2, centerId);
            rs = ps.executeQuery();
            if (rs.next()) {
                specimen = new SpecimenCda();
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
                specimen.setStatusCode(selectOrInsertStatuscode(connection, specimen.getStatusCode()));
            }

        } catch (SQLException e) {

        }

        return specimen;
    }

    /**
     * Returns the <code>StatusCode</code> matching <code>value</code>, if found; null otherwise.
     *
     * @param connection  A valid database connection
     * @param value The StatusCode value to search for
     * @return The <code>StatusCode</code> matching <code>value</code>, if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public static StatusCode getStatuscode(Connection connection, String value) {
        StatusCode statuscode = null;

        if (value == null)
            return statuscode;

        String query =
                "SELECT * FROM statuscode WHERE value = ?;\n";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, value);
            rs = ps.executeQuery();
            if (rs.next()) {
                long pk = rs.getLong("pk");
                if (pk > 0) {
                    statuscode = new StatusCode();
                    statuscode.setHjid(pk);
                    statuscode.setValue(rs.getString("value"));
                    Date dateOfStatuscode = rs.getDate("dateOfStatuscode");
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

    /**
     * Inserts the given values into the center table. If the centerId, pipline, and project already exist, an error
     * is logged and a value of 0 is returned.
     *
     * @param connection A valid database connection
     * @param centerId   The center id
     * @param pipeline   The pipeline
     * @param project    The Project
     *
     * @return the primary key matching the newly inserted values
     */
    public static long insertIntoCenter(Connection connection, String centerId, String pipeline, String project) {
        long centerPk = 0L;
        PreparedStatement ps;
        String query;
        ResultSet rs;

        try {
            query = "INSERT INTO center (centerId, pipeline, project) VALUES (?, ?, ?);";
            ps = connection.prepareStatement(query);
            ps.setString(1, centerId);
            ps.setString(2, pipeline);
            ps.setString(3, project);
            ps.execute();
            rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
            rs.next();
            centerPk = rs.getLong(1);
        } catch (SQLException e) {
            logger.error("Unable to insert into center(" + centerId + ", " + pipeline + ", " + project + ": " + e.getLocalizedMessage());
        }

        return centerPk;
    }

    /**
     * Given a parameterId value, attempts to fetch the matching <code>ParameterAssociation</code> instance. If there is
     * none, the parameterId and sequenceId are first inserted. The <code>ParameterAssociation</code> instance is then
     * returned.
     *
     * <i>NOTE: if <code>parameterId</code> is null, a null <code>ParameterAssociation</code> is returned.</i>
     * <i>NOTE: Any <code>Dimension</code></i> instances in the parameterAssociation are added to the dimension table
     *          and returned in the returned <code>ParameterAssociation</code> instance.
     *
     * @param connection  A valid database connection
     * @param parameterAssociation a valid <code>ParameterAssociation</code> instance
     *
     * @return The <code>ParameterAssociation</code> instance matching <code>parameterId</code> (and <code>sequenceId</code>,
     * if specified), inserted first if necessary.
     */
    public static ParameterAssociation selectOrInsertParameterAssociation(Connection connection, ParameterAssociation parameterAssociation) {
        String parameterId = parameterAssociation.getParameterID();
        Integer sequenceId = (parameterAssociation.getSequenceID() == null ? null : parameterAssociation.getSequenceID().intValue());

        ParameterAssociation retVal = getParameterAssociation(connection, parameterId, sequenceId);

        if (retVal == null) {
            String query = "INSERT INTO parameterAssociation (parameterId, sequenceId) VALUES (?, ?)\n";
            try {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, parameterId);
                if (sequenceId == null)
                    ps.setNull(2, Types.INTEGER);
                else
                    ps.setInt(2, sequenceId);
                ps.execute();
                long parameterAssociationPk = getParameterAssociation(connection, parameterId, sequenceId).getHjid();

                if ((parameterAssociation.getDim() != null) && ( ! parameterAssociation.getDim().isEmpty())) {
                    for (Dimension dimension : parameterAssociation.getDim()) {
                        query = "INSERT INTO dimension (id, origin, unit, value, parameterAssociation_pk)"
                              + "VALUES (?, ?, ?, ?, ?)";
                        ps = connection.prepareStatement(query);
                        ps.setString(1, dimension.getId());
                        ps.setString(2, dimension.getOrigin());
                        ps.setString(3, dimension.getUnit());
                        ps.setBigDecimal(4, dimension.getValue());
                        ps.setLong(5, parameterAssociationPk);
                        ps.execute();
                    }
                }

                retVal = getParameterAssociation(connection, parameterId, sequenceId);

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("INSERT of retVal(" + parameterId + ", " + sequenceId + " FAILED: " + e.getLocalizedMessage());
            }
        }

        return retVal;
    }

    /**
     * Given a parameterId value, attempts to fetch the matching <code>ProcedureMetadata</code> instance. If there is
     * none, the parameterId and sequenceId are first inserted. The <code>ProcedureMetadata</code> instance is then
     * returned.
     *
     * <i>NOTE: if <code>parameterId</code> is null, a null <code>ProcedureMetadata</code> is returned.</i>
     *
     * @param connection  A valid database connection
     * @param parameterId the parameterId to search for
     * @param sequenceId the sequence id to search for (may be null)
     *
     * @return The <code>ProcedureMetadata</code> instance matching <code>parameterId</code> (and <code>sequenceId</code>,
     * if specified), inserted first if necessary.
     */
    public static ProcedureMetadata selectOrInsertProcedureMetadata(Connection connection, String parameterId, Integer sequenceId) {
        ProcedureMetadata procedureMetadata = getProcedureMetadata(connection, parameterId, sequenceId);

        if (procedureMetadata == null) {
            String query = "INSERT INTO procedureMetadata (parameterId, sequenceId) VALUES (?, ?)\n";
            try {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, parameterId);
                if (sequenceId == null)
                    ps.setNull(2, Types.INTEGER);
                else
                    ps.setInt(2, sequenceId);
                ps.execute();
                procedureMetadata = getProcedureMetadata(connection, parameterId, sequenceId);

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("INSERT of parameterAssociation(" + parameterId + ", " + sequenceId + " FAILED: " + e.getLocalizedMessage());
            }
        }

        return procedureMetadata;
    }

    /**
     * Given a statuscode value, attempts to fetch the matching object. If there is none, the value and (nullable)
     * dateOfStatuscode are first inserted. The <code>StatusCode</code> instance is then returned.
     *
     * <i>NOTE: if <code>value</code> is null, a null <code>StatusCode</code> is returned.</i>
     *
     * @param connection  A valid database connection
     * @param value The status code value (required)
     * @param dateOfStatuscode statuscode date (may be null)  (Not used in SELECT)
     *
     * @return The <code>StatusCode</code> instance matching <code>value</code>, inserted first if necessary.
     */
    public static StatusCode selectOrInsertStatuscode(Connection connection, String value, Calendar dateOfStatuscode) {
        StatusCode statuscode = getStatuscode(connection, value);

        if (value == null)
            return statuscode;

        if (statuscode == null) {
            String query = "INSERT INTO statuscode (dateOfStatuscode, value) VALUES (?, ?)\n";
            try {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setDate(1, dateOfStatuscode == null ? null : new java.sql.Date(dateOfStatuscode.getTimeInMillis()));
                ps.setString(2, value);
                ps.execute();
                statuscode = getStatuscode(connection, value);

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Invalid date '" + dateOfStatuscode.getTime().toString());
            }
        }

        return statuscode;
    }

    /**
     * Given a statuscode instance, attempts to fetch the matching object. If there is none, the value and (nullable)
     * dateOfStatuscode are first inserted. The <code>StatusCode</code> instance is then returned.
     *
     * @param connection  A valid database connection
     * @param statuscode The <code>StatusCode</code> instance
     *
     * @return The <code>StatusCode</code> instance matching <code>value</code>, inserted first if necessary.
     */
    public static StatusCode selectOrInsertStatuscode(Connection connection, StatusCode statuscode) {
        if (statuscode == null)
            return null;

        return selectOrInsertStatuscode(connection, statuscode.getValue(), statuscode.getDate());
    }
}