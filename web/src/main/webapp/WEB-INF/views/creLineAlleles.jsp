<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Software/Web Release Notes</jsp:attribute>

	 <jsp:attribute name="breadcrumb"></jsp:attribute>
	

	<jsp:attribute name="header">

		<%-- <link rel="stylesheet" href="${baseUrl}/css/parallelCoordinates/style.css" type="text/css" /> --%>

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
                <li><a href="#top">Crelines</a></li>


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
			        <h1 class="title" id="top">Cre alleles from CREATE (coordination of resources for conditional expression of mutated mouse alleles)</h1>
                    <div class="section">
                        <div class="inner">
							<p>
							Mutant mouse ES cell lines are produced, each of which carries an altered or "floxed" allele of a single gene. These mutant ES cell mutations can be readily transformed into mice using blastocyst injection, and the mutation activated by crossing the mouse bearing the floxed allele with a Cre recombinase driver strain to induce the mutation in spatially and temporally determined patterns.
							</p>
							<p>
							Intricate conditional and inducible gene manipulation approaches have led to the generation of cell lineage- or developmental stage-specific alterations under temporal control. The vast number of Cre recombinase-expressing mouse lines - often referred to as "Cre-Zoo" animals - has greatly contributed to these accomplishments by allowing Cre recombinase to be expressed in specific cell types, in some cases in an inducible manner.
							</p>
							<p>
							The full power of conditional mutant ES cell libraries and mice can therefore only be exploited with the availability of well characterised mouse lines expressing Cre-recombinase in tissue, organ and cell type-specific patterns, to allow the creation of somatic mutations in defined genes.
							</p>
							<!-- <p>
							Although several privately curated and locally held databases currently provide a limited catalog of existing Cre driver strains, common problems are: 
							<uL>
							<li>
							These are not well integrated and often outdated, so that much of the field works by "word of mouth" to locate the necessary reagents for generating their conditional mutations.
							</li>
							<li>
							Published data on Cre driver mice from disparate groups do not always capture critical details such as the efficiency of recombination, cell and tissue specificity, or genetic background effects.
							</li>
							</uL>
							</p>
							<p>
							CREATE addresses these combined shortcomings - inaccessibility to existing Cre driver strains, their incomplete characterisation, and an inadequate coverage of cell and tissue types in which they are active by enlisting major mouse stakeholders dedicated to collecting, integrating and curating, expanding and disseminating the currently scattered Cre driver mouse databases through a unified portal to provide the necessary structure for worldwide access to these critical resources.
							</p>
								 -->
								<br/>
								<br/>
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
    
	/* $(document).ready(function() {
	    $('#creLineTable').DataTable();
	} ); */
	
	initCreLineTable();
	
	function initCreLineTable(){
		var aDataTblCols = [0,1,2,3,4,5,6];
		//	var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
	    $('#creLineTable').dataTable( {
			"aoColumns": [{ "sType": "string"},
			              { "sType": "string"},
			              { "sType": "html"},
			              { "sType": "string"},
			              { "sType": "string"},
			              { "sType": "string"}
			              ],
			"bDestroy": true,
			"bFilter":false,
			"ordering": false,
			"searching":true,
			"bPaginate":true,
	        "sPaginationType": "bootstrap"
		});
    }

    
});

</script>

</jsp:body>

</t:genericpage>


