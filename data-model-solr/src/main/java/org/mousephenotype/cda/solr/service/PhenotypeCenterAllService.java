/*******************************************************************************
 * Copyright 2019 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

/**
 * Created this new class to duplicate functionality of PhenotypeCenterService but with dataSource ALL instead of just
 * IMPC. While it's messy to duplicate the code, it's more of a risk for the ProcedureCompletenessAllReport to accidentally
 * call the IMPC-only methods in PhenotypeCenterService and quietly and incorrectly return only IMPC data. This new
 * class allows us to remove the ProcedureCompletenessAllReport dependency on an IMPC-only service.
 * <p>
 * Hopefully, someday this entire reposts module will be replaced with something more flexible.
 */

@Service
public class PhenotypeCenterAllService extends BasicService {

    private final Logger     logger = LoggerFactory.getLogger(this.getClass());
    private       SolrClient statisticalResultCore;
    private       SolrClient mpCore;

    @Inject
    public PhenotypeCenterAllService(
        @Qualifier("statisticalResultCore")
            SolrClient statisticalResultCore,
        @Qualifier("mpCore")
            SolrClient mpCore) {
        super();
        this.statisticalResultCore = statisticalResultCore;
        this.mpCore = mpCore;
    }

    public PhenotypeCenterAllService() {
        super();
    }


    /**
     * Get a list of all phenotyping Centers with data in the statistical-result core
     *
     * @return List of centers
     * @throws SolrServerException, IOException
     */
    public List<String> getPhenotypeCenters() throws SolrServerException, IOException {

        List<String> centers = new ArrayList<>();
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .addFacetField(StatisticalResultDTO.PHENOTYPING_CENTER)
            .setFacetMinCount(1)
            .setRows(0);

        QueryResponse    response = statisticalResultCore.query(query);
        List<FacetField> fields   = response.getFacetFields();
        for (FacetField.Count values : fields.get(0).getValues()) {
            centers.add(values.getName());
        }

        return centers;
    }


    public Map<String, String> getKeyValuePairs(SolrClient core, String pivotFacet) {

        Map<String, String> map = new HashMap<>();

        SolrQuery query = new SolrQuery();

        query
            .setQuery("*:*")
            .setRows(0)
            .setFacet(true)
            .setFacetMinCount(1)
            .setFacetLimit(-1)
            .add("facet.pivot", pivotFacet);

        try {
            QueryResponse response = core.query(query);

            for (PivotField pivot : response.getFacetPivot().get(pivotFacet)) {
                if (pivot.getPivot() != null) {
                    for (PivotField parameter : pivot.getPivot()) {
                        String[] row = {pivot.getValue().toString(), parameter.getValue().toString()};
                        map.put(row[0], row[1]);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return map;
    }


    public Map<String, String> getProcedureNamesById() {
        final String pivotFacet = StatisticalResultDTO.PROCEDURE_STABLE_ID + "," + StatisticalResultDTO.PROCEDURE_NAME;
        return getKeyValuePairs(statisticalResultCore, pivotFacet);
    }


    public Map<String, String> getParameterNamesById() {
        final String pivotFacet = StatisticalResultDTO.PARAMETER_STABLE_ID + "," + StatisticalResultDTO.PARAMETER_NAME;
        return getKeyValuePairs(statisticalResultCore, pivotFacet);
    }


    // http://ves-pg-d8.ebi.ac.uk:8986/solr/mp/select?fl=mp_id,mp_term&q=*:*&rows=1000000
    public Map<String, String> getMpNamesById() {

        Map<String, String> map = new HashMap<>();

        SolrQuery query = new SolrQuery();

        query
            .setQuery("*:*")
            .setRows(1000000)
            .setFields(MpDTO.MP_ID, MpDTO.MP_TERM);

        try {
            QueryResponse response = mpCore.query(query);

            SolrDocumentList documents = response.getResults();
            for (SolrDocument document : documents) {
                String mpId   = document.get(MpDTO.MP_ID).toString();
                String mpTerm = document.get(MpDTO.MP_TERM).toString();
                map.put(mpId, mpTerm);
            }

        } catch (Exception e) {
            System.err.println("Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return map;
    }


    public Set<PhenotypeCenterAllServiceBean> getDataByCenter(String center) throws SolrServerException, IOException {

        final String[] fields = {

            StatisticalResultDTO.MARKER_SYMBOL,
            StatisticalResultDTO.MARKER_ACCESSION_ID,
            StatisticalResultDTO.ALLELE_SYMBOL,
            StatisticalResultDTO.ALLELE_ACCESSION_ID,
            StatisticalResultDTO.STRAIN_NAME,
            StatisticalResultDTO.STRAIN_ACCESSION_ID,
            StatisticalResultDTO.COLONY_ID,
            StatisticalResultDTO.PHENOTYPING_CENTER,
            StatisticalResultDTO.ZYGOSITY,
            StatisticalResultDTO.LIFE_STAGE_NAME,
            StatisticalResultDTO.PROCEDURE_STABLE_ID,
            StatisticalResultDTO.PROCEDURE_NAME,
            StatisticalResultDTO.PARAMETER_STABLE_ID,
            StatisticalResultDTO.PARAMETER_NAME,
            StatisticalResultDTO.TOP_LEVEL_MP_TERM_ID,
            StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME,
            StatisticalResultDTO.MP_TERM_ID,
            StatisticalResultDTO.MP_TERM_NAME,
            StatisticalResultDTO.STATUS
        };

        SolrQuery                              query = new SolrQuery();
        HashSet<PhenotypeCenterAllServiceBean> data;
        List<StatisticalResultDTO>             dtos;

        try {
            query.setQuery("*:*");
            query.setFilterQueries(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + center + "\"");
            query.setFields(fields);
            query.setRows(Integer.MAX_VALUE);
            QueryResponse response = statisticalResultCore.query(query);
            dtos = response.getBeans(StatisticalResultDTO.class);
        } catch (Exception e) {
            logger.warn("Error fetching report data. Solr query = {}", query);
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
            throw e;
        }

        data = new HashSet<>();
        for (StatisticalResultDTO dto : dtos) {
            try {
                // Add a separate row for every topLevelMpTermId
                PhenotypeCenterAllServiceBean bean = new PhenotypeCenterAllServiceBean();

                bean.setColonyId(dto.getColonyId());
                bean.setZygosity(dto.getZygosity());
                bean.setGeneAccessionId(dto.getMarkerAccessionId());
                bean.setGeneSymbol(dto.getMarkerSymbol());
                bean.setAlleleAccessionId(dto.getAlleleAccessionId());
                bean.setAlleleSymbol(dto.getAlleleSymbol());
                bean.setStrainName(dto.getStrainName());
                bean.setStrainAccessionId(dto.getStrainAccessionId());
                bean.setProcedureStableId(dto.getProcedureStableId().get(0));
                bean.setParameterStableId(dto.getParameterStableId());
                bean.setTopLevelMpTermId(dto.getTopLevelMpTermId());
                bean.setMpTermId(dto.getMpTermId());
                bean.setLifeStageName(dto.getLifeStageName());
                bean.setStatus(dto.getStatus());

                data.add(bean);
            } catch (Exception e) {
                logger.warn("Error processing StatisticalResultDTO {}", dto);
                logger.error(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        return data;
    }
}