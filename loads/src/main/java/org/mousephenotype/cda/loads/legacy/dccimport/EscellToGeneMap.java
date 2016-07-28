package org.mousephenotype.cda.loads.legacy.dccimport;

import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.db.dao.AlleleDAO;
import org.mousephenotype.cda.db.dao.GenomicFeatureDAO;
import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class EscellToGeneMap {

	private ApplicationContext ac;

	Map<String, String> escToGeneAccMap = new HashMap<String, String>();
	Map<String, GenomicFeature> escToGeneMap = new HashMap<String, GenomicFeature>();

	Map<String, GenomicFeature> colonyToGeneMap = new HashMap<String, GenomicFeature>();
	Map<String, Allele> colonyToAlleleMap = new HashMap<String, Allele>();


	public void populateMaps(String line, GenomicFeatureDAO gfDAO, AlleleDAO alleleDAO) {
		String[] parts = line.replace("&lt;", "<").replace("&gt;", ">").split("\\t");
		if(parts.length<3) { return; }

		String colonyId = parts[0];

		if (colonyId.contains("_")) {
			// Colony IDs are concated with the genotype ID in the file
			// pop off the right most element after splitting on "_" to
			// leaving only the colony ID
			List<String> parts1 = new ArrayList<String>();
			Collections.addAll(parts1, parts[0].split("_"));
			parts1.remove(parts1.size()-1);
			colonyId = StringUtils.join(parts1, "_");
		}

		if (parts[1].toLowerCase().equals("null")) {
			// null gene in the file
			return;
		}

		if (parts[1].toLowerCase().equals("unknown")) {
			// unknown gene in the file
			return;
		}

		if (parts[1].toLowerCase().equals("")) {
			// empty gene in the file
			return;
		}

		try {

			GenomicFeature gene = null;

			if (parts[1].startsWith("MGI")) {

				gene = gfDAO.getGenomicFeatureByAccession(parts[1].trim());

			} else {
				gene = gfDAO.getGenomicFeatureBySymbol(parts[1].trim());

				if (gene == null) {
					gene = gfDAO.getGenomicFeatureBySymbolOrSynonym(parts[1].trim());
				}
			}

			if (gene == null) {
				return;
			}

			Allele allele = alleleDAO.getAlleleBySymbolAndGene(parts[2].trim(), gene);
			if (allele == null) {
				// We'll try to get the allele from imits
			}

			colonyToGeneMap.put(colonyId.trim(), gene);
			colonyToAlleleMap.put(colonyId.trim(), allele);
		} catch (org.hibernate.NonUniqueResultException e) {
			System.out.println("Error while processing: " + line);
			e.printStackTrace();
		}

	}

	/**
	 * Hugh sent a file that maps colony IDs in the europhenome dump to
	 * genes and alleles when they can -- load the file and cache the
	 * results for fast lookups
	 *
	 * @param gfDAO to get the actual gene objects
	 * @param alleleDAO to get the actual allele objects
	 */
	public void populateEurophenomeColonyMaps(GenomicFeatureDAO gfDAO, AlleleDAO alleleDAO) throws IOException {

		Resource resource = ac.getResource("classpath:EurophenomeColonyGeneAlleleMap.tsv");

		InputStreamReader in = new InputStreamReader(resource.getInputStream());
		try (BufferedReader bin = new BufferedReader(in)) {

			String line;
			while ((line = bin.readLine()) != null) {
				populateMaps(line, gfDAO, alleleDAO);
			}
		}
	}

	/**
	 * The file Hugh sent had some entries that aren't found.  This function
	 * loads the manually curated "not found" rows from the input file.
	 *
	 * @param gfDAO to get the actual gene objects
	 * @param alleleDAO to get the actual allele objects
	 */
	private void populateManuallyCuratedEurophenomeColonyMaps(GenomicFeatureDAO gfDAO, AlleleDAO alleleDAO) {

		Set<String> moreLines = new HashSet<String>();

		// WTSI
		moreLines.add("Dlg2Dlg2<tm1Dsb>	Dlg2	Dlg2<tm1Dsb>");
		moreLines.add("Chd7Chd7<Whi>	Chd7	Chd7<Whi>");
		moreLines.add("Ifitm3Ifitm3<tm1Masu>	Ifitm3	Ifitm3<tm1Masu>");
		moreLines.add("WhrnWhrn<wi>	Whrn	Whrn<wi>");
		moreLines.add("Secisbp2Secisbp2<tm1a(EUCOMM)Wtsi/H>	Secisbp2	Secisbp2<tm1a(EUCOMM)Wtsi>");
		moreLines.add("Ttll4Ttll4<tm1a(EUCOMM)Wtsi/H>	Ttll4	Ttll4<tm1a(EUCOMM)Wtsi>");
		moreLines.add("Myo7aMyo7a<sh1-6J>	Myo7a	Myo7a<sh1-6J>");
		moreLines.add("Dlg4Dlg4<tm1Grnt>	Dlg4	Dlg4<tm1Grnt>");
		moreLines.add("Git2Git2<Gt(XG510)Byg>	Git2	Git2<Gt(XG510)Byg>");
		moreLines.add("NfyaNfya<Gt(EUCJ0004f10)Hmgu>	Nfya	Nfya<Gt(EUCJ0004f10)Hmgu>");
		moreLines.add("Clk1Clk1<tm1a(EUCOMM)Wtsi/Ics>	Clk1	Clk1<tm1a(EUCOMM)Wtsi>");
		moreLines.add("Entpd1Entpd1<tm1a(EUCOMM)Wtsi/Hmgu>	Entpd1	Entpd1<tm1a(EUCOMM)Wtsi>");
		moreLines.add("Atp2b2Atp2b2<Obv>	Atp2b2	Atp2b2<Obv>");

		//moreLines.add("Grxcr1Grxcr1<tde>	Grxcr1	Grxcr1<tde>"); // not in MGI?

		// MRC Harwell
		moreLines.add("129P2/OlaHsd-Fbxl3<Gt(CB0226)Wtsi>/H	Fbxl3	Fbxl3<Gt(CB0226)Wtsi>");
		moreLines.add("129P2/OlaHsd-Map3k1<Gt(YTC001)Byg>/H	Map3k1	Map3k1<Gt(YTC001)Byg>");
		moreLines.add("129P2/OlaHsd-Tpcn1<Gt(XG716)Byg>/H	Tpcn1	Tpcn1<Gt(XG716)Byg>");
		moreLines.add("129S9/SvEvH-Ablim1<tm1H>/H	Ablim1	Ablim1<tm1H>");
		moreLines.add("B6Dnk;129P2-HBP1<Gt(EUCJ0053g09)>/H	Hbp1	Hbp1<Gt(EUCJ0053g09)Hmgu>");


		for (String line : moreLines) {
			populateMaps(line, gfDAO, alleleDAO);
		}

	}

	public Allele getAllele(String esCell) {
		return colonyToAlleleMap.get(esCell.replace("&lt;", "<").replace("&gt;", ">").trim());
	}

	public GenomicFeature getGene(String esCell) {
		return colonyToGeneMap.get(esCell.replace("&lt;", "<").replace("&gt;", ">").trim());
	}

	public Map<String, String> getEscToGeneMap() {
		return escToGeneAccMap;
	}

	public EscellToGeneMap(GenomicFeatureDAO gfDAO, AlleleDAO alleleDAO, ApplicationContext ac) throws IOException {

		this.ac = ac;

		populateEurophenomeColonyMaps(gfDAO, alleleDAO);

		// Overwrite the entries from the file with the curated entries
		populateManuallyCuratedEurophenomeColonyMaps(gfDAO, alleleDAO);

	}


}
