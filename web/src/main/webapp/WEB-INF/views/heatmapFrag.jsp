<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 23/02/2016
  Time: 10:37
  To change this template use File | Settings | File Templates.
--%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>



        <!-- phenotype heatmap -->
				<c:if test="${phenotypeStarted}">
	 						<a id="heatmap_link" class="btn">Heatmap / Table</a>
					<div id="heatmap_toggle_div" class="section hidden">
						<h2 class="title" id="heatmap">Phenotype Heatmap of Preliminary Data
							<span class="documentation"><a href='' id='heatmapSection' class="fa fa-question-circle pull-right"></a></span>
						</h2>

						<div class="dcc-heatmap-root">
							<div class="phenodcc-heatmap"
								 id="phenodcc-heatmap"></div>
						</div>
					</div>
								<!-- end of Pre-QC phenotype heatmap -->
				</c:if>
							
				
       

  