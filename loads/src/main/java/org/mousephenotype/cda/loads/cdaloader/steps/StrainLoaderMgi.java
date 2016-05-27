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
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.regex.Pattern;

/**
 * Loads a single ontology file into the ontology_term table of the target database.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class StrainLoaderMgi implements Step, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Required for ItemReader
    private String sourceFilename;

    private FlatFileItemReader<Strain> mgiReader;
    private FlatFileItemReader<Strain> imsrReader;
    private StrainWriter               writer;
    private StepBuilderFactory         stepBuilderFactory;

    @Autowired
    @Qualifier("komp2Loads")
    private DataSource komp2Loads;


    public StrainLoaderMgi(String sourceFilename, StepBuilderFactory stepBuilderFactory, StrainWriter writer) throws CdaLoaderException {
        this.sourceFilename = sourceFilename;
        this.stepBuilderFactory = stepBuilderFactory;
        this.writer = writer;

        mgiReader = new FlatFileItemReader<>();
        mgiReader.setResource(new FileSystemResource(sourceFilename));
        mgiReader.setComments( new String[] { "#" });
        mgiReader.setLineMapper((line, lineNumber) -> {
            Strain strain = new Strain();

            String[] values = line.split(Pattern.quote("\t"));
            strain.setId(new DatasourceEntityId(values[0], DbIdType.MGI.intValue()));
            strain.setName(values[1]);
            strain.setBiotype(getBiotype(values[2]));

            return strain;
        });
    }

    private OntologyTerm getBiotype(String strainType) throws CdaLoaderException {
        JdbcTemplate jdbcTemplate;
        try {
                jdbcTemplate = new JdbcTemplate(komp2Loads);
            } catch (Exception e) {
                throw new CdaLoaderException(e);
            }

        OntologyTerm term = SqlUtils.getOntologyTerm(jdbcTemplate, strainType);

        if (term == null) {
            throw new CdaLoaderException("No CV term found for strain biotype " + strainType);
        }

        return term;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
  	    Assert.notNull(sourceFilename, "sourceFilename must be set");
    }

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
        stepBuilderFactory.get("strainLoaderStep")
                .chunk(100)
                .reader(mgiReader)
                .writer(writer)
                .build()
                .execute(stepExecution);
    }
}