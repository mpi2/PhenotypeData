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
package uk.ac.ebi.phenotype.web.util;


import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;


public class CaptchaHttpProxy {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	public String getContent(String urlString, List<NameValuePair> params) throws IOException {

		// Convert to a URI and back to a URL to get any strange characters to
		// be encoded during the process
		URL escapedUrl = new URL(new URL(urlString).toExternalForm().replace(" ", "%20"));

		HttpPost httpPost = new HttpPost(urlString);
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		String content = "";

		try {

			String            proxyHostname = System.getProperty("https.proxyHost");
			int               proxyPort     = Integer.parseInt(System.getProperty("https.proxyPort"));
			InetSocketAddress inet          = new InetSocketAddress(proxyHostname, proxyPort);
			Proxy             proxy         = new Proxy(Proxy.Type.HTTP, inet);

			if (proxy != null) {
				RequestConfig config = RequestConfig.custom()
						.setProxy(new HttpHost(proxyHostname, proxyPort))
						.build();

				httpPost.setConfig(config);
				log.info("Using proxy {}:{}", proxyHostname, proxyPort);
			} else {
			    log.info("Not using proxy");
            }

		} catch (NumberFormatException e) {

			// Don't us a proxy. The proxyPort is either null or not convertible to an int.

		} catch (Exception e) {

			log.error("Exception: {}", e);
		}

		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = client.execute(httpPost);

		ResponseHandler<String> handler = new BasicResponseHandler();
		content = handler.handleResponse(response);

		return content;
	}
}