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

import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Synonym;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.OntologyParser;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.List;

/**
 * Implements an <code>ItemReader</code> that feeds a single ontology OWL XML file to an <code>OntologyParser</code>,
 * which returns a list of <code>OntologyTerm</code> that is iterated over by the read() method called by spring batch.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class OntologyReader implements ItemReader<OntologyTerm> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int dbId;
    private String sourceFilename;
    private String prefix;
    private int readIndex = 0;
    private List<OntologyTerm> terms = null;


    public OntologyReader(String sourceFilename, int dbId, String prefix) throws DataImportException {
        this.sourceFilename = sourceFilename;
        this.dbId = dbId;
        this.prefix = prefix;
    }

    /**
     * Reads a piece of input data and advance to the next one. Implementations
     * <strong>must</strong> return <code>null</code> at the end of the input
     * data set. In a transactional setting, caller might get the same item
     * twice from successive calls (or otherwise), if the first call was in a
     * transaction that rolled back.
     *
     * @return T the item to be processed
     * @throws ParseException                if there is a problem parsing the current record
     *                                       (but the next one may still be valid)
     * @throws NonTransientResourceException if there is a fatal exception in
     *                                       the underlying resource. After throwing this exception implementations
     *                                       should endeavour to return null from subsequent calls to read.
     * @throws UnexpectedInputException      if there is an uncategorised problem
     *                                       with the input data. Assume potentially transient, so subsequent calls to
     *                                       read might succeed.
     * @throws Exception                     if an there is a non-specific error.
     */
    @Override
    public OntologyTerm read() throws DataImportException {
        OntologyTerm term = null;

        if (terms == null) {
            try {
                terms = new OntologyParser(sourceFilename, prefix).getTerms();
                logger.info("");
                logger.info("FILENAME: {}. PREFIX: {}. TERMS COUNT: {}.",
                            sourceFilename, prefix, terms.size());

            } catch (OWLOntologyCreationException e) {

                throw new DataImportException(e);
            }
        }

        if (readIndex < terms.size()) {
            term = terms.get(readIndex);
            term.setId(new DatasourceEntityId(term.getId().getAccession(), dbId));
            if (term.getSynonyms() != null) {
                for (Synonym synonym : term.getSynonyms()) {
                    synonym.setAccessionId(term.getId().getAccession());
                    synonym.setDbId(term.getId().getDatabaseId());
                }
            }
        }

        readIndex++;

        return term;
    }
}