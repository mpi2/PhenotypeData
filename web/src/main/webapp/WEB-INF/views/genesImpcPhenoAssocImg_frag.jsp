<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 19:53
  To change this template use File | Settings | File Templates.
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<!-- IMPC Phenotype Associated Images -->
<%--<div class="section">--%>
  <%--<h2 class="title" id="section-impc-images">IMPC Phenotype Associated Images<span--%>
          <%--class="documentation"><a href='' id='impcImagesPanel'--%>
                                   <%--class="fa fa-question-circle pull-right"></a></span>--%>
  <%--</h2>--%>


<c:forEach var="entry" items="${impcImageFacets}" varStatus="status">

  <c:forEach var="doc"
             items="${impcFacetToDocs[entry.name]}">
    <c:if test="${doc.procedure_name ne 'Embryo LacZ' }">
      <div id="impc-images-heading" class="accordion-group">

        <div class="accordion-heading">
           <c:choose>
           	<c:when test="${doc.parameter_name eq 'Images'}">
           		${doc.procedure_name}
           	</c:when>
           	<c:otherwise>
            	${doc.parameter_name}
           	</c:otherwise>
           </c:choose>
           (${entry.count})
        </div>
        <div class="accordion-body">
          <ul>
            <c:set var="href" scope="page"
                   value="${baseUrl}/imageComparator/${acc}/${entry.name}"></c:set>
            <a href="${href}">
              <t:impcimgdisplay2
                      img="${doc}"
                      impcMediaBaseUrl="${impcMediaBaseUrl}"
                      pdfThumbnailUrl="${pdfThumbnailUrl}"
                      href="${href}"
                      count="${entry.count}"></t:impcimgdisplay2>
            </a>
          </ul>


            <%--  <div class="clear"></div>
                <c:if test="${entry.count>5}">
                    <p class="textright"><a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}"><i class="fa fa-caret-right"></i> show all ${entry.count} images</a></p>
                </c:if> --%>
        </div> <!--  end of accordion body -->
      </div> <!-- accordion-group -->
    </c:if>

  </c:forEach>

</c:forEach>


