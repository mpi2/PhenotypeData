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
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.db.dao.MpOntologyDAO;
import org.mousephenotype.cda.indexers.beans.*;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.OntologyBrowserGetter;
import org.mousephenotype.cda.indexers.utils.OntologyBrowserGetter.TreeHelper;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.PreQcService;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.web.dto.DataTableRow;
import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.mousephenotype.cda.solr.web.dto.PhenotypePageTableRow;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.IRI;
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
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

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
    private static final int LEVELS_FOR_NARROW_SYNONYMS = 2;

	@Autowired
	@Qualifier("phenodigmCore")
	private SolrClient phenodigmCore;

    @Autowired
    @Qualifier("alleleCore")
    private SolrClient alleleCore;

    @Autowired
    @Qualifier("preqcCore")
    private SolrClient preqcCore;

    @Autowired
    @Qualifier("genotypePhenotypeCore")
    private SolrClient genotypePhenotypeCore;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;

    /**
     * Destination Solr core
     */
    @Autowired
    @Qualifier("mpCore")
    private SolrClient mpCore;

    @Autowired
    MpOntologyDAO mpOntologyService;

    @Autowired
    @Qualifier("postqcService")
    PostQcService postqcService;

    @Autowired
    @Qualifier("preqcService")
    PreQcService preqcService;

    private static Connection komp2DbConnection;
    private static Connection ontoDbConnection;

    // Maps of supporting database content
    Map<String, List<Integer>> termNodeIds;

    Map<String, List<String>> goIds;

    // Alleles
    Map<String, List<AlleleDTO>> alleles;

    // Phenotype call summaries (1)
    Map<String, List<PhenotypeCallSummaryBean>> phenotypes1;
    Map<String, List<String>> impcBeans;
    Map<String, List<String>> legacyBeans;

    // Phenotype call summaries (2)
    Map<String, List<PhenotypeCallSummaryBean>> phenotypes2;
    Map<String, List<MPStrainBean>> strains;
    Map<String, List<ParamProcedurePipelineBean>> pppBeans;

    Map<Integer, String> lookupTableByNodeId = new HashMap<>(); // <nodeId, mpOntologyId>

    Map<String, Long> mpCalls = new HashMap<>();
    Map<String, Integer> mpGeneVariantCount = new HashMap<>();

    protected static final Set<String> TOP_LEVEL_MP_TERMS = new HashSet<>(Arrays.asList("MP:0010768", "MP:0002873", "MP:0001186", "MP:0003631",
            "MP:0003012", "MP:0005367",  "MP:0005369", "MP:0005370", "MP:0005371", "MP:0005377", "MP:0005378", "MP:0005375", "MP:0005376",
            "MP:0005379", "MP:0005380",  "MP:0005381", "MP:0005384", "MP:0005385", "MP:0005382", "MP:0005388", "MP:0005389", "MP:0005386",
            "MP:0005387", "MP:0005391",  "MP:0005390", "MP:0005394", "MP:0005397"));

    // Properties we want to follow to get MA terms form MP
    Set<OWLObjectPropertyImpl> viaProperties = new HashSet<>(Arrays.asList(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000052")),
            new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000070")),
            new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/mp/mp-logical-definitions#inheres_in_part_of"))));


    private OntologyParser mpHpParser;
    private OntologyParser mpParser;
    private OntologyParser mpMaParser;
    private OntologyParser maParser;

    public MPIndexer() {

    }


    @Override
    public RunStatus validateBuild()
    throws IndexerException {
        return super.validateBuild(mpCore);
    }


    @Override
    public RunStatus run ()
            throws IndexerException, SQLException, IOException, SolrServerException, URISyntaxException {

        int count = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();
        OntologyBrowserGetter ontologyBrowser = new OntologyBrowserGetter(ontodbDataSource);
        initializeDatabaseConnections();
        System.out.println("Started supporting beans");
        initialiseSupportingBeans();
        Set<String> wantedIds = getWantedSlimIds();

        try {
            mpParser = new OntologyParser(owlpath + "/mp.owl", "MP", TOP_LEVEL_MP_TERMS, wantedIds);
            System.out.println("Loaded mp parser");
            mpHpParser = new OntologyParser(owlpath + "/mp-hp.owl", "MP", null, null);
            System.out.println("Loaded mp hp parser");
            mpMaParser = new OntologyParser(owlpath + "/mp-ext-merged.owl", "MP", null, null);
            System.out.println("Loaded mp ma parser");
            maParser = new OntologyParser(owlpath + "/ma.owl", "MA", AnatomyIndexer.TOP_LEVEL_MA_TERMS, null);
            System.out.println("Loaded ma parser");

        	// maps MP to number of phenotyping calls
        	populateMpCallMaps();

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

                mp.setMpNodeId(termNodeIds.get(termId));
                mp.setMpNodeId(termNodeIds.get(termId));

                addTopLevelTerms(mp, mpDTO);
                addIntermediateTerms(mp, mpDTO);

                mp.setChildMpId(mpDTO.getChildIds());
                mp.setChildMpTerm(mpDTO.getChildNames());
                mp.setParentMpId(mpDTO.getParentIds());
                mp.setParentMpTerm(mpDTO.getParentNames());

                // add mp-hp mapping using Monarch's mp-hp hybrid ontology
                OntologyTermDTO mpTerm = mpHpParser.getOntologyTerm(termId);
		        if (mpTerm==null) {
			        logger.error("MP term not found using mpHpParser.getOntologyTerm(termId); where termId={}", termId);
		        } else {
                    Set <OntologyTermDTO> hpTerms = mpTerm.getEquivalentClasses();
                    for ( OntologyTermDTO hpTerm : hpTerms ){
                        Set<String> hpIds = new HashSet<>();
                        hpIds.add(hpTerm.getAccessionId());
                        mp.setHpId(new ArrayList(hpIds));
                        if ( hpTerm.getName() != null ){
                            Set<String> hpNames = new HashSet<>();
                            hpNames.add(hpTerm.getName());
                            mp.setHpTerm(new ArrayList(hpNames));
                        }
                        if ( hpTerm.getSynonyms() != null ){
                            mp.setHpTermSynonym(new ArrayList(hpTerm.getSynonyms()));
                        }
                    }
                    // get the children of MP not in our slim (narrow synonyms)
                    if (isOKForNarrowSynonyms(mp)){
                        mp.setMpNarrowSynonym(new ArrayList(mpHpParser.getNarrowSynonyms(mpTerm, LEVELS_FOR_NARROW_SYNONYMS)));
                    } else  {
                        mp.setMpNarrowSynonym(new ArrayList(getRestrictedNarrowSynonyms(mpTerm, LEVELS_FOR_NARROW_SYNONYMS)));
                    }
                }

                mp.setMpTermSynonym(mpDTO.getSynonyms());
                mp.setGoId(goIds.get(termId));
                getMaTermsForMp(mp);

                // this sets the number of postqc/preqc phenotyping calls of this MP
                addPhenotype1(mp, runStatus);
                mp.setPhenoCalls(sumPhenotypingCalls(termId));
                addPhenotype2(mp);

                // Ontology browser stuff
                TreeHelper helper = ontologyBrowser.getTreeHelper( "mp", termId);

                // for MP the root node id is 0 (MA is 1)
                List<JSONObject> searchTree = ontologyBrowser.createTreeJson(helper, "0", null, termId, mpGeneVariantCount);
                mp.setSearchTermJson(searchTree.toString());
                String scrollNodeId = ontologyBrowser.getScrollTo(searchTree);
                mp.setScrollNode(scrollNodeId);
                //TODO add child json back, from OntologyParser
//                List<JSONObject> childrenTree = ontologyBrowser.createTreeJson(helper, "" + mp.getMpNodeId().get(0), null, termId, mpGeneVariantCount);
//                mp.setChildrenJson(childrenTree.toString());

                logger.debug(" Added {} records for termId {}", count, termId);
                count ++;

                documentCount++;
                mpCore.addBean(mp, 60000);

                if (documentCount % 100 == 0){
                    System.out.println("Added " + documentCount);
                }
            }

            // Send a final commit
            mpCore.commit();

        } catch (SQLException | SolrServerException | IOException | OWLOntologyCreationException | OWLOntologyStorageException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }


    /**
     *
     * @return all MP ids that we want in the slim
     * @throws SQLException
     */
    private Set<String> getWantedSlimIds() throws SQLException {

        // Select MP terms from images too
        Set<String> wantedIds = new HashSet<>();

        // Get mp terms from Sanger images
        PreparedStatement statement = komp2DbConnection.prepareStatement("SELECT DISTINCT (UPPER(TERM_ID)) AS TERM_ID, (UPPER(TERM_NAME)) as TERM_NAME FROM  IMA_IMAGE_TAG iit INNER JOIN ANN_ANNOTATION aa ON aa.FOREIGN_KEY_ID=iit.ID");
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            String r = res.getString("TERM_ID");
            if (r.startsWith("MP:")) {
                wantedIds.add(r);
            }
        }

        //All MP terms we can have annotations to (from IMPRESS)
        wantedIds.addAll(getOntologyIds(5, komp2DataSource));

        return wantedIds;
    }


    public Map<String, Integer> getPhenotypeGeneVariantCounts(String termId)
    throws IOException, URISyntaxException, SolrServerException {

        PhenotypeFacetResult phenoResult = postqcService.getMPCallByMPAccessionAndFilter(termId,  null, null, null);
        PhenotypeFacetResult preQcResult = preqcService.getMPCallByMPAccessionAndFilter(termId,  null, null, null);

        List<PhenotypeCallSummaryDTO> phenotypeList;
        phenotypeList = phenoResult.getPhenotypeCallSummaries();
        phenotypeList.addAll(preQcResult.getPhenotypeCallSummaries());

        // This is a map because we need to support lookups
        Map<Integer, DataTableRow> phenotypes = new HashMap<Integer, DataTableRow>();

        for (PhenotypeCallSummaryDTO pcs : phenotypeList) {
            // On the phenotype pages we only display stats graphs as evidence, the MPATH links can't be linked from phen pages
            DataTableRow pr = new PhenotypePageTableRow(pcs, "", null, false);

            // Collapse rows on sex
            if (phenotypes.containsKey(pr.hashCode())) {

                pr = phenotypes.get(pr.hashCode());
                // Use a tree set to maintain an alphabetical order (Female, Male)
                TreeSet<String> sexes = new TreeSet<String>();
                for (String s : pr.getSexes()) {
                    sexes.add(s);
                }
                sexes.add(pcs.getSex().toString());

                pr.setSexes(new ArrayList<String>(sexes));
            }

            if (pr.getParameter() != null && pr.getProcedure() != null) {
                phenotypes.put(pr.hashCode(), pr);
            }
        }

        List<DataTableRow> uniqGenes = new ArrayList<DataTableRow>(phenotypes.values());

        int sumCount = 0;
        for(DataTableRow r : uniqGenes){
            // want all counts, even if sex field has no data
            sumCount += r.getSexes().size();
        }

        Map<String, Integer> kv = new HashMap<>();
        kv.put("sumCount", sumCount);

        return kv;
    }


    private Set<String> getRestrictedNarrowSynonyms(OntologyTermDTO mpFromFullOntology,  int levels) throws IOException, SolrServerException {

        TreeSet<String> synonyms = new TreeSet<String>();
        long calls = sumPhenotypingCalls(mpFromFullOntology.getAccessionId());

        // get narrow synonyms from all children not in slim.
        if (calls > 0 && mpFromFullOntology.getChildIds() != null && mpFromFullOntology.getChildIds().size() > 0){

            for (String childId : mpFromFullOntology.getChildIds()){
                if (!termNodeIds.containsKey(childId)) {// not in slim
                    OntologyTermDTO child = mpHpParser.getOntologyTerm(childId);
                    if (child != null) {
                        synonyms.addAll(mpHpParser.getNarrowSynonyms(child, levels));
                        synonyms.add(child.getName());
                        synonyms.addAll(child.getSynonyms());
                    }
                } else if (termNodeIds.containsKey(childId) && sumPhenotypingCalls(childId) == 0) { //in slim but no calls
                    OntologyTermDTO child = mpHpParser.getOntologyTerm(childId);
                    if (child != null) {
                        synonyms.addAll(getNarrowSynonymsOutsideSlim(child, levels, synonyms));
                    }
                }
            }
        }

        return  synonyms;

    }


    private TreeSet<String> getNarrowSynonymsOutsideSlim(OntologyTermDTO mpFromFullOntology,  int levels, TreeSet<String> synonyms){

        if (mpFromFullOntology != null &&  levels > 0) {
            if (!termNodeIds.containsKey(mpFromFullOntology.getAccessionId())) { // not in slim
                synonyms.addAll(mpHpParser.getNarrowSynonyms(mpFromFullOntology, levels - 1));
                synonyms.add(mpFromFullOntology.getName());
                synonyms.addAll(mpFromFullOntology.getSynonyms());
            } else if (mpFromFullOntology.getChildIds() != null){
                for (String childId : mpFromFullOntology.getChildIds()) {
                    if (!termNodeIds.containsKey(childId) && mpHpParser.getOntologyTerm(childId) != null) { // child not in slim either
                        getNarrowSynonymsOutsideSlim(mpHpParser.getOntologyTerm(childId), levels - 1, synonyms);
                    }
                }
            }
        }

        return synonyms;

    }

    private boolean isOKForNarrowSynonyms(MpDTO mp) throws IOException, SolrServerException {

        long calls = sumPhenotypingCalls(mp.getMpId());
        if (calls > 0 && mp.getChildMpId().size() == 0 ){ // leaf node and we have calls
            return true;
        }

        boolean hasCallForChildren = false;
        for (String childId: mp.getChildMpId()){
            long sum = sumPhenotypingCalls(childId);
            if (sum > 0) {
                hasCallForChildren = true;
                break;
            }
        }

        if (calls > 0 && !hasCallForChildren){
            return true;
        }

        return false;

    }

    private void populateMpCallMaps() throws IOException, SolrServerException, URISyntaxException {

        List<SolrClient> ss = new ArrayList<>();
        ss.add(preqcCore);
        ss.add(genotypePhenotypeCore);

        for (int i = 0; i < ss.size(); i++){

            SolrClient solrSvr = ss.get(i);
            SolrQuery query = new SolrQuery();
            query.setQuery("*:*");
            query.setFacet(true);
            query.setRows(0);
            query.addFacetField(GenotypePhenotypeDTO.MP_TERM_ID);
            query.addFacetField(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID);
            query.addFacetField(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
            query.setFacetLimit(-1);

            for (FacetField facetGroup: solrSvr.query(query).getFacetFields()){
                for (FacetField.Count facet: facetGroup.getValues()){
                    if (!mpCalls.containsKey(facet.getName())){
                        mpCalls.put(facet.getName(), new Long(0));

                        Map<String, Integer> geneVariantCount = getPhenotypeGeneVariantCounts(facet.getName());
                        int gvCount = geneVariantCount.get("sumCount");
                        mpGeneVariantCount.put(facet.getName(), gvCount);
                    }
                    mpCalls.put(facet.getName(), facet.getCount() + mpCalls.get(facet.getName()));

                }
            }

        }
    }


    private long sumPhenotypingCalls(String mpId)
    throws SolrServerException, IOException {

    	return mpCalls.containsKey(mpId) ? mpCalls.get(mpId) : new Long(0);

    }


    /**
     * Initialize the database connections required
     *
     * @throws IndexerException when there's an issue
     */
    private void initializeDatabaseConnections() throws IndexerException {

        try {
            komp2DbConnection = komp2DataSource.getConnection();
            ontoDbConnection = ontodbDataSource.getConnection();
        } catch (SQLException e) {
            throw new IndexerException(e);
        }

    }


    private void initialiseSupportingBeans()
    throws IndexerException {

        try {
            // Grab all the supporting database content
            termNodeIds = getNodeIds();
            goIds = getGOIds();

            // Alleles
            alleles = IndexerMap.getGeneToAlleles(alleleCore);

            // Phenotype call summaries (1)
            phenotypes1 = getPhenotypeCallSummary1();
            impcBeans = getImpcPipe();
            legacyBeans = getLegacyPipe();

            // Phenotype call summaries (2)
            phenotypes2 = getPhenotypeCallSummary2();
            strains = getStrains();
            pppBeans = getPPPBeans();
        } catch (SQLException e) {
            throw new IndexerException(e);
        }
    }


    private Map<String, List<Integer>> getNodeIds()
    throws SQLException {

        Map<String, List<Integer>> beans = new HashMap<>();
        String q = "select nt.node_id, ti.term_id from mp_term_infos ti, mp_node2term nt where ti.term_id=nt.term_id and ti.term_id !='MP:0000001'";
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
        logger.debug(" Added {} node Ids", count);

        return beans;
    }

    private Map<String, List<String>> getGOIds()
    throws SQLException {

        Map<String, List<String>> beans = new HashMap<>();

        String q = "select distinct x.xref_id, ti.term_id from mp_dbxrefs x inner join mp_term_infos ti on x.term_id=ti.term_id and x.xref_id like 'GO:%'";
        PreparedStatement ps = ontoDbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            String tId = rs.getString("term_id");
            String xrefId = rs.getString("xref_id");
            if ( ! beans.containsKey(tId)) {
                beans.put(tId, new ArrayList<String>());
            }
            beans.get(tId).add(xrefId);
            count ++;
        }
        logger.debug(" Added {} xrefs", count);

        return beans;
    }

    private Map<String, List<PhenotypeCallSummaryBean>> getPhenotypeCallSummary1()
    throws SQLException {

        Map<String, List<PhenotypeCallSummaryBean>> beans = new HashMap<>();

        String q = "select distinct gf_acc, mp_acc, concat(mp_acc,'_',gf_acc) as mp_mgi, parameter_id, procedure_id, pipeline_id, allele_acc, strain_acc from phenotype_call_summary where gf_db_id=3 and gf_acc like 'MGI:%' and allele_acc is not null and strain_acc is not null";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
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
                beans.put(mpAcc, new ArrayList<PhenotypeCallSummaryBean>());
            }
            beans.get(mpAcc).add(bean);
            count ++;
        }
        logger.debug(" Added {} phenotype call summaries (1)", count);

        return beans;
    }

    private Map<String, List<String>> getImpcPipe()
    throws SQLException {

        Map<String, List<String>> beans = new HashMap<>();

        String q = "select distinct external_db_id as 'impc', concat (mp_acc,'_', gf_acc) as mp_mgi from phenotype_call_summary where external_db_id = 22";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            String tId = rs.getString("mp_mgi");
            String impc = rs.getString("impc");
            if ( ! beans.containsKey(tId)) {
                beans.put(tId, new ArrayList<String>());
            }
            beans.get(tId).add(impc);
            count ++;
        }
        logger.debug(" Added {} IMPC records", count);

        return beans;
    }

    private Map<String, List<String>> getLegacyPipe()
    throws SQLException {

        Map<String, List<String>> beans = new HashMap<>();

        String q = "select distinct external_db_id as 'legacy', concat (mp_acc,'_', gf_acc) as mp_mgi from phenotype_call_summary where external_db_id = 12";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            String tId = rs.getString("mp_mgi");
            String legacy = rs.getString("legacy");
            if ( ! beans.containsKey(tId)) {
                beans.put(tId, new ArrayList<String>());
            }
            beans.get(tId).add(legacy);
            count ++;
        }
        logger.debug(" Added {} legacy records", count);

        return beans;
    }

    private Map<String, List<PhenotypeCallSummaryBean>> getPhenotypeCallSummary2()
    throws SQLException {

        Map<String, List<PhenotypeCallSummaryBean>> beans = new HashMap<>();

        String q = "select distinct gf_acc, mp_acc, parameter_id, procedure_id, pipeline_id, concat(parameter_id,'_',procedure_id,'_',pipeline_id) as ididid, allele_acc, strain_acc from phenotype_call_summary where gf_db_id=3 and gf_acc like 'MGI:%' and allele_acc is not null and strain_acc is not null";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
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
                beans.put(mpAcc, new ArrayList<PhenotypeCallSummaryBean>());
            }
            beans.get(mpAcc).add(bean);
            count ++;
        }
        logger.debug(" Added {} phenotype call summaries (2)", count);

        return beans;
    }

    private Map<String, List<MPStrainBean>> getStrains()
    throws SQLException {

        Map<String, List<MPStrainBean>> beans = new HashMap<>();

        String q = "select distinct name, acc from strain where db_id=3";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while (rs.next()) {
            MPStrainBean bean = new MPStrainBean();

            String acc = rs.getString("acc");

            bean.setAcc(acc);
            bean.setName(rs.getString("name"));

            if ( ! beans.containsKey(acc)) {
                beans.put(acc, new ArrayList<MPStrainBean>());
            }
            beans.get(acc).add(bean);
            count ++;
        }
        logger.debug(" Added {} strain beans", count);

        return beans;
    }

    private Map<String, List<ParamProcedurePipelineBean>> getPPPBeans()
    throws SQLException {

        Map<String, List<ParamProcedurePipelineBean>> beans = new HashMap<>();

        String q = "select concat(pp.id,'_',pproc.id,'_',ppipe.id) as ididid, pp.name as parameter_name, pp.stable_key as parameter_stable_key, pp.stable_id as parameter_stable_id, pproc.name as procedure_name, pproc.stable_key as procedure_stable_key, pproc.stable_id as procedure_stable_id, ppipe.name as pipeline_name, ppipe.stable_key as pipeline_key, ppipe.stable_id as pipeline_stable_id from phenotype_parameter pp inner join phenotype_procedure_parameter ppp on pp.id=ppp.parameter_id inner join phenotype_procedure pproc on ppp.procedure_id=pproc.id inner join phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id inner join phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id";
        PreparedStatement ps = komp2DbConnection.prepareStatement(q);
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
                beans.put(id, new ArrayList<ParamProcedurePipelineBean>());
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
     * @param mp
     * @param mpDTO
     */
    protected static void addTopLevelTerms(MpDTO mp, OntologyTermDTO mpDTO) {

      	if (mpDTO.getTopLevelIds() != null && mpDTO.getTopLevelIds().size() > 0){
            mp.addTopLevelMpId(mpDTO.getTopLevelIds());
            mp.addTopLevelMpTerm(mpDTO.getTopLevelNames());
            mp.addTopLevelMpTermSynonym(mpDTO.getTopLevelSynonyms());
            mp.addTopLevelMpTermId(mpDTO.getTopLevelMpTermIds());
            mp.addTopLevelMpTermInclusive(mpDTO.getTopLevelNames());
            mp.addTopLevelMpIdInclusive(mpDTO.getTopLevelIds());

        }
        else {
            // add self as top level
            mp.addTopLevelMpTermInclusive(mpDTO.getAccessionId() + mpDTO.getName());
            mp.addTopLevelMpIdInclusive(mpDTO.getAccessionId());
        }

    }

    private void getMaTermsForMp(MpDTO mp) {

        // get MA ids referenced from MP
        Set<String> maTerms = mpMaParser.getReferencedClasses(mp.getMpId(), viaProperties, "MA");
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
                //TODO intermediate terms - can't do this before merge to master as fields were not in schema
            } else {
                System.out.println("Term not found in MA : " + maId);
            }
        }

    }

    private void addPhenotype1(MpDTO mp, RunStatus runStatus) {
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
                addPreQc(mp, pheno1.getGfAcc(), runStatus);
                addAllele(mp, alleles.get(pheno1.getGfAcc()), false);
            }
        }
    }

    private void checkMgiDetails(MpDTO mp) {
        if (mp.getMgiAccessionId() == null) {
            mp.setMgiAccessionId(new ArrayList<String>());
            mp.setLatestPhenotypeStatus(new ArrayList<String>());
        }
    }

    private void addPreQc(MpDTO mp, String gfAcc, RunStatus runStatus) {
        SolrQuery query = new SolrQuery("mp_term_id:\"" + mp.getMpId() + "\" AND marker_accession_id:\"" + gfAcc + "\"");
        query.setFields("mp_term_id", "marker_accession_id");
        try {
            QueryResponse response = preqcCore.query(query);
            for (SolrDocument doc : response.getResults()) {
                if (doc.getFieldValue("mp_term_id") != null) {
                    // From JS mapping script - row.get('preqc_mp_id')
                    mp.getLatestPhenotypeStatus().add("Phenotyping Started");
                }
            }
        } catch (Exception e) {
            runStatus.addError(" Caught error accessing PreQC core: " + e.getMessage() + ".\nQuery: " + query);
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
                    mp.setDiseaseId(new ArrayList<String>(new HashSet<>(mp.getDiseaseId())));
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
                    mp.setMgiNovelPredictedInLocus(new ArrayList<Boolean>(new HashSet<>(mp.getMgiNovelPredictedInLocus())));
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
            mp.setType(new ArrayList<String>());
            mp.setDiseaseSource(new ArrayList<String>());
            mp.setDiseaseId(new ArrayList<String>());
            mp.setDiseaseTerm(new ArrayList<String>());
            mp.setDiseaseAlts(new ArrayList<String>());
            mp.setDiseaseClasses(new ArrayList<String>());
            mp.setHumanCurated(new ArrayList<Boolean>());
            mp.setMouseCurated(new ArrayList<Boolean>());
            mp.setMgiPredicted(new ArrayList<Boolean>());
            mp.setImpcPredicted(new ArrayList<Boolean>());
            mp.setMgiPredictedKnownGene(new ArrayList<Boolean>());
            mp.setImpcPredictedKnownGene(new ArrayList<Boolean>());
            mp.setMgiNovelPredictedInLocus(new ArrayList<Boolean>());
            mp.setImpcNovelPredictedInLocus(new ArrayList<Boolean>());
            // MGI accession ID should already be set
            mp.setMarkerSymbol(new ArrayList<String>());
            mp.setMarkerName(new ArrayList<String>());
            mp.setMarkerSynonym(new ArrayList<String>());
            mp.setMarkerType(new ArrayList<String>());
            mp.setHumanGeneSymbol(new ArrayList<String>());
            mp.setStatus(new ArrayList<String>());
            mp.setImitsPhenotypeStarted(new ArrayList<String>());
            mp.setImitsPhenotypeComplete(new ArrayList<String>());
            mp.setImitsPhenotypeStatus(new ArrayList<String>());
            mp.setLatestProductionCentre(new ArrayList<String>());
            mp.setLatestPhenotypingCentre(new ArrayList<String>());
            mp.setAlleleName(new ArrayList<String>());
            mp.setPreqcGeneId(new ArrayList<String>());
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
                mp.setStrainId(new ArrayList<String>());
                mp.setStrainName(new ArrayList<String>());
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
                mp.setParameterName(new ArrayList<String>());
                mp.setParameterStableId(new ArrayList<String>());
                mp.setParameterStableKey(new ArrayList<String>());
                mp.setProcedureName(new ArrayList<String>());
                mp.setProcedureStableId(new ArrayList<String>());
                mp.setProcedureStableKey(new ArrayList<String>());
                mp.setPipelineName(new ArrayList<String>());
                mp.setPipelineStableId(new ArrayList<String>());
                mp.setPipelineStableKey(new ArrayList<String>());
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

    // PROTECTED METHODS
    public static void main(String[] args) throws IndexerException {
        SpringApplication.run(MPIndexer.class, args);
    }

}
