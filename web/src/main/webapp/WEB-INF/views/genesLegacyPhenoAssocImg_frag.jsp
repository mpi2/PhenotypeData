<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 25/02/2016
  Time: 12:44
  To change this template use File | Settings | File Templates.
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<!-- nicolas accordion for Legacy images here -->

  <!-- Legacy Phenotype Associated Images -->
  <%--<div class="section">--%>
    <%--<h2 class="title" id="section-images">Phenotype Associated Images--%>
      <%--<span class="documentation"><a href='' id='legacyImagesPanel' class="fa fa-question-circle pull-right"></a></span>--%>
    <%--</h2>--%>
    <!--  <div class="alert alert-info">Work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</div>	 -->

<div class="accordion mb-3" id="legacyImagesAccordion">
  <div class="list-group">
    <c:forEach var="entry" items="${solrFacets}" varStatus="status">
      <div id="legacyImagesHeading${status.index}">
        <button class="btn btn-link list-group-item list-group-item-action" type="button"
                data-toggle="collapse"
                data-target="#legacyImagesCollapse${status.index}" aria-expanded="false"
                aria-controls="collapse${status.index}">
            ${entry.name} (${entry.count})
        </button>
      </div>

      <div id="legacyImagesCollapse${status.index}" class="collapse"
           aria-labelledby="legacyImagesHeading${status.index}"
           data-parent="#legacyImagesAccordion">
        <div class="tz-gallery small">
          <div class="container">
            <div class="row">
              <c:forEach var="doc" items="${facetToDocs[entry.name]}">

                <t:imgdisplay
                        img="${doc}"
                        mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>

              </c:forEach>
            </div>
          </div>
        </div>
        <c:if test="${entry.count>5}">
          <p class="text-right">
            <a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}">View
              all ${entry.count} images</a>
          </p>
        </c:if>
      </div>
    </c:forEach>
  </div>
