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

package org.mousephenotype.cda.loads.cdaloader.configs;

import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.DbItemWriter;
import org.mousephenotype.cda.loads.cdaloader.steps.DatabaseInitialiser;
import org.mousephenotype.cda.loads.cdaloader.steps.Downloader;
import org.mousephenotype.cda.loads.cdaloader.steps.OntologyLoader;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrelac on 03/05/16.
 */
@Configuration
public class ConfigBeans {

    @NotNull
    @Value("${cdaload.workspace}")
    protected String cdaWorkspace;

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;


    public class DownloadFilename {
        public final String sourceUrl;
        public final String targetFilename;
        public final int dbId;

        public DownloadFilename(String sourceUrl, String targetFilename, int dbId) {
            this.sourceUrl = sourceUrl;
            this.targetFilename = targetFilename;
            this.dbId = dbId;
        }
    }

    public class DownloadOntologyFilename extends DownloadFilename {
        public final String prefix;

        public DownloadOntologyFilename(String sourceUrl, String targetFilename, int dbId, String prefix) {
            super(sourceUrl, targetFilename, dbId);
            this.prefix = prefix;
        }
    }


    public DownloadFilename[] downloadFilenames;


    @PostConstruct
    public void initialise() {

        downloadFilenames = new DownloadFilename[] {
              // imsr
//              new DownloadFilename("http://www.findmice.org/report.txt?query=&states=Any&_states=1&types=Any&_types=1&repositories=Any&_repositories=1&_mutations=on&results=500000&startIndex=0&sort=score&dir=", cdaWorkspace + "/report.txt", DbIdType.IMSR.intValue())

              // mgi reports
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/ES_CellLine.rpt", cdaWorkspace + "/ES_CellLine.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/EUCOMM_Allele.rpt", cdaWorkspace + "/EUCOMM_Allele.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/HMD_HumanPhenotype.rpt", cdaWorkspace + "/HMD_HumanPhenotype.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MGI_EntrezGene.rpt", cdaWorkspace + "/MGI_EntrezGene.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MGI_Gene_Model_Coord.rpt", cdaWorkspace + "/MGI_Gene_Model_Coord.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MGI_GenePheno.rpt", cdaWorkspace + "/MGI_GenePheno.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MGI_GTGUP.gff", cdaWorkspace + "/MGI_GTGUP.gff", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MGI_PhenoGenoMP.rpt", cdaWorkspace + "/MGI_PhenoGenoMP.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MGI_PhenotypicAllele.rpt", cdaWorkspace + "/MGI_PhenotypicAllele.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MGI_QTLAllele.rpt", cdaWorkspace + "/MGI_QTLAllele.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MGI_Strain.rpt", cdaWorkspace + "/MGI_Strain.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MRK_ENSEMBL.rpt", cdaWorkspace + "/MRK_ENSEMBL.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MRK_List1.rpt", cdaWorkspace + "/MRK_List1.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MRK_List2.rpt", cdaWorkspace + "/MRK_List2.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MRK_Reference.rpt", cdaWorkspace + "/MRK_Reference.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MRK_Sequence.rpt", cdaWorkspace + "/MRK_Sequence.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MRK_SwissProt.rpt", cdaWorkspace + "/MRK_SwissProt.rpt", DbIdType.MGI.intValue())
//            , new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/MRK_VEGA.rpt", cdaWorkspace + "/MRK_VEGA.rpt", DbIdType.MGI.intValue())
              new DownloadFilename("ftp://ftp.informatics.jax.org/pub/reports/NorCOMM_Allele.rpt", cdaWorkspace + "/NorCOMM_Allele.rpt", DbIdType.MGI.intValue())

            // OWL ontologies
            , new DownloadOntologyFilename("https://raw.githubusercontent.com/evidenceontology/evidenceontology/master/eco.owl", cdaWorkspace + "/eco.owl", DbIdType.ECO.intValue(), DbIdType.ECO.getName())
            , new DownloadOntologyFilename("http://www.ebi.ac.uk/efo/efo.owl", cdaWorkspace + "/efo.owl", DbIdType.EFO.intValue(), DbIdType.EFO.getName())
            , new DownloadOntologyFilename("http://purl.obolibrary.org/obo/emap.owl", cdaWorkspace + "/emap.owl", DbIdType.EMAP.intValue(), DbIdType.EMAP.getName())
            , new DownloadOntologyFilename("http://www.berkeleybop.org/ontologies/emapa.owl", cdaWorkspace + "/emapa.owl", DbIdType.EMAPA.intValue(), DbIdType.EMAPA.getName())
            , new DownloadOntologyFilename("http://purl.obolibrary.org/obo/ma.owl", cdaWorkspace + "/ma.owl", DbIdType.MA.intValue(), DbIdType.MA.getName())
            , new DownloadOntologyFilename("ftp://ftp.informatics.jax.org/pub/reports/mp.owl", cdaWorkspace + "/mp.owl", DbIdType.MP.intValue(), DbIdType.MP.getName())
            , new DownloadOntologyFilename("http://purl.obolibrary.org/obo/mpath.owl", cdaWorkspace + "/mpath.owl", DbIdType.MPATH.intValue(), DbIdType.MPATH.getName())
            , new DownloadOntologyFilename("https://raw.githubusercontent.com/pato-ontology/pato/master/pato.owl", cdaWorkspace + "/pato.owl", DbIdType.PATO.intValue(), DbIdType.ECO.getName())
        };
    }


    @Bean(name = "databaseInitialiser")
    public DatabaseInitialiser databaseInitialiser() {
        return new DatabaseInitialiser();
    }

    @Bean(name = "downloaders")
    public List<Downloader> downloaders() {
        List<Downloader> downloaderList = new ArrayList<>();

        for (DownloadFilename download : downloadFilenames) {
            downloaderList.add(new Downloader(download.sourceUrl, download.targetFilename));
        }

        return downloaderList;
    }

    @Bean(name = "ontologyLoaders")
    public List<OntologyLoader> ontologyLoaders() throws CdaLoaderException {
        List<OntologyLoader> ontologyloaderList = new ArrayList<>();

        for (DownloadFilename download : downloadFilenames) {
            if (download instanceof DownloadOntologyFilename) {
                DownloadOntologyFilename downloadOntology = (DownloadOntologyFilename)download;
                ontologyloaderList.add(new OntologyLoader(downloadOntology.targetFilename, downloadOntology.dbId, downloadOntology.prefix));
            }
        }

        return ontologyloaderList;
    }

    @Bean(name = "dbItemWriter")
    @StepScope
    public DbItemWriter dbItemWriter() {
        DbItemWriter writer = new DbItemWriter();

        return writer;
    }
}