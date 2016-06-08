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
 <%-- <link href="${baseUrl}/css/comparator/comparator.css" rel="stylesheet" type="text/css" /> --%>
<!-- <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script> -->
<script type='text/javascript' src="${baseUrl}/js/comparator/comparator.js?v=${version}"></script>
 
 <!-- <link href="https://wwwdev.ebi.ac.uk/mi/media/static/omeroweb.viewer.min.css" type="text/css" rel="stylesheet"></link>  -->
    <script src="https://wwwdev.ebi.ac.uk/mi/media/static/omeroweb.viewer.min.js" type="text/javascript"></script>
     <link href="${baseUrl}/css/comparator/ome.viewport.css" rel="stylesheet" type="text/css" />
    <link href="https://wwwdev.ebi.ac.uk/mi/media/static/3rdparty/panojs-2.0.0/panojs.css" type="text/css" rel="stylesheet"></link>
     <link href="https://wwwdev.ebi.ac.uk/mi/media/static/webgateway/css/ome.gs_slider.css" type="text/css" rel="stylesheet"></link>
    <script>
    $(function(){
        /* Prepare the left viewport */
        var viewport = $.WeblitzViewport($("#viewport"), "https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/", {
            'mediaroot': "https://wwwdev.ebi.ac.uk/mi/media/static/"
        });
        /* Load the selected image into the viewport */
        viewport.load(87269);


        // Alternative for testing non-big image viewer
        // viewport = $.WeblitzViewport($("#viewport"), "https://learning.openmicroscopy.org/dundee/webgateway/", {
        //         'mediaroot': "https://learning.openmicroscopy.org/dundee/static/"
        //     });
        // viewport.load(1296);


        /* Prepare right viewport */
        var viewport2 = $.WeblitzViewport($("#viewport2"), "https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/", {
            'mediaroot': "https://wwwdev.ebi.ac.uk/mi/media/static/"
        });
        /* Load the selected image into the viewport */
        viewport2.load(87043);

        $(".thumb").click(function(){
            var iid = $(this).attr('data-id');
            iid = parseInt(iid);
            viewport.load(iid);
        });

        $(".thumb2").click(function(){
            var iid = $(this).attr('data-id');
            iid = parseInt(iid);
            viewport2.load(iid);
        });
    });
    </script>
    
    <style type="text/css">
    .viewport {
        height: 400px;
        width: 500px;
    }
    .thumbList {
        width: 500px;
        float: left;
        margin: 20px;
    }
    </style>
    
</jsp:attribute>
<jsp:body>

<div class="thumbList" style="float:left">
        <div id="viewport" class="viewport"></div>
    </div>
    <div class="thumbList" style="float:left">
        <div id="viewport2" class="viewport"></div>
    </div>

    <div style="clear: both; height:40px"></div>
    
    <div class="thumbList">
        <img class="thumb" data-id="86973" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/86973/96/"/>

        <img class="thumb" data-id="87133" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/87133/96/"/>

        <img class="thumb" data-id="86976" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/86976/96/"/>
    </div>
    <div class="thumbList">
        <img class="thumb2" data-id="87044" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/87044/96/"/>

        <img class="thumb2" data-id="87015" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/87015/96/"/>

        <img class="thumb2" data-id="86899" src="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/86899/96/"/>
    </div>
</jsp:body>
</t:genericpage>