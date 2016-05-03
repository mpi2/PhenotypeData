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

package org.mousephenotype.cda.loads.cdaloader.step;

import org.mousephenotype.cda.loads.cdaloader.exception.CdaLoaderException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mrelac on 13/04/2016.
 *
 */
 @Configuration
 @ComponentScan("org.mousephenotype.cda.loads.cdaloader")
 @PropertySource(value="file:${user.home}/configfiles/${profile}/application.properties")
 @PropertySource(value="file:${user.home}/configfiles/${profile}/cdaload.properties",
                 ignoreResourceNotFound=true)
public class DownloadReportsStep {

    @Value("${cdaload.workspace}")
    private String cdaWorkspace;

    CommonUtils commonUtils = new CommonUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public final long TASKLET_TIMEOUT = 10000;                                  // Timeout in milliseconds

    @Bean(name = "downloadReports")
    @StepScope
    public SystemCommandTasklet downloadReports() throws CdaLoaderException {
        String command;
        SystemCommandTasklet downloadReportsTasklet;

        final String[][] DOWNLOAD_FILES = new String[][] {
                  // imsr
//                  { "http://www.findmice.org/report.txt?query=&states=Any&_states=1&types=Any&_types=1&repositories=Any&_repositories=1&_mutations=on&results=500000&startIndex=0&sort=score&dir=", cdaWorkspace + "/report.txt" }
//
//                  // mgi reports
//                , { "ftp://ftp.informatics.jax.org/pub/reports/ES_CellLine.rpt", cdaWorkspace + "/ES_CellLine.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/EUCOMM_Allele.rpt", cdaWorkspace + "/EUCOMM_Allele.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/HMD_HumanPhenotype.rpt", cdaWorkspace + "/HMD_HumanPhenotype.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MGI_EntrezGene.rpt", cdaWorkspace + "/MGI_EntrezGene.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MGI_Gene_Model_Coord.rpt", cdaWorkspace + "/MGI_Gene_Model_Coord.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MGI_GenePheno.rpt", cdaWorkspace + "/MGI_GenePheno.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MGI_GTGUP.gff", cdaWorkspace + "/MGI_GTGUP.gff" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MGI_PhenoGenoMP.rpt", cdaWorkspace + "/MGI_PhenoGenoMP.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MGI_PhenotypicAllele.rpt", cdaWorkspace + "/MGI_PhenotypicAllele.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MGI_QTLAllele.rpt", cdaWorkspace + "/MGI_QTLAllele.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MGI_Strain.rpt", cdaWorkspace + "/MGI_Strain.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MRK_ENSEMBL.rpt", cdaWorkspace + "/MRK_ENSEMBL.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MRK_List1.rpt", cdaWorkspace + "/MRK_List1.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MRK_List2.rpt", cdaWorkspace + "/MRK_List2.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MRK_Reference.rpt", cdaWorkspace + "/MRK_Reference.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MRK_Sequence.rpt", cdaWorkspace + "/MRK_Sequence.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MRK_SwissProt.rpt", cdaWorkspace + "/MRK_SwissProt.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MRK_VEGA.rpt", cdaWorkspace + "/MRK_VEGA.rpt" }
//                , { "ftp://ftp.informatics.jax.org/pub/reports/NorCOMM_Allele.rpt", cdaWorkspace + "/NorCOMM_Allele.rpt" }
//
                // OWL ontologies
                  { "ftp://ftp.informatics.jax.org/pub/reports/mp.owl", cdaWorkspace + "/mp.owl" }                                              // mammalian_phenotype.obo
                , { "https://raw.githubusercontent.com/pato-ontology/pato/master/pato.owl", cdaWorkspace + "/pato.owl" }                        // quality.obo
                , { "http://purl.obolibrary.org/obo/ma.owl", cdaWorkspace + "/ma.owl" }                                                         // adult_mouse_anatomy.obo
                , { "http://purl.obolibrary.org/obo/chebi.owl", cdaWorkspace + "/chebi.owl" }                                                   // chebi.obo
                , { "http://purl.obolibrary.org/obo/emap.owl", cdaWorkspace + "/emap.owl" }                                                     // EMAP.obo
//                , { "http://purl.obolibrary.org/obo/emapa.owl", cdaWorkspace + "/emapa.owl" }                                                 // new
                , { "https://raw.githubusercontent.com/evidenceontology/evidenceontology/master/eco.owl", cdaWorkspace + "/eco.owl" }           // eco.obo
                , { "http://www.ebi.ac.uk/efo/efo.owl", cdaWorkspace + "/efo.owl" }                                                             // efo.obo
                , { "http://purl.obolibrary.org/obo/mpath.owl", cdaWorkspace + "/mpath.owl" }                                                   // mpath.obo

                // OBO ontologies (old)
//                , { "ftp://ftp.informatics.jax.org/pub/reports/MPheno_OBO.ontology", cdaWorkspace + "/mammalian_phenotype.obo" }
//                , { "http://pato.googlecode.com/svn/trunk/quality.obo", cdaWorkspace + "/quality.obo" }
//                  { "http://obo.cvs.sourceforge.net/viewvc/obo/obo/ontology/anatomy/gross_anatomy/animal_gross_anatomy/mouse/adult_mouse_anatomy.obo", cdaWorkspace + "/adult_mouse_anatomy.obo" }
//                , { "http://obo.cvs.sourceforge.net/viewvc/obo/obo/ontology/chemical/chebi.obo", cdaWorkspace + "/chebi.obo" }
//                , { "http://obo.cvs.sourceforge.net/viewvc/obo/obo/ontology/anatomy/gross_anatomy/animal_gross_anatomy/mouse/EMAP.obo", cdaWorkspace + "/EMAP.obo" }
//                , { "https://raw.githubusercontent.com/evidenceontology/evidenceontology/master/eco.obo", cdaWorkspace + "/eco.obo" }
//                , { "http://svn.code.sf.net/p/efo/code/trunk/src/efoinobo/efo.obo", cdaWorkspace + "/efo.obo" }
//                , { "http://mpath.googlecode.com/svn/trunk/mpath.obo", cdaWorkspace + "/mpath.obo" }
        };

        downloadReportsTasklet = new SystemCommandTasklet();
        FileOutputStream fos;
        ReadableByteChannel rbc;
        final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
        String outputAppender = DATE_FORMAT.format(new Date());
        String source;
        long start, startStep;
        String target;
        String targetTemp;
        URL url;

        startStep = new Date().getTime();
        try {
            Files.createDirectories(Paths.get(cdaWorkspace));
        } catch (IOException e) {
            logger.error("Create cda directory '" + cdaWorkspace + "' failed. Reason: " + e.getLocalizedMessage());
        }

        for (String[] downloadFile : DOWNLOAD_FILES) {
            start = new Date().getTime();
            target = downloadFile[1];
            targetTemp = target + "." + outputAppender;
            source = downloadFile[0];
            try {
                url = new URL(source);
                rbc = Channels.newChannel(url.openStream());
                fos = new FileOutputStream(targetTemp);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                Files.move(Paths.get(targetTemp), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);
                logger.info(source + " -> " + target + "(" + commonUtils.msToHms(new Date().getTime() - start) + ")");

            } catch (IOException e) {
                logger.error(source + " -> " + target + "(" + commonUtils.msToHms(new Date().getTime() - start) + "). Reason: " + e.getLocalizedMessage());
            }
        }

        // A SystemCommandTasklet needs something to execute or it throws an exception. This is a do-nothing command to satisfy that requirement.
        command = "ls";
        downloadReportsTasklet.setCommand(command);
        downloadReportsTasklet.setTimeout(TASKLET_TIMEOUT);
        downloadReportsTasklet.setWorkingDirectory(cdaWorkspace);

        logger.info("Total step elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));

        return downloadReportsTasklet;
    }
}