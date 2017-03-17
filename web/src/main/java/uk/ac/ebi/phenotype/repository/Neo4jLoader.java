package uk.ac.ebi.phenotype.repository;

import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import uk.ac.ebi.PhenotypeArchiveConfig;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by ckchen on 17/03/2017.
 */


@SpringBootApplication
@ComponentScan({ "uk.ac.ebi.phenotype" })
@EnableAutoConfiguration
@EnableNeo4jRepositories(basePackages = "uk.ac.ebi.phenotype.repository")
public class Neo4jLoader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("komp2DataSource")
    @NotNull
    private DataSource komp2DataSource;

    @NotNull
    @Value("${allele2File}")
    private String pathToAlleleFile;

    Integer alleleDocCount;
    Map<String, Integer> columns = new HashMap<>();

    @Autowired
    GeneRepository geneRepository;;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Neo4jLoader.class, args);
    }

    @Bean
    CommandLineRunner loadEnsemblGeneId(EnsemblGeneIdRepository ensemblGeneIdRepository) {
        return args -> {


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
                        // geneRepository.save(gene);
                    }
                    ensg.setGene(gene);
                    // ensemblGeneIdRepository.save(ensg);

                }

                logger.info("Loaded " +  count + " EnsemblGeneId nodes");

            }
            catch(Exception e){
                logger.error(e.getMessage());
            }

        };
    }



//    public RunStatus run() throws IOException, SolrServerException, SQLException {
//
//        loadEnsemblGeneId();

//        RunStatus runStatus = new RunStatus();
//
//        long start = System.currentTimeMillis();
//        BufferedReader in = new BufferedReader(new FileReader(new File(pathToAlleleFile)));
//        String[] header = in.readLine().split("\t");
//        for (int i = 0; i < header.length; i++){
//            columns.put(header[i], i);
//        }
//
//        int index = 0 ;
//        String line = in.readLine();
//
//        while (line != null){
//
//            String[] array = line.split("\t", -1);
//            index ++;
//
//
//
//            line = in.readLine();
//
//
//
//        }
//
//
//        return runStatus;
//
//    }


    public RunStatus validateBuild() {

        RunStatus runStatus = new RunStatus();
//        Long actualSolrDocumentCount = getImitsDocumentCount(allele2Core);

//        if (actualSolrDocumentCount < alleleDocCount) {
//            runStatus.addError("Expected " + alleleDocCount + " documents. Actual count: " + actualSolrDocumentCount + ".");
//        }

        return runStatus;
    }




}