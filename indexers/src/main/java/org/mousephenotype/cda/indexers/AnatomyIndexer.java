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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONObject;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.OntologyBrowserGetter;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Qualifier("anatomyCore")
    SolrClient anatomyCore;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;


    private OntologyParser maParser;
    private OntologyParser emapaParser;

    private Map<String, Map<String,List<String>>> maUberonEfoMap = new HashMap<>();      // key = term_id.


    public AnatomyIndexer() {

    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(anatomyCore);
    }


    @Override
    public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {
        
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();
        OntologyBrowserGetter browser = new OntologyBrowserGetter();

    	try {

            // Delete the documents in the core if there are any.
            anatomyCore.deleteByQuery("*:*");
            anatomyCore.commit();

            initialiseSupportingBeans();

            Set<String> maIds = maParser.getTermsInSlim();
            Set<String> emapaIds = emapaParser.getTermsInSlim();

            // Add all ma terms to the index.
            for (String maId : maIds ) {

                AnatomyDTO anatomyTerm = new AnatomyDTO();
                OntologyTermDTO maTerm = maParser.getOntologyTerm(maId);

                // Set scalars.
                anatomyTerm.setDataType("ma");
                anatomyTerm.setStage("adult");

                // Set fields in common with EMAPA documents
                addBasicFields(anatomyTerm, maTerm, browser, maParser, TREE_TOP_LEVEL_MA_TERMS);

                // index UBERON/EFO id for MA id
                //TODO replace file for uberon mapping, use OntologyParser directly
//                if (maUberonEfoMap.containsKey(maId)) {
//                    if (maUberonEfoMap.get(maId).containsKey("uberon_id")) {
//                        anatomyTerm.setUberonIds(maUberonEfoMap.get(maId).get("uberon_id"));
//                    }
//                    if (maUberonEfoMap.get(maId).containsKey("efo_id")) {
//                        anatomyTerm.setEfoIds(maUberonEfoMap.get(maId).get("efo_id"));
//                    }
//                }
//
//
//                // also index all UBERON/EFO ids for intermediate MA ids
//                Set<String> all_ae_mapped_uberonIds = new HashSet<>();
//                Set<String> all_ae_mapped_efoIds = new HashSet<>();
//
//                for (String intermediateId : anatomyTerm.getIntermediateAnatomyId()) {
//                    if (maUberonEfoMap.containsKey(intermediateId) && maUberonEfoMap.get(intermediateId).containsKey("uberon_id")) {
//                        all_ae_mapped_uberonIds.addAll(maUberonEfoMap.get(intermediateId).get("uberon_id"));
//                    }
//                    if (maUberonEfoMap.containsKey(intermediateId) && maUberonEfoMap.get(intermediateId).containsKey("efo_id")) {
//                        all_ae_mapped_efoIds.addAll(maUberonEfoMap.get(intermediateId).get("efo_id"));
//                    }
//                }
//
//                if (anatomyTerm.getUberonIds() != null) {
//                    all_ae_mapped_uberonIds.addAll(anatomyTerm.getUberonIds());
//                    anatomyTerm.setAll_ae_mapped_uberonIds(new ArrayList<String>(all_ae_mapped_uberonIds));
//                }
//                if (anatomyTerm.getEfoIds() != null) {
//                    all_ae_mapped_efoIds.addAll(anatomyTerm.getEfoIds());
//                    anatomyTerm.setAll_ae_mapped_efoIds(new ArrayList<String>(all_ae_mapped_efoIds));
//                }

                documentCount++;
                anatomyCore.addBean(anatomyTerm, 60000);

            }

            // Add all emapa terms to the index.
            for (String emapaId  : emapaIds) {

                AnatomyDTO emapa = new AnatomyDTO();
                OntologyTermDTO emapaDTO = emapaParser.getOntologyTerm(emapaId);

                emapa.setDataType("emapa");
                emapa.setStage("embryo");

                addBasicFields(emapa, emapaDTO, browser, emapaParser, TREE_TOP_LEVEL_EMAPA_TERMS);

                documentCount++;
                anatomyCore.addBean(emapa, 60000);

            }


            // Send a final commit
            anatomyCore.commit();

        } catch (SQLException | SolrServerException | IOException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", documentCount, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }


    private void addBasicFields(AnatomyDTO anatomyDTO, OntologyTermDTO termDTO, OntologyBrowserGetter browser, OntologyParser parser, List<String> topLevelsForTree){

        anatomyDTO.setAnatomyId(termDTO.getAccessionId());
        anatomyDTO.setAnatomyTerm(termDTO.getName());

        if (termDTO.getAlternateIds() != null && termDTO.getAlternateIds().size() > 0) {
            anatomyDTO.setAltAnatomyIds(termDTO.getAlternateIds());
        }

        anatomyDTO.setAnatomyTermSynonym(termDTO.getSynonyms());

        // TODO top level for anatomy are NOT selected top levels, but organ, organ sytem, anatomic region, postnatal mouse, ... (not including nodeId 0)

        anatomyDTO.setTopLevelAnatomyId(termDTO.getTopLevelIds());
        anatomyDTO.setTopLevelAnatomyTerm(termDTO.getTopLevelNames());
        anatomyDTO.setTopLevelAnatomyTermSynonym(termDTO.getTopLevelSynonyms());

        anatomyDTO.setSelectedTopLevelAnatomyId(termDTO.getTopLevelIds());
        anatomyDTO.setSelectedTopLevelAnatomyTerm(termDTO.getTopLevelNames());
        anatomyDTO.setSelectedTopLevelAnatomyTermSynonym(termDTO.getTopLevelSynonyms());

        anatomyDTO.setIntermediateAnatomyId(termDTO.getIntermediateIds());
        anatomyDTO.setIntermediateAnatomyTerm(termDTO.getIntermediateNames());
        anatomyDTO.setIntermediateAnatomyTermSynonym(termDTO.getIntermediateSynonyms());

        anatomyDTO.setParentAnatomyId(termDTO.getParentIds());
        anatomyDTO.setParentAnatomyTerm(termDTO.getParentNames());

        anatomyDTO.setChildAnatomyId(termDTO.getChildIds());
        anatomyDTO.setChildAnatomyTerm(termDTO.getChildNames());

        anatomyDTO.setAnatomyNodeId(termDTO.getNodeIds());

        System.out.println("----- " + termDTO.getAccessionId() + "  " + termDTO.getChildIds() + " " + termDTO.getPathsToRoot());
        List<JSONObject> searchTree = browser.createTreeJson(termDTO, "/data/anatomy/", parser, null, topLevelsForTree);
        anatomyDTO.setSearchTermJson(searchTree.toString());
        String scrollNodeId = browser.getScrollTo(searchTree);
        anatomyDTO.setScrollNode(scrollNodeId);
        List<JSONObject> childrenTree = browser.getChildrenJson(termDTO, "/data/anatomy/", parser, null);
        anatomyDTO.setChildrenJson(childrenTree.toString());


    }

    private void initialiseSupportingBeans() throws IndexerException, SQLException, IOException {

        try {
            maUberonEfoMap = IndexerMap.mapMaToUberronOrEfoForAnatomogram(anatomogramResource);
            emapaParser = getEmapaParser();
            emapaParser.fillJsonTreePath("EMAPA:25765"); // mouse
            maParser = getMaParser();
            maParser.fillJsonTreePath("MA:0000001");
        } catch (SQLException | IOException | OWLOntologyCreationException | OWLOntologyStorageException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) throws IndexerException, SQLException, IOException, SolrServerException {
        SpringApplication.run(AnatomyIndexer.class, args);
    }


}
