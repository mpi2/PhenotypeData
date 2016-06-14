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

package org.mousephenotype.cda.loads.cdaloader.steps;

import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.LineMapperFieldSet;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Loads the markers from the MGI_GTGUP.gff file into the strain and synonym tables of the target database.
 * As of 31-May-2016, the report.txt file's first line is a heading that must be skipped.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class MarkerLoader implements Step, InitializingBean {

    private       CommonUtils                     commonUtils         = new CommonUtils();
    private final Logger                          logger              = LoggerFactory.getLogger(this.getClass());
    public        Map<MarkerFilenameKeys, String> markerKeys          = new HashMap<>();
    private       FlatFileItemReader<FieldSet>    geneTypesReader     = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    markerListReader    = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    vegaModelsReader    = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    ensemblModelsReader = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    entrezGeneReader    = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    ccdsModelsReader    = new FlatFileItemReader<>();


    public enum MarkerFilenameKeys {
          GENE_TYPES
        , MARKER_LIST
        , VEGA_MODELS
        , ENSEMBL_MODELS
        , ENTREZ_GENE_MODELS
        , CCDS_MODELS
    }

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemProcessor markerProcessorGeneTypes;


    public MarkerLoader(Map<MarkerFilenameKeys, String> markerKeys) throws CdaLoaderException {
        this.markerKeys = markerKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        geneTypesReader.setResource(new FileSystemResource(markerKeys.get(MarkerFilenameKeys.GENE_TYPES)));
        geneTypesReader.setLineMapper(new LineMapperFieldSet());

        markerListReader.setResource(new FileSystemResource(markerKeys.get(MarkerFilenameKeys.MARKER_LIST)));
        markerListReader.setLineMapper(new LineMapperFieldSet());

        vegaModelsReader.setResource(new FileSystemResource(markerKeys.get(MarkerFilenameKeys.VEGA_MODELS)));
        vegaModelsReader.setLineMapper(new LineMapperFieldSet());

        ensemblModelsReader.setResource(new FileSystemResource(markerKeys.get(MarkerFilenameKeys.ENSEMBL_MODELS)));
        ensemblModelsReader.setLineMapper(new LineMapperFieldSet());

        entrezGeneReader.setResource(new FileSystemResource(markerKeys.get(MarkerFilenameKeys.ENTREZ_GENE_MODELS)));
        entrezGeneReader.setLineMapper(new LineMapperFieldSet());

        ccdsModelsReader.setResource(new FileSystemResource(markerKeys.get(MarkerFilenameKeys.CCDS_MODELS)));
        ccdsModelsReader.setLineMapper(new LineMapperFieldSet());
    }


    // Step IMPLEMENTATION


    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "markerLoaderStep";
    }

    /**
     * @return true if a step that is already marked as complete can be started again.
     */
    @Override
    public boolean isAllowStartIfComplete() {
        return false;
    }

    /**
     * @return the number of times a job can be started with the same identifier.
     */
    @Override
    public int getStartLimit() {
        return 1;
    }

    /**
     * Process the step and assign progress and status meta information to the {@link StepExecution} provided. The
     * {@link Step} is responsible for setting the meta information and also saving it if required by the
     * implementation.<br>
     * <p/>
     * It is not safe to re-use an instance of {@link Step} to process multiple concurrent executions.
     *
     * @param stepExecution an entity representing the step to be executed
     * @throws JobInterruptedException if the step is interrupted externally
     */
    @Override
    public void execute(StepExecution stepExecution) throws JobInterruptedException {

        stepBuilderFactory.get("markerLoaderStep")
                .listener(new MarkerLoaderStepListener())
                .chunk(100)
                .reader(geneTypesReader)
                .processor(markerProcessorGeneTypes)

//                .reader(markerListReader)
//                .processor((ItemProcessor)new MarkerListProcessor())
//
//                .reader(vegaModelsReader)
//                .processor((ItemProcessor)new ModelsProcessor())
//
//                .reader(ensemblModelsReader)
//                .processor((ItemProcessor)new ModelsProcessor())
//
//                .reader(entrezGeneReader)
//                .processor((ItemProcessor)new ModelsProcessor())
//
//                .reader(ccdsModelsReader)
//                .processor((ItemProcessor)new ModelsProcessor())

                .build()
                .execute(stepExecution);
    }


    /*
    ** ITEM PROCESSORS - This is where the business/parsing rules get applied for each file format. Each class updates
    ** the genomicFeatures map in preparation for INSERTing into the genomic_features table.
     */


    private class MarkerListProcessor implements ItemProcessor<FieldSet, FieldSet> {

        @Override
        public FieldSet process(FieldSet item) throws Exception {
            return null;
        }
    }

    private class ModelsProcessor implements ItemProcessor<FieldSet, FieldSet> {

        @Override
        public FieldSet process(FieldSet item) throws Exception {
            return null;
        }
    }


    public class MarkerLoaderStepListener implements StepExecutionListener {
        private Date start;
        private Date stop;

        /**
         * Initialize the state of the listener with the {@link StepExecution} from
         * the current scope.
         *
         * @param stepExecution
         */
        @Override
        public void beforeStep(StepExecution stepExecution) {
            start = new Date();
        }

        /**
         * Give a listener a chance to modify the exit status from a step. The value
         * returned will be combined with the normal exit status using
         * {@link ExitStatus#and(ExitStatus)}.
         * <p/>
         * Called after execution of step's processing logic (both successful or
         * failed). Throwing exception in this method has no effect, it will only be
         * logged.
         *
         * @param stepExecution
         * @return an {@link ExitStatus} to combine with the normal value. Return
         * null to leave the old value unchanged.
         */
        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            Map<String, GenomicFeature> genomicFeaturesMap = ((MarkerProcessorGeneTypes) markerProcessorGeneTypes).getGenomicFeatures();
            stop = new Date();

            logger.info("Added {} Marker gene types in {}", genomicFeaturesMap.size(), commonUtils.formatDateDifference(start, stop));

            Set<String> errMessages = ((MarkerProcessorGeneTypes)markerProcessorGeneTypes).getErrMessages();
            if ( ! errMessages.isEmpty()) {
                logger.warn("WARNINGS:");
                for (String s : ((MarkerProcessorGeneTypes) markerProcessorGeneTypes).errMessages) {
                    logger.warn("\t" + s);
                }
            }

            return ExitStatus.COMPLETED;
        }
    }
}