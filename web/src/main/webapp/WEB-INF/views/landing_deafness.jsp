<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">${pageTitle} landing page | IMPC Phenotype Information</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/landing">Landing
        Pages</a> &nbsp;&raquo; ${pageTitle}</jsp:attribute>

    <jsp:attribute name="header">

	<!-- CSS Local Imports -->
	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>

	</jsp:attribute>


    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="addToFooter">



	</jsp:attribute>
    <jsp:body>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">${pageTitle} </h1>

                        <div class="section">
                            <div class="inner">
                                <c:if test="${genePercentage.getDisplay()}">
                                    <c:if test="${genePercentage.getTotalGenesTested() > 0}">
                                        <p><span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of tested
                                            genes with null mutations on a B6N genetic background have a phenotype
                                            association to ${phenotype.getMpTerm()}
                                            (${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()})
                                        </p>
                                    </c:if>
                                    <p>
                                        <c:if test="${genePercentage.getFemaleGenesTested() > 0}">
                                            <span class="padleft"><span
                                                    class="bigger">${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </span>
                                        </c:if>
                                        <c:if test="${genePercentage.getMaleGenesTested() > 0}">
                                            <span class="padleft"><span class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</span>
                                        </c:if>
                                    </p>

                                    <div id="pieChart"> <script type="text/javascript">${genePercentage.getPieChartCode()}</script></div>

                                    <div class="clear"></div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>


