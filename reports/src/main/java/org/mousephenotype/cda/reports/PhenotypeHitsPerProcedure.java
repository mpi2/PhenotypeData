/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Hits Per Procedure report.
 */
@Component
public class PhenotypeHitsPerProcedure extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("genotype-phenotype-service")
    GenotypePhenotypeService genotypePhenotypeService;

    public PhenotypeHitsPerProcedure() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if (!errors.isEmpty()) {
            logger.error("HitsPerProcedureReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        int count = 0;
        long start = System.currentTimeMillis();

        List<String[]> rows;

        try {

            List<String> heading = Arrays.asList(
                    "Procedure Id",
                    "Procedure Name",
                    "Gene Symbols",
                    "Gene Accession Ids",
                    "# Significant Hits");
            csvWriter.write(heading);
            rows = genotypePhenotypeService.getHitsDistributionByProcedure(resources);
            csvWriter.writeRowsOfArray(rows);
            count += 1 + rows.size();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format(
                "Finished. %s rows written in %s",
                count, commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}