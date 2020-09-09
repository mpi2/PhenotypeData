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
package org.mousephenotype.cda.solr.web.dto;

public class ViabilityDTOVersion1 extends ViabilityDTO {

    private final String totalPups = "IMPC_VIA_003_001";
    private final String totalPupsWt = "IMPC_VIA_004_001";
    private final String totalPupsHom = "IMPC_VIA_006_001";
    private final String totalPupsHet = "IMPC_VIA_005_001";

    private final String totalMalePups = "IMPC_VIA_010_001";
    private final String totalMaleWt = "IMPC_VIA_007_001";
    private final String totalMaleHom = "IMPC_VIA_009_001";
    private final String totalMaleHet = "IMPC_VIA_008_001";

    private final String totalFemalePups = "IMPC_VIA_014_001";
    private final String totalFemaleWt = "IMPC_VIA_011_001";
    private final String totalFemaleHom = "IMPC_VIA_013_001";
    private final String totalFemaleHet = "IMPC_VIA_012_001";


    @Override public String getTotalPups() { return totalPups; }
    @Override public String getTotalPupsWt() { return totalPupsWt; }
    @Override public String getTotalPupsHom() { return totalPupsHom; }
    @Override public String getTotalPupsHet() { return totalPupsHet; }
    @Override public String getTotalMalePups() { return totalMalePups; }
    @Override public String getTotalFemalePups() { return totalFemalePups; }
    @Override public String getTotalMaleHom() { return totalMaleHom; }
    @Override public String getTotalFemaleHet() { return totalFemaleHet; }
    @Override public String getTotalMaleHet() { return totalMaleHet; }
    @Override public String getTotalFemaleWt() { return totalFemaleWt; }
    @Override public String getTotalMaleWt() { return totalMaleWt; }
    @Override public String getTotalFemaleHom() { return totalFemaleHom; }
}
