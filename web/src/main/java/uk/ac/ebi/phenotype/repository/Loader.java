package uk.ac.ebi.phenotype.repository;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONObject;
import org.mousephenotype.cda.indexers.MPIndexer;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyTermDTO;
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
import java.sql.Connection;
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


//    @Autowired
//    @Qualifier("allele2Core")
//    private SolrClient allele2Core;


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
    MPIndexer mpIndexer;



    Map<String, Gene> loadedGenes = new HashMap<>();
    Map<String, Gene> loadedGeneSymbols = new HashMap<>();

    private OntologyParser mpHpParser;
    private OntologyParser mpParser;


    public Loader() {}

    @Override
    public void run(String... strings) throws Exception {

        Map<String, Gene> loadedGenes = new HashMap<>();

//        geneRepository.deleteAll();
//        alleleRepository.deleteAll();
//        ensemblGeneIdRepository.deleteAll();
//        markerSynonymRepository.deleteAll();
//        humanGeneSymbolRepository.deleteAll();

        Connection komp2Conn = komp2DataSource.getConnection();
        Connection diseaseConn = phenodigmDataSource.getConnection();

        // loading Gene, Allele, EnsemblGeneId, MarkerSynonym
        // based on Peter's allele2 flatfile
       // loadGene1();
        //loadHumanOrtholog();

        loadPhenotypes();




    }


    public void loadPhenotypes() throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, SQLException {
        long begin = System.currentTimeMillis();

        mpParser = mpIndexer.getMpParser();
        System.out.println("Loaded mp parser");

        mpHpParser = new OntologyParser(owlpath + "/mp-hp.owl", "MP", null, null);
        System.out.println("Loaded mp hp parser");

        for (String mpId: mpParser.getTermsInSlim()) {

            OntologyTermDTO mpDTO = mpParser.getOntologyTerm(mpId);
            String termId = mpDTO.getAccessionId();

            Mp ph = mpRepository.findByMpId(termId);
            if (ph == null){
                ph = new Mp();
                ph.setMpId(termId);
            }
            if (ph.getMpTerm() == null) {
                ph.setMpTerm(mpDTO.getName());
            }
            if (ph.getMpDefinition() == null) {
                ph.setMpDefinition(mpDTO.getDefinition());
            }

            if (ph.getOntoSynonyms() == null) {
                for (String mpsym : mpDTO.getSynonyms()) {
                    OntoSynonym ms = new OntoSynonym();
                    ms.setOntoSynonym(mpsym);
                    ms.setMousePhenotype(ph);
                    if (ph.getOntoSynonyms() == null) {
                        ph.setOntoSynonyms(new HashSet<OntoSynonym>());
                    }
                    ph.getOntoSynonyms().add(ms);
                }
            }

            if (ph.getMpTopLevelIds() == null) {
                addTopLevelTerms(ph, mpDTO);
            }
            if (ph.getMpIntermediateIds() == null) {
                addIntermediateTerms(ph, mpDTO);
            }

            // PARENT

            if (ph.getMpParentIds() == null) {
                for (String parId : mpDTO.getParentIds()) {
                    Phenotype thisPh = mpRepository.findByMpId(parId);
                    if (thisPh == null) {
                        thisPh = new Phenotype();
                    }
                    thisPh.setMpId(parId);
                    mpRepository.save(thisPh);

                    if (ph.getMpParentIds() == null) {
                        ph.setMpParentIds(new HashSet<Phenotype>());
                    }
                    ph.getMpParentIds().add(thisPh);
                }
            }

            // CHILD
            if (ph.getMpChildIds() == null) {
                for (String childId : mpDTO.getChildIds()) {
                    Phenotype thisPh = mpRepository.findByMpId(childId);
                    if (thisPh == null) {
                        thisPh = new Phenotype();
                    }
                    thisPh.setMpId(childId);
                    mpRepository.save(thisPh);

                    if (ph.getMpChildIds() == null) {
                        ph.setMpChildIds(new HashSet<Phenotype>());
                    }
                    ph.getMpChildIds().add(thisPh);
                }
            }

//            ph.setChildMpId(mpDTO.getChildIds());
//            mp.setChildMpTerm(mpDTO.getChildNames());
//            mp.setParentMpId(mpDTO.getParentIds());
//            mp.setParentMpTerm(mpDTO.getParentNames());

            //------------

//            MpDTO mp = new MpDTO();
//            mp.setDataType("mp");
//            mp.setMpId(termId);
//            mp.setMpTerm(mpDTO.getName());
//            mp.setMpDefinition(mpDTO.getDefinition());
//
//            // alternative MP ID
//            if (mpDTO.getAlternateIds() != null && !mpDTO.getAlternateIds().isEmpty()) {
//                mp.setAltMpIds(mpDTO.getAlternateIds());
//            }
//
//            mp.setMpNodeId(mpDTO.getNodeIds());
//
//            addTopLevelTerms(mp, mpDTO);
//            addIntermediateTerms(mp, mpDTO);
//
//            mp.setChildMpId(mpDTO.getChildIds());
//            mp.setChildMpTerm(mpDTO.getChildNames());
//            mp.setParentMpId(mpDTO.getParentIds());
//            mp.setParentMpTerm(mpDTO.getParentNames());

            // add mp-hp mapping using Monarch's mp-hp hybrid ontology
            OntologyTermDTO mpTerm = mpHpParser.getOntologyTerm(termId);
            if (mpTerm == null) {
                logger.error("MP term not found using mpHpParser.getOntologyTerm(termId); where termId={}", termId);
            } else {
                Set<OntologyTermDTO> hpTerms = mpTerm.getEquivalentClasses();
                for (OntologyTermDTO hpTerm : hpTerms) {
                    Set<String> hpIds = new HashSet<>();
                    hpIds.add(hpTerm.getAccessionId());

                    for(String hpid : hpIds){

                    }

                    mp.setHpId(new ArrayList(hpIds));
                    if (hpTerm.getName() != null) {
                        Set<String> hpNames = new HashSet<>();
                        hpNames.add(hpTerm.getName());
                        mp.setHpTerm(new ArrayList(hpNames));
                    }
                    if (hpTerm.getSynonyms() != null) {
                        mp.setHpTermSynonym(new ArrayList(hpTerm.getSynonyms()));
                    }
                }
                // get the children of MP not in our slim (narrow synonyms)
                if (isOKForNarrowSynonyms(mp)) {
                    mp.setMpNarrowSynonym(new ArrayList(mpHpParser.getNarrowSynonyms(mpTerm, LEVELS_FOR_NARROW_SYNONYMS)));
                } else {
                    mp.setMpNarrowSynonym(new ArrayList(getRestrictedNarrowSynonyms(mpTerm, LEVELS_FOR_NARROW_SYNONYMS)));
                }
            }

            mp.setMpTermSynonym(mpDTO.getSynonyms());

            getMaTermsForMp(mp);

            // this sets the number of postqc/preqc phenotyping calls of this MP
            addPhenotype1(mp, runStatus);
            mp.setPhenoCalls(sumPhenotypingCalls(termId));
            addPhenotype2(mp);

            List<JSONObject> searchTree = browser.createTreeJson(mpDTO, "/data/phenotype/", mpParser, mpGeneVariantCount, TOP_LEVEL_MP_TERMS);
            mp.setSearchTermJson(searchTree.toString());
            String scrollNodeId = browser.getScrollTo(searchTree);
            mp.setScrollNode(scrollNodeId);
            List<JSONObject> childrenTree = browser.getChildrenJson(mpDTO, "/data/phenotype/", mpParser, mpGeneVariantCount);
            mp.setChildrenJson(childrenTree.toString());

            logger.debug(" Added {} records for termId {}", count, termId);
            count++;

            documentCount++;
            mpCore.addBean(mp, 60000);

            if (documentCount % 100 == 0) {
                System.out.println("Added " + documentCount);
            }
        }


    }


    private void addTopLevelTerms(Phenotype ph, OntologyTermDTO mpDTO) {

        if (mpDTO.getTopLevelIds() != null && mpDTO.getTopLevelIds().size() > 0){

            for (String topid : mpDTO.getTopLevelIds()){
                Phenotype thisPh = mpRepository.findByMpId(topid);
                if ( thisPh == null ){
                    thisPh = new Phenotype();
                }
                thisPh.setMpId(topid);
                thisPh.setTopLevelStatus(true);
                mpRepository.save(thisPh);

                if ( ph.getMpTopLevelIds() == null){
                    ph.setMpTopLevelIds(new HashSet<Phenotype>());
                }
                ph.getMpTopLevelIds().add(thisPh);
            }

//
//            mp.addTopLevelMpId(mpDTO.getTopLevelIds());
//            mp.addTopLevelMpTerm(mpDTO.getTopLevelNames());
//            mp.addTopLevelMpTermSynonym(mpDTO.getTopLevelSynonyms());
//            mp.addTopLevelMpTermId(mpDTO.getTopLevelMpTermIds());
//            mp.addTopLevelMpTermInclusive(mpDTO.getTopLevelNames());
//            mp.addTopLevelMpIdInclusive(mpDTO.getTopLevelIds());

        }
        else {
            // add self as top level
//            mp.addTopLevelMpTermInclusive(mpDTO.getName());
//            mp.addTopLevelMpIdInclusive(mpDTO.getAccessionId());
            ph.setTopLevelStatus(true);
        }

    }

    private void addIntermediateTerms(Phenotype ph, OntologyTermDTO mpDTO {

        if (mpDTO.getIntermediateIds() != null) {
            for (String intermId : mpDTO.getIntermediateIds()){
                Phenotype thisPh = mpRepository.findByMpId(intermId);
                if ( thisPh == null ){
                    thisPh = new Phenotype();
                }
                thisPh.setMpId(intermId);
                mpRepository.save(thisPh);

                if ( ph.getMpIntermediateIds() == null){
                    ph.setMpIntermediateIds(new HashSet<Phenotype>());
                }
                ph.getMpIntermediateIds().add(thisPh);
            }

            //mp.addIntermediateMpId(mpDTO.getIntermediateIds());
//            mp.addIntermediateMpTerm(mpDTO.getIntermediateNames());
//            mp.addIntermediateMpTermSynonym(mpDTO.getIntermediateSynonyms());
        }

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
                //if (loadedGeneSymbols.containsKey(mouseSym)) {
                Gene gene = geneRepository.findByMarkerSymbol(mouseSym);
                if (gene != null){

                    symcount++;

                    Set<HumanGeneSymbol> hgset = new HashSet<>();
                    HumanGeneSymbol hgs = new HumanGeneSymbol();
                    hgs.setHumanGeneSymbol(humanSym);
                    hgs.setGene(gene);
                    humanGeneSymbolRepository.save(hgs);


                   // Gene gene = loadedGeneSymbols.get(mouseSym);
                    if (gene.getHumanGeneSymbols() == null) {
                        hgset.add(hgs);
                        gene.setHumanGeneSymbols(hgset);
                    } else {
                        gene.getHumanGeneSymbols().add(hgs);
                    }

                    geneRepository.save(gene);

                    if (symcount % 5000 == 0) {
                        logger.info("Loaded {} HumanGeneSymbol nodes", symcount);
                    }
                }
            }

            line = in.readLine();
        }

        logger.info("Loaded {} HumanGeneSymbol nodes", symcount);

        String job = "HumanGeneSymbol nodes";
        loadTime(begin, System.currentTimeMillis(), job);
    }


    public void loadGene1() throws IOException, SolrServerException {

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
                Gene gene = geneRepository.findByMgiAccessionId(mgiAcc);
                if (gene == null) {
                    logger.debug("Gene {} not found. Creating Gene with ", mgiAcc);
                    gene = new Gene();
                    gene.setMgiAccessionId(mgiAcc);
                }

                String thisSymbol = null;

                if (! array[columns.get("marker_symbol")].isEmpty()) {
                    thisSymbol = array[columns.get("marker_symbol")];
                    gene.setMarkerSymbol(array[columns.get("marker_symbol")]);
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
                        MarkerSynonym msObj = markerSynonymRepository.findByMarkerSynonym(sym);
                        if (msObj == null) {
                            MarkerSynonym ms = new MarkerSynonym();
                            ms.setMarkerSynonym(sym);
                            ms.setGene(gene);
                            mss.add(ms);
                        }
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
                                EnsemblGeneId ensg = ensemblGeneIdRepository.findByEnsemblGeneId(ensgId);
                                if (ensg == null) {
                                    ensg = new EnsemblGeneId();
                                    ensg.setEnsemblGeneId(ensgId);
                                    ensg.setGene(gene);
                                    ensemblGeneIdRepository.save(ensg);

                                    if (gene.getEnsemblGeneIds() == null) {
                                        Set<EnsemblGeneId> eset = new HashSet<EnsemblGeneId>();
                                        eset.add(ensg);
                                        gene.setEnsemblGeneIds(eset);
                                    } else {
                                        gene.getEnsemblGeneIds().add(ensg);
                                    }
                                }
                            }
                        }
                    }
                }

                geneRepository.save(gene);
                loadedGenes.put(mgiAcc, gene);
                loadedGeneSymbols.put(thisSymbol, gene);

                geneCount++;
                if (geneCount % 5000 == 0) {
                    logger.info("Loaded {} Gene nodes", geneCount);
                }
            }

            line = in.readLine();
        }
        logger.info("Done loading the Type Gene");

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

//    public void loadEnsemblGeneIdsAndGene(Connection komp2Conn){
//        long begin = System.currentTimeMillis();
//
//        try {
//            String query = "SELECT acc, xref_acc FROM xref WHERE acc LIKE 'MGI:%' AND xref_acc LIKE 'ENSMUSG%'";
//            PreparedStatement p = komp2Conn.prepareStatement(query);
//
//            ResultSet r = p.executeQuery();
//            int count = 0;
//            while (r.next()) {
//
//                count++;
//
//                String ensgId = r.getString("xref_acc");
//                String mgiAcc = r.getString("acc");
//
//                //System.out.println(ensgId + " --- " + mgiAcc);
//
//                // multiple ensembl gene id can be assigned to a gene
//                Gene gene = geneRepository.findByMgiAccessionId(mgiAcc);
//                if (gene == null) {
//                    logger.debug("Gene {} not found. Creating Gene with ", mgiAcc);
//                    gene = new Gene();
//                    gene.setMgiAccessionId(mgiAcc);
//                    geneRepository.save(gene);
//                }
//
//                EnsemblGeneId ensg = new EnsemblGeneId();
//                ensg.setEnsemblGeneId(ensgId);
//                ensg.setGene(gene);
//                ensemblGeneIdRepository.save(ensg);
//
//                if (gene.getEnsemblGeneIds() == null){
//                    Set<EnsemblGeneId> eset = new HashSet<EnsemblGeneId>();
//                    eset.add(ensg);
//                    gene.setEnsemblGeneIds(eset);
//                }
//                else {
//                    gene.getEnsemblGeneIds().add(ensg);
//                }
//
//                if (count%5000 ==0){
//                    logger.info("Loaded " + count + " EnsemblGeneId and Gene nodes");
//                }
//            }
//
//            logger.info("Loaded " + count + " EnsemblGeneId/Gene nodes");
//            String job = "EnsemblGeneId/Gene nodes";
//            loadTime(begin, System.currentTimeMillis(), job);
//
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//    }

    public void loadTime(long begin, long end, String job){

        long elapsedTimeMillis = end - begin;
        double minutes = (elapsedTimeMillis / (1000.0 * 60)) % 60;
        logger.info("Time taken to load " + job + ": " + minutes + " min");
    }
}