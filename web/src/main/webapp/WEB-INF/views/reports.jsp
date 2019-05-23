<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Data Reports</jsp:attribute>
	
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Reports</jsp:attribute>

	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>
	
	<jsp:body>
			<div class="region region-content">
				<div class="block block-system">
					<div class="content">
						<div class="node node-gene">
							<h1 class="title" id="top">IMPC Data Reports</h1>	 
				
							<div class="section">
								<div class="inner">
									<h4>General Reports</h4>
									<table>
										<thead>
											<tr><td>Report Link</td><td>Description</td></tr>
										</thead>
										<tbody>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/csv/ALL_genotype_phenotype.csv.gz">ALL_genotype_phenotype.csv.gz</a></td>
												<td>All genotype-phenotype data (compressed)</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/bmdStatsGlucoseConcentrationReport.csv">bmdStatsGlucoseConcentrationReport.csv</a></td>
												<td>BMD - Fasted blood glucose concentration</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/bmdStatsGlucoseResponseReport.csv">bmdStatsGlucoseResponseReport.csv</a></td>
												<td>BMD - Area under the curve glucose response</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/bmdStatsReport.csv">bmdStatsReport.csv</a></td>
												<td>BMD - Bone Mineral Content, excluding skull</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/dataOverviewReport.csv">dataOverviewReport.csv</a></td>
												<td>Data overview</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/fertilityReport.csv">fertilityReport.csv</a></td>
												<td>Fertility</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/hitsPerLineReport.csv">hitsPerLineReport.csv</a></td>
												<td>Hits per line</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/impcGafReport.csv">impcGafReport.csv</a></td>
												<td>IMPC GAF 2.0</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/impcPValuesReport.csv">impcPValuesReport.csv</a></td>
												<td>IMPC P-Values</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/laczExpressionReport.csv">laczExpressionReport.csv</a></td>
												<td>lacZ expression</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/metabolismCalorimetryReport.csv">metabolismCalorimetryReport.csv</a></td>
												<td>Metabolism calorimetry</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/metabolismCBCReport.csv">metabolismCBCReport.csv</a></td>
												<td>Metabolism Clinical blood chemistry (CBC)</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/metabolismDEXAReport.csv">metabolismDEXAReport.csv</a></td>
												<td>Metabolism Body composition Dual Energy X-ray Absorptiometry (DEXA lean/fat)</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/metabolismIPGTTReport.csv">metabolismIPGTTReport.csv</a></td>
												<td>Metabolism Intraperitoneal glucose tolerance (IPGTT)</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/phenotypeHitsReport.csv">phenotypeHitsReport.csv</a></td>
												<td>Phenotype hits</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/phenotypeOverviewPerGeneReport.csv">phenotypeOverviewPerGeneReport.csv</a></td>
												<td>Phenotype overview per gene</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/procedureCompletenessImpcReport.csv">procedureCompletenessImpcReport.csv</a></td>
												<td>Procedure completeness Impc datasource/td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/procedureCompletenessAllReport.csv">procedureCompletenessAllReport.csv</a></td>
												<td>Procedure completeness all data sources</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/sexualDimorphismNoBodyWeightReport.csv">sexualDimorphismNoBodyWeightReport.csv</a></td>
												<td>Sexual dimorphism - no body weight</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/sexualDimorphismWithBodyWeightReport.csv">sexualDimorphismWithBodyWeightReport.csv</a></td>
												<td>Sexual dimorphism - with body weight</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/viabilityReport.csv">viabilityReport.csv</a></td>
												<td>Viability</td>
											</tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/zygosityReport.csv">zygosityReport.csv</a></td>
												<td>Zygosity</td>
											</tr>
										</tbody>
									</table>

									<hr />

									The <a href="ftp://ftp.ebi.ac.uk/pub/databases/impc">EBI ftp server</a> offers current and previous versions of data for download.
									Links to the latest version of the following artifacts may be found below.
									<table>
										<thead><tr><td>Artifact</td><td>Description</td></tr></thead>
										<tbody>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/cores">cores</a></td><td>The cores used to build the data release</td></tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/csv">csv</a></td><td>Full-data summary reports</td></tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/mysql">mysql</a></td><td>The compressed komp2 database</td></tr>
											<tr><td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports">reports</a></td><td>The general reports</td></tr>
										</tbody>
									</table>

								</div>
							</div>
							
					</div>
				</div>
			</div>
		</div>
		
		
		
	</jsp:body>
		
	</t:genericpage>
