<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Experiment details for alleles of ${allelePageDTO.getGeneSymbol()}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&facet=gene">Genes</a> &raquo; <a href="${baseUrl}/genes/${allelePageDTO.getGeneAccession()}">${allelePageDTO.getGeneSymbol()}</a> &raquo; allData </jsp:attribute>
   
	<jsp:attribute name="header">
		<script type="text/javascript">
			var base_url = '${baseUrl}';
		</script>
        <script type='text/javascript' src="${baseUrl}/js/general/dropDownExperimentPage.js?v=${version}"></script>
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
  	</jsp:attribute>

	<jsp:body>
		<div class="region region-content">
			<div class="block">
				<div class="content">
					<div class="node node-gene">
						<c:if test="${phenotypeFilters != null || phenotypeFilters.size() > 0}">
							<h1 id="top">All
								<c:forEach var="phenotypeDto" items="${phenotypeFilters}"  varStatus="loop">
		                        	${phenotypeDto.getMpTerm()}<c:if test="${!loop.last}">, </c:if>
		                        </c:forEach>
							 data for ${allelePageDTO.getGeneSymbol()}</h1>
							 
							 </c:if>
						<%-- <c:if test="${phenotypeFilters == null || phenotypeFilters.size() == 0}">
							<h1 id="top">All data for ${allelePageDTO.getGeneSymbol()}</h1>
						</c:if> --%>
							<div class="section">
							
								<div class="inner">
									<form class="tablefiltering no-style" id="target" action="destination.html">
									 	
									 	<select id="phenotypes"  class="impcdropdown"  multiple="multiple" title="Filter on phenotype top level">
		                                	<c:forEach var="pKey" items="${phenotypes.keySet()}">
		                                        <option value="${pKey}">${phenotypes.get(pKey)}</option>
		                                    </c:forEach>
		                                </select>
										
										<select id="alleleFilter" class="impcdropdown"  multiple="multiple" title="Filter on allele symbol">
		                                	<c:forEach var="allele" items="${allelePageDTO.getEscapedAlleleSymbols()}">
		                                        <option value="${allele}">${allele}</option>
		                                    </c:forEach>
		                                </select>
		                                
										<%--<select id="pipelinesFilter" class="impcdropdown"  multiple="multiple" title="Filter on allele symbol">--%>
		                                	<%--<c:forEach var="pipeline" items="${allelePageDTO.getPipelineNames()}">--%>
		                                        <%--<option value="${pipeline}">${pipeline}</option>--%>
		                                    <%--</c:forEach>--%>
		                                <%--</select>--%>

										<select id="proceduresFilter" class="impcdropdown"  multiple="multiple" title="Filter on procedure">
											<c:forEach var="procedure" items="${allelePageDTO.getProcedureNames()}">
												<option value="${procedure}">${procedure}</option>
											</c:forEach>
										</select>

										<select id="phenotypingCenterFilter"  class="impcdropdown"  multiple="multiple" title="Filter on allele symbol">
		                                	<c:forEach var="pCenter" items="${allelePageDTO.getPhenotypingCenters()}">
		                                        <option value="${pCenter}">${pCenter}</option>
		                                    </c:forEach>
		                                </select> 
		                                
		                                
	                                    <div class="clear"></div>
	                                </form>
								                                
									<div id="spinner-experiments-page"><i class="fa fa-refresh fa-spin"></i></div>
									
									<div id="chart-and-table">
										<jsp:include page="experimentsFrag.jsp" flush="true">
											<jsp:param name="geneAccession" value="<%=request.getParameter(\"geneAccession\")%>"/>
										</jsp:include>
										<p class="textright"> 
											Download data as: 
											<a id="tsvDownload" href="${baseUrl}/experiments/export?${requestScope['javax.servlet.forward.query_string']}&fileType=tsv&fileName=allData${allelePageDTO.getGeneSymbol()}" target="_blank" class="button fa fa-download">TSV</a> 
											<a id="xlsDownload" href="${baseUrl}/experiments/export?${requestScope['javax.servlet.forward.query_string']}&fileType=xls&fileName=allData${allelePageDTO.getGeneSymbol()}" target="_blank" class="button fa fa-download">XLS</a>
										</p>
									</div>
														
								</div>
							</div> <!-- parameter list -->
      					</div> <!--end of node wrapper should be after all secions  -->
    				</div>
    			</div>
   			</div>
 	</jsp:body>
  
</t:genericpage>
