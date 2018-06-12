<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">Register Interest Summary</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}"></a>Register Interest Summary</jsp:attribute>

    <jsp:attribute name="header">
        <style>
            table {
                border-collapse: collapse;
                border-spacing: 0;
            }

            /* Override allele ref style for datatable */
            table.dataTable thead tr {
                display: table-row;
            }

            #metabolism-table_length {
                width: 50%;
                float: left;
                /* text-align: right; */
            }

            #metabolism-table_filter {
                width: 50%;
                float: right;
                text-align: right;
            }

            /* .background_hover_axis {
                        background-color: rgb(173,216,230);
                    } */
        </style>
	</jsp:attribute>

    <jsp:attribute name="bodyTag">
		<body class="phenotype-node no-sidebars small-header">
	</jsp:attribute>


    <jsp:body>

        <a href="<c:url value="/logout" />">Logout</a>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">Register Interest Summary </h1>

                        <div class="section">
                            <div class="inner">

                                <c:forEach var="contactGene" items="${registerInterestSummaryList}" varStatus="loop">

                                    <h3>Genes for which ${contactGene.contact.address} has registered interest:</h3>

                                    <div id="registerInterestSummaryTableDiv">
                                        <table id="registerInterestSummary-table"
                                               class='table tableSorter'>
                                            <thead>
                                                <tr>
                                                    <th>Gene Symbol</th>
                                                    <th>Gene MGI Accession Id</th>
                                                    <th>Assignment Status</th>
                                                    <th>Null Allele Production Status</th>
                                                    <th>Conditional Allele Production Status</th>
                                                    <th>Phenotyping Data Available</th>
                                                    <th>Action</th>
                                                </tr>
                                            </thead>
                                            <tbody>

                                                <c:forEach var="gene" items="${contactGene.genes}" varStatus="loop">

                                                    <tr>
                                                        <td>
                                                            <a href='genes/${gene.mgiAccessionId}'>${gene.symbol}</a>
                                                        </td>
                                                        <td><a href="//www.informatics.jax.org/marker/${gene.mgiAccessionId}">${gene.mgiAccessionId}</a></td>
                                                        <td>${gene.riAssignmentStatus}</td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${gene.riNullAlleleProductionStatus == 'Genotype confirmed mice'}">
                                                                    <a href='search/allele2?kw="${gene.mgiAccessionId}"'>${gene.riNullAlleleProductionStatus}</a>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${gene.riNullAlleleProductionStatus}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${gene.riConditionalAlleleProductionStatus == 'Genotype confirmed mice'}">
                                                                    <a href='search/allele2?kw="${gene.mgiAccessionId}"'>${gene.riConditionalAlleleProductionStatus}</a>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${gene.riConditionalAlleleProductionStatus}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${gene.riPhenotypingStatus == 'Yes'}">
                                                                    <a href='genes/${gene.mgiAccessionId}#section-associations'>${gene.riPhenotypingStatus}</a>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${gene.riPhenotypingStatus}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>Unregister</td>
                                                    </tr>

                                                </c:forEach>

                                            </tbody>
                                        </table>
                                    </div>

                                    <div id="tsv-result" style="display: none;"></div>
                                    <br/>
                                    <div id="export">
                                        <a id="hideTable" style="float: left;">Hide</a>
                                        <!-- href="#heatMapContainer" -->
                                        <p class="textright">
                                            Download data as:
                                            <a id="downloadTsv"
                                               class="button fa fa-download">TSV</a>
                                            <a id="downloadExcel" class="button fa fa-download">XLS</a>
                                                <%-- <a id="tsvDownload" href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=tsv&fileName=${gene.markerSymbol}" target="_blank" class="button fa fa-download">TSV</a>
                                                <a id="xlsDownload" href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=xls&fileName=${gene.markerSymbol}" target="_blank" class="button fa fa-download">XLS</a> --%>
                                        </p>
                                    </div>
                                    <br/> <br/>

                                </c:forEach>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>