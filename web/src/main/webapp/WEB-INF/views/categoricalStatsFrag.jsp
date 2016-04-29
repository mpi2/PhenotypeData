<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<!-- categorical here -->


<c:forEach var="categoricalChartDataObject"	items="${categoricalResultAndChart.maleAndFemale}"	varStatus="chartLoop">

	<div id="chart${experimentNumber}"></div>
	<script type="text/javascript">	${categoricalChartDataObject.chart} </script>

	<div style="overflow: hidden; overflow-x: auto;">
		<c:forEach var="result"	items="${categoricalResultAndChart.statsResults}">
			<c:if test="${result.status ne 'Success'}">
				<div class="alert">
					<strong>Statistics ${result.status}. Control Sex ${result.controlSex} Experimental Sex ${result.experimentalSex}</strong>
				</div>
			</c:if>

		</c:forEach>
		<table id="catTable">
			<thead>
				<tr>
					<th>Control/Hom/Het</th>
					<c:forEach var="categoryObject"	items="${categoricalResultAndChart.maleAndFemale[0].categoricalSets[0].catObjects}"	varStatus="categoriesStatus">
						<th>${categoryObject.category}</th>
					</c:forEach>
					<th>P Value</th>
					<th>Effect Size</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="maleOrFemale" items="${categoricalResultAndChart.maleAndFemale}" varStatus="maleOrFemaleStatus">
					<c:forEach var="categoricalSet"	items="${maleOrFemale.categoricalSets}" varStatus="catSetStatus">
						<tr>
							<td>${categoricalSet.name }</td>
							<c:forEach var="catObject" items="${categoricalSet.catObjects}"	varStatus="catObjectStatus">
								<td>${catObject.count }</td>
							</c:forEach>
							<td>${categoricalSet.catObjects[0].pValue }</td>
							<td>${categoricalSet.catObjects[0].maxEffect }</td>
						</tr>
					</c:forEach>

				</c:forEach>

			</tbody>
		</table>
	</div>
</c:forEach>

<script>
 	$(document).ready(
		function() {
		 	$.fn.qTip({
						'pageName': 'stats',							
						'tip': 'top right',
						'corner' : 'right top'
			}); 
 	});
</script>
