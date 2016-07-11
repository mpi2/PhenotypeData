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

import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.Xref;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.*;
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
//    private FlatFileItemReader<GenomicFeature> geneCoordinatesReader = new FlatFileItemReader<>();
    private FlatFileItemReader<List<Xref>>     xrefsReader           = new FlatFileItemReader<>();

    public enum FilenameKeys {
          MARKER_LIST
        , MARKER_COORDINATES
        , XREFS
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

//    // Fields within MGI_GTGUP.gff:
//    private final String[] geneCoordinatesColumnNames = new String[] {
//            "chromosome"             // A - Chromosome
//          , "unused_2"               // B - Source of Feature (not used)
//          , "biotype"                // C - Marker Type
//          , "start"                  // D - Start Coordinate
//          , "end"                    // E - End Coordinate
//          , "unused_6"               // F - Empty Column
//          , "strand"                 // G - Strand
//          , "unused_8"               // H - Empty Column
//          , "composite"              // I - "ID=mgiMarkerAccessionId;Name=symbol;Note=marker feature" (i.e. Feature Type or subtype)
//    };

    // Fields within MGI_Gene.rpt:
    private final String[] xrefColumnNames = new String[] {
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

    @Autowired
    private ItemProcessor markerProcessorGenes;

//    @Autowired
//    private ItemProcessor markerProcessorGeneCoordinates;

    @Autowired
    private ItemProcessor markerProcessorXrefs;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MarkerWriter writer;


    public MarkerLoader(Map<FilenameKeys, String> markerKeys) throws CdaLoaderException {
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

//        geneCoordinatesReader.setResource(new FileSystemResource(markerKeys.get(FilenameKeys.MARKER_COORDINATES)));
//        geneCoordinatesReader.setComments(new String[] { "#" });
//        geneCoordinatesReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
//        DefaultLineMapper<GenomicFeature> lineMapperGeneCoordinates = new DefaultLineMapper<>();
//        DelimitedLineTokenizer tokenizerGeneCoordinates  = new DelimitedLineTokenizer("\t");
//        tokenizerGeneCoordinates.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
//        tokenizerGeneCoordinates.setNames(geneCoordinatesColumnNames);
//        lineMapperGeneCoordinates.setLineTokenizer(tokenizerGeneCoordinates);
//        lineMapperGeneCoordinates.setFieldSetMapper(new GeneCoordinatesFieldSetMapper());
//        geneCoordinatesReader.setLineMapper(lineMapperGeneCoordinates);

        xrefsReader.setResource(new FileSystemResource(markerKeys.get(FilenameKeys.XREFS)));
        xrefsReader.setComments(new String[] { "#" });
        xrefsReader.setRecordSeparatorPolicy(new BlankLineRecordSeparatorPolicy());
        DefaultLineMapper<List<Xref>> lineMapperXrefs = new DefaultLineMapper<>();
        DelimitedLineTokenizer  tokenizerXrefs  = new DelimitedLineTokenizer("\t");
        tokenizerXrefs.setStrict(false);     // Relax token count. Some lines have more tokens; others, less, causing a parsing exception.
        tokenizerXrefs.setNames(xrefColumnNames);
        lineMapperXrefs.setLineTokenizer(tokenizerXrefs);
        lineMapperXrefs.setFieldSetMapper(new XrefFieldSetMapper());
        xrefsReader.setLineMapper(lineMapperXrefs);
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
                .chunk(1000)
                .reader(genesReader)
                .processor(markerProcessorGenes)
                .build();

//        Step loadGeneCoordinatesStep = stepBuilderFactory.get("markerLoaderGeneCoordinatesStep")
//                .listener(new MarkerLoaderGeneCoordinatesStepListener())
//                .chunk(1000)
//                .reader(geneCoordinatesReader)
//                .processor(markerProcessorGeneCoordinates)
//                // Don't add a writer here, as this processing simply populates the featureTypes, genomicFeatures, and sequenceRegions maps.
//                .build();

        Step loadXrefsStep = stepBuilderFactory.get("markerLoaderXrefsStep")
                .listener(new MarkerLoaderXrefsStepListener())
                .chunk(1000)
                .reader(xrefsReader)
                .processor(markerProcessorXrefs)
                // Don't add the writer here. The listener writes all of the data in the map when all reading and processing is done.
                .build();

        List<Flow> flows = new ArrayList<>();

        // GeneCoordinates (MGI_GTGUP.gff) doesn't have the gene name, which is required for INSERT.
//        flows.add(new FlowBuilder<Flow>("markerLoaderGeneCoordinatesFlow")
//                .from(loadGeneCoordinatesStep).end());
        flows.add(new FlowBuilder<Flow>("markerLoaderGenesFlow")
                .from(loadGenesStep).end());
        flows.add(new FlowBuilder<Flow>("markerLoaderXrefsFlow")
                .from(loadXrefsStep).end());

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
            logger.info("GENES: Added {} new genes to map from file {} in {}.",
                    ((MarkerProcessorGenes) markerProcessorGenes).getAddedGenesCount(),
                    markerKeys.get(FilenameKeys.MARKER_LIST),
                    commonUtils.formatDateDifference(start, stop));

            return ((MarkerProcessorGenes) markerProcessorGenes).getErrMessages();
        }
    }

//    public class MarkerLoaderGeneCoordinatesStepListener extends LogStatusStepListener {
//
//        @Override
//        protected Set<String> logStatus() {
//            logger.info("");
//            logger.info("GENE COORDINATES: Added {} new Marker gene types to map from file {} in {}",
//                    ((MarkerProcessorGeneCoordinates) markerProcessorGeneCoordinates).getAddedCoordinatesCount(),
//                    markerKeys.get(FilenameKeys.MARKER_COORDINATES),
//                    commonUtils.formatDateDifference(start, stop));
//
//            return ((MarkerProcessorGeneCoordinates) markerProcessorGeneCoordinates).getErrMessages();
//        }
//    }

    public class MarkerLoaderXrefsStepListener extends LogStatusStepListener {
        @Override
        protected Set<String> logStatus() {

            // Write the genes to the database.
            start = new Date();
            logger.info("Writing genes, synonyms, and xrefs to database");

            List<GenomicFeature> genes = new ArrayList(((MarkerProcessorXrefs) markerProcessorXrefs).getGenes().values());
            try {
                writer.write(genes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            stop = new Date();
            logger.info("  Wrote {} genes and {} synonyms from file {} and {} xrefs from file {} to database in {}",
                      writer.getWrittenGenes()
                    , writer.getWrittenSynonyms()
                    , markerKeys.get(FilenameKeys.MARKER_LIST)
                    , writer.getWrittenXrefs()
                    , markerKeys.get(FilenameKeys.XREFS)
                    , commonUtils.formatDateDifference(start, stop));
            logger.info("");

            return ((MarkerProcessorXrefs) markerProcessorXrefs).getErrMessages();
        }
    }
}