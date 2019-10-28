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
                    "paging":         false
                });
            });
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
                                                <form id="formUnregister" style="border: 0;">
                                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                    <button
                                                            formaction="${paBaseUrl}/unregistration/gene/${gene.mgiAccessionId}"
                                                            class="btn btn-block btn-primary btn-default"
                                                            type="submit"
                                                            formmethod="POST">
                                                        Stop following
                                                    </button>
                                                </form>
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

        <div class="container">
            <div class="row mb-5 ml-3 mr-3">
                <a
                        href="${paBaseUrl}/rilogout"
                        title="Log out of My Genes">
                    Logout
                </a>
                <a
                        class="btn btn-outline-secondary mx-auto"
                        href="${paBaseUrl}/resetPasswordRequest"
                        title="Reset My Genes password">
                    Reset password
                </a>
                <a
                        class="btn btn-outline-danger"
                        href="${paBaseUrl}/accountDeleteRequest"
                        title="Delete My Genes account and all of my followed genes"
                        style="float: right;">
                    Delete account
                </a>
            </div>
        </div>

    </jsp:body>
</t:genericpage>