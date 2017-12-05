/*******************************************************************************
 * Copyright 2017 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.phenotype.chart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mousephenotype.cda.db.beans.AggregateCountXYBean;
import org.mousephenotype.cda.enumerations.SignificantType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;



public class CmgProjectStatus {
	
	private int inProgress;
	private int produced;
	private int phenotyped;
	private int other;
	private List<int> data=new ArrayList<>();
	
	private static void getData (String fileName, String sheetName) {
		FileInputStream file = new FileInputStream(fileName);
		
		// Finds the workbook instance for XLSX file 
		XSSFWorkbook workBook = new XSSFWorkbook (file); 
		
		// Return first sheet from the XLSX workbook 
		XSSFSheet sheet = workBook.getSheetName(sheetName);
		
		// Get iterator to all the rows in current sheet 
		Iterator<Row> rowIterator = sheet.iterator();

		// Traversing over each row of XLSX file 
		while (rowIterator.hasNext()) { 
			Row row = rowIterator.next(); 
			
			// For each row, iterate through each columns 
			Iterator<Cell> cellIterator = row.cellIterator(); 
			while (cellIterator.hasNext()) { 
				Cell cell = cellIterator.next(); 
				switch (cell.getCellType()) { 
					case Cell.CELL_TYPE_STRING: 
						System.out.print(cell.getStringCellValue() + "\t"); 
						break; 
					case Cell.CELL_TYPE_NUMERIC: 
						System.out.print(cell.getNumericCellValue() + "\t"); 
						break; 
					case Cell.CELL_TYPE_BOOLEAN: 
						System.out.print(cell.getBooleanCellValue() + "\t"); 
						break; 
					default : 
				} 
			} 
			System.out.println(""); 
		}

	}
	
	private static String makeChart(Map<String, Integer> labelToNumber, String chartId, String title, String subtitle, List<String> colors) {
		String chart = "$(function () { $('#"+chartId+"').highcharts({ "
				 + " chart: { plotBackgroundColor: null, plotShadow: false}, "
				 + " colors:"+colors+", "
				 + " title: {  text: '"+title+"' }, "
				 + " subtitle: {  text: '"+subtitle+"' }, "
				 + " credits: { enabled: false }, "
				 + " tooltip: {  pointFormat: '{point.y}: <b>{point.percentage:.1f}%</b>'},"
				 + " plotOptions: { "
				 	+ "pie: { "
				 		+ "size: 200, "
				 		+ "allowPointSelect: true, "
				 		+ "cursor: 'pointer', "
				 		+ "dataLabels: { distance: 1, enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', "
				 		+ "style: { color: '#666', width:'60px' }  }  },"
				 	+ "series: {  "
				 	+ "dataLabels: { enabled: false },"
				 	+ "showInLegend: true"
				 //	+ "dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} "
				 	+ "}"
				 + " },"
				 + " series: [{  type: 'pie',   name: '',  "
				 + "data: [";
		for (Entry<String, Integer> entry : labelToNumber.entrySet()){
			chart+="['"+entry.getKey()+"', " +entry.getValue()+ " ],";
		}
		
		chart+=	"]}] }); });";

		return chart;
	}
}