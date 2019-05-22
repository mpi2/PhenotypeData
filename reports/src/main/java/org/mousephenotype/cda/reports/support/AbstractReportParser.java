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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.mousephenotype.cda.reports.AbstractReport;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser to handle common report command-line parsing and validation.
 *
 * Created by mrelac on 28/07/2015.
 */
public abstract class AbstractReportParser {
    protected boolean                     showHelp;
    protected String                      prefix;
    protected File                        propertiesFile;
    protected PropertiesConfiguration     applicationProperties;
    protected AbstractReport.ReportFormat reportFormat = null;
    protected String                      targetDirectory;
    protected Map<String, String>         propertyMap  = new HashMap<>();

    public static final String TARGET_DIRECTORY_ARG = "targetDirectory";
    public static final String REPORT_FORMAT_ARG    = "reportFormat";
    public static final String PREFIX_ARG           = "prefix";
    public static final String HELP_ARG             = "help";

    public static final String                      DEFAULT_TARGET_DIRECTORY = "./reports";
    public static final AbstractReport.ReportFormat DEFAULT_REPORT_FORMAT    = AbstractReport.ReportFormat.csv;
    public static final String                      DEFAULT_PREFIX           = "";


    public Map<String, String> parse(String[] args) {
        PropertySource ps = new SimpleCommandLinePropertySource(args);

        // Basic expected properties are: targetDirectory, reportFormat, propertiesFile, prefix, and help.
        if ((ps.containsProperty(TARGET_DIRECTORY_ARG))  && ps.getProperty(TARGET_DIRECTORY_ARG) != null) {
            propertyMap.put(TARGET_DIRECTORY_ARG, ps.getProperty(TARGET_DIRECTORY_ARG).toString());
        }

        if ((ps.containsProperty(REPORT_FORMAT_ARG))  && ps.getProperty(REPORT_FORMAT_ARG) != null) {
            propertyMap.put(REPORT_FORMAT_ARG, ps.getProperty(REPORT_FORMAT_ARG).toString());
        }

        if ((ps.containsProperty(PREFIX_ARG)) && ps.getProperty(PREFIX_ARG) != null) {
            propertyMap.put(PREFIX_ARG, ps.getProperty(PREFIX_ARG).toString());
        }

        if (ps.containsProperty(HELP_ARG)) {
            propertyMap.put(HELP_ARG, "");
        }

        return propertyMap;
    }

    public List<String> validate(Map<String, String> propertyMap) {
        List<String> retVal = new ArrayList<>();

        if (propertyMap.containsKey(TARGET_DIRECTORY_ARG)) {
            this.targetDirectory = propertyMap.get(TARGET_DIRECTORY_ARG);
        } else {
            this.targetDirectory = DEFAULT_TARGET_DIRECTORY;
        }
        Path path = Paths.get(targetDirectory);
        if ( ! Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                retVal.add("Unable to create path " + path.toAbsolutePath() + ". Reason: " + e.getLocalizedMessage());
            }
        }
        if ( ! Files.isWritable(path)) {
            retVal.add("Target directory " + path.toAbsolutePath() + " is not writeable.");
        }


        if (propertyMap.containsKey(REPORT_FORMAT_ARG)) {
            try {
                this.reportFormat = AbstractReport.ReportFormat.valueOf(propertyMap.get(REPORT_FORMAT_ARG));
            } catch (IllegalArgumentException | NullPointerException e) {
                retVal.add("Unknown report format type '" + propertyMap.get(REPORT_FORMAT_ARG) + "'.");
            }
        }

        if (propertyMap.containsKey(PREFIX_ARG)) {
            this.prefix = propertyMap.get(PREFIX_ARG);
        } else {
            this.prefix = DEFAULT_PREFIX;
        }


        showHelp = (propertyMap.containsKey(HELP_ARG) ? true : false);

        return retVal;
    }

    public boolean showHelp() {
        return showHelp;
    }

    public String getPrefix() {
        return prefix;
    }

    public File getPropertiesFile() {
        return propertiesFile;
    }

    public AbstractReport.ReportFormat getReportFormat() {
        return reportFormat;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public PropertiesConfiguration getApplicationProperties() {
        return applicationProperties;
    }
}