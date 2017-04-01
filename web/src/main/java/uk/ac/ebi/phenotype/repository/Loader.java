package uk.ac.ebi.phenotype.repository;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONObject;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.PhenodigmDTO;
import org.neo4j.helpers.collection.Iterables;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by ckchen on 17/03/2017.
 */

@Component
public class Loader implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    @Qualifier("phenodigmDataSource")
    DataSource phenodigmDataSource;

    @NotNull
    @Value("${allele2File}")
    private String pathToAlleleFile;

    @NotNull
    @Value("${human2mouseFilename}")
    private String pathToHuman2mouseFilename;

    @NotNull
    @Value("${mpListPath}")
    private String mpListPath;

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;

    @Autowired
    GeneRepository geneRepository;

    @Autowired
    AlleleRepository alleleRepository;

    @Autowired
    EnsemblGeneIdRepository ensemblGeneIdRepository;

    @Autowired
    MarkerSynonymRepository markerSynonymRepository;

    @Autowired
    HumanGeneSymbolRepository humanGeneSymbolRepository;

    @Autowired
    MpRepository mpRepository;

    @Autowired
    HpRepository hpRepository;

    @Autowired
    OntoSynonymRepository ontoSynonymRepository;

    @Autowired
    DiseaseRepository diseaseRepository;

    @Autowired
    MouseModelRepository mouseModelRepository;

    OntologyParserFactory ontologyParserFactory;


    Map<String, Allele> loadedAlleles = new HashMap<>();
    Map<String, Gene> loadedGenes = new HashMap<>();
    Map<String, Gene> loadedSymbolGenes = new HashMap<>();
    Map<String, Mp> loadedMps = new HashMap<>();
    Map<String, Hp> loadedHps = new HashMap<>();
    Map<String, Disease> loadedDiseases = new HashMap<>();
    Map<Integer, MouseModel> loadedMouseModels = new HashMap<>();

    Map<String, EnsemblGeneId> ensGidEnsemblGeneIdMap = new HashMap<>();
    Map<Integer, Set<Mp>> mouseModelIdMpMap = new HashMap<>();
    Map<String, String> hpIdTermMap = new HashMap<>();
    Map<String, Set<Hp>> bestMpIdHpMap = new HashMap<>();
    Map<String, Set<Hp>> diseaseIdPhenotypeMap = new HashMap<>();

    private OntologyParser mpHpParser;
    private OntologyParser mpParser;

    private static final int LEVELS_FOR_NARROW_SYNONYMS = 2;

    public Loader() {}

    @Override
    public void run(String... strings) throws Exception {

        ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);

        // NOTE that deleting repositories takes lots of memory
        // would be way faster to just remove the db directory

        logger.info("Start deleting all repositories ...");
        geneRepository.deleteAll();
        alleleRepository.deleteAll();
        ensemblGeneIdRepository.deleteAll();
        markerSynonymRepository.deleteAll();
        ontoSynonymRepository.deleteAll();
        humanGeneSymbolRepository.deleteAll();
        mpRepository.deleteAll();
        hpRepository.deleteAll();
        diseaseRepository.deleteAll();
        mouseModelRepository.deleteAll();
        logger.info("Done deleting all repositories");

        Connection komp2Conn = komp2DataSource.getConnection();
        Connection diseaseConn = phenodigmDataSource.getConnection();

        //----------- STEP 1 -----------//
        // loading Gene, Allele, EnsemblGeneId, MarkerSynonym, human orthologs
        // based on Peter's allele2 flatfile
        loadGenes();

        //----------- STEP 2 -----------//
        populateHpIdTermMap();   // STEP 2.1
        populateBestMpIdHpMap(); // STEP 2.2
        loadMousePhenotypes();   // STEP 2.3

        //----------- STEP 3 -----------//
        populateMouseModelIdMpMap(); // run this before loadMouseModel()
        loadMouseModels();

        //----------- STEP 4 -----------//
        // load disease and Gene, Hp, Mp relationships
        populateDiseaseIdPhenotypeMap();
        loadDiseases();

        //----------- STEP 5 -----------//
        connectDiseaseMouseModels();

    }

    public void populateBestMpIdHpMap() throws SQLException {

        long begin = System.currentTimeMillis();
        String query = "SELECT hp_id, hp_term, mp_id FROM best_impc_mp_hp_mapping ";

        try (Connection connection = phenodigmDataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet r = p.executeQuery();
            while (r.next()) {
                String mpId = r.getString("mp_id");
                String hpId = r.getString("hp_id");
                String hpTerm = r.getString("hp_term");

                if (! loadedHps.containsKey(hpId)){
                    Hp hp = new Hp();
                    hp.setHpId(hpId);
                    hp.setHpTerm(hpTerm);
                    loadedHps.put(hpId, hp);
                }

                if (! bestMpIdHpMap.containsKey(mpId)){
                    bestMpIdHpMap.put(mpId, new HashSet<Hp>());
                }
                bestMpIdHpMap.get(mpId).add(loadedHps.get(hpId));
            }
        }

        String job = "populateBestMpIdHpMap";
        loadTime(begin, System.currentTimeMillis(), job);
    }

    public void populateHpIdTermMap() throws SolrServerException, IOException, SQLException {

        long begin = System.currentTimeMillis();
        String query = "SELECT DISTINCT hp_id, term FROM hp ";

        try (Connection connection = phenodigmDataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet r = p.executeQuery();
            while (r.next()) {
                String hpId = r.getString("hp_id");
                String hpTerm = r.getString("term");
                // human Phenotype id to term mapping
                hpIdTermMap.put(hpId, hpTerm);
            }
        }

        String job = "populateHpIdTermMap";
        loadTime(begin, System.currentTimeMillis(), job);
    }

    public void populateMouseModelIdMpMap() throws SQLException {

        long begin = System.currentTimeMillis();
        String query = "SELECT mm.model_id, mmm.mp_id FROM mouse_model_mp mmm, mouse_model mm WHERE mmm.model_id=mm.model_id AND mm.source LIKE '%IMPC%'";

        try (Connection connection = phenodigmDataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            ResultSet r = p.executeQuery();
            while (r.next()) {

                Integer id = r.getInt("model_id");
                if (!mouseModelIdMpMap.containsKey(id)) {
                    mouseModelIdMpMap.put(id, new HashSet<Mp>());
                }

                String mpId = r.getString("mp_id");
                // only want MPs that IMPC knows about
                if (loadedMps.containsKey(mpId)){
                    mouseModelIdMpMap.get(id).add(loadedMps.get(mpId));
                }
            }
            String job = "populateMouseModelIdMpMap";
            loadTime(begin, System.currentTimeMillis(), job);
        }
    }

    public void loadMouseModels() throws SQLException {

        // only MouseModels from IMPC
        long begin = System.currentTimeMillis();

        String query = "SELECT " +
                "  'mouse_model'         AS type, " +
                "  mm.model_id           AS model_id, " +
                "  mgo.model_gene_id     AS model_gene_id, " +
                "  mgo.model_gene_symbol AS model_gene_symbol, " +
                "  mm.source, " +
                "  mm.allelic_composition, " +
                "  mm.genetic_background, " +
                "  mm.allele_ids, " +
                "  mm.hom_het " +
                "FROM mouse_model mm " +
                "  JOIN mouse_model_gene_ortholog mmgo ON mmgo.model_id = mm.model_id " +
                "  JOIN mouse_gene_ortholog mgo ON mgo.model_gene_id = mmgo.model_gene_id " +
                "WHERE mm.source = 'IMPC'";

        try (Connection connection = phenodigmDataSource.getConnection();

             PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            int mmCount = 0;
            ResultSet r = p.executeQuery();
            while (r.next()) {

                int modelId = r.getInt("model_id");
                MouseModel mm = new MouseModel();
                mm.setModelId(modelId);

                String mgiAcc = r.getString("model_gene_id");
                // only want Genes that IMPC knows about
                if (loadedGenes.containsKey(mgiAcc)) {
                    mm.setGene(loadedGenes.get(mgiAcc));
                }

                mm.setAllelicComposition(r.getString("allelic_composition"));
                mm.setGeneticBackground(r.getString("genetic_background"));

                mm.setHomHet(r.getString("hom_het"));

                // mouse Phenotype associated with this mouseModel
                if (mouseModelIdMpMap.containsKey(modelId)) {
                    for (Mp mp : mouseModelIdMpMap.get(modelId)) {
                        if (mm.getMousePhenotypes() == null) {
                            mm.setMousePhenotypes(new HashSet<Mp>());
                        }
                        mm.getMousePhenotypes().add(mp);
                    }
                }

                mmCount++;
                mouseModelRepository.save(mm);
                loadedMouseModels.put(mm.getModelId(), mm);

                if (mmCount % 1000 == 0) {
                    logger.info("Added {} MouseModel nodes", mmCount);
                }
            }

            logger.info("Added total of {} MouseModel nodes", mmCount);
            String job = "MouseeModel nodes";
            loadTime(begin, System.currentTimeMillis(), job);
        }
    }

    public void populateDiseaseIdPhenotypeMap() throws SQLException {
        long begin = System.currentTimeMillis();

        String query = "SELECT DISTINCT dh.disease_id, dh.hp_id FROM disease d " +
                       "LEFT JOIN disease_hp dh ON d.disease_id=dh.disease_id " +
                       "LEFT JOIN mouse_disease_summary mds ON mds.disease_id=d.disease_id";

        try (Connection connection = phenodigmDataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet r = p.executeQuery();
            while (r.next()) {

                String diseaseId = r.getString("disease_id");
                if (! diseaseIdPhenotypeMap.containsKey(diseaseId)) {
                    diseaseIdPhenotypeMap.put(diseaseId, new HashSet<Hp>());
                }
                String hpId = r.getString("hp_id");
                if (loadedHps.containsKey(hpId)){
                    diseaseIdPhenotypeMap.get(diseaseId).add(loadedHps.get(hpId));
                }
                else {
                    Hp hp = new Hp();
                    hp.setHpId(hpId);
                    hp.setHpTerm(hpIdTermMap.get(hpId));
                    diseaseIdPhenotypeMap.get(diseaseId).add(hp);
                }
            }
            String job = "populateDiseaseIdPhenotypeMap";
            loadTime(begin, System.currentTimeMillis(), job);
        }
    }

    public void loadDiseases() throws SQLException {

        long begin = System.currentTimeMillis();

        String query = "SELECT" +
                "  'disease_gene_summary'               AS type, " +
                "  d.disease_id, " +
                "  disease_term, " +
                "  disease_alts, " +
                "  disease_locus, " +
                "  disease_classes                      AS disease_classes, " +
                "  mgo.model_gene_id                    AS model_gene_id, " +
                "  mgo.model_gene_symbol                AS model_gene_symbol, " +
                "  mgo.hgnc_id AS hgnc_gene_id, " +
                "  mgo.hgnc_gene_symbol, " +
                "  human_curated, " +
                "  mod_curated                          AS mod_curated, " +
                "  in_locus, " +
                "  max_mod_disease_to_model_perc_score  AS max_mod_disease_to_model_perc_score, " +
                "  max_mod_model_to_disease_perc_score  AS max_mod_model_to_disease_perc_score, " +
                "  max_htpc_disease_to_model_perc_score AS max_htpc_disease_to_model_perc_score, " +
                "  max_htpc_model_to_disease_perc_score AS max_htpc_model_to_disease_perc_score, " +
                "  mod_raw_score                        AS raw_mod_score, " +
                "  htpc_raw_score                       AS raw_htpc_score, " +
                "  mod_predicted                        AS mod_predicted, " +
                "  mod_predicted_known_gene             AS mod_predicted_known_gene, " +
                "  novel_mod_predicted_in_locus         AS novel_mod_predicted_in_locus, " +
                "  htpc_predicted                       AS htpc_predicted, " +
                "  htpc_predicted_known_gene            AS htpc_predicted_known_gene, " +
                "  novel_htpc_predicted_in_locus        AS novel_htpc_predicted_in_locus " +
                "FROM " +
                "  mouse_disease_gene_summary_high_quality mdgshq " +
                "  LEFT JOIN disease d ON d.disease_id = mdgshq.disease_id" +
                "  JOIN (SELECT DISTINCT model_gene_id, model_gene_symbol, hgnc_id, hgnc_gene_symbol FROM mouse_gene_ortholog) mgo ON mgo.model_gene_id = mdgshq.model_gene_id ";


        int dCount = 0;

        try (Connection connection = phenodigmDataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            ResultSet r = p.executeQuery();
            while (r.next()) {

                String diseaseId = r.getString("disease_id");

                if (diseaseId == null) {
                    continue;
                }

                Disease d = new Disease();

                d.setDiseaseId(diseaseId);
                d.setDiseaseTerm(r.getString("disease_term"));
                d.setDiseaseLocus(r.getString("disease_locus"));

                //doc.setDiseaseAlts(Arrays.asList(r.getString("disease_alts").split("\\|")));
                //doc.setDiseaseClasses(Arrays.asList(r.getString("disease_classes").split(",")));

                d.setHumanCurated(r.getBoolean("human_curated"));
                d.setMouseCurated(r.getBoolean("mod_curated"));

                // <!--summary fields for faceting-->
                d.setMgiPredicted(r.getBoolean("mod_predicted"));
                d.setImpcPredicted(r.getBoolean("htpc_predicted"));
                d.setInLocus(r.getBoolean("in_locus"));

                // mouse gene model for the human disease
                String mgiAcc = r.getString("model_gene_id");
                if (loadedGenes.containsKey(mgiAcc)) {
                    Gene g = loadedGenes.get(mgiAcc);
                    d.setGene(g);

                    if (g.getDiseases() == null){
                        g.setDiseases(new HashSet<Disease>());
                    }
                    g.getDiseases().add(d);
                }

                d.setHgncGeneId(r.getString("hgnc_gene_id"));
                d.setHgncGeneSymbol(r.getString("hgnc_gene_symbol"));

                // <!--model organism database (MGI) scores-->
                d.setMaxMgiD2mScore(getDoubleDefaultZero(r, "max_mod_disease_to_model_perc_score"));
                d.setMaxMgiM2dScore(getDoubleDefaultZero(r, "max_mod_model_to_disease_perc_score"));

                // <!--IMPC scores-->
                d.setMaxImpcD2mScore(getDoubleDefaultZero(r, "max_htpc_disease_to_model_perc_score"));
                d.setMaxImpcM2dScore(getDoubleDefaultZero(r, "max_htpc_model_to_disease_perc_score"));

                // <!--raw scores-->
                d.setRawModScore(getDoubleDefaultZero(r, "raw_mod_score"));
                d.setRawHtpcScore(getDoubleDefaultZero(r, "raw_htpc_score"));

                d.setMgiPredictedKnownGene(r.getBoolean("mod_predicted_known_gene"));
                d.setImpcPredictedKnownGene(r.getBoolean("htpc_predicted_known_gene"));
                d.setMgiNovelPredictedInLocus(r.getBoolean("novel_mod_predicted_in_locus"));
                d.setImpcNovelPredictedInLocus(r.getBoolean("novel_htpc_predicted_in_locus"));

                // Disease human phenotype associations
                if ( diseaseIdPhenotypeMap.containsKey(diseaseId)) {
                    for (Hp hp : diseaseIdPhenotypeMap.get(diseaseId)) {
                        if (d.getHumanPhenotypes() == null) {
                            d.setHumanPhenotypes(new HashSet<Hp>());
                        }
                        d.getHumanPhenotypes().add(hp);
                    }
                }

                dCount++;
                diseaseRepository.save(d);
                loadedDiseases.put(d.getDiseaseId(), d);

                if (dCount % 1000 == 0) {
                    logger.info("Added {} Disease nodes", dCount);
                }
            }
            logger.info("Added total of {} Disease nodes", dCount);
            String job = "Disease nodes";
            loadTime(begin, System.currentTimeMillis(), job);
        }
    }

    public void  connectDiseaseMouseModels() throws SQLException {

        long begin = System.currentTimeMillis();

//        Map<Integer, MouseModel> allMouseModels = new HashMap<>();
//        Iterable<MouseModel> mms = mouseModelRepository.findAll();
//            for(MouseModel mm : mms){
//            allMouseModels.put(mm.getModelId(), mm);
//        }
//        System.out.println("Done populating MouseModel map");
//
        // disease takes ages to finish (> 30 and still not finished, aborted)
//        Map<String, Disease> allDiseases = new HashMap<>();
//        Iterable<Disease> ds = diseaseRepository.findAll();
//        for(Disease d : ds){
//            allDiseases.put(d.getDiseaseId(), d);
//        }
//        System.out.println("Done populating Disease map");
//
//        Map<String, Hp> allHps = new HashMap<>();
//        Iterable<Hp> hps = hpRepository.findAll();
//        for(Hp hp : hps){
//            allHps.put(hp.getHpId(), hp);
//        }
//        System.out.println("Done populating Hp map");

//        Map<String, Mp> allMps = new HashMap<>();
//        Iterable<Mp> mps = mpRepository.findAll();
//        for(Mp mp : mps){
//            allMps.put(mp.getMpId(), mp);
//        }
//        System.out.println("Done populating Mp map");

        String query = "SELECT " +
                "  'disease_model_association'      AS type, " +
                "  mdgshq.disease_id, " +
                "  mmgo.model_gene_id               AS model_gene_id, " +
                "  mmgo.model_id, " +
                "  mdma.lit_model, " +
                "  mdma.disease_to_model_perc_score AS disease_to_model_perc_score, " +
                "  mdma.model_to_disease_perc_score AS model_to_disease_perc_score, " +
                "  mdma.raw_score, " +
                "  mdma.hp_matched_terms, " +
                "  mdma.mp_matched_terms, " +
                "  mdgshq.mod_predicted, " +
                "  mdgshq.htpc_predicted, " +
                "  d.disease_classes " +
                "FROM mouse_disease_gene_summary_high_quality mdgshq " +
                "  JOIN mouse_model_gene_ortholog mmgo ON mdgshq.model_gene_id = mmgo.model_gene_id " +
                "  JOIN mouse_disease_model_association mdma ON mdgshq.disease_id = mdma.disease_id AND mmgo.model_id = mdma.model_id " +
                "  JOIN disease d ON d.disease_id = mdgshq.disease_id " ;

        System.out.println("DISEASE MODEL ASSOC QUERY: " + query);
        try (Connection connection = phenodigmDataSource.getConnection();
             PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            ResultSet r = p.executeQuery();

            int mCount = 0;

            while (r.next()) {

                int modelId = r.getInt("model_id");

                // only want IMPC related mouse models
                if (loadedMouseModels.containsKey(modelId)) {
                    MouseModel mm = loadedMouseModels.get(modelId);
                    String diseaseId = r.getString("disease_id");
                    Disease d = loadedDiseases.get(diseaseId);
                    if (d.getMouseModels() == null) {
                        d.setMouseModels(new HashSet<MouseModel>());
                    }
                    d.getMouseModels().add(mm);

                    List<String> hpIds = Arrays.asList(r.getString("hp_matched_terms").split(","));
                    for (String hpId : hpIds) {
                        if (loadedHps.containsKey(hpId)) {
                            Hp hp = loadedHps.get(hpId);

                            if (mm.getHumanPhenotypes() == null) {
                                mm.setHumanPhenotypes(new HashSet<Hp>());
                            }
                            mm.getHumanPhenotypes().add(hp);
                        }
                    }

                    List<String> mpIds = Arrays.asList(r.getString("mp_matched_terms").split(","));
                    for (String mpId : mpIds) {
                        if (loadedMps.containsKey(mpId)) {
                            Mp mp = loadedMps.get(mpId);

                            if (mm.getMousePhenotypes() == null) {
                                mm.setMousePhenotypes(new HashSet<Mp>());
                            }
                            mm.getMousePhenotypes().add(mp);
                        }
                    }

                    // connects a MouseModel to allele
                    // allelicComposition:	Nes<tm1b(KOMP)Wtsi>/Nes<tm1b(KOMP)Wtsi>
                    // allele symbol: Nes<tm1b(KOMP)Wtsi>
                    String allelicComposition = mm.getAllelicComposition();
                    String[] diploids = StringUtils.split(allelicComposition, "/");
                    String alleleSymbol = !diploids[0].equals("+") ? diploids[0] : diploids[1];
                    if (loadedAlleles.containsKey(alleleSymbol)){
                        mm.setAllele(loadedAlleles.get(alleleSymbol));
                    }

                    diseaseRepository.save(d);
                    mouseModelRepository.save(mm);

                    mCount++;
                    if (mCount % 1000 == 0) {
                        logger.info("Added {} MouseModel - Disease connections", mCount);
                    }
                }
            }
            logger.info("Added total of {} MouseModel - Disease connections",  mCount);
            String job = "connectDiseaseMouseModels";
            loadTime(begin, System.currentTimeMillis(), job);
        }
    }

    public void loadMousePhenotypes() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, SQLException, URISyntaxException, SolrServerException {
        long begin = System.currentTimeMillis();

        mpParser = ontologyParserFactory.getMpParser();
        logger.info("Loaded mp parser");

//        mpHpParser = ontologyParserFactory.getMpHpParser();
//        logger.info("Loaded mp hp parser");

        int mpCount = 0;
        for (String mpId: mpParser.getTermsInSlim()) {

            OntologyTermDTO mpDTO = mpParser.getOntologyTerm(mpId);
            String termId = mpDTO.getAccessionId();

            Mp mp = mpRepository.findByMpId(termId);
            if (mp == null){
                mp = new Mp();
                mp.setMpId(termId);
            }
            if (mp.getMpTerm() == null) {
                mp.setMpTerm(mpDTO.getName());
            }
            if (mp.getMpDefinition() == null) {
                mp.setMpDefinition(mpDTO.getDefinition());
            }

            if (mp.getOntoSynonyms() == null) {
                for (String mpsym : mpDTO.getSynonyms()) {
                    OntoSynonym ms = new OntoSynonym();
                    ms.setOntoSynonym(mpsym);
                    ms.setMousePhenotype(mp);
                    //ontoSynonymRepository.save(ms);

                    if (mp.getOntoSynonyms() == null) {
                        mp.setOntoSynonyms(new HashSet<OntoSynonym>());
                    }
                    mp.getOntoSynonyms().add(ms);
                }
            }

            // PARENT
            if (mp.getMpParentIds() == null) {
                if ( mpDTO.getParentIds() != null) {
                    for (String parId : mpDTO.getParentIds()) {
                        Mp thisPh = mpRepository.findByMpId(parId);
                        if (thisPh == null) {
                            thisPh = new Mp();
                        }
                        thisPh.setMpId(parId);
                        //mpRepository.save(thisPh); same transactioin

                        if (mp.getMpParentIds() == null) {
                            mp.setMpParentIds(new HashSet<Mp>());
                        }
                        mp.getMpParentIds().add(thisPh);
                    }
                }
            }

            // MARK MP WHICH IS TOP LEVEL
            if (mpDTO.getTopLevelIds() == null || mpDTO.getTopLevelIds().size() == 0){
                // add self as top level
                mp.setTopLevelStatus(true);
            }

            // BEST MP to HP mapping
            if (bestMpIdHpMap.containsKey(termId)){
                for(Hp hp : bestMpIdHpMap.get(termId)){
                    if (mp.getHumanPhenotypes() == null){
                        mp.setHumanPhenotypes(new HashSet<Hp>());
                    }
                    mp.getHumanPhenotypes().add(hp);
                }
            }

            // add mp-hp mapping using Monarch's mp-hp hybrid ontology: gets the narrow synonym
            // TO DO

            mpRepository.save(mp);
            loadedMps.put(mp.getMpId(), mp);

            mpCount++;

            if (mpCount % 1000 == 0) {
                logger.info("Added {} mp nodes",  mpCount);
            }
        }

        logger.info("Added total of {} mp nodes",  mpCount);
        String job = "loadMousePhenotypes";
        loadTime(begin, System.currentTimeMillis(), job);
    }

    public void loadHumanOrtholog() throws IOException {

        long begin = System.currentTimeMillis();
        BufferedReader in = new BufferedReader(new FileReader(new File(pathToHuman2mouseFilename)));

        int symcount = 0;

        String line = in.readLine();
        while (line != null) {
            //System.out.println(line);
            String[] array = line.split("\t");

            if (! array[0].isEmpty() && ! array[4].isEmpty()) {

                String humanSym = array[0];
                String mouseSym = array[4];

                // only want IMPC gene symbols
                if (loadedSymbolGenes.containsKey(mouseSym)) {
                    Gene gene = loadedSymbolGenes.get(mouseSym);

                    symcount++;

                    Set<HumanGeneSymbol> hgset = new HashSet<>();
                    HumanGeneSymbol hgs = new HumanGeneSymbol();
                    hgs.setHumanGeneSymbol(humanSym);
                    hgs.setGene(gene);
                    humanGeneSymbolRepository.save(hgs);

                    if (gene.getHumanGeneSymbols() == null) {
                        hgset.add(hgs);
                        gene.setHumanGeneSymbols(new HashSet<HumanGeneSymbol>());
                    }
                    gene.getHumanGeneSymbols().add(hgs);

                    geneRepository.save(gene);

                    if (symcount % 5000 == 0) {
                        logger.info("Loaded {} HumanGeneSymbol nodes", symcount);
                    }
                }
            }

            line = in.readLine();
        }

        logger.info("Loaded total of {} HumanGeneSymbol nodes", symcount);

        String job = "HumanGeneSymbol nodes";
        loadTime(begin, System.currentTimeMillis(), job);
    }


    public void loadGenes() throws IOException, SolrServerException {

        long begin = System.currentTimeMillis();

        final Map<String, String> ES_CELL_STATUS_MAPPINGS = new HashMap<>();
        ES_CELL_STATUS_MAPPINGS.put("No ES Cell Production", "Not Assigned for ES Cell Production");
        ES_CELL_STATUS_MAPPINGS.put("ES Cell Production in Progress", "Assigned for ES Cell Production");
        ES_CELL_STATUS_MAPPINGS.put("ES Cell Targeting Confirmed", "ES Cells Produced");


        final Map<String, String> MOUSE_STATUS_MAPPINGS = new HashMap<>();
        MOUSE_STATUS_MAPPINGS.put("Chimeras obtained", "Assigned for Mouse Production and Phenotyping");
        MOUSE_STATUS_MAPPINGS.put("Micro-injection in progress", "Assigned for Mouse Production and Phenotyping");
        MOUSE_STATUS_MAPPINGS.put("Cre Excision Started", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Rederivation Complete", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Rederivation Started", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Genotype confirmed", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Cre Excision Complete", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Phenotype Attempt Registered", "Mice Produced");


        Map<String, Integer> columns = new HashMap<>();

        BufferedReader in = new BufferedReader(new FileReader(new File(pathToAlleleFile)));
        BufferedReader in2 = new BufferedReader(new FileReader(new File(pathToAlleleFile)));

        String[] header = in.readLine().split("\t");
        for (int i = 0; i < header.length; i++){
            columns.put(header[i], i);
        }

        int geneCount = 0;
        int alleleCount = 0;

        String line = in.readLine();
        while (line != null) {
            //System.out.println(line);
            String[] array = line.split("\t", -1);
            if (array.length == 1) {
                continue;
            }

            if (! array[columns.get("latest_project_status")].isEmpty() && array[columns.get("type")].equals("Gene")) {
                String mgiAcc = array[columns.get("mgi_accession_id")];
                Gene gene = new Gene();
                gene.setMgiAccessionId(mgiAcc);

                String thisSymbol = null;

                if (! array[columns.get("marker_symbol")].isEmpty()) {
                    thisSymbol = array[columns.get("marker_symbol")];
                    gene.setMarkerSymbol(thisSymbol);
                }
                if (! array[columns.get("feature_type")].isEmpty()) {
                    gene.setMarkerType(array[columns.get("feature_type")]);
                }
                if (! array[columns.get("marker_name")].isEmpty()) {
                    gene.setMarkerName(array[columns.get("marker_name")]);
                }
                if (! array[columns.get("synonym")].isEmpty()) {
                    List<String> syms = Arrays.asList(StringUtils.split(array[columns.get("synonym")], "|"));
                    Set<MarkerSynonym> mss = new HashSet<>();

                    for (String sym : syms) {
                        MarkerSynonym ms = new MarkerSynonym();
                        ms.setMarkerSynonym(sym);
                        ms.setGene(gene);
                        mss.add(ms);
                    }
                    gene.setMarkerSynonyms(mss);
                }
                if (! array[columns.get("feature_chromosome")].isEmpty()) {
                    gene.setChrId(array[columns.get("feature_chromosome")]);
                    gene.setChrStart(array[columns.get("feature_coord_start")]);
                    gene.setChrEnd(array[columns.get("feature_coord_end")]);
                    gene.setChrStrand(array[columns.get("feature_strand")]);
                }

                if (! array[columns.get("gene_model_ids")].isEmpty()) {
                    String[] ids = StringUtils.split(array[columns.get("gene_model_ids")], "|");
                    for(int j=0; j<ids.length; j++){
                        String thisId = ids[j];
                        if (thisId.startsWith("ensembl_ids") || thisId.startsWith("\"ensembl_ids")){
                            String[] vals = StringUtils.split(thisId, ":");
                            if (vals.length == 2) {
                                String ensgId = vals[1];
                                //System.out.println("Found " + ensgId);

                                EnsemblGeneId ensg = new EnsemblGeneId();
                                if (! ensGidEnsemblGeneIdMap.containsKey(ensgId)) {
                                    ensg.setEnsemblGeneId(ensgId);
                                    ensg.setGene(gene);
                                    ensGidEnsemblGeneIdMap.put(ensgId, ensg);
                                }
                                else {
                                    ensg = ensGidEnsemblGeneIdMap.get(ensgId);
                                }

                                if (gene.getEnsemblGeneIds() == null) {
                                    gene.setEnsemblGeneIds(new HashSet<EnsemblGeneId>());
                                }
                                gene.getEnsemblGeneIds().add(ensg);
                            }
                        }
                    }
                }

                geneRepository.save(gene);
                loadedGenes.put(mgiAcc, gene);
                loadedSymbolGenes.put(thisSymbol, gene);

                geneCount++;
                if (geneCount % 5000 == 0) {
                    logger.info("Loaded {} Gene nodes", geneCount);
                }
            }

            line = in.readLine();
        }
        logger.info("Done loading Gene nodes");

        String line2 = in2.readLine();
        while (line2 != null) {
            //System.out.println(line);
            String[] array = line2.split("\t", -1);
            if (array.length == 1) {
                continue;
            }

            String mgiAcc = array[columns.get("mgi_accession_id")];

            if (array[columns.get("type")].equals("Allele") && loadedGenes.containsKey(mgiAcc) && ! array[columns.get("allele_mgi_accession_id")].isEmpty()) {

                Gene gene = loadedGenes.get(mgiAcc);

                String alleleAcc = array[columns.get("allele_mgi_accession_id")];

                Allele allele = new Allele();
                allele.setAlleleMgiAccessionId(alleleAcc);
                allele.setGene(gene);

                if (!array[columns.get("allele_symbol")].isEmpty()) {
                    allele.setAlleleSymbol(array[columns.get("allele_symbol")]);
                }
                if (!array[columns.get("allele_description")].isEmpty()) {
                    allele.setAlleleDescription(array[columns.get("allele_description")]);
                }
                if (!array[columns.get("mutation_type")].isEmpty()) {
                    allele.setMutationType(array[columns.get("mutation_type")]);
                }
                if (!array[columns.get("es_cell_status")].isEmpty()) {
                    allele.setEsCellStatus(ES_CELL_STATUS_MAPPINGS.get(array[columns.get("es_cell_status")]));
                }
                if (!array[columns.get("mouse_status")].isEmpty()) {
                    allele.setMouseStatus(MOUSE_STATUS_MAPPINGS.get(array[columns.get("mouse_status")]));
                }
                if (!array[columns.get("phenotype_status")].isEmpty()) {
                    allele.setPhenotypeStatus(array[columns.get("phenotype_status")]);
                }

                if (gene.getAlleles() == null){
                    Set<Allele> aset = new HashSet<Allele>();
                    aset.add(allele);
                    gene.setAlleles(aset);
                }
                else {
                    gene.getAlleles().add(allele);
                }

                alleleRepository.save(allele);
                loadedAlleles.put(allele.getAlleleSymbol(), allele);

                alleleCount++;
                if (alleleCount % 5000 == 0){
                    logger.info("Loaded {} Allele nodes", alleleCount);
                }
            }

            line2 = in2.readLine();
        }

        logger.info("Loaded {} Allele nodes and {} Gene nodes", alleleCount, geneCount);

        String job = "Gene, Allele, MarkerSynonym and EnsemblGeneId nodes";
        loadTime(begin, System.currentTimeMillis(), job);

        // based on MGI report HMD_HumanPhenotype.rpt
        // Mouse/Human Orthology with Phenotype Annotations (tab-delimited)
        loadHumanOrtholog();
    }

    public void loadTime(long begin, long end, String job){

        long elapsedTimeMillis = end - begin;
        double sec = (elapsedTimeMillis / 1000.0);
        double min = Math. floor((elapsedTimeMillis / (1000.0 * 60)) % 60);
        logger.info("Time taken to load {}: {} min {} sec", job, min, sec);
    }

    private Double getDoubleDefaultZero(ResultSet r, String field) throws SQLException {
        Double v = r.getDouble(field);
        return r.wasNull() ? 0.0 : v;
    }
}