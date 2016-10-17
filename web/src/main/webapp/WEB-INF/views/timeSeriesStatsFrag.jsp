<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- time series charts here-->
	<c:if test="${timeSeriesChartsAndTable.chart!=null}">
	
    <br/> <br/>
		<div id="timechart${experimentNumber}"> </div>		
		    
	  <div>
		<p>
			<a class="toggle-button btn"> <i class="fa fa-caret-right"> </i> More Statistics </a>
		</p>

		<div class="toggle-div hidden">
			<p>
				<a href="${srUrl}"> Statistical result raw XML </a> &nbsp;&nbsp;
				<a href="${gpUrl}"> Genotype phenotype raw XML </a>&nbsp;&nbsp;
				<a href="${baseUrl}${phenStatDataUrl}"> PhenStat-ready raw experiment data</a>
			</p>

			<table id="timeTable">
				<tr>
					<th>Time</th>
					<c:forEach var="lineMap" items="${timeSeriesChartsAndTable.lines}"
						varStatus="keyCount">
						<th>${lineMap.key}</th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach var="lineKey"
						items="${timeSeriesChartsAndTable.uniqueTimePoints}"
						varStatus="timeRow">
						<tr>
							<td>${lineKey}</td>
							<c:forEach var="lineMap" items="${timeSeriesChartsAndTable.lines}"
								varStatus="column">
	
								<td><c:if test="${lineMap.value[timeRow.index].discreteTime==lineKey}">
										${lineMap.value[timeRow.index].data} (${lineMap.value[timeRow.index].count})
									</c:if>
								</td>
	
							</c:forEach>
						</tr>
					</c:forEach>
				</tr>
			</table>
		</div>
		</div>

	
	<script type="text/javascript">
		
		${timeSeriesChartsAndTable.chart}

		$(document).ready(
				function() {
					// bubble popup for brief panel documentation - added here as in stats page it doesn't work
					$.fn.qTip({
						'pageName' : 'stats',
						'tip' : 'top right',
						'corner' : 'right top'
					});
				});
	</script>
</c:if>
	