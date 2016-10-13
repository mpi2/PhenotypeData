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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static org.apache.xmlbeans.impl.piccolo.xml.Piccolo.STRING;

// XSSF


/*
// HSSF
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
*/

/**
* Creating spreadsheet using Apache POI.
* code modified from Apache POI timesheet example
*/

public class ExcelWorkBook {

	XSSFWorkbook wb = null;
	// use XSSF (Excel 2007 and later) as HSSF excel(Excel 2003 and earlier) cannot handle > 65536 rows
	public ExcelWorkBook(String[] titles, List<String> noTitleRows, String fileName) throws URISyntaxException {
		
		this.wb = new XSSFWorkbook(); // create a blank workbook
		CreationHelper createHelper = wb.getCreationHelper();

		// create a new sheet
		XSSFSheet sheet = wb.createSheet(fileName); // create a blank spreadsheet with name
		XSSFPrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		   
		//header row
		XSSFRow headerRow = sheet.createRow(0);
		//headerRow.setHeightInPoints(40);
		   
		XSSFCell headerCell;
		for (int j = 0; j < titles.length; j++) {
			headerCell = headerRow.createCell(j);
			headerCell.setCellValue(titles[j]);
            //headerCell.setCellStyle(styles.get("header"));
		}
		// data rows
	    // Create a row and put some cells in it. Rows are 0 based.
	    // Then set value for that created cell
    	for (int k=0; k<noTitleRows.size(); k++) { // data does not contain title row
			XSSFRow row = sheet.createRow(k+1);
            String[] vals = noTitleRows.get(k).split("\\t");
    		for (int l = 0; l < vals.length; l++) {
    			XSSFCell cell = row.createCell(l);
    			String cellStr;
    			
    			try{
    				cellStr = vals[l];
    			}catch(Exception e){
    				cellStr = "";
    			}
    			
    			// make hyperlink in cell
    			if ( ( cellStr.startsWith("http://") || cellStr.startsWith("https://") ) && !cellStr.contains("|") ){
					//System.out.println("chk cellStr: " + cellStr);

					if ( cellStr.contains("?") ) {
						String[] parts = cellStr.split("\\?");
						String params = parts[1].replaceAll(":", "%3A");
						params = params.replaceAll("\"","%22");
						cellStr = parts[0] + "?" + params;
					}

					cellStr = cellStr.replaceAll(" ","%20");  // so that url link would work
				    cellStr = new URI(cellStr).toASCIIString();
    				cellStr = cellStr.replace("%3F","?");  // so that url link would work

    				XSSFHyperlink url_link = (XSSFHyperlink)createHelper.createHyperlink(Hyperlink.LINK_URL);
    				
    				url_link.setAddress(cellStr);
    				
                    cell.setCellValue(cellStr);         
                    cell.setHyperlink(url_link);
    			}
    			else if (cellStr.contains("|")){
					String[] cvals = cellStr.split("\\|");

					// maximum number of characters a cell can contain
					// but there is perhaps no need to show all of them
					//int len = cellStr.length() > 32766 ? 32765 : cellStr.length();
					//cell.setCellValue(cellStr.substring(0, len));

					//cell.setCellValue(cvals[0]); // just take the first one and omit the rest

					// show max of 20 values
					int defaultValNum = cvals.length > 20 ? 20 : cvals.length;
					List<String> shownVal = new ArrayList<>();
					for ( int n=0; n<defaultValNum; n++){
						shownVal.add(cvals[n]);
					}
					cell.setCellValue(StringUtils.join(shownVal, "|"));
				}
    			else {
    				cell.setCellValue(cellStr);  
    			}
//				System.out.println(cell.getStringCellValue());
    			//System.out.println((String)tableData[k][l]);
    		}
    	}    
	}

	public XSSFWorkbook fetchWorkBook() {
		return this.wb;
	}	
}

