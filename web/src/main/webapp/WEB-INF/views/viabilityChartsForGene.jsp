<!-- copy and paste this page into another for a jsp page with header and footer with navigation menus at top -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:genericpage>
<jsp:attribute name="title">
Viability Charts for Gene blah
<!--Default title IMPC-  Your Title Goes here in Plain Text -->
</jsp:attribute>
    <jsp:attribute name="header">
<!--    extra header stuff goes here such as extra page specific javascript and css -->

    </jsp:attribute>
    <jsp:attribute name="footer">
<!--      Anything extra to go in the footer goes here -->
    </jsp:attribute>
    <jsp:body>
        <!--         main body content goes here -->
        <div class="container data-heading">
            <div class="row row-shadow">
                <div class="col-12 no-gutters">
                    <h2>${markerSymbol} Viability Data Charts</h2>
                </div>
            </div>
        </div>


        <div class="row">
            <div class="col-12 col-md-12">
                <div class="pre-content clear-bg">
                    <div class="page-content people white-bg">
                        <div class="breadcrumbs clear row">
                            <div class="col-10 d-none d-lg-block px-3 py-3">
                                <aside>
                                    <a href="/">Home</a> <span class="fal fa-angle-right"></span>
                                    <a href="${baseUrl}/search">Genes</a> <span class="fal fa-angle-right"></span>
                                    <a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a> <span class="fal fa-angle-right"></span>
                                        ${parameter.procedureNames[0]} / ${parameter.name}
                                </aside>
                            </div>
                            <div class="col-2 d-none d-lg-block px-3 py-3">
                                <aside>
                                    <a href="${cmsBaseUrl}/help/quick-guide-to-the-website/chart-page/" target="_blank" ><i class="fa fa-question-circle" style="font-size: xx-large; color: #ce6211;"></i></a>
                                </aside>
                            </div>
                        </div>
        first a table summary of all viability results and mp calls should go here.
                        <c:forEach var="graphUrl" items="${allGraphUrlSet}" varStatus="graphUrlLoop">

                            <div class="chart pb-5" id="chart${graphUrlLoop.count}" graphUrl="${baseUrl}/fileChart?${graphUrl}" id="divChart_${graphUrlLoop.count}">
                                <div id="spinner${graphUrlLoop.count}" class="container">
                                    <div class="pre-content">
                                        <div class="row no-gutters">
                                            <div class="col-12 my-5">
                                                <p class="h4 text-center text-justify"><i class="fas fa-atom fa-spin"></i> A moment please while we gather the data . . . .</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>


                        </c:forEach>


                    </div>
                </div>
            </div>
        </div>

    </jsp:body>


</t:genericpage>
