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

        <!-- <script type="text/javascript">
		    $(document).ready(function() {

                // bubble popup for brief panel documentation
                $.fn.qTip({
                    'pageName': 'phenome',
                    'tip': 'top right',
                    'corner': 'right top'
                });
            });
        </script> -->



	</jsp:attribute>

	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

	<jsp:attribute name="addToFooter">
			<div class="region region-pinned">

        <div id="flyingnavi" class="block smoothScroll ">

            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>

            <ul>
                <li><a href="#top">CreLine Alleles</a></li>
                
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
			        <h1 class="title" id="top">CreLine Alleles</h1>
                    <div class="section">
                        <div class="inner">
								
								<jsp:include page="orderSectionFrag.jsp"></jsp:include>
								
							</div>
                    </div>

                   
                    <!-- end of section -->

		</div>
	</div>
</div>
</div>
<script type="text/javascript">

$(document).ready(function () {

$('.iFrameFancy').click(function()
        {
            $.fancybox.open([
                  {
                     href : $(this).attr('data-url'),
                     
                  }
                  ],
                   {
                     'maxWidth'          : 1000,
                     'maxHeight'         : 1900,
                     'fitToView'         : false,
                     'width'             : '100%',
                     'height'            : '85%',
                     'autoSize'          : true,
                     'transitionIn'      : 'none',
                     'transitionOut'     : 'none',
                     'type'              : 'iframe',
                     scrolling           : 'auto'
                  });
        }
    );
    
});

</script>

</jsp:body>

</t:genericpage>


