/*******************************************************************************
 *  Copyright (c) 2013 - 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by mrelac on 22/06/2015.
 *
 * This class encapsulates the code and data necessary to represent the PhenotypeArchive data loading step that fetches
 * the imsr IMSR_report file.
 */
public class DownloadIMSRReport {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String IMSR_URL = "http://www.findmice.org/report.txt?query=&states=Any&_states=1&types=Any&_types=1&repositories=Any&_repositories=1&_mutations=on&results=500000&startIndex=0&sort=score&dir=";
    private final String TARGET = "./imsr/IMSR_report.txt";

    public DownloadIMSRReport() throws IOException {

    }

    /**
     * Executes the download.
     *
     * @return the fully-qualified target <code>File</code> instance
     *
     * @throws IOException
     */
    public File downloadReport() throws IOException {
        boolean append = false;
        DownloadStream downloadStream = new DownloadStream(IMSR_URL, TARGET, append);
        return downloadStream.getReport();
    }


    // MAIN


    public static void main(String[] args)  {
        try {
            DownloadIMSRReport downloadISMRReports = new DownloadIMSRReport();
            File outputFile = downloadISMRReports.downloadReport();
            String fqTarget = outputFile.getAbsolutePath();
            Path outputFilePath = Paths.get(fqTarget);
            long fqTargetSize = Files.size(outputFilePath);
            downloadISMRReports.logger.info("Wrote " + fqTargetSize + " bytes to " + fqTarget + ".");
        } catch (Exception e) { }
    }
}
