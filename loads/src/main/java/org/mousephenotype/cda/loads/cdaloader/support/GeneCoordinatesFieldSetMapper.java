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

package org.mousephenotype.cda.loads.cdaloader.support;

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrelac on 24/06/16.
 */
public class GeneCoordinatesFieldSetMapper implements FieldSetMapper<GenomicFeature> {

    private CommonUtils commonutils = new CommonUtils();
    
    /**
     * Method used to map data obtained from a {@link FieldSet} into an object.
     *
     * @param fs the {@link FieldSet} to map
     * @throws BindException if there is a problem with the binding
     *
     * RULES:
     * - Only rows of type 'Gene' and 'Pseudogene' qualify.
     * - If strand is "+" in the file, set the GenomicFeature strand field to 1; otherwise, set it to -1.
     * - The last column is always provided and is comprised of from one to three strings, delimited by a semicolon,
     *   with the values "ID=mgiMarkerAccessionId;Name=symbol;Note=markerFeature (i.e. Feature Type or subtype)
     * -delimited string containing
     * - The column 'Feature Type', if not empty, is the gene subtype.
     */
    @Override
    public GenomicFeature mapFieldSet(FieldSet fs) throws BindException {

        ParsedComposite parsedComposite = null;
        String chromosome = fs.readString("chromosome").replace("chr", "");
        String mgiMarkerAccessionId = null;
        String symbol = null;
        String rawSubtype = null;
        try {
            parsedComposite = new ParsedComposite(fs.readString("composite"));
            mgiMarkerAccessionId = parsedComposite.mgiMarkerAccessionId;
            symbol = (parsedComposite.symbol == null ? null : parsedComposite.symbol);
            rawSubtype = (parsedComposite.subtype == null ? "unknown" : parsedComposite.subtype);
        } catch (CdaLoaderException e) {
            throw new RuntimeException(e);
        }

        OntologyTerm biotype = new OntologyTerm();
        biotype.setName(fs.readString("biotype"));

        Integer start = commonutils.tryParseInt(fs.readString("start"));
        if (start == null) {
            start = 0;
        }

        Integer end = commonutils.tryParseInt(fs.readString("end"));
        if (end == null) {
            end = 0;
        }

        DatasourceEntityId dsIdGene = new DatasourceEntityId();
        dsIdGene.setAccession(mgiMarkerAccessionId);

        SequenceRegion sequenceRegion = new SequenceRegion();
        sequenceRegion.setName(chromosome);

        int strand = fs.readString("strand").equals("+") ? 1 : -1;

        // subtype is optional.
        OntologyTerm subtype = new OntologyTerm();
        try {
            subtype.setName(rawSubtype);
        } catch (Exception e) {
            subtype = null;
        }

        List<Synonym> synonyms = new ArrayList<>();
        List<Xref> xrefs = new ArrayList<>();

        GenomicFeature gene = new GenomicFeature();

        gene.setBiotype(biotype);
        gene.setStart(start);
        gene.setEnd(end);
        gene.setId(dsIdGene);
        gene.setSequenceRegion(sequenceRegion);
        gene.setStrand(strand);
        gene.setSubtype(subtype);
        gene.setSymbol(symbol);
        gene.setSynonyms(synonyms);
        gene.setXrefs(xrefs);

        return gene;
    }

    private class ParsedComposite {
        private String mgiMarkerAccessionId = null;
        private String symbol               = null;
        private String subtype              = null;

        /*
        * Lines come in the form "ID=MGIxxxxxxx;Name=yyyyy;Note=zzzzz" where ID is the markerAccessionId, Name is the markerSymbol, and Note is the markerFeature (not used).
        * ID is on every line in the file. Name and Note are optional. The three tokens always seem to appear in the
        * same order: ID (always), then Name (if it exists), then Note (if it exists). You can't split on ";" because
        * some names contain ";", and a Note might as well (though I haven't seen one).
        *
        * Extract the ID=MGI:xxxxxxx and remove it from the string.
         */
        public ParsedComposite(String composite) throws CdaLoaderException {
            if ((composite == null) || (composite.isEmpty()))
                return;

            String tmp      = composite;
            int    endIndex = tmp.indexOf(";");

            if (endIndex == -1) {
                mgiMarkerAccessionId = tmp.substring(3);
                return;
            }
            mgiMarkerAccessionId = tmp.substring(3, endIndex);

            tmp = tmp.replace("ID=" + mgiMarkerAccessionId, "");
            if (tmp.isEmpty()) {
                return;
            }

            if (tmp.startsWith(";Name=")) {
                tmp = tmp.replace(";Name=", "");
                endIndex = tmp.indexOf(";Note=");
                if (endIndex == -1) {
                    symbol = tmp;
                    return;
                } else {
                    symbol = tmp.substring(0, endIndex);
                    tmp = tmp.replace(symbol + ";Note=", "");
                    subtype = tmp;
                }
            }
        }
    }
}