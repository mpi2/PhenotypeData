<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


	<p class="resultCount">Total number of results: ${rows}</p>

	<!-- Associations table -->
	<c:if test="${chart != null}">
		<!-- chart here -->
		<div id="chartDiv"></div>
		<script type="text/javascript" async>${chart}</script>
	</c:if>

	<c:set var="count" value="0" scope="page" />

	<script>
		var resTemp = document.getElementsByClassName("resultCount");
		if (resTemp.length > 1){ resTemp[0].remove();}
	</script>

	<table id="strainPvalues">
		<thead>
			<tr>
				<th class="headerSort">Allele</th>
				<th class="headerSort">Center</th>
				<th class="headerSort">Procedure / Parameter</th>
				<th class="headerSort">Zygosity</th>
				<th class="headerSort">Mutants</th>
				<th class="headerSort">Statistical<br/>Method</th>
				<th class="headerSort">P Value</th>
				<th class="headerSort">Status</th>
				<th class="headerSort">Graph</th>
			</tr>
		</thead>

		<tbody>
			<c:forEach var="stableId" items="${experimentRows.keySet()}"
				varStatus="status">
				<c:set var="stableIdExperimentsRow" value="${experimentRows[stableId]}" />
				<c:forEach var="row" items="${stableIdExperimentsRow}">
					<tr>
						<td><t:formatAllele>${row.getAllele().getSymbol()}</t:formatAllele></td>
						<td>${row.getPhenotypingCenter()}</td>
						<td>${row.getProcedure().getName()} / ${row.getParameter().getName()}</td>
						<td>${row.getZygosity().substring(0,3).toUpperCase()}</td>
						<td>${row.femaleMutantCount}f:${pValueItem.maleMutantCount}m</td>
						<td>${row.getStatisticalMethod()}</td>
						<!-- pValue -->
						<c:choose>
							<c:when
								test="${ ! empty row && row.getStatus() == 'SUCCESS'}">
								<c:set var="paletteIndex" value="${row.colorIndex}" />
								<c:set var="Rcolor" value="${palette[0][paletteIndex]}" />
								<c:set var="Gcolor" value="${palette[1][paletteIndex]}" />
								<c:set var="Bcolor" value="${palette[2][paletteIndex]}" />
								<td style="background-color:rgb(${Rcolor},${Gcolor},${Bcolor})">
									<t:formatScientific> ${row.getPValue()}</t:formatScientific>
								</td>
							</c:when>
							<c:otherwise>
								<td><t:formatScientific>${row.getPValue()}</t:formatScientific></td>
							</c:otherwise>
						</c:choose>
						<td>${row.status}</td>
						<td style="text-align: center">
							<%--<a href='${baseUrl}/charts?accession=${pValueItem.getMarkerAccessionId()}&allele_accession_id=${pValueItem.getAlleleAccessionId()}&parameter_stable_id=${pValueItem.getParameterStableId()}&metadata_group=${pValueItem.metadataGroup}&zygosity=${pValueItem.zygosity}&phenotyping_center=${pValueItem.getPhenotypingCenter()}'>--%>
								<a href='${row.getGraphUrl()}'>
								<i class="fa fa-bar-chart-o" title="Graphs"> </i>
						</a></td>
					</tr>
				</c:forEach>
			</c:forEach>
		</tbody>
	</table>


		<script type="text/javascript">
			$(document).ready(function() {
			  var oTable = $('#strainPvalues').dataTable({
				//		"sPaginationType": "bootstrap"
						"bPaginate":false
			  });
			  // Sort immediately with p-value column starting with the lowest one
			  oTable.fnSort( [ [6,'asc'] ] );
			} );
		</script>
