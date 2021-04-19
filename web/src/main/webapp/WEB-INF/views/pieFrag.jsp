<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- PieChart here -->
<c:if test="${viabilityDTO!=null}">

	<div class="container">
		<div class="row">

			<div id="totalChart-${viabilityDTO.parameterStableId}" class="col-4">	</div>
			<script type="text/javascript">
				${viabilityDTO.totalChart}
			</script>

			<div id="maleChart-${viabilityDTO.parameterStableId}" class="col-4 "></div>
			<script type="text/javascript">
				${viabilityDTO.maleChart}
			</script>


			<div id="femaleChart-${viabilityDTO.parameterStableId}" class="col-4 "></div>
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
							<th>Het</th>
							<th>Hom</th>
							<th>Hemi</th>
							<th>Total</th>
						</tr>
						</thead>
						<tbody>
						<tr>
							<th>Male and Female</th>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalPupsWt].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalPupsHet].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalPupsHom].dataPoint}"/></td>
<%--							<c:if test="${element['class'].simpleName == 'ViabilityDTOVersion2'}">--%>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalMaleHem].dataPoint}"/></td>
<%--							</c:if>--%>
<%--							<c:if test="${element['class'].simpleName == 'ViabilityDTO'}">--%>
<%--								<td>N/A</td>--%>
<%--							</c:if>--%>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalPups].dataPoint}"/></td>
						</tr>
						<tr>
							<th>Male</th>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalMaleWt].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalMaleHet].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalMaleHom].dataPoint}"/></td>
<%--	<c:if test="${element['class'].simpleName == 'ViabilityDTOVersion2'}">--%>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalMaleHem].dataPoint}"/></td>
<%--	</c:if>--%>
<%--							<c:if test="${element['class'].simpleName == 'ViabilityDTO'}">--%>
<%--								<td>N/A</td>--%>
<%--							</c:if>--%>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalMalePups].dataPoint}"/></td>
						</tr>
						<tr>
							<th>Female</th>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalFemaleWt].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalFemaleHet].dataPoint}"/></td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalFemaleHom].dataPoint}"/></td>
							<td>N/A</td>
							<td><fmt:formatNumber type="number" pattern="#####" value="${viabilityDTO.paramStableIdToObservation[viabilityDTO.totalFemalePups].dataPoint}"/></td>
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
						<a target="_blank" class="btn btn-outline-dark btn-sm" data-toggle="tooltip" data-placement="top" title="Data not available for this procedure"> Statistical result raw XML </a>
						<a target="_blank" class="btn btn-outline-primary btn-sm" href='${gpUrl}'> Genotype phenotype raw XML </a>
						<a target="_blank" class="btn btn-outline-dark btn-sm" data-toggle="tooltip" data-placement="top" title="Viability data is not processed using PhenStat"> PhenStat-ready raw experiment data</a>
					</p>
				</div>
			</div>

		</div>
	</div>

<script>
	$(function () {
		$('[data-toggle="tooltip"]').tooltip()
	})
</script>


</c:if>
