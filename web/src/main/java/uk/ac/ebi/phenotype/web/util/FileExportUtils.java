package uk.ac.ebi.phenotype.web.util;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.generic.util.ExcelWorkBook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 2016/05/05
 * @author ilinca
 *
 */

public class FileExportUtils {

	
	private static final Logger log = LoggerFactory.getLogger(FileExportUtils.class.getCanonicalName());
	
	
	public static void writeOutputFile(HttpServletResponse response, List<String> dataRows, String fileType, String fileName, String filters)
	throws IOException, URISyntaxException {

		Workbook wb = null;
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

        String ext = fileType.toLowerCase();
        if (fileType.equals("xls")){
        	ext = "xlsx";
		}
		String outfile = escapeCharsFileName(fileName) + "." + ext;

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

			if (filters != null){
				dataRows.add(0, filters);
				dataRows.add(1, "");
			}

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
			
			ServletOutputStream output = response.getOutputStream();

            List<String> noTitleRows = null;
            if (! dataRows.isEmpty()) {
                noTitleRows = dataRows.subList(1,dataRows.size());
            }
            String[] titles = dataRows.get(0).split("\t");

            if (filters != null){
				wb = new ExcelWorkBook(titles, noTitleRows, fileName, filters).fetchWorkBook();
			}
			else {
				wb = new ExcelWorkBook(titles, noTitleRows, fileName).fetchWorkBook();
			}
			wb.write(output);
            output.close();
            System.out.println(outfile + " written successfully");
        }
		else if (fileType.equals("html")) {
			response.setContentType("text/html; charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=" + outfile);

			PrintWriter output = response.getWriter();

			String css = "<style>"
					+ "html {font-family: \"Source Sans Pro\",Arial,Helvetica,sans-serif}"
					+ "table {border-collapse: collapse;}"
					+ "table, th, td {border: 1px solid black;}"
					+ "caption {margin-bottom: 10px}"
					+ "th {padding: 0 5px;}"
					+ "td {font-size: 12px; padding: 0 3px; vertical-align: top;}"
					+ "li {list-style-type: none;}"
					+ "li.multi {list-style-type: square;}"
					+ "</style>";

			String caption = null;
			if (filters != null){
				caption = "<caption>" + filters + "</caption>";
			}

			String thead = null;
			String trs = "";
			int rownum = 0;
			System.out.println("rows: " + dataRows.size());
			long tstart = System.currentTimeMillis();

			List<String> noTitleRows = null;
			if (! dataRows.isEmpty()) {
				noTitleRows = dataRows.subList(1,dataRows.size());
			}

			String ths = "";
			for(String colname : StringUtils.split(dataRows.get(0), "\t") ){
				LinkedList<String> words = new LinkedList<String>();
				for (String w : colname.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
					words.add(w.toLowerCase());
				}
				ths += "<th>" + StringUtils.join(words," ") + "</th>";
			}
			thead = "<thead>" + ths + "</thead>";

			trs = StringUtils.join(noTitleRows, "");

			long tend = System.currentTimeMillis();
			System.out.println("done in " + (tend - tstart) + " msec");
			String html = "<html><head>" + css + "</head><table>" + caption + thead + "<tbody>" + trs + "</tbody></table></html>";
			output.println(html);
			output.flush();
			output.close();
		}
	}
	
	// ExcelWorkBook complains about some special characters, i.e. "/"
	private static String escapeCharsFileName(String fileName){
		return fileName.replaceAll("/", " ").replaceAll(",", "");
	}
	
}
