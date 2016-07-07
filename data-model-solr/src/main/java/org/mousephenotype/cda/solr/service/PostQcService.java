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
package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONObject;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.solr.web.dto.GraphTestDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("postqcService")
public class PostQcService extends AbstractGenotypePhenotypeService implements WebStatus {

    @Autowired
    @Qualifier("genotypePhenotypeCore")
    HttpSolrServer solr;

    public PostQcService() {
        super();
        isPreQc = false;
    }

    @PostConstruct
    public void postSetup() {
        // Ensure the superclass attributes are set
        super.solr = solr;
    }

    /**
     * Returns a list of <code>count GraphTestDTO</code> instances matching the
     * given parameter stable ids.
     *
     * @param parameterStableIds a list of parameter stable ids used to feed the
     *                           query
     * @param count the number of <code>GraphTestDTO</code> instances to return
     *
     * @return a list of <code>count GraphTestDTO</code> instances matching the
     * given parameter stable ids.
     *
     * @throws SolrServerException
     */
    public List<GraphTestDTO> getGeneAccessionIdsByParameterStableId(List<String> parameterStableIds, int count) throws SolrServerException {
      
    	List<GraphTestDTO> retVal = new ArrayList<>();

        if (count < 1){
            return retVal;
        }

        String queryString = "";
        for (String parameterStableId : parameterStableIds) {
            if ( ! queryString.isEmpty()) {
                queryString += " OR ";
            }
            queryString += GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":" + parameterStableId;
        }
        SolrQuery query = new SolrQuery();
        // http://ves-ebi-d0:8090/mi/impc/dev/solr/experiment/select?q=observation_type%3Acategorical&rows=12&wt=json&indent=true&facet=true&facet.field=parameter_stable_id
        query
            .setQuery(queryString)
            .setRows(count)
            .setFields(GenotypePhenotypeDTO.PARAMETER_STABLE_ID, GenotypePhenotypeDTO.MARKER_ACCESSION_ID, GenotypePhenotypeDTO.PROCEDURE_NAME,
            		GenotypePhenotypeDTO.PARAMETER_NAME)
            .add("group", "true")
            .add("group.field", GenotypePhenotypeDTO.MARKER_ACCESSION_ID)
            .add("group.limit", Integer.toString(count))
        ;

        QueryResponse response = solr.query(query);
        List<GroupCommand> groupResponse = response.getGroupResponse().getValues();
        for (GroupCommand groupCommand : groupResponse) {
            List<Group> groups = groupCommand.getValues();
            for (Group group : groups) {
                SolrDocumentList docs = group.getResult();

                SolrDocument doc = docs.get(0);                                                    // All elements in this collection have the same mgi_accession_id.
                GraphTestDTO geneGraph = new GraphTestDTO();
                geneGraph.setParameterStableId((String)doc.get(GenotypePhenotypeDTO.PARAMETER_STABLE_ID));
                geneGraph.setMgiAccessionId((String)doc.get(GenotypePhenotypeDTO.MARKER_ACCESSION_ID));
                geneGraph.setParameterName((String)doc.get(GenotypePhenotypeDTO.PARAMETER_NAME));
                geneGraph.setProcedureName((String)doc.get(GenotypePhenotypeDTO.PROCEDURE_NAME));
                retVal.add(geneGraph);
                count--;
                if (count == 0) {
                    return retVal;
                }
            }
        }

        return retVal;
    }

    /**
     * @author ilinca
     * @since 2016/07/05
     * @param anatomyId
     * @return Number of genes in g-p core for anatomy term given. 
     * @throws SolrServerException
     */
    public Integer getGenesByAnatomy(String anatomyId) 
    throws SolrServerException{
    	
    	 SolrQuery query = new SolrQuery();
         query.setQuery("(" + GenotypePhenotypeDTO.ANATOMY_TERM_ID + ":\"" + anatomyId + "\" OR " + 
        		 GenotypePhenotypeDTO.INTERMEDIATE_ANATOMY_TERM_ID + ":\"" + anatomyId + "\" OR " +
        		 GenotypePhenotypeDTO.TOP_LEVEL_ANATOMY_TERM_ID + ":\"" + anatomyId + "\")")
             .setRows(0)
             .add("group", "true")
             .add("group.field", GenotypePhenotypeDTO.MARKER_ACCESSION_ID)
             .add("group.ngroups", "true")
             .add("wt","json");

         JSONObject groups = new JSONObject(solr.query(query).getResponse().get("grouped").toString().replaceAll("=",":"));
         
         return groups.getJSONObject(GenotypePhenotypeDTO.MARKER_ACCESSION_ID).getInt("ngroups");
    }
    
	@Override
	public long getWebStatus() throws SolrServerException {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + solr.getBaseURL() + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}
	@Override
	public String getServiceName(){
		return "posQc (genotype-phenotype core)";
	}

}
