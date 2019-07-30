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
package org.mousephenotype.cda.solr.generic.util;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * class for getting appropriate image info from the solr image rest service
 * should use JSONRestService for generic rest/json work.
 * @author jwarren
 *
 */
public class JSONImageUtils{
	
	public static  JSONObject getAnatomyAssociatedExpressionImages(String anatomy_id, Map<String, String> config, int numberOfImagesToDisplay) throws IOException, URISyntaxException, JSONException {
		//url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
		String url=config.get("internalSolrUrl")+"/images/select/?q=annotationTermId:"+anatomy_id+"&wt=json&start=0&rows="+numberOfImagesToDisplay+"&facet=on&fq=expName:\"Wholemount Expression\"&defType=edismax";
		JSONObject result = JSONRestUtil.getResults(url);
		return result;
	}
}
