<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Release Notes</jsp:attribute>
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
                    <h2 id="top" class="mb-0">IMPC Release Notes</h2>
                </div>
            </div>
        </div>


        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <jsp:include page="IMPC_Release_Notes_${releaseVersion}.jsp"></jsp:include>
                            <br/><br/>

                            <h3>Previous Releases</h3>
                            <ul>
                                <c:forEach var="release" items="${releases}">
                                    <%--                                                <li><a href="${baseUrl}/release_notes/IMPC_Release_Notes_${release}.html">Release ${release} notes</a></li>--%>
                                    <li><a href="${baseUrl}/pastRelease/${release}">Release ${release} notes</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                        </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>
