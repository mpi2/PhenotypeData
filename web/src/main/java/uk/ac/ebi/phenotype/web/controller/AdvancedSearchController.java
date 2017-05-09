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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.neo4j.entity.*;
import org.mousephenotype.cda.neo4j.repository.*;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.AutoSuggestService;
import org.mousephenotype.cda.solr.service.SolrIndex;

import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.PipelineDTO;
import org.neo4j.ogm.model.GraphRowListModel;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public class AdvancedSearchController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private Session neo4jSession;

//    @Autowired
//    Neo4jTemplate neo4jSession;


    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    private SolrIndex solrIndex;

    @NotNull
    @Value("${neo4jDbPath}")
    private String neo4jDbPath;

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
    AutoSuggestService autoSuggestService;

    private String NA = "not available";
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
        SolrQuery query = new SolrQuery()
                .setQuery("mp_term:\"" + termName + "\"")
                .setFields("mp_id");

        List<MpDTO> mp = mpCore.query(query).getBeans(MpDTO.class);
        String mpId = mp.get(0).getMpId();

        return new ResponseEntity<String>(mpId, createResponseHeaders(), HttpStatus.CREATED);
    }


    @RequestMapping(value = "/dataTableNeo4jAdvSrch", method = RequestMethod.POST)
    public ResponseEntity<String> advSrchDataTableJson2(
            @RequestParam(value = "params", required = true) String params,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws Exception {

        baseUrl = request.getAttribute("baseUrl").toString();
        hostname = request.getAttribute("mappedHostname").toString();

        JSONObject jParams = (JSONObject) JSONSerializer.toJSON(params);
        System.out.println(jParams.toString());

        JSONArray properties = jParams.getJSONArray("properties");
        System.out.println("columns: " + properties);

        String content = null;
        Boolean isExport = false;
        JSONObject jcontent = fetchGraphDataAdvSrch(jParams, isExport);

        return new ResponseEntity<String>(jcontent.toString(), createResponseHeaders(), HttpStatus.CREATED);
    }

    public JSONObject fetchGraphDataAdvSrch(JSONObject jParams, Boolean isExport) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String significant = " AND sr.significant = true ";
        String phenotypeSexes = composePhenotypeSexStr(jParams);
        String chrRange = composeChrRangeStr(jParams);
        String geneList = composeGeneListStr(jParams);
        String genotypes = composeGenotypesStr(jParams);
        String alleleTypes = composeAlleleTypesStr(jParams);

        // disease
        String phenodigmScore = composePhenodigmScoreStr(jParams);  // always non-empty
        String diseaseGeneAssociation = composeDiseaseGeneAssociation(jParams);
        String humanDiseaseTerm = composeHumanDiseaseTermStr(jParams);

        String mpSearchCypher = "";

        String mpStr = null;
        if (jParams.containsKey("srchMp")) {
            mpStr = jParams.getString("srchMp");
        }

        //String mpStr = "  ( APERT-CROUZON DISEASE, INCLUDED  AND mpb alcohokl  )OR  mapasdfkl someting "; // (a and b) or c
        //String mpStr = " dfkdfkdfk kdkk AND ( sfjjjj ii OR kkkk, ) "; // a and (b or c)
        //String mpStr = " ( asdfsdaf OR erueuru asdfsda ,  sdfa ) AND sakdslfjie dfd ";  // (a or b) and c
        //String mpStr = " asdfsdaf OR  ( erueuru asdfsda ,  sdfa AND sakdslfjie dfd ) ";  // a or (b and c)
        //String mpStr = "asdfsdaf AND erueuru asdfsda ,  sdfa AND sakdslfjie dfd "; // a and b and c
        //String mpStr = "asdfsdaf AND erueuru asdfsda ,  sdfa  "; // a and b
        //String mpStr = "asdfsdaf OR erueuru asdfsda ,  sdfa OR sakdslfjie dfd ";  // a or b or c
        //String mpStr = "asdfsdaf OR erueuru asdfsda ,  sdfa ";  // a or b

        System.out.println("mp query: "+ mpStr);

        String regex_aAndb_Orc = "\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*AND\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*OR\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*";
        String regex_aAnd_bOrc = "\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*AND\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*OR\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*";
        String regex_aOrb_andc = "\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*OR\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*AND\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*";
        String regex_aOr_bAndc = "\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*OR\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*AND\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*";
        String regex_aAndb = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*AND\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";
        String regex_aAndbAndc = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*AND\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*AND\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";
        String regex_aOrb = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*OR\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";
        String regex_aOrbOrc = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*OR\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*OR\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";

        HashedMap params = new HashedMap();

        Result result = null;

        String sortStr = geneList.isEmpty() ? " ORDER BY g.markerSymbol " : "";

        if (mpStr == null ){
            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                    + " WHERE sr.significant = true "
                    + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                    + " WITH g, a, sr, mp "
                    + " MATCH (g)<-[:GENE]-(dm:DiseaseModel) WHERE "
                    + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm;
                    //+ " RETURN a, g, mp, dm";


            query += isExport ? " RETURN g, a, collect(distinct mp), collect(distinct dm)" + sortStr : " RETURN a, g, mp, dm" + sortStr;

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (! mpStr.contains("AND") && ! mpStr.contains("OR") ) {
            // single mp term

            mpStr = mpStr.trim();
            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) "
                    + " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                    + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                    + " WITH g, a, sr, mp "
                    + " MATCH (g)<-[:GENE]-(dm:DiseaseModel) WHERE "
                    + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm;

            query += isExport ? " RETURN a, g, collect(distinct mp), collect(distinct dm)" + sortStr
                    : " RETURN a, g, mp, dm" + sortStr;

            params.put("mpA", mpStr);

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aAndb_Orc)) {
            System.out.println("matches (a and b) or c");

            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) "
                + " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') WITH g, a, sr, mp "
                + " MATCH (g)<-[:GENE]-(a1:Allele)<-[:ALLELE]-(sr1:StatisticalResult)-[:MP]->(mp1:Mp)-[:PARENT*0..]->(mp0:Mp) WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                + " WITH collect({genes:g, mps:mp, srs:sr, alleles:a, mps1:mp1, srs1:sr1, alleles1:a1}) as list1 "
                + " MATCH (g2:Gene)<-[:GENE]-(a2:Allele)<-[:ALLELE]-(sr2:StatisticalResult)-[:MP]->(mp2:Mp)-[:PARENT*0..]->(mp0:Mp) "
                + " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                + " WITH list1 + collect({genes: g2, mps:mp2, srs:sr2, alleles:a2, mps1:'', srs1:'', alleles1:''}) as alllist "
                + " unwind alllist as nodes "
                + " WITH nodes.genes as g, nodes "
                + " MATCH (g)<-[:GENE]-(dm:DiseaseModel) ";
                //+ " RETURN g, nodes.mps, nodes.srs, nodes.alleles, dm, nodes.mps1, nodes.srs1, nodes.alleles1";

            query += isExport ? " RETURN g, collect(distinct nodes.alleles), collect(distinct nodes.alleles1), collect(distinct nodes.mps), collect(distinct nodes.mps1), collect(distinct dm)" + sortStr
                    : " RETURN g, nodes.alleles, nodes.alleles1, dm, nodes.mps, nodes.mps1" + sortStr;

            Pattern pattern = Pattern.compile(regex_aAndb_Orc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();;
                String mpB = matcher.group(2).trim();;
                String mpC = matcher.group(3).trim();;
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mpA);
                params.put("mpB", mpB);
                params.put("mpC", mpC);

                System.out.println("Query: "+ query);
                result =  neo4jSession.query(query, params);
            }
        }
        else if (mpStr.matches(regex_aAnd_bOrc)) {
            logger.info("matches a and (b or c)");

            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) "
                + "WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp "
                + " MATCH (g)<-[:GENE]-(a1:Allele)<-[:ALLELE]-(sr1:StatisticalResult)-[:MP]->(mp1:Mp)-[:PARENT*0..]->(mp0:Mp) WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp, a1, sr1, mp1 "
                + " MATCH (g)<-[:GENE]-(dm:DiseaseModel) WHERE "
                + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm;

            query += isExport ? "RETURN g, collect(distinct a), collect(distinct a1), collect(distinct mp), collect(distinct mp1), collect(distinct dm)" + sortStr
                    : " RETURN g, a, dm, mp, a1, mp1" + sortStr;

            Pattern pattern = Pattern.compile(regex_aAnd_bOrc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(2).trim();;
                String mpC = matcher.group(3).trim();;
                //logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mpA);
                params.put("mpB", mpB);
                params.put("mpC", mpC);

                System.out.println("Query: "+ query);
                result =  neo4jSession.query(query, params);
            }
        }
        else if (mpStr.matches(regex_aOrb_andc)) {
            logger.info("matches (a or b) and c");

            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) "
                    + "WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                    + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                    + " WITH g, a, sr, mp "
                    + " MATCH (g)<-[:GENE]-(a1:Allele)<-[:ALLELE]-(sr1:StatisticalResult)-[:MP]->(mp1:Mp)-[:PARENT*0..]->(mp0:Mp) WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                    + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                    + " WITH g, a, sr, mp, a1, sr1, mp1 "
                    + "MATCH (g)<-[:GENE]-(dm:DiseaseModel) WHERE "
                    + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm;
                    //+ "RETURN g, a, sr, dm, mp, a1, sr1, mp1";

            query += isExport ? " RETURN g, collect(distinct a), collect(distinct a1), collect(distinct mp), collect(distinct mp1), collect(distinct dm)" + sortStr
                    : " RETURN g, a, a1, dm, mp, mp1" + sortStr;

            Pattern pattern = Pattern.compile(regex_aOrb_andc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(2).trim();;
                String mpC = matcher.group(3).trim();;
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mpA);
                params.put("mpB", mpB);
                params.put("mpC", mpC);

                System.out.println("Query: "+ query);
                result =  neo4jSession.query(query, params);
            }
        }
        else if (mpStr.matches(regex_aOr_bAndc)) {
            System.out.println("matches a or (b and c)");

            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) "
                    + " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') WITH g, a, sr, mp "
                    + " MATCH (g)<-[:GENE]-(a1:Allele)<-[:ALLELE]-(sr1:StatisticalResult)-[:MP]->(mp1:Mp)-[:PARENT*0..]->(mp0:Mp) WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                    + " WITH collect({genes:g, mps:mp, srs:sr, alleles:a, mps1:mp1, srs1:sr1, alleles1:a1}) as list1 "
                    + " MATCH (g2:Gene)<-[:GENE]-(a2:Allele)<-[:ALLELE]-(sr2:StatisticalResult)-[:MP]->(mp2:Mp)-[:PARENT*0..]->(mp0:Mp) "
                    + " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                    + " WITH list1 + collect({genes: g2, mps:mp2, srs:sr2, alleles:a2, mps1:'', srs1:'', alleles1:''}) as alllist "
                    + " unwind alllist as nodes "
                    + " WITH nodes.genes as g, nodes "
                    + " MATCH (g)<-[:GENE]-(dm:DiseaseModel) WITH nodes, dm ";
                    //+ " RETURN g, nodes.mps, nodes.srs, nodes.alleles, dm, nodes.mps1, nodes.srs1, nodes.alleles1";

            query += isExport ? " RETURN g, collect(distinct nodes.alleles), collect(distinct nodes.alleles1), collect(distinct dm), collect(distinct nodes.mps), collect(distinct nodes.mps1)" + sortStr
                    : " RETURN g, nodes.alleles, nodes.alleles1, nodes.mps, nodes.mps1, dm" + sortStr;


            Pattern pattern = Pattern.compile(regex_aOr_bAndc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(2).trim();;
                String mpC = matcher.group(3).trim();;

                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mpA);
                params.put("mpB", mpB);
                params.put("mpC", mpC);

                System.out.println("Query: "+ query);
                result =  neo4jSession.query(query, params);
            }
        }
        else if (mpStr.matches(regex_aAndbAndc)) {
            logger.info("matches a and b and c");

            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) "
                + " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp "
                + " MATCH (g)<-[:GENE]-(a1:Allele)<-[:ALLELE]-(sr1:StatisticalResult)-[:MP]->(mp1:Mp)-[:PARENT*0..]->(mp0:Mp) WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp, a1, sr1, mp1 "
                + " MATCH (g)<-[:GENE]-(a2:Allele)<-[:ALLELE]-(sr2:StatisticalResult)-[:MP]->(mp2:Mp)-[:PARENT*0..]->(mp0:Mp) WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp, a1, sr1, mp1, a2, sr2, mp2 "
                + " MATCH (g)<-[:GENE]-(dm:DiseaseModel) WHERE "
                + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm;
                //+ " RETURN g, a, dm, mp, a1, sr1, mp1, a2, sr2, mp2";

            query += isExport ? " RETURN g, a2, collect(distinct dm), collect(distinct mp), collect(distinct mp1), collect(distinct mp2)" + sortStr
                    : " RETURN g, a2, dm, mp, mp1, mp2" + sortStr;

            Pattern pattern = Pattern.compile(regex_aAndbAndc);
            Matcher matcher = pattern.matcher(mpStr);;

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();;
                String mpB = matcher.group(2).trim();;
                String mpC = matcher.group(3).trim();;
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mpA);
                params.put("mpB", mpB);
                params.put("mpC", mpC);

                System.out.println("Query: "+ query);
                result =  neo4jSession.query(query, params);
            }
        }
        else if (mpStr.matches(regex_aAndb)) {
            logger.info("matches a and b");

            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) "
                + "WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp "
                + " MATCH (g)<-[:GENE]-(a1:Allele)<-[:ALLELE]-(sr1:StatisticalResult)-[:MP]->(mp1:Mp)-[:PARENT*0..]->(mp0:Mp) WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, sr, mp, a1, sr1, mp1 "
                + " MATCH (g)<-[:GENE]-(dm:DiseaseModel) WHERE "
                + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm;
//                + " RETURN g, a1, dm, mp, mp1";

            query += isExport ? " RETURN a1, g, collect(distinct mp), collect(distinct mp1), collect(distinct dm)" + sortStr
                    : "RETURN g, a1, dm, mp, mp1" + sortStr;

            Pattern pattern = Pattern.compile(regex_aAndb);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                String mpA = matcher.group(1).trim();;
                String mpB = matcher.group(2).trim();;
                logger.info("A: '{}', B: '{}'", mpA, mpB);

                params.put("mpA", mpA);
                params.put("mpB", mpB);

                System.out.println("Query: "+ query);
                result =  neo4jSession.query(query, params);
            }

        }
        else if (mpStr.matches(regex_aOrbOrc)) {
            logger.info("matches a or b or c");

            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) "
                + "WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp "
                + " MATCH (g)<-[:GENE]-(dm:DiseaseModel) WHERE "
                + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm;
                //+ " RETURN g, a, sr, dm, mp";

            query += isExport ? " RETURN g, a, collect(distinct dm), collect(distinct mp)" + sortStr : " RETURN g, a, dm, mp" + sortStr;

            Pattern pattern = Pattern.compile(regex_aOrbOrc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();;
                String mpB = matcher.group(2).trim();;
                String mpC = matcher.group(3).trim();;
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mpA);
                params.put("mpB", mpB);
                params.put("mpB", mpC);

                System.out.println("Query: "+ query);
                result =  neo4jSession.query(query, params);
            }
        }
        else if (mpStr.matches(regex_aOrb)) {
            logger.info("matches a or b");

            String query = "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) "
                + "WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                + significant + phenotypeSexes + chrRange + geneList + genotypes + alleleTypes
                + " WITH g, a, sr, mp "
                + " MATCH (g)<-[:GENE]-(dm:DiseaseModel) WHERE "
                + phenodigmScore + diseaseGeneAssociation + humanDiseaseTerm;
                //+ " RETURN g, a, sr, dm, mp";

            query += isExport ? " RETURN g, a, collect(distinct dm), collect(distinct mp)" + sortStr : " RETURN g, a, dm, mp" + sortStr;

            Pattern pattern = Pattern.compile(regex_aOrb);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                String mpA = matcher.group(1).trim();;
                String mpB = matcher.group(2).trim();;
                logger.info("A: '{}', B: '{}'", mpA, mpB);

                params.put("mpA", mpA);
                params.put("mpB", mpB);

                System.out.println("Query: "+ query);
                result =  neo4jSession.query(query, params);
            }
        }

        System.out.println("Done with query");

        int rowCount = 0;
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);
        j.put("iDisplayStart", 0);
        j.put("iDisplayLength", 10);

        List<String> rowDataExport = new ArrayList<>(); // for export
        List<String> rowDataOverview = new ArrayList<>(); // for overview

        if (isExport){

            List<String> cols = new ArrayList<>();
            List<String> dtypes = Arrays.asList("Allele", "Gene", "Mp", "DiseaseModel");
            Map<String, List<String>> node2Properties = new LinkedHashMap<>();

            for (String dtype : dtypes){

                node2Properties.put(dtype, new ArrayList<String>());

                for(Object obj : jParams.getJSONArray(dtype)) {
                    String colName = obj.toString();

                    //System.out.println("colname: " + colName);
                    if (colName.equals("alleleSymbol") &&  !jParams.getJSONArray(dtype).contains("alleleMgiAccessionId")){
                        cols.add(colName);
                        cols.add("alleleMgiAccessionId");
                        node2Properties.get(dtype).add(colName);
                        node2Properties.get(dtype).add("alleleMgiAccessionId");
                    }
                    else if (colName.equals("markerSymbol") &&  !jParams.getJSONArray(dtype).contains("mgiAccessionId")){
                        cols.add(colName);
                        cols.add("mgiAccessionId");
                        node2Properties.get(dtype).add(colName);
                        node2Properties.get(dtype).add("mgiAccessionId");
                    }
                    else if (colName.equals("mpTerm") &&  !jParams.getJSONArray(dtype).contains("mpId")){
                        cols.add(colName);
                        cols.add("mpId");
                        node2Properties.get(dtype).add(colName);
                        node2Properties.get(dtype).add("mpId");
                    }
                    else if (colName.equals("diseaseTerm") &&  !jParams.getJSONArray(dtype).contains("diseaseId")){
                        cols.add(colName);
                        cols.add("diseaseId");
                        node2Properties.get(dtype).add(colName);
                        node2Properties.get(dtype).add("diseaseId");
                    }
                    else {
                        cols.add(colName);
                        node2Properties.get(dtype).add(colName);
                    }
                }
            }

            rowDataExport.add(StringUtils.join(cols, "\t")); // column

            for (Map<String,Object> row : result) {
                //System.out.println(row.toString());
                //System.out.println("cols: " + row.size());

                List<String> data = new ArrayList<>(); // for export

                Map<String, Set<String>> colValMap = new HashedMap();

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    //System.out.println(entry.getKey() + " / " + entry.getValue());

                    if (entry.getValue() != null) {
                        if (entry.getKey().startsWith("collect(distinct")) {
                            List<Object> objs = (List<Object>) entry.getValue();

                            for (Object obj : objs) {
                                populateColValMapAdvSrch(node2Properties, obj, colValMap, jParams, isExport);
                            }
                        } else {
                            Object obj = entry.getValue();
                            populateColValMapAdvSrch(node2Properties, obj, colValMap, jParams, isExport);
                        }
                    }
                }
                //-------- start of export
                //System.out.println("colValMap: " + colValMap.toString());

                //System.out.println("cols: " + cols);
                if (colValMap.size() > 0) {
                    for (String col : cols) {
                        //System.out.println("col now-1: " + col);
                        if (colValMap.containsKey(col)) {
                            //System.out.println("col now-2: " + col);
                            List<String> vals = new ArrayList<>(colValMap.get(col));
                            //.out.println(vals);
                            data.add(StringUtils.join(vals, "|"));
                        }
                    }
                    //System.out.println("row: " + data);
                }

                rowDataExport.add(StringUtils.join(data, "\t"));
            }
            j.put("rows", rowDataExport);
        }
        else {

            // overview

            List<String> cols = new ArrayList<>();
            Map<String, List<String>> node2Properties = new LinkedHashMap<>();

            List<String> dtypes = Arrays.asList("Allele", "Gene", "Mp", "DiseaseModel");
            for (String dtype : dtypes) {

                node2Properties.put(dtype, new ArrayList<String>());

                for (Object obj : jParams.getJSONArray(dtype)) {
                    String colName = obj.toString();
                    cols.add(colName);
                    node2Properties.get(dtype).add(colName);
                }
            }

            System.out.println("columns: " + cols);
            Map<String, Set<String>> colValMap = new HashedMap(); // for export

            for (Map<String, Object> row : result) {
                //System.out.println(row.toString());
                //System.out.println("cols: " + row.size());

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    //System.out.println(entry.getKey() + " / " + entry.getValue());
                    if (entry.getValue() != null) {
                        Object obj = entry.getValue();
                        //System.out.println("col: " + obj.toString());

                        populateColValMapAdvSrch(node2Properties, obj, colValMap, jParams, isExport);
                    }
                }
            }

            System.out.println("keys: "+ colValMap.keySet());
            // for overview
            List<String> rowData = new ArrayList<>();

            for (String col : cols){
                if (colValMap.containsKey(col)) {
                    List<String> vals = new ArrayList<>(colValMap.get(col));

                    int valSize = vals.size();

                    if (valSize > 2) {
                        // add showmore
                        vals.add("<button rel=" + valSize + " class='showMore'>show all (" + valSize + ")</button>");
                    }
                    if (valSize == 1) {
                        rowDataOverview.add(StringUtils.join(vals, ""));
                    } else {
                        rowDataOverview.add("<ul>" + StringUtils.join(vals, "") + "</ul>");
                    }

                    //System.out.println("col: " + col);
                    if (col.equals("ontoSynonym")) {
                        System.out.println(col + " -- " + vals);
                    }
                }
                else {
                    rowDataOverview.add(NA);
                }
            }

            System.out.println("rows done");

            j.put("iTotalRecords", rowCount);
            j.put("iTotalDisplayRecords", rowCount);
            j.getJSONArray("aaData").add(rowDataOverview);

            //System.out.println(j.toString());
        }
        return j;
    }

    private String composePhenotypeSexStr(JSONObject jParams){

        String phenotypeSexes = "";
        if (jParams.containsKey("phenotypeSexes")) {
            JSONArray sexes = jParams.getJSONArray("phenotypeSexes");
            List<String> list = new ArrayList<>();
            for(Object sex : sexes) {
                list.add("'" + sex.toString() + "' in sr.phenotypeSex");
            }

            if (list.size() > 0) {
                phenotypeSexes = " AND (" + StringUtils.join(list, " OR ") + ")";
            }
        }
        return phenotypeSexes;
    }

    private String composeChrRangeStr(JSONObject jParams){
        String chrRange = "";

        if (jParams.containsKey("chrRange")) {
            String range = jParams.getString("chrRange");
            if (range.matches("^chr(\\w*):(\\d+)-(\\d+)$")) {
                System.out.println("find chr range");

                Pattern pattern = Pattern.compile("^chr(\\w*):(\\d+)-(\\d+)$");
                Matcher matcher = pattern.matcher(range);
                while (matcher.find()) {
                    System.out.println("found: " + matcher.group(1));
                    String regionId = matcher.group(1);
                    int regionStart = Integer.parseInt(matcher.group(2));
                    int regionEnd = Integer.parseInt(matcher.group(3));

                    chrRange = " AND g.chrId='" + regionId + "' AND g.chrStart >= " + regionStart + " AND g.chrEnd <= " + regionEnd + " ";
                }
            }
        }
        else if (jParams.containsKey("chr")) {
            chrRange = " AND g.chrId='" + jParams.getString("chr") + "' ";
        }

        return chrRange;
    }

    private String composeGeneListStr(JSONObject jParams) {
        String genelist = "";

        if (jParams.containsKey("mouseGeneList")) {
           // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();

            for (Object name : jParams.getJSONArray("mouseGeneList")){
               list.add("'" + name.toString() + "'");
           }

           String glist = StringUtils.join(list, ",");
           boolean isSym = glist.contains("MGI:") ? false : true;
           if (isSym) {
               genelist = " AND g.markerSymbol in [" + glist + "] ";
           }
           else {
               genelist = " AND g.mgiAccessionId in [" + glist + "] ";
           }
        }
        else if (jParams.containsKey("humanGeneList")) {
            List<String> list = new ArrayList<>();

            for (Object name : jParams.getJSONArray("humanGeneList")){
                list.add("'" + name.toString() + "' in g.humanGeneSymbol");
            }

            if (list.size() > 0) {
                genelist = " AND (" + StringUtils.join(list, " OR ") + ") ";
            }
        }

        return genelist;
    }

    private String composeGenotypesStr(JSONObject jParams) {
        String genotypes = "";

        if (jParams.containsKey("genotypes")) {
            // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();
            for (Object name : jParams.getJSONArray("genotypes")) {
                list.add("'" + name.toString() + "'");
            }
            if (list.size() > 0) {
                genotypes = " AND sr.zygosity IN [" + StringUtils.join(list, ",") + "]";
            }
        }
        return genotypes;
    }

    private String composeAlleleTypesStr(JSONObject jParams){
        String alleleTypes = "";

        Map<String, String> alleleTypeMapping = new HashMap<>();
        alleleTypeMapping.put("CRISPR(em)", "Endonuclease-mediated"); // mutation_type
        alleleTypeMapping.put("KOMP", "deletion");  // mutation_type
        alleleTypeMapping.put("KOMP.1", ".1");
        alleleTypeMapping.put("EUCOMM A", "a");
        alleleTypeMapping.put("EUCOMM B", "b");
        alleleTypeMapping.put("EUCOMM C", "c");
        alleleTypeMapping.put("EUCOMM D", "d");
        alleleTypeMapping.put("EUCOMM E", "e");

        List<String> mutationTypes = new ArrayList<>();

        if (jParams.containsKey("alleleTypes")) {
            // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();
            for (Object name : jParams.getJSONArray("alleleTypes")) {
                String atype = name.toString();

                if (atype.equals("CRISPR(em)") || atype.equals("KOMP")){
                    mutationTypes.add("'" + alleleTypeMapping.get(atype) + "'");
                }
                else {
                    list.add("'" + alleleTypeMapping.get(atype) + "'");
                }
            }

            if (list.size() > 0){
                alleleTypes = "a.alleleType IN [" + StringUtils.join(list, ",") + "]";
                if (mutationTypes.size() > 0) {
                    alleleTypes += " OR a.mutationType IN [" + StringUtils.join(mutationTypes, ",") + "]";
                }
                alleleTypes = " AND (" + alleleTypes + ") ";
            }
            else {
                if (mutationTypes.size() > 0) {
                    alleleTypes += "a.mutationType IN [" + StringUtils.join(mutationTypes, ",") + "]";
                }
                alleleTypes = " AND (" + alleleTypes + ") ";
            }
        }
        return alleleTypes;
    }

    private String composePhenodigmScoreStr(JSONObject jParams){
        String phenodigmScore = "";

        if (jParams.containsKey("phenodigmScore")) {
            String[] scores = jParams.getString("phenodigmScore").split(",");
            int low = Integer.parseInt(scores[0]);
            int high = Integer.parseInt(scores[1]);
            phenodigmScore = " dm.diseaseToModelScore >= " + low + " AND dm.diseaseToModelScore <= " + high + " ";
        }
        return phenodigmScore;
    }
    private String composeDiseaseGeneAssociation(JSONObject jParams){
        String diseaeGeneAssoc = "";

        if (jParams.containsKey("diseaseGeneAssociation")) {
            JSONArray assocs = jParams.getJSONArray("diseaseGeneAssociation");

            if (assocs.size() < 2 ){
                if (assocs.get(0).toString().equals("humanCurated")){
                    diseaeGeneAssoc = " AND dm.humanCurated = true ";
                }
                else {
                    diseaeGeneAssoc = " AND dm.humanCurated = false ";
                }
            }
        }
        return diseaeGeneAssoc;
    }
    private String composeHumanDiseaseTermStr(JSONObject jParams){
        String humanDiseaseTerm = "";

        if (jParams.containsKey("srchDiseaseModel")) {
            String name = jParams.getString("srchDiseaseModel");
            humanDiseaseTerm = " AND dm.diseaseTerm = '" + name.toString() + "' ";
        }
        return humanDiseaseTerm;
    }
   

    @RequestMapping(value = "/dataTableNeo4jBq", method = RequestMethod.POST)
    public ResponseEntity<String> bqDataTableJson2(
            @RequestParam(value = "idlist", required = true) String idlistStr,
            @RequestParam(value = "properties", required = true) String properties,
            @RequestParam(value = "datatypeProperties", required = true) String datatypeProperties,
            @RequestParam(value = "dataType", required = true) String dataType,
            @RequestParam(value = "childLevel", required = false) String childLevel,
           // @RequestParam(value = "chrRange", required = false) String chrRange,
            @RequestParam(value = "chr", required = false) String chr,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws Exception {

        System.out.println("dataType: " +  dataType);

        if (dataType.equals("EnsemblGeneId")){
            dataType = "Gene";
        }

        if (childLevel != null){
            System.out.println("MP childLevel: " + childLevel);
        }

        JSONObject jDatatypeProperties = (JSONObject) JSONSerializer.toJSON(datatypeProperties);
        System.out.println(jDatatypeProperties.toString());

        System.out.println("properties: "+ properties);

        Set<String> labels = new LinkedHashSet<>(jDatatypeProperties.keySet());
        System.out.println("Labels: "+ labels);

        System.out.println("idlistStr: " + idlistStr);
        Set<String> idlist = new LinkedHashSet<>();
        if (idlistStr.matches("^\".*\"$")){
            // ontology term name is quoted

            idlist.add(idlistStr.replaceAll("\"",""));
        }
        else {
            idlist = new LinkedHashSet<>(Arrays.asList(idlistStr.split(",")));
        }
        System.out.println("idlist: " + idlist);

        String content = null;

        String regionId = null;
        int regionStart = 0;
        int regionEnd = 0;

        if (idlistStr.matches("^chr(\\w*):(\\d+)-(\\d+)$") ) {
            System.out.println("find chr range");

            Pattern pattern = Pattern.compile("^chr(\\w*):(\\d+)-(\\d+)$");
            Matcher matcher = pattern.matcher(idlistStr);
            while(matcher.find()) {
                System.out.println("found: " + matcher.group(1));
                regionId = matcher.group(1);
                regionStart = Integer.parseInt(matcher.group(2));
                regionEnd = Integer.parseInt(matcher.group(3));
            }

            String mode = "nonExport";
        }

        // chr filter for mp, hp, disease input type
        if (chr != null){
            regionId = chr;
            System.out.println("chr filter: " + chr);
        }
        else {
            System.out.println("chr filter is null");
        }

        baseUrl = request.getAttribute("baseUrl").toString();

        content = fetchGraphData(dataType, idlist, labels, jDatatypeProperties, properties, regionId, regionStart, regionEnd, childLevel);

        return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
    }

    public String fetchGraphData(String dataType, Set<String> srchKw, Set<String> labels, JSONObject jDatatypeProperties, String properties, String regionId, int regionStart, int regionEnd, String childLevel) throws Exception {

        List<String> cols = new ArrayList<>();
        for (String property : properties.split(",")){
            cols.add(property);
        }

        int rowCount = 0;
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        for(String kw : srchKw) {

            Map<String, Set<String>> colValMap = new HashedMap();

            colValMap.put("searchBy", new HashSet<>());
            colValMap.get("searchBy").add(kw);

            rowCount++;

            System.out.println("-- Working on " + kw);
            System.out.println("-- region id " + regionId);

            List<Object> objs = null;

            if (kw.matches("^chr(\\w*):(\\d+)-(\\d+)$") && dataType.equals("Gene")) {
                objs = geneRepository.findDataByChrRange(regionId, regionStart, regionEnd);
            }
            else if (kw.startsWith("MGI:")){
                objs = geneRepository.findDataByMgiId(kw);
            }
            else if (dataType.equals("Gene")) {
                objs = geneRepository.findDataByMarkerSymbol(kw);
            }
            else if (kw.startsWith("ENSMUSG")){
                objs = ensemblGeneIdRepository.findDataByEnsemblGeneId(kw);
            }

            // DiseaseModel ID
            else if ((kw.startsWith("OMIM:") || kw.startsWith("ORPHANET:") || kw.startsWith("DECIPHER:")) && regionId != null){
                System.out.println("search disease " + kw + " and chr " + regionId);
                objs = diseaseModelRepository.findDataByDiseaseIdChr(kw, regionId);
                System.out.println("objs found: "+objs.size());
            }
            else if (kw.startsWith("OMIM:") || kw.startsWith("ORPHANET:") || kw.startsWith("DECIPHER:")){
                System.out.println("search disease");
                objs = diseaseModelRepository.findDataByDiseaseId(kw);
            }
            // DiseaseModel term
            else if (dataType.equals("DiseaseModel") && regionId != null) {
                System.out.println("Disease id normal");
                objs = diseaseModelRepository.findDataByDiseaseTermChr(kw, regionId);
            }
            else if (dataType.equals("DiseaseModel")) {
                System.out.println("Disease term normal");
                objs = diseaseModelRepository.findDataByDiseaseTerm(kw);
            }

            // MP ID
            else if (kw.startsWith("MP:") && regionId != null && childLevel != null) {

                if (childLevel.equals("all")) {
                    System.out.println("MP id with region and ALL children");
                    objs = mpRepository.findAllChildrenMpsByMpIdChr(kw, regionId);
                }
                else {
                    System.out.println("MP id with region and " + childLevel + " children level");
                    int level = Integer.parseInt(childLevel);
                    objs = mpRepository.findChildrenMpsByMpIdChr(kw, regionId, level);
                }
                objs = fetchTerms(objs);
            }
            else if (kw.startsWith("MP:") && regionId != null && childLevel == null) {
                System.out.println("MP id with region");
                objs = mpRepository.findDataByMpIdChr(kw, regionId);
            }
            else if (kw.startsWith("MP:") && regionId == null && childLevel != null) {
                if (childLevel.equals("all")){
                    System.out.println("MP id with ALL children");

                    // first get all children mps (including self)
                    objs = mpRepository.findAllChildrenMpsByMpId(kw);
                    System.out.println("1. Got " + objs.size() + "children");
                }
                else if (childLevel != "0"){
                    System.out.println("MP Id with " + childLevel + " children level");

                    // first get all children mps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = mpRepository.findChildrenMpsByMpId(kw, level);
                    System.out.println("2. Got " + objs.size() + "children");
                }

                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (kw.startsWith("MP:") && regionId == null && childLevel == null){
                System.out.println("MP id normal");
                objs = mpRepository.findDataByMpId(kw);
            }

            // MP term
            else if (dataType.equals("MP") && regionId != null && childLevel != null) {
                System.out.println("MP term with all children and chr filter");
                if (childLevel.equals("all")) {
                    objs = mpRepository.findAllChildrenMpsByMpTermChr(kw, regionId);
                }
                else if (childLevel != "0"){
                    System.out.println("MP term with " + childLevel + " children level and chr filter");

                    // first get all children mps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = mpRepository.findChildrenMpsByMpTermChr(kw, regionId, level);
                }
                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (dataType.equals("MP") && regionId != null && childLevel == null){
                System.out.println("MP term with region");
                objs = mpRepository.findDataByMpTermChr(kw, regionId);
            }
            else if (dataType.equals("Mp") && regionId == null && childLevel != null) {
                if (childLevel.equals("all")){
                    System.out.println("MP term with ALL children");

                    // first get all children mps (including self)
                    objs = mpRepository.findAllChildrenMpsByMpTerm(kw);
                }
                else if (childLevel != "0"){
                    System.out.println("MP term with " + childLevel + " children level");

                    // first get all children mps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = mpRepository.findChildrenMpsByMpTerm(kw, level);
                }

                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (dataType.equals("Mp") && regionId == null && childLevel == null) {
                System.out.println("MP term normal");
                objs = mpRepository.findDataByMpTerm(kw);
            }
            // HP ID
            else if (kw.startsWith("HP:") && regionId != null && childLevel != null) {
                if (childLevel.equals("all")) {
                    System.out.println("HP id with region and ALL children");
                    objs = hpRepository.findAllChildrenHpsByHpIdChr(kw, regionId);
                }
                else {
                    System.out.println("HP id with region and " + childLevel + " children level");
                    int level = Integer.parseInt(childLevel);
                    objs = hpRepository.findChildrenHpsByHpIdChr(kw, regionId, level);
                }

                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (kw.startsWith("HP:") && regionId != null && childLevel == null) {
                System.out.println("HP id with region");
                objs = hpRepository.findDataByHpIdChr(kw, regionId);
            }
            else if (kw.startsWith("HP:") && regionId == null && childLevel != null) {
                if (childLevel.equals("all")){
                    System.out.println("HP id with ALL children");

                    // first get all children hps (including self)
                    objs = hpRepository.findAllChildrenHpsByHpId(kw);
                    System.out.println("1. Got " + objs.size() + "children");
                }
                else if (childLevel != "0"){
                    System.out.println("HP Id with " + childLevel + " children level");

                    // first get all children hps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = hpRepository.findChildrenHpsByHpId(kw, level);

                    System.out.println("2. Got " + objs.size() + "children");
                }

                // then query data by each mp and put together
                objs = fetchTerms(objs);
            }
            else if (kw.startsWith("HP:") && regionId == null && childLevel == null){
                System.out.println("HP id normal");
                objs = hpRepository.findDataByHpId(kw);
            }

            // HP term
            else if (dataType.equals("HP") && regionId != null && childLevel != null) {
                System.out.println("HP term with all children and chr filter");
                if (childLevel.equals("all")) {
                    objs = hpRepository.findAllChildrenHpsByHpTermChr(kw, regionId);
                }
                else if (childLevel != "0"){
                    System.out.println("HP term with " + childLevel + " children level and chr filter");

                    // first get all children hps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = hpRepository.findChildrenHpsByHpTermChr(kw, regionId, level);
                }
                // then query data by each hp and put together
                objs = fetchTerms(objs);
            }
            else if (dataType.equals("HP") && regionId != null && childLevel == null){
                System.out.println("HP term with region");
                objs = hpRepository.findDataByHpTermChr(kw, regionId);
            }
            else if (dataType.equals("Hp") && regionId == null && childLevel != null) {
                if (childLevel.equals("all")){
                    System.out.println("HP term with ALL children");

                    // first get all children hps (including self)
                    objs = hpRepository.findAllChildrenHpsByHpTerm(kw);
                }
                else if (childLevel != "0"){
                    System.out.println("HP term with " + childLevel + " children level");

                    // first get all children hps (including self)
                    int level = Integer.parseInt(childLevel);
                    objs = hpRepository.findChildrenHpsByHpTerm(kw, level);
                }

                // then query data by each hp and put together
                objs = fetchTerms(objs);
            }
            else if (dataType.equals("Hp") && regionId == null && childLevel == null) {
                System.out.println("HP term normal");
                objs = hpRepository.findDataByHpTerm(kw);
            }
            else if (dataType.equals("HumanGeneSymbol")){
                objs = humanGeneSymbolRepository.findDataByHumanGeneSymbol(kw);
            }


            System.out.println("Data objects found: "+ objs.size());

            populateColValMap(objs, colValMap, jDatatypeProperties);

            System.out.println("About to prepare for rows");


            List<String> rowData = new ArrayList<>();
            for (String col : cols){
                if (colValMap.containsKey(col)) {
                    List<String> vals = new ArrayList<>(colValMap.get(col));

                    int valSize = vals.size();

                    if (valSize > 2) {
                        // add showmore
                        vals.add("<button rel=" + valSize + " class='showMore'>show all (" + valSize + ")</button>");
                    }
                    if (valSize == 1) {
                        rowData.add(StringUtils.join(vals, ""));
                    } else {
                        rowData.add("<ul>" + StringUtils.join(vals, "") + "</ul>");
                    }

                    //System.out.println("col: " + col);
                    if (col.equals("ontoSynonym")) {
                        System.out.println(col + " -- " + vals);
                    }
                }
                else {
                    rowData.add(NA);
                }

            }
            j.getJSONArray("aaData").add(rowData);

        }

        System.out.println("rows done");

        j.put("iTotalRecords", rowCount);
        j.put("iTotalDisplayRecords", rowCount);


        return j.toString();
    }

    private List<Object> fetchTerms(List<Object> objs){
        
        List<Object> childTerms = new ArrayList<>();
        for(Object o : objs){
            if (o.getClass().getSimpleName().equals("Mp")) {
                Mp m = (Mp) o;
                childTerms.addAll(mpRepository.findDataByMpId(m.getMpId()));
            }
            else {
                Hp h = (Hp) o;
                childTerms.addAll(hpRepository.findDataByHpId(h.getHpId()));
            }
        }
        return childTerms;
    }

    public void populateColValMapAdvSrch(Map<String, List<String>> node2Properties,  Object obj, Map<String, Set<String>> colValMap, JSONObject jParam, Boolean isExport) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String className = obj.getClass().getSimpleName();

        if (jParam.containsKey(className)) {

            List<String> nodeProperties = node2Properties.get(className);
//            System.out.println("className: " + className);
//            System.out.println("properties:" + nodeProperties);

            if (className.equals("Gene")) {
                Gene g = (Gene) obj;
                getValues(nodeProperties, g, colValMap, isExport);
            }
            else if (className.equals("EnsemblGeneId")) {
                EnsemblGeneId ensg = (EnsemblGeneId) obj;
                getValues(nodeProperties, ensg, colValMap, isExport);
            }
            else if (className.equals("MarkerSynonym")) {
                MarkerSynonym m = (MarkerSynonym) obj;
                getValues(nodeProperties, m, colValMap, isExport);
            }
            else if (className.equals("HumanGeneSymbol")) {
                HumanGeneSymbol hg = (HumanGeneSymbol) obj;
                getValues(nodeProperties, hg, colValMap, isExport);
            }
            else if (className.equals("DiseaseModel")) {
                DiseaseModel dm = (DiseaseModel) obj;
                getValues(nodeProperties, dm, colValMap, isExport);
            }
            else if (className.equals("MouseModel")) {
                MouseModel mm = (MouseModel) obj;
                getValues(nodeProperties, mm, colValMap, isExport);
            }
            else if (className.equals("Allele")) {
                Allele allele = (Allele) obj;
                getValues(nodeProperties, allele, colValMap, isExport);
            }
            else if (className.equals("Mp")) {
                Mp mp = (Mp) obj;
                getValues(nodeProperties, mp, colValMap, isExport);
            }
            else if (className.equals("OntoSynonym")) {
                OntoSynonym ontosyn = (OntoSynonym) obj;
                getValues(nodeProperties, ontosyn, colValMap, isExport);
            }
            else if (className.equals("Hp")) {
                Hp hp = (Hp) obj;
                getValues(nodeProperties, hp, colValMap, isExport);
            }
        }

    }

    public void populateColValMap(List<Object> objs, Map<String, Set<String>> colValMap, JSONObject jDatatypeProperties) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Boolean isExport = false;
        for (Object obj : objs) {
            String className = obj.getClass().getSimpleName();

            if (jDatatypeProperties.containsKey(className)) {

                //System.out.println("className: " + className);

                List<String> nodeProperties = jDatatypeProperties.getJSONArray(className);

                if (className.equals("Gene")) {
                    Gene g = (Gene) obj;
                    getValues(nodeProperties, g, colValMap, isExport);  // convert to class ???
                }
                else if (className.equals("EnsemblGeneId")) {
                    EnsemblGeneId ensg = (EnsemblGeneId) obj;
                    getValues(nodeProperties, ensg, colValMap, isExport);
                }
                else if (className.equals("MarkerSynonym")) {
                    MarkerSynonym m = (MarkerSynonym) obj;
                    getValues(nodeProperties, m, colValMap, isExport);
                }
                else if (className.equals("HumanGeneSymbol")) {
                    HumanGeneSymbol hg = (HumanGeneSymbol) obj;
                    getValues(nodeProperties, hg, colValMap, isExport);
                }
                else if (className.equals("DiseaseModel")) {
                    DiseaseModel dm = (DiseaseModel) obj;
                    getValues(nodeProperties, dm, colValMap, isExport);
                }
                else if (className.equals("MouseModel")) {
                    MouseModel mm = (MouseModel) obj;
                    getValues(nodeProperties, mm, colValMap, isExport);
                }
                else if (className.equals("Allele")) {
                    Allele allele = (Allele) obj;
                    getValues(nodeProperties, allele, colValMap, isExport);
                }
                else if (className.equals("Mp")) {
                    Mp mp = (Mp) obj;
                    getValues(nodeProperties, mp, colValMap, isExport);
                }
                else if (className.equals("OntoSynonym")) {
                    OntoSynonym ontosyn = (OntoSynonym) obj;
                    getValues(nodeProperties, ontosyn, colValMap, isExport);
                }
                else if (className.equals("Hp")) {
                    Hp hp = (Hp) obj;
                    getValues(nodeProperties, hp, colValMap, isExport);
                }
            }
        }
        
    }

    public void getValues(List<String> nodeProperties, Object o, Map<String, Set<String>> colValMap, Boolean isExport) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        int showCutOff = 3;
        ;
        String geneBaseUrl = baseUrl + "/genes/";
        String alleleBaseUrl = baseUrl + "/alleles/"; //MGI:2676828/tm1(mirKO)Wtsi
        String mpBaseUrl = baseUrl + "/phenotypes/";
        String diseaseBaseUrl = baseUrl + "/disease/";
        String ensemblGeneBaseUrl = "http://www.ensembl.org/Mus_musculus/Gene/Summary?db=core;g=";


        for(String property : nodeProperties) {

            if (property.equals("topLevelMpId")){
                property = "topLevelStatus";
            }

            char first = Character.toUpperCase(property.charAt(0));
            String property2 = first + property.substring(1);
            //System.out.println("property: " + property);

            Method method = o.getClass().getMethod("get"+property2);
            if (! colValMap.containsKey(property)) {
                colValMap.put(property, new LinkedHashSet<>());
            }
            String colVal = NA;

            if (method.invoke(o) == null){
                //System.out.println(property + " is null");
            }

            try {
                colVal = method.invoke(o).toString();
                //System.out.println(property + " : " +  colVal);

            } catch(Exception e) {
                //System.out.println(property + " set to " + colVal);
            }


            if (! colVal.isEmpty()) {
                if (property.equals("markerSymbol")){
                    Gene gene = (Gene) o;
                    if (! isExport){
                        String mgiAcc = gene.getMgiAccessionId();
                        colVal = "<a target='_blank' href='" + geneBaseUrl + mgiAcc + "'>" + colVal + "</a>";
                    }
                }
                else if (property.equals("mgiAccessionId")){
                    if (isExport){
                        colVal = hostname + geneBaseUrl + colVal;
                    }
                }
                else if (property.equals("mpTerm")){
                    Mp mp = (Mp) o;
                    if (! isExport) {
                        String mpId = mp.getMpId();
                        colVal = "<a target='_blank' href='" + mpBaseUrl + mpId + "'>" + colVal + "</a>";
                    }
                }
                else if (property.equals("mpId")){
                    if (isExport){
                        colVal = hostname + mpBaseUrl + colVal;
                    }
                }
                else if (property.equals("diseaseTerm")){
                    DiseaseModel dm = (DiseaseModel) o;
                    if (! isExport) {
                        String dt = dm.getDiseaseTerm();
                        colVal = "<a target='_blank' href='" + diseaseBaseUrl + dt + "'>" + colVal + "</a>";
                    }
                }
                else if (property.equals("diseaseId")){
                    if (isExport){
                        colVal = hostname + diseaseBaseUrl + colVal;
                    }
                }
                else if (property.equals("alleleSymbol")){

                    Allele al = (Allele) o;
                    if (! isExport) {
                        colVal = Tools.superscriptify(colVal);
                        String aid = al.getAlleleMgiAccessionId() + "/" + colVal;
                        colVal = "<a target='_blank' href='" + alleleBaseUrl + aid + "'>" + colVal + "</a>";
                    }
                }
                else if (property.equals("alleleMgiAccessionId")){
                    if (isExport){
                        Allele al = (Allele) o;
                        String asym = al.getAlleleSymbol();
                        colVal = hostname + alleleBaseUrl + colVal + "/" + asym;
                    }
                }
                else if (property.equals("ensemblGeneId")){
                    colVal = colVal.replaceAll("\\[", "").replaceAll("\\]","");
                    colVal = "<a target='_blank' href='" + ensemblGeneBaseUrl + colVal + "'>" + colVal + "</a>";
                }
                else if (property.equals("markerSynonym") || property.equals("humanGeneSymbol")){
                    colVal = colVal.replaceAll("\\[", "").replaceAll("\\]","");
                }


                if (isExport){
                    colValMap.get(property).add(colVal);
                }
                else {
                    colValMap.get(property).add("<li>" + colVal + "</li>");
                }

               // System.out.println("colval: "+colValMap);

            }
        }

    }

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
    Integer chrlen(
            @RequestParam(value = "chr", required = true) String chr,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SolrServerException, SQLException {

        //fetchChrLenJson();
        Connection connKomp2 = komp2DataSource.getConnection();

        String sql = "SELECT length FROM seq_region WHERE name ='" + chr + "'";
        Integer len = null;

        try (PreparedStatement p = connKomp2.prepareStatement(sql)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                len = resultSet.getInt("length");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return len;
    }
}
