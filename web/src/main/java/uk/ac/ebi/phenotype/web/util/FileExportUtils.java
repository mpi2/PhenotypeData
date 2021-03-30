package uk.ac.ebi.phenotype.web.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.web.exporter.Exporter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 2016/05/05
 * @author ilinca
 *
 */

// fixme fixme fixme Callers should replace this call by the Exporter class and Exportable implementations instead of using FileExportUtils.
@Deprecated
public class FileExportUtils {

	
	private static final Logger log = LoggerFactory.getLogger(FileExportUtils.class.getCanonicalName());
	
	
	public static void writeOutputFile(HttpServletResponse response, List<String> dataWithHeadings, String fileType, String fileName, String filters) throws IOException {

		if ((dataWithHeadings == null) || (dataWithHeadings.isEmpty())) {
			return;
		}

		if (fileType.equals("html")) {
			handleLegacyHtmlFiletype(response, dataWithHeadings, fileType, fileName, filters);
			return;
		}


		/*
		 * Ignore filters (not supported after 04-July-2019) to promote a cleaner interface. dataRows is a legacy
		 * {@link List} of rows containing tab-separated column cell String values.
		 */

		List<String>       headings = Arrays.asList(dataWithHeadings.get(0).split("\t"));
		List<String> dataWithoutHeadings = dataWithHeadings.subList(1, dataWithHeadings.size());
		List<List<String>> data     = new ArrayList<>();
		dataWithoutHeadings
				.forEach(s -> {
					data.add(new ArrayList<>(Arrays.asList(s.split("\t"))));
				});

		Exporter.export(response, fileType, fileName, headings, data);
	}

	private static void handleLegacyHtmlFiletype(HttpServletResponse response, List<String> dataRows, String fileType, String fileName, String filters) throws IOException {
		response.setContentType("text/html; charset=utf-8");
		response.setHeader("Content-disposition", "attachment; filename=" + escapeCharsFileName(fileName) + "." + fileType.toLowerCase());

		PrintWriter output  = response.getWriter();

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
		if (filters != null) {
			caption = "<caption>" + filters + "</caption>";
		}

		String thead  = null;
		String trs    = "";
		int    rownum = 0;
		System.out.println("rows: " + dataRows.size());
		long tstart = System.currentTimeMillis();

		List<String> noTitleRows = null;
		if (!dataRows.isEmpty()) {
			noTitleRows = dataRows.subList(1, dataRows.size());
		}

		String ths = "";
		for (String colname : StringUtils.split(dataRows.get(0), "\t")) {
			LinkedList<String> words = new LinkedList<String>();
			for (String w : colname.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
				words.add(w.toLowerCase());
			}
			ths += "<th>" + StringUtils.join(words, " ") + "</th>";
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
	
	// ExcelWorkBook complains about some special characters, i.e. "/"
	private static String escapeCharsFileName(String fileName){
		return fileName.replaceAll("/", " ").replaceAll(",", "");
	}
}