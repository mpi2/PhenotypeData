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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

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
     * Maps external dcc center, dcc project, and cda phenotyped_colony names to cda organisation.name and project.name.
     *
     * All dcc (and dcc_europhenome_final) center names should be in this list, even if the lookup is the same.
     */
    public static final Map<String, String> mappedExternalCenterNames = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {{
        put("Bcm",                      "BCM");                         // center.centerId -> organisation.name
        put("Gmc",                      "HMGU");                        // center.centerId -> organisation.name
        put("H",                        "MRC Harwell");                 // center.centerId -> organisation.name
        put("Hmgu",                     "HMGU");                        // center.centerId -> organisation.name
        put("Ics",                      "ICS");                         // center.centerId -> organisation.name
        put("J",                        "JAX");                         // center.centerId -> organisation.name
        put("Ncom",                     "CMHD");                        // center.centerId -> organisation.name
        put("Ning",                     "NING");                        // center.centerId -> organisation.name
        put("Rbrc",                     "RBRC");                        // center.centerId -> organisation.name
        put("Tcp",                      "TCP");                         // center.centerId -> organisation.name
        put("Ucd",                      "UC Davis");                    // center.centerId -> organisation.name
        put("Wtsi",                     "WTSI");                        // center.centerId -> organisation.name
    }};

    /**
     * Translates a center namee insensitively, lowercasing {@code name} and returning the standard CDA translated name,
     * if found; the orignal name, untranslated, otherwise.
     *
     * @param name the center name to be translated
     *
     * @return the translated name, if found; the orignal name, untranslated, otherwise.
     */
    public String translateCenterName(String name) {
        return (mappedExternalCenterNames.containsKey(name) ? mappedExternalCenterNames.get(name) : name);
    }

    public static <K, V> Map<V, K> inverseMap(Map<K, V> sourceMap) {
        return sourceMap.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey,
                                 (a, b) -> a) //if sourceMap has duplicate values, keep only first
            );
    }

    /**
     * Maps external dcc project names to cda project.name.
     */
    public static final Map<String, String> mappedExternalProjectNames = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {{
        put("EUCOMM-EUMODIC",           "EUMODIC");                     // center.project -> project.name
        put("Eumodic",                  "EUMODIC");                     // center.project -> project.name
        put("MGP Legacy",               "MGP");                         // center.project -> project.name
        put("RIKEN BRC",                "RBRC");                        // center.project -> project.name
    }};

    /**
     * Translates a project namee insensitively, lowercasing {@code name} and returning the standard CDA translated name,
     * if found; the orignal name, untranslated, otherwise.
     *
     * @param name the project name to be translated
     *
     * @return the translated name, if found; the orignal name, untranslated, otherwise.
     */
    public String translateProjectName(String name) {
        return (mappedExternalProjectNames.containsKey(name) ? mappedExternalProjectNames.get(name) : name);
    }


    /**
     * Maps external ontology terms to cda ontology terms.
     */
    public static final Map<String, String> mappedExternalOntologyTermNames = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {{
        //   External name        CDA name
        put("coisogenic strain", "coisogenic");
        put("congenic strain",   "congenic");
    }};

    /**
     * Translates a term case insensitively, lowercasing {@code term} and returning the standard CDA translated term,
     * if found; the orignal term, untranslated, otherwise.
     *
     * @param term the term to be translated
     *
     * @return the translated term, if found; the orignal term, untranslated, otherwise.
     */
    public String translateOntologyTerm(String term) {
        return (mappedExternalOntologyTermNames.containsKey(term) ? mappedExternalOntologyTermNames.get(term) : term);
    }
}