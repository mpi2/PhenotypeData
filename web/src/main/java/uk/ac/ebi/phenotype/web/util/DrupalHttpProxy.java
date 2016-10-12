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


import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

public class DrupalHttpProxy extends HttpProxy {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	private String debugSession = null;
	private HttpServletRequest request;

	protected static String authenticatedMenu = null;
	protected static String publicMenu = null;
	protected static String publicUserMenu = null;

	public DrupalHttpProxy(HttpServletRequest request) {
		super();
		this.request = request;
	}

	/**
	 * helper method to get a page from an external URL by including in the
	 * drupal session cookie
	 *
	 * NOTE: The drupal session cookie is identified because it starts with the
	 * string "SSESS" ... this is not a very strong signal.
	 *
	 * CAUTION: This method bypasses the SSL certificate verification to allow
	 * secure content from domains other than "www." to be accessed. This is
	 * required because the drupal content pages return coorectly only when
	 * SSL is enabled. We need the drupal session cookie to determine if the user
	 * is logged in.
	 *
	 * @param url
	 *            the url to interrogate
	 * @return the content in one big string with leading/trailing whitespace
	 *         removed
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String getContent(URL url) throws IOException, URISyntaxException {
		long startTime = System.currentTimeMillis();

		if (cache.containsKey(url)) {
			return cache.get(url);
		}

		// Convert to a URI and back to a URL to get any strange characters to
		// be encoded during the process
		URL escapedUrl = new URL(url.toExternalForm().replace(" ", "%20"));

		String content = "";

		// Set the drupal session before getting the content
		this.setCookieString(getDrupalSessionCookieString());

		if(url.getProtocol().toLowerCase().equals("https")) {
			content = getSecureContent(escapedUrl);
		} else {
			content = getNonSecureContent(escapedUrl);
		}

		long duration = System.currentTimeMillis() - startTime;
		log.debug("Got content from: "+escapedUrl+" in : "+duration +" milliseconds.");

		return content;
	}

	/**
	 * returns the drupal session cookie. If the debugSession class variable is
	 * set, the session variable is overriden.
	 *
	 * NOTE: If the class variable debugSession is set, then that string
	 * overrides any session variable picked up from the cookie jar.
	 *
	 * NOTE: The drupal session cookie is identified by starting with the string
	 * "SSESS" which, in hindsight, may turn out not to be the best idea in the
	 * world.
	 *
	 * @return the drupal session cookie string
	 *
	 */
	public String getDrupalSessionCookieString() {

		String session = "";

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().startsWith("SSESS")) {
					session = cookie.getName() + "=" + cookie.getValue();
					break;
				}
			}
		}

		if (this.debugSession != null) {
			session = this.debugSession;
		}

		return session;
	}

	public String getDebugSession() {
		return debugSession;
	}

	public void setDebugSession(String debugSession) {
		this.debugSession = debugSession;
	}

	/**
	 * Get the current menus for the drupal system -- both the main menu and the user menu
	 *
	 * @return a concatenated drupal menu as a string separated by "MAIN*MENU*BELOW", or a default if
	 *         drupal could not be contacted for some reason
	 */
	public String getDrupalMenu(String drupalBaseUrl) {
		
		long time = System.currentTimeMillis();
		String secureDrupalBaseUrl = drupalBaseUrl;

		if (secureDrupalBaseUrl != null) {
			secureDrupalBaseUrl = drupalBaseUrl.startsWith("//") ? "https:" + secureDrupalBaseUrl : secureDrupalBaseUrl.replaceAll("http:", "https:");
		}

		String content = "";
		Random randomGenerator = new Random();

		try {
			if (getDrupalSessionCookieString() != null && ! getDrupalSessionCookieString().equals("") ) {
				log.info("Getting drupal menu.");
				URL url = new URL(secureDrupalBaseUrl + "/menudisplaycombinedrendered");

				content = this.getContent(url);

			} else {
				if (publicMenu == null || randomGenerator.nextInt(100) == 1) {
					log.info("Not logged in, using standard menu.");
					URL url = new URL(secureDrupalBaseUrl + "/menudisplaycombinedrendered");

					publicMenu = this.getContent(url);
					content = publicMenu;

				} else {
					content = publicMenu;
				}
			}

			if (StringUtils.isEmpty(content)) {
				throw new Exception("Cannot retreive DCC drupal menu");
			}
		} catch (Exception e) {

			// If we can't get the menu, default to the logged out menu
			log.error("Cannot retrieve menu from drupal. Using default menu.");

			// Menu updated 2015-08-20
			content = "<ul class=\"links\"><li class=\"menu-3521 first\"><a href=\"/user/login\" title=\"Login with your account\" id=\"login\">Login</a></li>\n" +
				"<li class=\"menu-3523 last\"><a href=\"/user/register\" title=\"Register for an account\" id=\"register\">Register</a></li>\n" +
				"</ul>MAIN*MENU*BELOW<div id=\"block-menu-block-1\" class=\"block block-menu-block\">\n" +
				"<div class=\"content\"><div class=\"menu-block-wrapper menu-block-1 menu-name-main-menu parent-mlid-0 menu-level-1\">\n" +
				"<ul class=\"menu\"><li class=\"first expanded menu-mlid-3127\"><a href=\"/data/search\">Search</a><ul class=\"menu\"><li class=\"first leaf menu-mlid-4047\"><a href=\"/data/batchQuery\">Batch Query</a></li>\n" +
				"<li class=\"leaf menu-mlid-4049\"><a href=\"/phenoview\">Phenoview</a></li>\n" +
				"<li class=\"leaf menu-mlid-4053\"><a href=\"/search/tools\">Tools</a></li>\n" +
				"<li class=\"last leaf menu-mlid-4051\"><a href=\"/data/documentation/api-help\">API</a></li>\n" +
				"</ul></li>\n" +
				"<li class=\"expanded menu-mlid-530\"><a href=\"/goals-and-background\">About IMPC</a><ul class=\"menu\"><li class=\"first leaf menu-mlid-3125\"><a href=\"/goals-and-background\" title=\"\">Goals and Background</a></li>\n" +
				"<li class=\"leaf menu-mlid-537\"><a href=\"/about-impc/impc-members\">IMPC Members</a></li>\n" +
				"<li class=\"leaf menu-mlid-3197\"><a href=\"/sites/mousephenotype.org/files/IMPC%20Governance%20and%20Coordination%20v02%20October%202014.pdf\">Governance Documentation</a></li>\n" +
				"<li class=\"leaf has-children menu-mlid-3525\"><a href=\"/about-impc/coordination\">Coordination</a></li>\n" +
				"<li class=\"leaf menu-mlid-3229\"><a href=\"/about-impc/industry-sponsors\">Industry Sponsors</a></li>\n" +
				"<li class=\"leaf menu-mlid-546\"><a href=\"/about-impc/impc-secretariat\">Secretariat</a></li>\n" +
				"<li class=\"leaf has-children menu-mlid-3223\"><a href=\"/about-impc/publications\">Additional Information</a></li>\n" +
				"<li class=\"leaf menu-mlid-3983\"><a href=\"/about-impc/arrive-guidelines\">ARRIVE Guidelines</a></li>\n" +
				"<li class=\"last leaf menu-mlid-3975\"><a href=\"/about-ikmc\">About IKMC</a></li>\n" +
				"</ul></li>\n" +
				"<li class=\"expanded menu-mlid-526\"><a href=\"/news\" title=\"\">News &amp; Events</a><ul class=\"menu\"><li class=\"first leaf menu-mlid-3185\"><a href=\"/news-events/impc-lethal-lines\">IMPC Lethal Lines</a></li>\n" +
				"<li class=\"leaf menu-mlid-2102\"><a href=\"/news-events/meetings\">Meetings</a></li>\n" +
				"<li class=\"leaf menu-mlid-4041\"><a href=\"/data/alleleref\">References using IKMC and IMPC Resources</a></li>\n" +
				"<li class=\"last leaf menu-mlid-4043\"><a href=\"/news-events/phone-conferences\">Phone Conferences</a></li>\n" +
				"</ul></li>\n" +
				"<li class=\"leaf menu-mlid-559\"><a href=\"/contact-us\">Contact</a></li>\n" +
				"<li class=\"last expanded menu-mlid-1220\"><a href=\"/user?current=menudisplaycombinedrendered\">My IMPC</a><ul class=\"menu\"><li class=\"first leaf menu-mlid-1126\"><a href=\"/forum\" title=\"\">IMPC Forum</a></li>\n" +
				"<li class=\"leaf has-children menu-mlid-3133\"><a href=\"/my-impc/documentation\">Documentation</a></li>\n" +
				"<li class=\"last leaf menu-mlid-4029\"><a href=\"/my-impc/communications-materials\">Communications Materials</a></li>\n" +
				"</ul></li>\n" +
				"</ul></div>\n" +
				"<div class=\"clear\"></div></div>  \n" +
				"</div>\n";
		}
		return content;
	}


}
