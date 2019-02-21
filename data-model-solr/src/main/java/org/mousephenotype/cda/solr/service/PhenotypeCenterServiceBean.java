/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.solr.service;

/**
 * This is the DTO used with the PhenotypeCenterService. It holds the service components.
 */
public class PhenotypeCenterServiceBean {

    String mgiAccession;
    String allele;
    String geneSymbol;
    String colonyId;
    String zygosity;
    String lifeStage;


    public String getMgiAccession() {

        return mgiAccession;
    }

    public void setMgiAccession(String mgiAccession) {

        this.mgiAccession = mgiAccession;
    }

    public String getAllele() {

        return allele;
    }

    public void setAllele(String allele) {

        this.allele = allele;
    }

    public String getGeneSymbol() {

        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {

        this.geneSymbol = geneSymbol;
    }


    public String getColonyId() {

        return colonyId;
    }


    public void setColonyId(String colonyId) {

        this.colonyId = colonyId;
    }

    public String getZygosity() {
        return zygosity;
    }

    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    public String getLifeStage() {
        return lifeStage;
    }

    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }
}