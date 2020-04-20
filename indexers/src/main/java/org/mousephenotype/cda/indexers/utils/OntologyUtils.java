/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.cda.indexers.utils;

import org.mousephenotype.cda.indexers.beans.OrganisationBean;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mrelac
 */
public class OntologyUtils {
    
    /**
     * Return relevant impress pipeline map
     *
     * @param connection The database connection
     * 
     * @return a map, indexed by primary key, of <code>ImpressBean</code> instances.
     * 
     * @throws java.sql.SQLException when a database exception occurs
     */
    public static Map<Long, ImpressBaseDTO> populateImpressPipeline(Connection connection) throws SQLException {
        Map<Long, ImpressBaseDTO> impressMap;

        String query = "SELECT id, stable_key, name, stable_id FROM phenotype_pipeline";
        try (PreparedStatement p = connection.prepareStatement(query)) {

            impressMap = populateImpressMap(p);
        }

        return impressMap;
    }

    /**
     * Given an alt_mp_id, returns the matching mp_id, if found; null otherwise.
     *
     * @param connection A valid ontodb_komp2 database connection instance
     * @param altMpId alt_mp_Id
     *
     * @return Given an alt_mp_id, returns the matching mp_id, if found; null otherwise.
     *
     * @throws SQLException
     */
    public static String getMpId(Connection connection, String altMpId) throws SQLException {
        String retVal = null;

        String query = "SELECT distinct term_id FROM ontodb_komp2.mp_alt_ids where alt_id = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, altMpId);

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                retVal = resultSet.getString("term_id");
            }
        }

        return retVal;
    }

    /**
     * Return relevant impress procedure map
     *
     * @param connection The database connection
     * 
     * @return a map, indexed by primary key, of <code>ImpressBean</code> instances.
     *
     * @throws java.sql.SQLException when a database exception occurs
     */
    public static Map<Long, ImpressBaseDTO> populateImpressProcedure(Connection connection) throws SQLException {
        Map<Long, ImpressBaseDTO> impressMap;

        String query = "SELECT id, stable_key, name, stable_id FROM phenotype_procedure";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            impressMap = populateImpressMap(p);
        }

        return impressMap;
    }

    /**
     * Return relevant impress parameter map
     *
     * @param connection The database connection
     * 
     * @return a map, indexed by primary key, of <code>ImpressBean</code> instances.
     *
     * @throws java.sql.SQLException when a database exception occurs
     */
    public static Map<Long, ParameterDTO> populateImpressParameter(Connection connection)
    throws SQLException {
        
    	Map<Long, ParameterDTO> impressMap;

        String query = "SELECT id, stable_key, name, stable_id, annotate, datatype FROM phenotype_parameter";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            impressMap = populateParameterMap(p);
        }

        return impressMap;
    }

    /**
     * Populate the map with organisation beans
     *
     * @param connection The database connection
     * 
     * @return a map, indexed by internal db id, o
     * <code>OrganisationBean</code> instances.
     *
     * @throws SQLException when a database error occurs
     */
    public static Map<Long, OrganisationBean> populateOrganisationMap(Connection connection) throws SQLException {

        Map<Long, OrganisationBean> map = new HashMap<>();
        String query = "SELECT id, name, fullname, country FROM organisation";
        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                OrganisationBean b = new OrganisationBean();

                b.setId(resultSet.getLong("id"));
                b.setName(resultSet.getString("name"));
                b.setFullname(resultSet.getString("fullname"));
                b.setCountry(resultSet.getString("country"));
                map.put(b.getId(), b);
            }
        }

        return map;
    }


    // PRIVATE METHODS

    
    private static Map<Long, ImpressBaseDTO> populateImpressMap(PreparedStatement p) throws SQLException {

        Map<Long, ImpressBaseDTO> impressMap = new HashMap<>();
        ResultSet resultSet = p.executeQuery();

        while (resultSet.next()) {
            ImpressBaseDTO b = new ImpressBaseDTO(resultSet.getLong("id"),
            		resultSet.getLong("stable_key"),
            		resultSet.getString("stable_id"), 
            		resultSet.getString("name"));
            impressMap.put(resultSet.getLong("id"), b);
        }

        return impressMap;
    }
    
    
    private static Map<Long, ParameterDTO> populateParameterMap(PreparedStatement p) throws SQLException {

        Map<Long, ParameterDTO> impressMap = new HashMap<>();
        ResultSet resultSet = p.executeQuery();

        while (resultSet.next()) {
        	
        	ParameterDTO b = new ParameterDTO();
        	b.setId(resultSet.getLong("id"));
        	b.setStableKey(resultSet.getLong("stable_key"));
        	b.setStableId(resultSet.getString("stable_id")); 
        	b.setName(resultSet.getString("name"));
        	b.setAnnotate(resultSet.getBoolean("annotate"));
        	b.setDataType(resultSet.getString("datatype"));
            impressMap.put(resultSet.getLong("id"), b);
            
        }

        return impressMap;
    }
}
