/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.create.extract.impress;

import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.OntologyTermAnomaly;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.*;

/**
 * Updates the impress_x_y database terms with the latest terms.
 *
 * Created by mrelac on 31/08/2016.
 *
 */
public class ImpressUpdater implements CommandLineRunner {

    private CdaSqlUtils                 cdaSqlUtils;
    private CommonUtils                 commonUtils = new CommonUtils();
    private NamedParameterJdbcTemplate  jdbcImpress;
    private final Logger                logger = LoggerFactory.getLogger(this.getClass());


    @Inject
    @Lazy
    public ImpressUpdater(NamedParameterJdbcTemplate jdbcImpress, CdaSqlUtils cdaSqlUtils) {
        this.jdbcImpress = jdbcImpress;
        this.cdaSqlUtils = cdaSqlUtils;
    }


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ImpressUpdater.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }


    @Override
    public void run(String... strings) {

        Assert.notNull(jdbcImpress, "jdbcImpress must be set");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must be set");

        long startStep = new Date().getTime();
        String query = "SELECT ontology_acc FROM phenotype_parameter_ontology_annotation WHERE ontology_acc IS NOT NULL";

        List<String> ontologyAccessionIds = jdbcImpress.queryForList(query, new HashMap<>(), String.class);


        // FIXME Don't use cdaSqlUtils to do this work. See MPII-2953
        Set<OntologyTermAnomaly> anomalies = cdaSqlUtils.checkAndUpdateOntologyTerms(jdbcImpress, ontologyAccessionIds, "phenotype_parameter_ontology_annotation", "ontology_acc");

        if ( ! anomalies.isEmpty()) {
            System.out.println("\nanomalies:");
            List<String> anomalyReasons = new ArrayList<>();
            for (OntologyTermAnomaly anomaly : anomalies) {
                anomalyReasons.add(anomaly.getReason());
            }
            Collections.sort(anomalyReasons);
            for (String anomalyReason : anomalyReasons) {
                System.out.println("\t" + anomalyReason);
            }
        }

        // FIXME Add a count of the number of anomalies that were updated.
        logger.debug("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
    }
}