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
import org.mousephenotype.cda.reports.support.ReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.File;
import java.util.List;

/**
 * Bone Mineral Stats report.
 *
 * Created by mrelac on 28/07/2015.
 */
@SpringBootApplication
@Component
public class BmdStatsReport extends BoneMineralAbstractReport {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String parameter = "IMPC_DXA_004_001";

    public BmdStatsReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass().getSuperclass()));
    }

    public static void main(String args[]) {
        SpringApplication.run(BmdStatsReport.class, args);
    }

    @Override
    public void run(String[] args) throws ReportException {
        File file = null;

        List<String> errors = parser.validate(parser.parse(args));
        initialise(args);

        long start = System.currentTimeMillis();

        super.run(parameter);

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}