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

                            <h1>Parallel coordinates </h1>

                            <p> The parallel coordinates tool allows users to compare strains across different parameters.
                                Hover over a row in the table to highlight the corresponding line on the chart.</p>
                            
                            <p> The values displayed are the genotype effect, which accounts for different variation sources. 
                            Information about this and the statistical methods used is available in the <a href="statistics-help">statistics documentation</a>.</p>
							<br/>
                            <img class="well" src="img/parallel.png"/>
                            <br/>
                            <p> The tool allows <b>filtering</b> on each axis (parameter) by selecting the region of interest with the mouse.</p> 
                            <img class="well" src="img/parallel-filtered.png"/>
                            <br/><br/>
                            <p>The <b>clear</b> button removes existing filters.</p>
                            <p>The <b>shadows</b> button keeps a shadow of lines which have been filtered out.</p>
                            <p>The <b>opacity</b> allows the user to edit the transparency of the lines.</p>
                            <p>The <b>export</b> button generates an export of the values in the table. If any filter is set, only the data displayed in the table will be exported.</p>
                             
                            <p> The generation of this chart is computationally intensive and the number of parameters that can be plotted may vary from one machine to the other. 
                            	If you notice the tool becoming too slow, please consider selecting fewer or smaller procedures.</p>


                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
