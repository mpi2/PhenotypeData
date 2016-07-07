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

package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.AnatomyService;
import org.mousephenotype.cda.solr.service.MpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by ckc on 21/03/2016.
 * @author ilinca //edits
 */

@Controller
public class OntologyBrowserController {

    @Autowired
    MpService mpService;
    @Autowired
    AnatomyService anatomyService;
    
    @RequestMapping(value = "/ontologyBrowser", method = RequestMethod.GET)
    public String getParams( @RequestParam(value = "termId", required = true) String termId, HttpServletRequest request, Model model)
    throws IOException, URISyntaxException, SQLException, SolrServerException {


        model.addAttribute("termId", termId);
        if ( termId.startsWith("MA:") || termId.startsWith("EMAPA:")){
            model.addAttribute("scrollToNode", "1");
        }
        else if (termId.startsWith("MP:")) {
            model.addAttribute("scrollToNode", mpService.getPhenotype(termId).getScrollNode());
        }
        return "ontologyBrowser";
    }


    @RequestMapping(value = "/ontologyBrowser2", method = RequestMethod.GET)
    public ResponseEntity<String> getTreeJson( @RequestParam(value = "node", required = true) String rootId,
                                               @RequestParam(value = "termId", required = true) String termId,
            @RequestParam(value = "expandNodeIds", required = false) List<String> expandNodes, HttpServletRequest request, Model model)
    throws IOException, URISyntaxException, SQLException, SolrServerException {

        ResponseEntity<String> res = null;

        if (rootId.equalsIgnoreCase("src")) {

            if ( termId.startsWith("MA:") || termId.startsWith("EMAPA:")) {
                res = new ResponseEntity<String>(anatomyService.getSearchTermJson(termId), createResponseHeaders(), HttpStatus.CREATED);
            }
            else if (termId.startsWith("MP:")) {
                res = new ResponseEntity<String>(mpService.getSearchTermJson(termId), createResponseHeaders(), HttpStatus.CREATED);
            }

            return res;
        }
        else {

            if (termId.startsWith("MA:") || termId.startsWith("EMAPA:")) {
                res = new ResponseEntity<String>(anatomyService.getChildrenJson(rootId, termId), createResponseHeaders(), HttpStatus.CREATED);
            } else if (termId.startsWith("MP:")) {
                res = new ResponseEntity<String>(mpService.getChildrenJson(rootId, termId), createResponseHeaders(), HttpStatus.CREATED);
            }

            return res;
        }
    }




    private HttpHeaders createResponseHeaders() {
    	
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        return responseHeaders;
    }
}
