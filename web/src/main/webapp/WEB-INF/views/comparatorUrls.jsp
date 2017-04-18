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
		            	
			            				<iframe id="control_frame"
												src="${url1}"></iframe>
			            			
	            
	            		<div id="control_annotation" class="annotation">
	            			${param.url1}
	            		</div>
	            		<div class="picker">
	            			
	            		</div>
	            	
	            	</div>
	            
	 				
	 				
	            	<div id="mutant_box" class="box half_box_right">
	            	
			            	
			      						
			      								<iframe id="mutant_frame"
												src="${url2}"></iframe>
			      							
				            			
				            		
	            		<div id="mutant_annotation" class="annotation">
	            		${param.url2}
	            		</div>
	            		<div class="picker">
	            			
	            		</div>
	            	
	            	</div>
	            </div>
            </form>
            <!-- </div>




		
			</div>
 		</div>
	</div> -->
	
	

</jsp:body>
</t:genericpage>