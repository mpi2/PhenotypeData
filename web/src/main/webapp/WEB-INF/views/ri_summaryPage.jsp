<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">Register Interest Summary</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${paBaseUrl}/summary">Register Interest</a> &raquo; Summary</jsp:attribute>
    <jsp:attribute name="bodyTag">
        <body>
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">Register Interest Summary </h1>

                        <div class="section">
                            <div class="inner">
                                <form id="formActions" style="border: 0;">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <a href="${paBaseUrl}/logout2">Logout</a>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    <button type="submit" class="btn btn-block btn-primary btn-default" formaction="${paBaseUrl}/changePasswordRequest" formmethod="get">Reset registration of interest password</button>
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    <button type="submit" class="btn btn-block btn-primary btn-default" formaction="${paBaseUrl}/account" formmethod="get">Delete all registrations of interest</button>
                                </form>

                                <h6>Username: ${summary.emailAddress}</h6>

                                <br />

                                <c:choose>
                                    <c:when test="${fn:length(summary.genes) eq 0}">

                                        You have not yet registered interest in any genes.

                                        <br />

                                    </c:when>
                                    <c:otherwise>

                                        You have registered interest in the following ${fn:length(summary.genes)} genes:

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
                                                            <td><a href="//www.informatics.jax.org/marker/${gene.mgiAccessionId}">${gene.mgiAccessionId}</a></td>

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
                                                                <a href="${paBaseUrl}/unregistration/gene?geneAccessionId=${gene.mgiAccessionId}">Unregister</a>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <br />

                            <a href='${paBaseUrl}/search/gene?kw=*'>Search for more genes to register</a>

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script type="text/javascript">

            $(document).ready(function () {

                // Disable drupal login links on the page
                $('ul.menu li.leaf').css('display', 'none');
            });
        </script>

    </jsp:body>
</t:genericpage>