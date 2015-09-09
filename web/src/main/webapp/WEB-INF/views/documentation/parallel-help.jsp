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

                            <h1>Explore the Diverse Entry Points to Mouse Phenotype Data.</h1>

                            <p>
                                The parallel coordinates tool allows the users to compare strain values across different parameters.  
                            </p>
                            
                            <p> The values displayed are the genotype effect, which accounts for different variation sources. Information about this and the statistical methods used is available at ??????.

                            <h3 id="parallel-panel">Phenotype Association Stats</h3>
                            
                            <p> The tool allows <b>filtering</b> on each axis (parameter) by selecting the region of interest with the mouse.</p> 
                            <p><b>clear</b></p>
                            <p><b>shadows</b></p>
                            <p></p>
                            <p></p>
                            <p></p> 
                            <p> The generation of this chart is computationally intensive and the number of parameters that can be plotted may vary from one machine to the other. 
                            	If you notice the tool becoming too slow, please consider selecting less or smaller procedures.</p>

                            <img class="well" src="img/phenotype-overview.png"/>

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
