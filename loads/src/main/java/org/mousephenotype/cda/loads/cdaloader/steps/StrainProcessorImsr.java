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

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.db.pojo.Synonym;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.*;

/**
 * Contains the business processing rules for loading the imsr strains.
 * Created by mrelac on 03/06/16.
 */
public class StrainProcessorImsr implements ItemProcessor<FieldSet, List<Strain>> {

    private       int                 addedEucommStrainCount     = 0;
    private       int                 addedSynonymCount          = 0;
    public final  Set<String>         errMessages                = new HashSet<>();
    private       int                 lineNumber                 = 0;
    private final Logger              logger                     = LoggerFactory.getLogger(this.getClass());
    private       Map<String, Strain> strainsMap                 = new HashMap<>();     // Key = accession id. Value = Strain instance.
    private       Map<String, String> strainNameToAccessionIdMap = new HashMap<>();     // key = strain name (strainStock). Value = strain accession id with the same strain name

    // The following IMSR_ ints define the column offset of the given column in the report.txt file. Comment maps report.txt value column to equivalent column in MGI_Strains.rpt.
    public final static int OFFSET_ACCESSION_ID = 1;                    // StrainId       /   acc  (column 0)
    public final static int OFFSET_NAME         = 2;                    // Strain/Stock   /   name (column 1)
    public final static int OFFSET_SYNONYMS     = 5;                    // Synonyms       /   N/A
    public final static int OFFSET_STRAINTYPE   = 6;                    // type           /   type (column 2)
    public final static int OFFSET_MAX          = OFFSET_STRAINTYPE;    // Points to the highest value offset.

    // The following  ints define the column offset of the given column in the report.txt file.
    public final static String HEADING_ACCESSION_ID = "Strain ID";
    public final static String HEADING_NAME         = "Strain/Stock";
    public final static String HEADING_SYNONYMS     = "Synonyms";
    public final static String HEADING_STRAINTYPE   = "Type";

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    private void initialise() throws Exception {

        // Populate mgi strain list.
        List<Strain> strains = sqlLoaderUtils.getStrainList();
        for (Strain strain : strains) {
            strainsMap.put(strain.getId().getAccession(), strain);
            strainNameToAccessionIdMap.put(strain.getName(), strain.getId().getAccession());
        }
    }


    /**
     * Process the provided item, returning a potentially modified or new item for continued
     * processing.  If the returned result is null, it is assumed that processing of the item
     * should not continue.
     *
     * @param item to be processed
     * @return potentially modified or new item for continued processing, null if processing of the
     * provided item should not continue.
     * @throws Exception
     */
    @Override
    public List<Strain> process(FieldSet item) throws Exception {
        String accessionId;
        String synonymCell;

        List<Strain> strains = null;

        lineNumber++;

        // Initialise maps on first call to process().
        if (strainsMap.isEmpty()) {
            initialise();
        }

        String[] values = item.getValues();

        if (lineNumber == 1) {

            // Do some basic validation. The file format has changed drastically in the past.
            String expectedValue = HEADING_ACCESSION_ID;
            String actualValue   = values[OFFSET_ACCESSION_ID];
            if (!expectedValue.equals(actualValue)) {
                throw new CdaLoaderException("Expected column " + OFFSET_ACCESSION_ID
                        + " to equal " + expectedValue + " but was " + actualValue);
            }

            expectedValue = HEADING_NAME;
            actualValue   = values[OFFSET_NAME];
            if (!expectedValue.equals(actualValue)) {
                throw new CdaLoaderException("Expected column " + OFFSET_NAME
                        + " to equal " + expectedValue + " but was " + actualValue);
            }

            expectedValue = HEADING_SYNONYMS;
            actualValue = values[OFFSET_SYNONYMS];
            if (!expectedValue.equals(actualValue)) {
                throw new CdaLoaderException("Expected column " + OFFSET_SYNONYMS
                        + " to equal " + expectedValue + " but was " + actualValue);
            }

            expectedValue = HEADING_STRAINTYPE;
            actualValue = values[OFFSET_STRAINTYPE];
            if (!expectedValue.equals(actualValue)) {
                throw new CdaLoaderException("Expected column " + OFFSET_STRAINTYPE
                        + " to equal " + expectedValue + " but was " + actualValue);
            }

            return null;
        }

        // Skip empty lines. They have a field count of 1.
        if (item.getFieldCount() < 2) {
            return null;
        }

        // If the strain is already loaded, skip this record.
        try {
            accessionId = values[OFFSET_ACCESSION_ID];
            if (strainsMap.containsKey(accessionId)) {
                return null;
            }

        } catch (IndexOutOfBoundsException e) {
            logger.warn("StrainProcessorImsr(): report.txt Line " + lineNumber + " doesn't have a " + HEADING_ACCESSION_ID + " value. Skipping. Line: '" + StringUtils.join(item, ",") + "'.");
            return null;
        }

        // Sometimes the report.txt file incorrectly reports a strain name as a synonym. Fix this by comparing each
        // synonym with the strainNameToAccessionIdMap key and, if there is a match:
        //    if the strain/stock name does not yet exist as a synonym:
        //       create a synonym from the strain/stock value
        //       add the Synonym instance to this strain's list of synonyms.
        //       add the strain containing the new Synonym instance to the list of returned strains
        synonymCell = values[OFFSET_SYNONYMS];
        String[] synonymNames = synonymCell.split(",");
        try {
            boolean synonymFound = false;
            for (String synonymName: synonymNames) {

                if (synonymName.isEmpty()) {
                    continue;
                }

                if (strainNameToAccessionIdMap.containsKey(synonymName)) {
                    String strainAccessionId = strainNameToAccessionIdMap.get(synonymName);
                    Strain strain = strainsMap.get(strainAccessionId);

                    if (strain.getSynonym(values[OFFSET_NAME]) == null) {
                        Synonym synonym = new Synonym();
                        synonym.setSymbol(values[OFFSET_NAME]);
                        strain.getSynonyms().add(synonym);
                        if (strains == null) {
                            strains = new ArrayList<>();
                        }
                        strains.add(strain);                                    // Add the modified strain to the returned strain list.
                        strainsMap.put(strain.getId().getAccession(), strain);  // Update the strainsMap with the strain with the newly added synonym.

                        logger.info("[{}] Creating synonym '{}' for {}", lineNumber, synonym.getSymbol(), strain.getId().getAccession());
                        synonymFound = true;
                        addedSynonymCount++;
                    }
                }
            }

            if (synonymFound) {
                return strains;
            }

        } catch (IndexOutOfBoundsException e) {
            // There are no synonyms; thus, no strain or synonyms to add.
            return null;
        }

        // If we got here, it's because no synonyms were found.
        // If report.txt strainStock contains the string "EUCOMM", and the report.txt strainStock value is not in strainNameToAccessionIdMap,
        // add the strain and its synonyms to the database and to strainsMap and the strain name to strainNameToAccessionIdMap.

        if ((values[OFFSET_NAME]).contains("EUCOMM") && ( ! strainNameToAccessionIdMap.containsKey(values[OFFSET_NAME]))) {
            Strain strain = new Strain();
            try {
                strain.setBiotype(sqlLoaderUtils.getBiotype(values[OFFSET_STRAINTYPE]));
                strain.setId(new DatasourceEntityId("IMSR_EUCOMM_" + addedEucommStrainCount, DbIdType.MGI.intValue()));
                strain.setName(values[OFFSET_NAME]);
                List<Synonym> synonyms = new ArrayList<>();
                strain.setSynonyms(synonyms);

                for (String synonymName: synonymNames) {

                    if (synonymName.isEmpty()) {
                        continue;
                    }

                    Synonym synonym = new Synonym();
                    synonym.setSymbol(synonymName);
                    synonyms.add(synonym);

                    logger.info("[{}] Creating synonym '{}' for {}", lineNumber, synonym.getSymbol(), strain.getId().getAccession());

                    addedSynonymCount++;
                }

                if (strains == null) {
                    strains = new ArrayList<>();
                }
                strains.add(strain);
                strainNameToAccessionIdMap.put(strain.getName(), strain.getId().getAccession());
                strainsMap.put(strain.getId().getAccession(), strain);

                logger.info("[{}] Creating EUCOMM strain for {} with name '{}' and synonyms '[{}]'", lineNumber, strain.getId().getAccession(), strain.getName(), strain.toStringSynonyms());
                addedEucommStrainCount++;

            } catch (CdaLoaderException e) {
                System.out.println(e.getLocalizedMessage());
                errMessages.add(e.getLocalizedMessage());
            }
        }

        return strains;
    }

    public int getAddedEucommStrainCount() {
        return addedEucommStrainCount;
    }

    public int getAddedSynonymCount() {
        return addedSynonymCount;
    }
}