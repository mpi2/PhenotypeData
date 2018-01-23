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

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.GenomicFeature;

import java.util.Map;

/**
 * Created by mrelac on 09/06/16.
 */
public class AlleleProcessorQtl extends AlleleProcessorAbstract {

    @Override
    public Allele setBiotype(Allele allele) {
        return super.setBiotypeSkipAlleleIfNoBiotypeFound(allele);
    }

    @Override
    public Allele setGene(Allele allele) {
        return super.setGeneNullIsOk(allele);
    }

    public AlleleProcessorQtl(Map<String, GenomicFeature> genes) {
        super(genes);
    }

    @Override
    public Allele process(Allele allele) throws Exception {
        allele = super.process(allele);

//        if (lineNumber % 1000 == 0) {
//            System.out.println("Thread " + Thread.currentThread().getName() + ": QTL: " + lineNumber);
//            Thread.sleep(500);
//        }

        return allele;
    }
}