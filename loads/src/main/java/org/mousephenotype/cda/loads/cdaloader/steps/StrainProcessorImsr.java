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

import java.util.*;

/**
 * Contains the business processing rules for loading the imsr strains.
 * Created by mrelac on 03/06/16.
 */
public class StrainProcessorImsr implements ItemProcessor<Strain, Strain> {

    private       int                 addedEucommStrainsCount     = 0;
    private       int                 addedStrainNotSynonymCount  = 0;
    private       int                 addedEucommSynonymsCount    = 0;
    private       Map<String, Allele> allelesMap;                                           // Key = accession id. Value = Allele instance.
    public final  Set<String> errorMessages         = new HashSet<>();
    private       int         lineNumber            = 0;
    private final Logger      logger                = LoggerFactory.getLogger(this.getClass());
    private       int         strainIsAlleleCount   = 0;
    private       int         strainNotSynonymCount = 0;
    private       Map<String, Strain> strainsMap;                                           // Key = accession id. Value = Strain instance.
    private       Map<String, String> strainNameToAccessionIdMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);    // key = strain name (strainStock). Value = strain accession id with the same strain name

    // The following  strings define the column headings in the report.txt file.
    public final static String HEADING_ACCESSION_ID = "Strain ID";
    public final static String HEADING_NAME         = "Strain/Stock";
    public final static String HEADING_SYNONYMS     = "Synonyms";
    public final static String HEADING_STRAINTYPE   = "Type";

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    public StrainProcessorImsr(Map<String, Allele> allelesMap, Map<String, Strain> strainsMap) {
        this.allelesMap = allelesMap;
        this.strainsMap = strainsMap;
    }


    /**
     * Process the provided item, returning a potentially modified or new item for continued
     * processing.  If the returned result is null, it is assumed that processing of the item
     * should not continue.
     *
     * @param strain to be processed
     * @return potentially modified or new item for continued processing, null if processing of the
     * provided item should not continue.
     * @throws Exception
     */
    @Override
    public Strain process(Strain strain) throws Exception {

        lineNumber++;

        // Validate the file using the heading names.
        if (lineNumber == 1) {
            String[] actualHeadings   = new String[]{strain.getId().getAccession(), strain.getName(), strain.getSynonyms().get(0).getSymbol(), strain.getBiotype().getName()};
            String[] expectedHeadings = new String[]{"Strain ID", "Strain/Stock", "Synonyms", "Type"};

            for (int i = 0; i < expectedHeadings.length; i++) {
                if ( ! expectedHeadings[i].equals(actualHeadings[i])) {
                    throw new CdaLoaderException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
                }

                return null;
            }
        }

        // Initialise strainNameToAccessionIdMap
        if (strainNameToAccessionIdMap.isEmpty()) {
            for (Strain tempStrain : strainsMap.values()) {
                strainNameToAccessionIdMap.put(tempStrain.getName(), tempStrain.getId().getAccession());
            }
        }

        // If name is in strainNameToAccessionIdMap, return null.
        if (strainNameToAccessionIdMap.containsKey(strain.getName())) {
            return null;
        }

        /*
        * Sometimes the report.txt file incorrectly reports a strain name as a synonym. To handle this situation:
        * For each synonym:
        *     If the synonym symbol exists in the strainNameToAccessionIdMap
        *         If that strain's synonyms do not contain the name as a synonym,
        *             add the name (as a synonym) to the strain's synonyms and update the strainsMap
        *             update the synonym table in the database
        *             return null
        *
        * If name contains "EUCOMM"
        *     If the strain accession id is not really an allele accession id
        *         Create new Strain with synonyms
        *         Update the strainsMap and strainNameToAccessionIdMap
        *         Return the newly created strain object
        *
        * Return null
         */

        for (Synonym synonym : strain.getSynonyms()) {
            String strainAsSynonymAccessionId = strainNameToAccessionIdMap.get(synonym.getSymbol());

            if (strainAsSynonymAccessionId != null) {
                strainNotSynonymCount++;

                Strain strainAsSynonym = strainsMap.get(strainAsSynonymAccessionId);

                if (sqlLoaderUtils.getSynonym(strainAsSynonymAccessionId, strain.getName()) == null) {
                    addedStrainNotSynonymCount++;
                    Synonym newSynonym = new Synonym();
                    newSynonym.setSymbol(strain.getName());
                    newSynonym.setAccessionId(strainAsSynonym.getId().getAccession());
                    newSynonym.setDbId(strainAsSynonym.getId().getDatabaseId());
                    strainAsSynonym.getSynonyms().add(newSynonym);
                    strainsMap.put(strainAsSynonym.getId().getAccession(), strainAsSynonym);
                    sqlLoaderUtils.insertStrainSynonym(strainAsSynonym, newSynonym);

                    return null;
                }
            }
        }

        if (strain.getName().contains("EUCOMM")) {
            Allele allele = allelesMap.get(strain.getId().getAccession());
            if (allele != null) {
                logger.warn("Strain {} is already in the allele table as allele {}", strain.toString(), allele);
                strainIsAlleleCount++;
            } else if ( ! strainNameToAccessionIdMap.containsKey(strain.getName())) {
                // Fill in the missing common fields in preparation for writing to the database.
                strain.getId().setDatabaseId(DbIdType.MGI.intValue());

                // Call the remaining methods to finish setting the strain instance.
                OntologyTerm biotype = sqlLoaderUtils.getMappedBiotype(DbIdType.MGI.intValue(), strain.getBiotype().getName());
                if (biotype == null) {
                    logger.warn("Line {} : NO biotype FOR strain {}.", lineNumber, strain.toString());
                    return null;
                }
                strain.setBiotype(biotype);
                if (strain.getSynonyms() != null) {
                    for (Synonym synonym : strain.getSynonyms()) {
                        synonym.setAccessionId(strain.getId().getAccession());
                        synonym.setDbId(strain.getId().getDatabaseId());
                    }
                }

                strainsMap.put(strain.getId().getAccession(), strain);
                strainNameToAccessionIdMap.put(strain.getName(), strain.getId().getAccession());

                addedEucommStrainsCount++;
                addedEucommSynonymsCount += strain.getSynonyms().size();

                return strain;
            }
        }

        return null;
    }

    public int getAddedEucommStrainsCount() {
        return addedEucommStrainsCount;
    }

    public int getAddedEucommSynonymsCount() {
        return addedEucommSynonymsCount;
    }

    public int getAddedStrainNotSynonymCount() {
        return addedStrainNotSynonymCount;
    }

    public Set<String> getErrorMessages() {
        return errorMessages;
    }

    public int getStrainIsAlleleCount() {
        return strainIsAlleleCount;
    }

    public int getStrainNotSynonymCount() {
        return strainNotSynonymCount;
    }
}