<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<c:choose>


    <c:when test="${emptyExperiment}">
        <!-- <div class="alert alert-error">
        <strong>Error:</strong> experiment empty
        </div> -->
    </c:when>

    <c:otherwise>

        <c:if test="${ ! chartOnly}">

            <div class="container mt-3" id="section-associations">
                <%-- <div class="row">
                    <div class="col-12 no-gutters">
                        <h3>Allele -<t:formatAllele>${symbol}</t:formatAllele></h3>
                    </div>
                </div> --%>
            </div>

            <div class="container single single--no-side">
                <div class="row">
                    <div class="col-12 white-bg">
                        <div class="page-content pt-5 pb-5">
            <c:if test="${viabilityDTO!=null}">
                <h5>${viabilityDTO.category}</h5>
            </c:if>

            <c:if test="${embryoViabilityDTO!=null}">
                <h5>${embryoViabilityDTO.proceedureName}</h5>
                <h5>Outcome: ${embryoViabilityDTO.category}</h5>
            </c:if>

	<h3><a href="https://www.mousephenotype.org/impress/protocol/215">Procedure: ${parameter.procedureNames[0]}</a></h3>
	<h4><a href="https://www.mousephenotype.org/impress/parameterontologies/5553/215">Parameter: ${parameter.name}</a></h4>
	<h4>Allele: <t:formatAllele>${alleleSymbol}</t:formatAllele></h4>
            <p>Background - <t:formatAllele>${geneticBackgroundString}</t:formatAllele>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                Phenotyping Center - ${phenotypingCenter}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <c:if test="${pipeline.name!=null}">Pipeline - <a href="${pipelineUrl}">${pipeline.name }</a></c:if>
            </p>

            <p>
                <c:if test="${metadata != null}">
                    Metadata Group - ${metadata}
                </c:if>
            </p>


        </c:if>

        <c:choose>
            <c:when test="${param['chart_type'] eq 'UNIDIMENSIONAL_SCATTER_PLOT'}">
                <jsp:include page="scatterStatsFrag.jsp"/>
            </c:when>
            <c:when test="${param['chart_type'] eq 'UNIDIMENSIONAL_ABR_PLOT'}">
                <jsp:include page="abrFrag.jsp"/>
            </c:when>
            <c:when test="${viabilityDTO!=null}">
                <jsp:include page="pieFrag.jsp"/>
            </c:when>
            <c:when test="${embryoViabilityDTO!=null}">
                <jsp:include page="embryoViabilityPieFrag.jsp"/>
            </c:when>
            <c:when test="${fertilityDTO!=null}">
                <jsp:include page="fertPieFrag.jsp"/>
            </c:when>
            <c:otherwise>
                <jsp:include page="unidimensionalStatsFrag.jsp"/>
            </c:otherwise>
        </c:choose>

        <!-- only show scatter if scatter else only show unidimensional - otherwise we get unidimensional tables showing twice on the same page -->

        <jsp:include page="timeSeriesStatsFrag.jsp"/>

        <jsp:include page="categoricalStatsFrag.jsp"/>

    </c:otherwise>
</c:choose>
                        </div>
                    </div>
                </div>
            </div>

