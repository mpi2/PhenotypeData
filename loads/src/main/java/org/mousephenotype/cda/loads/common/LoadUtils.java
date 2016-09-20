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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mrelac on 10/08/16.
 */
public class LoadUtils {

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
     * Maps external input names to Organisation.name
     */
    private final Map<String, String> mappedTerms = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {{
        //   External name          Organisation.name
        put("Bcm",                  "BCM");
        put("Gmc",                  "HMGU");
        put("H",                    "MRC Harwell");
        put("Ics",                  "ICS");
        put("J",                    "JAX");
        put("Krb",                  "KMPC");
        put("Ning",                 "NING");
        put("Rbrc",                 "RBRC");
        put("Tcp",                  "TCP");
        put("Ucd",                  "UC Davis");
        put("Wtsi",                 "WTSI");
        put("CDTA",                 "CDTA");
    }};
    /**
     * @param ilarValue (e.g. J for Jax)
     * @return {@link Organisation} instance matching {@code ilarValue}, if found; null otherwise
     */
    public Organisation translateILAR(NamedParameterJdbcTemplate jdbcCda, String ilarValue) {
        String query = "SELECT * FROM organisation WHERE name = :name";
        String organisationName = mappedTerms.get(ilarValue);
        if (organisationName == null)
            return null;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("name", organisationName);
        List<Organisation> organisations = jdbcCda.query(query, parameterMap,new OrganisationRowMapper());

        if (organisations.isEmpty())
            return null;

        return organisations.get(0);
    }
}