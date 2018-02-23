/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.create.load;

import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.PhenotypeParameterOntologyAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Updates the cda_x_y.phenotype_parameter_ontology_annotation obsolete terms with the latest terms
 *
 * Created by mrelac on 31/08/2016.
 *
 */
@ComponentScan
public class PhenotypeParameterOntologyAssociationUpdater implements CommandLineRunner {

    private CdaSqlUtils                 cdaSqlUtils;
    private final Logger                logger = LoggerFactory.getLogger(this.getClass());


    @Inject
    @Lazy
    public PhenotypeParameterOntologyAssociationUpdater(CdaSqlUtils cdaSqlUtils) {
        this.cdaSqlUtils = cdaSqlUtils;
    }


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PhenotypeParameterOntologyAssociationUpdater.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }


    @Override
    public void run(String... strings) {

        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must be set");

        cdaSqlUtils.deletePhenotypeParameterOntologyAnnotationNullOntologyAcc();                                        // Delete any rows form phenotype_parameter_ontology_annotation with null ontology_acc

        Map<String, OntologyTerm> masterMap = cdaSqlUtils.getOntologyTermsByAccessionId();                              // This is the master map of ontology terms in the ontology_term table, indexed by ontology accession id

        List<PhenotypeParameterOntologyAnnotation> workingPopaList =                                                    // This is the working POPA list to query for updates
                cdaSqlUtils.getPhenotypeParameterOntologyAnnotations();

        Map<String, PhenotypeParameterOntologyAnnotation> workingPopaMap = new ConcurrentHashMap<>();                   // This is the working POPA map, indexed by ontology accession id
        for (PhenotypeParameterOntologyAnnotation impressPpoa : workingPopaList) {
            workingPopaMap.put(impressPpoa.getOntologyAcc(), impressPpoa);
        }

        // Create a list of working ontology terms. Only the accession id and dbId are populated (as that's all the popa table has)
        List<OntologyTerm> workingTerms = new ArrayList<>();                                                            // This is the working OntologyTerm list, extracted from the POPA list above
        for (PhenotypeParameterOntologyAnnotation workingPopa : workingPopaList) {
            workingTerms.add(new OntologyTerm(workingPopa.getOntologyAcc(), workingPopa.getOntologyDbId()));
        }

        Map<String, OntologyTerm> replacementMap = cdaSqlUtils.getUpdatedOntologyTermMap(workingTerms);                 // This is the map of replacement ontology terms, indexed by working ontology accession id
        int count = cdaSqlUtils.updatePhenotypeParameterOntologyAnnotations(replacementMap);

        logger.info("Updated " + count + " ontology terms in the phenotype_parameter_ontology_annotation table.");
    }
}