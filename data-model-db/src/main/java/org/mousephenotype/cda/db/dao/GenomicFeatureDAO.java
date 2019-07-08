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
 * Genomic feature data access manager interface.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */


import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mousephenotype.cda.db.pojo.GenomicFeature;


public interface GenomicFeatureDAO extends HibernateDAO {

	/**
	 * Get all genomic feature
	 * @return all coordinate system
	 */

	GenomicFeature getGenomicFeatureByAccession(String accession);
	GenomicFeature getGenomicFeatureByAccessionAndDbId(String accession, int dbId);
}