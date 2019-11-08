<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">My Genes</jsp:attribute>

    <jsp:attribute name="bodyTag">
        <body class="no-sidebars small-header">
    </jsp:attribute>

    <jsp:attribute name="addToFooter">
        <script>
            $(document).ready(function () {
                $('#following').DataTable({
                    "scrollY":        "50vh",
                    "scrollCollapse": true,
                    "paging":         false,
                    "oLanguage": {
                        "sSearch": "Search the table:"
                    }
                });
            });
        </script>
    </jsp:attribute>
MadedddddMa
    <jsp:attribute name="header">

        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>

        <script type="text/javascript">

            var paBaseUrl = '${paBaseUrl}';

            $(document).ready(function () {
                loadCsRf();
                registerInterestInitialise();
            });


            function loadCsRf() {
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                console.log('_csrf:_csrf_header' + token + ':' + header);
                $(document).ajaxSend(function(e, xhr, options) {
                    xhr.setRequestHeader(header, token);
                });
            }


            function registerInterestInitialise() {
                $('a.ri_unregister')
                    .click(function() {

                        var acc = $(this).data("acc");
                        var url = "${paBaseUrl}/unregistration/gene/" + acc;
                        var table = $('#following').DataTable();
                        var tr = $(this).closest('tr');

                        $.ajax({
                            type: "POST",
                            url: url,
                            success: function() {

                                table.row($(tr))
                                    .remove()
                                    .draw();
                            },
                            error: function(jqXhr, textStatus, errorThrown) {
                                console.log('Unregistration of ' + ${acc} + ' failed:');
                            }
                        });
                    });
            }

        </script>
    </jsp:attribute>

    <jsp:body>

        <div class="container single single--no-side">

            <div class="breadcrumbs" style="box-shadow: none; margin-top: auto; margin: auto; padding: auto">

                <div class="row">
                    <div class="col-md-12">
                        <p><a href="${paBaseUrl}">Home</a>
                            <span class="fal fa-angle-right"></span>My Genes
                        </p>
                    </div>
                </div>
            </div>

            <div class="row row-over-shadow">
                <div class="col-md-12 white-bg">
                    <div class="page-content">
                        <h2 class="title" id="top">My Genes</h2>
                        <p>
                            In this screen you may:
                            <ul class="mt-0 pt-0">
                                <li>
                                    <a
                                            href="${paBaseUrl}/rilogout"
                                            title="Log out of My Genes">
                                        Logout
                                    </a>
                                </li>
                                <li>
                                    <a
                                            href="${paBaseUrl}/resetPasswordRequest"
                                            title="Reset your My Genes password">
                                        Reset your password
                                    </a>
                                </li>
                                <li>
                                    <a
                                            href="${paBaseUrl}/accountDeleteRequest"
                                            title="Delete your My Genes account and all of your followed genes">
                                        Delete your account
                                    </a>
                                </li>
                                <li>View and manage the list of genes you've followed</li>
                            </ul>
                        </p>

                        <h4>Username: ${summary.emailAddress}</h4>

                        <c:choose>
                            <c:when test="${fn:length(summary.genes) eq 0}">
                                You have not yet registered interest in any genes.
                            </c:when>
                            <c:when test="${fn:length(summary.genes) eq 1}">
                                You are currently following this gene:
                            </c:when>
                            <c:otherwise>
                                You are currrently following these genes:
                            </c:otherwise>
                        </c:choose>

                        <div id="summaryTableDiv">
                            <table id="following" class='table table-bordered table-hoverr'>
                                <thead>
                                    <tr>
                                        <th>Gene Symbol</th>
                                        <th>Gene Accession Id</th>
                                        <th>Assignment Status</th>
                                        <th>Null Allele Production Status</th>
                                        <th>Conditional Allele Production Status</th>
                                        <th>Phenotyping Data Available</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <c:forEach var="gene" items="${summary.genes}" varStatus="loop">
                                        <tr>
                                            <td>
                                                <a href='${paBaseUrl}/genes/${gene.mgiAccessionId}'>${gene.symbol}</a>
                                            </td>
                                            <td><a href="http://www.informatics.jax.org/marker/${gene.mgiAccessionId}">${gene.mgiAccessionId}</a></td>

                                            <td>
                                                <c:choose>
                                                    <c:when test="${empty gene.riAssignmentStatus}">
                                                        None
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${gene.riAssignmentStatus}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${empty gene.riNullAlleleProductionStatus}">
                                                        None
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${gene.riNullAlleleProductionStatus}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${empty gene.riConditionalAlleleProductionStatus}">
                                                        None
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${gene.riConditionalAlleleProductionStatus}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${gene.riPhenotypingStatus == 'Phenotyping data available'}">

                                                        <a href='${paBaseUrl}/genes/${gene.mgiAccessionId}#order'>Yes</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href='${paBaseUrl}/genes/${gene.mgiAccessionId}#order'>No</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <a
                                                        id="ri_unregister_${gene.symbol}"
                                                        data-acc="${gene.mgiAccessionId}"
                                                        class="ri_unregister btn btn-primary"
                                                        title="You are following ${gene.symbol}. Click to stop following."
                                                        >
                                                    Stop following
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>