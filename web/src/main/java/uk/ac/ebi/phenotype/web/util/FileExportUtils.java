package uk.ac.ebi.phenotype.web.util;

import org.apache.poi.ss.usermodel.Workbook;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.generic.util.ExcelWorkBook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @since 2016/05/05
 * @author ilinca
 *
 */

public class FileExportUtils {


	private static final Logger log = LoggerFactory.getLogger(FileExportUtils.class.getCanonicalName());


	public static void writeOutputFile(HttpServletResponse response, List<String> dataRows, String fileType, String fileName)
	throws IOException, URISyntaxException {

		Workbook wb;
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		String outfile = fileName + "." + fileType;

		if (fileType.equals("tsv")) {

			response.setContentType("text/tsv; charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=" + outfile);

			/* ServletOutputStream output = response.getOutputStream();
			 *
			 * CKC NOTE: switch to use getWriter() so that we don't get error like
			 * java.io.CharConversionException: Not an ISO 8859-1 character
			 * and if we do, the error will cause the dump to end prematurely
			 * and we may not get the full rows (depending on which row causes error)
			 *
			 */

			PrintWriter output = response.getWriter();

			for (String line : dataRows) {

				line = line.replaceAll("\\t//", "\thttp://");
				line = line.replaceAll("\\|//", "|http://");

				output.println(line);
			}

			output.flush();
			output.close();

		} else if (fileType.equals("xls")) {

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename=" + outfile);

			String[] titles = null;
			String[][] tableData = new String[0][0];
			ServletOutputStream output = response.getOutputStream();

			if (!dataRows.isEmpty()) {
				titles = dataRows.get(0).split("\t");
				tableData = Tools.composeXlsTableData(dataRows);
			}

			wb = new ExcelWorkBook(titles, tableData, escapeCharsFileName(fileName)).fetchWorkBook();
			wb.write(output);
			output.close();

		}

	}

	// ExcelWorkBook complains about some special characters, i.e. "/"
	private static String escapeCharsFileName(String fileName){
		return fileName.replaceAll("/", " ");
	}

}
