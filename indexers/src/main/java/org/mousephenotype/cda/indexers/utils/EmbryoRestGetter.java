package org.mousephenotype.cda.indexers.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.web.client.RestTemplate;

/**
 * Class for getting the embryo data from the phenoDCC on embryo data available
 * 
 * @author jwarren
 *
 */
public class EmbryoRestGetter {

	private String embryoRestUrl="http://dev.mousephenotype.org/EmbryoViewerWebApp/rest/ready";//default is dev - needs to be wired up properly with spring when going to beta and live

	public void setRestUrl(String embryoRestUrl) {
		this.embryoRestUrl = embryoRestUrl;

	}

	// public EmbryoRestData getEmbryoRestData(){
	// RestTemplate restTemplate = new RestTemplate();
	// EmbryoRestData embryoData = restTemplate.getForObject(embryoRestUrl,
	// EmbryoRestData.class);
	// System.out.println(embryoData.toString());
	// //embryoRestData.add(embryoData);
	//
	//
	// return embryoData;
	//
	// }
	//
	public EmbryoRestData getEmbryoRestData() {
		//to be replaced with SpringRestTemplate when json format redone by Neil
		EmbryoRestData data = new EmbryoRestData();
		HttpClient client = new DefaultHttpClient();

		HttpGet request = new HttpGet(embryoRestUrl);

		HttpResponse response;
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			List<EmbryoStrain> strains = new ArrayList<>();
			String line = "";
			EmbryoStrain embryoStrain = null;
			String[] keyValue = null;
			String key = null;
			String value = null;
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
				if (line.contains(":")) {
					keyValue = line.split(":");
					key = keyValue[0].replace("\"", "").replace(",", "").trim();
					value = keyValue[1].replace("\"", "").replace(",", "").trim();
					System.out.println("key=" + key + " value=" + value);

				}

				if (line.contains("\": {")) {
					System.out.println("start of object");
					embryoStrain = new EmbryoStrain();
					embryoStrain.setName(key);
				}
				if (line.contains("\"mgi\"")) {
					embryoStrain.setMgi(value);
				}
				if (line.contains("\"centre\"")) {
					embryoStrain.setCentre(value);
				}
				if (line.contains("\"url\"")) {
					embryoStrain.setUrl(value);
				}
				if (line.contains("\"mgi\"")) {

				}
				if (line.contains("},")) {
					strains.add(embryoStrain);
				}

			}
			data.setStrains(strains);
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		return data;
	}
}
