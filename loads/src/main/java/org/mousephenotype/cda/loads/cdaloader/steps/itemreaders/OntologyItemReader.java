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

package org.mousephenotype.cda.loads.cdaloader.steps.itemreaders;

import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.loads.cdaloader.OntologyParser;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.springframework.batch.item.adapter.ItemReaderAdapter;

import java.util.List;

/**
 * Created by mrelac on 03/05/16.
 */
public class OntologyItemReader extends ItemReaderAdapter<OntologyTerm> {

    private int dbId;
    private String filename;
    private String prefix;
    private List<OntologyTerm> terms = null;

    private int index = 0;


    /**
     * Initialise a new <code>OntologyItemReader</code> instance
     * @param filename the fully qualified filename where the resource will be downloaded to and from which the <code>
     *                 ItemReader</code> will read
     * @param dbId the ontology identifier (from the komp2 table 'external_db')
     * @param prefix the <code>OntologyParser</code> OWL prefix (e.g. PATO, MA, MP, etc.)
     */
    public void initialise(String filename, int dbId, String prefix) throws CdaLoaderException {
        this.filename = filename;
        this.dbId = dbId;
        this.prefix = prefix;

        try {
            terms = new OntologyParser(filename, prefix).getTerms();
System.out.println("FILENAME: " + filename);
System.out.println("PREFIX: " + prefix);
System.out.println("TERMS COUNT: " + terms.size());

        } catch (OWLOntologyCreationException e) {
            throw new CdaLoaderException(e);
        }
    }

    @Override
    public OntologyTerm read () {
        OntologyTerm term = null;

        if (index < terms.size())
            term = terms.get(index);

        index++;

        return term;
    }


    /**
     * Return the db id for this ontology (e.g. PATO = db id 7)
     *
     * @return the db id for this ontology (e.g. PATO = db id 7)
     */
    public int getDbId() {
        return dbId;
    }

    /**
     * Set the db id for this ontology (e.g. PATO = db id 7)
     *
     * @param dbId Return the db id for this ontology (e.g. PATO = db id 7)
     */
    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    /**
     * Return the fully-qualified OWL source file name
     *
     * @return the fully-qualified OWL source file name
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set the fully-qualified OWL source file name
     *
     * @param filename the fully-qualified OWL source file name
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Return the <code>OntologyParser</code> OWL prefix (e.g. PATO, MA, MP, etc.)
     *
     * @return the <code>OntologyParser</code> OWL prefix (e.g. PATO, MA, MP, etc.)
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set the <code>OntologyParser</code> OWL prefix (e.g. PATO, MA, MP, etc.)
     *
     * @param prefix the <code>OntologyParser</code> OWL prefix (e.g. PATO, MA, MP, etc.)
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}