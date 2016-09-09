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

package org.mousephenotype.cda.loads.create.extract.cdabase.steps;

import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.BlankLineRecordSeparatorPolicy;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.LogStatusStepListener;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.StrainFieldSetMapper;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
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
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import java.util.*;

/**
 * Loads the MGI_Strain.rpt file into the strain table of the target database.
 * As of 31-May-2016, the MGI_Strain.rpt has no heading line; thus, no skip required.
 * Comments are denoted by lines starting with '#'
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class StrainLoader implements InitializingBean, Step {

    private FlatFileItemReader<Strain>      imsrReader          = new FlatFileItemReader<>();
    private FlatFileItemReader<Strain>      mgiReader           = new FlatFileItemReader<>();
    public        Map<FilenameKeys, String> strainKeys          = new HashMap<>();

    private final Logger                    logger              = LoggerFactory.getLogger(this.getClass());

    public enum FilenameKeys {
          IMSR
        , MGI
    }

    @Autowired
    private ItemProcessor strainProcessorImsr;

    @Autowired
    private ItemProcessor strainProcessorMgi;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private StrainWriter writer;


    public StrainLoader(Map<FilenameKeys, String> markerKeys) throws DataLoadException {
        this.strainKeys = markerKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        mgiReader.setResource(new FileSystemResource(strainKeys.get(FilenameKeys.MGI)));
        mgiReader.setComments( new String[] { "#" });
        mgiReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<Strain> lineMapperStrainMgi = new DefaultLineMapper<>();
        DelimitedLineTokenizer    tokenizerStrainMgi  = new DelimitedLineTokenizer("\t");
        tokenizerStrainMgi.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerStrainMgi.setNames(new String[] { "mgiStrainAccessionId", "name", "biotype" });
        lineMapperStrainMgi.setLineTokenizer(tokenizerStrainMgi);
        lineMapperStrainMgi.setFieldSetMapper(new StrainFieldSetMapper());
        mgiReader.setLineMapper(lineMapperStrainMgi);
        
        imsrReader.setResource(new FileSystemResource(strainKeys.get(FilenameKeys.IMSR)));
        imsrReader.setComments( new String[] { "#" });
        imsrReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<Strain> lineMapperStrainImsr = new DefaultLineMapper<>();
        DelimitedLineTokenizer    tokenizerStrainImsr  = new DelimitedLineTokenizer("\t");
        tokenizerStrainImsr.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerStrainImsr.setNames(new String[] { "unused_1", "mgiStrainAccessionId", "name", "unused_4", "unused_5", "synonyms", "biotype" });
        lineMapperStrainImsr.setLineTokenizer(tokenizerStrainImsr);
        lineMapperStrainImsr.setFieldSetMapper(new StrainFieldSetMapper());
        imsrReader.setLineMapper(lineMapperStrainImsr);
    }


    // Step IMPLEMENTATION


    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "strainLoaderStep";
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

        Step loadStrainMgiStep = stepBuilderFactory.get("strainLoaderMgiStep")
                .listener(new StrainLoaderMgiStepListener())
                .chunk(100000)
                .reader(mgiReader)
                .processor(strainProcessorMgi)
                .writer(writer)
                .build();

        Step loadStrainImsrStep = stepBuilderFactory.get("strainLoaderImsrStep")
                .listener(new StrainLoaderImsrStepListener())
                .chunk(100000)
                .reader(imsrReader)
                .processor(strainProcessorImsr)
                .writer(writer)
                .build();

        List<Flow> flows = new ArrayList<>();
        flows.add(new FlowBuilder<Flow>("strainLoaderMgiFlow")
                .from(loadStrainMgiStep).end());
        flows.add(new FlowBuilder<Flow>("strainLoaderImsrFlow")
                .from(loadStrainImsrStep).end());

        FlowBuilder<Flow> flowBuilder = new FlowBuilder<Flow>("strainFlows").start(flows.get(0));
        for (int i = 1; i < flows.size(); i++) {
            flowBuilder.next(flows.get(i));
        }

        stepBuilderFactory.get("strainLoaderStep")
                .flow(flowBuilder.build())
                .build()
                .execute(stepExecution);
    }

    public class StrainLoaderMgiStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("MGI: Added {} mgi strains (with no synonyms) to database from file {} in {}. StrainIsAllele count: {}.",
                    ((StrainProcessorMgi)strainProcessorMgi).getAddedMgiStrainsCount(),
                    strainKeys.get(FilenameKeys.MGI),
                    commonUtils.formatDateDifference(start, stop),
                    ((StrainProcessorMgi)strainProcessorMgi).getStrainIsAlleleCount());

            return ((StrainProcessorMgi)strainProcessorMgi).getErrorMessages();
        }
    }

    public class StrainLoaderImsrStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {

            int totalStrainsAdded =
                  ((StrainProcessorMgi)strainProcessorMgi).getAddedMgiStrainsCount()
                + ((StrainProcessorImsr)strainProcessorImsr).getAddedEucommStrainsCount();

            logger.info("IMSR: Added {} Eucomm strains and {} Eucomm synonyms to database from file {} in {}. StrainNotSynonym count: {}. AddedStrainNotSynonym count: {}. StrainIsAllele count: {}.",
                    ((StrainProcessorImsr)strainProcessorImsr).getAddedEucommStrainsCount(),
                    ((StrainProcessorImsr)strainProcessorImsr).getAddedEucommSynonymsCount(),
                    strainKeys.get(FilenameKeys.IMSR),
                    commonUtils.formatDateDifference(start, stop),
                    ((StrainProcessorImsr)strainProcessorImsr).getStrainNotSynonymCount(),
                    ((StrainProcessorImsr)strainProcessorImsr).getAddedStrainNotSynonymCount(),
                    ((StrainProcessorImsr)strainProcessorImsr).getStrainIsAlleleCount());
            logger.info("  Wrote {} strains to database.", totalStrainsAdded);
            logger.info("");

            return ((StrainProcessorImsr) strainProcessorImsr).getErrorMessages();
        }
    }
}