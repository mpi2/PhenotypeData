<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="omeroStaticUrl" value="${fn:replace(impcMediaBaseUrl,'/omero/webgateway', '/static/')}"/>
<t:genericpage>

<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol}</a>&nbsp;&raquo; Image Comparator</jsp:attribute>


 <jsp:attribute name="title">${gene.markerSymbol} Image Picker</jsp:attribute>
<jsp:attribute name="header">

  <link href="${baseUrl}/css/comparator/comparator.css" rel="stylesheet" type="text/css" />
  <!-- This min.css contains all the smaller css files below... ->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/omeroweb.viewer.min.css" type="text/css" rel="stylesheet"></link> -->

    <!-- But many of these can be removed if we limit the functionality of the viewer (E.g. no Channel sliders, color-pickers etc) -->

    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/reset.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/ome.body.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webclient/css/dusty.css" type="text/css" rel="stylesheet"></link> -->
    <link href="${omeroStaticUrl}webgateway/css/ome.viewport.css" type="text/css" rel="stylesheet"></link>
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/ome.toolbar.css" type="text/css" rel="stylesheet"></link> -->
  
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/base.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/ome.snippet_header_logo.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/ome.postit.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/ome.rangewidget.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/3rdparty/farbtastic-1.2/farbtastic.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/ome.colorbtn.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/3rdparty/JQuerySpinBtn-1.3a/JQuerySpinBtn.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/3rdparty/jquery-ui-1.10.4/themes/base/jquery-ui.all.css" type="text/css" rel="stylesheet"></link> -->
    <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/omero_image.css" type="text/css" rel="stylesheet"></link>  -->
   <link href="${omeroStaticUrl}3rdparty/panojs-2.0.0/panojs.css" type="text/css" rel="stylesheet"></link>
   <%--  <link href="${omeroStaticUrl}webgateway/css/ome.gs_slider.css" type="text/css" rel="stylesheet"></link>  --%>
   <script src="${omeroStaticUrl}webgateway/js/ome.gs_slider.js" type="text/javascript"></script> 
   <script src="${omeroStaticUrl}webgateway/js/ome.popup.js" type="text/javascript"></script>
    <script src="${omeroStaticUrl}webgateway/js/ome.gs_utils.js" type="text/javascript"></script>
   <script src="${omeroStaticUrl}webgateway/js/ome.viewport.js" type="text/javascript"></script>
   <script src="${omeroStaticUrl}webgateway/js/ome.viewportImage.js" type="text/javascript"></script>
    
</jsp:attribute>

<jsp:attribute name="addToFooter">
	<%-- <script src="${omeroStaticUrl}/omeroweb.viewer.min.js" type="text/javascript"></script> --%>
	<script type='text/javascript' src="${baseUrl}/js/comparator/comparator.js?v=${version}"></script>
</jsp:attribute>
<jsp:body>


<div class="block">
    <div class="content">
        	<div class="node"> 
	        <c:set var="jpegUrlThumbWithoutId" value="${impcMediaBaseUrl}/render_thumbnail"/>
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
            <form action="">
            <div id="comparator" class="section">
            	<c:if test="${mediaType !=null }">
	            	<input type="hidden" name="mediaType" value="${mediaType}">
	            </c:if>
	            <c:if test="${gene !=null }">
	            	<input type="hidden" name="acc" value="${gene.mgiAccessionId}">
	            </c:if>
	            anatomy_id=${param.anatomy_id}
	            	<input type="hidden" name="parameter_stable_id" value="${param.parameter_stable_id}">
	            	<input type="hidden" name="parameter_association_value" value="${param.parameter_association_value}">
	            	<input type="hidden" name="anatomy_id" value="${param.anatomy_id}">
	            
	            <div id="filters">Filter Images by gender: 
	            	${param.gender}
	            	<select name="gender">
	            	<option value="all" <c:if test="${param.gender eq null || 'all'}">selected</c:if> >all</option>
	            	<c:forEach var="genderType" items="${sexTypes}">
	            	<c:if test="${genderType.name ne  'both'}">	            	
	            		<option value="${genderType.name}" <c:if test="${param.gender eq genderType.name}">selected</c:if>>${genderType.name}</option>
	            	</c:if>
	            	</c:forEach>
	            	</select>
            		zygosity: 
            		${param.zygosity}
             		<select name="zygosity">
             		<option value="all" <c:if test="${param.zygosity eq null}">selected</c:if> >all</option>
             		<c:forEach var="zyg" items="${zygTypes}">
             			<option value="${zyg.name}" <c:if test="${param.zygosity eq zyg.name}">selected</c:if>>${zyg.name}</option>
            		</c:forEach>
            		</select>
            		<input type="submit" value="Go"> <span class="btn" id="mutant_only_button">Display Mutant Only</span>
            	</div>
            		<div id="control_box" class="box half_box_left">
            		<div class="thumbList" style="float:left">
					        <div id="viewport" class="viewport"></div>
					    </div>
		            	<%-- <c:choose>
			            	 <c:when test="${not empty controls}">
			            		<c:choose>
			            			<c:when test="${mediaType eq 'pdf' }">		            			
			            				<iframe id="control_frame"
											src="//docs.google.com/gview?url=${pdfWithoutId}/${controls[0].omero_id}&embedded=true"></iframe>
			            			</c:when>
			            			<c:otherwise>
			            				<div id="control_frame" class="viewport" 
												src="${jpegUrlDetailWithoutId}/${controls[0].omero_id }"
												>
										</div>
			            			</c:otherwise>
			            		</c:choose>
			            		
							</c:when>
							<c:otherwise>
								No Image for Controls Selected
							</c:otherwise>
						</c:choose> --%>
	            
	            		<div id="control_annotation" class="annotation">
	            		
	            		</div>
	            		<div class="thumbList">
	            		<c:forEach var="img" items="${controls}" varStatus="controlLoop">
	            			<c:set var="controlText" value="WT: ${img.sex}, ${img.parameter_name}"/>
	            				<c:set var="controlText" value="WT: ${img.zygosity}, ${img.sex}, ${img.parameter_name}"/>
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
										<img  id="${img.omero_id}" src="../${pdfThumbnailUrl}" data-id="${img.omero_id}" style="width:${thumbnailSize}px" class="thumb <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" title="${controlText}">
									</c:when>
									<c:otherwise>
	            						<img  id="${img.omero_id}" class="thumb" data-id="${img.omero_id}" src="https:${jpegUrlThumbWithoutId}/${img.omero_id}/96/" <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" title="${controlText}">
	            					</c:otherwise>
	            				</c:choose>
	            			</div> <!-- end of male female class -->
	            			</c:forEach>
	            		
	            		   
					       <!--  <img class="thumb" data-id="86973" src="http://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/86973/96/"/>
					
					        <img class="thumb" data-id="87133" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/87133/96/"/>
					
					        <img class="thumb" data-id="86976" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/86976/96/"/>
					        <img class="thumb" data-id="20850" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/20850/96/"/> -->
					    </div>
	            	</div>
	            		
	            		
	            		
	            		
	            		
	            		
	            		
	            		
					<div id="mutant_box" class="box half_box_right">
						<%-- <c:choose>
				            	<c:when test="${not empty mutants}">
				            		<c:choose>
				            			
				            			<c:when test="${mediaType eq 'pdf' }">		            			
				            			<iframe id="mutant_frame"
											src="//docs.google.com/gview?url=${pdfWithoutId}/${mutants[0].omero_id}&embedded=true"></iframe>
				            			</c:when>
				            			<c:otherwise>
				            			<iframe id="mutant_frame"
											src="${jpegUrlDetailWithoutId}/${mutants[0].omero_id }"></iframe>
				            			</c:otherwise>
				            			</c:choose>
									
								</c:when>
								<c:otherwise>
									No Image for Mutants Selected
								</c:otherwise>
						</c:choose> --%>
						 <div class="thumbList" style="float:left">
					        <div id="viewport2" class="viewport"></div>
					    </div>
		            	<div id="mutant_annotation" class="annotation">
		            	</div> 
					 
					 
					   
					
					    <!-- <div style="clear: both; height:40px"></div> -->
					    
					    
					    <div class="thumbList">
					    
					       <!--  <img class="thumb2" data-id="87044" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/87044/96/"/>
					
					        <img class="thumb2" data-id="87015" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/87015/96/"/>
					
					        <img class="thumb2" data-id="86899" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/86899/96/"/>  -->
					        
					        <c:forEach var="img" items="${mutants}" varStatus="mutantLoop">
	            			<c:set var="mutantText" value="Mutant: ${img.allele_symbol}, ${img.zygosity}, ${img.sex}, ${img.parameter_name}"/>
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
										<img id="${img.omero_id}" src="../${pdfThumbnailUrl}" style="width:${thumbnailSize}px" class="thumb2 <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${mutantText}">
									</c:when>
									<c:otherwise>
	            						<img class="thumb2" id="${img.omero_id}" data-id="${img.omero_id}" src="https:${jpegUrlThumbWithoutId}/${img.omero_id}/96/" <%-- class="clickable_image_mutant --%> <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${mutantText}">
	 
	            					</c:otherwise>
	            				</c:choose>
	            			</div>
	            			</c:forEach> 
					    </div>
					 </div>
					    
				</div>
				
				</form>
				
			</div>
	</div>
</div>

    
    
<%-- <div class="region region-content">
	<div class="block">
    	<div class="content">
        	<div class="node"> 
        	 mediaType: ${mediaType }
        	impcMediaBaseUrl: ${impcMediaBaseUrl } 
	        <c:set var="jpegUrlThumbWithoutId" value="${impcMediaBaseUrl}/render_birds_eye_view"/>
	        <c:set var="jpegUrlDetailWithoutId" value="${impcMediaBaseUrl}/img_detail"/>
	        <c:set var="pdfWithoutId" value="http:${fn:replace(impcMediaBaseUrl,'webgateway','webclient/annotation')}"/>
	        <c:set var="thumbnailSize" value="96"/>
	       jpegUrlThumbWithoutId: ${jpegUrlThumbWithoutId}
	        jpegUrlDetailWithoutId: ${jpegUrlDetailWithoutId}
	        pdfWithoutId: ${pdfWithoutId}
            <form action="">
           
	            <div id="comparator" class="section">
	            <c:if test="${mediaType !=null }">
	            <input type="hidden" name="mediaType" value="${mediaType}">
	            </c:if>
	            	<div id="filters">Filter Images by gender: 
	            	${param.gender}
	            	<select name="gender">
	            	<option value="not applicable" <c:if test="${param.gender eq 'not applicable'}">selected</c:if> >All</option>
            			<option value="male" <c:if test="${param.gender eq 'male'}">selected</c:if> >Males</option>
            			<option value="female"  <c:if test="${param.gender eq 'female'}">selected</c:if>>Females</option>
            		</select>
            		zygosity: 
            		${param.zygosity}
             		<select name="zygosity">
             		<option value="not_applicable" <c:if test="${param.zygosity eq 'not_applicable'}">selected</c:if>>All</option>
            			<option value="heterozygote" <c:if test="${param.zygosity eq 'heterozygote'}">selected</c:if>>Het</option>
            			<option value="homozygote" <c:if test="${param.zygosity eq 'homozygote'}">selected</c:if>>Hom</option>
            		</select>
            		<input type="submit" value="Go"> <span class="btn" id="mutant_only_button">Display Mutant Only</span>
            		</div>
	            	<div id="control_box" class="box half_box_left">
		            	<c:choose>
			            	<c:when test="${not empty controls}">
			            		<c:choose>
			            			<c:when test="${mediaType eq 'pdf' }">		            			
			            				<iframe id="control_frame"
											src="//docs.google.com/gview?url=${pdfWithoutId}/${controls[0].omero_id}&embedded=true"></iframe>
			            			</c:when>
			            			<c:otherwise>
			            				<div id="control_frame" class="viewport" 
												src="${jpegUrlDetailWithoutId}/${controls[0].omero_id }"
												>
										</div>
			            			</c:otherwise>
			            		</c:choose>
			            		
							</c:when>
							<c:otherwise>
								No Image for Controls Selected
							</c:otherwise>
						</c:choose>
	            
	            		<div id="control_annotation" class="annotation">
	            			WT: ${controls[0].sex}, ${controls[0].parameter_name }
	            		</div>
	            		<div class="picker">
	            			<c:forEach var="img" items="${controls}" varStatus="controlLoop">
	            			<c:set var="controlText" value="WT: ${img.sex}, ${img.parameter_name}"/>
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
										<img id="${img.omero_id}" src="../${pdfThumbnailUrl}" style="width:${thumbnailSize}px" class="clickable_image_control <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" title="${controlText}">
									</c:when>
									<c:otherwise>
	            						<img id="${img.omero_id}" src="${jpegUrlThumbWithoutId}/${img.omero_id}/" class="clickable_image_control <c:if test='${controlLoop.index eq 0}'>img_selected</c:if>" title="${controlText}">
	            					</c:otherwise>
	            				</c:choose>
	            				</div>
	            			</c:forEach>
	            		</div>
	            	
	            	</div>
	            
	 
	            	<div id="mutant_box" class="box half_box_right">
	            	<c:choose>
			            	<c:when test="${not empty mutants}">
			            		<c:choose>
			            			
			            			<c:when test="${mediaType eq 'pdf' }">		            			
			            			<iframe id="mutant_frame"
										src="//docs.google.com/gview?url=${pdfWithoutId}/${mutants[0].omero_id}&embedded=true"></iframe>
			            			</c:when>
			            			<c:otherwise>
			            			<iframe id="mutant_frame"
										src="${jpegUrlDetailWithoutId}/${mutants[0].omero_id }"></iframe>
			            			</c:otherwise>
			            			</c:choose>
								
							</c:when>
							<c:otherwise>
								No Image for Mutants Selected
							</c:otherwise>
					</c:choose>
	            		<div id="mutant_annotation" class="annotation">
	            		Mutant:<t:formatAllele>${mutants[0].allele_symbol}</t:formatAllele>, ${mutants[0].zygosity}, ${mutants[0].sex}, ${mutants[0].parameter_name }
	            		</div>
	            		<div class="picker">
	            			<c:forEach var="img" items="${mutants}" varStatus="mutantLoop">
	            			<c:set var="mutantText" value="Mutant: ${img.allele_symbol}, ${img.zygosity}, ${img.sex}, ${img.parameter_name}"/>
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
										<img id="${img.omero_id}" src="../${pdfThumbnailUrl}" style="width:${thumbnailSize}px" class="clickable_image_mutant <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${mutantText}">
									</c:when>
									<c:otherwise>
	            						<img id="${img.omero_id}" src="${jpegUrlThumbWithoutId}/${img.omero_id}/" class="clickable_image_mutant <c:if test='${mutantLoop.index eq 0}'>img_selected</c:if>" title="${mutantText}">
	            					</c:otherwise>
	            				</c:choose>
	            			</div>
	            			</c:forEach> 
	            		</div>
	            	
	            	</div>
	            </div>
            </form>
            </div>




		
			</div>
 		</div>
	</div> --%>
	
	<script type='text/javascript'>
	var jpegUrlDetailWithoutId = "${jpegUrlDetailWithoutId}";
	var pdfWithoutId = "${pdfWithoutId}";
	var googlePdf="//docs.google.com/gview?url=replace&embedded=true";
	var omeroStaticUrl="${omeroStaticUrl}";
	</script>

</jsp:body>

</t:genericpage>