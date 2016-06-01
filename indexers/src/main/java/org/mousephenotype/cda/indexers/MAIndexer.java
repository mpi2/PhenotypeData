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

import net.sf.json.JSONObject;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.MaOntologyDAO;
import org.mousephenotype.cda.db.dao.EmapaOntologyDAO;
import org.mousephenotype.cda.indexers.beans.OntologyTermHelperEmapa;
import org.mousephenotype.cda.indexers.beans.OntologyTermHelperMa;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.OntologyBrowserGetter;
import org.mousephenotype.cda.indexers.utils.OntologyBrowserGetter.TreeHelper;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.MaDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.SangerImageDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.mousephenotype.cda.db.dao.OntologyDAO.BATCH_SIZE;

/**
 * Populate the MA core
 */
@Component
public class MAIndexer extends AbstractIndexer {
    CommonUtils commonUtils = new CommonUtils();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("classpath:uberonEfoMa_mappings.txt")
	Resource resource;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;

    private static Connection ontoDbConnection;

    @Autowired
    @Qualifier("sangerImagesIndexing")
    SolrServer imagesCore;

    @Autowired
    @Qualifier("maIndexing")
    SolrServer maCore;

    @Autowired
    MaOntologyDAO maOntologyService;

    @Autowired
    EmapaOntologyDAO emapaOntologyService;

    private Map<String, List<Integer>> termNodeIds;
    private Map<Integer, String> lookupTableByNodeId = new HashMap<>(); // <nodeId, emapaOntologyId>
    private Map<String, List<String>> emapaTermSynonyms;

    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap();      // key = term_id.
    private Map<String, Map<String,List<String>>> maUberonEfoMap = new HashMap();      // key = term_id.

    public MAIndexer() {

    }

    /**
     * Initialize the database connections required
     *
     * @throws IndexerException when there's an issue
     */
    private void initializeDatabaseConnections() throws IndexerException {

        try {
            ontoDbConnection = ontodbDataSource.getConnection();
        } catch (SQLException e) {
            throw new IndexerException(e);
        }

    }
    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(maCore);
    }

    @Override
    public void initialise(String[] args) throws IndexerException {
        super.initialise(args);
    }

    @Override
    public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {

        int count = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();
        OntologyBrowserGetter ontologyBrowser = new OntologyBrowserGetter(ontodbDataSource);

        initializeDatabaseConnections();

    	try {

            // Delete the documents in the core if there are any.
            maCore.deleteByQuery("*:*");
            maCore.commit();

            logger.info(" Source of images core: " + SolrUtils.getBaseURL(imagesCore) );
            initialiseSupportingBeans();

            List<MaDTO> maBatch = new ArrayList(BATCH_SIZE);
            List<OntologyTermBean> beans = maOntologyService.getAllTerms();
            //List<OntologyTermBean> emapaBeans = emapaOntologyService.getAllTerms();
            //System.out.println("EMAPA beans: "+ emapaBeans.size());

            // fetch list of excludedNodeIds (do not want to display this part of tree for MA)
            //List<String> excludedNodeIds = ontologyBrowser.getExcludedNodeIds();

            // Add all ma terms to the index.
            for (OntologyTermBean bean : beans) {
                MaDTO ma = new MaDTO();

                String maId = bean.getId();

                // Set scalars.
                ma.setDataType("ma");
                ma.setMaId(maId);
                ma.setMaTerm(bean.getName());

                if (bean.getAltIds().size() > 0) {
                    ma.setAltMaIds(bean.getAltIds());
                }

                // Set collections.
                OntologyTermHelperMa sourceList = new OntologyTermHelperMa(maOntologyService, bean.getId());

                ma.setMaTermSynonym(sourceList.getSynonyms());

                ma.setChildMaId(sourceList.getChildren().getIds());
                ma.setChildMaIdTerm(sourceList.getChildren().getId_name_concatenations());
                ma.setChildMaTerm(sourceList.getChildren().getNames());
                ma.setChildMaTermSynonym(sourceList.getChildren().getSynonyms());

                ma.setParentMaId(sourceList.getParents().getIds());
                ma.setParentMaTerm(sourceList.getParents().getNames());
                ma.setParentMaTermSynonym(sourceList.getParents().getSynonyms());

                ma.setIntermediateMaId(sourceList.getIntermediates().getIds());
                ma.setIntermediateMaTerm(sourceList.getIntermediates().getNames());

                ma.setSelectedTopLevelMaId(sourceList.getTopLevels().getIds());
                ma.setSelectedTopLevelMaTerm(sourceList.getTopLevels().getNames());
                ma.setSelectedTopLevelMaTermSynonym(sourceList.getTopLevels().getSynonyms());

                ma.setMaNodeId(bean.getNodeIds());

                // index UBERON/EFO id for MA id

                if (maUberonEfoMap.containsKey(maId)) {
                    if (maUberonEfoMap.get(maId).containsKey("uberon_id")) {
                        ma.setUberonIds(maUberonEfoMap.get(maId).get("uberon_id"));
                    }
                    if (maUberonEfoMap.get(maId).containsKey("efo_id")) {
                        ma.setEfoIds(maUberonEfoMap.get(maId).get("efo_id"));
                    }
                }


                //System.out.println("MA ID: " + ma.getMaId() + " --- MA node id: " + ma.getMaNodeId() + " --- " + ma.getMaTerm());
                // OntologyBrowser stuff
                TreeHelper helper = ontologyBrowser.getTreeHelper("ma", ma.getMaId());
                //helper.setExcludedNodeIds(excludedNodeIds);

                // for MA the root node id is 1 (MP is 0)
                List<JSONObject> searchTree = ontologyBrowser.createTreeJson(helper, "1", null, ma.getMaId());
                ma.setSearchTermJson(searchTree.toString());

                String scrollNodeId = ontologyBrowser.getScrollTo(searchTree);
                ma.setScrollNode(scrollNodeId);
                List<JSONObject> childrenTree = ontologyBrowser.createTreeJson(helper, "" + maOntologyService.getNodeIds(ma.getMaId()).get(0), null, ma.getMaId());
                ma.setChildrenJson(childrenTree.toString());

                // also index all UBERON/EFO ids for intermediate MA ids
                Set<String> all_ae_mapped_uberonIds = new HashSet<>();
                Set<String> all_ae_mapped_efoIds = new HashSet<>();

                for (String intermediateMaId : ma.getIntermediateMaId()) {

                    if (maUberonEfoMap.containsKey(intermediateMaId) && maUberonEfoMap.get(intermediateMaId).containsKey("uberon_id")) {
                        all_ae_mapped_uberonIds.addAll(maUberonEfoMap.get(intermediateMaId).get("uberon_id"));
                    }
                    if (maUberonEfoMap.containsKey(intermediateMaId) && maUberonEfoMap.get(intermediateMaId).containsKey("efo_id")) {
                        all_ae_mapped_efoIds.addAll(maUberonEfoMap.get(intermediateMaId).get("efo_id"));
                    }
                }

                if (ma.getUberonIds() != null) {
                    all_ae_mapped_uberonIds.addAll(ma.getUberonIds());
                    ma.setAllAeMappedUberonIds(new ArrayList<String>(all_ae_mapped_uberonIds));
                }
                if (ma.getEfoIds() != null) {
                    all_ae_mapped_efoIds.addAll(ma.getEfoIds());
                    ma.setAllAeMappedEfoIds(new ArrayList<String>(all_ae_mapped_efoIds));
                }

                // Image association fields
                List<SangerImageDTO> sangerImages = maImagesMap.get(bean.getId());
                if (sangerImages != null) {
                    for (SangerImageDTO sangerImage : sangerImages) {
                        ma.setProcedureName(sangerImage.getProcedureName());
                        ma.setExpName(sangerImage.getExpName());
                        ma.setExpNameExp(sangerImage.getExpNameExp());
                        ma.setSymbolGene(sangerImage.getSymbolGene());

                        ma.setMgiAccessionId(sangerImage.getMgiAccessionId());
                        ma.setMarkerSymbol(sangerImage.getMarkerSymbol());
                        ma.setMarkerName(sangerImage.getMarkerName());
                        ma.setMarkerSynonym(sangerImage.getMarkerSynonym());
                        ma.setMarkerType(sangerImage.getMarkerType());
                        ma.setHumanGeneSymbol(sangerImage.getHumanGeneSymbol());

                        ma.setStatus(sangerImage.getStatus());

                        ma.setImitsPhenotypeStarted(sangerImage.getImitsPhenotypeStarted());
                        ma.setImitsPhenotypeComplete(sangerImage.getImitsPhenotypeComplete());
                        ma.setImitsPhenotypeStatus(sangerImage.getImitsPhenotypeStatus());

                        ma.setLatestPhenotypeStatus(sangerImage.getLatestPhenotypeStatus());
                        ma.setLatestPhenotypingCentre(sangerImage.getLatestPhenotypingCentre());

                        ma.setLatestProductionCentre(sangerImage.getLatestProductionCentre());
                        ma.setLatestPhenotypingCentre(sangerImage.getLatestPhenotypingCentre());

                        ma.setAlleleName(sangerImage.getAlleleName());

                    }
                }

                count++;
                if ( maBatch.contains(ma)) {
                    maBatch.add(ma);
                }
//                if (maBatch.size() == BATCH_SIZE) {
//                    // Update the batch, clear the list
//                    documentCount += maBatch.size();
//                    maCore.addBeans(maBatch, 60000);
//                    maBatch.clear();
//                }

                documentCount++;
                maCore.addBean(ma, 60000);

                if (documentCount % 100 == 0){
                    maCore.commit();
                }
            }

            // Loop through the emapa_term_infos
            String q = " select distinct 'emapa' as dataType, ti.term_id, ti.name, ti.definition, group_concat(distinct alt.alt_id) as alt_ids from emapa_term_infos ti left join emapa_alt_ids alt on ti.term_id=alt.term_id where (ti.term_id != 'EMAPA:0' AND ti.term_id not like 'TS%') group by ti.term_id";
            PreparedStatement ps = ontoDbConnection.prepareStatement(q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String termId = rs.getString("term_id");

                MaDTO emapa = new MaDTO();
                emapa.setDataType(rs.getString("dataType"));
                emapa.setEmapaId(termId);
                emapa.setEmapaTerm(rs.getString("name"));


                // alternative MP ID
                String alt_ids = rs.getString("alt_ids");
                if ( !rs.wasNull() ) {
                    emapa.setAltEmapaIds(Arrays.asList(alt_ids.split(",")));
                }

                emapa.setEmapaNodeId(termNodeIds.get(termId));

                addTopLevelNodes(emapa);
                addIntermediateLevelNodes(emapa);
                addParentLevelNodes(emapa);
                addChildLevelNodes(emapa);

                emapa.setEmapaTermSynonym(emapaTermSynonyms.get(termId));

                // Ontology browser stuff
                TreeHelper helper2 = ontologyBrowser.getTreeHelper( "emapa", termId);

                // for MP the root node id is 0 (MA is 1)
                System.out.println("DO TREE");
                List<JSONObject> searchTree2 = ontologyBrowser.createTreeJson(helper2, "1", null, termId);
                emapa.setSearchTermJson(searchTree2.toString());
                String emapaScrollNodeId = ontologyBrowser.getScrollTo(searchTree2);
                emapa.setScrollNode(emapaScrollNodeId);
                List<JSONObject> emapaChildrenTree = ontologyBrowser.createTreeJson(helper2, "" + emapa.getEmapaNodeId().get(0), null, termId);
                emapa.setChildrenJson(emapaChildrenTree.toString());

                logger.debug(" Added {} records for termId {}", count, termId);
                count ++;

                documentCount++;
                maCore.addBean(emapa, 60000);

                if (documentCount % 100 == 0){
                    maCore.commit();
                }
            }

            //---------------

            // Add all emapa terms to the index.
//            for (OntologyTermBean ebean : emapaBeans) {
//                MaDTO emapa = new MaDTO();
//
//                String emapaId = ebean.getId();
//
//                // Set scalars.
//                emapa.setDataType("emapa");
//                emapa.setEmapaId(emapaId);
//                emapa.setEmapaTerm(ebean.getName());
//
//                if (ebean.getAltIds().size() > 0) {
//                    emapa.setAltEmapaIds(ebean.getAltIds());
//                }
//
//                // Set collections.
//                OntologyTermHelperEmapa sourceList2 = new OntologyTermHelperEmapa(emapaOntologyService, emapaId);
//
//                //emapa.setOntologySubset(sourceList2.getSubsets());
//                emapa.setEmapaTermSynonym(sourceList2.getSynonyms());
//
//                emapa.setChildEmapaId(sourceList2.getChildren().getIds());
//                emapa.setChildEmapaTerm(sourceList2.getChildren().getNames());
//                emapa.setChildEmapaTermSynonym(sourceList2.getChildren().getSynonyms());
//
//                emapa.setParentEmapaId(sourceList2.getParents().getIds());
//                emapa.setParentEmapaTerm(sourceList2.getParents().getNames());
//                emapa.setParentEmapaTermSynonym(sourceList2.getParents().getSynonyms());
//
//                emapa.setIntermediateEmapaId(sourceList2.getIntermediates().getIds());
//                emapa.setIntermediateEmapaTerm(sourceList2.getIntermediates().getNames());
//
//                emapa.setTopLevelEmapaId(sourceList2.getTopLevels().getIds());
//                emapa.setTopLevelEmapaTerm(sourceList2.getTopLevels().getNames());
//
//                emapa.setEmapaNodeId(ebean.getNodeIds());
//
//                System.out.println("EMAPA ID: " + emapa.getEmapaId() + " --- EMAPA node id: " + emapa.getEmapaNodeId() + " --- " + emapa.getEmapaTerm());
//                // OntologyBrowser stuff
//                TreeHelper helper2 = ontologyBrowser.getTreeHelper("emapa", emapaId);
//                //helper.setExcludedNodeIds(excludedNodeIds);
//
//                // for MA the root node id is 1 (MP is 0)
//                List<JSONObject> searchTree2 = ontologyBrowser.createTreeJson(helper2, "1", null, emapa.getEmapaId());
//                emapa.setSearchTermJson(searchTree2.toString());
//                //System.out.println("JSON: "+ emapa.getSearchTermJson());
//                String scrollNodeId2 = ontologyBrowser.getScrollTo(searchTree2);
//                emapa.setScrollNode(scrollNodeId2);
//
//                List<JSONObject> childrenTree2 = ontologyBrowser.createTreeJson(helper2, "" + emapa.getEmapaNodeId().get(0), null, emapa.getEmapaId());
//                emapa.setChildrenJson(childrenTree2.toString());
//
//                count++;
//                maBatch.add(emapa);
//                if (maBatch.size() == BATCH_SIZE) {
//                    // Update the batch, clear the list
//                    documentCount += maBatch.size();
//                    maCore.addBeans(maBatch, 60000);
//                    maBatch.clear();
//                }
//
//            }

                // Make sure the last batch is indexed
//            if (maBatch.size() > 0) {
//                documentCount += maBatch.size();
//                maCore.addBeans(maBatch, 60000);
//            }

            // Send a final commit
            maCore.commit();

        } catch (SQLException | SolrServerException | IOException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));

        return runStatus;
    }


    // PROTECTED METHODS


//    @Override
//    protected void printConfiguration() {
//        if (logger.isDebugEnabled()) {
//            logger.debug(" WRITING ma     CORE TO: " + SolrUtils.getBaseURL(maCore));
//            logger.debug(" USING   images CORE AT: " + SolrUtils.getBaseURL(imagesCore));
//        }
//    }


    // PRIVATE METHODS


    private void addTopLevelNodes(MaDTO ma) throws SQLException {

        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> nameId = new ArrayList<>();
        Set<String> synonyms = new HashSet<>();

        for (OntologyTermBean term : emapaOntologyService.getTopLevel(ma.getEmapaId(), 2)) {

            ids.add(term.getId());
            names.add(term.getName());
            synonyms.addAll(term.getSynonyms());
            nameId.add(term.getTermIdTermName());
        }

        if (ids.size() > 0){
            ma.setTopLevelEmapaId(ids);
            ma.setTopLevelEmapaTerm(names);
            //ma.setTopLevelEmapaTermId(nameId);
            //ma.setTopLevelEmapTermSynonym(new ArrayList<>(synonyms));
        }
    }

    private void addIntermediateLevelNodes(MaDTO ma) {


        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        Set<String> synonyms = new HashSet<>();

        for (OntologyTermBean term : emapaOntologyService.getIntermediates(ma.getEmapaId())) {
            if ( term != null) {
                ids.add(term.getId());
                names.add(term.getName());
                synonyms.addAll(term.getSynonyms());
            }
        }

        if (ids.size() > 0){
            ma.setIntermediateEmapaId(ids);
            ma.setIntermediateEmapaTerm(names);
            ma.setIntermediateEmapaTermSynonym(new ArrayList<>(synonyms));
        }
    }

    private void addParentLevelNodes(MaDTO ma) {

        List<String> parentTermIds = new ArrayList<>();
        List<String> parentTermNames = new ArrayList<>();
        Set<String> parentSynonyms = new HashSet<>();

        for (OntologyTermBean parent : emapaOntologyService.getParents(ma.getEmapaId())) {
            if ( parent != null ) {
                parentTermIds.add(parent.getId());
                parentTermNames.add(parent.getName());
                parentSynonyms.addAll(parent.getSynonyms());
            }
        }

        ma.setParentEmapaId(parentTermIds);
        ma.setParentEmapaTerm(parentTermNames);
        ma.setParentEmapaTermSynonym(new ArrayList<>(parentSynonyms));
    }


    private void addChildLevelNodes(MaDTO ma) {

        List<String> childTermIds = new ArrayList<>();
        List<String> childTermNames = new ArrayList<>();
        Set<String> childSynonyms = new HashSet<>();

        for (OntologyTermBean child : emapaOntologyService.getChildren(ma.getEmapaId())) {
            if ( child != null ) {
                childTermIds.add(child.getId());
                childTermNames.add(child.getName());
                childSynonyms.addAll(child.getSynonyms());
            }
        }

        ma.setChildEmapaId(childTermIds);
        ma.setChildEmapaTerm(childTermNames);
        ma.setChildEmapaTermSynonym(new ArrayList<>(childSynonyms));

    }

    private Map<String, List<Integer>> getNodeIds() throws SQLException {
        Map<String, List<Integer>> beans = new HashMap<>();

        String q = "select nt.node_id, ti.term_id from emapa_term_infos ti, emapa_node2term nt where ti.term_id=nt.term_id and ti.term_id !='EMAPA:0' and ti.term_id not like 'TS%'";
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
            lookupTableByNodeId.put(nId, tId);
        }
        logger.debug(" Added {} EMAPA node Ids", count);

        return beans;
    }

    private Map<String, List<String>> getEmapaTermSynonyms() throws SQLException {
        Map<String, List<String>> beans = new HashMap<>();

        String q = "select term_id, syn_name from emapa_synonyms";
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
        logger.debug(" Added {} EMAPA term synonyms", count);

        return beans;
    }

    private final Integer MAX_ITERATIONS = 2;                                   // Set to non-null value > 0 to limit max_iterations.

    private void initialiseSupportingBeans() throws IndexerException, SQLException, IOException {
        // Grab all the supporting database content
        maImagesMap = IndexerMap.getSangerImagesByMA(imagesCore);
        if (logger.isDebugEnabled()) {
            IndexerMap.dumpSangerImagesMap(maImagesMap, "Images map:", MAX_ITERATIONS);
        }

        maUberonEfoMap = IndexerMap.mapMaToUberronOrEfo(resource);

        // Grab all the supporting database content for EMAPA
        termNodeIds = getNodeIds();
        emapaTermSynonyms = getEmapaTermSynonyms();

    }

    public static void main(String[] args) throws IndexerException, SQLException, IOException, SolrServerException {

        MAIndexer indexer = new MAIndexer();
        indexer.initialise(args);
        indexer.run();
        indexer.validateBuild();
    }
}
