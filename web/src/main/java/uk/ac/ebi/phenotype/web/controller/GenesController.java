/** *****************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ****************************************************************************** */
package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.bean.ExpressionImagesBean;
import org.mousephenotype.cda.solr.generic.util.JSONRestUtil;
import org.mousephenotype.cda.solr.generic.util.PhenotypeCallSummarySolr;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.web.dto.*;
import org.mousephenotype.cda.utilities.DataReaderTsv;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
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
import uk.ac.ebi.phenotype.service.PharosDTO;
import uk.ac.ebi.phenotype.service.PharosService;
import uk.ac.ebi.phenotype.service.UniprotDTO;
import uk.ac.ebi.phenotype.service.UniprotService;
import uk.ac.ebi.phenotype.web.util.FileExportUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

@Controller
public class GenesController {

    private final Logger LOGGER = LoggerFactory.getLogger(GenesController.class);
    private static final int numberOfImagesToDisplay = 5;

    @Autowired
    private PhenotypeSummaryDAO phenSummary;

    @Autowired
    private ImagesSolrDao imagesSolrDao;

    @Autowired
    private PhenotypeCallSummarySolr phenotypeCallSummaryService;

    @Autowired
    ObservationService observationService;

    @Autowired
    SolrIndex solrIndex;

    @Autowired
    SolrIndex2 solrIndex2;

    @Autowired
    ImageService imageService;

    @Autowired
    ExpressionService expressionService;

    @Autowired
    private GeneService geneService;

    @Autowired
    private PostQcService postqcService;

    @Autowired
    private UniprotService uniprotService;

    @Autowired
    OrderService orderService;

    @Autowired
    private ImpressService impressService;
    
    @Autowired
    private WebDao phenoDigm2Dao;

    @Autowired
    private RegisterInterestUtils riUtils;

    @Resource(name = "globalConfiguration")
    Map<String, String> config;

    private String drupalBaseUrl;

    private PharosService pharosService;

    @PostConstruct
    private void postConstruct() {

        drupalBaseUrl = config.get("drupalBaseUrl");
        pharosService = new PharosService();

    }

    HttpProxy proxy = new HttpProxy();

    private static final List<String> genesWithVignettes = Arrays.asList(new String[]{"MGI:1913761", "MGI:97491", "MGI:1922814", "MGI:3039593", "MGI:1915138", "MGI:1915138", "MGI:1195985", "MGI:102806", "MGI:1195985", "MGI:1915138", "MGI:1337104", "MGI:3039593", "MGI:1922814", "MGI:97491", "MGI:1928849", "MGI:2151064", "MGI:104606", "MGI:103226", "MGI:1920939", "MGI:95698", "MGI:1915091", "MGI:1924285", "MGI:1914797", "MGI:1351614", "MGI:2147810"});

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
            String header = (String) s.nextElement();
            Enumeration<String> headers = request.getHeaders(header);

            while (headers.hasMoreElements()) {
                String actualHeader = (String) headers.nextElement();
            }
        }

        HttpHeaders resp = new HttpHeaders();
        resp.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<String>(request.toString(), resp, HttpStatus.CREATED);
    }

    @RequestMapping("/genes/{acc}")
    public String genes(@PathVariable String acc, @RequestParam(value = "heatmap", required = false, defaultValue = "false") Boolean showHeatmap, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SQLException, SolrServerException {

        String debug = request.getParameter("debug");
        LOGGER.info("#### genesAllele2: debug: " + debug);
        boolean d = debug != null && debug.equals("true");
        if (d) {
            model.addAttribute("debug", "true");
        }

        processGeneRequest(acc, model, request);

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
            throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SQLException, SolrServerException {

        PhenotypeFacetResult phenoResult = phenotypeCallSummaryService.getPhenotypeCallByGeneAccessionAndFilter(acc, topLevelMpTermName, resourceFullname);
        List<PhenotypeCallSummaryDTO> phenotypeList = phenoResult.getPhenotypeCallSummaries();
        List<GenePageTableRow> phenotypes = new ArrayList<GenePageTableRow>();
        String url = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();

        for (PhenotypeCallSummaryDTO pcs : phenotypeList) {
            List<String> sex = new ArrayList<String>();
            sex.add(pcs.getSex().toString());
            // On the phenotype pages we only display stats graphs as evidence, the MPATH links can't be linked from phen pages
            GenePageTableRow pr = new GenePageTableRow(pcs, url, drupalBaseUrl);
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
        List<Map<String, String>> postQcDataMapList = new ArrayList<>();
        int numberOfTopLevelMpTermsWithStatisticalResult = 0;
        GeneDTO gene = geneService.getGeneById(acc);

        if (gene == null) {
            LOGGER.warn("Gene object from solr for " + acc + " can't be found.");
            throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
        }

        /**
         * PRODUCTION STATUS (SOLR)
         */
        String geneStatus = null;
        try {
            geneStatus = solrIndex.getGeneStatus(acc);
            model.addAttribute("geneStatus", geneStatus);
            // if gene status is null then the jsp declares a warning message at status div
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException exception) {
            throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
        }

        /**
         * Phenotype Summary
         */
        Map<ZygosityType, PhenotypeSummaryBySex> phenotypeSummaryObjects = null;
        Map<String, String> mpGroupsSignificant = new HashMap<>(); // <group, linktToAllData>
        Map<String, String> mpGroupsNotSignificant = new HashMap<>();

        String prodStatusIcons = "Production status not available.";
        // Get list of tripels of pipeline, allele acc, phenotyping center
        // to link to an experiment page will all data
        Set<String> viabilityCalls = observationService.getViabilityForGene(acc);

        try {

            phenotypeSummaryObjects = phenSummary.getSummaryObjectsByZygosity(acc);
            mpGroupsSignificant = getGroups(true, phenotypeSummaryObjects);
            mpGroupsNotSignificant = getGroups(false, phenotypeSummaryObjects);
            if (!mpGroupsSignificant.keySet().contains("mortality/aging") && viabilityCalls.size() > 0) {
                //if mortality aging is not significant we need to test if it's been tested or not
                mpGroupsNotSignificant.put("mortality/aging", "mpTermId=MP:0010768");
            }

            for (String str : mpGroupsSignificant.keySet()) {
                // str: top level term name
                if (mpGroupsNotSignificant.keySet().contains(str)) {
                    mpGroupsNotSignificant.remove(str);
                }
            }

            // add number of top level terms
            for (ZygosityType zyg : phenotypeSummaryObjects.keySet()) {
                numberOfTopLevelMpTermsWithStatisticalResult += phenotypeSummaryObjects.get(zyg).getTotalPhenotypesNumber();
            }
            model.addAttribute("numberOfTopLevelMpTermsWithStatisticalResult", numberOfTopLevelMpTermsWithStatisticalResult);

            postQcDataMapList = observationService.getDistinctPipelineAlleleCenterListByGeneAccession(acc);

            model.addAttribute("postQcDataMapList", postQcDataMapList);
            String genePageUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();
            Map<String, String> status = geneService.getProductionStatus(acc, genePageUrl);
            prodStatusIcons = (status.get("productionIcons").equalsIgnoreCase("")) ? prodStatusIcons : status.get("productionIcons");
            prodStatusIcons += (status.get("phenotypingIcons").equalsIgnoreCase("")) ? "" : status.get("phenotypingIcons");

            model.addAttribute("orderPossible", status.get("orderPossible"));

            //bodyweight parameter to see if we have bodyweight data for button to link to
            ParameterDTO parameter = impressService.getParameterByStableId("IMPC_BWT_008_001");
            if (observationService.getChartPivots("accession=" + acc, acc, parameter, null, null, null, null, null, null).size() > 0) {
                model.addAttribute("bodyWeight", true);
            }

        } catch (Exception e) {
        	e.printStackTrace();
            LOGGER.error("ERROR: ", e);
        }

        // GWAS Gene to IMPC gene mapping
        // commented out for now as we are going to use biosolr stuff to do this
//		List<GwasDTO> gwasMappings = gwasDao.getGwasMappingRows("mgi_gene_symbol", gene.getMarkerSymbol().toUpperCase());
//
//		if ( gwasMappings.size() > 0 ){
//			model.addAttribute("gwasPhenoMapping", gwasMappings.get(0).getPhenoMappingCategory());
//		}


        boolean               loggedIn = false;
        try {

            loggedIn = riUtils.isLoggedIn(request);

        } catch (Exception e) {
            // Nothing to do. If register interest service isn't working, a 500 is thrown. Handle as unauthenticated.
        }

        // Use Register Interest login link
        String paBaseUrlWithScheme = config.get("paBaseUrlWithScheme");
        String riBaseUrlWithScheme = config.get("riBaseUrlWithScheme");
        String registerButtonText = "Login to register interest";
        String registerButtonAnchor = new StringBuilder()
                .append(paBaseUrlWithScheme).append("/riLogin")
                .append("?target=" + paBaseUrlWithScheme + "/genes/" + acc)
                .toString();

        String registerButtonId = acc;
        String registerIconClass = "fa fa-sign-in";

        if (loggedIn) {

            Map<String, List<String>> geneAccessionIdMap = riUtils.getGeneAccessionIds(riBaseUrlWithScheme);
            List<String> geneAccessionIds = geneAccessionIdMap.get("geneAccessionIds");

            if (geneAccessionIds.contains(acc)) {

                registerIconClass = "fa fa-sign-out";
                registerButtonText = "Unregister interest";
                registerButtonAnchor = paBaseUrlWithScheme + "/riUnregistration/gene?geneAccessionId=" + acc + "&target=" + paBaseUrlWithScheme + "/genes/" + acc;

            } else {

                registerIconClass = "fa fa-sign-in";
                registerButtonText = "Register interest";
                registerButtonAnchor = paBaseUrlWithScheme + "/riRegistration/gene?geneAccessionId=" + acc + "&target=" + paBaseUrlWithScheme + "/genes/" + acc;
            }
        }

        model.addAttribute("registerButtonText", registerButtonText);
        model.addAttribute("registerButtonAnchor", registerButtonAnchor);
        model.addAttribute("registerButtonId", registerButtonId);
        model.addAttribute("registerIconClass", registerIconClass);

        try {
            getExperimentalImages(acc, model);
            getExpressionImages(acc, model);
            getImpcImages(acc, model);
            getImpcExpressionImages(acc, model);
            getImpcEmbryoExpression(acc, model);

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
            LOGGER.info("images solr not available");
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

        ArrayList<GenePageTableRow> rowsForPhenotypeTable = processPhenotypes(acc, model, null, null, request);

        model.addAttribute("viabilityCalls", viabilityCalls);
        model.addAttribute("phenotypeSummaryObjects", phenotypeSummaryObjects);
        model.addAttribute("prodStatusIcons", prodStatusIcons);
        model.addAttribute("gene", gene);
        model.addAttribute("request", request);
        model.addAttribute("acc", acc);
        model.addAttribute("isLive", new Boolean((String) request.getAttribute("liveSite")));
        boolean phenotypeStarted = geneService.checkPhenotypeStarted(acc);
        model.addAttribute("phenotypeStarted", phenotypeStarted);
        model.addAttribute("attemptRegistered", geneService.checkAttemptRegistered(acc));
        model.addAttribute("significantTopLevelMpGroups", mpGroupsSignificant);
        model.addAttribute("notsignificantTopLevelMpGroups", mpGroupsNotSignificant);
        if (genesWithVignettes.contains(acc)) {
            model.addAttribute("hasVignette", true);
        }
        // add in the disease predictions from phenodigm              
        processDisease(acc, model);

        model.addAttribute("countIKMCAlleles", countIKMCAlleles);
        LOGGER.debug("CHECK IKMC allele error : " + ikmcError);
        LOGGER.debug("CHECK IKMC allele found : " + countIKMCAlleles);

        //process ardering section
        List<OrderTableRow> orderRows = orderService.getOrderTableRows(acc, null, false);
        model.addAttribute("acc", acc);
        model.addAttribute("orderRows", orderRows);

        //for cre products link at bottom of table
        //model.addAttribute("alleleProductsCre2", orderService.getCreData(acc));
        model.addAttribute("creLineAvailable", orderService.crelineAvailable(acc));

        PhenotypeDisplayStatus phenotypeDisplayStatus = getPhenotypeDisplayStatus(phenotypeStarted, numberOfTopLevelMpTermsWithStatisticalResult, postQcDataMapList, rowsForPhenotypeTable);
        model.addAttribute("phenotypeDisplayStatus", phenotypeDisplayStatus);
    }


    /**
     * Encapsulate logic for how we are displaying phenotype information in the
     * gene page here so easy to read through and error check
     *
     * @return
     */
    private PhenotypeDisplayStatus getPhenotypeDisplayStatus(boolean phenotypeStarted, int numberOfTopLevelMpTermsWithStatisticalResult, List<Map<String, String>> postQcDataMapList, ArrayList<GenePageTableRow> rowsForPhenotypeTable) {
        //example of gene with preQc but not postQc Iqgap2
        //example of gene with preQc but not significant  Stox2, Mast3
        //example with lots of phenotype data http://localhost:8080/phenotype-archive/genes/MGI:109331

        PhenotypeDisplayStatus displayStatus = new PhenotypeDisplayStatus();
        if (phenotypeStarted) {
            displayStatus.setDisplayHeatmap(true);
        }

        if (numberOfTopLevelMpTermsWithStatisticalResult > 0) {
            displayStatus.setPostQcTopLevelMPTermsAvailable(true);
        }

        //dataMap is not empty
        if (!postQcDataMapList.isEmpty()) {
            displayStatus.setPostQcDataAvailable(true);
        }
        if (rowsForPhenotypeTable.size() > 0) {
            displayStatus.setEitherPostQcOrPreQcSignificantDataIsAvailable(true);
        }

        return displayStatus;
    }

    /**
     * @author ilinca
     * @since 2015/10/09
     * @param significant
     * @param phenotypeSummaryObjects
     * @return
     */
    public Map<String, String> getGroups(boolean significant, Map<ZygosityType, PhenotypeSummaryBySex> phenotypeSummaryObjects) {

        Map<String, String> mpGroups = new HashMap<>();

        for (PhenotypeSummaryBySex summary : phenotypeSummaryObjects.values()) {
            for (PhenotypeSummaryType phen : summary.getBothPhenotypes(significant)) {
            	mpGroups.put(phen.getGroup(), phen.getTopLevelIds());
            }
            for (PhenotypeSummaryType phen : summary.getMalePhenotypes(significant)) {
                mpGroups.put(phen.getGroup(), phen.getTopLevelIds());
            }
            for (PhenotypeSummaryType phen : summary.getFemalePhenotypes(significant)) {
                mpGroups.put(phen.getGroup(), phen.getTopLevelIds());
            }
        }

        return mpGroups;
    }

    /**
     * @throws IOException
     * @throws SolrServerException, IOException
     */
    @RequestMapping("/genesPhenoFrag/{acc}")
    public String genesPhenoFrag(@PathVariable String acc,
            @RequestParam(required = false, value = "top_level_mp_term_name") List<String> topLevelMpTermName,
            @RequestParam(required = false, value = "resource_fullname") List<String> resourceFullname,
            Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SolrServerException {

        // Pass on any query string after the
        String queryString = request.getQueryString();
        processPhenotypes(acc, model, topLevelMpTermName, resourceFullname, request);

        return "PhenoFrag";
    }

    /**
     * @author tudose
     * @throws Exception
     * @since 2015/10/02
     */
    @RequestMapping("/genes/summary")
    public String geneSummary(@RequestParam(value = "acc", required = true) String acc, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws Exception {

        GeneDTO gene = geneService.getGeneById(acc);

        UniprotDTO uniprotData = uniprotService.getUniprotData(gene);

        Map<ZygosityType, PhenotypeSummaryBySex> phenotypeSummaryObjects = phenSummary.getSummaryObjectsByZygosity(acc);
        Map<String, String> mpGroupsSignificant = getGroups(true, phenotypeSummaryObjects);
        Map<String, String> mpGroupsNotSignificant = getGroups(false, phenotypeSummaryObjects);
        for (String str : mpGroupsSignificant.keySet()) {
            if (mpGroupsNotSignificant.keySet().contains(str)) {
                mpGroupsNotSignificant.remove(str);
            }
        }
        Set<String> viabilityCalls = observationService.getViabilityForGene(acc);
        Set<String> allelesWithData = postqcService.getAllGenotypePhenotypesForGene(acc);
        Map<String, String> alleleCassette = (allelesWithData.size() > 0 && allelesWithData != null) ? solrIndex2.getAlleleImage(allelesWithData) : null;
        String genePageUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();
        Map<String, String> prod = geneService.getProductionStatus(acc, genePageUrl);
        String prodStatusIcons = (prod.get("productionIcons").equalsIgnoreCase("")) ? "" : prod.get("productionIcons");
        List<ImageSummary> imageSummary = imageService.getImageSummary(acc);

        if (gene.getUniprotHumanCanonicalAcc() != null) {
            JSONObject pfamJson = JSONRestUtil.getResultsArray("http://pfam.xfam.org/protein/" + gene.getUniprotHumanCanonicalAcc() + "/graphic").getJSONObject(0);
            model.addAttribute("pfamJson", pfamJson);
        }

        PharosDTO pharos = pharosService.getPharosInfo(gene.getHumanGeneSymbol().get(0));

        Map<String, Double> stringDBTable = readStringDbTable(gene.getMarkerSymbol());
        // Adds "orthologousDiseaseAssociations", "phenotypicDiseaseAssociations" to the model
        /** pdsimplify: replaced processDisease by processDisease2
         * The genesSummary jsp should be updated to use phenodigm2 objects **/         
        processDisease(acc, model);
        model.addAttribute("stringDbTable", stringDBTable);
        model.addAttribute("significantTopLevelMpGroups", mpGroupsSignificant);
        model.addAttribute("notsignificantTopLevelMpGroups", mpGroupsNotSignificant);
        model.addAttribute("viabilityCalls", viabilityCalls);
        model.addAttribute("phenotypeSummaryObjects", phenotypeSummaryObjects);

        model.addAttribute("gene", gene);
        model.addAttribute("alleleCassette", alleleCassette);
        model.addAttribute("imageSummary", imageSummary);
        model.addAttribute("prodStatusIcons", prodStatusIcons);
        model.addAttribute("uniprotData", uniprotData);
        model.addAttribute("pharos", pharos);
        return "geneSummary";
    }

    @RequestMapping("/pFam/{acc}")
    public String pFamGraphic(@PathVariable String acc, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws Exception {

        GeneDTO gene = geneService.getGeneById(acc);
        if (gene.getUniprotHumanCanonicalAcc() != null) {
            JSONObject pfamJson = JSONRestUtil.getResultsArray("http://pfam.xfam.org/protein/" + gene.getUniprotHumanCanonicalAcc() + "/graphic").getJSONObject(0);
            model.addAttribute("pfamJson", pfamJson);
        }
        return "pfamDomain";
    }

    private Map<String, Double> readStringDbTable(String geneSymbol) throws MalformedURLException, IOException, URISyntaxException {

        Map<String, Double> map = new java.util.HashMap<>();

        try {
            String stringDbUrl = "http://string-db.org/api/tsv-no-header/resolve?identifier=" + geneSymbol + "&format=only-ids&species=10090";
            String id = proxy.getContent(new URL(stringDbUrl), true);
            stringDbUrl = "http://string-db.org/api/psi-mi-tab/interactionsList?identifiers=" + id + "&limit=20";

            // Parse interactor gene symbol and score. Example return format : 
            // string:10090.ENSMUSP00000022100	string:10090.ENSMUSP00000003268	Slc6a3	Sh3gl1	-	-	-	-	-	taxid:10090	taxid:10090	-	-	-	score:0.654|tscore:0.654
            // Interactions http://string-db.org/api/psi-mi-tab/interactionsList?identifiers=10090.ENSMUSP00000087479&limit=20
            DataReaderTsv tsvReader = new DataReaderTsv(new URL(stringDbUrl));
            String[][] data = tsvReader.getData();

            for (int i = 0; i < data.length; i++) {
                if (data[i][2].equalsIgnoreCase(geneSymbol)) {// direct interaction
                    map.put(data[i][3], Double.valueOf(data[i][14].replaceAll("score:", "").split("\\|")[0]));
                } else if (data[i][3].equalsIgnoreCase(geneSymbol)) {// direct interaction presented in reverse order
                    map.put(data[i][2], Double.valueOf(data[i][14].replaceAll("score:", "").split("\\|")[0]));
                }
            }

        } catch (Exception e) {
            LOGGER.error("STRING db could not be accessed.");
            e.printStackTrace();
        }

        return map;
    }

    private Map<String, Map<String, Integer>> sortPhenFacets(Map<String, Map<String, Integer>> phenFacets) {

        Map<String, Map<String, Integer>> sortPhenFacets = phenFacets;
        for (String key : phenFacets.keySet()) {
            sortPhenFacets.put(key, new TreeMap<String, Integer>(phenFacets.get(key)));
        }
        return sortPhenFacets;
    }

    private ArrayList<GenePageTableRow> processPhenotypes(String acc, Model model, List<String> topLevelMpTermName, List<String> resourceFullname, HttpServletRequest request)
            throws IOException, URISyntaxException, SolrServerException {

        List<PhenotypeCallSummaryDTO> phenotypeList = new ArrayList<PhenotypeCallSummaryDTO>();
        PhenotypeFacetResult phenoResult = null;
        

        //for image links we need a query that brings back mp terms and colony_ids that have mp terms
        //http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1913955%22&fq=mp_id:*&facet=true&facet.mincount=1&facet.limit=-1&facet.field=colony_id&facet.field=mp_id&facet.field=mp_term&rows=0
        Map<String, Set<String>> mpToColony = imageService.getImagePropertiesThatHaveMp(acc);

        try {

            phenoResult = phenotypeCallSummaryService.getPhenotypeCallByGeneAccessionAndFilter(acc, topLevelMpTermName, resourceFullname);
           
            phenotypeList = phenoResult.getPhenotypeCallSummaries();
            Map<String, Map<String, Integer>> phenoFacets = phenoResult.getFacetResults();
            // sort facets
            model.addAttribute("phenoFacets", sortPhenFacets(phenoFacets));

        } catch (HibernateException | JSONException e) {
            LOGGER.error("ERROR GETTING PHENOTYPE LIST");
            e.printStackTrace();
            phenotypeList = new ArrayList<PhenotypeCallSummaryDTO>();
        }

        // This is a map because we need to support lookups
        HashMap<Integer, DataTableRow> phenotypes = new HashMap<>();

        for (PhenotypeCallSummaryDTO pcs : phenotypeList) {

            DataTableRow pr = new GenePageTableRow(pcs, request.getAttribute("baseUrl").toString(), drupalBaseUrl);

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

        ArrayList<GenePageTableRow> l = new ArrayList(phenotypes.values());
        Collections.sort(l);
        model.addAttribute("rowsForPhenotypeTable", l);
        return l;
    }

    /**
     * Get the first 5 wholemount expression images if available
     *
     * @param acc the gene to get the images for
     * @param model the model to add the images to
     * @throws SolrServerException, IOException
     */
    private void getExpressionImages(String acc, Model model)
            throws SolrServerException, IOException {

        QueryResponse solrExpressionR = imagesSolrDao.getExpressionFacetForGeneAccession(acc);
        if (solrExpressionR == null) {
            LOGGER.error("no response from solr data source for acc=" + acc);
            return;
        }

        List<FacetField> expressionfacets = solrExpressionR.getFacetFields();
        if (expressionfacets == null) {
            LOGGER.info("no expression facets from solr data source for acc=" + acc);
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
     * @param acc the gene to get the images for
     * @param model the model to add the images to
     * @throws SolrServerException, IOException
     */
    private void getExperimentalImages(String acc, Model model)
            throws SolrServerException, IOException {

        QueryResponse solrR = imagesSolrDao.getExperimentalFacetForGeneAccession(acc);
        if (solrR == null) {
            LOGGER.error("no response from solr data source for acc=" + acc);
            return;
        }

        List<FacetField> facets = solrR.getFacetFields();
        if (facets == null) {
            LOGGER.error("no facets from solr data source for acc=" + acc);
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
     * @param acc the gene to get the images for
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
        model.addAttribute("paramToNumber", paramToNumber);
        model.addAttribute("impcImageGroups", groups);

    }

    /**
     * Get the first 5 wholemount expression images if available
     *
     * @param acc the gene to get the images for
     * @param model the model to add the images to
     * @throws SolrServerException, IOException
     * @throws SQLException
     */
    private void getImpcExpressionImages(String acc, Model model)
            throws SolrServerException, IOException, SQLException {
        boolean overview = true;
        boolean embryoOnly = false;
        List<Count> parameterCounts = expressionService.getLaczCategoricalParametersForGene(acc);

        List<AnatomogramDataBean> anatomogramDataBeans = expressionService.getAnatomogramDataBeans(parameterCounts);
        Map<String, Long> topLevelMaCounts = expressionService.getLacSelectedTopLevelMaCountsForAnatomogram(anatomogramDataBeans);
        Set<String> topLevelMaIds = expressionService.getLacSelectedTopLevelMaIdsForAnatomogram(anatomogramDataBeans);

        //System.out.println("Genes controller: topLevelMaCounts"+topLevelMaCounts);
        model.addAttribute("topLevelMaCounts", topLevelMaCounts);
        model.addAttribute("topLevelMaIds", topLevelMaIds);
        JSONObject anatomogram = expressionService.getAnatomogramJson(anatomogramDataBeans);
        model.addAttribute("anatomogram", anatomogram);

        ExpressionImagesBean section = expressionService.getLacImageDataForGene(acc, null, "IMPC_ALZ_075_001", overview);
        ExpressionImagesBean wholemount = expressionService.getLacImageDataForGene(acc, null, "IMPC_ALZ_076_001", overview);
        model.addAttribute("sectionExpressionImagesBean", section);
        model.addAttribute("wholemountExpressionImagesBean", wholemount);

//		model.addAttribute("haveImpcAdultImagesSection", section.getHaveImpcImages());
//		model.addAttribute("haveImpcAdultImagesWholemount", wholemount.getHaveImpcImages());
        //need to do these for section and wholemount
//		model.addAttribute("impcAdultExpressionImageFacetsSection", section.getFilteredTopLevelAnatomyTerms());
//		model.addAttribute("impcAdultExpressionFacetToDocsSection", section.getExpFacetToDocs());
//		model.addAttribute("impcAdultExpressionImageFacetsWholemount", wholemount.getFilteredTopLevelAnatomyTerms());
//		model.addAttribute("impcAdultExpressionFacetToDocsWholemount", wholemount.getExpFacetToDocs());
        expressionService.getExpressionDataForGene(acc, model, embryoOnly);
    }

    /**
     * Get the first 5 wholemount expression images if available
     *
     * @param acc the gene to get the images for
     * @param model the model to add the images to
     * @throws SolrServerException, IOException
     * @throws SQLException
     */
    private void getImpcEmbryoExpression(String acc, Model model)
            throws SolrServerException, IOException, SQLException {
        //good test gene:Nxn with selected top level emap terms
        boolean overview = true;
        boolean embryoOnly = true;
        //get embryo images
        //solrQuery.addFilterQuery(ImageDTO.PARAMETER_STABLE_ID + ":IMPC_ELZ_064_001" + " OR "
        //+ ImageDTO.PARAMETER_STABLE_ID + ":IMPC_ELZ_063_001");

        //impcEmbryoExpressionFacetToDocsWholemount
        ExpressionImagesBean wholemount = expressionService.getLacImageDataForGene(acc, null, "IMPC_ELZ_064_001", overview);
        ExpressionImagesBean section = expressionService.getLacImageDataForGene(acc, null, "IMPC_ELZ_063_001", overview);
        model.addAttribute("sectionExpressionImagesEmbryoBean", section);
        model.addAttribute("wholemountExpressionImagesEmbryoBean", wholemount);
//		model.addAttribute("impcEmbryoExpressionImageFacetsWholemount", wholemount.getExpFacetToDocs());
//		model.addAttribute("impcEmbryoExpressionImageFacetsSection", section.getExpFacetToDocs());
//		
//		
//		model.addAttribute("topLevelMaCountsEmbryoWholemount", wholemount.getFilteredTopLevelAnatomyTerms());
//		model.addAttribute("topLevelMaCountsEmbryoSection", section.getFilteredTopLevelAnatomyTerms());
//		model.addAttribute("haveImpcAdultImagesEmbryoSection", section.getHaveImpcImages());
//		model.addAttribute("haveImpcAdultImagesEmbryoWholemount", wholemount.getHaveImpcImages());
        expressionService.getExpressionDataForGene(acc, model, embryoOnly);

    }

    /**
     * Error handler for gene not found
     *
     * @param exception
     * @return redirect to error page
     *
     */
    @ExceptionHandler(GenomicFeatureNotFoundException.class)
    public ModelAndView handleGenomicFeatureNotFoundException(GenomicFeatureNotFoundException exception) {

        ModelAndView mv = new ModelAndView("identifierErrorGenes");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", exception.getAcc());
        mv.addObject("type", "MGI gene");
        mv.addObject("exampleURI", "/genes/MGI:104874");
        return mv;
    }

    @ExceptionHandler(JDBCConnectionException.class)
    public ModelAndView handleJDBCConnectionException(JDBCConnectionException exception) {

        ModelAndView mv = new ModelAndView("uncaughtException");
        System.out.println(ExceptionUtils.getFullStackTrace(exception));
        mv.addObject("errorMessage", "An error occurred connecting to the database");
        return mv;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception exception) {

        ModelAndView mv = new ModelAndView("uncaughtException");
        System.out.println(ExceptionUtils.getFullStackTrace(exception));
        return mv;
    }

    /**
     * Display an identifier error page
     */
    @RequestMapping("/identifierErrorGenes")
    public String identifierError(@PathVariable String acc, Model model, HttpServletRequest request, RedirectAttributes attributes) {

        return "identifierErrorGenes";
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
            LOGGER.warn("Gene object from solr for " + acc + " can't be found.");
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
     * @param acc
     *
     * Geneid
     *
     * @param model
     */
    private void processDisease(String acc, Model model) {

        // fetch diseases that are linked to a gene via annotations/curation
        LOGGER.info(String.format("%s - getting gene-disease associations for gene ", acc));
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
        LOGGER.info("Found " + modelAssociations.size()+ " associations");
                        
        // create a js object representation of the models        
        String modelAssocsJsArray = "[]";
        boolean hasModelsByOrthology = false;
        if (modelAssociations.size() > 0) {
            List<String> jsons = new ArrayList<>();
            for (DiseaseModelAssociation assoc : modelAssociations) {                
                jsons.add(assoc.makeDiseaseJson());
                if (curatedDiseases.contains(assoc.getDiseaseId())) {
                    hasModelsByOrthology = true;
                }
            }
            modelAssocsJsArray = "[" + String.join(", ", jsons) + "]";
        }
        model.addAttribute("modelAssociations", modelAssocsJsArray);
        model.addAttribute("hasModelsByOrthology", hasModelsByOrthology);
        model.addAttribute("hasModelAssociations", modelAssociations.size()>0);                
    }

}
