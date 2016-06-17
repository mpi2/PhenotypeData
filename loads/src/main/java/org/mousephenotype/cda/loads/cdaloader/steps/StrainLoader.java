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

import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.LogStatusStepListener;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
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
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Loads the MGI_Strain.rpt file into the strain table of the target database.
 * As of 31-May-2016, the MGI_Strain.rpt has no heading line; thus, no skip required.
 * Comments are denoted by lines starting with '#'
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class StrainLoader implements InitializingBean, Step {

    private       int                       addedMgiStrainCount = 0;
    private final Logger                    logger              = LoggerFactory.getLogger(this.getClass());
    public        Map<FilenameKeys, String> strainKeys          = new HashMap<>();
    private FlatFileItemReader<Strain>      mgiReader;
    private FlatFileItemReader<FieldSet>    imsrReader;


    public enum FilenameKeys {
          MGI
        , IMSR
    }

    @Autowired
    private ItemProcessor strainProcessorImsr;

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private StrainWriter writer;


    public StrainLoader(Map<FilenameKeys, String> markerKeys) throws CdaLoaderException {
        this.strainKeys = markerKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        mgiReader = new FlatFileItemReader<>();
        mgiReader.setResource(new FileSystemResource(strainKeys.get(FilenameKeys.MGI)));
        mgiReader.setComments( new String[] { "#" });
        mgiReader.setLineMapper((line, lineNumber) -> {
            Strain strain = new Strain();

            String[] values = line.split(Pattern.quote("\t"));
            strain.setId(new DatasourceEntityId(values[0], DbIdType.MGI.intValue()));
            strain.setName(values[1]);
            strain.setBiotype(sqlLoaderUtils.getBiotype(values[2]));
            strain.setSynonyms(new ArrayList<>());
            addedMgiStrainCount++;

            return strain;
        });

        imsrReader = new FlatFileItemReader<>();
        imsrReader.setResource(new FileSystemResource(strainKeys.get(FilenameKeys.IMSR)));
        imsrReader.setLineMapper((line, lineNumber) -> {
            FieldSet fieldset = new DefaultFieldSet(line.split(Pattern.quote("\t")));
            return fieldset;
        });
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
                .chunk(1000)
                .reader(mgiReader)
                .writer(writer)
                .build();

        Step loadStrainImsrStep = stepBuilderFactory.get("strainLoaderImsrStep")
                .listener(new StrainLoaderImsrStepListener())
                .chunk(1000)
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
            logger.info("MGI: Added {} strains (with no synonyms) from file {} in {}",
                    addedMgiStrainCount,
                    strainKeys.get(FilenameKeys.MGI),
                    commonUtils.formatDateDifference(start, stop));

            return new HashSet<>();
        }
    }

    public class StrainLoaderImsrStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("IMSR: Added {} strains and {} synonyms from file {} in {}",
                    ((StrainProcessorImsr)strainProcessorImsr).getAddedEucommStrainCount(),
                    ((StrainProcessorImsr)strainProcessorImsr).getAddedSynonymCount(),
                    strainKeys.get(FilenameKeys.IMSR),
                    commonUtils.formatDateDifference(start, stop));

            return ((StrainProcessorImsr) strainProcessorImsr).getErrorMessages();
        }
    }
}