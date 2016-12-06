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
import org.mousephenotype.cda.db.pojo.Organisation;
import org.mousephenotype.cda.db.pojo.Project;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.loads.legacy.LoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 01/12/16.
 */
public class EurophenomeColonyProcessor /* implements ItemProcessor<EurophenomeColonyMapper, PhenotypedColony> */ {



    // fixme fixme fixme fixme fixme



    private int                           addedGenesCount = 0;
    private int                           addedPhenotypedColoniesCount = 0;
    private Map<String, GenomicFeature>   genes;                                        // Genes mapped by gene symbol
    private Map<String, Organisation>     organisations;                                // Organisations mapped by name
    private Map<String, Project>          projects;                                     // Projects mapped by name
    protected int                         lineNumber = 0;


    Set<String> missingOrganisations = ConcurrentHashMap.newKeySet();
    Set<String> missingProjects = ConcurrentHashMap.newKeySet();

    private       LoaderUtils loaderUtils = new LoaderUtils();
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());
    public final  Set<String> errMessages = ConcurrentHashMap.newKeySet();       // This is the java 8 way to create a concurrent hash set.

    @Autowired
    @Qualifier("cdabaseSqlUtils")
    private CdaSqlUtils cdaSqlUtils;

    @Autowired
    @Qualifier("dccSqlUtils")
    private DccSqlUtils dccSqlUtils;

    private final String[] expectedHeadings = new String[] {
              "colony"
            , "gene"
            , "allele"
    };


    public EurophenomeColonyProcessor(Map<String, GenomicFeature> genes) throws DataLoadException {

    }

//    @Override
//    public PhenotypedColony process(EurophenomeColonyMapper europhenomeColony) throws Exception {
//
//        lineNumber++;
//
//        PhenotypedColony phenotypedColony = new PhenotypedColony();

//        // Validate the file using the heading names and initialize any collections.
//        if (lineNumber == 1) {
//            String[] actualHeadings = new String[] {
//                  europhenomeColony.getColonyId()
//                , europhenomeColony.getGene()
//                , europhenomeColony.getAllele()
//            };
//
//            for (int i = 0; i < expectedHeadings.length; i++) {
//                if ( ! expectedHeadings[i].equals(actualHeadings[i])) {
//                    throw new DataLoadException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
//                }
//            }
//
//            organisations = cdaSqlUtils.getOrganisations();
//            projects = cdaSqlUtils.getProjects();
//
//            return null;
//        }
//
//        // Populate the necessary collections.
//        if (genes == null) {
//            genes = cdaSqlUtils.getGenesBySymbol();
//        }
//
//        // Look up the gene. Insert it if it doesn't yet exist.
//        GenomicFeature gene = genes.get(europhenomeColony.getGene());
//        if (gene == null) {
//
//
//
//
//            throw new DataLoadException("Gene " + europhenomeColony.getGene() + " not found and there is no accession id");
//
//
//
//
//
//
//
//
//           //  String markerAccessionId = europhenomeColony.getGene().getId().getAccession();
//            String markerSymbol = europhenomeColony.getGene();
//
//            gene = new GenomicFeature();
//            DatasourceEntityId id = new DatasourceEntityId();
////            id.setAccession(markerAccessionId);
//            id.setDatabaseId(DbIdType.IMPC.intValue());
//            gene.setId(id);
//            gene.setSymbol(markerSymbol);
//            gene.setName(markerSymbol);
//            OntologyTerm biotype = cdaSqlUtils.getOntologyTerm(DbIdType.Genome_Feature_Type.intValue(), "unknown");       // name = "unknown" (description = "A gene with no subtype")
//            gene.setBiotype(biotype);
//            gene.setStatus(CdaSqlUtils.STATUS_ACTIVE);
//            Map<String, Integer> counts = cdaSqlUtils.insertGene(gene);
//            if (counts.get("genes") > 0) {
//                addedGenesCount += counts.get("genes");
//                genes.put(gene.getId().getAccession(), gene);
////                logger.debug("markerAccessionId {} not in database. Inserted gene with marker symbol {}", markerAccessionId, markerSymbol);
//            }
//        }
//        phenotypedColony.setGene(gene);
//
//        String mappedOrganisation = loaderUtils.translateTerm(europhenomeColony.getProductionCentre().getName());
//        Organisation productionCentre = organisations.get(mappedOrganisation);
//        if (productionCentre == null) {
//            missingOrganisations.add("Skipped unknown productionCentre " + europhenomeColony.getProductionCentre().getName());
//            return null;
//        } else {
//            phenotypedColony.setProductionCentre(productionCentre);
//        }
//
//        String mappedProject = loaderUtils.translateTerm(europhenomeColony.getProductionConsortium().getName());
//        Project productionConsortium = projects.get(mappedProject);
//        if (productionConsortium == null) {
//            missingProjects.add("Skipped unknown productionConsortium " + europhenomeColony.getProductionConsortium().getName());
//            return null;
//        } else {
//            phenotypedColony.setProductionConsortium(productionConsortium);
//        }
//
//        String mappedPhenotypingCentre = loaderUtils.translateTerm(europhenomeColony.getPhenotypingCentre().getName());
//        Organisation phenotypingCentre = organisations.get(mappedPhenotypingCentre);
//        if (phenotypingCentre == null) {
//            missingOrganisations.add("Skipped unknown phenotypingCentre " + europhenomeColony.getPhenotypingCentre().getName());
//            return null;
//        } else {
//            phenotypedColony.setPhenotypingCentre(phenotypingCentre);
//        }
//
//        String mappedPhenotypingConsortium = loaderUtils.translateTerm(europhenomeColony.getPhenotypingConsortium().getName());
//        Project phenotypingConsortium = projects.get(mappedPhenotypingConsortium);
//        if (phenotypingConsortium == null) {
//            missingProjects.add("Skipped unknown phenotypingConsortium " + europhenomeColony.getPhenotypingConsortium().getName());
//            return null;
//        } else {
//            phenotypedColony.setPhenotypingConsortium(phenotypingConsortium);
//        }
//
//        String mappedCohortProductionCentre = loaderUtils.translateTerm(europhenomeColony.getCohortProductionCentre().getName());
//        Organisation cohortProductionCentre = organisations.get(mappedCohortProductionCentre);
//        if (cohortProductionCentre == null) {
//            missingOrganisations.add("Skipped unknown cohortProductionCentre " + europhenomeColony.getCohortProductionCentre().getName());
//            return null;
//        } else {
//            phenotypedColony.setCohortProductionCentre(cohortProductionCentre);
//        }
//
//        addedPhenotypedColoniesCount++;
//
//        return phenotypedColony;
//    }

    public int getAddedGenesCount() {
        return addedGenesCount;
    }

    public int getAddedPhenotypedColoniesCount() {
        return addedPhenotypedColoniesCount;
    }

    public Set<String> getMissingOrganisations() {
        return missingOrganisations;
    }

    public Set<String> getMissingProjects() {
        return missingProjects;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }
}