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

public class ViabilityDTOVersion2 extends ViabilityDTO {
    private final String totalPups = "IMPC_VIA_057_001";
    private final String totalPupsWt = "IMPC_VIA_058_001";
    private final String totalPupsHom = "IMPC_VIA_060_001";
    private final String totalPupsHet = "IMPC_VIA_059_001";

    private final String totalMalePups = "IMPC_VIA_061_001";
    private final String totalMaleWt = "IMPC_VIA_049_001";
    private final String totalMaleHem = "IMPC_VIA_055_001";
    private final String totalMaleHom = "IMPC_VIA_053_001";
    private final String totalMaleHet = "IMPC_VIA_051_001";

    private final String totalFemalePups = "IMPC_VIA_062_001";
    private final String totalFemaleWt = "IMPC_VIA_050_001";
    private final String totalFemaleHom = "IMPC_VIA_054_001";
    private final String totalFemaleHet = "IMPC_VIA_052_001";
    private final String totalFemaleAnz = "IMPC_VIA_056_001";

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
    @Override public String getTotalMaleHem() { return totalMaleHem; }
    @Override public String getTotalFemaleAnz() { return totalFemaleAnz; }
}
