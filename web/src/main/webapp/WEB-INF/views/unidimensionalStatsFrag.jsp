<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:if test="${fn:length(unidimensionalChartDataSet.statsObjects)>1}">
<c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>
${data.mpTermId}
</c:if>
<!-- unidimensional here -->
<c:if test="${unidimensionalChartDataSet!=null}">
	
	<div class="row">


	<div id="chart${experimentNumber}" class="col-md-4"></div>
	<div id="scatter${experimentNumber}" class="col-md-8"></div>
	<script type="text/javascript">
		${scatterChartAndData.chart};
		$(function () {${unidimensionalChartDataSet.chartData.chart}
	</script>

	</div>
	

</c:if>

<c:if test="${imageCountMax > 0}">
<br />
<h2>Flow cytometry results:</h2>
	<div class="row">

		<c:forEach begin="0" end="${imageCountMax}" var="i">
                <c:if test="${fn:length(controlImages) > i}">
                    <t:headline_image img="${controlImages[i]}" impcMediaBaseUrl="${impcMediaBaseUrl}"/>
                </c:if>
                <c:if test="${fn:length(mutantImages) > i}">
                    <t:headline_image img="${mutantImages[i]}" impcMediaBaseUrl="${impcMediaBaseUrl}"/>
                </c:if>
        </c:forEach>

	</div>
</c:if>

<jsp:include page="unidimensionalTables.jsp"></jsp:include>



