<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="omeroStaticUrl" value="${fn:replace(impcMediaBaseUrl,'/omero/webgateway', '/static/')}"/>
<t:genericpage>
 <jsp:attribute name="title">${gene.markerSymbol} Image Picker</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a>&nbsp;&raquo; Image Comparator</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="chartpage no-sidebars small-header"></jsp:attribute>
<jsp:attribute name="header">

    <link href="${omeroStaticUrl}webgateway/css/ome.viewport.css" type="text/css" rel="stylesheet"></link>
    
    <link href="${baseUrl}/css/comparator/comparator.css" rel="stylesheet" type="text/css" /><!-- put after default omero css so we can override -->
   <link href="${omeroStaticUrl}3rdparty/panojs-2.0.0/panojs.css" type="text/css" rel="stylesheet"></link>

</jsp:attribute>

<jsp:attribute name="addToFooter">
	<script src="${omeroStaticUrl}/omeroweb.viewer.min.js" type="text/javascript"></script> 
	<script type='text/javascript' src="${baseUrl}/js/comparator/comparator.js?v=${version}"></script>
</jsp:attribute>
<jsp:body>
 <div class="row">
       <div class="col-12 no-gutters">
        	<div class="node"> 
	        <c:set var="jpegUrlThumbWithoutId" value="${impcMediaBaseUrl}/render_birds_eye_view"/>
	        <c:set var="jpegUrlDetailWithoutId" value="${impcMediaBaseUrl}/img_detail"/>
	        <c:set var="pdfWithoutId" value="http:${fn:replace(impcMediaBaseUrl,'webgateway','webclient/annotation')}"/>
	        <c:set var="thumbnailSize" value="96"/>
	        <%-- 
	         mediaType: ${mediaType }
        	impcMediaBaseUrl: ${impcMediaBaseUrl } 
        	omeroStaticUrl=${omeroStaticUrl}
	       	jpegUrlThumbWithoutId: ${jpegUrlThumbWithoutId}
	        jpegUrlDetailWithoutId: ${jpegUrlDetailWithoutId}
	        pdfWithoutId: ${pdfWithoutId} --%>

				&nbsp;<a href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol}</a><span class="fal fa-angle-right"></span> Image Comparator
            <form action="">
            <div id="comparator" class="section">
            	<c:if test="${mediaType !=null }">
	            	<input type="hidden" name="mediaType" value="${mediaType}">
	            </c:if>
	            <c:if test="${gene !=null }">
	            	<input type="hidden" name="acc" value="${gene.mgiAccessionId}">
	            </c:if>
	            <c:if test="${param.parameter_stable_id!=null}">
	            	<input type="hidden" name="parameter_stable_id" value="${param.parameter_stable_id}">
	            </c:if>
	            <%-- <c:if test="${param.parameter_association_value!=null}">
	            	<input type="hidden" name="parameter_association_value" value="${param.parameter_association_value}">
	            </c:if> --%>
	            <c:if test="${param.anatomy_id!=null}">
	            	<input type="hidden" name="anatomy_id" value="${param.anatomy_id}">
	            </c:if>
	             <c:if test="${param.mp_id!=null}">
	            	<input type="hidden" name="mp_id" value="${param.mp_id}">
	            </c:if>
	             <c:if test="${param.anatomy_term!=null}">
	            	<input type="hidden" name="anatomy_term" value="${param.anatomy_term}">
	            </c:if>
	            <div id="control_key" class="key-annotation">
	            		<span class="clickbox_male">&nbsp;&nbsp;&nbsp;</span>male
	            		<span class="clickbox_female">&nbsp;&nbsp;&nbsp;</span> female
	            </div>
	            <div id="filters">Filter Images by gender: 
	         
	            	<select name="gender">
	            	<option value="all" <c:if test="${param.gender eq null || 'all'}">selected</c:if> >all</option>
	            	<c:forEach var="genderType" items="${sexTypes}">
	            	<c:if test="${genderType.name ne  'both'}">	            	
	            		<option value="${genderType.name}" <c:if test="${param.gender eq genderType.name}">selected</c:if>>${genderType.name}</option>
	            	</c:if>
	            	</c:forEach>
	            	</select>
            		zygosity:
             		<select name="zygosity">
             		<option  <c:if test="${param.zygosity eq null}">selected</c:if> >all</option>
             		<c:forEach var="zyg" items="${zygTypes}">
             			<option value="${zyg.name}" <c:if test="${param.zygosity eq zyg.name}">selected</c:if>>${zyg.name}</option>
            		</c:forEach>
            		</select>
            		<%-- Organ:
            		<select name="organ">
             		<option  <c:if test="${param.organ eq null}">selected</c:if> >all</option>
             		<c:forEach var="organ" items="${organ}">
             			<option value="${organ}" <c:if test="${param.organ eq organ}">selected</c:if>>${organ}</option>
            		</c:forEach>
            		</select> --%>
            		Expression:
            		<select name="parameter_association_value"><!-- expression is stored in the param ass value -->
             		<option  <c:if test="${param.parameter_association_value eq null}">selected</c:if> >all</option>
             		<c:forEach var="expression" items="${expression}">
             			<option value="${expression.displayName}" <c:if test="${param.parameter_association_value eq expression.displayName}">selected</c:if>>${expression.displayName}</option>
            		</c:forEach>
            		</select>
            		
            		<input type="submit" value="Go"> <span class="btn" id="mutant_only_button">Display Mutant Only</span>&nbsp;&nbsp;&nbsp;<span class="btn" id="overlap">Overlap Mode</span>
            	</div>
            		<div id="control_box" class="box half_box_left">
						<h3>WT Images</h3>
            		<!-- <div class="thumbList" style="float:left"> -->
					        <div id="viewport" class="viewport"></div>
					   <!--  </div> -->
		            	
	            		<div id="control_annotation" class="annotation">
	            		
	            		</div>

	            		<div class="thumbList">
	            		<c:forEach var="img" items="${controls}" varStatus="controlLoop">
	            		
	            		<c:forEach items="${img.parameterAssociationName}" var="currentItem" varStatus="stat">
  								<c:set var="controlParamAssValues" value="${stat.first ? '' : paramAssValues} ${currentItem}:${img.parameterAssociationValue[stat.index]}" />
  								
							</c:forEach> 
	            			
	            				<c:set var="controlText" value="${img.zygosity}<br/>${img.sex}"/>

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
	            			"> <div style="color: #0c0c0c">${controlText}</div>
	            				
	            						<img  id="${img.omeroId}" class="thumb" data-id="${img.omeroId}" src="https:${jpegUrlThumbWithoutId}/${img.omeroId}/" <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" title="${img.zygosity},${img.sex}">

	            			</div> <!-- end of male female class -->
	            			</c:forEach>
	            		
					    </div>
	            	</div>

					<div id="mutant_box" class="half_box_right">
						<h3>Mutant Images</h3>
						 <!-- <div class="thumbList" style="float:left"> -->
					        <div id="viewport2" class="viewport"></div>
					    <!-- </div> -->
		            	<div id="mutant_annotation" class="annotation">
		            	</div> 

					    <div class="thumbList">
					    
					       
					        <c:forEach var="img" items="${mutants}" varStatus="mutantLoop">
					        
					        <c:forEach items="${img.parameterAssociationName}" var="currentItem" varStatus="stat">
  								<c:set var="paramAssValues" value="${stat.first ? '' : paramAssValues} ${currentItem}:${img.parameterAssociationValue[stat.index]}" />
  								
							</c:forEach> 
	            			<c:set var="mutantText" value='${img.zygosity}<br/> ${img.sex} '/>
	            			
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
	            			"><div style="color: #0c0c0c">${mutantText}</div>
								
									
	            						<img class="thumb2" id="${img.omeroId}" data-id="${img.omeroId}" src="https:${jpegUrlThumbWithoutId}/${img.omeroId}/" <%-- class="clickable_image_mutant --%> <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${img.zygosity},${img.sex}">
	 
	            			
	            			</div>
	            			</c:forEach> 
					    </div>
					 </div>
					    
				</div>
				
				</form>
				
			</div>
	</div>
</div>

    
	
	<script type='text/javascript'>
	var jpegUrlDetailWithoutId = "${jpegUrlDetailWithoutId}";
	var pdfWithoutId = "${pdfWithoutId}";
	var googlePdf="//docs.google.com/gview?url=replace&embedded=true";
	var omeroStaticUrl="${omeroStaticUrl}";
	var acc="${gene.mgiAccessionId}";
	</script>

</jsp:body>

</t:genericpage>
