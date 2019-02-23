package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.tomcat.jni.Time;
import org.mousephenotype.cda.solr.service.SearchGeneService;
import org.mousephenotype.cda.solr.service.SearchPhenotypeService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller("error")
public class ErrorHandlerController implements ErrorController  {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PATH = "/error";


    private final SearchGeneService searchGeneService;
    private final SearchPhenotypeService searchPhenotypeService;

    @Autowired
    public ErrorHandlerController(SearchGeneService searchGeneService, SearchPhenotypeService searchPhenotypeService) {
        this.searchGeneService = searchGeneService;
        this.searchPhenotypeService = searchPhenotypeService;
    }

    @RequestMapping(PATH)
    public String error(Model model, HttpServletRequest request)
    {
        final String URL = request.getRequestURL().toString();
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");


        model.addAttribute("timestamp", Time.now());
        model.addAttribute("error", "An error");
        model.addAttribute("status", request.getAttribute("javax.servlet.error.status_code"));

        QueryResponse geneSuggestionResponse = null;
        QueryResponse phenSuggestionResponse = null;
        List<String> phenotypeSuggestions = new ArrayList<>();
        List<String> geneSuggestions = new ArrayList<>();

        // Try to get suggestions about genes, phenotypes, and content
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

            geneSuggestionResponse = searchGeneService.searchSuggestions(originalUri, 10);
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

    @Override
    public String getErrorPath() {
        return PATH;
    }

}
