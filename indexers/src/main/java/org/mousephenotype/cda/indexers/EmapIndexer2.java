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
import org.mousephenotype.cda.db.dao.EmapOntologyDAO;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
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
import static org.mousephenotype.cda.indexers.utils.IndexerMap.getGeneToAlleles;


/**
 * @author ckchen
 * Use OLS for ontology lookup, instead of ontodb
 *
 */
public class EmapIndexer2 extends AbstractIndexer {
    CommonUtils commonUtils = new CommonUtils();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("alleleIndexing")
    private SolrServer alleleCore;

    @Autowired
    @Qualifier("preqcIndexing")
    private SolrServer preqcCore;

    @Autowired
    @Qualifier("genotypePhenotypeIndexing")
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
    private Map<String, String> emapStableIdToGeneIdMap = new HashMap();

    // prepare alleleMap
    Map<String, List<AlleleDTO>> alleleMap;

    Map<String, String> emap2MgiId = new HashMap<>();

    Map<String, List<OmeroAssociation>> emap2Omero = new HashMap<>();

    Map<String, List<Integer>> emap2SangerImageId = new HashMap<>();

    public EmapIndexer2() {


    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(emapCore);
    }

    @Override
    public RunStatus run() throws IndexerException, IOException, SolrServerException {
        int count = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

        initializeDatabaseConnections();

        emapCore.deleteByQuery("*:*");

        try {
            initialiseSupportingBeans();

            System.out.println("FOUND " + emapTerms.size());
            List<EmapDTO> emapBatch = new ArrayList(BATCH_SIZE);

            // Add all emap terms to the index.

            for( String emapTermId : emapTerms ){
                EmapDTO emap = new EmapDTO();
                //http://purl.obolibrary.org/obo/MP_0004609
                emap.setEmapId(emapTermId);
                emap.setEmapIdUrl("http://purl.obolibrary.org/obo/" + emapTermId.replace(":","_"));
                //emap.setEmapTerm("need to set");

                // Genes annotated to an EMAP
                if ( emap2MgiId.containsKey(emapTermId) ) {
                    String mgiGeneId = emap2MgiId.get(emapTermId);

                    // Genes annotated to an EMAP
                    emap.setMarkerAccessionId(mgiGeneId);

                    List<AlleleDTO> alleleDTOs = alleleMap.get(mgiGeneId);
                    List<String> markerSymbols = new ArrayList<>();
                    List<String> markerNames = new ArrayList<>();
                    List<String> markerTypes = new ArrayList<>();
                    List<String> markerSynonyms = new ArrayList<>();

                    for( AlleleDTO allele : alleleDTOs ) {

                        markerSymbols.add(allele.getMarkerSymbol());
                        markerNames.add(allele.getMarkerName());
                        markerTypes.add(allele.getMarkerType());
                        if ( allele.getMarkerSynonym() != null ) {
                            markerSynonyms.addAll(allele.getMarkerSynonym());
                        }
                    }

                    emap.setMarkerType(markerTypes);
                    emap.setMarkerSymbol(markerSymbols);
                    emap.setMarkerName(markerNames);
                    emap.setMarkerSynonym(markerSynonyms);

                }

                // IMPC images annotated to an EMAP
                if ( emap2Omero.containsKey(emapTermId)){

                    List<Integer> omeroIds = new ArrayList<>();
                    List<String> parameterAssocValues = new ArrayList<>();
                    Set<String> parameterStableIds = new HashSet<>();

                    for ( OmeroAssociation oa : emap2Omero.get(emapTermId) ) {
                        omeroIds.add(oa.getOmeroId());
                        parameterAssocValues.add(oa.getParameterAssocValue());
                        parameterStableIds.add(oa.getParameterStableId());
                    }
                    emap.setOmeroIds(omeroIds);
                    emap.setParameterAssocValue(parameterAssocValues);
                    emap.setParameterStableId(new ArrayList<String>(parameterStableIds));
                }


                // Sanger images annotated to an EMAP
                if ( emap2SangerImageId.containsKey(emapTermId) ){

                    List<Integer> sangerImgIds = new ArrayList<>();

                    for ( Integer sangerImgId : emap2SangerImageId.get(emapTermId) ){
                        sangerImgIds.add(sangerImgId);
                    }

                    emap.setSangerImageIds(sangerImgIds);
                }


                count++;
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

        }
        catch (SolrServerException| IOException e) {
            throw new IndexerException(e);
        }

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

    private void initialiseSupportingBeans() throws IndexerException, IOException {
        try {
            // Grab all the supporting database content


            // get full list of EMAP terms from IMPRESS (loaded to /Sanger images/IMPC images
            getOntologyTermIds(14, komp2DbConnection);


            alleleMap = getGeneToAlleles(alleleCore);

            // get emap to MGI gene ID mapping from phenotype_call_summary table
            populateEmapGeneMap(komp2DbConnection);

            // EMAP to omero_id mapping
            populateEmapOmeroIdMap(komp2DbConnection);

            // EMAP to Sanger image id mapping
            populateEmapSangerImageIdMap(komp2DbConnection);


        } catch (SQLException e) {
            throw new IndexerException(e);
        }
    }

    private void populateEmapSangerImageIdMap(Connection conn) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "SELECT ID, TERM_ID FROM ANN_ANNOTATION");
        ResultSet res = statement.executeQuery();

        while (res.next()) {
            String emapId = res.getString("TERM_ID");

            if ( ! emap2SangerImageId.containsKey(emapId) ) {
                emap2SangerImageId.put(emapId, new ArrayList<Integer>());
            }

            emap2SangerImageId.get(emapId).add(res.getInt("ID"));
        }
    }


    /**
     * @param ontologyDbId database id (emap:14, mp:5, ma:8)
     * @return
     * @throws SQLException
     */

    private void getOntologyTermIds(Integer ontologyDbId, Connection conn) throws SQLException{

        // get all EMAPS from IMPC
        PreparedStatement statement = conn.prepareStatement(
                "SELECT DISTINCT(ontology_acc) FROM phenotype_parameter_ontology_annotation ppoa WHERE ppoa.ontology_db_id=" + ontologyDbId);
        ResultSet res = statement.executeQuery();


        while (res.next()) {
            emapTerms.add(res.getString("ontology_acc"));
        }

        // get all EMAPS from Sanger images
        PreparedStatement statement2 = conn.prepareStatement(
                "SELECT DISTINCT (upper(TERM_ID)) as ontology_acc " +
                "FROM IMA_IMAGE_TAG iit INNER JOIN ANN_ANNOTATION aa ON aa.FOREIGN_KEY_ID=iit.ID AND TERM_NAME LIKE 'TS20%' AND TERM_ID LIKE \"EMAP:%\"");
        ResultSet res2 = statement2.executeQuery();

        while (res2.next()) {
            emapTerms.add(res2.getString("ontology_acc"));
        }
    }

    private void populateEmapGeneMap(Connection conn) throws SQLException {

        PreparedStatement statement = conn.prepareStatement(
                "SELECT DISTINCT gf_acc, mp_acc FROM phenotype_call_summary WHERE mp_acc LIKE 'emap:%'");
        ResultSet res = statement.executeQuery();

        while (res.next()) {
            emap2MgiId.put(res.getString("mp_acc"), res.getString("gf_acc"));
        }
    }

    private void populateEmapOmeroIdMap(Connection conn) throws SQLException {

        PreparedStatement statement = conn.prepareStatement(
                "SELECT iro.omero_id, pa.parameter_association_value, pp.stable_id, ppoa.ontology_acc " +
                "FROM image_record_observation iro " +
                "INNER JOIN parameter_association pa on iro.id=pa.observation_id " +
                "INNER JOIN phenotype_parameter pp on pa.parameter_id=pp.stable_id " +
                "INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id=pploa.parameter_id " +
                "INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id " +
                "WHERE ppoa.ontology_db_id=14");
        ResultSet res = statement.executeQuery();

        while (res.next()) {
            String emapId = res.getString("ontology_acc");
            OmeroAssociation oa = new OmeroAssociation(res.getInt("omero_id"), res.getString("parameter_association_value"), res.getString("stable_id"));
            if ( ! emap2Omero.containsKey(emapId) ) {
                emap2Omero.put(emapId, new ArrayList<OmeroAssociation>());
            }

            emap2Omero.get(emapId).add(oa);
        }

    }


    class OmeroAssociation {
        Integer omero_id;
        String parameter_assoc_value;
        String parameter_stable_id;

        public OmeroAssociation(Integer omero_id, String parameter_assoc_value, String parameter_stable_id) {
            this.omero_id = omero_id;
            this.parameter_assoc_value = parameter_assoc_value;
            this.parameter_stable_id = parameter_stable_id;
        }

        public Integer getOmeroId() {
            return omero_id;
        }
        public void setOmeroId(Integer omero_id) {
            this.omero_id = omero_id;
        }

        public String getParameterAssocValue() {
            return parameter_assoc_value;
        }
        public void setParameterAssocValue(String parameter_assoc_value) {
            this.parameter_assoc_value = parameter_assoc_value;
        }

        public String getParameterStableId() {
            return parameter_stable_id;
        }
        public void setParameterStableId(String parameter_stable_id) {
            this.parameter_stable_id = parameter_stable_id;
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


    public static void main(String[] args) throws IndexerException, SQLException, IOException, SolrServerException {

        EmapIndexer2 main = new EmapIndexer2();
        main.initialise(args);
        main.run();
        main.validateBuild();
    }
}
