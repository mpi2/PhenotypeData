<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Search</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/search/searchoverview?">Search Overview</a> &raquo;</jsp:attribute>
    <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

    <jsp:attribute name="header">
		<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" />
        <style>
            .lb {
                float: left;
            }
            .rb {
                float: right;
            }

        </style>
	</jsp:attribute>

    <jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

    <jsp:body>

     <%--${data}--%>

        <div id="'overview">

            <div class="oData ovL" id="geneOv">

                <div class="lb">Gene<br>${coreCount.gene}</div>
                <div class="rb"><a href="${baseUrl}/genes/${coreData.gene.mgi_accession_id}">${coreData.gene.marker_symbol}</a><br>
                    name: ${coreData.gene.marker_name}<br>
                    <c:if test="${not empty coreData.gene.human_gene_symbol}">
                        human ortholog: ${coreData.gene.human_gene_symbol[0]}<br>
                    </c:if>
                    <c:if test="${not empty coreData.gene.marker_synonym}">
                        synonym: ${coreData.gene.marker_synonym[0]}
                    </c:if>
                    <div style="clear: both"></div>
                </div>
            </div>
            <div class="oData ovR" id="mpOv">
                <div class="lb">Phenotype<br>${coreCount.mp}</div>
                <div class="rb">${coreData.mp}</div>
            </div>
            <div class="oData ovL" id="diseaseOv">
                <div class="lb">Disease<br>${coreCount.disease}</div>
                <div class="rb"><${coreData.disease}/div>
            </div>
            <div class="oData ovR" id="anatomyOv">
                <div class="lb">Anatomy<br>${coreCount.anatomy}</div>
                <div class="rb">${coreData.anatomy}</div>
            </div>
            <div class="oData ovL" id="impc_imagesOv">
                <div class="lb">Images<br>${coreCount.impc_images}</div>
                <div class="rb">${coreData.impc_images}</div>
            </div>
            <div class="oData ovR" id="productOv">
                <div class="lb">Product<br>${coreCount.allele2}</div>
                <div class="rb">${coreData.allele2}</div>
            </div>

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


