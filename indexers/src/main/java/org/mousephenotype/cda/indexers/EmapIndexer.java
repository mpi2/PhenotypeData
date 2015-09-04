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
import org.mousephenotype.cda.indexers.exceptions.ValidationException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.EmapDTO;
import org.slf4j.Logger;
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

    private static final Logger logger = LoggerFactory.getLogger(EmapIndexer.class);

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
        Long numFound = getDocumentCount(emapCore);
        System.out.println("num found: " + numFound);
        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual emap document count is " + numFound + "."));

        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " emap documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " emap documents.");
    }

    @Override
    public void run() throws IndexerException, SQLException {

    	//initializeSolrCores();
        initializeDatabaseConnections();

    	try {
    		logger.info("Starting EMAP Indexer...");

            initialiseSupportingBeans();

            List<EmapDTO> emapBatch = new ArrayList(BATCH_SIZE);
            int count = 0;

            logger.info("Starting indexing loop");

            
            
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
            logger.info("Indexed {} beans in total", count);
        } catch (SolrServerException| IOException e) {
            throw new IndexerException(e);
        }


        logger.info("EMAP Indexer complete!");
    }

    /*
    
    @Override
    /*public void run() throws IndexerException {
        logger.info("Starting EMAP Indexer...");

        initializeSolrCores();

        initializeDatabaseConnections();

        initialiseSupportingBeans();

        int count = 0;

        logger.info("Starting indexing loop");
      
        
        
        try {

            // Delete the documents in the core if there are any.
            emapCore.deleteByQuery("*:*");
            emapCore.commit();

            // Loop through the emap_term_infos
            String q = "select 'emap' as dataType, ti.term_id, ti.name, ti.definition from emap_term_infos ti where ti.term_id !='EMAP:0' and ti.term_id !='EMAP:25785' order by ti.term_id";
            
            PreparedStatement ps = ontoDbConnection.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String termId = rs.getString("term_id");
               
                EmapDTO emap = new EmapDTO();
                emap.setDataType(rs.getString("dataType"));
                emap.setEmapId(termId);
                emap.setEmapTerm(rs.getString("name"));
                emap.setEmapDefinition(rs.getString("definition"));
                
                System.out.println(termId + " node id: "+ termNodeIds.get(termId) );
                emap.setEmapNodeId(termNodeIds.get(termId));
                buildNodes(emap);
                //emap.setOntologySubset(ontologySubsets.get(termId));
                emap.setEmapTermSynonym(emapTermSynonyms.get(termId));

                
                
                
                //emap.setGoId(goIds.get(termId));
                //addMaRelationships(emap, termId);
                //addPhenotype1(emap);
                 
                // this sets the number of postqc phenotyping calls of this MP
                //emap.setPhenoCalls(sumPhenotypingCalls(termId)); 
               
                //addPhenotype2(emap);
                
                logger.debug("{}: Built MP DTO {}", count, termId);
                count ++;

                documentCount++;
                emapCore.addBean(emap, 60000);
               
            }
            
            // Send a final commit
            emapCore.commit();

        } catch (SQLException | SolrServerException | IOException e) {
            throw new IndexerException(e);
        }
        
        logger.info("Indexed {} beans", count);

        logger.info("EMAP Indexer complete!");
    }
*/
    /*
    private int sumPhenotypingCalls(String emapId) throws SolrServerException {
    
    	List<SolrServer> ss = new ArrayList<>();
    	ss.add(preqcCore);
    	ss.add(genotypePhenotypeCore);
    	
    	int calls = 0;
    	for ( int i=0; i<ss.size(); i++ ){
    		
    		SolrServer solrSvr = ss.get(i);
    
	    	SolrQuery query = new SolrQuery();
			query.setQuery("emap_term_id:\"" + mpId + "\" OR intermediate_emap_term_id:\"" + mpId + "\" OR top_level_emap_term_id:\"" + mpId + "\"");
			query.setRows(0);
			
			QueryResponse response = solrSvr.query(query);
			calls += response.getResults().getNumFound();
		
		}
    	
        return calls;
    }
    */
//    private void populateGene2MpCalls() throws SQLException {
//    	
//    	String qry = "select emap_acc, count(*) as calls from phenotype_call_summary where p_value < 0.0001 group by emap_acc";
//    	
//    	PreparedStatement ps = komp2DbConnection.prepareStatement(qry);
//    	ResultSet rs = ps.executeQuery();
//    	
//    	while (rs.next()) {
//    		String mpAcc = rs.getString("emap_acc");
//    		int calls = rs.getInt("calls");
//    	
//    		mpCalls.put(mpAcc, calls);
//    	}
//    	
//    	logger.info("Finished creating a mapping of MP to postqc phenotyping calls");
//    }

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


    /**
     * Initialize the phenodigm core -- using a proxy if configured.
     * <p/>
     * A proxy is specified by supplying two JVM variables
     * - externalProxyHost the host (not including the protocol)
     * - externalProxyPort the integer port number
     */
    /*
    private void initializeSolrCores() {

        final String PHENODIGM_URL = config.get("phenodigm.solrserver");

        // Use system proxy if set for external solr servers
        if (System.getProperty("externalProxyHost") != null && System.getProperty("externalProxyPort") != null) {

            String PROXY_HOST = System.getProperty("externalProxyHost");
            Integer PROXY_PORT = Integer.parseInt(System.getProperty("externalProxyPort"));

            HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();

            logger.info("Using Proxy Settings: " + PROXY_HOST + " on port: " + PROXY_PORT);

            this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL, client);

        } else {

            this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL);

        }
    }
    */


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
        logger.debug("Loaded {} node Ids", count);

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
        logger.debug("Loaded {} top level terms", count);

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
        logger.info("Loaded {} intermediate node Ids", count);

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
        logger.debug("Loaded {} child node Ids", count);

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
        logger.debug("Loaded {} intermediate level terms", count);

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
        logger.debug("Loaded {} parent node Ids", count);

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
        logger.debug("Loaded {} EMAP term synonyms", count);

        return beans;
    }

    /*
    private Map<String, List<PhenotypeCallSummaryBean>> getPhenotypeCallSummary1() throws SQLException {
        Map<String, List<PhenotypeCallSummaryBean>> beans = new HashMap<>();

        String q = "select distinct gf_acc, mp_acc, concat(mp_acc,'_',gf_acc) as mp_mgi, parameter_id, procedure_id, pipeline_id, allele_acc, strain_acc from phenotype_call_summary where p_value <= 0.0001 and gf_db_id=3 and gf_acc like 'MGI:%' and allele_acc is not null and strain_acc is not null";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            PhenotypeCallSummaryBean bean = new PhenotypeCallSummaryBean();

            String mpAcc = rs.getString("mp_acc");

            bean.setGfAcc(rs.getString("gf_acc"));
            bean.setMpAcc(mpAcc);
            bean.setMpMgi(rs.getString("mp_mgi"));
            bean.setParameterId(rs.getString("parameter_id"));
            bean.setProcedureId(rs.getString("procedure_id"));
            bean.setPipelineId(rs.getString("pipeline_id"));
            bean.setAlleleAcc(rs.getString("allele_acc"));
            bean.setStrainAcc(rs.getString("strain_acc"));

            if ( ! beans.containsKey(mpAcc)) {
                beans.put(mpAcc, new ArrayList<PhenotypeCallSummaryBean>());
            }
            beans.get(mpAcc).add(bean);
            count ++;
        }
        logger.debug("Loaded {} phenotype call summaries (1)", count);

        return beans;
    }
*/
    /*
    private Map<String, List<String>> getImpcPipe() throws SQLException {
        Map<String, List<String>> beans = new HashMap<>();

        String q = "select distinct external_db_id as 'impc', concat (mp_acc,'_', gf_acc) as mp_mgi from phenotype_call_summary where p_value < 0.0001 and external_db_id = 22";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            String tId = rs.getString("emap_mgi");
            String impc = rs.getString("impc");
            if ( ! beans.containsKey(tId)) {
                beans.put(tId, new ArrayList<String>());
            }
            beans.get(tId).add(impc);
            count ++;
        }
        logger.debug("Loaded {} IMPC", count);

        return beans;
    }

    private Map<String, List<String>> getLegacyPipe() throws SQLException {
        Map<String, List<String>> beans = new HashMap<>();

        String q = "select distinct external_db_id as 'legacy', concat (emap_acc,'_', gf_acc) as emap_mgi from phenotype_call_summary where p_value < 0.0001 and external_db_id = 12";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            String tId = rs.getString("emap_mgi");
            String legacy = rs.getString("legacy");
            if ( ! beans.containsKey(tId)) {
                beans.put(tId, new ArrayList<String>());
            }
            beans.get(tId).add(legacy);
            count ++;
        }
        logger.debug("Loaded {} legacy", count);

        return beans;
    }

    private Map<String, List<PhenotypeCallSummaryBean>> getPhenotypeCallSummary2() throws SQLException {
        Map<String, List<PhenotypeCallSummaryBean>> beans = new HashMap<>();

        String q = "select distinct gf_acc, emap_acc, parameter_id, procedure_id, pipeline_id, concat(parameter_id,'_',procedure_id,'_',pipeline_id) as ididid, allele_acc, strain_acc from phenotype_call_summary where gf_db_id=3 and gf_acc like 'MGI:%' and allele_acc is not null and strain_acc is not null";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            PhenotypeCallSummaryBean bean = new PhenotypeCallSummaryBean();

            String mpAcc = rs.getString("emap_acc");

            bean.setGfAcc(rs.getString("gf_acc"));
            bean.setMpAcc(mpAcc);
            bean.setParamProcPipelineId(rs.getString("ididid"));
            bean.setParameterId(rs.getString("parameter_id"));
            bean.setProcedureId(rs.getString("procedure_id"));
            bean.setPipelineId(rs.getString("pipeline_id"));
            bean.setAlleleAcc(rs.getString("allele_acc"));
            bean.setStrainAcc(rs.getString("strain_acc"));

            if ( ! beans.containsKey(mpAcc)) {
                beans.put(mpAcc, new ArrayList<PhenotypeCallSummaryBean>());
            }
            beans.get(mpAcc).add(bean);
            count ++;
        }
        logger.debug("Loaded {} phenotype call summaries (2)", count);

        return beans;
    }

    private Map<String, List<MPStrainBean>> getStrains() throws SQLException {
        Map<String, List<MPStrainBean>> beans = new HashMap<>();

        String q = "select distinct name, acc from strain where db_id=3";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            MPStrainBean bean = new MPStrainBean();

            String acc = rs.getString("acc");

            bean.setAcc(acc);
            bean.setName(rs.getString("name"));

            if ( ! beans.containsKey(acc)) {
                beans.put(acc, new ArrayList<MPStrainBean>());
            }
            beans.get(acc).add(bean);
            count ++;
        }
        logger.debug("Loaded {} strain beans", count);

        return beans;
    }

    private Map<String, List<ParamProcedurePipelineBean>> getPPPBeans() throws SQLException {
        Map<String, List<ParamProcedurePipelineBean>> beans = new HashMap<>();

        String q = "select concat(pp.id,'_',pproc.id,'_',ppipe.id) as ididid, pp.name as parameter_name, pp.stable_key as parameter_stable_key, pp.stable_id as parameter_stable_id, pproc.name as procedure_name, pproc.stable_key as procedure_stable_key, pproc.stable_id as procedure_stable_id, ppipe.name as pipeline_name, ppipe.stable_key as pipeline_key, ppipe.stable_id as pipeline_stable_id from phenotype_parameter pp inner join phenotype_procedure_parameter ppp on pp.id=ppp.parameter_id inner join phenotype_procedure pproc on ppp.procedure_id=pproc.id inner join phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id inner join phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            ParamProcedurePipelineBean bean = new ParamProcedurePipelineBean();

            String id = rs.getString("ididid");

            bean.setParameterName(rs.getString("parameter_name"));
            bean.setParameterStableId(rs.getString("parameter_stable_id"));
            bean.setParameterStableKey(rs.getString("parameter_stable_key"));
            bean.setProcedureName(rs.getString("procedure_name"));
            bean.setProcedureStableId(rs.getString("procedure_stable_id"));
            bean.setProcedureStableKey(rs.getString("procedure_stable_key"));
            bean.setPipelineName(rs.getString("pipeline_name"));
            bean.setPipelineStableId(rs.getString("pipeline_stable_id"));
            bean.setPipelineStableKey(rs.getString("pipeline_key"));

            if ( ! beans.containsKey(id)) {
                beans.put(id, new ArrayList<ParamProcedurePipelineBean>());
            }
            beans.get(id).add(bean);
            count ++;
        }
        logger.debug("Loaded {} PPP beans", count);

        return beans;
    }

   
*/
    
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

    
//    private void addPhenotype1(EmapDTO emap) {
//        if (phenotypes1.containsKey(emap.getEmapId())) {
//            checkMgiDetails(emap);
//
//            for (PhenotypeCallSummaryBean pheno1 : phenotypes1.get(emap.getEmapId())) {
//                emap.getMgiAccessionId().add(pheno1.getGfAcc());
//                if (impcBeans.containsKey(pheno1.getMpMgi())) {
//                     From JS mapping script - row.get('impc')
//                    emap.getLatestPhenotypeStatus().add("Phenotyping Complete");
//                }
//                if (legacyBeans.containsKey(pheno1.getMpMgi())) {
//                     From JS mapping script - row.get('legacy')
//                    mp.setLegacyPhenotypeStatus(1);
//                }
//                addPreQc(mp, pheno1.getGfAcc());
//                addAllele(mp, alleles.get(pheno1.getGfAcc()), false);
//            }
//        }
//    }
/*
    private void checkMgiDetails(EmapDTO emap) {
        if (emap.getMgiAccessionId() == null) {
        	emap.setMgiAccessionId(new ArrayList<String>());
        	emap.setLatestPhenotypeStatus(new ArrayList<String>());
        }
    }

*/
    /*
    private void addAllele(EmapDTO emap, List<AlleleDTO> alleles, boolean includeStatus) {
        if (alleles != null) {
            initialiseAlleleFields(emap);

            for (AlleleDTO allele : alleles) {
                // Copy the fields from the allele to the MP
                // NO TYPE FIELD IN ALLELE DATA!!! emap.getType().add(???)
                if (allele.getDiseaseSource() != null) {
                    emap.getDiseaseSource().addAll(allele.getDiseaseSource());
                    emap.setDiseaseSource(new ArrayList<>(new HashSet<>(emap.getDiseaseSource())));
                }
                if (allele.getDiseaseId() != null) {
                    emap.getDiseaseId().addAll(allele.getDiseaseId());
                    emap.setDiseaseId(new ArrayList<>(new HashSet<>(emap.getDiseaseId())));
                }
                if (allele.getDiseaseTerm() != null) {
                    emap.getDiseaseTerm().addAll(allele.getDiseaseTerm());
                    emap.setDiseaseTerm(new ArrayList<>(new HashSet<>(emap.getDiseaseTerm())));
                }
                if (allele.getDiseaseAlts() != null) {
                    emap.getDiseaseAlts().addAll(allele.getDiseaseAlts());
                    emap.setDiseaseAlts(new ArrayList<>(new HashSet<>(emap.getDiseaseAlts())));
                }
                if (allele.getDiseaseClasses() != null) {
                    emap.getDiseaseClasses().addAll(allele.getDiseaseClasses());
                    emap.setDiseaseClasses(new ArrayList<>(new HashSet<>(emap.getDiseaseClasses())));
                }
                if (allele.getHumanCurated() != null) {
                    emap.getHumanCurated().addAll(allele.getHumanCurated());
                    emap.setHumanCurated(new ArrayList<>(new HashSet<>(emap.getHumanCurated())));
                }
                if (allele.getMouseCurated() != null) {
                    emap.getMouseCurated().addAll(allele.getMouseCurated());
                    emap.setMouseCurated(new ArrayList<>(new HashSet<>(emap.getMouseCurated())));
                }
                if (allele.getMgiPredicted() != null) {
                    emap.getMgiPredicted().addAll(allele.getMgiPredicted());
                    emap.setMgiPredicted(new ArrayList<>(new HashSet<>(emap.getMgiPredicted())));
                }
                if (allele.getImpcPredicted() != null) {
                    emap.getImpcPredicted().addAll(allele.getImpcPredicted());
                    emap.setImpcPredicted(new ArrayList<>(new HashSet<>(emap.getImpcPredicted())));
                }
                if (allele.getMgiPredictedKnownGene() != null) {
                    emap.getMgiPredictedKnownGene().addAll(allele.getMgiPredictedKnownGene());
                    emap.setMgiPredictedKnownGene(new ArrayList<>(new HashSet<>(emap.getMgiPredictedKnownGene())));
                }
                if (allele.getImpcPredictedKnownGene() != null) {
                    emap.getImpcPredictedKnownGene().addAll(allele.getImpcPredictedKnownGene());
                    emap.setImpcPredictedKnownGene(new ArrayList<>(new HashSet<>(emap.getImpcPredictedKnownGene())));
                }
                if (allele.getMgiNovelPredictedInLocus() != null) {
                    emap.getMgiNovelPredictedInLocus().addAll(allele.getMgiNovelPredictedInLocus());
                    emap.setMgiNovelPredictedInLocus(new ArrayList<>(new HashSet<>(emap.getMgiNovelPredictedInLocus())));
                }
                if (allele.getImpcNovelPredictedInLocus() != null) {
                    emap.getImpcNovelPredictedInLocus().addAll(allele.getImpcNovelPredictedInLocus());
                    emap.setImpcNovelPredictedInLocus(new ArrayList<>(new HashSet<>(emap.getImpcNovelPredictedInLocus())));
                }
                if (allele.getMarkerSymbol() != null) {
                    emap.getMarkerSymbol().add(allele.getMarkerSymbol());
                }
                if (allele.getMarkerName() != null) {
                    emap.getMarkerName().add(allele.getMarkerName());
                }
                if (allele.getMarkerSynonym() != null) {
                    emap.getMarkerSynonym().addAll(allele.getMarkerSynonym());
                }
                if (allele.getMarkerType() != null) {
                    emap.getMarkerType().add(allele.getMarkerType());
                }
                if (allele.getHumanGeneSymbol() != null) {
                    emap.getHumanGeneSymbol().addAll(allele.getHumanGeneSymbol());
                }
                // NO STATUS FIELD IN ALLELE DATA!!! emap.getStatus().add(allele.getStatus());
                if (allele.getImitsPhenotypeStarted() != null) {
                    emap.getImitsPhenotypeStarted().add(allele.getImitsPhenotypeStarted());
                }
                if (allele.getImitsPhenotypeComplete() != null) {
                    emap.getImitsPhenotypeComplete().add(allele.getImitsPhenotypeComplete());
                }
                if (allele.getImitsPhenotypeStatus() != null) {
                    emap.getImitsPhenotypeStatus().add(allele.getImitsPhenotypeStatus());
                }
                if (allele.getLatestProductionCentre() != null) {
                    emap.getLatestProductionCentre().addAll(allele.getLatestProductionCentre());
                }
                if (allele.getLatestPhenotypingCentre() != null) {
                    emap.getLatestPhenotypingCentre().addAll(allele.getLatestPhenotypingCentre());
                }
                if (allele.getAlleleName() != null) {
                    emap.getAlleleName().addAll(allele.getAlleleName());
                }

                if (includeStatus && allele.getMgiAccessionId() != null) {
                    emap.getLatestPhenotypeStatus().add("Phenotyping Started");
                }
            }
        }
    }

    private void initialiseAlleleFields(EmapDTO emap) {
        if (emap.getType() == null) {
            emap.setType(new ArrayList<String>());
            emap.setDiseaseSource(new ArrayList<String>());
            emap.setDiseaseId(new ArrayList<String>());
            emap.setDiseaseTerm(new ArrayList<String>());
            emap.setDiseaseAlts(new ArrayList<String>());
            emap.setDiseaseClasses(new ArrayList<String>());
            emap.setHumanCurated(new ArrayList<Boolean>());
            emap.setMouseCurated(new ArrayList<Boolean>());
            emap.setMgiPredicted(new ArrayList<Boolean>());
            emap.setImpcPredicted(new ArrayList<Boolean>());
            emap.setMgiPredictedKnownGene(new ArrayList<Boolean>());
            emap.setImpcPredictedKnownGene(new ArrayList<Boolean>());
            emap.setMgiNovelPredictedInLocus(new ArrayList<Boolean>());
            emap.setImpcNovelPredictedInLocus(new ArrayList<Boolean>());
            // MGI accession ID should already be set
            emap.setMarkerSymbol(new ArrayList<String>());
            emap.setMarkerName(new ArrayList<String>());
            emap.setMarkerSynonym(new ArrayList<String>());
            emap.setMarkerType(new ArrayList<String>());
            emap.setHumanGeneSymbol(new ArrayList<String>());
            emap.setStatus(new ArrayList<String>());
            emap.setImitsPhenotypeStarted(new ArrayList<String>());
            emap.setImitsPhenotypeComplete(new ArrayList<String>());
            emap.setImitsPhenotypeStatus(new ArrayList<String>());
            emap.setLatestProductionCentre(new ArrayList<String>());
            emap.setLatestPhenotypingCentre(new ArrayList<String>());
            emap.setAlleleName(new ArrayList<String>());
            emap.setPreqcGeneId(new ArrayList<String>());
        }
    }
*/
//    private void addPhenotype2(MpDTO mp) {
//        if (phenotypes2.containsKey(emap.getMpId())) {
//            checkMgiDetails(mp);
//
//            for (PhenotypeCallSummaryBean pheno2 : phenotypes2.get(mp.getMpId())) {
//                addStrains(mp, pheno2.getStrainAcc());
//                addParamProcPipeline(mp, pheno2.getParamProcPipelineId());
//            }
//        }
//    }

    /*
    private void addStrains(MpDTO mp, String strainAcc) {
        if (strains.containsKey(strainAcc)) {
            if (mp.getStrainId() == null) {
                // Initialise the strain lists
                mp.setStrainId(new ArrayList<String>());
                mp.setStrainName(new ArrayList<String>());
            }

            for (MPStrainBean strain : strains.get(strainAcc)) {
                mp.getStrainId().add(strain.getAcc());
                mp.getStrainName().add(strain.getName());
            }
        }
    }

    private void addParamProcPipeline(MpDTO mp, String pppId) {
        if (pppBeans.containsKey(pppId)) {
            if (mp.getParameterName() == null) {
                // Initialise the PPP lists
                mp.setParameterName(new ArrayList<String>());
                mp.setParameterStableId(new ArrayList<String>());
                mp.setParameterStableKey(new ArrayList<String>());
                mp.setProcedureName(new ArrayList<String>());
                mp.setProcedureStableId(new ArrayList<String>());
                mp.setProcedureStableKey(new ArrayList<String>());
                mp.setPipelineName(new ArrayList<String>());
                mp.setPipelineStableId(new ArrayList<String>());
                mp.setPipelineStableKey(new ArrayList<String>());
            }

            for (ParamProcedurePipelineBean pppBean : pppBeans.get(pppId)) {
                mp.getParameterName().add(pppBean.getParameterName());
                mp.getParameterStableId().add(pppBean.getParameterStableId());
                mp.getParameterStableKey().add(pppBean.getParameterStableKey());
                mp.getProcedureName().add(pppBean.getProcedureName());
                mp.getProcedureStableId().add(pppBean.getProcedureStableId());
                mp.getProcedureStableKey().add(pppBean.getProcedureStableKey());
                mp.getPipelineName().add(pppBean.getPipelineName());
                mp.getPipelineStableId().add(pppBean.getPipelineStableId());
                mp.getPipelineStableKey().add(pppBean.getPipelineStableKey());
            }
        }
    }
*/
    // PROTECTED METHODS
    @Override
    protected Logger getLogger() {
        return logger;
    }

    public static void main(String[] args) throws IndexerException, SQLException {

        EmapIndexer main = new EmapIndexer();
        main.initialise(args);
        main.run();
        main.validateBuild();

        logger.info("Process finished.  Exiting.");

    }

}

