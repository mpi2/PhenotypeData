<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Allele details </jsp:attribute>
    <jsp:attribute name="header"/>
    <jsp:attribute name="breadcrumb" />
    <jsp:attribute name="bodyTag"><body class="gene-node no-sidebars small-header"></jsp:attribute>
    <jsp:attribute name="addToFooter"/>

    <jsp:body>

        <c:set var="selectUrl" value='${baseUrl}/gene/allele2?kw="${acc}"'/>
        <c:if test="${creLine}">
            <c:set var="selectUrl" value="${baseUrl}/order/creline?acc=${acc}"/>
        </c:if>


        <div class="container data-heading">
            <div class="row row-shadow">
                <div class="col-12 no-gutters">
                    <h2>Allele ${title}</h2>
                </div>
            </div>
        </div>


        <div id="allele-page">
            <div class="container">
                <div>
                    <div class="breadcrumbs m-0">
                        <div class="row">
                            <div class="col-md-12">
                                <p><a href="/">Home</a> <span><span class="fal fa-angle-right"></span></span>
                                    <a href="${baseUrl}/search">Genes</a> <span
                                            class="fal fa-angle-right"></span>
                                    <a href="${baseUrl}/genes/${acc}">${summary['marker_symbol']}</a> <span
                                            class="fal fa-angle-right"></span>
                                        ${title}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="pre-content">
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
                                            test="${creLine}">Cre </c:if>Alleles for ${summary['marker_symbol']}</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>
