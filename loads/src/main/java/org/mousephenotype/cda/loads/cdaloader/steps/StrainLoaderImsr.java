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
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Loads the report.txt IMSR strain file into the strain and synonym tables of the target database.
 * As of 31-May-2016, the report.txt file's first line is a heading that must be skipped.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class StrainLoaderImsr implements Step, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Required for ItemReader
    private String sourceFilename;

    private FlatFileItemReader<Strain> imsrReader;
    private JdbcTemplate               jdbcTemplate;
    private List<Strain>               mgiStrainList = new ArrayList<>();
    private int                        readIndex;
    private StepBuilderFactory         stepBuilderFactory;
    private StrainWriter               writer;

    // The following IMSR_ ints define the column offset of the given column in the report.txt file.
    public final static int    STRAIN_STOCK_OFFSET  = 2;
    public final static int    SYNONYMS_OFFSET      = 5;
    public final static int    STRAINTYPE_OFFSET    = 6;

    // The following  ints define the column offset of the given column in the report.txt file.
    public final static String STRAIN_STOCK_HEADING = "Strain/Stock";
    public final static String SYNONYMS_HEADING     = "Synonyms";
    public final static String STRAINTYPE_HEADING   = "Type";
    

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    public StrainLoaderImsr(String sourceFilename, StepBuilderFactory stepBuilderFactory, StrainWriter writer) throws CdaLoaderException {
        this.sourceFilename = sourceFilename;
        this.stepBuilderFactory = stepBuilderFactory;
        this.writer = writer;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
  	    Assert.notNull(sourceFilename, "sourceFilename must be set");
        jdbcTemplate = sqlLoaderUtils.getJdbcTemplate();

        imsrReader = new FlatFileItemReader<>();
        imsrReader.setResource(new FileSystemResource(sourceFilename));
        imsrReader.setLineMapper((line, lineNumber) -> {
            Strain strain = new Strain();
            String[] values = line.split(Pattern.quote("\t"));

            // lineNumber is 1-relative. The heading is on line 1.
            if (lineNumber == 1) {
                // Do some basic validation. The file format has changed drastically in the past.
                String expectedValue = STRAIN_STOCK_HEADING;
                String actualValue = values[STRAIN_STOCK_OFFSET];
                if ( ! expectedValue.equals(actualValue)) {
                    throw new CdaLoaderException("Expected column " + STRAIN_STOCK_OFFSET
                            + " to equal " + expectedValue + " but was " + actualValue);
                }

                expectedValue = SYNONYMS_HEADING;
                actualValue = values[SYNONYMS_OFFSET];
                if ( ! expectedValue.equals(actualValue)) {
                    throw new CdaLoaderException("Expected column " + SYNONYMS_OFFSET
                            + " to equal " + expectedValue + " but was " + actualValue);
                }

                expectedValue = STRAINTYPE_HEADING;
                actualValue = values[STRAINTYPE_OFFSET];
                if ( ! expectedValue.equals(actualValue)) {
                    throw new CdaLoaderException("Expected column " + STRAINTYPE_OFFSET
                            + " to equal " + expectedValue + " but was " + actualValue);
                }
            } else {
                strain.setId(new DatasourceEntityId(values[STRAIN_STOCK_OFFSET], DbIdType.MGI.intValue()));
                strain.setName(values[STRAIN_STOCK_OFFSET]);



                try {
                    strain.setBiotype(sqlLoaderUtils.getBiotype(values[STRAINTYPE_OFFSET]));
                } catch (CdaLoaderException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }

            return strain;
        });

        // Populate mgi strain list.
        mgiStrainList.addAll(sqlLoaderUtils.getStrains());

        // Read the file.
        imsrReader.open(new ExecutionContext());
        int i = 0;
        Strain strain;
        while ((strain = imsrReader.read()) != null) {
            if (i == 0)
                continue;       // Skip over the heading. It is only used to validate the column names are as expected.

            // Process a row.


            i++;
        }
    }

    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "strainLoaderImsrStep";
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
                .reader(imsrReader)
                .writer(writer)
                .build()
                .execute(stepExecution);
    }
}