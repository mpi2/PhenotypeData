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
import org.mousephenotype.cda.solr.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.beans.Introspector;
import java.io.IOException;
import java.util.List;

/**
 * Lac-Z Expression report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class LaczExpressionReport extends AbstractReport {

    protected Logger       logger = LoggerFactory.getLogger(this.getClass());
    protected String       reportsHostname;
    protected ImageService imageService;

    public final String IMAGE_COLLECTION_LINK_BASE_KEY = "image_collection_link_base";
    public String imageCollectionLinkBase = "https://www.mousephenotype.org/data";

    @Inject
    public LaczExpressionReport(ImageService imageService) {
        super();
        this.imageService = imageService;
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException, IOException, SolrServerException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("LaczExpressionReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);


        PropertySource ps = new SimpleCommandLinePropertySource(args);
        if (ps.containsProperty(IMAGE_COLLECTION_LINK_BASE_KEY)) {
            imageCollectionLinkBase = ps.getProperty(IMAGE_COLLECTION_LINK_BASE_KEY).toString();

            throw new ReportException("Required reports_hostname parameter is missing. Format is like http://www.mousephenotype.org.");
        }

        long start = System.currentTimeMillis();

        List<List<String>> results = imageService.getLaczExpressionSpreadsheet(imageCollectionLinkBase);
        csvWriter.writeRows(results);

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    protected void initialise(String[] args) throws ReportException {
        super.initialise(args);
    }
}