/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.ri.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class mimics the {@link Summary} class, replacing the {@link List<Gene>} with a {@link List<GeneWithDecoration>}
 * that indicates whether or not the gene status element should be decorated to show that the gene status has changed.
 */
public class SummaryWithDecoration extends Summary {

    public SummaryWithDecoration(Summary summary, Map<String, GeneSent> genesSentByGeneAccessionId) {

        this.emailAddress = summary.getEmailAddress();

        List<Gene> genesWithDecoration = new ArrayList<>();
        for (Gene gene : summary.getGenes()) {
            GeneWithDecoration geneWithDecoration = new GeneWithDecoration(gene, genesSentByGeneAccessionId.get(gene.getMgiAccessionId()));
            genesWithDecoration.add(geneWithDecoration);
        }

        this.genes = genesWithDecoration;
    }

    public boolean isDecorated() {
        if ((genes == null) || (genes.isEmpty())) {
            return false;
        }

        for (Gene gene : genes) {
            if (gene instanceof GeneWithDecoration) {
                if (((GeneWithDecoration) gene).isDecorated()) {
                    return true;
                }
            }
        }

        return false;
    }
}