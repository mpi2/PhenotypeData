package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.neo4j.entity.Allele;
import org.mousephenotype.cda.neo4j.entity.DiseaseModel;
import org.mousephenotype.cda.neo4j.entity.EnsemblGeneId;
import org.mousephenotype.cda.neo4j.entity.Gene;
import org.mousephenotype.cda.neo4j.entity.Hp;
import org.mousephenotype.cda.neo4j.entity.HumanGeneSymbol;
import org.mousephenotype.cda.neo4j.entity.MarkerSynonym;
import org.mousephenotype.cda.neo4j.entity.MouseModel;
import org.mousephenotype.cda.neo4j.entity.Mp;
import org.mousephenotype.cda.neo4j.entity.OntoSynonym;
import org.mousephenotype.cda.neo4j.entity.StatisticalResult;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class AdvancedSearchService {
	
	 @Autowired
	 private Session neo4jSession;
	 
	 @Autowired
	    @Qualifier("autosuggestCore")
	    private SolrClient autosuggestCore;
	
//	@Autowired
	@Qualifier("postqcService")
	PostQcService genotypePhenotypeService;
	
	 private String NA = "not available";
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public AdvancedSearchService(PostQcService genotypePhenotypeService){
		this.genotypePhenotypeService=genotypePhenotypeService;
	}
	
	
	

    public JSONObject fetchGraphDataAdvSrch(String hostname, String baseUrl,JSONObject jParams, Boolean significantPValue, SexType sexType, String impressParameter, Double lowerPvalue, Double upperPvalue, List<String> chromosome,  Integer regionStart, Integer regionEnd, boolean isMouseGenes, List<String> geneList, List<String> genotypeList,  List<String> alleleTypesFilter, List<String> mutationTypesFilter, int phenodigmScoreLow, int phenodigmScoreHigh, String fileType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException, SolrServerException {

//        JSONArray properties = jParams.getJSONArray("properties");
//        System.out.println("columns: " + properties);

        Map<String, String> dtypeMap = new HashMap<>();
        JSONArray dataTypes = jParams.getJSONArray("dataTypes");
        List<String> dts = new ArrayList<>();
        for (int d = 0; d < dataTypes.size(); d++){
            dts.add(dataTypes.get(d).toString());
            dtypeMap.put("a", "nodes.alleles");
            dtypeMap.put("sr", "nodes.srs");
            dtypeMap.put("mp", "nodes.mps");
        }
        String returnDtypes = StringUtils.join(dts, ", ");
        System.out.println("return types: " + returnDtypes);

        //compose cipher based on input parameters here from Java parameters
        String significantCypher= composeSignificanceCypher(significantPValue);
        String phenotypeSexesCypher= composePhenotypeSexCypher(sexType);
        String impressParameterNameCypher= composeImpressParameterCypher(impressParameter);
        String pvaluesCypher = composePvaluesCypher(lowerPvalue, upperPvalue);//looks like we don't have one of these for each phenotype at the moment - need to implement this at some point JW. we need to use the other method with MP ids as param?
        String chrRangeCypher= composeChrRangeCypher(chromosome, regionStart, regionEnd);
        String geneListCypher = composeGeneListCypher(isMouseGenes, geneList);
        String genotypesCypher = composeGenotypesCypher(genotypeList);
        String alleleTypesCypher= composeAlleleTypesCypher(alleleTypesFilter, mutationTypesFilter);

        // disease
        String phenodigmScoreCypher = composePhenodigmScoreStr(phenodigmScoreLow, phenodigmScoreHigh);  // always non-empty
        String diseaseGeneAssociation = composeDiseaseGeneAssociation(jParams);
        String humanDiseaseTerm = composeHumanDiseaseTermStr(jParams);

        Boolean noMpChild = jParams.containsKey("noMpChild") ? true : false;

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

        String regex_aAndb_Orc = "\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*";
        String regex_aAnd_bOrc = "\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*";
        String regex_aOrb_andc = "\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*";
        String regex_aOr_bAndc = "\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*\\(([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)\\s*";
        String regex_aAndb = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";
        String regex_aAndbAndc = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*\\b(AND|and)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";
        String regex_aOrb = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";
        String regex_aOrbOrc = "\\s*\\(*([A-Za-z0-9-\\\\,;:\\s]{1,})\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*\\b(OR|or)\\b\\s*([A-Za-z0-9-\\\\,;:\\s]{1,})\\)*\\s*";

        HashedMap params = new HashedMap();

        Result result = null;

        String sortStr = " ORDER BY g.markerSymbol ";
        String query = null;


        String geneToMpPath = noMpChild ? "MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                : " MATCH (g:Gene)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

        String mpToGenePath = noMpChild ? "MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g:Gene) "
                : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g:Gene) ";

        String geneToDmPathClause = " OPTIONAL MATCH (g)<-[:GENE]-(dm:DiseaseModel)-[:MOUSE_PHENOTYPE]->(dmp:Mp) WHERE "
                + phenodigmScoreCypher + diseaseGeneAssociation + humanDiseaseTerm;


        String pvaluesA = "";
        String pvaluesB = "";
        String pvaluesC = "";
        List<String> narrowMapping = new ArrayList<>();

        long begin = System.currentTimeMillis();

        if (mpStr == null && ! humanDiseaseTerm.isEmpty()){

            String where = noMpChild ? " WHERE mp.mpTerm =~ '.*' " : " WHERE mp0.mpTerm =~ '.*' ";

            query = mpToGenePath + "<-[:GENE]-(dm:DiseaseModel)-[:MOUSE_PHENOTYPE]->(dmp:Mp)"
                + where
                + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + pvaluesCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                + " AND " + phenodigmScoreCypher + diseaseGeneAssociation + humanDiseaseTerm
                + " AND dmp.mpTerm in mp.mpTerm";

            query += fileType != null ?
                   // " RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result = neo4jSession.query(query, params);
        }
        else if (mpStr == null ){

            String where = noMpChild ? " WHERE mp.mpTerm =~ '.*' " : " WHERE mp0.mpTerm =~ '.*' ";

            if (geneList.isEmpty()) {
                query = mpToGenePath
                        + where
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + pvaluesCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, a, mp, sr, "
                        + " extract(x in collect(distinct mp) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }
            else {
                query = geneToMpPath
                        + where
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + pvaluesCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, a, mp, sr, "
                        + " extract(x in collect(distinct mp) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result = neo4jSession.query(query, params);
        }
        else if (! mpStr.contains("AND") && ! mpStr.contains("OR") ) {
            // single mp term

            mpStr = mpStr.trim();
            mpStr = mapNarrowSynonym2MpTerm(narrowMapping, mpStr, autosuggestCore);

            params.put("mpA", mpStr);
            logger.info("A: '{}'", mpStr);


            String whereClause = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpA") + "'" : " WHERE mp0.mpTerm = '" + params.get("mpA") + "'";

            if (geneList.isEmpty()) {
                query = mpToGenePath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + pvaluesCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, a, mp, sr, "
                        + " extract(x in collect(distinct mp) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }
            else {
                query = geneToMpPath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + pvaluesCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, a, mp, sr, "
                        + " extract(x in collect(distinct mp) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aAndb_Orc)) {
            System.out.println("matches (a and b) or c"); // due to join empty list to a non-empty list evals to empty, convert this to (a or c) + (b or c)

            Pattern pattern = Pattern.compile(regex_aAndb_Orc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();;
                String mpB = matcher.group(3).trim();;
                String mpC = matcher.group(5).trim();;
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp0.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + "))";

            String whereClause2 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp0.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + "))";


            String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                    : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

            if (geneList.isEmpty()){

                // collect() will be null if one of the list is empty, to avoid this, use OPTIONAL MATCH instead

                query = mpToGenePath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause1
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "

                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";

            }
            else {
                query = geneToMpPath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause2
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "

                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aOr_bAndc)) {
            System.out.println("matches a or (b and c)"); // due to join empty list to a non-empty list evals to empty, convert this to (b or a) + (c or a)

            Pattern pattern = Pattern.compile(regex_aOr_bAndc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();;
                String mpC = matcher.group(5).trim();;

                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm ='" + params.get("mpA") + "'" + pvaluesA + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp0.mpTerm ='" + params.get("mpA") + "'" + pvaluesA + "))";

            String whereClause2 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC + ") OR (mp.mpTerm ='" + params.get("mpA") + "'" + pvaluesA + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpC") + "'" + pvaluesC + ") OR (mp0.mpTerm ='" + params.get("mpA") + "'" + pvaluesA + "))";


            String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                    : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

            if (geneList.isEmpty()){

                // collect() will be null if one of the list is empty, to avoid this, use OPTIONAL MATCH instead

                query = mpToGenePath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause1
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "

                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";

            }
            else {
                query = geneToMpPath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause2
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "

                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aOrb_andc)) {
            System.out.println("matches (a or b) and c");

            Pattern pattern = Pattern.compile(regex_aOrb_andc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();;
                String mpC = matcher.group(5).trim();;
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp0.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ")) ";

            String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC : " WHERE mp0.mpTerm = '" + params.get("mpC") + "'" + pvaluesC;

            String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                    : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

            if (geneList.isEmpty()){
                query = mpToGenePath
                        //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*')) "
                        + whereClause1
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause1
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "
                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }
            else {
                query = geneToMpPath
                        //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*')) "
                        + whereClause1
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause2
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "
                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr :
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aAnd_bOrc)) {
            System.out.println("matches a and (b or c)");

            Pattern pattern = Pattern.compile(regex_aAnd_bOrc);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();;
                String mpC = matcher.group(5).trim();;
                //logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB + ") OR (mp0.mpTerm ='" + params.get("mpC") + "'" + pvaluesC + ")) ";

            String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA : " WHERE mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA;

            String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                    : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

            if (geneList.isEmpty()){
                query = mpToGenePath
                        //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*')) "
                        + whereClause1
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause1
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "
                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }
            else {
                query = geneToMpPath
                        //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*')) "
                        + whereClause1
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({alleles:a, srs:sr, mps:mp}) as list1 "

                        + matchClause2
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) as list2 "
                        + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 as list "
                        + " UNWIND list as nodes "
                        + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";
            }

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";


            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }

        else if (mpStr.matches(regex_aAndbAndc)) {
            System.out.println("matches a and b and c");

            Pattern pattern = Pattern.compile(regex_aAndbAndc);
            Matcher matcher = pattern.matcher(mpStr);;

            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();
                String mpC = matcher.group(5).trim();
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA : " WHERE mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA;
            String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB : " WHERE mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB;
            String whereClause3 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC : " WHERE mp0.mpTerm = '" + params.get("mpC") + "'" + pvaluesC;

            String mpMatchClause = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            query = mpToGenePath
                    //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                    + whereClause1
                    + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                    + " WITH g, collect({alleles:a, mps:mp, srs:sr}) as list1 "

                    + mpMatchClause
                    //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                    + whereClause2
                    + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                    + " WITH g, list1, collect({alleles:a, mps:mp, srs:sr}) as list2 "

                    + mpMatchClause
                    //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                    + whereClause3
                    + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                    + " WITH g, list1, list2, collect({alleles:a, mps:mp, srs:sr}) as list3 "
                    + " WHERE ALL (x IN list1 WHERE x IN list2) AND ALL (x IN list2 WHERE x IN list3) "
                    + " WITH g, list1+list2+list3 as list "
                    + " UNWIND list as nodes "
                    + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                    + geneToDmPathClause
                    + " AND dmp.mpTerm IN mps";

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aAndb)) {
            System.out.println("matches a and b");

            Pattern pattern = Pattern.compile(regex_aAndb);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                String mpA = matcher.group(1).trim();;
                String mpB = matcher.group(3).trim();;
                logger.info("A: '{}', B: '{}'", mpA, mpB);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
            }

            String whereClause1 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA : " WHERE mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA;
            String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + params.get("mpB") + "'" + pvaluesB : " WHERE mp0.mpTerm = '" + params.get("mpB") + "'" + pvaluesB;

            String mpMatchClause = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                    : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

            query = mpToGenePath
                  //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                  + whereClause1
                  + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                  + " WITH g, collect({alleles:a, mps:mp, srs:sr}) as list1 "

                  + mpMatchClause
                  //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                  + whereClause2
                  + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                  + " WITH g, list1, collect({alleles:a, mps:mp, srs:sr}) as list2 "
                  + " WHERE ALL (x IN list1 WHERE x IN list2) "
                  + " WITH g, list1+list2 as list "
                  + " UNWIND list as nodes "
                  + " WITH g, nodes, extract(x in collect(distinct nodes.mps) | x.mpTerm) as mps "
                  + geneToDmPathClause
                  + " AND dmp.mpTerm IN mps";

            //query += fileType != null ? " RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
              //      : " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            dts = new ArrayList<>();
            for (int d = 0; d < dataTypes.size(); d++){
                String dt = dataTypes.get(d).toString();
                dts.add(dtypeMap.containsKey(dt) ? dt : dtypeMap.get(dt));
            }
            returnDtypes = StringUtils.join(dts, ", ");

            query += fileType != null ?
                    //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                    //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }
        else if (mpStr.matches(regex_aOrbOrc)) {
            System.out.println("matches a or b or c");

            Pattern pattern = Pattern.compile(regex_aOrbOrc);
            Matcher matcher = pattern.matcher(mpStr);
            while (matcher.find()) {
                System.out.println("found: " + matcher.group(0));
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();
                String mpC = matcher.group(5).trim();
                logger.info("A: '{}', B: '{}', C: '{}'", mpA, mpB, mpC);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));
                params.put("mpC", mapNarrowSynonym2MpTerm(narrowMapping, mpC, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
                pvaluesC = composePvalues(params.get("mpC").toString(), jParams);
            }

            if (geneList.isEmpty()){
                query = mpToGenePath;
            }
            else {
                query = geneToMpPath;
            }

            String whereClause = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC + ") ) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp0.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ") OR (mp.mpTerm = '" + params.get("mpC") + "'" + pvaluesC + ")) ";

            System.out.println("A:  "+ pvaluesA);
            System.out.println("B:  "+ pvaluesB);
            System.out.println("whereClause: " + whereClause);

            // using regular expression to match mp term name drastically lowers the performance, use exact match instead
            query +=
                  //  " WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*')) "
                  whereClause
                + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                + " WITH g, a, sr, mp, extract(x in collect(distinct mp) | x.mpTerm) as mps "
                + geneToDmPathClause
                + " AND dmp.mpTerm IN mps";

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);

        }
        else if (mpStr.matches(regex_aOrb)) {
            System.out.println("matches a or b");

            Pattern pattern = Pattern.compile(regex_aOrb);
            Matcher matcher = pattern.matcher(mpStr);

            while (matcher.find()) {
                String mpA = matcher.group(1).trim();
                String mpB = matcher.group(3).trim();
                logger.info("A: '{}', B: '{}'", mpA, mpB);

                params.put("mpA", mapNarrowSynonym2MpTerm(narrowMapping, mpA, autosuggestCore));
                params.put("mpB", mapNarrowSynonym2MpTerm(narrowMapping, mpB, autosuggestCore));

                pvaluesA = composePvalues(params.get("mpA").toString(), jParams);
                pvaluesB = composePvalues(params.get("mpB").toString(), jParams);
            }

            if (geneList.isEmpty()){
                query = mpToGenePath;
            }
            else {
                query = geneToMpPath;
            }

            String whereClause = noMpChild ? " WHERE ((mp.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + ")) "
                    : " WHERE ((mp0.mpTerm = '" + params.get("mpA") + "'" + pvaluesA + ") OR (mp0.mpTerm ='" + params.get("mpB") + "'" + pvaluesB + "))";
            query +=
                  //  " WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*')) "
                  whereClause
                + " AND " + significantCypher + phenotypeSexesCypher + impressParameterNameCypher + chrRangeCypher + geneList + genotypesCypher + alleleTypesCypher
                + " WITH g, a, sr, mp, extract(x in collect(distinct mp) | x.mpTerm) as mps "
                + geneToDmPathClause
                + " AND dmp.mpTerm IN mps";

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

            System.out.println("Query: "+ query);
            result =  neo4jSession.query(query, params);
        }

        long end = System.currentTimeMillis();
        System.out.println("Done with query in " + (end - begin) + " ms");

        int rowCount = 0;
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);
        j.put("iDisplayStart", 0);
        j.put("iDisplayLength", 10);
        j.put("narrowMapping", StringUtils.join(narrowMapping, ", "));

        List<String> rowDataExport = new ArrayList<>(); // for export
        List<String> rowDataOverview = new ArrayList<>(); // for overview
        List<String> dtypes = Arrays.asList("Allele", "Gene", "Mp", "DiseaseModel", "StatisticalResult");

        long tstart = System.currentTimeMillis();
        if (fileType != null){

            List<String> cols = new ArrayList<>();
            Map<String, List<String>> node2Properties = new LinkedHashMap<>();

            for (String dtype : dtypes){

                node2Properties.put(dtype, new ArrayList<String>());

                if (jParams.containsKey(dtype)) {
                    for (Object obj : jParams.getJSONArray(dtype)) {
                        String colName = obj.toString();

                        //System.out.println("colname: " + colName);
                        if (colName.equals("alleleSymbol") && !jParams.getJSONArray(dtype).contains("alleleMgiAccessionId")) {
                            cols.add(colName);
                            cols.add("alleleMgiAccessionId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("alleleMgiAccessionId");
                        } else if (colName.equals("markerSymbol") && !jParams.getJSONArray(dtype).contains("mgiAccessionId")) {
                            cols.add(colName);
                            cols.add("mgiAccessionId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("mgiAccessionId");
                        } else if (colName.equals("mpTerm") && !jParams.getJSONArray(dtype).contains("mpId")) {
                            cols.add(colName);
                            cols.add("mpId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("mpId");
                        } else if (colName.equals("diseaseTerm") && !jParams.getJSONArray(dtype).contains("diseaseId")) {
                            cols.add(colName);
                            cols.add("diseaseId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("diseaseId");
                        } else {
                            cols.add(colName);
                            node2Properties.get(dtype).add(colName);
                        }
                    }
                }
            }

            rowDataExport.add(StringUtils.join(cols, "\t")); // column

            for (Map<String,Object> row : result) {
                //System.out.println(row.toString());
                //System.out.println("cols: " + row.size());

                List<String> data = new ArrayList<>(); // for export

                Map<String, Set<String>> colValMap = new TreeMap();

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    //System.out.println(entry.getKey() + " / " + entry.getValue());

                    if (entry.getValue() != null) {

                        if (entry.getKey().startsWith("collect")) {
                            List<Object> objs = (List<Object>) entry.getValue();
                            for (Object obj : objs) {
                                populateColValMapAdvSrch(hostname, baseUrl,node2Properties, obj, colValMap, jParams, fileType);
                            }
                        } else {
                            Object obj = entry.getValue();
                            populateColValMapAdvSrch(hostname, baseUrl,node2Properties, obj, colValMap, jParams, fileType);
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
                            //System.out.println("vals: "+ vals);

                            if (fileType.equals("html")){
                                data.add("<td>" + StringUtils.join(vals, "|") + "</td>");
                            }
                            else {
                                data.add(StringUtils.join(vals, "|"));
                            }

                        }
                    }
                    //System.out.println("row: " + data);
                }

                if (fileType.equals("html")){
                    rowDataExport.add("<tr>" + StringUtils.join(data, "") + "<tr>");
                }
                else {
                    rowDataExport.add(StringUtils.join(data, "\t"));
                }
            }
            j.put("rows", rowDataExport);
        }
        else {

            // overview

            List<String> cols = new ArrayList<>();
            Map<String, List<String>> node2Properties = new LinkedHashMap<>();

            for (String dtype : dtypes) {

                node2Properties.put(dtype, new ArrayList<String>());

                if (jParams.containsKey(dtype)) {
                    for (Object obj : jParams.getJSONArray(dtype)) {
                        String colName = obj.toString();
                        cols.add(colName);
                        node2Properties.get(dtype).add(colName);
                    }
                }
            }

            //System.out.println("columns: " + cols);
            Map<String, Set<String>> colValMap = new TreeMap<>(); // for export

            for (Map<String, Object> row : result) {
                //System.out.println(row.toString());
                //System.out.println("cols: " + row.size());

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    //System.out.println(entry.getKey() + " / " + entry.getValue());

                    if (entry.getValue() != null && ! entry.getValue().toString().startsWith("[Ljava.lang.Object")) {
                        if (entry.getKey().startsWith("collect")) {
                            List<Object> objs = (List<Object>) entry.getValue();
                            for (Object obj : objs) {
                                populateColValMapAdvSrch(hostname, baseUrl,node2Properties, obj, colValMap, jParams, fileType);
                            }
                        } else {
                            Object obj = entry.getValue();
                            populateColValMapAdvSrch(hostname, baseUrl,node2Properties, obj, colValMap, jParams, fileType);
                        }
                    }
                }
            }

            //System.out.println("keys: "+ colValMap.keySet());
            // for overview

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
        long tend = System.currentTimeMillis();

        System.out.println((tend - tstart) + " ms taken");
        return j;
    }

    
    public String mapNarrowSynonym2MpTerm(List<String> narrowMapping, String mpTerm, SolrClient autosuggestCore) throws IOException, SolrServerException {

        String mpStr = null;
        SolrQuery query = new SolrQuery();
        query.setQuery("\"" + mpTerm + "\"");
        query.set("qf", "auto_suggest");
        query.set("defType", "edismax");
        query.setStart(0);
        query.setRows(100);
        query.setFilterQueries("docType:mp");

        QueryResponse response = autosuggestCore.query(query);
        System.out.println("response: " + response);
        SolrDocumentList results = response.getResults();

        for(SolrDocument doc : results){
            if (doc.containsKey("mp_term") && doc.getFieldValue("mp_term").equals(mpTerm)){
                System.out.println(doc.getFieldValue("mp_term"));
                mpStr = mpTerm;
                break;
            }
            else if (doc.containsKey("mp_narrow_synonym") && doc.getFieldValue("mp_narrow_synonym").equals(mpTerm)){
                System.out.println("NS: "+ doc.getFieldValue("mp_narrow_synonym"));
                mpStr = doc.getFieldValue("mp_term").toString();
                narrowMapping.add(mpTerm + " is not directly annotated in IMPC and is a child term of " + mpStr);
                break;
            }
        }

        return mpStr;
    }
    
	private String composeImpressParameterCypher(String impressParameter) {

		String parameter = " AND sr.parameterName ='" + impressParameter + "' ";

		return parameter;
	}

    private String composePvaluesCypher(Double lower, Double upper){
        String pvalues = "";

        if (lower!=null) {
            
            pvalues = " AND sr.pvalue > " + lower + " ";
        }
        if (upper!=null) {
          
            pvalues += " AND sr.pvalue < " + upper + " ";
        }

        return pvalues;
    }

    private String composePvalues(String mpTerm, JSONObject jParams) {
        String pvalues = "";

        List<String> pvals = new ArrayList<>();
        if (jParams.containsKey("pvaluesMap")) {
            JSONObject map = jParams.getJSONObject("pvaluesMap").getJSONObject(mpTerm);
            System.out.println("MAP: " + map.toString());
            if (map.containsKey("lowerPvalue")){
                double lowerPvalue = map.getDouble("lowerPvalue");
                pvals.add("sr.pvalue > " + lowerPvalue);
            }
            if (map.containsKey("upperPvalue")){
                double upperPvalue = map.getDouble("upperPvalue");
                pvals.add("sr.pvalue < " + upperPvalue);
            }
        }

        if (pvals.size() > 0){
            pvalues = " AND (" +  StringUtils.join(pvals, " AND ") + ")";
        }
        System.out.println("----- pvalue: " + pvalues);
        return pvalues;
    }

    private String composeSignificanceCypher(boolean significantPValue) {
        
           String significantPvalueCipher = significantPValue == true ? " sr.significant = true " : " exists(sr.significant) ";
        
        return significantPvalueCipher;
    }

    private String composePhenotypeSexCypher(SexType sexType){

        String phenotypeSex = "";
        
            if (sexType.equals(SexType.female)){
                phenotypeSex = " AND none (tag IN sr.phenotypeSex WHERE tag IN ['male','both']) ";
            }
            else if (sexType.equals(SexType.male)){
                phenotypeSex = " AND none (tag IN sr.phenotypeSex WHERE tag IN ['female','both']) ";
            }
            else if (sexType.equals(SexType.both)) {
                phenotypeSex = " AND ('both' IN sr.phenotypeSex) ";
            }
        
        return phenotypeSex;
    }

    private String composeChrRangeCypher(List<String> chromosomes, Integer regionStart, Integer regionEnd){
        String chrRange = "";
//@TODO this needs refactoring to make sure it works and with multiple phenotypes.
        if (!chromosomes.isEmpty() && regionStart!=null && regionEnd!=null) 
        {
            String range = "";
           
                
//                    String[] ids = org.apache.commons.lang3.StringUtils.split(regionId, ",");
//                    List<String> chrs = new ArrayList<>();
//                    for (int i=0; i<ids.length; i++){
//                        chrs.add("'" + ids[i] + "'");
//                    }
                    //regionId = org.apache.commons.lang3.StringUtils.join(chrs, ",");

                    chrRange = " AND g.chrId IN [" + chromosomes.get(0) + "] AND g.chrStart >= " + regionStart + " AND g.chrEnd <= " + regionEnd + " ";
         }
        else if (chromosomes.size()>0 && chromosomes.size()<2) {// is this for the case where chromosome has been specified but region hasn't??
                chrRange = " AND g.chrId IN " + chromosomes.get(0) + " ";
        }

        return chrRange;
    }

	private String composeGeneListCypher(boolean isMouseGenes, List<String> genes) {
		String genelist = "";

		List<String> list = new ArrayList<>();

		for (String name : genes) {
			list.add("'" + name.toString() + "'");
		}

		String glist = StringUtils.join(list, ",");

		if (isMouseGenes) {
			// genelist = "AND g.markerSymbol in [" +

			boolean isSym = glist.contains("MGI:") ? false : true; 
			if (isSym) {
				genelist = " AND g.markerSymbol in [" + glist + "] ";
			} else {
				genelist = " AND g.mgiAccessionId in [" + glist + "] ";
			}
		} else if (!isMouseGenes) {// must be human at the moment until we get
									// more than 2 choices

			if (list.size() > 0) {
				genelist = " AND ANY (hs in g.humanGeneSymbol WHERE hs IN [" + StringUtils.join(list, ", ") + "]) ";
			}
		}

		return genelist;
	}

    private String composeGenotypesCypher(List<String> genotypeList) {
        String genotypes = "";
        List<String> list=new ArrayList<>();
        if (!genotypeList.isEmpty()) {
            for (String name : genotypeList) {
                list.add("'" + name + "'");
            }
            if (list.size() > 0) {
                genotypes = " AND sr.zygosity IN [" + StringUtils.join(list, ",") + "]";
            }
        }
        return genotypes;
    }

	private String composeAlleleTypesCypher(List<String> alleleTypesFilters, List<String> mutantTypesFilters) {
		String alleleTypes = "";

		Map<String, String> alleleTypeMapping = new HashMap<>();
		alleleTypeMapping.put("CRISPR(em)", "Endonuclease-mediated"); // mutation_type
		alleleTypeMapping.put("KOMP", ""); // empty allele_type
		alleleTypeMapping.put("KOMP.1", ".1");
		alleleTypeMapping.put("EUCOMM A", "a");
		alleleTypeMapping.put("EUCOMM B", "b");
		alleleTypeMapping.put("EUCOMM C", "c");
		alleleTypeMapping.put("EUCOMM D", "d");
		alleleTypeMapping.put("EUCOMM E", "e");

		List<String> mutationTypes = new ArrayList<>();

		if (!alleleTypes.isEmpty()) {
			List<String> list = new ArrayList<>();
			for (String alleleType : alleleTypesFilters) {
				String atype = alleleType;
				list.add("'" + alleleTypeMapping.get(atype) + "'");
			}

			for (String mutantType : mutantTypesFilters) {
				mutationTypes.add("'" + alleleTypeMapping.get(mutantType) + "'");
			}

			if (list.size() > 0) {
				alleleTypes = "a.alleleType IN [" + StringUtils.join(list, ",") + "]";
				if (mutationTypes.size() > 0) {
					alleleTypes += " OR a.mutationType IN [" + StringUtils.join(mutationTypes, ",") + "]";
				}
				alleleTypes = " AND (" + alleleTypes + ") ";
			} else {
				if (mutationTypes.size() > 0) {
					alleleTypes += "a.mutationType IN [" + StringUtils.join(mutationTypes, ",") + "]";
				}
				alleleTypes = " AND (" + alleleTypes + ") ";
			}
		}
		return alleleTypes;
	}

    private String composePhenodigmScoreStr(int low, int high){
        String phenodigmScore = "";

        
            phenodigmScore = " dm.diseaseToModelScore >= " + low + " AND dm.diseaseToModelScore <= " + high + " ";
        
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
    
    
    public void populateColValMap(String hostname, String baseUrl, List<Object> objs, Map<String, Set<String>> colValMap, JSONObject jDatatypeProperties) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String fileType = null;
        for (Object obj : objs) {
            String className = obj.getClass().getSimpleName();

            if (jDatatypeProperties.containsKey(className)) {

                //System.out.println("className: " + className);

                List<String> nodeProperties = jDatatypeProperties.getJSONArray(className);

                if (className.equals("Gene")) {
                    Gene g = (Gene) obj;
                    getValues(hostname, baseUrl, nodeProperties, g, colValMap, fileType, jDatatypeProperties);  // convert to class ???
                }
                else if (className.equals("EnsemblGeneId")) {
                    EnsemblGeneId ensg = (EnsemblGeneId) obj;
                    getValues(hostname, baseUrl,nodeProperties, ensg, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("MarkerSynonym")) {
                    MarkerSynonym m = (MarkerSynonym) obj;
                    getValues(hostname, baseUrl,nodeProperties, m, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("HumanGeneSymbol")) {
                    HumanGeneSymbol hg = (HumanGeneSymbol) obj;
                    getValues(hostname, baseUrl,nodeProperties, hg, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("DiseaseModel")) {
                    DiseaseModel dm = (DiseaseModel) obj;
                    getValues(hostname, baseUrl,nodeProperties, dm, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("MouseModel")) {
                    MouseModel mm = (MouseModel) obj;
                    getValues(hostname, baseUrl,nodeProperties, mm, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("Allele")) {
                    Allele allele = (Allele) obj;
                    getValues(hostname, baseUrl,nodeProperties, allele, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("Mp")) {
                    Mp mp = (Mp) obj;
                    getValues(hostname, baseUrl,nodeProperties, mp, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("OntoSynonym")) {
                    OntoSynonym ontosyn = (OntoSynonym) obj;
                    getValues(hostname, baseUrl,nodeProperties, ontosyn, colValMap, fileType, jDatatypeProperties);
                }
                else if (className.equals("Hp")) {
                    Hp hp = (Hp) obj;
                    getValues(hostname, baseUrl,nodeProperties, hp, colValMap, fileType, jDatatypeProperties);
                }
            }
        }
        
    }
    
    public Map<String, Set<String>> getValues(String hostname, String baseUrl, List<String> nodeProperties, Object o, Map<String, Set<String>> colValMap, String fileType, JSONObject jParam) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        int showCutOff = 3;

        if (hostname == null) {
            hostname = jParam.getString("hostname").toString();
        }
        if ( ! hostname.startsWith("http")){
            hostname = "http:" + hostname;
        }
        if  (baseUrl == null) {
            baseUrl = jParam.getString("baseUrl").toString();
        }

        //System.out.println("TEST hostname: " + hostname);

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
            //System.out.println("property method: " + property2);

            Method method = o.getClass().getMethod("get"+property2);
            if (! colValMap.containsKey(property)) {
                colValMap.put(property, new LinkedHashSet<>());
            }
            String colVal = NA;

//            if (method.invoke(o) == null){
//                System.out.println(property + " is null");
//            }

            try {
                colVal = method.invoke(o).toString();
                //System.out.println(property + " : " +  colVal);

            } catch(Exception e) {
               // System.out.println(property + " set to " + colVal);
            }


            if (! colVal.isEmpty()) {
                if (property.equals("markerSymbol")){
                    Gene gene = (Gene) o;
                    if (fileType == null || fileType.equals("html")){
                        String mgiAcc = gene.getMgiAccessionId();
                        colVal = "<a target='_blank' href='" + hostname + geneBaseUrl + mgiAcc + "'>" + colVal + "</a>";
                    }
                }
                else if (property.equals("mgiAccessionId")){
                    if (fileType != null  && ! fileType.equals("html")){
                        colVal = hostname + geneBaseUrl + colVal;
                    }
                }
                else if (property.equals("mpTerm")){
                    Mp mp = (Mp) o;
                    if (fileType == null || fileType.equals("html")) {
                        String mpId = mp.getMpId();
                        colVal = "<a target='_blank' href='" + hostname + mpBaseUrl + mpId + "'>" + colVal + "</a>";
                    }
                }
//                else if (property.equals("mpId")){
//                    if (isExport){
//                        colVal = hostname + mpBaseUrl + colVal;
//                    }
//                }
                else if (property.equals("diseaseTerm")){
                    DiseaseModel dm = (DiseaseModel) o;

                    if (fileType == null || fileType.equals("html")) {
                        String dId = dm.getDiseaseId();
                        colVal = "<a target='_blank' href='" + hostname + diseaseBaseUrl + dId + "'>" + colVal + "</a>";
                    }
                }
                // multiple http links won't work in excel cell
//                else if (property.equals("diseaseId")){
//                    if (isExport){
//                        colVal = hostname + diseaseBaseUrl + colVal;
//                    }
//                }
                else if (property.equals("alleleSymbol")){

                    Allele al = (Allele) o;
                    if (fileType == null || fileType.equals("html")) {

                        int index = colVal.indexOf('<');
                        String wantedSymbol = colVal.replace(colVal.substring(0, index+1), "").replace(">","");
                        colVal = Tools.superscriptify(colVal);
                        String aid = al.getMgiAccessionId() + "/" + wantedSymbol;
                        colVal = "<a target='_blank' href='" + hostname + alleleBaseUrl + aid + "'>" + colVal + "</a>";
                    }
                }
                else if (property.equals("alleleMgiAccessionId")){

                    if (fileType != null && ! fileType.equals("html")){
                        Allele al = (Allele) o;
                        String asym = al.getAlleleSymbol();
                        int index = asym.indexOf('<');
                        String wantedSymbol = asym.replace(asym.substring(0, index+1), "").replace(">","");
                        String aid = al.getMgiAccessionId() + "/" + wantedSymbol;

                       // System.out.println("asym: " + asym + " wanted: " + wantedSymbol);
                        colVal = hostname + alleleBaseUrl + aid;
                    }
                }
                else if (property.equals("ensemblGeneId")){
                    colVal = colVal.replaceAll("\\[", "").replaceAll("\\]","");
                    colVal = "<a target='_blank' href='" + ensemblGeneBaseUrl + colVal + "'>" + colVal + "</a>";
                }
                else if (property.equals("markerSynonym") || property.equals("humanGeneSymbol")){
                    colVal = colVal.replaceAll("\\[", "").replaceAll("\\]","");
                }
                else if (property.equals("parameterName")){
                    StatisticalResult sr = (StatisticalResult) o;
                    String procedureName = sr.getProcedureName();

                    if (fileType == null || fileType.equals("html")){
                        colVal = "<b>(" + procedureName + ")</b> " + colVal;
                    }
                    else {
                        colVal = "(" + procedureName + ") " + colVal;
                    }
                }
                else if (property.equals("pvalue")){
                    if (fileType == null || fileType.equals("html")) {
                        colVal = "<span class='pv'>" + colVal + "</span>";
                    }
                }


                if (property.equals("diseaseClasses")) {

                    List<String> dcls = Arrays.asList(StringUtils.split(colVal, ","));
                    List<String> vals = new ArrayList<>();

                    if (fileType == null){
                        for (String dcl : dcls) {
                            vals.add("<li>" + dcl + "</li>");
                        }
                        colValMap.get(property).addAll(vals);
                    }
                    else {
                        for (String dcl : dcls) {
                            vals.add(dcl);
                        }
                        colValMap.get(property).add(colVal);
                    }
                }
                else if (fileType == null || fileType.equals("html")){
                    colValMap.get(property).add("<li>" + colVal + "</li>");
                }
                else {
                    colValMap.get(property).add(colVal);
                }

               // System.out.println("colval: "+colValMap);

            }
        }
        return colValMap;
    }
    
    public void populateColValMapAdvSrch(String hostname, String baseUrl, Map<String, List<String>> node2Properties,  Object obj, Map<String, Set<String>> colValMap, JSONObject jParam, String fileType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String className = obj.getClass().getSimpleName();

        if (jParam.containsKey(className)) {

            List<String> nodeProperties = node2Properties.get(className);
//            System.out.println("className: " + className);
//            System.out.println("properties:" + nodeProperties);

            if (className.equals("Gene")) {
                Gene g = (Gene) obj;
                getValues(hostname, baseUrl,nodeProperties, g, colValMap, fileType, jParam);
            }
            else if (className.equals("EnsemblGeneId")) {
                EnsemblGeneId ensg = (EnsemblGeneId) obj;
                getValues(hostname, baseUrl,nodeProperties, ensg, colValMap, fileType, jParam);
            }
            else if (className.equals("MarkerSynonym")) {
                MarkerSynonym m = (MarkerSynonym) obj;
                getValues(hostname, baseUrl,nodeProperties, m, colValMap, fileType, jParam);
            }
            else if (className.equals("HumanGeneSymbol")) {
                HumanGeneSymbol hg = (HumanGeneSymbol) obj;
                getValues(hostname, baseUrl,nodeProperties, hg, colValMap, fileType, jParam);
            }
            else if (className.equals("DiseaseModel")) {
                DiseaseModel dm = (DiseaseModel) obj;
                getValues(hostname, baseUrl,nodeProperties, dm, colValMap, fileType, jParam);
            }
            else if (className.equals("MouseModel")) {
                MouseModel mm = (MouseModel) obj;
                getValues(hostname, baseUrl,nodeProperties, mm, colValMap, fileType, jParam);
            }
            else if (className.equals("Allele")) {
                Allele allele = (Allele) obj;
                getValues(hostname, baseUrl,nodeProperties, allele, colValMap, fileType, jParam);
            }
            else if (className.equals("Mp")) {
                Mp mp = (Mp) obj;
                getValues(hostname, baseUrl,nodeProperties, mp, colValMap, fileType, jParam);
            }
            else if (className.equals("StatisticalResult")) {
                StatisticalResult sr = (StatisticalResult) obj;
                getValues(hostname, baseUrl,nodeProperties, sr, colValMap, fileType, jParam);
            }
            else if (className.equals("OntoSynonym")) {
                OntoSynonym ontosyn = (OntoSynonym) obj;
                getValues(hostname, baseUrl,nodeProperties, ontosyn, colValMap, fileType, jParam);
            }
            else if (className.equals("Hp")) {
                Hp hp = (Hp) obj;
                getValues(hostname, baseUrl,nodeProperties, hp, colValMap, fileType, jParam);
            }
        }

    }

	
	

	
	//get the number of genes associated with phenotypes - the same way as we do for the phenotypes pages?
	 public List<String> getGenesForPhenotype(String phenotypeId) throws IOException, URISyntaxException, SolrServerException{
		List<String> geneSymbols=genotypePhenotypeService.getGenesForMpId(phenotypeId);
		return geneSymbols;
	 }
	 
	 public List<String> getGenesForPhenotypeAndPhenotype(String phenotypeId, String phenotypeId2) throws IOException, URISyntaxException, SolrServerException{
		 List<String> geneSymbols=genotypePhenotypeService.getGenesForMpId(phenotypeId);
		 System.out.println(geneSymbols.size());
		 List<String> geneSymbols2=genotypePhenotypeService.getGenesForMpId(phenotypeId2);
		 @SuppressWarnings("unchecked")
		List<String> list=(List<String>) CollectionUtils.intersection(geneSymbols, geneSymbols2);
		 System.out.println(geneSymbols2.size());
		return list;
	 }
	 
	 public List<String> getGenesForPhenotypeORPhenotype(String phenotypeId, String phenotypeId2) throws IOException, URISyntaxException, SolrServerException{
		 List<String> geneSymbols=genotypePhenotypeService.getGenesForMpId(phenotypeId);
		 System.out.println(geneSymbols.size());
		 List<String> geneSymbols2=genotypePhenotypeService.getGenesForMpId(phenotypeId2);
		 System.out.println(geneSymbols2.size());
		 @SuppressWarnings("unchecked")
		List<String> list=(List<String>) CollectionUtils.union(geneSymbols, geneSymbols2);
		 
		return list;
	 }
}
