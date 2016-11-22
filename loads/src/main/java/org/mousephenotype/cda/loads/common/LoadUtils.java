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

package org.mousephenotype.cda.loads.common;


import org.mousephenotype.cda.db.pojo.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by mrelac on 10/08/16.
 */
public class LoadUtils {

    private static final Logger logger = LoggerFactory.getLogger(LoadUtils.class);

    /**
     * Wrapper for namedParameterJdbcTemplat.queryForObject
     * @param query the query to be executed
     * @param paramMap the parameter map
     * @return the result, if found; 0 otherwise
     */
    public long queryForPk(NamedParameterJdbcTemplate npJdbcTemplate, String query, Map<String, ?> paramMap) {
        long pk = 0L;

        try {
            pk = npJdbcTemplate.queryForObject(query, paramMap, Long.class);
        } catch (EmptyResultDataAccessException e) {

        }

        return pk;
    }


    /**
     * Maps external input names to Organisation.name. Lookups are case-insensitive.
     */
    public static final Map<String, String> mappedOrganisationNames = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {{
        //   External name          Organisation.name
        put("CDTA",                 "CDTA");
        put("Bcm",                  "BCM");
        put("EUMODIC",              "EUCOMM-EUMODIC");
        put("Gmc",                  "HMGU");
        put("Hmgu",                 "HMGU");
        put("H",                    "MRC Harwell");
        put("Ics",                  "ICS");
        put("J",                    "JAX");
        put("Krb",                  "KMPC");
        put("MGP",                  "MGP Legacy");
        put("Ning",                 "NING");
        put("Ncom",                 "CMHD");
        put("Rbrc",                 "RBRC");
        put("RIKEN BRC",            "RBRC");
        put("Tcp",                  "TCP");
        put("Ucd",                  "UC Davis");
        put("Wtsi",                 "WTSI");
    }};
    /**
     * @param ilarValue (e.g. J for Jax)
     * @return {@link Organisation} instance matching {@code ilarValue}, if found; null otherwise
     */
    public Organisation translateILAR(NamedParameterJdbcTemplate jdbcCda, String ilarValue) {
        String query = "SELECT * FROM organisation WHERE name = :name";
        String organisationName = mappedOrganisationNames.get(ilarValue);
        if (organisationName == null)
            return null;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("name", organisationName);
        List<Organisation> organisations = jdbcCda.query(query, parameterMap,new OrganisationRowMapper());

        if (organisations.isEmpty())
            return null;

        return organisations.get(0);
    }

    public static <K, V> Map<V, K> inverseMap(Map<K, V> sourceMap) {
        return sourceMap.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey,
                                 (a, b) -> a) //if sourceMap has duplicate values, keep only first
            );
    }

    /**
     * Maps external input names to cda project.name. Lookups are case-insensitive.
     */
    public static final Map<String, String> mappedProjectNames = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {{
        //   External name          Organisation.name
        put("BaSH",                 "BaSH");
        put("DTCC",                 "DTCC");
        put("Eumodic",              "EUMODIC");
//        put("EUCOMM-EUMODIC",       "???EUCOMM-EUMODIC???");
        put("Helmholtz GMC",        "HMGU");
        put("JAX",                  "JAX");
        put("MARC",                 "MARC");
        put("MGP",                  "MGP");
//        put("MGP Legacy",           "???MGP Legacy???");
        put("MRC",                  "MRC");
        put("NorCOMM2",             "NorCOMM2");
        put("Phenomin",             "Phenomin");
        put("RIKEN BRC",            "RBRC");
    }};
}