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

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Loads a single ontology file into the ontology_term table of the target database.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class OntologyLoader implements Step, Tasklet, InitializingBean {

    private CdaSqlUtils cdaSqlUtils;
    private CommonUtils          commonUtils = new CommonUtils();
    private long                 dbId;
    private final Logger         logger      = LoggerFactory.getLogger(this.getClass());
    private String               prefix;
    private StepBuilderFactory   stepBuilderFactory;
    private String               sourceFilename;
    private Map<String, Integer> written     = new HashMap<>();


    public OntologyLoader(String sourceFilename, int dbId, String prefix, StepBuilderFactory stepBuilderFactory, CdaSqlUtils cdaSqlUtils) throws DataLoadException {
        this.sourceFilename = sourceFilename;
        this.dbId = dbId;
        this.prefix = prefix;
        this.stepBuilderFactory = stepBuilderFactory;
        this.cdaSqlUtils = cdaSqlUtils;

        written.put("terms", 0);
        written.put("synonyms", 0);
        written.put("considerIds", 0);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(dbId, "dbId must be set");
  	    Assert.notNull(sourceFilename, "sourceFilename must be set");
        Assert.notNull(prefix, "prefix must be set");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must be set");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must be set");
    }

    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "ontologyLoaderStep";
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
        return 10;
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
        stepBuilderFactory.get("ontologyLoaderStep")
                .tasklet(this)
                .build()
                .execute(stepExecution);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();

        List<OntologyTermDTO> dtoTerms;
        List<OntologyTerm> terms;

        dtoTerms = new OntologyParser(sourceFilename, prefix, null, null).getTerms();
        terms = ontologyDTOTermsToOntologyTerms(dtoTerms);

        logger.info("FILENAME: {}. PREFIX: {}. TERMS COUNT: {}.",
                    sourceFilename, prefix, terms.size());

        Map<String, Integer> counts = cdaSqlUtils.insertOntologyTerm(terms);
        written.put("terms", written.get("terms") + counts.get("terms"));
        written.put("synonyms", written.get("synonyms") + counts.get("synonyms"));
        written.put("considerIds", written.get("considerIds") + counts.get("considerIds"));

        logger.debug("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
        contribution.setExitStatus(ExitStatus.COMPLETED);
        chunkContext.setComplete();

        return RepeatStatus.FINISHED;
    }

    private List<OntologyTerm> ontologyDTOTermsToOntologyTerms(List<OntologyTermDTO> dtoTerms) {
        List<OntologyTerm> terms = new ArrayList<>();

        for (OntologyTermDTO dtoTerm : dtoTerms) {
            OntologyTerm term = new OntologyTerm();

            term.setId(new DatasourceEntityId(dtoTerm.getAccessionId(), dbId));

            Set<AlternateId> alternateIds = new HashSet<>();
            if ((dtoTerm.getAlternateIds() != null) && ( ! dtoTerm.getAlternateIds().isEmpty())) {
                alternateIds = dtoTerm.getAlternateIds().stream().map(alternateIdString -> new AlternateId(dtoTerm.getAccessionId(), alternateIdString)).collect(Collectors.toSet());
            }
            term.setAlternateIds(alternateIds);

            Set<ConsiderId> considerIds = new HashSet<>();
            if ((dtoTerm.getConsiderIds() != null) && ( ! dtoTerm.getConsiderIds().isEmpty())) {
                considerIds = dtoTerm.getConsiderIds().stream().map(considerIdString -> new ConsiderId(dtoTerm.getAccessionId(), considerIdString)).collect(Collectors.toSet());
            }
            term.setConsiderIds(considerIds);

            term.setDescription(dtoTerm.getDefinition());
            term.setIsObsolete(dtoTerm.isObsolete());
            term.setName(dtoTerm.getName());
            term.setReplacementAcc(dtoTerm.getReplacementAccessionId());
            List<Synonym> synonyms = new ArrayList<>();
            if ((term.getSynonyms() == null) || (term.getSynonyms().isEmpty())) {
                term.setSynonyms(synonyms);
            } else {
                synonyms = dtoTerm.getSynonyms().stream().map(synonymString -> {
                    Synonym synonym = new Synonym();
                    synonym.setAccessionId(dtoTerm.getAccessionId());
                    synonym.setDbId(dbId);
                    synonym.setSymbol(synonymString);
                    return synonym;
                }).collect(Collectors.toList());
                term.setSynonyms(synonyms);
            }

            terms.add(term);
        }

        return terms;
    }
}