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

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.SolrIndex;

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
import uk.ac.ebi.phenotype.repository.Gene;
import uk.ac.ebi.phenotype.repository.GeneRepository;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Controller
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public class AdvancedSearchController {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

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
    private GeneRepository geneRepository;


    @RequestMapping(value = "/meshtree", method = RequestMethod.GET)
    public String loadBatchQueryPage(
            @RequestParam(value = "core", required = false) String core,
            HttpServletRequest request,
            Model model) {

        String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml(core);
        model.addAttribute("outputFields", outputFieldsHtml);


        return "treetest";
    }

    @RequestMapping(value = "/dataTable_bq2", method = RequestMethod.POST)
    public ResponseEntity<String> bqDataTableJson2(
            @RequestParam(value = "idlist", required = true) String idlistStr,
            @RequestParam(value = "labelFllist", required = true) String labelFllistStr,
            @RequestParam(value = "dataType", required = true) String dataType,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SolrServerException {

        System.out.println("dataType: " + dataType);
        ;
        System.out.println("idlist: " + idlistStr);
        JSONObject jLabelFieldsParam = (JSONObject) JSONSerializer.toJSON(labelFllistStr);
        System.out.println("Labels: " + jLabelFieldsParam.keySet());

        if (dataType.equals("geneChr")) {
            // convert coordiantes range to list of mouse gene ids
            String[] parts = idlistStr.replaceAll("\"", "").split(":");
            String chr = parts[0].replace("chr", "");
            String[] se = parts[1].split("-");
            String start = se[0];
            String end = se[1];
            String mode = "nonExport";
            List<String> geneIds = solrIndex.fetchQueryIdsFromChrRange(chr, start, end, mode);
            idlistStr = StringUtils.join(geneIds, ",");
        }

        // lowercase all queries for case insensitive purpose and sorting
        String idlistLower = lowercaseListStr(idlistStr);
        System.out.println("idlistLower: " + idlistLower);

        //List<String> cypherCols = getCheckedCols(jLabelFieldsParam);
        Gene gene = geneRepository.findByMarkerSymbol("Nxn");
        System.out.println(gene);

       // String cypherQry = buildCypherQueries(dataType, idlistLower, jLabelFieldsParam);

//		String colname = dataTypeCol.get(dataType).replaceAll("^\\w*\\.", "");
//
  //      String content = fetchCypherResult(cypherQry, idlistLower, jLabelFieldsParam);
        //return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
        return null;
    }

    public String lowercaseListStr(String idlist) {
        List<String> lst = Arrays.asList(StringUtils.split(idlist, ","));
        List<String> lst2 = new ArrayList<>();
        for (String s : lst) {
            lst2.add("lower(" + s + ")");
        }
        return StringUtils.join(lst2, ",");
    }


    public String buildCypherQueries(String dataType, String idlistStrLower, JSONObject jLabelFieldsParam) {

        String cyphers = "";

        if (dataType.equals("mouse_marker_symbol")) {
            cyphers += "MATCH (g:Gene) WHERE lower(g.marker_symbol) IN [" + idlistStrLower + "] WITH g, ";
            cyphers += getRelationsAndColVals("Gene", jLabelFieldsParam);
        }
        else if (dataType.equals("geneId") || dataType.equals("geneChr")) {
            cyphers += "MATCH (g:Gene) WHERE lower(g.mgi_accession_id) IN [" + idlistStrLower + "] WITH g, ";
            cyphers += getRelationsAndColVals("Gene", jLabelFieldsParam);
        }
        else if (dataType.equals("ensembl")) {
            cyphers += "MATCH (ensg:EnsemblGeneId)-[OF_GENE]->(g) WHERE lower(ensg.ensembl_gene_id) IN [" + idlistStrLower + "] WITH ensg, g, ";
            cyphers += getRelationsAndColVals("EnsemblGeneId", jLabelFieldsParam);
        }
        else if (dataType.equals("mp")) {
            cyphers += idlistStrLower.toLowerCase().contains("mp:") ? "MATCH (p:Phenotype) WHERE lower(p.mp_id) IN [" + idlistStrLower + "] WITH p, "
                    : "MATCH (p:Phenotype) WHERE lower(p.mp_term) IN [" + idlistStrLower + "] WITH p, ";

            cyphers += getRelationsAndColVals("Phenotype", jLabelFieldsParam);
        }
        else if (dataType.equals("human_marker_symbol")) {
            cyphers += "MATCH (hgncgs:HumanGeneSymbol)-[:OF_GENE]->(g:Genes) WHERE lower(hgs.human_gene_symbol) IN [" + idlistStrLower + "] WITH hgncgs, g, ";
            cyphers += getRelationsAndColVals("HumanGeneSymbol", jLabelFieldsParam);
        }
        else if (dataType.equals("hp")) {
            cyphers += idlistStrLower.toLowerCase().contains("hp:") ? "MATCH (hp:Hp) WHERE lower(hp.hp_id) IN [" + idlistStrLower + "] WITH hp, "
                    : "MATCH (hp:Hp) WHERE lower(hp.hp_term) IN [" + idlistStrLower + "] WITH hp, ";
            cyphers += getRelationsAndColVals("Hp", jLabelFieldsParam);
        }
        else if (dataType.equals("disease")) {
            cyphers += idlistStrLower.toLowerCase().contains("omim:") || idlistStrLower.contains("orphanet:") || idlistStrLower.contains("decipher:")
                    ? "MATCH (dma:DiseaseModelAssociation) WHERE lower(dma.disease_id) IN [" + idlistStrLower + "] WITH dma, "
                    : "MATCH (dma:DiseaseModelAssociation) WHERE lower(dma.disease_term) IN [" + idlistStrLower + "] WITH dma, ";
            cyphers += getRelationsAndColVals("DiseaseModelAssociation", jLabelFieldsParam);
        }

        System.out.println("CYPHER: " + cyphers);

        return cyphers;
    }

    public String getRelationsAndColVals(String queryLabel, JSONObject jLabelFieldsParam) {

        List<String> relations = new ArrayList<>();
        List<String> colNames = new ArrayList<>();

        Iterator it = jLabelFieldsParam.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String thisLbl = pair.getKey().toString();
            List<String> fllist = (List<String>) pair.getValue();


            if (queryLabel.equals("Gene") || queryLabel.equals("EnsemblGeneId") || queryLabel.equals("HumanGeneSymbol")) {
                if (thisLbl.equals("Gene")) {
                    for (String fl : fllist) {
                        if (fl.equals("ensembl_gene_id")) {
                            if (queryLabel.equals("Gene") || queryLabel.equals("HumanGeneSymbol")) {
                                relations.add("[(g)-[:IS_ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg.ensembl_gene_id] as ensemble_gene_ids");
                                colNames.add("ensemble_gene_ids");
                            } else if (queryLabel.equals("EnsemblGeneId")) {
                                colNames.add("ensg.ensembl_gene_id");
                            }
                        } else if (fl.equals("marker_synonym")) {
                            relations.add("[(g)-[:HAS_MARKER_SYNONYM]->(ms:MarkerSynonym) | ms.marker_synonym] as marker_synonyms");
                            colNames.add("marker_synonyms");
                        } else if (fl.equals("human_gene_symbol")) {
                            relations.add("[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs.human_gene_symbol] as human_othologs");
                            colNames.add("human_othologs");
                        } else {
                            colNames.add("g." + fl);
                        }
                    }
                }
                else if (thisLbl.equals("Allele")) {
                    relations.add("[(g)-[:HAS_ALLELE]->(a:Allele) | a {" + composeCols(fllist) + "}] as alleles");
                    colNames.add("alleles");
                }
                else if (thisLbl.equals("Phenotype")) {
                    relations.add(" [(g)-[:HAS_PHENOTYPE]->(p:Phenotype) | p {" + composeCols(fllist) + "}] as phenotypes");
                    colNames.add("phenotypes");
                }
                else if (thisLbl.equals("HumanGeneSymbol")) {
                    if (queryLabel.equals("HumanGeneSymbol")){
                        for (String fl : fllist) {
                            colNames.add("hgs." + fl);
                        }
                    }
                    else {
                        relations.add("[(g)-[:HUMAN_GENE_SYMBOL]->(hgncgs:HumanGeneSymbol) | hgncgs {" + composeCols(fllist) + "}] as HGNC_genes");
                        colNames.add("HGNC_genes");
                    }
                }
                else if (thisLbl.equals("Hp")) {
                    relations.add("[(g)-[:HAS_DISEASE]->(:DiseaseModelAssociation)-[:HAS_HP]->(hp:Hp) | hp {" + composeCols(fllist) + "}] as human_phenotypes");
                    colNames.add("human_phenotypes");
                }
                else if (thisLbl.equals("DiseaseModelAssociation")) {
                    relations.add("[(g)-[:HAS_DISEASE]->(dma:DiseaseModelAssociation) | dma {" + composeCols(fllist) + "}] as diseases");
                    colNames.add("diseases");
                }
            }
            else if (queryLabel.equals("Phenotype")) {
                if (thisLbl.equals("Gene")) {
                    for (String fl : fllist) {
                        if (fl.equals("ensembl_gene_id")) {
                            relations.add("[(g)-[:IS_ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg.ensembl_gene_id] as ensemble_gene_ids");
                            colNames.add("ensemble_gene_ids");
                        } else if (fl.equals("marker_synonym")) {
                            relations.add("[(g)-[:HAS_MARKER_SYNONYM]->(ms:MarkerSynonym) | ms.marker_synonym] as marker_synonyms");
                            colNames.add("marker_synonyms");
                        } else if (fl.equals("human_gene_symbol")) {
                            relations.add("[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs.human_gene_symbol] as human_orthologs");
                            colNames.add("human_orthologs");
                        } else {
                            colNames.add("g." + fl);
                        }
                    }
                }
                else if (thisLbl.equals("Allele")) {
                    relations.add("[(g)-[:HAS_ALLELE]->(a:Allele) | a {" + composeCols(fllist) + "}] as alleles");
                    colNames.add("alleles");
                }
                else if (thisLbl.equals("Phenotype")) {
                    for (String fl : fllist) {
                        colNames.add("p." + fl);
                    }
                }
                else if (thisLbl.equals("HumanGeneSymbol")) {
                    relations.add("[(g)-[:HUMAN_GENE_SYMBOL]->(hgncgs:HumanGeneSymbol) | hgncgs {" + composeCols(fllist) + "}] as HGNC_genes");
                    colNames.add("HGNC_genes");
                }
                else if (thisLbl.equals("Hp")) {
                    relations.add("[(p)-[:HAS_DISEASE]->(:DiseaseModelAssociation)-[:HAS_HP]->(hp:Hp) | hp {" + composeCols(fllist) + "}] as human_phenotypes");
                    colNames.add("human_phenotypes");
                }
                else if (thisLbl.equals("DiseaseModelAssociation")) {
                    relations.add("[(p)-[:HAS_DISEASE]->(dma:DiseaseModelAssociation) | dma {" + composeCols(fllist) + "}] as diseases");
                    colNames.add("diseases");
                }
            }
            else if (queryLabel.equals("Hp")) {
                if (thisLbl.equals("Gene")) {

                    List<String> gcols = new ArrayList<>();
                    for (String fl : fllist) {
                        if (fl.equals("ensembl_gene_id")) {
                            relations.add("[(hp)-[:HAS_DISEASE]->(dma:DiseaseModelAssociation)-[:OF_GENE]->(g)-[:IS_ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg.ensembl_gene_id] as ensembl_gene_ids");
                            colNames.add("ensembl_gene_ids");
                        } else if (fl.equals("marker_synonym")) {
                            relations.add("[(hp)-[:HAS_DISEASE]->(dma:DiseaseModelAssociation)-[:OF_GENE]->(g)-[:HAS_MARKER_SYNONYM]->(ms:MarkerSynonym) | ms.marker_synonym] as marker_synonyms");
                            colNames.add("marker_synonyms");
                        } else if (fl.equals("human_gene_symbol")) {
                            relations.add("[(hp)-[:HAS_DISEASE]->(dma:DiseaseModelAssociation)-[:OF_GENE]->(g)-[:HumanGeneSymbol]->(hgs:HumanGeneSymbol) | hgs.human_gene_symbol] as human_orthologs");
                            colNames.add("human_orthologs");
                        } else {
                            gcols.add(fl);
                        }
                    }
                    relations.add("[(hp)-[:HAS_DISEASE]->(dma:DiseaseModelAssociation)-[:OF_GENE]->(g:Genes) | g {" + composeCols(gcols) + "}] as genes");
                    colNames.add("genes");
                }
                else if (thisLbl.equals("Allele")) {
                    relations.add("[(hp)-[:HAS_DISEASE]->(dma:DiseaseModelAssociation)-[:OF_GENE]->(g:Genes)-[:HAS_ALLELE]->(a:Alleles) | a {.allele_id}] as alleles");
                    colNames.add("alleles");
                }
                else if (thisLbl.equals("Phenotype")) {
                    relations.add("[(hp)-[:HAS_MP]->(p:Phenotype) | p {" + composeCols(fllist) + "}] as phenotypes");
                    colNames.add("phenotypes");
                }
                else if (thisLbl.equals("HumanGeneSymbol")) {
                    relations.add("[(hp)-[:HAS_DISEASE]->(dma:DiseaseModelAssociation)-[:OF_GENE]->(g)-[:HumanGeneSymbol]->(hgnc:HumanGeneSymbol) | hgnc {" + composeCols(fllist) + "}] as hgnc_genes");
                    colNames.add("hgnc_genes");
                }
                else if (thisLbl.equals("Hp")) {
                    for(String fl : fllist) {
                        colNames.add("hp." + fl);
                    }
                }
                else if (thisLbl.equals("DiseaseModelAssociation")) {
                    relations.add("[(hp)-[:HAS_DISEASE]->(dma:DiseaseModelAssociation) | dma {" + composeCols(fllist) + "}] as diseases");
                    colNames.add("diseases");
                }
            }
            else if (queryLabel.equals("DiseaseModelAssociation")) {
                if (thisLbl.equals("Gene")) {
                    List<String> gcols = new ArrayList<>();
                    for (String fl : fllist) {
                        if (fl.equals("ensembl_gene_id")) {
                            relations.add("[(dma)-[:OF_GENE]->(g)-[:IS_ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg.ensembl_gene_id] as ensembl_gene_ids");
                            colNames.add("collect(distinct ensemble_gene_ids)");
                        } else if (fl.equals("marker_synonym")) {
                            relations.add("[(dma)-[:OF_GENE]->(g)-[:HAS_MARKER_SYNONYM]->(ms:MarkerSynonym) | ms.marker_synonym] as marker_synonyms");
                            colNames.add("collect(distinct marker_synonyms)");
                        } else if (fl.equals("human_gene_symbol")) {
                            relations.add("[(dma)-[:OF_GENE]->(g:Genes)-[:HumanGeneSymbol]->(hgs:HumanGeneSymbol) | hgs.human_gene_symbol] as human_orthologs");
                            colNames.add("collect(distinct human_orthologs)");
                        } else {
                            gcols.add(fl);
                        }
                    }
                    relations.add("[(dma)-[:OF_GENE]->(g:Genes) | g {" + composeCols(gcols) + "}] as genes");
                    colNames.add("collect(distinct genes)");
                }
                else if (thisLbl.equals("Allele")) {
                    relations.add("[(dma)-:OF_GENE]->(g:Genes)-[:HAS_ALLELE]->(a:Alleles) | a {.allele_id}] as alleles");
                    colNames.add("collect(distinct alleles)");
                }
                else if (thisLbl.equals("Phenotype")) {
                    relations.add("[(dma)-[:HAS_MP]->(p:Phenotypes) | p {" + composeCols(fllist) + "}] as phenotypes");
                    colNames.add("collect(distinct phenotypes)");
                }
                else if (thisLbl.equals("HumanGeneSymbol")) {
                    relations.add("[(dma)-[:OF_GENE]->(g:Genes)-[:HumanGeneSymbol]->(hgnc:HumanGeneSymbol) | hgnc {" + composeCols(fllist) + "}] as hgnc_genes");
                    colNames.add("collect(distinct hgnc_genes)");
                }
                else if (thisLbl.equals("Hp")) {
                    relations.add("[(dma)-[:HAS_HP]->(hp) | hp {" + composeCols(fllist) + "}] as human_phenotypes");
                    colNames.add("collect(distinct human_phenotypes)");
                }
                else if (thisLbl.equals("DiseaseModelAssociation")) {
                    for(String fl : fllist) {
                        colNames.add("dma." + fl);
                    }
                }

            }

        }

        return StringUtils.join(relations, ", ") + " return distinct " + StringUtils.join(colNames, ", ");
    }

    private String composeCols(List<String> fllist) {

        List<String> cols = new ArrayList<>();
        for (String fl : fllist) {
            cols.add("." + fl);
        }
        return StringUtils.join(cols, ",");
    }

//	public List<String> getCheckedCols(JSONObject jLabelFieldsParam){
//
//		Map<String, String> labelShort = new HashMap<>();
//		labelShort.put("Gene", "g");
//		labelShort.put("Allele", "a");
//		labelShort.put("Phenotype", "p");
//		labelShort.put("Anatomy", "an");
//		labelShort.put("Hp", "h");
//		labelShort.put("MouseModel", "mm");
//		labelShort.put("DiseaseModelAssociation", "dma");
//		labelShort.put("DiseaseGeneSummary", "dgs");
//
//		List<String> cypher = new ArrayList<>();
//
//		Iterator<String> keys = jLabelFieldsParam.keys();
//		while(keys.hasNext()){
//			String key = keys.next();
//			String val = null;
//			try{
//				JSONArray fields = jLabelFieldsParam.getJSONArray(key);
//				for (int i=0; i<fields.size(); i++) {
//					cypher.add(labelShort.get(key) + "." + fields.get(i).toString());
//				}
//			}catch(Exception e){
//				System.out.println("Error: " + e.getMessage());
//			}
//		}
//
//		return cypher;
//	}

    public String fetchCypherResult(String cypherQry, String idlistLower, JSONObject jLabelFieldsParam) {
        System.out.println("dbpath: " + neo4jDbPath);

        return null;
//		File dbpath = new File(neo4jDbPath);
//		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbpath);
//		System.out.println("db: "+ graphDb);
//
//
//		try ( Session session = driver.session() )
//		{
//
//			try ( Transaction tx = session.beginTransaction() )
//			{
//				tx.run(cypherQry);
//				tx.success();
//			}
//
//			try ( Transaction tx = session.beginTransaction() )
//			{
//				StatementResult result = tx.run( "MATCH (a:Person) WHERE a.name = {name} " +
//								"RETURN a.name AS name, a.title AS title",
//						parameters( "name", "Arthur" ) );
//				while ( result.hasNext() )
//				{
//					Record record = result.next();
//					System.out.println( String.format( "%s %s", record.get( "title" ).asString(), record.get( "name" ).asString() ) );
//				}
//			}
//
//
//
//
//
//			JSONObject j = new JSONObject();
//		j.put("aaData", new Object[0]);;
//		int totalDocs = 10; //default for showing how result looks like
//
//		j.put("iTotalRecords", totalDocs);
//		j.put("iTotalDisplayRecords", totalDocs);
//
//		String NA = "not available";
//
//		Transaction tx = graphDb.beginTx();
//		System.out.println("query starts....");
//
//		Map<String, List<String>> valRow = new HashMap<>();
//
//		try {
//			Result result = graphDb.execute(cypherQry);
//			System.out.println("query ends....");
//			//String resultStr = result.resultAsString();
//			//System.out.println("result str: " + resultStr);
//
//			while ( result.hasNext() ){
//
//				Map<String, Object> row = result.next();
//				System.out.println("Do result row..." + row.toString());
//				List<String> rowData = new ArrayList<String>();
//				String sortVal = null;
//
//				for ( String key : result.columns() ){
//					key = key.replaceAll("^\\w*\\.","");
//					System.out.println("key:" + key );
//					System.out.println("Colname: " + colname);
//
//					if (key.equals(colname)){
//						sortVal = "\""+ row.get(key).toString().toLowerCase() + "\"";
//						System.out.println("SortVal: "+sortVal);
//					}
//
//					if (row.get(key) == null){
//						rowData.add(NA);
//						//System.out.println("0 - " + key + " - " + NA);
//					}
//					else if ( row.get( key ) instanceof Object[]) {
//						Set<String> keyVals = new HashSet<>();
//						for (Object val : Arrays.asList((Object[])row.get(key))) {
//							String valstr = null;
//							if ( val== null){
//								valstr = NA;
//							}
//							else {
//								valstr = val.toString();
//							}
//
//							if (key.equals("ensembl_gene_id")){
//								if (valstr.startsWith("ENSMUSG")){
//									keyVals.add(valstr);
//								}
//							}
//							else {
//								keyVals.add(valstr);
//							}
//						}
//
//						if (keyVals.size() == 0){
//							keyVals.add(NA);
//						}
//						rowData.add(StringUtils.join(keyVals, "|<br>"));
//						//System.out.println("1 - " + key + " - " + StringUtils.join(keyVals, "|"));
//					}
//					else {
//						String thisVal = row.get(key).toString();
//
//						if (thisVal.startsWith("[") && thisVal.endsWith("]")) {
//							thisVal = thisVal.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(", ", "|<br>");
//							if (thisVal.isEmpty()) {
//								thisVal = NA;
//							}
//						}
//
//						//System.out.printf("2 - %s = %s%n", key, thisVal);
//						rowData.add(thisVal);
//
//					}
//
//					valRow.put(sortVal, rowData);
//					System.out.println(sortVal + " ---> " + rowData.toString());
//				}
//				//j.getJSONArray("aaData").add(rowData);
//			}
//
//			// output rows in query order
//			for (String q : Arrays.asList(StringUtils.split(idlist, ","))){
//				System.out.println("Got qry term: " + q);
//				j.getJSONArray("aaData").add(valRow.get(q.toLowerCase()));
//			}
//
//			System.out.println("");
//			tx.success();
//
//		}
//		catch(Exception e){
//			System.out.println("Error: "+ e.getMessage());
//		}
//		finally {
//			tx.close();
//		}
//		graphDb.shutdown();
//		return j.toString();
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
    public
    @ResponseBody
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
