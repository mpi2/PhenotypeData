/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this targetFile except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.reports;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.PhenotypeCenterProcedureCompletenessAllService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.List;

/**
 * Procedure Completeness report.
 *
 * Created by mrelac on 28/02/2019.
 */

@Component
public class ProcedureCompletenessAll extends AbstractReport {

    protected Logger                                         logger = LoggerFactory.getLogger(this.getClass());
    private   PhenotypeCenterProcedureCompletenessAllService phenotypeCenterProcedureCompletenessAllService;


    public ProcedureCompletenessAll(
            PhenotypeCenterProcedureCompletenessAllService phenotypeCenterProcedureCompletenessAllService
    ) {
        super();
        this.phenotypeCenterProcedureCompletenessAllService = phenotypeCenterProcedureCompletenessAllService;
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ProcedureCompletenessReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<String[]> result;
        try {
            result = phenotypeCenterProcedureCompletenessAllService.getCentersProgressByStrainCsv();
        } catch (Exception e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        csvWriter.writeRowsOfArray(result);

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format(
            "Finished. %s rows written in %s",
            result.size(), commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}