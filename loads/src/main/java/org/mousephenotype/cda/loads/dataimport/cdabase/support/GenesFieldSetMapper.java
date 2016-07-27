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

package org.mousephenotype.cda.loads.dataimport.cdabase.support;

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.loads.dataimport.cdabase.steps.MarkerProcessorGenes;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by mrelac on 24/06/16.
 */
public class GenesFieldSetMapper implements FieldSetMapper<GenomicFeature> {

    private CommonUtils commonutils = new CommonUtils();
    
    /**
     * Method used to map data obtained from a {@link FieldSet} into an object.
     *
     * @param fs the {@link FieldSet} to map
     * @throws BindException if there is a problem with the binding
     *
     * RULES:
     * - The first line of the file contains the headings, so GenomicFeature components that are not strings must do something sensible.
     * - The headings are validated in the processor.
     * - Only rows of type 'Gene' and 'Pseudogene' qualify.
     * - Exclude rows of status 'W'.
     * - If strand is "+" in the file, set the GenomicFeature strand field to 1; otherwise, set it to -1.
     * - The column 'Feature Type', if not empty, is the gene subtype.
     * - We cannot handle column 'Feature Type' containing a "|" and throw an exception if one is found.
     */
    @Override
    public GenomicFeature mapFieldSet(FieldSet fs) throws BindException {

        OntologyTerm biotype = new OntologyTerm();
        biotype.setName(fs.readString("biotype"));

        Integer start = commonutils.tryParseInt(fs.readString("start"));
        if (start == null) {
            if (fs.readString("start").equals("genome coordinate start")) {
                start = MarkerProcessorGenes.START_COLUMN_SENTINAL;
            } else {
                start = 0;
            }
        }

        Integer end = commonutils.tryParseInt(fs.readString("end"));
        if (end == null) {
            if (fs.readString("end").equals("genome coordinate end")) {
                end = MarkerProcessorGenes.END_COLUMN_SENTINAL;
            } else {
                end = 0;
            }
        }

        DatasourceEntityId dsIdGene = new DatasourceEntityId();
        dsIdGene.setAccession(fs.readString("mgiMarkerAccessionId"));

        SequenceRegion sequenceRegion = new SequenceRegion();
        sequenceRegion.setName(fs.readString("chromosome"));

        int strand;
        if (fs.readString("strand").equals("strand")) {
            strand = MarkerProcessorGenes.STRAND_COLUMN_SENTINAL;
        } else {
            strand = fs.readString("strand").equals("+") ? 1 : -1;
        }

        // subtype is optional.
        OntologyTerm subtype = new OntologyTerm();
        try {
            subtype.setName(fs.readString("subtype"));
        } catch (Exception e) {
            subtype = null;
        }

        List<Synonym> synonyms = new ArrayList<>();
        // Synonyms are optional.
        if (Arrays.asList(fs.getNames()).contains("synonyms")) {
            try {
                String[] synonymsArray = fs.readString("synonyms").split(Pattern.quote("|"));  // Optional field that may throw IndexOutOfBoundsException
                for (String synonymSymbol : synonymsArray) {
                    if (synonymSymbol.isEmpty())
                        continue;
                    Synonym synonym = new Synonym();
                    synonym.setSymbol(synonymSymbol);
                    synonyms.add(synonym);
                }
            } catch (Exception e) { }
        }

        List<Xref> xrefs = new ArrayList<>();

        GenomicFeature gene = new GenomicFeature();

        gene.setBiotype(biotype);
        gene.setcMposition(fs.readString("cMposition"));
        gene.setStart(start);
        gene.setEnd(end);
        gene.setId(dsIdGene);
        gene.setName(fs.readString("name"));
        gene.setSequenceRegion(sequenceRegion);
        gene.setStatus(fs.readString("status"));
        gene.setStrand(strand);
        gene.setSubtype(subtype);
        gene.setSymbol(fs.readString("symbol"));
        gene.setSynonyms(synonyms);
        gene.setXrefs(xrefs);

        return gene;
    }
}