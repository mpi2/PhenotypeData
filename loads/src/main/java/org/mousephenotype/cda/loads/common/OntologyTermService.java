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

package org.mousephenotype.cda.loads.common;

import org.mousephenotype.cda.db.pojo.OntologyTerm;

import java.util.List;

/**
 * Given a list of {@link OntologyTerm} , provides services against that list to:
 * <ul>
 *     <li>return a term (returns null if the term was not found)</li>
 *     <li>return the latest termm (returns null if the term was not found)</li>
 *     <li>Add a term to the list (which updates all the internal maps)</li>
 * </ul>
 *
 */
public class OntologyTermService {

    private List<OntologyTerm> terms;

    public OntologyTermService(List<OntologyTerm> terms) {
        this.terms = terms;
    }


    public OntologyTerm getTermByAccessionId(String accessionId) {

    }

    public OntologyTerm getTermByName(String name) {

    }





}
