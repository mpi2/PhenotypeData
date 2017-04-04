package uk.ac.ebi.phenotype.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.phenotype.repository.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ckchen on 17/03/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {Neo4jConfig.class})
@EnableNeo4jRepositories(basePackages = "uk.ac.ebi.phenotype.repository")
@Transactional
public class GraphQueryTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    DiseaseGeneRepository diseaseGeneRepository;

    @Autowired
    DiseaseModelRepository diseaseModelRepository;

    @Autowired
    MouseModelRepository mouseModelRepository;


    @Test
    public void graphQueryTest() throws Exception {

        List<String> markerSymbols = Arrays.asList("Nxn","Dst");
        for(String ms : markerSymbols) {
            List<Object> objs = geneRepository.findDiseaseModelByMarkerSymbol(ms);
            for (Object obj : objs) {
                String className = obj.getClass().getSimpleName();

                if (className.equals("Gene")) {
                    Gene g = (Gene) obj;
                    System.out.println(g.getMarkerSymbol());
                    System.out.println(g.getMgiAccessionId());
                }

                Set<MouseModel> mms = new HashSet<>();
                Set<Allele> alleles = new HashSet<>();
                Set<DiseaseModel> dms = new HashSet<>();

                if (className.equals("DiseaseModel")) {
                    DiseaseModel dm = (DiseaseModel) obj;
                    dms.add(dm);

                    alleles.add(dm.getAllele());

                    mms.add(dm.getMouseModel());
                }

                for (MouseModel mm : mms) {
                    System.out.println("MouseModel: " + mm.toString());
                }

                for (Allele allele : alleles) {
                    System.out.println("Allele: " + allele.toString());
                }

                for (DiseaseModel dm : dms) {
                    System.out.println("DiseaseModel: " + dm.toString());
                }

            }
        }
    }

}