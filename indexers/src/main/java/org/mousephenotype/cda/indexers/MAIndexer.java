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
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.MaOntologyDAO;
import org.mousephenotype.cda.indexers.beans.OntologyTermMaBeanList;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.MaDTO;
import org.mousephenotype.cda.solr.service.dto.SangerImageDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mousephenotype.cda.db.dao.OntologyDAO.BATCH_SIZE;

/**
 * Populate the MA core
 */
public class MAIndexer extends AbstractIndexer {
    CommonUtils commonUtils = new CommonUtils();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
   
    @Value("classpath:uberonEfoMa_mappings.txt")
	Resource resource;
    
    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;

    @Autowired
    @Qualifier("sangerImagesReadOnlyIndexing")
    SolrServer imagesCore;

    @Autowired
    @Qualifier("maIndexing")
    SolrServer maCore;

    @Autowired
    MaOntologyDAO maOntologyService;
    
    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap();      // key = term_id.
    private Map<String, Map<String,List<String>>> maUberonEfoMap = new HashMap();      // key = term_id.
    
    public MAIndexer() {

    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(maCore);
    }

    @Override
    public void initialise(String[] args, RunStatus runStatus) throws IndexerException {
        super.initialise(args, runStatus);
    }

    @Override
    public RunStatus run() throws IndexerException {
        int count = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

    	try {
    		logger.info(" Source of images core: " + ((HttpSolrServer) imagesCore).getBaseURL() );
            initialiseSupportingBeans();

            List<MaDTO> maBatch = new ArrayList(BATCH_SIZE);

            // Add all ma terms to the index.
            List<OntologyTermBean> beans = maOntologyService.getAllTerms();
            for (OntologyTermBean bean : beans) {
                MaDTO ma = new MaDTO();

                String maId = bean.getId();
                // Set scalars.
                ma.setDataType("ma");
                ma.setMaId(maId);
                ma.setMaTerm(bean.getName());

                if ( bean.getAltIds().size() > 0) {
                    ma.setAltMaIds(bean.getAltIds());
                }

                // index UBERON/EFO id for MA id
                if ( maUberonEfoMap.containsKey(maId) ){
                	
                	if ( maUberonEfoMap.get(maId).containsKey("uberon_id") ){
                		ma.setUberonIds(maUberonEfoMap.get(maId).get("uberon_id"));
                	}
                	if ( maUberonEfoMap.get(maId).containsKey("efo_id") ){
                		ma.setEfoIds(maUberonEfoMap.get(maId).get("efo_id"));
                	}
                }
                
                // Set collections.
                OntologyTermMaBeanList sourceList = new OntologyTermMaBeanList(maOntologyService, bean.getId());
                ma.setOntologySubset(sourceList.getSubsets());
                ma.setMaTermSynonym(sourceList.getSynonyms());

                ma.setChildMaId(sourceList.getChildren().getIds());
                ma.setChildMaIdTerm(sourceList.getChildren().getId_name_concatenations());
                ma.setChildMaTerm(sourceList.getChildren().getNames());
                ma.setChildMaTermSynonym(sourceList.getChildren().getSynonyms());

                ma.setSelectedTopLevelMaId(sourceList.getTopLevels().getIds());
                ma.setSelectedTopLevelMaTerm(sourceList.getTopLevels().getNames());
                ma.setSelectedTopLevelMaTermSynonym(sourceList.getTopLevels().getSynonyms());

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

                count ++;
                maBatch.add(ma);
                if (maBatch.size() == BATCH_SIZE) {
                    // Update the batch, clear the list
                    documentCount += maBatch.size();
                    maCore.addBeans(maBatch, 60000);
                    maBatch.clear();
                }
            }

            // Make sure the last batch is indexed
            if (maBatch.size() > 0) {
                documentCount += maBatch.size();
                maCore.addBeans(maBatch, 60000);
                count += maBatch.size();
            }

            // Send a final commit
            maCore.commit();

        } catch (SQLException | SolrServerException | IOException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));

        return runStatus;
    }


    // PROTECTED METHODS


    @Override
    protected void printConfiguration() {
        if (logger.isDebugEnabled()) {
            logger.debug(" WRITING ma     CORE TO: " + SolrUtils.getBaseURL(maCore));
            logger.debug(" USING   images CORE AT: " + SolrUtils.getBaseURL(imagesCore));
        }
    }


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

    public static void main(String[] args) throws IndexerException, SQLException {

        RunStatus runStatus = new RunStatus();
        MAIndexer indexer = new MAIndexer();
        indexer.initialise(args, runStatus);
        indexer.run();
        indexer.validateBuild();
    }
}
