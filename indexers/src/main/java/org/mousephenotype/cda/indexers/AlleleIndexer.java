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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.indexers.beans.DiseaseBean;
import org.mousephenotype.cda.indexers.beans.SangerAlleleBean;
import org.mousephenotype.cda.indexers.beans.SangerGeneBean;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Index the allele core from the sanger allele2 core
 *
 * @author Matt
 * @author jmason
 *
 */
@EnableAutoConfiguration
public class AlleleIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@NotNull
    @Autowired
    @Qualifier("allele2Core")
    private SolrClient allele2Core;

    @Value("${human2mouseFilename}")
    private String human2mouseFilename;


    public static final int PHENODIGM_BATCH_SIZE = 50000;
    private static Connection connection;
    private static final int BATCH_SIZE = 2500;


    // Map gene MGI ID to sanger allele bean
    private static Map<String, List<SangerAlleleBean>> statusLookup = new HashMap<>();

    // Map gene MGI ID to human symbols
    private static Map<String, Set<String>> humanSymbolLookup = new HashMap<>();

    // Map gene MGI ID to disease bean
    private static Map<String, List<DiseaseBean>> diseaseLookup = new HashMap<>();

    // Set of MGI IDs that have legacy projects
    private static Map<String, Integer> legacyProjectLookup = new HashMap<>();

    private static final Map<String, String> ES_CELL_STATUS_MAPPINGS = new HashMap<>();

    // Map MGI accession id to longest Uniprot accession
    private static Map<String, UniprotCanonical> mgi2UniprotLookup = new HashMap<>();

	// MGI gene id to Ensembl gene id mapping
	private static Map<String, List<String>> mgiGeneId2EnsemblGeneId = new HashMap<>();

	static {
        ES_CELL_STATUS_MAPPINGS.put("No ES Cell Production", "Not Assigned for ES Cell Production");
        ES_CELL_STATUS_MAPPINGS.put("ES Cell Production in Progress", "Assigned for ES Cell Production");
        ES_CELL_STATUS_MAPPINGS.put("ES Cell Targeting Confirmed", "ES Cells Produced");
    }

    private static final Map<String, String> MOUSE_STATUS_MAPPINGS = new HashMap<>();

    static {
        MOUSE_STATUS_MAPPINGS.put("Chimeras obtained", "Assigned for Mouse Production and Phenotyping");
        MOUSE_STATUS_MAPPINGS.put("Micro-injection in progress", "Assigned for Mouse Production and Phenotyping");
        MOUSE_STATUS_MAPPINGS.put("Cre Excision Started", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Rederivation Complete", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Rederivation Started", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Genotype confirmed", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Cre Excision Complete", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Phenotype Attempt Registered", "Mice Produced");
    }

    @NotNull
    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @NotNull
    @Autowired
    @Qualifier("uniprotDataSource")
    DataSource uniprotDataSource;

    @NotNull
	@Autowired
	@Qualifier("phenodigmCore")
	private SolrClient phenodigmCore;

    @NotNull
	@Autowired
    @Qualifier("alleleCore")
    private SolrClient alleleCore;


    public AlleleIndexer() {

    }


    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(alleleCore);
    }


    @Override
    public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {
        int count = 0;
        long rows = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

        try {
            connection = komp2DataSource.getConnection();

            // this query would only pick up lines that imits have phenotype / production status info about
            SolrQuery query = new SolrQuery("latest_project_status:*");
            query.addFilterQuery("feature_type:* AND type:(gene OR Gene)");

            query.setRows(BATCH_SIZE);

            populateStatusLookup();
            logger.info(" Added {} total status lookup beans", statusLookup.size());

            populateHumanSymbolLookup();
            logger.info(" Added {} total human symbol lookup beans", humanSymbolLookup.size());

            populateDiseaseLookup();
            logger.info(" Added {} total disease lookup beans", diseaseLookup.size());

            populateLegacyLookup(runStatus);
            logger.info(" Added {} total legacy project lookup beans", legacyProjectLookup.size());

            populateMgiGeneId2EnsemblGeneId();
            logger.info(" Added {} total Ensembl id to MGI gene id lookup beans", mgiGeneId2EnsemblGeneId.size());

            // MGI gene id to Uniprot accession mapping
            populateMgi2UniprotLookup();
            logger.info(" Added {} MGI to UNIPROT lookup beans", mgi2UniprotLookup.size());

            alleleCore.deleteByQuery("*:*");
            alleleCore.commit();

            while (count <= rows) {
                query.setStart(count);
                QueryResponse response = allele2Core.query(query);
                rows = response.getResults().getNumFound();
                List<SangerGeneBean> sangerGenes = response.getBeans(SangerGeneBean.class);

                // Convert to Allele DTOs
                Map<String, AlleleDTO> alleles = convertSangerGeneBeans(sangerGenes);

                // Look up the marker synonyms
                lookupMarkerSynonyms(alleles, runStatus);

                // Look up ensembl id to MGI gene id mapping
                lookupMgiGeneId2EnsemblGeneId(alleles);

                // Look up the human mouse symbols
                lookupHumanMouseSymbols(alleles);

                // Look up the ES cell status
                lookupEsCellStatus(alleles);

                // Look up the disease data
                lookupDiseaseData(alleles);

                // Look up gene to Uniprot mapping
                lookupUniprotAcc(alleles);


                // Now index the alleles
                documentCount += alleles.size();
                indexAlleles(alleles);

                count += BATCH_SIZE;
            }

            alleleCore.commit();

        } catch (SQLException | SolrServerException | IOException | ClassNotFoundException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }

    Map<String, List<String>> populateMgiGeneId2EnsemblGeneId() {

    	String query = "SELECT acc, xref_acc FROM xref WHERE db_id=3 AND xref_db_id=18 AND xref_acc like 'ENS%'";

    	try (PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
            	String mgiGeneId = resultSet.getString("acc");
            	String ensemblGeneId = resultSet.getString("xref_acc");

            	if ( ! mgiGeneId2EnsemblGeneId.containsKey(mgiGeneId) ){
            		mgiGeneId2EnsemblGeneId.put(mgiGeneId, new ArrayList<>());
            	}
            	mgiGeneId2EnsemblGeneId.get(mgiGeneId).add(ensemblGeneId);
            }
    	} catch (Exception e) {
            e.printStackTrace();
        }

    	return mgiGeneId2EnsemblGeneId;
    }


    private void populateMgi2UniprotLookup() throws IOException, SQLException, ClassNotFoundException{

        //-- w/o haplotypes
        String queryString = "WITH human AS " +
                "(SELECT gce.accession, gce.entry_type, gce.name mod_id, upper(eg.gene_name) gene_name, gce.LENGTH, eg.ENSG_ID " +
                "FROM sptr.GENE_CENTRIC_ENTRY gce, ENSEMBL_GENE eg " +
                "WHERE gce.RELEASE IN " +
                "   (SELECT max(release) release " +
                "   FROM sptr.GENE_CENTRIC_ENTRY " +
                "   WHERE tax_id = 9606 AND IS_CANONICAL = 1) " +
                "   AND gce.TAX_ID = 9606 " +
                "   AND gce.ACCESSION IN " +
                "       (SELECT ACCESSION " +
                "       FROM sptr.GENE_CENTRIC_ENTRY " +
                "       WHERE RELEASE IN " +
                "           (SELECT max(release) release FROM sptr.GENE_CENTRIC_ENTRY WHERE tax_id = 9606 AND IS_CANONICAL = 1) " +
                "       AND tax_id = 9606 " +
                "       AND IS_CANONICAL = 1) " +
                "       AND gce.GENE_NAME_TYPE = 1 " +
                "       AND gce.name = eg.mod_id " +
                "       AND gce.tax_id = eg.tax_id), " +
                "mouse AS " +
                "(SELECT gce.accession, gce.entry_type, gce.name mod_id, upper(eg.gene_name) gene_name, gce.LENGTH, eg.ENSG_ID " +
                "FROM sptr.GENE_CENTRIC_ENTRY gce, ENSEMBL_GENE eg " +
                "WHERE gce.RELEASE IN " +
                "   (SELECT max(release) release " +
                "   FROM sptr.GENE_CENTRIC_ENTRY " +
                "   WHERE tax_id = 9606 " +
                "   AND IS_CANONICAL = 1) " +
                "   AND gce.TAX_ID = 10090 " +
                "   AND gce.ACCESSION IN " +
                "       (SELECT ACCESSION FROM sptr.GENE_CENTRIC_ENTRY " +
                "       WHERE RELEASE IN " +
                "           (SELECT max(release) release FROM sptr.GENE_CENTRIC_ENTRY where tax_id = 9606 and IS_CANONICAL = 1) " +
                "   AND tax_id = 10090 " +
                "   AND IS_CANONICAL = 1) " +
                "   AND gce.GENE_NAME_TYPE = 1 " +
                "   AND gce.name = eg.mod_id AND gce.tax_id = eg.tax_id) " +
                "SELECT DISTINCT h.accession h_acc, m.accession m_acc, h.gene_name symbol " +
                "FROM human h, mouse m " +
                "WHERE h.gene_name = m.gene_name " +
                "ORDER BY h.gene_name";

        Connection connUniprot = uniprotDataSource.getConnection();

	    // take all isoforms of gene product mapped to uniprot (swissprot or trembl)
	    try (PreparedStatement p = connUniprot.prepareStatement(queryString)) {
            ResultSet resultSet = p.executeQuery();

            UniprotCanonical uc = new UniprotCanonical();

            while (resultSet.next()) {

                String geneLabel = resultSet.getString("symbol");

                if ( ! mgi2UniprotLookup.containsKey(geneLabel) ) {
                    mgi2UniprotLookup.put(geneLabel, new UniprotCanonical());
                }
                uc = mgi2UniprotLookup.get(geneLabel);

                uc.setHumanCanonicalProteinAcc(resultSet.getString("h_acc"));
                uc.setMouseCanonicalProteinAcc(resultSet.getString("m_acc"));

            }
	    }
	    catch(Exception e) {
            e.printStackTrace();
	    }
	}


    class UniprotCanonical {

        String human_canonical_protein_acc;
        String mouse_canonical_protein_acc;


        public String getHumanCanonicalProteinAcc() {
            return human_canonical_protein_acc;
        }

        public void setHumanCanonicalProteinAcc(String human_canonical_protein_acc) {
            this.human_canonical_protein_acc = human_canonical_protein_acc;
        }

        public String getMouseCanonicalProteinAcc() {
            return mouse_canonical_protein_acc;
        }

        public void setMouseCanonicalProteinAcc(String mouse_canonical_protein_acc) {
            this.mouse_canonical_protein_acc = mouse_canonical_protein_acc;
        }

    }


    private void populateLegacyLookup(RunStatus runStatus) throws SolrServerException {

    	String query = "SELECT DISTINCT external_db_id, gf_acc FROM phenotype_call_summary WHERE p_value < 0.0001 AND (external_db_id = 12 OR external_db_id = 20)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                legacyProjectLookup.put(rs.getString("gf_acc"), 1);
            }
        } catch (SQLException e) {

            runStatus.addError(" SQL Exception looking up legacy projects: " + e.getMessage());
        }
    }

    private void populateStatusLookup() throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery("*:*");
        query.setRows(Integer.MAX_VALUE);
        query.addFilterQuery("type:Allele");

        QueryResponse response = allele2Core.query(query);
        List<SangerAlleleBean> sangerAlleles = response.getBeans(SangerAlleleBean.class);
        for (SangerAlleleBean allele : sangerAlleles) {
            if ( ! statusLookup.containsKey(allele.getMgiAccessionId())) {
                statusLookup.put(allele.getMgiAccessionId(), new ArrayList<SangerAlleleBean>());
            }
            statusLookup.get(allele.getMgiAccessionId()).add(allele);
        }
    }


    private void populateHumanSymbolLookup() throws IOException {

        File file = new File(human2mouseFilename);
        List<String> lines = FileUtils.readLines(file, "UTF-8");

        for (String line : lines) {
            String[] pieces = line.trim().split("\t");

            if (pieces.length < 6) {
                continue;
            }

            String humanSymbol = pieces[0];
            String mgiId = pieces[5].trim();
            if ( ! mgiId.startsWith("MGI:")) {
                continue;
            }

            if ( ! humanSymbolLookup.containsKey(mgiId)) {
                humanSymbolLookup.put(mgiId, new HashSet<String>());
            }

            (humanSymbolLookup.get(mgiId)).add(humanSymbol);
        }
    }

    private void populateDiseaseLookup() throws SolrServerException, IOException {

        int docsRetrieved = 0;
        int numDocs = getDiseaseDocCount();

        // Fields in the solr core to bring back
        String fields = StringUtils.join(Arrays.asList(DiseaseBean.DISEASE_ID,
                                                       DiseaseBean.MGI_ACCESSION_ID,
                                                       DiseaseBean.DISEASE_SOURCE,
                                                       DiseaseBean.DISEASE_TERM,
                                                       DiseaseBean.DISEASE_ALTS,
                                                       DiseaseBean.DISEASE_CLASSES,
                                                       DiseaseBean.HUMAN_CURATED,
                                                       DiseaseBean.MOUSE_CURATED,
                                                       DiseaseBean.MGI_PREDICTED,
                                                       DiseaseBean.IMPC_PREDICTED,
                                                       DiseaseBean.MGI_PREDICTED_KNOWN_GENE,
                                                       DiseaseBean.IMPC_PREDICTED_KNOWN_GENE,
                                                       DiseaseBean.MGI_NOVEL_PREDICTED_IN_LOCUS,
                                                       DiseaseBean.IMPC_NOVEL_PREDICTED_IN_LOCUS), ",");

		// The solrcloud instance cannot give us all results back at once,
        // we must batch up the calls and build it up piece at a time
        while (docsRetrieved < numDocs + PHENODIGM_BATCH_SIZE) {

            SolrQuery query = new SolrQuery("*:*");
            query.addFilterQuery("type:disease_gene_summary");
            query.setFields(fields);
            query.setStart(docsRetrieved);
            query.setRows(PHENODIGM_BATCH_SIZE);
            query.setSort(DiseaseBean.DISEASE_ID, SolrQuery.ORDER.asc);

            QueryResponse response = phenodigmCore.query(query);
            if (response == null) {
                throw new SolrServerException("Response from phendigm core is null. Chcek phenodigm core is up with query: " + query);
            }
            List<DiseaseBean> diseases = response.getBeans(DiseaseBean.class);
            for (DiseaseBean disease : diseases) {
                if ( ! diseaseLookup.containsKey(disease.getMgiAccessionId())) {
                    diseaseLookup.put(disease.getMgiAccessionId(), new ArrayList<DiseaseBean>());
                }
                diseaseLookup.get(disease.getMgiAccessionId()).add(disease);
            }

            docsRetrieved += PHENODIGM_BATCH_SIZE;
        }
    }

    private int getDiseaseDocCount() throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery("*:*");
        query.setRows(0);
        query.addFilterQuery("type:disease_gene_summary");

        QueryResponse response = phenodigmCore.query(query);
        return (int) response.getResults().getNumFound();
    }

    private Map<String, AlleleDTO> convertSangerGeneBeans(List<SangerGeneBean> beans) {

        Map<String, AlleleDTO> map = new HashMap<>(beans.size());

        for (SangerGeneBean bean : beans) {
            String id = bean.getMgiAccessionId();
            AlleleDTO dto = new AlleleDTO();

            // Copy the fields
            dto.setMgiAccessionId(id);
            dto.setMarkerType(bean.getFeatureType());
            dto.setMarkerSymbol(bean.getMarkerSymbol());
            dto.setMarkerSymbolLowercase(bean.getMarkerSymbol());
            dto.setGeneLatestEsCellStatus(bean.getLatestEsCellStatus());
            dto.setGeneLatestMouseStatus(bean.getLatestMouseStatus());
            dto.setImitsPhenotypeStarted(bean.getLatestPhenotypeStarted());
            dto.setImitsPhenotypeComplete(bean.getLatestPhenotypeComplete());
            dto.setLatestPhenotypeStatus(bean.getLatestPhenotypeStatus());
            dto.setLatestProductionCentre(bean.getLatestProductionCentre());
            dto.setLatestPhenotypingCentre(bean.getLatestPhenotypingCentre());
            dto.setLatestProjectStatus(bean.getLatestProjectStatus());
            dto.setAlleleAccessionIds(bean.getMgiAlleleAccessionIds());

	        if ( bean.getChrName() != null && ! bean.getChrName().equals("null") ) {
		        dto.setChrName(bean.getChrName());
	        }

	        if ( bean.getChrStrand() != null && ! bean.getChrStrand().equals("null") ) {
		        dto.setChrStrand(bean.getChrStrand());
	        }

            if (bean.getChrStart() != null) {
                dto.setChrStart(bean.getChrStart());
            }

            if (bean.getChrEnd() != null) {
                dto.setChrEnd(bean.getChrEnd());
            }

            String latestEsStatus = ES_CELL_STATUS_MAPPINGS.containsKey(bean.getLatestEsCellStatus()) ? ES_CELL_STATUS_MAPPINGS.get(bean.getLatestEsCellStatus()) : bean.getLatestEsCellStatus();
            dto.setLatestProductionStatus(latestEsStatus);
            dto.setLatestEsCellStatus(latestEsStatus);

            if (StringUtils.isNotEmpty(bean.getLatestMouseStatus())) {
                String latestMouseStatus = MOUSE_STATUS_MAPPINGS.containsKey(bean.getLatestMouseStatus()) ? MOUSE_STATUS_MAPPINGS.get(bean.getLatestMouseStatus()) : bean.getLatestMouseStatus();
                dto.setLatestProductionStatus(latestMouseStatus);
                dto.setLatestMouseStatus(latestMouseStatus);
            }

            if (legacyProjectLookup.containsKey(bean.getMgiAccessionId())) {
                dto.setLegacyPhenotypeStatus(1);
            }

            // Do the additional mappings
            dto.setDataType(AlleleDTO.ALLELE_DATA_TYPE);

            map.put(id, dto);
        }

        return map;
    }

    private void lookupMarkerSynonyms(Map<String, AlleleDTO> alleles, RunStatus runStatus) {
        // Build the lookup string
        String lookup = buildIdQuery(alleles.keySet());

        String query = "select gf.acc as id, s.symbol as marker_synonym, gf.name as marker_name "
                + "from genomic_feature gf left join synonym s "
                + "on gf.acc=s.acc "
                + "where gf.acc IN (" + lookup + ")";

        try {
            logger.debug(" Starting marker synonym lookup");
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                AlleleDTO allele = alleles.get(id);
                if (allele.getMarkerSynonym() == null) {
                    allele.setMarkerSynonym(new ArrayList<>());
                }
                if (allele.getMarkerSynonymLowercase() == null) {
                    allele.setMarkerSynonymLowercase(new ArrayList<>());
                }
                allele.getMarkerSynonym().add(rs.getString("marker_synonym"));
                allele.getMarkerSynonymLowercase().add(rs.getString("marker_synonym"));
                allele.setMarkerName(rs.getString("marker_name"));
            }

            logger.debug(" Finished marker synonym lookup");

        } catch (SQLException sqle) {

            runStatus.addError(" SQL Exception looking up marker symbols: " + sqle.getMessage());
        }
    }

    private void lookupHumanMouseSymbols(Map<String, AlleleDTO> alleles) {

        for (String id : alleles.keySet()) {
            AlleleDTO dto = alleles.get(id);

            if (humanSymbolLookup.containsKey(id)) {
                dto.setHumanGeneSymbol(new ArrayList<>(humanSymbolLookup.get(id)));
            }
        }

        logger.debug(" Finished human marker symbol lookup");
    }

    private void lookupMgiGeneId2EnsemblGeneId(Map<String, AlleleDTO> alleles){
    	for (String id : alleles.keySet()) {
            AlleleDTO dto = alleles.get(id);

            if (mgiGeneId2EnsemblGeneId.containsKey(id)) {
                dto.setEnsemblGeneIds(new ArrayList<>(mgiGeneId2EnsemblGeneId.get(id)));
            }
        }

        logger.debug(" Finished MGI gene id to Ensembl gene id lookup");
    }

    private String buildIdQuery(Collection<String> ids) {

        StringBuilder lookup = new StringBuilder();
        int i = 0;
        for (String id : ids) {
            if (i > 0) {
                lookup.append(",");
            }
            lookup.append("'").append(id).append("'");
            i ++;
        }

        return lookup.toString();
    }

    private void lookupEsCellStatus(Map<String, AlleleDTO> alleles) {

        for (String id : alleles.keySet()) {
            AlleleDTO dto = alleles.get(id);

            if ( ! statusLookup.containsKey(id)) {
                continue;
            }

            for (SangerAlleleBean sab : statusLookup.get(id)) {

                dto.getAlleleName().add(sab.getAlleleName());
                dto.getPhenotypeStatus().add(sab.getPhenotypeStatus());
                dto.getProductionCentre().add(sab.getProductionCentre());
                dto.getPhenotypingCentre().add(sab.getPhenotypingCentre());

                String esCellStat = ES_CELL_STATUS_MAPPINGS.containsKey(sab.getEsCellStatus()) ? ES_CELL_STATUS_MAPPINGS.get(sab.getEsCellStatus()) : sab.getEsCellStatus();
                dto.getEsCellStatus().add(esCellStat);

                if (StringUtils.isNotEmpty(sab.getMouseStatus())) {
                    String mouseStatus = MOUSE_STATUS_MAPPINGS.containsKey(sab.getMouseStatus()) ? MOUSE_STATUS_MAPPINGS.get(sab.getMouseStatus()) : sab.getMouseStatus();
                    dto.getMouseStatus().add(mouseStatus);
                } else {
                    dto.getMouseStatus().add("");
                }
            }
        }

        logger.debug(" Finished ES cell status lookup");
    }

    private void lookupDiseaseData(Map<String, AlleleDTO> alleles) {

        logger.debug(" Starting disease data lookup");
        for (String id : alleles.keySet()) {

            AlleleDTO dto = alleles.get(id);

            if ( ! diseaseLookup.containsKey(id)) {
                continue;
            }

            for (DiseaseBean db : diseaseLookup.get(id)) {
                dto.getDiseaseId().add(db.getDiseaseId());
                dto.getDiseaseSource().add(db.getDiseaseSource());
                dto.getDiseaseTerm().add(db.getDiseaseTerm());
                if (db.getDiseaseAlts() != null) {
                    dto.getDiseaseAlts().addAll(db.getDiseaseAlts());
                }
                if (db.getDiseaseClasses() != null) {
                    dto.getDiseaseClasses().addAll(db.getDiseaseClasses());
                }
                dto.getHumanCurated().add(db.isHumanCurated());
                dto.getMouseCurated().add(db.isMouseCurated());
                dto.getMgiPredicted().add(db.isMgiPredicted());
                dto.getImpcPredicted().add(db.isImpcPredicted());
                dto.getMgiPredictedKnownGene().add(db.isMgiPredictedKnownGene());
                dto.getImpcPredictedKnownGene().add(db.isImpcPredictedKnownGene());
                dto.getMgiNovelPredictedInLocus().add(db.isMgiNovelPredictedInLocus());
                dto.getImpcNovelPredictedInLocus().add(db.isImpcNovelPredictedInLocus());
            }
        }

        logger.debug(" Finished disease data lookup");
    }

    private void lookupUniprotAcc(Map<String, AlleleDTO> alleles) {
    	 logger.debug(" Starting Uniprot Acc lookup");
         for (String id : alleles.keySet()) {

             AlleleDTO dto = alleles.get(id);

             String gSymbol = dto.getMarkerSymbol().toUpperCase();

             if ( mgi2UniprotLookup.containsKey(gSymbol)  ){
                 dto.setUuniprotHumanCanonicalAcc(mgi2UniprotLookup.get(gSymbol).getHumanCanonicalProteinAcc());
                 dto.setUuniprotMouseCanonicalAcc(mgi2UniprotLookup.get(gSymbol).getMouseCanonicalProteinAcc());
             }

         }
    }

    private void indexAlleles(Map<String, AlleleDTO> alleles) throws SolrServerException, IOException {
        alleleCore.addBeans(alleles.values(), 60000);
    }

    public static void main(String[] args) throws IndexerException {
        SpringApplication.run(AlleleIndexer.class, args);
    }
}