<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%-- DEFINE IMAGE GROUPS AND CALL impcimgdisplaycard FOR EACH GROUP --%>
<div class="row">
    <c:forEach var="group" items="${impcImageGroups}" varStatus="status">
        <c:forEach var="doc" items="${group.result}">

            <c:if test="${doc.omero_id == '-1'}"><!-- these are secondary project images so compara image view won't work on them -->
                <c:if test="${acc!=null}">
                    <c:set var="query" value='q=gene_accession_id:"${acc}"'/>
                </c:if>
                <c:if test="${phenotype.getMpId()!=null}">
                    <c:set var="query"
                           value='q=mp_id:"${phenotype.getMpId()}" OR+intermediate_mp_id:"${phenotype.getMpId()}" OR intermediate_mp_term:"${phenotype.getMpId()}" OR top_level_mp_term:"${phenotype.getMpId()}"&fq=parameter_stable_id:${doc.parameter_stable_id}'/>
                </c:if>
                <c:set var="href" scope="page"
                       value='${baseUrl}/impcImages/images?${query}&fq=parameter_stable_id:${doc.parameter_stable_id}'></c:set>
            </c:if>

            <c:if test="${doc.omero_id != '-1'}">

                <c:set var="query" value='parameter_stable_id=${doc.parameter_stable_id}'/>

                <c:if test="${acc!=null}">
                    <c:set var="query" scope="page" value='&${query}&acc=${acc}'></c:set>
                </c:if>
                <c:if test="${phenotype.getMpId()!=null}">
                    <c:set var="query" scope="page" value='&${query}&mp_id=${phenotype.getMpId()}'></c:set>
                </c:if>
                <c:set var="href" scope="page" value='${baseUrl}/imageComparator?${query}'></c:set>
            </c:if>
            <t:impcimgdisplaycard
                img="${doc}"
                impcMediaBaseUrl="${impcMediaBaseUrl}"
                pdfThumbnailUrl="${pdfThumbnailUrl}"
                href="${fn:escapeXml(href)}"
                count="${paramToNumber[doc.parameter_stable_id]}"
                procedureName="${doc.procedure_name}"
                parameterName="${doc.parameter_name}"></t:impcimgdisplaycard>
        </c:forEach>
    </c:forEach>
</div>




