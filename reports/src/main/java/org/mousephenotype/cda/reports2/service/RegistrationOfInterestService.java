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

package org.mousephenotype.cda.reports2.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.ri.entities.ContactGene;
import org.mousephenotype.cda.ri.utils.RiSqlUtils;
import org.mousephenotype.cda.solr.service.BasicService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RegistrationOfInterestService extends BasicService {

    private final Logger      logger = LoggerFactory.getLogger(this.getClass());
    private final GeneService geneService;
    private final RiSqlUtils  riSqlUtils;

    @Inject
    public RegistrationOfInterestService(
        GeneService geneService,
        RiSqlUtils riSqlUtils
    ) {
        super();
        this.geneService = geneService;
        this.riSqlUtils = riSqlUtils;
    }

    public Map<String, List<Integer>> getCountsByAcc() {
        final List<List<String>> data = new ArrayList<>();
        final Map<String, List<LocalDateTime>> createdAtByAcc =
            riSqlUtils.getContactGenes()
                .stream()
                .collect(Collectors.groupingBy(ContactGene::getGeneAccessionId,
                    Collectors.mapping(cg -> cg.getCreatedAt()
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        Collectors.toList())));

        // bucket values: [0] = 0 to 3 months, [1] = 3 to 6 months, [2] = 6 to 12 months, [3] = > 12 months, [4] = total
        final LocalDateTime   now = LocalDateTime.now();
        final LocalDateTime[] p   = {now.minusMonths(3), now.minusMonths(6), now.minusMonths(12)};

//        logger.info("less03: {}", p[0].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        logger.info("less06: {}", p[1].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        logger.info("less12: {}", p[2].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        final Map<String, Integer[]> countsByAcc = new HashMap<>();
        createdAtByAcc
            .entrySet()
            .stream()
            .forEach(e -> {
                final Integer[] bucket = {0, 0, 0, 0, 0};
                e.getValue()
                    .stream()
                    .forEach(d -> {
                        if (d.isAfter(p[0])) bucket[0]++;
                        else if (d.isAfter(p[1])) bucket[1]++;
                        else if (d.isAfter(p[2])) bucket[2]++;
                        else bucket[3]++;
                        bucket[4]++;
                    });
                countsByAcc.put(e.getKey(), bucket);
            });
        return countsByAcc
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                e -> Arrays.stream(e.getValue()).collect(Collectors.toList())));
    }

    public Map<String, GeneDTO> getGeneStatuses() throws ReportException {
        Map<String, GeneDTO> statuses = new HashMap<>();
        try {
            statuses = geneService.getRegisterInterestGeneDetails()
                .stream()
                .collect(Collectors.toMap(GeneDTO::getMgiAccessionId, Function.identity()));
        } catch (IOException | SolrServerException e) {
            throw new ReportException(e.getLocalizedMessage(), e);
        }
        return statuses;
    }
}
