<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
	
<div class="row pb-5 white-bg">
						<c:if test="${errorMessage != null}">
							<div class="alert alert-info"><p>${errorMessage}</p></div>
						</c:if>

						<div id="phenotypesDiv">
							<div class="container span12">
								<%-- <c:forEach var="filterParameters" items="${paramValues.fq}">
									${filterParameters}
								</c:forEach> --%>
								<c:if test="${not empty phenotypes}">
									<%-- <form class="tablefiltering no-style" id="target" action="">
											<c:forEach var="phenoFacet" items="${phenoFacets}" varStatus="phenoFacetStatus">
													<select id="${phenoFacet.key}" class="selectpicker"	multiple="multiple" title="Filter on ${phenoFacet.key}">
														<c:forEach var="facet" items="${phenoFacet.value}">
															<option>${facet.key}</option>
														</c:forEach>
													</select>
											</c:forEach>

											<div class="clear"></div>
									</form> --%>

									<jsp:include page="geneVariantsWithPhenotypeTable.jsp"/>

									<br/>
									<div id="export">
										<p class="textright">
											Download data as:
											<a id="tsvDownload" href="${baseUrl}/phenotypes/export/${phenotype.getMpId()}?fileType=tsv&fileName=${phenotype.getMpTerm()}" target="_blank" class="btn btn-outline-primary download-data"><i class="fa fa-download"></i>&nbsp; TSV</a>
											<a id="xlsDownload" href="${baseUrl}/phenotypes/export/${phenotype.getMpId()}?fileType=xls&fileName=${phenotype.getMpTerm()}" target="_blank" class="btn btn-outline-primary download-data"><i class="fa fa-download"></i>&nbsp; XLS</a>
										</p>
									</div>
							</c:if>
							</div>
							<c:if test="${empty phenotypes}">
								<div class="alert alert-info"> Phenotype associations to genes and alleles will be available once data has completed quality control.</div>
							</c:if>
						</div>
</div>		