<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC/Crelines</jsp:attribute>

	 <jsp:attribute name="breadcrumb"></jsp:attribute>
	

	<jsp:attribute name="header">

		<%-- <link rel="stylesheet" href="${baseUrl}/css/parallelCoordinates/style.css" type="text/css" /> --%>

		<script type="text/javascript">
			var drupalBaseUrl = '${drupalBaseUrl}';
		</script>
		<style>
			.hide {
			  display: none;
			}
		</style>

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
							
							The EUCOMMTOOLS project created a new inducible Cre Driver mouse line resource that is available to researchers via <a href="https://www.infrafrontier.eu/search?keyword=EUCOMMToolsCre">INFRAFRONTIER repository</a>.
							 This resource contains over 220 Cre Driver mouse lines where tamoxifin inducible Cre expression vectors have replaced the coding elements of genes with restricted tissue expression. Individual gene drivers were selected by community surveys and gene expression studies with the resulting mouse lines being characterised to confirm restricted Cre expression (<a href="http://www.imib.es/AnotadorWeb">http://www.imib.es/AnotadorWeb</a>).
							  All EUCOMMTOOL Cre Driver mouse lines are generated on a pure C57BL/6N genetic background making this a unique resource for IMPC researchers.<a class="read-more-show hide" href="#"> Read more....</a>
							
							
							
							<div class="read-more-content">
								<br/>
								<p>
								Conditional knockout models have greatly contributed to studies into gene function and disease processess by allowing the inactivation of gene function in specific tissues and/or developmental timepoints in mice. This is generally achieved by flanking a critical gene exon with loxP sites (e.g. a "floxed" allele) in such a manner that does not interfere with gene transcription. In the presence of the Cre recombinase, recombination occurs between the loxP sites resulting in the removal of the critical gene segment and inactivating the gene.
								</p>
								<p>
								The full potential of conditional knockout models mice is only realized with the availability of well characterised mouse lines expressing Cre-recombinase in tissue, organ and cell type-specific patterns. This is best achieved by substituting a Cre recombinase expression vector for the coding exons of genes that have the desired spatial and temporal restricted expression. The promoter and other genomic elements that control expression of the native gene will "drive" expression of the Cre recombinase instead.   These "Cre Driver" mice can then be bred with mice carrying a floxed allele to generate the desired conditional knockout. Variants of Cre Driver mice allow temporal control of Cre activity by employing fusion proteins of the Cre enzyme with modified versions of the ligand binding domains of the estrogen receptor that are responsive to the synthetic ligand tamoxifen. 
								</p>
								
								<a class="read-more-hide hide" href="#">...Read less</a>
							</div>
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

		
		
		
		
	// Hide the extra content initially, using JS so that if JS is disabled, no problemo:
	$('.read-more-content').addClass('hide')
	$('.read-more-show, .read-more-hide').removeClass('hide')

	// Set up the toggle effect:
	$('.read-more-show').on('click', function(e) {
	  $(this).next('.read-more-content').removeClass('hide');
	  $(this).addClass('hide');
	  e.preventDefault();
	});

	// Changes contributed by @diego-rzg
	$('.read-more-hide').on('click', function(e) {
	  var p = $(this).parent('.read-more-content');
	  p.addClass('hide');
	  p.prev('.read-more-show').removeClass('hide'); // Hide only the preceding "Read More"
	  e.preventDefault();
	});
    
});

</script>

</jsp:body>

</t:genericpage>


