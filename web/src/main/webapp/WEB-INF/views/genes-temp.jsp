<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 20:38
  To change this template use File | Settings | File Templates.
--%>
<div id="tabs">
  <ul class='tabs'>
    <li><a href="#tabs-1">Adult Expression Overview</a></li>
    <li><a href="#tabs-2">Adult Expression Data Overview</a></li>
    <li><a href="#tabs-3">Adult lacZ+ Expression Images</a></li>

    <c:if test="${not empty embryoExpressionDocs}">
      <li><a href="#tabs-4">Embryo Expression Images</a></li>
    </c:if>
  </ul>

  <div id="tabs-1">
    <!-- Expression in Anatomogram -->
    <c:if test="${!isLive}">
      <jsp:include page="genesAnatomogram_frag.jsp"></jsp:include>
    </c:if>
  </div>

  <div id="tabs-2" style="height: 500px; overflow: auto;">
    <jsp:include page="genesAdultExpEata_frag.jsp"></jsp:include>
  </div>

  <div id="tabs-3">
    <jsp:include page="genesAdultLacZ+ExpImg_frag.jsp"></jsp:include>
  </div>


  <!--  <a href="/phenotype-archive/imagePicker/MGI:1922730/IMPC_ELZ_063_001">
<img src="//wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/177626/200/" style="max-height: 200px;"></a> -->

  <c:if test="${not empty embryoExpressionDocs}">
    <div id="tabs-4">
      <jsp:include page="genesEmbExpData_frag.jsp"></jsp:include>
    </div>
  </c:if>


</div><!-- end of tabs -->
</div><!-- end of inner -->
</div><!-- end of section -->