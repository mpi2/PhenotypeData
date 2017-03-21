package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.CountTableRow;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.bean.LandingPageDTO;
import uk.ac.ebi.phenotype.chart.AnalyticsChartProvider;
import uk.ac.ebi.phenotype.chart.ScatterChartAndTableProvider;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;

import javax.servlet.http.HttpServletRequest;
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
	public String getAlleles(Model model, HttpServletRequest request) throws IOException {

		List<LandingPageDTO> bsPages = new ArrayList<>();
		LandingPageDTO cardiovascular = new LandingPageDTO();

		cardiovascular.setTitle("Cardiovascular");
		cardiovascular.setImage("render_thumbnail/211474/400/");
		cardiovascular.setDescription(
				"This page aims to present cardiovascular system related phenotypes lines which have been produced by IMPC.");
		cardiovascular.setLink("biological-system/cardiovascular");
		bsPages.add(cardiovascular);

		model.addAttribute("pages", bsPages);

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
    public String loadBiologicalSystemPage(@PathVariable String page, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException, ExecutionException, InterruptedException {

        String pageTitle = "";
        List<String> resources = new ArrayList<>();
        resources.add("IMPC");
        List<String> anatomyIds = new ArrayList<>(); // corresponding anatomical system, used for images
        MpDTO mpDTO = null;


//        if (page.equalsIgnoreCase("deafness")) { // Need to decide if we want deafness only or top level hearing/vestibular phen
//            mpDTO = mpService.getPhenotype("MP:0005377");
//            anatomyIds.add("MA:0002443");
//            anatomyIds.add("EMAPA:36002");
//            model.addAttribute("shortDescription", "We have undertaken a deafness screen in the IMPC cohort of mouse knockout strains. We detected known deafness genes and the vast majority of loci were novel.");
//            pageTitle = "Hearing/Vestibular/Ear";
//
//        } else
        if (page.equalsIgnoreCase("cardiovascular")) {
            mpDTO = mpService.getPhenotype("MP:0005385");
            anatomyIds.add("MA:0000010");
            anatomyIds.add("EMAPA:16104");
            pageTitle = "Cardiovascular";
        }
//        } else if (page.equalsIgnoreCase("metabolism")) {
//            mpDTO = mpService.getPhenotype("MP:0005376");
//            pageTitle = "Metabolism";
//        } else if (page.equalsIgnoreCase("vision")) {
//            mpDTO = mpService.getPhenotype("MP:0005391");
//            anatomyIds.add("EMAPA:36003");
//            anatomyIds.add("MA:0002444");
//            pageTitle = "Vision";
//        } else if (page.equalsIgnoreCase("nervous")) {
//            mpDTO = mpService.getPhenotype("MP:0003631");
//            anatomyIds.add("MA:0000016");
//            anatomyIds.add("EMAPA:16469");
//            pageTitle = "Nervous phenotypes";
//        } else if (page.equalsIgnoreCase("neurological")) {
//            mpDTO = mpService.getPhenotype("MP:0005386");
//            pageTitle = "Behavioural/neurological phenotypes";
//        }

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
                "for genes with at least one " + pageTitle + " phenotype", "Number of phenotype associations to " + pageTitle, "Number of associations to other phenotypes",
                "Other phenotype calls: ", pageTitle + " phenotype calls: ", "Gene"));
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("paramToNumber", paramToNumber);
        model.addAttribute("impcImageGroups", groups);
        model.addAttribute("genePercentage", ControllerUtils.getPercentages(mpDTO.getMpId(), srService, gpService));
        model.addAttribute("phenotypes", gpService.getAssociationsCount(mpDTO.getMpId(), resources));
        model.addAttribute("mpId", mpDTO.getMpId());
        model.addAttribute("mpDTO", mpDTO);
        model.addAttribute("systemName", mpDTO.getMpTerm().replace(" phenotype", ""));
        model.addAttribute("procedures", procedures);

//        model.addAttribute("dataJs", getData(null, null, null, mpDTO.getAccession(), request) + ";");
//
        return "landing_" + page;

    }


    @ResponseBody
    @RequestMapping(value = "/orthology.jsonp", method = RequestMethod.GET)
    public String getOrthologyJson(
            @RequestParam(required = true, value = "mpId") String mpId,
            @RequestParam( required =  true, value = "phenotypeShort") String phenotypeShort,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException, ExecutionException, InterruptedException {


        Set<String> diseaseClasses = new HashSet<>();
        diseaseClasses.add("cardiac");
        diseaseClasses.add("cardiac malformations");
        diseaseClasses.add("circulatory system");

        return "var mgiSets =  " + getOrtologyDiseaseModelVennDiagram(mpId, diseaseClasses, true, false, false, phenotypeShort) + ";"
            + "var impcSets = " +  getOrtologyDiseaseModelVennDiagram(mpId, diseaseClasses, false, true, false, phenotypeShort ) + ";";

    }



    @ResponseBody
    @RequestMapping(value = "/orthology.tsv", method = RequestMethod.GET)
    public String getOrthologyDownload(
            @RequestParam(required = true, value = "mpId") String mpId,
            @RequestParam( required =  true, value = "diseaseClasses") Set<String> diseaseClasses,
            @RequestParam( required =  true, value = "phenotypeShort") String phenotypeShort,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException, ExecutionException, InterruptedException {

        StringBuffer result = new StringBuffer();
        result.append("IMPC sets\t Set label\t Genes \n");

        JSONArray jsonArray = getOrtologyDiseaseModelVennDiagram(mpId, diseaseClasses, false, true, true, phenotypeShort);
        for (int i = 0; i < jsonArray.size(); i++){
            JSONObject object = jsonArray.getJSONObject(i);
            result.append(object.getString("sets")).append("\t");
            result.append(object.containsKey("label") ? object.getString("label") : "").append("\t");
            result.append(object.getString("set")).append("\t");
            result.append("\n");
        }

        return result.toString();

    }

    private JSONArray getOrtologyDiseaseModelVennDiagram(String mpId, Set<String> diseaseClasses, Boolean mgi, Boolean impc, Boolean download, String phenotypeShort) throws IOException, SolrServerException {

        Map<String, Set<String>> sets = new HashMap<>();

        // get gene sets for human orthology (with/without)
        sets.put(" IMPC " + phenotypeShort + " phenotypes", geneService.getGenesSymbolsBy(mpId).stream().map(geneDTO -> {return geneDTO.getMarkerSymbol();}).collect(Collectors.toSet()));
        // get gene sets for IMPC and MGI disease models

        Map<String, Set<String>> genesWithDisease = phenodigmService.getGenesWithDisease(diseaseClasses);

        if (mgi && !impc){
            sets.put("Human curated (orthology) in " + phenotypeShort + " phenotypes", genesWithDisease.get("Human curated (orthology)"));
            sets.put("MGI " + phenotypeShort + "D predicted", genesWithDisease.get("MGI predicted"));
        }
        else if (!mgi && impc) {
            sets.put("IMPC " + phenotypeShort + "D predicted", genesWithDisease.get("IMPC predicted"));
            sets.put("Human curated (orthology) in " + phenotypeShort + " phenotypes", genesWithDisease.get("Human curated (orthology)"));
        } else {
            sets.putAll(genesWithDisease);
        }
        return getJsonForVenn(sets, download);
    }

    /**
     *
     * @param allSets
     * @param download true if you need data for download - all gene names, false if you need it for display - gene counts only
     * @return
     */
    private JSONArray getJsonForVenn( Map<String, Set<String>> allSets, Boolean download){

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

        // return in right format for venn diagram http://benfred.github.io/venn.js/examples/styled.html
        JSONArray result = new JSONArray();
        if (download) {
            result.addAll(wholeSets);
        } else {
            result.addAll(sets);
        }

        return result;

        }



    /**
     * @param label
     * @param sets
     * @param size
     * @return JSON objects in the format required by venn diagram library. At the moment used on biological system pages.
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
//just hold these here until we create the new set of pages from Nat, Terry and Violeta
//{
//    "title": "Embryo Landing Page",
//    "description": "Each IMPC gene knockout strain is assessed for viability by examination of litters produced from mating heterozygous animals. Embryonic lethal and subviable lines are assessed in a dedicated phenotyping pipeline.",
//    "image": "/img/Tmem100_het.jpeg" ,
//    "link": "embryo"
//  },
//  {
//    "title": "Deafness Landing Page",
//    "image": "" ,
//    "description": "We have undertaken a deafness screen in the IMPC cohort of mouse knockout strains. We detected known deafness genes and the vast majority of loci were novel.",
//    "link": "biological-system/deafness"
//  },
//  {
//    "title": "Metabolism Landing Page",
//    "image": "" ,
//    "description": "description....",
//    "link": "biological-system/metabolism"
//  },
//  {
//    "title": "Cardiovascular Landing Page",
//    "image": "render_thumbnail/211474/400/" ,
//    "description": "Cardiovascular system refers to the observable morphological and physiological characteristics of the mammalian heart, blood vessels, or circulatory system that are manifested through development and lifespan",
//    "link": "biological-system/cardiovascular"
//  },
//  {
//    "title": "Vision Landing Page",
//    "image": "" ,
//    "description": "Description...",
//    "link": "biological-system/vision"
//  },
//  {
//    "title": "Nervous System Landing Page",
//    "image": "" ,
//    "description": "Description...",
//    "link": "biological-system/nervous"
//  },
//  {
//    "title": "Neurological and Behavioural Landing Page",
//    "image": "" ,
//    "description": "Description...",
//    "link": "biological-system/neurological"
//  }
