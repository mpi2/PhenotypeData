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

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.ConcurrentHashMapAllowNull;
import org.mousephenotype.cda.loads.common.LoadUtils;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.Set;

/**
 * Created by mrelac on 09/06/16.
 */
public class PhenotypedColonyProcessor implements ItemProcessor<PhenotypedColony, PhenotypedColony> {

    private int                         addedGenesCount = 0;
    private int                         addedPhenotypedColoniesCount = 0;
    private Map<String, GenomicFeature> genesByGeneAccessionId;                             // Genes mapped by gene accession id
    private Map<String, Organisation>   organisationsByName;                                // Organisations mapped by name
    private Map<String, Project>        projectsByName;                                     // Projects mapped by name
    protected int                       lineNumber = 0;


    Set<String> missingOrganisations = ConcurrentHashMapAllowNull.newKeySet();
    Set<String> missingProjects      = ConcurrentHashMapAllowNull.newKeySet();
    Set<String> ignoredProjects      = ConcurrentHashMapAllowNull.newKeySet();        // Ignore these projects.

    private final LoadUtils   loadUtils   = new LoadUtils();
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());
    public final  Set<String> errMessages = ConcurrentHashMapAllowNull.newKeySet();       // This is the java 8 way to create a concurrent hash set.

    @Autowired
    @Qualifier("cdabaseSqlUtils")
    private CdaSqlUtils cdaSqlUtils;

    private final String[] expectedHeadings = new String[] {
            "Marker Symbol"
          , "MGI Accession ID"
          , "Colony Name"
          , "Es Cell Name"
          , "Colony Background Strain"
          , "Mgi Strain Accession id"
          , "Cohort Production Centre"
          , "Production Consortium"
          , "Phenotyping Centre"
          , "Phenotyping Consortium"
          , "Allele Symbol"
    };


    public PhenotypedColonyProcessor(Map<String, GenomicFeature> genesByGeneAccessionId) throws DataLoadException {
        this.genesByGeneAccessionId = genesByGeneAccessionId;
        ignoredProjects.add("EUCOMMToolsCre");
    }

    @Override
    public PhenotypedColony process(PhenotypedColony newPhenotypedColony) throws Exception {

        lineNumber++;

        // Validate the file using the heading names and initialize any collections.
        if (lineNumber == 1) {
            String[] actualHeadings = new String[] {
                    newPhenotypedColony.getGene().getSymbol()
                  , newPhenotypedColony.getGene().getId().getAccession()
                  , newPhenotypedColony.getColonyName()
                  , newPhenotypedColony.getEs_cell_name()
                  , newPhenotypedColony.getBackgroundStrain()
                  , newPhenotypedColony.getBackgroundStrainAcc()
                  , newPhenotypedColony.getProductionCentre().getName()
                  , newPhenotypedColony.getProductionConsortium().getName()
                  , newPhenotypedColony.getPhenotypingCentre().getName()
                  , newPhenotypedColony.getPhenotypingConsortium().getName()
                  , newPhenotypedColony.getAlleleSymbol()
            };

            for (int i = 0; i < expectedHeadings.length; i++) {
                if ( ! expectedHeadings[i].equals(actualHeadings[i])) {
                    throw new DataLoadException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
                }
            }

            organisationsByName = cdaSqlUtils.getOrganisations();
            projectsByName = cdaSqlUtils.getProjectsByName();

            return null;
        }

        // Populate the necessary collections.
        if ((genesByGeneAccessionId == null) || (genesByGeneAccessionId.isEmpty())) {
            genesByGeneAccessionId = cdaSqlUtils.getGenesByAcc();
        }

        // Look up the gene. Insert it if it doesn't yet exist.
        GenomicFeature gene = genesByGeneAccessionId.get(newPhenotypedColony.getGene().getId().getAccession());
        if (gene == null) {
            String markerAccessionId = newPhenotypedColony.getGene().getId().getAccession();
            String markerSymbol = newPhenotypedColony.getGene().getSymbol();

            gene = new GenomicFeature();
            DatasourceEntityId id = new DatasourceEntityId();
            id.setAccession(markerAccessionId);
            id.setDatabaseId(DbIdType.IMPC.intValue());
            gene.setId(id);
            gene.setSymbol(markerSymbol);
            gene.setName(markerSymbol);
            OntologyTerm biotype = cdaSqlUtils.getOntologyTerm(DbIdType.Genome_Feature_Type.intValue(), "unknown");       // name = "unknown" (description = "A gene with no subtype")
            gene.setBiotype(biotype);
            gene.setStatus(CdaSqlUtils.STATUS_ACTIVE);
            Map<String, Integer> counts = cdaSqlUtils.insertGene(gene);
            if (counts.get("genes") > 0) {
                addedGenesCount += counts.get("genes");
                genesByGeneAccessionId.put(gene.getId().getAccession(), gene);
                logger.debug("markerAccessionId {} not in database. Inserted gene with marker symbol {}", markerAccessionId, markerSymbol);
            }
        }
        newPhenotypedColony.setGene(gene);

        //
        // Every colony should have an allele symbol rovided by iMits
        // Validate that the allele symbol is not null or empty string
        //
        if ( StringUtils.isEmpty(newPhenotypedColony.getAlleleSymbol()) ) {
            logger.warn("Allele symbol is null or empty for colony {}. Skipping record", newPhenotypedColony.getColonyName());
            return null;
        }

        String mappedProject = loadUtils.translateTerm(newPhenotypedColony.getProductionConsortium().getName());
        Project productionConsortium = projectsByName.get(mappedProject);
        if (productionConsortium == null) {
            if ( ! ignoredProjects.contains(mappedProject)) {
                missingProjects.add("Skipped unknown productionConsortium::translated " + newPhenotypedColony.getProductionConsortium().getName() + "::" + mappedProject);
            }
            return null;
        } else {
            newPhenotypedColony.setProductionConsortium(productionConsortium);
        }

        String mappedPhenotypingCentre = loadUtils.translateTerm(newPhenotypedColony.getPhenotypingCentre().getName());
        Organisation phenotypingCentre = organisationsByName.get(mappedPhenotypingCentre);
        if (phenotypingCentre == null) {
            missingOrganisations.add("Skipped unknown phenotypingCentre::translated " + newPhenotypedColony.getPhenotypingCentre().getName() + "::" + mappedPhenotypingCentre);
            return null;
        } else {
            newPhenotypedColony.setPhenotypingCentre(phenotypingCentre);
        }

        String mappedPhenotypingConsortium = loadUtils.translateTerm(newPhenotypedColony.getPhenotypingConsortium().getName());
        Project phenotypingConsortium = projectsByName.get(mappedPhenotypingConsortium);
        if (phenotypingConsortium == null) {
            if ( ! ignoredProjects.contains(mappedProject)) {
                missingProjects.add("Skipped unknown phenotypingConsortium::translated " + newPhenotypedColony.getPhenotypingConsortium().getName() + "::" + mappedPhenotypingConsortium);
            }
            return null;
        } else {
            newPhenotypedColony.setPhenotypingConsortium(phenotypingConsortium);
        }

        String mappedProductionCentre = loadUtils.translateTerm(newPhenotypedColony.getProductionCentre().getName());
        Organisation productionCentre = organisationsByName.get(mappedProductionCentre);
        if (productionCentre == null) {
            missingOrganisations.add("Skipped unknown productionCentre::translated " + newPhenotypedColony.getProductionCentre().getName() + "::" + mappedProductionCentre);
            return null;
        } else {
            newPhenotypedColony.setProductionCentre(productionCentre);
        }

        addedPhenotypedColoniesCount++;

        return newPhenotypedColony;
    }

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