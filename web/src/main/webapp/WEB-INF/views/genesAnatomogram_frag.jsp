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
            <input type="radio" name="options" id="expressionTableToggle" value="table" autocomplete="off"> Table
        </label>
        <label class="btn btn-outline-primary btn-sm">
            <input type="radio" name="options" id=" " autocomplete="off" value="anatogram" checked> Images
        </label>
    </div>
</div>

<div id="anatomo1" class="row justify-content-center">
    <jsp:include page="genesAdultExpEata_frag.jsp"></jsp:include>
</div>
<div id="anatomo2" class="container mt-3">

    <div class="row justify-content-center">
        <jsp:include page="genesAdultLacZExpImg_frag.jsp"></jsp:include>
    </div>
</div>



