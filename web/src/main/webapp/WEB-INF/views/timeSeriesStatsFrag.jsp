<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- time series charts here-->
	<c:if test="${timeSeriesChartsAndTable.chart!=null}">

		<div class="row">

			<div class="col-md-12">
				<div id="timechart${experimentNumber}"></div>
			</div>
		</div>

	  <div class="row">
		<div class="col-md-12">

			<%--<p>--%>
			<%--<c:if test="${isDerivedBodyWeight}"> <!-- these don't work for IPGTT -->--%>
				<%--<a href='${srUrl}'> Statistical result raw XML </a> &nbsp;&nbsp;--%>
				<%--<a href='${gpUrl}'> Genotype phenotype raw XML </a>&nbsp;&nbsp;--%>
			<%--</c:if>--%>
			<%----%>
				<%--<a href='${baseUrl}${phenStatDataUrl}'> PhenStat-ready raw experiment data</a>--%>
			<%--</p>--%>

			<table class="table table-striped">

				<c:choose>
						<c:when test="${isDerivedBodyWeight}">

				<tr>
					<th>Week</th>
					<c:forEach var="lineMap" items="${timeSeriesChartsAndTable.lines}"
						varStatus="keyCount">
						<th>${lineMap.key}</th>
					</c:forEach>
				</tr>
					<c:forEach begin="${timeSeriesChartsAndTable.minWeek}"
							   end="${timeSeriesChartsAndTable.maxWeek}"
							   varStatus="week"
					>


						<!-- This is the loop -->
						<tr><td>${week.index}</td>
							<c:forEach var="timeRow" items="${timeSeriesChartsAndTable.lines}" varStatus="column">
								<c:set var="data" value="false"></c:set>

								<td>
								<c:forEach var="values" items="${timeRow.value}" varStatus="valueIndex">

								<%--value = ${values}--%>
										<c:if test="${values.discreteTime==week.index}">
										${values.data} (${values.count})
											<c:set var="data" value="true"></c:set>
									</c:if>

								</c:forEach>

									<c:if test="${data!=true}">-</c:if>

								</td>
							</c:forEach>
						</tr>


					<%--<tr>--%>
						<%--<td>${lineKey}</td>--%>
						<%--<c:forEach var="timeRow" items="${timeSeriesChartsAndTable.lines}"--%>
								   <%--varStatus="column">--%>

							<%--<td><c:if test="${lineMap.value[loop.index].discreteTime==lineKey}">--%>
								<%--${lineMap.value[loop.index].data} (${lineMap.value[loop.index].count})--%>
							<%--</c:if>--%>
							<%--</td>--%>

						<%--</c:forEach>--%>
					<%--</tr>--%>

					</c:forEach>
						</c:when>
				<c:otherwise>

					<tr>
						<th class="capitalize">${timeSeriesChartsAndTable.parameter.unitX}</th>
						<c:forEach var="lineMap" items="${timeSeriesChartsAndTable.lines}"
								   varStatus="keyCount">
							<th>${lineMap.key}</th>
						</c:forEach>
					</tr>

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
				</c:otherwise>

				</c:choose>
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
	