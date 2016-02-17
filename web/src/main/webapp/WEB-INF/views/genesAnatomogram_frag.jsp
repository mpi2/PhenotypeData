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


<div id="expDataView">Show adult expression data in table</div>

<div id="anatomo1">
    <jsp:include page="genesAdultExpEata_frag.jsp"></jsp:include>
</div>
<div id="anatomo2">
    <br>
    <h6 class="title" id="expression-anatomogram">Expression in Anatomogram
        <!--<span
			class="documentation"><a href='' id='expressionAnatomogramPanel'
									 class="fa fa-question-circle pull-right"></a></span>-->
        <!--  this works, but need js to drive tip position -->
    </h6>

    <div class='aright' id='anatomogramContainer'></div>
    <div class='aleft'>
      <h6>Annotated lacZ+ tissues / organs:</h6>
      <ul id='expList'>
        <c:forEach var="entry" items="${impcExpressionImageFacets}"
                   varStatus="status">
          <c:set var="href"
                 scope="page"
                 value="${baseUrl}/impcImages/laczimages/${acc}/${entry.name}">
          </c:set>
          <li class="showAdultImage">${entry.name}(${entry.count})</li>
        </c:forEach>
      </ul>
    </div>
</div>



