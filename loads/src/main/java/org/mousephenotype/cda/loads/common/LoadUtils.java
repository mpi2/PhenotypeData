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


import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

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
        put("Ning",                     "MARC");                        // center.centerId -> organisation.name
        put("Rbrc",                     "RBRC");                        // center.centerId -> organisation.name
        put("Tcp",                      "TCP");                         // center.centerId -> organisation.name
        put("Ucd",                      "UC Davis");                    // center.centerId -> organisation.name
        put("Wtsi",                     "WTSI");                        // center.centerId -> organisation.name
        put("Kmpc",                     "KMPC");                        // center.centerId -> organisation.name
        put("Biat",                     "BIAT");                        // center.centerId -> organisation.name
        put("Ph",                       "PH");                          // center.centerId -> organisation.name
        put("CDTA",                     "CDTA");                        // center.centerId -> organisation.name
        put("Crl",                      "CRL");                         // center.centerId -> organisation.name
        put("Ccpcz",                    "CCP-IMG");                     // center.centerId -> organisation.name
    }};

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
        put("BaSH",                     "BaSH");                        // center.project -> project.name
        put("DTCC",                     "DTCC");                        // center.project -> project.name
        put("EUMODIC",                  "EUMODIC");                     // center.project -> project.name
        put("EUCOMM-EUMODIC",           "EUMODIC");                     // center.project -> project.name
        put("Helmholtz GMC",            "Helmholtz GMC");               // center.project -> project.name
        put("JAX",                      "JAX");                         // center.project -> project.name
        put("NING",                     "MARC");                        // center.project -> project.name
        put("MARC",                     "MARC");                        // center.project -> project.name
        put("MGP",                      "MGP");                         // center.project -> project.name
        put("MGP Legacy",               "MGP");                         // center.project -> project.name
        put("MRC",                      "MRC");                         // center.project -> project.name
        put("NorCOMM2",                 "NorCOMM2");                    // center.project -> project.name
        put("Phenomin",                 "Phenomin");                    // center.project -> project.name
        put("RIKEN BRC",                "RBRC");                        // center.project -> project.name
        put("KMPC",                     "KMPC");                        // center.project -> project.name
        put("Kmpc",                     "KMPC");                        // center.project -> project.name
        put("3i",                       "3i");                          // center.project -> project.name
        put("IMPC",                     "IMPC");                        // center.project -> project.name
        put("Ccp",                      "CCP-IMG");                     // center.project -> project.name
    }};


    /**
     * Maps external input names to CDA known names for project, organisation, ontology term, etc.
     */
    private final Map<String, String> mappedTerms = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {{
        //   External name        CDA name
        put("coisogenic strain", "coisogenic");
        put("congenic strain",   "congenic");
        put("eucomm-eumodic",    "EUMODIC");
        put("harwell",           "MRC Harwell");
        put("mgp legacy",        "MGP");
        put("monterotondo",      "EMBL Monterotondo");
        put("narlabs",           "NARLabs");
        put("Ning",              "MARC");
        put("riken brc",         "RBRC");
        put("ucd",               "UC Davis");
        put("ucd-komp",          "UC Davis");
    }};


    /**
     * Translates a term case insensitively, lowercasing {@code term} and returning the standard CDA translated term,
     * if found; the orignal term, untranslated, otherwise.
     *
     * @param term the term to be translated
     *
     * @return the translated term, if found; the orignal term, untranslated, otherwise.
     */
    public String translateTerm(String term) {
        return (mappedTerms.containsKey(term) ? mappedTerms.get(term) : term);
    }

    public enum SampleGroup {
        CONTROL("control"),
        EXPERIMENTAL("experimental");

        private final String value;

        SampleGroup(String value) {
            this.value = value;
        }


        public String getName(){
            return this.toString();
        }

        public String value() {
            return value;
        }
    }



    /**
     * Compute the line-level [mutant] zygosity (line-level controls have no specimens and, thus, no zygosity)
     * @param simpleParameters a list of the caller's experiment's {@link SimpleParameter} instances
     * @return The zygosity string, suitable for insertion into the database
     */
    public static String getLineLevelZygosity(List<SimpleParameter> simpleParameters) {

        // Default zygosity is homozygous since most of the time this will be the case
        ZygosityType zygosity = ZygosityType.homozygote;

        // Check if Hemizygote
        for (SimpleParameter param : simpleParameters) {

            // Find the associated "Outcome" parameter
            if (param.getParameterID()
                    .equals("IMPC_VIA_001_001")) {

                // Found the outcome parameter, check zygosity
                String category = param.getValue();

                if (category != null && category.contains("Hemizygous")) {
                    zygosity = ZygosityType.hemizygote;
                }

                break;
            }
        }

        return zygosity.getName();
    }


    /**
     * Maps dcc zygosity string to cda zygosity string suitable for insertion into the cda database.
     *
     * @param dccZygosity The dcc zygosity string
     *
     * @return the cda zygosity string, or null if the dccZygosity is unknown.
     */
    public static String getSpecimenLevelMutantZygosity(String dccZygosity) {

        String zygosity;
        switch (dccZygosity) {
            case "wild type":
            case "homozygous":
                zygosity = ZygosityType.homozygote.getName();
                break;
            case "heterozygous":
                zygosity = ZygosityType.heterozygote.getName();
                break;
            case "hemizygous":
                zygosity = ZygosityType.hemizygote.getName();
                break;

            default:
                String message = "Unknown dcc zygosity '" + dccZygosity + "'";
                logger.error(message);
                zygosity = null;
        }

        return zygosity;
    }

    public static String getControlZygosity() {
        return ZygosityType.homozygote.getName();
    }
}