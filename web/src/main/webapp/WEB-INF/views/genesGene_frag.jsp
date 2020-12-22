<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 23/02/2016
  Time: 10:37
  To change this template use File | Settings | File Templates.
--%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<c:if test="${gene.markerName != null}">
    <div class="row">
        <div class="col-12 col-md-2 font-weight-bold">
            Name
        </div>
        <div class="col-12 col-md-10">
                ${gene.markerName}
        </div>
    </div>
</c:if>

<c:if test="${!(empty gene.markerSynonym)}">
    <div class="row">
            <div class="col-12 col-md-2 align-middle">
                <div class="align-middle font-weight-bold">Synonyms</div>
            </div>
            <div class="col-12 col-md-10 align-middle">
                <c:if test='${fn:length(gene.markerSynonym) gt 1}'>

                        <c:forEach var="synonym" items="${gene.markerSynonym}" varStatus="loop">
                            <span class="badge badge-info">${synonym}</span>
                        </c:forEach>

                </c:if>
                <c:if test='${fn:length(gene.markerSynonym) == 1}'>

                    <c:forEach var="synonym" items="${gene.markerSynonym}" varStatus="loop">
                        <t:formatAllele>${synonym}</t:formatAllele>
                        <%--<c:if test="${!loop.last}">,&nbsp;</c:if>--%>
                    </c:forEach>

                </c:if>
            </div>
    </div>
</c:if>

<c:if test="${!(prodStatusIcons == '')}">
    <div class="row">
        <div class="col-12 col-md-2 align-middle">
            <div class="align-middle font-weight-bold">Production Status</div>
        </div>
        <div class="col-12 col-md-10 align-middle">
                ${prodStatusIcons}
        </div>
    </div>
</c:if>

<c:if test="${viabilityCalls != null && viabilityCalls.size() > 0}">
    <div class="row">
        <div class="col-12 col-md-2 align-middle">
            <div class="align-middle font-weight-bold">Viability</div>
        </div>
        <div class="col-12 col-md-10 align-middle">
            <t:viabilityButton callList="${viabilityCalls}" geneAcc="${gene.mgiAccessionId}" ></t:viabilityButton>
        </div>
    </div>
</c:if>

<div class="row">
    <div class="col-12 col-md-2 align-middle">
        <div class="align-middle font-weight-bold">Links</div>
    </div>
    <div class="col-12 col-md-10 align-middle">
        <a target="_blank" href="http://www.informatics.jax.org/marker/${gene.mgiAccessionId}"
           title="View gene at JAX">${gene.mgiAccessionId}</a>
        &nbsp;&nbsp;

        <a target="_blank" href="http://www.ensembl.org/Mus_musculus/Gene/Summary?g=${gene.mgiAccessionId}"
           title="View mouse gene with ensembl genome broswer">Ensembl Gene</a>
        &nbsp;&nbsp;

        <a target="_blank"
           href="http://www.ensembl.org/Mus_musculus/Location/Compara_Alignments/Image?align=677;db=core;g=${gene.mgiAccessionId}"
           title="View mouse-human gene orthologs with Ensembl comparative genomics browser">Ensembl Orthologs</a>
        &nbsp;&nbsp;

        <a target="_blank" href="${baseUrl}/summary" title="Show your genes of interest">My Genes</a>
        &nbsp;&nbsp;

        <c:if test="${gene.isIdgGene}">
            <a href="${baseUrl}/secondaryproject/idg"
               title="Illuminating the Druggable Genome mouse-human orthologue">IDG</a>
            &nbsp;&nbsp;
        </c:if>
    </div>
</div>


<!-- login interest button -->
<div class="row mt-2">
    <div class="col-12 col-md-12">
        <div class="float-left mr-1 mb-1">
            <c:if test="${orderPossible}">
                <a class="btn btn-primary inactive"  role="button" aria-pressed="false" href="#order2"><i class="fa fa-shopping-cart"></i>&nbsp;Order</a>
            </c:if>
        </div>
        <div class="float-left">
            <form style="border: 0;">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" name="target" value="${baseUrl}/genes/${gene.mgiAccessionId}"/>
                <button type="submit" class="btn btn-primary" formaction="${registerButtonAnchor}"
                        formmethod="${formMethod}">
                    <i class="${registerIconClass}"></i>&nbsp;
                    ${registerButtonText}
                </button>
            </form>
        </div>


    </div>

</div>


