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
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.reports.support.SexualDimorphismDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.beans.Introspector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Sexual Dimorphism With Body Weight report.
 *
 * Created by mrelac on 24/07/2015.
 *
 * 2017-11-30 (mrelac) The SexualDimorphismNoBodyWeightReport and SexualDimorphismWithBodyWeightReport were
 * temporary reports meant to support the Sexual Dimorphism paper and are no longer needed. SexualDimorphismWithBodyWeightReport
 * no longer builds as it has a hard-coded dependency on database komp2_4_0_with_weight, which no longer exists. We are
 * leaving the code for future reference but have annotated these report classes as @Deprecated and removed them from
 * the ReportManager.
 */
@Component
@Deprecated
public class SexualDimorphismWithBodyWeightReport extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull @Autowired
    SexualDimorphismDAO sexualDimorphismDAO;

    @Value("${cms_base_url}")
    protected String cmsBaseUrl;

    public SexualDimorphismWithBodyWeightReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("SexualDimorphismWithBodyWeightReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<String[]> result = new ArrayList<>();
        try {
//            result = sexualDimorphismDAO.sexualDimorphismReportWithBodyWeight(cmsBaseUrl);
        } catch (Exception e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        csvWriter.writeAll(result);

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}