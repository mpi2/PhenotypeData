package uk.ac.ebi.phenotype.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by ckchen on 17/03/2017.
 */

@Component
public class Loader implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @NotNull
//    @Value("${allele2File}")
//    private String pathToAlleleFile;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    GeneRepository geneRepository;

    @Autowired
    EnsemblGeneIdRepository ensemblGeneIdRepository;

    public Loader() {}

    @Override
    public void run(String... strings) throws Exception {

        Connection connection = komp2DataSource.getConnection();
        try {
            String query = "SELECT acc, xref_acc FROM xref WHERE acc LIKE 'MGI:%' AND xref_acc LIKE 'ENSMUSG%'";
            PreparedStatement p = connection.prepareStatement(query);

            ResultSet r = p.executeQuery();
            int count = 0;
            while (r.next()) {


                count++;
                String ensgId = r.getString("xref_acc");
                EnsemblGeneId ensg = new EnsemblGeneId();
                ensg.setEnsemblGeneId(ensgId);

                String mgiAcc = r.getString("acc");

                System.out.println(ensgId + " --- " + mgiAcc);
                Gene gene = geneRepository.findByMgiAccessionId(mgiAcc);
                if (gene == null) {
                    logger.debug("Gene {} not found. Creating Gene with ", mgiAcc);
                    gene = new Gene();
                    gene.setMgiAccessionId(mgiAcc);
                    geneRepository.save(gene);
                }
                if (ensemblGeneIdRepository.findByEnsemblGeneId(gene.getMgiAccessionId())== null) {
                    ensg.setGene(gene);
                    ensemblGeneIdRepository.save(ensg);
                }

            }

            logger.info("Loaded " + count + " EnsemblGeneId nodes");

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
}