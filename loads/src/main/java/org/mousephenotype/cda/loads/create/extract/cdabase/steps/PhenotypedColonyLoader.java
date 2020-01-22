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
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.BlankLineRecordSeparatorPolicy;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.LogStatusStepListener;
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
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Loads the iMits colony information from the iMits report files.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class PhenotypedColonyLoader implements InitializingBean, Step {

    public Map<FilenameKeys, String> phenotypedColonyKeys = new HashMap<>();
    private final Logger             logger               = LoggerFactory.getLogger(this.getClass());

    private FlatFileItemReader<PhenotypedColony> phenotypedColonyReader = new FlatFileItemReader<>();
    private Boolean resourceAlreadySet = false;

    public enum FilenameKeys {
        EBI_PhenotypedColony
    }


    // Fields within EBI_phenotyped_colonies.tsv:
    private final String[] phenotypedColonyColumnNames = new String[] {
              "markerSymbol"                // A - Marker Symbol
            , "mgiMarkerAccessionId"        // B - MGI Accession ID
            , "colonyName"                  // C - Colony Name
            , "esCellName"                  // D - Es Cell Name
            , "backgroundStrainName"        // E - Colony Background Strain Name
            , "backgroundStrainAcc"         // F - Colony Background Strain MGI Accession ID
            , "productionCentre"            // G - Production Centre        (IGNORED)
            , "productionConsortium"        // H - Production Consortium
            , "phenotypingCentre"           // I - Phenotyping Centre
            , "phenotypingConsortium"       // J - Phenotyping Consortium
            , "cohortProductionCentre"      // K - Cohort Production Centre
            , "alleleSymbol"                // L - Allele Symbol
        };

    @Autowired
    @Qualifier("phenotypedColonyProcessor")
    private ItemProcessor phenotypedColonyProcessor;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PhenotypedColonyWriter writer;


    public PhenotypedColonyLoader(Map<FilenameKeys, String> phenotypedColonyKeys) throws DataLoadException {
        this.phenotypedColonyKeys = phenotypedColonyKeys;
    }

    public PhenotypedColonyLoader(Resource inputFileResource) throws DataLoadException {
        phenotypedColonyReader.setResource(inputFileResource);
        this.resourceAlreadySet = true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if ( ! resourceAlreadySet ) {
            phenotypedColonyReader.setResource(new FileSystemResource(phenotypedColonyKeys.get(FilenameKeys.EBI_PhenotypedColony)));
        }

        phenotypedColonyReader.setComments(new String[] {"#" });
        phenotypedColonyReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<PhenotypedColony> lineMapperPhenotypedColony = new DefaultLineMapper<>();
        DelimitedLineTokenizer              tokenizerPhenotypedColony  = new DelimitedLineTokenizer("\t");
        tokenizerPhenotypedColony.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerPhenotypedColony.setNames(phenotypedColonyColumnNames);
        lineMapperPhenotypedColony.setLineTokenizer(tokenizerPhenotypedColony);
        lineMapperPhenotypedColony.setFieldSetMapper(new PhenotypedColonyFieldSetMapper());
        phenotypedColonyReader.setLineMapper(lineMapperPhenotypedColony);
    }

    public class PhenotypedColonyFieldSetMapper implements FieldSetMapper<PhenotypedColony> {

        /**
         * Method used to map data obtained from a {@link FieldSet} into an object.
         *
         * @param fs the {@link FieldSet} to map
         * @throws BindException if there is a problem with the binding
         */
        @Override
        public PhenotypedColony mapFieldSet(FieldSet fs) throws BindException {
            PhenotypedColony phenotypedColony = new PhenotypedColony();

            phenotypedColony.setColonyName(fs.readString("colonyName"));

            GenomicFeature gene = new GenomicFeature();
            gene.setSymbol(fs.readString("markerSymbol"));
            DatasourceEntityId dsId = new DatasourceEntityId();
            dsId.setAccession(fs.readString("mgiMarkerAccessionId"));
            gene.setId(dsId);
            phenotypedColony.setGene(gene);

            phenotypedColony.setAlleleSymbol(fs.readString("alleleSymbol"));

            phenotypedColony.setEs_cell_name(fs.readString("esCellName"));

            phenotypedColony.setBackgroundStrain(fs.readString("backgroundStrainName"));
            phenotypedColony.setBackgroundStrainAcc(fs.readString("backgroundStrainAcc"));

            Organisation phenotypingCentre = new Organisation();

            // iMits stores the center name as MARC, we receive the center name as Ning, with MARC
            // being the consortium name
            String phenotypingCenterName = fs.readString("phenotypingCentre");
            phenotypingCenterName = (phenotypingCenterName.equalsIgnoreCase("MARC")) ? "Ning" : phenotypingCenterName;
            phenotypingCentre.setName(phenotypingCenterName);
            phenotypedColony.setPhenotypingCentre(phenotypingCentre);

            Project phenotypingConsortium = new Project();
            phenotypingConsortium.setName(fs.readString("phenotypingConsortium"));
            phenotypedColony.setPhenotypingConsortium(phenotypingConsortium);

            /**
             * The imits column 'cohortProductionCentre' maps to our productionCentre.
             // iMits stores the center name as MARC, we receive the center name as Ning, with MARC
             // being the consortium name
             */
            Organisation productionCentre = new Organisation();
            String productionCenterName = fs.readString("cohortProductionCentre");
            productionCenterName = (productionCenterName.equalsIgnoreCase("MARC")) ? "Ning" : productionCenterName;
            productionCentre.setName(productionCenterName);
            phenotypedColony.setProductionCentre(productionCentre);

            Project productionConsortium = new Project();
            productionConsortium.setName(fs.readString("productionConsortium"));
            phenotypedColony.setProductionConsortium(productionConsortium);

            return phenotypedColony;
        }
    }


    // Step IMPLEMENTATION


    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "phenotypedColonyLoaderStep";
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

        Step loadPhenotypedColonyStep = stepBuilderFactory.get("loadPhenotypedColonyStep")
                .listener(new PhenotypedColonyLoaderStepListener())
                .chunk(100000)
                .reader(phenotypedColonyReader)
                .processor(phenotypedColonyProcessor)
                .writer(writer)
                .build();

        // Synchronous flows.
        FlowBuilder<Flow> synchronousFlowBuilder = new FlowBuilder<Flow>("phenotypedColonyLoaderFlow").start(loadPhenotypedColonyStep);
        Flow flow = synchronousFlowBuilder.build();

        stepBuilderFactory.get("phenotypedColonyLoaderStep")
                .flow(flow)
                .build()
                .execute(stepExecution);
    }

    public class PhenotypedColonyLoaderStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("PHENOTYPED_COLONY: Added {} new phenotypeColonies and {} genes to database from file {} in {}.",
                        ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getAddedPhenotypedColoniesCount(),
                        ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getAddedGenesCount(),
                        phenotypedColonyKeys.get(FilenameKeys.EBI_PhenotypedColony),
                        commonUtils.formatDateDifference(start, stop));

            Set<String> missingOrganisations = ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getMissingOrganisations();
            if ( ! missingOrganisations.isEmpty()) {
                logger.info("Missing organisations:");
                for (String organisation : missingOrganisations.toArray(new String[0])) {
                    logger.warn("\t" + organisation);
                }
            }
            
            Set<String> missingProjects = ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getMissingProjects();
            if ( ! missingProjects.isEmpty()) {
                logger.info("Missing projects:");
                for (String project : missingProjects.toArray(new String[0])) {
                    logger.warn("\t" + project);
                }
            }
            logger.info("");

            return ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getErrMessages();
        }
    }
}