<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:if test="${not empty phenotype.getMpDefinition()}">
	
	<div class="row">
        <div class="col-12 col-md-2 font-weight-bold">
             Definition
        </div>
        <div class="col-12 col-md-10">
                ${phenotype.getMpDefinition()}
        </div>
    </div>
    
    <c:if test="${not empty phenotype.getMpTermSynonym()}">
        <div class="row">
        <div class="col-12 col-md-2 font-weight-bold">
             Synonyms
        </div>
        <div class="col-12 col-md-10">
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
                                    <div class="col-12 col-md-2 font-weight-bold">Related<br>Synonyms <i class="fa fa-question-circle fa-1x relatedSyn"></i>
                                    </div>
									<div class="col-12 col-md-10">
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