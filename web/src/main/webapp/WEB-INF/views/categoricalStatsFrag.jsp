<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<!-- categorical here -->


<c:forEach var="categoricalChartDataObject"	items="${categoricalResultAndChart.maleAndFemale}"	varStatus="chartLoop">

	<div id="chart${experimentNumber}"></div>
	
	<a id="checkAll" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
    <a id="uncheckAll"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
    
    <div class="clear both"></div>
	<script type="text/javascript">	${categoricalChartDataObject.chart} </script>

	<div class="row">
            <div class="col-8">
		<c:forEach var="result"	items="${categoricalResultAndChart.statsResults}">
			<c:if test="${result.status ne 'Success'}">
				<div class="alert">
					<strong>Statistics ${result.status}. Control Sex ${result.controlSex} Experimental Sex ${result.experimentalSex}</strong>
				</div>
			</c:if>

		</c:forEach>
		<table class="table table-striped small">
			<thead>
				<tr>
					<th>Control/Hom/Het</th>
					<c:forEach var="categoryObject"	items="${categoricalResultAndChart.maleAndFemale[0].categoricalSets[0].catObjects}"	 varStatus="categoriesStatus">
						<th>${categoryObject.category}</th>
					</c:forEach>
					<th>P Value</th>
					<%-- <th>Effect Size</th> --%>
				</tr>
			</thead>
			<tbody>
			<c:set var="emptyColumnNumber" value="0" scope="page"/>
				<c:forEach var="maleOrFemale" items="${categoricalResultAndChart.maleAndFemale}" varStatus="maleOrFemaleStatus">
					<c:forEach var="categoricalSet"	items="${maleOrFemale.categoricalSets}" varStatus="catSetStatus">
						<tr>
							<td>${categoricalSet.name }</td>
							<c:forEach var="catObject" items="${categoricalSet.catObjects}"	varStatus="catObjectStatus">
								<td>${catObject.count }</td>
							</c:forEach>
							<td>${categoricalSet.catObjects[0].pValue }</td>
							<%-- <td>${categoricalSet.catObjects[0].maxEffect }</td> removed effect size as per Terrys request --%>
						</tr>	
					</c:forEach>

				</c:forEach>
				
				<c:if test="${categoricalResultAndChart.combinedPValue!=null && categoricalResultAndChart.combinedPValue!=0.0}">
				<tr>
					<td>Combined Male and Female Analysis</td>
					<c:forEach begin="1" end="${fn:length(categoricalResultAndChart.maleAndFemale[0].categoricalSets[0].catObjects)}" varStatus="loop">
					<td></td>
					</c:forEach>
					<td>${categoricalResultAndChart.combinedPValue}</td></tr> 
				</c:if>

			</tbody>
		</table>

		<div>
		
		<div class="row mt-5">
                    <div class="col-12">
                        <h4> Access the results programmatically </h4>
                        <hr>
                        <p>
                            <a target="_blank" class="btn btn-outline-primary btn-sm" href='${srUrl}'> Statistical result raw XML </a>
                            <a target="_blank" class="btn btn-outline-primary btn-sm" href='${gpUrl}'> Genotype phenotype raw XML </a>
                            <a target="_blank" class="btn btn-outline-primary btn-sm" href='${baseUrl}${phenStatDataUrl}'> PhenStat-ready raw experiment data</a>
                        </p>
                    </div>
                </div>
			
		</div>
	</div>
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
