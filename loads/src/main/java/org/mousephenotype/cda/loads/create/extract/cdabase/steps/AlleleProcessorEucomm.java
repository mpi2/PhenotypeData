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
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

/**
 * Created by mrelac on 09/06/16.
 */
public class AlleleProcessorEucomm extends AlleleProcessorAbstract {

    @Autowired
    @Qualifier("cdabaseSqlUtils")
    private CdaSqlUtils cdaSqlUtils;

    @Override
    public Allele setBiotype(Allele allele) throws DataLoadException {
        return super.setBiotypeMouseMutants(allele);
    }

    @Override
    public Allele setGene(Allele allele) throws DataLoadException {
        return super.setGeneNullMeansAddWithdraw(allele);
    }

    public AlleleProcessorEucomm(Map<String, GenomicFeature> genes) {
        super(genes);
    }

    @Override
    public Allele process(Allele allele) throws Exception {
        allele = super.process(allele);

//        if (lineNumber % 10000 == 0) {
//            System.out.println("Thread " + Thread.currentThread().getName() + ": PHENOTYPIC: " + lineNumber);
//            Thread.sleep(1000);
//        }

        return allele;
    }
}