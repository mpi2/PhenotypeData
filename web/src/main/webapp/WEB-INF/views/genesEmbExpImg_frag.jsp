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
  <c:forEach var="entry" items="${impcEmbryoExpressionImageFacets}"
             varStatus="status">

    <c:set var="href"
           scope="page"
           value="${baseUrl}/impcImages/embryolaczimages/${acc}/${entry.name}"></c:set>
    <ul>
      <t:impcimgdisplay2
              category="${fn:replace(entry.name, 'TS20 ','')}(${entry.count})" href="${href}"
              img="${impcEmbryoExpressionFacetToDocs[entry.name][0]}"
              impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
    </ul>

  </c:forEach> <!-- solrFacets end -->

</div>
