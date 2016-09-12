<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Search</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/search?">Search Overview</a> &raquo;</jsp:attribute>
    <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>
    <jsp:attribute name="header">

		<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" />
        <style>
            table.out {
                border-spacing: 5px;
            }
            table.out tr {
                /*background-color: #EEEEEE;*/
                background-color: #F7F7F7;
            }
            td.tdout {
                padding: 0;
                margin: 0;
                width: 50%;
                /*border-radius: 5px !important;*/
            }
            table.in {
                padding: 0 0 0 20px;
            }

            td.in {
                width: 150px;
            }
            td.in2 {
                padding: 5px 10px;
                color: #A8A8A8;
            }
            td.dtypeBdr {
                background-color: rgb(248, 183, 97);
                text-align: center;
                padding: 10px 20px;
                border-radius: 5px;
                border: none;
                width: 120px
            }
            span.dtype {
                font-weight: bold;
                font-size: 20px;
            }
            span.dtype a {
                color: #184A89;
            }
            li {
                list-style-type: square;
                margin-left: 20px;
            }​
            li#liImg {
                display: block;
                height: 100px;
                line-height: 100px;
            }
            li#liImg img, li#liImg span {
                vertical-align: middle;
            }
            div#overview {
                margin-top: -10px;
            }
        </style>
	</jsp:attribute>

    <jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

    <jsp:body>

        <c:set var="subMatch" value="subMatch"/>
        <div id="overview">
            Search result overview with best hit
            <table class="out">
                <tr>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/search/gene?${params}">Genes<br><span class="coreCount">(${coreCount.gene})</span></a><p><p><img src="${baseUrl}/img/dna.png"/></span></td>
                            <td class="in2">
                                <c:if test="${not empty coreData.gene}">
                                    <a href="${baseUrl}/genes/${coreData.gene.mgi_accession_id}">${coreData.gene.marker_symbol}</a><br>
                                    <c:if test="${not empty coreData.gene.marker_name}">
                                        <li><span class="label">name</span>: ${coreData.gene.marker_name}</li>
                                    </c:if>
                                    <c:if test="${not empty coreData.gene.human_gene_symbol}">
                                        <li><span class="label">human ortholog</span>: ${coreData.gene.human_gene_symbol[0]}</li>
                                    </c:if>
                                    <c:if test="${not empty coreData.gene.marker_synonym}">
                                        <li><span class="label">synonym</span>: ${coreData.gene.marker_synonym[0]}</li>
                                    </c:if>
                                </c:if>
                            </td>
                        </table>
                    </td>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/search/mp?${params}">Phenotypes<br><span class="coreCount">(${coreCount.mp})</span></a><p><p><img src="${baseUrl}/img/stats.png" /></span></td>
                            <td class="in2">
                                <c:if test="${not empty coreData.mp}">
                                    <a href="${baseUrl}/phenotypes/${coreData.mp.mp_id}">${coreData.mp.mp_term}</a><br>

                                    <c:if test="${not empty coreData.mp.mp_definition}">
                                        <li><span class="label">definition</span>: ${coreData.mp.mp_definition}</li>
                                    </c:if>
                                    <c:if test="${not empty coreData.mp.mp_term_synonym[0] && coreData.mp.mp_term_synonym[0].contains(subMatch)}">
                                        <li><span class="label">synonym</span>: ${coreData.mp.mp_term_synonym[0]}</li>
                                    </c:if>

                                    <c:if test="${(not empty coreData.mp.mp_term_synonym[0]
                                        && ! coreData.mp.mp_term_synonym[0].contains(subMatch))
                                        || empty coreData.mp.mp_term_synonym[0]}">
                                        <c:if test="${not empty coreData.mp.mp_narrow_synonym[0] && coreData.mp.mp_narrow_synonym[0].contains(subMatch)}">
                                            <li><span class="label">synonym</span>: ${coreData.mp.mp_narrow_synonym[0]}</li>
                                        </c:if>
                                    </c:if>
                                </c:if>
                            </td>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/search/disease?${params}">Disease<br><span class="coreCount">(${coreCount.disease})</span></a><p><p><img src="${baseUrl}/img/disease.png"/></span></td>
                            <td class="in2">
                                <c:if test="${not empty coreData.disease}">
                                    <a href="${baseUrl}/disease/${coreData.disease.disease_id}">${coreData.disease.disease_term}</a>
                                    <c:if test="${not empty coreData.disease.disease_source}">
                                        <li><span class="label">source</span>: ${coreData.disease.disease_source}</li>
                                    </c:if>
                                </c:if>
                            </td>
                        </table>
                    </td>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/search/anatomy?${params}">Anatomy<br><span class="coreCount">(${coreCount.anatomy})</span></a><p><p><img src="${baseUrl}/img/anatomy.png"/></span></td>
                            <td class="in2">
                                <c:if test="${not empty coreData.anatomy}">
                                    <a href="${baseUrl}/anatomy/${coreData.anatomy.anatomy_id}">${coreData.anatomy.anatomy_term}</a>
                                    <c:if test="${not empty coreData.anatomy.stage}">
                                        <li><span class="label">stage</span>: ${coreData.anatomy.stage}</li>
                                    </c:if>
                                </c:if>
                            </td>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class="tdout">
                        <table class="in"><td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/search/impc_images?${params}">Images<br><span class="coreCount">(${coreCount.impc_images})</span></a><p><p><img src="${baseUrl}/img/image.png"/></span></td>
                            <td class="in2">
                                <c:if test="${not empty coreData.impc_images}">
                                    <c:if test="${not empty coreData.impc_images.procedure_name}">
                                        <li><span class="label">procedure name</span>: ${coreData.impc_images.procedure_name}</li>
                                    </c:if>
                                    <c:if test="${not empty coreData.impc_images.gene_symbol}">
                                        <li><span class="label">gene</span>: <a href="${baseUrl}/genes/${coreData.impc_images.gene_accession_id}">${coreData.impc_images.gene_symbol}</a>
                                            <%--<c:if test="${not empty coreData.impc_images.marker_synonym} && ${fn:containsIgnoreCase(coreData.impc_images.marker_synonym[0], \"subMatch\")}">--%>

                                            <c:if test="${not empty coreData.impc_images.marker_synonym[0] && coreData.impc_images.marker_synonym[0].contains(subMatch)}">
                                                (${coreData.impc_images.marker_synonym[0]})
                                            </c:if>
                                        </li>
                                    </c:if>
                                    <c:if test="${not empty coreData.impc_images.anatomy_id}">
                                        <li><span class="label">anatomy</span>: <a href="${baseUrl}/anatomy/${coreData.impc_images.anatomy_id[0]}">${coreData.impc_images.anatomy_term[0]}</a>
                                            <c:if test="${not empty coreData.impc_images.selected_top_level_anatomy_term[0]
                                                && coreData.impc_images.selected_top_level_anatomy_term[0].contains(subMatch)}">
                                                (${coreData.impc_images.selected_top_level_anatomy_term[0]})
                                            </c:if>
                                            <c:if test="${(not empty coreData.impc_images.selected_top_level_anatomy_term[0]
                                                && ! coreData.impc_images.selected_top_level_anatomy_term[0].contains(subMatch))
                                                || empty coreData.impc_images.selected_top_level_anatomy_term[0]}">
                                                <c:if test="${not empty coreData.impc_images.intermediate_anatomy_term[0]
                                                    && coreData.impc_images.intermediate_anatomy_term[0].contains(subMatch)}">
                                                    (${coreData.impc_images.intermediate_anatomy_term[0]})
                                                </c:if>
                                            </c:if>
                                        </li>
                                    </c:if>
                                    <c:if test="${not empty coreData.impc_images.jpeg_url}">
                                        <li id="liImg"><span class="label">image</span>: <a href="${coreData.impc_images.download_url}"><img src="${coreData.impc_images.jpeg_url}"/></a></li>
                                    </c:if>
                                </c:if>
                            </td>

                        </table>

                    </td>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/search/allele2?${params}">Product<br><span class="coreCount">(${coreCount.allele2})</span></a><p><p><img src="${baseUrl}/img/mouse.png"/></span></td>
                            <td class="in2">
                                <c:if test="${not empty coreData.allele2}">
                                    <c:if test="${not empty coreData.allele2.allele_name}">
                                        <li><span class="label">allele</span>: ${coreData.allele2.marker_symbol}${coreData.allele2.allele_name}</li>
                                    </c:if>
                                    <c:if test="${not empty coreData.allele2.mutation_type}">
                                        <li><span class="label">mutation type</span>: ${coreData.allele2.mutation_type}</li>
                                    </c:if>
                                </c:if>
                            </td>
                        </table>
                    </td>
                </tr>
            </table>
        </div>


        <compress:html enabled="${param.enabled != 'false'}" compressJavaScript="true">
            <script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchFacets.js?v=${version}'></script>
        </compress:html>

        <script>
            $(document).ready(function() {
                'use strict';

                console.log("${coreData.mp.mp_definition}");
                $('span.coreCount').each(function(){
                    var count = $(this).text().replace(/\(|\)/g, "");
                    if ( count == 0 ){
                        $(this).parent().css("cursor", "not-allowed");
                        $(this).parent().click(function(e){
                            return false;
                        });
                    }
                });


                // show more/less for mp definition
                $('div.moreLess').click(function(){
                    if ( $(this).hasClass('expand') ){
                        $(this).removeClass('expand');
                        $(this).siblings('div.fullDef').hide();
                        $(this).siblings('div.partDef').show();
                        $(this).text("Show more ...");

                    }
                    else {
                        $(this).addClass('expand');
                        $(this).siblings('div.fullDef').show();
                        $(this).siblings('div.partDef').hide();
                        $(this).text("Show less ...");
                    }
                });

            });



        </script>



    </jsp:body>

</t:genericpage>


