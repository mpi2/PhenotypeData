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
import org.mousephenotype.cda.utilities.MpCsvWriter;
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
 * @author mrelac
 */
@EnableAutoConfiguration
public class MPIndexer extends AbstractIndexer implements CommandLineRunner {

    private final Logger       logger                   = LoggerFactory.getLogger(MPIndexer.class);

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

    private OntologyParser        mpHpParser;
    private OntologyParser        mpParser;
    private OntologyParser        mpMaParser;
    private OntologyParser        maParser;
    private OntologyParserFactory ontologyParserFactory;

    private SolrClient               alleleCore;
    private SolrClient               genotypePhenotypeCore;
    private SolrClient               mpCore;
    private GenotypePhenotypeService genotypePhenotypeService;

    public static final boolean USE_LEGACY_MP_HP_OWL = true;
    private Map<String, Set<String>> owlHpTermIdMap = new HashMap<>();
    private Map<String, Set<String>> owlHpTermNameMap = new HashMap<>();
    private Map<String, Set<String>> owlHpSynonymNameMap = new HashMap<>();
    private Map<String, Set<String>> owlMpNarrowSynonymMap = new HashMap<>();
    private Map<String, Set<String>> csvHpTermNameMap = new HashMap<>();

    // These csv writers write the files in CSVs below
    private class CsvWriters {
        final String MP_HP_TERMS_FOUND_BY_OWL          = "mpHpTermsFoundByOwl.csv";
        final String MP_HP_TERMS_FOUND_BY_UPHENO       = "mpHpTermsFoundByUpheno.csv";

        public MpCsvWriter byOwl;
        public MpCsvWriter byUpheno;

        public void createAll() throws IOException {
            byOwl = new MpCsvWriter(MP_HP_TERMS_FOUND_BY_OWL, false);
            byUpheno = new MpCsvWriter(MP_HP_TERMS_FOUND_BY_UPHENO, false);
            logger.info("upheno csv created at {}", byUpheno.getFqFilename());
            writeHeadings();
        }

        private void writeHeadings() {
            byUpheno.write("MP Term", "HP Terms");
        }

        public void closeAll() throws IOException {
            if (byOwl != null) byOwl.close();
            if (byUpheno != null) byUpheno.close();
        }
    }
    private CsvWriters csv;


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

            runStatus = initialise(connection);

            // Delete the documents in the core if there are any.
            mpCore.deleteByQuery("*:*");
            mpCore.commit();

            csv.createAll();
            count = saveMpTermsInSlim(count, runStatus);
            addHpTermDifferencesToCsv();    // Compute set difference and add to csv files
            csv.closeAll();

            // Send a final commit
            mpCore.commit();

        } catch (SolrServerException | IOException | OWLOntologyCreationException | OWLOntologyStorageException | SQLException | URISyntaxException | JSONException e) {
            e.printStackTrace();
            // Try to close any csv files
            try { csv.closeAll(); } catch (IOException io) { }
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));

        return runStatus;
    }

    private void addHpTermDifferencesToCsv() {
        csv.byOwl.write("");
        csv.byOwl.write("");
        csv.byOwl.write("MP TERMS MISSING FROM UPHENO:");
        csv.byOwl.write("MP ID", "Terms ...");

        owlHpTermIdMap.keySet()
                .stream()
                .forEach(mpId -> {
                    Set<String> owlHpTermNames = owlHpTermNameMap.get(mpId);
                    owlHpTermNames.addAll(owlHpSynonymNameMap.get(mpId));
                    owlHpTermNames.addAll(owlMpNarrowSynonymMap.get(mpId));

                    Set<String> csvHpTermNames = csvHpTermNameMap.get(mpId);
                    owlHpTermNames.removeAll(csvHpTermNames);
                    if ( ! owlHpTermNames.isEmpty()) {
                        List<String> data = new ArrayList<>();
                        data.add(mpId);  data.addAll(csvHpTermNames);
                        csv.byUpheno.write(data);
                    }
                });
    }

    private RunStatus initialise(Connection connection) throws IndexerException, OWLOntologyCreationException, OWLOntologyStorageException, IOException, SQLException, SolrServerException, URISyntaxException, JSONException {

        RunStatus runStatus;
        initialiseSupportingBeans(connection);
        initialiseOntologyParsers();
        runStatus = populateMpCallMaps();
        csv = new CsvWriters();

        runStatus.getErrorMessages().stream().forEach( s -> logger.error(s));
        runStatus.getWarningMessages().stream().forEach( s -> logger.warn(s));

        return runStatus;
    }

    private void initialiseOntologyParsers() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, SQLException {

        ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);
        mpParser = ontologyParserFactory.getMpParser();
        logger.debug("Loaded mp parser");
        mpHpParser = ontologyParserFactory.getMpHpParser();
        logger.debug("Loaded mp hp parser");
        mpMaParser = ontologyParserFactory.getMpMaParser();
        logger.debug("Loaded mp ma parser");
        maParser = ontologyParserFactory.getMaParser();
        logger.debug("Loaded ma parser");
    }

    private int saveMpTermsInSlim(int count, RunStatus runStatus)
            throws IOException, SolrServerException, JSONException
    {
        for (String mpIdFromSlim: mpParser.getTermIdsInSlim()) {

            OntologyTermDTO mpDtoFromSlim = mpParser.getOntologyTerm(mpIdFromSlim);

            MpDTO mpDtoToAdd = new MpDTO();
            mpDtoToAdd.setDataType("mp");
            mpDtoToAdd.setMpId(mpIdFromSlim);
            mpDtoToAdd.setMpTerm(mpDtoFromSlim.getName());
            mpDtoToAdd.setMpDefinition(mpDtoFromSlim.getDefinition());

            if ( mpDtoFromSlim.getAlternateIds() != null &&                 // Add alternate ids
                 ! mpDtoFromSlim.getAlternateIds().isEmpty() )
            {
                mpDtoToAdd.setAltMpIds(mpDtoFromSlim.getAlternateIds());
            }

            mpDtoToAdd.setMpNodeId(mpDtoFromSlim.getNodeIds() != null       // Add mp node ids
                                           ? mpDtoFromSlim.getNodeIds()
                                           : new HashSet<>());

            addTopLevelTerms(mpDtoToAdd, mpDtoFromSlim);                    // Add top-level terms
            addIntermediateTerms(mpDtoToAdd, mpDtoFromSlim);                // Add intermediate terms

            mpDtoToAdd.setChildMpId(mpDtoFromSlim.getChildIds());           // Add child mp ids
            mpDtoToAdd.setChildMpTerm(mpDtoFromSlim.getChildNames());       // Add child mp names
            mpDtoToAdd.setParentMpId(mpDtoFromSlim.getParentIds());         // Add parent mp ids
            mpDtoToAdd.setParentMpTerm(mpDtoFromSlim.getParentNames());     // Add parent mp names
            mpDtoToAdd.setMpTermSynonym(mpDtoFromSlim.getSynonyms());       // Add synonym names

            addHpTerms(runStatus, mpIdFromSlim, mpDtoToAdd);                // Add HP terms
            addMaTerms(mpDtoToAdd);                                         // Add ma terms

            // this sets the number of postqc/preqc phenotyping calls of this MP
            addPhenotype1(mpDtoToAdd);
            mpDtoToAdd.setPhenoCalls(sumPhenotypingCalls(mpIdFromSlim));
            addPhenotype2(mpDtoToAdd);

            mpDtoToAdd.setSearchTermJson(mpDtoFromSlim.getSeachJson());
            mpDtoToAdd.setScrollNode(mpDtoFromSlim.getScrollToNode());
            mpDtoToAdd.setChildrenJson(mpDtoFromSlim.getChildrenJson());

            logger.debug(" Added {} records for termId {}", count, mpIdFromSlim);
            count ++;

            expectedDocumentCount++;
            mpCore.addBean(mpDtoToAdd, 60000);

            mpParser.fillJsonTreePath("MP:0000001",
                                      "/data/phenotypes/",
                                      mpGeneVariantCount,
                                      OntologyParserFactory.TOP_LEVEL_MP_TERMS,
                                      false);
        }

        return count;
    }

    private void addHpTerms(RunStatus runStatus, String mpIdFromSlim, MpDTO mpDtoToAdd) {

        // Get the data the Monarch legacy OWL way
        final List<String> owlHpTermIds        = new ArrayList<>();
        final List<String> owlHpTermNames      = new ArrayList<>();
        final List<String> owlHpSynonymNames   = new ArrayList<>();
        final List<String> owlMpNarrowSynonyms = new ArrayList<>();
        getHpTermsFromOWL(runStatus, mpIdFromSlim, owlHpTermIds, owlHpTermNames,
                          owlHpSynonymNames, owlMpNarrowSynonyms);

        // Get the data the Monarch new CSV file way
        final List<String> csvHpTermNames = new ArrayList<>();
        getHpTermsFromCSV(mpIdFromSlim, csvHpTermNames);

        if (USE_LEGACY_MP_HP_OWL) {
            mpDtoToAdd.setHpId(owlHpTermIds);                   // hp term ids
            mpDtoToAdd.setHpTerm(owlHpTermNames);               // hp term names
            mpDtoToAdd.setHpTermSynonym(owlHpSynonymNames);     // hp synonym names
            mpDtoToAdd.setMpNarrowSynonym(owlMpNarrowSynonyms); // mp-hp narrow synonym names
        } else {
            mpDtoToAdd.setHpTerm(csvHpTermNames);
        }

        // Write csv files with results for comparison.
        List<String> row = new ArrayList<>();
        row.add(mpIdFromSlim);
        row.addAll(csvHpTermNames);
        csv.byUpheno.write(row);

        // owl csv file layout
        // mp_term   OWL HP Term Ids             mpDtoToAdd.getHpId()
        //           OWL HP Term Names           mpDtoToAdd.getHpTerm()
        //           OWL HP Synonym Names        mpDtoToAdd.getHpTermSynonym()
        //           OWL MP-HP narrow synonyms   mpDtoToAdd.getMpNarrowSynonym()
        row = new ArrayList<>();
        row.add(mpIdFromSlim);
        row.add("OWL HP Term Ids");
        row.addAll(owlHpTermIds);
        csv.byOwl.write(row);

        row = new ArrayList<>();
        row.add("");
        row.add("OWL HP Term Names");
        row.addAll(owlHpTermNames);
        csv.byOwl.write(row);

        row = new ArrayList<>();
        row.add("");
        row.add("OWL HP Synonym Names");
        row.addAll(owlHpSynonymNames);
        row.add("MP_NARROW_SYNONYM_NAMES");
        csv.byOwl.write(row);

        row = new ArrayList<>();
        row.add("");
        row.add("OWL MP-HP Narrow Synonyms");
        row.addAll(owlMpNarrowSynonyms);
        csv.byOwl.write(row);

        // Add data to sets for later analysis
        owlHpTermIdMap.put(mpIdFromSlim, new HashSet<>(owlHpTermIds));
        owlHpTermNameMap.put(mpIdFromSlim, new HashSet(owlHpTermNames));
        owlHpSynonymNameMap.put(mpIdFromSlim, new HashSet(owlHpSynonymNames));
        owlMpNarrowSynonymMap.put(mpIdFromSlim, new HashSet(owlMpNarrowSynonyms));
        csvHpTermNameMap.put(mpIdFromSlim, new HashSet(csvHpTermNames));
    }

    private void getHpTermsFromOWL(
            RunStatus runStatus,
            String mpIdFromSlim,
            List<String> hpTermIds,
            List<String> hpTermNames,
            List<String> hpSynonymNames,
            List<String> mpNarrowSynonyms
    ) {

        OntologyTermDTO mpHpTerm = mpHpParser.getOntologyTerm(mpIdFromSlim);

        if (mpHpTerm == null) {
            String message = "Term " + mpIdFromSlim + " missing from mpHpParser";
            runStatus.addWarning(message);
            logger.warn(message);
        } else {
            Set<OntologyTermDTO> hpTerms = mpHpTerm.getEquivalentClasses();
            for (OntologyTermDTO hpTerm : hpTerms) {
                Set<String> hpIds = new HashSet<>();
                hpIds.add(hpTerm.getAccessionId());
                hpTermIds.addAll(hpIds);
                if (hpTerm.getName() != null) {
                    Set<String> hpNames = new HashSet<>();
                    hpNames.add(hpTerm.getName());
                    hpTermNames.addAll(hpNames);
                }
                if (hpTerm.getSynonyms() != null) {
                    hpSynonymNames.addAll(hpTerm.getSynonyms());
                }
            }

            mpNarrowSynonyms.addAll(getMpNarrowSynonymsFromOWL(mpHpTerm));
        }
    }

    private List<String> getMpNarrowSynonymsFromOWL(OntologyTermDTO mpHpTerm) {
        List<String> narrowSynonymNames = new ArrayList<>();

        // the narrow synonyms are subclasses from 2 levels down
        Set<String> narrowSynonymSet = new TreeSet<>();
        // MP root term MP:0000001 does not have narrow synonyms
        if (mpHpTerm.getNarrowSynonymClasses() != null) {

            mpHpTerm.getNarrowSynonymClasses()
                    .stream()
                    .forEach(narrowSynonym -> narrowSynonymSet
                            .add(narrowSynonym.getName()));

            // 20190202 Per TFM. In an effort to restrict the extraneous terms found in the phenotype search
            // while still keeping relevant narrow terms (like deafness), TFM has empirically determined
            // that, to include deafness, we need to include no more than 80 narrow terms
            if (narrowSynonymSet.size() > 0 && narrowSynonymSet.size() < 80) {
                narrowSynonymNames.addAll(narrowSynonymSet);
            }
        }

        return narrowSynonymNames;
    }


    private void getHpTermsFromCSV(String mpIdFromSlim, List<String> csvHpTermNames) {

        Set <String> hpTermNames = mpHpTermsMap.get(mpIdFromSlim);

        if (hpTermNames != null) {
            csvHpTermNames.addAll(hpTermNames);
        }
    }

    public Map<String, Integer> getPhenotypeGeneVariantCounts(String termId, RunStatus status)
            throws IOException, URISyntaxException, SolrServerException, JSONException {

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

    private RunStatus populateMpCallMaps()
            throws IOException, SolrServerException, URISyntaxException, JSONException {
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

    private void addIntermediateTerms(MpDTO mpDtoToAdd, OntologyTermDTO mpDtoFromSlim) {

        if (mpDtoFromSlim.getIntermediateIds() != null) {
            mpDtoToAdd.addIntermediateMpId(mpDtoFromSlim.getIntermediateIds());
            mpDtoToAdd.addIntermediateMpTerm(mpDtoFromSlim.getIntermediateNames());
            mpDtoToAdd.addIntermediateMpTermSynonym(mpDtoFromSlim.getIntermediateSynonyms());
        }
    }

    // Decorate MpDTO with info for top levels
    private void addTopLevelTerms(MpDTO mpDtoToAdd, OntologyTermDTO mpDtoFromSlim) {

        if (mpDtoFromSlim.getTopLevelIds() != null && mpDtoFromSlim.getTopLevelIds().size() > 0){
            mpDtoToAdd.addTopLevelMpId(mpDtoFromSlim.getTopLevelIds());
            mpDtoToAdd.addTopLevelMpTerm(mpDtoFromSlim.getTopLevelNames());
            mpDtoToAdd.addTopLevelMpTermSynonym(mpDtoFromSlim.getTopLevelSynonyms());
            mpDtoToAdd.addTopLevelMpTermId(mpDtoFromSlim.getTopLevelTermIdsConcatenated());
            mpDtoToAdd.addTopLevelMpTermInclusive(mpDtoFromSlim.getTopLevelNames());
            mpDtoToAdd.addTopLevelMpIdInclusive(mpDtoFromSlim.getTopLevelIds());

        }
        else {
            // add self as top level
            mpDtoToAdd.addTopLevelMpTermInclusive(mpDtoFromSlim.getName());
            mpDtoToAdd.addTopLevelMpIdInclusive(mpDtoFromSlim.getAccessionId());
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

    protected void addMaTerms(MpDTO mpDtoToAdd) {

        // get MA ids referenced from MP
        Set<String> maTerms = mpMaParser.getReferencedClasses(mpDtoToAdd.getMpId(), OntologyParserFactory.VIA_PROPERTIES, "MA");
        for (String maId : maTerms) {
            // get info about these MA terms. In the mp-ma file the MA classes have no details but the id. For example the labels or synonyms are not there.
            OntologyTermDTO ma = maParser.getOntologyTerm(maId);
            if (ma != null) {
                mpDtoToAdd.addInferredMaId(ma.getAccessionId());
                mpDtoToAdd.addInferredMaTerm(ma.getName());
                if (ma.getTopLevelIds() != null) {
                    mpDtoToAdd.addInferredSelectedTopLevelMaId(ma.getTopLevelIds());
                    mpDtoToAdd.addInferredSelectedTopLevelMaTerm(ma.getTopLevelNames());
                }
                if (ma.getIntermediateIds() != null){
                    mpDtoToAdd.addInferredIntermediatedMaId(ma.getIntermediateIds());
                    mpDtoToAdd.addInferredIntermediateMaTerm(ma.getIntermediateNames());
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
