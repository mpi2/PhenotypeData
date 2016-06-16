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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.web.dto.GraphTestDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Service("preqcService")
public class PreQcService extends AbstractGenotypePhenotypeService implements WebStatus {

    @Autowired
    @Qualifier("preqcCore")
    HttpSolrServer solr;

    public PreQcService() {
        super();
        isPreQc = true;
    }

    @PostConstruct
    public void postSetup() {
        // Ensure the superclass attributes are set
        super.solr = solr;
    }


    /**
     * Returns a list of <code>count GraphTestDTO</code> instances.
     *
     * @param count the number of <code>GraphTestDTO</code> instances to return
     *
     * @return a list of <code>count GraphTestDTO</code> instances.
     *
     * @throws SolrServerException
     */
    public List<GraphTestDTO> getGeneAccessionIds(int count) throws SolrServerException {
        List<GraphTestDTO> retVal = new ArrayList<GraphTestDTO>();

        if (count < 1)
            return retVal;

        SolrQuery query = new SolrQuery();
        query
            .setQuery("*:*")
            .setRows(count)
            .setFields("parameter_stable_id, marker_accession_id", "procedure_name", "parameter_name")
            .add("group", "true")
            .add("group.field", "marker_accession_id")
            .add("group.limit", Integer.toString(count))
        ;

        QueryResponse response = solr.query(query);
        List<GroupCommand> groupResponse = response.getGroupResponse().getValues();
        for (GroupCommand groupCommand : groupResponse) {
            List<Group> groups = groupCommand.getValues();
            for (Group group : groups) {
                SolrDocumentList docs = group.getResult();

                SolrDocument doc = docs.get(0);                                 // All elements in this collection have the same mgi_accession_id.
                GraphTestDTO geneGraph = new GraphTestDTO();
                geneGraph.setParameterStableId((String)doc.get("parameter_stable_id"));
                geneGraph.setMgiAccessionId((String)doc.get("marker_accession_id"));
                geneGraph.setParameterName((String)doc.get("parameter_name"));
                geneGraph.setProcedureName((String)doc.get("procedure_name"));
                retVal.add(geneGraph);
                count--;
                if (count == 0) {
                    return retVal;
                }
            }
        }

        return retVal;
    }

	@Override
	public long getWebStatus() throws SolrServerException {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + solr.getBaseURL() + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}

	public String getServiceName(){
		return "preQc";
	}
}
