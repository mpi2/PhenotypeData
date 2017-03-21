package uk.ac.ebi.phenotype.repository;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


//    @Autowired
//    @Qualifier("allele2Core")
//    private SolrClient allele2Core;



    @Autowired
    GeneRepository geneRepository;

    @Autowired
    AlleleRepository alleleRepository;

    @Autowired
    EnsemblGeneIdRepository ensemblGeneIdRepository;

    public Loader() {}

    @Override
    public void run(String... strings) throws Exception {

        geneRepository.deleteAll();
        alleleRepository.deleteAll();
        ensemblGeneIdRepository.deleteAll();

        Connection komp2Conn = komp2DataSource.getConnection();
        Connection diseaseConn = phenodigmDataSource.getConnection();

        loadGeneAndAllelesAndEnsemblGeneId();
        //loadEnsemblGeneIdsAndGene(komp2Conn);



    }
    public void loadGeneAndAllelesAndEnsemblGeneId() throws IOException, SolrServerException {

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
        BufferedReader in2 = in;
        String[] header = in.readLine().split("\t");
        for (int i = 0; i < header.length; i++){
            columns.put(header[i], i);
        }

        int geneCount = 0;
        int alleleCount = 0;
        Map<String, Gene> loadedGenes = new HashMap<>();

        String line = in.readLine();
        while (line != null) {
            //System.out.println(line);
            String[] array = line.split("\t", -1);
            if (array.length == 1) {
                continue;
            }

            if (! array[columns.get("gene_model_ids")].isEmpty()) {
                System.out.println(array[columns.get("gene_model_ids")]);
//                String[] ids = array[columns.get("gene_model_ids")].split("|");
//                for (int j = 0; j < ids.length; j++) {
//                    String thisId = ids[j];
//                    if (thisId.startsWith("ensembl_ids")) {
//                        String[] vals = thisId.split(":");
//                        String ensgId = vals[1];
//                        System.out.println("Found " + ensgId);
//                    }
//                }
            }

            line = in.readLine();
        }

        String line2 = in.readLine();
        while (line2 != null) {
            //System.out.println(line);
            String[] array = line2.split("\t", -1);
            if (array.length == 1) {
                continue;
            }



            line2 = in2.readLine();
        }


//        String line = in.readLine();
//        while (line != null) {
//            //System.out.println(line);
//            String[] array = line.split("\t", -1);
//            if (array.length == 1) {
//                continue;
//            }
//
//            if (! array[columns.get("latest_project_status")].isEmpty() && array[columns.get("type")].equals("Gene")) {
//                String mgiAcc = array[columns.get("mgi_accession_id")];
//                Gene gene = geneRepository.findByMgiAccessionId(mgiAcc);
//                if (gene == null) {
//                    logger.debug("Gene {} not found. Creating Gene with ", mgiAcc);
//                    gene = new Gene();
//                    gene.setMgiAccessionId(mgiAcc);
//
//                    geneCount++;
//                    if (geneCount % 5000 == 0) {
//                        logger.info("Loaded {} Gene nodes...", geneCount);
//                    }
//                }
//
//                if (! array[columns.get("marker_symbol")].isEmpty()) {
//                    gene.setMarkerSymbol(array[columns.get("marker_symbol")]);
//                }
//                if (! array[columns.get("feature_type")].isEmpty()) {
//                    gene.setMarkerType(array[columns.get("feature_type")]);
//                }
//                if (! array[columns.get("marker_name")].isEmpty()) {
//                    gene.setMarkerName(array[columns.get("marker_name")]);
//                }
//                if (! array[columns.get("synonym")].isEmpty()) {
//                    List<String> syms = Arrays.asList(StringUtils.split(array[columns.get("synonym")], "|"));
//                    Set<MarkerSynonym> mss = new HashSet<>();
//
//                    for (String sym : syms) {
//                        MarkerSynonym ms = new MarkerSynonym();
//                        ms.setMarkerSynonym(sym);
//                        ms.setGene(gene);
//                        mss.add(ms);
//                        mss.add(ms);
//                    }
//                    gene.setMarkerSynonyms(mss);
//                }
//                if (! array[columns.get("feature_chromosome")].isEmpty()) {
//                    gene.setChrId(array[columns.get("feature_chromosome")]);
//                    gene.setChrStart(array[columns.get("feature_coord_start")]);
//                    gene.setChrEnd(array[columns.get("feature_coord_end")]);
//                    gene.setChrStrand(array[columns.get("feature_strand")]);
//                }
//
//                if (! array[columns.get("gene_model_ids")].isEmpty()) {
//                    String[] ids = array[columns.get("gene_model_ids")].split("|");
//                    for(int j=0; j<ids.length; j++){
//                        String thisId = ids[j];
//                        if (thisId.startsWith("ensembl_ids")){
//                            String[] vals = thisId.split(":");
//                            String ensgId = vals[1];
//                            System.out.println("Found " + ensgId);
//                            EnsemblGeneId ensg = ensemblGeneIdRepository.findByEnsemblGeneId(ensgId);
//                            if (ensg == null){
//                                ensg = new EnsemblGeneId();
//                                ensg.setEnsemblGeneId(ensgId);
//                                ensg.setGene(gene);
//                                ensemblGeneIdRepository.save(ensg);
//
//                                if (gene.getEnsemblGeneIds() == null){
//                                    Set<EnsemblGeneId> eset = new HashSet<EnsemblGeneId>();
//                                    eset.add(ensg);
//                                    gene.setEnsemblGeneIds(eset);
//                                }
//                                else {
//                                    gene.getEnsemblGeneIds().add(ensg);
//                                }
//                            }
//                        }
//                    }
//                }
//                geneRepository.save(gene);
//                loadedGenes.put(mgiAcc, gene);
//            }
//
//            line = in.readLine();
//        }
//
//        String line2 = in2.readLine();
//        while (line2 != null) {
//            //System.out.println(line);
//            String[] array = line2.split("\t", -1);
//            if (array.length == 1) {
//                continue;
//            }
//
//            String mgiAcc = array[columns.get("mgi_accession_id")];
//
//            if (array[columns.get("type")].equals("Allele") && loadedGenes.containsKey(mgiAcc) && ! array[columns.get("allele_mgi_accession_id")].isEmpty()) {
//
//                Gene gene = loadedGenes.get(mgiAcc);
//
//                alleleCount++;
//                if (alleleCount % 5000 == 0){
//                    logger.info("Loaded {} Allele nodes....", alleleCount);
//                }
//
//                String alleleAcc = array[columns.get("allele_mgi_accession_id")];
//
//                Allele allele = new Allele();
//                allele.setAlleleMgiAccessionId(alleleAcc);
//                allele.setGene(gene);
//
//                if (!array[columns.get("allele_symbol")].isEmpty()) {
//                    allele.setAlleleSymbol(array[columns.get("allele_symbol")]);
//                }
//                if (!array[columns.get("allele_description")].isEmpty()) {
//                    allele.setAlleleDescription(array[columns.get("allele_description")]);
//                }
//                if (!array[columns.get("mutation_type")].isEmpty()) {
//                    allele.setMutationType(array[columns.get("mutation_type")]);
//                }
//                if (!array[columns.get("es_cell_status")].isEmpty()) {
//                    allele.setEsCellStatus(ES_CELL_STATUS_MAPPINGS.get(array[columns.get("es_cell_status")]));
//                }
//                if (!array[columns.get("mouse_status")].isEmpty()) {
//                    allele.setMouseStatus(MOUSE_STATUS_MAPPINGS.get(array[columns.get("mouse_status")]));
//                }
//                if (!array[columns.get("phenotype_status")].isEmpty()) {
//                    allele.setPhenotypeStatus(array[columns.get("phenotype_status")]);
//                }
//
//                if (gene.getAlleles() == null){
//                    Set<Allele> aset = new HashSet<Allele>();
//                    aset.add(allele);
//                    gene.setAlleles(aset);
//                }
//                else {
//                    gene.getAlleles().add(allele);
//                }
//
//                alleleRepository.save(allele);
//            }
//
//            line2 = in2.readLine();
//        }

        logger.info("Loaded {} Allele nodes and {} Gene nodes", alleleCount, geneCount);

        String job = "Gene, Allele and EnsemblGeneId nodes";
        loadTime(begin, System.currentTimeMillis(), job);

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