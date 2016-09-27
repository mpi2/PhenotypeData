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

package org.mousephenotype.cda.loads.legacy;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mrelac on 20/07/16.
 */
public class LoaderUtils {


    /**
     * Maps external input names to CDA known names for project, organisation, ontology term, etc.
     *
     * Key terms in this array (column 0) should be entered in lowercase.
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
}