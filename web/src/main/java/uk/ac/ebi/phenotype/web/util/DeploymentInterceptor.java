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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
public class DeploymentInterceptor extends HandlerInterceptorAdapter {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@Autowired
	DataReleaseVersionManager dataReleaseVersionManager;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	/**
	 * set baseUrl and other variables for all controllers
	 */
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {

		// Do not set attributes for assets
		if (request.getRequestURI().endsWith(".js")
				|| request.getRequestURI().endsWith(".css")
				|| request.getRequestURI().endsWith(".gif")
				|| request.getRequestURI().endsWith(".png")) {
			return true;
		}

		Map<String, Object> requestConfig = new HashMap<>();
		requestConfig.put("releaseVersion", dataReleaseVersionManager.getReleaseVersion());
		requestConfig.put("version", "v3.4.9");

		// Map the global config values into the request configuration
		config.keySet().forEach(key -> {
			requestConfig.put(key, config.get(key));
		});

		// Base url and mapped hostname get overwritten when there is a proxy in the middle.
		// Set the defaults here and override below if necessary.
		String mappedHostname = getMappedHostname(request.getServerName(), request.getServerPort(), request.getHeader("x-forwarded-host"));
		requestConfig.put("mappedHostname", mappedHostname);
		requestConfig.put("baseUrl", request.getContextPath());
		requestConfig.put("isProxied", Boolean.FALSE);

		log.debug("mappedHostName = {}. baseUrl before translation = {}", mappedHostname, request.getContextPath());

		// If this webapp is being accessed behind a proxy, the
		// x-forwarded-host header will be set, in which case, use the
		// configured baseUrl and mediaBaseUrl paths.  If this webapp is
		// being accessed directly set the baseUrl and mediaBaseUrl as
		// the current context path
		if (request.getHeader("x-forwarded-host") != null) {

			// We need to use scheme https when we are proxied. When we are running localhost, we need to use the value
			// in the request header (typically http, but if you configure your localhost to use https, you want to stay
			// in https). Set this session variable here so it can be queried later on (e.g. in SearchController) to
			// use the correct scheme.
			requestConfig.put("isProxied", Boolean.TRUE);

			String[] forwards = request.getHeader("x-forwarded-host").split(",");

			for (String forward : forwards) {
				if ( ! forward.matches(".*ebi\\.ac\\.uk")) {
					log.debug("Request proxied. Using baseUrl: " + config.get("baseUrl"));
					requestConfig.put("baseUrl", config.get("baseUrl"));
					break;
				}
			}
		}

		log.debug("mappedHostName = {}. baseUrl after translation = {}", mappedHostname, requestConfig.get("baseUrl"));

		if (config.get("liveSite").equals("false")) {
			// If DEV or BETA, refresh the cache daily
			String dateStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
			requestConfig.put("version", dateStamp);
		}

		// Map the request configuration into the request object
		requestConfig.keySet().forEach(key -> {
			log.debug("Setting {} to {}", key, requestConfig.get(key));
			request.setAttribute(key, requestConfig.get(key));
		});
		request.setAttribute("requestConfig", requestConfig);

        log.debug("Interception! Intercepted path " + request.getRequestURI());
		return true;
	}

	/**
	 * Get the correct mapped hostname depending on if the request has been proxied or not
	 *
	 * @param hostname request hostname
	 * @param port request port
	 * @param forward list of forwards
	 * @return the appropriate mapped hostname
	 */
	private String getMappedHostname(String hostname, Integer port, String forward) {

		if (forward == null) {
			String mappedHostname = "//" + hostname;

			if (port != 80) {
				mappedHostname += ":" + port;
			}

			return mappedHostname;
		}

		return config.get("cmsBaseUrl");
	}
}