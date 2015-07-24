/*******************************************************************************
 *  Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.reports;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for reports.
 *
 * Created by mrelac on 24/07/2015.
 */
public abstract class AbstractReport implements CommandLineRunner {
    protected PropertiesConfiguration applicationProperties;
    protected MpCSVWriter csvWriter;
    protected File file;
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    protected String propertiesFilename;
    protected ReportFormat reportFormat;
    protected boolean showHelp;
    protected String targetDirectory;
    protected String targetFilename;

    protected static final String HELP_ARG = "help";
    protected static final String PROPERTIES_FILE_ARG = "propertiesFile";
    protected static final String REPORT_FORMAT_ARG = "reportFormat";
    protected static final String TARGET_DIRECTORY_ARG = "targetDirectory";
    protected static final String TARGET_FILENAME_ARG = "targetFilename";

    public enum ReportFormat {
        csv(','),
        tsv('\t')
        ;

        char separator;

        private ReportFormat(char separator) {
            this.separator = separator;
        }

        public char getSeparator() {
            return this.separator;
        }
    }

    public AbstractReport() throws ReportException {

    }

    public Map<String, String> parse(String[] args) {
        PropertySource ps = new SimpleCommandLinePropertySource(args);
        Map<String, String> propertyMap = new HashMap<>();

        // Basic expected properties are: help, propertiesFile, targetDirectory, targetFilename, and reportFormat.
        if (ps.containsProperty(HELP_ARG)) {
            propertyMap.put(HELP_ARG, "");
        }

        if ((ps.containsProperty(PROPERTIES_FILE_ARG)) && ps.getProperty(PROPERTIES_FILE_ARG) != null) {
            propertyMap.put(PROPERTIES_FILE_ARG, ps.getProperty(PROPERTIES_FILE_ARG).toString());
        }

        if ((ps.containsProperty(TARGET_FILENAME_ARG))  && ps.getProperty(TARGET_FILENAME_ARG) != null) {
            propertyMap.put(TARGET_FILENAME_ARG, ps.getProperty(TARGET_FILENAME_ARG).toString());
        }

        if ((ps.containsProperty(TARGET_DIRECTORY_ARG))  && ps.getProperty(TARGET_DIRECTORY_ARG) != null) {
            propertyMap.put(TARGET_DIRECTORY_ARG, ps.getProperty(TARGET_DIRECTORY_ARG).toString());
        }

        if ((ps.containsProperty(REPORT_FORMAT_ARG))  && ps.getProperty(REPORT_FORMAT_ARG) != null) {
            propertyMap.put(REPORT_FORMAT_ARG, ps.getProperty(REPORT_FORMAT_ARG).toString());
        }

        return propertyMap;
    }

    public List<String> validate(Map<String, String> propertyMap) {
        List<String> retVal = new ArrayList<>();

        showHelp = (propertyMap.containsKey(HELP_ARG) ? true : false);

        if (propertyMap.containsKey(PROPERTIES_FILE_ARG)) {
            try {
                this.applicationProperties = new PropertiesConfiguration(propertyMap.get(PROPERTIES_FILE_ARG));
            } catch( ConfigurationException e) {
                retVal.add("Expected required properties file.");
            }
        } else {
            retVal.add("Expected required properties file.");
        }

        if (propertyMap.containsKey(TARGET_DIRECTORY_ARG)) {
            this.targetDirectory = propertyMap.get(TARGET_DIRECTORY_ARG);
        } else {
            retVal.add("Expected required target directory.");
        }
        Path path = Paths.get((String) targetDirectory);
        if ( ! Files.exists(path)) {
            retVal.add("Target directory " + path.toAbsolutePath() + " does not exist.");
        }
        if ( ! Files.isWritable(path)) {
            retVal.add("Target directory " + path.toAbsolutePath() + " is not writeable.");
        }

        if (propertyMap.containsKey(TARGET_FILENAME_ARG)) {
            this.targetFilename = propertyMap.get(TARGET_FILENAME_ARG);
        } else {
            retVal.add("Expected required target filename.");
        }

        if (propertyMap.containsKey(REPORT_FORMAT_ARG)) {
            try {
                this.reportFormat = ReportFormat.valueOf(REPORT_FORMAT_ARG);
            } catch (IllegalArgumentException | NullPointerException e) {
                retVal.add("Unknown report format type '" + propertyMap.get(REPORT_FORMAT_ARG) + "'.");
            }
        } else {
            this.reportFormat = ReportFormat.csv;
        }

        if (retVal.isEmpty()) {
            this.targetFilename += "." + this.reportFormat.toString();
            this.file = new File(Paths.get(targetDirectory, targetFilename).toAbsolutePath().toString());
            try {
                FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
                this.csvWriter = new MpCSVWriter(fileWriter, reportFormat.getSeparator());
            } catch (IOException e) {
                retVal.add("Exception opening FileWriter: " + e.getLocalizedMessage());
            }
        }

        return retVal;
    }

    public PropertiesConfiguration getApplicationProperties() {
        return applicationProperties;
    }

    public File getFile() {
        return file;
    }

    public String getPropertiesFilename() {
        return propertiesFilename;
    }

    public ReportFormat getReportFormat() {
        return reportFormat;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public String getTargetFilename() {
        return targetFilename;
    }

// PROTECTED METHODS


    protected void usage() {
        System.out.println("Usage:");
        System.out.println(
                "--" + PROPERTIES_FILE_ARG       + "=properties_file (no extension)\n" +
                "--" + TARGET_DIRECTORY_ARG      + "=target_directory\n" +
                "--" + TARGET_FILENAME_ARG       + "=target_filename\n" +
                "[--" + REPORT_FORMAT_ARG + "=target_report_filename] (default is csv)\n" +
                "[--" + HELP_ARG + "]\n");
        System.out.println();
        System.out.println("Report Formats: { csv | tsv }\n");
    }

    protected void logInputParameters() {
        log.info("Application properties file: " + this.applicationProperties.getURL());
        log.info("Target filename:             " + this.file.getAbsolutePath());
        log.info("Target report format:        " + this.reportFormat);
    }
}
