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
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.EmapaOntologyDAO;
import org.mousephenotype.cda.db.dao.MaOntologyDAO;
import org.mousephenotype.cda.db.dao.OntologyDetail;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.OntologyBrowserGetter;
import org.mousephenotype.cda.indexers.utils.OntologyBrowserGetter.TreeHelper;
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
import java.sql.SQLException;
import java.util.*;

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

    @Value("classpath:uberonEfoMaAnatomogram_mapping.txt")
    Resource anatomogramResource;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;

//    @Autowired
//    @Qualifier("sangerImagesCore")
//    SolrClient imagesCore;

    @Autowired
    @Qualifier("anatomyCore")
    SolrClient anatomyCore;

    @Autowired
    MaOntologyDAO maOntologyService;

    @Autowired
    EmapaOntologyDAO emapaOntologyService;


    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap<>();      // key = term_id.
    private Map<String, Map<String,List<String>>> maUberonEfoMap = new HashMap<>();      // key = term_id.

    protected static final Set<String> TOP_LEVEL_MA_TERMS = new HashSet<>(Arrays.asList("EMAP:31887", "EMAP:31902", "EMAP:33590",
    "EMAP:33659", "EMAP:3981", "EMAP:3987", "EMAP:4011", "EMAP:4103", "EMAP:4109", "EMAP:4636", "EMAP:4651",
    "MA:0000004", "MA:0000007", "MA:0000009", "MA:0000010", "MA:0000012", "MA:0000014", "MA:0000016", "MA:0000017",
    "MA:0000325", "MA:0000326", "MA:0000327", "MA:0002411", "MA:0002418", "MA:0002431", "MA:0002711"));

    public AnatomyIndexer() {

    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(anatomyCore);
    }


    @Override
    public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {

        int count = 0;
        
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();
        OntologyBrowserGetter ontologyBrowser = new OntologyBrowserGetter(ontodbDataSource);


    	try {

            // Delete the documents in the core if there are any.
            anatomyCore.deleteByQuery("*:*");
            anatomyCore.commit();

//            logger.info(" Source of images core: " + SolrUtils.getBaseURL(imagesCore) );
            initialiseSupportingBeans();

            List<OntologyTermBean> maBeans = maOntologyService.getAllTerms();
            List<OntologyTermBean> emapaBeans = emapaOntologyService.getAllTerms();


            // fetch list of excludedNodeIds (do not want to display this part of tree for MA)
            //List<String> excludedNodeIds = ontologyBrowser.getExcludedNodeIds();

            // Add all ma terms to the index.
            for (OntologyTermBean bean : maBeans) {
                AnatomyDTO anatomyTerm = new AnatomyDTO();

                String id = bean.getId();

                // Set scalars.
                anatomyTerm.setDataType("ma");
                anatomyTerm.setAnatomyId(id);
                anatomyTerm.setAnatomyTerm(bean.getName());
                anatomyTerm.setStage("adult");

                if (bean.getAltIds().size() > 0) {
                    anatomyTerm.setAltAnatomyIds(bean.getAltIds());
                }

                anatomyTerm.setAnatomyTermSynonym(maOntologyService.getSynonyms(id));

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

                OntologyDetail topLevels = maOntologyService.getTopLevel(level, id);
                anatomyTerm.setTopLevelAnatomyId(topLevels.getIds());
                anatomyTerm.setTopLevelAnatomyTerm(topLevels.getNames());
                anatomyTerm.setTopLevelAnatomyTermSynonym(topLevels.getSynonyms());

//                System.out.println("1 CHECK TOP: " + ma.getTopLevelId() + " -- " +  ma.getTopLevelTerm() + " -- " + ma.getTopLevelTermSynonym());
//                System.out.println("1 CHECK TOP: " + topLevels.getIds() + " -- " +  topLevels.getNames() + " -- " + topLevels.getSynonyms());

                OntologyDetail selectedTopLevels = maOntologyService.getSelectedTopLevelDetails(id);
                anatomyTerm.setSelectedTopLevelAnatomyId(selectedTopLevels.getIds());
                anatomyTerm.setSelectedTopLevelAnatomyTerm(selectedTopLevels.getNames());
                anatomyTerm.setSelectedTopLevelAnatomyTermSynonym(selectedTopLevels.getSynonyms());
                //ma.setSelectedTopLevelIdTerm(selectedTopLevels.getId_name_concatenations());

//                System.out.println("1 CHECK SELTOP: " + ma.getSelectedTopLevelId() + " -- " +  ma.getSelectedTopLevelTerm() + " -- " + ma.getSelectedTopLevelTermSynonym());

                OntologyDetail intermediates = maOntologyService.getIntermediatesDetail(id);
                anatomyTerm.setIntermediateAnatomyId(intermediates.getIds());
                anatomyTerm.setIntermediateAnatomyTerm(intermediates.getNames());
                anatomyTerm.setIntermediateAnatomyTermSynonym(intermediates.getSynonyms());

                OntologyDetail parents = maOntologyService.getParentsDetails(id);
                anatomyTerm.setParentAnatomyId(parents.getIds());
                anatomyTerm.setParentAnatomyTerm(parents.getNames());
                anatomyTerm.setParentAnatomyTermSynonym(parents.getSynonyms());

                OntologyDetail children = maOntologyService.getChildrenDetails(id);
                anatomyTerm.setChildAnatomyId(children.getIds());
                anatomyTerm.setChildAnatomyTerm(children.getNames());
                anatomyTerm.setChildAnatomyTermSynonym(children.getSynonyms());

                anatomyTerm.setAnatomyNodeId(bean.getNodeIds());

                // index UBERON/EFO id for MA id

                if (maUberonEfoMap.containsKey(id)) {
                    if (maUberonEfoMap.get(id).containsKey("uberon_id")) {
                        anatomyTerm.setUberonIds(maUberonEfoMap.get(id).get("uberon_id"));
                    }
                    if (maUberonEfoMap.get(id).containsKey("efo_id")) {
                        anatomyTerm.setEfoIds(maUberonEfoMap.get(id).get("efo_id"));
                    }
                }

                //System.out.println("MA ID: " + ma.getMaId() + " --- MA node id: " + ma.getMaNodeId() + " --- " + ma.getMaTerm());
                // OntologyBrowser stuff
                TreeHelper helper = ontologyBrowser.getTreeHelper("ma", anatomyTerm.getAnatomyId());
                //helper.setExcludedNodeIds(excludedNodeIds);

                // for  the root node id is 1 (MP is 0)
                List<JSONObject> searchTree = ontologyBrowser.createTreeJson(helper, "1", null, anatomyTerm.getAnatomyId(), null);
                anatomyTerm.setSearchTermJson(searchTree.toString());

                String scrollNodeId = ontologyBrowser.getScrollTo(searchTree);
                anatomyTerm.setScrollNode(scrollNodeId);
                List<JSONObject> childrenTree = ontologyBrowser.createTreeJson(helper, "" + maOntologyService.getNodeIds(anatomyTerm.getAnatomyId()).get(0), null, anatomyTerm.getAnatomyId(), null);
                anatomyTerm.setChildrenJson(childrenTree.toString());

                // also index all UBERON/EFO ids for intermediate MA ids
                Set<String> all_ae_mapped_uberonIds = new HashSet<>();
                Set<String> all_ae_mapped_efoIds = new HashSet<>();

                for (String intermediateId : anatomyTerm.getIntermediateAnatomyId()) {

                    if (maUberonEfoMap.containsKey(intermediateId) && maUberonEfoMap.get(intermediateId).containsKey("uberon_id")) {
                        all_ae_mapped_uberonIds.addAll(maUberonEfoMap.get(intermediateId).get("uberon_id"));
                    }
                    if (maUberonEfoMap.containsKey(intermediateId) && maUberonEfoMap.get(intermediateId).containsKey("efo_id")) {
                        all_ae_mapped_efoIds.addAll(maUberonEfoMap.get(intermediateId).get("efo_id"));
                    }
                }

                if (anatomyTerm.getUberonIds() != null) {
                    all_ae_mapped_uberonIds.addAll(anatomyTerm.getUberonIds());
                    anatomyTerm.setAll_ae_mapped_uberonIds(new ArrayList<String>(all_ae_mapped_uberonIds));
                }
                if (anatomyTerm.getEfoIds() != null) {
                    all_ae_mapped_efoIds.addAll(anatomyTerm.getEfoIds());
                    anatomyTerm.setAll_ae_mapped_efoIds(new ArrayList<String>(all_ae_mapped_efoIds));
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

                documentCount++;
                anatomyCore.addBean(anatomyTerm, 60000);

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

                emapa.setAnatomyTermSynonym(emapaOntologyService.getSynonyms(emapaId));

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

                OntologyDetail topLevels = emapaOntologyService.getTopLevel(level, emapaId);
                emapa.setTopLevelAnatomyId(topLevels.getIds());
                emapa.setTopLevelAnatomyTerm(topLevels.getNames());
                emapa.setTopLevelAnatomyTermSynonym(topLevels.getSynonyms());

                //System.out.println("1 CHECK TOP: " + emapa.getTopLevelAnatomyId() + " -- " +  emapa.getTopLevelAnatomyTerm() + " -- " + emapa.getTopLevelAnatomyTermSynonym());
                //System.out.println("1 CHECK TOP: " + topLevels.getIds() + " -- " +  topLevels.getNames() + " -- " + topLevels.getSynonyms());

                OntologyDetail selectedTopLevels = emapaOntologyService.getSelectedTopLevelDetails(emapaId);
                emapa.setSelectedTopLevelAnatomyId(selectedTopLevels.getIds());
                emapa.setSelectedTopLevelAnatomyTerm(selectedTopLevels.getNames());
                emapa.setSelectedTopLevelAnatomyTermSynonym(selectedTopLevels.getSynonyms());
                //emapa.setSelectedTopLevelIdTerm(selectedTopLevels.getId_name_concatenations());

                //System.out.println("1 CHECK SELTOP: " + emapa.getSelectedTopLevelAnatomyId() + " -- " +  emapa.getSelectedTopLevelAnatomyTerm() + " -- " + emapa.getSelectedTopLevelAnatomyTermSynonym());

                OntologyDetail intermediates = emapaOntologyService.getIntermediatesDetail(emapaId);
                emapa.setIntermediateAnatomyId(intermediates.getIds());
                emapa.setIntermediateAnatomyTerm(intermediates.getNames());
                emapa.setIntermediateAnatomyTermSynonym(intermediates.getSynonyms());

                OntologyDetail parents = emapaOntologyService.getParentsDetails(emapaId);
                emapa.setParentAnatomyId(parents.getIds());
                emapa.setParentAnatomyTerm(parents.getNames());
                emapa.setParentAnatomyTermSynonym(parents.getSynonyms());

                OntologyDetail children = emapaOntologyService.getChildrenDetails(emapaId);
                emapa.setChildAnatomyId(children.getIds());
                emapa.setChildAnatomyTerm(children.getNames());
                emapa.setChildAnatomyTermSynonym(children.getSynonyms());

                emapa.setAnatomyNodeId(ebean.getNodeIds());

                //System.out.println("EMAPA ID: " + emapa.getAnatomyId() + " --- EMAPA node id: " + emapa.getAnatomyNodeId() + " --- " + emapa.getAnatomyTerm());
                // OntologyBrowser stuff
                TreeHelper helper2 = ontologyBrowser.getTreeHelper("emapa", emapaId);
                //helper.setExcludedNodeIds(excludedNodeIds);

                // for MA the root node id is 1 (MP is 0)
                List<JSONObject> searchTree2 = ontologyBrowser.createTreeJson(helper2, "1", null, emapa.getAnatomyId(), null);
                emapa.setSearchTermJson(searchTree2.toString());
                //System.out.println("JSON: "+ emapa.getSearchTermJson());
                String scrollNodeId2 = ontologyBrowser.getScrollTo(searchTree2);
                emapa.setScrollNode(scrollNodeId2);

                List<JSONObject> childrenTree2 = ontologyBrowser.createTreeJson(helper2, "" + emapa.getAnatomyNodeId().get(0), null, emapa.getAnatomyId(), null);
                emapa.setChildrenJson(childrenTree2.toString());

                count++;

                documentCount++;
                anatomyCore.addBean(emapa, 60000);

            }


            // Send a final commit
            anatomyCore.commit();

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
//            logger.debug(" WRITING ma     CORE TO: " + SolrUtils.getBaseURL(anatomyCore));
//            logger.debug(" USING   images CORE AT: " + SolrUtils.getBaseURL(imagesCore));
//        }
//    }


    // PRIVATE METHODS

    private final Integer MAX_ITERATIONS = 2;                                   // Set to non-null value > 0 to limit max_iterations.

    private void initialiseSupportingBeans() throws IndexerException, SQLException, IOException {
//        // Grab all the supporting database content
//        maImagesMap = IndexerMap.getSangerImagesByMA(imagesCore);
//        if (logger.isDebugEnabled()) {
//            IndexerMap.dumpSangerImagesMap(maImagesMap, "Images map:", MAX_ITERATIONS);
//        }

        try {
            maUberonEfoMap = IndexerMap.mapMaToUberronOrEfoForAnatomogram(anatomogramResource);
        } catch (SQLException | IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) throws IndexerException, SQLException, IOException, SolrServerException {
        SpringApplication.run(AnatomyIndexer.class, args);
    }
}
