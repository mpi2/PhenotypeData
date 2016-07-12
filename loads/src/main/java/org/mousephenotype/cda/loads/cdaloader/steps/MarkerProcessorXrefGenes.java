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

import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.Xref;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mrelac on 09/06/16.
 */
public class MarkerProcessorXrefGenes implements ItemProcessor<List<Xref>, List<Xref>> {

    public final Set<String>            errMessages = new HashSet<>();
    private Map<String, GenomicFeature> genes;
    private       int                   lineNumber  = 0;
    private final Logger                logger      = LoggerFactory.getLogger(this.getClass());
    private       int                   xrefsAdded  = 0;

    private final String[] expectedHeadings = new String[]{
              "MGI Accession ID"        // A
            , "Marker Symbol"           // B - (unused)
            , "Marker Name"             // C - (unused)
            , "Feature Type"            // D - (unused)
            , "EntrezGene ID"           // E
            , "NCBI Gene chromosome"    // F - (unused)
            , "NCBI Gene start"         // G - (unused)
            , "NCBI Gene end"           // H - (unused)
            , "NCBI Gene strand"        // I - (unused)
            , "Ensembl Gene ID"         // J
            , "Ensembl Gene chromosome" // K - (unused)
            , "Ensembl Gene start"      // L - (unused)
            , "Ensembl Gene end"        // M - (unused)
            , "Ensembl Gene strand"     // N - (unused)
            , "VEGA Gene ID"            // O
            , "VEGA Gene chromosome"    // P - (unused)
            , "VEGA Gene start"         // Q - (unused)
            , "VEGA Gene end"           // R - (unused)
            , "VEGA Gene strand"        // S - (unused)
            , "CCDS IDs"                // T
            , "HGNC ID"                 // U - (unused)
            , "HomoloGene ID"           // V - (unused)
    };

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    public MarkerProcessorXrefGenes(Map<String, GenomicFeature> genomicFeatures) {
        this.genes = genomicFeatures;
    }

    @Override
    public List<Xref> process(List<Xref> xrefs) throws Exception {

        lineNumber++;

        // Validate the file using the heading names.
        // xref[0] = entrez. xref[1] = ensembl. xref[2] = vega. xref[3] = ccds.
        if (lineNumber == 1) {
            String[] actualHeadings = new String[] {
                  xrefs.get(0).getAccession()       // A
                , "Marker Symbol"                   // B - (unused)
                , "Marker Name"                     // C - (unused)
                , "Feature Type"                    // D - (unused)
                , xrefs.get(0).getXrefAccession()   // E
                , "NCBI Gene chromosome"            // F - (unused)
                , "NCBI Gene start"                 // G - (unused)
                , "NCBI Gene end"                   // H - (unused)
                , "NCBI Gene strand"                // I - (unused)
                , xrefs.get(1).getXrefAccession()   // J
                , "Ensembl Gene chromosome"         // K - (unused)
                , "Ensembl Gene start"              // L - (unused)
                , "Ensembl Gene end"                // M - (unused)
                , "Ensembl Gene strand"             // N - (unused)
                , xrefs.get(2).getXrefAccession()   // O
                , "VEGA Gene chromosome"            // P - (unused)
                , "VEGA Gene start"                 // Q - (unused)
                , "VEGA Gene end"                   // R - (unused)
                , "VEGA Gene strand"                // S - (unused)
                , xrefs.get(3).getXrefAccession()   // T
                , "HGNC ID"                         // U - (unused)
                , "HomoloGene ID"                   // V - (unused)
            };

            for (int i = 0; i < expectedHeadings.length; i++) {
                if ( ! expectedHeadings[i].equals(actualHeadings[i])) {
                    throw new CdaLoaderException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
                }
            }

            return null;
        }

        // If there are any Xref instances, add them to the gene.
        if ( ! xrefs.isEmpty()) {
            GenomicFeature gene = genes.get(xrefs.get(0).getAccession());
            if (gene == null) {
//                logger.error("Line {}: no gene for xref {}.", lineNumber, xrefs.get(0));
                return null;
            }

            gene.getXrefs().addAll(xrefs);
            xrefsAdded += xrefs.size();

            return xrefs;
        }

        return null;
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