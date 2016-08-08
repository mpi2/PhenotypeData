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

import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.Xref;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.CdabaseSqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by mrelac on 09/06/16.
 *
 * This class processes xref data from the older MGI files: MGI_EntrezGene.rpt, MRK_ENSEMBL.rpt, and MRK_VEGA.rpt.
 * In theory the data from these files should all be included in MGI_Gene.rpt (processed by MarkerProcessorXrefGenes).
 * However, as of 12-July-2016, many xrefs are missing from MGI_Gene.rpt.
 */
public class MarkerProcessorXrefOthers implements ItemProcessor<List<Xref>, List<Xref>> {

    public final Set<String>            errMessages = new HashSet<>();
    private Map<String, GenomicFeature> genes;
    private       int                   lineNumber  = 0;
    private final Logger                logger      = LoggerFactory.getLogger(this.getClass());
    private       int                   xrefsAdded;

    @Autowired
    @Qualifier("cdabaseLoaderUtils")
    private CdabaseSqlUtils cdabaseSqlUtils;


    public MarkerProcessorXrefOthers(Map<String, GenomicFeature> genomicFeatures) {
        this.genes = genomicFeatures;
        xrefsAdded = 0;
    }

    @Override
    public List<Xref> process(List<Xref> xrefs) throws Exception {

        lineNumber++;
        int added = 0;

        // If there are any Xref instances that don't already exist in the gene, add them to the gene.
        // NOTE: All xrefs injected here belong to the same gene.
        if ( ! xrefs.isEmpty()) {
            GenomicFeature gene = genes.get(xrefs.get(0).getAccession());
            if (gene == null) {
                return null;
            }
            if (gene.getXrefs() == null) {
                gene.setXrefs(new ArrayList<>());
            }

            // Only add missing xrefs and report the count of xrefs added.
            for (Xref newXref : xrefs) {
                if (contains(gene.getXrefs(), newXref)) {
                    continue;
                } else {
                    gene.getXrefs().add(newXref);
                    added++;
                }
            }
        }

        if (added > 0) {
            xrefsAdded += added;
            return xrefs;
        }

        return null;
    }

    private boolean contains(List<Xref> xrefs, Xref xref) {
        if ((xrefs == null) || (xref == null)) {
            return false;
        }
        if (xrefs.isEmpty()) {
            return false;
        }

        for (Xref anXref : xrefs) {
            if ((anXref.getXrefAccession().equals(xref.getXrefAccession())
            && (anXref.getXrefDatabaseId() == xref.getXrefDatabaseId())))
            {
                return true;
            }
        }

        return false;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }

    public Map<String, GenomicFeature> getGenes() {
        return genes;
    }

    public int getXrefsAdded() {
        return xrefsAdded;
    }
}