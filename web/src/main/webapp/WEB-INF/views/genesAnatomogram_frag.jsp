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
        <c:forEach var="entry" items="${topLevelMaCounts}"
                   varStatus="status">
         <c:set var="href"
                 scope="page"
                 value="${baseUrl}/impcImages/laczimages/${acc}/${entry.key}">
          </c:set> 
          <c:choose>
          <c:when test="${haveImpcAdultImages[entry.key]}">
          <li class="showAdultImage" title="images available">
          		<a href="${baseUrl}/impcImages/laczimages/${acc}/${entry.key}">${entry.key} </a>
          </li>
          </c:when>
          <c:otherwise>
          	<li class="showAdultImage" title="no images available, only categorical data - click the expression table link to the right to see data">
          		${entry.key}
          	</li>
          </c:otherwise>
          </c:choose>
        </c:forEach>
      </ul>
    </div>
</div>



