<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage-landing>

	<jsp:attribute name="title">3i | Infection, Immunology, Immunophenotyping Project Information</jsp:attribute>
	<jsp:attribute name="pagename">Infection and Immunity Immunophenotyping (3i)</jsp:attribute>
	<jsp:attribute name="breadcrumb">3I</jsp:attribute>

	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
    </jsp:attribute>

	<jsp:attribute name="addToFooter">

		<script>
			$(document).ready(function() {
				$.fn.qTip({
					'pageName' : '3i',
					'textAlign' : 'left',
					'tip' : 'topLeft'
				}); // bubble popup for brief panel documentation
			});
			var geneHeatmapUrl = "../threeIMap?project=threeI";
			$.ajax({
				url : geneHeatmapUrl,
				cache : false
			}).done(function(html) {
				$('#3iHeatmap').append(html);
				//$( '#spinner'+ id ).html('');

			});
		</script>

    </jsp:attribute>


	<jsp:body>
    <!-- Assign this as a variable for other components -->
		<script type="text/javascript">
			var base_url = '${baseUrl}';
		</script>

		<div class="container">
			<div class="row align-items-center">
				<div class="col-9">
					<p> The <a href="http://www.immunophenotyping.org/">3i project</a> is building an encyclopaedia of
						immunological gene functions to advance basic and translational research. </p>
				</div>
				<div class="col-3">
					<a href="http://www.immunophenotyping.org/"><img src="${baseUrl}/img/3i.png" width="100%"></a>
				</div>
			</div>

			<div class="row">
				<div class="col-12">&nbsp;</div>
			</div>


			<div class="row">
				<div class="col-12">
					<h2 class="title">Gene to Procedure Phenodeviance Heat Map</h2>
					<table>
						<tr>
							<td>
								<div class="table_legend_color" style="background-color: rgb(191, 75, 50)"></div>
								<div class="table_legend_key">Deviance Significant</div>
							</td>
							<td>
								<div class="table_legend_color" style="background-color: rgb(247, 157, 70)"></div>
								<div class="table_legend_key">Data analysed, no significant call</div>
							</td>
							<td>
								<div class="table_legend_color" style="background-color: rgb(119, 119, 119)"></div>
								<div class="table_legend_key">Could not analyse</div>
							</td>
							<td>
								<div class="table_legend_color" style="background-color: rgb(230, 242, 246)"></div>
								<div class="table_legend_key">No data</div>
							</td>
						</tr>
					</table>
					<div id="3iHeatmap" style="overflow: hidden; overflow-x: auto;">	</div>
				</div>
			</div>
		</div>

</jsp:body>


</t:genericpage-landing>
