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

package org.mousephenotype.cda.solr.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

@Service
public class SolrIndex {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@NotNull @Autowired
	SolrClient mpCore;

	@NotNull @Autowired
	SolrClient geneCore;

	@NotNull @Autowired
	SolrClient phenodigmCore;

	@NotNull @Autowired
	SolrClient anatomyCore;

	@NotNull @Autowired
	SolrClient impcImagesCore;

	@NotNull @Autowired
	SolrClient allele2Core;

	private List<String> phenoStatuses = new ArrayList<String>();

	private Object Json;


	public SolrClient getSolrServer(String corename){

		switch (corename){
			case "gene" : return geneCore;
			case "mp" : return mpCore;
			case "disease" : return phenodigmCore;  // search URL still uses disease in the controller path but queries against the phenodigm core
			case "anatomy" : return anatomyCore;
			case "impc_images" : return impcImagesCore;
			case "allele2" : return allele2Core;
		}
		return geneCore;
	}

	public List<String> fetchQueryIdsFromChrRange(String chr, String chrStart, String chrEnd, String mode) throws IOException, SolrServerException {
		List<String> queryIds = new ArrayList<>();

		SolrClient server = null;
		server = getSolrServer("gene");

		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		query.setFilterQueries("seq_region_id:" + chr
				+ " AND seq_region_start:["
				+ chrStart + " TO " + chrEnd + "]"
				+ " AND seq_region_end:["
				+ chrStart + " TO " + chrEnd + "]");
		query.setFields("mgi_accession_id");

		System.out.println("Query: " + query);
		QueryResponse response = null;
		if ( !mode.equals("export")) {
			query.setRows(10); // default, display only a max of 10 records on batchQuery page
			response = server.query(query, METHOD.POST);
		}
		else {
			query.setRows(0);
			response = server.query(query, METHOD.POST);
			int rows = (int) response.getResults().getNumFound();  // need to figure out how many docs found for full export
			query.setRows(rows); // default
			response = server.query(query, METHOD.POST);
		}

		System.out.println("Found " + response.getResults().getNumFound() + " gene(s) in range");
		for (SolrDocument doc : response.getResults() ){
			queryIds.add(doc.get("mgi_accession_id").toString());
		}
		return queryIds;
	}

    public static Map<Integer, String> getGomapCategory(){

    	Map<Integer, String> evidRank = new HashMap<>();

		evidRank.put(1, "No biological data available");
		evidRank.put(2, "Other");
		evidRank.put(3, "Automated electronic");
		evidRank.put(4, "Curated computational");
		evidRank.put(5, "Experimental");

		return evidRank;
    }

    public static Map<String, String> coreIdQMap(){

    	Map<String, String> map = new HashMap<>();

		map.put("gene", "mgi_accession_id");
		map.put("ensembl", "ensembl_gene_id");
		map.put("mp", "mp_id");
		map.put("anatomy", "anatomy_id");
		map.put("disease", "disease_id");
		map.put("hp", "hp_id");
		//map.put("phenodigm", "hp_id");
		map.put("impc_images", "gene_accession_id");

		return map;
    }

	/**
	 * Return the number of documents found for a specified solr query on a
	 * specified core.
	 *
	 * @param query
	 *            the query
	 * @param core
	 *            which solr core to query
	 * @param mode
	 *            which configuration mode to operate in
	 * @param solrParams
	 *            the default solr parameters to also restrict the query
	 * @return integer count of the number of matching documents
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Integer getNumFound(String query, String core, String mode,
			String solrParams) throws IOException, URISyntaxException, JSONException {
		JSONObject json = getResults(composeSolrUrl(core, mode, query,
				solrParams, 0, 0, false));
		return json.getJSONObject("response").getInt("numFound");
	}

	/**
	 * Gets the json string representation for the query.
	 *
	 * @param query
	 *            the query for which documents
	 * @param core
	 *            the solr core to query
	 * @param gridSolrParams
	 *            the default solr parameters to append to the query
	 * @param mode
	 *            the configuration mode to operate in
	 * @param start
	 *            where to start the offset
	 * @param length
	 *            how many documents to return
	 * @param showImgView
	 *            is this query showing the annotation view of the images
	 *            (true/false)
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getQueryJson(String query, String core,
			String gridSolrParams, String mode, int start, int length,
			boolean showImgView) throws IOException, URISyntaxException {

		if (gridSolrParams.equals("")) {
			gridSolrParams = "qf=auto_suggest&defType=edismax&wt=json&q=*:*";
		}

		return getResults(composeSolrUrl(core, mode, query, gridSolrParams,
				start, length, showImgView));
	}

	public QueryResponse getBatchQueryJson(String idlist, String fllist, String dataTypeName) throws SolrServerException, IOException {

		SolrClient server = null;

		Map<String, String> coreIdQMap = coreIdQMap();
		String qField = coreIdQMap.get(dataTypeName);

		if ( dataTypeName.equals("disease") ){
			server = getSolrServer("disease"); // points to new phenodigm
		}
		if ( dataTypeName.equals("hp") ){
			server = getSolrServer("mp");
		}
		else if ( dataTypeName.equals("ensembl") || dataTypeName.contains("marker_symbol")){
			server = getSolrServer("gene");
			fllist += ",datasets_raw_data";
		}
		else {
			server = getSolrServer(dataTypeName);
		}

		String[] idList = StringUtils.split(idlist, ",");
		String querystr = null;

		// search keyword by mouse symbol: check 2 fields in gene core (marker_symbol_lowercase, marker_synonym_lowercase)
		if (dataTypeName.equals("mouse_marker_symbol")){
			querystr = "marker_symbol_lowercase:(" + StringUtils.join(idList, " OR ") + ")"
					+ " OR marker_synonym_lowercase:(" + StringUtils.join(idList, " OR ") + ")";
		}
		// search keyword by human symbol: check 2 fields in gene core (human_gene_symbol_lowercase, human_symbol_synonym_lowercase)
		else if (dataTypeName.equals("human_marker_symbol")){
			querystr = "human_gene_symbol_lowercase:(" + StringUtils.join(idList, " OR ") + ")"
					+ " OR human_symbol_synonym_lowercase:(" + StringUtils.join(idList, " OR ") + ")";
		}
		else {
			querystr = qField + ":(" + StringUtils.join(idList, " OR ") + ")";
		}

		//System.out.println("BatchQuery: " + querystr);
		SolrQuery query = new SolrQuery();
		query.setQuery(querystr);

		if ( dataTypeName.equals("disease") ){ // points to phenodigm
			//query.setFilterQueries("type:hp_mp");
			query.setFilterQueries("type:disease_gene_summary");
		}

		query.setStart(0);

		query.setRows(0);

		QueryResponse response = server.query(query, METHOD.POST);
		long rowCount = response.getResults().getNumFound(); // so that we know how many rows is returned

//		System.out.println("row count: "+rowCount);
		//query.setRows(idList.length);  // default
		query.setRows((int) rowCount);

		// retrieves wanted fields
		query.setFields(fllist);
		System.out.println("BATCHQUERY " + dataTypeName + " : " + query);

		QueryResponse response2 = server.query(query, METHOD.POST);
		//System.out.println("response: "+ response2);

		return response2;
	}

	/**
	 * Get rows for saving to an external file.
	 *
	 * @param core
	 *            the solr core to query
	 * @param gridSolrParams
	 *            the default parameters to use in the query
	 * @param start
	 *            where to start the query
	 * @param gridFields
	 *            the default solr parameters to append to the query
	 * @param length
	 *            how many documents to return
	 * @return json representation of the results of the solr query
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getDataTableExportRows(String core,
			String gridSolrParams, String gridFields, int start, int length, boolean showImgView)
			throws IOException, URISyntaxException {

		//System.out.println("GRID SOLR PARAMS : " + gridSolrParams);

		//if (core.equals("gene")) {
			//gridFields += ",imits_report_phenotyping_complete_date,imits_report_genotype_confirmed_date,imits_report_mi_plan_status,escell,ikmc_project,imits_phenotype_started,imits_phenotype_complete,imits_phenotype_status";
		//}

		//String url = composeSolrUrl(core, "", "", newgridSolrParams, start,
				//length, false);
		String url = composeSolrUrl(core, "", "", gridSolrParams, start,
				length, showImgView);

		System.out.println("Export data URL: " + url);
		return getResults(url);
	}

	public JSONObject getBqDataTableExportRows(String core, String gridFields, String idList)
			throws IOException, URISyntaxException {

		Map<String, String> idMap = coreIdQMap();
		String qField = idMap.get(core);
		if ( core.equals("hp") ){
			qField = "hp_id";
			core = "mp"; // use mp core to fetch for hp data
		}
		else if ( core.equals("ensembl") ){
			qField = "ensembl_gene_id";
			core = "gene"; // use gene core to fetch dataset via ensembl identifiers
		}

		String url = SolrUtils.getBaseURL(getSolrServer(core)) + "/select?";

		url += "q=" + qField + ":(" + idList + ")";
		url += "&start=0&rows=999999&wt=json&fl=" + gridFields;

		System.out.println("Export data URL: " + url);

		return getResults(url);
	}


	/**
	 * Prepare a url for querying the solr indexes based on the passed in
	 * arguments.
	 *
	 * @param core
	 *            which solr core to query
	 * @param mode
	 *            which configuration mode to operate in
	 * @param query
	 *            what to query
	 * @param gridSolrParams
	 *            default parameters to add to the solr query
	 * @param iDisplayStart
	 *            starting point of the query
	 * @param iDisplayLength
	 *            length of the query
	 * @param showImgView
	 *            which image mode to operate in
	 * @return the constructed url including all parameters
	 */
	private String composeSolrUrl(String core, String mode, String query,
			String gridSolrParams, Integer iDisplayStart,
			Integer iDisplayLength, boolean showImgView) {

		String url = SolrUtils.getBaseURL(getSolrServer(core)) + "/select?";

		//System.out.println(("SolrIndex BASEURL: " + url));

		if (mode.equals("mpPage")) {
			url += "q=" + query;
			url += "&start=0&rows=0&wt=json&qf=auto_suggest&defType=edismax";
		} else if (mode.equals("geneGrid")) {
			url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
					+ iDisplayLength;
			//System.out.println("GENE PARAMS: " + url);
		} else if (mode.equals("pipelineGrid")) {
			url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
					+ iDisplayLength;
//			System.out.println("PROTOCOL PARAMS: " + url);
		} else if (mode.equals("impc_imagesGrid")) {

			url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
					+ iDisplayLength;
			if (!showImgView) {
				url += "&facet=on&facet.field=symbol_gene&facet.field=procedure_name&facet.field=anatomy_id_term&facet.mincount=0&facet.limit=-1";
			}
		} else if (mode.equals("imagesGrid")) {
			url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
					+ iDisplayLength;
			if (!showImgView) {
				url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.limit=-1&facet.mincount=0";
			}
//			System.out.println("IMG PARAMS: " + url);
		} else if (mode.equals("mpGrid")) {
			url += gridSolrParams.replaceAll(" ", "%20") + "&start="
					+ iDisplayStart + "&rows=" + iDisplayLength;
//			System.out.println("MP PARAMS: " + url);
		} else if (mode.equals("anatomyGrid")) {
			url += gridSolrParams.replaceAll(" ", "%20") + "&start="
					+ iDisplayStart + "&rows=" + iDisplayLength;
//			System.out.println("ANATOMY PARAMS: " + url);
		} else if ( mode.equals("diseaseGrid") ){
			url += gridSolrParams.replaceAll(" ", "%20") + "&start="
					+ iDisplayStart + "&rows=" + iDisplayLength;
//			System.out.println("DISEASE PARAMS: " + url);
		}
		else if (mode.equals("ikmcAlleleGrid")) {
			url += "q=" + query;
			url += "&start=0&rows=0&wt=json";
//			System.out.println("IKMC ALLELE PARAMS: " + url);
		}
		else if ( mode.equals("allele2Grid")){
			url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
					+ iDisplayLength;
//			System.out.println("ALLELE2 PARAMS: " + url);
		}
		else if (mode.equals("all") || mode.equals("page") || mode.equals("")) { // download search page result
			url += gridSolrParams + "&start=" + iDisplayStart + "&rows=" + iDisplayLength;
			if (core.equals("images") && !showImgView) {
				url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
			}
			else if (core.equals("impc_images") && !showImgView) {
				url += "&facet=on&facet.field=symbol_gene&facet.field=procedure_name&facet.field=ma_id_term&facet.mincount=1&facet.limit=-1";
			}
//			System.out.println("GRID DUMP PARAMS - " + core + ": " + url);
		}
		// OTHER solrCoreNames to be added here
		//System.out.println("SolrIndex url: " +  url);
		return url;
	}

	/**
	 * Generates a map of label, field, link representing a facet field
	 *
	 * @param names
	 *            an array of strings representing the facet ID and the name of
	 *            the facet
	 *            the base url of the generated links
	 * @return a map represneting the facet, facet label and link
	 */
	public Map<String, String> renderFacetField(String[] names, String hostName, String baseUrl) {

		// key: display label, value: facetField
		Map<String, String> hm = new HashMap<String, String>();
		String name = names[0];
		String id = names[1];

		System.out.println("BASEURL in renderFacetField: "+ baseUrl);
		
		if (id.startsWith("MP:")) {
			String url = baseUrl + "/phenotypes/" + id;
			hm.put("label", "MP");
			hm.put("field", "annotationTermName");
			//hm.put("field", "mpTermName");
			hm.put("fullLink", hostName + url);
			hm.put("link", "<a href='" + url + "'>" + name + "</a>");
		} else if (id.startsWith("MA:")) {
			String url = baseUrl + "/anatomy/" + id;
			hm.put("label", "MA");
			hm.put("field", "annotationTermName");
			hm.put("fullLink", hostName + url);
			hm.put("link", "<a href='" + url + "'>" + name + "</a>");
		} else if (id.equals("exp")) {
			hm.put("label", "Procedure");
			hm.put("field", "expName");
			hm.put("link", name);
		} else if (id.startsWith("MGI:")) {
			
			String url = baseUrl + "/genes/" + id;
			hm.put("label", "Gene");
			hm.put("field", "symbol");
			hm.put("fullLink", hostName + url);
			hm.put("link", "<a href='" + url + "'>" + name + "</a>");
		}
		hm.put("id", id);
		return hm;
	}

	/**
	 * Merge all the facets together based on whether they include an underscore
	 * because underscore facet names means that the solr field name represents
	 * a facet and it's identifier, which are the ones we are concerned with.
	 *
	 * Each facet is formatted as an array where, starting at the zeroth
	 * element, it alternates between facet name and count for that facet
	 *
	 * @param facetFields
	 *            the json representation of all the facets as arrays
	 * @return a json array which combined all the passed in facets filtered for
	 *         inclusion of underscore
	 */
	public JSONArray mergeFacets(JSONObject facetFields) throws JSONException {

		JSONArray fields = new JSONArray();

		// Initialize a list on creation using an inner anonymous class
		List<String> facetNames = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("symbol_gene");
				add("expName_exp");
				add("mpTermName");
				add("maTermName");
			}
		};
		for (String facet : facetNames) {

			JSONArray arr = facetFields.getJSONArray(facet);
			for (int i = 0; i < arr.length(); i = i + 2) {

				if ( (Integer) arr.get(i + 1) > 0 ) {
					// We only want facet fields that contain an underscore
					// as it contains ID info we want
					if (((String) arr.get(i)).contains("_")) {
						fields.put(arr.get(i));
						fields.put(arr.get(i + 1));
					}
				}
			}
		}

		return fields;
	}

	public class AnnotNameValCount {
		public String name;
		public String id;
		public String fq;
		public String val;
		public String link;
		public String markerSynonym;
        public String paramAssociationName;
        public String relatedSynonym;
		public int imgCount;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFq() {
            return fq;
        }

        public void setFq(String fq) {
            this.fq = fq;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getMarkerSynonym() {
            return markerSynonym;
        }

        public void setMarkerSynonym(String markerSynonym) {
            this.markerSynonym = markerSynonym;
        }

        public String getParamAssociationName() {
            return paramAssociationName;
        }

        public void setParamAssociationName(String paramAssociationName) {
            this.paramAssociationName = paramAssociationName;
        }

        public String getRelatedSynonym() {
            return relatedSynonym;
        }

        public void setRelatedSynonym(String relatedSynonym) {
            this.relatedSynonym = relatedSynonym;
        }

        public int getImgCount() {
            return imgCount;
        }

        public void setImgCount(int imgCount) {
            this.imgCount = imgCount;
        }

        @Override
        public String toString() {
            return "AnnotNameValCount{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    ", fq='" + fq + '\'' +
                    ", val='" + val + '\'' +
                    ", link='" + link + '\'' +
                    ", markerSynonym='" + markerSynonym + '\'' +
                    ", paramAssociationName='" + paramAssociationName + '\'' +
                    ", relatedSynonym='" + relatedSynonym + '\'' +
                    ", imgCount=" + imgCount +
                    '}';
        }
    }

	public List<AnnotNameValCount> mergeImpcFacets(String query, JSONObject json, String baseUrl) throws JSONException {


	    query = query.replaceAll("\"", "");
		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		List<AnnotNameValCount> annots = new ArrayList<>();

		Map<String, String> hm = new HashMap<>();
		hm.put("symbol_gene", "Gene");
        hm.put("marker_synonym_symbol_gene", "Gene");
		hm.put("procedure_name", "Procedure");
		hm.put("anatomy_id_term", "Anatomy");
        hm.put("anatomy_term_synonym_anatomy_id_term", "Anatomy");
        hm.put("selected_top_level_anatomy_id_anatomy_id_term", "Anatomy");
        hm.put("selected_top_level_anatomy_term_anatomy_id_term", "Anatomy");
        hm.put("selected_top_level_anatomy_term_synonym_anatomy_id_term", "Anatomy");
        hm.put("intermediate_anatomy_id_anatomy_id_term", "Anatomy");
        hm.put("intermediate_anatomy_term_anatomy_id_term", "Anatomy");
        hm.put("intermediate_anatomy_term_synonym_anatomy_id_term", "Anatomy");

		Set<String> ffList = new HashSet<>();

		facetFields.keys()
				.forEachRemaining(key -> ffList.add(key.toString()));

        Set<String> seenGenes = new HashSet<>();
        Set<String> seenProcedures = new HashSet<>();
        Set<String> seenAnatomy = new HashSet<>();
        String separator = "___";

        for( String fieldName : ffList){
            try {
                JSONArray arr = facetFields.getJSONArray(fieldName);

                for (int i = 0; i < arr.length(); i = i + 2) {
					if (! arr.get(i+1).toString().equals("0")) {
						Object facetValue = arr.get(i);

						if (facetValue instanceof String) {
							String fv = facetValue.toString();

							if (fv.toLowerCase().contains(query.toLowerCase()) || query.equals("*:*") || query.equals("*")) {

								AnnotNameValCount annotNameValCount = new AnnotNameValCount();
								annotNameValCount.setImgCount(Integer.parseInt(arr.get(i + 1).toString()));
								annotNameValCount.setName(hm.get(fieldName));

								if (fieldName.equals("symbol_gene")) {
									String fqName = "gene_accession_id";
									annotNameValCount.setFq(fqName);
									String[] fields = fv.split("_");
									annotNameValCount.setVal(fields[0]);
									annotNameValCount.setId(fields[1]);
									annotNameValCount.setLink(baseUrl + "/genes/" + annotNameValCount.getId());
									seenGenes.add(annotNameValCount.getId());

								} else if (fieldName.equals("marker_synonym_symbol_gene")) {
									String fqName = "gene_accession_id";
									annotNameValCount.setFq(fqName);
									String[] fieldConcat = fv.split(separator);
									String markerSynonym = fieldConcat[0];
									String[] vals = fieldConcat[1].split("_");
									String markerSymbol = vals[0];
									String markerAcc = vals[1];
									if (seenGenes.contains(markerAcc)) {
										continue;
									} else {
										annotNameValCount.setVal(markerSymbol);
										annotNameValCount.setId(markerAcc);
										annotNameValCount.setLink(baseUrl + "/genes/" + annotNameValCount.getId());
										annotNameValCount.setMarkerSynonym(markerSynonym);
										seenGenes.add(annotNameValCount.getId());

									}
								} else if (fieldName.equals("procedure_name")) {
									annotNameValCount.setFq(fieldName);
									annotNameValCount.setVal(fv);
									seenProcedures.add(fv);

								} else if (fieldName.equals("parameter_association_name_procedure_name")) {
									annotNameValCount.setFq("procedure_name");
									String[] fieldConcat = fv.split(separator);
									String paramAssocName = fieldConcat[0];
									String procName = fieldConcat[1];

									if (seenProcedures.contains(paramAssocName)) {
										continue;
									} else {
										annotNameValCount.setVal(procName);
										annotNameValCount.setParamAssociationName(paramAssocName);
										seenProcedures.add(procName);

									}
								} else if (fieldName.equals("anatomy_id_term")) {
									String[] fields = fv.split("_");
									annotNameValCount.setFq("anatomy_id");
									annotNameValCount.setVal(fields[1]);
									annotNameValCount.setId(fields[0]);
									annotNameValCount.setLink(baseUrl + "/anatomy/" + annotNameValCount.getId());
									seenAnatomy.add(annotNameValCount.getId());

								} else if (fieldName.equals("anatomy_term_synonym_anatomy_id_term")
										|| fieldName.equals("selected_top_level_anatomy_id_anatomy_id_term")
										|| fieldName.equals("selected_top_level_anatomy_term_anatomy_id_term")
										|| fieldName.equals("selected_top_level_anatomy_term_synonym_anatomy_id_term")
										|| fieldName.equals("intermediate_anatomy_id_anatomy_id_term")
										|| fieldName.equals("intermediate_anatomy_term_anatomy_id_term")
										|| fieldName.equals("intermediate_anatomy_term_synonym_anatomy_id_term")
										) {
									annotNameValCount.setFq("anatomy_id");
									String[] fieldConcat = fv.split(separator);
									String relatedTerm = fieldConcat[0];
									String[] vals = fieldConcat[1].split("__");
									String anatomyId = vals[0];
									String anatomyTerm = vals[1];

									if (seenAnatomy.contains(anatomyId)) {
										continue;
									} else {
										annotNameValCount.setVal(anatomyTerm);
										annotNameValCount.setId(anatomyId);
										annotNameValCount.setLink(baseUrl + "/anatomy/" + annotNameValCount.getId());
										annotNameValCount.setRelatedSynonym(relatedTerm);
										seenAnatomy.add(annotNameValCount.getId());

									}
								}

								if (hm.containsKey(fieldName)) {
									annots.add(annotNameValCount);
                                	//System.out.println("ANNOT: "+annotNameValCount.toString());
								}
							}
						}
					}
                }
            }
            catch (Exception e){
				System.out.println("mergeImpcFacets warning: "+ e);
			}
        }


//		for (String facet : facetNames) {
//
//			//JSONObject arr = facetFields.getJSONArray(facet);
//			try {
//				if (json.getJSONObject("facet_counts").getJSONObject("facet_fields").containsKey(facet)) {
//					JSONArray arr = json.getJSONObject("facet_counts").getJSONObject("facet_fields").getJSONArray(facet);
//					for (int i = 0; i < arr.size(); i = i + 2) {
//
//
//						if ( (Integer) arr.get(i + 1) > 0) {
//							AnnotNameValCount annotNameValCount = new AnnotNameValCount();
//
//							annotNameValCount.name = hm.get(facet);
//							annotNameValCount.facet = facet;
//							annotNameValCount.val = arr.get(i).toString();
//
//							if (facet.equals("symbol_gene")) {
//								annotNameValCount.facet = "gene_symbol"; // query field name
//								String[] fields = annotNameValCount.val.split("_");
//								annotNameValCount.val = fields[0];
//								annotNameValCount.id = fields[1];
//								annotNameValCount.link = baseUrl + "/genes/" + fields[1];
//							} else if (facet.equals("anatomy_id_term")) {
//								annotNameValCount.facet = "anatomy_term"; // query field name
//								String[] fields = annotNameValCount.val.split("_");
//								annotNameValCount.val = fields[1];
//								annotNameValCount.id = fields[0];
//								annotNameValCount.link = baseUrl + "/anatomy/" + fields[0];
//							}
//							annotNameValCount.imgCount = Integer.parseInt(arr.get(i + 1).toString());
//
//							annots.add(annotNameValCount);
//						}
//					}
//				}
//			}
//			catch (Exception e){
//				System.out.println("Stack trace: "+ Arrays.toString(e.getStackTrace()));
//			}
//		}

		return annots;
	}
	/**
	 * Get the IMPC status for a gene identified by accession id.
	 *
	 * @param accession
	 *            the MGI id of the gene in question
	 * @return the status
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String getGeneStatus(String accession) throws IOException,
			URISyntaxException, JSONException {

		String url = SolrUtils.getBaseURL(getSolrServer("gene"))
				+ "/select?wt=json&q=mgi_accession_id:"
				+ accession.replace(":", "\\:");

		log.info("url for geneDao=" + url);

		JSONObject json = getResults(url);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

		if (docs.length() > 1) {
			log.error("Error, Only expecting 1 document from an accession/gene request");
		}

		String geneStatus = docs.getJSONObject(0).getString("status");

		log.debug("gene status=" + geneStatus);

		return geneStatus;
	}

/*	public List<Map<String, String>> getGenesWithPhenotypeStartedFromAll()
			throws IOException, URISyntaxException {
		List<Map<String, String>> geneStatuses = new ArrayList<Map<String, String>>();
		String url = internalSolrUrl
				+ "/gene/select?wt=json&q=*%3A*&version=2.2&start=0&rows=100";// 2147483647";//max
																				// size
																				// of
																				// int
																				// to
																				// make
																				// sure
																				// we
																				// get
																				// back
																				// all
																				// the
																				// rows
																				// in
																				// index

		log.info("url for geneDao=" + url);

		JSONObject json = getResults(url);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		for (Object doc : docs) {
			JSONObject jsonObject = (JSONObject) doc;
			String geneStatus = this.deriveLatestPhenotypingStatus(jsonObject);
			if (geneStatus.equals("Started")) {
				String mgi = jsonObject.getString("mgi_accession_id");
				String symbol = jsonObject.getString("marker_symbol");
				Map map = new HashMap<String, String>();
				map.put("mgi", mgi);
				map.put("symbol", symbol);
				geneStatuses.add(map);
			}
		}
		return geneStatuses;
	}*/

	/**
	 * Get the results of a query from the provided url.
	 *
	 * @param url
	 *            the URL from which to get the content
	 * @return a JSONObject representing the result of the query
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getResults(String url) throws IOException,
			URISyntaxException {

		log.debug("GETTING CONTENT FROM: " + url);
		HttpProxy proxy = new HttpProxy();

		try {
			String content = proxy.getContent(new URL(url));
			return new JSONObject(content);
		}
		catch (Exception e) {
			e.printStackTrace();
        }
		return null;
	}

	public Map<String, Map<String, Map<String, JSONArray>>> getGO2ImpcGeneAnnotationStats() throws IOException, URISyntaxException, JSONException {
		String internalBaseSolrUrl = SolrUtils.getBaseURL(getSolrServer("gene")) + "/select?";

		Map<String, Map<String, Map<String, JSONArray>>> statusEvidCount = new LinkedHashMap<>();

		phenoStatuses.add("Phenotyping Complete");
		phenoStatuses.add("Phenotyping Started");

		// all queries here take into account of having MP calls
		for ( String status : phenoStatuses ){
			String phenoParams = "q=latest_phenotype_status:\"" + status + "\"&wt=json&fq=mp_id:*&rows=0";

			//String allGoParams = "q=latest_phenotype_status:\"" + status + "\"&wt=json&fq=mp_id:* AND go_term_id:*&rows=0";

			// either molecular_function or biological_process
			//String goParamsFP = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND (go_term_domain:\"biological_process\" OR go_term_domain:\"molecular_function\")&wt=json&rows=0&fq=mp_id:*&facet=on&facet.limit=-1&facet.field=go_term_evid";
			String goParamsFP = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND (go_term_domain:\"biological_process\" AND go_term_domain:\"molecular_function\")&wt=json&rows=0&fq=mp_id:*&facet=on&facet.mincount=1&facet.limit=-1&facet.field=evidCodeRank";

			// only molecular_function
			//String goParamsF = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND go_term_domain:\"molecular_function\"&wt=json&rows=0&fq=mp_id:*&facet=on&facet.limit=-1&facet.field=go_term_evid";
			String goParamsF = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND go_term_domain:\"molecular_function\"&wt=json&rows=0&fq=mp_id:*&facet=on&facet.mincount=1&facet.limit=-1&facet.field=evidCodeRank";

			// only biological_process
			//String goParamsP = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND go_term_domain:\"biological_process\"&wt=json&rows=0&fq=mp_id:*&facet=on&facet.limit=-1&facet.field=go_term_evid";
			String goParamsP = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND go_term_domain:\"biological_process\"&wt=json&rows=0&fq=mp_id:*&facet=on&facet.mincount=1&facet.limit=-1&facet.field=evidCodeRank";


			Map<String, String> goQueries = new LinkedHashMap<>();
			goQueries.put("FP", internalBaseSolrUrl + goParamsFP);
			goQueries.put("F",  internalBaseSolrUrl + goParamsF);
			goQueries.put("P",  internalBaseSolrUrl + goParamsP);

			Map<String, String> phenoQueries = new LinkedHashMap<>();
			phenoQueries.put(status,  internalBaseSolrUrl + phenoParams);

			String noGoParams = "q=latest_phenotype_status:\"" + status + "\" AND -go_term_id:*&fq=mp_id:*&wt=json&rows=0";
			Map<String, String> noGoQueries = new LinkedHashMap<>();
			noGoQueries.put("none", internalBaseSolrUrl + noGoParams);

			Map<String, Map<String, String>> annotUrls = new LinkedHashMap<>();
			String noGo  = "w/o GO";
			String hasGo = "w/  GO";
			String allPheno = "allPheno";

			annotUrls.put(noGo, noGoQueries);
			annotUrls.put(hasGo, goQueries);
			annotUrls.put(allPheno, phenoQueries);

			Map<String, Map<String, JSONArray>> annotCounts = new LinkedHashMap<>();

			Iterator it = annotUrls.entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        String annot = pairs.getKey().toString();

		        Map<String, String> queries = (Map<String, String>) pairs.getValue();
		        it.remove(); // avoids a ConcurrentModificationException

		        Map<String, JSONArray> jlist = new LinkedHashMap<>();
		        Iterator itq = queries.entrySet().iterator();

		        while (itq.hasNext()) {
			        Map.Entry pairs2 = (Map.Entry)itq.next();
			        String domain = pairs2.getKey().toString();
			        String query = pairs2.getValue().toString();
			        itq.remove(); // avoids a ConcurrentModificationException

			        JSONObject json = getResults(query);
			        //System.out.println("DOMAIN: " + domain);
			        //System.out.println(annot + " QUERY: " + query);

			        if ( annot.equals(hasGo) ){
			        	JSONArray jfacet = json.getJSONObject("facet_counts").getJSONObject("facet_fields").getJSONArray("evidCodeRank");
			        	jlist.put(domain, jfacet);
			        	annotCounts.put(annot, jlist);
			        }
			        else if ( annot.equals(noGo) || annot.equals(allPheno) )  {
			        	int numFound = json.getJSONObject("response").getInt("numFound");
			        	JSONArray ja = new JSONArray();
			        	ja.put(numFound);
			        	jlist.put(domain, ja);
			        	annotCounts.put(annot, jlist);
			        }
		        }
			}

			statusEvidCount.put(status, annotCounts);

		}

		return statusEvidCount;
	}
}