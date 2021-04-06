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

package uk.ac.ebi.phenotype.generic.util;

import org.mousephenotype.cda.ri.services.SummaryService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class RegisterInterestUtils {
    private SummaryService summaryService;

    @Inject
    public RegisterInterestUtils(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    /**
     *
     * @return a list of the currently logged in user's gene interest mgi accession ids
     */
    @Secured("ROLE_USER")
    public List<String> getGeneAccessionIds() {
        return summaryService.getGeneAccessionIds(SecurityUtils.getPrincipal());
    }

    @Secured("ROLE_USER")
    public boolean isLoggedIn() {
        return SecurityUtils.isLoggedIn();
    }
}