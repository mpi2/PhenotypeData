package uk.ac.ebi.phenotype.chart;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class CmgColumnChart {
	public static String getColumnChart(ArrayList<JSONObject> info_genes, String typeGenes, String chartId, String title, String subtitle) throws JSONException {
		int impc_with_phenotype = 0;
		int impc_in_production = 0;
		int total_genes = 0;
			
		for (JSONObject gene : info_genes) {
			String status = gene.getString("impc_status");
			if (typeGenes.equals("tier1")) {
				if (!gene.getString("tier_1_gene").equals("")) {
					if (status.equals("Genotype confirmed") || status.equals("Phenotype Attempt Registered") || status.equals("Rederivation Complete")) {
						impc_with_phenotype++; 
					} else if (status.equals("Phenotyping Complete") || status.equals("Phenotyping Started")) {
						impc_in_production++;
					} 
					total_genes++;
				}
			}
			else if (typeGenes.equals("tier2")) {
				if (!gene.getString("tier_2_gene").equals("")) {
					if (status.equals("Genotype confirmed") || status.equals("Phenotype Attempt Registered") || status.equals("Rederivation Complete")) {
						impc_with_phenotype++; 
					} else if (status.equals("Phenotyping Complete") || status.equals("Phenotyping Started")) {
						impc_in_production++;
					} 
					total_genes++;
				}
			}
		}
		
		return makeChart(impc_with_phenotype, impc_in_production, total_genes, chartId, title);
	}

	private static String makeChart(int impc_with_phenotype, int impc_in_production, int total_genes, String chartId, String title) {
		String chart = "$(function () { $('#" + chartId + "').highcharts({ "
				 + " chart: { type: 'column' }, "
				 + " colors: [ '#ef7b0b' ], "
				 + " title: { text: '" + title + "' }, "
				 + " subtitle: { text: 'Total: " + total_genes + " genes' }, "
				 + " xAxis: {"
				 	+ " categories: [ 'IMPC with phenotype', 'IMPC in production' ], "
			 		+ " crosshair: true, "
			 		+ " labels: { rotation: -45 } "
				 + " }, "
				 + " yAxis: { "
				 	+ " min: 0, "
				 	+ " title: { text: 'Number of genes' } "
				 + " }, "
				 + " tooltip: { "
				 	+ " headerFormat: '<span style=\"font-size:10px\">{point.key}</span><table>', " 
				 	+ " pointFormat: '<tr><td style=\"color:{series.color};padding:0\">{series.name}: </td>' + "
				 		+ " '<td style=\"padding:0\"><b> {point.y} </b></td> </tr>', "
				 	+ " footerFormat: '</table>', "
				 	+ " shared: true, "
				 	+ " useHTML: true}, "
				 + " plotOptions: { "
				 	+ " column: { pointPadding: 0.2, borderWidth: 0 } "
				 + " }, "
				 + " series: [{ "
				 	+ " name: 'Number of genes', " 
				 	+ " showInLegend: false, "
				 	+ " data: [ " + impc_with_phenotype + ", " + impc_in_production + " ] "
				 + " }] }); });";

		return chart;
	}
}
