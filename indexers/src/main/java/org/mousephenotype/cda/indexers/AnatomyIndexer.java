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
import org.mousephenotype.cda.db.dao.EmapaOntologyDAO;
import org.mousephenotype.cda.db.dao.MaOntologyDAO;
import org.mousephenotype.cda.indexers.beans.OntologyDetail;
import org.mousephenotype.cda.indexers.beans.OntologyTermHelper;
import org.mousephenotype.cda.indexers.beans.OntologyTermHelperEmapa;
import org.mousephenotype.cda.indexers.beans.OntologyTermHelperMa;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.OntologyBrowserGetter;
import org.mousephenotype.cda.indexers.utils.OntologyBrowserGetter.TreeHelper;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.SangerImageDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.mousephenotype.cda.db.dao.OntologyDAO.BATCH_SIZE;

/**
 * Populate the Anatomy core
 *
 * This includes MA and EMAPA and both top_levels and selected_top_levels are indexed for each core
 *
 *
 * @author ckchen based on the old MAIndexer
 */

@EnableAutoConfiguration
public class AnatomyIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(AnatomyIndexer.class);

    @Value("classpath:uberonEfoMa_mappings.txt")
	Resource resource;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;

    @Autowired
    @Qualifier("sangerImagesCore")
    SolrServer imagesCore;

    @Autowired
    @Qualifier("anatomyIndexing")
    SolrServer anatomyIndexing;

    @Autowired
    MaOntologyDAO maOntologyService;

    @Autowired
    EmapaOntologyDAO emapaOntologyService;


    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap<>();      // key = term_id.
    private Map<String, Map<String,List<String>>> maUberonEfoMap = new HashMap<>();      // key = term_id.

    public AnatomyIndexer() {

    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(anatomyIndexing);
    }


    @Override
    public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {

        int count = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();
        OntologyBrowserGetter ontologyBrowser = new OntologyBrowserGetter(ontodbDataSource);


    	try {

            // Delete the documents in the core if there are any.
            anatomyIndexing.deleteByQuery("*:*");
            anatomyIndexing.commit();

            logger.info(" Source of images core: " + SolrUtils.getBaseURL(imagesCore) );
            initialiseSupportingBeans();

            List<AnatomyDTO> maBatch = new ArrayList<>(BATCH_SIZE);
            List<OntologyTermBean> beans = maOntologyService.getAllTerms();
            List<OntologyTermBean> emapaBeans = emapaOntologyService.getAllTerms();


            // fetch list of excludedNodeIds (do not want to display this part of tree for MA)
            //List<String> excludedNodeIds = ontologyBrowser.getExcludedNodeIds();

            // Add all ma terms to the index.
            for (OntologyTermBean bean : beans) {
                AnatomyDTO ma = new AnatomyDTO();

                String id = bean.getId();

                // Set scalars.
                ma.setDataType("ma");
                ma.setAnatomyId(id);
                ma.setAnatomyTerm(bean.getName());
                ma.setStage("adult");

                if (bean.getAltIds().size() > 0) {
                    ma.setAltAnatomyIds(bean.getAltIds());
                }

                // Set collections.
                OntologyTermHelper sourceList = new OntologyTermHelperMa(maOntologyService, bean.getId());

                ma.setAnatomyTermSynonym(sourceList.getSynonyms());

                // Using the level for MA to get top-levels
                // eg. the fullpath:
                // +---------+----------------------------------+
                // | node_id | fullpath                         |
                // +---------+----------------------------------+
                // |     364 | 0 1 2 281 289 363                |
                // |    1170 | 0 1 807 808 1087 1095 1169       |
                // +---------+----------------------------------+

                // level 2 would be 2 and 807 in the fullpath example above
                // this gets you ie, organ, organ sytem, anatomic region, postnatal mouse, ... (not including nodeId 0)

                int level = 2; // if call w/0 level, default is 1

                OntologyDetail topLevels = sourceList.getTopLevels(level);
                ma.setTopLevelAnatomyId(topLevels.getIds());
                ma.setTopLevelAnatomyTerm(topLevels.getNames());
                ma.setTopLevelAnatomyTermSynonym(topLevels.getSynonyms());

//                System.out.println("1 CHECK TOP: " + ma.getTopLevelId() + " -- " +  ma.getTopLevelTerm() + " -- " + ma.getTopLevelTermSynonym());
//                System.out.println("1 CHECK TOP: " + topLevels.getIds() + " -- " +  topLevels.getNames() + " -- " + topLevels.getSynonyms());

                OntologyDetail selectedTopLevels = sourceList.getSelectedTopLevels();
                ma.setSelectedTopLevelAnatomyId(selectedTopLevels.getIds());
                ma.setSelectedTopLevelAnatomyTerm(selectedTopLevels.getNames());
                ma.setSelectedTopLevelAnatomyTermSynonym(selectedTopLevels.getSynonyms());
                //ma.setSelectedTopLevelIdTerm(selectedTopLevels.getId_name_concatenations());

//                System.out.println("1 CHECK SELTOP: " + ma.getSelectedTopLevelId() + " -- " +  ma.getSelectedTopLevelTerm() + " -- " + ma.getSelectedTopLevelTermSynonym());

                OntologyDetail intermediates = sourceList.getIntermediates();
                ma.setIntermediateAnatomyId(intermediates.getIds());
                ma.setIntermediateAnatomyTerm(intermediates.getNames());
                ma.setIntermediateAnatomyTermSynonym(intermediates.getSynonyms());

                OntologyDetail parents = sourceList.getParents();
                ma.setParentAnatomyId(parents.getIds());
                ma.setParentAnatomyTerm(parents.getNames());
                ma.setParentAnatomyTermSynonym(parents.getSynonyms());

                OntologyDetail children = sourceList.getChildren();
                ma.setChildAnatomyId(children.getIds());
                ma.setChildAnatomyTerm(children.getNames());
                ma.setChildAnatomyTermSynonym(children.getSynonyms());

                ma.setAnatomyNodeId(bean.getNodeIds());

                // index UBERON/EFO id for MA id

                if (maUberonEfoMap.containsKey(id)) {
                    if (maUberonEfoMap.get(id).containsKey("uberon_id")) {
                        ma.setUberonIds(maUberonEfoMap.get(id).get("uberon_id"));
                    }
                    if (maUberonEfoMap.get(id).containsKey("efo_id")) {
                        ma.setEfoIds(maUberonEfoMap.get(id).get("efo_id"));
                    }
                }

                //System.out.println("MA ID: " + ma.getMaId() + " --- MA node id: " + ma.getMaNodeId() + " --- " + ma.getMaTerm());
                // OntologyBrowser stuff
                TreeHelper helper = ontologyBrowser.getTreeHelper("ma", ma.getAnatomyId());
                //helper.setExcludedNodeIds(excludedNodeIds);

                // for  the root node id is 1 (MP is 0)
                List<JSONObject> searchTree = ontologyBrowser.createTreeJson(helper, "1", null, ma.getAnatomyId());
                ma.setSearchTermJson(searchTree.toString());

                String scrollNodeId = ontologyBrowser.getScrollTo(searchTree);
                ma.setScrollNode(scrollNodeId);
                List<JSONObject> childrenTree = ontologyBrowser.createTreeJson(helper, "" + maOntologyService.getNodeIds(ma.getAnatomyId()).get(0), null, ma.getAnatomyId());
                ma.setChildrenJson(childrenTree.toString());

                // also index all UBERON/EFO ids for intermediate MA ids
                Set<String> all_ae_mapped_uberonIds = new HashSet<>();
                Set<String> all_ae_mapped_efoIds = new HashSet<>();

                for (String intermediateId : ma.getIntermediateAnatomyId()) {

                    if (maUberonEfoMap.containsKey(intermediateId) && maUberonEfoMap.get(intermediateId).containsKey("uberon_id")) {
                        all_ae_mapped_uberonIds.addAll(maUberonEfoMap.get(intermediateId).get("uberon_id"));
                    }
                    if (maUberonEfoMap.containsKey(intermediateId) && maUberonEfoMap.get(intermediateId).containsKey("efo_id")) {
                        all_ae_mapped_efoIds.addAll(maUberonEfoMap.get(intermediateId).get("efo_id"));
                    }
                }

                if (ma.getUberonIds() != null) {
                    all_ae_mapped_uberonIds.addAll(ma.getUberonIds());
                    ma.setAll_ae_mapped_uberonIds(new ArrayList<String>(all_ae_mapped_uberonIds));
                }
                if (ma.getEfoIds() != null) {
                    all_ae_mapped_efoIds.addAll(ma.getEfoIds());
                    ma.setAll_ae_mapped_efoIds(new ArrayList<String>(all_ae_mapped_efoIds));
                }

                // Image association fields
//                List<SangerImageDTO> sangerImages = maImagesMap.get(bean.getId());
//                if (sangerImages != null) {
//                    for (SangerImageDTO sangerImage : sangerImages) {
//                        ma.setProcedureName(sangerImage.getProcedureName());
//                        ma.setExpName(sangerImage.getExpName());
//                        ma.setExpNameExp(sangerImage.getExpNameExp());
//                        ma.setSymbolGene(sangerImage.getSymbolGene());
//
//                        ma.setMgiAccessionId(sangerImage.getMgiAccessionId());
//                        ma.setMarkerSymbol(sangerImage.getMarkerSymbol());
//                        ma.setMarkerName(sangerImage.getMarkerName());
//                        ma.setMarkerSynonym(sangerImage.getMarkerSynonym());
//                        ma.setMarkerType(sangerImage.getMarkerType());
//                        ma.setHumanGeneSymbol(sangerImage.getHumanGeneSymbol());
//
//                        ma.setStatus(sangerImage.getStatus());
//
//                        ma.setImitsPhenotypeStarted(sangerImage.getImitsPhenotypeStarted());
//                        ma.setImitsPhenotypeComplete(sangerImage.getImitsPhenotypeComplete());
//                        ma.setImitsPhenotypeStatus(sangerImage.getImitsPhenotypeStatus());
//
//                        ma.setLatestPhenotypeStatus(sangerImage.getLatestPhenotypeStatus());
//                        ma.setLatestPhenotypingCentre(sangerImage.getLatestPhenotypingCentre());
//
//                        ma.setLatestProductionCentre(sangerImage.getLatestProductionCentre());
//                        ma.setLatestPhenotypingCentre(sangerImage.getLatestPhenotypingCentre());
//
//                        ma.setAlleleName(sangerImage.getAlleleName());
//
//                    }
//                }

                count++;
                if ( !maBatch.contains(ma)) {
                    maBatch.add(ma);
                }
//                if (maBatch.size() == BATCH_SIZE) {
//                    // Update the batch, clear the list
//                    documentCount += maBatch.size();
//                    anatomyIndexing.addBeans(maBatch, 60000);
//                    maBatch.clear();
//                }

                documentCount++;
                anatomyIndexing.addBean(ma, 60000);

                if (documentCount % 100 == 0){
                    anatomyIndexing.commit();
                }
            }

            // Add all emapa terms to the index.
            for (OntologyTermBean ebean : emapaBeans) {
                AnatomyDTO emapa = new AnatomyDTO();

                String emapaId = ebean.getId();

                // Set scalars.
                emapa.setDataType("emapa");
                emapa.setAnatomyId(emapaId);
                emapa.setAnatomyTerm(ebean.getName());
                emapa.setStage("embryo");

                if (ebean.getAltIds().size() > 0) {
                    emapa.setAltAnatomyIds(ebean.getAltIds());
                }

                // Set collections.
                OntologyTermHelperEmapa sourceList2 = new OntologyTermHelperEmapa(emapaOntologyService, emapaId);

                //emapa.setOntologySubset(sourceList2.getSubsets());
                emapa.setAnatomyTermSynonym(sourceList2.getSynonyms());

                // Using the level for EMAPA to get top-levels
                // eg. the fullpath:
                // +---------+---------------------------------------+
                // | node_id | fullpath                              |
                // +---------+---------------------------------------+
                // |     729 | 0 1 724 725                           |
                // |    1246 | 0 1 724 1084 1085 1089 1240 1241 1242 |
                // +---------+---------------------------------------+

                // level 2 would be 724 in the fullpath example above
                // this gets you ie, organ, organ sytem, conceptus, embryo, ... (not including nodeId 0)

                int level = 2; // if call w/0 level, default is 1

                OntologyDetail topLevels = sourceList2.getTopLevels(level);
                emapa.setTopLevelAnatomyId(topLevels.getIds());
                emapa.setTopLevelAnatomyTerm(topLevels.getNames());
                emapa.setTopLevelAnatomyTermSynonym(topLevels.getSynonyms());

                //System.out.println("1 CHECK TOP: " + emapa.getTopLevelAnatomyId() + " -- " +  emapa.getTopLevelAnatomyTerm() + " -- " + emapa.getTopLevelAnatomyTermSynonym());
                //System.out.println("1 CHECK TOP: " + topLevels.getIds() + " -- " +  topLevels.getNames() + " -- " + topLevels.getSynonyms());

                OntologyDetail selectedTopLevels = sourceList2.getSelectedTopLevels();
                emapa.setSelectedTopLevelAnatomyId(selectedTopLevels.getIds());
                emapa.setSelectedTopLevelAnatomyTerm(selectedTopLevels.getNames());
                emapa.setSelectedTopLevelAnatomyTermSynonym(selectedTopLevels.getSynonyms());
                //emapa.setSelectedTopLevelIdTerm(selectedTopLevels.getId_name_concatenations());

                //System.out.println("1 CHECK SELTOP: " + emapa.getSelectedTopLevelAnatomyId() + " -- " +  emapa.getSelectedTopLevelAnatomyTerm() + " -- " + emapa.getSelectedTopLevelAnatomyTermSynonym());

                OntologyDetail intermediates = sourceList2.getIntermediates();
                emapa.setIntermediateAnatomyId(intermediates.getIds());
                emapa.setIntermediateAnatomyTerm(intermediates.getNames());
                emapa.setIntermediateAnatomyTermSynonym(intermediates.getSynonyms());

                OntologyDetail parents = sourceList2.getParents();
                emapa.setParentAnatomyId(parents.getIds());
                emapa.setParentAnatomyTerm(parents.getNames());
                emapa.setParentAnatomyTermSynonym(parents.getSynonyms());

                OntologyDetail children = sourceList2.getChildren();
                emapa.setChildAnatomyId(children.getIds());
                emapa.setChildAnatomyTerm(children.getNames());
                emapa.setChildAnatomyTermSynonym(children.getSynonyms());

                emapa.setAnatomyNodeId(ebean.getNodeIds());

                //System.out.println("EMAPA ID: " + emapa.getAnatomyId() + " --- EMAPA node id: " + emapa.getAnatomyNodeId() + " --- " + emapa.getAnatomyTerm());
                // OntologyBrowser stuff
                TreeHelper helper2 = ontologyBrowser.getTreeHelper("emapa", emapaId);
                //helper.setExcludedNodeIds(excludedNodeIds);

                // for MA the root node id is 1 (MP is 0)
                List<JSONObject> searchTree2 = ontologyBrowser.createTreeJson(helper2, "1", null, emapa.getAnatomyId());
                emapa.setSearchTermJson(searchTree2.toString());
                //System.out.println("JSON: "+ emapa.getSearchTermJson());
                String scrollNodeId2 = ontologyBrowser.getScrollTo(searchTree2);
                emapa.setScrollNode(scrollNodeId2);

                List<JSONObject> childrenTree2 = ontologyBrowser.createTreeJson(helper2, "" + emapa.getAnatomyNodeId().get(0), null, emapa.getAnatomyId());
                emapa.setChildrenJson(childrenTree2.toString());

                count++;
//                maBatch.add(emapa);
//                if (maBatch.size() == BATCH_SIZE) {
//                    // Update the batch, clear the list
//                    documentCount += maBatch.size();
//                    anatomyIndexing.addBeans(maBatch, 60000);
//                    maBatch.clear();
//                }
                documentCount++;
                anatomyIndexing.addBean(emapa, 60000);

                if (documentCount % 100 == 0){
                    anatomyIndexing.commit();
                }

            }

            // Make sure the last batch is indexed
//            if (maBatch.size() > 0) {
//                documentCount += maBatch.size();
//                anatomyIndexing.addBeans(maBatch, 60000);
//            }

            // Send a final commit
            anatomyIndexing.commit();

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
//            logger.debug(" WRITING ma     CORE TO: " + SolrUtils.getBaseURL(anatomyIndexing));
//            logger.debug(" USING   images CORE AT: " + SolrUtils.getBaseURL(imagesCore));
//        }
//    }


    // PRIVATE METHODS

    private final Integer MAX_ITERATIONS = 2;                                   // Set to non-null value > 0 to limit max_iterations.

    private void initialiseSupportingBeans() throws IndexerException, SQLException, IOException {
        // Grab all the supporting database content
        maImagesMap = IndexerMap.getSangerImagesByMA(imagesCore);
        if (logger.isDebugEnabled()) {
            IndexerMap.dumpSangerImagesMap(maImagesMap, "Images map:", MAX_ITERATIONS);
        }

        maUberonEfoMap = IndexerMap.mapMaToUberronOrEfo(resource);
    }

    public static void main(String[] args) throws IndexerException, SQLException, IOException, SolrServerException {
        SpringApplication.run(AnatomyIndexer.class, args);
    }
}
