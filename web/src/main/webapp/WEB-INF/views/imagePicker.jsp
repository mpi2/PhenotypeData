<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:genericpage>
<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol}</a>&nbsp;&raquo; Image Picker</jsp:attribute>


 <jsp:attribute name="title">Image Picker</jsp:attribute>
<jsp:attribute name="header">
 <!--  <link rel="stylesheet" type="text/css" href="css/bootstrap.css">
  <link rel="stylesheet" type="text/css" href="css/bootstrap-responsive.css">
  <link rel="stylesheet" type="text/css" href="examples.css">
  <link rel="stylesheet" type="text/css" href="image-picker/image-picker.css"> -->
  
  <link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
 <link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.core.css">
<link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.slider.css">

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>

 <!--  <script src="js/prettify.js" type="text/javascript"></script>
  <script src="js/jquery.masonry.min.js" type="text/javascript"></script>
  <script src="js/show_html.js" type="text/javascript"></script> -->
  <link rel="stylesheet" href="${baseUrl}/js/vendor/image-picker/image-picker.css">
  <script src="${baseUrl}/js/vendor/image-picker/image-picker.js" type="text/javascript"></script>
 <script src="${baseUrl}/js/imaging/imagesInteraction.js"></script>
<!-- http://rvera.github.io/image-picker/ -->
</jsp:attribute>
<jsp:body>

<div class="region region-content">
	<div class="block">
    	<div class="content">
        	<div class="node">
            <c:choose>
            	<c:when test="${mediaType=='pdf'}">
            		<form action="../../image_compara/pdf_compara.html" method="get">
            	</c:when>
            	<c:otherwise>
            		<form action="../../image_compara/image_compara.html" method="get">
            	</c:otherwise>
            </c:choose>               
        	
        	<p><input type="submit" value="Click to display selected images"></p>
        	
				<div class="section">
				 <h1 class="title" id="control">Controls</h1>
					<div class="inner">
						<select name="ctrImgId" multiple size="2" class="show-html">
  							<c:if test="${not empty controls}">
                            	<c:forEach var="img" items="${controls}">
                                <%-- <t:impcimgdisplay2 img="${doc}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2> --%>
                                <c:set var="thumbnailUrl" value="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200"/>
         		<c:if test="${mediaType=='pdf'}">
         		<c:set var="thumbnailUrl" value="../${pdfThumbnailUrl}"/>
         		</c:if>
                                	<option data-img-src="${thumbnailUrl}" value="${img.omero_id}" data-img-label="
   										<%-- <c:if test="${not empty img.external_sample_id}">sample id: ${img.external_sample_id}<br/></c:if> --%>
   										<c:if test="${not empty img.parameter_name}">${img.parameter_name}<br/></c:if>
   										<c:if test="${not empty img.sex and img.sex ne 'no_data'}">
   											<c:if test="${not empty img.sex}">
   									${img.sex}
   									<%-- <img alt="Female" src="${baseUrl}/img/female.jpg" />
   											<c:choose>
   											
   												<c:when test="${img.sex == 'female'}">
   												
													<img alt="Female" src="${baseUrl}/img/female.jpg" />
												</c:when>
												<c:otherwise>
												
													<img alt="Male" src="${baseUrl}/img/male.jpg" />
												</c:otherwise>
											</c:choose> --%>
										
									</c:if>
   										
   										</c:if>
   										<%-- <c:if test="${not empty img.date_of_experiment}">${img.date_of_experiment}<br/></c:if> --%>
   										 <c:if test="${not empty count}">${count} Images<br/></c:if>
                                                 <c:if test="${not empty img.parameter_association_name}">
                                                	<c:forEach items="${img.parameter_association_name}" varStatus="status">
                                                		<c:out value="${img.parameter_association_name[status.index]}"/> (<c:out value="${img.parameter_association_value[status.index]}"/>)
                                                	</br>
                                                	</c:forEach>
                                                </c:if>
                                                <%-- <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}</c:if> --%>
                                                <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>
   										<%-- <c:if test="${not empty img.date_of_experiment}">${img.date_of_experiment}</c:if> --%>
   										<%-- <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}</c:if> --%>
   										">
   									</option>
                             	</c:forEach>
  							</c:if>	
  						</select>
  					</div>
  				</div>
  				
  				<div class="section">
  				<h1 class="title" id="top">Mutants</h1>
  					<div class="inner">
  						<select name="expImgId" multiple size="2" class="show-html">		
  						<c:if test="${not empty experimental}">
                            <c:forEach var="img" items="${experimental}">
                            
                           <%--  <a href="${img.download_url}" >
         		<img  src="${pdfThumbnailUrl}/200" style="max-height: 200px;"></a> --%>
         		<c:set var="thumbnailUrl" value="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200"/>
         		<c:if test="${mediaType=='pdf'}">
         		<c:set var="thumbnailUrl" value="../${pdfThumbnailUrl}"/>
         		</c:if>
         		                                <option 
                                data-img-src="${thumbnailUrl}" 
                                value="${img.omero_id}" data-img-label="
                                   <%--  <c:if test="${not empty img.external_sample_id}">sample id: ${img.external_sample_id}<br/></c:if> --%>
                                    <c:if test="${not empty img.parameter_name}">${img.parameter_name}<br/></c:if>
                                    <c:if test="${not empty img.zygosity}">${img.zygosity}</c:if>
   									<c:if test="${not empty img.sex}">
   									${img.sex}
   									<%-- <c:choose>
   										<c:when test="${img.sex == 'female'}">
											<img alt="Female" src="${baseUrl}/img/female.jpg" />
										</c:when>
										<c:otherwise>
											<img alt="Male" src="${baseUrl}/img/male.jpg" />
										</c:otherwise>
									</c:choose> --%>
										
									</c:if>
   									 <c:if test="${not empty count}">${count} Images<br/></c:if>
   									 <%-- <c:if test="${not empty img.date_of_experiment}">${img.date_of_experiment}<br/></c:if> --%>
                                               <%--  <c:if test="${not empty img.parameter_association_name}">
                                                	<c:forEach var="pAssName" items="${img.parameter_association_name}" varStatus="status">${pAssName}<br/></c:forEach>
                                                </c:if>
                                                <c:if test="${not empty img.parameter_association_value}">
                                                	<c:forEach var="pAssValue" items="${img.parameter_association_value}" varStatus="status">${pAssValue}<br/> </c:forEach>
                                                </c:if> --%>
                                                
                                                <c:if test="${not empty img.parameter_association_name}">
                                                	<c:forEach items="${img.parameter_association_name}" varStatus="status">
                                                		<c:out value="${img.parameter_association_name[status.index]}"/> (<c:out value="${img.parameter_association_value[status.index]}"/>)
                                                	</br>
                                                	</c:forEach>
                                                </c:if>
                                     <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>
   									<%-- <c:if test="${not empty img.date_of_experiment}">${img.date_of_experiment}</c:if> --%>
   									<%-- <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}</c:if> --%>
   									"> <!--  end of option element -->
   								</option>
                            </c:forEach>
						</c:if>			
						</select>
					</div>
				</div>
				<p>
				<input type="submit" value="Click to display selected images">
				</p>
			</form>
		
	</div>
 </div>
</div>
</div>
</jsp:body>
</t:genericpage>