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
   
    <script src="${omeroStaticUrl}3rdparty/jquery.mousewheel-3.0.6.js" type="text/javascript"></script> 
   <script src="${omeroStaticUrl}webgateway/js/ome.gs_slider.js" type="text/javascript"></script> 
   <script src="${omeroStaticUrl}webgateway/js/ome.popup.js" type="text/javascript"></script>
    <script src="${omeroStaticUrl}webgateway/js/ome.gs_utils.js" type="text/javascript"></script>
   <script src="${omeroStaticUrl}webgateway/js/ome.viewport.js" type="text/javascript"></script>
   <script src="${omeroStaticUrl}webgateway/js/ome.viewportImage.js" type="text/javascript"></script>
    
</jsp:attribute>

<jsp:attribute name="addToFooter">
	<%-- <script src="${omeroStaticUrl}/omeroweb.viewer.min.js" type="text/javascript"></script> --%>
	<script type='text/javascript' src="${baseUrl}/js/comparator/comparator.js?v=${version}"></script>
	
	 <link rel="stylesheet" href="//code.jquery.com/ui/1.12.0/themes/base/jquery-ui.css">
  

<style>

#overlap_container{
    position: relative;
    width: 800px;
    height: auto;
}
#control_box{
    width: 100%;
	height: auto;
    background: blue;
     opacity: 1.0;
    filter: alpha(opacity=100); /* For IE8 and earlier */
}
#mutant_box{
    width: 100%;
    auto: 100%;
   
}

#resizable{
    position: absolute;
    bottom: -20px;
    right: -20px;
    width: 100%;
    auto: 100%;
     /* background: red; */
    opacity: 0.5;
    filter: alpha(opacity=50); /* For IE8 and earlier */
}
</style>
	<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
  	<script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"></script>
  
	<script>
	  $( function() {
	    $( "#resizable" ).draggable({ cursor: "crosshair"});
	    var initialResizableWidth=$('#resizable').width();
	    var initialResizableHeight=$('#resizable').height();
	    $("#resizable").resizable({
	        aspectRatio: true
	    });
	    
	    $("#reset").click(function () {
			  console.log('calling reset');
			    $("#resizable").animate({
			        top: "20px",
			        left: "20px",
			        width: initialResizableWidth,
			        height : initialResizableWidth
			    });
			});
	    
	  } );
	  
	 
	</script>
</jsp:attribute>
<jsp:body>

<c:set var="jpegUrlThumbWithoutId" value="${impcMediaBaseUrl}/render_birds_eye_view"/>
		        <c:set var="jpegUrlDetailWithoutId" value="${impcMediaBaseUrl}/img_detail"/>
		        <c:set var="pdfWithoutId" value="http:${fn:replace(impcMediaBaseUrl,'webgateway','webclient/annotation')}"/>
		        <c:set var="thumbnailSize" value="96"/>
<div class="block">
    <div class="content">
        <div class="node node-gene">
        <h1 class="title" id="overlap"> Overlapping images - drag the mutant image to compare images<span class="documentation"><a href='' id='overlap' class="fa fa-question-circle pull-right"></a></span>
				</h1>
        	<div class="section" >
        		
				<div class="inner">
					<p>
						<div id="reset" class="btn">Reset</div>
					</p>
				
				
					<div id="overlap_container" class="ui-widget-content">
						<img id="control_box"  src="${impcMediaBaseUrl}/render_thumbnail/${param.id1}/800">
						<div id="resizable" style="display:inline-block; height:100%" class="ui-widget-content">
							<img id="mutant_box" src="${impcMediaBaseUrl}/render_thumbnail/${param.id2}/800">
						</div>
					
					</div>
				</div>
			</div>

		</div>
	</div>
</div>
</jsp:body>
</t:genericpage>