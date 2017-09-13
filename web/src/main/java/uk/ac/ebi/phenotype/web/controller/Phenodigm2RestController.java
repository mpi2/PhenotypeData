/*******************************************************************************
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
 *******************************************************************************/

package uk.ac.ebi.phenotype.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenodigm2.*;

/**
 * Controller that responds to requests for phenodigm data.
 *  
 */
@Controller
public class Phenodigm2RestController {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(Phenodigm2RestController.class);

    @Autowired
    private WebDao phenoDigm2Dao;
      
    //AJAX method
    @RequestMapping(value="/api/phenodigm2", method=RequestMethod.GET)
    public String getAjaxData(@RequestParam String requestPageType, @RequestParam String diseaseId, @RequestParam String geneId, Model model) {        
        LOGGER.info(String.format("AJAX call for %s %s from %s page", diseaseId, geneId, requestPageType));                       
        return "404";
    }
}
