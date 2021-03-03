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
package org.mousephenotype.cda.solr.generic.util;

import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.*;

public class Tools {

	// check a string contains only numbers
	public static boolean isNumeric(String input) {
		try {
			Integer.parseInt(input);
			return true;
		}
		catch (NumberFormatException e) {
			// string is not numeric
			return false;
		}
	}
	public static String superscriptify(String input) {
		return input.replace("<", "<sup")
				.replace(">", "/sup>")
				.replace("<sup", "<sup>")
				.replace("/sup>", "</sup>");
	}

        /**
         * Given two dates (in any order), returns a <code>String</code> in the
         * format "xxx days, yyy hours, zzz minutes, nnn seconds" that equals
         * the absolute value of the time difference between the two days.
         * @param date1 the first operand
         * @param date2 the second operand
         * @return a <code>String</code> in the format "dd:hh:mm:ss" that equals the
         * absolute value of the time difference between the two date.
         */
	public static String dateDiff(Date date1, Date date2) {
            long lower = Math.min(date1.getTime(), date2.getTime());
            long upper = Math.max(date1.getTime(), date2.getTime());
            long diff = upper - lower;

            long days = diff / (24 * 60 * 60 * 1000);
            long hours = diff / (60 * 60 * 1000) % 24;
            long minutes = diff / (60 * 1000) % 60;
            long seconds = diff / 1000 % 60;

            return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
	}

	public static String highlightMatchedStrIfFound(String qry, String target, String selector, String cssClass) {
		// NOTE this can work for multiple words in the qry; it will match multiple places in the target string

		String kw = "";

		try {
			kw = URLDecoder.decode(qry, "UTF-8");
			//System.out.println("kw decoded: "+ kw);
		}
		catch( Exception e){
			System.out.println("Failed to decode " + qry);
		}

		if ( qry.equals("*:*") ) {
			return target;
		}
		else if ( kw.startsWith("\"") && kw.endsWith("\"") ) {
			// exact phrase search - with double quotes
			kw = kw.replace("\"", "")
				   .replace("(", "\\(")
				   .replace(")", "\\)");
		}
//		else {
//			// non phrase search - split string into words and search using OR
//			// very loose match not using boundry: ie, matches anywhere in string -> less specificity
//
//			StringBuffer patBuff = new StringBuffer();
//			int count = 0;
//			for ( String s : kw.split(" |,") ){
//				count++;
//				if ( count != kw.split(" ").length ){
//					patBuff.append(s+"|");
//				}
//				else {
//					patBuff.append(s);
//				}
//			}
//			kw = patBuff.toString();
//		}

		kw = kw.replace("*","")
				.replace("+", "\\+");

		//working pattern: vang\-like|2|\\(van|gogh,|Drosophila\\)

		// (?im) at the beginning of the regex turns on CASE_INSENSITIVE and MULTILINE modes.
		// $0 in the replacement string is a placeholder whatever the regex matched in this iteration.
		target = target.replaceAll("<(.+)>", "<sup>$1</sup>");

		String result = target.replaceAll("(?im)"+kw, "<" + selector + " class='" + cssClass + "'>$0" + "</" + selector + ">");
		return result;
	}

	public static String[][] composeXlsTableData(List<String> rows) {
        int rowNum = rows.size();
		int colNum = (rows.size() > 0) ? rows.get(0).split("\t").length : 0; // title row, tells how many columns

        String[][] tableData = new String[rowNum][colNum];

		for (int i = 0; i < rowNum; i++) {  // index 0 is title row
            String[] colVals = rows.get(i).split("\t");

            for (int j = 0; j < colVals.length; j++) {
            	String currVal = colVals[j];

				if ( currVal.contains("|") ){

					List<String> vals = Arrays.asList(currVal.split("\\|"));
					List<String> newVals = new ArrayList<>();
					for ( String val : vals ){
						if ( val.startsWith("//") ) {
							val = "http:" + val;
						}
						if ( val.startsWith("http:")){
							val = val.replaceAll(" ", "%20");
							val = val.replaceAll("\"", "%22");
						}
						newVals.add(val);
					}
					currVal = StringUtils.join(newVals, "|");
				}
				else if ( currVal.startsWith("//") ){
					currVal = "http:" + currVal;
				}


                tableData[i][j] = currVal;
            }
        }

        return tableData;
    }
//localhost:8080/phenotype-archive/imagesb?qf=auto_suggest&defType=edismax&wt=json&fq=*:*&q=*:* AND symbol:"2010107G12Rik"&fl=annotationTermId,annotationTermName,expName,symbol,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath

//http://localhost:8080/phenotype-archive/imagesb?qf=auto_suggest&defType=edismax&wt=json&fq=*:*&q=*:*%20AND%20symbol:"2010107G12Rik"&fl=annotationTermId,annotationTermName,expName,symbol,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath

	public static String fetchOutputFieldsCheckBoxesHtml(String corename) {

		corename = (corename == null) ? "gene" : corename;

		if ( corename.contains("marker_symbol")){
			corename = "marker_symbol";
		}

		String htmlStr1 = "";
		String htmlStr2 = "";

		// main attrs.
		List<String> mainAttrs = new ArrayList<>();

		// additional information
		List<String> additionalInfos = new ArrayList<>();

		Map<String, String> friendlyNameMap = new HashMap<>();
		friendlyNameMap.put("mgi_accession_id", "MGI gene id");
		friendlyNameMap.put("marker_id", "MGI gene id");
		friendlyNameMap.put("marker_symbol", "MGI gene symbol");
		friendlyNameMap.put("human_gene_symbol", "Human ortholog");
		friendlyNameMap.put("marker_name", "MGI marker name");
		friendlyNameMap.put("marker_synonym", "MGI marker synonym");
		friendlyNameMap.put("marker_type", "MGI marker type");
		friendlyNameMap.put("p_value", "p value (phenotyping significance)");
		friendlyNameMap.put("mp_id", "MP id");
		friendlyNameMap.put("mp_term", "MP term");
		friendlyNameMap.put("mp_term_synonym", "MP term synonym");
		friendlyNameMap.put("mp_term_definition", "MP term definition");
		friendlyNameMap.put("hp_id", "HP id");
		friendlyNameMap.put("hp_term", "HP term");

		// first n fields checked by default
		Map<String, Integer> defaultOffset = new HashMap<>();
		defaultOffset.put("gene", 8);
		defaultOffset.put("marker_symbol", 8);
		defaultOffset.put("ensembl", 9);
		defaultOffset.put("disease", 5);
		defaultOffset.put("mp", 6);
		defaultOffset.put("anatomy", 5);
		defaultOffset.put("hp", 8);

		if ( corename.equals("gene") || corename.equals("marker_symbol") ){

			// gene attr fields
			// these first 6 ones are checked by default
			mainAttrs.add("mgi_accession_id");
			mainAttrs.add("marker_symbol");
			mainAttrs.add("human_gene_symbol");
			mainAttrs.add("marker_synonym");
			//mainAttrs.add("human_symbol_synonym");
			mainAttrs.add("marker_name");
			mainAttrs.add("marker_type");
			mainAttrs.add("mouse_status");
			mainAttrs.add("phenotype_status");
			mainAttrs.add("es_cell_status");
			mainAttrs.add("project_status");
			mainAttrs.add("production_centre");
			mainAttrs.add("phenotyping_centre");

			// gene has QC: ie, a record in experiment core
			additionalInfos.add("ensembl_gene_id");
			additionalInfos.add("hasQc");

			// annotated mp term
			additionalInfos.add("p_value");


			additionalInfos.add("mp_id");
			additionalInfos.add("mp_term");
			additionalInfos.add("mp_term_synonym");
			additionalInfos.add("mp_term_definition");

			// mp to hp mapping
			additionalInfos.add("hp_id");
			additionalInfos.add("hp_term");

			// disease fields
			additionalInfos.add("disease_id");
			additionalInfos.add("disease_term");

			// impc images link
            //additionalInfos.add("images_link");

			//GO stuff for gene : not shown for now
		}
		else if ( corename.equals("ensembl") ){

			// gene attr fields
			mainAttrs.add("mgi_accession_id");
			mainAttrs.add("ensembl_gene_id");
			mainAttrs.add("marker_symbol");
			mainAttrs.add("human_gene_symbol");
			mainAttrs.add("marker_name");
			mainAttrs.add("marker_synonym");
			mainAttrs.add("marker_type");
			mainAttrs.add("latest_phenotype_status");
			mainAttrs.add("latest_mouse_status");

			mainAttrs.add("legacy_phenotype_status");
			mainAttrs.add("es_cell_status");
			mainAttrs.add("project_status");
			mainAttrs.add("production_centre");
			mainAttrs.add("phenotyping_centre");

			// gene has QC: ie, a record in experiment core
			additionalInfos.add("hasQc");

			// annotated mp term
			additionalInfos.add("p_value");
			additionalInfos.add("mp_id");
			additionalInfos.add("mp_term");
			additionalInfos.add("mp_term_synonym");
			additionalInfos.add("mp_term_definition");

			// mp to hp mapping
			additionalInfos.add("hp_id");
			additionalInfos.add("hp_term");

			// disease fields
			additionalInfos.add("disease_id");
			additionalInfos.add("disease_term");

			// impc images link
            //additionalInfos.add("images_link");

			//GO stuff for gene : not shown for now
		}

		else if ( corename.equals("disease") ) {
			mainAttrs.add("disease_id");
			mainAttrs.add("disease_term");
			mainAttrs.add("marker_symbol");
			mainAttrs.add("marker_id");
			mainAttrs.add("hgnc_gene_symbol");

//			// annotated and inferred mp term
//			additionalInfos.add("p_value");
//			additionalInfos.add("mp_id");
//			additionalInfos.add("mp_term");
//			additionalInfos.add("mp_term_synonym");
//
//			// mp to hp mapping
//			additionalInfos.add("hp_id");
//			additionalInfos.add("hp_term");

		}
		else if ( corename.equals("mp") ) {
			mainAttrs.add("mp_id");
			mainAttrs.add("mp_term");
			mainAttrs.add("mp_definition");

			// gene core stuff
			mainAttrs.add("mgi_accession_id");
			mainAttrs.add("marker_symbol");
			mainAttrs.add("human_gene_symbol");

			additionalInfos.add("top_level_mp_id");
			additionalInfos.add("top_level_mp_term");

			//  mp to hp mapping
			additionalInfos.add("hp_id");
			additionalInfos.add("hp_term");

			//disease core stuff
			additionalInfos.add("disease_id");
			additionalInfos.add("disease_term");
		}
		else if ( corename.equals("anatomy") ) {
			mainAttrs.add("anatomy_id");
			mainAttrs.add("anatomy_term");

			// gene core stuff
			mainAttrs.add("mgi_accession_id");
			mainAttrs.add("marker_symbol");
			mainAttrs.add("human_gene_symbol");

			additionalInfos.add("selected_top_level_anatomy_id");
			additionalInfos.add("selected_top_level_anatomy_term");
			// impc images link
            //additionalInfos.add("images_link");
		}

		else if ( corename.equals("hp") ) {

			mainAttrs.add("hp_id");
			mainAttrs.add("hp_term");

			//  hp to mp mapping
			mainAttrs.add("mp_id");
			mainAttrs.add("mp_term");
			mainAttrs.add("mp_definition");

			// gene core stuff
			mainAttrs.add("mgi_accession_id");
			mainAttrs.add("marker_symbol");
			mainAttrs.add("human_gene_symbol");

			additionalInfos.add("top_level_mp_id");
			additionalInfos.add("top_level_mp_term");

			//disease core stuff
			additionalInfos.add("disease_id");
			additionalInfos.add("disease_term");
		}

		String dataType = corename.toUpperCase().replaceAll("_"," ");

		htmlStr1 += "<div class='cat'>" + dataType + " attributes</div>";
		for ( int i=0; i<mainAttrs.size(); i++ ){
			String checked = "";
			String checkedClass = "";

			if ( i < defaultOffset.get(corename) ) {
				checked = "checked";
				checkedClass = "default";
				// first two of each dataType are uncheckable, so they are minimum fields (order is important in the mainAttrs above
				if ( i < 2 ) {
					checkedClass = "default frozen";
				}

			}

			String friendlyFieldName = friendlyNameMap.get(mainAttrs.get(i)) != null ? friendlyNameMap.get(mainAttrs.get(i)) : mainAttrs.get(i).replaceAll("_", " ");
			htmlStr1 += "<input type='checkbox' class='" + checkedClass + "' name='" + corename + "' value='" + mainAttrs.get(i) + "'" + checked + ">" + friendlyFieldName;
			if ( (i+1) % 3 == 0 ){
				htmlStr1 += "<br>";
			}
		}

		if (additionalInfos.size() > 0) {
			htmlStr2 += "<div class='cat'>Additional annotations to " + dataType + "</div>";
		}

		for ( int i=0; i<additionalInfos.size(); i++ ){
			String friendlyFieldName = friendlyNameMap.get(additionalInfos.get(i)) != null ? friendlyNameMap.get(additionalInfos.get(i)) : additionalInfos.get(i).replaceAll("_", " ");
			htmlStr2 += "<input type='checkbox' name='" + corename + "' value='" + additionalInfos.get(i) + "'>" + friendlyFieldName;
			if ( (i+1) % 3 == 0 ){
				htmlStr2 += "<br>";
			}
		}

		String hrStr = "<hr>";
		String checkAllBoxStr = "<button type='button' id='chkFields'>Check all fields</button>";

		return htmlStr1 + htmlStr2 + hrStr + checkAllBoxStr;
	}

	public static String fetchOutputFieldsCheckBoxesHtml2(String corename) {

		corename = (corename == null) ? "gene" : corename;

		if ( corename.contains("marker_symbol")){
			corename = "marker_symbol";
		}

		// main attrs.
		Map<String, String> geneAttrs = new LinkedHashMap<>();
		Map<String, String> alleleAttrs = new LinkedHashMap<>();
		Map<String, String> phenotypeAttrs = new LinkedHashMap<>();
		Map<String, String> anatomyAttrs = new LinkedHashMap<>();
		Map<String, String> diseaseAttrs = new LinkedHashMap<>();
		Map<String, String> humanAttrs = new LinkedHashMap<>();
		Map<String, String> humanGeneAttrs = new LinkedHashMap<>();

		geneAttrs.put("marker_symbol", "default");
		geneAttrs.put("mgi_accession_id", "");
		geneAttrs.put("ensembl_gene_id", "");
		geneAttrs.put("human_gene_symbol", "");
		geneAttrs.put("marker_name", "");
		geneAttrs.put("marker_synonym", "");
		geneAttrs.put("marker_type", "");
		geneAttrs.put("seq_region_id", "");
		geneAttrs.put("seq_region_start", "");
		geneAttrs.put("seq_region_end", "");

		alleleAttrs.put("allele_mgi_accession_id", "");
		alleleAttrs.put("allele_name", "");
		alleleAttrs.put("es_cell_status", "");
		alleleAttrs.put("mouse_status", "" );
		alleleAttrs.put("phenotype_status", "");
		alleleAttrs.put("allele_description", "");

		diseaseAttrs.put("disease_id", "");
		diseaseAttrs.put("disease_term", "");
		diseaseAttrs.put("disease_classes", "");
		diseaseAttrs.put("disease_locus", "");
		diseaseAttrs.put("human_curated", "");  // ortholog
		diseaseAttrs.put("mouse_curated", "");  // ortholog
		diseaseAttrs.put("mgi_predicted", "");  // pheno sim
		diseaseAttrs.put("impc_predicted", ""); // pheno sim
		diseaseAttrs.put("mgi_predicted_known_gene", "");
		diseaseAttrs.put("impc_predicted_known_gene", "");
		diseaseAttrs.put("mgi_novel_predicted_in_locus", "");
		diseaseAttrs.put("impc_novel_predicted_in_locus", "");
		diseaseAttrs.put("impc_novel_predicted_in_locus", "");

		phenotypeAttrs.put("mp_id", "");
		phenotypeAttrs.put("mp_term", "");
		phenotypeAttrs.put("mp_definition", "");
		phenotypeAttrs.put("top_level_mp_id", "");
		phenotypeAttrs.put("top_level_mp_term", "");
		phenotypeAttrs.put("p_value", "");
		phenotypeAttrs.put("hasQc", "");

		humanGeneAttrs.put("human_gene_symbol", "");
		humanGeneAttrs.put("marker_symbol", "");

//		anatomyAttrs.put("ma_id", "default");
//		anatomyAttrs.put("ma_term", "default");
//		anatomyAttrs.put("selected_top_level_ma_id", "");
//		anatomyAttrs.put("selected_top_level_ma_term", "");

		humanAttrs.put("hp_id", "");
		humanAttrs.put("hp_term", "");

		Map<String, String> friendlyNameMap = new HashMap<>();
		friendlyNameMap.put("mgi_accession_id", "MGI gene id");
		friendlyNameMap.put("ensembl_gene_id", "Ensembl mouse gene id");
		friendlyNameMap.put("marker_symbol", "MGI gene symbol");
		friendlyNameMap.put("human_gene_symbol", "Human ortholog");
		friendlyNameMap.put("marker_name", "MGI gene name");
		friendlyNameMap.put("marker_synonym", "MGI gene synonym");
		friendlyNameMap.put("marker_type", "MGI gene type");
		friendlyNameMap.put("seq_region_id", "Chromosome name");
		friendlyNameMap.put("seq_region_start", "Chromosome start");
		friendlyNameMap.put("seq_region_end", "Chromosome end");
		friendlyNameMap.put("p_value", "p value (phenotyping significance)");
		friendlyNameMap.put("mp_id", "MP id");
		friendlyNameMap.put("mp_term", "MP term");
		friendlyNameMap.put("mp_term_synonym", "MP term synonym");
		friendlyNameMap.put("mp_term_definition", "MP term definition");
		friendlyNameMap.put("hp_id", "HP id");
		friendlyNameMap.put("hp_term", "HP term");

		Map<String, String> friendlyHumanNameMap = new HashMap<>();
		friendlyHumanNameMap.put("human_gene_symbol", "HGNC gene symbol");
		friendlyHumanNameMap.put("marker_symbol", "Mouse ortholog");

		String checkAlltheseAtt = "<i class='fa fa-plus'></i>";
		String htmlStr = "";
		String checked = "";
		String checkedClass = "";

		String htmlStrMouseGene = getCheckBoxes(geneAttrs, friendlyNameMap, "Gene");
		htmlStr += "<fieldset class='mouse' id='genefs'><legend><i class='icon icon-species'>M</i>Mouse gene attributes</legend>" + htmlStrMouseGene + checkAlltheseAtt+ "</fieldset>";

		String htmlStrAllele = getCheckBoxes(alleleAttrs, friendlyNameMap, "Allele");
		htmlStr += "<fieldset class='mouse' ><legend><i class='icon icon-species'>M</i>Mouse allele attributes</legend>" + htmlStrAllele + checkAlltheseAtt+ "</fieldset>";

		String htmlStrPhenotype = getCheckBoxes(phenotypeAttrs, friendlyNameMap, "Phenotype");
		htmlStr += "<fieldset class='mouse' ><legend><i class='icon icon-species'>M</i>Mouse phenotype attributes</legend>" + htmlStrPhenotype + checkAlltheseAtt+ "</fieldset>";

		//		String htmlStrAnatomy = getCheckBoxes(anatomyAttrs, friendlyNameMap, "Anatomy");
//		htmlStr += "<fieldset><legend>Mouse anatomy attributes</legend>" + htmlStrAnatomy + checkAlltheseAtt+ "</fieldset>";

		String htmlStrHumanGene = getCheckBoxes(humanGeneAttrs, friendlyHumanNameMap, "HumanGeneSymbol");
		htmlStr += "<fieldset class='human'><legend class='human'><i class='icon icon-species'>H</i>Human gene attributes</legend>" + htmlStrHumanGene + checkAlltheseAtt+ "</fieldset>";

		String htmlStrHuman = getCheckBoxes(humanAttrs, friendlyNameMap, "Hp");
		htmlStr += "<fieldset class='human'><legend class='human'><i class='icon icon-species'>H</i>Human phenotype attributes</legend>" + htmlStrHuman + checkAlltheseAtt+ "</fieldset>";

		String htmlStrDisease = getCheckBoxes(diseaseAttrs, friendlyNameMap, "DiseaseModelAssociation");
		htmlStr += "<fieldset class='human'><legend class='human'><i class='icon icon-species'>H</i>Human disease attributes</legend>" + htmlStrDisease + checkAlltheseAtt+ "</fieldset>";

		String hrStr = "<hr>";
		String checkAllBoxStr = "<button type='button' id='chkFields'>Check all fields</button>";


		return htmlStr + hrStr + checkAllBoxStr;
	}

	public static String getCheckBoxes(Map<String, String> map, Map<String, String> friendlyNameMap, String label) {

		String htmlStr = "";

		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String field = pair.getKey().toString();
			String val = pair.getValue().toString();

			String checkedClass = val.equals("default") ? "isDefault" : "";
			String checked = val.equals("default") ? "checked" : "";

			String friendlyFieldName = friendlyNameMap.get(field) != null ? friendlyNameMap.get(field) : field.replaceAll("_", " ");
			htmlStr += "<input type='checkbox' class='" + checkedClass + "' name='" + label + "' value='" + field+ "'" + checked + ">" + friendlyFieldName;

			it.remove(); // avoids a ConcurrentModificationException
		}

		return htmlStr;
	}

	private static String fetchOptionalMatch(String dataType, Set<String>labels){

		String optMatch = "";

		Map<String, Map<String, String>> dataTypelabelMatch = new HashMap<>();
		dataTypelabelMatch.put("gene", new HashMap<>());
		dataTypelabelMatch.get("gene").put("Allele", "OPTIONAL MATCH (g)-[HAS_ALLELE]->(a:Allele)");
		dataTypelabelMatch.get("gene").put("Phenotype", "OPTIONAL MATCH (g)-[HAS_PHENOTYPE]->(p:Phenotype)");
		dataTypelabelMatch.get("gene").put("Disease", "OPTIONAL MATCH (g)-[HAS_DISEASE]->(dma:DiseaseModelAssociation)");
		dataTypelabelMatch.get("gene").put("Hp", "OPTIONAL MATCH (dma)-[HAS_HP]->(hp:Hp)");

		dataTypelabelMatch.put("mp", new HashMap<>());
		dataTypelabelMatch.get("mp").put("Gene", " (p)-[OF_GENE]->(g:Gene)");
		dataTypelabelMatch.get("mp").put("Allele", "OPTIONAL MATCH (g)-[HAS_ALLELE]->(a:Allele)");
		dataTypelabelMatch.get("mp").put("Disease", "OPTIONAL MATCH (p)->[HAS_DISEASE]->(dma:DiseaseModelAssociation)");
		dataTypelabelMatch.get("mp").put("Hp", "OPTIONAL MATCH (dma)-[HAS_HP]->(hp:Hp)");

		Map<String, String> om = dataTypelabelMatch.get(dataType);
		for (String l : labels){
			optMatch += om.get(l);
		}

		return optMatch;
	}
}