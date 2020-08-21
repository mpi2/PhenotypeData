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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author jwarren
 */
@Controller
@RequestMapping("/documentation")
public class DocumentationController {

    @RequestMapping("/{page}")
    public String documentation(@PathVariable String page, HttpServletRequest request){

        String helpBase = (request.getAttribute("cmsBaseUrl").toString().startsWith("http")) ?
                request.getAttribute("cmsBaseUrl").toString() :
                request.getScheme() + "://" + request.getAttribute("cmsBaseUrl");

        if (page.equalsIgnoreCase("data-access-api-genotype-phenotype")) {
            return "redirect:" + helpBase + "/help/programmatic-data-access/phenotype-calls/";
        } else if (page.equalsIgnoreCase("data-access-api-statistical-result")) {
            return "redirect:" + helpBase + "/help/programmatic-data-access/statistical-results/";
        }
        return "documentation/" + page;
    }
}
