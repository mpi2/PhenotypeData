package org.mousephenotype.cda.neo4j.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.TestConfig;
import org.mousephenotype.cda.neo4j.entity.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ckchen on 17/03/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableNeo4jRepositories(basePackages = "org.mousephenotype.cda.neo4j.repository")
@Transactional
@ContextConfiguration(classes = {TestConfig.class, Neo4jConfig.class})
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

    //@Before
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

        List<DiseaseModel> d = diseaseModelRepository.findByAllele_Gene_MarkerSymbol("TEST_MARKER_NXN");
        System.out.println("Disease models found when looking by marker symbol "+ d.size());

    }

    //@Test
    public void testTraverseDiseaseModelRepository() throws Exception {
        DiseaseModel testDiseaseModelFind = diseaseModelRepository.findByDiseaseId("TEST_DISEASE");

        assert (testDiseaseModelFind!= null);
        assert (testDiseaseModelFind.getAllele() != null);

        List<DiseaseModel> byAllele = diseaseModelRepository.findByAlleleAlleleSymbol("TEST_ALLELE_SYMBOL");
        assert (byAllele != null);
        System.out.println("allele is : " + byAllele.get(0).getAllele());

        List<DiseaseModel> d = diseaseModelRepository.findByAllele_Gene_MarkerSymbol("TEST_MARKER_NXN");
        System.out.println("Disease models found "+ d.size());
        for (DiseaseModel d1 : d) {
            System.out.println(d1.getDiseaseTerm());
            System.out.println("allele:"+ d1.getAllele());
        }
        assert (d.size() > 0);
    }

    //@Test
    public void ensemblGraphQueryTest() throws Exception {
        List<String> ensgIds = Arrays.asList("ENSMUSG00000025903","ENSMUSG00000062588");
        for(String ensgId : ensgIds) {
            List<Object> objs = ensemblGeneIdRepository.findDataByEnsemblGeneId(ensgId);
//            List<DiseaseModel> d = diseaseModelRepository.findByAllele_Gene_MarkerSymbol(ms);
//            System.out.println("Disease models found "+ d.size());
//            for (DiseaseModel d1 : d) {
//                System.out.println(d1.getDiseaseTerm());
//                System.out.println("allele:"+ d1.getAllele());
//            }
//            if (true) continue;

            Set<EnsemblGeneId> engs = new HashSet<>();
            Set<HumanGeneSymbol> hgs = new HashSet<>();
            Set<MarkerSynonym> ms = new HashSet<>();
            Set<MouseModel> mms = new HashSet<>();
            Set<Allele> alleles = new HashSet<>();
            Set<DiseaseModel> dms = new HashSet<>();
            Set<Mp> mps = new HashSet<>();
            Set<OntoSynonym> mpsyns = new HashSet<>();
            Set<Hp> hps = new HashSet<>();

            for (Object obj : objs) {
                String className = obj.getClass().getSimpleName();


                if (className.equals("Gene")) {
                    Gene g = (Gene) obj;
                    System.out.println(g.getMarkerSymbol());
                    System.out.println(g.getMgiAccessionId());
                }

                if (className.equals("EnsemblGeneId")) {
                    EnsemblGeneId ensg = (EnsemblGeneId) obj;
                    engs.add(ensg);
                }


                if (className.equals("MarkerSynonym")) {
                    MarkerSynonym m = (MarkerSynonym) obj;
                    ms.add(m);
                }


                if (className.equals("HumanGeneSymbol")) {
                    HumanGeneSymbol hg = (HumanGeneSymbol) obj;
                    hgs.add(hg);
                }


                if (className.equals("DiseaseModel")) {
                    DiseaseModel dm = (DiseaseModel) obj;
                    dms.add(dm);
                }


                if (className.equals("MouseModel")) {
                    MouseModel mm = (MouseModel) obj;
                    mms.add(mm);
                }


                if (className.equals("Allele")) {
                    Allele allele = (Allele) obj;
                    alleles.add(allele);
                }


                if (className.equals("Mp")) {
                    Mp mp = (Mp) obj;
                    mps.add(mp);
                }


                if (className.equals("OntoSynonym")) {
                    OntoSynonym mpsyn = (OntoSynonym) obj;
                    mpsyns.add(mpsyn);
                }


                if (className.equals("Hp")) {
                    Hp hp = (Hp) obj;
                    hps.add(hp);
                }
            }

            for (EnsemblGeneId eng : engs) {
                System.out.println("EnsemblGeneId: " + eng.toString());
            }
            for (MarkerSynonym m : ms) {
                System.out.println("MarkerSynonym: " + m.toString());
            }
            for (HumanGeneSymbol hg : hgs) {
                System.out.println("HumanGeneSymbol: " + hg.toString());
            }
            for (DiseaseModel dm : dms) {
                System.out.println("DiseaseModel: " + dm.toString());
            }
            for (MouseModel mm : mms) {
                System.out.println("MouseModel: " + mm.toString());
            }
            for (Allele allele : alleles) {
                System.out.println("Allele: " + allele.toString());
            }
            for (Mp mp : mps) {
                System.out.println("Mp: " + mp.toString());
            }
            for (OntoSynonym mpsyn : mpsyns) {
                System.out.println("OntoSynonym: " + mpsyn.toString());
            }
            for (Hp hp : hps) {
                System.out.println("Hp: " + hp.toString());
            }

        }

    }

    @Test
    public void mpGraphQueryTest() throws Exception {
        List<String> hgsyms = Arrays.asList("NXN","DST");
//        List<String> mpIds = Arrays.asList("MP:0005385");
//        List<String> mpIds = Arrays.asList("cardiovascular system phenotype");
       // List<String> mpIds = Arrays.asList("glucose");
        for(String hgsym : hgsyms) {
//        for(String mpId : mpIds) {
            List<Object> objs = humanGeneSymbolRepository.findDataByHumanGeneSymbol(hgsym);
            //        List<Object> objs = mpRepository.findChildrenMpsByMpTerm(mpId, 3);
//            List<Object> objs = mpRepository.findDataByMpTerm(mpId);
            System.out.println("found " + objs.size());
            System.out.println(objs.get(0));
      //      List<Object> objs = mpRepository.findDataByMpTermWithTopLevel(mpId);
       //     List<Object> objs = mpRepository.findDataByMpIdChrRange(mpId, "1", 12345678, 34567890);
//            List<Object> objs = mpRepository.findDataByMpTerm(mpId);
//            List<DiseaseModel> d = diseaseModelRepository.findByAllele_Gene_MarkerSymbol(ms);
//            System.out.println("Disease models found "+ d.size());
//            for (DiseaseModel d1 : d) {
//                System.out.println(d1.getDiseaseTerm());
//                System.out.println("allele:"+ d1.getAllele());
//            }
//            if (true) continue;

            Set<EnsemblGeneId> engs = new HashSet<>();
            Set<HumanGeneSymbol> hgs = new HashSet<>();
            Set<MarkerSynonym> ms = new HashSet<>();
            Set<MouseModel> mms = new HashSet<>();
            Set<Allele> alleles = new HashSet<>();
            Set<DiseaseModel> dms = new HashSet<>();
            Set<Mp> mps = new HashSet<>();
            Set<OntoSynonym> mpsyns = new HashSet<>();
            Set<Hp> hps = new HashSet<>();

            for (Object obj : objs) {
                String className = obj.getClass().getSimpleName();


                if (className.equals("Gene")) {
                    Gene g = (Gene) obj;
                    System.out.println(g.getMarkerSymbol());
                    System.out.println(g.getMgiAccessionId());
                }

                if (className.equals("EnsemblGeneId")) {
                    EnsemblGeneId ensg = (EnsemblGeneId) obj;
                    engs.add(ensg);
                }


                if (className.equals("MarkerSynonym")) {
                    MarkerSynonym m = (MarkerSynonym) obj;
                    ms.add(m);
                }


                if (className.equals("HumanGeneSymbol")) {
                    HumanGeneSymbol hg = (HumanGeneSymbol) obj;
                    hgs.add(hg);
                }


                if (className.equals("DiseaseModel")) {
                    DiseaseModel dm = (DiseaseModel) obj;
                    dms.add(dm);
                }


                if (className.equals("MouseModel")) {
                    MouseModel mm = (MouseModel) obj;
                    mms.add(mm);
                }


                if (className.equals("Allele")) {
                    Allele allele = (Allele) obj;
                    alleles.add(allele);
                }


                if (className.equals("Mp")) {
                    Mp mp = (Mp) obj;
                    mps.add(mp);
                }


                if (className.equals("OntoSynonym")) {
                    OntoSynonym mpsyn = (OntoSynonym) obj;
                    mpsyns.add(mpsyn);
                }


                if (className.equals("Hp")) {
                    Hp hp = (Hp) obj;
                    hps.add(hp);
                }
            }

            for (EnsemblGeneId eng : engs) {
                System.out.println("EnsemblGeneId: " + eng.toString());
            }
            for (MarkerSynonym m : ms) {
                System.out.println("MarkerSynonym: " + m.toString());
            }
            for (HumanGeneSymbol hg : hgs) {
                System.out.println("HumanGeneSymbol: " + hg.toString());
            }
            for (DiseaseModel dm : dms) {
                System.out.println("DiseaseModel: " + dm.toString());
            }
            for (MouseModel mm : mms) {
                System.out.println("MouseModel: " + mm.toString());
            }
            for (Allele allele : alleles) {
                System.out.println("Allele: " + allele.toString());
            }
            for (Mp mp : mps) {
                System.out.println("Mp: " + mp.toString());
            }
            for (OntoSynonym mpsyn : mpsyns) {
                System.out.println("OntoSynonym: " + mpsyn.toString());
            }
            for (Hp hp : hps) {
                System.out.println("Hp: " + hp.toString());
            }

        }

    }

    //@Test
    public void graphQueryTest2() throws Exception {

        List<String> markerSymbols = Arrays.asList("Nxn","Dst");
        for(String symbol : markerSymbols) {
            List<Object> objs = geneRepository.findDataByMarkerSymbol(symbol);
//            List<DiseaseModel> d = diseaseModelRepository.findByAllele_Gene_MarkerSymbol(ms);
//            System.out.println("Disease models found "+ d.size());
//            for (DiseaseModel d1 : d) {
//                System.out.println(d1.getDiseaseTerm());
//                System.out.println("allele:"+ d1.getAllele());
//            }
//            if (true) continue;

            Set<EnsemblGeneId> engs = new HashSet<>();
            Set<HumanGeneSymbol> hgs = new HashSet<>();
            Set<MarkerSynonym> ms = new HashSet<>();
            Set<MouseModel> mms = new HashSet<>();
            Set<Allele> alleles = new HashSet<>();
            Set<DiseaseModel> dms = new HashSet<>();
            Set<Mp> mps = new HashSet<>();
            Set<OntoSynonym> mpsyns = new HashSet<>();
            Set<Hp> hps = new HashSet<>();

            for (Object obj : objs) {
                String className = obj.getClass().getSimpleName();


                if (className.equals("Gene")) {
                    Gene g = (Gene) obj;
                    System.out.println(g.getMarkerSymbol());
                    System.out.println(g.getMgiAccessionId());
                }

                if (className.equals("EnsemblGeneId")) {
                    EnsemblGeneId ensg = (EnsemblGeneId) obj;
                    engs.add(ensg);
                }


                if (className.equals("MarkerSynonym")) {
                    MarkerSynonym m = (MarkerSynonym) obj;
                    ms.add(m);
                }


                if (className.equals("HumanGeneSymbol")) {
                    HumanGeneSymbol hg = (HumanGeneSymbol) obj;
                    hgs.add(hg);
                }


                if (className.equals("DiseaseModel")) {
                    DiseaseModel dm = (DiseaseModel) obj;
                    dms.add(dm);
                }


                if (className.equals("MouseModel")) {
                    MouseModel mm = (MouseModel) obj;
                    mms.add(mm);
                }


                if (className.equals("Allele")) {
                    Allele allele = (Allele) obj;
                    alleles.add(allele);
                }


                if (className.equals("Mp")) {
                    Mp mp = (Mp) obj;
                    mps.add(mp);
                }


                if (className.equals("OntoSynonym")) {
                    OntoSynonym mpsyn = (OntoSynonym) obj;
                    mpsyns.add(mpsyn);
                }


                if (className.equals("Hp")) {
                    Hp hp = (Hp) obj;
                    hps.add(hp);
                }
            }

            for (EnsemblGeneId eng : engs) {
                System.out.println("EnsemblGeneId: " + eng.toString());
            }
            for (MarkerSynonym m : ms) {
                System.out.println("MarkerSynonym: " + m.toString());
            }
            for (HumanGeneSymbol hg : hgs) {
                System.out.println("HumanGeneSymbol: " + hg.toString());
            }
            for (DiseaseModel dm : dms) {
                System.out.println("DiseaseModel: " + dm.toString());
            }
            for (MouseModel mm : mms) {
                System.out.println("MouseModel: " + mm.toString());
            }
            for (Allele allele : alleles) {
                System.out.println("Allele: " + allele.toString());
            }
            for (Mp mp : mps) {
                System.out.println("Mp: " + mp.toString());
            }
            for (OntoSynonym mpsyn : mpsyns) {
                System.out.println("OntoSynonym: " + mpsyn.toString());
            }
            for (Hp hp : hps) {
                System.out.println("Hp: " + hp.toString());
            }

        }
    }


    //@Test
    public void codeTest() throws Exception {

        String idlistStr = "\"this is a test of mp term\"";

        if (idlistStr.matches("^\".*\"$")){
            // ontology term name is quoted
            System.out.println("mp term");
            System.out.println(idlistStr.replaceAll("\"",""));
            System.out.println("found: " + idlistStr);
            //idlist.add(idlistStr);
        }

    }

}