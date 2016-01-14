package uk.ac.ebi.phenotype.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmWebDao;
import uk.ac.sanger.phenodigm2.model.*;
import uk.ac.sanger.phenodigm2.web.DiseaseGeneAssociationDetail;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
@RestController
public class PhenogridController {

    private static final Logger logger = LoggerFactory.getLogger(PhenogridController.class);

    @Autowired
    private PhenoDigmWebDao phenoDigmDao;

//    @Autowired
//    private ServletContext servletContext;

    @RequestMapping(value="/phenodigm/phenogrid", method= RequestMethod.GET)
    public PhenoGrid getPhenogrid(@RequestParam String requestPageType, @RequestParam String diseaseId, @RequestParam String geneId) {
        logger.info(String.format("Making phenogrid for %s %s from %s page", diseaseId, geneId, requestPageType));

        DiseaseGeneAssociationDetail diseaseGeneAssociationDetail = phenoDigmDao.getDiseaseGeneAssociationDetail(new DiseaseIdentifier(diseaseId), new GeneIdentifier(geneId, geneId));

        List<DiseaseModelAssociation> diseaseAssociations = diseaseGeneAssociationDetail.getDiseaseAssociations();

        Map<Boolean, List<DiseaseModelAssociation>> diseaseModelAssociations = diseaseAssociations.stream()
                .collect(Collectors.partitioningBy(DiseaseModelAssociation::hasLiteratureEvidence));

        List<DiseaseModelAssociation> literatureAssociations = diseaseModelAssociations.get(true);
        List<DiseaseModelAssociation> phenotypicAssociations = diseaseModelAssociations.get(false);

        //The lists need sorting according to the view in which they will be appearing
        //we'll assume that the default is going to be a disease page
        //but it could be a gene page
        Comparator<DiseaseModelAssociation> pageComparator = makePageComparator(requestPageType);

        //sort the lists according to the view in which they will be appearing
        Collections.sort(literatureAssociations, pageComparator);
        Collections.sort(phenotypicAssociations, pageComparator);

        List<DiseaseModelAssociation> sortedAssociations = new ArrayList<>(literatureAssociations);
        sortedAssociations.addAll(phenotypicAssociations);

        String title = String.format("Comparison %s - %s for %s page", diseaseId, geneId, requestPageType);
        List<PhenoGridEntity> xAxis = makeMouseModelEntities(sortedAssociations, requestPageType);
        List<PhenoGridEntity> yAxis = Collections.singletonList(makeDiseaseEntity(diseaseGeneAssociationDetail));
        return new PhenoGrid(title, xAxis, yAxis);
    }

    private final PhenoGridTaxon HUMAN_TAXON = new PhenoGridTaxon("NCBITaxon:9606", "Homo sapiens");

    private PhenoGridEntity makeDiseaseEntity(DiseaseGeneAssociationDetail diseaseGeneAssociationDetail) {
        List<String> phenotypes = diseaseGeneAssociationDetail.getDiseasePhenotypes().stream().map(PhenotypeTerm::getId).collect(toList());
        String label = diseaseGeneAssociationDetail.getDiseaseId().getCompoundIdentifier();
        return new PhenoGridEntity(label, phenotypes, null, HUMAN_TAXON, new ArrayList<>());
    }

    private List<PhenoGridEntity> makeMouseModelEntities(List<DiseaseModelAssociation> diseaseAssociations, String requestPageType) {
        List<PhenoGridEntity> mouseEntities = new ArrayList<>(diseaseAssociations.size());
        for (int i = 0; i < diseaseAssociations.size(); i++) {
            PhenoGridEntity mouseEntity = makeMouseModelEntityForPage(diseaseAssociations.get(i), i, requestPageType);
            mouseEntities.add(mouseEntity);
        }
        return mouseEntities;
    }

    private final PhenoGridTaxon MOUSE_TAXON = new PhenoGridTaxon("NCBITaxon:10090", "Mus musculus");

    private PhenoGridEntity makeMouseModelEntityForPage(DiseaseModelAssociation diseaseModelAssociation, int rank, String requestPageType) {
            MouseModel mouseModel = diseaseModelAssociation.getMouseModel();
            String label = mouseModel.getAllelicComposition() + " " + mouseModel.getGeneticBackground();
            List<String> phenotypes = mouseModel.getPhenotypeTerms().stream().map(PhenotypeTerm::getId).collect(toList());
            PhenoGridScore score = new PhenoGridScore("phenodigm", makeScoreForPageType(requestPageType, diseaseModelAssociation), rank);
            List<EntityInfo> info = makeMouseModelInfo(mouseModel);
            return new PhenoGridEntity(label, phenotypes, score, MOUSE_TAXON, info);
//            return new PhenoGridEntityBuilder(label, phenotypes, score).info(info).buildMouseEntity();
    }

    private List<EntityInfo> makeMouseModelInfo(MouseModel mouseModel) {
        EntityInfo source = new EntityInfo("Source", mouseModel.getSource(), null);
        //TODO: re-work the MouseModel to have a proper allele representation e.g. list<Allele> where Allele has an mgi id and a label.
//        EntityInfo genotype = new EntityInfo("Genotype", mouseModel.getAllelicComposition(), mouseModel.getAllelicCompositionLink());
        EntityInfo background = new EntityInfo("Background", mouseModel.getGeneticBackground(), null);
        //TODO: get the proper context path
        EntityInfo impcGene = new EntityInfo("IMPC gene", mouseModel.getMgiGeneId(), "https://dev.mousephenotype.org/data/genes/" +  mouseModel.getMgiGeneId());
        return Arrays.asList(source, background, impcGene);
    }

    private double makeScoreForPageType(String requestPageType, DiseaseModelAssociation diseaseModelAssociation){
        if (requestPageType.equals("disease")) {
            return diseaseModelAssociation.getDiseaseToModelScore();
        }
        return diseaseModelAssociation.getModelToDiseaseScore();
    }

    private Comparator<DiseaseModelAssociation> makePageComparator(String requestPageType) {
        if (requestPageType.equals("gene")) {
            logger.info("Sorting DiseaseAssociations according to m2d score for Gene page");
            return DiseaseModelAssociation.GeneToDiseaseScoreComparator;
        }
        else {
            logger.info("Sorting DiseaseAssociations according to d2m score for Disease page");
            return DiseaseModelAssociation.DiseaseToGeneScoreComparator;
        }
    }

    private class PhenoGrid {

        private final String title;
        private final List<PhenoGridEntity> xAxis;
        private final List<PhenoGridEntity> yAxis;

        public PhenoGrid(String title, List<PhenoGridEntity> xAxis, List<PhenoGridEntity> yAxis) {
            this.title = title;
            this.xAxis = xAxis;
            this.yAxis = yAxis;
        }

        public String getTitle() {
            return title;
        }

        public List<PhenoGridEntity> getxAxis() {
            return xAxis;
        }

        public List<PhenoGridEntity> getyAxis() {
            return yAxis;
        }
    }

    private class PhenoGridEntity {
        private String id; //not sure if this is required or not
        private String label;
        private List<String> phenotypes = new ArrayList<>();
        private PhenoGridScore score;
        private PhenoGridTaxon taxon;
        private List<EntityInfo> info = new ArrayList<>();

        public PhenoGridEntity(String label, List<String> phenotypes, PhenoGridScore score, PhenoGridTaxon taxon, List<EntityInfo> info) {
            this.label = label;
            this.phenotypes = phenotypes;
            this.score = score;
            this.taxon = taxon;
            this.info = info;
        }

        public String getLabel() {
            return label;
        }

        public List<String> getPhenotypes() {
            return phenotypes;
        }

        public PhenoGridScore getScore() {
            return score;
        }

        public PhenoGridTaxon getTaxon() {
            return taxon;
        }

        public List<EntityInfo> getInfo() {
            return info;
        }
    }

    private class PhenoGridScore {
        private final String metric;
        private final double score;
        private final int rank;

        public PhenoGridScore(String metric, double score, int rank) {
            this.metric = metric;
            this.score = score;
            this.rank = rank;
        }

        public String getMetric() {
            return metric;
        }

        public double getScore() {
            return score;
        }

        public int getRank() {
            return rank;
        }
    }

    private class PhenoGridTaxon {

        private final String id;
        private final String label;

        public PhenoGridTaxon(String id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }
    }

    private class EntityInfo {
        private final String id;
        private final String value;
        private final String href;

        public EntityInfo(String id, String value, String href) {
            this.id = id;
            this.value = value;
            this.href = href;
        }

        public String getId() {
            return id;
        }

        public String getValue() {
            return value;
        }

        public String getHref() {
            return href;
        }
    }

    private class PhenoGridEntityBuilder {

        public PhenoGridEntityBuilder(String label, List<String> phenotypes, PhenoGridScore score) {
        }

    }
//            "info": {
//        "fields": [
//        {
//            "id": "Genotype",
//                "value": "Fgfr2<sup>tm2.3Dsn</sup>/Fgfr2<sup>+</sup>"
//        },
//        {
//            "id": "Background",
//                "value": "involves: 129 * C57BL/6 * FVB/N"
//        },
//        {
//            "id": "Source",
//                "value": "MGI"
//        },
//        {
//            "id": "MGI allele",
//                "value": "Fgfr2<sup>tm2.3Dsn</sup>",
//                "href": "http://informatics.jax.org/accession/MGI:2153817"
//        },
//        {
//            "id": "IMPC gene",
//                "value": "MGI:95523",
//                "href": "http://www.mousephenotype.org/data/genes/MGI:95523"
//        }
//        ]
//    }
}
