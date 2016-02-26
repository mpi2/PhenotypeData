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

<!-- always show phenotype icons -->
<jsp:include page="phenotype_icons_frag.jsp"/>
<c:choose>
  <c:when test="${summaryNumber > 0}">

    <%--<jsp:include page="phenotype_icons_frag.jsp"/>--%>

    <c:if test="${!(empty dataMapList)}">
      <br/>
      <!-- best example http://localhost:8080/PhenotypeArchive/genes/MGI:1913955 -->

      <div class="floatright marginup"
           style="clear: both">
        <p>
          <a class="btn"
             href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}'>All Adult Phenotype</a>
        </p>
      </div>
    </c:if>

    <c:if test="${gene.embryoDataAvailable}">
      <div class="floatright marginup"
           style="clear: both">
        <a class="btn"
           href="${drupalBaseUrl}/embryoviewer?mgi=${acc}">Embryo Viewer</a>
      </div>
    </c:if>
    <h5 class="sectHint">Phenotype Summary</h5>
    <p>Based on automated MP annotations supported by experiments
      on knockout mouse models. </p>

    <c:forEach var="zyg"
               items="${phenotypeSummaryObjects.keySet()}">
      <p>In <b>${zyg} :</b>
      </p>
      <ul class="phenoSum">
        <c:if test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getBothPhenotypes(true).size() > 0}'>
          <li> <b>Both sexes</b> have the following phenotypic abnormalities
            <ul>
              <c:forEach var="summaryObj"
                         items='${phenotypeSummaryObjects.get(zyg).getBothPhenotypes(true)}'>
                <li>
                  <a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>.
                  Evidence from
                  <c:forEach var="evidence"
                             items="${summaryObj.getDataSources()}"
                             varStatus="loop">
                    ${evidence}
                    <c:if test="${!loop.last}">,&nbsp;
                    </c:if>
                  </c:forEach> <%--&nbsp;&nbsp;&nbsp; (<a
                        class="filterTrigger"
                        id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>) --%>
                </li>
              </c:forEach>
            </ul>
          </li>
        </c:if>

        <c:if test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getFemalePhenotypes(true).size() > 0}'>
          <li><b>females</b> only have the following phenotypic abnormalities only only
            <ul>
              <c:forEach
                      var="summaryObj"
                      items='${phenotypeSummaryObjects.get(zyg).getFemalePhenotypes(true)}'>
                <li><a
                        href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>.
                  Evidence from <c:forEach
                          var="evidence"
                          items="${summaryObj.getDataSources()}"
                          varStatus="loop"> ${evidence} <c:if
                          test="${!loop.last}">,&nbsp;</c:if>
                  </c:forEach> <%-- &nbsp;&nbsp;&nbsp; (<a
                          class="filterTrigger"
                          id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>) --%>
                </li>
              </c:forEach>
            </ul>
          </li>
        </c:if>

        <c:if test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getMalePhenotypes(true).size() > 0}'>
          <li> <b>males</b> only have the following phenotypic abnormalities only
            <ul>
              <c:forEach
                      var="summaryObj"
                      items='${phenotypeSummaryObjects.get(zyg).getMalePhenotypes(true)}'>
                <li><a
                        href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>.
                  Evidence from <c:forEach
                          var="evidence"
                          items="${summaryObj.getDataSources()}"
                          varStatus="loop"> ${evidence} <c:if
                          test="${!loop.last}">,&nbsp;</c:if>
                  </c:forEach> <%-- &nbsp;&nbsp;&nbsp; (<a
                          class="filterTrigger"
                          id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>) --%>
                </li>
              </c:forEach>
            </ul>
          </li>
        </c:if>
      </ul>
    </c:forEach>

  </c:when>

  <c:when test="${summaryNumber == 0}">

    <c:if test="${empty dataMapList && empty phenotypes}">
      <c:if test="${attemptRegistered}">
        <div class="alert alert-info">
          <h5>Registered for phenotyping</h5>

          <p>Phenotyping is planned for a knockout strain of this gene but
            data is not currently available.</p>
        </div>
      </c:if>

      <c:if test="${!attemptRegistered}">
        <div class="alert alert-info">
          <h5>Phenotype data is undergoing quality control</h5>

          <p>Any phenotype assocations appearing below are preliminary and may
            change. Links are provided to the Pheno-DCC quality control
            resource.</p>
        </div>
      </c:if>
      <br/>
    </c:if>
    <c:if test="${!(empty dataMapList) && empty phenotypes}">
      <div class="alert alert-info">
        <h5>No Significant Phenotype Associations Found</h5>

        <p>No significant phenotype associations were found with data that has
          passed quality control (QC), but you can click
          on the "All Adult Data" button to see all phenotype data that has
          passed QC. Preliminary phenotype assocations
          may appear with new pre-QC phenotype data.</p>
      </div>
      <br/>
      <!-- best example http://localhost:8080/PhenotypeArchive/genes/MGI:1913955 -->
      <div class="floatright marginup" style="clear: both">
        <a class="btn" href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}'>All Adult Data</a>
      </div>
        <div class="clear"></div>
    </c:if>

    <c:if
            test="${gene.embryoDataAvailable}">
      <div class="floatright marginup"
           style="clear: both">
        <a class="btn"
           href="${drupalBaseUrl}/embryoviewer?mgi=${acc}">Embryo Viewer</a>
      </div>
    </c:if>
  </c:when>
  <c:when test="${hasPreQcData}">
    <!-- Only pre QC data available, suppress post QC phenotype summary -->
  </c:when>
  <c:otherwise>
    <div class="alert alert-info">There are currently no IMPC phenotype associations
      for the gene ${gene.markerSymbol} </div>
    <br/>
  </c:otherwise>
</c:choose>

<c:if
        test='${hasPreQcData || summaryNumber > 0 || phenotypes.size() > 0}'>
  <!-- Associations table -->
  <h5>Filter significant phenotype</h5> (significant p value: <= 0.0001)


  <div class="row-fluid">
    <div class="container span12">
      <br/>

      <div class="row-fluid" id="phenotypesDiv">

        <div class="container span12">

          <c:if test="${not empty phenotypes}">
            <form
                    class="tablefiltering no-style" id="target"
                    action="destination.html">
              <c:forEach
                      var="phenoFacet" items="${phenoFacets}"
                      varStatus="phenoFacetStatus">
                <select
                        id="${phenoFacet.key}" class="impcdropdown"
                        multiple="multiple"
                        title="Filter on ${phenoFacet.key}">
                  <c:forEach
                          var="facet" items="${phenoFacet.value}">
                    <option>${facet.key}</option>
                  </c:forEach>
                </select>
              </c:forEach>
              <div class="clear"></div>
            </form>
            <div class="clear"></div>

            <c:set var="count" value="0" scope="page"/>
            <c:forEach
                    var="phenotype" items="${phenotypes}"
                    varStatus="status">
              <c:forEach
                      var="sex" items="${phenotype.sexes}">
                <c:set var="count" value="${count + 1}" scope="page"/>
              </c:forEach>
            </c:forEach>

            <jsp:include page="PhenoFrag.jsp"></jsp:include>
            <br/>

            <div
                    id="exportIconsDiv"></div>
          </c:if>

          <!-- if no data to show -->
          <c:if
                  test="${empty phenotypes}">
            <div
                    class="alert alert-info">Pre QC data has been submitted
              for this gene. Once the QC process is finished phenotype
              associations stats will be made available.
            </div>
          </c:if>

        </div>
      </div>
    </div>
  </div>

</c:if>