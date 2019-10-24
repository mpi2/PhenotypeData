<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">My Genes</jsp:attribute>


    <jsp:attribute name="breadcrumb">&nbsp;&raquo;
        <a href="${paBaseUrl}/summary">
            My genes
        </a> &raquo; Summary
    </jsp:attribute>

    <jsp:attribute name="bodyTag">
        <body class="no-sidebars small-header">
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

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
                        <h1 class="title" id="top">My Genes</h1>
                        <c:choose>
                            <c:when test="${fn:length(summary.genes) eq 0}">
                                You have not yet registered interest in any genes.
                            </c:when>
                            <c:when test="${fn:length(summary.genes) eq 1}">
                                You are currently following this gene:
                            </c:when>
                            <c:otherwise>
                                You are currrently following these ${fn:length(summary.genes)} genes:
                            </c:otherwise>
                        </c:choose>

                        <div id="summaryTableDiv">
                            <table id="summary-table" class='table tableSorter'>
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
                                                    <c:when test="${gene.riNullAlleleProductionStatus == 'Genotype confirmed mice'}">
                                                        <a href='${paBaseUrl}/search/allele2?kw="${gene.mgiAccessionId}"'>${gene.riNullAlleleProductionStatus}</a>
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
                                                    <c:when test="${gene.riConditionalAlleleProductionStatus == 'Genotype confirmed mice'}">
                                                        <a href='${paBaseUrl}/search/allele2?kw="${gene.mgiAccessionId}"'>${gene.riConditionalAlleleProductionStatus}</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${gene.riConditionalAlleleProductionStatus}
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${gene.riPhenotypingStatus == 'Phenotyping data available'}">

                                                        <a href='${paBaseUrl}/genes/${gene.mgiAccessionId}#section-associations'>Yes</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        No
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <form id="formUnregister" style="border: 0;">
                                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                    <button type="submit" class="btn btn-block btn-primary btn-default" formaction="${paBaseUrl}/unregistration/gene/${gene.mgiAccessionId}" formmethod="POST">Unregister</button>
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

            <div class="row">
                <a
                        href="${paBaseUrl}/search"
                        title="Search for more genes to follow">
                    More genes
                </a>
            </div>

            <div class="row">
                <div class=col-3">

                    <a
                            href="${paBaseUrl}/rilogout"
                            title="Log out of My Genes">
                        Logout
                    </a>

                    <a
                            class="btn btn-outline-secondary"
                            href="${paBaseUrl}/resetPasswordRequest"
                            title="Reset My Genes password">
                        Reset password
                    </a>

                    <a
                            class="btn btn-outline-danger"
                            href="${paBaseUrl}/accountDeleteRequest"
                            title="Delete my account and all of my followed genes">
                        Delete account
                    </a>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>