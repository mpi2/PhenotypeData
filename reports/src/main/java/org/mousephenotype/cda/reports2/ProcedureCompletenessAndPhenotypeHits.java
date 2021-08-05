/*******************************************************************************
 * Copyright © 2020 EMBL - European Bioinformatics Institute
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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.reports2.service.ProcedureCompletenessService;
import org.mousephenotype.cda.reports2.support.ReportUtils;
import org.mousephenotype.cda.reports2.support.Separator;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.MpCsvWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.ClassUtils;

import javax.inject.Inject;
import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PURPOSE: This report is meant to answer the question "How many lines are complete?"
 */

@ComponentScan({"org.mousephenotype.cda.reports2"})
public class ProcedureCompletenessAndPhenotypeHits implements CommandLineRunner {

    private Separator separator = Separator.csv;
    private MpCsvWriter writer;
    private Map<String, MpDTO> mpToTopLevelMp;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProcedureCompletenessService procedureCompletenessService;
    private final MpService mpService;
    private final CommonUtils commonUtils = new CommonUtils();
    private final String simpleClassName = ClassUtils.getUserClass(ProcedureCompletenessAndPhenotypeHits.class).getSimpleName();

    private final String[] heading = {
        "Gene Symbol",
        "Gene Accession Id",
        "Allele Symbol",
        "Allele Accession Id",
        "Background Strain Name",
        "Background Strain Accession Id",
        "Colony Id",
        "Phenotyping Center",
        "Zygosity",
        "Life Stage",

        "Procedure Name: Successful",
        "Procedure Stable Id: Successful",
        "Procedure Count: Successful",
        "Procedure Name: NotProcessed",
        "Procedure Stable Id: NotProcessed",
        "Procedure Count: NotProcessed",

        "Parameter Name: Successful",
        "Parameter Stable Id: Successful",
        "Parameter Count: Successful",
        "Parameter Name: NotProcessed",
        "Parameter Stable Id: NotProcessed",
        "Parameter Count: NotProcessed",

        "Top-level MP Name: Successful",
        "Top-level MP Accession Id: Successful",
        "Top-level MP Count: Successful",

        "Top-level MP Name: Significant",
        "Top-level MP Accession Id: Significant",
        "Top-level MP Count: Significant",

        "MP Name: Significant",
        "MP Accession Id: Significant",
        "MP Count: Significant"
    };

    @Inject
    public ProcedureCompletenessAndPhenotypeHits(ProcedureCompletenessService procedureCompletenessService,
                                                 MpService mpService) {
        this.procedureCompletenessService = procedureCompletenessService;
        this.mpService = mpService;
    }

    @Override
    public void run(String[] args) throws ReportException {

        initialise(args);

        long         start     = System.currentTimeMillis();
        int          geneCount = 0;
        List<String> geneSymbols;
        try {
            geneSymbols = procedureCompletenessService.getGeneSymbols();
            Collections.sort(geneSymbols);
            writer.write(heading);

            for (String geneSymbol : geneSymbols) {
                List<StatisticalResultDTO> dtos = procedureCompletenessService.getGeneData(geneSymbol);

                // Group by: [geneSymbol,] colonyId, zygosity, lifeStage, one per row
                dtos
                    .stream()
                    .collect(Collectors.groupingBy(d ->
                        d.getColonyId() + "_" +
                            (d.getZygosity() == null ? "null" : d.getZygosity()) + "_" +
                            (d.getLifeStageName() == null ? "null" : d.getLifeStageName())))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(sr -> writer.write(buildRow(sr.getValue())));

                if ((geneCount > 0) && (geneCount % 1000 == 0)) {
                    writer.flushQuietly();
                    logger.info("Processing {} of {} genes", geneCount, geneSymbols.size());
                }
                geneCount++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        writer.closeQuietly();

        logger.info(String.format(
            "Finished. %s genes written in %s",
            geneCount, commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ProcedureCompletenessAndPhenotypeHits.class)
            .web(WebApplicationType.NONE)
            .bannerMode(Banner.Mode.OFF)
            .logStartupInfo(false)
            .run(args);

        context.close();
    }

    private List<String> buildRow(List<StatisticalResultDTO> dtos) {
        List<String>         row = new ArrayList<>();
        StatisticalResultDTO d   = dtos.get(0);
        row.add(d.getMarkerSymbol());
        row.add(d.getMarkerAccessionId());
        row.add(d.getAlleleSymbol());
        row.add(d.getAlleleAccessionId());
        row.add(d.getStrainName());
        row.add(d.getStrainAccessionId());
        row.add(d.getColonyId());
        row.add(d.getPhenotypingCenter());
        row.add(d.getZygosity());
        row.add(d.getLifeStageName());

        row.addAll(buildStatusRow(dtos));

        return row;
    }

    private List<String> buildStatusRow(List<StatisticalResultDTO> dtos) {
        List<String> row = new ArrayList<>();
        Map<String, List<StatisticalResultDTO>> byStatus = dtos
            .stream()
            .collect(Collectors.groupingBy(StatisticalResultDTO::getStatus));

        Statuses success      = new Statuses(byStatus.get("Successful"));
        Statuses notProcessed = new Statuses(byStatus.get("NotProcessed"));

        // Remove those procedures that are Successful from the NotProcessed set
        notProcessed.procStatusSet.removeAll(success.procStatusSet);

        setProcedureStatus(row, success);
        setProcedureStatus(row, notProcessed);

        setParameterStatus(row, success);
        setParameterStatus(row, notProcessed);

        row.add(success.tlmpStatusSet.isEmpty() ? Constants.NONE : Objects.requireNonNull(success.tlmpStatus).name);
        row.add(success.tlmpStatusSet.isEmpty() ? Constants.NONE : success.tlmpStatus.id);
        row.add(success.tlmpStatusSet.isEmpty() ? "0" : Integer.toString(success.tlmpStatusSet.size()));

        row.add(success.tlmpSignificantStatusSet.isEmpty() ? Constants.NONE : Objects.requireNonNull(success.tlmpSignificantStatus).name);
        row.add(success.tlmpSignificantStatusSet.isEmpty() ? Constants.NONE : success.tlmpSignificantStatus.id);
        row.add(success.tlmpSignificantStatusSet.isEmpty() ? "0" : Integer.toString(success.tlmpSignificantStatusSet.size()));

        row.add(success.mpStatusSet.isEmpty() ? Constants.NONE : Objects.requireNonNull(success.mpStatus).name);
        row.add(success.mpStatusSet.isEmpty() ? Constants.NONE : success.mpStatus.id);
        row.add(success.mpStatusSet.isEmpty() ? "0" : Integer.toString(success.mpStatusSet.size()));

        return row;
    }

    private void setParameterStatus(List<String> row, Statuses statuses) {
        row.add(statuses.parmStatusSet.isEmpty() ? Constants.NONE : Objects.requireNonNull(statuses.parmStatus).name);
        row.add(statuses.parmStatusSet.isEmpty() ? Constants.NONE : Objects.requireNonNull(statuses.parmStatus).id);
        row.add(statuses.parmStatusSet.isEmpty() ? "0" : Integer.toString(statuses.parmStatusSet.size()));
    }

    private void setProcedureStatus(List<String> row, Statuses statuses) {
        row.add(statuses.procStatusSet.isEmpty() ? Constants.NONE : Objects.requireNonNull(statuses.procStatus).name);
        row.add(statuses.procStatusSet.isEmpty() ? Constants.NONE : Objects.requireNonNull(statuses.procStatus).id);
        row.add(statuses.procStatusSet.isEmpty() ? "0" : Integer.toString(statuses.procStatusSet.size()));
    }

    private class Statuses {
        public final IdName parmStatus;
        public final IdName procStatus;
        public final IdName tlmpStatus;
        public final IdName tlmpSignificantStatus;
        public final IdName mpStatus;

        public final Set<IdName> parmStatusSet            = new TreeSet<>();
        public final Set<IdName> procStatusSet            = new TreeSet<>();
        public final Set<IdName> tlmpStatusSet            = new TreeSet<>();
        public final Set<IdName> tlmpSignificantStatusSet = new TreeSet<>();
        public final Set<IdName> mpStatusSet              = new TreeSet<>();

        public Statuses(List<StatisticalResultDTO> dtos) {
            if (dtos == null) {
                parmStatus = null;
                procStatus = null;
                tlmpStatus = null;
                tlmpSignificantStatus = null;
                mpStatus = null;
            } else {
                for (StatisticalResultDTO d : dtos) {

                    parmStatusSet.add(new IdName(d.getParameterName(), d.getParameterStableId()));
                    for (String procId : d.getProcedureStableId()) {
                        procStatusSet.add(new IdName(d.getProcedureName(), procId));
                    }

                    if (d.getMpTermName() != null) {
                        mpStatusSet.add(new IdName(d.getMpTermName(), d.getMpTermId()));
                        MpDTO tlmpDto = mpToTopLevelMp.get(d.getMpTermId());
                        // Skip terms with no top-level. They are already top-level and have null top level mp terms
                        if ((tlmpDto != null) && (tlmpDto.getTopLevelMpTerm() != null)) {
                            for (int i = 0; i < tlmpDto.getTopLevelMpTerm().size(); i++) {
                                tlmpSignificantStatusSet.add(new IdName(tlmpDto.getTopLevelMpTerm().get(i),
                                        tlmpDto.getTopLevelMpId().get(i)));
                            }
                        }
                    }

                    if (d.getTopLevelMpTermName() != null) {
                        for (int i = 0; i < d.getTopLevelMpTermName().size(); i++) {
                            tlmpStatusSet.add(new IdName(d.getTopLevelMpTermName().get(i),
                                    d.getTopLevelMpTermId().get(i)));
                        }
                    }
                }

                parmStatus = new IdName(parmStatusSet);
                procStatus = new IdName(procStatusSet);
                tlmpStatus = new IdName(tlmpStatusSet);
                tlmpSignificantStatus = new IdName(tlmpSignificantStatusSet);
                mpStatus = new IdName(mpStatusSet);
            }
        }
    }

    public class IdName implements Comparable<IdName> {
        public final String name;
        public final String id;

        public IdName(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public IdName(Set<IdName> set) {
            this.name = set.stream().map(x -> x.name).collect(Collectors.joining("|"));
            this.id = set.stream().map(x -> x.id).collect(Collectors.joining("|"));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IdName)) return false;
            IdName idName = (IdName) o;
            return name.equals(idName.name) &&
                id.equals(idName.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, id);
        }

        @Override
        public int compareTo(IdName o) {
            return StringUtils.compareIgnoreCase(this.name, o.name);
        }
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
        String reportName = Introspector.decapitalize(simpleClassName) + "." + separator.toString();
        File targetFile = new File(Paths.get(targetDir, reportName).toAbsolutePath().toString());
        try {
            writer = new MpCsvWriter(targetFile.getAbsolutePath(), false, separator.getSeparator());
            mpToTopLevelMp = mpService.getMpToTopLevelMp();
        } catch (IOException | SolrServerException e) {
            throw new ReportException(e);
        }

        logger.info("Target:        {}", targetFile.getAbsolutePath());
        logger.info("Report format: {}", separator.toString());
    }

    private void usage() {
        System.out.printf("Usage: %s [--help] | [[--targetDirectory=xxx] | [--reportFormat={csv | tsv}]] ...\n", simpleClassName);
    }
}
