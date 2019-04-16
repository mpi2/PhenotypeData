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

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the DTO used with the PhenotypeCenterService. It holds the service components.
 */
public class PhenotypeCenterServiceBean {

    String center;
    String mgiAccession;
    String allele;
    String geneSymbol;
    String colonyId;


    @Inject
    public PhenotypeCenterServiceBean(String data) {
        List<String> fields = Arrays.asList((data.split("\","))).stream().map(x -> x.replaceAll("\"", "")).collect(Collectors.toList());

        this.center = fields.get(0);
        this.colonyId = fields.get(1);
    }

    public PhenotypeCenterServiceBean() {

    }


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
}