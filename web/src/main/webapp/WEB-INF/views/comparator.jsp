<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:genericpage>
<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol}</a>&nbsp;&raquo; Image Comparator</jsp:attribute>


 <jsp:attribute name="title">Image Picker</jsp:attribute>
<jsp:attribute name="header">
 <!--  <link rel="stylesheet" type="text/css" href="css/bootstrap.css">
  <link rel="stylesheet" type="text/css" href="css/bootstrap-responsive.css">
  <link rel="stylesheet" type="text/css" href="examples.css">
  <link rel="stylesheet" type="text/css" href="image-picker/image-picker.css"> -->
  
  <link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
 <link href="${baseUrl}/css/comparator/comparator.css" rel="stylesheet" type="text/css" />
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script type='text/javascript' src="${baseUrl}/js/comparator/comparator.js?v=${version}"></script>
 
 
</jsp:attribute>
<jsp:body>

<!-- <div class="region region-content">
	<div class="block">
    	<div class="content">
        	<div class="node"> -->
        	<%--  mediaType: ${mediaType }
        	impcMediaBaseUrl: ${impcMediaBaseUrl }  --%>
        	request uri=${httpSerlvetRequest.isSecure()}
        	<c:set var="protocol" value="http:"/>
	        <c:if test="${fn:startsWith(pageContext.request.requestURI,'https:')}">
	        protocol before set in if  =${protocol }
	        	<c:set var="protocol" value="https:"/>
	        	protocol after set in if  =${protocol }
	        </c:if>
	        
	        <c:set var="jpegUrlThumbWithoutId" value="${protocol}/${impcMediaBaseUrl}/render_thumbnail/"/>
	        <c:set var="jpegUrlDetailWithoutId" value="${protocol}/${impcMediaBaseUrl}/img_detail/"/>
	        <c:set var="pdfWithoutId" value="http:${fn:replace(impcMediaBaseUrl,'webgateway/','webclient/annotation/')}"/>
	        <c:set var="thumbnailSize" value="70"/>
	       <%-- jpegUrlThumbWithoutId: ${jpegUrlThumbWithoutId}
	        jpegUrlDetailWithoutId: ${jpegUrlDetailWithoutId}
	        pdfWithoutId: ${pdfWithoutId} --%>
            <form action="">
           
	            <div id="comparator" class="section">
	            <c:if test="${mediaType !=null }">
	            <input type="hidden" name="mediaType" value="${mediaType}">
	            </c:if>
	            	<div id="filters">Filter Images by gender: 
	            	<%-- ${param.gender} --%>
	            	<select name="gender">
	            	<option value="both" <c:if test="${param.gender eq 'both'}">selected</c:if> >Both</option>
            			<option value="male" <c:if test="${param.gender eq 'male'}">selected</c:if> >Males</option>
            			<option value="female"  <c:if test="${param.gender eq 'female'}">selected</c:if>>Females</option>
            		</select>
            		zygosity: 
            		<%-- ${param.zygosity} --%>
             		<select name="zygosity">
             		<option value="not_applicable" <c:if test="${param.zygosity eq 'not_applicable'}">selected</c:if>>All</option>
            			<option value="heterozygote" <c:if test="${param.zygosity eq 'heterozygote'}">selected</c:if>>Het</option>
            			<option value="homozygote" <c:if test="${param.zygosity eq 'homozygote'}">selected</c:if>>Hom</option>
            		</select>
            		<input type="submit" value="Go">
            		</div>
	            	<div id="control_box" class=box>
		            	<c:choose>
			            	<c:when test="${not empty controls}">
			            		<c:choose>
			            			<c:when test="${mediaType eq 'pdf' }">		            			
			            				<iframe id="control_frame"
											src="//docs.google.com/gview?url=${pdfWithoutId}${controls[0].omero_id}&embedded=true"></iframe>
			            			</c:when>
			            			<c:otherwise>
			            				<iframe id="control_frame"
												src="${jpegUrlDetailWithoutId}${controls[0].omero_id }"></iframe>
			            			</c:otherwise>
			            		</c:choose>
			            		
							</c:when>
							<c:otherwise>
								No Image for Controls Selected
							</c:otherwise>
						</c:choose>
	            
	            		<div id="control_annotation" class="annotation">
	            			${controls[0].sex}, ${controls[0].zygosity}
	            		</div>
	            		<div class="picker">
	            			<c:forEach var="img" items="${controls}" varStatus="controlLoop">
	            			<div class="clickbox">
	            				<c:choose>
									<c:when test="${mediaType eq 'pdf' }">
										<img id="${img.omero_id}" src="../${pdfThumbnailUrl}" style="width:${thumbnailSize}px" class="clickable_image_control <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" title="${img.sex}">
									</c:when>
									<c:otherwise>
	            						<img id="${img.omero_id}" src="${jpegUrlThumbWithoutId}${img.omero_id}/${thumbnailSize}" class="clickable_image_control <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" title="${img.sex}">
	            					</c:otherwise>
	            				</c:choose>
	            				</div>
	            			</c:forEach>
	            		</div>
	            	
	            	</div>
	            
	 
	            	<div id="mutant_box" class=box>
	            	<c:choose>
			            	<c:when test="${not empty mutants}">
			            		<c:choose>
			            			
			            			<c:when test="${mediaType eq 'pdf' }">		            			
			            			<iframe id="mutant_frame"
										src="//docs.google.com/gview?url=${pdfWithoutId}${mutants[0].omero_id}&embedded=true"></iframe>
			            			</c:when>
			            			<c:otherwise>
			            			<iframe id="mutant_frame"
										src="${jpegUrlDetailWithoutId}${mutants[0].omero_id }"></iframe>
			            			</c:otherwise>
			            			</c:choose>
								
							</c:when>
							<c:otherwise>
								No Image for Mutants Selected
							</c:otherwise>
					</c:choose>
	            		<div id="mutant_annotation" class="annotation">
	            		${mutants[0].sex}, ${mutants[0].zygosity}
	            		</div>
	            		<div class="picker">
	            			<c:forEach var="img" items="${mutants}" varStatus="mutantLoop">
	            			<div class="clickbox">
								<c:choose>
									<c:when test="${mediaType eq 'pdf' }">
										<img id="${img.omero_id}" src="../${pdfThumbnailUrl}" style="width:${thumbnailSize}px" class="clickable_image_mutant <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${img.sex}, ${img.zygosity}">
									</c:when>
									<c:otherwise>
	            						<img id="${img.omero_id}" src="${jpegUrlThumbWithoutId}${img.omero_id}/${thumbnailSize}" class="clickable_image_mutant <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${img.sex}, ${img.zygosity}">
	            					</c:otherwise>
	            				</c:choose>
	            			</div>
	            			</c:forEach> 
	            		</div>
	            	
	            	</div>
	            </div>
            </form>
            <!-- </div>




		
			</div>
 		</div>
	</div> -->
	
	<script type='text/javascript'>
	var jpegUrlDetailWithoutId = "${jpegUrlDetailWithoutId}";
	var pdfWithoutId = "${pdfWithoutId}";
	var googlePdf="${protocol}//docs.google.com/gview?url=replace&embedded=true";
	</script>

</jsp:body>
</t:genericpage>