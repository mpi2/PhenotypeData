package org.mousephenotype.cda.solr.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.stereotype.Service;

@Service
public class OmeroStatusService implements WebStatus{
	
	 @Resource(name = "globalConfiguration")
	  private Map<String, String> config;
	 private String omeroUrl;

	 @PostConstruct
	 public void initisialise(){
		 
		 omeroUrl=config.get("impcMediaBaseUrl");
		 System.out.println("initialised OmeroStatus Service with url="+omeroUrl);
	 }

	@Override
	public long getWebStatus() throws SolrServerException {
		String url="http:"+omeroUrl+"/render_thumbnail/1/";
		
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "OmeroStatus request from PhenotypeArchive");
			int responseCode = con.getResponseCode();
			if(responseCode!=200){
				return 0;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;//if all is well return 1 to indicate service is up.
	}
	
	@Override
	public String getServiceName(){
		return "OmeroStatus Service";
	}

}
