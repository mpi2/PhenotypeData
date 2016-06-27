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

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by mrelac on 24/06/16.
 */
public class StrainProcessorMgi implements ItemProcessor<Strain, Strain> {

    private      int                addedMgiStrainsCount = 0;
    public final Set<String>        errorMessages        = new HashSet<>();
    protected    int                lineNumber           = 0;
    protected    int                strainIsAlleleCount  = 0;
    private     Map<String, Strain> strainsMap;
    private     Map<String, Allele> allelesMap;

    private       Map<String, String> strainNameToAccessionIdMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);    // key = strain name (strainStock). Value = strain accession id with the same strain name

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    public StrainProcessorMgi(Map<String, Strain> strainsMap, Map<String, Allele> allelesMap) {
        this.strainsMap = strainsMap;
        this.allelesMap = allelesMap;
    }

    @Override
    public Strain process(Strain strain) throws Exception {
        Allele allele = allelesMap.get(strain.getId().getAccession());
        if (allele != null) {
            logger.warn("Strain {} is already in the allele table as allele {}", strain.toString(), allele);
            strainIsAlleleCount++;
            return null;
        } else if ( ! strainNameToAccessionIdMap.containsKey(strain.getName())) {

            // Fill in the missing common fields in preparation for writing to the database.
            strain.getId().setDatabaseId(DbIdType.MGI.intValue());

            // Call the remaining methods to finish setting the strain instance.
            OntologyTerm biotype = sqlLoaderUtils.getOntologyTerm(DbIdType.MGI.intValue(), strain.getBiotype().getName());
            if (biotype == null) {
                logger.warn("Line {} : NO biotype FOR strain {}.", lineNumber, strain.toString());
                return null;
            }
            strain.setBiotype(biotype);

            strainsMap.put(strain.getId().getAccession(), strain);
            strainNameToAccessionIdMap.put(strain.getName(), strain.getId().getAccession());

            addedMgiStrainsCount++;

            return strain;
        }

        return null;
    }

    public int getStrainIsAlleleCount() {
        return strainIsAlleleCount;
    }

    public int getAddedMgiStrainsCount() {
        return addedMgiStrainsCount;
    }

    public Set<String> getErrorMessages() {
        return errorMessages;
    }
}