<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:if test="${not empty es_cells}">

    <h3 id="es_cell_block">ES Cells</h3>

    <table class="table table-striped" id="es_cell_table">
        <thead>
        <tr>
            <th>ES Cell Clone</th>
            <th>ES Cell strain</th>
            <th>Parental Cell Line</th>
            <th>IKMC Project</th>
            <th>QC Data</th>
            <th>Targeting Vector</th>
            <th>Order</th>
        </tr>
        </thead>

        <tbody>

        <c:forEach var="es_cell" items="${es_cells}" varStatus="es_cellsx">

            <tr>


                <td>${es_cell['es_cell_clone']}</td>
                <td>${es_cell['es_cell_strain']}</td>
                <td>${es_cell['parental_cell_line']}</td>
                <td>${es_cell['ikmc_project_id']}</td>

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
                        <div class="qcData" data-type="es_cell" data-name="${es_cell['es_cell_clone']}"
                             data-alleletype="${es_cell['allele_type']}"></div>
                    </div>
                    <c:if test="${not empty es_cell['qc_about']}">
                        <a target="_blank" href="${es_cell['qc_about']}">(&nbsp;about&nbsp;)</a>
                    </c:if>

                </td>
                <td>${es_cell['associated_product_vector_name']}</td>

                <td>
                    <c:forEach var="order" items="${es_cell['orders']}" varStatus="ordersx">
                        <a class="btn" href="${order['url']}"> <i class="fa fa-shopping-cart"></i> ${order['name']}</a>&nbsp;
                    </c:forEach>
                </td>


            </tr>

        </c:forEach>

        </tbody>
    </table>


</c:if>
