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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.solr.web.dto.GraphTestDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service("postqcService")
public class PostQcService extends AbstractGenotypePhenotypeService implements WebStatus {

    @Autowired
    @Qualifier("genotypePhenotypeCore")
    SolrClient solr;

    private Map<String,Long> documentCountGyGene; //<marker_acc,count>

    public PostQcService() {
        super();
        isPreQc = false;
    }

    @PostConstruct
    public void postSetup() {
        // Ensure the superclass attributes are set
        super.solr = solr;
        documentCountGyGene = getDocumentCountByGene();
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
     * @throws SolrServerException, IOException
     */
    public List<GraphTestDTO> getGeneAccessionIdsByParameterStableId(List<String> parameterStableIds, int count) throws SolrServerException, IOException {
      
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
     * Used by chord diagram
     * @param topLevelMpTerms the mp terms are used with AND. If not null returns data for genes that have ALL phenotypes in the passed list.
     * @return
     */
    public JSONObject getPleiotropyMatrix(List<String> topLevelMpTerms) throws IOException, SolrServerException {


        try {

            SolrQuery query = getPleiotropyQuery(topLevelMpTerms);
            QueryResponse queryResponse = solr.query(query, SolrRequest.METHOD.POST);
            Set<String> facets = getFacets(queryResponse).get(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME).keySet();

            Map<String, Set<String>> genesByTopLevelMp = new HashMap<>(); // <mp, <genes>>
            Integer[][] matrix = new Integer[facets.size()][facets.size()];
            // initialize labels -> needed to keep track of order for the matrix cells
            List<String>  matrixLabels = facets.stream().collect(Collectors.toList());

            // Fill matrix with 0s
            for (int i = 0; i < matrixLabels.size(); i++) {
                for (int j = 0; j< matrixLabels.size(); j++) {
                    matrix[i][j] = 0;
                }
                genesByTopLevelMp.put(matrixLabels.get(i), new HashSet<>());
            }


            Map<String, List<String>> facetPivotResults = getFacetPivotResults(queryResponse, query.get("facet.pivot")); // <gene, <top_level_mps>>

            // Count genes associated to each pair of top-level mps. Gene count not g-p doc count, nor allele.
            for (String gene : facetPivotResults.keySet()) {
                List<String> mpTerms = facetPivotResults.get(gene);
                for (int i = 0; i < matrixLabels.size(); i++){
                    String mpI = matrixLabels.get(i);
                    if (mpTerms.contains(mpI)) {
                        Set<String> temp = genesByTopLevelMp.get(mpI);
                        temp.add(gene);
                        genesByTopLevelMp.put(mpI, temp);
                        if (mpTerms.size() > 1) { // Other phenotypes too
                            for (int j = 0; j < matrixLabels.size(); j++) {
                                String mpJ = matrixLabels.get(j);
                                if (mpTerms.contains(mpJ)) {
                                    if (!mpI.equalsIgnoreCase(mpJ)) {
                                        matrix[i][j]++;
                                    }
                                }
                            }
                        } else { // Only one top level mp for this gene; count as self, will display as arch to self
                            matrix[i][i]++;
                        }
                    }
                }
            }

            List<JSONObject> labelList = genesByTopLevelMp.entrySet().stream().map(entry -> new JSONObject().put("name", entry.getKey()).put("geneCount", entry.getValue().size())).collect(Collectors.toList());

            JSONObject result = new JSONObject();
            result.put("matrix", new JSONArray(matrix));
            result.put("labels", labelList);

            return result;

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    public String getPleiotropyDownload(List<String> topLevelMpTerms) throws IOException, SolrServerException {

        SolrQuery query = getPleiotropyQuery(topLevelMpTerms);
        query.add("wt", "xslt");
        query.add("tr", "pivot.xsl");

        HttpURLConnection connection = (HttpURLConnection) new URL(SolrUtils.getBaseURL(solr) + "/select?" + query).openConnection();
        BufferedReader br = new BufferedReader( new InputStreamReader(connection.getInputStream()));

        return br.lines().collect(Collectors.joining("\n"));

    }


    /**
     * The query differs depending on topLevelMpTerms. If at least one term is passed, we need to find out the list of
     * genes that have the required phenotypes and use them in the query. We can't use the list of MP terms themselves
     * as t will filter out the other phenotype associations for the genese we're interested in.
     * @param topLevelMpTerms
     * @author ilinca
     * @return Solr query for g-p associations of genes that have all phenotypes passed in topLevelMpTerms.
     * @throws IOException
     * @throws SolrServerException
     */
    private SolrQuery getPleiotropyQuery(List<String> topLevelMpTerms) throws IOException, SolrServerException {

        String pivot = GenotypePhenotypeDTO.MARKER_SYMBOL  + "," + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME;
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFacet(true)
            .setFacetLimit(-1);
        query.add("facet.pivot", pivot);
        query.addFacetField(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);

        if (topLevelMpTerms != null) {

            // We want data for genes that have ALL top level phenotypes in the list
            String interimPivot = GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + "," + GenotypePhenotypeDTO.MARKER_SYMBOL;
            SolrQuery interimQuery = new SolrQuery()
                    .setFacet(true)
                    .setFacetLimit(-1)
                    .setQuery("*:*")
                    .addFilterQuery(topLevelMpTerms.stream().collect(Collectors.joining("\" OR \"", GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + ":(\"", "\")")));
            interimQuery.add("facet.pivot", interimPivot);

            // Filter out the pivot facets for un-wanted MP top level terms. We can get other top levels in the facets due to multiple parents.
            Map<String, Set<String>> genesByMpTopLevel = getFacetPivotResultsKeepCount(solr.query(interimQuery), interimPivot).entrySet().stream()
                    .filter(entry -> topLevelMpTerms.contains(entry.getKey()))
                    .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().keySet()));

            // Instantiate commonGenes with itself as it will work as identity on first set intersection (intersection of same sets)
            Set<String> commonGenes = genesByMpTopLevel.values().iterator().next();
            // Keep only genes that are present in all top level mp groups
            commonGenes = genesByMpTopLevel.values().stream()
                    .reduce(commonGenes, (a, b) -> {
                        a.retainAll(b);
                        return a;
                    });

            query.addFilterQuery(commonGenes.stream().collect(Collectors.joining(" OR ", GenotypePhenotypeDTO.MARKER_SYMBOL + ":(", ")")));

        }

        return query;
    }


    Map<String,Long> getDocumentCountByGene() {

        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setRows(0);
        query.setFacet(true);
        query.addFacetField(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        query.setFacetLimit(-1);
        query.setFacetMinCount(1);

        try {
            return getFacets(solr.query(query)).get(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        return new HashMap<>();

    }


    public JSONArray getTopLevelPhenotypeIntersection(String mpId){

        String pivot = GenotypePhenotypeDTO.MARKER_ACCESSION_ID + "," + GenotypePhenotypeDTO.MARKER_SYMBOL;
        SolrQuery query = new SolrQuery();
        query.setQuery(mpId == null ? "*:*" : GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\"");
        query.setRows(0);
        query.setFacet(true);
        query.addFacetField(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        query.setFacetLimit(-1);
        query.setFacetMinCount(1);
        query.add("facet.pivot", pivot);

        try {
            QueryResponse response = solr.query(query);
            Map<String,Long> countByGene = getFacets(response).get(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
            Map<String,List<String>>  geneAccSymbol = getFacetPivotResults(response,pivot);

            Set<String> jitter = new HashSet<>();
            JSONArray array = new JSONArray();
            for (String markerAcc: countByGene.keySet()){
                JSONObject obj = new JSONObject();
                Double y = new Double(countByGene.get(markerAcc));
                Double x =(documentCountGyGene.get(markerAcc) - y);
                obj = addJitter(x, y, jitter, obj);
                obj.accumulate("markerAcc", markerAcc);
                obj.accumulate("markerSymbol", geneAccSymbol.get(markerAcc).get(0));
                array.put(obj);
            }

            return array;

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject addJitter(Double x, Double y, Set<String> existingPoints, JSONObject obj){

        String s = x + "_" + y;
        if (!existingPoints.contains(s)){
            obj.accumulate("y", y);
            obj.accumulate("x", x);
            existingPoints.add(s);
        } else {
            if (existingPoints.size()%6 == 0) {
                y += 0.05;
            } else if (existingPoints.size()%6 == 1) {
                x += 0.05;
            } else if (existingPoints.size()%6 == 2) {
                x += 0.05;
                y += 0.05;
            } else if (existingPoints.size()%6 == 3) {
                x -= 0.05;
            } else if (existingPoints.size()%6 == 4) {
                y -= 0.05;
            } else if (existingPoints.size()%6 == 5) {
                x -= 0.05;
                y -= 0.05;
            }
            addJitter(x, y, existingPoints, obj);
        }
        return obj;
    }

    /**
     * @author ilinca
     * @since 2016/07/05
     * @param anatomyId
     * @return Number of genes in g-p core for anatomy term given. 
     * @throws SolrServerException, IOException
     */
    public Integer getGenesByAnatomy(String anatomyId) 
    throws SolrServerException, IOException{
    	
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
	public long getWebStatus() throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(solr) + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}
	@Override
	public String getServiceName(){
		return "posQc (genotype-phenotype core)";
	}

}
