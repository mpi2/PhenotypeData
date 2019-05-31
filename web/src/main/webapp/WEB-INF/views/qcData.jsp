<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">QC Data</jsp:attribute>

    <jsp:attribute name="header"/>

    <jsp:body>


        <div class="container data-heading">
            <div class="row row-shadow">
                <div class="col-12 no-gutters">
                    <h2>QC data fo rAllele ${title}</h2>
                </div>
            </div>
        </div>


        <div id="allele-page">
            <div class="container">
                <div>
                    <div class="breadcrumbs m-0">
                        <div class="row">
                            <div class="col-md-12">
                                <p><a href="/">Home</a> <span class="fal fa-angle-right"></span>
                                    <a href="${baseUrl}/search">Genes</a> <span class="fal fa-angle-right"></span>
                                        ${type} "${name}" QC data
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="pre-content">
                    <div class="row no-gutters">
                        <div class="col-12 px-5 pb-5">

                            <c:choose>
                                <c:when test='${qcData.isEmpty()}'>
                                    <p>No QC Data Available</p>
                                </c:when>
                                <c:otherwise>

                                    <c:forEach var="qcGroup" items="${qcData.keySet()}" varStatus="status">

                                        <h3 class="my-5">${qcGroup}</h3>

                                        <table class="table table-striped">
                                            <tr>
                                                <c:forEach var="qcData" items="${qcData[qcGroup]['fieldNames']}" varStatus="status">

                                                    <th>${qcData}</th>

                                                </c:forEach>
                                            </tr>
                                            <tr>
                                                <c:forEach var="qcData" items="${qcData[qcGroup]['values']}" varStatus="status">


                                                    <c:choose>
                                                        <c:when test='${qcData.equals("pass")}'>
                                                            <td style="color: green;font-weight: bold;">${qcData}</td>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <td style="color: red;">${qcData}</td>
                                                        </c:otherwise>
                                                    </c:choose>

                                                </c:forEach>
                                            </tr>
                                        </table>
                                    </c:forEach>

                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>
