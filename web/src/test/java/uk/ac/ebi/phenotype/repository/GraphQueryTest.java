package uk.ac.ebi.phenotype.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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

    @Before
    public void before() {

        Gene g = new Gene();
        g.setMarkerName("TEST MARKER NXN");
        g.setMarkerSymbol("TEST_MARKER_NXN");

        // Add a disease model, gene and allele for testing
        Allele a = new Allele();
        a.setAlleleSymbol("TEST_ALLELE_SYMBOL");
        a.setGene(g);

        g.setAlleles(new HashSet<>(Arrays.asList(a)));

        DiseaseModel dm = new DiseaseModel();
        dm.setAllele(a);
        dm.setGene(g);
        dm.setDiseaseClasses("class1,class2");
        dm.setDiseaseId("TEST_DISEASE");

        System.out.println("Gene before saving is " + dm.getGene());
        System.out.println("Allele before saving is " + dm.getAllele());
        System.out.println("Disease model before saving is :" + dm);

        diseaseModelRepository.save(dm);
        System.out.println("Gene after saving is " + dm.getGene());
        System.out.println("Allele after saving is " + dm.getAllele());
        System.out.println("Disease model after saving is :" + dm);

        DiseaseModel dm2 = diseaseModelRepository.findByDiseaseId("TEST_DISEASE");
        System.out.println("DM after retriving is: " + dm2);
        System.out.println("DM Allele after retreiving is : " + dm2.getAllele());

        List<DiseaseModel> d = diseaseModelRepository.findByAlleleGeneMarkerSymbol("TEST_MARKER_NXN");
        System.out.println("Disease models found when looking by marker symbol "+ d.size());

    }

    @Test
    public void testTraverseDiseaseModelRepository() throws Exception {
        DiseaseModel testDiseaseModelFind = diseaseModelRepository.findByDiseaseId("TEST_DISEASE");

        assert (testDiseaseModelFind!= null);
        assert (testDiseaseModelFind.getAllele() != null);

        List<DiseaseModel> byAllele = diseaseModelRepository.findByAlleleAlleleSymbol("TEST_ALLELE_SYMBOL");
        assert (byAllele != null);
        System.out.println("allele is : " + byAllele.get(0).getAllele());

        List<DiseaseModel> d = diseaseModelRepository.findByAlleleGeneMarkerSymbol("TEST_MARKER_NXN");
        System.out.println("Disease models found "+ d.size());
        for (DiseaseModel d1 : d) {
            System.out.println(d1.getDiseaseTerm());
            System.out.println("allele:"+ d1.getAllele());
        }
        assert (d.size() > 0);
    }

    @Test
    public void graphQueryTest() throws Exception {

        List<String> markerSymbols = Arrays.asList("TEST_MARKER_NXN","Dst");
        for(String ms : markerSymbols) {
            List<Object> objs = geneRepository.findDiseaseModelByMarkerSymbol(ms);
            List<DiseaseModel> d = diseaseModelRepository.findByAlleleGeneMarkerSymbol(ms);
            System.out.println("Disease models found "+ d.size());
            for (DiseaseModel d1 : d) {
                System.out.println(d1.getDiseaseTerm());
                System.out.println("allele:"+ d1.getAllele());
            }
            if (true) continue;

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

                    if (dm.getAllele() != null) {
                        alleles.add(dm.getAllele());
                    }

                    if (dm.getMouseModel() != null) {
                        mms.add(dm.getMouseModel());
                    }
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