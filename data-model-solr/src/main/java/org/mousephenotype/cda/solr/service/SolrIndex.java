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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

@Service
public class SolrIndex {

	public static final String IMG_NOT_FOUND = "No information available";

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());
	
	@NotNull
	@Value("${pdfThumbnailUrl}")
	private String pdfThumbnailUrl;

	@NotNull
	@Value("${impcMediaBaseUrl}")
	private String impcMediaBaseUrl;

	@Autowired
	@Qualifier("autosuggestCore")
	HttpSolrClient autosuggestCore;

	@Autowired
	@Qualifier("mpCore")
	HttpSolrClient mpCore;

	@Autowired
	@Qualifier("geneCore")
	HttpSolrClient geneCore;

	@Autowired
	@Qualifier("diseaseCore")
	HttpSolrClient diseaseCore;

	@Autowired
	@Qualifier("anatomyCore")
	HttpSolrClient anatomyCore;

	@Autowired
	@Qualifier("impcImagesCore")
	HttpSolrClient impcImagesCore;

	@Autowired
	@Qualifier("allele2Core")
	HttpSolrClient allele2Core;

	private List<String> phenoStatuses = new ArrayList<String>();

	private Object Json;


	public SolrClient getSolrServer(String corename){

		switch (corename){
			case "autosuggest" : return autosuggestCore;
			case "gene" : return geneCore;
			case "mp" : return mpCore;
			case "disease" : return diseaseCore;
			case "anatomy" : return anatomyCore;
			case "impc_images" : return impcImagesCore;
			case "allele2" : return allele2Core;
		}
		return geneCore;
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
		map.put("anatomy", "id");
		map.put("disease", "disease_id");
		map.put("hp", "hp_id");
		map.put("phenodigm", "hp_id");
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
			String solrParams) throws IOException, URISyntaxException {
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

		if ( dataTypeName.equals("phenodigm") ){
			//server = getSolrServer("disease");
		}
		else if ( dataTypeName.equals("hp") ){

			server = getSolrServer("mp");
		}
		else if ( dataTypeName.equals("ensembl") || dataTypeName.equals("marker_symbol")){

			server = getSolrServer("gene");
		}
		else {
			server = getSolrServer(dataTypeName);
		}
		//System.out.println("solrurl: " + server.toString());

		String[] idList = StringUtils.split(idlist, ",");
		String querystr = null;
		
		if (dataTypeName.equals("marker_symbol")){
			querystr = "marker_symbol_lowercase:(" + StringUtils.join(idList, " OR ") + ")"
					+ " OR marker_synonym_lowercase:(" + StringUtils.join(idList, " OR ") + ")";
		}
		else {
			querystr = qField + ":(" + StringUtils.join(idList, " OR ") + ")";
		}
		//System.out.println("queryStr: " + querystr);

		SolrQuery query = new SolrQuery();
		query.setQuery(querystr);

		if ( dataTypeName.equals("phenodigm") ){
			query.setFilterQueries("type:hp_mp");
		}

		query.setStart(0);
		query.setRows(idList.length);  // default

		// retrieves wanted fields
		query.setFields(fllist);
		//System.out.println("QUERY: " + query);
		QueryResponse response = server.query(query, METHOD.POST);
		//System.out.println("response: "+ response);

		return response;
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

		if (core.equals("gene")) {
			//gridFields += ",imits_report_phenotyping_complete_date,imits_report_genotype_confirmed_date,imits_report_mi_plan_status,escell,ikmc_project,imits_phenotype_started,imits_phenotype_complete,imits_phenotype_status";
		}

		//String url = composeSolrUrl(core, "", "", newgridSolrParams, start,
				//length, false);
		String url = composeSolrUrl(core, "", "", gridSolrParams, start,
				length, showImgView);

		log.debug("Export data URL: " + url);
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
		else if (mode.equals("gene2go")) {
			url += gridSolrParams.replaceAll(" ", "%20") + "&start="
					+ iDisplayStart + "&rows=" + iDisplayLength;
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

	public List fetchImpcImagePathByAnnotName(String query, String fqStr) throws IOException, URISyntaxException {

    	List pathAndCount = new ArrayList<>();
		//String mediaBaseUrl = config.get("mediaBaseUrl");
        final int maxNum = 4; // max num of images to display in grid column

		String qryBaseUrl = SolrUtils.getBaseURL(getSolrServer("impc_images")) + "/select?qf=imgQf&defType=edismax&wt=json&q=" + query
				+ "&fq=" + fqStr + "&rows=";

        String queryUrl = qryBaseUrl + maxNum;
        String queryUrlCount = qryBaseUrl + "0";

		//System.out.println("SolrIndex: " + queryUrl);
		List<String> imgs = new ArrayList<String>();
		//List<String> imgPath = new ArrayList<String>();

        JSONObject imgCountJson = getResults(queryUrlCount);
        JSONObject thumbnailJson = getResults(queryUrl);

        Integer imgCount = imgCountJson.getJSONObject("response").getInt("numFound");
        JSONArray docs = thumbnailJson.getJSONObject("response").getJSONArray("docs");

        int dataLen = docs.size() < 5 ? docs.size() : maxNum;

        for (int i = 0; i < dataLen; i ++) {
            JSONObject doc = docs.getJSONObject(i);

            //String link = null;
			String img = null;

            if (doc.containsKey("omero_id") && (doc.getInt("omero_id")!=0)) {
                String fullSizePath =impcMediaBaseUrl+"/render_image/"+ doc.getString("omero_id"); //http://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/7257/
                String downloadUrl=doc.getString("download_url");
                //System.out.println("full size path="+downloadUrl);
                String thumbnailPath = fullSizePath.replace("render_image", "render_birds_eye_view");
                String smallThumbNailPath = thumbnailPath + "/";
                img = "<img src='" + smallThumbNailPath + "'/>";
                if(downloadUrl.contains("/annotation/")){
                	//img="<img style='width: 200px' src='../" + pdfThumbnailUrl + "'/>";
					img="<img style='width: 96px' src='../" + pdfThumbnailUrl + "'/>";
                	//link = "<a href='" + downloadUrl +"'>" + img + "</a>";
                }
            } else 
            if(doc.getInt("omero_id")==0)
            {//we have a secondary project image (currently only available through PHIS)
            	//String fullSizePath =impcMediaBaseUrl+"/render_image/"+ doc.getString("omero_id"); //http://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/7257/
                String downloadUrl=doc.getString("download_url");
                //System.out.println("full size path="+downloadUrl);
                
                String smallThumbNailPath = doc.getString("thumbnail_url");
                img = "<img style='width: 96px' src='" + smallThumbNailPath + "'/>";
                if(downloadUrl.contains("/annotation/")){
                	//img="<img style='width: 200px' src='../" + pdfThumbnailUrl + "'/>";
					img="<img style='width: 96px' src='../" + pdfThumbnailUrl + "'/>";
                }
            }else{
                //link = IMG_NOT_FOUND;
				img = IMG_NOT_FOUND;
            }
            //imgPath.add(link);
			imgs.add(img);
        }

        //pathAndCount.add(StringUtils.join(imgPath, ""));
		pathAndCount.add(StringUtils.join(imgs, ""));
        pathAndCount.add(imgCount);

        return pathAndCount;
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
	public JSONArray mergeFacets(JSONObject facetFields) {

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
			for (int i = 0; i < arr.size(); i = i + 2) {

				if ( (Integer) arr.get(i + 1) > 0 ) {
					// We only want facet fields that contain an underscore
					// as it contains ID info we want
					if (((String) arr.get(i)).contains("_")) {
						fields.add(arr.get(i));
						fields.add(arr.get(i + 1));
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

	public List<AnnotNameValCount> mergeImpcFacets(String query, JSONObject json, String baseUrl) {


	    query = query.replaceAll("\"", "");
		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		List<AnnotNameValCount> annots = new ArrayList<>();

		Map<String, String> hm = new HashMap<>();
		hm.put("symbol_gene", "Gene");
        hm.put("marker_synonym_symbol_gene", "Gene");
		hm.put("procedure_name", "Procedure");
      //  hm.put("parameter_association_name_procedure_name", "Procedure");
		hm.put("anatomy_id_term", "Anatomy");
        hm.put("anatomy_term_synonym_anatomy_id_term", "Anatomy");
        hm.put("selected_top_level_anatomy_id_anatomy_id_term", "Anatomy");
        hm.put("selected_top_level_anatomy_term_anatomy_id_term", "Anatomy");
        hm.put("selected_top_level_anatomy_term_synonym_anatomy_id_term", "Anatomy");
        hm.put("intermediate_anatomy_id_anatomy_id_term", "Anatomy");
        hm.put("intermediate_anatomy_term_anatomy_id_term", "Anatomy");
        hm.put("intermediate_anatomy_term_synonym_anatomy_id_term", "Anatomy");

		Set<String> ffList = facetFields.keySet();

		Set<String> ignoreFields = new HashSet<>();
        Set<String> seenGenes = new HashSet<>();
        Set<String> seenProcedures = new HashSet<>();
        Set<String> seenAnatomy = new HashSet<>();
        String separator = "___";

        for( String fieldName : ffList){
			//System.out.println("facet field: "+ fieldName);
            try {
                JSONArray arr = facetFields.getJSONArray(fieldName);

                for (int i = 0; i < arr.size(); i = i + 2) {
					if (! arr.get(i+1).toString().equals("0")) {
						Object facetValue = arr.get(i);

						if (facetValue instanceof String) {
							String fv = facetValue.toString();
							//System.out.println(fieldName + " -- " + fv);

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
			URISyntaxException {

		String url = SolrUtils.getBaseURL(getSolrServer("gene"))
				+ "/select?wt=json&q=mgi_accession_id:"
				+ accession.replace(":", "\\:");

		log.info("url for geneDao=" + url);

		JSONObject json = getResults(url);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

		if (docs.size() > 1) {
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
			return (JSONObject) JSONSerializer.toJSON(content);
		}
		catch (Exception e) {
			e.printStackTrace();
        }
		return null;
	}


	public JSONObject getImageInfo(int imageId) throws SolrServerException, IOException,
			IOException, URISyntaxException {

		String url = SolrUtils.getBaseURL(getSolrServer("images"))
				+ "/select?wt=json&q=id:" + imageId;
		JSONObject json = getResults(url);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

		if (docs.size() > 1) {
			log.error("Error, Only expecting 1 document from an accession/gene request");
		}
		if(docs.size()<1) {//if nothing returned return an empty json object
			return new JSONObject();
		}

		JSONObject imageInfo = docs.getJSONObject(0);
		return imageInfo;

	}

	public Map<String, JSONObject> getExampleImages(int controlImageId,
			int expImageId) throws SolrServerException, IOException ,
			URISyntaxException {
		Map<String, JSONObject> map = new HashMap<String, JSONObject>();
		JSONObject controlDocument = this.getImageInfo(controlImageId);
		JSONObject expDocument = this.getImageInfo(expImageId);

		map.put("control", controlDocument);
		map.put("experimental", expDocument);
		return map;
	}

	public class PfamAnnotations {

		public String scdbId;
		public String scdbLink;
		public String clanId;
		public String clanAcc;
		public String clanDesc;
		public String uniprotAcc;
		public String uniprotId;
		public String pfamAacc;
		public String pfamAId;
		public String pfamAgoId;
		public String pfamAgoTerm;
		public String pfamAgoCat;
		public String pfamAnnots;

		// these getters/setters are needed as JSONSerializer.toJSON() works on JavaBeans
		public String getScdbId() {
			return scdbId;
		}
		public void setScdbId(String scdbId) {
			this.scdbId = scdbId;
		}
		public String getScdbLink() {
			return scdbLink;
		}
		public void setScdbLink(String scdbLink) {
			this.scdbLink = scdbLink;
		}
		public String getClanId() {
			return clanId;
		}
		public void setClanId(String clanId) {
			this.clanId = clanId;
		}
		public String getClanAcc() {
			return clanAcc;
		}
		public void setClanAcc(String clanAcc) {
			this.clanAcc = clanAcc;
		}
		public String getClanDesc() {
			return clanDesc;
		}
		public void setClanDesc(String clanDesc) {
			this.clanDesc = clanDesc;
		}
		public String getUniprotAcc() {
			return uniprotAcc;
		}
		public void setUniprotAcc(String uniprotAcc) {
			this.uniprotAcc = uniprotAcc;
		}
		public String getUniprotId() {
			return uniprotId;
		}
		public void setUniprotId(String uniprotId) {
			this.uniprotId = uniprotId;
		}
		public String getPfamAacc() {
			return pfamAacc;
		}
		public void setPfamAacc(String pfamAacc) {
			this.pfamAacc = pfamAacc;
		}
		public String getPfamAId() {
			return pfamAId;
		}
		public void setPfamAId(String pfamAId) {
			this.pfamAId = pfamAId;
		}
		public String getPfamAgoId() {
			return pfamAgoId;
		}
		public void setPfamAgoId(String pfamAgoId) {
			this.pfamAgoId = pfamAgoId;
		}
		public String getPfamAgoTerm() {
			return pfamAgoTerm;
		}
		public void setPfamAgoTerm(String pfamAgoTerm) {
			this.pfamAgoTerm = pfamAgoTerm;
		}
		public String getPfamAgoCat() {
			return pfamAgoCat;
		}
		public void setPfamAgoCat(String pfamAgoCat) {
			this.pfamAgoCat = pfamAgoCat;
		}
		public String getPfamAnnots() {
			return pfamAnnots;
		}
		public void setPfamAnnots(String pfamAnnots) {
			this.pfamAnnots = pfamAnnots;
		}

	 }

	@SuppressWarnings("deprecation")
	public String getMgiGenesClansDataTable(String baseUrl) throws IOException, URISyntaxException {

		String qParam = "&q=latest_phenotype_status:\"Phenotyping Complete\" OR latest_phenotype_status:\"Phenotyping Started\"";
		//String facetParam = "&facet=on&facet.field=clan_id&facet.mincount=1&facet.limit=-1&facet.sort=count";
		String flParam = "&fl=mgi_accession_id,marker_symbol,latest_phenotype_status,pfama_json";
		String internalBaseSolrUrl = SolrUtils.getBaseURL(getSolrServer("allele")) + "/select?wt=json";
		//String internalBaseSolrUrl = "http://localhost:8090/solr/allele/select?";

		String url = internalBaseSolrUrl + qParam + flParam;

		JSONObject json = getResults(url);

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

        JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);

		for (int i = 0; i < docs.size(); i++) {

			List<String> rowData = new ArrayList<String>();

			JSONObject doc = docs.getJSONObject(i);

			String mgiId = doc.getString("mgi_accession_id");
			String geneLink = baseUrl + "/genes/" + mgiId;
			String marker = "<a href='" + geneLink + "'>" + doc.getString("marker_symbol") + "</a>";
			rowData.add(marker);

			String phenoStatus = doc.getString("latest_phenotype_status");
			rowData.add(phenoStatus);

			if ( doc.containsKey("pfama_json") ){
				JSONArray pfamJsonStrs = doc.getJSONArray("pfama_json");

				List<String> clans = new ArrayList<>();
				List<String> scdbs = new ArrayList<>();
	            List<String> pfamAs = new ArrayList<>();

	            String pfamBaseUrl = "http://pfam.xfam.org";
	            String scopBaseUrl = "http://scop.mrc-lmb.cam.ac.uk/scop/search.cgi?sunid=";

				for ( int p=0; p<pfamJsonStrs.size(); p++ ){
					String pfstr = pfamJsonStrs.getString(p).replaceAll("^\"|\"$", "");
					JSONObject pfamj = JSONObject.fromObject(pfstr);

					if ( doc.containsKey("pfamAacc") ){
						String pfamAacc = doc.getString("pfamAacc");
						String pfamUrl = pfamBaseUrl + "/family/" + pfamAacc;
						String pfamLink = "<a href='" + pfamUrl + "'>" + pfamAacc + "</a>";
						rowData.add(pfamLink);
					}

					String clanUrl = pfamBaseUrl + "/clan/" + pfamj.getString("clanAcc");
					String clanLink = "<a href='" + clanUrl + "'>" + pfamj.getString("clanId") + "</a>";
					clans.add(clanLink);

					if ( doc.containsKey("scdbId") ){
						String scdbId = doc.getString("scdbId");
						if ( scdbId.equals("SCOP") ){
							rowData.add(scopBaseUrl + pfamj.getString("scdbLink"));
						}
						else if ( scdbId.equals("CATH") ){
							rowData.add("cath_url");
						}
						else if ( scdbId.equals("MEROPS") ){
							rowData.add("merops_url");
						}
					}
				}
			}
			else {
				rowData.add("not available");
				rowData.add("not available");
				rowData.add("not available");
			}

			j.getJSONArray("aaData").add(rowData);
		}

		return j.toString();
	}

	public String getMgiGenesClansPlainTable(String baseUrl) throws IOException, URISyntaxException {

		String qParam = "&q=latest_phenotype_status:\"Phenotyping Complete\" OR latest_phenotype_status:\"Phenotyping Started\"";
		//String facetParam = "&facet=on&facet.field=clan_id&facet.mincount=1&facet.limit=-1&facet.sort=count";
		String flParam = "&fl=mgi_accession_id,marker_symbol,latest_phenotype_status,pfama_json";
		String internalBaseSolrUrl = SolrUtils.getBaseURL(getSolrServer("gene")) + "/select?wt=json&rows=999999&fq=mp_id:*";
		//String internalBaseSolrUrl = "http://localhost:8090/solr/allele/select?";

		String url = internalBaseSolrUrl + qParam + flParam;
		JSONObject json = getResults(url);

		String table = "";
		String th = "<thead><tr><th>Marker symbol</th><th>Phenotyping status</th><th>PfamA family</th><th>Pfam clan</th><th>Structure</th><th>Evidence</th></tr></thead>";
		String trs = "";

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		for (int i = 0; i < docs.size(); i++) {

			List<String> rowData = new ArrayList<String>();

			JSONObject doc = docs.getJSONObject(i);

			String mgiId = doc.getString("mgi_accession_id");
			String geneLink = baseUrl + "/genes/" + mgiId;
			String marker = "<a href='" + geneLink + "'>" + doc.getString("marker_symbol") + "</a>";
			rowData.add("<td>" + marker + "</td>");

			String phenoStatus = doc.getString("latest_phenotype_status");
			rowData.add("<td>" + phenoStatus + "</td>");

			if ( doc.containsKey("pfama_json") ){
				JSONArray pfamJsonStrs = doc.getJSONArray("pfama_json");

				Set<String> pfams = new HashSet<>();
				Set<String> clans = new HashSet<>();
				Set<String> scdbs = new HashSet<>();

	            String pfamBaseUrl = "http://pfam.xfam.org";
	            String cathBaseUrl = "http://www.cathdb.info/version/latest/superfamily/";
	            String scopBaseUrl = "http://scop.mrc-lmb.cam.ac.uk/scop/search.cgi?sunid=";

				for ( int p=0; p<pfamJsonStrs.size(); p++ ){
					String pfstr = pfamJsonStrs.getString(p).replaceAll("^\"|\"$", "");
					JSONObject pfamj = JSONObject.fromObject(pfstr);

					if ( pfamj.containsKey("pfamAacc") ){
						//System.out.println("got pfamAacc");
						String pfamAacc = pfamj.getString("pfamAacc");
						String pfamUrl = pfamBaseUrl + "/family/" + pfamAacc;
						String pfamLink = "<a href='" + pfamUrl + "'>" + pfamAacc + "</a>";
						pfams.add(pfamLink);
					}

					if ( pfamj.containsKey("clanAcc") ){
						//System.out.println("got clanAcc");
						String clanUrl = pfamBaseUrl + "/clan/" + pfamj.getString("clanAcc");
						String clanLink = "<a href='" + clanUrl + "'>" + pfamj.getString("clanId") + "</a>";
						clans.add(clanLink);
					}

					if ( pfamj.containsKey("scdbId") ){
						//System.out.println("got scdbId");
						String scdbId = pfamj.getString("scdbId");
						String scdbLinkVal = pfamj.getString("scdbLink");
						String scdbLink = "";
						String scdbUrl = "";
						if ( scdbId.equals("SCOP") ){
							scdbUrl = scopBaseUrl + scdbLinkVal;
						}
						else if ( scdbId.equals("CATH") ){
							scdbUrl = cathBaseUrl + scdbLinkVal;
						}
						else if ( scdbId.equals("MEROPS") ){
							scdbUrl = "#";
						}
						scdbLink = scdbId + ": <a href='" + scdbUrl + "'>" + scdbLinkVal + "</a>";
						scdbs.add(scdbLink);
					}
				}

				// natural sort
				Set<String> sortedPfams = new TreeSet<String>(pfams);
				Set<String> sortedClans = new TreeSet<String>(clans);
				Set<String> sortedScdbs = new TreeSet<String>(scdbs);

				rowData.add("<td>" + StringUtils.join(sortedPfams, "<br>") + "</td>");
				rowData.add("<td>" + StringUtils.join(sortedClans, "<br>") + "</td>");
				rowData.add("<td>" + StringUtils.join(sortedScdbs, "<br>") + "</td>");
				rowData.add("<td>pfam-positive</td>");
			}
			else {
				rowData.add("<td>not available</td>");
				rowData.add("<td>not available</td>");
				rowData.add("<td>not available</td>");
				rowData.add("<td>pfam-negative</td>");
			}

			trs += "<tr>" + StringUtils.join(rowData, "") + "</tr>";
		}

		table = "<table id='gene2pfam'>" + th + "<tbody>" + trs + "</tbody></table>";
		return table;
	}

	public String getGO2ImpcGeneAnnotationTable(String baseUrl) throws IOException, URISyntaxException {
		String internalBaseSolrUrl = SolrUtils.getBaseURL(getSolrServer("gene")) + "/select?";
		String flStr = "&fl=mgi_accession_id,marker_symbol,latest_phenotype_status,go_term_id,go_term_evid,go_term_domain,go_term_name";
		String queryParams = "q=latest_phenotype_status:\"Phenotyping Complete\" OR latest_phenotype_status:\"Phenotyping Started\" OR (go_term_domain:\"biological_process\" OR go_term_domain:\"molecular_function\")&wt=json&rows=10&fq=mp_id:*";

		String table = "";
		String th = "<thead><tr><th>Marker symbol</th><th>Phenotyping status</th><th>GO Id</th><th>GO Evidence</th><th>GO name</th><th>GO domain</th></tr></thead>";
		String trs = "";
		//System.out.println(internalBaseSolrUrl + queryParams + flStr);

		JSONObject json = getResults(internalBaseSolrUrl + queryParams + flStr);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

		for (int i = 0; i < docs.size(); i++) {

			JSONObject doc = docs.getJSONObject(i);

			String mgiId = doc.getString("mgi_accession_id");
			String geneLink = baseUrl + "/genes/" + mgiId;
			String marker = "<a href='" + geneLink + "'>" + doc.getString("marker_symbol") + "</a>";

			String phenoStatus = doc.getString("latest_phenotype_status");

			if ( doc.containsKey("go_term_id") ){

				JSONArray goTermIds = doc.getJSONArray("go_term_id");
				JSONArray goTermEvids = doc.getJSONArray("go_term_evid");
				JSONArray goTermNames = doc.getJSONArray("go_term_name");
				JSONArray goTermDomains = doc.getJSONArray("go_term_domain");

	            String goBaseUrl = "http://www.ebi.ac.uk/QuickGO/GTerm?id=";

				for ( int p=0; p<goTermIds.size(); p++ ){

					List<String> rowData = new ArrayList<String>();
					rowData.add("<td>" + marker + "</td>");
					rowData.add("<td>" + phenoStatus + "</td>");

					String goId = goTermIds.get(p).toString();
					String goUrl = goBaseUrl + goId;
					String goLink = "<a href='" + goUrl + "'>" + goId + "</a>";
					rowData.add("<td>" + goLink + "</td>");

					String goEvid = goTermEvids.get(p).toString();
					rowData.add("<td>" + goEvid + "</td>");

					String goName = goTermNames.get(p).toString();
					rowData.add("<td>" + goName + "</td>");

					String goDomain = goTermDomains.get(p).toString();
					rowData.add("<td>" + goDomain + "</td>");
					trs += "<tr>" + StringUtils.join(rowData, "") + "</tr>";
				}
			}
			else {
				List<String> rowData = new ArrayList<String>();
				rowData.add("<td>" + marker + "</td>");
				rowData.add("<td>" + phenoStatus + "</td>");
				rowData.add("<td>not available</td>");
				rowData.add("<td>not available</td>");
				rowData.add("<td>not available</td>");
				rowData.add("<td>not available</td>");
				trs += "<tr>" + StringUtils.join(rowData, "") + "</tr>";
			}
		}

		table = "<table id='gene2go'>" + th + "<tbody>" + trs + "</tbody></table>";

		return table;

	}

	public Map<String, Map<String, Map<String, JSONArray>>> getGO2ImpcGeneAnnotationStats() throws IOException, URISyntaxException{
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
			String goParamsFP = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND (go_term_domain:\"biological_process\" AND go_term_domain:\"molecular_function\")&wt=json&rows=0&fq=mp_id:*&facet=on&facet.limit=-1&facet.field=evidCodeRank";

			// only molecular_function
			//String goParamsF = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND go_term_domain:\"molecular_function\"&wt=json&rows=0&fq=mp_id:*&facet=on&facet.limit=-1&facet.field=go_term_evid";
			String goParamsF = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND go_term_domain:\"molecular_function\"&wt=json&rows=0&fq=mp_id:*&facet=on&facet.limit=-1&facet.field=evidCodeRank";

			// only biological_process
			//String goParamsP = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND go_term_domain:\"biological_process\"&wt=json&rows=0&fq=mp_id:*&facet=on&facet.limit=-1&facet.field=go_term_evid";
			String goParamsP = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:* AND go_term_domain:\"biological_process\"&wt=json&rows=0&fq=mp_id:*&facet=on&facet.limit=-1&facet.field=evidCodeRank";


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
			        	ja.add(numFound);
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
