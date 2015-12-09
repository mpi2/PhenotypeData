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
package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.EmapOntologyDAO;
import org.mousephenotype.cda.indexers.beans.OntologyTermEmapBeanList;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.EmapDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.mousephenotype.cda.db.dao.OntologyDAO.BATCH_SIZE;

/**
 * @author ckchen based on mike relac's MaIndexer
 *
 */
public class EmapIndexer extends AbstractIndexer {
    CommonUtils commonUtils = new CommonUtils();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("alleleReadOnlyIndexing")
    private SolrServer alleleCore;

    @Autowired
    @Qualifier("preqcReadOnlyIndexing")
    private SolrServer preqcCore;

    @Autowired
    @Qualifier("genotypePhenotypeReadOnlyIndexing")
    private SolrServer genotypePhenotypeCore;
    
    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;

    @Autowired
    EmapOntologyDAO emapOntologyService;
    
    /**
     * Destination Solr core
     */
    @Autowired
    @Qualifier("emapIndexing")
    private SolrServer emapCore;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;


    private static Connection komp2DbConnection;
    private static Connection ontoDbConnection;

   
    // Maps of supporting database content
    
    //Map<String, List<MPHPBean>> mphpBeans;
    Map<String, List<Integer>> termNodeIds;
    Map<Integer, List<OntologyTermBean>> topLevelTerms;
    
    // Intermediate node IDs and terms can also be used for allChildren
    Map<Integer, List<Integer>> intermediateNodeIds;
    Map<Integer, List<Integer>> childNodeIds;
    // Intermediate terms can also be used for parents
    Map<Integer, List<OntologyTermBean>> intermediateTerms;
    Map<Integer, List<Integer>> parentNodeIds;
    // Use single synonym hash
    Map<String, List<String>> emapTermSynonyms;
    //Map<String, List<String>> ontologySubsets;
    //Map<String, List<String>> goIds;

    // Alleles
    Map<String, List<AlleleDTO>> alleles;

    // Phenotype call summaries (1)
    //Map<String, List<PhenotypeCallSummaryBean>> phenotypes1;
    //Map<String, List<String>> impcBeans;
    //Map<String, List<String>> legacyBeans;

    // Phenotype call summaries (2)
    //Map<String, List<PhenotypeCallSummaryBean>> phenotypes2;
    //Map<String, List<MPStrainBean>> strains;
    //Map<String, List<ParamProcedurePipelineBean>> pppBeans;

    public EmapIndexer() {
    
    }

    @Override
    public void validateBuild() throws IndexerException {
        super.validateBuild(emapCore);
    }

    @Override
    public void run() throws IndexerException {
        int count = 0;
        long start = System.currentTimeMillis();

        initializeDatabaseConnections();

    	try {
            initialiseSupportingBeans();

            List<EmapDTO> emapBatch = new ArrayList(BATCH_SIZE);

            // Add all emap terms to the index.
            List<OntologyTermBean> beans = emapOntologyService.getAllTerms();
           
            for (OntologyTermBean bean : beans) {
                EmapDTO emap = new EmapDTO();

                String emapId = bean.getId();
                // Set scalars.
                emap.setDataType("emap");
                emap.setEmapId(emapId);
                emap.setEmapTerm(bean.getName());
                
                emap.setEmapNodeId(termNodeIds.get(emapId));
                buildNodes(emap);
                
                // Set collections.
                OntologyTermEmapBeanList sourceList = new OntologyTermEmapBeanList(emapOntologyService, bean.getId());
               
                emap.setEmapTermSynonym(sourceList.getSynonyms());

                emap.setChildEmapId(sourceList.getChildren().getIds());
                emap.setChildEmapIdTerm(sourceList.getChildren().getId_name_concatenations());
                emap.setChildEmapTerm(sourceList.getChildren().getNames());
                emap.setChildEmapTermSynonym(sourceList.getChildren().getSynonyms());

                emap.setSelectedTopLevelEmapId(sourceList.getTopLevels().getIds());
                emap.setSelectedTopLevelEmapTerm(sourceList.getTopLevels().getNames());
                emap.setSelectedTopLevelEmapTermSynonym(sourceList.getTopLevels().getSynonyms());

                // Image association fields here when we have data
                

                count ++;
                emapBatch.add(emap);
                if (emapBatch.size() == BATCH_SIZE) {
                    // Update the batch, clear the list
                    documentCount += emapBatch.size();
                    emapCore.addBeans(emapBatch, 60000);
                    emapBatch.clear();
                }
            }

            // Make sure the last batch is indexed
            if (emapBatch.size() > 0) {
                documentCount += emapBatch.size();
                emapCore.addBeans(emapBatch, 60000);
                count += emapBatch.size();
            }

            // Send a final commit
            emapCore.commit();

        } catch (SolrServerException| IOException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));
    }

    /**
     * Initialize the database connections required
     *
     * @throws IndexerException when there's an issue
     */
    
    private void initializeDatabaseConnections() throws IndexerException {

        try {
            komp2DbConnection = komp2DataSource.getConnection();
            ontoDbConnection = ontodbDataSource.getConnection();
        } catch (SQLException e) {
            throw new IndexerException(e);
        }

    }

    private void initialiseSupportingBeans() throws IndexerException {
        try {
            // Grab all the supporting database content
            
            termNodeIds = getNodeIds();
            topLevelTerms = getTopLevelTerms();
            
            // Intermediate node terms can also be used for allChildren
            intermediateNodeIds = getIntermediateNodeIds();
            
            //ChildNodeIds is inverse of intermediateNodeIds
            childNodeIds = getChildNodeIds();
            
            // Intermediate terms can also be used for parents
            intermediateTerms = getIntermediateTerms();
            
            parentNodeIds = getParentNodeIds();
            // Use single synonym hash
            emapTermSynonyms = getEmapTermSynonyms();

            // Alleles
            alleles = IndexerMap.getGeneToAlleles(alleleCore);

            // Phenotype call summaries (1)
            //phenotypes1 = getPhenotypeCallSummary1();
            //impcBeans = getImpcPipe();
            //legacyBeans = getLegacyPipe();

            // Phenotype call summaries (2)
            //phenotypes2 = getPhenotypeCallSummary2();
            //strains = getStrains();
            //pppBeans = getPPPBeans();
        } catch (SQLException e) {
            throw new IndexerException(e);
        }
    }

    
    private Map<String, List<Integer>> getNodeIds() throws SQLException {
        Map<String, List<Integer>> beans = new HashMap<>();

        String q = "select nt.node_id, ti.term_id from emap_term_infos ti, emap_node2term nt where ti.term_id=nt.term_id and ti.term_id !='EMAP:0'";
        PreparedStatement ps = ontoDbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            String tId = rs.getString("term_id");
            int nId = rs.getInt("node_id");
            if ( ! beans.containsKey(tId)) {
                beans.put(tId, new ArrayList<Integer>());
            }
            beans.get(tId).add(nId);
            count ++;
        }
        logger.debug(" Added {} node Ids", count);

        return beans;
    }

    
    private Map<Integer, List<OntologyTermBean>> getTopLevelTerms() throws SQLException {
        Map<Integer, List<OntologyTermBean>> beans = new HashMap<>();

        String q = "select lv.node_id as emap_node_id, ti.term_id, ti.name, ti.definition, concat(ti.name, '___', ti.term_id) as top_level_emap_term_id from emap_node_top_level lv inner join emap_node2term nt on lv.top_level_node_id=nt.node_id inner join emap_term_infos ti on nt.term_id=ti.term_id and ti.term_id !='EMAP:0'";
        PreparedStatement ps = ontoDbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            int nId = rs.getInt("emap_node_id");

            OntologyTermBean bean = new OntologyTermBean();
            bean.setId(rs.getString("term_id"));
            bean.setName(rs.getString("name"));
            bean.setDefinition(rs.getString("definition"));
            bean.setTopLevelTermId(rs.getString("top_level_emap_term_id"));

            if ( ! beans.containsKey(nId)) {
                beans.put(nId, new ArrayList<OntologyTermBean>());
            }
            beans.get(nId).add(bean);
            count ++;
        }
        logger.debug(" Added {} top level terms", count);

        return beans;
    }

    /**
     * Build a map of child node ID -> node IDs, to use to build the
     * intermediate nodes.
     *
     * @return the map.
     * @throws SQLException
     */
    
    private Map<Integer, List<Integer>> getIntermediateNodeIds() throws SQLException {
        Map<Integer, List<Integer>> beans = new HashMap<>();

        String q = "select node_id, child_node_id from emap_node_subsumption_fullpath";
        PreparedStatement ps = ontoDbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            int childId = rs.getInt("child_node_id");
            int nodeId = rs.getInt("node_id");
            if ( ! beans.containsKey(childId)) {
                beans.put(childId, new ArrayList<Integer>());
            }
            beans.get(childId).add(nodeId);
            count ++;
        }
        logger.debug(" Added {} intermediate node Ids", count);

        return beans;
    }

    /**
     * Build a map of node ID -> child node IDs.
     *
     * @return the map.
     * @throws SQLException
     */
    
    private Map<Integer, List<Integer>> getChildNodeIds() throws SQLException {
        Map<Integer, List<Integer>> beans = new HashMap<>();

        String q = "select node_id, child_node_id from emap_node_subsumption_fullpath";
        PreparedStatement ps = ontoDbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            int nId = rs.getInt("node_id");
            int childId = rs.getInt("child_node_id");
            if ( ! beans.containsKey(nId)) {
                beans.put(nId, new ArrayList<Integer>());
            }
            beans.get(nId).add(childId);
            count ++;
        }
        logger.debug(" Added {} child node Ids", count);

        return beans;
    }

    
    private Map<Integer, List<OntologyTermBean>> getIntermediateTerms() throws SQLException {
        Map<Integer, List<OntologyTermBean>> beans = new HashMap<>();

        String q = "select nt.node_id, ti.term_id, ti.name, ti.definition from emap_term_infos ti, emap_node2term nt where ti.term_id=nt.term_id and ti.term_id !='EMAP:25785'";
        PreparedStatement ps = ontoDbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            int nId = rs.getInt("node_id");

            OntologyTermBean bean = new OntologyTermBean();
            bean.setId(rs.getString("term_id"));
            bean.setName(rs.getString("name"));
            bean.setDefinition(rs.getString("definition"));

            if ( ! beans.containsKey(nId)) {
                beans.put(nId, new ArrayList<OntologyTermBean>());
            }
            beans.get(nId).add(bean);
            count ++;
        }
        logger.debug(" Added {} intermediate level terms", count);

        return beans;
    }

    
    private Map<Integer, List<Integer>> getParentNodeIds() throws SQLException {
        Map<Integer, List<Integer>> beans = new HashMap<>();

        String q = "select parent_node_id, child_node_id from emap_parent_children";
        PreparedStatement ps = ontoDbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            int nId = rs.getInt("child_node_id");
            int parentId = rs.getInt("parent_node_id");
            if ( ! beans.containsKey(nId)) {
                beans.put(nId, new ArrayList<Integer>());
            }
            beans.get(nId).add(parentId);
            count ++;
        }
        logger.debug(" Added {} parent node Ids", count);

        return beans;
    }

    private Map<String, List<String>> getEmapTermSynonyms() throws SQLException {
        Map<String, List<String>> beans = new HashMap<>();

        String q = "select term_id, syn_name from emap_synonyms";
        PreparedStatement ps = ontoDbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            String tId = rs.getString("term_id");
            String syn = rs.getString("syn_name");
            if ( ! beans.containsKey(tId)) {
                beans.put(tId, new ArrayList<String>());
            }
            beans.get(tId).add(syn);
            count ++;
        }
        logger.debug(" Added {} EMAP term synonyms", count);

        return beans;
    }

    private void buildNodes(EmapDTO emap) {
        List<Integer> nodeIds = termNodeIds.get(emap.getEmapId());

        if (nodeIds != null) {
            for (Integer nodeId : nodeIds) {
                // Build the top level nodes
                buildTopLevelNodes(emap, nodeId);
                buildIntermediateLevelNodes(emap, nodeId);
                //buildChildLevelNodes(emap, nodeId);
                buildParentLevelNodes(emap, nodeId);
            }
        }
    }
    

    private void buildTopLevelNodes(EmapDTO emap, int nodeId) {
        List<OntologyTermBean> topLevelTermBeans = topLevelTerms.get(nodeId);
        if (topLevelTermBeans != null) {
            List<String> topLevelEmapIds = new ArrayList<>(topLevelTermBeans.size());
            List<String> topLevelEmapTerms = new ArrayList<>(topLevelTermBeans.size());
            List<String> topLevelEmapTermIds = new ArrayList<>(topLevelTermBeans.size());
            Set<String> topLevelEmapSynonyms = new HashSet<>();

            for (OntologyTermBean bean : topLevelTermBeans) {
                topLevelEmapIds.add(bean.getId());
                topLevelEmapTerms.add(bean.getName());
                topLevelEmapTermIds.add(bean.getTopLevelTermId());
                if (emapTermSynonyms != null){
	                if (emapTermSynonyms.containsKey(bean.getId())) {
	                	topLevelEmapSynonyms.addAll(emapTermSynonyms.get(bean.getId()));
	                }
                }
            }

            if (emap.getTopLevelEmapId() == null) {
                emap.setTopLevelEmapId(new ArrayList<String>());
                emap.setTopLevelEmapTerm(new ArrayList<String>());
                emap.setTopLevelEmapTermId(new ArrayList<String>());
                emap.setTopLevelEmapTermSynonym(new ArrayList<String>());
            }
            emap.getTopLevelEmapId().addAll(topLevelEmapIds);
            emap.getTopLevelEmapTerm().addAll(topLevelEmapTerms);
            emap.getTopLevelEmapTermId().addAll(topLevelEmapTermIds);
            emap.getTopLevelEmapTermSynonym().addAll(new ArrayList<>(topLevelEmapSynonyms));
        }
    }

    private void buildIntermediateLevelNodes(EmapDTO emap, int nodeId) {
        if (intermediateNodeIds.containsKey(nodeId)) {
            List<String> intermediateTermIds = new ArrayList<>();
            List<String> intermediateTermNames = new ArrayList<>();
            Set<String> intermediateSynonyms = new HashSet<>();
            for (Integer intId : intermediateNodeIds.get(nodeId)) {
            	if ( intermediateTerms.get(intId) != null ){
	                for (OntologyTermBean bean : intermediateTerms.get(intId)) {
	                    intermediateTermIds.add(bean.getId());
	                    intermediateTermNames.add(bean.getName());
	                    if (emapTermSynonyms != null){
		                    if (emapTermSynonyms.containsKey(bean.getId())) {
		                        intermediateSynonyms.addAll(emapTermSynonyms.get(bean.getId()));
		                    }
	                    }
	                }
            	}
            }

            if (emap.getIntermediateEmapId() == null) {
                emap.setIntermediateEmapId(new ArrayList<String>());
                emap.setIntermediateEmapTerm(new ArrayList<String>());
                emap.setIntermediateEmapTermSynonym(new ArrayList<String>());
            }
            emap.getIntermediateEmapId().addAll(intermediateTermIds);
            emap.getIntermediateEmapTerm().addAll(intermediateTermNames);
            emap.getIntermediateEmapTermSynonym().addAll(new ArrayList<>(intermediateSynonyms));
        }
    }

    private void buildChildLevelNodes(EmapDTO emap, int nodeId) {
        if (childNodeIds.containsKey(nodeId)) {
            List<String> childTermIds = new ArrayList<>();
            List<String> childTermNames = new ArrayList<>();
            Set<String> childSynonyms = new HashSet<>();

            for (Integer childId : childNodeIds.get(nodeId)) {
            	if (intermediateTerms != null && intermediateTerms.get(childId) != null ){
	                for (OntologyTermBean bean : intermediateTerms.get(childId)) {
	                    childTermIds.add(bean.getId());
	                    childTermNames.add(bean.getName());
	                    if (emapTermSynonyms != null){
		                    if (emapTermSynonyms.containsKey(bean.getId())) {
		                        childSynonyms.addAll(emapTermSynonyms.get(bean.getId()));
		                    }
	                    }
	                }
            	}
            }

            if (emap.getChildEmapId() == null) {
                emap.setChildEmapId(new ArrayList<String>());
                emap.setChildEmapTerm(new ArrayList<String>());
                emap.setChildEmapTermSynonym(new ArrayList<String>());
            }
            emap.getChildEmapId().addAll(childTermIds);
            emap.getChildEmapTerm().addAll(childTermNames);
            emap.getChildEmapTermSynonym().addAll(new ArrayList<>(childSynonyms));
        }
    }

    private void buildParentLevelNodes(EmapDTO emap, int nodeId) {
        List<String> parentTermIds = new ArrayList<>();
        List<String> parentTermNames = new ArrayList<>();
        Set<String> parentSynonyms = new HashSet<>();

        for (Integer parentId : parentNodeIds.get(nodeId)) {
            if (intermediateTerms.containsKey(parentId)) {
                for (OntologyTermBean bean : intermediateTerms.get(parentId)) {
                    parentTermIds.add(bean.getId());
                    parentTermNames.add(bean.getName());
                }
            }
            if (emapTermSynonyms != null){
	            if (emapTermSynonyms.containsKey(parentId.toString())) {
	                parentSynonyms.addAll(emapTermSynonyms.get(parentId.toString()));
	            }
            }
        }

        if (emap.getParentEmapId() == null) {
            emap.setParentEmapId(new ArrayList<String>());
            emap.setParentEmapTerm(new ArrayList<String>());
            emap.setParentEmapTermSynonym(new ArrayList<String>());
        }
        emap.getParentEmapId().addAll(parentTermIds);
        emap.getParentEmapTerm().addAll(parentTermNames);
        emap.getParentEmapTermSynonym().addAll(parentSynonyms);
    }


    // PROTECTED METHODS


    public static void main(String[] args) throws IndexerException, SQLException {

        EmapIndexer main = new EmapIndexer();
        main.initialise(args);
        main.run();
        main.validateBuild();
    }
}