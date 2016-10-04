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

/**
 * Created by mrelac on 04/10/16.
 */
public enum OntologyTermAnomalyReason {

    NOT_FOUND_HAS_ALTERNATE_ID("Term not found. Has alternate id."),
    NOT_FOUND_HAS_MULTIPLE_ALTERNATE_IDS("Term not found. Has multiple alternate ids."),
    NOT_FOUND_INVALID_ALTERNATE_ID("Term not found Has invalid alternate id."),
    NOT_FOUND_NO_OTHER_ID("Term not found. No other replacement, consider, or alternate id found."),
    OBSOLETE_INVALID_CONSIDER_ID("Obsolete. Has invalid consider id."),
    OBSOLETE_HAS_CONSIDER_ID("Obsolete. Has consider id."),
    OBSOLETE_HAS_MULTIPLE_CONSIDER_IDS("Obsolete. Has multiple consider ids."),
    OBSOLETE_HAS_REPLACEMENT("Obsolete. Has replacement."),
    OBSOLETE_NO_OTHER_ID("Obsolete. No other replacement, consider, or alternate id found."),
    OBSOLETE_HAS_INVALID_REPLACEMENT("The term was replaced but the replacement term is obsolete.")
    ;

    private String description;
    OntologyTermAnomalyReason(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public static OntologyTermAnomalyReason getValueByDescription(String description) {
        switch (description) {
            case "Term not found. Has alternate id.":                                       return NOT_FOUND_HAS_ALTERNATE_ID;
            case "Term not found. Has multiple alternate ids.":                             return NOT_FOUND_HAS_MULTIPLE_ALTERNATE_IDS;
            case "Term not found Has invalid alternate id.":                                return NOT_FOUND_INVALID_ALTERNATE_ID;
            case "Term not found. No other replacement, consider, or alternate id found.":  return NOT_FOUND_NO_OTHER_ID;
            case "Obsolete. Has invalid consider id.":                                      return OBSOLETE_INVALID_CONSIDER_ID;
            case "Obsolete. Has consider id.":                                              return OBSOLETE_HAS_CONSIDER_ID;
            case "Obsolete. Has multiple consider ids.":                                    return OBSOLETE_HAS_MULTIPLE_CONSIDER_IDS;
            case "Obsolete. Has replacement.":                                              return OBSOLETE_HAS_REPLACEMENT;
            case "Obsolete. No other replacement, consider, or alternate id found.":        return OBSOLETE_NO_OTHER_ID;
            case "The term was replaced but the replacement term is obsolete.":             return OBSOLETE_HAS_INVALID_REPLACEMENT;
            default: throw new RuntimeException("Unknown description '" + description + "'.");
        }
    }
}
