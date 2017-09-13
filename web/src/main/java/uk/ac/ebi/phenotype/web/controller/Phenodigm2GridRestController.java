package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ebi.phenodigm2.*;

/**
 * Controller that responds to requests for phenodigm2 data. Suitable for
 * obtaining data for a phenogrid object through ajax.
 *
 * Much of this class is copied from PhenogridController.java but includes
 * slight modifications to suit the phenodigm2 format.
 *
 */
@RestController
public class Phenodigm2GridRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Phenodigm2GridRestController.class);

    @Autowired
    private WebDao phenoDigm2Dao;

    @RequestMapping(value = "/phenodigm2/phenogrid", method = RequestMethod.GET)
    public PhenoGrid getPhenoGrid(@RequestParam String pageType, @RequestParam String diseaseId, @RequestParam String geneId, HttpServletRequest request) {
        LOGGER.info(String.format("Making phenogigm2/phenogrid for %s %s from %s page", diseaseId, geneId, pageType));

        String baseUrl = (String) request.getAttribute("baseUrl");

        List<Phenotype> diseasePhenotypes = phenoDigm2Dao.getDiseasePhenotypes(diseaseId);
        LOGGER.info("got disease phenotypes: " + diseasePhenotypes.toString());

        List<Model> modelDetails = phenoDigm2Dao.getGeneModelDetails(geneId);
        List<PhenoGridGroup> xAxisGroups = makePhenoGridGroups(pageType, baseUrl, modelDetails);
        String title = " "; //use a space instead of null or empty string to prevent the phenogrid from displaying an unwanted default title

        return new PhenoGrid(title, xAxisGroups, diseasePhenotypes);
    }

    private List<PhenoGridGroup> makePhenoGridGroups(String pageType, String baseUrl, List<Model> models) {
        List<PhenoGridGroup> xAxisGroups = new ArrayList<>();
        xAxisGroups.add(new PhenoGridGroup("pheno", "Phenotype Associated", makeGridEntities(pageType, baseUrl, models)));
        return xAxisGroups;
    }

    private List<PhenoGridEntity> makeGridEntities(String pageType, String baseUrl, List<Model> models) {
        List<PhenoGridEntity> result = new ArrayList<>(models.size());
        for (Model model : models) {
            result.add(makeGridEntity(model, result.size(), pageType, baseUrl));
        }
        return result;
    }

    private PhenoGridEntity makeGridEntity(Model model, int rank, String pageType, String baseUrl) {

        // collect information from the association oject
        String id = model.getId();
        String label = model.getDescription();
        //List<Phenotype> phenotypes = makeIdOnlyPhenotypes(model.getPhenotypes());
        List<Phenotype> phenotypes = model.getPhenotypes();
        PhenoGridScore score = new PhenoGridScore("Phenodigm score", 0, rank);
        List<EntityInfo> info = makeModelInfo(model, 0, baseUrl);

        return new PhenoGridEntity(id, label, phenotypes, score, info);
    }

    /**
     * Convert a list of phenotypes into a new list that contains only ids (not
     * terms) This is an optimization that avoid sending phenotype terms in the 
     * Rest response.
     *
     * To display phenotype details in a table, the terms are actually required.
     * So this function comes into and out of use during development.
     *
     * @param phenotypes
     * @return
     */
    private List<Phenotype> makeIdOnlyPhenotypes(List<Phenotype> phenotypes) {
        List<Phenotype> result = new ArrayList<>(phenotypes.size());
        for (Phenotype p : phenotypes) {
            result.add(new Phenotype(p.getId(), ""));
        }
        return result;
    }

    /**
     *
     */
    private List<EntityInfo> makeModelInfo(Model model, double score, String baseUrl) {

        List<EntityInfo> result = new ArrayList<>();
        result.add(new EntityInfo("Source: ", model.getSource(), null));
        result.add(new EntityInfo("Genotype: ", model.getDescription(), null));
        result.add(new EntityInfo("Background: ", model.getGeneticBackground(), null));
        result.add(new EntityInfo("Gene ID: ", model.getMarkerId(), null));
        result.add(new EntityInfo("Gene symbol: ", model.getMarkerSymbol(), null));
        result.add(new EntityInfo("Observed phenotypes: ", "", null));

        // copy the list of phenotypes
        for (Phenotype phenotype : model.getPhenotypes()) {
            result.add(new EntityInfo("", phenotype.getTerm(), null));
        }

        return result;
    }

    /**
     * Object defining a phenogrid skeleton.
     *
     * Holds a title, definitions of the x and y axes. See the Phenogrid docs
     * on github for details.
     */
    private class PhenoGrid {

        private final String title;
        private final List<PhenoGridGroup> xAxis;
        private final List<Phenotype> yAxis;

        public PhenoGrid(String title, List<PhenoGridGroup> xAxis, List<Phenotype> yAxis) {
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

        public List<Phenotype> getyAxis() {
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
        private List<Phenotype> phenotypes = new ArrayList<>();
        private PhenoGridScore score;
        private List<EntityInfo> info = new ArrayList<>();

        public PhenoGridEntity(String id, String label, List<Phenotype> phenotypes, PhenoGridScore score, List<EntityInfo> info) {
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

        public List<Phenotype> getPhenotypes() {
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
