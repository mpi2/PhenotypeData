<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Data Release ${metaInfo["data_release_version"]} Notes</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Release Notes</jsp:attribute>
	<jsp:attribute name="header">

		<script type="text/javascript">
			var cmsBaseUrl = '${cmsBaseUrl}';
		</script>

        <script type="text/javascript">
		    $(document).ready(function() {

                // bubble popup for brief panel documentation
                $.fn.qTip({
                    'pageName': 'phenome',
                    'tip': 'top right',
                    'corner': 'right top'
                }); 
            });
        </script>

        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/highcharts-more.js"></script>
        <script src="https://code.highcharts.com/modules/exporting.js"></script>
        <script src="https://code.highcharts.com/modules/export-data.js"></script>

	</jsp:attribute>

	<jsp:attribute name="bodyTag"><body class="no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter" />

	<jsp:body>

        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 id="top" class="mb-0">IMPC Data Release ${metaInfo["data_release_version"]} Notes</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content pt-3 pb-5">
                            <div class="container p-0 p-md-2">
                                <div class="row">
                                    <div class="col-6">
                                        <div>
                                            <h4>IMPC</h4>
                                            <ul class="mt-0">
                                                <li>Release:&nbsp;${metaInfo["data_release_version"]}</li>
                                                <li>Published:&nbsp;${metaInfo["data_release_date"]}</li>
                                            </ul>
                                        </div>

                                        <div>
                                            <h4>Statistical Package</h4>
                                            <ul class="mt-0">
                                                <li>${metaInfo["statistical_packages"]}</li>
                                                <li>Version:&nbsp;${metaInfo["PhenStat_release_version"]}</li>
                                            </ul>
                                        </div>
                                        <div>
                                            <h4>Genome Assembly</h4>
                                            <ul class="mt-0">
                                                <li>${metaInfo["species"]}</li>
                                                <li>Version:&nbsp${metaInfo["genome_assembly_version"]}</li>
                                            </ul>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div>
                                            <h4>Summary</h4>
                                            <ul class="mt-0">
                                                <li>Number of phenotyped genes:&nbsp;<fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo['phenotyped_genes']}"/></li>
                                                <li>Number of phenotyped mutant lines:&nbsp;<fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo['phenotyped_lines']}"/></li>
                                                <li>Number of phenotype calls:&nbsp;<fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo['statistically_significant_calls']}"/></li>
                                            </ul>
                                        </div>
                                        <div>
                                            <h4>Data access</h4>
                                            <ul class="mt-0">
                                                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-${metaInfo['data_release_version']}">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-${metaInfo['data_release_version']}</a>
                                                </li>
                                                <li>RESTful interfaces:&nbsp;<a
                                                        href="${baseUrl}/help/programmatic-data-access/">APIs</a></li>
                                            </ul>
                                        </div>
                                    </div>
                                </div> <!-- end row -->

                                <div class="row mt-5">
                                    <div class="col-12">
                                        <h2 id="new-features" class="pb-3">Highlights</h2>
                                        <div class="col-12">
                                            <h4>Data release ${metaInfo["data_release_version"]}</h4>

                                            <div class="well">
                                                <strong>Release notes:</strong>
                                                ${metaInfo["data_release_notes"]}
                                            </div>
                                        </div>
                                    </div>
                                </div> <!-- end row -->

                                <div class="row py-5">
                                    <div class="col-12">
                                        <h2 class="title pb-3" id="data_reports">Data Reports</h2>
                                        <div class="col-12">
                                            <h3>Lines and Specimens</h3>
                                            <table class="table table-striped">
                                                <thead>
                                                <tr>
                                                    <th class="headerSort">Phenotyping Center</th>
                                                    <th class="headerSort">Mutant Lines</th>
                                                    <th class="headerSort">Baseline Mice</th>
                                                    <th class="headerSort">Mutant Mice</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="center" items="${phenotypingCenters}">
                                                    <c:set var="phenotyped_lines" value="phenotyped_lines_${center}"/>
                                                    <c:set var="control_specimens" value="control_specimens_${center}"/>
                                                    <c:set var="mutant_specimens" value="mutant_specimens_${center}"/>
                                                    <tr>
                                                        <td>${center}</td>
                                                        <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo[phenotyped_lines]}"/></td>
                                                        <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo[control_specimens]}"/></td>
                                                        <td><fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo[mutant_specimens]}"/></td>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>

                                            <h3>Experimental Data and Quality Checks</h3>
                                            <table class="table table-striped">
                                                <thead>
                                                <tr>
                                                    <th class="headerSort">Data Type</th>
                                                    <c:forEach var="qcType" items="${qcTypes}">
                                                        <th class="headerSort">${fn:replace(qcType, '_', ' ')}</th>
                                                    </c:forEach>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="dataType" items="${dataTypes}">
                                                    <tr>
                                                        <td>${fn:replace(dataType, '_', ' ')}</td>
                                                        <c:forEach var="qcType" items="${qcTypes}">
                                                            <c:set var="qcKey" value="${dataType}_datapoints_${qcType}"/>
                                                            <c:set var="qcValue" value="${metaInfo[qcKey]}"/>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${qcValue != null && qcValue != ''}">
                                                                        <fmt:formatNumber type="number" maxFractionDigits="3" value="${qcValue}"/>
                                                                        <c:if test="${qcType != 'QC_passed'}"><sup>*</sup></c:if>
                                                                    </c:when>
                                                                    <c:otherwise>0</c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                        </c:forEach>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                            <p><sup>*</sup>&nbsp;Excluded from statistical analysis.</p>
                                        </div>
                                    </div>
                                </div> <!-- end row -->

                                <div class="row mt-5">
                                    <div class="col-12">
                                        <h2>Distribution of Phenotype Annotations</h2>
                                        <div id="distribution">
                                            <script type="text/javascript">
                                                ${annotationDistributionChart}
                                            </script>
                                        </div>
                                    </div>
                                </div> <!-- end row -->

                                <div class="row mt-5">
                                    <div class="col-12">
                                        <h2 class="title" id="section-associations"> Status </h2>
                                        <div class="row">
                                            <div class="col-12">
                                                <h4>Overall</h4>
                                                <div class="row">
                                                    <div class="col-6">
                                                        <div id="genotypeStatusChart">
                                                            <script type="text/javascript">${genotypeStatusChart.getChart()}</script>
                                                        </div>
                                                    </div>
                                                    <div class="col-6">
                                                        <div id="phenotypeStatusChart">
                                                            <script type="text/javascript">${phenotypeStatusChart.getChart()}</script>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-12">
                                        <h4>By Center</h4>
                                        <div class="row">
                                            <div class="col-6">
                                                <div id="genotypeStatusByCenterChart">
                                                    <script type="text/javascript">${genotypingDistributionChart}</script>
                                                </div>
                                                <a id="checkAllGenByCenter" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
                                                <a id="uncheckAllGenByCenter"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
                                                <div class="clear both"></div>
                                            </div>
                                            <div class="col-6">
                                                <div id="phenotypeStatusByCenterChart">
                                                    <script type="text/javascript">${phenotypingDistributionChart}</script>
                                                </div>
                                                <a id="checkAllPhenByCenter" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
                                                <a id="uncheckAllPhenByCenter"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
                                                <div class="clear both"></div>
                                            </div>
                                        </div>
                                        <p>More charts and status information are available from <a href="https://www.mousephenotype.org/imits/v2/reports/mi_production/komp2_graph_report_display">iMits</a>. </p>
                                    </div>
                                </div> <!-- end row -->

                                <div class="row mt-5">

                                    <div class="col-12">

                                        <h2>Phenotype Associations</h2>

                                        <div id="callProcedureChart">
                                            <script type="text/javascript">
                                                ${callProcedureChart}
                                            </script>
                                        </div>

                                        <a id="checkAllPhenCalls" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
                                        <a id="uncheckAllPhenCalls"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
                                        <div class="clear both"></div>

                                    </div>
                                </div> <!-- end row -->

                                <div class="row mt-5">

                                    <div class="col-12">
                                        <h2>Trends</h2>

                                        <div id="trendsChart">
                                            <script type="text/javascript">
                                                ${trendsChart}
                                            </script>
                                        </div>

                                        <div id="datapointsTrendsChart">
                                            <script type="text/javascript">
                                                ${datapointsTrendsChart}
                                            </script>
                                        </div>
                                        <a id="checkAllDataPoints" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
                                        <a id="uncheckAllDataPoints"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
                                        <div class="clear both"></div>
                                        <br/><br/>

                                        <h3>Previous Releases</h3>
                                        <ul>
                                            <c:forEach var="release" items="${releases}">
                                                <li><a href="${baseUrl}/previous-releases/${release}">Release ${release} notes</a></li>
                                            </c:forEach>
                                        </ul>
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
