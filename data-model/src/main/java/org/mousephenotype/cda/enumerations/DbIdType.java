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

package org.mousephenotype.cda.enumerations;

/**
 * DbId enumerations
 * 
 * Created by mrelac on 24/05/16.
 */
public enum DbIdType {
    NCBI_m38(1),
    Genome_Feature_Type(2),
    MGI(3),
    X_Y(4),
    MP(5),
    IMPReSS(6),
    PATO(7),
    MA(8),
    CHEBI(9),
    EnvO(10),
    GO(11),
    EuroPhenome(12),
    ECO(13),
    EMAP(14),
    EFO(15),
    IMSR(16),
    VEGA(17),
    Ensembl(18),
    EntrezGene(19),
    MGP(20),
    cCDS(21),
    IMPC(22),
    _3i(23),
    MPATH(24),
    MmusDv(25),
    EMAPA(26);

    private final int value;

    DbIdType(int value) {
        this.value = value;
    }


    public String getName(){
        return this.toString();
    }
    
    public Integer intValue() {
        return value;
    }
    public Long longValue() {
        return intValue().longValue();
    }
}