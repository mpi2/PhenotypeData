package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Service
public class OmeroStatusService implements WebStatus {
 
    @NotNull
    @Value("${internalImpcMediaBaseUrl}")
    private String omeroUrl;

    @PostConstruct
    public void initisialise() {

        System.out.println("initialised OmeroStatus Service with url=" + omeroUrl);
    }

    @Override
    public long getWebStatus() throws SolrServerException, IOException {
        String url = "http:" + omeroUrl + "/render_birds_eye_view/1/";

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "OmeroStatus request from PhenotypeArchive");
            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
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
    public String getServiceName() {
        return "OmeroStatus Service";
    }

}
