<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<!-- PieChart here -->
<c:if test="${embryoViabilityDTO!=null}">


	<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalEmbryos].dataPoint!=null && embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalEmbryos].dataPoint!=0}">
	<div id="totalChart" class="onethirdForPie ">	</div>
	<script type="text/javascript">
		${embryoViabilityDTO.totalChart}
	</script>
	</c:if>

	<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalDeadEmbryos].dataPoint!=null && embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalDeadEmbryos].dataPoint!=0}">
  	<div id="deadChart" class="onethirdForPie "></div>
  	<script type="text/javascript">
		${embryoViabilityDTO.deadChart}
	</script>
	</c:if>


	<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalLiveEmbryos].dataPoint!=null && embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalLiveEmbryos].dataPoint!=0}">
  	<div id="liveChart" class="onethirdForPie "></div>
  	<script type="text/javascript">
		${embryoViabilityDTO.liveChart}
	</script>
	</c:if>

	
	<table>
		<tr>
			<th></th>
			<th>WT</th>
			<th>Hom</th
			><th>Het</th>
			<th>Total</th>
		</tr>
		<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalEmbryos].dataPoint!=null}">
		<tr>
			<td>Total</td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalEmbryosWt].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalEmbryosHom].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalEmbryosHet].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalEmbryos].dataPoint}"/></td>
		</tr>
		</c:if>
		<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalDeadEmbryos].dataPoint!=null}">
		<tr>
			<td>Dead</td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalDeadEmbryosWt].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalDeadEmbryosHom].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalDeadEmbryosHet].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalDeadEmbryos].dataPoint}"/></td>
		</tr>
		</c:if>
		<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalLiveEmbryos].dataPoint!=null}">
		<tr>
			<td>Live</td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalLiveEmbryosWt].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalLiveEmbryosHom].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalLiveEmbryosHet].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalLiveEmbryos].dataPoint}"/></td>
		</tr>
		</c:if>
		<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalGrossDefect].dataPoint!=null}">
		<tr>
			<td>Gross defects at dissection<br />(alive or dead)</td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalGrossDefectWt].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalGrossDefectHom].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalGrossDefectHet].dataPoint}"/></td>
			<td><fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.totalGrossDefect].dataPoint}"/></td>
		</tr>
		</c:if>
	</table>

	<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.averageLitterSize].dataPoint!=null || embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.reabsorptionNumber].dataPoint!=null}">
	<table>
		<tr><th></th></tr>
		<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.reabsorptionNumber].dataPoint!=null}">
		<tr>
			<td>Number of Reabsorptions: <fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.reabsorptionNumber].dataPoint}"/></td>
		</tr>
		</c:if>
		<c:if test="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.averageLitterSize].dataPoint!=null}">
		<tr>
			<td>Average Litter Size: <fmt:formatNumber type="number" pattern="#####" value="${embryoViabilityDTO.paramStableIdToObservation[embryoViabilityDTO.parameters.averageLitterSize].dataPoint}"/></td>
		</tr>
		</c:if>
	</table>
	</c:if>

	<div>
		<p>
			<a class="toggle-button btn"> <i class="fa fa-caret-right"> </i> More Statistics </a>
		</p>

		<div class="toggle-div hidden">
			<p>
				<a href='${srUrl}'> Statistical result raw XML </a> &nbsp;&nbsp;
				<a href='${gpUrl}'> Genotype phenotype raw XML </a>&nbsp;&nbsp;
			</p>
		</div>
	</div>

</c:if>