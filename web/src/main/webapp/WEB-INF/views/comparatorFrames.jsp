<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:genericpage>
<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol}</a>&nbsp;&raquo; Image Comparator</jsp:attribute>


 <jsp:attribute name="title">${gene.markerSymbol} Image Picker</jsp:attribute>
<jsp:attribute name="header">
  
  <%-- <link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" /> --%>
 <link href="${baseUrl}/css/comparator/comparator.css" rel="stylesheet" type="text/css" />
<!-- <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script> -->
<script type='text/javascript' src="${baseUrl}/js/comparator/comparatorFrames.js?v=${version}"></script>
 
 
</jsp:attribute>
<jsp:body>

<!-- <div class="region region-content">
	<div class="block">
    	<div class="content">
        	<div class="node"> -->
        	<%--  mediaType: ${mediaType }
        	impcMediaBaseUrl: ${impcMediaBaseUrl }  --%>
	        <c:set var="jpegUrlThumbWithoutId" value="${impcMediaBaseUrl}/render_birds_eye_view"/>
	        <c:set var="jpegUrlDetailWithoutId" value="${impcMediaBaseUrl}/img_detail"/>

	        <c:set var="pdfWithoutId" value="http:${fn:replace(impcMediaBaseUrl,'webgateway','webclient/annotation')}"/>
	        <c:set var="thumbnailSize" value="96"/>
	       <%-- jpegUrlThumbWithoutId: ${jpegUrlThumbWithoutId}
	        jpegUrlDetailWithoutId: ${jpegUrlDetailWithoutId}
	        pdfWithoutId: ${pdfWithoutId} --%>
            <form action="">
           
	            <div id="comparator" class="section">
	            <c:if test="${mediaType !=null }">
	            <input type="hidden" name="mediaType" value="${mediaType}">
	            <c:if test="${gene !=null }">
	            	<input type="hidden" name="acc" value="${gene.mgiAccessionId}">
	            </c:if>
	            <c:if test="${param.parameter_stable_id!=null}">
	            	<input type="hidden" name="parameter_stable_id" value="${param.parameter_stable_id}">
	            </c:if>
	            <c:if test="${param.parameter_association_value!=null}">
	            	<input type="hidden" name="parameter_association_value" value="${param.parameter_association_value}">
	            </c:if>
	            <c:if test="${param.anatomy_id!=null}">
	            	<input type="hidden" name="anatomy_id" value="${param.anatomy_id}">
	            </c:if>
	             <c:if test="${param.mp_id!=null}">
	            	<input type="hidden" name="mp_id" value="${param.mp_id}">
	            </c:if>
	            </c:if>
	             <div id="control_key" class="key-annotation">
	            		<span class="clickbox_male">&nbsp;&nbsp;&nbsp;</span>male
	            		<span class="clickbox_female">&nbsp;&nbsp;&nbsp;</span> female
	            		</div>
	            	<div id="filters">Filter Images by gender: 
	            	<%-- ${param.gender} --%>
	            	
	            	<select name="gender">
	            	<option value="not applicable" <c:if test="${param.gender eq 'not applicable'}">selected</c:if> >All</option>
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
            		<input type="submit" value="Go"> <span class="btn" id="mutant_only_button">Display Mutant Only</span>
            		</div>
            		<c:set var="ctrlImageLink" value="${controls[0].imageLink}"/>
	 				<c:set var="srcForControl" value="${jpegUrlDetailWithoutId}/${controls[0].omeroId }"/>
	 				<c:if test="${fn:containsIgnoreCase(ctrlImageLink, 'omero' ) || fn:containsIgnoreCase(ctrlImageLink, 'ndp' )}">
	 					<c:set var="srcForControl" value="${fn:replace(ctrlImageLink, 'http:','https:')}"/>
	 				</c:if>
	            	<div id="control_box" class="box half_box_left">
		            	<c:choose>
			            	<c:when test="${not empty controls}">
			            		<c:choose>
			            			<c:when test="${mediaType eq 'pdf' }">		            			
			            				<iframe id="control_frame"
											src="//docs.google.com/gview?url=${pdfWithoutId}/${controls[0].omeroId}&embedded=true"></iframe>
			            			</c:when>
			            			<c:otherwise>
			            				<iframe id="control_frame"
												src="${srcForControl}"></iframe>
			            			</c:otherwise>
			            		</c:choose>
			            		
							</c:when>
							<c:otherwise>
								No Image for Controls Selected
							</c:otherwise>
						</c:choose>
	            
	            		<div id="control_annotation" class="annotation">
	            			WT: ${controls[0].sex}, ${controls[0].parameterName }
	            		</div>
	            		<div class="picker">
	            			<c:forEach var="img" items="${controls}" varStatus="controlLoop">
	            			<c:set var="controlText" value="blah WT: ${img.sex}, ${img.parameterName}"/>
	            			<div class="
	            			<c:choose>
	            				<c:when test="${img.sex eq 'male' }">
	            					clickbox_male"
	            				</c:when>
	            				<c:when test="${img.sex eq 'female' }">
	            					clickbox_female"
	            				</c:when>
	            				<c:otherwise>
	            					clickbox_no_sex
	            				</c:otherwise>
	            			</c:choose>
	            			">
	            				<c:choose>
									<c:when test="${mediaType eq 'pdf' }">
										<img id="${img.omeroId}" src="${pdfThumbnailUrl}" style="width:${thumbnailSize}px" class="clickable_image_control <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" title="${controlText}">
									</c:when>
									<c:otherwise>
	            						<img id="${img.omeroId}" src="${jpegUrlThumbWithoutId}/${img.omeroId}/" data-imageLink="${img.imageLink}" class="clickable_image_control <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" title="${controlText}">
	            					</c:otherwise>
	            				</c:choose>
	            				</div>
	            			</c:forEach>
	            		</div>
	            	
	            	</div>
	            
	 				<c:set var="imageLink" value="${mutants[0].imageLink}"/>
	 				<c:set var="srcForMutant" value="${jpegUrlDetailWithoutId}/${mutants[0].omeroId }"/>
	 				<c:if test="${fn:containsIgnoreCase(imageLink, 'omero' ) || fn:containsIgnoreCase(imageLink, 'ndp' )}">
	 					<c:set var="srcForMutant" value="${fn:replace(imageLink, 'http:','https:') }"/>
	 				</c:if>
	 				
	            	<div id="mutant_box" class="box half_box_right">
	            	<c:choose>
			            	<c:when test="${not empty mutants}">
			            		<c:choose>
			            			
			            			<c:when test="${mediaType eq 'pdf' }">		            			
			            				<iframe id="mutant_frame"
											src="//docs.google.com/gview?url=${pdfWithoutId}/${mutants[0].omeroId}&embedded=true"></iframe>
			            			</c:when>
			            			<c:otherwise>
			      						
			      								<iframe id="mutant_frame"
												src="${srcForMutant}"></iframe>
			      							
				            			
				            		</c:otherwise>
			            			
			            			
			            			</c:choose>
								
							</c:when>
							<c:otherwise>
								No Image for Mutants Selected
							</c:otherwise>
					</c:choose>
	            		<div id="mutant_annotation" class="annotation">
	            		Mutant:<t:formatAllele>${mutants[0].alleleSymbol}</t:formatAllele>, ${mutants[0].zygosity}, ${mutants[0].sex}, ${mutants[0].parameterName }
	            		</div>
	            		<div class="picker">
	            			<c:forEach var="img" items="${mutants}" varStatus="mutantLoop">
	            			<c:set var="mutantText" value="Mutant: ${img.alleleSymbol}, ${img.zygosity}, ${img.sex}, ${img.parameterName}"/>
	            			<div class="
	            			<c:choose>
	            				<c:when test="${img.sex eq 'male' }">
	            					clickbox_male"
	            				</c:when>
	            				<c:when test="${img.sex eq 'female' }">
	            					clickbox_female"
	            				</c:when>
	            				<c:otherwise>
	            					clickbox_no_sex
	            				</c:otherwise>
	            			</c:choose>
	            			">
								<c:choose>
									<c:when test="${mediaType eq 'pdf' }">
										<img id="${img.omeroId}" src="${pdfThumbnailUrl}" style="width:${thumbnailSize}px" class="clickable_image_mutant <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${mutantText}">
									</c:when>
									
									<c:otherwise>
	            						<img id="${img.omeroId}" src="${jpegUrlThumbWithoutId}/${img.omeroId}/" data-imageLink="${img.imageLink}" class="clickable_image_mutant <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${mutantText}">
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
	var googlePdf="//docs.google.com/gview?url=replace&embedded=true";
	</script>

</jsp:body>
</t:genericpage>
