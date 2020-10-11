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

/**
 * Simple enumeration of zygosity
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * 
 */

public enum ZygosityType {

	homozygote,
	heterozygote,
	hemizygote,
	anzygote,
	not_applicable;
	
	public String getName(){
		return this.toString();
	}

	public String getShortName(){
		
		if (this.getName().equals(not_applicable.toString())){
			return "N/A";
		} else {
			return this.toString().substring(0, 3).toUpperCase();
		}
	}

	public static ZygosityType getByDisplayName(String displayName) {
		switch (displayName) {
			case "homozygote":
				return ZygosityType.homozygote;
			case "heterozygote":
				return ZygosityType.heterozygote;
			case "hemizygote":
				return ZygosityType.hemizygote;
			case "anzygote":
				return ZygosityType.anzygote;
			case "not applicable":
				return ZygosityType.not_applicable;
			default:
				throw new IllegalArgumentException("No enum constant " + SexType.class + "." + displayName);
		}
	}

}
