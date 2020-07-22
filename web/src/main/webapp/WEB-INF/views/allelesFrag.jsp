<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="alleleId" value="${fn:replace(fn:replace(allele_name, '%29', ''), '%28', '')}"/>
<c:set var="miceActive" value="${not empty mice ? 'active' : 'disabled'}"/>
<c:set var="cellActive" value="${not empty es_cells? (empty mice ? 'active' : ''): 'disabled'}"/>
<c:set var="vectorActive" value="${not empty targeting_vectors? (empty mice and empty es_cells ? 'active' : ''): 'disabled'}"/>
<c:set var="tissueActive" value="${not empty tissue_enquiry_links? (empty mice and empty es_cells and empty targeting_vectors ? 'active' : ''): 'disabled'}"/>
<div class="container" style="font-size: medium;">
    <div class="row">
        <div class="col-12">
            <c:if test="${not empty summary['map_image']}">
                <div id="image">
                    <img alt="image not found!" src="${summary['map_image']}" width="80%"
                         style="margin-top: 0px; margin-bottom: 20px;">
                </div>
            </c:if>
        </div>
    </div>

    <div class="row">
        <div class="col-12 justify-content-center">
            <ul class="nav nav-tabs" id="order-${alleleId}-Tab" role="tablist" style="margin-top: 0px">
                <li class="nav-item">
                    <a class="nav-link ${miceActive}" id="order-${alleleId}-mice-tab" data-toggle="tab"
                       href="#order-${alleleId}-mice" role="tab" aria-controls="order${alleleId}-mice"
                       aria-selected="${miceActive == 'active'}">Mice (${mice.size()})</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${cellActive}" id="order-${alleleId}-cells-tab" data-toggle="tab"
                       href="#order-${alleleId}-cells" role="tab" aria-controls="order${alleleId}-cells"
                       aria-selected="${cellActive == 'active'}">ES Cells (${es_cells.size()})</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${vectorActive}" id="order-${alleleId}-vectors-tab" data-toggle="tab"
                       href="#order-${alleleId}-vectors" role="tab" aria-controls="order${alleleId}-vectors"
                       aria-selected="${vectorActive == 'active'}">Targeting Vectors (${targeting_vectors.size()})</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${tissueActive}" id="order-${alleleId}-tissues-tab" data-toggle="tab"
                       href="#order-${alleleId}-tissues" role="tab" aria-controls="order${alleleId}-tissues"
                       aria-selected="${tissueActive == 'active'}">Tissues (${tissue_enquiry_links.size()})</a>
                </li>
            </ul>
            <div class="tab-content" id="order-${alleleId}TabContent">
                <div class="tab-pane fade ${miceActive == 'active'? 'show active': ''}" id="order-${alleleId}-mice" role="tabpanel"
                     aria-labelledby="order-${alleleId}-mice-tab">
                    <div class="container justify-content-center pt-3">
                        <table id="mouse_table" class="small">
                            <thead>
                            <tr>
                                <th style="text-align: center;">Colony Name</th>
                                <th style="text-align: center;">Genetic Background</th>
                                <th style="text-align: center;">Production Centre</th>
                                <th style="text-align: center;">QC Data</th>
                                <th style="text-align: center;">ES Cell/Parent Mouse Colony</th>
                                <th>Order / Contact</th>
                            </tr>
                            </thead>
                            <tbody>

                            <c:forEach var="mouse" items="${mice}" varStatus="micex">
                                <tr>
                                    <td style="text-align: center;">${mouse['colony_name']}</td>
                                    <td style="text-align: center;">${mouse['genetic_background']}</td>
                                    <td style="text-align: center;">${mouse['production_centre']}</td>
                                    <td style="text-align: center;">


                                        <c:choose>
                                            <c:when test='${not empty mouse["qc_data_url"]}'>
                                                <a class="hasTooltip"
                                                   href="${baseUrl}/${mouse['qc_data_url']}<c:if test="${bare == true}">?bare=true</c:if>">QC
                                                    data</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="hasTooltip" href="#">QC data</a>
                                            </c:otherwise>
                                        </c:choose>


                                        <div class="hidden">
                                            <div class="qcData" data-type="mouse" data-name="${mouse['colony_name']}"
                                                 data-alleletype="${mouse['allele_type']}"></div>
                                        </div>
                                        <c:if test="${not empty mouse['qc_about']}">
                                            <a target="_blank" href="${mouse['qc_about']}">(&nbsp;about&nbsp;)</a>
                                        </c:if>
                                    </td>

                                    <td style="text-align: center;">
                                            ${mouse['associated_product_es_cell_name']}
                                        <a href="${baseUrl}/alleles/${acc}/${mouse['associated_colony_allele_name']}<c:if test="${bare == true}">?bare=true</c:if>">${mouse['associated_product_colony_name']}</a>
                                            ${mouse['associated_product_vector_name']}

                                    </td>

                                    <td>

                                        <c:if test="${not empty mouse['orders']}">

                                            <c:forEach var="order" items="${mouse['orders']}" varStatus="ordersx">
                                                <a class="btn btn-outline-primary" href="${order['url']}"  target="_blank"> <i
                                                        class="fa fa-shopping-cart"></i>&nbsp;Order
                                                    from ${order['name']}</a>
                                            </c:forEach>

                                        </c:if>

                                        <c:if test="${empty mouse['orders'] and not empty mouse['contacts']}">

                                            <c:forEach var="contact" items="${mouse['contacts']}" varStatus="contactsx">
                                                <a class="btn btn-outline-primary" href="${contact['url']}"  target="_blank"> <i
                                                        class="fa  fa-envelope"></i>&nbsp;Contact ${contact['name']}</a>
                                            </c:forEach>

                                        </c:if>

                                    </td>


                                </tr>

                            </c:forEach>

                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="tab-pane fade ${cellActive == 'active'? 'show active': ''}" id="order-${alleleId}-cells" role="tabpanel"
                     aria-labelledby="order-${alleleId}-cells-tab">
                    <div class="container justify-content-center pt-3">
                        <table id="es_cell_table" class="small">
                            <thead>
                            <tr>
                                <th style="text-align: center;">ES Cell Clone</th>
                                <th style="text-align: center;">ES Cell strain</th>
                                <th style="text-align: center;">Parental Cell Line</th>
                                <th style="text-align: center;">IKMC Project</th>
                                <th style="text-align: center;">QC Data</th>
                                <th style="text-align: center;">Targeting Vector</th>
                                <th>Order</th>
                            </tr>
                            </thead>

                            <tbody>

                            <c:forEach var="es_cell" items="${es_cells}" varStatus="es_cellsx">
                                <tr>

                                    <td style="text-align: center;">${es_cell['es_cell_clone']}</td>
                                    <td style="text-align: center;">${es_cell['es_cell_strain']}</td>
                                    <td style="text-align: center;">${es_cell['parental_cell_line']}</td>
                                    <td style="text-align: center;">${es_cell['ikmc_project_id']}</td>

                                    <td>

                                        <c:choose>
                                            <c:when test='${not empty es_cell["qc_data_url"]}'>
                                                <a class="hasTooltip"
                                                   href="${baseUrl}/${es_cell['qc_data_url']}<c:if test="${bare == true}">?bare=true</c:if>">QC
                                                    data</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="hasTooltip" href="#">QC data</a>
                                            </c:otherwise>
                                        </c:choose>


                                        <div class="hidden">
                                            <div class="qcData" data-type="es_cell"
                                                 data-name="${es_cell['es_cell_clone']}"
                                                 data-alleletype="${es_cell['allele_type']}"></div>
                                        </div>
                                        <c:if test="${not empty es_cell['qc_about']}">
                                            <a target="_blank" href="${es_cell['qc_about']}">(&nbsp;about&nbsp;)</a>
                                        </c:if>

                                    </td>
                                    <td style="text-align: center;">${es_cell['associated_product_vector_name']}</td>

                                    <td>
                                        <c:forEach var="order" items="${es_cell['orders']}" varStatus="ordersx">
                                            <a class="btn btn-outline-primary" href="${order['url']}"  target="_blank"> <i
                                                    class="fa fa-shopping-cart"></i>&nbsp; Order from ${order['name']}</a>&nbsp;
                                        </c:forEach>
                                    </td>


                                </tr>

                            </c:forEach>

                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="tab-pane fade ${vectorActive == 'active'? 'show active': ''}" id="order-${alleleId}-vectors" role="tabpanel"
                     aria-labelledby="order-${alleleId}-vectors-tab">
                    <div class="container justify-content-center pt-3">
                        <div class="row">
                            <div class="col-12">
                                <table id="targeting_vector_table" class="small">
                                    <thead>
                                    <tr>

                                        <th style="text-align: center;">Targeting Vector</th>
                                        <th style="text-align: center;">Cassette</th>
                                        <th style="text-align: center;">Backbone</th>
                                        <th style="text-align: center;">IKMC Project</th>
                                        <th style="text-align: center;">Order</th>
                                        <th style="text-align: center;">Genbank File</th>
                                        <th style="text-align: center;">Vector Map</th>
                                        <%--<th style="text-align: center;">Design Oligos</th>--%>

                                    </tr>
                                    </thead>

                                    <tbody>

                                    <c:forEach var="targeting_vector" items="${targeting_vectors}" varStatus="targeting_vectorsx">
                                        <tr>



                                            <td style="text-align: center;">${targeting_vector['targeting_vector']}</td>
                                            <td style="text-align: center;">${targeting_vector['cassette']}</td>
                                            <td style="text-align: center;">${targeting_vector['backbone']}</td>
                                            <td style="text-align: center;" >
                                                   <c:set var="ikmc_id" value="${targeting_vector['ikmc_project_id']}"></c:set>
                                              <c:if test="${ikmcDesignMapForRow[ikmc_id]}">
                                                     <a class="hasTooltip" href="${baseUrl}/designs/${targeting_vector['ikmc_project_id']}?accession=${accession}">
                                                    </c:if>

                                                    ${targeting_vector['ikmc_project_id']}
                                                        <c:if test="${ikmcDesignMapForRow[ikmc_id]}">
                                            </a>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:forEach var="order" items="${targeting_vector['orders']}" varStatus="ordersx">
                                                    <a class="btn btn-outline-primary" href="${order['url']}"> <i
                                                            class="fa fa-shopping-cart"></i>&nbsp; Order from ${order['name']}</a>
                                                </c:forEach>
                                            </td>

                                            <td style="text-align: center;">
            <span>
                <c:if test="${not empty targeting_vector['genbank_file']}">
                    <a href="${targeting_vector['genbank_file']}" target="_blank">
                        <i class="fa fa-file-text fa-lg"></i>
                    </a>
                </c:if>
            </span>
                                            </td>

                                            <td style="text-align: center;">
            <span>
                <c:if test="${not empty targeting_vector['allele_image']}">
                    <a href="${targeting_vector['allele_image']}" target="_blank">
                        <i class="fa fa-image fa-lg"></i>
                    </a>
                </c:if>
            </span>
                                            </td>

                                            <%--<td style="text-align: center;">
            <span>
                <c:if test="${not empty targeting_vector['design_oligos_url']}">
                    <a href="${targeting_vector['design_oligos_url']}" target="_blank"><i
                            class="fa fa-external-link-square fa-lg"></i></a>
                </c:if>
            </span>
                                            </td>--%>
                                        </tr>
                                    </c:forEach>

                                    </tbody>
                                </table>
                            </div>
                        </div>

                    </div>

                </div>
                <div class="tab-pane fade ${vectorActive == 'active'? 'show active': ''}" id="order-${alleleId}-tissues" role="tabpanel"
                     aria-labelledby="order-${alleleId}-tissues-tab">
                    <div class="container justify-content-center pt-3">
                        <c:forEach var="tissueLink" items="${tissue_enquiry_links}" varStatus="ordersx">
                            <div class="row m-2">
                                <a class="tissue-order btn btn-outline-primary" href="${tissueLink}" target="_blank">
                                    <i class="fa fa-envelope"></i>&nbsp;Make a ${tissue_enquiry_types.get(ordersx.index)} enquiry to ${tissue_distribution_centres.get(ordersx.index)} </a>&nbsp;
                            </div>
                        </c:forEach>
                    </div>
            </div>
        </div>

    </div>

</div>
</div>
