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

<table id="significant-phenotypes-table"
       data-toggle="table"
       data-pagination="true"
       data-sortable="true"
       data-custom-sort="sortString"
       data-search="true"
       data-card-view="true"
       data-show-search-clear-button="true"
       data-mobile-responsive="true">

    <thead>
    <tr>
        <th data-sortable="true" data-field="0" >Gene / Allele</th>
        <th data-sortable="true" data-field="1">Zygosity</th>
        <th data-field="2">Sex</th>
        <th data-sortable="true" data-field="3">Life Stage</th>
        <th data-sortable="true" data-field="4">Phenotype</th>
        <th data-sortable="true" data-field="5">Parameter</th>
        <th data-sortable="true" data-field="6">Phenotyping Center</th>
        <th data-sortable="true" data-field="7">P Value</th>
    </tr>
    </thead>

    <tbody>
    <c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
        <c:set var="europhenome_gender" value="Both-Split"/>
        <tr data-link="${fn:escapeXml(phenotype.getEvidenceLink().url)}" class="clickableRow">

            <td><span href="${baseUrl}/genes/${phenotype.gene.accessionId}">${phenotype.gene.symbol}</span><br/>
                <span class="small font-italic"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></span>
            </td>

            <td>${phenotype.zygosity.getShortName()}</td>

            <td style="font-family:Verdana;font-weight:bold;">
                <t:displaySexes sexes="${phenotype.sexes}"></t:displaySexes>
            </td>

            <td>${phenotype.lifeStageName}</td>

            <td>${phenotype.phenotypeTerm.name}</td>

            <td>${phenotype.parameter.name}<br /><span class="small font-italic">${phenotype.procedure.name}</td>

            <td>${phenotype.phenotypingCenter}<br /><span class="small font-italic">${phenotype.dataSourceName}</span></td>

            <td data-sort="${phenotype.prValueAsString}">
                <t:formatScientific>${phenotype.prValueAsString}</t:formatScientific>
            </td>

        </tr>
    </c:forEach>
    </tbody>

</table>

<script>

    $('#significant-phenotypes-table').on('click-row.bs.table', function (e, row) {
        if (row._data['link']) {
            window.location = row._data['link'];
        }
    });

</script>