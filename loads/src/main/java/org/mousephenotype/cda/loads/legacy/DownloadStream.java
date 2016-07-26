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

package org.mousephenotype.cda.loads.legacy;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by mrelac on 22/06/2015.
 *
 * This class encapsulates the code and data necessary to load a stream.
 */
public class DownloadStream {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private URL source;
    private Path target;
    private boolean append;

    public DownloadStream() {

    }

    /**
     * Creates a new instance that is ready to get the report.
     *
     * @param source the source url
     * @param target the fully-qualified target filename
     * @param append if true, <code>target</code> is appended; otherwise it is created fresh.
     *
     * @throws IOException
     */
    public DownloadStream(String source, String target, boolean append) throws IOException {
        this(new URL(source), Paths.get(target), append);
    }

    /**
     * Creates a new instance that is ready to get the report.
     *
     * @param source the source url
     * @param target the fully-qualified target filename
     * @param append if true, <code>target</code> is appended; otherwise it is created fresh.
     */
    public DownloadStream(URL source, Path target, boolean append) {
        this.source = source;
        this.target = target;
        this.append = append;
    }

    /**
     * Given the components provided to the constructor at instantiation time: a url, a fully-qualified filename target
     * file name, and a boolean indicating whether or not <code>target </code> is to be appended to if it already
     * exists, this method gets the report using the following algorithm:
     * <ul>
     *     <li>Create the output directory if it does not yet exist</li>
     *     <li>Open and read the source url stream</li>
     *     <li>Write or append the stream to the output file (depending on value of <code>append</code>)</li>
     *     <li>Close the source url stream and the output file</li>
     *     <li>Return the number of bytes written to the file</li>
     * </ul>
     *
     * The
     * If <code>target</code> already exists and <code>append</code> is true,
     * <code>target</code> is appended to; otherwise it is first truncated if it exists, or created as new if it
     * doesn't.
     *
     * @throws IOException
     *
     * @return The number of bytes written to <code>target</code>
     */
    public File getReport() throws IOException {
        File outputFile = null;
        final Process p;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(source.toURI());
            CloseableHttpResponse response = httpClient.execute(httpGet);

            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            try {
                outputFile = new File(target.toString());
                int lastDelimeter = outputFile.getPath().lastIndexOf("/");
                if (lastDelimeter >= 0) {
                    File outputFileDir = new File(outputFile.getPath().substring(0, lastDelimeter));
                    outputFileDir.mkdirs();
                }
                FileWriter writer = new FileWriter(outputFile, append);
                while ((line = input.readLine()) != null) {
                    writer.write(line + "\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Path outputFilePath = Paths.get(target.toString());
            logger.info("Wrote " + Files.size(outputFilePath) + " bytes to " + outputFilePath.toAbsolutePath());

        } catch (Exception e) {
            logger.error("Exception: " + e.getLocalizedMessage());
            throw new IOException(e.getLocalizedMessage());
        }

        return outputFile;
    }


    // SETTERS AND GETTERS


    public URL getSource() {
        return source;
    }

    public void setSource(URL source) {
        this.source = source;
    }

    public Path getTarget() {
        return target;
    }

    public void setTarget(Path target) {
        this.target = target;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }


    // PRIVATE METHODS


    // MAIN


    public static void main(String[] args)  {
        try {
            DownloadStream downloadISMRReports = new DownloadStream();
            downloadISMRReports.getReport();
        } catch (Exception e) { }
    }
}
