<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 17:01
  To change this template use File | Settings | File Templates.
--%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<br/>
<c:set var="expressionIcon" scope="page" value="fa fa-check"/>
<c:set var="noTissueIcon" scope="page" value="fa fa-circle-o"/>
<c:set var="noExpressionIcon" scope="page" value="fa fa-times"/>
<c:set var="ambiguousIcon" scope="page" value="fa fa-circle"/>
<c:set var="yesColor" scope="page" value="#0978a1"/>
<c:set var="noColor" scope="page" value="gray"/>

		                                    <span title="Expression" class="${expressionIcon}"
                                                  style="color:${yesColor}">&nbsp;Expression</span>&nbsp;&nbsp;
		                                    <span title="No Expression" class="${noExpressionIcon}"
                                                  style="color: gray">&nbsp;No Expression</span>&nbsp;&nbsp;
		                                    <span title="No Tissue Available" class="${noTissueIcon}"
                                                  style="color: gray">&nbsp;No Tissue Available</span>&nbsp;&nbsp;
		                                    <span title="Ambiguous" class="${ambiguousIcon}"
                                                  style="color: gray">&nbsp;Ambiguous</span>&nbsp;&nbsp;

<br/> <br/>
<!-- <h2 class="title" id="section-impc_expression">Expression Overview<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
-->

<table>
    <tr>
        <th>Anatomy</th>
        <th
                title="Number of heterozygous mutant specimens with data for the specified anatomy">
            #HET Specimens
        </th>
        <th
                title="Status of expression for Wild Type specimens from any colony with data for this anatomy">
            WT Expr
        </th>
        <th title="">Mutant Expr</th>
        <%-- <th>Mutant specimens</th> --%>
        <th
                title="An clickable image icon will show if images are available for mutant specimens">
            Images
        </th>
    </tr>
    <c:forEach var="mapEntry"
               items="${embryoExpressionAnatomyToRow}">
        <tr>
            <td><a
                    href="${baseUrl}/anatomy/${mapEntry.value.abnormalAnatomyId}"> ${mapEntry.value.abnormalAnatomyName}
                    <%--${fn:replace(mapEntry.value.abnormalAnatomyName, "TS20 ","")}--%>

                    </a>
                </td>
            <td><span
                    title="${mapEntry.value.numberOfHetSpecimens} Heterozygous Mutant Mice">${mapEntry.value.numberOfHetSpecimens}</span>
            </td>
           
            <td>
                <c:choose>
                    <c:when
                            test="${embryoWtAnatomyToRow[mapEntry.key].expression}">
                                     				<span
                                                            title="WT Expressed: ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimenExpressed)} wild type specimens expressed from a total of ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimen)} wild type specimens"
                                                            class="${expressionIcon}"
                                                            style="color:${yesColor}"></span>(${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimenExpressed)}/${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimen)})
                    </c:when>
                    <c:when
                            test="${embryoWtAnatomyToRow[mapEntry.key].notExpressed}">
                          							<span
                                                            title="WT NOT expressed: ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimenNotExpressed)} Not Expressed ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimen)} wild type specimens"
                                                            class="${noExpressionIcon}" style="color:${noColor}"></span>
                    </c:when>
                    <c:when
                            test="${embryoWtAnatomyToRow[mapEntry.key].noTissueAvailable}">
                        <i title="WT No Tissue Available"
                           class="${noTissueIcon}"
                           style="color:${noColor}"></i>
                    </c:when>
                    <c:when
                            test="${embryoWtAnatomyToRow[mapEntry.key].imageOnly}">
                       <%--  <i title="Image Only"
                           class="${noTissueIcon}"
                           style="color:${noColor}"> --%>Image Only<!-- </i> -->
                    </c:when>

                    <c:otherwise>
                                     				<span title="Ambiguous"
                                                          class="${ambiguousIcon}" style="color:${noColor}">Wrong data???</span>
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:choose>
                    <c:when
                            test="${mapEntry.value.expression}">
                                     				<span
                                                            title="Expressed: ${fn:length(mapEntry.value.specimenExpressed)} mutant specimens expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens"
                                                            class="${expressionIcon}"
                                                            style="color:${yesColor}"></span>(${fn:length(mapEntry.value.specimenExpressed)}/${fn:length(mapEntry.value.specimen)})
                    </c:when>
                    <c:when
                            test="${mapEntry.value.notExpressed}">
                          							<span
                                                            title="Not Expressed: ${fn:length(mapEntry.value.specimenNotExpressed)} Not Expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens"
                                                            class="${noExpressionIcon}" style="color:${noColor}"></span>
                    </c:when>
                    <c:when
                            test="${mapEntry.value.noTissueAvailable}">
                          							<span title="No Tissue Available"
                                                          class="${noTissueIcon}" style="color:${noColor}"></span>
                    </c:when>
                    
                    <c:when
                            test="${mapEntry.value.imageOnly}">
                          							<%-- <span title="Image Only"
                                                          class="${noTissueIcon}" style="color:${noColor}"> --%>Image Only<!-- </span> -->
                    </c:when>

                    <c:otherwise>
                                     				<span title="Ambiguous"
                                                          class="${ambiguousIcon}" style="color:${noColor}"></span>
                    </c:otherwise>
                </c:choose>
            </td>

                <%-- <td>
				<c:forEach var="specimen" items="${mapEntry.value.specimen}">
				<i title="zygosity= ${specimen.value.zyg}">${specimen.key}</i>
				</c:forEach></td> --%>

            <td>
                               
                <c:if
                        test="${embryoMutantImagesAnatomyToRow[mapEntry.key].wholemountImagesAvailable}">
                        <!-- imageComparator?acc=MGI:1859162&anatomy_term=respiratory%20system&parameter_stable_id=IMPC_ALZ_075_001 -->
                    <a
                            href='${baseUrl}/imageComparator?acc=${acc}&anatomy_id=${mapEntry.value.abnormalAnatomyId}&parameter_stable_id=IMPC_ELZ_064_001'><i
                            title="Wholemount Images available (click on this icon to view images)"
                            class="fa fa-image"
                            alt="Images"><%-- (${mutantImagesAnatomyToRow[mapEntry.key].numberOfImages}) --%></i>
                    </a>
                </c:if>
                <c:if
                        test="${embryoMutantImagesAnatomyToRow[mapEntry.key].sectionImagesAvailable}">
                    <a
                            href='${baseUrl}/imageComparator?acc=${acc}&anatomy_id=${mapEntry.value.abnormalAnatomyId}&parameter_stable_id=IMPC_ELZ_063_001'><i
                            title="Section Images available (click on this icon to view images)"
                            class="fa fa-image"
                            alt="Images"><%-- (${mutantImagesAnatomyToRow[mapEntry.key].numberOfImages}) --%></i>
                    </a>
                </c:if>
            </td>
            
            
        </tr>
    </c:forEach>

</table>