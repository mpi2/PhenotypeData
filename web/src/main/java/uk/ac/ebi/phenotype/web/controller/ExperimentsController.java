/*******************************************************************************
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
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.exporter.Exporter;
import org.mousephenotype.cda.interfaces.Exportable;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.CombinedObservationKey;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.solr.web.dto.AllelePageDTO;
import org.mousephenotype.cda.solr.web.dto.ExperimentsDataTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.web.util.FileExportUtils;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ExperimentsController implements Exportable<ExperimentsDataTableRow> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final GenotypePhenotypeService gpService;
    private final StatisticalResultService srService;
    private final MpService mpService;
    private final GeneService geneService;
    private PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();

    public ExperimentsController(@NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService gpService,
                                 @NotNull @Named("statistical-result-service") StatisticalResultService srService,
                                 @NotNull MpService mpService,
                                 @NotNull GeneService geneService
                                 ) {
        this.gpService = gpService;
        this.srService = srService;
        this.mpService = mpService;
        this.geneService = geneService;
    }

    /**
     * all data table on the gene page - used to be path /experimentsTableFrag now allDataTable
     */
    @RequestMapping("/allDataTable")
    public String getAllDataTable(
            @RequestParam(required = true, value = "geneAccession") String geneAccession,
            //@RequestParam(required = false, value = "alleleSymbol") List<String> alleleSymbol,
            @RequestParam(required = false, value = "mpTerm") List<String> mpTermId,
            Model model,
            HttpServletRequest request)
            throws IOException, SolrServerException, JSONException {

        Set<ExperimentsDataTableRow> experimentRows = getExperimentsDataTableRows(geneAccession, mpTermId, model, request);

        return "allDataTable";
    }

    /**
     * Runs when the request missing an accession ID. This redirects to the
     * search page which defaults to showing all genes in the list
     */
    @RequestMapping(value="/allDataTableExport", method = RequestMethod.GET)
    public void exportAllDataTable(
            @RequestParam(required = true, value = "geneAccession") String geneAccession,
            @RequestParam(required = false, value = "alleleSymbol") List<String> alleleSymbol,
            @RequestParam(required = false, value = "mpTermId") List<String> mpTermId,
            @RequestParam(value = "fileType", required = true) String fileType,
            @RequestParam(value = "fileName", required = true) String fileName,
            Model model,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, SolrServerException, JSONException {
        System.out.println("calling export on allDataTable in experiments controller");
        Set<ExperimentsDataTableRow> experimentRows = getExperimentsDataTableRows(geneAccession, mpTermId, model, request);
        List<ExperimentsDataTableRow> rowsList=new ArrayList<>();
        rowsList.addAll(experimentRows);
        //experimentRows
        Collections.sort(rowsList);
        List<List<String>> matrix = getRows(rowsList);
        Exporter.export(response, fileType, fileName, getHeading(), matrix);
    }

    private Set<ExperimentsDataTableRow> getExperimentsDataTableRows(@RequestParam(required = true, value = "geneAccession") String geneAccession, @RequestParam(required = false, value = "mpTermId") List<String> mpTermId, Model model, HttpServletRequest request) throws IOException, SolrServerException, JSONException {
        ExperimentsTable experimentsTable = new ExperimentsTable(geneAccession, mpTermId, request).invoke();
        Set<ExperimentsDataTableRow> experimentRows = experimentsTable.getExperimentRows();
        JSONArray sortedJsonArray = experimentsTable.getSortedJsonArray();

        model.addAttribute("rows", experimentRows.size());
        model.addAttribute("allData", sortedJsonArray.toString().replace("'", "\\'"));
        return experimentRows;
    }

    @Override
    public List<String> getRow(ExperimentsDataTableRow allDataRow) {

        List<String> row = new ArrayList<>();
        String       geneAccessionId = null;
        if (allDataRow.getAllele() != null) {
            row.add(allDataRow.getAllele().getSymbol());
        }else{
            row.add(Constants.NO_INFORMATION_AVAILABLE);
        }
        if(allDataRow.getPhenotypingCenter()!=null){
            row.add(allDataRow.getPhenotypingCenter());
        }else{
            row.add(Constants.NO_INFORMATION_AVAILABLE);
        }
        String procParam=Constants.NO_INFORMATION_AVAILABLE;
        if(allDataRow.getProcedure()!=null){
            procParam=allDataRow.getProcedure().getName();
        }
        if(allDataRow.getParameter()!=null){
            procParam.join("/",allDataRow.getParameter().getName());
        }
        row.add(procParam);
        if(allDataRow.getLifeStageName()!=null){
            row.add(allDataRow.getLifeStageName());
        }else{
            row.add(Constants.NO_INFORMATION_AVAILABLE);
        }
        if(allDataRow.getZygosity().getName()!=null){
            row.add(allDataRow.getZygosity().getName());
        }else{
            row.add(Constants.NO_INFORMATION_AVAILABLE);
        }
        if(allDataRow.getSignificant()!=null){
            row.add(allDataRow.getSignificant().toString());
        }else{
            row.add(Constants.NO_INFORMATION_AVAILABLE);
        }
        if(allDataRow.getpValue()!=null) {
            row.add(allDataRow.getpValue().toString());
        }else{
            row.add(Constants.NO_INFORMATION_AVAILABLE);
        }
        if(allDataRow.getPhenotypeTerm().getName()!=null){
            row.add(allDataRow.getPhenotypeTerm().getName());
        }else{
            row.add(Constants.NO_INFORMATION_AVAILABLE);
        }


        return row;
    }


    @Override
    public List<String> getHeading() {

        final List<String> headings = Arrays.asList(new String[] {
                "Allele",
                "Center",
                "Procedure / Parameter",
                "Life Stage",
                "Zygosity",
                "Significant",
                "P Value",
                "Phenotype"
        });

        return headings;
    }


    public String mpIdToName(String id) {
        try {
            return mpService.getPhenotypeById(id).getMpTerm();
        } catch (IOException | SolrServerException e) {
            logger.warn("Error trying to get phenotype for term ", id, e.getMessage());
        }
        return null;
    }

    /**
     * Runs when the request missing an accession ID. This redirects to the
     * search page which defaults to showing all genes in the list
     */
    @RequestMapping("/experimentsChartFrag")
    public String getAllelesChart(
            @RequestParam(required = true, value = "geneAccession") String geneAccession,
            @RequestParam(required = false, value = "alleleSymbol") List<String> alleleSymbol,
            @RequestParam(required = false, value = "phenotypingCenter") List<String> phenotypingCenter,
            @RequestParam(required = false, value = "pipelineName") List<String> pipelineName,
            @RequestParam(required = false, value = "procedureStableId") List<String> procedureStableId,
            @RequestParam(required = false, value = "procedureName") List<String> procedureName,
            @RequestParam(required = false, value = "mpTerm") List<String> mpTermNames,
            @RequestParam(required = false, value = "resource") ArrayList<String> resource,
            Model model,
            HttpServletRequest request)
            throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SolrServerException {

        AllelePageDTO allelePageDTO = srService.getAllelesInfo(geneAccession, null, null, null, null, null, null, null);
        String graphBaseUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();
        List<String> mpTermNamesNormalized = Collections.emptyList();


        if(mpTermNames != null) {
            mpTermNamesNormalized = mpTermNames.stream().map(x -> x.startsWith("MP:") ? mpIdToName(x) : x).filter(Objects::nonNull).collect(Collectors.toList());
        }
        // Need to convert MP IDs to MP term names

        Map<String, List<ExperimentsDataTableRow>> experimentRows = new HashMap<>(srService.getPvaluesByAlleleAndPhenotypingCenterAndPipeline(geneAccession, procedureName, alleleSymbol, phenotypingCenter, pipelineName, procedureStableId, resource, mpTermNamesNormalized, graphBaseUrl));
        //remove any trace of viability data from chart as we decided as a group in ticket #184
        //experimentRows.remove("IMPC_VIA_001_001");
        Map<String, List<ExperimentsDataTableRow>> experimentRowsToFilter=new HashMap<>();//need
        for(Iterator<String> iterator= experimentRows.keySet().iterator(); iterator.hasNext();){
            String key=iterator.next();
            if(key.contains("_VIA_")){//remove any viability data from chart as often no p values - was a group decision JW.
                iterator.remove();//using the iterator directly resolves any concurrent modification exceptions
            }
        }
        Map<String, Object> chartData = phenomeChartProvider.generatePvaluesOverviewChart(experimentRows, Constants.P_VALUE_THRESHOLD, allelePageDTO.getParametersByProcedure());
        model.addAttribute("chart", chartData.get("chart"));
        model.addAttribute("count", chartData.get("count"));
        return "experimentsChartFrag";
    }

    @RequestMapping("/experiments")
    public String getBasicInfo(
            @RequestParam(value = "geneAccession") String geneAccession,
            @RequestParam(required = false, value = "alleleSymbol") List<String> alleleSymbol,
            @RequestParam(required = false, value = "phenotypingCenter") List<String> phenotypingCenter,
            @RequestParam(required = false, value = "pipelineName") List<String> pipelineName,
            @RequestParam(required = false, value = "procedureStableId") List<String> procedureStableId,
            @RequestParam(required = false, value = "procedureName") List<String> procedureName,
            @RequestParam(required = false, value = "mpTermId") List<String> mpTermIds,
            @RequestParam(required = false, value = "resource") ArrayList<String> resource,
            Model model,
            HttpServletRequest request)
            throws SolrServerException, IOException, URISyntaxException {

        AllelePageDTO allelePageDTO = srService.getAllelesInfo(geneAccession, null, null, null, null, null, null, null);
        Map<String, List<ExperimentsDataTableRow>> experimentRows = new HashMap<>();
        int rows = 0;
        String graphBaseUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();

        List<String> mpTermNamesNormalized = Collections.emptyList();

        if(mpTermIds != null) {
            mpTermNamesNormalized = mpTermIds.stream().map(x -> x.startsWith("MP:") ? mpIdToName(x) : x).filter(Objects::nonNull).collect(Collectors.toList());
        }

        experimentRows.putAll(srService.getPvaluesByAlleleAndPhenotypingCenterAndPipeline(geneAccession, procedureName, alleleSymbol, phenotypingCenter, pipelineName,
                procedureStableId, resource, mpTermNamesNormalized, graphBaseUrl));
        for (List<ExperimentsDataTableRow> list : experimentRows.values()) {
            rows += list.size();
        }

        Map<String, Object> chart = phenomeChartProvider.generatePvaluesOverviewChart(experimentRows, Constants.P_VALUE_THRESHOLD, allelePageDTO.getParametersByProcedure());
        //top level mp names often are not in same order as ids so this mehod if used for getting name from id is wrong. SR indexer needs fixing.
        Map<String, String> phenotypeTopLevels = srService.getTopLevelMPTerms(geneAccession, null);
        List<MpDTO> mpTerms = new ArrayList<>();

        mpTerms.addAll(mpService.getPhenotypes(mpTermIds));
        model.addAttribute("phenotypeFilters", mpTerms);
        model.addAttribute("phenotypes", phenotypeTopLevels);
        model.addAttribute("chart", chart.get("chart"));
        model.addAttribute("rows", rows);
        model.addAttribute("experimentRows", experimentRows);
        model.addAttribute("allelePageDTO", allelePageDTO);

        return "experiments";
    }


    /**
     * @author ilinca
     * @since 2016/05/05
     */
    @RequestMapping("/experiments/export")
    public void downloadBasicInfo(
            @RequestParam(value = "fileType") String fileType,
            @RequestParam(value = "fileName") String fileName,
            @RequestParam(value = "geneAccession") String geneAccession,
            @RequestParam(required = false, value = "alleleSymbol") List<String> alleleSymbol,
            @RequestParam(required = false, value = "phenotypingCenter") List<String> phenotypingCenter,
            @RequestParam(required = false, value = "pipelineName") List<String> pipelineName,
            @RequestParam(required = false, value = "procedureStableId") List<String> procedureStableId,
            @RequestParam(required = false, value = "procedureName") List<String> procedureName,
            @RequestParam(required = false, value = "mpTermId") List<String> mpTermId,
            @RequestParam(required = false, value = "resource") ArrayList<String> resource,
            HttpServletRequest request,
            HttpServletResponse response)
            throws Exception {

        List<ExperimentsDataTableRow> experimentList = new ArrayList<>();
        String graphBaseUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();

        for (List<ExperimentsDataTableRow> list : srService.getPvaluesByAlleleAndPhenotypingCenterAndPipeline(geneAccession, procedureName, alleleSymbol, phenotypingCenter, pipelineName, procedureStableId, resource, mpTermId, graphBaseUrl).values()) {
            experimentList.addAll(list);
        }

        List<String> dataRows = new ArrayList<>();
        dataRows.add(ExperimentsDataTableRow.getTabbedHeader());
        for (ExperimentsDataTableRow row : experimentList) {
            dataRows.add(row.toTabbedString());
        }

        String filters = null;
        FileExportUtils.writeOutputFile(response, dataRows, fileType, fileName, filters);

    }


    public ModelAndView handleGeneralException(Exception exception) {
        ModelAndView mv = new ModelAndView("uncaughtException");
        exception.printStackTrace();
        return mv;
    }


    private class ExperimentsTable {
        private String geneAccession;
        private List<String> mpTermId;
        private HttpServletRequest request;
        private Set<ExperimentsDataTableRow> experimentRows;
        private JSONArray sortedJsonArray;

        public ExperimentsTable(String geneAccession, List<String> mpTermId, HttpServletRequest request) {
            this.geneAccession = geneAccession;
            this.mpTermId = mpTermId;
            this.request = request;
        }

        public Set<ExperimentsDataTableRow> getExperimentRows() {
            return experimentRows;
        }

        public JSONArray getSortedJsonArray() {
            return sortedJsonArray;
        }

        public ExperimentsTable invoke() throws IOException, SolrServerException, JSONException {
            experimentRows = new HashSet<>();
            String graphBaseUrl = request.getAttribute("baseUrl").toString();

            // Get data from gene core
            Map<CombinedObservationKey, ExperimentsDataTableRow> srResult = getCombinedObservationKeyExperimentsDataTableRowMap(geneAccession, mpTermId, experimentRows);


            JSONArray experimentRowsJson = new JSONArray();
            experimentRows.stream().forEach(experimentsDataTableRow -> {
                JSONObject experimentRowJson = new JSONObject();
                try {
                    String phenotypeTermName =  experimentsDataTableRow.getPhenotypeTerm() != null ?  experimentsDataTableRow.getPhenotypeTerm().getName() : null;

                    final Optional<ExperimentsDataTableRow> obj = srResult.values().stream().filter(x -> x == experimentsDataTableRow).findFirst();
                    Boolean srSignificant = null;
                    if (obj.isPresent()) {
                        final ExperimentsDataTableRow row = obj.get();
                        srSignificant = row.getSignificant();
                    }
                    String significant = getSignificanceStringForTable(experimentsDataTableRow, phenotypeTermName);

                    experimentRowJson.put(ObservationDTO.ALLELE_SYMBOL, experimentsDataTableRow.getAllele().getSymbol());
                    experimentRowJson.put(ObservationDTO.ALLELE_ACCESSION_ID, experimentsDataTableRow.getAllele().getAccessionId());
                    experimentRowJson.put(ObservationDTO.GENE_ACCESSION_ID, experimentsDataTableRow.getGene().getAccessionId());
                    experimentRowJson.put(ObservationDTO.PHENOTYPING_CENTER, experimentsDataTableRow.getPhenotypingCenter());
                    experimentRowJson.put(ObservationDTO.PROCEDURE_STABLE_ID, experimentsDataTableRow.getProcedure().getStableId());
                    experimentRowJson.put(ObservationDTO.PIPELINE_STABLE_ID, experimentsDataTableRow.getPipeline().getStableId());
                    experimentRowJson.put(ObservationDTO.PROCEDURE_NAME, experimentsDataTableRow.getProcedure().getName());
                    experimentRowJson.put(ObservationDTO.PARAMETER_STABLE_ID, experimentsDataTableRow.getParameter().getStableId());
                    experimentRowJson.put(ObservationDTO.PARAMETER_NAME, experimentsDataTableRow.getParameter().getName());
                    experimentRowJson.put(ObservationDTO.ZYGOSITY, experimentsDataTableRow.getZygosity().getShortName());
                    experimentRowJson.put(StatisticalResultDTO.SIGNIFICANT, significant);
                    experimentRowJson.put("phenotype_term", phenotypeTermName);
                    experimentRowJson.put("female_mutants", experimentsDataTableRow.getFemaleMutantCount());
                    experimentRowJson.put("male_mutants", experimentsDataTableRow.getMaleMutantCount());
                    experimentRowJson.put("life_stage", experimentsDataTableRow.getLifeStageName());
                    experimentRowJson.put("p_value", experimentsDataTableRow.getpValue());
                    experimentRowJson.put("status", experimentsDataTableRow.getStatus());
                    experimentRowsJson.put(experimentRowJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });


            //sort

            sortedJsonArray = new JSONArray();

            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
            for (int i = 0; i < experimentRowsJson.length(); i++) {
                jsonValues.add(experimentRowsJson.getJSONObject(i));
            }
            Collections.sort( jsonValues, new Comparator<JSONObject>() {
                //You can change "Name" with "ID" if you want to sort by ID
                private static final String KEY_NAME = ObservationDTO.PROCEDURE_NAME;

                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get(KEY_NAME);
                        valB = (String) b.get(KEY_NAME);
                    }
                    catch (JSONException e) {
                        //do something
                    }

                    return valA.compareTo(valB);
                    //if you want to change the sort order, simply use the following:
                    //return -valA.compareTo(valB);
                }
            });

            for (int i = 0; i < experimentRowsJson.length(); i++) {
                sortedJsonArray.put(jsonValues.get(i));
            }
            return this;
        }

        private Map<CombinedObservationKey, ExperimentsDataTableRow> getCombinedObservationKeyExperimentsDataTableRowMap(
                String geneAccession,
                List<String> mpTermId,
                Set<ExperimentsDataTableRow> experimentRows
        ) throws IOException, SolrServerException {
            Set<ExperimentsDataTableRow> experimentRowsFromObservations = geneService.getAllData(geneAccession);

            if(mpTermId != null && mpTermId.size() > 0) {

                for(ExperimentsDataTableRow row:experimentRowsFromObservations){
                    System.out.println(row.getPhenotypeTerm().getName()+"top level group term="+row.getTopLevelMpGroups()+ "top level phenotype terms"+row.getTopLevelPhenotypeTerms());
                }

                //experimentRows.addAll(experimentRowsFromObservations.stream().filter(x-> mpTermId.contains(x.getPhenotypeTerm().getName())).collect(Collectors.toSet()));
            } else {
                experimentRows.addAll(experimentRowsFromObservations);
            }
            Map<CombinedObservationKey, ExperimentsDataTableRow> observationsMap = experimentRows.stream().collect(Collectors.toMap(ExperimentsDataTableRow::getCombinedKey, row -> row));

            return observationsMap;
        }
    }

    private String getSignificanceStringForTable(ExperimentsDataTableRow experimentsDataTableRow, String phenotypeTermName) {
        // Significant, Not significant, Unable to process, Supplied
        String significant = "N/A";
        if (experimentsDataTableRow.getStatisticalMethod() != null && experimentsDataTableRow.getStatisticalMethod().toLowerCase().contains("failed")) {
            significant = "Unable to process";
        } else if (experimentsDataTableRow.getStatisticalMethod() != null && experimentsDataTableRow.getStatisticalMethod().toLowerCase().contains("supplied")) {
            significant = "Supplied";
        } else if (experimentsDataTableRow.getStatus()!= null && experimentsDataTableRow.getStatus().toLowerCase().contains("not tested (no variation)")) {
            significant = "Not significant";
        } else if (StringUtils.isNotEmpty(phenotypeTermName)) {
                significant = "Significant";
        } else if (experimentsDataTableRow.getStatus()!= null && experimentsDataTableRow.getStatus().equals("Success")) {
            significant = "Not significant";
        }
        return significant;
    }
}
