<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<c:set var="count" value="0" scope="page"/>
<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
    <c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/></c:forEach>
</c:forEach>

<p class="resultCount">
    Total number of significant genotype-phenotype associations: ${count}
</p>

<script>
    var resTemp = document.getElementsByClassName("resultCount");
    if (resTemp.length > 1)
        resTemp[0].remove();
</script>

<table id="phenotypes" class="table clickableRows" style="width: 100%">

    <thead>
    <tr>
        <th>Gene / Allele</th>
        <th>Zygosity</th>
        <th>Sex</th>
        <th>Life Stage</th>
        <th>Phenotype</th>
        <th>Procedure | Parameter</th>
        <th>Phenotyping Center | Source</th>
        <th>P Value</th>
    </tr>
    </thead>

    <tbody>
    <c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
        <c:set var="europhenome_gender" value="Both-Split"/>
        <tr>

            <td><span href="${baseUrl}/genes/${phenotype.gene.accessionId}">${phenotype.gene.symbol}</span><br/>
                <span class="smallerAlleleFont"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></span>
            </td>

            <td>${phenotype.zygosity.getShortName()}</td>

            <td style="font-family:Verdana;font-weight:bold;">


                <t:displaySexes sexes="${phenotype.sexes}"></t:displaySexes>


            </td>

            <td>${phenotype.lifeStageName}</td>

            <td>
                <span href="${baseUrl}/phenotypes/${phenotype.phenotypeTerm.id}">${phenotype.phenotypeTerm.name}</span>
            </td>

            <td>${phenotype.procedure.name} | ${phenotype.parameter.name}</td>

            <td>${phenotype.phenotypingCenter} | ${phenotype.dataSourceName}</td>

            <td data-sort="${phenotype.prValueAsString}">
                <t:formatScientific>${phenotype.prValueAsString}</t:formatScientific>
            </td>


            <c:if test="${phenotype.getEvidenceLink().getDisplay()}">
                <c:if test='${phenotype.getEvidenceLink().getIconType().name().equalsIgnoreCase("IMAGE")}'>
                    <td data-sort="${phenotype.getEvidenceLink().getUrl()}">
                    </td>
                </c:if>
                <c:if test='${phenotype.getEvidenceLink().getIconType().name().equalsIgnoreCase("GRAPH")}'>
                    <td data-sort="${phenotype.getEvidenceLink().getUrl() }&pageTitle=${phenotype.phenotypeTerm.name} phenotype with knockout ${phenotype.gene.symbol}&linkBack=linkback link here"></td>
                </c:if>
                <c:if test='${phenotype.getEvidenceLink().getIconType().name().equalsIgnoreCase("TABLE")}'>
                    <td data-sort="${phenotype.getEvidenceLink().getUrl()}"></td>
                </c:if>
            </c:if>

            <c:if test="${!phenotype.getEvidenceLink().getDisplay()}">
                <td data-sort="none"></td>
            </c:if>


        </tr>
    </c:forEach>
    </tbody>

</table>
