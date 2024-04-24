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
package org.mousephenotype.cda.enumerations;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SignificantType {
	none("No significant change"),
	cannot_classify("Can not differentiate genders"),
	both_equally("Both genders equally"),
	female_only("Female only"),
	male_only("Male only"),
	female_greater("Different effect size, females greater"),
	male_greater("Different effect size, males greater"),
	different_directions("Female and male different directions"),
	one_genotype_tested("If phenotype is significant it is for the one genotype tested"),
	one_sex_tested("If phenotype is significant it is for the one sex tested"),
	significant("Significant")
	;

	private final String text;
	private static final HashMap<SignificantType, String> tag_patterns = new HashMap<>();

	static {
		tag_patterns.put(none, "(Not significant.*)|(.*no significant change)|(.*significant in combined dataset only.*)");
		tag_patterns.put(one_sex_tested, "With phenotype threshold value 1e-04 - significant for the .* sex \\(.*\\) tested \\(.*\\)");
		tag_patterns.put(male_only, "With phenotype threshold value 1e-04 - significant in males.*");
		tag_patterns.put(male_greater, "With phenotype threshold value 1e-04 - different size as males greater");
		tag_patterns.put(female_only, "With phenotype threshold value 1e-04 - significant in females.*");
		tag_patterns.put(female_greater, "With phenotype threshold value 1e-04 - different size as females greater");
		tag_patterns.put(different_directions, "With phenotype threshold value 1e-04 - different direction for the sexes");
		tag_patterns.put(both_equally, "(With phenotype threshold value 1e-04 - both sexes equally) | (With phenotype threshold value 1e-04 - cannot classify effect \\[Interaction pvalue = .*, Genotype Female pvalue = .* Genotype Male pvalue = .*\\]) | (With phenotype threshold value 1e-04 - regardless of gender) | (With phenotype threshold value 1e-04 - significant in males \\(.*\\) and females \\(.*\\) datasets) | (With phenotype threshold value 1e-04 - significant in males \\(.*\\), females \\(.*\\) and in combined dataset \\(.*\\))|(With phenotype threshold value 1e-04 - significant in males, females and in combined dataset)");
	}

	SignificantType(String text) {
		this.text = text;
	}

	public String toString() {
		return text;
	}

	/**
	 * convenience method for bean access from jsp
	 * @return
	 */
	public String getText(){
		return this.text;
	}

	public static SignificantType fromString(String text) {
		if (text != null) {
			for (SignificantType b : SignificantType.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}

	public static SignificantType getValue(String databaseSignificanceType, Boolean isSignificant){

		for(SignificantType type : tag_patterns.keySet()){
			Pattern pattern = Pattern.compile(tag_patterns.get(type));
			Matcher matcher = pattern.matcher(databaseSignificanceType);
			if(matcher.matches()){
				return type == none && isSignificant ? significant : type;
			}
		}
		return null;
	}

}
