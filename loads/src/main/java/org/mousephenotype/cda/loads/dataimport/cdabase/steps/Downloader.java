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

package org.mousephenotype.cda.loads.dataimport.cdabase.steps;

import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

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
 * Downloads a single download file from a source url to a target filename.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class Downloader implements Tasklet, InitializingBean {

    CommonUtils commonUtils = new CommonUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String sourceUrl;
    private String targetFilename;


    public Downloader(String sourceUrl, String targetFilename) {
        this.sourceUrl = sourceUrl;
        this.targetFilename = targetFilename;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
  	    Assert.notNull(sourceUrl, "sourceUrl must be set");
        Assert.notNull(targetFilename, "targetFilename must be set");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();

        FileOutputStream fos;
        ReadableByteChannel rbc;
        final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
        String outputAppender = DATE_FORMAT.format(new Date());
        String source;
        long start;
        String target;
        String targetTemp;
        URL url;

        try {
            Files.createDirectories(Paths.get(targetFilename).getParent());
        } catch (IOException e) {
            logger.error("Create cda directory '" + targetFilename + "' failed. Reason: " + e.getLocalizedMessage());
        }

        start = new Date().getTime();
        target = targetFilename;
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

        logger.info("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
        contribution.setExitStatus(ExitStatus.COMPLETED);
        chunkContext.setComplete();
        return RepeatStatus.FINISHED;
    }

    @StepScope
    public Step getStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("databaseInitialiserStep")
                .tasklet(this)
                .build();
    }
}