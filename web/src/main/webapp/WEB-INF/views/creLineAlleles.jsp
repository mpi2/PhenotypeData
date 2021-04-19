<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC/Crelines</jsp:attribute>

    <jsp:attribute name="breadcrumb"></jsp:attribute>


    <jsp:attribute name="header">


	</jsp:attribute>

    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="addToFooter">
		<script type="text/javascript">

			var orderContent = {};
			function detailFormatter(index, row) {
				if (!row._data['shown']) {
					$.ajax({
						url: row._data['link'],
						type: 'GET',
						success: function (data) {
							$('#orderAllele' + index).html(data);
							row._data['shown'] = true;
							orderContent[index] = data;
						}
					});
					return "<div class='container'>" +
							'<div id="orderAllele' + index + '" class="col-12">' +
							"     <div class=\"pre-content\">\n" +
							"                        <div class=\"row no-gutters\">\n" +
							"                            <div class=\"col-12 my-5\">\n" +
							"                                <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
							"                            </div>\n" +
							"                        </div>\n" +
							"                    </div>" +
							'</div>' +
							'</div>';
				} else {
					return "<div class='container'>" +
							'<div id="orderAllele' + index + '" class="col-12">' +
							orderContent[index] +
							'</div>' +
							'</div>';
				}

			}


			$(document).ready(function () {

            	// Init the creline data table
				$('#creLineTable').dataTable({
					"aoColumns": [{"sType": "string"},
						{"sType": "string"},
						{"sType": "html"},
						{"sType": "string"},
						{"sType": "string"},
						{"sType": "string"}
					],
					"bDestroy": true,
					"bFilter": false,
					"ordering": false,
					"searching": true,
					"bPaginate": true
				});



			});
        </script>
	</jsp:attribute>

    <jsp:body>

        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Cre alleles from CREATE (coordination of resources for conditional expression of
                        mutated mouse alleles)</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">

                                <%-- Project description --%>
                            <div class="row no-gutters mb-2 mb-sm-0">
                                <p>
                                    The EUCOMMTOOLS project created a new inducible Cre Driver mouse line resource that
                                    is available to
                                    researchers via <a
                                        href="https://www.infrafrontier.eu/search?keyword=EUCOMMToolsCre">INFRAFRONTIER
                                    repository</a>.
                                    This resource contains over 220 Cre Driver mouse lines where tamoxifin inducible Cre
                                    expression vectors
                                    have replaced the coding elements of genes with restricted tissue expression.
                                    Individual gene drivers
                                    were selected by community surveys and gene expression studies with the resulting
                                    mouse lines being
                                    characterised to confirm restricted Cre expression (<a
                                        href="http://www.imib.es/AnotadorWeb">http://www.imib.es/AnotadorWeb</a>).
                                    All EUCOMMTOOL Cre Driver mouse lines are generated on a pure C57BL/6N genetic
                                    background making this a
                                    unique resource for IMPC researchers.
                                </p>

                                <p>
                                    Conditional knockout models have greatly contributed to studies into gene function
                                    and disease processess by allowing the inactivation of gene function in specific
                                    tissues and/or developmental timepoints in mice. This is generally achieved by
                                    flanking a critical gene exon with loxP sites (e.g. a "floxed" allele) in such a
                                    manner that does not interfere with gene transcription. In the presence of the Cre
                                    recombinase, recombination occurs between the loxP sites resulting in the removal of
                                    the critical gene segment and inactivating the gene.
                                </p>
                                <p>
                                    The full potential of conditional knockout models mice is only realized with the
                                    availability of well characterised mouse lines expressing Cre-recombinase in tissue,
                                    organ and cell type-specific patterns. This is best achieved by substituting a Cre
                                    recombinase expression vector for the coding exons of genes that have the desired
                                    spatial and temporal restricted expression. The promoter and other genomic elements
                                    that control expression of the native gene will "drive" expression of the Cre
                                    recombinase instead. These "Cre Driver" mice can then be bred with mice carrying a
                                    floxed allele to generate the desired conditional knockout. Variants of Cre Driver
                                    mice allow temporal control of Cre activity by employing fusion proteins of the Cre
                                    enzyme with modified versions of the ligand binding domains of the estrogen receptor
                                    that are responsive to the synthetic ligand tamoxifen.
                                </p>
                            </div>
                        </div>
                        <div class="page-contact py-5 white-bg">
                            <c:if test="${orderRows.size() > 0}">
                                <c:if test="${creLine}">
                                    <c:set var="creLineParam" value="&creLine=true"/>
                                </c:if>
                                <h4>This service may be affected by the Covid-19 pandemic. <a
                                        href="https://www.mousephenotype.org/news/impc-covid-19-update/">See how</a>
                                </h4>
                                <table id="creLineTable" data-toggle="table" data-pagination="true"
                                       data-mobile-responsive="true" data-sortable="true" data-detail-view="true"
                                       data-detail-formatter="detailFormatter">
                                    <thead>
                                    <tr>
                                        <th>MGI Allele</th>
                                        <th>Allele Type</th>
                                        <th>Produced</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="row" items="${orderRows}" varStatus="status">
                                        <tr data-link="${baseUrl}/allelesFrag/${row.mgiAccessionId}/${row.encodedAlleleName}?${creLineParam}"
                                            data-shown="false">
                                            <td>
                                                <span class="text-dark"
                                                      style="font-size: larger; font-weight: bolder;">${row.markerSymbol}<sup>${row.alleleName}</sup></span>
                                            </td>
                                            <td>
                                                    ${row.alleleDescription}
                                            </td>
                                            <td>
                                                <c:if test="${row.mouseAvailable}">
													<span>
														Mice<c:if
                                                            test="${row.targetingVectorAvailable or row.esCellAvailable or row.tissuesAvailable}">,</c:if>
													</span>
                                                </c:if>
                                                <c:if test="${row.targetingVectorAvailable}">
                                                    <span>Targeting vectors<c:if
                                                            test="${row.esCellAvailable or row.tissuesAvailable}">,</c:if></span>
                                                </c:if>
                                                <c:if test="${row.esCellAvailable}">
                                                    <span>ES Cells<c:if test="${row.tissuesAvailable}">,</c:if></span>
                                                </c:if>
                                                <c:if test="${row.tissuesAvailable}">
                                                    <span>Tissue</span>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>

                            <c:choose>
                                <c:when test="${creLineAvailable}">
                                    <div><a href="${baseUrl}/order/creline?acc=${acc}" target="_blank">Cre
                                        Knockin ${alleleProductsCre2.get("product_type")} are available for this
                                        gene.</a></div>
                                </c:when>
                            </c:choose>


                        </div>
                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>


