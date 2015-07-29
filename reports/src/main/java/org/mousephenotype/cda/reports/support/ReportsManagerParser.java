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

package org.mousephenotype.cda.reports.support;

import org.mousephenotype.cda.reports.ReportsManager;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Implements the ReportManager parser.
 *
 * Created by mrelac on 28/07/2015.
 */
public class ReportsManagerParser extends AbstractReportParser {
    protected List<ReportsManager.ReportType> reports = new ArrayList<>();

    public static final String REPORTS_ARG = "reports";

    public Map<String, String> parse(String[] args) {
        PropertySource ps = new SimpleCommandLinePropertySource(args);

        super.parse(args);

        if ((ps.containsProperty(REPORTS_ARG))  && ps.getProperty(REPORTS_ARG) != null) {
            propertyMap.put(REPORTS_ARG, ps.getProperty(REPORTS_ARG).toString());
        }

        return propertyMap;
    }

    public List<String> validate(Map<String, String> propertyMap) {

        List<String> errors = super.validate(propertyMap);

        if (propertyMap.containsKey(REPORTS_ARG)) {
            List<String> reportNames = Arrays.asList(propertyMap.get(REPORTS_ARG).split(","));
            for (String reportName : reportNames) {
                if (reportName.isEmpty()) {
                    reports.addAll(Arrays.asList(ReportsManager.ReportType.values()));
                } else {
                    try {
                        ReportsManager.ReportType reportType = ReportsManager.ReportType.fromTag(reportName);
                        reports.add(reportType);
                    } catch (IllegalArgumentException | NullPointerException e) {
                        errors.add("Unknown report name '" + reportName + "'.");
                    }
                }
            }
        } else {
            reports.addAll(Arrays.asList(ReportsManager.ReportType.values()));
        }

        return errors;
    }

    public List<ReportsManager.ReportType> getReports() {
        return reports;
    }
}