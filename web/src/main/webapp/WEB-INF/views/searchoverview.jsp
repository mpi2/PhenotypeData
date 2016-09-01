<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Search</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/search/searchoverview?">Search Overview</a> &raquo;</jsp:attribute>
    <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>
    <jsp:attribute name="header">
		<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" />
        <style>
            table {
                border-spacing: 8px;
            }
            td {
                border-bottom: 1px solid grey;
            }
            td:nth-child(1) {
                width: 150px;
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
        </style>
	</jsp:attribute>

    <jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

    <jsp:body>

     <%--${data}--%>
        <%--<div style="clear: both"></div>--%>
        <div id="overview">

            <table>
                <tr>
                    <td class="dtypeBdr"><span class="dtype"><a href="${baseUrl}/search/gene">Genes</span><br>(${coreCount.gene})<p><p><p><img src="http://www.mousephenotype.org/sites/dev.mousephenotype.org/files/homepageicons/gene.png"/></a></td>
                    <td><a href="${baseUrl}/genes/${coreData.gene.mgi_accession_id}">${coreData.gene.marker_symbol}</a><br>
                        name: ${coreData.gene.marker_name}<br>
                        <c:if test="${not empty coreData.gene.human_gene_symbol}">
                            human ortholog: ${coreData.gene.human_gene_symbol[0]}<br>
                        </c:if>
                        <c:if test="${not empty coreData.gene.marker_synonym}">
                            synonym: ${coreData.gene.marker_synonym[0]}
                        </c:if>
                    </td>
                    <td class="dtypeBdr"><span class="dtype"><a href="${baseUrl}/search/mp">Phenotypes</span><br>(${coreCount.mp})<p><p><p><img src="http://www.mousephenotype.org/sites/dev.mousephenotype.org/files/homepageicons/graph.png"/></a></td>
                    <td><a href="${baseUrl}/genes/${coreData.mp.mp_id}">${coreData.mp.mp_term}</a><br>
                        <c:if test="${not empty coreData.mp.mp_definition}">
                            definition: ${coreData.mp.mp_definition}<br>
                        </c:if>
                        <c:if test="${not empty coreData.mp.mp_synonym}">
                            synonym: ${coreData.mp.mp_synonym[0]}<br>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <td class="dtypeBdr"><span class="dtype"><a href="${baseUrl}/search/disease">Disease</span><br>(${coreCount.disease})<p><p><p><img src="http://www.mousephenotype.org/sites/dev.mousephenotype.org/files/homepageicons/people.png"/></a></td>
                    <td><a href="${baseUrl}/disease/${coreData.disease.disease_id}">${coreData.disease.disease_term}</a><br>
                        <c:if test="${not empty coreData.desease.disease_source}">
                            Source: ${coreData.desease.disease_source}<br>
                        </c:if>
                    </td>
                    <td class="dtypeBdr"><span class="dtype">Anatomy</span><br>(${coreCount.anatomy})</td>
                    <td><a href="${baseUrl}/anatomy/${coreData.anatomy.anatomy_id}">${coreData.anatomy.anatomy_term}</a><br>
                        stage: ${coreData.anatomy.stage}
                    </td>
                </tr>
                <tr>
                    <td class="dtypeBdr"><span class="dtype">Images</span><br>(${coreCount.impc_images})</td>
                    <td><c:if test="${not empty coreData.impc_images.procedure_name}">
                            procedure name: ${coreData.impc_images.procedure_name}<br>
                        </c:if>
                        <c:if test="${not empty coreData.impc_images.gene_symbol}">
                            gene: <a href="${baseUrl}/genes/${coreData.impc_images.gene_accession_id}">${coreData.impc_images.gene_symbol}</a><br>
                        </c:if>
                        <c:if test="${not empty coreData.impc_images.anatomy_id}">
                            anatomy: <a href="${baseUrl}/genes/${coreData.impc_images.anatomy_id[0]}">${coreData.impc_images.anatomy_term[0]}</a><br>
                        </c:if>
                        <c:if test="${not empty coreData.impc_images.jpeg_url}">
                            image: <a href="${coreData.impc_images.download_url}"><img src="{coreData.impc_images.jpeg_url}"/></a><br>
                        </c:if>
                    </td>
                    <td class="dtypeBdr"><span class="dtype">Product</span><br>(${coreCount.allele2})</td>
                    <td>allele: ${coreData.allele2.marker_symbol}${coreData.allele2.allele_name}<br>
                        mutation type: ${coreData.allele2.mutation_type}
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
            });

        </script>

    </jsp:body>

</t:genericpage>


