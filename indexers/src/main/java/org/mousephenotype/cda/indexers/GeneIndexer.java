/*
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
 */
package org.mousephenotype.cda.indexers;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.Procedure;
import org.mousephenotype.cda.db.pojo.Xref;
import org.mousephenotype.cda.db.repositories.DatasourceRepository;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ProcedureRepository;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.DmddDataUnit;
import org.mousephenotype.cda.indexers.utils.EmbryoStrain;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Populate the gene core
 */
@EnableAutoConfiguration
public class GeneIndexer extends AbstractIndexer implements CommandLineRunner {

    @Value("${dmddDataFilename}")
    private String dmddDataFilename;

    @Value("${embryoViewerFilename}")
    private String embryoViewerFilename;

	private final Logger logger = LoggerFactory.getLogger(GeneIndexer.class);


    private SolrClient           alleleCore;
    private DatasourceRepository datasourceRepository;
    private SolrClient           geneCore;
    private SolrClient           mpCore;
    private ProcedureRepository  procedureRepository;


    private Map<String, List<DmddDataUnit>>        dmddImageData;
    private Map<String, List<DmddDataUnit>>        dmddLethalData;
    private Map<String, List<EmbryoStrain>>        embryoRestData                               = null;
    private Map<String, Map<String, String>>       genomicFeatureCoordinates                    = new HashMap<>();
    private Map<String, List<Xref>>                genomicFeatureXrefs                          = new HashMap<>();
    private Set<String>                            idgGenes                                     = new HashSet<>();
    private IndexerMap                             indexerMap                                   = new IndexerMap();
    private Map<String, List<MpDTO>>               mgiAccessionToMP                             = new HashMap<>();
    private Map<String, List<Map<String, String>>> phenotypeSummaryGeneAccessionsToPipelineInfo = new HashMap<>();
    private List<String> originalUmassGeneSymbols = Arrays.asList("4933427D14Rik","Actr8","Alg14","Ap2s1","Atp2b1","B4gat1","Bc052040","Bcs1l","Borcs6","Casc3","Ccdc59","Cenpo","Clpx","Dbr1","Dctn6","Ddx59","Dnaaf2","Dolk","Elof1","Exoc2","Fastkd6","Glrx3","Hlcs","Ipo11","Isca1","mars2","Mcrs1","med20","Mepce","Mrm3","Mrpl22","Mrpl3","Mrpl44","Mrps18c","Mrps22","Mrps25","mtpap","Nars2","Ndufa9","Ndufs8","Orc6","Pmpcb","Pold2","Polr1a","Polr1d","Ppp1r35","Prim1","Prpf4b","Rab11a","Ranbp2","Rbbp4","Riok1","Rpain","Sars","Sdhaf2","Ska2","Snapc2","Sptssa","Strn3","Timm22","tmx2","Tpk1","Trit1","Tubgcp4","Ube2m","Washc4","Ylpm1","Zc3h4","Zfp407","Zwint");



    protected GeneIndexer() {

    }

    @Inject
    public GeneIndexer(
            @NotNull DataSource komp2DataSource,
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull SolrClient alleleCore,
            @NotNull DatasourceRepository datasourceRepository,
            @NotNull SolrClient geneCore,
            @NotNull SolrClient mpCore,
            @NotNull ProcedureRepository procedureRepository)
    {
        super(komp2DataSource, ontologyTermRepository);
        this.alleleCore = alleleCore;
        this.datasourceRepository = datasourceRepository;
        this.geneCore = geneCore;
        this.mpCore = mpCore;
        this.procedureRepository = procedureRepository;
    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(geneCore);
    }

    @Override
    public RunStatus run() throws IndexerException {
        int count = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

        Datasource ensembl = datasourceRepository.getByShortName("Ensembl");
        Datasource vega    = datasourceRepository.getByShortName("VEGA");
        Datasource ncbi    = datasourceRepository.getByShortName("EntrezGene");
        Datasource ccds    = datasourceRepository.getByShortName("cCDS");

        try (Connection connection = komp2DataSource.getConnection()) {
            initialiseSupportingBeans(connection);

            List<AlleleDTO> alleles = IndexerMap.getAlleles(alleleCore);
            geneCore.deleteByQuery("*:*");

            int proceduresFoundCount = 0;
            int proceduresMissingCount = 0;

            for (AlleleDTO allele : alleles) {
                //System.out.println("gene="+allele.getMarkerSymbol());
                GeneDTO gene = new GeneDTO();
                gene.setMgiAccessionId(allele.getMgiAccessionId());
                gene.setDataType(allele.getDataType());
                gene.setMarkerType(allele.getMarkerType());
                gene.setMarkerName(allele.getMarkerName());

                // mouse symbol/synonym
                gene.setMarkerSymbol(allele.getMarkerSymbol());
                gene.setMarkerSymbolLowercase(allele.getMarkerSymbol());
                gene.setMarkerSynonym(allele.getMarkerSynonym());
                gene.setMarkerSynonymLowercase(allele.getMarkerSynonym());

                // human symbol/synonym
                gene.setHumanGeneSymbol(allele.getHumanGeneSymbol());
                gene.setHumanSymbolSynonym(new ArrayList<>());

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
                gene.getGoTermDefs().addAll(allele.getGoTermDefs());
                gene.setGoTermEvids(allele.getGoTermEvids());
                gene.setGoTermDomains(allele.getGoTermDomains());
                gene.setEvidCodeRank(allele.getEvidCodeRank());
                gene.setGoCount(allele.getGoCount());
                gene.setGoUniprot(allele.getGoUniprot());

                // pfam stuff
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

                if (embryoRestData != null) {

                	List<EmbryoStrain> embryoStrainsForGene = embryoRestData.get(gene.getMgiAccessionId());
                	//for the moment lets just set an embryo data available flag!
                	if ((embryoStrainsForGene != null) && (embryoStrainsForGene.size() > 0)) {
                		gene.setEmbryoDataAvailable(true);
                		List<String> embryoModalitiesForGene=new ArrayList<>();

                		for( EmbryoStrain strain : embryoStrainsForGene){

                			if(strain.getModalities()!=null && strain.getModalities().size()>0){
                				embryoModalitiesForGene.addAll(strain.getModalities());
                			}
                			if(strain.getAnalysisViewUrl()!=null){
                				gene.setEmbryoAnalysisUrl(strain.getAnalysisViewUrl());
                				gene.setEmbryoAnalysisName("volumetric analysis");
                			}
                			for ( Long procedureStableKey : strain.getProcedureStableKeys() ) {
                                Procedure procedure = procedureRepository.getByStableKey(procedureStableKey);

                                if (procedure == null) {
                                    logger.warn("Procedure lookup for center::colonyId::mgiAccessionId::procedureStableKey {} {}::{}::{} failed. Procedure skipped.",
                                                strain.getCentre(), strain.getColonyId(), strain.getMgiGeneAccessionId(), procedureStableKey);
                                    proceduresMissingCount++;

                                    continue;
                                }

                                String procedureStableId = procedure.getStableId();

                				if ( gene.getProcedureStableId() == null ) {

                                    List<String> procedureStableIds = new ArrayList<>();
                                    List<String> procedureNames     = new ArrayList<>();

                					procedureStableIds.add(procedureStableId);
                					gene.setProcedureStableId(procedureStableIds);
                					procedureNames.add(procedure.getName());
                					gene.setProcedureName(procedureNames);
                				}
                				else {
                					gene.getProcedureStableId().add(procedure.getStableId());
	                				gene.getProcedureName().add(procedure.getName());
                				}

                				proceduresFoundCount++;
                			}
                		}

                		gene.setEmbryoModalities(embryoModalitiesForGene);
                	}
                }
                
                if(dmddImageData.containsKey(gene.getMgiAccessionId())){
                	//add dmdd image data here
                	gene.setDmddImageDataAvailable(true);
                }
                if(dmddLethalData.containsKey(gene.getMgiAccessionId())){
                	//add dmdd image data here
                	gene.setDmddLethalDataAvailable(true);
                }
                
                if(idgGenes.contains(gene.getMgiAccessionId())){
                	gene.setIsIdgGene(true);
                }
                if(originalUmassGeneSymbols.contains(gene.getMarkerSymbol())){
                    gene.setIsUmassGene(true);
                }

                if(genomicFeatureCoordinates!=null && genomicFeatureXrefs!=null){
                	if(genomicFeatureCoordinates.containsKey(allele.getMgiAccessionId())){
                		Map<String, String> coordsMap = genomicFeatureCoordinates.get(allele.getMgiAccessionId());
                		gene.setSeqRegionId(coordsMap.get(GeneDTO.SEQ_REGION_ID));
                		gene.setSeqRegionStart(Integer.valueOf(coordsMap.get(GeneDTO.SEQ_REGION_START)));
                		gene.setSeqRegionEnd(Integer.valueOf(coordsMap.get(GeneDTO.SEQ_REGION_END)));

                        gene.setChrName(allele.getChrName());

                        List<String> ensemblIds = new ArrayList<>();
        				List<String> vegaIds = new ArrayList<>();
        				List<String> ncbiIds = new ArrayList<>();
        				List<String> ccdsIds = new ArrayList<>();
        				List<String> xrefAccessions=new ArrayList<>();
                		if(genomicFeatureXrefs.containsKey(allele.getMgiAccessionId())){
                			List<Xref> xrefs = genomicFeatureXrefs.get(allele.getMgiAccessionId());
                			for(Xref xref:xrefs){
                				String xrefAccession=xref.getXrefAccession();
                				xrefAccessions.add(xrefAccession);
                				//System.out.println("setting xrefs:"+xrefAccession);

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
                			gene.setEnsemblGeneIds(ensemblIds);
                		}
                	}
                }

                //
                // Override the genomic location with values from the allele core (if available)
                //
                if (allele.getChrName()!=null && !allele.getChrName().equals("null")) {
                    gene.setSeqRegionId(allele.getChrName());
                }

                if (allele.getChrStart()!=null) {
                    gene.setSeqRegionStart(allele.getChrStart());
                }

                if (allele.getChrEnd() != null) {
                    gene.setSeqRegionEnd(allele.getChrEnd());
                }

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

                    // pipeline
                    if ( gene.getPipelineStableId() == null ){
                    	gene.setPipelineStableId(pipelineStableIds);
                	}
                    else {
                    	gene.getPipelineStableId().addAll(pipelineStableIds);
                    }
                    if ( gene.getPipelineName() == null ){
                    	gene.setPipelineName(pipelineNames);
                	}
                    else {
                    	gene.getPipelineName().addAll(pipelineNames);
                    }

                    // procedure
                    if ( gene.getProcedureName() == null ){
                    	gene.setProcedureName(procedureNames);
                    }
                    else {
                    	gene.getProcedureName().addAll(procedureNames);
                    }
                    if ( gene.getProcedureStableId() == null ){
                    	gene.setProcedureStableId(procedureStableIds);
                    }
                    else {
                    	gene.getProcedureStableId().addAll(procedureStableIds);
                    }

                    // parameter
                    if ( gene.getParameterName() == null ){
                    	gene.setParameterName(parameterNames);
                    }
                    else {
                    	gene.getParameterName().addAll(parameterNames);
                    }
                    if ( gene.getParameterStableId() == null ){
                    	gene.setParameterStableId(parameterStableIds);
                    }
                    else {
                    	gene.getParameterStableId().addAll(parameterStableIds);
                    }

                }

				//do images core data
                // Initialize all the ontology term lists
                gene.setMpId(new ArrayList<>());
                gene.setMpTerm(new ArrayList<>());
                gene.setMpTermSynonym(new ArrayList<>());
                gene.setMpTermDefinition(new ArrayList<>());

                gene.setMaId(new ArrayList<>());
                gene.setMaTerm(new ArrayList<>());
                gene.setMaTermSynonym(new ArrayList<>());
                gene.setMaTermDefinition(new ArrayList<>());

                gene.setHpId(new ArrayList<>());
                gene.setHpTerm(new ArrayList<>());

                gene.setTopLevelMpId(new ArrayList<>());
                gene.setTopLevelMpTerm(new ArrayList<>());
                gene.setTopLevelMpTermSynonym(new ArrayList<>());

                gene.setIntermediateMpId(new ArrayList<>());
                gene.setIntermediateMpTerm(new ArrayList<>());
                gene.setIntermediateMpTermSynonym(new ArrayList<>());

                gene.setChildMpId(new ArrayList<>());
                gene.setChildMpTerm(new ArrayList<>());
                gene.setChildMpTermSynonym(new ArrayList<>());

                gene.setChildMpId(new ArrayList<>());
                gene.setChildMpTerm(new ArrayList<>());
                gene.setChildMpTermSynonym(new ArrayList<>());

                gene.setInferredMaId(new ArrayList<>());
                gene.setInferredMaTerm(new ArrayList<>());
                gene.setInferredMaTermSynonym(new ArrayList<>());

                gene.setSelectedTopLevelMaId(new ArrayList<>());
                gene.setSelectedTopLevelMaTerm(new ArrayList<>());
                gene.setSelectedTopLevelMaTermSynonym(new ArrayList<>());

                gene.setInferredChildMaId(new ArrayList<>());
                gene.setInferredChildMaTerm(new ArrayList<>());
                gene.setInferredChildMaTermSynonym(new ArrayList<>());

                gene.setInferredSelectedTopLevelMaId(new ArrayList<>());
                gene.setInferredSelectedTopLevelMaTerm(new ArrayList<>());
                gene.setInferredSelectedTopLevelMaTermSynonym(new ArrayList<>());

                // Add all ontology information directly associated from MP to this gene
                if (StringUtils.isNotEmpty(allele.getMgiAccessionId())) {

                    if (mgiAccessionToMP.containsKey(allele.getMgiAccessionId())) {

                        List<MpDTO> mps = mgiAccessionToMP.get(allele.getMgiAccessionId());
                        for (MpDTO mp : mps) {

                            gene.getMpId().add(mp.getMpId());
                            gene.getMpTerm().add(mp.getMpTerm());

                            if (mp.getMpDefinition() != null) {
                                gene.getMpTermDefinition().add(mp.getMpDefinition());
                            }

                            if (mp.getMpTermSynonym() != null) {
                                gene.getMpTermSynonym().addAll(mp.getMpTermSynonym());
                            }

                            if (mp.getHpId() != null) {
                                gene.getHpId().addAll(mp.getHpId());
                                gene.getHpTerm().addAll(mp.getHpTerm());
                            }

                            if (mp.getTopLevelMpId() != null) {
                                gene.getTopLevelMpId().addAll(mp.getTopLevelMpId());
                                gene.getTopLevelMpTerm().addAll(mp.getTopLevelMpTerm());
                            }


                            if (mp.getIntermediateMpId() != null) {
                                gene.getIntermediateMpId().addAll(mp.getIntermediateMpId());
                                gene.getIntermediateMpTerm().addAll(mp.getIntermediateMpTerm());
                            }

                            if (mp.getChildMpId() != null) {
                                gene.getChildMpId().addAll(mp.getChildMpId());
                                gene.getChildMpTerm().addAll(mp.getChildMpTerm());
                            }

                            if (mp.getInferredMaId() != null) {
                                gene.getInferredMaId().addAll(mp.getInferredMaId());
                                gene.getInferredMaTerm().addAll(mp.getInferredMaTerm());
                            }
                            if (mp.getInferredSelectedTopLevelMaId() != null) {
                                gene.getInferredSelectedTopLevelMaId().addAll(mp.getInferredSelectedTopLevelMaId());
                                gene.getInferredSelectedTopLevelMaTerm().addAll(mp.getInferredSelectedTopLevelMaTerm());
                            }

                        }
                    }
                }

                /*
                 * Unique all the sets
                 */

                gene.setMpId(new ArrayList<>(new HashSet<>(gene.getMpId())));
                gene.setMpTerm(new ArrayList<>(new HashSet<>(gene.getMpTerm())));
                gene.setMpTermSynonym(new ArrayList<>(new HashSet<>(gene.getMpTermSynonym())));
                gene.setMpTermDefinition(new ArrayList<>(new HashSet<>(gene.getMpTermDefinition())));

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

                expectedDocumentCount++;
                geneCore.addBean(gene, 60000);
                count ++;
            }

            logger.info("proceduresMissing: {}. procedures found: {}", proceduresMissingCount, proceduresFoundCount);

            geneCore.commit();

        } catch (SQLException | IOException | SolrServerException e) {
            e.printStackTrace();
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));

        return runStatus;
    }


	// PRIVATE METHODS


    private void initialiseSupportingBeans(Connection connection) throws IndexerException {
        phenotypeSummaryGeneAccessionsToPipelineInfo = populatePhenotypeCallSummaryGeneAccessions(connection);
        mgiAccessionToMP = populateMgiAccessionToMp();
        logger.info(" mgiAccessionToMP size=" + mgiAccessionToMP.size());
        embryoRestData = indexerMap.populateEmbryoData(embryoViewerFilename);
        genomicFeatureCoordinates=this.populateGeneGenomicCoords(connection);
        genomicFeatureXrefs=this.populateXrefs(connection);
        idgGenes=this.populateIdgGeneList(connection);
        dmddImageData=indexerMap.populateDmddImagedData(dmddDataFilename);
        dmddLethalData = indexerMap.populateDmddLethalData(dmddDataFilename);
    }

	private Set<String> populateIdgGeneList(Connection connection) {

		Set<String> idgGenes=new HashSet<>();
		String queryString = "SELECT * FROM genes_secondary_project where secondary_project_id='idg'";

      try (PreparedStatement p = connection.prepareStatement(queryString)) {
          ResultSet resultSet = p.executeQuery();

          while (resultSet.next()) {
        	  idgGenes.add(resultSet.getString("acc"));
          }
          
          }catch(Exception e){
        	  e.printStackTrace();
          }

		return idgGenes;
	}

	private Map<String, List<MpDTO>> populateMgiAccessionToMp() throws IndexerException {

        Map<String, List<MpDTO>> map;

        try {
            map = SolrUtils.populateMgiAccessionToMp(mpCore);
        } catch (SolrServerException | IOException e) {
            throw new IndexerException("Unable to query phenodigm_core in SolrUtils.populateMgiAccessionToMp()", e);
        }

        return map;
    }

    private Map<String, List<Map<String, String>>> populatePhenotypeCallSummaryGeneAccessions(Connection connection) {
    	Map<String, List<Map<String, String>>> localPhenotypeSummaryGeneAccessionsToPipelineInfo = new HashMap<>();
        String queryString = "select pcs.*, param.name, param.stable_id, proc.stable_id, proc.name, pipe.stable_id, pipe.name"
                + " from phenotype_call_summary pcs"
                + " inner join ontology_term term on term.acc=mp_acc"
                + " inner join genomic_feature gf on gf.acc=pcs.gf_acc"
                + " inner join phenotype_parameter param on param.id=pcs.parameter_id"
                + " inner join phenotype_procedure proc on proc.id=pcs.procedure_id"
                + " inner join phenotype_pipeline pipe on pipe.id=pcs.pipeline_id";

        try (PreparedStatement p = connection.prepareStatement(queryString)) {
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
                List<Map<String, String>> rows;

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

    private Map<String, List<Xref>> populateXrefs(Connection connection) {

        Map<String, List<Xref>> localGenomicFeatureXrefs = new HashMap<>();
        String queryString = "select acc, xref_acc, xref_db_id from xref";

        try (PreparedStatement p = connection.prepareStatement(queryString)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                String gf_acc = resultSet.getString("acc");

                if(!localGenomicFeatureXrefs.containsKey(gf_acc)){
                    localGenomicFeatureXrefs.put(gf_acc, new ArrayList<>());
                }

                String xrefAcc = resultSet.getString("xref_acc");
                long xrefDbId = resultSet.getLong("xref_db_id");

                Xref xref = new Xref();
                xref.setXrefAccession(xrefAcc);
                xref.setXrefDatabaseId(xrefDbId);

                localGenomicFeatureXrefs.get(gf_acc).add(xref);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return localGenomicFeatureXrefs;

    }

    private Map<String, Map<String, String>> populateGeneGenomicCoords(Connection connection) {
    	Map<String, Map<String, String>> localGenomicFeatureCoordinates = new HashMap<>();
        String queryString = "select  gf.acc, gf.seq_region_id, gf.seq_region_start, gf.seq_region_end, gf.subtype_db_id, gf.db_id from genomic_feature gf";

        try (PreparedStatement p = connection.prepareStatement(queryString)) {
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

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(GeneIndexer.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }
}
