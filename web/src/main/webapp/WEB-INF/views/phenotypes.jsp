<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute
            name="title">${phenotype.getMpId()} (${phenotype.getMpTerm()}) | IMPC Phenotype Information</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/search/mp?kw=*">Phenotypes</a> &raquo; ${phenotype.getMpTerm()}</jsp:attribute>

    <jsp:attribute name="header">

	<!-- CSS Local Imports -->
        <link rel="stylesheet" href="${baseUrl}/css/treeStyle.css">
        <link rel="stylesheet" href="${baseUrl}/css/vendor/font-awesome/font-awesome.min.css"/>

		<script type="text/javascript">
            var phenotypeId = '${phenotype.getMpId()}';
            var cmsBaseUrl = '${cmsBaseUrl}';
        </script>

		<script type='text/javascript' src="${baseUrl}/js/general/dropDownPhenPage.js?v=${version}"></script>
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>

        <style>
            @media(max-width: 768px) {
                #phenotype_nav_icons .fal {font-size: 3em;}
                #phenotype_numbers .phenotype_number_text {font-size: 0.75em; display: inline-block;}
            }
        </style>


<script>
    function sortString(sortName, sortOrder, data) {
        console.log("In sortString function");
        console.log("sortName = " + sortName);
        console.log("sortOrder = " + sortOrder);
        console.log("data = ");
        console.log(data);
        var order = sortOrder === 'desc' ? -1 : 1;
        data.sort(function (a, b) {
            var aa = sortName === 7 ? parseFloat(a['_' + sortName + '_data']['value']) || 0.0: a['_' + sortName + '_data']['value'];
            var bb = sortName === 7 ? parseFloat(b['_' + sortName + '_data']['value']) || 0.0: b['_' + sortName + '_data']['value'];
            if (aa < bb) {
                return order * -1
            }
            if (aa > bb) {
                return order
            }
            return 0
        })
    }

    function sortPValue(a, b) {
        console.log("a: " + a);
        console.log("b: " + b);
        if (parseFloat(a) < parseFloat(b)) return 1;
        if (parseFloat(a) > parseFloat(b)) return -1;
        return 0;
    }

    function sortPValueOld(a, b, rowA, rowB) {
        console.log(rowA);
        if (parseFloat(rowA._6_data.value) < parseFloat(rowB._6_data.value)) return 1;
        if (parseFloat(rowA._6_data.value) > parseFloat(rowB._6_data.value)) return -1;
        return 0;
    }
</script>

	</jsp:attribute>


    <jsp:attribute name="bodyTag">
	
	</jsp:attribute>

    <jsp:body>

        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Phenotype: ${phenotype.getMpTerm()} <a href="${cmsBaseUrl}/help/quick-guide-to-the-website/phenotype-page/" target="_blank"><i class="fa fa-question-circle" style="float: right; color: #212529;"></i></a></h2>
                </div>
            </div>
        </div>

        <c:if test="${hasData}">
            <div class="container white-bg-small">
                <div class="row pb-5">
                    <div class="col-12 col-md-12">
                        <div  class="pre-content clear-bg">
                            <div class="page-content people py-5 white-bg">

                                <div class="row no-gutters">
                                    <div class="col-md-8 align-middle">
                                        <div class="row no-gutters">
                                            <div class="col-md-2 align-middle text-md-right pr-1">
                                                <div class="align-middle font-weight-bold pr-2">Definition</div>
                                            </div>
                                            <div class="col-md-10 align-middle">
                                                <span>${phenotype.getMpDefinition()}</span>
                                            </div>
                                        </div>
                                        <c:if test="${not empty phenotype.getMpTermSynonym()}">
                                            <div class="row no-gutters">
                                                <div class="col-md-2 align-middle text-md-right pr-1">
                                                    <div class="align-middle font-weight-bold pr-2">Synonyms</div>
                                                </div>
                                                <div class="col-md-8">
                                                    <c:if test='${phenotype.getMpTermSynonym().size() == 1}'>

                                                        <c:forEach var="synonym" items="${phenotype.getMpTermSynonym()}"
                                                                   varStatus="loop">
                                                            <span><t:formatAllele>${synonym}</t:formatAllele><c:if test="${!loop.last}">,&nbsp;</c:if></span>
                                                            <c:if test="${!loop.last}">,&nbsp;</c:if>
                                                        </c:forEach>
                                                    </c:if>

                                                    <c:if test='${phenotype.getMpTermSynonym().size() gt 1}'>
                                                        <c:set var="count" value="0" scope="page"/>

                                                        <c:forEach var="synonym" items="${phenotype.getMpTermSynonym()}"
                                                                   varStatus="loop">
                                                            <c:set var="count" value="${count + 1}" scope="page"/>
                                                            <span>${synonym}<c:if test="${!loop.last}">,&nbsp;</c:if></span>

                                                        </c:forEach>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:if>

                                        <div id="phenotype_nav_icons" class="row no-gutters justify-content-around text-center mt-3">
                                            <a href="#genesAssociations" class="col-4">
                                                <i class="fal fa-dna fa-5x mb-1 text-dark"></i>
                                                <span class="page-nav-link">Significant gene associations</span>
                                            </a>
                                            <a href="#phenotypeProcedures" class="col-4">
                                                <i class="fal fa-tasks fa-5x mb-1 text-dark" ></i>
                                                <span class="page-nav-link">The way we measure</span>
                                            </a>
                                            <a href="#phenotypeStats" class="col-4">
                                                <i class="fal fa-chart-line fa-5x mb-1 text-dark"
                                                   data-toggle="tooltip"
                                                   data-placement="top"></i>
                                                <span class="page-nav-link">Phenotype stats</span>
                                            </a>
                                        </div>
                                    </div>
                                    <div class="col-lg-4 justify-content-center text-center text-primary"
                                         style="font-size: 2.5em; font-weight: bolder; line-height: 1.0;">
                                        <div id="phenotype_numbers" class="row no-gutters mt-3">
                                            <div class="col-4">
                                                <div><span id="percentageOfGenes" class="phenotype_number_text">0</span><span id="percentageOfGenesSign" class="phenotype_number_text">%</span></div>
                                                <div style="font-size: small; font-weight: lighter;">of tested genes</div>
                                            </div>
                                            <div class="col-4">
                                                <div id="numberOfSignificantGenes" class="phenotype_number_text">0</div>
                                                <div style="font-size: small; font-weight: lighter;">significant genes</div>
                                            </div>
                                            <div class="col-4">
                                                <div id="numberOfGenes" class="phenotype_number_text">0</div>
                                                <div style="font-size: small; font-weight: lighter;">tested genes</div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row no-gutters mt-5">
                                        <h4 id="genesAssociations">IMPC Gene variants with ${phenotype.getMpTerm()}</h4>
                                        <jsp:include page="phenotypes_gene_variant_frag.jsp"></jsp:include>
                                    </div>
                                </div>
                            </div>

                        </div>
                </div>
            </div>
            </div>
        </c:if>

        <div class="container">
            <div class="row pb-2">
                <div class="col-12 col-md-12">
                    <h3 id="phenotypeProcedures"><i class="fas fa-tasks"></i>&nbsp;The way we measure</h3>
                </div>
            </div>
        </div>
        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                    <div class="page-content pt-5 pb-5">
                        <div class="container p-0 p-md-2">
                        <jsp:include page="phenotypes_summary_frag.jsp"/>
                        </div>
                    </div>
                    </div>
                </div>
            </div>
        </div>

        <c:if test="${genePercentage.getDisplay()}">

            <div class="container">
                <div class="row pb-2">
                    <div class="col-12 col-md-12">
                        <h3 id="phenotypeStats"><i class="fas fa-chart-line"></i>&nbsp;Phenotype associations stats</h3>
                    </div>
                </div>
            </div>

            <div class="container white-bg-small">
                <div class="row pb-5">
                    <div class="col-12 col-md-12">
                        <div class="pre-content clear-bg">
                        <div class="page-content pt-5 pb-5">
                            <div class="container p-0 p-md-2">
                            <jsp:include page="phenotypes_ass_stats_frag.jsp"/>
                        </div>
                        </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>


        <script type="text/javascript">
            $('document').ready(function () {

                var whatIsRelatedSyn = "Related synonyms are mostly terms of the Human Phenotype Ontology that are mapped to an mammalian phenotype (MP) term. Occasionally, they may be children of the current MP term.";
                var number = 0;
                var animatedNumbers = {
                    percentageOfGenes: ${genePercentage.getTotalPercentage()},
                    percentageOfFemales: ${genePercentage.getFemalePercentage()},
                    percentageOfMales: ${genePercentage.getMalePercentage()},
                    numberOfGenes: ${genePercentage.getTotalGenesTested()},
                    numberOfSignificantGenes: ${genePercentage.getTotalGenesAssociated()},
                    numberOfMeasumeasurements: ${parametersAssociated.size()},
                };
                var intervals = {};
                var counters = {
                    percentageOfGenes: 0,
                    percentageOfFemales: 0,
                    percentageOfMales: 0,
                    numberOfGenes: 0,
                    numberOfSignificantGenes: 0,
                    numberOfMeasumeasurements: 0,
                };

                Object.keys(animatedNumbers).forEach(key => {

                    intervals[key] = setInterval(function () {
                        if(isNaN(animatedNumbers[key])) {
                            clearInterval(intervals[key]);
                            $('#' + key).text("-");
                            $('#' + key + "Sign").text("");
                        } else {
                            $('#' + key).text(Math.floor(counters[key]));
                            if (counters[key] >= animatedNumbers[key]) {
                                clearInterval(intervals[key]);
                                $('#' + key).text(animatedNumbers[key]);
                            }
                            counters[key] = counters[key] + (animatedNumbers[key] / 250);
                        }
                    }, 1);

            });

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
                $('span.synToggle').click(function () {
                    var partList = $(this).siblings('ul').find('li.defaultList');
                    var fullList = $(this).siblings('ul').find('li.fullList');

                    if ($(this).siblings('ul').find('li.defaultList').is(':visible')) {
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
            $("#show_other_procedures").click(function () {
                $("#other_procedures").toggle("slow", function () {
                    // Animation complete.
                });

                var text = $('#procedureToogleLink').text();
                $('#procedureToogleLink').text(
                    text == "Show more" ? "Show less" : "Show more");
            });
        </script>

    </jsp:body>

</t:genericpage>


