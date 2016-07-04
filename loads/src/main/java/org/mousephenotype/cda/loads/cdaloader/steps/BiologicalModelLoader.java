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
import org.mousephenotype.cda.loads.cdaloader.support.BiologicalModelAggregator;
import org.mousephenotype.cda.loads.cdaloader.support.BlankLineRecordSeparatorPolicy;
import org.mousephenotype.cda.loads.cdaloader.support.LogStatusStepListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.validation.BindException;

import java.util.*;

/**
 * Loads the alleles from the mgi report files.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class BiologicalModelLoader implements InitializingBean, Step {

    public Map<FilenameKeys, String> bioModelKeys = new HashMap<>();
    private final Logger             logger       = LoggerFactory.getLogger(this.getClass());

    private FlatFileItemReader<BiologicalModelAggregator> bioModelReader = new FlatFileItemReader<>();

    public enum FilenameKeys {
        MGI_PhenoGenoMP
    }

    @Autowired
    @Qualifier("bioModelProcessor")
    private ItemProcessor bioModelProcessor;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private BiologicalModelWriter writer;


    public BiologicalModelLoader(Map<FilenameKeys, String> bioModelKeys) throws CdaLoaderException {
        this.bioModelKeys = bioModelKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        bioModelReader.setResource(new FileSystemResource(bioModelKeys.get(FilenameKeys.MGI_PhenoGenoMP)));
        bioModelReader.setComments(new String[] { "#" });
        bioModelReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<BiologicalModelAggregator> lineMapperBioModel = new DefaultLineMapper<>();
        DelimitedLineTokenizer                       tokenizerBioModel  = new DelimitedLineTokenizer("\t");
        tokenizerBioModel.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerBioModel.setNames(new String[] { "allelicComposition", "alleleSymbol", "geneticBackground", "mpAccessionId", "unused_5", "markerAccessionId" });
        lineMapperBioModel.setLineTokenizer(tokenizerBioModel);
        lineMapperBioModel.setFieldSetMapper(new BiologicalModelAggregatorFieldSetMapper());
        bioModelReader.setLineMapper(lineMapperBioModel);
    }

    public class BiologicalModelAggregatorFieldSetMapper implements FieldSetMapper<BiologicalModelAggregator> {

        /**
         * Method used to map data obtained from a {@link FieldSet} into an object.
         *
         * @param fs the {@link FieldSet} to map
         * @throws BindException if there is a problem with the binding
         */
        @Override
        public BiologicalModelAggregator mapFieldSet(FieldSet fs) throws BindException {
            BiologicalModelAggregator bioModelAggregator = new BiologicalModelAggregator();

            bioModelAggregator.setAllelicComposition(fs.readString("allelicComposition"));
            bioModelAggregator.setAlleleSymbol(fs.readString("alleleSymbol"));
            bioModelAggregator.setGeneticBackground(fs.readString("geneticBackground"));
            bioModelAggregator.setMarkerAccessionId(fs.readString("markerAccessionId"));
            bioModelAggregator.setMpAccessionId(fs.readString("mpAccessionId"));

            return bioModelAggregator;
        }
    }


    // Step IMPLEMENTATION


    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "bioModelLoaderStep";
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

        // NOTE: PROCESS THE ENTIRE FILE BEFORE WRITING, AS THE FILE IS AN AGGREGATE.

        Step loadBioModelStep = stepBuilderFactory.get("loadBioModelStep")
                .listener(new BiologicalModelLoaderStepListener())
                .chunk(1000)
                .reader(bioModelReader)
                .processor(bioModelProcessor)
                // Don't add a writer here, as this processing simply populates the bioModels map.
                .build();

        // Synchronous flows.
        FlowBuilder<Flow> synchronousFlowBuilder = new FlowBuilder<Flow>("bioModelLoaderFlow").start(loadBioModelStep);
        Flow flow = synchronousFlowBuilder.build();

        stepBuilderFactory.get("bioModelLoaderStep")
                .flow(flow)
                .build()
                .execute(stepExecution);
    }

    public class BiologicalModelLoaderStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("BIOMODEL: Added {} new bioModels to map from file {} in {}. Multiple alleles per row (skipped): {}. Multiple genes per row (skipped): {}",
                    ((BiologicalModelProcessor)bioModelProcessor).getAddedBioModelsCount(),
                    bioModelKeys.get(FilenameKeys.MGI_PhenoGenoMP),
                    commonUtils.formatDateDifference(start, stop),
                    ((BiologicalModelProcessor)bioModelProcessor).getMultipleAllelesPerRowCount(),
                    ((BiologicalModelProcessor)bioModelProcessor).getMultipleGenesPerRowCount());

            // Write the bioModels map to the database.
            start = new Date();
            logger.info("Writing bioModels to database");

            List<BiologicalModelProcessor> bioModels = new ArrayList(((BiologicalModelProcessor) bioModelProcessor).getBioModels().values());
            try {
                writer.write(bioModels);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            stop = new Date();
            logger.info("Wrote {} bioModels to database in {}",
                    bioModels.size(),
                    commonUtils.formatDateDifference(start, stop));

            return ((BiologicalModelProcessor) bioModelProcessor).getErrMessages();
        }
    }
}