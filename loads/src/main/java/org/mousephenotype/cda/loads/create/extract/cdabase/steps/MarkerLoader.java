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

import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.Xref;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.XrefFieldSetMapper;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.BlankLineRecordSeparatorPolicy;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.GenesFieldSetMapper;
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
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;

import java.util.*;

/**
 * Loads the markers from the mgi report files.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class MarkerLoader implements InitializingBean, Step {

    private final Logger                       logger     = LoggerFactory.getLogger(this.getClass());
    public        Map<FilenameKeys, String>    markerKeys = new HashMap<>();

    private FlatFileItemReader<GenomicFeature> genesReader           = new FlatFileItemReader<>();
    private FlatFileItemReader<List<Xref>>     xrefEntrezGenesReader = new FlatFileItemReader<>();
    private FlatFileItemReader<List<Xref>>     xrefGenesReader       = new FlatFileItemReader<>();
    private FlatFileItemReader<List<Xref>>     xrefEnsemblReader     = new FlatFileItemReader<>();
    private FlatFileItemReader<List<Xref>>     xrefVegaReader        = new FlatFileItemReader<>();

    public enum FilenameKeys {
          MARKER_LIST
        , MARKER_COORDINATES
        , XREFS_HGNC_homologene
        , XREFS_MGI_EntrezGene
        , XREFS_MRK_ENSEMBL
        , XREFS_MRK_VEGA
    }


    // Fields within MRK_List1.rpt:
    private final String[] genesColumnNames = new String[] {
            "mgiMarkerAccessionId"      // A - MGI Accession ID
          , "chromosome"                // B - Chromosome
          , "cMposition"                // C - cM Position
          , "start"                     // D - Start Coordinate
          , "end"                       // E - End Coordinate
          , "strand"                    // F - Strand
          , "symbol"                    // G - Symbol
          , "status"                    // H - Status
          , "name"                      // I - Name
          , "biotype"                   // J - Marker Type
          , "subtype"                   // K - Marker Subtype
          , "synonyms"                  // L - Synonyms (| - delimited)
        };

    // Fields within HGNC_homologene.rpt:
    private final String[] xrefGeneColumnNames = new String[] {
            "mgiMarkerAccessionId"    // A - MGI Accession ID
          , "unused_2"                // B - Marker Symbol
          , "unused_3"                // C - Marker Name
          , "unused_4"                // D - Feature Type
          , "entrezId"                // E - EntrezGene ID
          , "unused_6"                // F - NCBI Gene chromosome
          , "unused_7"                // G - NCBI Gene start
          , "unused_8"                // H - NCBI Gene end
          , "unused_9"                // I - NCBI Gene strand
          , "ensemblId"               // J - Ensembl Gene ID
          , "unused_11"               // K - Ensembl Gene chromosom
          , "unused_12"               // L - Ensembl Gene start
          , "unused_13"               // M - Ensembl Gene end
          , "unused_14"               // N - Ensembl Gene strand
          , "vegaId"                  // O - VEGA Gene ID
          , "unused_16"               // P - VEGA Gene chromosome
          , "unused_17"               // Q - VEGA Gene start
          , "unused_18"               // R - VEGA Gene end
          , "unused_19"               // S - VEGA Gene strand
          , "ccdsId"                  // T - CCDS IDs
          , "unused_21"               // U - HGNC ID
          , "unused_22"               // V - HomoloGene ID
        };

    // Fields within MGI_EntrezGene.rpt:
    private final String[] xrefEntrezGeneColumnNames = new String[] {
              "mgiMarkerAccessionId"    // A - MGI Accession ID
            , "unused_2"                // B - Marker Symbol
            , "unused_3"                // C - Status
            , "unused_4"                // D - Marker Name
            , "unused_5"                // E - cM Position
            , "unused_6"                // F - Chromosome
            , "unused_7"                // G - Type
            , "unused_8"                // H - Secondary Accession IDs (|-delimited)
            , "entrezId"                // I - EntrezGene ID
            , "unused_10"               // J - Synonyms (|-delimited)
            , "unused_11"               // K - Feature Types (|-delimited)
            , "unused_12"               // L - Genome Coordinate Start
            , "unused_13"               // M - Genome Coordinate End
            , "unused_14"               // N - Strand
            , "unused_15"               // O - Biotypes (|-delimited)
        };

    // Fields within MRK_ENSEMBL.rpt:
    private final String[] xrefEnsemblColumnNames = new String[] {
              "mgiMarkerAccessionId"    // A - MGI Accession ID
            , "unused_2"                // B - Marker Symbol
            , "unused_3"                // C - Marker Name
            , "unused_4"                // D - cM Position
            , "unused_5"                // E - Chromosome
            , "ensemblId"               // F - Ensembl Accession ID
            , "unused_7"                // G - Ensembl Transcript ID (space-delimited, if any)
            , "unused_8"                // H - Ensembl Protein ID (space-delimited, if any)
            , "unused_9"                // I - Feature Types (|-delimited)
            , "unused_10"               // J - Genome Coordinate Start
            , "unused_11"               // K - Genome Coordinate End
            , "unused_12"               // L - Strand
            , "unused_13"               // M - Biotypes (|-delimited)
        };

    // Fields within MRK_VEGA.rpt:
    private final String[] xrefVegaColumnNames = new String[] {
            "mgiMarkerAccessionId"    // A - MGI Accession ID
          , "unused_2"                // B - Marker Symbol
          , "unused_3"                // C - Marker Name
          , "unused_4"                // D - cM Position
          , "unused_5"                // E - Chromosome
          , "ensemblId"               // F - VEGA Accession ID
          , "unused_7"                // G - VEGA Transcript ID (space-delimited, if any)
          , "unused_8"                // H - VEGA Protein ID (space-delimited, if any)
          , "unused_9"                // I - Feature Types (|-delimited)
          , "unused_10"               // J - Genome Coordinate Start
          , "unused_11"               // K - Genome Coordinate End
          , "unused_12"               // L - Strand
          , "unused_13"               // M - Biotypes (|-delimited)
        };

    @Autowired
    private ItemProcessor markerProcessorGenes;

    @Autowired
    private ItemProcessor markerProcessorXrefGenes;

    @Autowired
    private ItemProcessor markerProcessorXrefEntrezGene;

    @Autowired
    private ItemProcessor markerProcessorXrefEnsembl;

    @Autowired
    private ItemProcessor markerProcessorXrefVega;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MarkerWriter writer;


    public MarkerLoader(Map<FilenameKeys, String> markerKeys) throws DataImportException {
        this.markerKeys = markerKeys;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        genesReader.setResource(new FileSystemResource(markerKeys.get(FilenameKeys.MARKER_LIST)));
        genesReader.setComments(new String[] { "#" });
        genesReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<GenomicFeature> lineMapperGenes = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizerGenes  = new DelimitedLineTokenizer("\t");
        tokenizerGenes.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerGenes.setNames(genesColumnNames);
        lineMapperGenes.setLineTokenizer(tokenizerGenes);
        lineMapperGenes.setFieldSetMapper(new GenesFieldSetMapper());
        genesReader.setLineMapper(lineMapperGenes);

        xrefGenesReader.setResource(new FileSystemResource(markerKeys.get(FilenameKeys.XREFS_HGNC_homologene)));
        xrefGenesReader.setComments(new String[] {"#" });
        xrefGenesReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<List<Xref>> lineMapperGeneXrefs = new DefaultLineMapper<>();
        DelimitedLineTokenizer  tokenizerGeneXrefs  = new DelimitedLineTokenizer("\t");
        tokenizerGeneXrefs.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerGeneXrefs.setNames(xrefGeneColumnNames);
        lineMapperGeneXrefs.setLineTokenizer(tokenizerGeneXrefs);
        lineMapperGeneXrefs.setFieldSetMapper(new XrefFieldSetMapper());
        xrefGenesReader.setLineMapper(lineMapperGeneXrefs);

        xrefEntrezGenesReader.setResource(new FileSystemResource(markerKeys.get(FilenameKeys.XREFS_MGI_EntrezGene)));
        xrefEntrezGenesReader.setComments(new String[] {"#" });
        xrefEntrezGenesReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<List<Xref>> lineMapperEntrezGeneXrefs = new DefaultLineMapper<>();
        DelimitedLineTokenizer  tokenizerEntrezGeneXrefs  = new DelimitedLineTokenizer("\t");
        tokenizerEntrezGeneXrefs.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerEntrezGeneXrefs.setNames(xrefEnsemblColumnNames);
        lineMapperEntrezGeneXrefs.setLineTokenizer(tokenizerEntrezGeneXrefs);
        lineMapperEntrezGeneXrefs.setFieldSetMapper(new XrefFieldSetMapper());
        xrefEntrezGenesReader.setLineMapper(lineMapperEntrezGeneXrefs);

        xrefEnsemblReader.setResource(new FileSystemResource(markerKeys.get(FilenameKeys.XREFS_MRK_ENSEMBL)));
        xrefEnsemblReader.setComments(new String[] {"#" });
        xrefEnsemblReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<List<Xref>> lineMapperEnsemblXrefs = new DefaultLineMapper<>();
        DelimitedLineTokenizer  tokenizerEnsemblXrefs  = new DelimitedLineTokenizer("\t");
        tokenizerEnsemblXrefs.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerEnsemblXrefs.setNames(xrefEnsemblColumnNames);
        lineMapperEnsemblXrefs.setLineTokenizer(tokenizerEnsemblXrefs);
        lineMapperEnsemblXrefs.setFieldSetMapper(new XrefFieldSetMapper());
        xrefEnsemblReader.setLineMapper(lineMapperEnsemblXrefs);

        xrefVegaReader.setResource(new FileSystemResource(markerKeys.get(FilenameKeys.XREFS_MRK_VEGA)));
        xrefVegaReader.setComments(new String[] {"#" });
        xrefVegaReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<List<Xref>> lineMapperVegaXrefs = new DefaultLineMapper<>();
        DelimitedLineTokenizer  tokenizerVegaXrefs  = new DelimitedLineTokenizer("\t");
        tokenizerVegaXrefs.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerVegaXrefs.setNames(xrefVegaColumnNames);
        lineMapperVegaXrefs.setLineTokenizer(tokenizerVegaXrefs);
        lineMapperVegaXrefs.setFieldSetMapper(new XrefFieldSetMapper());
        xrefVegaReader.setLineMapper(lineMapperVegaXrefs);
    }


    // Step IMPLEMENTATION


    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "markerLoaderStep";
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

        // Don't add the writer here. The MarkerLoaderXrefsStepListener writes all of the data in the map when all reading and processing is done.
        Step loadGenesStep = stepBuilderFactory.get("markerLoaderGenesStep")
                .listener(new MarkerLoaderGenesStepListener())
                .chunk(200000)
                .reader(genesReader)
                .processor(markerProcessorGenes)
                .build();

        Step loadXrefGeneStep = stepBuilderFactory.get("markerLoaderXrefGenesStep")
                .listener(new MarkerLoaderXrefGeneStepListener())
                .chunk(200000)
                .reader(xrefGenesReader)
                .processor(markerProcessorXrefGenes)
                // Don't add the writer here. The listener writes all of the data in the map when all reading and processing is done.
                .build();

        Step loadXrefEnsemblStep = stepBuilderFactory.get("markerLoaderXrefEnsemblStep")
                .listener(new MarkerLoaderXrefEnsemblStepListener())
                .chunk(200000)
                .reader(xrefEnsemblReader)
                .processor(markerProcessorXrefEnsembl)
                // Don't add the writer here. The listener writes all of the data in the map when all reading and processing is done.
                .build();

        Step loadXrefEntrezGeneStep = stepBuilderFactory.get("markerLoaderXrefEntrezGeneStep")
                .listener(new MarkerLoaderXrefEntrezGeneStepListener())
                .chunk(200000)
                .reader(xrefEntrezGenesReader)
                .processor(markerProcessorXrefEntrezGene)
                // Don't add the writer here. The listener writes all of the data in the map when all reading and processing is done.
                .build();

        Step loadXrefVegaStep = stepBuilderFactory.get("markerLoaderXrefVegaStep")
                .listener(new MarkerLoaderXrefVegaStepListener())
                .chunk(200000)
                .reader(xrefVegaReader)
                .processor(markerProcessorXrefVega)
                // Don't add the writer here. The listener writes all of the data in the map when all reading and processing is done.
                .build();

        List<Flow> flows = new ArrayList<>();

        flows.add(new FlowBuilder<Flow>("markerLoaderGenesFlow")
                .from(loadGenesStep).end());
        flows.add(new FlowBuilder<Flow>("markerLoaderXrefGeneFlow")
                .from(loadXrefGeneStep).end());
        flows.add(new FlowBuilder<Flow>("markerLoaderXrefEnsemblFlow")
                .from(loadXrefEnsemblStep).end());
        flows.add(new FlowBuilder<Flow>("markerLoaderXrefEntrezGeneFlow")
                .from(loadXrefEntrezGeneStep).end());
        flows.add(new FlowBuilder<Flow>("markerLoaderXrefVegaFlow")
                .from(loadXrefVegaStep).end());

        FlowBuilder<Flow> flowBuilder = new FlowBuilder<Flow>("markerLoaderFlows").start(flows.get(0));
        for (int i = 1; i < flows.size(); i++) {
            flowBuilder.next(flows.get(i));
        }

        stepBuilderFactory.get("markerLoaderStep")
                .flow(flowBuilder.build())
                .build()
                .execute(stepExecution);
    }

    public class MarkerLoaderGenesStepListener extends LogStatusStepListener {

        @Override
        protected Set<String> logStatus() {
            logger.info("GENE: Added {} new genes to map from file {} in {}.",
                    ((MarkerProcessorGenes) markerProcessorGenes).getAddedGenesCount(),
                    markerKeys.get(FilenameKeys.MARKER_LIST),
                    commonUtils.formatDateDifference(start, stop));

            return ((MarkerProcessorGenes) markerProcessorGenes).getErrMessages();
        }
    }

    public class MarkerLoaderXrefGeneStepListener extends LogStatusStepListener {
        @Override
        protected Set<String> logStatus() {
            logger.info("XREF GENE: Added {} new gene xrefs to map from file {} in {}.",
                    ((MarkerProcessorXrefGenes) markerProcessorXrefGenes).getXrefsAdded(),
                    markerKeys.get(FilenameKeys.XREFS_HGNC_homologene),
                    commonUtils.formatDateDifference(start, stop));

            return ((MarkerProcessorXrefGenes) markerProcessorXrefGenes).getErrMessages();
        }
    }

    public class MarkerLoaderXrefEnsemblStepListener extends LogStatusStepListener {
        @Override
        protected Set<String> logStatus() {
            logger.info("XREF ENSEMBL: Added {} new ensembl xrefs to map from file {} in {}.",
                    ((MarkerProcessorXrefOthers) markerProcessorXrefEnsembl).getXrefsAdded(),
                    markerKeys.get(FilenameKeys.XREFS_MRK_ENSEMBL),
                    commonUtils.formatDateDifference(start, stop));

            return ((MarkerProcessorXrefOthers) markerProcessorXrefEnsembl).getErrMessages();
        }
    }

    public class MarkerLoaderXrefEntrezGeneStepListener extends LogStatusStepListener {
        @Override
        protected Set<String> logStatus() {
            logger.info("XREF ENTREZ_GENE: Added {} new entrezGene xrefs to map from file {} in {}.",
                    ((MarkerProcessorXrefOthers) markerProcessorXrefEntrezGene).getXrefsAdded(),
                    markerKeys.get(FilenameKeys.XREFS_MGI_EntrezGene),
                    commonUtils.formatDateDifference(start, stop));

            return ((MarkerProcessorXrefOthers) markerProcessorXrefEntrezGene).getErrMessages();
        }
    }

    public class MarkerLoaderXrefVegaStepListener extends LogStatusStepListener {
        @Override
        protected Set<String> logStatus() {
            logger.info("XREF VEGA: Added {} new vega xrefs to map from file {} in {}.",
                    ((MarkerProcessorXrefOthers) markerProcessorXrefVega).getXrefsAdded(),
                    markerKeys.get(FilenameKeys.XREFS_MRK_VEGA),
                    commonUtils.formatDateDifference(start, stop));

            // Write the genes to the database.
            start = new Date();
            logger.info("Writing genes, synonyms, and xrefs to database");

            List<GenomicFeature> genes = new ArrayList(((MarkerProcessorXrefOthers) markerProcessorXrefVega).getGenes().values());
            try {
                writer.write(genes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            stop = new Date();
            logger.info("  Wrote {} genes and {} synonyms from file {} and {} xrefs to database in {}",
                      writer.getWrittenGenes()
                    , writer.getWrittenSynonyms()
                    , markerKeys.get(FilenameKeys.MARKER_LIST)
                    , writer.getWrittenXrefs()
                    , commonUtils.formatDateDifference(start, stop));
            logger.info("");

            return ((MarkerProcessorXrefOthers) markerProcessorXrefVega).getErrMessages();
        }
    }
}