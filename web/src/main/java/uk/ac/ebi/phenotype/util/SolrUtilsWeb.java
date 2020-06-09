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
package uk.ac.ebi.phenotype.util;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SolrUtilsWeb {

	@Value("${impc_media_base_url}")
	private String impcMediaBaseUrl;


	private SolrIndex solrIndex;


	@Inject
	public SolrUtilsWeb(SolrIndex solrIndex) {
		this.solrIndex = solrIndex;
	}


	public List fetchImpcImagePathByAnnotName(String query, String fqStr) throws IOException, URISyntaxException, JSONException  {

		List pathAndCount = new ArrayList<>();
		//String mediaBaseUrl = config.get("mediaBaseUrl");
		final int maxNum = 4; // max num of images to display in grid column

		String qryBaseUrl = SolrUtils.getBaseURL(solrIndex.getSolrServer("impc_images")) + "/select?qf=imgQf&defType=edismax&wt=json&q=" + query
				+ "&fq=" + fqStr + "&rows=";

		String queryUrl = qryBaseUrl + maxNum;
		String queryUrlCount = qryBaseUrl + "0";

		List<String> imgs = new ArrayList<>();

		JSONObject imgCountJson  = solrIndex.getResults(queryUrlCount);
		JSONObject thumbnailJson = solrIndex.getResults(queryUrl);

		Integer   imgCount = imgCountJson.getJSONObject("response").getInt("numFound");
		JSONArray docs     = thumbnailJson.getJSONObject("response").getJSONArray("docs");

		int dataLen = docs.length() < 5 ? docs.length() : maxNum;

		for (int i = 0; i < dataLen; i ++) {
			JSONObject doc = docs.getJSONObject(i);

			//String link = null;
			String img = null;

			if (doc.has("omero_id") && (doc.getInt("omero_id")!=0)) {
				String fullSizePath =impcMediaBaseUrl+"/render_image/"+ doc.getString("omero_id"); //http://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/7257/
				String downloadUrl=doc.getString("download_url");
				//System.out.println("full size path="+downloadUrl);
				String thumbnailPath = fullSizePath.replace("render_image", "render_birds_eye_view");
				String smallThumbNailPath = thumbnailPath + "/";
				img = "<img class='thumbnailStyle' src='" + smallThumbNailPath + "'/>";
				if(downloadUrl.contains("/annotation/")){
					img="<img class='thumbnailStyle' src='../" + Constants.PDF_THUMBNAIL_RELATIVE_URL + "'/>";
				}
			} else
			if(doc.getInt("omero_id")==0)
			{//we have a secondary project image (currently only available through PHIS)
				String downloadUrl=doc.getString("download_url");
				String smallThumbNailPath = doc.getString("thumbnail_url");
				img = "<img class='thumbnailStyle' src='" + smallThumbNailPath + "'/>";
				if(downloadUrl.contains("/annotation/")){
					img="<img class='thumbnailStyle' src='../" + Constants.PDF_THUMBNAIL_RELATIVE_URL + "'/>";
				}
			}else{
				//link = IMG_NOT_FOUND;
				img = Constants.NO_INFORMATION_AVAILABLE;
			}
			//imgPath.add(link);
			imgs.add(img);
		}

		//pathAndCount.add(StringUtils.join(imgPath, ""));
		pathAndCount.add(StringUtils.join(imgs, ""));
		pathAndCount.add(imgCount);

		return pathAndCount;
	}
}