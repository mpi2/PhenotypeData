package uk.ac.ebi.phenotype.web.controller;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ebi.phenodigm.dao.PhenoDigmWebDao;
import uk.ac.ebi.phenodigm.model.DiseaseIdentifier;
import uk.ac.ebi.phenodigm.model.DiseaseModelAssociation;
import uk.ac.ebi.phenodigm.model.GeneIdentifier;
import uk.ac.ebi.phenodigm.model.MouseModel;
import uk.ac.ebi.phenodigm.model.PhenotypeTerm;
import uk.ac.ebi.phenodigm.web.DiseaseGeneAssociationDetail;

/**
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
@RestController
public class PhenogridController {

    private static final Logger logger = LoggerFactory.getLogger(PhenogridController.class);
    private static final Comparator<DiseaseModelAssociation> GENE_TO_DISEASE_SCORE_COMPARATOR = DiseaseModelAssociation.GeneToDiseaseScoreComparator;
    private static final Comparator<DiseaseModelAssociation> DISEASE_TO_GENE_SCORE_COMPARATOR = DiseaseModelAssociation.DiseaseToGeneScoreComparator;

    @Autowired
    private PhenoDigmWebDao phenoDigmDao;

    @RequestMapping(value="/phenodigm/phenogrid", method= RequestMethod.GET)
    public PhenoGrid getPhenogrid(@RequestParam String requestPageType, @RequestParam String diseaseId, @RequestParam String geneId, HttpServletRequest request) {
        logger.info(String.format("Making phenogrid for %s %s from %s page", diseaseId, geneId, requestPageType));

        DiseaseGeneAssociationDetail diseaseGeneAssociationDetail = phenoDigmDao.getDiseaseGeneAssociationDetail(new DiseaseIdentifier(diseaseId), new GeneIdentifier(geneId, geneId));

        List<DiseaseModelAssociation> diseaseAssociations = diseaseGeneAssociationDetail.getDiseaseAssociations();

        Map<Boolean, List<DiseaseModelAssociation>> diseaseModelAssociationsWithLiteratureEvidence = diseaseAssociations.stream()
                .collect(Collectors.partitioningBy(DiseaseModelAssociation::hasLiteratureEvidence));

        List<DiseaseModelAssociation> literatureAssociations = diseaseModelAssociationsWithLiteratureEvidence.get(true);
        List<DiseaseModelAssociation> phenotypicAssociations = diseaseModelAssociationsWithLiteratureEvidence.get(false);

        String title = " "; //use a space instead of null or empty string to prevent the phenogrid from displaying an unwanted default title
        List<PhenoGridGroup> xAxisGroups = makePhenoGridGroups(requestPageType, (String) request.getAttribute("baseUrl"), literatureAssociations, phenotypicAssociations);
        List<PhenotypeTerm> diseasePhenotypes = diseaseGeneAssociationDetail.getDiseasePhenotypes();
        return new PhenoGrid(title, xAxisGroups, diseasePhenotypes);
    }

    private List<PhenoGridGroup> makePhenoGridGroups(String requestPageType, String baseUrl, List<DiseaseModelAssociation> literatureAssociations, List<DiseaseModelAssociation> phenotypicAssociations) {
        List<PhenoGridGroup> xAxisGroups = new ArrayList<>();
        if (!literatureAssociations.isEmpty()) {
            Collections.sort(literatureAssociations, comparatorForPageType(requestPageType));
            xAxisGroups.add(new PhenoGridGroup("lit", "Lit. Associated", makeMouseModelEntities(literatureAssociations, requestPageType, baseUrl)));
        }
        if (!phenotypicAssociations.isEmpty()) {
            Collections.sort(phenotypicAssociations, comparatorForPageType(requestPageType));
            xAxisGroups.add(new PhenoGridGroup("pheno", "Phenotype Associated", makeMouseModelEntities(phenotypicAssociations, requestPageType, baseUrl)));
        }
        return xAxisGroups;
    }

    /*
     * The lists need sorting according to the view in which they will be appearing. We'll assume that the default is
     * going to be a disease page, but it could be a gene page
     */
    private Comparator<DiseaseModelAssociation> comparatorForPageType(String requestPageType) {
        switch (requestPageType) {
            case "gene":
                return GENE_TO_DISEASE_SCORE_COMPARATOR;
            default:
                return DISEASE_TO_GENE_SCORE_COMPARATOR;
        }
    }

    private List<PhenoGridEntity> makeMouseModelEntities(List<DiseaseModelAssociation> diseaseAssociations, String requestPageType, String baseUrl) {
        List<PhenoGridEntity> mouseEntities = new ArrayList<>(diseaseAssociations.size());
        for (int i = 0; i < diseaseAssociations.size(); i++) {
            PhenoGridEntity mouseEntity = makeMouseModelEntityForPage(diseaseAssociations.get(i), i, requestPageType, baseUrl);
            mouseEntities.add(mouseEntity);
        }
        return mouseEntities;
    }

    private PhenoGridEntity makeMouseModelEntityForPage(DiseaseModelAssociation diseaseModelAssociation, int rank, String requestPageType, String baseUrl) {
        MouseModel mouseModel = diseaseModelAssociation.getMouseModel();
        Integer modelId = mouseModel.getMgiModelId();

        String label = shortFormNotation(mouseModel.getAllelicComposition());
        //we only want to show the ids for the mouse phenotypes to cut down on payload size
        List<PhenotypeTerm> phenotypes = makeIdOnlyPhenotypes(mouseModel.getPhenotypeTerms());
        double phenodigmScore = makeScoreForPageType(requestPageType, diseaseModelAssociation);
        PhenoGridScore score = new PhenoGridScore("Phenodigm score", phenodigmScore, rank);
        List<EntityInfo> info = makeMouseModelInfo(mouseModel, phenodigmScore, baseUrl);
        return new PhenoGridEntity(modelId.toString(), label, phenotypes, score, info);
    }

    private List<PhenotypeTerm> makeIdOnlyPhenotypes(List<PhenotypeTerm> phenotypeTerms) {
        return phenotypeTerms.stream().map(phenotypeTerm -> new PhenotypeTerm(phenotypeTerm.getId(), "")).collect(toList());
    }

    private double makeScoreForPageType(String requestPageType, DiseaseModelAssociation diseaseModelAssociation){
        switch (requestPageType) {
            case "gene":
                return diseaseModelAssociation.getModelToDiseaseScore();
            default:
                return diseaseModelAssociation.getDiseaseToModelScore();
        }
    }

    private List<EntityInfo> makeMouseModelInfo(MouseModel mouseModel, double phenodigmScore, String baseUrl) {
        EntityInfo source = new EntityInfo("Source: ", mouseModel.getSource(), null);
        //TODO: re-work the MouseModel to have a proper allele representation e.g. list<Allele> where Allele has an mgi id, gene symbol and a lab code.
        EntityInfo genotype = new EntityInfo("Genotype: ", mouseModel.getAllelicCompositionLink(), null);
        EntityInfo background = new EntityInfo("Background: ", mouseModel.getGeneticBackground(), null);
        EntityInfo impcGene = new EntityInfo("Gene: ", mouseModel.getMgiGeneId(), baseUrl + "/genes/" +  mouseModel.getMgiGeneId());
        EntityInfo scoreInfo = new EntityInfo("Phenodigm score: ", Double.toString(phenodigmScore), null);
        List<EntityInfo> info = new ArrayList<>(Arrays.asList(source, genotype, background, impcGene, scoreInfo));

        info.add(new EntityInfo("Observed phenotypes: ", "", null));
        List<EntityInfo> phenotypes = mouseModel.getPhenotypeTerms()
                .stream()
                .map(phenotype -> new EntityInfo("", phenotype.getTerm(), baseUrl + "/phenotypes/" + phenotype.getId()))
                .collect(toList());
        info.addAll(phenotypes);

        return info;
    }

    /**
     * Transforms "Fgfr2<tm1.1Dsn>/Fgfr2<+>" to "Fgfr2 (tm1.1Dsn/+)"
     *
     * @param allelicComposition
     * @return The short-form notation of a mouse allele
     */
    private String shortFormNotation(String allelicComposition) {
        //e.g. "Fgfr2<tm1.1Dsn>/Fgfr2<+>" -> "Fgfr2 (tm1.1Dsn/+)"
        String[] alleles = allelicComposition.split("/");
        //return gene symbol lab codes for model -
        String geneSymbol = geneSymbol(alleles[0]);
        String alleleId0 = labCode(geneSymbol, alleles[0]);
        //cater for single-allele cases such as "mt-Rnr2<m1Dwa>"
        String alleleId1 = "";
        if (alleles.length == 2) {
            alleleId1 = "/" + labCode(geneSymbol, alleles[1]);
        }
        return String.format("%s (%s%s)", geneSymbol, alleleId0, alleleId1);
    }

    private String geneSymbol(String allele) {
        //"Fgfr2<tm1.1Dsn>" -> "Fgfr2"
        String[] tokens = allele.split("\\<");
        return tokens[0];
    }

    private String labCode(String geneSymbol, String alleleId) {
        //"Fgfr2<tm1.1Dsn>" -> "tm1.1Dsn"
        return alleleId.replace(geneSymbol, "").replace("<", "").replace(">", "");
    }

    private String addSuppTagsToLabCodes(String allelicComposition) {
        //e.g. "Fgfr2<tm1.1Dsn>/Fgfr2<+>"
        String[] alleles = allelicComposition.split("/");
        String alleleString = stripAngleBracketsAndAddSuppTags(alleles[0]) + "/" + stripAngleBracketsAndAddSuppTags(alleles[1]);
        return alleleString;
    }

    private String stripAngleBracketsAndAddSuppTags(String allele) {
        //allele examples "Fgfr2<+>", "Fgfr2<tm1.1Dsn>", "Fgfr2<tm1Lni>"
        String[] tokens = allele.split("\\<");
        String geneSymbol = tokens[0];
        if (tokens.length == 2) {
            String labCode = tokens[1].replace(">", "");
            return geneSymbol + htmlSup(labCode);
        } else {
            return geneSymbol;
        }
    }

    private String htmlSup(String string) {
        return "<sup>" + string + "</sup>";
    }

    private class PhenoGrid {

        private final String title;
        private final List<PhenoGridGroup> xAxis;
        private final List<PhenotypeTerm> yAxis;

        public PhenoGrid(String title, List<PhenoGridGroup> xAxis, List<PhenotypeTerm> yAxis) {
            this.title = title;
            this.xAxis = xAxis;
            this.yAxis = yAxis;
        }

        public String getTitle() {
            return title;
        }

        public List<PhenoGridGroup> getxAxis() {
            return xAxis;
        }

        public List<PhenotypeTerm> getyAxis() {
            return yAxis;
        }
    }

    private class PhenoGridGroup {
        private final String groupId;
        private final String groupName;
        private final List<PhenoGridEntity> entities;

        public PhenoGridGroup(String groupId, String groupName, List<PhenoGridEntity> entities) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.entities = entities;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public List<PhenoGridEntity> getEntities() {
            return entities;
        }
    }

    private class PhenoGridEntity {
        private String id; //not sure if this is required or not
        private String label;
        private List<PhenotypeTerm> phenotypes = new ArrayList<>();
        private PhenoGridScore score;
        private List<EntityInfo> info = new ArrayList<>();

        public PhenoGridEntity(String id, String label, List<PhenotypeTerm> phenotypes, PhenoGridScore score, List<EntityInfo> info) {
            this.id = id;
            this.label = label;
            this.phenotypes = phenotypes;
            this.score = score;
            this.info = info;
        }

        public String getId() {
            return id;
        }
        public String getLabel() {
            return label;
        }

        public List<PhenotypeTerm> getPhenotypes() {
            return phenotypes;
        }

        public PhenoGridScore getScore() {
            return score;
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

}
