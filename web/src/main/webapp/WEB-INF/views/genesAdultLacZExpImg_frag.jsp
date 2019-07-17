<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 16:45
  To change this template use File | Settings | File Templates.
--%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<!-- <h2 class="title" id="section-impc_expression">Expression Data<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
-->

<div class="tz-gallery small">
    <div class="container p-md-0">
        <div class="heading">
            <h5>LacZ Wholemount Images</h5>
        </div>
        <div class="row">
            <c:forEach var="entry" items="${wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms}"
                       varStatus="status">
                <c:set var="href"
                       scope="page"

                       value="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.name}&parameter_stable_id=IMPC_ALZ_076_001"></c:set>
                <t:impcimgdisplay2
                        category="${entry.name} (${entry.count})"
                        href="${fn:escapeXml(href)}"
                        img="${wholemountExpressionImagesBean.expFacetToDocs[entry.name][0]}"
                        impcMediaBaseUrl="${impcMediaBaseUrl}"
                >
                </t:impcimgdisplay2>
            </c:forEach>

        </div>
    </div>
</div>


<div class="tz-gallery small">
    <div class="container p-md-0">
        <div class="heading">
            <h5>LacZ Section Images</h5>
        </div>
        <div class="row">
            <c:forEach var="entry" items="${sectionExpressionImagesBean.filteredTopLevelAnatomyTerms}"
                       varStatus="status">
                <c:set var="href"
                       scope="page"

                       value="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.name}&parameter_stable_id=IMPC_ALZ_075_001"></c:set>
                <t:impcimgdisplay2
                        category="${entry.name} (${entry.count})"
                        href="${fn:escapeXml(href)}"
                        img="${sectionExpressionImagesBean.expFacetToDocs[entry.name][0]}"
                        impcMediaBaseUrl="${impcMediaBaseUrl}"
                >
                </t:impcimgdisplay2>
            </c:forEach>

        </div>
    </div>
</div>

<c:if test="${not empty expressionFacets}">
    <div class="container p-0 p-md-2">
        <div class="heading row">
            <h5>LacZ Section Images</h5>
        </div>

        <div class="row accordion mb-3" id="secondaryExpressionAccordion">
            <div class="list-group col-12">
                <c:forEach var="entry" items="${expressionFacets}" varStatus="status">
                    <div id="heading${status.index}">
                        <button class="btn btn-link list-group-item list-group-item-action"
                                type="button"
                                data-toggle="collapse"
                                data-target="#collapse${status.index}" aria-expanded="false"
                                aria-controls="collapse${status.index}">
                                ${entry.name} (${entry.count})
                        </button>
                    </div>

                    <div id="collapse${status.index}" class="collapse"
                         aria-labelledby="heading${status.index}"
                         data-parent="#secondaryExpressionAccordion">
                        <div class="tz-gallery small">
                            <div class="container">
                                <div class="row">
                                    <c:forEach var="doc" items="${expFacetToDocs[entry.name]}">

                                        <t:imgdisplay
                                                img="${doc}"
                                                mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>

                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                        <c:if test="${entry.count>5}">
                            <p class="text-right">
                                <a href='${baseUrl}/images?gene_id=${acc}&fq=sangerProcedureName:"Wholemount Expression"&fq=selected_top_level_ma_term:"${entry.name}"'>View
                                    all ${entry.count} images</a>
                            </p>
                        </c:if>
                    </div>
                </c:forEach>
            </div>


            <!-- thumbnail scroller markup begin -->

                <%--</div>--%>

        </div>
    </div>

</c:if>



