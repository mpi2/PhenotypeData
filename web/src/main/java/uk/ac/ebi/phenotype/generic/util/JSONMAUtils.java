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
package uk.ac.ebi.phenotype.generic.util;

import java.util.Collection;
import java.util.HashSet;

public class JSONMAUtils {

	/**
	 * returns a Collection of Strings with multiples/duplicates removed
	 * @param childTermStrings
	 * @return
	 */
	private static Collection<String> getDistinct(Collection<String> childTermStrings) {
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(childTermStrings);
		childTermStrings.clear();
		childTermStrings.addAll(hs);
		return childTermStrings;
	}

}
