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

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.LineMapperFieldSet;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.validation.BindException;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Loads the alleles from the mgi report files.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class AlleleLoader implements InitializingBean, Step {

    public        Map<FilenameKeys, String> alleleKeys = new HashMap<>();
    private final Logger                    logger     = LoggerFactory.getLogger(this.getClass());

    private FlatFileItemReader<FieldSet> eucommReader     = new FlatFileItemReader<>();
    private FlatFileItemReader<FieldSet> genophenoReader  = new FlatFileItemReader<>();
    private FlatFileItemReader<FieldSet> kompReader       = new FlatFileItemReader<>();
    private FlatFileItemReader<FieldSet> norcomReader     = new FlatFileItemReader<>();


    private FlatFileItemReader<Allele> phenotypicReader = new FlatFileItemReader<>();
    private FlatFileItemReader<Allele> qtlReader        = new FlatFileItemReader<>();

    public enum FilenameKeys {
          EUCOMM
        , GENOPHENO
        , KOMP
        , NORCOM
        , PHENOTYPIC
        , QTL
    }

    @Autowired
    @Qualifier("alleleProcessorPhenotypic")
    private ItemProcessor alleleProcessorPhenotypic;

    @Autowired
    @Qualifier("alleleProcessorQtl")
    private ItemProcessor alleleProcessorQtl;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private AlleleWriter writer;


    public AlleleLoader(Map<FilenameKeys, String> alleleKeys) throws CdaLoaderException {
        this.alleleKeys = alleleKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eucommReader.setResource(new FileSystemResource(alleleKeys.get(FilenameKeys.EUCOMM)));
        eucommReader.setLineMapper(new LineMapperFieldSet());

        genophenoReader.setResource(new FileSystemResource(alleleKeys.get(FilenameKeys.GENOPHENO)));
        genophenoReader.setLineMapper(new LineMapperFieldSet());

        kompReader.setResource(new FileSystemResource(alleleKeys.get(FilenameKeys.KOMP)));
        kompReader.setLineMapper(new LineMapperFieldSet());

        norcomReader.setResource(new FileSystemResource(alleleKeys.get(FilenameKeys.NORCOM)));
        norcomReader.setLineMapper(new LineMapperFieldSet());

        phenotypicReader.setResource(new FileSystemResource(alleleKeys.get(FilenameKeys.PHENOTYPIC)));
        phenotypicReader.setComments(new String[] { "#" });
        DefaultLineMapper<Allele> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer("\t");
        tokenizer.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizer.setNames(new String[] { "mgiAlleleAccessionId", "symbol", "name", "biotype", "unused_4", "unused_5", "mgiMarkerAccessionId", "unused_7", "unused_8", "unused_9", "unused_10", "synonyms" });
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new AlleleFieldSetMapper());
        phenotypicReader.setLineMapper(lineMapper);

        qtlReader.setResource(new FileSystemResource(alleleKeys.get(FilenameKeys.QTL)));
        qtlReader.setComments(new String[] { "#" });
        DefaultLineMapper<Allele> lineMapperQtl = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizerQtl = new DelimitedLineTokenizer("\t");
        tokenizerQtl.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerQtl.setNames(new String[] { "mgiAlleleAccessionId", "symbol", "name", "biotype", "unused_4", "mgiMarkerAccessionId" });
        lineMapperQtl.setLineTokenizer(tokenizerQtl);
        lineMapperQtl.setFieldSetMapper(new AlleleFieldSetMapper());
        qtlReader.setLineMapper(lineMapperQtl);
    }

    public class AlleleFieldSetMapper implements FieldSetMapper<Allele> {

        /**
         * Method used to map data obtained from a {@link FieldSet} into an object.
         *
         * @param fs the {@link FieldSet} to map
         * @throws BindException if there is a problem with the binding
         */
        @Override
        public Allele mapFieldSet(FieldSet fs) throws BindException {
            Allele allele = new Allele();
            DatasourceEntityId dsIdAllele = new DatasourceEntityId();
            dsIdAllele.setAccession(fs.readString("mgiAlleleAccessionId"));

            DatasourceEntityId dsIdGene = new DatasourceEntityId();
            dsIdGene.setAccession(fs.readString("mgiMarkerAccessionId"));

            GenomicFeature gene = new GenomicFeature();
            gene.setId(dsIdGene);

            OntologyTerm biotype = new OntologyTerm();
            try {
                biotype.setName(fs.readString("biotype"));                      // Optional field that may throw IndexOutOfBoundsException
            } catch (Exception e) { }

            List<Synonym> synonyms = new ArrayList<>();
            try {
                String[] synonymsArray = fs.readString("synonyms").split(Pattern.quote("|"));  // Optional field that may throw IndexOutOfBoundsException
                for (String synonymSymbol : synonymsArray) {
                    if (synonymSymbol.isEmpty())
                        continue;
                    Synonym synonym = new Synonym();
                    synonym.setSymbol(synonymSymbol);
                    synonyms.add(synonym);
                }
            } catch (Exception e) { }


            allele.setId(dsIdAllele);
            allele.setBiotype(biotype);
            allele.setGene(gene);
            allele.setName(fs.readString("name"));
            allele.setSymbol(fs.readString("symbol"));
            allele.setSynonyms(synonyms);

            return allele;
        }
    }


    // Step IMPLEMENTATION


    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "alleleLoaderStep";
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

        Step loadPhenotypicsStep = stepBuilderFactory.get("alleleLoaderPhenotypicStep")
                .listener(new AlleleLoaderPhenotypicStepListener())
                .chunk(1000)
                .reader(phenotypicReader)
                .processor(alleleProcessorPhenotypic)
                .writer(writer)
                .build();

        Step loadQtlsStep = stepBuilderFactory.get("alleleLoaderQtlStep")
                .listener(new AlleleLoaderQtlStepListener())
                .chunk(1000)
                .reader(qtlReader)
                .processor(alleleProcessorQtl)
                .writer(writer)
                .build();


        // Synchronous flows.
        List<Flow> synchronousFlows = new ArrayList<>();
        synchronousFlows.add(new FlowBuilder<Flow>("alleleLoaderPhenotypicsFlow")
                .from(loadPhenotypicsStep).end());
        synchronousFlows.add(new FlowBuilder<Flow>("alleleLoaderQtlsFlow")
                .from(loadQtlsStep).end());
        FlowBuilder<Flow> synchronousFlowBuilder = new FlowBuilder<Flow>("alleleLoaderFlows").start(synchronousFlows.get(0));
        for (int i = 1; i < synchronousFlows.size(); i++) {
            synchronousFlowBuilder.next(synchronousFlows.get(i));
        }
        Flow flow = synchronousFlowBuilder.build();


//        // Parallel flows.
//        List<Flow> parallelFlows = new ArrayList<>();
//        parallelFlows.add(new FlowBuilder<Flow>("alleleLoaderPhenotypicsFlow")
//                .from(loadPhenotypicsStep).end());
//        parallelFlows.add(new FlowBuilder<Flow>("alleleLoaderQtlsFlow")
//                .from(loadQtlsStep).end());
//        FlowBuilder<Flow> parallelFlowBuilder = new FlowBuilder<Flow>("alleleLoaderParallelFlows").start(parallelFlows.get(0));
//        for (int i = 1; i < parallelFlows.size(); i++) {
//            SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor(Executors.defaultThreadFactory());
//            parallelFlowBuilder.split(executor).add(parallelFlows.get(i));
//        }
//        Flow flow = parallelFlowBuilder.build();


        stepBuilderFactory.get("alleleLoaderStep")
                .flow(flow)
                .build()
                .execute(stepExecution);
    }

    public class AlleleLoaderPhenotypicStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("PHENOTYPIC: Added {} new alleles to database from file {} in {}",
                    ((AlleleProcessorPhenotypic) alleleProcessorPhenotypic).getAddedAllelesCount(),
                    alleleKeys.get(FilenameKeys.PHENOTYPIC),
                    commonUtils.formatDateDifference(start, stop));

            return ((AlleleProcessorPhenotypic) alleleProcessorPhenotypic).getErrMessages();
        }
    }

    public class AlleleLoaderQtlStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("QTL: Added {} new alleles to database from file {} in {}",
                    ((AlleleProcessorQtl) alleleProcessorQtl).getAddedAllelesCount(),
                    alleleKeys.get(FilenameKeys.QTL),
                    commonUtils.formatDateDifference(start, stop));

            return ((AlleleProcessorQtl) alleleProcessorQtl).getErrMessages();
        }
    }
}