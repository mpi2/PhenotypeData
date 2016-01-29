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
package org.mousephenotype.cda.indexers.utils;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.EmapOntologyDAO;
import org.mousephenotype.cda.indexers.AbstractIndexer;
import org.mousephenotype.cda.indexers.beans.OntologyTermEmapBeanList;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.EmapDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
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
 * @author ckchen
 * Use OLS for ontology lookup, instead of ontodb
 *
 */
public class EmapIndexer2 extends AbstractIndexer {
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

//    @Autowired
//    @Qualifier("ontodbDataSource")
//    DataSource ontodbDataSource;

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


    HashSet <String> emapTerms = new HashSet<String>();


    public EmapIndexer2() {
    
    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(emapCore);
    }

    @Override
    public RunStatus run() throws IndexerException {
        int count = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

       // initializeDatabaseConnections();
/*
    	try {
            initialiseSupportingBeans();

            System.out.println("FOUND " + emapTerms.size());
            List<EmapDTO> emapBatch = new ArrayList(BATCH_SIZE);

            // Add all emap terms to the index.

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
*/
        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));

        return runStatus;

    }

    /**
     * Initialize the database connections required
     *
     * @throws IndexerException when there's an issue
     */
    
    private void initializeDatabaseConnections() throws IndexerException {

        try {
            komp2DbConnection = komp2DataSource.getConnection();
        } catch (SQLException e) {
            throw new IndexerException(e);
        }

    }

    private void initialiseSupportingBeans() throws IndexerException {
        try {
            // Grab all the supporting database content
            
           // get full list of EMAP terms from IMPRESS (loaded to /Sanger images/IMPC images
            getOntologyTermIds(14, komp2DbConnection);



            // Alleles
          //  alleles = IndexerMap.getGeneToAlleles(alleleCore);


        } catch (SQLException e) {
            throw new IndexerException(e);
        }
    }

    /**
     * @param ontologyDbId database id (emap:14, mp:5, ma:8)
     * @return
     * @throws SQLException
     */

    private void getOntologyTermIds(Integer ontologyDbId, Connection conn) throws SQLException{

        // get all EMPAS from IMPC
        PreparedStatement statement = conn.prepareStatement(
                "SELECT DISTINCT(ontology_acc) FROM phenotype_parameter_ontology_annotation ppoa WHERE ppoa.ontology_db_id=" + ontologyDbId);
        ResultSet res = statement.executeQuery();

        while (res.next()) {
            emapTerms.add(res.getString("ontology_acc"));
        }
        // get all EMAPS from Sanger images

        PreparedStatement statement2 = conn.prepareStatement("SELECT DISTINCT (upper(TERM_ID)) as ontology_acc FROM ANN_ANNOTATION WHERE TERM_ID LIKE \"EMAP:%\"");
        ResultSet res2 = statement2.executeQuery();

        while (res2.next()) {
            emapTerms.add(res2.getString("ontology_acc"));
        }
    }


    
//    private Map<Integer, List<OntologyTermBean>> getTopLevelTerms() throws SQLException {
//
//    }

    /**
     * Build a map of child node ID -> node IDs, to use to build the
     * intermediate nodes.
     *
     * @return the map.
     * @throws SQLException
     */
    

    /**
     * Build a map of node ID -> child node IDs.
     *
     * @return the map.
     * @throws SQLException
     */
    
//    private Map<Integer, List<Integer>> getChildNodeIds() throws SQLException {
//
//    }

//    private Map<Integer, List<OntologyTermBean>> getIntermediateTerms() throws SQLException {
//
//    }
    
//    private Map<Integer, List<Integer>> getParentNodeIds() throws SQLException {
//
//    }
//
//    private Map<String, List<String>> getEmapTermSynonyms() throws SQLException {
//
//    }


    // PROTECTED METHODS


    public static void main(String[] args) throws IndexerException, SQLException {

        RunStatus runStatus = new RunStatus();
        EmapIndexer2 main = new EmapIndexer2();
        main.initialise(args, runStatus);
        main.run();
        main.validateBuild();
    }
}