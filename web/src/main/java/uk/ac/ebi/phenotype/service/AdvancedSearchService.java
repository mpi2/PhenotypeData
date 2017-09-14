package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
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

import net.sf.json.JSONObject;

import javax.swing.text.html.HTMLDocument;

@Service
public class AdvancedSearchService {

    @Autowired
    private Session neo4jSession;

    //@Autowired
    @Qualifier("autosuggestCore")
    private SolrClient autosuggestCore;

    //	@Autowired
    @Qualifier("postqcService")
    PostQcService genotypePhenotypeService;

    private String NA = "not available";


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AdvancedSearchService(PostQcService genotypePhenotypeService, SolrClient autosuggestCore, Session neo4jSession){
        this.genotypePhenotypeService=genotypePhenotypeService;
        this.autosuggestCore=autosuggestCore;
        this.neo4jSession=neo4jSession;
    }

    private String composeSignificance(AdvancedSearchPhenotypeForm mpForm) {
        return mpForm.getSignificantPvaluesOnly() ? " sr.significant = true " : " exists(sr.significant) ";
    }
    private String composeParameter(AdvancedSearchPhenotypeForm mpForm){
        String parameter = "";
        if (mpForm.getImpressParameterName() != null) {
            parameter = " AND sr.parameterName ='" + mpForm.getImpressParameterName() + "' ";
        }
        return parameter;
    }
    private String composePvalues(AdvancedSearchPhenotypeForm mpForm, String mpTerm){
        String pvalues = "";
        List<String> pvals = new ArrayList<>();
        Map<String, String> mpPvalue = new HashMap<>();

        for(AdvancedSearchMpRow row : mpForm.getPhenotypeRows()) {
            if (row.getLowerPvalue() != null) {
                pvals.add("sr.pvalue > " + row.getLowerPvalue());
            }
            if (row.getUpperPvalue() != null) {
                pvals.add("sr.pvalue < " + row.getUpperPvalue());
            }

            if (pvals.size() > 0) {
                pvalues = " AND (" + StringUtils.join(pvals, " AND ") + ")";
            }

            mpPvalue.put(row.getPhenotypeTerm(), pvalues);
        }

        if (mpPvalue.size() > 0) {
            pvalues = mpPvalue.get(mpTerm);
        }

        if (pvalues == null){
            pvalues = "";
        }
        return pvalues;
    }
    private String composeChrRangeStr(AdvancedSearchGeneForm geneForm){
        String chrRange = "";

        if (geneForm.getChrId() != null && geneForm.getChrStart() != null && geneForm.getChrEnd() != null) {
            chrRange = " AND g.chrId IN ['" + geneForm.getChrId() + "'] AND g.chrStart >= " + geneForm.getChrStart() + " AND g.chrEnd <= " + geneForm.getChrEnd() + " ";
        }
        else if (geneForm.getChrIds() != null){
            List<String> chrIds = new ArrayList<>();
            for(String id : geneForm.getChrIds() ){
                chrIds.add("'" + id + "'");
            }
            chrRange = " AND g.chrId IN [" + StringUtils.join(chrIds, ",")  + "] ";
        }

        return chrRange;
    }

    private String composeGeneListStr(AdvancedSearchGeneForm geneForm) {
        String genelist = "";

        if (geneForm.getMouseMarkerSymbols().size() > 0) {
            // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();

            for (String symbol : geneForm.getMouseMarkerSymbols()) {
                list.add("'" + symbol + "'");
            }
            String glist = StringUtils.join(list, ",");
            genelist = " AND g.markerSymbol in [" + glist + "] ";
        }
        else if (geneForm.getMouseMgiGeneIds().size() > 0) {
            // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();

            for (String id : geneForm.getMouseMgiGeneIds()) {
                list.add("'" + id + "'");
            }
            String glist = StringUtils.join(list, ",");
            genelist = " AND g.mgiAccessionId in [" + glist + "] ";
        }
        else if (geneForm.getHumanMarkerSymbols().size() > 0){
            List<String> list = new ArrayList<>();

            for (String hsymbol : geneForm.getHumanMarkerSymbols()) {
                list.add("'" + hsymbol + "'");
            }
            genelist = " AND ANY (hs in g.humanGeneSymbol WHERE hs IN [" + StringUtils.join(list, ", ") + "]) ";
        }

        return genelist;
    }

    private String composeGenotypesStr(AdvancedSearchGeneForm geneForm) {
        String genotypes = "";

        if (geneForm.getGenotypes().size() > 0) {
            // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();
            for (String gt : geneForm.getGenotypes()) {
                list.add("'" + gt + "'");
            }

            genotypes = " AND sr.zygosity IN [" + StringUtils.join(list, ",") + "]";
        }

        return genotypes;
    }

    private String composeAlleleTypesStr(AdvancedSearchGeneForm geneForm){
        String alleleTypes = "";

        Map<String, String> alleleTypeMapping = new HashMap<>();
        alleleTypeMapping.put("CRISPR(em)", "Endonuclease-mediated"); // mutation_type
        alleleTypeMapping.put("KOMP", "");  // empty allele_type
        alleleTypeMapping.put("KOMP.1", ".1");
        alleleTypeMapping.put("EUCOMM A", "a");
        alleleTypeMapping.put("EUCOMM B", "b");
        alleleTypeMapping.put("EUCOMM C", "c");
        alleleTypeMapping.put("EUCOMM D", "d");
        alleleTypeMapping.put("EUCOMM E", "e");

        List<String> mutationTypes = new ArrayList<>();

        if (geneForm.getAlleleTypes().size() > 0) {
            // genelist = "AND g.markerSymbol in [" +
            List<String> list = new ArrayList<>();
            for (String atype : geneForm.getAlleleTypes()) {
                if (atype.equals("CRISPR(em)")){
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

    private String composePhenodigmScoreStr(AdvancedSearchDiseaseForm diseaseForm){
        String phenodigmScore = "";

        if (! diseaseForm.getPhenodigmLowerScore().equals("")){
            return " dm.diseaseToModelScore >= " + diseaseForm.getPhenodigmLowerScore() + " AND dm.diseaseToModelScore <= " + diseaseForm.getPhenodigmUpperScore() + " ";
        }
        return phenodigmScore;
    }

    private String composeDiseaseGeneAssociation(AdvancedSearchDiseaseForm diseaseForm){
        String diseaeGeneAssoc = "";

        if (diseaseForm.getHumanCurated() != null && diseaseForm.getHumanCurated() == true) {
            diseaeGeneAssoc = " AND dm.humanCurated = true ";
        }
        else if (diseaseForm.getHumanCurated() != null && diseaseForm.getHumanCurated() == false){
            diseaeGeneAssoc = " AND dm.humanCurated = false ";
        }
        return diseaeGeneAssoc;
    }
    private String composeHumanDiseaseTermStr(AdvancedSearchDiseaseForm diseaseForm){
        String humanDiseaseTerm = "";

        if (diseaseForm.getHumanDiseaseTerm() != null) {
            humanDiseaseTerm = " AND dm.diseaseTerm = '" + diseaseForm.getHumanDiseaseTerm() + "' ";
        }
        return humanDiseaseTerm;
    }
    private String fetchReturnTypes(List<String> dataTypes, Map<String, String> dtypeMap, AdvancedSearchPhenotypeForm mpForm) {
        List<String> dts = new ArrayList<>();

        String returnTypes = null;


        if (mpForm.getPhenotypeRows().size() > 1) {
            for (String dt : dataTypes) {
                dts.add(dtypeMap.containsKey(dt) ? dtypeMap.get(dt) : dt);
            }
            returnTypes = StringUtils.join(dts, ", ");
        }
        else {
            returnTypes = StringUtils.join(dataTypes, ", ");
        }
        System.out.println("returntypes: " + returnTypes);
        return returnTypes;
    }

    private Map<String, List<String>> doDataTypeColumnsMap(AdvancedSearchPhenotypeForm mpForm, AdvancedSearchGeneForm geneForm, AdvancedSearchDiseaseForm diseaseForm){

        Map<String, List<String>> dataTypeColsMap = new HashMap<>();

        if (mpForm.getHasOutputColumn() != null){

            dataTypeColsMap.put("Mp", new ArrayList<>());
            dataTypeColsMap.put("StatisticalResult", new ArrayList<>());

            if (mpForm.getShowMpTerm() != null){
                dataTypeColsMap.get("Mp").add("mpTerm");
            }
            if (mpForm.getShowMpId() != null ){
                dataTypeColsMap.get("Mp").add("mpId");
            }
            if (mpForm.getShowMpTermSynonym() != null ){
                dataTypeColsMap.get("Mp").add("mpSynonym");
            }
            if (mpForm.getShowMpDefinition() != null ){
                dataTypeColsMap.get("Mp").add("mpDefinition");
            }
            if (mpForm.getShowTopLevelMpTerm() != null ){
                dataTypeColsMap.get("Mp").add("topLevelMpTerms");
            }
            if (mpForm.getShowTopLevelMpId() != null ){
                dataTypeColsMap.get("Mp").add("topLevelMpIds");
            }
            if (mpForm.getShowParameterName() != null ){
                dataTypeColsMap.get("StatisticalResult").add("parameterName");
            }
            if (mpForm.getShowPvalue() != null ){
                dataTypeColsMap.get("StatisticalResult").add("pvalue");
            }
        }
        if (geneForm.getHasOutputColumn() != null){

            dataTypeColsMap.put("Allele", new ArrayList<>());
            dataTypeColsMap.put("Gene", new ArrayList<>());

            if (geneForm.getShowAlleleSymbol() != null){
                dataTypeColsMap.get("Allele").add("alleleSymbol");
            }
            if (geneForm.getShowAlleleId() != null){
                dataTypeColsMap.get("Allele").add("alleleMgiAccessionId");
            }
            if (geneForm.getShowAlleleDesc() != null){
                dataTypeColsMap.get("Allele").add("alleleDescription");
            }
            if (geneForm.getShowAlleleType() != null){
                dataTypeColsMap.get("Allele").add("alleleType");
            }
            if (geneForm.getShowAlleleMutationType() != null){
                dataTypeColsMap.get("Allele").add("mutationType");
            }
            if (geneForm.getShowEsCellAvailable() != null){
                dataTypeColsMap.get("Gene").add("esCellStatus");
            }
            if (geneForm.getShowMouseAvailable() != null){
                dataTypeColsMap.get("Gene").add("mouseStatus");
            }
            if (geneForm.getShowPhenotypingAvailable() != null){
                dataTypeColsMap.get("Gene").add("phenotypeStatus");
            }
            if (geneForm.getShowHgncGeneSymbol() != null){
                dataTypeColsMap.get("Gene").add("humanGeneSymbols");
            }
            if (geneForm.getShowMgiGeneSymbol() != null){
                dataTypeColsMap.get("Gene").add("markerSymbol");
            }
            if (geneForm.getShowMgiGeneId() != null){
                dataTypeColsMap.get("Gene").add("mgiAccessionId");
            }
            if (geneForm.getShowMgiGeneType() != null){
                dataTypeColsMap.get("Gene").add("markerType");
            }
            if (geneForm.getShowMgiGeneName() != null){
                dataTypeColsMap.get("Gene").add("markerName");
            }
            if (geneForm.getShowMgiGeneSynonym() != null){
                dataTypeColsMap.get("Gene").add("markerSynonyms");
            }
            if (geneForm.getShowChrId() != null){
                dataTypeColsMap.get("Gene").add("chrId");
            }
            if (geneForm.getShowChrStart() != null){
                dataTypeColsMap.get("Gene").add("chrStart");
            }
            if (geneForm.getShowChrEnd() != null){
                dataTypeColsMap.get("Gene").add("chrEnd");
            }
            if (geneForm.getShowChrStrand() != null){
                dataTypeColsMap.get("Gene").add("chrStrand");
            }
            if (geneForm.getShowEnsemblGeneId() != null){
                dataTypeColsMap.get("Gene").add("ensemblGeneIds");
            }
        }
        if (diseaseForm.getHasOutputColumn() != null){

            dataTypeColsMap.put("DiseaseModel", new ArrayList<>());

            if (diseaseForm.getShowDiseaseTerm() != null){
                dataTypeColsMap.get("DiseaseModel").add("diseaseTerm");
            }
            if (diseaseForm.getShowDiseaseId() != null){
                dataTypeColsMap.get("DiseaseModel").add("diseaseId");
            }
            if (diseaseForm.getShowDiseaseToModelScore() != null){
                dataTypeColsMap.get("DiseaseModel").add("diseaseToModelScore");
            }
            if (diseaseForm.getShowDiseaseClasses() != null){
                dataTypeColsMap.get("DiseaseModel").add("diseaseClasses");
            }
            if (diseaseForm.getShowImpcPredicted() != null){
                dataTypeColsMap.get("DiseaseModel").add("impcPredicted");
            }
            if (diseaseForm.getShowMgiPredicted() != null){
                dataTypeColsMap.get("DiseaseModel").add("mgiPredicted");
            }
        }
        return dataTypeColsMap;
    }

    public List<Object> fetchGraphDataAdvSrchResult(AdvancedSearchPhenotypeForm mpForm, AdvancedSearchGeneForm geneForm, AdvancedSearchDiseaseForm diseaseForm, String fileType) throws IOException, SolrServerException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {

        String sortStr = " ORDER BY g.markerSymbol ";
        String query = null;

        // for cypher column output
        Map<String, List<String>> dataTypeColsMap = doDataTypeColumnsMap(mpForm, geneForm, diseaseForm);
        Map<String, String> dtypeMap = new HashMap<>();
        List<String> dataTypes = new ArrayList<>();

        if (mpForm.getHasOutputColumn() != null) {
            dataTypes.add("mp");
            dtypeMap.put("mp", "nodes.mps");
            if (mpForm.getShowParameterName() != null || mpForm.getShowPvalue() != null) {
                dataTypes.add("sr");
                dtypeMap.put("sr", "nodes.srs");
            }
        }
        if (geneForm.getHasOutputColumn() != null) {
            dataTypes.add("g");

            if (geneForm.getShowAlleleSymbol() != null ||
                    geneForm.getShowAlleleId() != null ||
                    geneForm.getShowAlleleDesc() != null ||
                    geneForm.getShowAlleleMutationType() != null ||
                    geneForm.getShowAlleleType() != null) {
                dataTypes.add("a");
                dtypeMap.put("a", "nodes.alleles");
            }
        }
        if (diseaseForm.getHasOutputColumn() != null) {
            dataTypes.add("dm");
        }

        String returnDtypes = null;

        // phenotype form
        Boolean noMpChild = mpForm.getExcludeNestedPhenotype();
        String significantCypher = composeSignificance(mpForm);
        String parameterCypher = composeParameter(mpForm);

        // gene form
        String chrRangeCypher = composeChrRangeStr(geneForm);
        String geneListCypher = composeGeneListStr(geneForm);
        String genotypesCypher = composeGenotypesStr(geneForm);
        String alleleTypesCypher = composeAlleleTypesStr(geneForm);

        // disease
        String phenodigmScoreCypher = composePhenodigmScoreStr(diseaseForm);  // always non-empty
        String diseaseGeneAssociationCypher = composeDiseaseGeneAssociation(diseaseForm);
        String humanDiseaseTermCypher = composeHumanDiseaseTermStr(diseaseForm);

        String geneToMpPath = noMpChild ? "MATCH (g:Gene)-[:ALLELE]->(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                : " MATCH (g:Gene)-[:ALLELE]->(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

        String mpToGenePath = noMpChild ? "MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)<-[:ALLELE]-(g:Gene) "
                : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)<-[:ALLELE]-(g:Gene) ";


        String geneToDmPathClause = " OPTIONAL MATCH (g)<-[:GENE]-(dm:DiseaseModel)-[:MOUSE_PHENOTYPE]->(dmp:Mp) WHERE "
                + phenodigmScoreCypher + diseaseGeneAssociationCypher + humanDiseaseTermCypher;

        String mpToDmClause = " OPTIONAL MATCH (mp)<-[:MOUSE_PHENOTYPE]-(dm:DiseaseModel) WHERE "
                + phenodigmScoreCypher + diseaseGeneAssociationCypher + humanDiseaseTermCypher;

        List<String> narrowOrSynonymMapping = new ArrayList<>();

        //------------------------------------------------------
        //   Building cypher for each of the search use cases
        //------------------------------------------------------

        if (mpForm.getPhenotypeRows().size() == 0 && diseaseForm.getHumanDiseaseTerm() != null) {
            // CASE 1 - search by disease term AND NO phenotype
            System.out.println("Search by disease term AND NO phenotype");

            String whereClause = noMpChild ? " WHERE mp.mpTerm =~ '.*' " : " WHERE mp0.mpTerm =~ '.*' ";
            String mpTerm = null;

            query = mpToGenePath + "<-[:GENE]-(dm:DiseaseModel)-[:MOUSE_PHENOTYPE]->(dmp:Mp)"
                    + whereClause
                    + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                    + " AND " + phenodigmScoreCypher + diseaseGeneAssociationCypher + humanDiseaseTermCypher
                    + " AND dmp.mpTerm IN mp.mpTerm";

            returnDtypes = fetchReturnTypes(dataTypes, dtypeMap, mpForm);

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";
        } else if (mpForm.getPhenotypeRows().size() == 0) {
            // CASE 2 - search by no phenotype and no disease
            System.out.println("Search by no phenotype and no disease");

            String whereClause = noMpChild ? " WHERE mp.mpTerm =~ '.*' " : " WHERE mp0.mpTerm =~ '.*' ";
            String mpTerm = null;

            query = mpToGenePath
                    + whereClause
                    + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                    + " WITH g, a, mp, sr, "
                    + " extract(x IN collect(DISTINCT mp) | x.mpTerm) AS mps "
                    + geneToDmPathClause
                    + " AND dmp.mpTerm IN mps";

            returnDtypes = fetchReturnTypes(dataTypes, dtypeMap, mpForm);

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";

        } else if (mpForm.getPhenotypeRows().size() == 1) {
            // CASE 3 - search by only 1 mp

            System.out.println("Search by A");

            String mpTerm = mpForm.getPhenotypeRows().get(0).getPhenotypeTerm();
            mpTerm = mapNarrowSynonym2MpTerm(narrowOrSynonymMapping, mpTerm, autosuggestCore);
            String pvaluesCypher = composePvalues(mpForm, mpTerm);
            String whereClause = noMpChild ? " WHERE mp.mpTerm = '" + mpTerm + "'" : " WHERE mp0.mpTerm = '" + mpTerm + "'";

//            System.out.println("1: "+  significantCypher + "2: "+  parameterCypher + "3: "+  pvaluesCypher + "4: "+  chrRangeCypher +
//                    "5: "+  geneListCypher + "6: "+  genotypesCypher + "7: "+  alleleTypesCypher);

            if (geneListCypher.isEmpty()) {
                query = mpToGenePath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause
                        + " AND " + significantCypher + parameterCypher + pvaluesCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, a, mp, sr "
                        + mpToDmClause;
//                        + " extract(x IN collect(DISTINCT mp) | x.mpTerm) AS mps "
//                        + geneToDmPathClause
//                        + " AND dmp.mpTerm IN mps";
            } else {
                query = geneToMpPath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause
                        + " AND " + significantCypher + parameterCypher + pvaluesCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, a, mp, sr "
                        + mpToDmClause;
//                        + " extract(x IN collect(DISTINCT mp) | x.mpTerm) AS mps "
//                        + geneToDmPathClause
//                        + " AND dmp.mpTerm IN mps";
            }

            returnDtypes = fetchReturnTypes(dataTypes, dtypeMap, mpForm);

            query += fileType != null ?
                    //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                    " RETURN distinct " + returnDtypes + sortStr :
                    " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";
        } else if (mpForm.getPhenotypeRows().size() == 2) {
            // search by 2 phenotypes

            String mpA = mpForm.getPhenotypeRows().get(0).getPhenotypeTerm();
            String pvaluesACypher = composePvalues(mpForm, mpA);
            mpA = mapNarrowSynonym2MpTerm(narrowOrSynonymMapping, mpA, autosuggestCore);

            String mpB = mpForm.getPhenotypeRows().get(1).getPhenotypeTerm();
            String pvaluesBCypher = composePvalues(mpForm, mpB);
            mpB = mapNarrowSynonym2MpTerm(narrowOrSynonymMapping, mpB, autosuggestCore);

            Logical logical1 = mpForm.getLogical1();

            if (logical1.equals(Logical.AND)) {
                // CASE 4 - mpA AND mpB

                System.out.println("Search by A AND B");

                String whereClause1 = noMpChild ? " WHERE mp.mpTerm = '" + mpA + "'" + pvaluesACypher : " WHERE mp0.mpTerm = '" + mpA + "'" + pvaluesACypher;
                String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + mpB + "'" + pvaluesBCypher : " WHERE mp0.mpTerm = '" + mpB + "'" + pvaluesBCypher;

                String mpMatchClause = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                        : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

                query = mpToGenePath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({alleles:a, mps:mp, srs:sr}) AS list1 "

                        + mpMatchClause
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({alleles:a, mps:mp, srs:sr}) AS list2 "
                        // + " WHERE ALL (x IN list1 WHERE x IN list2) "
                        + " WITH g, list1+list2 AS list "
                        + " UNWIND list AS nodes "
                        + " WITH g, nodes, extract(x IN collect(DISTINCT nodes.mps) | x.mpTerm) AS mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";

                returnDtypes = fetchReturnTypes(dataTypes, dtypeMap, mpForm);

                query += fileType != null ?
                        //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                        //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                        " RETURN distinct " + returnDtypes + sortStr :
                        " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";
            } else if (logical1.equals(Logical.OR)) {
                // CASE 5 - mpA OR mpB

                System.out.println("Search by A OR B");
                if (geneListCypher.isEmpty()) {
                    query = mpToGenePath;
                } else {
                    query = geneToMpPath;
                }

                String whereClause = noMpChild ? " WHERE ((mp.mpTerm = '" + mpA + "'" + pvaluesACypher + ") OR (mp.mpTerm ='" + mpB + "'" + pvaluesBCypher + ")) "
                        : " WHERE ((mp0.mpTerm = '" + mpA + "'" + pvaluesACypher + ") OR (mp0.mpTerm ='" + mpB + "'" + pvaluesBCypher + "))";
                query +=
                        //  " WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*')) "
                        whereClause
                                + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                                + " WITH g, a, sr, mp, extract(x in collect(distinct mp) | x.mpTerm) as mps "
                                + geneToDmPathClause
                                + " AND dmp.mpTerm IN mps";

                returnDtypes = fetchReturnTypes(dataTypes, dtypeMap, mpForm);

                query += fileType != null ?
                        //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                        " RETURN distinct " + returnDtypes + sortStr :
                        " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";
            }

        } else if (mpForm.getPhenotypeRows().size() == 3) {
            // search by 3  phenotypes

            String mpA = mpForm.getPhenotypeRows().get(0).getPhenotypeTerm();
            String pvaluesACypher = composePvalues(mpForm, mpA);
            mpA = mapNarrowSynonym2MpTerm(narrowOrSynonymMapping, mpA, autosuggestCore);

            String mpB = mpForm.getPhenotypeRows().get(1).getPhenotypeTerm();
            String pvaluesBCypher = composePvalues(mpForm, mpB);
            mpB = mapNarrowSynonym2MpTerm(narrowOrSynonymMapping, mpB, autosuggestCore);

            String mpC = mpForm.getPhenotypeRows().get(2).getPhenotypeTerm();
            String pvaluesCCypher = composePvalues(mpForm, mpC);
            mpC = mapNarrowSynonym2MpTerm(narrowOrSynonymMapping, mpC, autosuggestCore);

            Logical logical1 = mpForm.getLogical1();
            Logical logical2 = mpForm.getLogical2();

            // CASE 6 - mpA AND mpB AND mpC
            if (logical1.equals(Logical.AND) && logical2.equals(Logical.AND)) {
                System.out.println("Search by A AND B AND C");

                String whereClause1 = noMpChild ? " WHERE mp.mpTerm = '" + mpA + "'" + pvaluesACypher : " WHERE mp0.mpTerm = '" + mpA + "'" + pvaluesACypher;
                String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + mpB + "'" + pvaluesBCypher : " WHERE mp0.mpTerm = '" + mpB + "'" + pvaluesBCypher;
                String whereClause3 = noMpChild ? " WHERE mp.mpTerm = '" + mpC + "'" + pvaluesCCypher : " WHERE mp0.mpTerm = '" + mpC + "'" + pvaluesCCypher;

                String mpMatchClause = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                        : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

                query = mpToGenePath
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                        + whereClause1
                        + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, collect({alleles:a, mps:mp, srs:sr}) AS list1 "

                        + mpMatchClause
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                        + whereClause2
                        + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, collect({alleles:a, mps:mp, srs:sr}) AS list2 "

                        + mpMatchClause
                        //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                        + whereClause3
                        + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                        + " WITH g, list1, list2, collect({alleles:a, mps:mp, srs:sr}) AS list3 "
                        // + " WHERE ALL (x IN list1 WHERE x IN list2) AND ALL (x IN list2 WHERE x IN list3) "
                        + " WITH g, list1+list2+list3 AS list "
                        + " UNWIND list AS nodes "
                        + " WITH g, nodes, extract(x IN collect(DISTINCT nodes.mps) | x.mpTerm) AS mps "
                        + geneToDmPathClause
                        + " AND dmp.mpTerm IN mps";

                returnDtypes = fetchReturnTypes(dataTypes, dtypeMap, mpForm);

                query += fileType != null ?
                        //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                        //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                        " RETURN distinct " + returnDtypes + sortStr :
                        " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";
            }
            // CASE 7 - mpA OR mpB OR mpC
            else if (logical1.equals(Logical.OR) && logical2.equals(Logical.OR)) {
                System.out.println("Search by A OR B OR C");

                if (geneListCypher.isEmpty()) {
                    query = mpToGenePath;
                } else {
                    query = geneToMpPath;
                }

                String whereClause = noMpChild ? " WHERE ((mp.mpTerm = '" + mpA + "'" + pvaluesACypher + ") OR (mp.mpTerm ='" + mpB + "'" + pvaluesBCypher + ") OR (mp.mpTerm = '" + mpC + "'" + pvaluesCCypher + ") ) "
                        : " WHERE ((mp0.mpTerm = '" + mpA + "'" + pvaluesACypher + ") OR (mp0.mpTerm ='" + mpB + "'" + pvaluesBCypher + ") OR (mp.mpTerm = '" + mpC + "'" + pvaluesCCypher + ")) ";

                query +=
                        //  " WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*')) "
                        whereClause
                                + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                                + " WITH g, a, sr, mp, extract(x in collect(distinct mp) | x.mpTerm) as mps "
                                + geneToDmPathClause
                                + " AND dmp.mpTerm IN mps";

                returnDtypes = fetchReturnTypes(dataTypes, dtypeMap, mpForm);

                query += fileType != null ?
                        //" RETURN distinct a, g, sr, collect(distinct mp), collect(distinct dm)" + sortStr :
                        " RETURN distinct " + returnDtypes + sortStr :
                        " RETURN collect(distinct a), collect(distinct g), collect(distinct sr), collect(distinct mp), collect(distinct dm)";
            }
            // CASE 8 - (mpA AND mpB) OR mpC
            else if (logical1.equals(Logical.AND) && logical2.equals(Logical.OR)) {
                System.out.println("Search by (A AND B) OR C)");

                String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + mpA + "'" + pvaluesACypher + ") OR (mp.mpTerm ='" + mpC + "'" + pvaluesCCypher + ")) "
                        : " WHERE ((mp0.mpTerm = '" + mpA + "'" + pvaluesACypher + ") OR (mp0.mpTerm ='" + mpC + "'" + pvaluesCCypher + "))";

                String whereClause2 = noMpChild ? " WHERE ((mp.mpTerm = '" + mpB + "'" + pvaluesBCypher + ") OR (mp.mpTerm ='" + mpC + "'" + pvaluesCCypher + ")) "
                        : " WHERE ((mp0.mpTerm = '" + mpB + "'" + pvaluesBCypher + ") OR (mp0.mpTerm ='" + mpC + "'" + pvaluesCCypher + "))";

                String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                        : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

                String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                        : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

                if (geneListCypher.isEmpty()) {

                    // collect() will be null if one of the list is empty, to avoid this, use OPTIONAL MATCH instead

                    query = mpToGenePath
                            //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                            + whereClause1
                            + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                            + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) AS list1 "

                            + matchClause1
                            //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                            + whereClause2
                            + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                            + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) AS list2 "
                            //  + " WHERE ALL (x IN list1 WHERE x IN list2) "
                            + " WITH g, list1+list2 AS list "
                            + " UNWIND list AS nodes "

                            + " WITH g, nodes, extract(x IN collect(DISTINCT nodes.mps) | x.mpTerm) AS mps "
                            + geneToDmPathClause
                            + " AND dmp.mpTerm IN mps";
                } else {
                    query = geneToMpPath
                            //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') "
                            + whereClause1
                            + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                            + " WITH g, collect({genes:g, alleles:a, srs:sr, mps:mp}) AS list1 "

                            + matchClause2
                            //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*') "
                            + whereClause2
                            + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                            + " WITH g, list1, collect({genes:g, alleles:a, srs:sr, mps:mp}) AS list2 "
                            // + " WHERE ALL (x IN list1 WHERE x IN list2) "
                            + " WITH g, list1+list2 AS list "
                            + " UNWIND list AS nodes "

                            + " WITH g, nodes, extract(x IN collect(DISTINCT nodes.mps) | x.mpTerm) AS mps "
                            + geneToDmPathClause
                            + " AND dmp.mpTerm IN mps";
                }

                returnDtypes = fetchReturnTypes(dataTypes, dtypeMap, mpForm);

                query += fileType != null ?
                        //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr
                        //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                        " RETURN distinct " + returnDtypes + sortStr :
                        " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";
            }
            // CASE 9 - (mpA OR mpB) AND mpC
            else if (logical1.equals(Logical.OR) && logical2.equals(Logical.AND)) {
                System.out.println("Search by (A OR B) AND C)");

                String whereClause1 = noMpChild ? " WHERE ((mp.mpTerm = '" + mpA + "'" + pvaluesACypher + ") OR (mp.mpTerm ='" + mpB + "'" + pvaluesBCypher + ")) "
                        : " WHERE ((mp0.mpTerm = '" + mpA + "'" + pvaluesACypher + ") OR (mp0.mpTerm ='" + mpB + "'" + pvaluesBCypher + ")) ";

                String whereClause2 = noMpChild ? " WHERE mp.mpTerm = '" + mpC + "'" + pvaluesCCypher : " WHERE mp0.mpTerm = '" + mpC + "'" + pvaluesCCypher;

                String matchClause1 = noMpChild ? " MATCH (mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) "
                        : " MATCH (mp0:Mp)<-[:PARENT*0..]-(mp:Mp)<-[:MP]-(sr:StatisticalResult)-[:ALLELE]->(a:Allele)-[:GENE]->(g) ";

                String matchClause2 = noMpChild ? " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp) "
                        : " MATCH (g)<-[:GENE]-(a:Allele)<-[:ALLELE]-(sr:StatisticalResult)-[:MP]->(mp:Mp)-[:PARENT*0..]->(mp0:Mp) ";

                if (geneListCypher.isEmpty()) {
                    query = mpToGenePath
                            //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*')) "
                            + whereClause1
                            + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                            + " WITH g, collect({alleles:a, srs:sr, mps:mp}) AS list1 "

                            + matchClause1
                            //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                            + whereClause2
                            + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                            + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) AS list2 "
                            // + " WHERE ALL (x IN list1 WHERE x IN list2) "
                            + " WITH g, list1+list2 AS list "
                            + " UNWIND list AS nodes "
                            + " WITH g, nodes, extract(x IN collect(DISTINCT nodes.mps) | x.mpTerm) AS mps "
                            + geneToDmPathClause
                            + " AND dmp.mpTerm IN mps";
                } else {
                    query = geneToMpPath
                            //+ "WHERE (mp0.mpTerm =~ ('(?i)'+'.*'+{mpA}+'.*') OR mp0.mpTerm =~ ('(?i)'+'.*'+{mpB}+'.*')) "
                            + whereClause1
                            + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                            + " WITH g, collect({alleles:a, srs:sr, mps:mp}) AS list1 "

                            + matchClause2
                            //+ " WHERE mp0.mpTerm =~ ('(?i)'+'.*'+{mpC}+'.*') "
                            + whereClause2
                            + " AND " + significantCypher + parameterCypher + chrRangeCypher + geneListCypher + genotypesCypher + alleleTypesCypher
                            + " WITH g, list1, collect({alleles:a, srs:sr, mps:mp}) AS list2 "
                            // + " WHERE ALL (x IN list1 WHERE x IN list2) "
                            + " WITH g, list1+list2 AS list "
                            + " UNWIND list AS nodes "
                            + " WITH g, nodes, extract(x IN collect(DISTINCT nodes.mps) | x.mpTerm) AS mps "
                            + geneToDmPathClause
                            + " AND dmp.mpTerm IN mps";
                }

                returnDtypes = fetchReturnTypes(dataTypes, dtypeMap, mpForm);

                query += fileType != null ?
                        //" RETURN distinct nodes.alleles, g, nodes.srs, collect(distinct nodes.mps), collect(distinct dm)" + sortStr :
                        //" RETURN distinct nodes.alleles, g, nodes.srs, nodes.mps, dm" + sortStr :
                        " RETURN distinct " + returnDtypes + sortStr :
                        " RETURN collect(distinct nodes.alleles), collect(distinct g), collect(distinct nodes.srs), collect(distinct nodes.mps), collect(distinct dm)";
            }

        }

        System.out.println("CYPHER QUERY: " + query);

        HashedMap params = new HashedMap();
        Result result = null;

        long begin = System.currentTimeMillis();
        result = neo4jSession.query(query, params); // params is empty, I don't need it in my case, but method needs it as 2nd argument
        long end = System.currentTimeMillis();
        System.out.println("Neo4j returned query in " + (end - begin) + " ms");

        List<Object> objects = new ArrayList<>();
        objects.add(result);
        objects.add(narrowOrSynonymMapping);
        objects.add(dataTypeColsMap);

        int dataCount = 0;
        for (Map<String,Object> row : result) {

//            System.out.println(row.toString());
//            System.out.println("cols: " + row.size());
//            Thread.sleep(10000);
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getValue() != null && ! entry.getValue().toString().startsWith("[Ljava.lang.Object")) {

                    dataCount++;
                    //System.out.println("---------- " + entry.getKey() + " / " + entry.getValue());
                    //System.out.println("---------- " + entry.getKey() + " / " +  entry.getValue().toString());
                }
            }
        }

        if (dataCount == 0){
            System.out.println("No data found based on your query");
        }
        return objects;
    }

    public JSONObject parseGraphResult(Result result, AdvancedSearchPhenotypeForm mpForm, AdvancedSearchGeneForm geneForm, AdvancedSearchDiseaseForm diseaseForm,
                                       String fileType, String baseUrl, String hostname, List<String> narrowOrSynonymMapping, Map<String, List<String>> dataTypeColsMap, List<String> colOrder)
            throws IOException, SolrServerException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InterruptedException {

        int rowCount = 0;
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);
        j.put("iDisplayStart", 0);
        j.put("iDisplayLength", 10);
        j.put("narrowOrSynonymMapping", StringUtils.join(narrowOrSynonymMapping, ", "));

        List<String> rowDataExport = new ArrayList<>(); // for export
        List<String> rowDataOverview = new ArrayList<>(); // for overview
        List<String> dtypes = Arrays.asList("Allele", "Gene", "Mp", "DiseaseModel", "StatisticalResult");
        //List<String> dtypes = Arrays.asList("a", "g", "mp", "dm", "sr");

        long tstart = System.currentTimeMillis();

        if (fileType != null){
            // export result

            List<String> cols = new ArrayList<>();
            Map<String, List<String>> node2Properties = new LinkedHashMap<>();

            for (String dtype : dtypes){

                node2Properties.put(dtype, new ArrayList<String>());

                if (dataTypeColsMap.containsKey(dtype)) {
                    for (String colName : dataTypeColsMap.get(dtype)) {

                        //System.out.println("colname: " + colName);
                        if (colName.equals("alleleSymbol") && ! dataTypeColsMap.get(dtype).contains("alleleMgiAccessionId")) {
                            cols.add(colName);
                            cols.add("alleleMgiAccessionId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("alleleMgiAccessionId");
                        } else if (colName.equals("markerSymbol") && ! dataTypeColsMap.get(dtype).contains("mgiAccessionId")) {
                            cols.add(colName);
                            cols.add("mgiAccessionId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("mgiAccessionId");
                        } else if (colName.equals("mpTerm") && ! dataTypeColsMap.get(dtype).contains("mpId")) {
                            cols.add(colName);
                            cols.add("mpId");
                            node2Properties.get(dtype).add(colName);
                            node2Properties.get(dtype).add("mpId");
                        } else if (colName.equals("diseaseTerm") && ! dataTypeColsMap.get(dtype).contains("diseaseId")) {
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

                List<String> data = new ArrayList<>(); // for export

                Map<String, Set<String>> colValMap = new TreeMap();

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    //System.out.println(entry.getKey() + " / " + entry.getValue());

                    if (entry.getValue() != null) {

                        if (entry.getKey().startsWith("collect")) {
                            List<Object> objs = (List<Object>) entry.getValue();
                            for (Object obj : objs) {
                                populateColValMapAdvSrch(hostname, baseUrl,node2Properties, obj, colValMap, fileType, mpForm, geneForm, diseaseForm);
                            }
                        } else {
                            Object obj = entry.getValue();
                            populateColValMapAdvSrch(hostname, baseUrl,node2Properties, obj, colValMap, fileType, mpForm, geneForm, diseaseForm);
                        }
                    }
                }
                //-------- start of export
                //System.out.println("colValMap: " + colValMap.toString());

                //System.out.println("cols: " + cols);
                if (colValMap.size() > 0) {
                    for (String col : cols) {
                        if (colValMap.containsKey(col)) {
                            //System.out.println("col now-2: " + col);
                            List<String> vals = new ArrayList<>(colValMap.get(col));

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

            Map<String, List<String>> node2Properties = new LinkedHashMap<>();

            for (String dtype : dtypes) {

                node2Properties.put(dtype, new ArrayList<String>());
                if (dataTypeColsMap.containsKey(dtype)) {
                    for(String colName : dataTypeColsMap.get(dtype)){
                        node2Properties.get(dtype).add(colName);
                    }

                }
            }

            //System.out.println("columns: " + colOrder);
            Map<String, Set<String>> colValMap = new TreeMap<>(); // for export

            for (Map<String, Object> row : result) {
                //System.out.println(row.toString());
                //System.out.println("cols: " + row.size());

                for (Map.Entry<String, Object> entry : row.entrySet()) {

                    if (entry.getValue() != null && ! entry.getValue().toString().startsWith("[Ljava.lang.Object")) {
                        // value is empty if startsWith("[Ljava.lang.Object")

                        if (entry.getKey().startsWith("collect")) {
                            List<Object> objs = (List<Object>) entry.getValue();

                            //System.out.println("---------- " + entry.getKey() + " / " + entry.getValue());
                            //System.out.println("---------- " + entry.getKey() + " / " + ((List<Object>) entry.getValue()).size());

                            for (Object obj : objs) {
                                populateColValMapAdvSrch(hostname, baseUrl,node2Properties, obj, colValMap, fileType, mpForm, geneForm, diseaseForm);
                            }
                        } else {
                            Object obj = entry.getValue();
                            populateColValMapAdvSrch(hostname, baseUrl,node2Properties, obj, colValMap, fileType, mpForm, geneForm, diseaseForm);
                        }
                    }
                }
            }

            //System.out.println("keys: "+ colValMap.keySet());
            // for overview

            for (String col : colOrder){
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
                        //  System.out.println(col + " -- " + vals);
                    }
                }
                else {
                    rowDataOverview.add(NA);
                }
            }

            //System.out.println("rows done");

            j.put("iTotalRecords", rowCount);
            j.put("iTotalDisplayRecords", rowCount);
            j.getJSONArray("aaData").add(rowDataOverview);

            //System.out.println(j.toString());
        }

        long tend = System.currentTimeMillis();

        System.out.println("Parsing Neo4j result in " + (tend - tstart) + " ms");
        return j;
    }

    public String mapNarrowSynonym2MpTerm(List<String> termMapping, String mpTerm, SolrClient autosuggestCore) throws IOException, SolrServerException {

        String mpStr = null;
        SolrQuery query = new SolrQuery();
        query.setQuery("\"" + mpTerm + "\"");
        query.set("qf", "auto_suggest");
        query.set("defType", "edismax");
        query.setStart(0);
        query.setRows(100);
        query.setFilterQueries("docType:mp");
        System.out.println("mapNarrow query="+query);
        //System.out.println(autosuggestCore);
        QueryResponse response = autosuggestCore.query(query);
        System.out.println("response: " + response);
        SolrDocumentList results = response.getResults();

        for(SolrDocument doc : results){
            if (doc.containsKey("mp_term") && doc.getFieldValue("mp_term").equals(mpTerm)){
                //System.out.println(doc.getFieldValue("mp_term"));
                mpStr = mpTerm;
                break;
            }
            else if (doc.containsKey("mp_term_synonym") && doc.getFieldValue("mp_term_synonym").equals(mpTerm)){
                //System.out.println("NS: "+ doc.getFieldValue("mp_narrow_synonym"));
                mpStr = doc.getFieldValue("mp_term").toString();
                termMapping.add(mpTerm + " is a synonym of " + mpStr);
                break;
            }
            else if (doc.containsKey("mp_narrow_synonym") && doc.getFieldValue("mp_narrow_synonym").equals(mpTerm)){
                //System.out.println("NS: "+ doc.getFieldValue("mp_narrow_synonym"));
                mpStr = doc.getFieldValue("mp_term").toString();
                termMapping.add(mpTerm + " is not directly annotated in IMPC and is a child term of " + mpStr);
                break;
            }
        }

        return mpStr;
    }

    public AdvancedSearchPhenotypeForm parsePhenotypeForm(JSONObject jParams){

        AdvancedSearchPhenotypeForm mpForm = new AdvancedSearchPhenotypeForm();

        // exclude nested phenotypes
        mpForm.setExcludeNestedPhenotype(jParams.containsKey("noMpChild") ? true : false);

        // phenotype rows
        for(int i=1; i<4; i++) {
            String mpKey = "mp" + i;

            if (jParams.containsKey(mpKey)){
                String mpTerm = jParams.getString(mpKey);

                System.out.println(mpKey + ":" + mpTerm);
                Double lowerPvalue = null;
                Double upperPvalue = null;

                System.out.println(" jParams.containsKey pvaluesMap: " + jParams.containsKey("pvaluesMap"));
                if (jParams.containsKey("pvaluesMap")) {
                    JSONObject map = jParams.getJSONObject("pvaluesMap").getJSONObject(mpTerm);
                    //System.out.println("map: " + map.toString());
                    if (map.containsKey("lowerPvalue")){
                        lowerPvalue = map.getDouble("lowerPvalue");
                        //System.out.println("lower: " + lowerPvalue);
                    }
                    if (map.containsKey("upperPvalue")){
                        upperPvalue = map.getDouble("upperPvalue");
                        //System.out.println("upper: " + upperPvalue);
                    }
                }
                AdvancedSearchMpRow mpRow = new AdvancedSearchMpRow(mpTerm, lowerPvalue, upperPvalue);
                mpForm.addPhenotypeRows(mpRow);
            }
        }

        // MP booleans
        if (jParams.containsKey("logical1")){
            Logical logical1 = null;
            mpForm.setLogical1(jParams.getString("logical1").equals("AND") ? Logical.AND : Logical.OR);
        }
        if (jParams.containsKey("logical2")){
            Logical logical2 = null;
            mpForm.setLogical2(jParams.getString("logical2").equals("AND") ? Logical.AND : Logical.OR);
        }

        // measured parameters
        if (jParams.containsKey("srchPipeline")) {
            mpForm.setImpressParameterName(jParams.getString("srchPipeline"));
        }

        // only significant pvalue for measured parameter
        mpForm.setSignificantPvaluesOnly(jParams.getBoolean("onlySignificantPvalue") ? true : false);

        // customed output columns
        if (jParams.containsKey("Mp")) {
            mpForm.setHasOutputColumn(true);

            JSONArray cols = jParams.getJSONArray("properties");
            if (cols.contains("mpTerm")){
                mpForm.setShowMpTerm(true);
            }
            if (cols.contains("mpId")){
                mpForm.setShowMpId(true);
            }
            if (cols.contains("mpDefinition")){
                mpForm.setShowMpDefinition(true);
            }
            if (cols.contains("topLevelMpId")){
                mpForm.setShowTopLevelMpId(true);
            }
            if (cols.contains("topLevelMpTerm")){
                mpForm.setShowTopLevelMpTerm(true);
            }
            if (cols.contains("ontoSynonym")) {
                mpForm.setShowMpTermSynonym(true);
            }
            if (cols.contains("parameterName")) {
                mpForm.setShowParameterName(true);
            }
            if (cols.contains("pvalue")) {
                mpForm.setShowPvalue(true);
            }
        }

        System.out.println("MP FORM: "+ mpForm.toString());

        return mpForm;
    }
    public AdvancedSearchGeneForm parseGeneForm(JSONObject jParams) {

        AdvancedSearchGeneForm geneForm = new AdvancedSearchGeneForm();

        if (jParams.containsKey("chrRange")) {
            String range = jParams.getString("chrRange");
            if (range.matches("^chr(\\w*):(\\d+)-(\\d+)$")) {
                System.out.println("find chr range");

                Pattern pattern = Pattern.compile("^chr(\\w*):(\\d+)-(\\d+)$");
                Matcher matcher = pattern.matcher(range);
                while (matcher.find()) {
                    String regionId = matcher.group(1);
                    int regionStart = Integer.parseInt(matcher.group(2));
                    int regionEnd = Integer.parseInt(matcher.group(3));

                    geneForm.setChrId(regionId);
                    geneForm.setChrStart(regionStart);
                    geneForm.setChrEnd(regionEnd);

                    //chrRange = " AND g.chrId IN [" + regionId + "] AND g.chrStart >= " + regionStart + " AND g.chrEnd <= " + regionEnd + " ";
                }
            }
        }
        else if (jParams.containsKey("chr")) {
            geneForm.setChrIds(jParams.getJSONArray("chr"));
        }

        if (jParams.containsKey("mouseGeneList")){
            JSONArray glist = jParams.getJSONArray("mouseGeneList");

            if (glist.get(0).toString().contains("MGI:")){
                geneForm.setMouseMgiGeneIds(glist);
            }
            else {
                geneForm.setMouseMarkerSymbols(glist);
            }
        }
        if (jParams.containsKey("humanGeneList")){
            JSONArray glist = jParams.getJSONArray("humanGeneList");
            List<String> symbols = new ArrayList<>();
            for(int i=0; i<glist.size(); i++) {
                symbols.add(glist.get(i).toString());
            }

            geneForm.setHumanMarkerSymbols(symbols);
        }

        if (jParams.containsKey("genotypes")) {
            JSONArray genotypes = jParams.getJSONArray("genotypes");
            List<String> gtypes = new ArrayList<>();
            for(int i=0; i<genotypes.size(); i++) {
                gtypes.add(genotypes.get(i).toString());
            }
            geneForm.setGenotypes(gtypes);
        }

        if (jParams.containsKey("alleleTypes")) {
            JSONArray alleleTypes = jParams.getJSONArray("alleleTypes");
            List<String> atypes = new ArrayList<>();
            for(int i=0; i<alleleTypes.size(); i++) {
                atypes.add(alleleTypes.get(i).toString());
            }
            geneForm.setAlleleTypes(atypes);
        }

        // customed output columns
        if (jParams.containsKey("Gene")) {
            geneForm.setHasOutputColumn(true);

            JSONArray cols = jParams.getJSONArray("properties");
            if (cols.contains("alleleSymbol")){
                geneForm.setShowAlleleSymbol(true);
            }
            if (cols.contains("alleleMgiAccessionId")){
                geneForm.setShowAlleleId(true);
            }
            if (cols.contains("alleleDescription")){
                geneForm.setShowAlleleDesc(true);
            }
            if (cols.contains("alleleType")){
                geneForm.setShowAlleleType(true);
            }
            if (cols.contains("mutationType")){
                geneForm.setShowAlleleMutationType(true);
            }
            if (cols.contains("esCellStatus")) {
                geneForm.setShowEsCellAvailable(true);
            }
            if (cols.contains("mouseStatus")) {
                geneForm.setShowMouseAvailable(true);
            }
            if (cols.contains("phenotypeStatus")) {
                geneForm.setShowPhenotypingAvailable(true);
            }
            if (cols.contains("humanGeneSymbol")){
                geneForm.setShowHgncGeneSymbol(true);
            }
            if (cols.contains("markerSymbol")){
                geneForm.setShowMgiGeneSymbol(true);
            }
            if (cols.contains("mgiAccessionId")){
                geneForm.setShowMgiGeneId(true);
            }
            if (cols.contains("markerType")){
                geneForm.setShowMgiGeneType(true);
            }
            if (cols.contains("markerName")){
                geneForm.setShowMgiGeneName(true);
            }
            if (cols.contains("markerSynonym")) {
                geneForm.setShowMgiGeneSynonym(true);
            }
            if (cols.contains("chrId")) {
                geneForm.setShowChrId(true);
            }
            if (cols.contains("chrStart")) {
                geneForm.setShowChrStart(true);
            }
            if (cols.contains("chrEnd")) {
                geneForm.setShowChrEnd(true);
            }
            if (cols.contains("chrStrand")) {
                geneForm.setShowChrStrand(true);
            }
            if (cols.contains("ensemblGeneId")) {
                geneForm.setShowEnsemblGeneId(true);
            }
        }

        System.out.println("GENE FORM: "+ geneForm);
        return geneForm;
    }
    public AdvancedSearchDiseaseForm parseDiseaseForm(JSONObject jParams) {

        AdvancedSearchDiseaseForm diseaseForm = new AdvancedSearchDiseaseForm();

        if (jParams.containsKey("diseaseGeneAssociation")) {
            diseaseForm.setHumanCurated(jParams.getString("diseaseGeneAssociation").equals("humanCurated") ? true : false);
        }

        if (jParams.containsKey("phenodigmScore")) {
            String[] scores = jParams.getString("phenodigmScore").split(",");
            diseaseForm.setPhenodigmLowerScore(Integer.parseInt(scores[0]));
            diseaseForm.setPhenodigmUpperScore(Integer.parseInt(scores[1]));
        }

        if (jParams.containsKey("srchDiseaseModel")) {
            diseaseForm.setHumanDiseaseTerm(jParams.getString("srchDiseaseModel"));
        }


        // customed output columns
        if (jParams.containsKey("DiseaseModel")) {

            diseaseForm.setHasOutputColumn(true);

            JSONArray cols = jParams.getJSONArray("properties");
            if (cols.contains("diseaseTerm")) {
                diseaseForm.setShowDiseaseTerm(true);
            }
            if (cols.contains("diseaseId")) {
                diseaseForm.setShowDiseaseId(true);
            }
            if (cols.contains("diseaseToModelScore")) {
                diseaseForm.setShowDiseaseToModelScore(true);
            }
            if (cols.contains("diseaseClasses")) {
                diseaseForm.setShowDiseaseClasses(true);
            }
            if (cols.contains("impcPredicted")) {
                diseaseForm.setShowImpcPredicted(true);
            }
            if (cols.contains("mgiPredicted")) {
                diseaseForm.setShowMgiPredicted(true);
            }
        }

        System.out.println("DISEASE FORM: "+ diseaseForm);
        return diseaseForm;
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
        if (chromosomes!=null && !chromosomes.isEmpty() && regionStart!=null && regionEnd!=null)
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
        else if (chromosomes!=null && chromosomes.size()>0 && chromosomes.size()<2) {// is this for the case where chromosome has been specified but region hasn't??
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


    private String composeDiseaseGeneAssociationCypher(String diseaseTerm, boolean humanCurated){
        String diseaeGeneAssoc = "";

        if (diseaseTerm!=null && diseaseTerm!="") {



            if (humanCurated){
                diseaeGeneAssoc = " AND dm.humanCurated = true ";
            }
            else {
                diseaeGeneAssoc = " AND dm.humanCurated = false ";
            }

        }
        return diseaeGeneAssoc;
    }
    private String composeHumanDiseaseTermCypher(String searchDiseaseModel){
        String humanDiseaseTerm = "";

        if (searchDiseaseModel!=null && !searchDiseaseModel.equals("")) {

            humanDiseaseTerm = " AND dm.diseaseTerm = '" + searchDiseaseModel + "' ";
        }
        return humanDiseaseTerm;
    }


//    public void populateColValMap(String hostname, String baseUrl, List<Object> objs, Map<String, Set<String>> colValMap, JSONObject jDatatypeProperties) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//
//        String fileType = null;
//        for (Object obj : objs) {
//            String className = obj.getClass().getSimpleName();
//
//            if (jDatatypeProperties.containsKey(className)) {
//
//                //System.out.println("className: " + className);
//
//                List<String> nodeProperties = jDatatypeProperties.getJSONArray(className);
//
//                if (className.equals("Gene")) {
//                    Gene g = (Gene) obj;
//                    getValues(hostname, baseUrl, nodeProperties, g, colValMap, fileType, jDatatypeProperties);  // convert to class ???
//                }
//                else if (className.equals("EnsemblGeneId")) {
//                    EnsemblGeneId ensg = (EnsemblGeneId) obj;
//                    getValues(hostname, baseUrl,nodeProperties, ensg, colValMap, fileType, jDatatypeProperties);
//                }
//                else if (className.equals("MarkerSynonym")) {
//                    MarkerSynonym m = (MarkerSynonym) obj;
//                    getValues(hostname, baseUrl,nodeProperties, m, colValMap, fileType, jDatatypeProperties);
//                }
//                else if (className.equals("HumanGeneSymbol")) {
//                    HumanGeneSymbol hg = (HumanGeneSymbol) obj;
//                    getValues(hostname, baseUrl,nodeProperties, hg, colValMap, fileType, jDatatypeProperties);
//                }
//                else if (className.equals("DiseaseModel")) {
//                    DiseaseModel dm = (DiseaseModel) obj;
//                    getValues(hostname, baseUrl,nodeProperties, dm, colValMap, fileType, jDatatypeProperties);
//                }
//                else if (className.equals("MouseModel")) {
//                    MouseModel mm = (MouseModel) obj;
//                    getValues(hostname, baseUrl,nodeProperties, mm, colValMap, fileType, jDatatypeProperties);
//                }
//                else if (className.equals("Allele")) {
//                    Allele allele = (Allele) obj;
//                    getValues(hostname, baseUrl,nodeProperties, allele, colValMap, fileType, jDatatypeProperties);
//                }
//                else if (className.equals("Mp")) {
//                    Mp mp = (Mp) obj;
//                    getValues(hostname, baseUrl,nodeProperties, mp, colValMap, fileType, jDatatypeProperties);
//                }
//                else if (className.equals("OntoSynonym")) {
//                    OntoSynonym ontosyn = (OntoSynonym) obj;
//                    getValues(hostname, baseUrl,nodeProperties, ontosyn, colValMap, fileType, jDatatypeProperties);
//                }
//                else if (className.equals("Hp")) {
//                    Hp hp = (Hp) obj;
//                    getValues(hostname, baseUrl,nodeProperties, hp, colValMap, fileType, jDatatypeProperties);
//                }
//            }
//        }
//
//    }

    public Map<String, Set<String>> getValues(String hostname, String baseUrl, List<String> nodeProperties, Object o, Map<String, Set<String>> colValMap, String fileType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        int showCutOff = 3;

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
                //System.out.println(property + " set to " + colVal);
            }


            if (! colVal.isEmpty()) {
                if (property.equals("markerSymbol")){
                    Gene gene = (Gene) o;
                    String mgiAcc = gene.getMgiAccessionId();
                    if (fileType == null || fileType.equals("html")){
                        colVal = "<a target='_blank' href='" + hostname + geneBaseUrl + mgiAcc + "'>" + colVal + "</a>";
                    }
                    else if (fileType != null){
                        colVal = hostname + geneBaseUrl + mgiAcc;
                    }
                    //System.out.println(fileType+ " mgi symbol link: " + colVal);
                }
                else if (property.equals("mpTerm")){
                    Mp mp = (Mp) o;
                    String mpId = mp.getMpId();
                    if (fileType == null || fileType.equals("html")) {
                        colVal = "<a target='_blank' href='" + hostname + mpBaseUrl + mpId + "'>" + colVal + "</a>";
                    }
                    else if (fileType != null){
                        colVal = hostname + geneBaseUrl + mpId;
                    }
                    //System.out.println(fileType+ " mp term link: " + colVal);
                }
                else if (property.equals("diseaseTerm")){
                    DiseaseModel dm = (DiseaseModel) o;
                    String dId = dm.getDiseaseId();
                    if (fileType == null || fileType.equals("html")) {
                        colVal = "<a target='_blank' href='" + hostname + diseaseBaseUrl + dId + "'>" + colVal + "</a>";
                    }
                    else if (fileType != null){
                        colVal = hostname + diseaseBaseUrl + dId;
                    }
                    //System.out.println(fileType+ " disease term link: " + colVal);
                }
                else if (property.equals("alleleSymbol")){

                    Allele al = (Allele) o;

                    int index = colVal.indexOf('<');
                    String wantedSymbol = colVal.replace(colVal.substring(0, index+1), "").replace(">","");
                    colVal = Tools.superscriptify(colVal);
                    String aid = al.getMgiAccessionId() + "/" + wantedSymbol;

                    if (fileType == null || fileType.equals("html")) {
                        colVal = "<a target='_blank' href='" + hostname + alleleBaseUrl + aid + "'>" + colVal + "</a>";
                    }
                    else if (fileType != null){
                        colVal = hostname + alleleBaseUrl + colVal;
                    }
                    //System.out.println(fileType+ " disease term link: " + colVal);
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

                    if (fileType == null || fileType.equals("html")){
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

//                System.out.println("colval: "+colValMap);

            }
        }
        return colValMap;
    }

    public void populateColValMapAdvSrch(String hostname, String baseUrl, Map<String, List<String>> node2Properties, Object obj, Map<String, Set<String>> colValMap, String fileType, AdvancedSearchPhenotypeForm mpForm, AdvancedSearchGeneForm geneForm, AdvancedSearchDiseaseForm diseaseForm) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String className = obj.getClass().getSimpleName();

        //if (jParam.containsKey(className)) {

        List<String> nodeProperties = node2Properties.get(className);
//            System.out.println("className: " + className);
//            System.out.println("properties:" + nodeProperties);

        if (className.equals("Gene")) {
            Gene g = (Gene) obj;
            getValues(hostname, baseUrl,nodeProperties, g, colValMap, fileType);
        }
        else if (className.equals("EnsemblGeneId")) {
            EnsemblGeneId ensg = (EnsemblGeneId) obj;
            getValues(hostname, baseUrl,nodeProperties, ensg, colValMap, fileType);
        }
        else if (className.equals("MarkerSynonym")) {
            MarkerSynonym m = (MarkerSynonym) obj;
            getValues(hostname, baseUrl,nodeProperties, m, colValMap, fileType);
        }
        else if (className.equals("HumanGeneSymbol")) {
            HumanGeneSymbol hg = (HumanGeneSymbol) obj;
            getValues(hostname, baseUrl,nodeProperties, hg, colValMap, fileType);
        }
        else if (className.equals("DiseaseModel")) {
            DiseaseModel dm = (DiseaseModel) obj;
            getValues(hostname, baseUrl,nodeProperties, dm, colValMap, fileType);
        }
        else if (className.equals("MouseModel")) {
            MouseModel mm = (MouseModel) obj;
            getValues(hostname, baseUrl,nodeProperties, mm, colValMap, fileType);
        }
        else if (className.equals("Allele")) {
            Allele allele = (Allele) obj;
            getValues(hostname, baseUrl,nodeProperties, allele, colValMap, fileType);
        }
        else if (className.equals("Mp")) {
            Mp mp = (Mp) obj;
            getValues(hostname, baseUrl,nodeProperties, mp, colValMap, fileType);
        }
        else if (className.equals("StatisticalResult")) {
            StatisticalResult sr = (StatisticalResult) obj;
            getValues(hostname, baseUrl,nodeProperties, sr, colValMap, fileType);
        }
        else if (className.equals("OntoSynonym")) {
            OntoSynonym ontosyn = (OntoSynonym) obj;
            getValues(hostname, baseUrl,nodeProperties, ontosyn, colValMap, fileType);
        }
        else if (className.equals("Hp")) {
            Hp hp = (Hp) obj;
            getValues(hostname, baseUrl,nodeProperties, hp, colValMap, fileType);
        }
        // }

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
