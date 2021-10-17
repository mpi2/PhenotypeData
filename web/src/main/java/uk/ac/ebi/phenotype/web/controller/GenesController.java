/**
 * ****************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 * *****************************************************************************
 */
package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.bean.ExpressionImagesBean;
import org.mousephenotype.cda.solr.generic.util.PhenotypeCallSummarySolr;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.GeneTopLevelMpTerms;
import org.mousephenotype.cda.solr.web.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenodigm2.Disease;
import uk.ac.ebi.phenodigm2.DiseaseModelAssociation;
import uk.ac.ebi.phenodigm2.GeneDiseaseAssociation;
import uk.ac.ebi.phenodigm2.WebDao;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.generic.util.RegisterInterestUtils;
import uk.ac.ebi.phenotype.generic.util.SolrIndex2;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryBySex;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryType;
import uk.ac.ebi.phenotype.util.PublicationFetcher;
import uk.ac.ebi.phenotype.web.dao.ReferenceService;
import uk.ac.ebi.phenotype.web.dto.Publication;
import uk.ac.ebi.phenotype.web.util.FileExportUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class GenesController {

    private final Logger logger = LoggerFactory.getLogger(GenesController.class);
    private static final int numberOfImagesToDisplay = 5;

    private final PhenotypeSummaryDAO phenSummary;
    private final ImagesSolrDao imagesSolrDao;
    private final PhenotypeCallSummarySolr phenotypeCallSummaryService;
    private final ObservationService observationService;
    private final SolrIndex solrIndex;
    private final SolrIndex2 solrIndex2;
    private final ImageService imageService;
    private final ExpressionService expressionService;
    private final GeneService geneService;
    private final StatisticalResultService statisticalResultService;
    private final OrderService orderService;
    private final ImpressService impressService;
    private final ReferenceService referenceService;
    private final WebDao phenoDigm2Dao;
    private final RegisterInterestUtils riUtils;

    @Resource(name = "globalConfiguration")
    Map<String, String> config;

    private String cmsBaseUrl;

    SearchGeneService searchGeneService;

    @Inject
    public GenesController(PhenotypeCallSummarySolr phenotypeCallSummaryService,
                           PhenotypeSummaryDAO phenSummary,
                           ImagesSolrDao imagesSolrDao,
                           ObservationService observationService,
                           SolrIndex solrIndex,
                           SolrIndex2 solrIndex2,
                           WebDao phenoDigm2Dao,
                           ImageService imageService,
                           ExpressionService expressionService,
                           RegisterInterestUtils riUtils,
                           GeneService geneService,
                           ImpressService impressService,
                           ReferenceService referenceService,
                           OrderService orderService,
                           @Named("statistical-result-service") StatisticalResultService statisticalResultService,
                           SearchGeneService searchGeneService) {
        this.phenotypeCallSummaryService = phenotypeCallSummaryService;
        this.phenSummary = phenSummary;
        this.imagesSolrDao = imagesSolrDao;
        this.observationService = observationService;
        this.solrIndex = solrIndex;
        this.solrIndex2 = solrIndex2;
        this.phenoDigm2Dao = phenoDigm2Dao;
        this.imageService = imageService;
        this.expressionService = expressionService;
        this.riUtils = riUtils;
        this.geneService = geneService;
        this.impressService = impressService;
        this.referenceService = referenceService;
        this.orderService = orderService;
        this.statisticalResultService = statisticalResultService;
        this.searchGeneService = searchGeneService;
    }

    @PostConstruct
    private void postConstruct() {

        cmsBaseUrl = config.get("cmsBaseUrl");
    }

    private static final List<String> genesWithVignettes = Arrays.asList("MGI:1913761", "MGI:97491", "MGI:1922814", "MGI:3039593", "MGI:1915138", "MGI:1915138", "MGI:1195985", "MGI:102806", "MGI:1195985", "MGI:1915138", "MGI:1337104", "MGI:3039593", "MGI:1922814", "MGI:97491", "MGI:1928849", "MGI:2151064", "MGI:104606", "MGI:103226", "MGI:1920939", "MGI:95698", "MGI:1915091", "MGI:1924285", "MGI:1914797", "MGI:1351614", "MGI:2147810");
    private static final List<String> phenotypeGroups = Arrays.asList("mortality/aging", "embryo phenotype", "reproductive system phenotype", "growth/size/body region phenotype", "homeostasis/metabolism phenotype or adipose tissue phenotype", "behavior/neurological phenotype or nervous system phenotype", "cardiovascular system phenotype", "respiratory system phenotype", "digestive/alimentary phenotype or liver/biliary system phenotype", "renal/urinary system phenotype", "limbs/digits/tail phenotype", "skeleton phenotype", "immune system phenotype or hematopoietic system phenotype", "muscle phenotype", "integument phenotype or pigmentation phenotype", "craniofacial phenotype", "hearing/vestibular/ear phenotype", "taste/olfaction phenotype", "endocrine/exocrine gland phenotype", "vision/eye phenotype");
    private static final List<String> phenotypeGroupIcons = Arrays.asList("fas fa-skull-crossbones", "impc-embryo", "impc-sperm", "fas fa-balance-scale-right", "fas fa-bolt", "fas fa-brain", "fas fa-heart", "fas fa-lungs", "fas fa-stomach", "fas fa-kidneys", "fas fa-hand-paper", "fas fa-bone", "fas fa-tint", "fas fa-dumbbell", "fas fa-diagnoses", "fas fa-meh", "fas fa-ear", "impc-nose", "impc-trachea", "fas fa-eye");


    /**
     * Runs when the request missing an accession ID. This redirects to the
     * search page which defaults to showing all genes in the list
     */
    @RequestMapping("/genes")
    public String rootForward() {
        return "redirect:/search";
    }

    /**
     * Prints out the request object
     */
    @RequestMapping("/genes/print-request")
    public ResponseEntity<String> printRequest(HttpServletRequest request) {

        Enumeration<String> s = request.getHeaderNames();

        while (s.hasMoreElements()) {
            String header = s.nextElement();
            Enumeration<String> headers = request.getHeaders(header);

            while (headers.hasMoreElements()) {
                String actualHeader = headers.nextElement();
            }
        }

        HttpHeaders resp = new HttpHeaders();
        resp.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<String>(request.toString(), resp, HttpStatus.CREATED);
    }

    @RequestMapping("/genes/{acc}")
    public String genes(@PathVariable String acc,
                        @RequestParam(value = "heatmap", required = false, defaultValue = "false") Boolean showHeatmap,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException, SolrServerException {

        String debug = request.getParameter("debug");
        boolean d = debug != null && debug.equals("true");
        if (d) {
            model.addAttribute("debug", "true");
        }

        // 2020-10-02 (mrelac) Catch this exception and display the 'identifierError' page.  Also provide suggestions.
        //                     Failing to catch inserts a long stack trace in the log file.
        //                     Prints out the first stack trace frame to assist with identifying the error.
        try {
            processGeneRequest(acc, model, request);
        } catch (Exception e) {
            logger.error(
                    String.format("processGeneRequest(acc, model, request) exception: %s\n%s\n%s",
                            e.getLocalizedMessage(),
                            e.getStackTrace()[0],
                            e.getStackTrace()[1]));

            List<String> geneSuggestions = new ArrayList<>();
            QueryResponse geneSuggestionResponse;
            geneSuggestionResponse = searchGeneService.searchSuggestions(acc, 6);

            if (geneSuggestionResponse != null) {
                geneSuggestions.addAll(geneSuggestionResponse
                        .getBeans(GeneDTO.class)
                        .stream()
                        .map(GeneDTO::getMarkerSymbol)
                        .collect(Collectors.toList()));

            }

            model.addAttribute("geneSuggestions", geneSuggestions);
            return "identifierError";
        }

        return "genes";
    }


    @RequestMapping("/genes/export/{acc}")
    public void genesExport(@PathVariable String acc,
                            @RequestParam(required = true, value = "fileType") String fileType,
                            @RequestParam(required = true, value = "fileName") String fileName,
                            @RequestParam(required = false, value = "top_level_mp_term_name") List<String> topLevelMpTermName,
                            @RequestParam(required = false, value = "resource_fullname") List<String> resourceFullname,
                            @RequestParam(value = "heatmap", required = false, defaultValue = "false") Boolean showHeatmap,
                            Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes attributes)
            throws URISyntaxException, IOException, SolrServerException, JSONException {

        PhenotypeFacetResult phenoResult = phenotypeCallSummaryService.getPhenotypeCallByGeneAccessionAndFilter(acc, topLevelMpTermName, resourceFullname);
        List<PhenotypeCallSummaryDTO> phenotypeList = phenoResult.getPhenotypeCallSummaries();
        List<GenePageTableRow> phenotypes = new ArrayList<>();
        String url = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();

        for (PhenotypeCallSummaryDTO pcs : phenotypeList) {
            List<String> sex = new ArrayList<>();
            sex.add(pcs.getSex().toString());
            // On the phenotype pages we only display stats graphs as evidence, the MPATH links can't be linked from phen pages
            GenePageTableRow pr = new GenePageTableRow(pcs, url, cmsBaseUrl);
            phenotypes.add(pr);
        }

        List<String> dataRows = new ArrayList<>();
        dataRows.add(GenePageTableRow.getTabbedHeader());
        for (GenePageTableRow row : phenotypes) {
            dataRows.add(row.toTabbedString());
        }

        String filters = null;
        FileExportUtils.writeOutputFile(response, dataRows, fileType, fileName, filters);
    }

    private void processGeneRequest(String acc, Model model, HttpServletRequest request)
            throws GenomicFeatureNotFoundException, URISyntaxException, IOException, SQLException, SolrServerException {

        GeneDTO gene = geneService.getGeneById(acc);

        if (gene == null) {
            logger.warn("Gene object from solr for " + acc + " can't be found.");
            throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
        }

        /**
         * PRODUCTION STATUS (SOLR)
         */
        String geneStatus = gene.getAssignmentStatus();
        model.addAttribute("geneStatus", geneStatus);

        /**
         * Phenotype Summary
         */
        Map<ZygosityType, PhenotypeSummaryBySex> phenotypeSummaryObjects = null;
        Map<String, String> mpGroupsSignificant = new HashMap<>();
        Map<String, String> mpGroupsNotSignificant = new HashMap<>();

        String prodStatusIcons = "Production status not available.";
        // Get list of tripels of pipeline, allele acc, phenotyping center
        // to link to an experiment page will all data
        Set<String> viabilityCalls = observationService.getViabilityForGene(acc);

        try {

            // Get the lists of significant and not significant top level MP terms
            GeneTopLevelMpTerms geneTopLevelMpTerms = geneService.getTopLevelMpTerms(gene);

            if (geneTopLevelMpTerms.getSignificantTopLevelMpTerms() != null) {
                mpGroupsSignificant = geneTopLevelMpTerms.getSignificantTopLevelMpTerms().stream()
                        .map(PhenotypeSummaryType::getGroup)
                        .collect(Collectors.toMap(
                                Function.identity(), //key
                                value -> "true", //value
                                (existing, replacement) -> existing));
            }

            if (geneTopLevelMpTerms.getNotSignificantTopLevelMpTerms() != null) {
                final Set<String> significant = mpGroupsSignificant.keySet();
                mpGroupsNotSignificant = geneTopLevelMpTerms.getNotSignificantTopLevelMpTerms().stream()
                        .map(PhenotypeSummaryType::getGroup).filter(s -> !significant.contains(s))
                        .collect(Collectors.toMap(
                                Function.identity(),
                                value -> "false",
                                (existing, replacement) -> existing));
            }

            if (observationService.hasBodyWeight(acc)) {
                model.addAttribute("bodyWeight", true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ERROR: ", e);
        }

        // Register Interest setup
        boolean loggedIn = false;
        boolean following = false;
        try {
            loggedIn = riUtils.isLoggedIn();
            if (loggedIn) {
                following = riUtils.getGeneAccessionIds().contains(acc);
            }

        } catch (Exception e) {
            // Nothing to do. Handle as unauthenticated.
        }

        // Register Interest model requirements
        model.addAttribute("acc", acc);
        model.addAttribute("isLoggedIn", loggedIn);
        model.addAttribute("isFollowing", following);


        try {
            getExperimentalImages(acc, model);
            getExpressionImages(acc, model);
            getImpcImages(acc, model);
            getImpcExpressionImages(acc, model);
            getImpcEmbryoExpression(acc, model);

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
            logger.info("images solr not available");
            model.addAttribute("imageErrors", "Something is wrong Images are not being returned when normally they would");
        }

        // ES Cell and IKMC Allele check (Gautier)
        String solrCoreName = "allele";
        String mode = "ikmcAlleleGrid";
        int countIKMCAlleles = 0;
        boolean ikmcError = false;

        try {
            countIKMCAlleles = solrIndex.getNumFound("allele_name:" + gene.getMarkerSymbol(), solrCoreName, mode, "");
        } catch (Exception e) {
            model.addAttribute("countIKMCAllelesError", Boolean.TRUE);
            e.printStackTrace();
        }

        processPhenotypes(acc, model, null, null, request);

        model.addAttribute("viabilityCalls", viabilityCalls);
        model.addAttribute("phenotypeSummaryObjects", phenotypeSummaryObjects);
        model.addAttribute("prodStatusIcons", prodStatusIcons);
        model.addAttribute("gene", gene);
        model.addAttribute("request", request);
        model.addAttribute("acc", acc);
        model.addAttribute("isLive", new Boolean((String) request.getAttribute("liveSite")));
        boolean phenotypeStarted = gene.getPhenotypeStatus() != null && gene.isPhenotypingDataAvailable();
        model.addAttribute("phenotypeStarted", phenotypeStarted);
        model.addAttribute("attemptRegistered", geneService.checkAttemptRegistered(acc));
        model.addAttribute("significantTopLevelMpGroups", mpGroupsSignificant);
        model.addAttribute("notsignificantTopLevelMpGroups", mpGroupsNotSignificant);
        model.addAttribute("allMeasurementsNumber", geneService.getAllDataCount(acc));

        model.addAttribute("measurementsChartNumber", statisticalResultService.getParameterCountByGene(acc));
        model.addAttribute("phenotypeGroups", phenotypeGroups);
        model.addAttribute("phenotypeGroupIcons", phenotypeGroupIcons);
        if (genesWithVignettes.contains(acc)) {
            model.addAttribute("hasVignette", true);
        }
        // add in the disease predictions from phenodigm              
        processDisease(acc, model);

        model.addAttribute("countIKMCAlleles", countIKMCAlleles);
        logger.debug("CHECK IKMC allele error : " + ikmcError);
        logger.debug("CHECK IKMC allele found : " + countIKMCAlleles);

        //process ardering section
        List<OrderTableRow> orderRows = orderService.getOrderTableRows(acc, null, false);
        model.addAttribute("acc", acc);
        model.addAttribute("orderRows", orderRows);

        //for cre products link at bottom of table
        //model.addAttribute("alleleProductsCre2", orderService.getCreData(acc));
        model.addAttribute("creLineAvailable", orderService.crelineAvailable(acc));

        /*
        PUBLICATIONS
         */
        PublicationFetcher publicationFetcher = new PublicationFetcher(referenceService, PublicationFetcher.PublicationType.ACCEPTED_IMPC_PUBLICATION);
        publicationFetcher.setFilter(gene.getMarkerSymbol());

        // Filter out publications not directly related to this gene
        final List<Publication> publications = publicationFetcher.getAllPublications().stream()
                .filter(x -> x.getAlleles().stream()
                        .anyMatch(y-> y.getAlleleSymbol().contains(gene.getMarkerSymbol())))
                .collect(Collectors.toList());
        model.addAttribute("publications", publications);


    }


    /**
     * Return the phenotypes section of the gene page
     */
    @RequestMapping("/genesPhenoFrag/{acc}")
    public String genesPhenoFrag(@PathVariable String acc,
                                 @RequestParam(required = false, value = "top_level_mp_term_name") List<String> topLevelMpTermName,
                                 @RequestParam(required = false, value = "resource_fullname") List<String> resourceFullname,
                                 Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws URISyntaxException, IOException, SolrServerException {

        processPhenotypes(acc, model, topLevelMpTermName, resourceFullname, request);

        return "PhenoFrag";
    }

    private Map<String, Map<String, Integer>> sortPhenFacets(Map<String, Map<String, Integer>> phenFacets) {

        for (String key : phenFacets.keySet()) {
            phenFacets.put(key, new TreeMap<>(phenFacets.get(key)));
        }
        return phenFacets;
    }

    private List<GenePageTableRow> processPhenotypes(String acc, Model model, List<String> topLevelMpTermName, List<String> resourceFullname, HttpServletRequest request)
            throws IOException, URISyntaxException, SolrServerException {

        List<PhenotypeCallSummaryDTO> phenotypeList;
        PhenotypeFacetResult phenoResult;


        //for image links we need a query that brings back mp terms and colony_ids that have mp terms
        //http://wp-np2-e1.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1913955%22&fq=mp_id:*&facet=true&facet.mincount=1&facet.limit=-1&facet.field=colony_id&facet.field=mp_id&facet.field=mp_term&rows=0
        Map<String, Set<String>> mpToColony = imageService.getImagePropertiesThatHaveMp(acc);

        try {

            phenoResult = phenotypeCallSummaryService.getPhenotypeCallByGeneAccessionAndFilter(acc, topLevelMpTermName, resourceFullname);

            phenotypeList = phenoResult.getPhenotypeCallSummaries();
            Map<String, Map<String, Integer>> phenoFacets = phenoResult.getFacetResults();
            // sort facets
            model.addAttribute("phenoFacets", sortPhenFacets(phenoFacets));

        } catch (JSONException e) {
            logger.error("ERROR GETTING PHENOTYPE LIST");
            e.printStackTrace();
            phenotypeList = new ArrayList<>();
        }

        // This is a map because we need to support lookups
        HashMap<Integer, DataTableRow> phenotypes = new HashMap<>();

        for (PhenotypeCallSummaryDTO pcs : phenotypeList) {
            // Temporary fix for ABR data
            if (pcs.getProcedure() != null && pcs.getProcedure().getStableId().contains("ABR")) {
                pcs.setpValue(statisticalResultService.resolveAbrPValue(pcs.getGene().getAccessionId(), pcs.getPipeline().getStableId(), pcs.getProcedure().getStableId(), pcs.getParameter().getStableId(), pcs.getColonyId(), pcs.getSex().getName()));
            }
            DataTableRow pr = new GenePageTableRow(pcs, request.getAttribute("baseUrl").toString(), cmsBaseUrl);

            // Collapse rows on sex	and p-value		
            if (phenotypes.containsKey(pr.hashCode())) {
                pr = phenotypes.get(pr.hashCode());
                TreeSet<String> sexes = new TreeSet<String>();
                for (String s : pr.getSexes()) {
                    sexes.add(s);
                }
                sexes.add(pcs.getSex().toString());
                pr.setSexes(new ArrayList<String>(sexes));
                // Display lowest p-value only
                if (pr.getpValue() > pcs.getpValue()) {
                    pr.setpValue(pcs.getpValue());
                }
                //now we severely collapsing rows by so we need to store these as an list
                List<PhenotypeCallUniquePropertyBean> phenotypeCallUniquePropertyBeans = pr.getPhenotypeCallUniquePropertyBeans();
                //keep the set of properties as a set so we can generate unique graph urls if necessary
                PhenotypeCallUniquePropertyBean propBean = new PhenotypeCallUniquePropertyBean();
                if (pcs.getProject() != null && pcs.getProject().getId() != null) {
                    propBean.setProject(Integer.parseInt(pcs.getProject().getId()));
                }
                if (pcs.getPhenotypingCenter() != null) {
                    propBean.setPhenotypingCenter(pcs.getPhenotypingCenter());
                }
                if (pcs.getProcedure() != null) {
                    propBean.setProcedure(pcs.getProcedure());
                }
                if (pcs.getParameter() != null) {
                    propBean.setParameter(pcs.getParameter());
                }
                if (pcs.getPipeline() != null) {
                    propBean.setPipeline(pcs.getPipeline());
                }

                if (pcs.getAllele() != null) {
                    propBean.setAllele(pcs.getAllele());
                }
                if (pcs.getgId() != null) {
                    propBean.setgId(pcs.getgId());
                }
                phenotypeCallUniquePropertyBeans.add(propBean);
                pr.setPhenotypeCallUniquePropertyBeans(phenotypeCallUniquePropertyBeans);

            }

            if (pr.getTopLevelPhenotypeTerms() != null) {
                Set<String> topLevelMpGroups = new TreeSet<>();
                for (BasicBean topMp : pr.getTopLevelPhenotypeTerms()) {
                    String group = PhenotypeSummaryType.getGroup(topMp.getName());
                    topLevelMpGroups.add(group);

                }
                pr.setTopLevelMpGroups(topLevelMpGroups);
            }

            //We need to build the urls now we have more parameters for multiple graphs
            //this should be refactored so we make fewer requests
            //pr.buildEvidenceLink(request.getAttribute("baseUrl").toString());
            //need to formulate a solr query that will let us know if we have mp terms with images and then generate urls based on that result so we don't do so many requests
            //need to loop over the property beans get the unique set of properties 
            phenotypes.put(pr.hashCode(), pr);
        }

        //now we have all the rows as they should be lets see if they need image links and if so generate them and add them to the row
        for (DataTableRow row : phenotypes.values()) {

            row.buildEvidenceLink(request.getAttribute("baseUrl").toString());
            String rowMpId = row.getPhenotypeTerm().getId();

            if (mpToColony.containsKey(rowMpId)) {
                Set<String> colonyIds = mpToColony.get(rowMpId);
                if (colonyIds.contains(row.getColonyId())) {
                    EvidenceLink imageLink = new EvidenceLink();
                    imageLink.setDisplay(true);
                    imageLink.setIconType(EvidenceLink.IconType.IMAGE);
                    String url = request.getAttribute("baseUrl").toString() + "/imageComparator?acc=" + row.getGene().getAccessionId() + "&mp_id=" + row.getPhenotypeTerm().getId() + "&colony_id=" + row.getColonyId();
                    imageLink.setUrl(url);
                    row.setImagesEvidenceLink(imageLink);
                }
            }
        }

        List<GenePageTableRow> l = phenotypes.values().stream().map(x -> ((GenePageTableRow) x)).collect(Collectors.toList());
        List<GenePageTableRow> histopath = l.stream().filter(phenotype -> phenotype.getEvidenceLink().getUrl().contains("histopath")).collect(Collectors.toList());
        l = l.stream().filter(phenotype -> !phenotype.getEvidenceLink().getUrl().contains("histopath")).collect(Collectors.toList());
        boolean hasHistopath  = observationService.hasHistopathData(acc);

        model.addAttribute("rowsForPhenotypeTable", l);
        model.addAttribute("rowsForHistopathTable", histopath);
        model.addAttribute("hasHistopath", hasHistopath);
        return l;
    }

    /**
     * Get the first 5 wholemount expression images if available
     *
     * @param acc   the gene to get the images for
     * @param model the model to add the images to
     * @throws SolrServerException, IOException
     */
    private void getExpressionImages(String acc, Model model)
            throws SolrServerException, IOException {

        QueryResponse solrExpressionR = imagesSolrDao.getExpressionFacetForGeneAccession(acc);
        if (solrExpressionR == null) {
            logger.error("no response from solr data source for acc=" + acc);
            return;
        }

        List<FacetField> expressionfacets = solrExpressionR.getFacetFields();
        if (expressionfacets == null) {
            logger.info("no expression facets from solr data source for acc=" + acc);
            return;
        }

        Map<String, SolrDocumentList> facetToDocs = new HashMap<String, SolrDocumentList>();

        for (FacetField facet : expressionfacets) {
            if (facet.getValueCount() != 0) {
                for (Count value : facet.getValues()) {
                    QueryResponse response = imagesSolrDao.getDocsForGeneWithFacetField(acc, "selected_top_level_ma_term", value.getName(), "expName:\"Wholemount Expression\"", 0, numberOfImagesToDisplay);
                    if (response != null) {
                        facetToDocs.put(value.getName(), response.getResults());
                    }
                }
            }
            model.addAttribute("expressionFacets", expressionfacets.get(0).getValues());
            model.addAttribute("expFacetToDocs", facetToDocs);
        }
    }

    /**
     * Get the first 5 images for aall but the wholemount expression images if
     * available
     *
     * @param acc   the gene to get the images for
     * @param model the model to add the images to
     * @throws SolrServerException, IOException
     */
    private void getExperimentalImages(String acc, Model model)
            throws SolrServerException, IOException {

        QueryResponse solrR = imagesSolrDao.getExperimentalFacetForGeneAccession(acc);
        if (solrR == null) {
            logger.error("no response from solr data source for acc=" + acc);
            return;
        }

        List<FacetField> facets = solrR.getFacetFields();
        if (facets == null) {
            logger.error("no facets from solr data source for acc=" + acc);
            return;
        }

        Map<String, SolrDocumentList> facetToDocs = new HashMap<String, SolrDocumentList>();
        List<Count> filteredCounts = new ArrayList<Count>();

        for (FacetField facet : facets) {
            if (facet.getValueCount() != 0) {

                // get rid of wholemount expression facet
                for (Count count : facets.get(0).getValues()) {
                    if (!count.getName().equals("Wholemount Expression")) {
                        filteredCounts.add(count);
                    }
                }

                for (Count count : facet.getValues()) {
                    if (!count.getName().equals("Wholemount Expression")) {

                        // get 5 images if available for this experiment type
                        QueryResponse response = imagesSolrDao.getDocsForGeneWithFacetField(acc, "expName", count.getName(), "", 0, numberOfImagesToDisplay);
                        if (response != null) {
                            facetToDocs.put(count.getName(), response.getResults());
                        }
                    }
                }
            }

            model.addAttribute("solrFacets", filteredCounts);
            model.addAttribute("facetToDocs", facetToDocs);
        }
    }

    /**
     * Get the first 5 images for impc experimental images if available
     *
     * @param acc   the gene to get the images for
     * @param model the model to add the images to
     * @throws SolrServerException, IOException
     */
    private void getImpcImages(String acc, Model model)
            throws SolrServerException, IOException {

        List<Group> groups = imageService.getPhenotypeAssociatedImages(acc, null, null, true, 1);
        Map<String, String> paramToNumber = new HashMap<>();
        for (Group group : groups) {
            if (!paramToNumber.containsKey(group.getGroupValue())) {
                paramToNumber.put(group.getGroupValue(), Long.toString(group.getResult().getNumFound()));
            }
        }
        groups.sort((group1, group2) -> {
            String fileType1 = "";
            String fileType2 = "";
            if (group1.getResult().size() > 0) {
                fileType1 = group1.getResult().get(0).containsKey("file_type") ? group1.getResult().get(0).get("file_type").toString() : "";
            }
            if (group2.getResult().size() > 0) {
                fileType2 = group2.getResult().get(0).containsKey("file_type") ? group2.getResult().get(0).get("file_type").toString() : "";
            }

            boolean group1IsImage = !fileType1.contains("octet-stream") && !fileType1.contains("fcs") && !fileType1.contains("pdf");
            boolean group2IsImage = !fileType2.contains("octet-stream") && !fileType2.contains("fcs") && !fileType2.contains("pdf");

            if (group1IsImage && !group2IsImage) {
                return -1;
            }

            if (!group1IsImage && group2IsImage) {
                return 1;
            }

            return 0;
        });
        model.addAttribute("paramToNumber", paramToNumber);
        model.addAttribute("impcImageGroups", groups);

    }

    /**
     * Get the first 5 wholemount expression images if available
     *
     * @param acc   the gene to get the images for
     * @param model the model to add the images to
     * @throws SolrServerException, IOException
     * @throws SQLException
     */
    private void getImpcExpressionImages(String acc, Model model)
            throws SolrServerException, IOException, SQLException {
        boolean overview = true;
        boolean embryoOnly = false;

        ExpressionImagesBean section = expressionService.getLacImageDataForGene(acc, null, "IMPC_ALZ_075_001", overview);
        ExpressionImagesBean wholemount = expressionService.getLacImageDataForGene(acc, null, "IMPC_ALZ_076_001", overview);
        model.addAttribute("sectionExpressionImagesBean", section);
        model.addAttribute("wholemountExpressionImagesBean", wholemount);

        expressionService.getExpressionDataForGene(acc, model, embryoOnly);
    }

    /**
     * Get the first 5 wholemount expression images if available
     *
     * @param acc   the gene to get the images for
     * @param model the model to add the images to
     * @throws SolrServerException, IOException
     * @throws SQLException
     */
    private void getImpcEmbryoExpression(String acc, Model model)
            throws SolrServerException, IOException, SQLException {
        //good test gene:Nxn with selected top level emap terms
        boolean overview = true;
        boolean embryoOnly = true;

        //impcEmbryoExpressionFacetToDocsWholemount
        ExpressionImagesBean wholemount = expressionService.getLacImageDataForGene(acc, null, "IMPC_ELZ_064_001", overview);
        ExpressionImagesBean section = expressionService.getLacImageDataForGene(acc, null, "IMPC_ELZ_063_001", overview);
        model.addAttribute("sectionExpressionImagesEmbryoBean", section);
        model.addAttribute("wholemountExpressionImagesEmbryoBean", wholemount);

        expressionService.getExpressionDataForGene(acc, model, embryoOnly);

    }


    /**
     * @throws IOException
     */
    @RequestMapping("/genomeBrowser/{acc}")
    public String genomeBrowser(@PathVariable String acc, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SolrServerException {

        GeneDTO gene = geneService.getGeneById(acc);
        model.addAttribute("geneDTO", gene);
        if (gene == null) {
            logger.warn("Gene object from solr for " + acc + " can't be found.");
            throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
        }

        List<String> ensemblIds = new ArrayList<String>();
        List<String> vegaIds = new ArrayList<String>();
        List<String> ncbiIds = new ArrayList<String>();
        List<String> ccdsIds = new ArrayList<String>();

        if (gene.getEnsemblGeneIds() != null) {
            ensemblIds = gene.getEnsemblGeneIds();
        }
        if (gene.getVegaIds() != null) {
            vegaIds = gene.getVegaIds();
        }
        if (gene.getCcdsIds() != null) {
            ccdsIds = gene.getCcdsIds();
        }
        if (gene.getNcbiIds() != null) {
            ncbiIds = gene.getNcbiIds();
        }

        model.addAttribute("ensemblIds", ensemblIds);
        model.addAttribute("vegaIds", vegaIds);
        model.addAttribute("ncbiIds", ncbiIds);
        model.addAttribute("ccdsIds", ccdsIds);

        model.addAttribute("gene", gene);
        return "genomeBrowser";
    }

    @RequestMapping("/genesAllele2/{acc}")
    public String genesAllele2(@PathVariable String acc, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, Exception {

        List<Map<String, Object>> constructs2 = solrIndex2.getGeneProductInfo(acc, false);//creLine is false as don't want to show creLine stuff on gene page apart from a link
        Map<String, Object> creProducts = null;

        if (constructs2 != null) {
            creProducts = constructs2.get(constructs2.size() - 1);
            constructs2.remove(constructs2.size() - 1);
        }

        model.addAttribute("alleleProducts2", constructs2);
        model.addAttribute("alleleProductsCre2", creProducts);

        String debug = request.getParameter("debug");
        boolean d = debug != null && debug.equals("true");
        if (d) {
            model.addAttribute("debug", "true");
        }

        return "genesAllele2_frag";
    }

    /**
     * Adds disease-related info to the model using the Phenodigm2 core.
     *
     * @param acc   Geneid
     * @param model
     */
    private void processDisease(String acc, Model model) {

        // fetch diseases that are linked to a gene via annotations/curation
        logger.debug(String.format("%s - getting gene-disease associations for gene ", acc));
        List<GeneDiseaseAssociation> geneAssociations = phenoDigm2Dao.getGeneToDiseaseAssociations(acc);

        // fetch just the ids, and encode them into an array
        HashSet<String> curatedDiseases = new HashSet<>();
        for (Disease assoc : geneAssociations) {
            curatedDiseases.add(assoc.getId());
        }
        String curatedJsArray = String.join("\", \"", curatedDiseases);
        if (curatedDiseases.size() > 0) {
            curatedJsArray = "[\"" + curatedJsArray + "\"]";
        } else {
            curatedJsArray = "[]";
        }
        model.addAttribute("curatedDiseases", curatedJsArray);

        // fetch models that have this gene
        List<DiseaseModelAssociation> modelAssociations = phenoDigm2Dao.getGeneToDiseaseModelAssociations(acc);
        logger.debug("Found " + modelAssociations.size() + " associations");

        boolean hasModelsByOrthology = modelAssociations.stream().anyMatch(x -> curatedDiseases.contains(x.getDiseaseId()));

        final List<DiseaseModelAssociationDisplay> displayList = modelAssociations.stream()
                .map(x -> new DiseaseModelAssociationDisplay(
                        x.getDiseaseId(),
                        x.getDiseaseTerm(),
                        x.getDiseaseMatchedPhenotypes(),
                        x.getAvgNorm(),
                        x.getMaxNorm()))
                .sorted()
                .distinct()
                .collect(Collectors.toList());
        // Preserve only the maximum associated disease term for this gene
        Map<String, DiseaseModelAssociationDisplay> maxByPhenotype = new HashMap<>();
        for (DiseaseModelAssociationDisplay d : displayList) {
            if (! maxByPhenotype.containsKey(d.getDiseaseId())) {
                maxByPhenotype.put(d.getDiseaseId(), d);
            }
            else {
                if (maxByPhenotype.get(d.getDiseaseId()).getPhenodigmScore() < d.getPhenodigmScore()) {
                    maxByPhenotype.put(d.getDiseaseId(), d);
                }
            }
        }
        model.addAttribute("modelAssociations", maxByPhenotype.values().stream().sorted().distinct().collect(Collectors.toList()));

        // Keep those models that are annotated directly to this gene
        final List<DiseaseModelAssociationDisplay> displayListAnnotations = displayList.stream()
                .filter(x -> curatedDiseases.contains(x.getDiseaseId()))
                .collect(Collectors.toList());

        // Preserve only the maximum associated disease term for this gene
        Map<String, DiseaseModelAssociationDisplay> max = new HashMap<>();
        for (DiseaseModelAssociationDisplay d : displayListAnnotations) {
            if (! max.containsKey(d.getDiseaseId())) {
                max.put(d.getDiseaseId(), d);
            }
            else {
                if (max.get(d.getDiseaseId()).getPhenodigmScore() < d.getPhenodigmScore()) {
                    max.put(d.getDiseaseId(), d);
                }
            }
        }
        model.addAttribute("diseasesByAnnotation", max.values().stream().sorted().distinct().collect(Collectors.toList()));


    }

}
