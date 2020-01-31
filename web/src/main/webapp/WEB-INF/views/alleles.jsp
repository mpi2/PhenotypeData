<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Allele details </jsp:attribute>
    <jsp:attribute name="header"/>
    <jsp:attribute name="breadcrumb"/>
    <jsp:attribute name="bodyTag"><body class="gene-node no-sidebars small-header"></jsp:attribute>
    <jsp:attribute name="addToFooter"/>

    <jsp:body>

        <c:set var="selectUrl" value='${baseUrl}/genes/${acc}#order'/>
        <c:if test="${creLine}">
            <c:set var="selectUrl" value="${baseUrl}/order/creline?acc=${acc}"/>
        </c:if>


        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Allele ${title}</h2>
                </div>
            </div>
        </div>


        <div id="allele-page" class="container white-bg-small">
            <div class="breadcrumbs clear row">
                <div class="col-12 d-none d-lg-block px-5 pt-5">
                    <aside><a href="/">Home</a> <span><span class="fal fa-angle-right"></span></span>
                        <a href="${baseUrl}/search">Genes</a> <span
                                class="fal fa-angle-right"></span>
                        <a href="${baseUrl}/genes/${acc}">${summary['marker_symbol']}</a> <span
                                class="fal fa-angle-right"></span>
                            ${title}
                    </aside>
                </div>
                <div class="col-12 d-block d-lg-none px-3 px-md-5 pt-5">
                    <aside>
                        <a href="${baseUrl}/genes/${acc}"><span
                                class="fal fa-angle-left mr-2"></span> ${summary['marker_symbol']}</a>
                    </aside>
                </div>
            </div>
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="row no-gutters">
                                <div class="col-12 px-0">

                                    <div class="row">
                                        <div class="col-12 px-5">
                                            <jsp:include page="alleles_summary_frag.jsp"/>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-12 px-5">
                                            <jsp:include page="alleles_mice_frag.jsp"/>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-12 px-5">
                                            <jsp:include page="alleles_es_cells_frag.jsp"/>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-12 px-5">
                                            <jsp:include page="alleles_targeting_vectors_frag.jsp"/>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-12 px-5 my-5">

                                            <a class="btn btn-success" href="${fn:escapeXml(selectUrl)}">See all <c:if
                                                    test="${creLine}">Cre </c:if>Alleles
                                                for ${summary['marker_symbol']}</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>
