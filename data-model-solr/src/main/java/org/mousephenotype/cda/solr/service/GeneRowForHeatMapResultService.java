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
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.db.dao.BiologicalModelDAO;
import org.mousephenotype.cda.db.dao.DatasourceDAO;
import org.mousephenotype.cda.db.dao.OrganisationDAO;
import org.mousephenotype.cda.db.dao.ProjectDAO;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.mousephenotype.cda.solr.web.dto.HeatMapCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class GeneRowForHeatMapResultService {

    @Autowired
    BiologicalModelDAO bmDAO;

    @Autowired
    DatasourceDAO datasourceDAO;

    @Autowired
    OrganisationDAO organisationDAO;

    @Autowired
	@Qualifier("postqcService")
    AbstractGenotypePhenotypeService gpService;

    @Autowired
    ProjectDAO projectDAO;


    private static final Logger LOG = LoggerFactory.getLogger(GeneRowForHeatMapResultService.class);

    Map<String, ArrayList<String>> maleParamToGene = null;
    Map<String, ArrayList<String>> femaleParamToGene = null;

	private SolrClient solr;

	@Autowired
	StatisticalResultService stasticalResultsService;


	public GeneRowForHeatMapResultService() {
	}


	public GeneRowForHeatMapResultService(String solrUrl) {
		solr = new HttpSolrClient(solrUrl);
	}


	    public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, Map<String, Set<String>> map, String resourceName) {

        GeneRowForHeatMap row = new GeneRowForHeatMap(accession);
        Map<String, HeatMapCell> paramPValueMap = new HashMap<>();

        if (gene != null) {
            row.setSymbol(gene.getSymbol());
        } else {
            System.err.println("error no symbol for gene " + accession);
        }

        for (String procedure : map.get(accession)) {
        	paramPValueMap.put(procedure, null);
        }

        SolrQuery q = new SolrQuery()
                .setQuery(StatisticalResultDTO.MARKER_ACCESSION_ID + ":\"" + accession + "\"")
                .addFilterQuery(StatisticalResultDTO.RESOURCE_NAME + ":\"" + resourceName + "\"")
                .setSort(StatisticalResultDTO.P_VALUE, SolrQuery.ORDER.asc)
                .addField(StatisticalResultDTO.PROCEDURE_STABLE_ID)
                .addField(StatisticalResultDTO.STATUS)
                .addField(StatisticalResultDTO.P_VALUE)
                .setRows(10000000);
        q.add("group", "true");
        q.add("group.field", StatisticalResultDTO.PROCEDURE_STABLE_ID);
        q.add("group.sort", StatisticalResultDTO.P_VALUE + " asc");

        try {
        	GroupCommand groups = solr.query(q).getGroupResponse().getValues().get(0);
            for (Group group:  groups.getValues()){
            	HeatMapCell cell = new HeatMapCell();
            	SolrDocument doc = group.getResult().get(0);
            	cell.setxAxisKey(doc.get(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString());
            	if(Double.valueOf(doc.getFieldValue(StatisticalResultDTO.P_VALUE).toString()) < 0.0001){
            		cell.setStatus("Significant call");
            	} else if (doc.getFieldValue(StatisticalResultDTO.STATUS).toString().equals("Success")){
            			cell.setStatus("Data analysed, no significant call");
            		} else {
            			cell.setStatus("Could not analyse");
            		}
            	paramPValueMap.put(doc.getFieldValue(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString(), cell);
            }
            row.setXAxisToCellMap(paramPValueMap);
        } catch (SolrServerException | IOException ex) {
            LOG.error(ex.getMessage());
        }
        return row;
    }

    public List<GeneRowForHeatMap> getSecondaryProjectMapForResource(String resourceName) {

    	List<GeneRowForHeatMap> res = new ArrayList<>();
        HashMap<String, GeneRowForHeatMap> geneRowMap = new HashMap<>(); // <geneAcc, row>
        List<BasicBean> procedures = stasticalResultsService.getProceduresForDataSource(resourceName);

        for (BasicBean procedure : procedures){
	        SolrQuery q = new SolrQuery()
	        .setQuery(StatisticalResultDTO.RESOURCE_NAME + ":\"" + resourceName + "\"")
	        .addFilterQuery(StatisticalResultDTO.PROCEDURE_STABLE_ID + ":" + procedure.getId())
	        .setSort(StatisticalResultDTO.P_VALUE, SolrQuery.ORDER.asc)
	        .addField(StatisticalResultDTO.PROCEDURE_STABLE_ID)
	        .addField(StatisticalResultDTO.MARKER_ACCESSION_ID)
	        .addField(StatisticalResultDTO.MARKER_SYMBOL)
	        .addField(StatisticalResultDTO.STATUS)
	        .addField(StatisticalResultDTO.P_VALUE)
	        .setRows(10000000);
	        q.add("group", "true");
	        q.add("group.field", StatisticalResultDTO.MARKER_ACCESSION_ID);
	        q.add("group.sort", StatisticalResultDTO.P_VALUE + " asc");

	        try {
	        	GroupCommand groups = solr.query(q).getGroupResponse().getValues().get(0);

		        for (Group group:  groups.getValues()){
		        	GeneRowForHeatMap row;
		            HeatMapCell cell = new HeatMapCell();
		            SolrDocument doc = group.getResult().get(0);
		        	String geneAcc = doc.get(StatisticalResultDTO.MARKER_ACCESSION_ID).toString();
		            Map<String, HeatMapCell> xAxisToCellMap = new HashMap<>();

		        	if (geneRowMap.containsKey(geneAcc)){
		        		row = geneRowMap.get(geneAcc);
		        		xAxisToCellMap = row.getXAxisToCellMap();
		        	} else {
		        		row = new GeneRowForHeatMap(geneAcc);
		        		row.setSymbol(doc.get(StatisticalResultDTO.MARKER_SYMBOL).toString());
			        	xAxisToCellMap.put(procedure.getId(), null);
		        	}
		            cell.setxAxisKey(doc.get(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString());
		            if(Double.valueOf(doc.getFieldValue(StatisticalResultDTO.P_VALUE).toString()) < 0.0001){
		            	cell.setStatus(HeatMapCell.THREE_I_DEVIANCE_SIGNIFICANT);
		            } else if (doc.getFieldValue(StatisticalResultDTO.STATUS).toString().equals("Success")){
		            		cell.setStatus(HeatMapCell.THREE_I_DATA_ANALYSED_NOT_SIGNIFICANT);
		            } else {
		            	cell.setStatus(HeatMapCell.THREE_I_COULD_NOT_ANALYSE);
		            }
		            xAxisToCellMap.put(doc.getFieldValue(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString(), cell);
			        row.setXAxisToCellMap(xAxisToCellMap);
			        geneRowMap.put(geneAcc, row);
		            }
		        } catch (SolrServerException | IOException ex) {
		            LOG.error(ex.getMessage());
		        }
        }

        res = new ArrayList<>(geneRowMap.values());
        Collections.sort(res, new GeneRowForHeatMap3IComparator());

        return res;
    }



    /*
	 * End of method for PhenotypeCallSummarySolrImpl
	 */
	public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, List<BasicBean> xAxisBeans, Map<String, List<String>> geneToTopLevelMpMap) {

		GeneRowForHeatMap row = new GeneRowForHeatMap(accession);
		if (gene != null) {
			row.setSymbol(gene.getSymbol());
		} else {
			System.err.println("error no symbol for gene " + accession);
		}

		Map<String, HeatMapCell> xAxisToCellMap = new HashMap<>();
		for (BasicBean xAxisBean : xAxisBeans) {
			HeatMapCell cell = new HeatMapCell();
			if (geneToTopLevelMpMap.containsKey(accession)) {

				List<String> mps = geneToTopLevelMpMap.get(accession);
				// cell.setLabel("No Phenotype Detected");
				if (mps != null && !mps.isEmpty()) {
					if (mps.contains(xAxisBean.getId())) {
						cell.setxAxisKey(xAxisBean.getId());
						cell.setLabel("Data Available");
						cell.setStatus("Data Available");
					} else {
						cell.setStatus("No MP");
					}
				} else {
					// System.err.println("mps are null or empty");
					cell.setStatus("No MP");
				}
			} else {
				// if no doc found for the gene then no data available
				cell.setStatus("No Data Available");
			}
			xAxisToCellMap.put(xAxisBean.getId(), cell);
		}
		row.setXAxisToCellMap(xAxisToCellMap);

		return row;
	}




    public class GeneRowForHeatMap3IComparator implements Comparator<GeneRowForHeatMap> {

    	@Override
    	public int compare(GeneRowForHeatMap row1, GeneRowForHeatMap row2) {

    		int score1 = scoreGeneRowForHeatMap(row1);
    		int score2 = scoreGeneRowForHeatMap(row2);
    		return Integer.compare(score2, score1);
    	}

    	private int scoreGeneRowForHeatMap(GeneRowForHeatMap row){
    		Map<String, HeatMapCell> cells = row.getXAxisToCellMap();
    		int score = 0;
    		for (HeatMapCell cell : cells.values()){
    			if (cell.getStatus().equalsIgnoreCase(HeatMapCell.THREE_I_COULD_NOT_ANALYSE)){
    				score += 1;
    			}
    			else if (cell.getStatus().equalsIgnoreCase(HeatMapCell.THREE_I_DATA_ANALYSED_NOT_SIGNIFICANT)){
    				score += 2;
    			}
    			else if (cell.getStatus().equalsIgnoreCase(HeatMapCell.THREE_I_DEVIANCE_SIGNIFICANT)){
    				score += 20;
    			}
    		}
    		return score;
    	}

    }

}
