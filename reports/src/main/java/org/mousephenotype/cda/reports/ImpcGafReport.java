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
import org.mousephenotype.cda.db.dao.AnalyticsDAO;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * IMPC GAF report.
 *
 * Created by mrelac on 08/02/2016.
 */
@Component
public class ImpcGafReport extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("postqcService")
    PostQcService genotypePhenotypeService;

    @Autowired
    ObservationService observationService;

    @Autowired
    GeneService geneService;

    @Autowired
    AnalyticsDAO analyticsDAO;

    public ImpcGafReport() {
        super(ReportFormat.tsv);
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ImpcGafReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        // According to Terry, there is a 1 to 1 relationship between Gene Symbol and MGI Gene Id, so we can just use a map, keyed by gene name, to accumulate the MGI Gene Ids.
        Map<String, String> geneSymbolToId = new HashMap<>();
        List<String> headerParams = Arrays.asList( new String[] {
                "DB", "DB Object ID", "DB Object Symbol","Qualifier", "MP ID", "DB:Reference (|DB:Reference)", "Evidence Code",
                "With (or) From", "Aspect", "DB Object Name", "DB Object Synonym (|Synonym)", "DB Object Type", "Taxon(|taxon)",
                "Date", "Assigned By", "Annotation Extension", "Gene Product Form ID"});

        csvWriter.writeRow(headerParams);

        try {

            List<GenotypePhenotypeDTO> gpDTOList = genotypePhenotypeService.getAllGenotypePhenotypes(resources);

            Map<String, GenotypePhenotypeDTO> geneToPhenotypes = new TreeMap<>();        // key is MGI Gene Id + "_" + MP ID. Value is GenotypePheontypeDTO.

            // Gather the data into a TreeMap so it appears sorted by gene symbol and MpID and contains no duplicate key entries.
            for (GenotypePhenotypeDTO gpDTO : gpDTOList) {
                String mgiGeneId = gpDTO.getMarkerAccessionId();
                GeneDTO geneDTO = geneService.getGeneById(mgiGeneId);

                // Exclude LacZ calls
                if( gpDTO.getParameterStableId().contains("ALZ")) {
                    continue;
                }

                // Exclude all non-MP terms.
                if ((gpDTO.getMpTermName() == null) || (gpDTO.getMpTermName().isEmpty())) {
                    continue;
                }

                String key = geneDTO.getMarkerSymbol() + "_" + gpDTO.getMpTermId();
                geneToPhenotypes.put(key, gpDTO);
            }

            // Write the data.
            Date releaseDate = commonUtils.tryParseDate(new SimpleDateFormat("dd MMM yyyy"), analyticsDAO.getMetaData().get("data_release_date"));
            String dataReleaseDate = (releaseDate == null ? "" : new SimpleDateFormat("yyyyMMdd").format(releaseDate));

            for (GenotypePhenotypeDTO gpDTO : geneToPhenotypes.values()) {
                String mgiGeneId = gpDTO.getMarkerAccessionId();
                GeneDTO geneDTO = geneService.getGeneById(mgiGeneId);

                String geneSymbol = geneDTO.getMarkerSymbol();
                String mpId = gpDTO.getMpTermId();geneDTO.getMpId();
                String geneName = geneDTO.getMarkerName();

                List<String> row = Arrays.asList(new String[] {
                    "IMPC"                  // DB
                    , mgiGeneId             // DB Object ID (MGI Gene Id)
                    , geneSymbol            // DB Object Symbol (Gene Symbol)
                    , ""                    // Qualifier
                    , mpId                  // MP ID (MP ID)
                    , "PMID:24194600"       // DB:Reference
                    , "IMP"                 // Evidence Code
                    , ""                    // With (or From)
                    , "MP"                  // Aspect
                    , geneName              // DB Object Name (Gene Name)
                    , ""                    // DB Object Synonym
                    , "gene"                // DB Object Type
                    , "taxon:10090"         // Taxon
                    , dataReleaseDate       // Date (Date of data release) YYYYMMDD
                    , "IMPC"                // Assigned By
                    , ""                    // Annotation Extension
                    , ""                    // Gene Product Form ID

                });

                csvWriter.writeRow(row);
            }

        } catch (SolrServerException | SQLException | IOException e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}