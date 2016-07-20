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

package org.mousephenotype.cda.loads;

/**
 * Created by mrelac on 20/07/16.
 */
public class LoaderUtils {


    /**
     * Maps external input names to CDA known names for project, organisation, ontology term, etc.
     *
     * Key terms in this array (column 0) should be entered in lowercase.
     */
    private final String[][] mappedTerms = new String[][] {
         //     External name        CDA name
              { "coisogenic strain", "coisogenic" }
            , { "congenic strain",   "congenic" }
            , { "eucomm-eumodic",    "EUMODIC" }
            , { "harwell",           "MRC Harwell" }
            , { "mgp legacy",        "MGP" }
            , { "monterotondo",      "EMBL Monterotondo" }
            , { "riken brc",         "RBRC" }
            , { "ucd",               "UC Davis" }
            , { "ucd-komp",          "UC Davis" }
    };

    /**
     * Translates a term case insensitively, lowercasing {@code term} and returning the standard CDA translated term,
     * if found; the orignal term, untranslated, otherwise.
     *
     * @param term the term to be translated
     *
     * @return the translated term, if found; the orignal term, untranslated, otherwise.
     */
    public String translateTerm(String term) {
        for (int i = 0; i < mappedTerms.length; i++) {
            if (term.toLowerCase().equals(mappedTerms[i][0])) {
                return mappedTerms[i][1];
            }
        }

        return term;
    }
}