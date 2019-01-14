<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>

<t:genericpage>

    <jsp:attribute name="title">${gene.markerSymbol} - ${gene.markerName}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
    <jsp:attribute name="bodyTag">
        <body class="gene-node no-sidebars small-header">

        </jsp:attribute>
        <jsp:attribute name="addToFooter">
            <%--  floating menu, displays quick links to sections of long page --%>
            <div class="region region-pinned">
                <div id="flyingnavi" class="block smoothScroll">
                    <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
                        <%-- Menu list always displays a fixed number of headings --%> 
                    <ul>                                                
                        <li><a href="#top">Gene</a></li>
                        <li><a href="#section-associations">Phenotype Associations</a></li>						
                        <li><a href="#section-expression">Expression</a></li>
                        <li><a href="#section-images">Associated Images</a></li>
                        <li><a href="#section-disease-models">Disease Models</a></li>
                        <li><a href="#order2">Order Mouse and ES Cells</a></li>
                    </ul>
                    <div class="clear"></div>
                </div>
            </div>
        </jsp:attribute>


        <jsp:attribute name="header">

            <script src="${baseUrl}/js/general/enu.js"></script>
            <script src="${baseUrl}/js/general/dropdownfilters.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/general/allele.js"></script>            
            <%-- Phenogrid requirements --%>
            <script type="text/javascript" src="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.js?v=${version}"></script>
            <link rel="stylesheet" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.css?v=${version}">

            <%-- Phenodigm2 requirements --%>                                
            <script src="//d3js.org/d3.v4.min.js"></script>
            <script type="text/javascript">var impc = {baseUrl: "${baseUrl}"}</script>        
            <script type="text/javascript" src="${baseUrl}/js/vendor/underscore/underscore-1.8.3.min.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/phenodigm2/phenodigm2.js?v=${version}"></script>       
            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/phenodigm2.css"/>                                
            <%-- End of phenodigm2 requirements --%>

            <script type="text/javascript">
                var gene_id = '${acc}';

                $(document).ready(function () {
                    var heatmap_generated = 0;
                    var expressionTab = 0;
                    var hash = location.hash;
                    if (hash.indexOf("tabs-") > -1) {
                        expressionTab = $('a[href="' + hash + '"]').parent().index();
                        $("#section-expression").focus();
                    }

                    $("#exptabs").tabs({active: expressionTab});
                    $("#phenotabs").tabs({active: 0});
                    $("#phenotabs2").tabs({active: 0});
                    $("#tabs").tabs();

                    $('div#anatomo1').hide(); // by default

                    $('.wtExp').hide();
                    $('div#toggleWt').click(function () {
                        if ($('.wtExp').is(':visible')) {
                            $('.wtExp').hide();
                            $(this).text("Show Wildtype Expression");
                        } else {
                            $('.wtExp').show();
                            $(this).text("Hide Wildtype Expression");
                        }
                    });

                    $('div#expDataView').click(function () {
                        if ($('#anatomo1').is(':visible')) {
                            $('#anatomo1').hide();
                            $('#anatomo2').show();
                            $(this).text("Show expression table");
                        } else {
                            $('#anatomo1').show();
                            $('#anatomo2').hide();
                            $(this).text("Hide expression table");
                        }
                    });

                    $('#heatmap_link').click(function () {
                        console.log('heatmap link clicked');

                        /* //load the css
                         var cssId = 'myCss';  // you could encode the css path itself to generate id..
                         if (!document.getElementById(cssId))
                         {
                         var head  = document.getElementsByTagName('head')[0];
                         var link  = document.createElement('link');
                         link.id   = cssId;
                         link.rel  = 'stylesheet';
                         link.type = 'text/css';
                         link.href = '${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css';
                         link.media = 'all';
                         head.appendChild(link);
                         } */

                        if ($('#heatmap_toggle_div').length) {//check if this div exists first as this will ony exist if phenotypeStarted and we don't want to do this if not.
                            $('#heatmap_toggle_div').toggleClass('hidden');//toggle the div whether the heatmap has been generated or not.
                            $('#phenotypeTableDiv').toggleClass('hidden');
                            if (!heatmap_generated) {

                                var script = document.createElement('script');
                                script.src = "${drupalBaseUrl}/heatmap/js/heatmap.1.3.1.js";
                                script.onload = function () {

                                    //do stuff with the script
                                    new dcc.PhenoHeatMap({
                                        /* identifier of <div> node that will host the heatmap */
                                        'container': 'phenodcc-heatmap',
                                        /* colony identifier (MGI identifier) */
                                        'mgiid': '${gene.mgiAccessionId}',
                                        /* default usage mode: ontological or procedural */
                                        'mode': 'ontological',
                                        /* number of phenotype columns to use per section */
                                        'ncol': 5,
                                        /* heatmap title to use */
                                        'title': '${gene.markerSymbol}',
                                        'url': {
                                            /* the base URL of the heatmap javascript source */
                                            'jssrc': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/js/',
                                            /* the base URL of the heatmap data source */
                                            'json': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/rest/',
                                            /* function that generates target URL for data visualisation */
                                            'viz': dcc.heatmapUrlGenerator
                                        }
                                    });
                                    heatmap_generated = 1;

                                };


                                document.head.appendChild(script);



                            }//end of if heatmap generated

                        }



                    });

                    // registerInterest();

                });


                function registerInterest() {

                    $('a.regInterest').click(function () {

                        var anchorControl = $(this);
                        var iconControl = $(anchorControl).find('i');
                        var endpoint = $(anchorControl).attr('href');

                        var currentAnchorText = $(anchorControl).text().trim();

                        function riSuccess() {

                            if (currentAnchorText.toUpperCase() === 'Unregister Interest'.toUpperCase()) {

                                $(iconControl).removeClass('fa-sign-out');
                                $(iconControl).addClass('fa-sign-in');

                                endpoint = endpoint.replace('unregistration', 'registration');
                                $(anchorControl).attr('href', endpoint);

                                $(anchorControl).html('Register interest');

                            } else {

                                // Register -> Unregister
                                $(iconControl).removeClass('fa-sign-in');
                                $(iconControl).addClass('fa-sign-out');

                                endpoint = endpoint.replace('registration', 'unregistration');
                                $(anchorControl).attr('href', endpoint);

                                $(anchorControl).html('Unregister interest');
                            }
                        }

                        function riError() {
                            window.alert('Unable to access register interest service at this time.');
                        }

                        if (endpoint.includes('login?target')) {
                            $.ajax({
                                url: endpoint,
                                dataType: 'jsonp',
                                beforeSend: setHeader,
                                error: riError
                            });
                        } else {
                            $.ajax({
                                url: endpoint,
                                dataType: undefined,
                                beforeSend: undefined,
                                success: riSuccess,
                                error: riError
                            });
                        }

                        return false;
                    });
                }


            </script>

            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/genes.css"/>				

            <c:if test="${phenotypeStarted}">
                <!--[if !IE]><!-->
                <link rel="stylesheet" type="text/css"
                      href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css"/>
                <!--<![endif]-->
                <!--[if IE 8]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmapIE8.1.3.1.css">
                <![endif]-->
                <!--[if gte IE 9]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css">
                <![endif]-->
            </c:if>

        </jsp:attribute>

        <jsp:body>
            <div class="container data-heading">
                <div class="row">
                    <div class="col-12 no-gutters">
                        <h2>Gene: ${gene.markerSymbol}</h2>
                    </div>
                </div>
            </div>

            <div class="container single single--no-side">
                <div class="row">
                    <div class="col-12 white-bg">
                        <div class="page-content pt-5 pb-5">
                            <jsp:include page="genesGene_frag.jsp"/>
                        </div>
                    </div>
                </div>
            </div>

            <div class="container">
                <div class="row">
                    <div class="col-12 no-gutters">
                        <h3>Phenotype associations for ${gene.markerSymbol}</h3>
                    </div>
                </div>
            </div>

            <div class="container single single--no-side">
                <div class="row">
                    <div class="col-12 white-bg">
                        <div class="page-content pt-5 pb-5">
                            <jsp:include page="genesPhenotypeAssociation_frag.jsp"/>
                        </div>
                    </div>
                </div>
            </div>

        </jsp:body>

    </t:genericpage>
