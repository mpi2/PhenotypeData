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
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.SequenceRegion;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

/**
 * Created by mrelac on 09/06/16.
 */
public class AlleleProcessorQtl extends AlleleProcessorAbstract {

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;

    public AlleleProcessorQtl(Map<String, GenomicFeature> genomicFeatures, Map<String, OntologyTerm> featureTypes, Map<String, SequenceRegion> sequenceRegions) {
        super(genomicFeatures, featureTypes, sequenceRegions);
    }

    @Override
    public Allele process(Allele allele) throws Exception {
        super.process(allele);

//        if (lineNumber % 1000 == 0) {
//            System.out.println("Thread " + Thread.currentThread().getName() + ": QTL: " + lineNumber);
//            Thread.sleep(500);
//        }

        return allele;
    }
}