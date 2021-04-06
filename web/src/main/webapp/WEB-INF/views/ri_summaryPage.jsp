<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">My Genes</jsp:attribute>

    <jsp:attribute name="header">
        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>
    </jsp:attribute>

    <jsp:attribute name="bodyTag">
        <body class="no-sidebars small-header">
    </jsp:attribute>

    <jsp:attribute name="addToFooter">
        <script>
            $(document).ready(function () {
                $('#following').DataTable({
                    "paging": false,
                    "oLanguage": {
                        "sSearch": "Search the table:"
                    }
                });

                // Enable CSRF processing for forms on this page
                function loadCsRf() {
                    var token = $("meta[name='_csrf']").attr("content");
                    var header = $("meta[name='_csrf_header']").attr("content");
                    console.log('_csrf:_csrf_header' + token + ':' + header);
                    $(document).ajaxSend(function (e, xhr, options) {
                        xhr.setRequestHeader(header, token);
                    });
                }

                loadCsRf();

                // Wire up the AJAX callbacks to the approprate forms
                $('form.follow-form').submit(function (event) {

                    // Prevent the form from submitting when JS is enabled
                    event.preventDefault();

                    // Get which gene this is a form for
                    var $form = $(this),
                        acc = $form.find('input[name="geneAccessionId"]').val();

                    // Do asynch request to change the state of the follow flag for this gene
                    // and update button appropriately on success
                    $.ajax({
                        type: "POST",
                        url: "${baseUrl}/update-gene-registration?asynch=true",
                        data: $(this).serialize(),
                        success: function (data) {

                            // Data is a map of gene accession id -> status
                            // Status is either "Following" or "Not Following"
                            var acc = Object.keys(data)[0];
                            switch (data[acc]) {
                                case "Following":
                                    $('form#follow-form-' + acc.replace(":", "")).find("button")
                                        .attr('title', 'You are following ${gene.markerSymbol}. Click to stop following.')
                                        .removeClass('btn-primary')
                                        .addClass('btn-outline-secondary');

                                    $('form#follow-form-' + acc.replace(":", "")).find("span")
                                        .text('Unfollow');

                                    $('form#follow-form-' + acc.replace(":", "")).find('i')
                                        .removeClass('fa-user-plus')
                                        .addClass('fa-user-minus');
                                    break;

                                case "Not Following":
                                    $('form#follow-form-' + acc.replace(":", "")).find("button")
                                        .attr('title', 'Click to follow ${gene.markerSymbol}.')
                                        .addClass('btn-primary')
                                        .removeClass('btn-outline-secondary');

                                    $('form#follow-form-' + acc.replace(":", "")).find("span")
                                        .text('Follow');

                                    $('form#follow-form-' + acc.replace(":", "")).find('i')
                                        .removeClass('fa-user-minus')
                                        .addClass('fa-user-plus');
                                    break;

                                default:
                                    console.log("Cannot find response type for response: " + acc);
                                    break;
                            }
                        },
                        error: function () {
                            window.location("${baseUrl}/rilogin?target=${baseUrl}/summary");
                        }
                    });
                });

            });
        </script>
    </jsp:attribute>

    <jsp:body>

        <div class="container data-heading">
            <div class="row">
                <noscript>
                    <div class="col-12 no-gutters">
                        <h5 style="float: left">Please enable javascript if you want to log in to follow or stop
                            following this gene.</h5>
                    </div>
                </noscript>

                <div class="col-12 no-gutters">
                    <h2 class="mb-0">My Genes</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">

            <div class="breadcrumbs clear row">
                <div class="col-12 px-5 pt-5">
                    <aside><a href="${baseUrl}">Home</a>
                        <span class="fal fa-angle-right"></span>My Genes
                    </aside>
                </div>
            </div>

            <div class="row">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="row no-gutters">
                                <div class="col-12 px-0">
                                    <div class="row">
                                        <p>
                                            In this screen you may:
                                        <ul class="mt-0 pt-0">
                                            <li>
                                                <a
                                                        href="${baseUrl}/rilogout"
                                                        title="Log out of My Genes">
                                                    Logout
                                                </a>
                                            </li>
                                            <li>
                                                <a
                                                        href="${baseUrl}/resetPasswordRequest"
                                                        title="Reset your My Genes password">
                                                    Reset your password
                                                </a>
                                            </li>
                                            <li>
                                                <a
                                                        href="${baseUrl}/accountDeleteRequest"
                                                        title="Delete your My Genes account and all of your followed genes">
                                                    Delete your account
                                                </a>
                                            </li>
                                            <li>View and manage the list of genes you've followed</li>
                                        </ul>
                                        </p>

                                        <h4>Username: ${summary.emailAddress}</h4>
                                    </div>
                                    <i style="font-size: small">Gene status is refreshed daily. Please refresh your
                                        browser for the latest status.</i>
                                    <br/>
                                    <c:choose>
                                        <c:when test="${fn:length(summary.details) eq 0}">
                                            You have not yet registered interest in any genes.
                                        </c:when>
                                        <c:when test="${fn:length(summary.details) eq 1}">
                                            You are currently following this gene:
                                        </c:when>
                                        <c:otherwise>
                                            You are currently following these genes:
                                        </c:otherwise>
                                    </c:choose>

                                    <div id="summaryTableDiv" class="pb-4">
                                        <table id="following" class='table table-bordered'>
                                            <thead>
                                            <tr>
                                                <th>Gene Symbol</th>
                                                <th>Gene Accession Id</th>
                                                <th>Assignment Status</th>
                                                <th>Null Allele Production Status</th>
                                                <th>Conditional Allele Production Status</th>
                                                <th>Crispr Allele Production Status</th>
                                                <th>Phenotyping Data Available</th>
                                                <th>Action</th>
                                            </tr>
                                            </thead>

                                            <tbody>
                                            <c:forEach var="gene" items="${summary.details}" varStatus="loop">
                                                <tr>
                                                    <td>
                                                        <a href='${baseUrl}/genes/${gene.geneAccessionId}'>${gene.symbol}</a>
                                                    </td>
                                                    <td>
                                                        <a href="http://www.informatics.jax.org/marker/${gene.geneAccessionId}">${gene.geneAccessionId}</a>
                                                    </td>

                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${empty gene.assignmentStatus}">
                                                                None
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${gene.assignmentStatus}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${empty gene.nullAlleleProductionStatus}">
                                                                None
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${gene.nullAlleleProductionStatus}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${empty gene.conditionalAlleleProductionStatus}">
                                                                None
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${gene.conditionalAlleleProductionStatus}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${empty gene.crisprAlleleProductionStatus}">
                                                                None
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${gene.crisprAlleleProductionStatus}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${empty gene.phenotypingDataAvailable}">
                                                                No
                                                            </c:when>
                                                            <c:when test="${!gene.phenotypingDataAvailable}">
                                                                No
                                                            </c:when>
                                                            <c:otherwise>
                                                                Yes
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <form id="follow-form-${fn:replace(gene.geneAccessionId, ':', '')}"
                                                              action="${baseUrl}/update-gene-registration"
                                                              method="POST"
                                                              class="follow-form">
                                                            <input type="hidden" name="${_csrf.parameterName}"
                                                                   value="${_csrf.token}"/>
                                                            <input type="hidden" name="geneAccessionId"
                                                                   value="${gene.geneAccessionId}"/>
                                                            <button class="btn btn-outline-secondary" type="submit">
                                                                <i class="fas fa-user-minus"></i>
                                                                <span>Unfollow</span>
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
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>