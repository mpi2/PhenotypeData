<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 21:02
  To change this template use File | Settings | File Templates.
--%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div class="accordion-body"
     style="display: block;">

  <a href="${baseUrl}/impcImages/embryolaczimages/${acc}">All Images</a>
  <c:forEach var="entry" items="${wholemountExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms}"
             varStatus="status">

    <c:set var="href"
           scope="page"
           value='${baseUrl}/imageComparator?acc=${acc}&anatomy_term="${entry.name}"&parameter_stable_id=IMPC_ELZ_064_001'></c:set>
    <ul>
      <t:impcimgdisplay2
              category="${fn:replace(entry.name, 'TS20 ','')}(${entry.count})" href="${fn:escapeXml(href)}"
              img="${wholemountExpressionImagesEmbryoBean.expFacetToDocs[entry.name][0]}"
              impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
    </ul>

  </c:forEach> <!-- solrFacets end -->
  
   <c:forEach var="entry" items="${sectionExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms}"
             varStatus="status">

    <c:set var="href"
           scope="page"
           value='${baseUrl}/imageComparator?acc=${acc}&anatomy_term="${entry.name}"&parameter_stable_id=IMPC_ELZ_063_001'></c:set>
    <ul>
      <t:impcimgdisplay2
              category="${fn:replace(entry.name, 'TS20 ','')}(${entry.count})" href="${fn:escapeXml(href)}"
              img="${sectionExpressionImagesEmbryoBean.expFacetToDocs[entry.name][0]}"
              impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
    </ul>

  </c:forEach> <!-- solrFacets end -->

</div>
