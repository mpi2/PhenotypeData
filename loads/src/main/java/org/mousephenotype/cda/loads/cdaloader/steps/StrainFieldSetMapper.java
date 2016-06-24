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

import org.mousephenotype.cda.db.pojo.*;
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
public class StrainFieldSetMapper implements FieldSetMapper<Strain> {

    /**
     * Method used to map data obtained from a {@link FieldSet} into an object.
     *
     * @param fs the {@link FieldSet} to map
     * @throws BindException if there is a problem with the binding
     */
    @Override
    public Strain mapFieldSet(FieldSet fs) throws BindException {
        Strain             strain     = new Strain();
        DatasourceEntityId dsIdStrain = new DatasourceEntityId();
        dsIdStrain.setAccession(fs.readString("mgiStrainAccessionId"));

        OntologyTerm biotype = new OntologyTerm();
        try {
            biotype.setName(fs.readString("biotype"));                      // Optional field that may throw IndexOutOfBoundsException
        } catch (Exception e) {
            biotype = null;
        }

        List<Synonym> synonyms = new ArrayList<>();
        // Synonyms are optional.
        if (Arrays.asList(fs.getNames()).contains("synonyms")) {
            try {
                String[] synonymsArray = fs.readString("synonyms").split(Pattern.quote(","));  // Optional field that may throw IndexOutOfBoundsException
                for (String synonymSymbol : synonymsArray) {
                    if (synonymSymbol.isEmpty())
                        continue;
                    Synonym synonym = new Synonym();
                    synonym.setSymbol(synonymSymbol);
                    synonyms.add(synonym);
                }
            } catch (Exception e) { }
        }

        strain.setId(dsIdStrain);
        strain.setBiotype(biotype);
        strain.setName(fs.readString("name"));
        strain.setSynonyms(synonyms);

        return strain;
    }
}