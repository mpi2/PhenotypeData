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

package org.mousephenotype.cda.loads.create.extract.cdabase.support;

import org.mousephenotype.cda.db.pojo.Xref;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mrelac on 24/06/16.
 */
public class XrefFieldSetMapper implements FieldSetMapper<List<Xref>> {

    private CommonUtils commonutils = new CommonUtils();
    
    /**
     * Method used to map data obtained from a {@link FieldSet} into an object.
     *
     * @param fs the {@link FieldSet} to map
     * @throws BindException if there is a problem with the binding
     *
     * RULES: CCSC IDs can appear
     */
    @Override
    public List<Xref> mapFieldSet(FieldSet fs) throws BindException {

        List<Xref> xrefs = new ArrayList<>();

        String rawId;
        if (Arrays.asList(fs.getNames()).contains("entrezId")) {
            rawId = fs.readString("entrezId");

            if ((rawId != null) && (!rawId.isEmpty()) && (!rawId.toLowerCase().equals("null"))) {
                String[] ids = rawId.split(",");
                for (String id : ids) {
                    Xref entrez = new Xref();
                    entrez.setAccession(fs.readString("mgiMarkerAccessionId"));
                    entrez.setDatabaseId(DbIdType.MGI.intValue());
                    entrez.setXrefAccession(id);
                    entrez.setXrefDatabaseId(DbIdType.EntrezGene.intValue());
                    xrefs.add(entrez);
                }
            }
        }

        if (Arrays.asList(fs.getNames()).contains("ensemblId")) {
            rawId = fs.readString("ensemblId");

            if ((rawId != null) && (!rawId.isEmpty()) && (!rawId.toLowerCase().equals("null"))) {
                String[] ids = rawId.split(",");
                for (String id : ids) {
                    Xref ensembl = new Xref();
                    ensembl.setAccession(fs.readString("mgiMarkerAccessionId"));
                    ensembl.setDatabaseId(DbIdType.MGI.intValue());
                    ensembl.setXrefAccession(id);
                    ensembl.setXrefDatabaseId(DbIdType.Ensembl.intValue());
                    xrefs.add(ensembl);
                }
            }
        }

        if (Arrays.asList(fs.getNames()).contains("vegaId")) {
            rawId = fs.readString("vegaId");

            if ((rawId != null) && (!rawId.isEmpty()) && (!rawId.toLowerCase().equals("null"))) {
                String[] ids = rawId.split(",");
                for (String id : ids) {
                    Xref vega = new Xref();
                    vega.setAccession(fs.readString("mgiMarkerAccessionId"));
                    vega.setDatabaseId(DbIdType.MGI.intValue());
                    vega.setXrefAccession(id);
                    vega.setXrefDatabaseId(DbIdType.VEGA.intValue());
                    xrefs.add(vega);
                }
            }
        }

        if (Arrays.asList(fs.getNames()).contains("ccdsId")) {
            rawId = fs.readString("ccdsId");

            if ((rawId != null) && (!rawId.isEmpty()) && (!rawId.toLowerCase().equals("null"))) {
                String[] ids = rawId.split(",");
                for (String id : ids) {
                    Xref ccds = new Xref();
                    ccds.setAccession(fs.readString("mgiMarkerAccessionId"));
                    ccds.setDatabaseId(DbIdType.MGI.intValue());
                    ccds.setXrefAccession(id);
                    ccds.setXrefDatabaseId(DbIdType.cCDS.intValue());
                    xrefs.add(ccds);
                }
            }
        }

        return xrefs;
    }
}