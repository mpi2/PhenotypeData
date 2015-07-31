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

import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.List;
import java.util.Map;

/**
 * Implements the Report parser.
 *
 * Created by mrelac on 28/07/2015.
 */
public class ReportParser extends AbstractReportParser {
    protected String targetFilename;

    public static final String TARGET_FILENAME_ARG = "targetFilename";

    public Map<String, String> parse(String[] args) {
        PropertySource ps = new SimpleCommandLinePropertySource(args);

        super.parse(args);

        if ((ps.containsProperty(TARGET_FILENAME_ARG))  && ps.getProperty(TARGET_FILENAME_ARG) != null) {
            propertyMap.put(TARGET_FILENAME_ARG, ps.getProperty(TARGET_FILENAME_ARG).toString());
        }

        return propertyMap;
    }

    public List<String> validate(Map<String, String> propertyMap) {

        List<String> errors = super.validate(propertyMap);

        if (propertyMap.containsKey(TARGET_FILENAME_ARG)) {
            targetFilename = propertyMap.get(TARGET_FILENAME_ARG).toString();
        } else {
            targetFilename = null;
        }

        return errors;
    }

    public String getTargetFilename() {
        return targetFilename;
    }
}