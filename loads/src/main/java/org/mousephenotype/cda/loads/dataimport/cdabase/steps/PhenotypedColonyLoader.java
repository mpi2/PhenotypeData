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

package org.mousephenotype.cda.loads.dataimport.cdabase.steps;

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.BlankLineRecordSeparatorPolicy;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.LogStatusStepListener;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Loads the alleles from the mgi report files.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class PhenotypedColonyLoader implements InitializingBean, Step {

    public Map<FilenameKeys, String> phenotypedColonyKeys = new HashMap<>();
    private final Logger             logger               = LoggerFactory.getLogger(this.getClass());

    private FlatFileItemReader<PhenotypedColony> phenotypedColonyReader = new FlatFileItemReader<>();

    public enum FilenameKeys {
        EBI_PhenotypedColony
    }


    // Fields within EBI_phenotyped_colonies.tsv:
    private final String[] phenotypedColonyColumnNames = new String[] {
              "markerSymbol"                // A - Marker Symbol
            , "mgiMarkerAccessionId"        // B - MGI Accession ID
            , "colonyName"                  // C - Colony Name
            , "esCellName"                  // D - Es Cell Name
            , "backgroundStrain"            // E - Colony Background Strain
            , "productionCentre"            // F - Production Centre
            , "productionConsortium"        // G - Production Consortium
            , "phenotypingCentre"           // H - Phenotyping Centre
            , "phenotypingConsortium"       // I - Phenotyping Consortium
            , "cohortProductionCentre"      // J - Cohort Production Centre
            , "alleleSymbol"                // K - Allele Symbol
        };

    @Autowired
    @Qualifier("phenotypedColonyProcessor")
    private ItemProcessor phenotypedColonyProcessor;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PhenotypedColonyWriter writer;


    public PhenotypedColonyLoader(Map<FilenameKeys, String> phenotypedColonyKeys) throws DataImportException {
        this.phenotypedColonyKeys = phenotypedColonyKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        phenotypedColonyReader.setResource(new FileSystemResource(phenotypedColonyKeys.get(FilenameKeys.EBI_PhenotypedColony)));
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

            GenomicFeature gene = new GenomicFeature();
            gene.setSymbol(fs.readString("markerSymbol"));
            DatasourceEntityId dsId = new DatasourceEntityId();
            dsId.setAccession(fs.readString("mgiMarkerAccessionId"));
            gene.setId(dsId);
            phenotypedColony.setGene(gene);

            phenotypedColony.setColonyName(fs.readString("colonyName"));

            phenotypedColony.setEs_cell_name(fs.readString("esCellName"));

            Strain backgroundStrain = new Strain();
            backgroundStrain.setName(fs.readString("backgroundStrain"));
            phenotypedColony.setStrain(backgroundStrain);

            Organisation productionCentre = new Organisation();
            productionCentre.setName(fs.readString("productionCentre"));
            phenotypedColony.setProductionCentre(productionCentre);

            Project productionConsortium = new Project();
            productionConsortium.setName(fs.readString("productionConsortium"));
            phenotypedColony.setProductionConsortium(productionConsortium);

            Organisation phenotypingCentre = new Organisation();
            phenotypingCentre.setName(fs.readString("phenotypingCentre"));
            phenotypedColony.setPhenotypingCentre(phenotypingCentre);

            Project phenotypingConsortium = new Project();
            phenotypingConsortium.setName(fs.readString("phenotypingConsortium"));
            phenotypedColony.setPhenotypingConsortium(phenotypingConsortium);

            Organisation cohortProductionCentre = new Organisation();
            cohortProductionCentre.setName(fs.readString("cohortProductionCentre"));
            phenotypedColony.setCohortProductionCentre(cohortProductionCentre);

            Allele allele = new Allele();
            allele.setSymbol(fs.readString("alleleSymbol"));
            phenotypedColony.setAllele(allele);

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
                .chunk(1000)
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
            logger.info("PHENOTYPED_COLONY: Added {} new phenotypeColonies, {} alleles, {} genes, and {} strains to database from file {} in {}.",
                        ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getAddedPhenotypedColoniesCount(),
                        ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getAddedAllelesCount(),
                        ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getAddedGenesCount(),
                        ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getAddedStrainsCount(),
                        phenotypedColonyKeys.get(FilenameKeys.EBI_PhenotypedColony),
                        commonUtils.formatDateDifference(start, stop));
            
            Set<String> missingStrains = ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getMissingStrains();
            if ( ! missingStrains.isEmpty()) {
                logger.warn("Missing strains:");
                for (String strain : missingStrains.toArray(new String[0])) {
                    logger.warn("\t" + strain);
                }
            }
            
            Set<String> missingOrganisations = ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getMissingOrganisations();
            if ( ! missingOrganisations.isEmpty()) {
                logger.warn("Missing organisations:");
                for (String organisation : missingOrganisations.toArray(new String[0])) {
                    logger.warn("\t" + organisation);
                }
            }
            
            Set<String> missingProjects = ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getMissingProjects();
            if ( ! missingProjects.isEmpty()) {
                logger.warn("Missing projects:");
                for (String project : missingProjects.toArray(new String[0])) {
                    logger.warn("\t" + project);
                }
            }
            logger.info("");

            return ((PhenotypedColonyProcessor) phenotypedColonyProcessor).getErrMessages();
        }
    }
}