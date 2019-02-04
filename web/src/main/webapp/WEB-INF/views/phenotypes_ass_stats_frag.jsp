<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
	
<div class="row pt-5 pb-5 white-bg">
  <div class="col-12 col-md-12">
							<!-- Phenotype Assoc. summary -->


							<c:if test="${parametersAssociated.size() == 0}">

									<c:if test="${genePercentage.getTotalGenesTested() > 0}">
										<p> <span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of tested genes with null mutations on a B6N genetic background have a phenotype association to ${phenotype.getMpTerm()}
										(${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()}) </p>
									</c:if>
									<p>
									<c:if test="${genePercentage.getFemaleGenesTested() > 0}">
										<span class="padleft"><span class="bigger">${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </span>
									</c:if>
									<c:if test="${genePercentage.getMaleGenesTested() > 0}">
										<span class="padleft"><span class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</span>
									</c:if>
									</p>


							</c:if>
							<c:if test="${parametersAssociated.size() > 0}">

									<c:if test="${genePercentage.getTotalGenesTested() > 0}">
										<p> <span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of tested genes with null mutations on a B6N genetic background have a phenotype association to ${phenotype.getMpTerm()}
										(${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()}) </p>
									</c:if>
									<p>
									<c:if test="${genePercentage.getFemaleGenesTested() > 0}">
										<span class="padleft"><span class="bigger">${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </span>
									</c:if>
									<c:if test="${genePercentage.getMaleGenesTested() > 0}">
										<span class="padleft"><span class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</span>
									</c:if>
									</p>



							</c:if>

							<!-- baseline charts -->
							<c:if test="${parametersAssociated.size() > 0}">
								<c:if test="${parametersAssociated.size() > 1}">
										<p> Select a parameter <i class="fa fa-bar-chart-o" ></i>&nbsp; &nbsp;
											<select class="overviewSelect" onchange="ajaxToBe('${phenotype.getMpId()}', this.options[this.selectedIndex].value);">
												<c:forEach var="assocParam" items="${parametersAssociated}" varStatus="loop">
													<option value="${assocParam.getStableId()}">${assocParam.getName()} (${assocParam.getStableId()})</option>
												</c:forEach>
											</select>
										</p>
									</c:if>
									</div>
<div class="col-12 col-md-6">
									<div id="baselineChart"></div>
										<c:if test="${parametersAssociated.size() > 0}">
										<div id="chartsHalfBaseline">
												
												<%-- <c:if test="${parametersAssociated.size() > 1}">
													<p> Select a parameter <i class="fa fa-bar-chart-o" ></i>&nbsp; &nbsp;
														<select class="overviewSelect" onchange="ajaxToBeBaseline('${phenotype.getMpId()}', this.options[this.selectedIndex].value);">
															<c:forEach var="assocParam" items="${parametersAssociated}" varStatus="loop">
																<option value="${assocParam.getStableId()}">${assocParam.getName()} (${assocParam.getStableId()})</option>
															</c:forEach>
														</select>
													</p>
												</c:if> --%>
												<br/>

												<div id="baseline-chart-container">
													<div id="baseline-chart-div" class="baselineChart" parameter="${parametersAssociated.get(0).getStableId()}" mp="${phenotype.getMpId()}">
													</div>
													<div id="spinner-baseline-charts"><i class="fa fa-refresh fa-spin"></i></div>
												</div>

												<div id='baseline-chartFilters'></div>

											</div>
										</c:if>






</div>

								<!-- Overview Graphs -->
								<c:if test="${parametersAssociated.size() > 0}">
								<div class="col-12 col-md-6 ">

									<br/>

									<div id="chart-container">
										<div id="single-chart-div" class="oChart" parameter="${parametersAssociated.get(0).getStableId()}" mp="${phenotype.getMpId()}">
										</div>
										<div id="spinner-overview-charts"><i class="fa fa-refresh fa-spin"></i></div>
									</div>

									<div id='chartFilters'></div>

								</div>
							</c:if>
						</c:if>
							<div class="clear"></div>
					


	
</div> <!-- end of main section -->
