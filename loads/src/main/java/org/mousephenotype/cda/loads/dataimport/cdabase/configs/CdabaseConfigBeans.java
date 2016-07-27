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

package org.mousephenotype.cda.loads.dataimport.cdabase.configs;

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.dataimport.cdabase.steps.*;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.CdabaseLoaderUtils;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 03/05/16.
 */
@Configuration
public class CdabaseConfigBeans {

    private Map<String, OntologyTerm>   mgiFeatureTypes;

    private Map<String, Allele>         alleles = new HashMap<>();    // key = allele accession id
    private Map<String, GenomicFeature> genes   = new HashMap<>();    // key = marker accession id
    private Map<String, Strain>         strains = new HashMap<>();    // key = strain accession id

    @NotNull
    @Value("${cdabase.workspace}")
    protected String cdabaseWorkspace;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    public class DownloadFilename {
        public final DownloadFileEnum downloadFileEnum;
        public final String sourceUrl;
        public final String targetFilename;
        public final int dbId;

        public DownloadFilename(DownloadFileEnum downloadFileEnum, String sourceUrl, String targetFilename, int dbId) {
            this.downloadFileEnum = downloadFileEnum;
            this.sourceUrl = sourceUrl;
            this.targetFilename = targetFilename;
            this.dbId = dbId;
        }
    }

    public class DownloadOntologyFilename extends DownloadFilename {
        public final String prefix;

        public DownloadOntologyFilename(DownloadFileEnum downloadFileEnum, String sourceUrl, String targetFilename, int dbId, String prefix) {
            super(downloadFileEnum, sourceUrl, targetFilename, dbId);
            this.prefix = prefix;
        }
    }


    public DownloadFilename[] filenames;
    private enum DownloadFileEnum {
          IMSR_report
        , EBI_PhenotypedColony
        , ES_CellLine
        , EUCOMM_Allele
        , HMD_HumanPhenotype
        , KOMP_Allele
        , MGI_EntrezGene
        , MGI_Gene
        , MGI_Gene_Model_Coord
        , MGI_GenePheno
        , MGI_PhenoGenoMP
        , MGI_PhenotypicAllele
        , MGI_QTLAllele
        , MGI_Strain
        , MRK_ENSEMBL
        , MRK_List1
        , MRK_Reference
        , MRK_Sequence
        , MRK_SwissProt
        , MRK_VEGA
        , NorCOMM_Allele
        , eco
        , efo
        , emap
        , emapa
        , ma
        , MmusDv
        , mp
        , mpath
        , pato

    }


    @PostConstruct
    public void initialise() {

        filenames = new DownloadFilename[] {
          // imsr
              new DownloadFilename(DownloadFileEnum.IMSR_report, "http://www.findmice.org/report.txt?query=&states=Any&_states=1&types=Any&_types=1&repositories=Any&_repositories=1&_mutations=on&results=500000&startIndex=0&sort=score&dir=", cdabaseWorkspace + "/IMSR_report.txt", DbIdType.IMSR.intValue())

          // mgi reports
            , new DownloadFilename(DownloadFileEnum.ES_CellLine, "ftp://ftp.informatics.jax.org/pub/reports/ES_CellLine.rpt", cdabaseWorkspace + "/ES_CellLine.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.EUCOMM_Allele, "ftp://ftp.informatics.jax.org/pub/reports/EUCOMM_Allele.rpt", cdabaseWorkspace + "/EUCOMM_Allele.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.HMD_HumanPhenotype, "ftp://ftp.informatics.jax.org/pub/reports/HMD_HumanPhenotype.rpt", cdabaseWorkspace + "/HMD_HumanPhenotype.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.KOMP_Allele, "ftp://ftp.informatics.jax.org/pub/reports/KOMP_Allele.rpt", cdabaseWorkspace + "/KOMP_Allele.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MGI_EntrezGene, "ftp://ftp.informatics.jax.org/pub/reports/MGI_EntrezGene.rpt", cdabaseWorkspace + "/MGI_EntrezGene.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MGI_Gene, "ftp://ftp.informatics.jax.org/pub/reports/MGI_Gene.rpt", cdabaseWorkspace + "/MGI_Gene.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MGI_Gene_Model_Coord, "ftp://ftp.informatics.jax.org/pub/reports/MGI_Gene_Model_Coord.rpt", cdabaseWorkspace + "/MGI_Gene_Model_Coord.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MGI_GenePheno, "ftp://ftp.informatics.jax.org/pub/reports/MGI_GenePheno.rpt", cdabaseWorkspace + "/MGI_GenePheno.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MGI_PhenoGenoMP, "ftp://ftp.informatics.jax.org/pub/reports/MGI_PhenoGenoMP.rpt", cdabaseWorkspace + "/MGI_PhenoGenoMP.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MGI_PhenotypicAllele, "ftp://ftp.informatics.jax.org/pub/reports/MGI_PhenotypicAllele.rpt", cdabaseWorkspace + "/MGI_PhenotypicAllele.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MGI_QTLAllele, "ftp://ftp.informatics.jax.org/pub/reports/MGI_QTLAllele.rpt", cdabaseWorkspace + "/MGI_QTLAllele.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MGI_Strain, "ftp://ftp.informatics.jax.org/pub/reports/MGI_Strain.rpt", cdabaseWorkspace + "/MGI_Strain.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MRK_ENSEMBL, "ftp://ftp.informatics.jax.org/pub/reports/MRK_ENSEMBL.rpt", cdabaseWorkspace + "/MRK_ENSEMBL.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MRK_List1, "ftp://ftp.informatics.jax.org/pub/reports/MRK_List1.rpt", cdabaseWorkspace + "/MRK_List1.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MRK_Reference, "ftp://ftp.informatics.jax.org/pub/reports/MRK_Reference.rpt", cdabaseWorkspace + "/MRK_Reference.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MRK_Sequence, "ftp://ftp.informatics.jax.org/pub/reports/MRK_Sequence.rpt", cdabaseWorkspace + "/MRK_Sequence.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MRK_SwissProt, "ftp://ftp.informatics.jax.org/pub/reports/MRK_SwissProt.rpt", cdabaseWorkspace + "/MRK_SwissProt.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.MRK_VEGA, "ftp://ftp.informatics.jax.org/pub/reports/MRK_VEGA.rpt", cdabaseWorkspace + "/MRK_VEGA.rpt", DbIdType.MGI.intValue())
            , new DownloadFilename(DownloadFileEnum.NorCOMM_Allele, "ftp://ftp.informatics.jax.org/pub/reports/NorCOMM_Allele.rpt", cdabaseWorkspace + "/NorCOMM_Allele.rpt", DbIdType.MGI.intValue())

          // iMits phenotyped colony report
            , new DownloadFilename(DownloadFileEnum.EBI_PhenotypedColony, "https://www.mousephenotype.org/imits/v2/reports/mp2_load_phenotyping_colonies_report.tsv", cdabaseWorkspace + "/EBI_PhenotypedColonies.tsv", DbIdType.IMPC.intValue())

            // OWL ontologies
            , new DownloadOntologyFilename(DownloadFileEnum.eco, "https://raw.githubusercontent.com/evidenceontology/evidenceontology/master/eco.owl", cdabaseWorkspace + "/eco.owl", DbIdType.ECO.intValue(), DbIdType.ECO.getName())
            , new DownloadOntologyFilename(DownloadFileEnum.efo, "http://www.ebi.ac.uk/efo/efo.owl", cdabaseWorkspace + "/efo.owl", DbIdType.EFO.intValue(), DbIdType.EFO.getName())
            , new DownloadOntologyFilename(DownloadFileEnum.emap, "http://purl.obolibrary.org/obo/emap.owl", cdabaseWorkspace + "/emap.owl", DbIdType.EMAP.intValue(), DbIdType.EMAP.getName())
            , new DownloadOntologyFilename(DownloadFileEnum.emapa, "http://www.berkeleybop.org/ontologies/emapa.owl", cdabaseWorkspace + "/emapa.owl", DbIdType.EMAPA.intValue(), DbIdType.EMAPA.getName())
            , new DownloadOntologyFilename(DownloadFileEnum.ma, "http://purl.obolibrary.org/obo/ma.owl", cdabaseWorkspace + "/ma.owl", DbIdType.MA.intValue(), DbIdType.MA.getName())
            , new DownloadOntologyFilename(DownloadFileEnum.MmusDv, "http://www.berkeleybop.org/ontologies/mmusdv.owl", cdabaseWorkspace + "/MmusDv.owl", DbIdType.MmusDv.intValue(), DbIdType.MmusDv.getName())
            , new DownloadOntologyFilename(DownloadFileEnum.mp, "ftp://ftp.informatics.jax.org/pub/reports/mp.owl", cdabaseWorkspace + "/mp.owl", DbIdType.MP.intValue(), DbIdType.MP.getName())
            , new DownloadOntologyFilename(DownloadFileEnum.mpath, "http://purl.obolibrary.org/obo/mpath.owl", cdabaseWorkspace + "/mpath.owl", DbIdType.MPATH.intValue(), DbIdType.MPATH.getName())
            , new DownloadOntologyFilename(DownloadFileEnum.pato, "https://raw.githubusercontent.com/pato-ontology/pato/master/pato.owl", cdabaseWorkspace + "/pato.owl", DbIdType.PATO.intValue(), DbIdType.PATO.getName())
        };

        for (DownloadFilename downloadFilename : filenames) {
            downloadFilenameMap.put(downloadFilename.downloadFileEnum, downloadFilename);
        }
    }

    Map<DownloadFileEnum, DownloadFilename> downloadFilenameMap = new HashMap<>();


    @Bean(name = "databaseInitialiser")
    public DatabaseInitialiser databaseInitialiser() {
        return new DatabaseInitialiser();
    }

    @Bean(name = "downloader")
    public List<Downloader> downloader() {
        List<Downloader> downloaderList = new ArrayList<>();

        for (DownloadFilename download : filenames) {
            downloaderList.add(new Downloader(download.sourceUrl, download.targetFilename));
        }

        return downloaderList;
    }

    @Bean(name = "cdabaseLoaderUtils")
    public CdabaseLoaderUtils cdabaseLoaderUtils() {
        return new CdabaseLoaderUtils();
    }


    // LOADERS, PROCESSORS, AND WRITERS


    @Bean(name = "alleleLoader")
    public AlleleLoader alleleLoader() throws DataImportException {
        Map<AlleleLoader.FilenameKeys, String> filenameKeys = new HashMap<>();
        filenameKeys.put(AlleleLoader.FilenameKeys.EUCOMM, downloadFilenameMap.get(DownloadFileEnum.EUCOMM_Allele).targetFilename);
        filenameKeys.put(AlleleLoader.FilenameKeys.GENOPHENO, downloadFilenameMap.get(DownloadFileEnum.MGI_GenePheno).targetFilename);
        filenameKeys.put(AlleleLoader.FilenameKeys.KOMP, downloadFilenameMap.get(DownloadFileEnum.KOMP_Allele).targetFilename);
        filenameKeys.put(AlleleLoader.FilenameKeys.NORCOMM, downloadFilenameMap.get(DownloadFileEnum.NorCOMM_Allele).targetFilename);
        filenameKeys.put(AlleleLoader.FilenameKeys.PHENOTYPIC, downloadFilenameMap.get(DownloadFileEnum.MGI_PhenotypicAllele).targetFilename);
        filenameKeys.put(AlleleLoader.FilenameKeys.QTL, downloadFilenameMap.get(DownloadFileEnum.MGI_QTLAllele).targetFilename);

        return new AlleleLoader(filenameKeys);
    }

    @Bean(name = "alleleProcessorPhenotypic")
    public AlleleProcessorPhenotypic alleleProcessorPhenotypic() {
        return new AlleleProcessorPhenotypic(genes);
    }

    @Bean(name = "alleleProcessorEucomm")
    public AlleleProcessorEucomm alleleProcessorEucomm() {
        return new AlleleProcessorEucomm(genes);
    }

    @Bean(name = "alleleProcessorKomp")
    public AlleleProcessorKomp alleleProcessorKomp() {
        return new AlleleProcessorKomp(genes);
    }

    @Bean(name = "alleleProcessorNorcomm")
    public AlleleProcessorNorcomm alleleProcessorNorcomm() {
        return new AlleleProcessorNorcomm(genes);
    }

    @Bean(name = "alleleProcessorGenopheno")
    public AlleleProcessorGenopheno alleleProcessorGenopheno() {
        return new AlleleProcessorGenopheno(genes);
    }

    @Bean(name = "alleleProcessorQtl")
    public AlleleProcessorQtl alleleProcessorQtl() {
        return new AlleleProcessorQtl(genes);
    }

    @Bean(name = "alleleWriter")
    public AlleleWriter alleleWriter() {
        return new AlleleWriter();
    }



    @Bean(name = "bioModelLoader")
    public BiologicalModelLoader bioModelLoader() throws DataImportException {
        Map<BiologicalModelLoader.FilenameKeys, String> filenameKeys = new HashMap<>();
        filenameKeys.put(BiologicalModelLoader.FilenameKeys.MGI_PhenoGenoMP, downloadFilenameMap.get(DownloadFileEnum.MGI_PhenoGenoMP).targetFilename);

        return new BiologicalModelLoader(filenameKeys);
    }

    @Bean(name = "bioModelProcessor")
    public BiologicalModelProcessor bioModelProcessor() {
        return new BiologicalModelProcessor(alleles, genes);
    }

    @Bean(name = "bioModelWriter")
    public BiologicalModelWriter bioModelWriter() {
        return new BiologicalModelWriter();
    }



    @Bean(name = "markerLoader")
    public MarkerLoader markerLoader() throws DataImportException {
        Map<MarkerLoader.FilenameKeys, String> filenameKeys = new HashMap<>();
        filenameKeys.put(MarkerLoader.FilenameKeys.MARKER_LIST, downloadFilenameMap.get(DownloadFileEnum.MRK_List1).targetFilename);
        filenameKeys.put(MarkerLoader.FilenameKeys.XREFS_MGI_EntrezGene, downloadFilenameMap.get(DownloadFileEnum.MGI_EntrezGene).targetFilename);
        filenameKeys.put(MarkerLoader.FilenameKeys.XREFS_MGI_Gene, downloadFilenameMap.get(DownloadFileEnum.MGI_Gene).targetFilename);
        filenameKeys.put(MarkerLoader.FilenameKeys.XREFS_MRK_ENSEMBL, downloadFilenameMap.get(DownloadFileEnum.MRK_ENSEMBL).targetFilename);
        filenameKeys.put(MarkerLoader.FilenameKeys.XREFS_MRK_VEGA, downloadFilenameMap.get(DownloadFileEnum.MRK_VEGA).targetFilename);

        return new MarkerLoader(filenameKeys);
    }

    @Bean(name = "markerProcessorGenes")
    public MarkerProcessorGenes markerProcessorGenes() {
        return new MarkerProcessorGenes(genes);
    }

    @Bean(name = "markerProcessorXrefGenes")
    public MarkerProcessorXrefGenes markerProcessorXrefGenes() {
        return new MarkerProcessorXrefGenes(genes);
    }

    @Bean(name = "markerProcessorXrefEntrezGene")
    public MarkerProcessorXrefOthers markerProcessorXrefEntrezGene() {
        return new MarkerProcessorXrefOthers(genes);
    }

    @Bean(name = "markerProcessorXrefEnsembl")
    public MarkerProcessorXrefOthers markerProcessorXrefEnsembl() {
        return new MarkerProcessorXrefOthers(genes);
    }

    @Bean(name = "markerProcessorXrefVega")
    public MarkerProcessorXrefOthers markerProcessorXrefVega() {
        return new MarkerProcessorXrefOthers(genes);
    }

    @Bean(name = "markerWriter")
    public MarkerWriter markerWriter() {
        return new MarkerWriter();
    }


    
    @Bean(name = "ontologyLoaderList")
    public List<OntologyLoader> ontologyLoader() throws DataImportException {
        List<OntologyLoader> ontologyloaderList = new ArrayList<>();

        for (DownloadFilename filename : filenames) {
            if (filename instanceof DownloadOntologyFilename) {
                DownloadOntologyFilename downloadOntology = (DownloadOntologyFilename) filename;
                ontologyloaderList.add(new OntologyLoader(downloadOntology.targetFilename, downloadOntology.dbId, downloadOntology.prefix, stepBuilderFactory, ontologyWriter()));
            }
        }

        return ontologyloaderList;
    }

    @Bean(name = "ontologyWriter")
    public OntologyWriter ontologyWriter() {
        return new OntologyWriter();
    }



    @Bean(name = "phenotypedcolonyLoader")
    public PhenotypedColonyLoader phenotypedcolonyLoader() throws DataImportException {
        Map<PhenotypedColonyLoader.FilenameKeys, String> filenameKeys = new HashMap<>();
        filenameKeys.put(PhenotypedColonyLoader.FilenameKeys.EBI_PhenotypedColony, downloadFilenameMap.get(DownloadFileEnum.EBI_PhenotypedColony).targetFilename);

        return new PhenotypedColonyLoader(filenameKeys);
    }

    @Bean(name = "phenotypedColonyProcessor")
    public PhenotypedColonyProcessor phenotypedColonyProcessor() throws DataImportException {
        return new PhenotypedColonyProcessor(alleles, genes, strains);
    }

    @Bean(name = "phenotypedColonyWriter")
    public PhenotypedColonyWriter phenotypedColonyWriter() {
        return new PhenotypedColonyWriter();
    }


    
    @Bean(name = "strainLoader")
    public StrainLoader strainLoader() throws DataImportException {
        Map<StrainLoader.FilenameKeys, String> filenameKeys = new HashMap<>();
        filenameKeys.put(StrainLoader.FilenameKeys.MGI, downloadFilenameMap.get(DownloadFileEnum.MGI_Strain).targetFilename);
        filenameKeys.put(StrainLoader.FilenameKeys.IMSR, downloadFilenameMap.get(DownloadFileEnum.IMSR_report).targetFilename);

        return new StrainLoader(filenameKeys);
    }

    @Bean(name = "strainProcessorMgi")
    public StrainProcessorMgi strainProcessorMgi() {
        return new StrainProcessorMgi(strains, alleles);
    }

    @Bean(name = "strainProcessorImsr")
    public StrainProcessorImsr strainProcessorImsr() {
        return new StrainProcessorImsr(alleles, strains);
    }

    @Bean(name = "strainWriter")
    public StrainWriter strainWriter() {
       return new StrainWriter();
    }


    // NOTE: Using @Lazy here and in the @Autowire to postpone creation of this bean (so that @PostConstruct can be used)
    //       doesn't delay invocation of the @PostConstruct as we would like, so we shant use it.
    /**
     * ******** DO NOT DELETE. JUST UNCOMMENT IF YOU NEED THE SPRING BATCH TABLES REBUILT ********
     * Using this home-grown jobRepository correctly recreates the spring batch tables. Without it, you have to
     * recreate the tables manually after dropping them.
     */
//    @Bean(name = "jobRepository")
//    public JobRepository jobRepository() throws Exception {
//        MapJobRepositoryFactoryBean b = new MapJobRepositoryFactoryBean();
//
//        JobRepository jobRepository = b.getObject();
//
//        return jobRepository;
//    }
}