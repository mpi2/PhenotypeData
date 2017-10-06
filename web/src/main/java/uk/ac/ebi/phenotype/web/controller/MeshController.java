/** *****************************************************************************
 * Copyright 2017 QMUL - Queen Mary University of London
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
 ****************************************************************************** */
package uk.ac.ebi.phenotype.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for mesh terms.
 *
 * This class contains a url mapping, presumably for Medical Subject Headings.
 * This declaration used to be in the SearchController, now moved here.
 *
 */
@Controller
public class MeshController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    /**
     * TK: requires documentation
     * 
     * @param request
     * @param model
     * @return 
     */
    @RequestMapping(value = "/mesh", method = RequestMethod.GET)
    public String mesh(HttpServletRequest request, Model model) {
        LOGGER.info("mesh");
        return "mesh";
    }
}
