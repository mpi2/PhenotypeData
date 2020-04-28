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
package uk.ac.ebi.phenotype.web.controller;

import com.opencsv.CSVWriter;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import uk.ac.ebi.phenotype.util.PhenotypeGeneSummaryDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class ControllerUtils {


	public static void writeAsCSV(String toWrite, String fileName, HttpServletResponse response) throws IOException{

	    response.setContentType("text/csv;charset=utf-8");
	    response.setHeader("Content-Disposition","attachment; filename="+fileName);
	    OutputStream resOs= response.getOutputStream();
	    OutputStream buffOs= new BufferedOutputStream(resOs);
	    OutputStreamWriter outputwriter = new OutputStreamWriter(buffOs);
        outputwriter.write(toWrite);
	    outputwriter.close();
	    outputwriter.close();
	}


	public static void writeAsCSV(List<String[]> toWrite, String fileName, HttpServletResponse response) throws IOException{

	    response.setContentType("text/csv;charset=utf-8");
	    response.setHeader("Content-Disposition","attachment; filename="+fileName);
	    OutputStream resOs= response.getOutputStream();
	    OutputStream buffOs= new BufferedOutputStream(resOs);
	    CSVWriter writer = new CSVWriter(new OutputStreamWriter(buffOs));
	    writer.writeAll(toWrite);
		writer.close();
	}


	public static void writeAsCSVMultipleTables(List<List<String[]>> toWrite, String fileName, HttpServletResponse response) throws IOException{

	    response.setContentType("text/csv;charset=utf-8");
	    response.setHeader("Content-Disposition","attachment; filename="+fileName);
	    OutputStream resOs  = response.getOutputStream();
	    OutputStream buffOs = new BufferedOutputStream(resOs);
	    CSVWriter    writer = new CSVWriter(new OutputStreamWriter(buffOs));
	    for (List<String[]> table : toWrite){
	    	writer.writeAll(table);
	    	writer.writeNext(new String[0]);
	    }
		writer.close();
	}



	/**
	 *
	 * @param phenotype_id
	 * @return <sex, percentage>, to be used on overview pie chart
	 * @throws SolrServerException, IOException
	 */
	public static PhenotypeGeneSummaryDTO getPercentages(String phenotype_id, StatisticalResultService srService, GenotypePhenotypeService gpService)
			throws SolrServerException, IOException {

		PhenotypeGeneSummaryDTO pgs = new PhenotypeGeneSummaryDTO();

		int total = 0;
		int nominator = 0;

		nominator = gpService.getGenesBy(phenotype_id, null, false).size();
		total = srService.getGenesBy(phenotype_id, null).size();
		pgs.setTotalPercentage(100 * (float) nominator / (float) total);
		pgs.setTotalGenesAssociated(nominator);
		pgs.setTotalGenesTested(total);
		boolean display = (total > 0);
		pgs.setDisplay(display);

		List<String> genesFemalePhenotype = new ArrayList<String>();
		List<String> genesMalePhenotype = new ArrayList<String>();
		List<String> genesBothPhenotype;

		if (display) {
			for (Group g : gpService.getGenesBy(phenotype_id, "female", false)) {
				genesFemalePhenotype.add((String) g.getGroupValue());
			}
			nominator = genesFemalePhenotype.size();
			total = srService.getGenesBy(phenotype_id, SexType.female).size();
			pgs.setFemalePercentage(100 * (float) nominator / (float) total);
			pgs.setFemaleGenesAssociated(nominator);
			pgs.setFemaleGenesTested(total);

			for (Group g : gpService.getGenesBy(phenotype_id, "male", false)) {
				genesMalePhenotype.add(g.getGroupValue());
			}
			nominator = genesMalePhenotype.size();
			total = srService.getGenesBy(phenotype_id, SexType.male).size();
			pgs.setMalePercentage(100 * (float) nominator / (float) total);
			pgs.setMaleGenesAssociated(nominator);
			pgs.setMaleGenesTested(total);
		}

		genesBothPhenotype = new ArrayList<String>(genesFemalePhenotype);
		genesBothPhenotype.retainAll(genesMalePhenotype);
		genesFemalePhenotype.removeAll(genesBothPhenotype);
		genesMalePhenotype.removeAll(genesBothPhenotype);
		pgs.setBothNumber(genesBothPhenotype.size());
		pgs.setFemaleOnlyNumber(genesFemalePhenotype.size());
		pgs.setMaleOnlyNumber(genesMalePhenotype.size());
		pgs.fillPieChartCode(phenotype_id);

		return pgs;
	}


}


