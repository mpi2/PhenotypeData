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
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data Overview report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class DataOverview extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("genotype-phenotype-service")
    GenotypePhenotypeService genotypePhenotypeService;

    @Autowired
    ImageService imageService;

    @Autowired
    ObservationService observationService;

    public DataOverview() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("DataOverviewReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<List<String[]>> result = new ArrayList<>();
        List<String[]> overview = new ArrayList<>();
        String[] forArrayType = new String[0];

        try {

            List<String> row = new ArrayList<>();
            row.add("# phenotyped genes");
            row.add(Integer.toString(observationService.getAllGeneIdsByResource(resources, false).size()));
            overview.add(row.toArray(forArrayType));

            row = new ArrayList<>();
            row.add("# phenotyped mutant lines");
            row.add(Integer.toString(observationService.getAllColonyIdsByResource(resources, true).size()));
            overview.add(row.toArray(forArrayType));

            row = new ArrayList<>();
            row.add("# phenotype hits");
            row.add(Long.toString(genotypePhenotypeService.getNumberOfDocuments(resources)));
            overview.add(row.toArray(forArrayType));

            row = new ArrayList<>();
            row.add("# data points");
            row.add(Long.toString(observationService.getNumberOfDocuments(resources, false)));
            overview.add(row.toArray(forArrayType));

            row = new ArrayList<>();
            row.add("# images");
            row.add(Long.toString(imageService.getNumberOfDocuments(resources, false)));
            overview.add(row.toArray(forArrayType));

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        result.add(overview);

        List<String[]> linesPerCenter = new ArrayList<>();
        try {
            Map<String, Set<String>> observationServiceResult = observationService.getColoniesByPhenotypingCenter(resources, null);
            for (String center: observationServiceResult.keySet()){
                String[] row= {"# mutant lines phenotyped at " + center, Integer.toString(observationServiceResult.get(center).size())};
                linesPerCenter.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        result.add(linesPerCenter);


        List<String> genesAll;
        List<String> genesComplete;
        List<String[]> mpTable = new ArrayList<>();

        try {

            genesAll = observationService.getGenesWithMoreProcedures(1, resources);
            genesComplete = observationService.getGenesWithMoreProcedures(13, resources);

            // Process top level MP terms
            String mpTopLevelGenePivot = GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + "," + GenotypePhenotypeDTO.MARKER_SYMBOL;
            Map<String, List<String>> topLevelMpTermByGeneMapAll = genotypePhenotypeService.getMpTermByGeneMap(genesAll, mpTopLevelGenePivot, resources);
            Map<String, List<String>> topLevelMpTermByGeneMapComplete = genotypePhenotypeService.getMpTermByGeneMap(genesComplete, mpTopLevelGenePivot, resources);

            String[] headerTopLevel = {"Top Level MP term", "# associated genes with >= 1 procedure done", "% associated genes of all genes with >= 1 procedure done", "# associated genes with >= 13 procedures done", "% associated genes of all genes with >= 13 procedures done"};
            mpTable.add(headerTopLevel);
            for(String mpTerm : topLevelMpTermByGeneMapAll.keySet()) {
                String[] row = {
                        mpTerm,
                        Integer.toString(topLevelMpTermByGeneMapAll.get(mpTerm).size()),
                        (float) topLevelMpTermByGeneMapAll.get(mpTerm).size() / genesAll.size() * 100 +"%",
                        Integer.toString(topLevelMpTermByGeneMapComplete.get(mpTerm).size()),
                        (float) topLevelMpTermByGeneMapComplete.get(mpTerm).size() / genesComplete.size() * 100 +"%"};
                mpTable.add(row);
            }

            String[] emptyRow = {""};
            mpTable.add(emptyRow);

            // Process granular MP terms
            String mpGenePivot = GenotypePhenotypeDTO.MP_TERM_NAME + "," + GenotypePhenotypeDTO.MARKER_SYMBOL;
            Map<String, List<String>> mpTermByGeneMapAll = genotypePhenotypeService.getMpTermByGeneMap(genesAll, mpGenePivot, resources);
            Map<String, List<String>> mpTermByGeneMapComplete = genotypePhenotypeService.getMpTermByGeneMap(genesComplete, mpGenePivot, resources);

            String[] headerMp = {"MP term", "# Associated Genes With >= 1 Procedure Done", "% Associated Genes of All Genes With >= 1 Procedure Done", "# Associated Genes With >= 13 Procedures Done", "% Associated Genes of All Genes With >= 13 Procedures Done"};
            mpTable.add(headerMp);
            for(String mpTerm : mpTermByGeneMapAll.keySet()) {
                String[] row = {
                        mpTerm,
                        Integer.toString(mpTermByGeneMapAll.get(mpTerm).size()),
                        (float) mpTermByGeneMapAll.get(mpTerm).size() / genesAll.size() * 100 +"%",
                        Integer.toString(mpTermByGeneMapComplete.get(mpTerm).size()),
                        (float) mpTermByGeneMapComplete.get(mpTerm).size() / genesComplete.size() * 100 +"%"};
                mpTable.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        result.add(mpTable);
        csvWriter.writeRowsMulti(result);

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