<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 16:45
  To change this template use File | Settings | File Templates.
--%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<!-- <h2 class="title" id="section-impc_expression">Expression Data<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
-->

    <h3><a href="${baseUrl}/impcImages/laczimages/${acc}">All Images</a>
    </h3>
    <div class="accordion-body"
     style="display: block;">
    <h3>LacZ Wholemount Images</h3>
    <c:forEach var="entry" items="${wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms}"
               varStatus="status">
               
               
 <%--  value="${baseUrl}/imageComparator?&acc=${acc}&anatomy_term=${entry.name}"></c:set> --%><!--  &acc=${acc}&parameter_stable_id=IMPC_ALZ_076_001 -->
        <c:set var="href"
               scope="page"
             
               value="${baseUrl}/impcImages/laczimages/${acc}/${entry.name}"></c:set>
        <ul>
            <t:impcimgdisplay2
                    category="${entry.name}(${entry.count})" 
                    href="${fn:escapeXml(href)}"
                    img="${wholemountExpressionImagesBean.expFacetToDocs[entry.name][0]}"
                    impcMediaBaseUrl="${impcMediaBaseUrl}"
                    >
            </t:impcimgdisplay2>
        </ul>

    </c:forEach> <!-- solrFacets end -->
    
    </div>
    
    <div class="accordion-body"
     style="display: block;">
   <h3>LacZ Section Images</h3>
     <c:forEach var="entry" items="${sectionExpressionImagesBean.filteredTopLevelAnatomyTerms}"
               varStatus="status">
               
               
 <%--  value="${baseUrl}/imageComparator?&acc=${acc}&anatomy_term=${entry.name}"></c:set> --%><!--  &acc=${acc}&parameter_stable_id=IMPC_ALZ_076_001 -->
        <c:set var="href"
               scope="page"
             
               value="${baseUrl}/impcImages/laczimages/${acc}/${entry.name}"></c:set>
        <ul>
            <t:impcimgdisplay2
                    category="${entry.name}(${entry.count})" 
                    href="${fn:escapeXml(href)}"
                    img="${sectionExpressionImagesBean.expFacetToDocs[entry.name][0]}"
                    impcMediaBaseUrl="${impcMediaBaseUrl}"
                    >
            </t:impcimgdisplay2>
        </ul>

    </c:forEach> <!-- solrFacets end -->
	</div>



