<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    <jsp:attribute name="title">International Mouse Phenotyping Consortium Documentation</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/documentation/index">Documentation</a></jsp:attribute>
    <jsp:attribute name="bodyTag"><body class="page-node searchpage one-sidebar sidebar-first small-header"></body></jsp:attribute>
    <jsp:attribute name="addToFooter"></jsp:attribute>
    <jsp:attribute name="header"></jsp:attribute>

    <jsp:body>

        <div id="wrapper">

            <div id="main">
                <!-- Sidebar First -->
                <jsp:include page="doc-menu.jsp"></jsp:include>

                <!-- Maincontent -->

                <div class="region region-content">

                    <div class="block block-system">

                        <div id="top" class="content node">

                            <h3>More information about the IMPC RESTful API.</h3>

                            <p>The IMPC offers the following RESTful APIs for consuming data:

                            <h4 id="genetype-phenotype">Genotype-Phenotype API</h4>
                            <p>Please see the <a href="data-access-api-genotype-phenotype">Genotype-Phenotype API documentation</a> </p>

                            <h4 id="experiment">Experimental observation API</h4>
                            <p>Please see the <a href="data-access-api-observation">Experimental observation API documentation</a> </p>

                            <h4 id="statistical-results">Statistical results API</h4>
                            <p>Please see the <a href="data-access-api-statistical-result">Statistical results API documentation</a> </p>

                            <h4 id="impc-images">IMPC images API</h4>
                            <p>Please see the <a href="impc-images-api-help">IMPC images API documentation</a> </p>


                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>