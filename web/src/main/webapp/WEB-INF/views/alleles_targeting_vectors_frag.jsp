<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<c:if test="${not empty targeting_vectors}">

    <h3>Targeting Vectors</h3>


    <table class="table table-striped" id="targeting_vector_table">
        <thead>
        <tr>

            <th>Design Oligos</th>
            <th>Targeting Vector</th>
            <th>Cassette</th>
            <th>Backbone</th>
            <th>IKMC Project</th>
            <th>Genbank File</th>
            <th>Vector Map</th>
            <th>Order</th>

        </tr>
        </thead>

        <tbody>

        <c:forEach var="targeting_vector" items="${targeting_vectors}" varStatus="targeting_vectorsx">


            <tr>

                <td>
            <span>
                <c:if test="${not empty targeting_vector['design_oligos_url']}">
                    <a href="${targeting_vector['design_oligos_url']}" target="_blank"><i
                            class="fa fa-pencil-square-o fa-2x"></i></a>
                </c:if>
            </span>
                </td>

                <td>${targeting_vector['targeting_vector']}</td>
                <td>${targeting_vector['cassette']}</td>
                <td>${targeting_vector['backbone']}</td>
                <td>${targeting_vector['ikmc_project_id']}</td>

                <td>
            <span>
                <c:if test="${not empty targeting_vector['genbank_file']}">
                    <a href="${targeting_vector['genbank_file']}">
                        <i class="fa fa-file-text fa-lg"></i>
                    </a>
                </c:if>
            </span>
                </td>

                <td>
            <span>
                <c:if test="${not empty targeting_vector['allele_image']}">
                    <a href="${targeting_vector['allele_image']}">
                        <i class="fa fa-file-text fa-lg"></i>
                    </a>
                </c:if>
            </span>
                </td>

                <td>
                    <c:forEach var="order" items="${targeting_vector['orders']}" varStatus="ordersx">
                        <a href="${order['url']}"> <i class="fa fa-shopping-cart"></i> ${order['name']}</a>
                    </c:forEach>
                </td>

            </tr>
        </c:forEach>

        </tbody>
    </table>

</c:if>
