<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">${pageTitle} Register Interest Summary</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}">Register Interest
        Summary</a> &nbsp;&raquo; ${pageTitle}</jsp:attribute>

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
                                <h3>Genes for which you have registered interest</h3>

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
                                            <tr>
                                                <td>row 1</td>
                                                <td>row 1</td>
                                                <td>row 1</td>
                                                <td>row 1</td>
                                                <td>row 1</td>
                                                <td>row 1</td>
                                                <td>row 1</td>
                                            </tr>
                                        </tbody>
                                        <!-- BODY -->
                                    </table>
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
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>
