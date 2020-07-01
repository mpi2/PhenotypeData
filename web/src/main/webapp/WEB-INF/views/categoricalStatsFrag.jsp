<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<!-- categorical here -->


<c:forEach var="categoricalChartDataObject"	items="${categoricalResultAndChart.maleAndFemale}"	varStatus="chartLoop">

	<div id="chart${experimentNumber}"></div>
	
	<a id="checkAll" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
    <a id="uncheckAll"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
    
    <div class="clear both"></div>
	<script type="text/javascript">	${categoricalChartDataObject.chart} </script>
<p>
	<div class="row">
            <div class="col-md-12">
            	<div class="row">
            		<div class="col-md-4">
            		
           			 <c:if test="${categoricalResultAndChart.combinedPValue!=null}">
						 <h4> Results of statistical analysis  </h4>
							
                            <dl class="alert alert-success">
									<dt>Combined Male and Female P value</dt>
									<dd><t:formatScientific>${categoricalResultAndChart.combinedPValue}</t:formatScientific></dd>
									
									<%-- <dt>Males only</dt>
									<dd>${categoricalResultAndChart.malePValue}</dd>
									
									<dt>Females only</dt>
									<dd>${categoricalResultAndChart.femalePValue}</dd> --%>
							</dl> 
					</c:if>
					
					
					</div>
				
            
            
            		<div class="col-md-12">
					
						<table class="table table-striped small">
							<thead>
								<tr>
									<th>Sample type</th>
									<c:forEach var="categoryObject"	items="${categoricalResultAndChart.maleAndFemale[0].categoricalSets[0].catObjects}"	 varStatus="categoriesStatus">
										<th>${categoryObject.category}</th>
									</c:forEach>
									<%-- <th>P Value</th> --%>
									<%-- <th>Effect Size</th> --%>
								</tr>
							</thead>
							<tbody>
							<c:set var="emptyColumnNumber" value="0" scope="page"/>
								<c:forEach var="maleOrFemale" items="${categoricalResultAndChart.maleAndFemale}" varStatus="maleOrFemaleStatus">
									<c:forEach var="categoricalSet"	items="${maleOrFemale.categoricalSets}" varStatus="catSetStatus">
										<tr>
											<td>${categoricalSet.name }</td>
											<c:forEach var="catObject" items="${categoricalSet.catObjects}"	varStatus="catObjectStatus">
												<td>${catObject.count }</td>
											</c:forEach>
											<%-- <td>${categoricalSet.catObjects[0].pValue }</td> --%>
											<%-- <td>${categoricalSet.catObjects[0].maxEffect }</td> removed effect size as per Terrys request --%>
										</tr>	
									</c:forEach>
				
								</c:forEach>
								
								
							</tbody>
						</table>
						</div>
				
					</div>
		
		
		<div class="row mt-5">
                    <div class="col-md-12">
                        <h4> Access the results programmatically </h4>
                        <hr>
                        <p>
                            <a target="_blank" class="btn btn-outline-primary btn-sm" href='${srUrl}'> Statistical result raw XML </a>
                            <a target="_blank" class="btn btn-outline-primary btn-sm" href='${gpUrl}'> Genotype phenotype raw XML </a>
                            <a target="_blank" class="btn btn-outline-primary btn-sm" href='${baseUrl}${phenStatDataUrl}'> PhenStat-ready raw experiment data</a>
                        </p>
                    </div>
                </div>
			
		</div>
	</div>
	
</c:forEach>

<script>
 	$(document).ready(
		function() {
		 	$.fn.qTip({
						'pageName': 'stats',							
						'tip': 'top right',
						'corner' : 'right top'
			});
 	});
</script>
