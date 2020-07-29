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

package org.mousephenotype.cda.enumerations;

/**
 * Simple enumeration of sexes
 */

public enum SexType {

    female("female"),
    hermaphrodite("hermaphrodite"),
    male("male"),
    not_applicable("not applicable"),
    no_data("no data"),
    not_considered("not_considered"),
    both("both");

    private final String name;

    SexType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.toString();
    }

    public static SexType getByDisplayName(String displayName) {
        switch (displayName) {
            case "female":
                return SexType.female;
            case "hermaphrodite":
                return SexType.hermaphrodite;
            case "male":
                return SexType.male;
            case "not applicable":
                return SexType.not_applicable;
            case "no data":
                return SexType.no_data;
            case "not_considered":
            case "both":
                return SexType.not_considered;
            default:
                throw new IllegalArgumentException("No enum constant " + SexType.class + "." + displayName);
        }
    }
}
