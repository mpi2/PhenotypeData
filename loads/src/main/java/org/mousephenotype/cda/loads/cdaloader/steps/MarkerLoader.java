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

import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.LineMapperFieldSet;
import org.mousephenotype.cda.loads.cdaloader.support.LogStatusStepListener;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.*;

/**
 * Loads the markers from the MGI_GTGUP.gff file into the strain and synonym tables of the target database.
 * As of 31-May-2016, the report.txt file's first line is a heading that must be skipped.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class MarkerLoader implements InitializingBean, Step {

    private       CommonUtils                     commonUtils         = new CommonUtils();
    private final Logger                          logger              = LoggerFactory.getLogger(this.getClass());
    public        Map<MarkerFilenameKeys, String> markerKeys          = new HashMap<>();

    private       FlatFileItemReader<FieldSet>    ccdsModelsReader    = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    ensemblModelsReader = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    entrezGeneReader    = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    geneTypesReader     = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    markerListReader    = new FlatFileItemReader<>();
    private       FlatFileItemReader<FieldSet>    vegaModelsReader    = new FlatFileItemReader<>();


    public enum MarkerFilenameKeys {
          CCDS_MODELS
        , ENSEMBL_MODELS
        , ENTREZ_GENE_MODELS
        , GENE_TYPES
        , MARKER_LIST
        , VEGA_MODELS
    }

    @Autowired
    private ItemProcessor markerProcessorGeneTypes;

    @Autowired
    private ItemProcessor markerProcessorMarkerList;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


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

//        ccdsModelsReader.setResource(new FileSystemResource(markerKeys.get(MarkerFilenameKeys.CCDS_MODELS)));
//        ccdsModelsReader.setLineMapper(new LineMapperFieldSet());
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

        Step loadGeneTypesStep = stepBuilderFactory.get("markerLoaderGeneTypesStep")
                .listener(new MarkerLoaderGeneTypesStepListener())
                .chunk(1000)
                .reader(geneTypesReader)
                .processor(markerProcessorGeneTypes)
                .build();

        Step loadMarkerListStep = stepBuilderFactory.get("markerLoaderMarkerListStep")
                .listener(new MarkerLoaderMarkerListStepListener())
                .chunk(1000)
                .reader(markerListReader)
                .processor(markerProcessorMarkerList)
                .build();

        List<Flow> flows = new ArrayList<>();
        flows.add(new FlowBuilder<Flow>("markerLoadGeneTypesFlow")
                .from(loadGeneTypesStep).end());
        flows.add(new FlowBuilder<Flow>("markerLoadMarkerListFlow")
                .from(loadMarkerListStep).end());

        FlowBuilder<Flow> flowBuilder = new FlowBuilder<Flow>("markerLoaderFlows").start(flows.get(0));
        for (int i = 1; i < flows.size(); i++) {
            TaskExecutor executor = new SyncTaskExecutor();
            flowBuilder.next(flows.get(i));
        }







        stepBuilderFactory.get("markerLoaderStep")
                .flow(flowBuilder.build())
//                .listener(new MarkerLoaderGeneTypesStepListener())
//                .chunk(100)
//                .reader(geneTypesReader)
//                .processor(markerProcessorGeneTypes)
//
//                .reader(markerListReader)
//                .processor(markerProcessorMarkerList)

//                .reader(vegaModelsReader)
//                .processor((ItemPrmor)new ModelsPVegarocessor())
//
//                .reader(ensemblModelsReader)
//                .processor((ItemPrmor)new ModelsPEnsemblrocessor())
//
//                .reader(entrezGeneReader)
//                .processor((ItemPrmor)new ModelsPEntrezrocessor())
//
//                .reader(ccdsModelsReader)
//                .processor((ItemPrmor)new ModelsPCcdsrocessor())

                .build()
                .execute(stepExecution);
    }

    public class MarkerLoaderGeneTypesStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("GENE TYPES: Added {} new Marker gene types from file {} in {}",
                    ((MarkerProcessorGeneTypes) markerProcessorGeneTypes).getAddedGeneTypesCount(),
                    markerKeys.get(MarkerFilenameKeys.GENE_TYPES),
                    commonUtils.formatDateDifference(start, stop));

            return ((MarkerProcessorGeneTypes) markerProcessorGeneTypes).getErrMessages();
        }
    }

    public class MarkerLoaderMarkerListStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("MARKER LIST: Added {} new Marker gene types and updated {} Marker gene types from file {} in {}",
                    ((MarkerProcessorMarkerList) markerProcessorMarkerList).getAddedMarkerListCount(),
                    ((MarkerProcessorMarkerList) markerProcessorMarkerList).getUpdatedMarkerListCount(),
                    markerKeys.get(MarkerFilenameKeys.MARKER_LIST),
                    commonUtils.formatDateDifference(start, stop));

            return ((MarkerProcessorMarkerList) markerProcessorMarkerList).getErrMessages();
        }
    }
}