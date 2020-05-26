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
package org.mousephenotype.cda.utilities;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;


public class HttpProxy {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	// Timeout after 30 seconds
	private int                    CONNECTION_TIMEOUT_IN_MILLISECONDS = 30000;
	protected HashMap<URL, String> cache                              = new HashMap<>();
	private String                 cookieString                       = null;

	
	public String getContent(URL url) throws IOException, URISyntaxException {
		return this.getContent(url, false);
	}
	/**
	 * Method to get page content from an external URL. 'External URL' means any url external to the EBI.
	 *
	 * @param url the url to interrogate
	 * @return the content in a String
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String getContent(URL url, boolean external) throws IOException, URISyntaxException {

		// If this url has already been fetched, return the cached content
		// rather than re-fetching
		if (cache.containsKey(url)) {
			return cache.get(url);
		}

		// Convert spaces to the URL encoded form
		URL escapedUrl = new URL(url.toExternalForm().replace(" ", "%20"));
		log.debug("PARSING URL: " + escapedUrl);

		String content = "";

                try {
                    if(url.getProtocol().toLowerCase().equals("https")) {
                            content = getSecureContent(escapedUrl);
                    } else {
                            content = getNonSecureContent(escapedUrl, external);
                    }
                } catch (Exception e) {
                    log.error("EXCEPTION for protocol " + url.getProtocol().toLowerCase() + ": " + e.getLocalizedMessage() + ". URL: " + escapedUrl);
                    throw e;
                }

		cache.put(url, content);

		return content;
	}

	public String getSecureContent(URL url) throws IOException {

		HttpsURLConnection urlConn;
		InputStreamReader inStream;
		String content = "";
		Proxy proxy = getProxy(url, false);

		// Set up custom SSL verification which always verifies certs, even when
		// the server name doesn't match the cert name so that requests to
		// dev. and beta. will not fail
		SSLContext ctx;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0],
					new TrustManager[] { new DefaultTrustManager() },
					new SecureRandom());
			SSLContext.setDefault(ctx);
		} catch (NoSuchAlgorithmException e) {
			log.error("Could not setup SSLContext: " + e.getLocalizedMessage());
		} catch (KeyManagementException e) {
			log.error("Could not setup SSLContext: " + e.getLocalizedMessage());
		}

		if (proxy != null) {
			urlConn = (HttpsURLConnection) url.openConnection(proxy);
		} else {
			urlConn = (HttpsURLConnection) url.openConnection();
		}

		log.debug("Getting content from URL: " + url);

		// Send the cookieString (if there is one) along with request
		if (cookieString != null) {
			log.debug("Setting cookieString: " + cookieString);
			urlConn.setRequestProperty("Cookie", cookieString);
		}

		urlConn.setHostnameVerifier(new CustomizedHostNameVerifier());
		urlConn.setReadTimeout(CONNECTION_TIMEOUT_IN_MILLISECONDS);
		urlConn.connect();
		inStream = new InputStreamReader(urlConn.getInputStream());

		content = IOUtils.toString(inStream).trim();

		return content;
	}
	
	public String getNonSecureContent(URL url) throws IOException {
		return this.getNonSecureContent(url, false);
	}

	/**
	 * 
	 * @param url
	 * @param external - seems we use a different system property for solr indexers?
	 * @return
	 * @throws IOException
	 */
	public String getNonSecureContent(URL url, boolean external) throws IOException {

		HttpURLConnection urlConn;
		InputStreamReader inStream;
		String content = "";
		Proxy proxy = getProxy(url, external);
		if (proxy != null) {
			urlConn = (HttpURLConnection) url.openConnection(proxy);
		} else {
			urlConn = (HttpURLConnection) url.openConnection();
		}

		urlConn.setReadTimeout(CONNECTION_TIMEOUT_IN_MILLISECONDS);
		urlConn.connect();
		try {
			inStream = new InputStreamReader(urlConn.getInputStream());
		} catch (Exception e) {
			throw new IOException("url " + url.toString() + " not found");
		}

		content = IOUtils.toString(inStream).trim();
		return content;
	}

	public Proxy getProxy(URL url, boolean external) {

		String proxyHost = null;
		
		
		
		Integer proxyPort = null;
		if(external){
			
			proxyHost=System.getProperty("externalProxyHost");
			try {
				proxyPort = Integer.parseInt(System.getProperty("externalProxyPort"));
			} catch (NumberFormatException e) {
				// proxy port is either not defined or not defined properly
				// don't proxy anything
				return null;
			}

		}else{
			proxyHost=System.getProperty("http.proxyHost");
			try {
				
				proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
			} catch (NumberFormatException e) {
				// proxy port is either not defined or not defined properly
				// don't proxy anything
				return null;
			}
		}

		// Do not proxy requests to these hosts
		// e.g. http.nonProxyHosts=*.ebi.ac.uk|localhost|127.0.0.1
		String noProxyStr = System.getProperty("http.nonProxyHosts");
		if (noProxyStr != null){
			String[] noProxy = noProxyStr.split("\\|");
			for (String host : noProxy) {
				if(url.getHost().matches(host.replaceAll("\\*", ".*"))) {
					return null;
				}
			}
		}

		Proxy proxy = null;
		if (proxyHost != null && proxyPort != null) {
			log.debug("Proxy Settings: " + proxyHost + " on port: " + proxyPort);
			InetSocketAddress inet = new InetSocketAddress(proxyHost, proxyPort);
			proxy = new Proxy(Proxy.Type.HTTP, inet);
		}
		return proxy;
	}

	/**
	 * @return the cookie string
	 */
	public String getCookieString() {
		return cookieString;
	}

	/**
	 * @param cookieString the cookieString string to set
	 */
	public void setCookieString(String cookieString) {
		this.cookieString = cookieString;
	}
}