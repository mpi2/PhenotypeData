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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.dao.DatasourceDAO;
import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.Xref;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.exceptions.ValidationException;
import org.mousephenotype.cda.indexers.utils.EmbryoRestData;
import org.mousephenotype.cda.indexers.utils.EmbryoRestGetter;
import org.mousephenotype.cda.indexers.utils.EmbryoStrain;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.*;
import org.netbeans.lib.cvsclient.commandLine.command.log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Populate the MA core
 */
public class GeneIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(GeneIndexer.class);
    private Connection komp2DbConnection;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    @Qualifier("alleleIndexing")
    SolrServer alleleCore;

    @Autowired
    @Qualifier("geneIndexing")
    SolrServer geneCore;

    @Autowired
    @Qualifier("mpIndexing")
    SolrServer mpCore;

    @Autowired
    @Qualifier("sangerImagesIndexing")
    SolrServer imagesCore;
    
    @Autowired
	private DatasourceDAO datasourceDao;

    private Map<String, List<Map<String, String>>> phenotypeSummaryGeneAccessionsToPipelineInfo = new HashMap<>();
    private Map<String, Map<String, String>> genomicFeatureCoordinates = new HashMap<>();
    private Map<String, List<Xref>> genomicFeatureXrefs = new HashMap<>();
    private Map<String, List<SangerImageDTO>> sangerImages = new HashMap<>();
    private Map<String, List<MpDTO>> mgiAccessionToMP = new HashMap<>();
    Map<String, List<EmbryoStrain>> embryoRestData=null;
    
    
    EmbryoRestGetter embryoGetter;
    
    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    public GeneIndexer() {

    }

    @Override
    public void validateBuild() throws IndexerException {
        Long numFound = getDocumentCount(geneCore);

        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual gene document count is " + numFound + "."));

        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " gene documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " gene documents.");
    }

    @Override
    public void initialise(String[] args) throws IndexerException {
    	
        super.initialise(args);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        final String embryoRestUrl=config.get("embryoRestUrl");
        this.embryoGetter=new EmbryoRestGetter(embryoRestUrl);
        try {

            komp2DbConnection = komp2DataSource.getConnection();

        } catch (SQLException sqle) {
            logger.error("Caught SQL Exception initialising database connections: {}", sqle.getMessage());
            throw new IndexerException(sqle);
        }

    }

    @Override
    public void run() throws IndexerException {

    	
    	Datasource ensembl = datasourceDao.getDatasourceByShortName("Ensembl");
		Datasource vega = datasourceDao.getDatasourceByShortName("VEGA");
		Datasource ncbi = datasourceDao.getDatasourceByShortName("EntrezGene");
		Datasource ccds = datasourceDao.getDatasourceByShortName("cCDS");
		
        long startTime = System.currentTimeMillis();
        try {
            logger.info("Starting Gene Indexer...");

            initialiseSupportingBeans();

            int count = 0;
            List<AlleleDTO> alleles = IndexerMap.getAlleles(alleleCore);
            logger.info("alleles size=" + alleles.size());

            geneCore.deleteByQuery("*:*");

            for (AlleleDTO allele : alleles) {
                //System.out.println("allele="+allele.getMarkerSymbol());
                GeneDTO gene = new GeneDTO();
                gene.setMgiAccessionId(allele.getMgiAccessionId());
                gene.setDataType(allele.getDataType());
                gene.setMarkerType(allele.getMarkerType());
                gene.setMarkerSymbol(allele.getMarkerSymbol());
                gene.setMarkerSymbolLowercase(allele.getMarkerSymbol());
                gene.setMarkerSynonym(allele.getMarkerSynonym());
                gene.setMarkerName(allele.getMarkerName());
                gene.setHumanGeneSymbol(allele.getHumanGeneSymbol());
                gene.setEnsemblGeneIds(allele.getEnsemblGeneIds());
                gene.setLatestEsCellStatus(allele.getLatestEsCellStatus());
                gene.setImitsPhenotypeStarted(allele.getImitsPhenotypeStarted());
                gene.setImitsPhenotypeComplete(allele.getImitsPhenotypeComplete());
                gene.setImitsPhenotypeStatus(allele.getImitsPhenotypeStatus());
                gene.setLatestMouseStatus(allele.getLatestMouseStatus());
                gene.setLatestProjectStatus(allele.getLatestProjectStatus());
                gene.setStatus(allele.getStatus());
                gene.setLatestPhenotypeStatus(allele.getLatestPhenotypeStatus());
                gene.setLegacy_phenotype_status(allele.getLegacyPhenotypeStatus());
                gene.setLatestProductionCentre(allele.getLatestProductionCentre());
                gene.setLatestPhenotypingCentre(allele.getLatestPhenotypingCentre());
                gene.setAlleleName(allele.getAlleleName());
                gene.setEsCellStatus(allele.getEsCellStatus());
                gene.setMouseStatus(allele.getMouseStatus());
                gene.setPhenotypeStatus(allele.getPhenotypeStatus());
                gene.setProductionCentre(allele.getProductionCentre());
                gene.setPhenotypingCentre(allele.getPhenotypingCentre());
                gene.setType(allele.getType());
                gene.setDiseaseSource(allele.getDiseaseSource());
                gene.setDiseaseId(allele.getDiseaseId());
                gene.setDiseaseTerm(allele.getDiseaseTerm());
                gene.setDiseaseAlts(allele.getDiseaseAlts());
                gene.setDiseaseClasses(allele.getDiseaseClasses());
                gene.setHumanCurated(allele.getHumanCurated());
                gene.setMouseCurated(allele.getMouseCurated());
                gene.setMgiPredicted(allele.getMgiPredicted());
                gene.setImpcPredicted(allele.getImpcPredicted());
                gene.setMgiPredicted(allele.getMgiPredicted());
                gene.setMgiPredictedKnonwGene(allele.getMgiPredictedKnownGene());
                gene.setImpcNovelPredictedInLocus(allele.getImpcNovelPredictedInLocus());
                gene.setDiseaseHumanPhenotypes(allele.getDiseaseHumanPhenotypes());

                // GO stuff
                gene.setGoTermIds(allele.getGoTermIds());
                gene.setGoTermNames(allele.getGoTermNames());
               // gene.getGoTermDefs().addAll(allele.getGoTermDefs());
                gene.setGoTermEvids(allele.getGoTermEvids());
                gene.setGoTermDomains(allele.getGoTermDomains());
                gene.setEvidCodeRank(allele.getEvidCodeRank());
                gene.setGoCount(allele.getGoCount());
                gene.setGoUniprot(allele.getGoUniprot());

                // pfam stuff
                gene.setUniprotAccs(allele.getUniprotAccs());
                gene.setScdbIds(allele.getScdbIds());
                gene.setScdbLinks(allele.getScdbLinks());
                gene.setClanIds(allele.getClanIds());
                gene.setClanAccs(allele.getClanAccs());
                gene.setClanDescs(allele.getClanDescs());
                gene.setPfamaIds(allele.getPfamaIds());
                gene.setPfamaAccs(allele.getPfamaAccs());
                gene.setPfamaGoIds(allele.getPfamaGoIds());
                gene.setPfamaGoTerms(allele.getPfamaGoTerms());
                gene.setPfamaGoCats(allele.getPfamaGoCats());
                gene.setPfamaJsons(allele.getPfamaJsons());
                
                if(embryoRestData!=null){
                	List<EmbryoStrain> embryoStrainsForGene = embryoRestData.get(gene.getMgiAccessionId());
                	//for the moment lets just set an embryo data available flag!
                	if(embryoStrainsForGene!=null && embryoStrainsForGene.size()>0){
                		gene.setEmbryoDataAvailable(true);
                		logger.info("setting embryo true");
                	}
                	
                }
                
                if(genomicFeatureCoordinates!=null && genomicFeatureXrefs!=null){
                	if(genomicFeatureCoordinates.containsKey(allele.getMgiAccessionId())){
                		Map<String, String> coordsMap = genomicFeatureCoordinates.get(allele.getMgiAccessionId());
                		//System.out.println("coords map found:"+coordsMap);
                		gene.setSeqRegionId(coordsMap.get(GeneDTO.SEQ_REGION_ID));
                		gene.setSeqRegionStart(Integer.valueOf(coordsMap.get(GeneDTO.SEQ_REGION_START)));
                		gene.setSeqRegionEnd(Integer.valueOf(coordsMap.get(GeneDTO.SEQ_REGION_END)));
                		List<String> ensemblIds = new ArrayList<String>();
        				List<String> vegaIds = new ArrayList<String>();
        				List<String> ncbiIds = new ArrayList<String>();
        				List<String> ccdsIds = new ArrayList<String>();
        				List<String> xrefAccessions=new ArrayList<>();
                		if(genomicFeatureXrefs.containsKey(allele.getMgiAccessionId())){
                			List<Xref> xrefs = genomicFeatureXrefs.get(allele.getMgiAccessionId());
                			for(Xref xref:xrefs){
                				String xrefAccession=xref.getXrefAccession();
                				System.out.println("setting xrefs:"+xrefAccession);
                				
                        			if (xref.getXrefDatabaseId() == ensembl.getId()) {
                        				ensemblIds.add(xref.getXrefAccession());
                        			} else if (xref.getXrefDatabaseId() == vega.getId()) {
                        				vegaIds.add(xref.getXrefAccession());
                        			} else if (xref.getXrefDatabaseId() == ncbi.getId()) {
                        				ncbiIds.add(xref.getXrefAccession());
                        			} else if (xref.getXrefDatabaseId() == ccds.getId()) {
                        				ccdsIds.add(xref.getXrefAccession());
                        			}	
                			}
                			gene.setXrefs(xrefAccessions);
                			gene.setNcbiIds(ncbiIds);
                			gene.setVegaIds(vegaIds);
                			gene.setCcdsIds(ccdsIds);
                		}
                	}
                }

				//gene.setMpId(allele.getM)
                // Populate pipeline and procedure info if we have a phenotypeCallSummary entry for this allele/gene
                if (phenotypeSummaryGeneAccessionsToPipelineInfo.containsKey(allele.getMgiAccessionId())) {
                    List<Map<String, String>> rows = phenotypeSummaryGeneAccessionsToPipelineInfo.get(allele.getMgiAccessionId());
                    List<String> pipelineNames = new ArrayList<>();
                    List<String> pipelineStableIds = new ArrayList<>();
                    List<String> procedureNames = new ArrayList<>();
                    List<String> procedureStableIds = new ArrayList<>();
                    List<String> parameterNames = new ArrayList<>();
                    List<String> parameterStableIds = new ArrayList<>();
                    for (Map<String, String> row : rows) {
                        pipelineNames.add(row.get(ObservationDTO.PIPELINE_NAME));
                        pipelineStableIds.add(row.get(ObservationDTO.PIPELINE_STABLE_ID));
                        procedureNames.add(row.get(ObservationDTO.PROCEDURE_NAME));
                        procedureStableIds.add(row.get(ObservationDTO.PROCEDURE_STABLE_ID));
                        parameterNames.add(row.get(ObservationDTO.PARAMETER_NAME));
                        parameterStableIds.add(row.get(ObservationDTO.PARAMETER_STABLE_ID));

                    }
                    gene.setPipelineName(pipelineNames);
                    gene.setPipelineStableId(pipelineStableIds);
                    gene.setProcedureName(procedureNames);
                    gene.setProcedureStableId(procedureStableIds);
                    gene.setParameterName(parameterNames);
                    gene.setParameterStableId(parameterStableIds);
                }

				//do images core data
                // Initialize all the ontology term lists
                gene.setMpId(new ArrayList<String>());
                gene.setMpTerm(new ArrayList<String>());
                gene.setMpTermSynonym(new ArrayList<String>());
                gene.setMpTermDefinition(new ArrayList<String>());
                gene.setOntologySubset(new ArrayList<String>());

                gene.setMaId(new ArrayList<String>());
                gene.setMaTerm(new ArrayList<String>());
                gene.setMaTermSynonym(new ArrayList<String>());
                gene.setMaTermDefinition(new ArrayList<String>());

                gene.setHpId(new ArrayList<String>());
                gene.setHpTerm(new ArrayList<String>());

                gene.setTopLevelMpId(new ArrayList<String>());
                gene.setTopLevelMpTerm(new ArrayList<String>());
                gene.setTopLevelMpTermSynonym(new ArrayList<String>());

                gene.setIntermediateMpId(new ArrayList<String>());
                gene.setIntermediateMpTerm(new ArrayList<String>());
                gene.setIntermediateMpTermSynonym(new ArrayList<String>());

                gene.setChildMpId(new ArrayList<String>());
                gene.setChildMpTerm(new ArrayList<String>());
                gene.setChildMpTermSynonym(new ArrayList<String>());

                gene.setChildMpId(new ArrayList<String>());
                gene.setChildMpTerm(new ArrayList<String>());
                gene.setChildMpTermSynonym(new ArrayList<String>());

                gene.setInferredMaId(new ArrayList<String>());
                gene.setInferredMaTerm(new ArrayList<String>());
                gene.setInferredMaTermSynonym(new ArrayList<String>());

                gene.setSelectedTopLevelMaId(new ArrayList<String>());
                gene.setSelectedTopLevelMaTerm(new ArrayList<String>());
                gene.setSelectedTopLevelMaTermSynonym(new ArrayList<String>());

                gene.setInferredChildMaId(new ArrayList<String>());
                gene.setInferredChildMaTerm(new ArrayList<String>());
                gene.setInferredChildMaTermSynonym(new ArrayList<String>());

                gene.setInferredSelectedTopLevelMaId(new ArrayList<String>());
                gene.setInferredSelectedTopLevelMaTerm(new ArrayList<String>());
                gene.setInferredSelectedTopLevelMaTermSynonym(new ArrayList<String>());

                // Add all ontology information from images associated to this gene
                if (sangerImages.containsKey(allele.getMgiAccessionId())) {

                    List<SangerImageDTO> list = sangerImages.get(allele.getMgiAccessionId());
                    for (SangerImageDTO image : list) {

                        if (image.getMp_id() != null &&  ! gene.getMpId().contains(image.getMp_id())) {

                            gene.getMpId().addAll(image.getMp_id());
                            gene.getMpTerm().addAll(image.getMpTerm());
                            if (image.getMpSyns() != null) {
                                gene.getMpTermSynonym().addAll(image.getMpSyns());
                            }

                            if (image.getAnnotatedHigherLevelMpTermId() != null) {
                                gene.getTopLevelMpId().addAll(image.getAnnotatedHigherLevelMpTermId());
                            }
                            if (image.getAnnotatedHigherLevelMpTermName() != null) {
                                gene.getTopLevelMpTerm().addAll(image.getAnnotatedHigherLevelMpTermName());
                            }
                            if (image.getTopLevelMpTermSynonym() != null) {
                                gene.getTopLevelMpTermSynonym().addAll(image.getTopLevelMpTermSynonym());
                            }

                            if (image.getIntermediateMpId() != null) {
                                gene.getIntermediateMpId().addAll(image.getIntermediateMpId());
                            }
                            if (image.getIntermediateMpTerm() != null) {
                                gene.getIntermediateMpTerm().addAll(image.getIntermediateMpTerm());
                            }
                            if (image.getIntermediateMpTermSyn() != null) {
                                gene.getIntermediateMpTermSynonym().addAll(image.getIntermediateMpTermSyn());
                            }

                        }

                        if (image.getMaTermId() != null) {

                            gene.getMaId().addAll(image.getMaTermId());
                            gene.getMaTerm().addAll(image.getMaTermName());
                            if (image.getMaTermSynonym() != null) {
                                gene.getMaTermSynonym().addAll(image.getMaTermSynonym());
                            }

                            if (image.getSelectedTopLevelMaTermId() != null) {
                                gene.setSelectedTopLevelMaId(image.getSelectedTopLevelMaTermId());
                            }
                            if (image.getSelectedTopLevelMaTerm() != null) {
                                gene.setSelectedTopLevelMaTerm(image.getSelectedTopLevelMaTerm());
                            }
                            if (image.getSelectedTopLevelMaTermSynonym() != null) {
                                gene.setSelectedTopLevelMaTermSynonym(image.getSelectedTopLevelMaTermSynonym());
                            }

                        }
                    }
                }

                // Add all ontology information directly associated from MP to this gene
                if (StringUtils.isNotEmpty(allele.getMgiAccessionId())) {

                    if (mgiAccessionToMP.containsKey(allele.getMgiAccessionId())) {

                        List<MpDTO> mps = mgiAccessionToMP.get(allele.getMgiAccessionId());
                        for (MpDTO mp : mps) {

                            gene.getMpId().add(mp.getMpId());
                            gene.getMpTerm().add(mp.getMpTerm());
                            if (mp.getMpTermSynonym() != null) {
                                gene.getMpTermSynonym().addAll(mp.getMpTermSynonym());
                            }

                            if (mp.getOntologySubset() != null) {
                                gene.getOntologySubset().addAll(mp.getOntologySubset());
                            }

                            if (mp.getHpId() != null) {
                                gene.getHpId().addAll(mp.getHpId());
                                gene.getHpTerm().addAll(mp.getHpTerm());
                            }

                            if (mp.getTopLevelMpId() != null) {
                                gene.getTopLevelMpId().addAll(mp.getTopLevelMpId());
                                gene.getTopLevelMpTerm().addAll(mp.getTopLevelMpTerm());
                            }
                            if (mp.getTopLevelMpTermSynonym() != null) {
                                gene.getTopLevelMpTermSynonym().addAll(mp.getTopLevelMpTermSynonym());
                            }

                            if (mp.getIntermediateMpId() != null) {
                                gene.getIntermediateMpId().addAll(mp.getIntermediateMpId());
                                gene.getIntermediateMpTerm().addAll(mp.getIntermediateMpTerm());
                            }
                            if (mp.getIntermediateMpTermSynonym() != null) {
                                gene.getIntermediateMpTermSynonym().addAll(mp.getIntermediateMpTermSynonym());
                            }

                            if (mp.getChildMpId() != null) {
                                gene.getChildMpId().addAll(mp.getChildMpId());
                                gene.getChildMpTerm().addAll(mp.getChildMpTerm());
                            }
                            if (mp.getChildMpTermSynonym() != null) {
                                gene.getChildMpTermSynonym().addAll(mp.getChildMpTermSynonym());
                            }

                            if (mp.getInferredMaId() != null) {
                                gene.getInferredMaId().addAll(mp.getInferredMaId());
                                gene.getInferredMaTerm().addAll(mp.getInferredMaTerm());
                            }
                            if (mp.getInferredMaTermSynonym() != null) {
                                gene.getInferredMaTermSynonym().addAll(mp.getInferredMaTermSynonym());
                            }

                            if (mp.getInferredSelectedTopLevelMaId() != null) {
                                gene.getInferredSelectedTopLevelMaId().addAll(mp.getInferredSelectedTopLevelMaId());
                                gene.getInferredSelectedTopLevelMaTerm().addAll(mp.getInferredSelectedTopLevelMaTerm());
                            }
                            if (mp.getInferredSelectedTopLevelMaTermSynonym() != null) {
                                gene.getInferredSelectedTopLevelMaTermSynonym().addAll(mp.getInferredSelectedTopLevelMaTermSynonym());
                            }

                            if (mp.getInferredChildMaId() != null) {
                                gene.getInferredChildMaId().addAll(mp.getInferredChildMaId());
                                gene.getInferredChildMaTerm().addAll(mp.getInferredChildMaTerm());
                            }
                            if (mp.getInferredChildMaTermSynonym() != null) {
                                gene.getInferredChildMaTermSynonym().addAll(mp.getInferredChildMaTermSynonym());
                            }
                        }
                    }
                }


                /**
                 * Unique all the sets
                 */

                gene.setMpId(new ArrayList<>(new HashSet<>(gene.getMpId())));
                gene.setMpTerm(new ArrayList<>(new HashSet<>(gene.getMpTerm())));
                gene.setMpTermSynonym(new ArrayList<>(new HashSet<>(gene.getMpTermSynonym())));
                gene.setMpTermDefinition(new ArrayList<>(new HashSet<>(gene.getMpTermDefinition())));
                gene.setOntologySubset(new ArrayList<>(new HashSet<>(gene.getOntologySubset())));

                gene.setMaId(new ArrayList<>(new HashSet<>(gene.getMaId())));
                gene.setMaTerm(new ArrayList<>(new HashSet<>(gene.getMaTerm())));
                gene.setMaTermSynonym(new ArrayList<>(new HashSet<>(gene.getMaTermSynonym())));
                gene.setMaTermDefinition(new ArrayList<>(new HashSet<>(gene.getMaTermDefinition())));

                gene.setHpId(new ArrayList<>(new HashSet<>(gene.getHpId())));
                gene.setHpTerm(new ArrayList<>(new HashSet<>(gene.getHpTerm())));

                gene.setTopLevelMpId(new ArrayList<>(new HashSet<>(gene.getTopLevelMpId())));
                gene.setTopLevelMpTerm(new ArrayList<>(new HashSet<>(gene.getTopLevelMpTerm())));
                gene.setTopLevelMpTermSynonym(new ArrayList<>(new HashSet<>(gene.getTopLevelMpTermSynonym())));

                gene.setIntermediateMpId(new ArrayList<>(new HashSet<>(gene.getIntermediateMpId())));
                gene.setIntermediateMpTerm(new ArrayList<>(new HashSet<>(gene.getIntermediateMpTerm())));
                gene.setIntermediateMpTermSynonym(new ArrayList<>(new HashSet<>(gene.getIntermediateMpTermSynonym())));

                gene.setChildMpId(new ArrayList<>(new HashSet<>(gene.getChildMpId())));
                gene.setChildMpTerm(new ArrayList<>(new HashSet<>(gene.getChildMpTerm())));
                gene.setChildMpTermSynonym(new ArrayList<>(new HashSet<>(gene.getChildMpTermSynonym())));

                gene.setChildMpId(new ArrayList<>(new HashSet<>(gene.getChildMpId())));
                gene.setChildMpTerm(new ArrayList<>(new HashSet<>(gene.getChildMpTerm())));
                gene.setChildMpTermSynonym(new ArrayList<>(new HashSet<>(gene.getChildMpTermSynonym())));

                gene.setInferredMaId(new ArrayList<>(new HashSet<>(gene.getInferredMaId())));
                gene.setInferredMaTerm(new ArrayList<>(new HashSet<>(gene.getInferredMaTerm())));
                gene.setInferredMaTermSynonym(new ArrayList<>(new HashSet<>(gene.getInferredMaTermSynonym())));

                gene.setSelectedTopLevelMaId(new ArrayList<>(new HashSet<>(gene.getSelectedTopLevelMaId())));
                gene.setSelectedTopLevelMaTerm(new ArrayList<>(new HashSet<>(gene.getSelectedTopLevelMaTerm())));
                gene.setSelectedTopLevelMaTermSynonym(new ArrayList<>(new HashSet<>(gene.getSelectedTopLevelMaTermSynonym())));

                gene.setInferredChildMaId(new ArrayList<>(new HashSet<>(gene.getInferredChildMaId())));
                gene.setInferredChildMaTerm(new ArrayList<>(new HashSet<>(gene.getInferredChildMaTerm())));
                gene.setInferredChildMaTermSynonym(new ArrayList<>(new HashSet<>(gene.getInferredChildMaTermSynonym())));

                gene.setInferredSelectedTopLevelMaId(new ArrayList<>(new HashSet<>(gene.getInferredSelectedTopLevelMaId())));
                gene.setInferredSelectedTopLevelMaTerm(new ArrayList<>(new HashSet<>(gene.getInferredSelectedTopLevelMaTerm())));
                gene.setInferredSelectedTopLevelMaTermSynonym(new ArrayList<>(new HashSet<>(gene.getInferredSelectedTopLevelMaTermSynonym())));
          
                documentCount++;
                geneCore.addBean(gene, 60000);
                count ++;

                if (count % 10000 == 0) {
                    logger.info(" added " + count + " beans");
                }
            }

            logger.info("Committing to gene core for last time");
            geneCore.commit();

        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
            throw new IndexerException(e);
        }

        long endTime = System.currentTimeMillis();
        logger.info("time was " + (endTime - startTime) / 1000);

        logger.info("Gene Indexer complete!");
    }

	// PROTECTED METHODS
    @Override
    protected Logger getLogger() {

        return logger;
    }

	// PRIVATE METHODS
    private void initialiseSupportingBeans() throws IndexerException {

        phenotypeSummaryGeneAccessionsToPipelineInfo = populatePhenotypeCallSummaryGeneAccessions();
        sangerImages = IndexerMap.getSangerImagesByMgiAccession(imagesCore);
        mgiAccessionToMP = populateMgiAccessionToMp();
        logger.info("mgiAccessionToMP size=" + mgiAccessionToMP.size());
        embryoRestData=populateEmbryoData();
        genomicFeatureCoordinates=this.populateGeneGenomicCoords();
        genomicFeatureXrefs=this.populateXrefs();
    }

    private Map<String, List<EmbryoStrain>> populateEmbryoData() {
    	System.out.println("populating embryo data");
		EmbryoRestData restData=null;
		try {
			restData = embryoGetter.getEmbryoRestData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<EmbryoStrain> strains = restData.getStrains();
		Map<String,List<EmbryoStrain>> mgiToEmbryoMap=new HashMap<>();
		for(EmbryoStrain strain: strains){
			String mgi=strain.getMgi();
			if(!mgiToEmbryoMap.containsKey(mgi)){
				mgiToEmbryoMap.put(mgi,new ArrayList<>());
			}
				mgiToEmbryoMap.get(mgi).add(strain);
		}
		return mgiToEmbryoMap;
	}

	private Map<String, List<MpDTO>> populateMgiAccessionToMp() throws IndexerException {

        Map<String, List<MpDTO>> map= null;

        try {
            map = SolrUtils.populateMgiAccessionToMp(mpCore);
        } catch (SolrServerException e) {
            throw new IndexerException("Unable to query phenodigm_core in SolrUtils.populateMgiAccessionToMp()", e);
        }

        return map;
    }

    private Map<String, List<Map<String, String>>> populatePhenotypeCallSummaryGeneAccessions() {
    	Map<String, List<Map<String, String>>> localPhenotypeSummaryGeneAccessionsToPipelineInfo = new HashMap<>();
        logger.info("populating PCS pipeline info");
        String queryString = "select pcs.*, param.name, param.stable_id, proc.stable_id, proc.name, pipe.stable_id, pipe.name"
                + " from phenotype_call_summary pcs"
                + " inner join ontology_term term on term.acc=mp_acc"
                + " inner join genomic_feature gf on gf.acc=pcs.gf_acc"
                + " inner join phenotype_parameter param on param.id=pcs.parameter_id"
                + " inner join phenotype_procedure proc on proc.id=pcs.procedure_id"
                + " inner join phenotype_pipeline pipe on pipe.id=pcs.pipeline_id";

        try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                String gf_acc = resultSet.getString("gf_acc");

                Map<String, String> rowMap = new HashMap<>();
                rowMap.put(ObservationDTO.PARAMETER_NAME, resultSet.getString("param.name"));
                rowMap.put(ObservationDTO.PARAMETER_STABLE_ID, resultSet.getString("param.stable_id"));
                rowMap.put(ObservationDTO.PROCEDURE_STABLE_ID, resultSet.getString("proc.stable_id"));
                rowMap.put(ObservationDTO.PROCEDURE_NAME, resultSet.getString("proc.name"));
                rowMap.put(ObservationDTO.PIPELINE_STABLE_ID, resultSet.getString("pipe.stable_id"));
                rowMap.put(ObservationDTO.PIPELINE_NAME, resultSet.getString("pipe.name"));
                rowMap.put("proc_param_name", resultSet.getString("proc.name") + "___" + resultSet.getString("param.name"));
                rowMap.put("proc_param_stable_id", resultSet.getString("proc.stable_id") + "___" + resultSet.getString("param.stable_id"));
                List<Map<String, String>> rows = null;

                if (localPhenotypeSummaryGeneAccessionsToPipelineInfo.containsKey(gf_acc)) {
                    rows = localPhenotypeSummaryGeneAccessionsToPipelineInfo.get(gf_acc);
                } else {
                    rows = new ArrayList<>();
                }
                rows.add(rowMap);

                localPhenotypeSummaryGeneAccessionsToPipelineInfo.put(gf_acc, rows);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localPhenotypeSummaryGeneAccessionsToPipelineInfo;

    }
    
    
    private Map<String, List<Xref>> populateXrefs() {
    	   	
        Map<String, List<Xref>> localGenomicFeatureXrefs = new HashMap<>();
        logger.info("populating xref info");
        String queryString = "select acc, xref_acc, xref_db_id from xref";

        try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
            ResultSet resultSet = p.executeQuery();
            List<Xref> xrefs = null;
            while (resultSet.next()) {
                String gf_acc = resultSet.getString("acc");
                
                
                List<String> ensemblIds = new ArrayList<String>();
        		List<String> vegaIds = new ArrayList<String>();
        		List<String> ncbiIds = new ArrayList<String>();
        		List<String> ccdsIds = new ArrayList<String>();


                
                if(!localGenomicFeatureXrefs.containsKey(gf_acc)){
                	xrefs = new ArrayList<>();
                }
                String xrefAcc=resultSet.getString("xref_acc");
                int xrefDbId=resultSet.getInt("xref_db_id");
                Xref xref=new Xref();
                xref.setXrefAccession(xrefAcc);
                xref.setXrefDatabaseId(xrefDbId);
                
                xrefs.add(xref);
                localGenomicFeatureXrefs.put(gf_acc, xrefs);
            }
           

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localGenomicFeatureXrefs;

    }
    private Map<String, Map<String, String>> populateGeneGenomicCoords() {
    	Map<String, Map<String, String>> localGenomicFeatureCoordinates = new HashMap<>();
        logger.info("populating Gene Genomic location info");
        String queryString = "select  gf.acc, gf.seq_region_id, gf.seq_region_start, gf.seq_region_end, gf.subtype_db_id, gf.db_id from genomic_feature gf";

        try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                String gf_acc = resultSet.getString("gf.acc");

                Map<String, String> rowMap = new HashMap<>();
                rowMap.put(GeneDTO.MGI_ACCESSION_ID, resultSet.getString("gf.acc"));
                rowMap.put(GeneDTO.SEQ_REGION_ID, resultSet.getString("gf.seq_region_id"));
                rowMap.put(GeneDTO.SEQ_REGION_START, resultSet.getString("gf.seq_region_start"));
                rowMap.put(GeneDTO.SEQ_REGION_END, resultSet.getString("gf.seq_region_end"));
                

                if (localGenomicFeatureCoordinates.containsKey(gf_acc)) {
                	System.err.println("Error: Genomic Feature exists in map already!!!!!");       
                } 
                localGenomicFeatureCoordinates.put(gf_acc, rowMap);
   
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localGenomicFeatureCoordinates;

    }


    public static void main(String[] args) throws IndexerException {

        GeneIndexer indexer = new GeneIndexer();
        indexer.initialise(args);
        indexer.run();
        indexer.validateBuild();

        logger.info("Process finished.  Exiting.");
    }
    
//    private class GfBean{
//    	String acc;
//    	int start;
//    	int end;
//    	String seqRegionId;
//    	
//    }
}
