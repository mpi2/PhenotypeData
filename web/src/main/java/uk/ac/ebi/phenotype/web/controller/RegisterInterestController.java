/*******************************************************************************
 * Copyright 2018 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.phenotype.web.controller;

import org.mousephenotype.cda.utilities.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.phenotype.generic.util.RegisterInterestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.Map;


@Controller
public class RegisterInterestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

    @NotNull
    @Value("${paBaseUrl}")
    private String paBaseUrl;

    @NotNull
    @Value("${riBaseUrl}")
    private String riBaseUrl;

    @Autowired
	private RegisterInterestUtils riUtils;


	@RequestMapping(value = "/riRegistration/gene", method = RequestMethod.GET)
	public String riRegistrationGene(
			@RequestParam("geneAccessionId") String geneAccessionId,
			@RequestParam("target") String target,
			HttpServletRequest request,
			HttpServletResponse response) {

        String targetWithScheme    = (target == null ? null : UrlUtils.urlWithScheme(request.getRequestURL().toString(), target));

		riUtils.registerGene(request, geneAccessionId);
		return "redirect:" + targetWithScheme;
	}

	/**
	 * This endpoint is called to log in to Register Interest. After a successful login, the caller is redirected back
	 * to {code target} along with the Register Interest JSESSIONID token.
	 *
	 * @param target The target to redirect to after successful authentication
	 * @return
	 */
	@RequestMapping(value = "/riLogin", method = RequestMethod.GET)
	public String riRegistrationGetGene(
	        HttpServletRequest request,
			@RequestParam(value = "target", required = false) String target) {

        String paBaseUrlWithScheme = UrlUtils.urlWithScheme(request.getRequestURL().toString(), paBaseUrl);
        String riBaseUrlWithScheme = UrlUtils.urlWithScheme(request.getRequestURL().toString(), riBaseUrl);

		if (target == null) {
			target = paBaseUrlWithScheme + "/search/gene?kw=*";
		}

		return "redirect:" + riBaseUrlWithScheme + "/login?target=" + target;
	}

	@RequestMapping(value = "/riSuccessHandler", method = RequestMethod.GET)
	public String riSuccessHandler(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "riToken", required = false) String riToken,
			HttpServletRequest request
	) {

        String paBaseUrlWithScheme = UrlUtils.urlWithScheme(request.getRequestURL().toString(), paBaseUrl);
        String targetWithScheme    = (target == null ? null : UrlUtils.urlWithScheme(request.getRequestURL().toString(), target));

		if (target == null) {
			targetWithScheme = paBaseUrlWithScheme + "/search/gene?kw=*";
		}

		if (riToken != null) {
			request.getSession().setAttribute("riToken", riToken);
		}

		return "redirect:" + targetWithScheme;
	}

    @RequestMapping(value = "/riUnregistration/gene", method = RequestMethod.GET)
    public String riUnregistrationGene(
            @RequestParam("geneAccessionId") String geneAccessionId,
			@RequestParam("target") String target,
            HttpServletRequest request) {

        String targetWithScheme = (target == null ? null : UrlUtils.urlWithScheme(request.getRequestURL().toString(), target));

        riUtils.unregisterGene(request, geneAccessionId);
        return "redirect:" + targetWithScheme;
    }
}