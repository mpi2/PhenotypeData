package uk.ac.ebi.phenotype.web.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    ImageService imageService;

    @Autowired
    MpService mpService;

    @Autowired
    ImpressService is;

    @RequestMapping("/landing")
    public String getAlleles(
            Model model,
            HttpServletRequest request) throws IOException {

        BufferedReader in = new BufferedReader( new FileReader(new ClassPathResource("landingPages.json").getFile()));
        if (in != null) {
            String json = in.lines().collect(Collectors.joining(" "));
            ObjectMapper mapper = new ObjectMapper();
            LandingPageDTO[] readValue = mapper.readValue(json, TypeFactory.defaultInstance().constructArrayType(LandingPageDTO.class));
            model.addAttribute("pages", new ArrayList<LandingPageDTO>(Arrays.asList(readValue)));
        }
        return "landing";
    }

    @RequestMapping(value="/embryo", method = RequestMethod.GET)
    public String loadEmbryoPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();
        List<String> resources = new ArrayList<>();
        resources.add("IMPC");
        Map<String, Set<String>> viabilityRes = os.getViabilityCategories(resources);

        Map<String, Long> viabilityMap = os.getViabilityCategories(viabilityRes);
        List<CountTableRow> viabilityTable = os.consolidateZygosities(viabilityRes);

        model.addAttribute("viabilityChart", chartsProvider.getSlicedPieChart(new HashMap<String, Long> (), viabilityMap, "", "viabilityChart"));
        model.addAttribute("viabilityTable", viabilityTable);

        return "embryo";
    }


    @RequestMapping(value="/biological-system/{page}",  method = RequestMethod.GET)
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
        } else if (page.equalsIgnoreCase("cardiovascular")){
            mpDTO = mpService.getPhenotype("MP:0005385");
            anatomyIds.add("MA:0000010");
            anatomyIds.add("EMAPA:16104");
            pageTitle = "Cardiovascular";
        } else if (page.equalsIgnoreCase("metabolism")){
            mpDTO = mpService.getPhenotype("MP:0005376");
            pageTitle = "Metabolism";
        } else if (page.equalsIgnoreCase("vision")){
            mpDTO = mpService.getPhenotype("MP:0005391");
            anatomyIds.add("EMAPA:36003");
            anatomyIds.add("MA:0002444");
            pageTitle = "Vision";
        } else if (page.equalsIgnoreCase("nervous")){
            mpDTO = mpService.getPhenotype("MP:0003631");
            anatomyIds.add("MA:0000016");
            anatomyIds.add("EMAPA:16469");
            pageTitle = "Nervous phenotypes";
        } else if (page.equalsIgnoreCase("neurological")){
            mpDTO = mpService.getPhenotype("MP:0005386");
            pageTitle = "Behavioural/neurological phenotypes";
        }

        // IMPC image display at the bottom of the page
        List<Group> groups = imageService.getPhenotypeAssociatedImages(null, mpDTO.getMpId(), anatomyIds, true, 1);
        Map<String, String> paramToNumber=new HashMap<>();
        for(Group group: groups){
            if(!paramToNumber.containsKey(group.getGroupValue())){
                paramToNumber.put(group.getGroupValue(), Long.toString(group.getResult().getNumFound()));
            }
        }
        ArrayList<ImpressDTO> procedures = new ArrayList<>();
        procedures.addAll(is.getProceduresByMpTerm(mpDTO.getMpId(), true));
        Collections.sort(procedures, ImpressDTO.getComparatorByProcedureName());


        model.addAttribute("phenotypeChart", ScatterChartAndTableProvider.getScatterChart("phenotypeChart", gpService.getTopLevelPhenotypeIntersection(mpDTO.getMpId()), "Gene pleiotropy",
            "for genes with at least one " + pageTitle + " phenotype", "Number of associations to " + pageTitle , "Number of associations to other phenotypes",
                "Other phenotype calls: ", pageTitle + " phenotype calls: "));
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("paramToNumber", paramToNumber);
        model.addAttribute("impcImageGroups",groups);
        model.addAttribute("genePercentage", ControllerUtils.getPercentages(mpDTO.getMpId(), srService, gpService));
        model.addAttribute("phenotypes", gpService.getAssociationsCount(mpDTO.getMpId(), resources));
        model.addAttribute("mpId", mpDTO.getMpId());
        model.addAttribute("mpDTO", mpDTO);
        model.addAttribute("systemName", mpDTO.getMpTerm().replace(" phenotype", "" ));
        model.addAttribute("procedures", procedures);


        return "landing_" + page;

    }



//    @RequestMapping(value = "/embryo", method = RequestMethod.GET)
//    public String redirectEmbryoPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
//            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {
//
//        return "redirect:/landing/embryo";
//    }

    @RequestMapping(value = "/embryo/vignettes", method = RequestMethod.GET)
    public String loadVignettes(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        return "embryoVignettes";
    }


}
