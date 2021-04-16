/*******************************************************************************
 * Copyright © 2021 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.reports2;

import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.reports2.service.RegistrationOfInterestService;
import org.mousephenotype.cda.reports2.support.ReportUtils;
import org.mousephenotype.cda.reports2.support.Separator;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.MpCsvWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ClassUtils;

import javax.inject.Inject;
import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PURPOSE: This report is meant to answer the question "How many lines are complete?"
 */

// Randomly, when printing the Usage message, spring barks about hibernate not being enabled. The exclusion below
// seems to prevent the warning.
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class})
public class RegistrationOfInterest implements CommandLineRunner {

    private Logger                        logger          = LoggerFactory.getLogger(this.getClass());
    private RegistrationOfInterestService registrationOfInterestService;
    private CommonUtils                   commonUtils     = new CommonUtils();
    private Separator                     separator       = Separator.csv;
    private String                        simpleClassName = ClassUtils.getUserClass(RegistrationOfInterest.class).getSimpleName();
    private String                        reportName;
    private MpCsvWriter                   writer;

    private final String[] heading = {
        "Gene Symbol",
        "Gene Accession Id",
        "Null Allele Production Status",
        "Conditional Allele Production Status",
        "Crispr Allele Production Status",
        "Phenotyping Data Available",
        "Registration Counts Last 3 Months",
        "Registration Counts Last 6 Months",
        "Registration Counts Last 12 Months",
        "Registration Counts Older Than 12 Months",
        "Registration Counts Total",
    };

    @Inject
    public RegistrationOfInterest(RegistrationOfInterestService registrationOfInterestService) {
        this.registrationOfInterestService = registrationOfInterestService;
    }

    @Override
    public void run(String[] args) throws ReportException {

        initialise(args);

        writer.write(heading);

        long                       start        = System.currentTimeMillis();
        final String               NONE         = "None";
        Map<String, List<Integer>> countsByAcc  = registrationOfInterestService.getCountsByAcc();
        Map<String, GeneDTO>       statuses     = registrationOfInterestService.getGeneStatuses();
        final List<List<String>>   unsortedData = new ArrayList<>();
        countsByAcc
            .entrySet()
            .stream()
            .forEach(e -> {
                GeneDTO       g       = statuses.get(e.getKey());
                List<Integer> buckets = e.getValue();
                List<String>  row     = new ArrayList<>();
                row.add(g.getMarkerSymbol());
                row.add(g.getMgiAccessionId());
                row.add(g.getNullAlleleProductionStatus() == null ? NONE : g.getNullAlleleProductionStatus());
                row.add(g.getConditionalAlleleProductionStatus() == null ? NONE : g.getConditionalAlleleProductionStatus());
                row.add(g.getCrisprAlleleProductionStatus() == null ? NONE : g.getCrisprAlleleProductionStatus());
                row.add(g.isPhenotypingDataAvailable() ? "Yes" : "No");
                row.add(Integer.toString(buckets.get(0)));
                row.add(Integer.toString(buckets.get(1)));
                row.add(Integer.toString(buckets.get(2)));
                row.add(Integer.toString(buckets.get(3)));
                row.add(Integer.toString(buckets.get(4)));
                unsortedData.add(row);
            });

        unsortedData     // Sort by geneSymbol (0)
            .stream()
            .sorted(Comparator.comparing((List<String> l) -> l.get(0)))
            .collect(Collectors.toList())
            .forEach(r -> writer.write(r));

        writer.closeQuietly();

        logger.info(String.format(
            "Finished. %s gene rows written in %s",
            unsortedData.size(), commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    public static void main(String args[]) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(RegistrationOfInterest.class)
            .web(WebApplicationType.NONE)
            .bannerMode(Banner.Mode.OFF)
            .logStartupInfo(false)
            .run(args);

        context.close();
    }

    private void initialise(String[] args) throws ReportException {
        Map<String, String> props = null;
        try {
            props = ReportUtils.getReportProperties(args);
        } catch (ReportException e) {
            System.out.println(e.getLocalizedMessage());
            usage();
            System.exit(1);
        }

        if (props.containsKey(ReportUtils.HELP)) {
            usage();
            System.exit(0);
        }

        if (props.containsKey(ReportUtils.REPORT_FORMAT)) {
            separator = Separator.valueOf(props.get(ReportUtils.REPORT_FORMAT));
        }

        String targetDir = props.get(ReportUtils.TARGET_DIRECTORY_ARG);
        reportName = Introspector.decapitalize(simpleClassName) + "." + separator.toString();
        File targetFile = new File(Paths.get(targetDir, reportName).toAbsolutePath().toString());
        try {
            writer = new MpCsvWriter(targetFile.getAbsolutePath(), false, separator.getSeparator());
        } catch (IOException e) {
            throw new ReportException(e);
        }

        logger.info("Target:        {}", targetFile.getAbsolutePath());
        logger.info("Report format: {}", separator.toString());
    }

    private void usage() {
        System.out.printf("Usage: %s [--help] | [[--targetDirectory=xxx] | [--reportFormat={csv | tsv}]] ...\n", simpleClassName);
    }
}
