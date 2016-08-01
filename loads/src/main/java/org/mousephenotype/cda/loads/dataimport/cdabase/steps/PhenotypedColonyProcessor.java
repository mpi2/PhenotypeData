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

import org.apache.commons.codec.digest.DigestUtils;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.CdabaseLoaderUtils;
import org.mousephenotype.cda.loads.legacy.LoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 09/06/16.
 */
public class PhenotypedColonyProcessor implements ItemProcessor<PhenotypedColony, PhenotypedColony> {

    private int                           addedAllelesCount = 0;
    private int                           addedGenesCount = 0;
    private int                           addedPhenotypedColoniesCount = 0;
    private int                           addedStrainsCount = 0;
    private Map<String, List<String>>     alleleSymbolToAccessionIdMap;                 // key = allele symbol. Value = list of allele accession ids
    private Map<String, Allele>           alleles;                                      // Alleles mapped by allele accession id
    private Map<String, GenomicFeature>   genes;                                        // Genes mapped by marker accession id
    private Map<String, Organisation>     organisations;                                // Organisations mapped by name
    private Map<String, Project>          projects;                                     // Projects mapped by name
    private Map<String, String>           strainNameToAccessionIdMap;                   // key = strain name. Value = strain accession id
    private Map<String, Strain>           strains;                                      // Strains mapped by strain accession id
    protected int                         lineNumber = 0;


    Set<String> missingOrganisations = ConcurrentHashMap.newKeySet();
    Set<String> missingProjects = ConcurrentHashMap.newKeySet();
    Set<String> missingStrains = ConcurrentHashMap.newKeySet();

    private       LoaderUtils loaderUtils = new LoaderUtils();
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());
    public final  Set<String> errMessages = ConcurrentHashMap.newKeySet();       // This is the java 8 way to create a concurrent hash set.

    @Autowired
    @Qualifier("cdabaseLoaderUtils")
    private CdabaseLoaderUtils cdabaseLoaderUtils;

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


    public PhenotypedColonyProcessor(Map<String, Allele> alleles, Map<String, GenomicFeature> genes, Map<String, Strain> strains) throws DataImportException {
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
                    throw new DataImportException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
                }
            }

            organisations = cdabaseLoaderUtils.getOrganisations();
            projects = cdabaseLoaderUtils.getProjects();

            return null;
        }

        // Populate the necessary collections.
        if ((genes == null) || (genes.isEmpty())) {
            genes = cdabaseLoaderUtils.getGenes();
        }
        if ((alleles == null) || (alleles.isEmpty())) {
            alleles = cdabaseLoaderUtils.getAlleles();
        }
        if (alleleSymbolToAccessionIdMap == null) {
            alleleSymbolToAccessionIdMap = new HashMap<>();
            for (Allele allele : alleles.values()) {
                List<String> alleleAccessionIdList = alleleSymbolToAccessionIdMap.get(allele.getSymbol());
                if (alleleAccessionIdList == null) {
                    alleleAccessionIdList = new ArrayList<>();
                    alleleSymbolToAccessionIdMap.put(allele.getSymbol(), alleleAccessionIdList);
                }
                alleleAccessionIdList.add(allele.getId().getAccession());
            }
        }
        if ((strains == null) || (strains.isEmpty())) {
            strains = cdabaseLoaderUtils.getStrains();
        }
        if (strainNameToAccessionIdMap == null) {
            strainNameToAccessionIdMap = new HashMap<>();
            for (Strain strain : strains.values()) {
                strainNameToAccessionIdMap.put(strain.getName(), strain.getId().getAccession());
            }
        }

        // Look up the gene. Insert it if it doesn't yet exist.
        GenomicFeature gene = genes.get(newPhenotypedColony.getGene().getId().getAccession());
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
            OntologyTerm biotype = cdabaseLoaderUtils.getOntologyTerm(DbIdType.Genome_Feature_Type.intValue(), "unknown");       // name = "unknown" (description = "A gene with no subtype")
            gene.setBiotype(biotype);
            gene.setStatus(CdabaseLoaderUtils.STATUS_ACTIVE);
            Map<String, Integer> counts = cdabaseLoaderUtils.insertGene(gene);
            if (counts.get("genes") > 0) {
                addedGenesCount += counts.get("genes");
                genes.put(gene.getId().getAccession(), gene);
                logger.warn("markerAccessionId {} not in database. Inserted gene with marker symbol {}", markerAccessionId, markerSymbol);
            }
        }
        newPhenotypedColony.setGene(gene);

        // Look up allele by symbol. There can be multiple alleleAccessionIds for a single symbol. Look for the allele
        // symbol in the list of allele accession ids returned from alleleSymbolToAccessionIdMap and, if it is still not found, insert it.
        List<String> alleleAccessionIds = alleleSymbolToAccessionIdMap.get(newPhenotypedColony.getAllele().getSymbol());
        Allele allele = null;
        if (alleleAccessionIds != null) {
            for (String alleleAccessionId : alleleAccessionIds) {
                allele = alleles.get(alleleAccessionId);
                if (allele != null) {
                    break;
                }
            }
        }

        if (allele == null) {

            String alleleSymbol = newPhenotypedColony.getAllele().getSymbol();
            String alleleAccessionId = "NULL-" + DigestUtils.md5Hex(newPhenotypedColony.getAllele().getSymbol()).substring(0,9).toUpperCase();

//            logger.debug("Allele symbol {} not in database. Inserting with alleleAccessionId {}", alleleSymbol, alleleAccessionId);

            allele = new Allele();
            DatasourceEntityId id = new DatasourceEntityId();
            id.setAccession(alleleAccessionId);
            id.setDatabaseId(DbIdType.MGI.intValue());
            allele.setId(id);
            allele.setGene(gene);
            allele.setSymbol(newPhenotypedColony.getAllele().getSymbol());
            allele.setName(newPhenotypedColony.getAllele().getSymbol());
            OntologyTerm biotype = new OntologyTerm("CV:00000013", DbIdType.MGI.intValue());
            allele.setBiotype(biotype);
            int count = cdabaseLoaderUtils.insertAllele(allele);
            if (count > 0) {
                addedAllelesCount += count;
                alleles.put(allele.getId().getAccession(), allele);
                List<String> alleleList = new ArrayList<>();
                alleleList.add(allele.getId().getAccession());
                alleleSymbolToAccessionIdMap.put(allele.getSymbol(), alleleList);
            }
        }
        newPhenotypedColony.setAllele(allele);

        // Look up strain by name in the strainNameToAccessionIdMap and, if it is not found, insert it.
        String strainName = newPhenotypedColony.getStrain().getName();
        String strainAccessionId = strainNameToAccessionIdMap.get(strainName);
        Strain strain;
        if (strainAccessionId == null) {
            String datasourceAccName = "IMPC";
            String hex = DigestUtils.md5Hex(strainName).substring(0, 5).toUpperCase();
            String strainAcc = datasourceAccName + "-" + hex;

//            logger.debug("Strain name {} not in database. Inserting as an {} strain with strainAccessionId {}", strainName, datasourceAccName, strainAcc);

            DatasourceEntityId id = new DatasourceEntityId();
            id.setAccession(strainAcc);
            id.setDatabaseId(DbIdType.MGI.intValue());

            strain = new Strain();
            strain.setId(id);
            strain.setName(strainName);
            Map<String, Integer> counts = cdabaseLoaderUtils.insertStrain(strain);
            if (counts.get("strains") > 0) {
                addedStrainsCount += counts.get("strains");
                strains.put(strain.getId().getAccession(), strain);
                strainNameToAccessionIdMap.put(strain.getName(), strain.getId().getAccession());
            }
        } else {
            strain = strains.get(strainAccessionId);
        }
        newPhenotypedColony.setStrain(strain);

        String mappedOrganisation = loaderUtils.translateTerm(newPhenotypedColony.getProductionCentre().getName());
        Organisation productionCentre = organisations.get(mappedOrganisation);
        if (productionCentre == null) {
            missingOrganisations.add("Skipped unknown productionCentre " + newPhenotypedColony.getProductionCentre().getName());
            return null;
        } else {
            newPhenotypedColony.setProductionCentre(productionCentre);
        }

        String mappedProject = loaderUtils.translateTerm(newPhenotypedColony.getProductionConsortium().getName());
        Project productionConsortium = projects.get(mappedProject);
        if (productionConsortium == null) {
            missingProjects.add("Skipped unknown productionConsortium " + newPhenotypedColony.getProductionConsortium().getName());
            return null;
        } else {
            newPhenotypedColony.setProductionConsortium(productionConsortium);
        }

        String mappedPhenotypingCentre = loaderUtils.translateTerm(newPhenotypedColony.getPhenotypingCentre().getName());
        Organisation phenotypingCentre = organisations.get(mappedPhenotypingCentre);
        if (phenotypingCentre == null) {
            missingOrganisations.add("Skipped unknown phenotypingCentre " + newPhenotypedColony.getPhenotypingCentre().getName());
            return null;
        } else {
            newPhenotypedColony.setPhenotypingCentre(phenotypingCentre);
        }

        String mappedPhenotypingConsortium = loaderUtils.translateTerm(newPhenotypedColony.getPhenotypingConsortium().getName());
        Project phenotypingConsortium = projects.get(mappedPhenotypingConsortium);
        if (phenotypingConsortium == null) {
            missingProjects.add("Skipped unknown phenotypingConsortium " + newPhenotypedColony.getPhenotypingConsortium().getName());
            return null;
        } else {
            newPhenotypedColony.setPhenotypingConsortium(phenotypingConsortium);
        }

        String mappedCohortProductionCentre = loaderUtils.translateTerm(newPhenotypedColony.getCohortProductionCentre().getName());
        Organisation cohortProductionCentre = organisations.get(mappedCohortProductionCentre);
        if (cohortProductionCentre == null) {
            missingOrganisations.add("Skipped unknown cohortProductionCentre " + newPhenotypedColony.getCohortProductionCentre().getName());
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

    public Set<String> getMissingStrains() {
        return missingStrains;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }
}