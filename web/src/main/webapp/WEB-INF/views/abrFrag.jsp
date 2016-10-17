<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<!-- categorical here -->

<c:if test="${abrChart != null}">

	<div id="abrChart${experimentNumber}"></div>
	<script type="text/javascript">
		${abrChart}
	</script>

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
		</div>
	</div>


</c:if>
