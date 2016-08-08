package org.mousephenotype.cda.loads.legacy.dccimport;
/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.db.dao.StrainDAO;
import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.Strain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * $HeadURL: https://svn.ebi.ac.uk/komp2/trunk/AdminTools/src/main/java/uk/ac/ebi/phenotype/data/europhenome/StrainNames.java
 * $ Id: $Id: StrainNames.java 5529 2016-01-08 15:54:36Z jmason $ Last changed: $LastChangedDate: 2016-01-08 15:54:36
 * +0000 (Fri, 08 Jan 2016) $ Last changed by: $LastChangedBy: jmason $
 * <p>
 * StrainNames is a curated set of strains for EuroPhenome data.
 *
 * @since February 2012
 */

public final class StrainNames {

	private static final Logger logger = LoggerFactory.getLogger(StrainNames.class);

	public static final Map<String, String> STRAIN_MAPPING = new HashMap<>();
	public static final Map<String, String> BACKGROUND_MAPPING = new HashMap<>();

	static {

		/**
		 * as curated by Karen Pickford Some of the keys map to themselves but
		 * there is no assignment in IMSR.
		 */

		STRAIN_MAPPING.put("B6NDen;B6N-Snip1<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Snip1<tm1a(EUCOMM)Wtsi>/H");
		STRAIN_MAPPING.put("B6NTac;B6N-Tmem167<tm1a(EUCOMM)Hmgu>/H", "B6NTac;B6N-Tmem167<tm1a(EUCOMM)Hmgu>/H");
		STRAIN_MAPPING.put("B6N;B6N-Nsun2<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyrc-Brd Nsun2tm1a(EUCOMM)Wtsi/WtsiOulu");
		STRAIN_MAPPING.put("B6N;B6N-Zranb2<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyrc-Brd Zranb2tm1a(EUCOMM)Wtsi/WtsiCnbc");
		STRAIN_MAPPING.put("C57BL/6NTac-Ddx27<tm1a(KOMP)Wtsi>/H", "C57BL/6NTac-Ddx27<tm1a(KOMP)Wtsi>/H");
		STRAIN_MAPPING.put("STOCK Vangl2<Lp>/H", "STOCK Vangl2<Lp>/H");
		STRAIN_MAPPING.put("B6N;B6N-Sparc<tm2a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyrc-Brd Sparctm1a(EUCOMM)Wtsi/WtsiCnbc");
		STRAIN_MAPPING.put("B6NDen;B6N-Fam83g<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Fam83g<tm1a(EUCOMM)Wtsi>/H");

		/**
		 * As curated by myself but double-checked by Karen.
		 */

		STRAIN_MAPPING.put("B6NDen;B6N-Uhrf1tm1a(EUCOMM)Wtsi/Ieg", "B6Dnk;B6N-Uhrf1<tm1a(EUCOMM)Wtsi>/Ieg");
		STRAIN_MAPPING.put("B6NDnk;B6N-Elk4<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Elk4<tm1a(EUCOMM)Wtsi>/H");
		STRAIN_MAPPING.put("B6NDen;B6N-Zfp704<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Zfp704<tm1a(EUCOMM)Wtsi>/H");
		STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Arpc4<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Arpc4<tm1a(EUCOMM)Wtsi>/WtsiOulu");
		STRAIN_MAPPING.put("B6NDen;B6N-Trnt1<tm1a(EUCOMM)Hmgu>/Ieg", "C57BL/6N-Trnt1<tm1a(EUCOMM)Hmgu>/Ieg");
		STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Prmt1<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Prmt1<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
		STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Ints12<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Ints12<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
		STRAIN_MAPPING.put("C57BL/6NTac-Ube2btm1a(EUCOMM)Wtsi/Ics", "C57BL/6NTac-Ube2b<tm1a(EUCOMM)Wtsi>/Ics");
		STRAIN_MAPPING.put("C57BL/6NTac-B9d1tm1a(EUCOMM)Wtsi/Ics", "C57BL/6NTac-B9d1<tm1a(EUCOMM)Wtsi>/Ics");
		STRAIN_MAPPING.put("B6NDen;B6N-Secisbp2<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Secisbp2<tm1a(EUCOMM)Wtsi>/H");
		STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Cntfr<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Cntfr<tm1a(EUCOMM)Wtsi>/WtsiBiat");
		STRAIN_MAPPING.put("B6NDen;B6N-Ercc2<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Ercc2<tm1a(EUCOMM)Wtsi>/H");
		STRAIN_MAPPING.put("B6J;B6N-Tyrc-Brd Snf8tm1a(EUCOMM)Wtsi/Wtsi", "B6J;B6N-Tyr<c-Brd> Snf8<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
		STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Rad18<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Rad18<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
		STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Mthfd1l<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> A<tm1Brd> Mthfd1l<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
		STRAIN_MAPPING.put("C57BL/6NTac-Sept8tm1a(EUCOMM)Wtsi/Ics", "C57BL/6NTac-Sept8<tm1a(EUCOMM)Wtsi>/Ics");
		STRAIN_MAPPING.put("C3H;B6-Tulp3<hhkr>/H", "C3H.B6-Tulp3<hhkr>/H");

		/**
		 * Background mapping
		 */

		BACKGROUND_MAPPING.put("B6NTac", "C57BL/6NTac");
		BACKGROUND_MAPPING.put("129/Sv", "129");
		BACKGROUND_MAPPING.put("129S5", "129S5/SvEvBrd");
		BACKGROUND_MAPPING.put("C57BL/6NTacDen", "C57BL/6Dnk");
		BACKGROUND_MAPPING.put("C57BL/6JTyr", "C57BL/6Brd-Tyr<c-Brd>");
		BACKGROUND_MAPPING.put("129P2", "129P2/OlaHsd");
		BACKGROUND_MAPPING.put("B6J.129S2", "129S2");
		BACKGROUND_MAPPING.put("129/SvEv", "129S/SvEv");
		BACKGROUND_MAPPING.put("129SvJ-Iso", "129X1/SvJ");
		BACKGROUND_MAPPING.put("C3H/NHG", "C3H");
		BACKGROUND_MAPPING.put("129/SvPas", "129S2/SvPas");
		BACKGROUND_MAPPING.put("C57BL/6NTac-ICS-USA(ImportedLive)", "C57BL/6NTac");
		BACKGROUND_MAPPING.put("C57BL/6NTac-ICS-Denmark(ImportedLive)", "C57BL/6Dnk");

	}

	public static String getAllelicComposition(String sampleZygosity, String alleleSymbol, String geneSymbol, String sampleGroup) {

		String allelicComposition = null;

		//System.out.println(sampleZygosity);
		if (sampleGroup.equals("control")) {
			allelicComposition = "";
		} else if (sampleGroup.equals("experimental")) {

			switch (sampleZygosity) {
				case "homozygous":
				case "homozygote":
					if (!alleleSymbol.equals("baseline")) {
						if (alleleSymbol.length() > 0 && !alleleSymbol.contains(" ")) {
							allelicComposition = alleleSymbol + "/" + alleleSymbol;
						} else {
							allelicComposition = geneSymbol + "<?>/" + geneSymbol + "<?>";
						}
					} else {
						allelicComposition = geneSymbol + "<+>/" + geneSymbol + "<+>";
					}
					break;
				case "heterozygous":
				case "heterozygote":
					if (!alleleSymbol.equals("baseline")) {
						if (alleleSymbol.length() > 0 && !alleleSymbol.contains(" ")) {
							allelicComposition = alleleSymbol + "/" + geneSymbol + "<+>";
						} else {
							allelicComposition = geneSymbol + "<?>/" + geneSymbol + "<+>";
						}
					}
					break;
				case "hemizygous":
				case "hemizygote":
					if (!alleleSymbol.equals("baseline")) {
						if (alleleSymbol.length() > 0 && !alleleSymbol.contains(" ")) {
							allelicComposition = alleleSymbol + "/0";
						} else {
							allelicComposition = geneSymbol + "<?>/0";
						}
					}
					break;
			}
		}

		return allelicComposition;
	}

	/**
	 *
	 * Return a reformatted representation based on the string representation of the genetic background as specified in EuroPhenome
	 * @param background test representation of the background
	 * @param strainDAO the access object to interrogate the database
	 * @return the genetic background reformatted
	 * @throws StrainNotFoundException
	 */
	public static String getGeneticBackground(String background, String datasourceShortName, Integer datasourceId, StrainDAO strainDAO) throws StrainNotFoundException {

		List<Strain> strains = getGeneticBackgroundStrains(background, datasourceShortName, datasourceId, strainDAO);
		String geneticBackground = "involves: ";
		int cBg = 0;
		for (Strain strain : strains) {
			if (strain == null) continue;
			geneticBackground += ((cBg > 0) ? " * " + strain.getName() : strain.getName());
			cBg++;
		}
		return geneticBackground;

	}

	/**
	 * Return a list of strains based on the string representation of the genetic background as specified in EuroPhenome
	 * @param background test representation of the background
	 * @param strainDAO the access object to interrogate the database
	 * @return a list of strains
	 * @throws StrainNotFoundException if a strain is not found
	 */
	public static List<Strain> getGeneticBackgroundStrains(String background, String datasourceShortName, Integer datasourceId, StrainDAO strainDAO) throws StrainNotFoundException {

		// Fail fast
		if (background == null) {
			throw new StrainNotFoundException("Unknown genetic background. No background strain supplied.");
		}

		List<Strain> strains = new ArrayList<Strain>();
		String[] backgrounds = null;

		if (background.contains("_")) {
			backgrounds = background.split("_");
			//if ()
		} else if (background.contains(";")) {
			backgrounds = background.split(";");
			//if ()
		} else if (background.equals("Balb/c.129S2")) {
			backgrounds = "BALB/c;129S2/SvPas".split(";");
		} else if (background.equals("B6N.129S2.B6J") || background.equals("B6J.129S2.B6N") || background.equals("B6N.B6J.129S2")) {
			backgrounds = "C57BL/6N;129S2/SvPas;C57BL/6J".split(";");
		} else if (background.equals("B6J.B6N")) {
			backgrounds = "C57BL/6J;C57BL/6N".split(";");
		} else {
			backgrounds = new String[1];
			backgrounds[0] = background;
		}

		Strain strain = strainDAO.getStrainByName(background);

		if (strain == null) {
			strain = strainDAO.getStrainByAcc(background);
		}

		if (strain != null) {

			//Found the strain
			strains.add(strain);

		} else {

			// Iterate through the strain pieces to find appropriate strains
			for (String bg : backgrounds) {

				if (BACKGROUND_MAPPING.containsKey(bg)) {
					bg = BACKGROUND_MAPPING.get(bg);
				}

				// Try to get strain accession name
				strain = getStrain(bg, datasourceShortName, datasourceId, strainDAO);

				if (strain == null) {
					logger.warn("Unknown genetic background. Strain not found for background: " + bg);
				}
				strains.add(strain);

			}
		}

		if (strains.isEmpty()) {
			throw new StrainNotFoundException("Unknown genetic background. Strains not found for background " + background);
		}

		return strains;
	}

	public static Strain getStrain(String strainName, String datasourceShortName, Integer datasourceId, StrainDAO strainDAO) {

		Strain strain = null;

		if (strainName != null) {

			// check existing Mapping table

			boolean wasMapped = BACKGROUND_MAPPING.containsKey(strainName);

			if (wasMapped) {
				strainName = BACKGROUND_MAPPING.get(strainName);
			}

			// then select official strain accession

			if (strainName == null) {
				strainName = "";
				// LOG.debug("Strain is set to '" + strainName + "'.");
			}

			strain = strainDAO.getStrainByName(strainName);

			// if we can't find it, let's search by synonym

			if (strain == null) {

				strain = strainDAO.getStrainBySynonym(strainName);

				// if we can't find it, let's search by accession

				if (strain == null) {

					strain = strainDAO.getStrainByAcc(strainName);

					if (strain == null) {

						// well let's put it in the database

						String datasourceAccName = StringUtils.left(datasourceShortName, 4).toUpperCase();
						String hex = DigestUtils.md5Hex(strainName).substring(0, 5).toUpperCase();
						String strainAcc = (wasMapped) ? (datasourceAccName + "-MAPPED-" + hex) : (datasourceAccName + "-CURATE-" + hex);

						logger.warn("Strain {} not in DB. Inserting as a {} strain with acc ID {}", strainName, datasourceShortName, strainAcc);

						DatasourceEntityId id = new DatasourceEntityId();
						id.setAccession(strainAcc);
						id.setDatabaseId(datasourceId);

						strain = new Strain();
						strain.setId(id);
						strain.setName(strainName);

						logger.info("Uncharacterized strain -- Saving strain {}", strain.getName());
						strainDAO.saveOrUpdateStrain(strain);

					}
				}
			}
		}

		return strain;
	}

}
