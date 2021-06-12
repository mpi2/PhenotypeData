package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.SearchGeneService;
import org.mousephenotype.cda.solr.service.SearchPhenotypeService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller("error")
public class ErrorHandlerController implements ErrorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static Set<String> embeddedPages = new HashSet<>(Arrays.asList("/chart", "/experiment"));
    private static final String PATH = "/error";

    private final SearchGeneService searchGeneService;
    private final SearchPhenotypeService searchPhenotypeService;

    @Inject
    public ErrorHandlerController(SearchGeneService searchGeneService, SearchPhenotypeService searchPhenotypeService) {
        this.searchGeneService = searchGeneService;
        this.searchPhenotypeService = searchPhenotypeService;
    }

    @RequestMapping(PATH)
    public String error(Model model, HttpServletRequest request)
    {
        final String URL = request.getRequestURL().toString();
        final String forwardedUrl = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI).toString();

        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("error", "An error");
        model.addAttribute("status", request.getAttribute("javax.servlet.error.status_code"));
        model.addAttribute("showFullpage", ! embeddedPages.stream().map(forwardedUrl::contains).findFirst().orElse(false));

        System.out.println("Request URL: " + request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));
        System.out.println("Status: " + request.getAttribute("javax.servlet.error.status_code"));

        QueryResponse geneSuggestionResponse;
        QueryResponse phenSuggestionResponse;
        List<String> phenotypeSuggestions = new ArrayList<>();
        List<String> geneSuggestions = new ArrayList<>();

        // Try to get suggestions from the URL about genes, phenotypes, and content
        try {

            String originalUri = ((String)request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));
            originalUri = Arrays.stream(originalUri
                    .replaceAll("/", " ")
                    .split(" "))
                    .map(x -> x.split("="))
                    .filter(x -> ! x.equals("phenotype-archive"))
                    .filter(x -> ! x.equals("data"))
                    .map(x -> "*"+x+"*")
                    .collect(Collectors.joining(" "));

            geneSuggestionResponse = searchGeneService.searchSuggestions(originalUri, 4);
            phenSuggestionResponse = searchPhenotypeService.searchSuggestions(originalUri, 10);

            if(geneSuggestionResponse != null){
                geneSuggestions.addAll(geneSuggestionResponse
                        .getBeans(GeneDTO.class)
                        .stream()
                        .map(GeneDTO::getMarkerSymbol)
                        .collect(Collectors.toList()));

            }

            if(phenSuggestionResponse != null){
                phenotypeSuggestions.addAll(phenSuggestionResponse
                        .getBeans(MpDTO.class)
                        .stream()
                        .map(MpDTO::getMpTerm)
                        .collect(Collectors.toList()));
            }

        } catch (IOException | SolrServerException e) {
            logger.info("Exception getting suggestions for search. URL: " + URL);
            e.printStackTrace();
        }


        model.addAttribute("phenotypeSuggestions", phenotypeSuggestions);
        model.addAttribute("geneSuggestions", geneSuggestions);

        return "error";
    }

}
