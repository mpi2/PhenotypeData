/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.cdaloader.steps.tasklets;

import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.repeat.RepeatStatus;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mrelac on 13/04/2016.
 *
 */
public class Downloader extends SystemCommandTasklet {

    private String sourceUrl;
    private String filename;

    CommonUtils commonUtils = new CommonUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public final long TASKLET_TIMEOUT = 10000;                                  // Timeout in milliseconds

    /**
     * Initialise a new <code>Downloader</code> instance
     * @param sourceUrl the resource source url
     * @param filename the fully qualified filename where the resource will be downloaded to and from which the <code>
     *                 ItemReader</code> will read
     */
    public void initialise(String sourceUrl, String filename) {
        this.sourceUrl = sourceUrl;
        this.filename = filename;
    }

    @PostConstruct
    public void systemCommandTaskletInitialisation() {
        // A SystemCommandTasklet needs something to execute or it throws an exception. This is a do-nothing command to satisfy that requirement.
        setCommand("ls");
        setTimeout(TASKLET_TIMEOUT);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        FileOutputStream fos;
        ReadableByteChannel rbc;
        final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
        String outputAppender = DATE_FORMAT.format(new Date());
        String source;
        long start, startStep;
        String target;
        String targetTemp;
        URL url;

        try {
            Files.createDirectories(Paths.get(filename).getParent());
        } catch (IOException e) {
            logger.error("Create cda directory '" + filename + "' failed. Reason: " + e.getLocalizedMessage());
        }

        start = new Date().getTime();
        target = filename;
        targetTemp = target + "." + outputAppender;
        source = sourceUrl;
        try {
            url = new URL(source);
            rbc = Channels.newChannel(url.openStream());
            fos = new FileOutputStream(targetTemp);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            Files.move(Paths.get(targetTemp), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
            logger.info(source + " -> " + target + "(" + commonUtils.msToHms(new Date().getTime() - start) + ")");

        } catch (IOException e) {
            logger.error(source + " -> " + target + "(" + commonUtils.msToHms(new Date().getTime() - start) + "). Reason: " + e.getLocalizedMessage());
        }

        return null;
    }
}