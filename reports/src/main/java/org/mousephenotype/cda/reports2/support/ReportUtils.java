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

package org.mousephenotype.cda.reports2.support;

import org.mousephenotype.cda.reports.support.ReportException;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.HashMap;
import java.util.Map;

public class ReportUtils {
    public static String TARGET_DIRECTORY_ARG = "targetDirectory";
    public static String REPORT_FORMAT        = "reportFormat";
    public static String HELP                 = "help";

    public static Map<String, String> getReportProperties(String ... args) throws ReportException {

        Map<String, String> properties = new HashMap<>();
        PropertySource ps = new SimpleCommandLinePropertySource(args);

        if ((ps.containsProperty(TARGET_DIRECTORY_ARG))  && ps.getProperty(TARGET_DIRECTORY_ARG) != null) {
            properties.put(TARGET_DIRECTORY_ARG, ps.getProperty(TARGET_DIRECTORY_ARG).toString());
        } else {
            throw new ReportException("Required target directory is missing");
        }

        if ((ps.containsProperty(REPORT_FORMAT))  && ps.getProperty(REPORT_FORMAT) != null) {
            String separator = ps.getProperty(REPORT_FORMAT).toString();
            if ((separator.equalsIgnoreCase("csv")) ||
                (separator.equalsIgnoreCase("tsv"))) {
                properties.put(REPORT_FORMAT, separator);
            } else {
                throw new ReportException("Invalid report format '" + separator + "'");
            }
        }

        if ((ps.containsProperty(HELP))  && ps.getProperty(HELP) != null) {
            properties.put(HELP, HELP);
        }

        return properties;
    }
}
