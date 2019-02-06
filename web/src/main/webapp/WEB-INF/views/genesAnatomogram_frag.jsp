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

<div class="row justify-content-end mt-3 mr-2">
    <div class="btn-group btn-group-toggle" data-toggle="buttons">
        <label class="btn btn-outline-primary btn-sm active">
            <input type="radio" name="options" id=" " autocomplete="off" value="anatogram" checked> Anatogram
        </label>
        <label class="btn btn-outline-primary btn-sm">
            <input type="radio" name="options" id="expressionTableToggle" value="table" autocomplete="off"> Table
        </label>
    </div>
</div>

<div id="anatomo1" class="row justify-content-center">
    <jsp:include page="genesAdultExpEata_frag.jsp"></jsp:include>
</div>
<div id="anatomo2" class="container mt-3">

    <div class="row justify-content-center">
        <div class='col-sm-4 text-center'>
            <h6 id="expression-anatomogram" class="font-weight-bold">Expression in Anatomogram</h6>
            <div id='anatomogramContainer'></div>
        </div>
        <div class='col-sm-4'>
            <h6 class="font-weight-bold text-center">Tissues/organs lacZ+ expression</h6>
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



</div>



