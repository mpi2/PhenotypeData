<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage-landing>

    <jsp:attribute name="title">${pageTitle}</jsp:attribute>
    <jsp:attribute name="pagename">${pageTitle}</jsp:attribute>
    <jsp:attribute name="breadcrumb">${systemName}</jsp:attribute>


    <jsp:attribute name="header">

	<!-- CSS Local Imports -->
    <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />

    <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>

	</jsp:attribute>


    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>

        <div class="container">
            <div class="row">
                <div class="col-12">

                    <c:import url="landing_overview_frag.jsp"/>

                    <h2 class="title">Approach</h2>
                    <h4>Procedures that can lead to relevant phenotype associations</h4>
                    <c:import url="landing_procedures_frag.jsp"/>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <h2 class="title">Phenotypes distribution</h2>
                    <div id="phenotypeChart">
                        <script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <jsp:include page="paper_frag.jsp"></jsp:include>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage-landing>


