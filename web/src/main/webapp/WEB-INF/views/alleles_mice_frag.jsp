<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:if test="${not empty mice}">


    <h3 id="mice_block">Mice</h3>

    <table class="table table-striped" id="mouse_table">
        <thead>
        <tr>
            <th>Colony Name</th>
            <th>Genetic Background</th>
            <th>Production Centre</th>
            <th>QC Data</th>
            <th>ES Cell/Parent Mouse Colony</th>
            <th>Order / Contact</th>
        </tr>
        </thead>
        <tbody class="products">

        <c:forEach var="mouse" items="${mice}" varStatus="micex">

            <tr>

                <td>${mouse['colony_name']}</td>
                <td>${mouse['genetic_background']}</td>
                <td>${mouse['production_centre']}</td>
                <td>


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

                <td>
                        ${mouse['associated_product_es_cell_name']}
                    <a href="${baseUrl}/alleles/${acc}/${mouse['associated_colony_allele_name']}<c:if test="${bare == true}">?bare=true</c:if>">${mouse['associated_product_colony_name']}</a>
                        ${mouse['associated_product_vector_name']}

                </td>

                <td>

                    <c:if test="${not empty mouse['orders']}">

                        <c:forEach var="order" items="${mouse['orders']}" varStatus="ordersx">
                            <a class="btn" href="${order['url']}"> <i class="fa fa-shopping-cart"></i> ${order['name']}
                            </a>
                        </c:forEach>

                    </c:if>

                    <c:if test="${empty mouse['orders'] and not empty mouse['contacts']}">

                        <c:forEach var="contact" items="${mouse['contacts']}" varStatus="contactsx">
                            <a class="btn" href="${contact['url']}"> <i class="fa  fa-envelope"></i> ${contact['name']}
                            </a>
                        </c:forEach>

                    </c:if>

                </td>
            </tr>

        </c:forEach>

        </tbody>
    </table>

</c:if>

