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

package org.mousephenotype.cda.loads;

/**
 * This class encapsulates bean configuration necessary to set up and execute 'loads' spring-batch processing.
 *
 * 03-Nov-2015 (mrelac)
 * This confgiguration class is curently not used. I've left it in as an example but commented the annotations out
 * as they cause intellij to complain about unmapped configuration files.
 *
 * Created by mrelac on 25/06/2015.
 */
//@Configuration
//@EnableBatchProcessing
public class BatchConfiguration {

//    @Autowired
//    private JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    private StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Step downloadGenomicFeaturesStep() {
//        return stepBuilderFactory.get("downloadGenomicFeaturesStep")
//                .step(new Tasklet() {
//                    @Override
//                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
//                        return null;
//                    }
//                }).build();
//    }
//
//    @Bean
//    public Job job(Step downloadGenomicFeaturesStep) throws Exception {
//        return jobBuilderFactory.get("job1")
//                .incrementer(new RunIdIncrementer())
//                .start(downloadGenomicFeaturesStep)
//                .build();
//    }
}
