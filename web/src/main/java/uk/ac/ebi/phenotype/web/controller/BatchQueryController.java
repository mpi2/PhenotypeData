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
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TK: class description?
 *
 * Comment: This class contains functions previously defined in
 * SearchController. As the urls do not seem search related, they are moved
 * here. This class requires documentation.
 *
 */
@Controller
public class BatchQueryController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    /**
     * TK: requires documentation
     *
     * @param core
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/batchquery2", method = RequestMethod.GET)
    public @ResponseBody
    String fetchDataFields(
            @RequestParam(value = "core", required = false) String core,
            HttpServletRequest request,
            Model model) {

        LOGGER.info("fetchDataFields core: " + core);
        return Tools.fetchOutputFieldsCheckBoxesHtml(core);

    }

    /**
     * TK: requires documentation
     *
     * @param core
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/batchquery2-1", method = RequestMethod.GET)
    public @ResponseBody
    String fetchDataFields2(
            @RequestParam(value = "core", required = false) String core,
            HttpServletRequest request,
            Model model) {

        LOGGER.info("fetchDataFields2 core: " + core);
        return Tools.fetchOutputFieldsCheckBoxesHtml2(core);

    }

    /**
     * TK: requires documentation
     *
     * @param core
     * @param fllist
     * @param idlist
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/batchQuery", method = RequestMethod.GET)
    public String loadBatchQueryPage(
            @RequestParam(value = "core", required = false) String core,
            @RequestParam(value = "fllist", required = false) String fllist,
            @RequestParam(value = "idlist", required = false) String idlist,
            HttpServletRequest request,
            Model model) {

        LOGGER.info("loadBatchQueryPage");
        String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml(core);
        model.addAttribute("outputFields", outputFieldsHtml);

        if (idlist != null) {
            model.addAttribute("core", core);
            model.addAttribute("fllist", fllist);
            model.addAttribute("idlist", idlist);
        }

        return "batchQuery";
    }
}
