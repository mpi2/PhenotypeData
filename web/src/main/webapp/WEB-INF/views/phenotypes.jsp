<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">${phenotype.getMpId()} (${phenotype.getMpTerm()}) | IMPC Phenotype Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search/mp?kw=*">Phenotypes</a> &raquo; ${phenotype.getMpTerm()}</jsp:attribute>

	<jsp:attribute name="header">

	<!-- CSS Local Imports -->
        <link rel="stylesheet" href="${baseUrl}/css/treeStyle.css">

		<script type="text/javascript">
			var phenotypeId = '${phenotype.getMpId()}';
			var drupalBaseUrl = '${drupalBaseUrl}';
		</script>

		<script type='text/javascript' src="${baseUrl}/js/general/dropDownPhenPage.js?v=${version}"></script>
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.v3.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>
		
		
		
		<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.2/css/bootstrap-select.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.2/js/bootstrap-select.min.js"></script>

	</jsp:attribute>


	<jsp:attribute name="bodyTag">
	
	</jsp:attribute>

	<jsp:attribute name="addToFooter">

		<script type="text/javascript">
			// Stuff dor parent-child. Will be used in parentChildTree.js.
			console.log('getting phenotype infor for parentChildTree.js');
			var ont_id = '${phenotype.getMpId()}';
			var ontPrefix = "mp";
			var page = "phenotypes";
			var hasChildren = ${hasChildren};
			var hasParents = ${hasParents};
		</script>
    	<script type="text/javascript" src="${baseUrl}/js/parentChildTree.js"></script>

		<div class="region region-pinned">
	        <div id="flyingnavi" class="block smoothScroll">

				<a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>

		        <ul>
		 	        <li><a href="#top">Phenotype</a></li>
		            <c:if test="${genePercentage.getDisplay()}">
		                		<li><a href="#data-summary">Phenotype Association Stats</a></li>
		            </c:if>
		            <c:if test="${hasData}">
		                <li><a href="#gene-variants">Gene Variants</a></li><!-- message comes up in this section so dont' check here -->
		            </c:if>
		            <c:if test="${not empty images && fn:length(images) !=0}">
		                <li><a href="#imagesSection">Images</a></li>
		            </c:if>
		        </ul>

		        <div class="clear"></div>

	        </div>

	</div>

	</jsp:attribute>
	<jsp:body>
	
	<div class="container data-heading">
                <div class="row">
                    <div class="col-12 no-gutters">
                        <h2>Phenotype: ${phenotype.getMpTerm()}</h2>
                    </div>
                </div>
    </div>

			<c:if test="${hasData}">
           <div class="container single single--no-side">
           
                <div class="row">
                    <div class="col-12 white-bg">
                    <div class="col-12 white-bg">
                    <br/>
                        <h3>IMPC Gene variants with ${phenotype.getMpTerm()}</h3>
                        <jsp:include page="phenotypes_gene_variant_frag.jsp"></jsp:include>
           			</div>
           			</div>
           		</div>
           </div>
           
           </c:if>
	  
            
            <c:if test="${genePercentage.getDisplay()}">
            
             <div class="container">
                <div class="row">
                    <div class="col-12 no-gutters">
                        <h3>Phenotype associations stats</h3>
                          </div>
                    </div>
              </div>
              
              <div class="container single single--no-side">
            	<div class="row">
                	<div class="col-12 white-bg">
                    	<div class="page-content pt-5 pb-5">
                        	<jsp:include page="phenotypes_ass_stats_frag.jsp"/>
                        </div>
                     </div>  		
                  </div>
              </div>
           </c:if>
      
           
           <div class="container">
                <div class="row">
                    <div class="col-12 no-gutters">
                    <h3>Phenotype Summary Information</h3>
                        </div>
                </div>
            </div> 
            <div class="container single single--no-side">
            	<div class="row">
                	<div class="col-12 white-bg">
                    	<div class="page-content pt-5 pb-5">
                        	<jsp:include page="phenotypes_summary_frag.jsp"/>     		
                     	</div>
                 	</div>
            	</div>
            </div> 

				
 <script type="text/javascript">
        $('document').ready(function(){

            var whatIsRelatedSyn = "Related synonyms are mostly terms of the Human Phenotype Ontology that are mapped to an mammalian phenotype (MP) term. Occasionally, they may be children of the current MP term.";

            // what is related synonym
            /* $('i.relatedSyn').qtip({
                content: {
                    text: whatIsRelatedSyn
                },
                style: {
                    classes: 'qtipimpc'
                }
            }); */


            // show more/less for related synonyms
            $('span.synToggle').click(function(){
               var partList = $(this).siblings('ul').find('li.defaultList');
               var fullList = $(this).siblings('ul').find('li.fullList');

               if ($(this).siblings('ul').find('li.defaultList').is(':visible')){
                   partList.hide();
                   fullList.show();
                   $(this).text('Show less');
               }
               else {
                   partList.show();
                   fullList.hide();
                   $(this).text('Show more');
               }
            });
        });
        
        //really this method below could be changed to use the same method as above??? JW
        $( "#show_other_procedures" ).click(function() {
    		$( "#other_procedures" ).toggle( "slow", function() {
    		// Animation complete.
    		 });
    		
    		var text = $('#procedureToogleLink').text();
    		$('#procedureToogleLink').text(
                    text == "Show more" ? "Show less" : "Show more");
    	});
    </script>
			
</jsp:body>

</t:genericpage>


