/*******************************************************************************
 * Copyright © 2020 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.reports2.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.BasicService;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcedureCompletenessService extends BasicService {

    private final Logger     logger = LoggerFactory.getLogger(this.getClass());
    private       SolrClient srCore;

    @Inject
    public ProcedureCompletenessService(
        @Qualifier("statisticalResultCore")
        SolrClient srCore) {
        super();
        this.srCore = srCore;
    }

    public List<String> getGeneSymbols() throws SolrServerException, IOException {

        List<String> geneIds = new ArrayList<>();
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .addFacetField(StatisticalResultDTO.MARKER_SYMBOL)
            .setFacetMinCount(1)
            .setFacetLimit(-1)
            .setRows(0);

        QueryResponse    response = srCore.query(query);
        List<FacetField> fields   = response.getFacetFields();
        for(FacetField.Count values: fields.get(0).getValues()){
            geneIds .add(values.getName());
        }

        return geneIds ;
    }

    public List<StatisticalResultDTO> getGeneData(String geneSymbol) throws SolrServerException, IOException {

        final String[] fields = {
            StatisticalResultDTO.MARKER_SYMBOL,
            StatisticalResultDTO.MARKER_ACCESSION_ID,
            StatisticalResultDTO.ALLELE_SYMBOL,
            StatisticalResultDTO.ALLELE_ACCESSION_ID,
            StatisticalResultDTO.STRAIN_NAME,
            StatisticalResultDTO.STRAIN_ACCESSION_ID,
            StatisticalResultDTO.PHENOTYPING_CENTER,
            StatisticalResultDTO.COLONY_ID,
            StatisticalResultDTO.ZYGOSITY,
            StatisticalResultDTO.PROCEDURE_NAME,
            StatisticalResultDTO.PROCEDURE_STABLE_ID,
            StatisticalResultDTO.PARAMETER_NAME,
            StatisticalResultDTO.PARAMETER_STABLE_ID,
            StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME,
            StatisticalResultDTO.TOP_LEVEL_MP_TERM_ID,
            StatisticalResultDTO.MP_TERM_NAME,
            StatisticalResultDTO.MP_TERM_ID,
            StatisticalResultDTO.LIFE_STAGE_NAME,
            StatisticalResultDTO.STATUS
        };

        SolrQuery query = new SolrQuery()
            .setQuery("marker_symbol:" + geneSymbol)
            .setFields(fields)
            .setRows(Integer.MAX_VALUE);

        QueryResponse    response = srCore.query(query);
        return response.getBeans(StatisticalResultDTO.class);
    }
}
