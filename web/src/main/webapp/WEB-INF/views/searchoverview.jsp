<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Search</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/searchoverview?">Search Overview</a> &raquo;</jsp:attribute>
    <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>
    <jsp:attribute name="header">

		<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" />
        <style>
            table.out {
                border-spacing: 5px;
            }
            table.out tr {
                background-color: #EEEEEE;
            }
            td.tdout {
                padding: 0;
                margin: 0;
                width: 50%;
            }
            table.in {
                padding: 0 0 0 20px;
            }

            td.in {
                width: 150px;
            }
            td.in2 {
                padding: 5px 10px;
            }
            td.dtypeBdr {
                background-color: rgb(248, 183, 97);
                text-align: center;
                padding: 10px 20px;
                border-radius: 5px;
                border: none;
                width: 120px
            }
            td {
                border-radius: 10px;
            }
            span.dtype {
                font-weight: bold;
                font-size: 20px;
            }
            li {
                list-style-type: square;
                margin-left: 20px;
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

     <%--${data}--%>
        <%--<div style="clear: both"></div>--%>
        <div id="overview">

            <table class="out">
                <tr>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/searchoverview/gene?${params}">Genes<br><span class="coreCount">(${coreCount.gene})</span></a><p><p><img src="${baseUrl}/img/dna.png"/></span></td>
                            <td class="in2"><a href="${baseUrl}/genes/${coreData.gene.mgi_accession_id}">${coreData.gene.marker_symbol}</a><br>
                                <c:if test="${not empty coreData.gene.name}">
                                    name: ${coreData.gene.marker_name}<br>
                                </c:if>
                                <c:if test="${not empty coreData.gene.human_gene_symbol}">
                                    <li>human ortholog: ${coreData.gene.human_gene_symbol[0]}</li>
                                </c:if>
                                <c:if test="${not empty coreData.gene.marker_synonym}">
                                    <li>synonym: ${coreData.gene.marker_synonym[0]}</li>
                                </c:if>
                            </td>
                        </table>
                    </td>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/searchoverview/mp?${params}">Phenotypes<br><span class="coreCount">(${coreCount.mp})</span></a><p><p><img src="${baseUrl}/img/stats.png" /></span></td>
                            <td class="in2"><a href="${baseUrl}/phenotypes/${coreData.mp.mp_id}">${coreData.mp.mp_term}</a><br>
                                <c:if test="${not empty coreData.mp.mp_definition}">
                                    <li>definition: ${coreData.mp.mp_definition}</li>
                                </c:if>
                                <c:if test="${not empty coreData.mp.mp_synonym}">
                                    <li>synonym: ${coreData.mp.mp_synonym[0]}</li>
                                </c:if>
                            </td>
                        </table>
                    </td>

                </tr>
                <tr>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/searchoverview/disease?${params}">Disease<br><span class="coreCount">(${coreCount.disease})</span></a><p><p><img src="${baseUrl}/img/disease.png"/></span></td>
                            <td class="in2"><a href="${baseUrl}/disease/${coreData.disease.disease_id}">${coreData.disease.disease_term}</a><br>
                                <c:if test="${not empty coreData.desease.disease_source}">
                                    <li>Source: ${coreData.desease.disease_source}</li>
                                </c:if>
                            </td>
                        </table>

                    </td>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/searchoverview/anatomy?${params}">Anatomy<br><span class="coreCount">(${coreCount.anatomy})</span></a><p><p><img src="${baseUrl}/img/anatomy.png"/></span></td>
                            <td class="in2"><a href="${baseUrl}/anatomy/${coreData.anatomy.anatomy_id}">${coreData.anatomy.anatomy_term}</a>
                                <c:if test="${not empty coreData.anatomy.stage}">
                                    <li>stage: ${coreData.anatomy.stage}</li>
                                </c:if>
                            </td>
                        </table>
                    </td>


                </tr>
                <tr>
                    <td class="tdout">
                        <table class="in"><td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/searchoverview/impc_images?${params}">Images<br><span class="coreCount">(${coreCount.impc_images})</span></a><p><p><img src="${baseUrl}/img/image.png"/></span></td>
                            <td class="in2"><c:if test="${not empty coreData.impc_images.procedure_name}">
                                <li>procedure name: ${coreData.impc_images.procedure_name}</li>
                            </c:if>
                                <c:if test="${not empty coreData.impc_images.gene_symbol}">
                                    <li>gene: <a href="${baseUrl}/genes/${coreData.impc_images.gene_accession_id}">${coreData.impc_images.gene_symbol}</a></li>
                                </c:if>
                                <c:if test="${not empty coreData.impc_images.anatomy_id}">
                                    <li>anatomy: <a href="${baseUrl}/anatomy/${coreData.impc_images.anatomy_id[0]}">${coreData.impc_images.anatomy_term[0]}</a</li>
                                </c:if>
                                <c:if test="${not empty coreData.impc_images.jpeg_url}">
                                    <li>image: <a href="${coreData.impc_images.download_url}"><img src="{coreData.impc_images.jpeg_url}"/></a></li>
                                </c:if>
                            </td>

                        </table>

                    </td>
                    <td class="tdout">
                        <table class="in">
                            <td class="in dtypeBdr"><span class="dtype"><a href="${baseUrl}/searchoverview/allele2?${params}">Product<br><span class="coreCount">(${coreCount.allele2})</span></a><p><p><img src="${baseUrl}/img/mouse.png"/></span></td>
                            <td class="in2">
                                <c:if test="${not empty coreData.allele2.allele_name}">
                                    <li>allele: ${coreData.allele2.marker_symbol}${coreData.allele2.allele_name}</li>
                                </c:if>
                                <c:if test="${not empty coreData.allele2.mutation_type}">
                                    <li>mutation type: ${coreData.allele2.mutation_type}</li>
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
                $('span.coreCount').each(function(){
                    var count = $(this).text().replace(/\(|\)/g, "");
                    if ( count == 0 ){
                        $(this).parent().css("cursor", "not-allowed");
                    }
                });

            });

        </script>



    </jsp:body>

</t:genericpage>


