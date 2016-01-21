<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Embryo Landing Page</jsp:attribute>
	<jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">
	</jsp:attribute>          


	<jsp:attribute name="header">
		
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
            <c:if test="${phenotypeStarted}">
                <!--[if !IE]><!-->
                <link rel="stylesheet" type="text/css"
                      href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css"/>
                <!--<![endif]-->
                <!--[if IE 8]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmapIE8.1.3.1.css">
                <![endif]-->
                <!--[if gte IE 9]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css">
                <![endif]-->
            </c:if>

        </jsp:attribute>

    <jsp:body>
        <div class="region region-content">
            <div class="block">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">IMPC Embryo Data </h1>

                        <div class="section">
                            <div class="inner">
                            	<div id="viabilityChart" class="twothird right">
				            		<script type="text/javascript">${viabilityChart}</script>
								</div>
								<div id="viabilityChart" class="onethird right">
				            		<table> <tbody>
				            		<c:forEach var="key" items="${viabilityTable.keySet()}">
					            		<tr>
					            			<td><h4 class="capitalize">${key}</h4></td>
					            			<td><h4>${viabilityTable.get(key)}</h4></td> 
					            			<td><a href="" style="text-decoration:none;"> <i class="fa fa-download" alt="Download"></i></a></td>
					            		</tr>
									</c:forEach>
				            		</tbody></table>
								</div>
								<div class="clear"> </div>								
	                           
                                                        	
                            </div>
                        </div>

        				<div class="section">

                            <h2 class="title"> Vignettes </h2>

                            <div class="inner">
                              
                            </div>

                        </div>

                        <div class="section">

                            <h2 class="title"> 3D Imaging </h2>

                            <div class="inner">
                              
                            </div>

                        </div>
                        
                         <div class="section">
							<h2 class="title ">IMPC Embryonic Pipeline</h2>
                            <div class="inner">
	                        	<div><img src="${baseUrl}/img/embryo_impress.png"/></div>
                            </div>

                        </div>
                        
                      </div>
                    <!--end of node wrapper should be after all secions  -->
                </div>
            </div>
        </div>

      </jsp:body>

</t:genericpage>
