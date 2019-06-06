<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- PieChart here -->
<c:if test="${viabilityDTO!=null}">

	<div class="container">
		<div class="row">

			<div id="totalChart" class="col-4">	</div>
			<script type="text/javascript">
				${viabilityDTO.totalChart}
			</script>

			<div id="maleChart" class="col-4 "></div>
			<script type="text/javascript">
				${viabilityDTO.maleChart}
			</script>


			<div id="femaleChart" class="col-4 "></div>
			<script type="text/javascript">
				${viabilityDTO.femaleChart}
			</script>

		</div>
	</div>



	<div class="row">
		<div class="col-md-12">
			<div class="row">
				<div class="col-md-4">

					<c:if test="${categoricalResultAndChart.combinedPValue!=null}">
						<h4> Results of statistical analysis  </h4>

						<dl class="alert alert-success">
							<dt>Combined Male and Female P value</dt>
							<dd><t:formatScientific>${categoricalResultAndChart.combinedPValue}</t:formatScientific></dd>

								<%-- <dt>Males only</dt>
                                <dd>${categoricalResultAndChart.malePValue}</dd>

                                <dt>Females only</dt>
                                <dd>${categoricalResultAndChart.femalePValue}</dd> --%>
						</dl>
					</c:if>


				</div>



				<div class="col-md-12">

					<table class="table table-striped small">
						<thead>
						<tr>
							<th></th>
							<th>WT</th>
							<th>Hom</th>
							<th>Het</th>
							<th>Total</th>
						</tr>
						</thead>
						<tbody>
						<tr>
							<th>Male and Female</th>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_004_001'].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_006_001'].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_005_001'].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_003_001'].dataPoint}"/></td>
						</tr>
						<tr>
							<th>Male</th>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_007_001'].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_009_001'].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_008_001'].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_010_001'].dataPoint}"/></td>
						</tr>
						<tr>
							<th>Female</th>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_011_001'].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_013_001'].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_012_001'].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation['IMPC_VIA_014_001'].dataPoint}"/></td>
						</tr>
						</tbody>
					</table>

				</div>

			</div>


			<div class="row mt-5">
				<div class="col-md-12">
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




</c:if>