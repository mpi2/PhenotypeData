package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.bean.LandingPageDTO;
import uk.ac.ebi.phenotype.chart.AnalyticsChartProvider;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
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

    @RequestMapping(value="/landing/embryo", method = RequestMethod.GET)
    public String loadEmbryoPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();
        List<String> resources = new ArrayList<>();
        resources.add("IMPC");
        Map<String, Set<String>> viabilityRes = os.getViabilityCategories(resources);

        Map<String, Long> viabilityMap = os.getViabilityCategories(viabilityRes);
        List<ObservationService.EmbryoTableRow> viabilityTable = os.consolidateZygosities(viabilityRes);

        model.addAttribute("viabilityChart", chartsProvider.getSlicedPieChart(new HashMap<String, Long> (), viabilityMap, "", "viabilityChart"));
        model.addAttribute("viabilityTable", viabilityTable);

        return "embryo";
    }


    @RequestMapping(value="/landing/deafness", method = RequestMethod.GET)
    public String loadDeafnessPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        String mpId = "MP:0001963";
        model.addAttribute("genePercentage", ControllerUtils.getPercentages(mpId, srService, gpService));
        model.addAttribute("pageTitle", "Deafness");
        return "landing_deafness";
    }



    @RequestMapping(value = "/embryo", method = RequestMethod.GET)
    public String redirectEmbryoPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        return "redirect:/landing/embryo";
    }

    @RequestMapping(value = "/embryo/vignettes", method = RequestMethod.GET)
    public String loadVignettes(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        return "embryoVignettes";
    }


}
