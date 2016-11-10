<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Software/Web Release Notes</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Release Notes</jsp:attribute>

	<jsp:attribute name="header">

		<script type="text/javascript">
			var drupalBaseUrl = '${drupalBaseUrl}';
		</script>

        <script type="text/javascript">
		    $(document).ready(function() {

                // bubble popup for brief panel documentation
                $.fn.qTip({
                    'pageName': 'phenome',
                    'tip': 'top right',
                    'corner': 'right top'
                });
            });
        </script>



	</jsp:attribute>

	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

	<jsp:attribute name="addToFooter">
			<div class="region region-pinned">

        <div id="flyingnavi" class="block smoothScroll ">

            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>

            <ul>
                <li><a href="#top">Current Software/Web Release Notes</a></li>
                
            </ul>

            <div class="clear"></div>

        </div>

    </div>

	</jsp:attribute>
	<jsp:body>

	<div class="region region-content">
		<div class="block block-system">
			<div class="content">
				<div class="node node-gene">
			        <h1 class="title" id="top">Software/Web Release Notes</h1>
                    <div class="section">
                        <div class="inner">
                            
								<div class="with-label"><span class="label">Website Updates:</span>
                                    <ul>
                                        <li>Version:&nbsp;v2.2.0</li>
                                        <li>Genbank files for products now available on the products search results tab
                                        <li>New image overlap view available from the image comparison pages to make it easy to compare for example the size of bones on XRay images</li>
                                        <li>Images associated with an MP term are now displayed on the MP pages</li>
                                        
                                        <li>New federated comparison viewer page: brings images from the JAX ndpi server and omero server seemlessly into the side by side image viewer</li>
                                    </ul>
                                </div>
                                <div class="with-label"><span class="label">RESTful Services Updates</span>
                                    <ul>
                                       
                                        <li>Version:&nbspv2.2.0</li>
                                    </ul>
                                </div>
                           

                       

                            <div class="clear"></div>
                        </div>
                    </div>

                   
                    <!-- end of section -->

		</div>
	</div>
</div>
</div>

</jsp:body>

</t:genericpage>


