<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div class="row pt-5 pb-5 white-bg">
 <div class="col-12 col-md-7">
<c:if test="${not empty phenotype.getMpDefinition()}">
	
	<div class="row">
        <div class="col-12 col-md-4 font-weight-bold">
             Definition
        </div>
        <div class="col-12 col-md-8">
                ${phenotype.getMpDefinition()}
        </div>
    </div>
    
    <c:if test="${not empty phenotype.getMpTermSynonym()}">
        <div class="row">
        <div class="col-12 col-md-4 font-weight-bold">
             Synonyms
        </div>
        <div class="col-12 col-md-8">
        	<c:if test='${phenotype.getMpTermSynonym().size() == 1}'>
				
                     <c:forEach var="synonym" items="${phenotype.getMpTermSynonym()}" varStatus="loop">
                          ${synonym}
                                                <%--<c:if test="${!loop.last}">,&nbsp;</c:if>--%>
                      </c:forEach>
        	</c:if>

            <c:if test='${phenotype.getMpTermSynonym().size() gt 1}'>
                                            <c:set var="count" value="0" scope="page"/>
                                            <ul>
                                                <c:forEach var="synonym" items="${phenotype.getMpTermSynonym()}" varStatus="loop">
                                                    <c:set var="count" value="${count + 1}" scope="page"/>
                                                    <c:if test='${count lt 3}'>
                                                        <li class='defaultList'>${synonym}</li>
                                                    </c:if>
                                                    <li class='fullList'>${synonym}</li>
                                                </c:forEach>
                                            </ul>
                                            <c:if test='${count gt 2}'>
                                                <span class='synToggle'>Show more</span>
                                            </c:if>
            </c:if>
        </div>
        </div>
     </c:if>

								<c:if test="${not empty phenotype.getMpNarrowSynonym()}">
								<div class="row">
                                    <div class="col-12 col-md-4 font-weight-bold">Related Synonyms <i class="fa fa-question-circle fa-1x relatedSyn"></i>
                                    </div>
									<div class="col-12 col-md-8">
                                        <c:if test='${phenotype.getMpNarrowSynonym().size() == 1}'>
                                            <c:forEach var="nsynonym" items="${phenotype.getMpNarrowSynonym()}" varStatus="loop">
                                                ${nsynonym}
                                                <%--<c:if test="${!loop.last}">,&nbsp;</c:if>--%>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test='${phenotype.getMpNarrowSynonym().size() gt 1}'>
                                            <c:set var="count" value="0" scope="page"/>
                                            <ul>
                                                <c:forEach var="nsynonym" items="${phenotype.getMpNarrowSynonym()}" varStatus="loop">
                                                    <c:set var="count" value="${count + 1}" scope="page"/>
                                                    <c:if test='${count lt 3}'>
                                                      <li class='defaultList'>${nsynonym}</li>
                                                    </c:if>
                                                      <li class='fullList'>${nsynonym}</li>
                                                </c:forEach>
                                            </ul>
                                            <c:if test='${count gt 2}'>
                                                <span class='synToggle'>Show more</span>
                                            </c:if>
                                        </c:if>
                                    </div>
                                   </div>
								</c:if>
    
</c:if>

<c:if test="${not empty procedures}">
	<div class="row">
		<div class="col-12 col-md-4 font-weight-bold">Procedure
			</div>
			<div class="col-12 col-md-4">
				<ul>
					<c:set var="count" value="0" scope="page"/>
						<c:forEach var="procedure" items="${procedures}" varStatus="firstLoop">
		 						<c:set var="count" value="${count+1}" />
		 								<c:set var="hrefVar" value="${drupalBaseUrl}/impress/protocol/${procedure.procedureStableKey}"/>
		 									<c:if test="${fn:contains(procedure.procedureStableId,'M-G-P')}">
		 											<c:set var="hrefVar" value="${drupalBaseUrl}/impress/parameters/${procedure.procedureStableKey}/4"/>
		 									</c:if>
		  									<li><a href="${hrefVar}">
		  											${procedure.procedureName} (${procedure.procedureStableId.split("_")[0]},
		  											v${procedure.procedureStableId.substring(procedure.procedureStableId.length()-1, procedure.procedureStableId.length())})
		  										</a>
		  									</li>
			 									<c:if test="${count==3 && !firstLoop.last}"><p ><a id='show_other_procedures'><i class="fa fa-caret-right"></i><span id="procedureToogleLink">Show more</span></a></p> <div id="other_procedures"></c:if>
												<c:if test="${firstLoop.last && fn:length(procedures) > 3}"></c:if>
											</c:forEach>
										</ul>
			</div>
	</div>
</c:if>
<div class="row">
<div class="col-12 col-md-4 font-weight-bold">
	MP browser
</div>
<div class="col-12 col-md-8">
	<a href="${baseUrl}/ontologyBrowser?termId=${phenotype.getMpId()}">${phenotype.getMpId()}</a>
								<c:if test="${!hasData}">
									<p>This MP term has not been considered for annotation in <a href="https://www.mousephenotype.org/impress">IMPReSS</a>. However, you can search and retrieve all MP terms currently associated to the Knock-out mutant lines from the <a href="${baseUrl}/search">IMPC Search</a> page. You can also look at all the MP terms used to annotate the IMPReSS SOPs from the <a href="https://www.mousephenotype.org/impress/ontologysearch">IMPReSS ontology search</a> page.</p>
								</c:if>

		                        <c:if test="${orderPossible}">
		                          	<p> <a class="btn" href="#order2"> <i class="fa fa-shopping-cart"></i> Order </a> </p>
		                        </c:if>
</div>
</div>


</div>
 <!-- end of half section -->


<div id="parentChild" class="col-12 col-md-5">
	                        <div class="row no-gutters">
									<c:if test="${hasChildren && hasParents}">
		                            	<div class="col-12 col-md-6" id="parentDiv"></div>
										<div class="col-12 col-md-6" id="childDiv"></div>
									</c:if>
									<c:if test="${hasChildren && !hasParents}">
										<div id="childDiv"></div>
									</c:if>
									<c:if test="${!hasChildren && hasParents}">
		                            	<div id="parentDiv"></div>
									</c:if>
							</div>
</div>
</div>