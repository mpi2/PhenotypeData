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

<c:set var="expressionIcon" scope="page" value="fa fa-circle"/>
<c:set var="noTissueIcon" scope="page" value="fa fa-times"/>
<c:set var="noExpressionIcon" scope="page" value="fa fa-circle-o"/>
<c:set var="ambiguousIcon" scope="page" value="fa fa-adjust"/>
<c:set var="yesColor" scope="page" value="text-primary"/>
<c:set var="noColor" scope="page" value="text-info"/>
<c:set var="amColor" scope="page" value="text-warning"/>
<c:set var="noAvaColor" scope="page" value="text-danger"/>

<div class="row justify-content-end mt-3 mr-2">
    <div class="btn-group btn-group-toggle" data-toggle="buttons">
        <label class="btn btn-outline-primary btn-sm active">
            <input type="radio" name="optionsEmbryo" id="optionsEmbryo" value="table" autocomplete="off" checked> Table
        </label>
        <label class="btn btn-outline-primary btn-sm">
            <input type="radio" name="optionsEmbryo" id=" " autocomplete="off" value="anatogram"> Images
        </label>
    </div>
</div>

<div class="row justify-content-center" id="embryo1">

  <div class="container p-0 p-md-2">
      <div class="mb-2 row justify-content-center">
          <span title="Expression" class="${yesColor} mr-3"><i class="${expressionIcon}"></i>&nbsp;Expression</span>
          <span title="No Expression" class="${noColor} mr-3"> <i class="${noExpressionIcon}"></i>&nbsp;No Expression</span>
          <span title="Ambiguous" class="${amColor} mr-3"><i class="${ambiguousIcon}"></i>&nbsp;Ambiguous</span>
          <span title="No Tissue Available" class="${noAvaColor}"><i
                  class="${noTissueIcon}"></i>&nbsp;No Tissue Available</span>
      </div>

      <!-- <h2 class="title" id="section-impc_expression">Expression Overview<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
      -->

<%--      <script>
          $(document).ready(function () {
              $('#embryoExpressionTable').DataTable({
                  responsive: true,
                  "bFilter":false,
                  "bLengthChange": false
              });
          });
      </script>--%>
      <div class="row justify-content-center">
          <div class="col-md-12">
              <table id="embryoExpressionTable" data-toggle="table" data-pagination="true" data-mobile-responsive="true" data-sortable="true">
                  <thead>
                  <th data-sortable="true">Anatomy</th>
                  <th
                          title="Number of heterozygous mutant specimens with data for the specified anatomy" data-sortable="true">
                      #HET Specimens
                  </th>
                  <th
                          title="Status of expression for Wild Type specimens from any colony with data for this anatomy">
                      WT Expr
                  </th>
                  <th title="">Mutant Expr</th>
                  <%-- <th>Mutant specimens</th> --%>
                  <th
                          title="An clickable image icon will show if images are available for mutant specimens" data-sortable="true">
                      Images
                  </th>
                  </thead>
                  <tbody>
                  <c:forEach var="mapEntry" items="${embryoExpressionAnatomyToRow}">
                      <tr>
                          <td>
                              <span>${fn:toUpperCase(fn:substring(mapEntry.key, 0, 1))}${fn:toLowerCase(fn:substring(mapEntry.key, 1,fn:length(mapEntry.key)))}</span>
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
                                                            class="${expressionIcon} ${yesColor}"
                                                    ></span>&nbsp;(${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimenExpressed)}/${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimen)})
                                  </c:when>
                                  <c:when
                                          test="${embryoWtAnatomyToRow[mapEntry.key].notExpressed}">
                          							<span
                                                            title="WT NOT expressed: ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimenNotExpressed)} Not Expressed ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimen)} wild type specimens"
                                                            class="${noExpressionIcon} ${noColor}"></span>
                                  </c:when>
                                  <c:when
                                          test="${embryoWtAnatomyToRow[mapEntry.key].noTissueAvailable}">
                                      <i title="WT No Tissue Available"
                                         class="${noTissueIcon} ${noAvaColor}"
                                      ></i>
                                  </c:when>
                                  <c:when
                                          test="${embryoWtAnatomyToRow[mapEntry.key].imageOnly}">
                                      <%--  <i title="Image Only"
                                          class="${noTissueIcon}"
                                          style="color:${noColor}"> --%>Image Only<!-- </i> -->
                                  </c:when>
                                  <c:when
                                          test="${embryoWtAnatomyToRow[mapEntry.key].ambiguous}">
                       <i title="Ambiguous"
                             class="${ambiguousIcon} ${amColor}"></i>

                                  </c:when>

                                  <c:otherwise>
                                      No Data
                                  </c:otherwise>
                              </c:choose>
                          </td>
                          <td>
                              <c:choose>
                                  <c:when
                                          test="${mapEntry.value.expression}">
                                     				<span
                                                            title="Expressed: ${fn:length(mapEntry.value.specimenExpressed)} mutant specimens expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens"
                                                            class="${expressionIcon} ${yesColor}"
                                                    ></span>&nbsp;(${fn:length(mapEntry.value.specimenExpressed)}/${fn:length(mapEntry.value.specimen)})
                                  </c:when>
                                  <c:when
                                          test="${mapEntry.value.notExpressed}">
                          							<span
                                                            title="Not Expressed: ${fn:length(mapEntry.value.specimenNotExpressed)} Not Expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens"
                                                            class="${noExpressionIcon} ${noColor}"></span>
                                  </c:when>
                                  <c:when
                                          test="${mapEntry.value.noTissueAvailable}">
                          							<span title="No Tissue Available"
                                                          class="${noTissueIcon} ${noAvaColor}"></span>
                                  </c:when>

                                  <c:when
                                          test="${mapEntry.value.imageOnly}">
                                      <%-- <span title="Image Only"
                                          class="${noTissueIcon}" style="color:${noColor}"> --%>Image Only<!-- </span> -->
                                  </c:when>

                                  <c:when test="${mapEntry.value.ambiguous}">
                                     				<span title="Ambiguous"
                                                          class="${ambiguousIcon} ${amColor}"></span>
                                  </c:when>

                                  <c:otherwise>
                                      No Data
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
                                  <a
                                          href='${baseUrl}/imageComparator?acc=${acc}&anatomy_id=${mapEntry.value.abnormalAnatomyId}&parameter_stable_id=IMPC_ELZ_064_001' class="mr-1" style="font-size: small"><i
                                          title="Wholemount Images available (click on this icon to view images)"
                                          class="fa fa-image"
                                          alt="Images"></i>&nbsp;Wholemount images
                                  </a>
                              </c:if>
                              <c:if
                                      test="${embryoMutantImagesAnatomyToRow[mapEntry.key].sectionImagesAvailable}">
                                  <a
                                          href='${baseUrl}/imageComparator?acc=${acc}&anatomy_id=${mapEntry.value.abnormalAnatomyId}&parameter_stable_id=IMPC_ELZ_063_001' class="mr-1" style="font-size: small"><i
                                          title="Section Images available (click on this icon to view images)"
                                          class="fa fa-image"
                                          alt="Images"></i>&nbsp;Section images
                                  </a>
                              </c:if>
                              <c:if test="${not embryoMutantImagesAnatomyToRow[mapEntry.key].sectionImagesAvailable and not embryoMutantImagesAnatomyToRow[mapEntry.key].wholemountImagesAvailable}">
                                <span>
                                    N/A
                                </span>
                              </c:if>
                          </td>


                      </tr>
                  </c:forEach>
                  </tbody>
              </table>

          </div>
      </div>
  </div>
</div>

<div id="embryo2" class="container mt-3">
    <c:choose>
        <c:when test="${not empty wholemountExpressionImagesEmbryoBean.expFacetToDocs || not empty sectionExpressionEmbryoImagesBean.expFacetToDocs}">
            <jsp:include page="genesEmbExpImg_frag.jsp"></jsp:include>
        </c:when>
        <c:otherwise>
            <h5>
                No expression image was found for this embryo tab
            </h5>
        </c:otherwise>
    </c:choose>
</div>



