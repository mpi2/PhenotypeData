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
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the business processing rules for loading the imsr strains.
 * Created by mrelac on 03/06/16.
 */
public class StrainProcessorImsr implements ItemProcessor<FieldSet, List<Strain>> {

    private       int    lineNumber = 0;
    private final Logger logger     = LoggerFactory.getLogger(this.getClass());
    private List<String> mgiStrainAccessionIds = new ArrayList<>();

    // The following IMSR_ ints define the column offset of the given column in the report.txt file.
    public final static int OFFSET_ACCESSION_ID = 1;
    public final static int OFFSET_NAME         = 2;
    public final static int OFFSET_SYNONYMS     = 5;
    public final static int OFFSET_STRAINTYPE   = 6;
    public final static int OFFSET_MAX          = OFFSET_STRAINTYPE;    // Points to the highest value offset.

    // The following  ints define the column offset of the given column in the report.txt file.
    public final static String HEADING_ACCESSION_ID = "Strain ID";
    public final static String HEADING_NAME         = "Strain/Stock";
    public final static String HEADING_SYNONYMS     = "Synonyms";
    public final static String HEADING_STRAINTYPE   = "Type";

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    @PostConstruct
    public void initialise() throws Exception {

        // Populate mgi strain list.
        mgiStrainAccessionIds.addAll(sqlLoaderUtils.getStrainAccessionIds());
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
        int addedStrainCount = 0;
        int    addedSynonymCount = 0;
        String accessionId;
        String synonymCell;
        String strainType;

        List<Strain> strains = null;

        lineNumber++;

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
            if (mgiStrainAccessionIds.contains(accessionId)) {
                return null;
            }

        } catch (IndexOutOfBoundsException e) {
            logger.warn("StrainProcessorImsr(): report.txt Line " + lineNumber + " doesn't have a " + HEADING_ACCESSION_ID + " value. Skipping. Line: '" + StringUtils.join(item, ",") + "'.");
            return null;
        }

        // For every synonym that matches an existing mgi strain accession id, add the synonym to that strain's synonym list and add that strain to the list of strains returned.
        try {
            synonymCell = values[OFFSET_SYNONYMS];
            String[] synonyms = synonymCell.split(",");
            boolean synonymFound = false;
            for (String synonym: synonyms) {
                if (mgiStrainAccessionIds.contains(synonym)) {
                    synonymFound = true;
                    addedSynonymCount++;

//                    Strain strain = sqlLoaderUtils.getStrain(synonym, DbIdType.MGI.intValue());
//                    strain.addSynonym();
//                    strains.add(strain);

                    // Add strain to mgiStrainAccessionIds.


                }
            }
            if (synonymFound) {
                Strain strain = new Strain();

//                strain = sqlLoaderUtils.getStrainByName()
//
//                strain.setBiotype(sqlLoaderUtils.getBiotype(values[STRAINTYPE_OFFSET]));
//                strain.setId((new DatasourceEntityId(values[S])));
            }

        } catch (IndexOutOfBoundsException e) {
            // There are no synonyms; thus, no strain or synonyms to add.
            return null;
        }





//            try {
//                strain.setBiotype(sqlLoaderUtils.getBiotype(values[STRAINTYPE_OFFSET]));
//            } catch (CdaLoaderException e) {
//                System.out.println(e.getLocalizedMessage());
//            }



        return strains;
    }
}
