/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.db.dao;

/**
 *
 * Ontology term and controlled vocabulary data access manager interface.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */


import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.web.WebStatus;

import java.util.Collection;
import java.util.List;


public interface OntologyTermDAO extends HibernateDAO, WebStatus{

	List<OntologyTerm> getAllOntologyTerms();

	OntologyTerm getOntologyTermByName(String name);

	OntologyTerm getOntologyTermByAccession(String accession);

	OntologyTerm getOntologyTermByAccessionAndDatabaseId(String accession, int databaseId);

	int batchInsertion(Collection<OntologyTerm> ontologyTerms);
}