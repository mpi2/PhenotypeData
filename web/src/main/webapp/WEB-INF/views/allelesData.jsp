<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Experiment details for alleles of ???</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
	
	

	<jsp:attribute name="header">
		<script type="text/javascript">
			var base_url = '${baseUrl}';
		</script>

		<style>
			.pagination ul {
				list-style-type: none;
			}
		</style>
  	</jsp:attribute>

	<jsp:body>
		<div class="region region-content">
			<div class="block">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">Experiments for ${allelePageDTO.getGeneSymbol()}</h1>

							<!--  Phenotype Associations Panel -->
							<div class="section">
								<div class="inner">
										
									<p class="resultCount">
									Total number of results: ${rows}
									</p>
									
									<!-- Associations table -->
									<c:if test="${chart != null}">						
										<!-- chart here -->
						  				<div id="chartDiv"></div>
										<script type="text/javascript" async>${chart}</script>	
									</c:if>
									
									<c:set var="count" value="0" scope="page" />
											
									<script>
										var resTemp = document.getElementsByClassName("resultCount");
										if (resTemp.length > 1){ resTemp[0].remove();}
									</script>
									
									<table id="strainPvalues">
										<thead>
											<tr>
												<th class="headerSort">Procedure</th>
												<th class="headerSort">Parameter</th>
												<th class="headerSort">Zygosity</th>
												<th class="headerSort">Mutants</th>
												<th class="headerSort">Statistical</br>Method</th>
												<th class="headerSort">P Value</th>
												<th class="headerSort">Status</th>
												<th class="headerSort">Graph</th>
											</tr>
										</thead>
										
										<tbody>
											<c:forEach var="stableId" items="${pvaluesMap.keySet()}" varStatus="status">
												<c:set var="stableIdpValuesMap" value="${pvaluesMap[stableId]}"/>
												<c:forEach var="pValueItem" items="${stableIdpValuesMap}">
														<tr>
														<td>${pValueItem.getProcedureName()}</td>
														<td>${pValueItem.getParameterName()}</td>
														<td>${pValueItem["zygosity"]}</td>
														<td>${pValueItem.femaleMutantCount}f:${pValueItem.maleMutantCount}m</td>
														<td>${pValueItem.statisticalMethod}</td>
														<!-- pValue -->
														<c:choose>
															<c:when test="${ ! empty pValueItem && pValueItem.getStatus() == 'SUCCESS'}">
																<c:set var="paletteIndex" value="${pValueItem.colorIndex}"/>
																<c:set var="Rcolor" value="${palette[0][paletteIndex]}"/>
																<c:set var="Gcolor" value="${palette[1][paletteIndex]}"/>
																<c:set var="Bcolor" value="${palette[2][paletteIndex]}"/>
																<td style="background-color:rgb(${Rcolor},${Gcolor},${Bcolor})">
																	${pValueItem.pValue}
																</td>
															</c:when>
															<c:otherwise><td>${pValueItem.pValue}</td></c:otherwise>
														</c:choose>
														<td>${pValueItem.status}</td>
														<td style="text-align:center">
														<a href='${baseUrl}/charts?accession=${allele.gene.id.accession}&allele_accession=${allele.id.accession}&parameter_stable_id=${stableId}&metadata_group=${pValueItem.metadataGroup}&zygosity=${pValueItem.zygosity}&phenotyping_center=${phenotyping_center}'>
														<i class="fa fa-bar-chart-o" alt="Graphs" > </i></a>
														</td>
														</tr>
												</c:forEach>
											</c:forEach>
										</tbody>
									</table>								
								</div>
							</div> <!-- parameter list -->
      					</div> <!--end of node wrapper should be after all secions  -->
    				</div>
    			</div>
   			</div>

		<script type="text/javascript">
			$(document).ready(function() {
			  var oTable = $('#strainPvalues').dataTable({
				  "sPaginationType": "bootstrap"
//						"bPaginate":false
			  });
			  // Sort immediately with p-value column starting with the lowest one
			  oTable.fnSort( [ [5,'asc'] ] );
			} );	
		</script>
	
 	</jsp:body>
  
</t:genericpage>
