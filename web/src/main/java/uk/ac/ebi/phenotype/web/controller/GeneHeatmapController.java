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
import org.mousephenotype.cda.solr.service.GenesSecondaryProjectServiceIdg;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.mousephenotype.cda.solr.web.dto.HeatMapCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.phenotype.web.dao.GenesSecondaryProject3IImpl;
import uk.ac.ebi.phenotype.web.dao.GenesSecondaryProjectService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
public class GeneHeatmapController {

    @Autowired
    @Qualifier("idg")
	private GenesSecondaryProjectServiceIdg idgGenesSecondaryProjectService;


    @Autowired
    StatisticalResultService srService;


	/**
         *
         * @param project is the external project for example a secondary screen e.g. IDG or 3I
         * @param model
         * @param request
         * @return
	 * @throws SolrServerException, IOException
         */
	@RequestMapping("/geneHeatMap")
	@Cacheable("geneHeatMapCache")
	public String getHeatmapJS(@RequestParam(value = "project") String project,
                Model model,
                HttpServletRequest request) throws SolrServerException, IOException, SQLException {



			Long                         time                         = System.currentTimeMillis();
			List<GeneRowForHeatMap>      geneRows                     = idgGenesSecondaryProjectService.getGeneRowsForHeatMap(request);
			List<BasicBean>              xAxisBeans                   = idgGenesSecondaryProjectService.getXAxisForHeatMap();
		    model.addAttribute("geneRows", geneRows);
		    model.addAttribute("xAxisBeans", xAxisBeans);
			System.out.println("HeatMap: Getting the data took " + (System.currentTimeMillis() - time) + "ms");
			

        return "geneHeatMap";
	}


	/** Download the gene report
	 * @param project is the external project for example a secondary screen e.g. IDG or 3I
	 * @param model
	 * @param request
	 * @return
	 * @throws SolrServerException, IOException
	 */
	@RequestMapping("/geneHeatMapDownload")
	public ResponseEntity<String> getHeatmapExport(
			@RequestParam(value = "project") String project,
			Model model,
			HttpServletRequest request) throws SolrServerException, IOException, SQLException {



		List<GeneRowForHeatMap> geneRows = idgGenesSecondaryProjectService.getGeneRowsForHeatMap(request);
		List<BasicBean> xAxisBeans = idgGenesSecondaryProjectService.getXAxisForHeatMap();
		model.addAttribute("geneRows", geneRows);
		model.addAttribute("xAxisBeans", xAxisBeans);

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = dateFormat.format(new Date());
		StringWriter stringWriter = new StringWriter();//now + "_IMPC_IDG_download.tsv", true);

		CSVWriter csvWriter = new CSVWriter(stringWriter, '\t', '"', '\\', "\n");

		final List<String> xAxisHeaders = xAxisBeans.stream().map(BasicBean::getName).collect(Collectors.toList());
		// Create report header
		List<String> headers = new ArrayList<>();
		headers.add("Human Gene Symbol");
		headers.add("MGI Gene Symbol");
		headers.add("MGI Accession ID");
		headers.add("IDG Gene Family");
		headers.add("IMPC Mice Produced");
		headers.addAll(xAxisHeaders);
		String[] arrayType = new String[headers.size()];
		csvWriter.writeNext(headers.toArray(arrayType));

		geneRows.forEach(x -> {

			List<String>  row = new ArrayList<>();

			row.add(x.getHumanSymbolToString());
			row.add(x.getSymbol());
			row.add(x.getAccession());
			row.add(x.getGroupLabel());
			row.add(x.getMiceProducedPlain());

			Map<String, HeatMapCell> xAxisToCellMap = x.getXAxisToCellMap();
			for (String header : xAxisHeaders) {
				row.add(xAxisToCellMap.get(header).getStatus());
			}
			csvWriter.writeNext(row.toArray(arrayType));

		});

		String responseText = stringWriter.toString();
		csvWriter.close();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + now + "_IMPC_IDG_download.tsv" + "\"");
		httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
		httpHeaders.add("Pragma", "no-cache");
		httpHeaders.add("Expires", "0");

		return ResponseEntity.ok()
				.headers(httpHeaders)
				.contentType(MediaType.parseMediaType("text/csv"))
				.contentLength(responseText.length())
				.body(responseText);

	}

	



}
