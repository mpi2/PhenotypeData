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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by mrelac on 22/06/2015.
 */
public class DownloadIMGIReports {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String WORKSPACE = "";

    public DownloadIMGIReports() throws IOException {
        initialise();
    }


    // PRIVATE METHODS


    private void initialise() throws IOException {
        createDirectories();
    }

    private void createDirectories() throws IOException {
        Path path = Paths.get(WORKSPACE + "imsr");

        if ( ! Files.isDirectory(path)) {
            Files.createDirectory(path);
        }
        logger.info("Using IMSR load directory " + path);

        // Create MGI reports directory.
        path = Paths.get(WORKSPACE + "mgi_reports");

        if ( ! Files.isDirectory(path)) {
            Files.createDirectory(path);
        }
        logger.info("Using mgi reports directory " + path);
    }

    private void downloadMGIReports() {

    }
}
