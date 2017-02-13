<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 16:31
  To change this template use File | Settings | File Templates.
--%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<div id="expDataView">Show expression table</div>

<div id="anatomo1">
    <jsp:include page="genesAdultExpEata_frag.jsp"></jsp:include>
</div>
<div id="anatomo2">
    <div class="title" id="expression-anatomogram">Expression in Anatomogram
        <!--<span
			class="documentation"><a href='' id='expressionAnatomogramPanel'
									 class="fa fa-question-circle pull-right"></a></span>-->
        <!--  this works, but need js to drive tip position -->
    </div><br>

    <div class='aright' id='anatomogramContainer'></div>
    <div class='aleft'>
      <div>Tissues/organs lacZ+ expression</div><br>
      <ul id='expList'>
       <table>
       <%-- <tr><th>Anatomy</th><th>Wholemounts</th><th>Sections</th></tr> --%>
        <c:forEach var="entry" items="${topLevelMaCounts}"
                   varStatus="status">
        <%--  <c:set var="href"
                 scope="page"
                 value="${baseUrl}/impcImages/laczimages/${acc}/${entry.key}">
          </c:set>  --%>
          <c:choose>
          <c:when test="${wholemountExpressionImagesBean.haveImpcImages[entry.key] || sectionExpressionImagesBean.haveImpcImages[entry.key]}">
          
         
          <tr>
          	<td class="showAdultImage" title="images available">
          		${entry.key}
          	</td>
          	<td>
          		<c:choose>
          			<c:when test="${wholemountExpressionImagesBean.haveImpcImages[entry.key]}">
          			<a title="Wholemount Images available" href="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.key}&parameter_stable_id=IMPC_ALZ_076_001"><i class="fa fa-image"></i></a>
          			</c:when>
          			<c:otherwise>
          			</c:otherwise>
          		</c:choose>
          	</td>
          	<td>
          		<c:choose>
          			<c:when test="${sectionExpressionImagesBean.haveImpcImages[entry.key]}">
          		<a title="Section Images Available" href="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.key}&parameter_stable_id=IMPC_ALZ_075_001"><i class="fa fa-image"></i></a>
          		</c:when>
          		<c:otherwise></c:otherwise>
          		</c:choose>
          	</td>
          </tr>
          
          
          </c:when>
          <c:otherwise>
          	<td class="showAdultImage" title="no images available, only categorical data - click the expression table link to the right to see data">
          		${entry.key}
          	</td>
          </c:otherwise>
          </c:choose>
        </c:forEach>
        </table>
      </ul>
    </div>
</div>



