package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.CountTableRow;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.bean.LandingPageDTO;
import uk.ac.ebi.phenotype.chart.AnalyticsChartProvider;
import uk.ac.ebi.phenotype.chart.ScatterChartAndTableProvider;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by ilinca on 24/10/2016.
 */

@Controller
public class LandingPageController {

    @Autowired
    ObservationService os;

    @Autowired
    PostQcService gpService;

    @Autowired
    StatisticalResultService srService;

    @Autowired
    GeneService geneService;

    @Autowired
    ImageService imageService;

    @Autowired
    PhenodigmService phenodigmService;

    @Autowired
    MpService mpService;

    @Autowired
    ImpressService is;

    @RequestMapping("/biological-system")
    public String getAlleles(
            Model model,
            HttpServletRequest request) throws IOException {

        BufferedReader in = new BufferedReader(new FileReader(new ClassPathResource("landingPages.json").getFile()));
        if (in != null) {
            String json = in.lines().collect(Collectors.joining(" "));
            ObjectMapper mapper = new ObjectMapper();
            LandingPageDTO[] readValue = mapper.readValue(json, TypeFactory.defaultInstance().constructArrayType(LandingPageDTO.class));
            model.addAttribute("pages", new ArrayList<LandingPageDTO>(Arrays.asList(readValue)));
        }
        return "landing";
    }

    @RequestMapping(value = "/embryo", method = RequestMethod.GET)
    public String loadEmbryoPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();
        List<String> resources = new ArrayList<>();
        resources.add("IMPC");
        Map<String, Set<String>> viabilityRes = os.getViabilityCategories(resources, true);

        Map<String, Long> viabilityMap = os.getViabilityCategories(viabilityRes);
        List<CountTableRow> viabilityTable = os.consolidateZygosities(viabilityRes);

        model.addAttribute("viabilityChart", chartsProvider.getSlicedPieChart(new HashMap<String, Long>(), viabilityMap, "", "viabilityChart"));
        model.addAttribute("viabilityTable", viabilityTable);

        return "embryo";
    }


    @RequestMapping(value = "/biological-system/{page}", method = RequestMethod.GET)
    public String loadDeafnessPage(@PathVariable String page, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException, ExecutionException, InterruptedException {

        String pageTitle = "";
        List<String> resources = new ArrayList<>();
        resources.add("IMPC");
        List<String> anatomyIds = new ArrayList<>(); // corresponding anatomical system, used for images
        MpDTO mpDTO = null;


        if (page.equalsIgnoreCase("deafness")) { // Need to decide if we want deafness only or top level hearing/vestibular phen
            mpDTO = mpService.getPhenotype("MP:0005377");
            anatomyIds.add("MA:0002443");
            anatomyIds.add("EMAPA:36002");
            model.addAttribute("shortDescription", "We have undertaken a deafness screen in the IMPC cohort of mouse knockout strains. We detected known deafness genes and the vast majority of loci were novel.");
            pageTitle = "Hearing/Vestibular/Ear";
        } else if (page.equalsIgnoreCase("cardiovascular")) {
            mpDTO = mpService.getPhenotype("MP:0005385");
            anatomyIds.add("MA:0000010");
            anatomyIds.add("EMAPA:16104");
            pageTitle = "Cardiovascular";
        } else if (page.equalsIgnoreCase("metabolism")) {
            mpDTO = mpService.getPhenotype("MP:0005376");
            pageTitle = "Metabolism";
        } else if (page.equalsIgnoreCase("vision")) {
            mpDTO = mpService.getPhenotype("MP:0005391");
            anatomyIds.add("EMAPA:36003");
            anatomyIds.add("MA:0002444");
            pageTitle = "Vision";
        } else if (page.equalsIgnoreCase("nervous")) {
            mpDTO = mpService.getPhenotype("MP:0003631");
            anatomyIds.add("MA:0000016");
            anatomyIds.add("EMAPA:16469");
            pageTitle = "Nervous phenotypes";
        } else if (page.equalsIgnoreCase("neurological")) {
            mpDTO = mpService.getPhenotype("MP:0005386");
            pageTitle = "Behavioural/neurological phenotypes";
        }

        // IMPC image display at the bottom of the page
        List<Group> groups = imageService.getPhenotypeAssociatedImages(null, mpDTO.getMpId(), anatomyIds, true, 1);
        Map<String, String> paramToNumber = new HashMap<>();
        for (Group group : groups) {
            if (!paramToNumber.containsKey(group.getGroupValue())) {
                paramToNumber.put(group.getGroupValue(), Long.toString(group.getResult().getNumFound()));
            }
        }
        ArrayList<ImpressDTO> procedures = new ArrayList<>();
        procedures.addAll(is.getProceduresByMpTerm(mpDTO.getMpId(), true));
        Collections.sort(procedures, ImpressDTO.getComparatorByProcedureName());


        model.addAttribute("phenotypeChart", ScatterChartAndTableProvider.getScatterChart("phenotypeChart", gpService.getTopLevelPhenotypeIntersection(mpDTO.getMpId()), "Gene pleiotropy",
                "for genes with at least one " + pageTitle + " phenotype", "Number of associations to " + pageTitle, "Number of associations to other phenotypes",
                "Other phenotype calls: ", pageTitle + " phenotype calls: "));
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("paramToNumber", paramToNumber);
        model.addAttribute("impcImageGroups", groups);
        model.addAttribute("genePercentage", ControllerUtils.getPercentages(mpDTO.getMpId(), srService, gpService));
        model.addAttribute("phenotypes", gpService.getAssociationsCount(mpDTO.getMpId(), resources));
        model.addAttribute("mpId", mpDTO.getMpId());
        model.addAttribute("mpDTO", mpDTO);
        model.addAttribute("systemName", mpDTO.getMpTerm().replace(" phenotype", ""));
        model.addAttribute("procedures", procedures);

        return "landing_" + page;

    }


    @ResponseBody
    @RequestMapping(value = "/orthology.jsonp", method = RequestMethod.GET)
    public String getOrthologyJson(
            @RequestParam(required = true, value = "mpId") String mpId,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException, ExecutionException, InterruptedException {


        Set<String> diseaseClasses = new HashSet<>();
        diseaseClasses.add("cardiac");
        diseaseClasses.add("cardiac malformations");
        diseaseClasses.add("circulatory system");

        return "var mgiSets =  " + getOrtologyDiseaseModelVennDiagram(mpId, diseaseClasses, true, false) + ";"
            + "var impcSets = " +  getOrtologyDiseaseModelVennDiagram(mpId, diseaseClasses, false, true) + ";";

    }



    @ResponseBody
    @RequestMapping(value = "/orthology.csv", method = RequestMethod.GET)
    public String getOrthologyDownload(
            @RequestParam(required = false, value = "mpId") String mpId,
            @RequestParam( required =  false, value = "diseaseClasses") Set<String> diseaseClasses,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException, ExecutionException, InterruptedException {

        //TODO marker_symbol,marker_accession,disease_term,disease_id,impc_predicted,mgi_predicted,human_curated,max_mgi_m2d_score,max_impc_m2d_score

        return phenodigmService.getGenesWithDiseaseDownload(diseaseClasses);

    }

    private JSONArray getOrtologyDiseaseModelVennDiagram(String mpId, Set<String> diseaseClasses, Boolean mgi, Boolean impc) throws IOException, SolrServerException {

        Map<String, Set<String>> sets = new HashMap<>();

        // get gene sets for human orthology (with/without)
        sets.put("IMPC phenotype", geneService.getGenesSumbolsBy(mpId));
        System.out.println("Gene count : " + geneService.getGenesSumbolsBy(mpId).size());

        // get gene sets for IMPC and MGI disease models
        // http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/phenodigm/select?q=*:*&facet=true&facet.field=type&fq=type:disease_gene_summary&fq=impc_predicted:true&fq=raw_htpc_score:[1.79%20TO%20*]&fq=disease_classes:cardiac*&group=true&group.field=marker_symbol&group.ngroups=true

        Map<String, Set<String>> genesWithDisease = phenodigmService.getGenesWithDisease(diseaseClasses);

        if (mgi && !impc){
            sets.put("Human curated (orthology)", genesWithDisease.get("Human curated (orthology)"));
            sets.put("MGI predicted", genesWithDisease.get("MGI predicted"));
            return getJsonForVenn(sets);
        }
        else if (!mgi && impc) {
            sets.put("IMPC predicted", genesWithDisease.get("IMPC predicted"));
            sets.put("Human curated (orthology)", genesWithDisease.get("Human curated (orthology)"));
            return getJsonForVenn(sets);
        } else {
            sets.putAll(genesWithDisease);
            return getJsonForVenn(sets);
        }

    }

    private JSONArray getJsonForVenn( Map<String, Set<String>> allSets){

        // get counts for intersections
        JSONArray sets = new JSONArray();
        JSONArray wholeSets = new JSONArray();
        List<String> keysIndex = new ArrayList<>(allSets.keySet()); // need this to get the index of each key

        // Add whole sets to the object
        for (int i = 0; i < keysIndex.size(); i++) {
            JSONArray currentSets = new JSONArray();
            currentSets.add(i);
            sets.add(getSetVennFormat(keysIndex.get(i), currentSets, allSets.get(keysIndex.get(i)).size()));
            wholeSets.add(getSetJSON(keysIndex.get(i), currentSets, allSets.get(keysIndex.get(i))));
        }

        // Intersections of 2 sets at a time
        for (int i = 0; i < keysIndex.size()-1; i++) {
            for (int j = i + 1; j < keysIndex.size(); j++) {
                JSONArray currentSets = new JSONArray();
                currentSets.add(i);
                currentSets.add(j);
                Set<String> intersection = new HashSet<>(CollectionUtils.intersection(allSets.get(keysIndex.get(i)), allSets.get(keysIndex.get(j))));
                sets.add(getSetVennFormat(null, currentSets, intersection.size()));
                wholeSets.add(getSetJSON(null, currentSets, intersection));
            }
        }

        // Intersections of 3 sets at a time
        for (int i = 0; i < keysIndex.size()-2; i++) {
            for (int j = i + 1; j < keysIndex.size()-1; j++) {
                for (int k = j + 1; k < keysIndex.size(); k++) {
                    JSONArray currentSets = new JSONArray();
                    currentSets.add(i);
                    currentSets.add(j);
                    currentSets.add(k);
                    Set<String> intersection = new HashSet<>(CollectionUtils.intersection(allSets.get(keysIndex.get(i)),
                            CollectionUtils.intersection(allSets.get(keysIndex.get(j)), allSets.get(keysIndex.get(k)))));
                    sets.add(getSetVennFormat(null, currentSets, intersection.size()));
                    wholeSets.add(getSetJSON(null, currentSets, intersection));
                }
            }
        }

//        // Intersections of 4 sets
//        JSONArray currentSets = new JSONArray();
//        currentSets.add(0);
//        currentSets.add(1);
//        currentSets.add(2);
//        currentSets.add(3);
//        int intersectionSize = CollectionUtils.intersection(allSets.get(keysIndex.get(0)),
//                CollectionUtils.intersection(allSets.get(keysIndex.get(1)), CollectionUtils.intersection(allSets.get(keysIndex.get(2)),allSets.get(keysIndex.get(3))))).size();
//        sets.add(getSetVennFormat(null, currentSets, intersectionSize));

        System.out.println("SIZES HERE :::: " + sets);
        System.out.println("SETS HERE :::: " + wholeSets);

        // return in right format for venn diagram http://benfred.github.io/venn.js/examples/styled.html
        JSONArray result = new JSONArray();
        result.addAll(sets);

        return result;

        }



    /**
     * @param label
     * @param sets
     * @param size
     * @return JSON objects in the format required by venn diagram library. At the moment used on biliogical system pages.
     */
    private JSONObject getSetVennFormat(String label, JSONArray sets, Integer size) {

        JSONObject set = new JSONObject();
        set.put("sets", sets);
        if (label != null) {
            set.put("label", label);
        }
        set.put("size", size);

        return set;

    }

    private JSONObject getSetJSON(String label, JSONArray sets, Set<String> set) {

        JSONObject obj = new JSONObject();
        obj.put("sets", sets);
        if (label != null) {
            obj.put("label", label);
        }
        obj.put("set", set);

        return obj;

    }

    @RequestMapping(value = "/embryo/vignettes", method = RequestMethod.GET)
    public String loadVignettes(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        return "embryoVignettes";
    }


}
