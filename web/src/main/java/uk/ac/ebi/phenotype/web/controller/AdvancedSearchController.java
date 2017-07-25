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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.neo4j.repository.AlleleRepository;
import org.mousephenotype.cda.neo4j.repository.DiseaseGeneRepository;
import org.mousephenotype.cda.neo4j.repository.DiseaseModelRepository;
import org.mousephenotype.cda.neo4j.repository.EnsemblGeneIdRepository;
import org.mousephenotype.cda.neo4j.repository.GeneRepository;
import org.mousephenotype.cda.neo4j.repository.HpRepository;
import org.mousephenotype.cda.neo4j.repository.HumanGeneSymbolRepository;
import org.mousephenotype.cda.neo4j.repository.MarkerSynonymRepository;
import org.mousephenotype.cda.neo4j.repository.MouseModelRepository;
import org.mousephenotype.cda.neo4j.repository.MpRepository;
import org.mousephenotype.cda.neo4j.repository.OntoSynonymRepository;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import uk.ac.ebi.phenotype.service.*;
import org.neo4j.ogm.model.Result;

@Controller
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public class AdvancedSearchController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    private String NA = "not available";

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    private SolrIndex solrIndex;

    @Autowired
    @Qualifier("komp2DataSource")
    private DataSource komp2DataSource;

    @Autowired
    @Qualifier("mpCore")
    private SolrClient mpCore;

    @Autowired
    GeneRepository geneRepository;

    @Autowired
    AlleleRepository alleleRepository;

    @Autowired
    EnsemblGeneIdRepository ensemblGeneIdRepository;

    @Autowired
    MarkerSynonymRepository markerSynonymRepository;

    @Autowired
    HumanGeneSymbolRepository humanGeneSymbolRepository;

    @Autowired
    MpRepository mpRepository;

    @Autowired
    HpRepository hpRepository;

    @Autowired
    OntoSynonymRepository ontoSynonymRepository;

    @Autowired
    DiseaseGeneRepository diseaseGeneRepository;

    @Autowired
    DiseaseModelRepository diseaseModelRepository;

    @Autowired
    MouseModelRepository mouseModelRepository;

    @Autowired
    @Qualifier("autosuggestCore")
    private SolrClient autosuggestCore;
    
    @Autowired
    private AdvancedSearchService advancedSearchService;


    private String hostname = null;
    private String baseUrl = null;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    @RequestMapping(value = "/graph/nodeName?", method = RequestMethod.POST)
//    public ResponseEntity<String> dataTableNeo4jBq(
//            @RequestParam(value = "docType", required = true) String docType,
//            @RequestParam(value = "q", required = true) String query,
//            HttpServletResponse response,
//            Model model) throws IOException, URISyntaxException, SolrServerException{
//
//        String sortStr = "&sort=score desc";
//        String solrBq = null;
//        String qfStr = query.contains("*") ? "auto_suggest" : "string auto_suggest";
//
////        if ( thisInput.hasClass('srchMp') ){
////            docType = 'mp';
////            solrBq = "&bq=mp_term:*^90 mp_term_synonym:*^80 mp_narrow_synonym:*^75";
////        }
////        else if ( thisInput.hasClass('srchHp') ){
////            docType = 'hp';
////            solrBq = "&bq=hp_term:*^90 hp_term_synonym:*^80";
////        }
////        else if ( thisInput.hasClass('srchDiseaseModel') ){
////            docType = 'disease';
////        }
////
////        thisInput.autocomplete({
////                source: function( request, response ) {
////            var qfStr = request.term.indexOf("*") != -1 ? "auto_suggest" : "string auto_suggest";
////            // var facetStr = "&facet=on&facet.field=docType&facet.mincount=1&facet.limit=-1";
////            var sortStr = "&sort=score desc";
////            // alert(solrUrl + "/autosuggest/select?rows=10&fq=docType:" + docType + "&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq + sortStr);                        )
////
////            $.ajax({
////                    //url: solrUrl + "/autosuggest/select?wt=json&qf=string auto_suggest&defType=edismax" + solrBq,
////                    url: solrUrl + "/autosuggest/select?rows=10&fq=docType:" + docType + "&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq + sortStr,
////                    dataType
//
//        if(docType.equals("mp")){
//            solrBq = "&bq=mp_term:*^90 mp_term_synonym:*^80 mp_narrow_synonym:*^75";
////            autoSuggestService.getMpTermByKeyword(docType, qfStr, solrBq);
//            String solrurl = SolrUtils.getBaseURL(solrIndex.getSolrServer("autosuggest"))
//                    + "/select?q=" + query +
//
//            //System.out.println("QueryBroker url: "+ solrurl);
//            JSONObject json = solrIndex.getResults(solrurl);
//        }
//        return null;
//    }


        @RequestMapping(value="/meshtree", method=RequestMethod.GET)
    public String loadmeshtreePage(
            @RequestParam(value = "core", required = false) String core,
            HttpServletRequest request,
            Model model) {

        String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml(core);
        model.addAttribute("outputFields", outputFieldsHtml);

        return "treetest";
    }

    @RequestMapping(value="/batchQuery3", method=RequestMethod.GET)
    public String loadBatchQueryPage3(
            @RequestParam(value = "core", required = false) String core,
            @RequestParam(value = "fllist", required = false) String fllist,
            @RequestParam(value = "idlist", required = false) String idlist,
            HttpServletRequest request,
            Model model) {

        String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml2(core);
        model.addAttribute("outputFields", outputFieldsHtml);

        if ( idlist != null) {
            model.addAttribute("core", core);
            model.addAttribute("fllist", fllist);
            model.addAttribute("idlist", idlist);
        }

        return "batchQuery3";
    }
    @RequestMapping(value="/advancedSearch", method=RequestMethod.GET)
    public String loadAdvSrchPage(
            HttpServletRequest request,
            Model model) {

        return "advancedSearch";
    }
    @RequestMapping(value="/batchQuery2", method=RequestMethod.GET)
    public String loadBatchQueryPage2(
            @RequestParam(value = "core", required = false) String core,
            @RequestParam(value = "fllist", required = false) String fllist,
            @RequestParam(value = "idlist", required = false) String idlist,
            HttpServletRequest request,
            Model model) {

        String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml2(core);
        model.addAttribute("outputFields", outputFieldsHtml);

        if ( idlist != null) {
            model.addAttribute("core", core);
            model.addAttribute("fllist", fllist);
            model.addAttribute("idlist", idlist);
        }

        return "batchQuery2";
    }


    @RequestMapping(value = "/dataTable_bq2", method = RequestMethod.POST)
    public ResponseEntity<String> dataTableNeo4jBq(
            @RequestParam(value = "idlist", required = true) String idlistStr,
            @RequestParam(value = "datatypeProperty", required = true) String datatypeProperty,
            @RequestParam(value = "dataType", required = true) String dataType,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SolrServerException {

        System.out.println("dataType: " +  dataType);;
        System.out.println("idlist: " + idlistStr);
        JSONObject dp = (JSONObject) JSONSerializer.toJSON(datatypeProperty);
        Set<String> labels = dp.keySet();
        System.out.println("Labels: "+ dp.keySet());

        if (dataType.equals("geneChr")){
            // convert coordiantes range to list of mouse gene ids
            String[] parts = idlistStr.replaceAll("\"","").split(":");
            String chr = parts[0].replace("chr","");
            String[] se = parts[1].split("-");
            String start = se[0];
            String end = se[1];
            String mode = "nonExport";
            List<String> geneIds = solrIndex.fetchQueryIdsFromChrRange(chr, start, end, mode);
            idlistStr = StringUtils.join(geneIds, ",");
        }

//        String cypher = null;
//        if (labels.size() == 2 && labels.contains("Gene") && labels.contains("Allele")){
//            cypher = "MATCH (g:Gene)-[:ALLELE]->(a:Allele)"
//        }

        return null;
    }

    @RequestMapping(value = "/fetchmpid", method = RequestMethod.GET)
    public ResponseEntity<String> fetchmpid(
            @RequestParam(value = "name", required = true) String termName,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws Exception {

        //System.out.println("****"+ termName);

        List<MpDTO> mp = getMpIdByTerm(termName);
        String mpId = null;

        if (mp.size() == 0){
            // use parent instead if term does not exits in slim (mpCore) but in narrowSynonym in the autoSuggestCore
            List<String> narrowMapping = new ArrayList<>();
            termName = advancedSearchService.mapNarrowSynonym2MpTerm(narrowMapping, termName, autosuggestCore);
            mp = getMpIdByTerm(termName);
            mpId = "narrow synonym of " + mp.get(0).getMpId()+ "," + termName;

            System.out.println(mpId + "," + termName);
        }
        else {
            mpId = mp.get(0).getMpId();
            System.out.println(mpId);
        }

        return new ResponseEntity<String>(mpId, createResponseHeaders(), HttpStatus.CREATED);
    }
    
    
    
    public List<MpDTO> getMpIdByTerm(String termName) throws IOException, SolrServerException {
        //System.out.println("****"+ termName);
        SolrQuery query = new SolrQuery()
                .setQuery("mp_term:\"" + termName + "\"")
                .setFields("mp_id");

        System.out.println("query for : " + termName + " with " + query);

        List<MpDTO> mp = mpCore.query(query).getBeans(MpDTO.class);

        return mp;
    }

    @RequestMapping(value = "/dataTableNeo4jAdvSrch", method = RequestMethod.POST)
    public ResponseEntity<String> advSrchDataTableJson2(
            @RequestParam(value = "params", required = true) String params,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws Exception {

    	System.out.println("calling dataTableNeo4jAdvSrch");

        baseUrl = request.getAttribute("baseUrl").toString();
        hostname = request.getAttribute("mappedHostname").toString();

        JSONObject jParams = (JSONObject) JSONSerializer.toJSON(params);
        System.out.println("jparams="+jParams.toString());

        AdvancedSearchPhenotypeForm mpForm = advancedSearchService.parsePhenotypeForm(jParams);
        AdvancedSearchGeneForm geneForm = advancedSearchService.parseGeneForm(jParams);
        AdvancedSearchDiseaseForm diseaseForm = advancedSearchService.parseDiseaseForm(jParams);
        String fileType = null;

        List<Object> objects = advancedSearchService.fetchGraphDataAdvSrchResult(mpForm, geneForm, diseaseForm, fileType);
        Result result = (Result) objects.get(0);
        List<String> narrowOrSynonymMappingList = (List<String>) objects.get(1);
        Map<String, List<String>> dataTypeColsMap = (Map<String, List<String>>) objects.get(2);

        JSONObject jcontent = advancedSearchService.parseGraphResult(result, mpForm, geneForm, diseaseForm, fileType, baseUrl, hostname, narrowOrSynonymMappingList, dataTypeColsMap);
        System.out.println("--- " + jcontent.toString());
        System.out.println("narrowSynonym or synonym Mapping msg: " + jcontent.get("narrowOrSynonymMapping"));
        return new ResponseEntity<String>(jcontent.toString(), createResponseHeaders(), HttpStatus.CREATED);

        //return new ResponseEntity<String>("test", createResponseHeaders(), HttpStatus.CREATED);
    }

//    @RequestMapping(value = "/dataTableNeo4jBq", method = RequestMethod.POST)
//    public ResponseEntity<String> bqDataTableJson2(
//            @RequestParam(value = "idlist", required = true) String idlistStr,
//            @RequestParam(value = "properties", required = true) String properties,
//            @RequestParam(value = "datatypeProperties", required = true) String datatypeProperties,
//            @RequestParam(value = "dataType", required = true) String dataType,
//            @RequestParam(value = "childLevel", required = false) String childLevel,
//           // @RequestParam(value = "chrRange", required = false) String chrRange,
//            @RequestParam(value = "chr", required = false) String chr,
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Model model) throws Exception {
//
//        System.out.println("dataType: " +  dataType);
//
//        if (dataType.equals("EnsemblGeneId")){
//            dataType = "Gene";
//        }
//
//        if (childLevel != null){
//            System.out.println("MP childLevel: " + childLevel);
//        }
//
//        JSONObject jDatatypeProperties = (JSONObject) JSONSerializer.toJSON(datatypeProperties);
//        System.out.println(jDatatypeProperties.toString());
//
//        System.out.println("properties: "+ properties);
//
//        Set<String> labels = new LinkedHashSet<>(jDatatypeProperties.keySet());
//        System.out.println("Labels: "+ labels);
//
//        System.out.println("idlistStr: " + idlistStr);
//        Set<String> idlist = new LinkedHashSet<>();
//        if (idlistStr.matches("^\".*\"$")){
//            // ontology term name is quoted
//
//            idlist.add(idlistStr.replaceAll("\"",""));
//        }
//        else {
//            idlist = new LinkedHashSet<>(Arrays.asList(idlistStr.split(",")));
//        }
//        System.out.println("idlist: " + idlist);
//
//        String content = null;
//
//        String regionId = null;
//        int regionStart = 0;
//        int regionEnd = 0;
//
//        if (idlistStr.matches("^chr(\\w*):(\\d+)-(\\d+)$") ) {
//            System.out.println("find chr range");
//
//            Pattern pattern = Pattern.compile("^chr(\\w*):(\\d+)-(\\d+)$");
//            Matcher matcher = pattern.matcher(idlistStr);
//            while(matcher.find()) {
//                System.out.println("found: " + matcher.group(1));
//                regionId = matcher.group(1);
//                regionStart = Integer.parseInt(matcher.group(2));
//                regionEnd = Integer.parseInt(matcher.group(3));
//            }
//
//            String mode = "nonExport";
//        }
//
//        // chr filter for mp, hp, disease input type
//        if (chr != null){
//            regionId = chr;
//            System.out.println("chr filter: " + chr);
//        }
//        else {
//            System.out.println("chr filter is null");
//        }
//
//        baseUrl = request.getAttribute("baseUrl").toString();
//
//        content = fetchGraphData(dataType, idlist, labels, jDatatypeProperties, properties, regionId, regionStart, regionEnd, childLevel);
//
//        return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
//    }

//    public String fetchGraphData(String dataType, Set<String> srchKw, Set<String> labels, JSONObject jDatatypeProperties, String properties, String regionId, int regionStart, int regionEnd, String childLevel) throws Exception {
//
//        List<String> cols = new ArrayList<>();
//        for (String property : properties.split(",")){
//            cols.add(property);
//        }
//
//        int rowCount = 0;
//        JSONObject j = new JSONObject();
//        j.put("aaData", new Object[0]);
//
//        for(String kw : srchKw) {
//
//            Map<String, Set<String>> colValMap = new HashedMap();
//
//            colValMap.put("searchBy", new TreeSet<>());
//            colValMap.get("searchBy").add(kw);
//
//            rowCount++;
//
//            System.out.println("-- Working on " + kw);
//            System.out.println("-- region id " + regionId);
//
//            List<Object> objs = null;
//
//            if (kw.matches("^chr(\\w*):(\\d+)-(\\d+)$") && dataType.equals("Gene")) {
//                objs = geneRepository.findDataByChrRange(regionId, regionStart, regionEnd);
//            }
//            else if (kw.startsWith("MGI:")){
//                objs = geneRepository.findDataByMgiId(kw);
//            }
//            else if (dataType.equals("Gene")) {
//                objs = geneRepository.findDataByMarkerSymbol(kw);
//            }
//            else if (kw.startsWith("ENSMUSG")){
//                objs = ensemblGeneIdRepository.findDataByEnsemblGeneId(kw);
//            }
//
//            // DiseaseModel ID
//            else if ((kw.startsWith("OMIM:") || kw.startsWith("ORPHANET:") || kw.startsWith("DECIPHER:")) && regionId != null){
//                System.out.println("search disease " + kw + " and chr " + regionId);
//                objs = diseaseModelRepository.findDataByDiseaseIdChr(kw, regionId);
//                System.out.println("objs found: "+objs.size());
//            }
//            else if (kw.startsWith("OMIM:") || kw.startsWith("ORPHANET:") || kw.startsWith("DECIPHER:")){
//                System.out.println("search disease");
//                objs = diseaseModelRepository.findDataByDiseaseId(kw);
//            }
//            // DiseaseModel term
//            else if (dataType.equals("DiseaseModel") && regionId != null) {
//                System.out.println("Disease id normal");
//                objs = diseaseModelRepository.findDataByDiseaseTermChr(kw, regionId);
//            }
//            else if (dataType.equals("DiseaseModel")) {
//                System.out.println("Disease term normal");
//                objs = diseaseModelRepository.findDataByDiseaseTerm(kw);
//            }
//
//            // MP ID
//            else if (kw.startsWith("MP:") && regionId != null && childLevel != null) {
//
//                if (childLevel.equals("all")) {
//                    System.out.println("MP id with region and ALL children");
//                    objs = mpRepository.findAllChildrenMpsByMpIdChr(kw, regionId);
//                }
//                else {
//                    System.out.println("MP id with region and " + childLevel + " children level");
//                    int level = Integer.parseInt(childLevel);
//                    objs = mpRepository.findChildrenMpsByMpIdChr(kw, regionId, level);
//                }
//                objs = fetchTerms(objs);
//            }
//            else if (kw.startsWith("MP:") && regionId != null && childLevel == null) {
//                System.out.println("MP id with region");
//                objs = mpRepository.findDataByMpIdChr(kw, regionId);
//            }
//            else if (kw.startsWith("MP:") && regionId == null && childLevel != null) {
//                if (childLevel.equals("all")){
//                    System.out.println("MP id with ALL children");
//
//                    // first get all children mps (including self)
//                    objs = mpRepository.findAllChildrenMpsByMpId(kw);
//                    System.out.println("1. Got " + objs.size() + "children");
//                }
//                else if (childLevel != "0"){
//                    System.out.println("MP Id with " + childLevel + " children level");
//
//                    // first get all children mps (including self)
//                    int level = Integer.parseInt(childLevel);
//                    objs = mpRepository.findChildrenMpsByMpId(kw, level);
//                    System.out.println("2. Got " + objs.size() + "children");
//                }
//
//                // then query data by each mp and put together
//                objs = fetchTerms(objs);
//            }
//            else if (kw.startsWith("MP:") && regionId == null && childLevel == null){
//                System.out.println("MP id normal");
//                objs = mpRepository.findDataByMpId(kw);
//            }
//
//            // MP term
//            else if (dataType.equals("MP") && regionId != null && childLevel != null) {
//                System.out.println("MP term with all children and chr filter");
//                if (childLevel.equals("all")) {
//                    objs = mpRepository.findAllChildrenMpsByMpTermChr(kw, regionId);
//                }
//                else if (childLevel != "0"){
//                    System.out.println("MP term with " + childLevel + " children level and chr filter");
//
//                    // first get all children mps (including self)
//                    int level = Integer.parseInt(childLevel);
//                    objs = mpRepository.findChildrenMpsByMpTermChr(kw, regionId, level);
//                }
//                // then query data by each mp and put together
//                objs = fetchTerms(objs);
//            }
//            else if (dataType.equals("MP") && regionId != null && childLevel == null){
//                System.out.println("MP term with region");
//                objs = mpRepository.findDataByMpTermChr(kw, regionId);
//            }
//            else if (dataType.equals("Mp") && regionId == null && childLevel != null) {
//                if (childLevel.equals("all")){
//                    System.out.println("MP term with ALL children");
//
//                    // first get all children mps (including self)
//                    objs = mpRepository.findAllChildrenMpsByMpTerm(kw);
//                }
//                else if (childLevel != "0"){
//                    System.out.println("MP term with " + childLevel + " children level");
//
//                    // first get all children mps (including self)
//                    int level = Integer.parseInt(childLevel);
//                    objs = mpRepository.findChildrenMpsByMpTerm(kw, level);
//                }
//
//                // then query data by each mp and put together
//                objs = fetchTerms(objs);
//            }
//            else if (dataType.equals("Mp") && regionId == null && childLevel == null) {
//                System.out.println("MP term normal");
//                objs = mpRepository.findDataByMpTerm(kw);
//            }
//            // HP ID
//            else if (kw.startsWith("HP:") && regionId != null && childLevel != null) {
//                if (childLevel.equals("all")) {
//                    System.out.println("HP id with region and ALL children");
//                    objs = hpRepository.findAllChildrenHpsByHpIdChr(kw, regionId);
//                }
//                else {
//                    System.out.println("HP id with region and " + childLevel + " children level");
//                    int level = Integer.parseInt(childLevel);
//                    objs = hpRepository.findChildrenHpsByHpIdChr(kw, regionId, level);
//                }
//
//                // then query data by each mp and put together
//                objs = fetchTerms(objs);
//            }
//            else if (kw.startsWith("HP:") && regionId != null && childLevel == null) {
//                System.out.println("HP id with region");
//                objs = hpRepository.findDataByHpIdChr(kw, regionId);
//            }
//            else if (kw.startsWith("HP:") && regionId == null && childLevel != null) {
//                if (childLevel.equals("all")){
//                    System.out.println("HP id with ALL children");
//
//                    // first get all children hps (including self)
//                    objs = hpRepository.findAllChildrenHpsByHpId(kw);
//                    System.out.println("1. Got " + objs.size() + "children");
//                }
//                else if (childLevel != "0"){
//                    System.out.println("HP Id with " + childLevel + " children level");
//
//                    // first get all children hps (including self)
//                    int level = Integer.parseInt(childLevel);
//                    objs = hpRepository.findChildrenHpsByHpId(kw, level);
//
//                    System.out.println("2. Got " + objs.size() + "children");
//                }
//
//                // then query data by each mp and put together
//                objs = fetchTerms(objs);
//            }
//            else if (kw.startsWith("HP:") && regionId == null && childLevel == null){
//                System.out.println("HP id normal");
//                objs = hpRepository.findDataByHpId(kw);
//            }
//
//            // HP term
//            else if (dataType.equals("HP") && regionId != null && childLevel != null) {
//                System.out.println("HP term with all children and chr filter");
//                if (childLevel.equals("all")) {
//                    objs = hpRepository.findAllChildrenHpsByHpTermChr(kw, regionId);
//                }
//                else if (childLevel != "0"){
//                    System.out.println("HP term with " + childLevel + " children level and chr filter");
//
//                    // first get all children hps (including self)
//                    int level = Integer.parseInt(childLevel);
//                    objs = hpRepository.findChildrenHpsByHpTermChr(kw, regionId, level);
//                }
//                // then query data by each hp and put together
//                objs = fetchTerms(objs);
//            }
//            else if (dataType.equals("HP") && regionId != null && childLevel == null){
//                System.out.println("HP term with region");
//                objs = hpRepository.findDataByHpTermChr(kw, regionId);
//            }
//            else if (dataType.equals("Hp") && regionId == null && childLevel != null) {
//                if (childLevel.equals("all")){
//                    System.out.println("HP term with ALL children");
//
//                    // first get all children hps (including self)
//                    objs = hpRepository.findAllChildrenHpsByHpTerm(kw);
//                }
//                else if (childLevel != "0"){
//                    System.out.println("HP term with " + childLevel + " children level");
//
//                    // first get all children hps (including self)
//                    int level = Integer.parseInt(childLevel);
//                    objs = hpRepository.findChildrenHpsByHpTerm(kw, level);
//                }
//
//                // then query data by each hp and put together
//                objs = fetchTerms(objs);
//            }
//            else if (dataType.equals("Hp") && regionId == null && childLevel == null) {
//                System.out.println("HP term normal");
//                objs = hpRepository.findDataByHpTerm(kw);
//            }
//            else if (dataType.equals("HumanGeneSymbol")){
//                objs = humanGeneSymbolRepository.findDataByHumanGeneSymbol(kw);
//            }
//
//
//            System.out.println("Data objects found: "+ objs.size());
//
//            advancedSearchService.populateColValMap(hostname, baseUrl,objs, colValMap, jDatatypeProperties);
//
//            System.out.println("About to prepare for rows");
//
//
//            List<String> rowData = new ArrayList<>();
//            for (String col : cols){
//                if (colValMap.containsKey(col)) {
//                    List<String> vals = new ArrayList<>(colValMap.get(col));
//
//                    int valSize = vals.size();
//
//                    if (valSize > 2) {
//                        // add showmore
//                        vals.add("<button rel=" + valSize + " class='showMore'>show all (" + valSize + ")</button>");
//                    }
//                    if (valSize == 1) {
//                        rowData.add(StringUtils.join(vals, ""));
//                    } else {
//                        rowData.add("<ul>" + StringUtils.join(vals, "") + "</ul>");
//                    }
//
//                    //System.out.println("col: " + col);
//                    if (col.equals("ontoSynonym")) {
//                        System.out.println(col + " -- " + vals);
//                    }
//                }
//                else {
//                    rowData.add(NA);
//                }
//
//            }
//            j.getJSONArray("aaData").add(rowData);
//
//        }
//
//        System.out.println("rows done");
//
//        j.put("iTotalRecords", rowCount);
//        j.put("iTotalDisplayRecords", rowCount);
//
//
//        return j.toString();
//    }
    
    
    
//
//    private List<Object> fetchTerms(List<Object> objs){
//
//        List<Object> childTerms = new ArrayList<>();
//        for(Object o : objs){
//            if (o.getClass().getSimpleName().equals("Mp")) {
//                Mp m = (Mp) o;
//                childTerms.addAll(mpRepository.findDataByMpId(m.getMpId()));
//            }
//            else {
//                Hp h = (Hp) o;
//                childTerms.addAll(hpRepository.findDataByHpId(h.getHpId()));
//            }
//        }
//        return childTerms;
//    }

    
    

    

    public String lowercaseListStr(String idlist) {
        List<String> lst = Arrays.asList(StringUtils.split(idlist,","));
        List<String> lst2 = new ArrayList<>();
        for (String s : lst){
            lst2.add("lower(" + s + ")");
        }
        return StringUtils.join(lst2, ",");
    }

    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();

        // this returns json, but utf encoding failed
        //responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        // this returns html string, not json, and is utf encoded
        responseHeaders.add("Content-Type", "text/html; charset=utf-8");


        return responseHeaders;
    }


    @RequestMapping(value = "/chrlen", method = RequestMethod.GET)
    public @ResponseBody
    String chrlen(
            @RequestParam(value = "chr", required = false) String chr,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SolrServerException, SQLException {

        //fetchChrLenJson();
        Connection connKomp2 = komp2DataSource.getConnection();
        String len = null;

        if (chr == null){
            String sql = "select name, length from seq_region where name not like '%patch'";

            List<String> nameLen = new ArrayList<>();
            try (PreparedStatement p = connKomp2.prepareStatement(sql)) {
                ResultSet resultSet = p.executeQuery();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String length = Integer.toString(resultSet.getInt("length"));
                    nameLen.add("<span class='chrname'>Chr: " + name + "</span><span class='chrlen'>" + length + "</span>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            len = StringUtils.join(nameLen, "<br>");

        }
        else {
            String sql = "SELECT length FROM seq_region WHERE name ='" + chr + "'";

            try (PreparedStatement p = connKomp2.prepareStatement(sql)) {
                ResultSet resultSet = p.executeQuery();

                while (resultSet.next()) {
                    len = Integer.toString(resultSet.getInt("length"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return len;
    }
}