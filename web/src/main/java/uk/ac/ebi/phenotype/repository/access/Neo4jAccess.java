package uk.ac.ebi.phenotype.repository.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.phenotype.repository.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ckchen on 17/03/2017.
 */

@Component
public class Neo4jAccess implements CommandLineRunner {

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


    public Neo4jAccess() {}

    @Override
    public void run(String... strings) throws Exception {

        logger.info("access");


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
                if (className.equals("MouseModel")) {
                    MouseModel mm = (MouseModel) obj;
                    mms.add(mm);
                }
                for (MouseModel mm : mms) {
                    System.out.println("MouseModel: " + mm.toString());
                }

                Set<Allele> alleles = new HashSet<>();
                if (className.equals("Allele")) {
                    Allele allele = (Allele) obj;
                    alleles.add(allele);
                }
                for (Allele allele : alleles) {
                    System.out.println("Allele: " + allele.toString());
                }

                Set<DiseaseModel> dms = new HashSet<>();
                if (className.equals("DiseaseModel")) {
                    DiseaseModel dm = (DiseaseModel) obj;
                    dms.add(dm);
                }
                for (DiseaseModel dm : dms) {
                    System.out.println("DiseaseModel: " + dm.toString());
                }

            }
        }
    }

}