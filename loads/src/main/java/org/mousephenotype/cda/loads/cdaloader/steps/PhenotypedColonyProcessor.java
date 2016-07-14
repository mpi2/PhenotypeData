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

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 09/06/16.
 */
public class PhenotypedColonyProcessor implements ItemProcessor<PhenotypedColony, PhenotypedColony> {

    private int                           addedAllelesCount = 0;
    private int                           addedGenesCount = 0;
    private int                           addedPhenotypedColoniesCount = 0;
    private int                           addedStrainsCount = 0;
    private Map<String, String>           alleleSymbolToAccessionIdMap;                 // key = allele symbol. Value = allele accession id
    private Map<String, Allele>           alleles;                                      // Alleles mapped by allele accession id
    private Map<String, PhenotypedColony> phenotypedColonies = new HashMap<>(60000);    // phenotyped colonies mapped by colony name
    private Map<String, GenomicFeature>   genes;                                        // Genes mapped by marker accession id
    private Map<String, Organisation>     organisations;                                // Organisations mapped by name
    private Map<String, Project>          projects;                                     // Projects mapped by name
    private Map<String, Strain>           strains;                                      // Strains mapped by strain accession id
    protected int                         lineNumber = 0;


    Set<String> missingOrganisations = ConcurrentHashMap.newKeySet();
    Set<String> missingProjects = ConcurrentHashMap.newKeySet();

    private final Logger     logger      = LoggerFactory.getLogger(this.getClass());
    public final Set<String> errMessages = ConcurrentHashMap.newKeySet();       // This is the java 8 way to create a concurrent hash set.

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;

    private final String[] expectedHeadings = new String[] {
              "Marker Symbol"
            , "MGI Accession ID"
            , "Colony Name"
            , "Es Cell Name"
            , "Colony Background Strain"
            , "Production Centre"
            , "Production Consortium"
            , "Phenotyping Centre"
            , "Phenotyping Consortium"
            , "Cohort Production Centre"
            , "Allele Symbol"
    };


    public PhenotypedColonyProcessor(Map<String, Allele> alleles, Map<String, GenomicFeature> genes, Map<String, Strain> strains) throws CdaLoaderException {
        this.alleles = alleles;
        this.genes = genes;
        this.strains = strains;
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
                , newPhenotypedColony.getStrain().getName()
                , newPhenotypedColony.getProductionCentre().getName()
                , newPhenotypedColony.getProductionConsortium().getName()
                , newPhenotypedColony.getPhenotypingCentre().getName()
                , newPhenotypedColony.getPhenotypingConsortium().getName()
                , newPhenotypedColony.getCohortProductionCentre().getName()
                , newPhenotypedColony.getAllele().getSymbol()
            };

            for (int i = 0; i < expectedHeadings.length; i++) {
                if ( ! expectedHeadings[i].equals(actualHeadings[i])) {
                    throw new CdaLoaderException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
                }
            }

            organisations = sqlLoaderUtils.getOrganisations();
            projects = sqlLoaderUtils.getProjects();

            return null;
        }

        // Populate the necessary collections.
        if ((genes == null) || (genes.isEmpty())) {
            genes = sqlLoaderUtils.getGenes();
        }
        if ((alleles == null) || (alleles.isEmpty())) {
            alleles = sqlLoaderUtils.getAlleles();
        }
        if (alleleSymbolToAccessionIdMap == null) {
            alleleSymbolToAccessionIdMap = new HashMap<>(150000);
            for (Allele allele : alleles.values()) {
                alleleSymbolToAccessionIdMap.put(allele.getSymbol(), allele.getId().getAccession());
            }
        }
        if ((strains == null) || (strains.isEmpty())) {
            strains = sqlLoaderUtils.getStrains();
        }

        // FIXME FIXME FIXME - Team needs to check this!
        // Look up the gene. Insert it if it doesn't yet exist.
        GenomicFeature gene = genes.get(newPhenotypedColony.getGene().getId().getAccession());
        if (gene == null) {
            gene = new GenomicFeature();
            DatasourceEntityId id = new DatasourceEntityId();
            id.setAccession(newPhenotypedColony.getGene().getId().getAccession());
            id.setDatabaseId(DbIdType.IMPC.intValue());
            gene.setId(id);
            gene.setSymbol(newPhenotypedColony.getGene().getSymbol());
            gene.setName(gene.getSymbol());
            OntologyTerm biotype = sqlLoaderUtils.getOntologyTerm(26, "MmusDv:0000041");        // FIXME FIXME FIXME Use "unknown" for now.
            gene.setBiotype(biotype);
            Map<String, Integer> counts = sqlLoaderUtils.insertGene(gene, null);
            genes.put(gene.getId().getAccession(), gene);
            addedGenesCount += counts.get("genes");
        }
        newPhenotypedColony.setGene(gene);

        // FIXME FIXME FIXME - Team needs to check this!
        // Look up allele. Insert it if it doesn't yet exist.
        Allele allele = alleles.get(newPhenotypedColony.getAllele().getId().getAccession());
        if (allele == null) {
            allele = new Allele();
            DatasourceEntityId id = new DatasourceEntityId();
            id.setAccession(newPhenotypedColony.getAllele().getId().getAccession());
            id.setDatabaseId(DbIdType.IMPC.intValue());
            allele.setId(id);
            allele.setGene(gene);
            allele.setSymbol(newPhenotypedColony.getAllele().getSymbol());
            allele.setName(allele.getSymbol());
            OntologyTerm biotype = sqlLoaderUtils.getOntologyTerm(26, "MmusDv:0000041");        // FIXME FIXME FIXME Use "unknown" for now.
            allele.setBiotype(biotype);
            alleles.put(allele.getId().getAccession(), allele);
            addedAllelesCount += sqlLoaderUtils.insertAllele(allele, null);
        }
        newPhenotypedColony.setAllele(allele);

        // FIXME FIXME FIXME - Team needs to check this!
        // Look up strain. Insert it if it doesn't yet exist.
        Strain strain = strains.get(newPhenotypedColony.getStrain().getId().getAccession());
        if (strain == null) {
            strain = new Strain();
            DatasourceEntityId id = new DatasourceEntityId();
            id.setAccession(newPhenotypedColony.getStrain().getId().getAccession());
            id.setDatabaseId(DbIdType.IMPC.intValue());
            strain.setId(id);
            strain.setName(newPhenotypedColony.getStrain().getName());
            OntologyTerm biotype = sqlLoaderUtils.getOntologyTerm(26, "MmusDv:0000041");        // FIXME FIXME FIXME Use "unknown" for now.
            strain.setBiotype(biotype);
            Map<String, Integer> counts = sqlLoaderUtils.insertStrain(strain);
            strains.put(strain.getId().getAccession(), strain);
            addedStrainsCount += counts.get("strains");
        }
        newPhenotypedColony.setStrain(strain);

        Organisation productionCentre = organisations.get(newPhenotypedColony.getProductionCentre());
        if (productionCentre == null) {
            missingOrganisations.add("Unknown productionCentre " + newPhenotypedColony.getProductionCentre() + ". Record skipped...");
            return null;
        } else {
            newPhenotypedColony.setProductionCentre(productionCentre);
        }

        Project productionConsortium = projects.get(newPhenotypedColony.getProductionConsortium());
        if (productionConsortium == null) {
            missingProjects.add("Unknown productionConsortium " + newPhenotypedColony.getProductionConsortium() + ". Record skipped...");
            return null;
        } else {
            newPhenotypedColony.setProductionConsortium(productionConsortium);
        }

        Organisation phenotypingCentre = organisations.get(newPhenotypedColony.getPhenotypingCentre());
        if (phenotypingCentre == null) {
            missingOrganisations.add("Unknown phenotypingCentre " + newPhenotypedColony.getPhenotypingCentre() + ". Record skipped...");
            return null;
        } else {
            newPhenotypedColony.setPhenotypingCentre(phenotypingCentre);
        }

        Project phenotypingConsortium = projects.get(newPhenotypedColony.getPhenotypingConsortium());
        if (phenotypingConsortium == null) {
            missingProjects.add("Unknown phenotypingConsortium " + newPhenotypedColony.getPhenotypingConsortium() + ". Record skipped...");
            return null;
        } else {
            newPhenotypedColony.setPhenotypingConsortium(phenotypingConsortium);
        }

        Organisation cohortProductionCentre = organisations.get(newPhenotypedColony.getCohortProductionCentre());
        if (cohortProductionCentre == null) {
            missingOrganisations.add("Unknown cohortProductionCentre " + newPhenotypedColony.getCohortProductionCentre() + ". Record skipped...");
            return null;
        } else {
            newPhenotypedColony.setCohortProductionCentre(cohortProductionCentre);
        }

        addedPhenotypedColoniesCount++;

        return newPhenotypedColony;
    }

    public int getAddedAllelesCount() {
        return addedAllelesCount;
    }

    public int getAddedGenesCount() {
        return addedGenesCount;
    }

    public int getAddedPhenotypedColoniesCount() {
        return addedPhenotypedColoniesCount;
    }

    public int getAddedStrainsCount() {
        return addedStrainsCount;
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