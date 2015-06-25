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
 * the imsr report file.
 */
public class DownloadIMSRReport {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String IMSR_URL = "http://www.findmice.org/report.txt?query=&states=Any&_states=1&types=Any&_types=1&repositories=Any&_repositories=1&_mutations=on&results=500000&startIndex=0&sort=score&dir=";
    private final String TARGET = "./imsr/report.txt";
//    private final String HTTP_PROXY = "http://hx-wwwcache.ebi.ac.uk:3128";
//    private HttpClient httpClient;
//    private Path fqDirectoryPath;
//    private Path fqFilePath;
//    private String workspace;


    public DownloadIMSRReport() throws IOException {

    }

    public File downloadReport() throws IOException {
        boolean append = false;
        DownloadStream downloadStream = new DownloadStream(IMSR_URL, TARGET, append);
        return downloadStream.getReport();



//        workspace = (System.getProperty("WORKSPACE") == null ? "." : System.getProperty("WORKSPACE"));
//        fqDirectoryPath = Paths.get(workspace + "/imsr");
//        fqFilePath = Paths.get(fqDirectoryPath.toAbsolutePath() + "/report.txt");
    }

//    public void downloadReport() throws IOException {
//        createDirectory();
//        final Process p;
//        try {
//            CloseableHttpClient httpClient = HttpClients.createDefault();
//            HttpGet httpGet = new HttpGet(IMSR_URL);
//            CloseableHttpResponse response = httpClient.execute(httpGet);
//
//            HttpEntity entity = response.getEntity();
//            InputStream inputStream = entity.getContent();
//            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
//            String line = null;
//
//            try {
//                File outputFile = new File(fqFilePath.toString());
//                boolean append = false;
//                FileWriter writer = new FileWriter(outputFile, append);
//                while ((line = input.readLine()) != null) {
//                    writer.write(line + "\n");
//                }
//                writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Path outputFilePath = Paths.get(fqFilePath.toString());
//            logger.info("Wrote " + Files.size(outputFilePath) + " bytes to " + outputFilePath.toAbsolutePath());
//
//        } catch (Exception e) {
//            logger.error("Exception: " + e.getLocalizedMessage());
//            throw new IOException(e.getLocalizedMessage());
//        }
//    }


    // PRIVATE METHODS


//    /**
//     * Create the IMSR directory if it doesn't already exist.
//     *
//     * @throws IOException
//     */
//    private void createDirectory() throws IOException {
//        if ( ! Files.isDirectory(fqDirectoryPath)) {
//            Files.createDirectory(fqDirectoryPath);
//        }
//        logger.info("Using IMSR load directory " + fqDirectoryPath.toAbsolutePath().toString());
//    }


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
