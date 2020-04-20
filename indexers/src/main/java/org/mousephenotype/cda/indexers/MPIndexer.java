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
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.indexers.beans.MPStrainBean;
import org.mousephenotype.cda.indexers.beans.ParamProcedurePipelineBean;
import org.mousephenotype.cda.indexers.beans.PhenotypeCallSummaryBean;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.MpHpCsvReader;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.web.dto.DataTableRow;
import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.mousephenotype.cda.solr.web.dto.PhenotypePageTableRow;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Matt Pearce
 * @author ilinca
 *
 */
@EnableAutoConfiguration
public class MPIndexer extends AbstractIndexer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(MPIndexer.class);

    private Map<String, List<AlleleDTO>> allelesByMgiAlleleAccessionId;
    private Map<String, Set<String>>     mpHpTermsMap = new HashMap<>();

    // Phenotype call summaries (1)
    Map<String, List<PhenotypeCallSummaryBean>> phenotypes1;
    Map<String, List<String>>                   impcBeans;
    Map<String, List<String>>                   legacyBeans;

    // Phenotype call summaries (2)
    Map<String, List<PhenotypeCallSummaryBean>>   phenotypes2;
    Map<String, List<MPStrainBean>>               strains;
    Map<String, List<ParamProcedurePipelineBean>> pppBeans;

    Map<String, Long>    mpCalls            = new HashMap<>();
    Map<String, Integer> mpGeneVariantCount = new HashMap<>();

    private OntologyParser        mpMaParser;
    private OntologyParser        maParser;

    private SolrClient               alleleCore;
    private SolrClient               genotypePhenotypeCore;
    private SolrClient               mpCore;
    private GenotypePhenotypeService genotypePhenotypeService;

    protected MPIndexer() {

    }

    @Inject
    public MPIndexer(
            @NotNull DataSource komp2DataSource,
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull SolrClient alleleCore,
            @NotNull SolrClient genotypePhenotypeCore,
            @NotNull SolrClient mpCore,
            @NotNull GenotypePhenotypeService genotypePhenotypeService)
    {
        super(komp2DataSource, ontologyTermRepository);
        this.alleleCore = alleleCore;
        this.genotypePhenotypeCore = genotypePhenotypeCore;
        this.mpCore = mpCore;
        this.genotypePhenotypeService = genotypePhenotypeService;
    }


    @Override
    public RunStatus validateBuild()
            throws IndexerException {
        return super.validateBuild(mpCore);
    }

    @Override
    public RunStatus run ()
            throws IndexerException{

        int count = 0;
        RunStatus runStatus;
        long start = System.currentTimeMillis();

        try (Connection connection = komp2DataSource.getConnection()) {
            initialiseSupportingBeans(connection);
            OntologyParserFactory ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);
            OntologyParser mpParser = ontologyParserFactory.getMpParser();
            logger.debug("Loaded mp parser");

            mpHpTermsMap = IndexerMap.getMpToHpTerms(owlpath + "/" + MpHpCsvReader.MP_HP_CSV_FILENAME);
            if ((mpHpTermsMap == null) || mpHpTermsMap.isEmpty()) {
                throw new IndexerException("mp-hp error: Unable to open" + owlpath + "/" + MpHpCsvReader.MP_HP_CSV_FILENAME);
            }

            logger.debug("Loaded mp hp term names from {}", owlpath + "/" + MpHpCsvReader.MP_HP_CSV_FILENAME);
            mpMaParser = ontologyParserFactory.getMpMaParser();
            logger.debug("Loaded mp ma parser");
            maParser = ontologyParserFactory.getMaParser();
            logger.debug("Loaded ma parser");

            // maps MP to number of phenotyping calls
            runStatus = populateMpCallMaps();

            for (String error : runStatus.getErrorMessages()) {
                logger.error(error);
            }
            for (String warning : runStatus.getWarningMessages()) {
                logger.warn(warning);
            }

            // Delete the documents in the core if there are any.
            mpCore.deleteByQuery("*:*");
            mpCore.commit();

            for (String mpId: mpParser.getTermsInSlim()) {

                OntologyTermDTO mpDTO = mpParser.getOntologyTerm(mpId);
                String termId = mpDTO.getAccessionId();

                MpDTO mp = new MpDTO();
                mp.setDataType("mp");
                mp.setMpId(termId);
                mp.setMpTerm(mpDTO.getName());
                mp.setMpDefinition(mpDTO.getDefinition());

                // alternative MP ID
                if ( mpDTO.getAlternateIds() != null && !mpDTO.getAlternateIds().isEmpty() ) {
                    mp.setAltMpIds(mpDTO.getAlternateIds());
                }

                mp.setMpNodeId(mpDTO.getNodeIds() != null ? mpDTO.getNodeIds() : new HashSet<>());

                addTopLevelTerms(mp, mpDTO);
                addIntermediateTerms(mp, mpDTO);

                mp.setChildMpId(mpDTO.getChildIds());
                mp.setChildMpTerm(mpDTO.getChildNames());
                mp.setParentMpId(mpDTO.getParentIds());
                mp.setParentMpTerm(mpDTO.getParentNames());
                mp.setMpTermSynonym(mpDTO.getSynonyms());

                // add mp-hp mapping using Monarch's mp-hp hybrid ontology

                Set <String> hpTermNames = mpHpTermsMap.get(termId);
                mp.setHpTerm(new ArrayList<>(hpTermNames));

                getMaTermsForMp(mp);

                // this sets the number of postqc/preqc phenotyping calls of this MP
                addPhenotype1(mp);
                mp.setPhenoCalls(sumPhenotypingCalls(termId));
                addPhenotype2(mp);

                mp.setSearchTermJson(mpDTO.getSeachJson());
                mp.setScrollNode(mpDTO.getScrollToNode());
                mp.setChildrenJson(mpDTO.getChildrenJson());

                logger.debug(" Added {} records for termId {}", count, termId);
                count ++;

                expectedDocumentCount++;
                mpCore.addBean(mp, 60000);

                mpParser.fillJsonTreePath("MP:0000001", "/data/phenotypes/", mpGeneVariantCount, OntologyParserFactory.TOP_LEVEL_MP_TERMS, false); // call this if you want node ids from the objects
            }

            // Send a final commit
            mpCore.commit();

        } catch (SolrServerException | IOException | OWLOntologyCreationException | OWLOntologyStorageException | SQLException | URISyntaxException | JSONException e) {
            e.printStackTrace();
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }

    // 22-Mar-2017 (mrelac) Added status to query for errors and warnings.
    public Map<String, Integer> getPhenotypeGeneVariantCounts(String termId, RunStatus status)
            throws IOException, URISyntaxException, SolrServerException, JSONException
    {
        // Errors and warnings are returned in PhenotypeFacetResult.status.
        PhenotypeFacetResult phenoResult = genotypePhenotypeService.getMPCallByMPAccessionAndFilter(termId, null, null, null);
        status.add(phenoResult.getStatus());

        List<PhenotypeCallSummaryDTO> phenotypeList;
        phenotypeList = phenoResult.getPhenotypeCallSummaries();

        // This is a map because we need to support lookups
        Map<Integer, DataTableRow> phenotypes = new HashMap<>();

        for (PhenotypeCallSummaryDTO pcs : phenotypeList) {
            // On the phenotype pages we only display stats graphs as evidence, the MPATH links can't be linked from phen pages
            DataTableRow pr = new PhenotypePageTableRow(pcs, "", null, false);

            // Collapse rows on sex
            if (phenotypes.containsKey(pr.hashCode())) {

                pr = phenotypes.get(pr.hashCode());
                // Use a tree set to maintain an alphabetical order (Female, Male)
                TreeSet<String> sexes = new TreeSet<>(pr.getSexes());
                sexes.add(pcs.getSex().toString());

                pr.setSexes(new ArrayList<>(sexes));
            }

            if (pr.getParameter() != null && pr.getProcedure() != null) {
                phenotypes.put(pr.hashCode(), pr);
            }
        }

        List<DataTableRow> uniqGenes = new ArrayList<>(phenotypes.values());

        int sumCount = 0;
        for(DataTableRow r : uniqGenes){
            // want all counts, even if sex field has no data
            sumCount += r.getSexes().size();
        }

        Map<String, Integer> kv = new HashMap<>();
        kv.put("sumCount", sumCount);

        return kv;
    }

    private RunStatus populateMpCallMaps() throws IOException, SolrServerException, URISyntaxException, JSONException {
        RunStatus status = new RunStatus();

        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setFacet(true);
        query.setRows(0);
        query.addFacetField(GenotypePhenotypeDTO.MP_TERM_ID);
        query.addFacetField(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID);
        query.addFacetField(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
        query.setFacetLimit(-1);

        for (FacetField facetGroup : genotypePhenotypeCore.query(query).getFacetFields()) {
            for (FacetField.Count facet : facetGroup.getValues()) {
                if (!mpCalls.containsKey(facet.getName())) {
                    mpCalls.put(facet.getName(), 0L);

                    Map<String, Integer> geneVariantCount = getPhenotypeGeneVariantCounts(facet.getName(), status);
                    int                  gvCount          = geneVariantCount.get("sumCount");
                    mpGeneVariantCount.put(facet.getName(), gvCount);
                }
                mpCalls.put(facet.getName(), facet.getCount() + mpCalls.get(facet.getName()));
            }
        }

        return status;
    }


    private long sumPhenotypingCalls(String mpId) {

        return mpCalls.containsKey(mpId) ? mpCalls.get(mpId) : new Long(0);

    }

    private void initialiseSupportingBeans(Connection connection) throws IndexerException {

        try {
            // Alleles
            allelesByMgiAlleleAccessionId = IndexerMap.getGeneToAlleles(alleleCore);

            // Phenotype call summaries (1)
            phenotypes1 = getPhenotypeCallSummary1(connection);
            impcBeans = getImpcPipe(connection);
            legacyBeans = getLegacyPipe(connection);

            // Phenotype call summaries (2)
            phenotypes2 = getPhenotypeCallSummary2(connection);
            strains = getStrains(connection);
            pppBeans = getPPPBeans(connection);

        } catch (SQLException e) {
            throw new IndexerException(e);
        }
    }

    private Map<String, List<PhenotypeCallSummaryBean>> getPhenotypeCallSummary1(Connection connection)
            throws SQLException {

        Map<String, List<PhenotypeCallSummaryBean>> beans = new HashMap<>();

        String q = "select distinct gf_acc, mp_acc, concat(mp_acc,'_',gf_acc) as mp_mgi, parameter_id, procedure_id, pipeline_id, allele_acc, strain_acc from phenotype_call_summary where gf_db_id=3 and gf_acc like 'MGI:%' and allele_acc is not null and strain_acc is not null";
        PreparedStatement ps = connection.prepareStatement(q);
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
                beans.put(mpAcc, new ArrayList<>());
            }
            beans.get(mpAcc).add(bean);
            count ++;
        }
        logger.debug(" Added {} phenotype call summaries (1)", count);

        return beans;
    }

    private Map<String, List<String>> getImpcPipe(Connection connection)
            throws SQLException {

        Map<String, List<String>> beans = new HashMap<>();

        String q = "select distinct external_db_id as 'impc', concat (mp_acc,'_', gf_acc) as mp_mgi from phenotype_call_summary where external_db_id = 22";
        PreparedStatement ps = connection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            String tId = rs.getString("mp_mgi");
            String impc = rs.getString("impc");
            if ( ! beans.containsKey(tId)) {
                beans.put(tId, new ArrayList<>());
            }
            beans.get(tId).add(impc);
            count ++;
        }
        logger.debug(" Added {} IMPC records", count);

        return beans;
    }

    private Map<String, List<String>> getLegacyPipe(Connection connection) throws SQLException {

        Map<String, List<String>> beans = new HashMap<>();

        String q = "select distinct external_db_id as 'legacy', concat (mp_acc,'_', gf_acc) as mp_mgi from phenotype_call_summary where external_db_id = 12";
        PreparedStatement ps = connection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            String tId = rs.getString("mp_mgi");
            String legacy = rs.getString("legacy");
            if ( ! beans.containsKey(tId)) {
                beans.put(tId, new ArrayList<>());
            }
            beans.get(tId).add(legacy);
            count ++;
        }
        logger.debug(" Added {} legacy records", count);

        return beans;
    }

    private Map<String, List<PhenotypeCallSummaryBean>> getPhenotypeCallSummary2(Connection connection)
            throws SQLException {

        Map<String, List<PhenotypeCallSummaryBean>> beans = new HashMap<>();

        String q = "select distinct gf_acc, mp_acc, parameter_id, procedure_id, pipeline_id, concat(parameter_id,'_',procedure_id,'_',pipeline_id) as ididid, allele_acc, strain_acc from phenotype_call_summary where gf_db_id=3 and gf_acc like 'MGI:%' and allele_acc is not null and strain_acc is not null";
        PreparedStatement ps = connection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            PhenotypeCallSummaryBean bean = new PhenotypeCallSummaryBean();

            String mpAcc = rs.getString("mp_acc");

            bean.setGfAcc(rs.getString("gf_acc"));
            bean.setMpAcc(mpAcc);
            bean.setParamProcPipelineId(rs.getString("ididid"));
            bean.setParameterId(rs.getString("parameter_id"));
            bean.setProcedureId(rs.getString("procedure_id"));
            bean.setPipelineId(rs.getString("pipeline_id"));
            bean.setAlleleAcc(rs.getString("allele_acc"));
            bean.setStrainAcc(rs.getString("strain_acc"));

            if ( ! beans.containsKey(mpAcc)) {
                beans.put(mpAcc, new ArrayList<>());
            }
            beans.get(mpAcc).add(bean);
            count ++;
        }
        logger.debug(" Added {} phenotype call summaries (2)", count);

        return beans;
    }

    private Map<String, List<MPStrainBean>> getStrains(Connection connection)
            throws SQLException {

        Map<String, List<MPStrainBean>> beans = new HashMap<>();

        String q = "select distinct name, acc from strain where db_id=3";
        PreparedStatement ps = connection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            MPStrainBean bean = new MPStrainBean();

            String acc = rs.getString("acc");

            bean.setAcc(acc);
            bean.setName(rs.getString("name"));

            if ( ! beans.containsKey(acc)) {
                beans.put(acc, new ArrayList<>());
            }
            beans.get(acc).add(bean);
            count ++;
        }
        logger.debug(" Added {} strain beans", count);

        return beans;
    }

    private Map<String, List<ParamProcedurePipelineBean>> getPPPBeans(Connection connection)
            throws SQLException {

        Map<String, List<ParamProcedurePipelineBean>> beans = new HashMap<>();

        String q = "select concat(pp.id,'_',pproc.id,'_',ppipe.id) as ididid, pp.name as parameter_name, pp.stable_key as parameter_stable_key, pp.stable_id as parameter_stable_id, pproc.name as procedure_name, pproc.stable_key as procedure_stable_key, pproc.stable_id as procedure_stable_id, ppipe.name as pipeline_name, ppipe.stable_key as pipeline_key, ppipe.stable_id as pipeline_stable_id from phenotype_parameter pp inner join phenotype_procedure_parameter ppp on pp.id=ppp.parameter_id inner join phenotype_procedure pproc on ppp.procedure_id=pproc.id inner join phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id inner join phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id";
        PreparedStatement ps = connection.prepareStatement(q);
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
                beans.put(id, new ArrayList<>());
            }
            beans.get(id).add(bean);
            count ++;
        }
        logger.debug(" Added {} PPP beans", count);

        return beans;
    }

    protected static void addIntermediateTerms(MpDTO mp, OntologyTermDTO mpDTO) {

        if (mpDTO.getIntermediateIds() != null) {
            mp.addIntermediateMpId(mpDTO.getIntermediateIds());
            mp.addIntermediateMpTerm(mpDTO.getIntermediateNames());
            mp.addIntermediateMpTermSynonym(mpDTO.getIntermediateSynonyms());
        }
    }

    /**
     * Decorate MpDTO with info for top levels
     */
    protected static void addTopLevelTerms(MpDTO mp, OntologyTermDTO mpDTO) {

        if (mpDTO.getTopLevelIds() != null && mpDTO.getTopLevelIds().size() > 0){
            mp.addTopLevelMpId(mpDTO.getTopLevelIds());
            mp.addTopLevelMpTerm(mpDTO.getTopLevelNames());
            mp.addTopLevelMpTermSynonym(mpDTO.getTopLevelSynonyms());
            mp.addTopLevelMpTermId(mpDTO.getTopLevelTermIdsConcatenated());
            mp.addTopLevelMpTermInclusive(mpDTO.getTopLevelNames());
            mp.addTopLevelMpIdInclusive(mpDTO.getTopLevelIds());

        }
        else {
            // add self as top level
            mp.addTopLevelMpTermInclusive(mpDTO.getName());
            mp.addTopLevelMpIdInclusive(mpDTO.getAccessionId());
        }
    }

    private void addPhenotype1(MpDTO mp) {
        if (phenotypes1.containsKey(mp.getMpId())) {
            checkMgiDetails(mp);

            for (PhenotypeCallSummaryBean pheno1 : phenotypes1.get(mp.getMpId())) {
                mp.getMgiAccessionId().add(pheno1.getGfAcc());
                if (impcBeans.containsKey(pheno1.getMpMgi())) {
                    // From JS mapping script - row.get('impc')
                    mp.getLatestPhenotypeStatus().add("Phenotyping Complete");
                }
                if (legacyBeans.containsKey(pheno1.getMpMgi())) {
                    // From JS mapping script - row.get('legacy')
                    mp.setLegacyPhenotypeStatus(1);
                }
                addAllele(mp, allelesByMgiAlleleAccessionId.get(pheno1.getGfAcc()), false);
            }
        }
    }

    private void checkMgiDetails(MpDTO mp) {
        if (mp.getMgiAccessionId() == null) {
            mp.setMgiAccessionId(new ArrayList<>());
            mp.setLatestPhenotypeStatus(new ArrayList<>());
        }
    }

    private void addAllele(MpDTO mp, List<AlleleDTO> alleles, boolean includeStatus) {
        if (alleles != null) {
            initialiseAlleleFields(mp);

            for (AlleleDTO allele : alleles) {
                // Copy the fields from the allele to the MP
                // NO TYPE FIELD IN ALLELE DATA!!! mp.getType().add(???)
                if (allele.getDiseaseSource() != null) {
                    mp.getDiseaseSource().addAll(allele.getDiseaseSource());
                    mp.setDiseaseSource(new ArrayList<>(new HashSet<>(mp.getDiseaseSource())));
                }
                if (allele.getDiseaseId() != null) {
                    mp.getDiseaseId().addAll(allele.getDiseaseId());
                    mp.setDiseaseId(new ArrayList<>(new HashSet<>(mp.getDiseaseId())));
                }
                if (allele.getDiseaseTerm() != null) {
                    mp.getDiseaseTerm().addAll(allele.getDiseaseTerm());
                    mp.setDiseaseTerm(new ArrayList<>(new HashSet<>(mp.getDiseaseTerm())));
                }
                if (allele.getDiseaseAlts() != null) {
                    mp.getDiseaseAlts().addAll(allele.getDiseaseAlts());
                    mp.setDiseaseAlts(new ArrayList<>(new HashSet<>(mp.getDiseaseAlts())));
                }
                if (allele.getDiseaseClasses() != null) {
                    mp.getDiseaseClasses().addAll(allele.getDiseaseClasses());
                    mp.setDiseaseClasses(new ArrayList<>(new HashSet<>(mp.getDiseaseClasses())));
                }
                if (allele.getHumanCurated() != null) {
                    mp.getHumanCurated().addAll(allele.getHumanCurated());
                    mp.setHumanCurated(new ArrayList<>(new HashSet<>(mp.getHumanCurated())));
                }
                if (allele.getMouseCurated() != null) {
                    mp.getMouseCurated().addAll(allele.getMouseCurated());
                    mp.setMouseCurated(new ArrayList<>(new HashSet<>(mp.getMouseCurated())));
                }
                if (allele.getMgiPredicted() != null) {
                    mp.getMgiPredicted().addAll(allele.getMgiPredicted());
                    mp.setMgiPredicted(new ArrayList<>(new HashSet<>(mp.getMgiPredicted())));
                }
                if (allele.getImpcPredicted() != null) {
                    mp.getImpcPredicted().addAll(allele.getImpcPredicted());
                    mp.setImpcPredicted(new ArrayList<>(new HashSet<>(mp.getImpcPredicted())));
                }
                if (allele.getMgiPredictedKnownGene() != null) {
                    mp.getMgiPredictedKnownGene().addAll(allele.getMgiPredictedKnownGene());
                    mp.setMgiPredictedKnownGene(new ArrayList<>(new HashSet<>(mp.getMgiPredictedKnownGene())));
                }
                if (allele.getImpcPredictedKnownGene() != null) {
                    mp.getImpcPredictedKnownGene().addAll(allele.getImpcPredictedKnownGene());
                    mp.setImpcPredictedKnownGene(new ArrayList<>(new HashSet<>(mp.getImpcPredictedKnownGene())));
                }
                if (allele.getMgiNovelPredictedInLocus() != null) {
                    mp.getMgiNovelPredictedInLocus().addAll(allele.getMgiNovelPredictedInLocus());
                    mp.setMgiNovelPredictedInLocus(new ArrayList<>(new HashSet<>(mp.getMgiNovelPredictedInLocus())));
                }
                if (allele.getImpcNovelPredictedInLocus() != null) {
                    mp.getImpcNovelPredictedInLocus().addAll(allele.getImpcNovelPredictedInLocus());
                    mp.setImpcNovelPredictedInLocus(new ArrayList<>(new HashSet<>(mp.getImpcNovelPredictedInLocus())));
                }
                if (allele.getMarkerSymbol() != null) {
                    mp.getMarkerSymbol().add(allele.getMarkerSymbol());
                }
                if (allele.getMarkerName() != null) {
                    mp.getMarkerName().add(allele.getMarkerName());
                }
                if (allele.getMarkerSynonym() != null) {
                    mp.getMarkerSynonym().addAll(allele.getMarkerSynonym());
                }
                if (allele.getMarkerType() != null) {
                    mp.getMarkerType().add(allele.getMarkerType());
                }
                if (allele.getHumanGeneSymbol() != null) {
                    mp.getHumanGeneSymbol().addAll(allele.getHumanGeneSymbol());
                }
                // NO STATUS FIELD IN ALLELE DATA!!! mp.getStatus().add(allele.getStatus());
                if (allele.getImitsPhenotypeStarted() != null) {
                    mp.getImitsPhenotypeStarted().add(allele.getImitsPhenotypeStarted());
                }
                if (allele.getImitsPhenotypeComplete() != null) {
                    mp.getImitsPhenotypeComplete().add(allele.getImitsPhenotypeComplete());
                }
                if (allele.getImitsPhenotypeStatus() != null) {
                    mp.getImitsPhenotypeStatus().add(allele.getImitsPhenotypeStatus());
                }
                if (allele.getLatestProductionCentre() != null) {
                    mp.getLatestProductionCentre().addAll(allele.getLatestProductionCentre());
                }
                if (allele.getLatestPhenotypingCentre() != null) {
                    mp.getLatestPhenotypingCentre().addAll(allele.getLatestPhenotypingCentre());
                }
                if (allele.getAlleleName() != null) {
                    mp.getAlleleName().addAll(allele.getAlleleName());
                }

                if (includeStatus && allele.getMgiAccessionId() != null) {
                    mp.getLatestPhenotypeStatus().add("Phenotyping Started");
                }
            }
        }
    }

    private void initialiseAlleleFields(MpDTO mp) {
        if (mp.getType() == null) {
            mp.setType(new ArrayList<>());
            mp.setDiseaseSource(new ArrayList<>());
            mp.setDiseaseId(new ArrayList<>());
            mp.setDiseaseTerm(new ArrayList<>());
            mp.setDiseaseAlts(new ArrayList<>());
            mp.setDiseaseClasses(new ArrayList<>());
            mp.setHumanCurated(new ArrayList<>());
            mp.setMouseCurated(new ArrayList<>());
            mp.setMgiPredicted(new ArrayList<>());
            mp.setImpcPredicted(new ArrayList<>());
            mp.setMgiPredictedKnownGene(new ArrayList<>());
            mp.setImpcPredictedKnownGene(new ArrayList<>());
            mp.setMgiNovelPredictedInLocus(new ArrayList<>());
            mp.setImpcNovelPredictedInLocus(new ArrayList<>());
            // MGI accession ID should already be set
            mp.setMarkerSymbol(new ArrayList<>());
            mp.setMarkerName(new ArrayList<>());
            mp.setMarkerSynonym(new ArrayList<>());
            mp.setMarkerType(new ArrayList<>());
            mp.setHumanGeneSymbol(new ArrayList<>());
            mp.setStatus(new ArrayList<>());
            mp.setImitsPhenotypeStarted(new ArrayList<>());
            mp.setImitsPhenotypeComplete(new ArrayList<>());
            mp.setImitsPhenotypeStatus(new ArrayList<>());
            mp.setLatestProductionCentre(new ArrayList<>());
            mp.setLatestPhenotypingCentre(new ArrayList<>());
            mp.setAlleleName(new ArrayList<>());
        }
    }

    private void addPhenotype2(MpDTO mp) {
        if (phenotypes2.containsKey(mp.getMpId())) {
            checkMgiDetails(mp);

            for (PhenotypeCallSummaryBean pheno2 : phenotypes2.get(mp.getMpId())) {
                addStrains(mp, pheno2.getStrainAcc());
                addParamProcPipeline(mp, pheno2.getParamProcPipelineId());
            }
        }
    }

    private void addStrains(MpDTO mp, String strainAcc) {
        if (strains.containsKey(strainAcc)) {
            if (mp.getStrainId() == null) {
                // Initialise the strain lists
                mp.setStrainId(new ArrayList<>());
                mp.setStrainName(new ArrayList<>());
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
                mp.setParameterName(new ArrayList<>());
                mp.setParameterStableId(new ArrayList<>());
                mp.setParameterStableKey(new ArrayList<>());
                mp.setProcedureName(new ArrayList<>());
                mp.setProcedureStableId(new ArrayList<>());
                mp.setProcedureStableKey(new ArrayList<>());
                mp.setPipelineName(new ArrayList<>());
                mp.setPipelineStableId(new ArrayList<>());
                mp.setPipelineStableKey(new ArrayList<>());
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


    protected void getMaTermsForMp(MpDTO mp) {

        // get MA ids referenced from MP
        Set<String> maTerms = mpMaParser.getReferencedClasses(mp.getMpId(), OntologyParserFactory.VIA_PROPERTIES, "MA");
        for (String maId : maTerms) {
            // get info about these MA terms. In the mp-ma file the MA classes have no details but the id. For example the labels or synonyms are not there.
            OntologyTermDTO ma = maParser.getOntologyTerm(maId);
            if (ma != null) {
                mp.addInferredMaId(ma.getAccessionId());
                mp.addInferredMaTerm(ma.getName());
                if (ma.getTopLevelIds() != null) {
                    mp.addInferredSelectedTopLevelMaId(ma.getTopLevelIds());
                    mp.addInferredSelectedTopLevelMaTerm(ma.getTopLevelNames());
                }
                if (ma.getIntermediateIds() != null){
                    mp.addInferredIntermediatedMaId(ma.getIntermediateIds());
                    mp.addInferredIntermediateMaTerm(ma.getIntermediateNames());
                }
            } else {
                logger.info("Term not found in MA : " + maId);
            }
        }

    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(MPIndexer.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }
}
