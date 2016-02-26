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


<c:forEach var="entry" items="${solrFacets}" varStatus="status">
  <div class="accordion-group">
    <div class="accordion-heading">
        ${entry.name} (${entry.count})
    </div>
    <div class="accordion-body">
      <ul>
        <c:forEach var="doc" items="${facetToDocs[entry.name]}">
          <li>
            <t:imgdisplay
                    img="${doc}"
                    mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
          </li>
        </c:forEach>
      </ul>
      <div class="clear"></div>
      <c:if test="${entry.count>5}">
        <p class="textright">
          <a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}">
            <i class="fa fa-caret-right"></i> show all ${entry.count} images</a>
        </p>
      </c:if>
    </div>
    <!--  end of accordion body -->
  </div>
</c:forEach><!-- solrFacets end -->

