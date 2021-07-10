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
       data-custom-sort="sortString"
       data-search="true"
       data-mobile-responsive="true">

    <thead>
    <tr>
        <th data-sortable="true" data-sort-name="value">Gene / Allele</th>
        <th data-sortable="true" data-sort-name="value">Zygosity</th>
        <th>Sex</th>
        <th data-sortable="true" data-sort-name="value">Life Stage</th>
        <th data-sortable="true" data-sort-name="value">Phenotype</th>
        <th data-sortable="true" data-sort-name="value">Parameter</th>
        <th data-sortable="true" data-sort-name="value">Phenotyping<br/>Center</th>
        <th data-sortable="true" data-sorter="sortPValue" data-sort-name="value">P Value</th>
    </tr>
    </thead>

    <tbody>
    <c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
        <c:set var="europhenome_gender" value="Both-Split"/>
        <tr data-link="${fn:escapeXml(phenotype.evidenceLink.url)}" class="clickableRow">

            <td data-value="${phenotype.gene.symbol}_${phenotype.allele.symbol}"><span href="${baseUrl}/genes/${phenotype.gene.accessionId}">${phenotype.gene.symbol}</span><br/>
                <span class="small font-italic"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></span>
            </td>

            <td data-value="${phenotype.zygosity.shortName}">${phenotype.zygosity.shortName}</td>

            <td><t:displaySexes sexes="${phenotype.sexes}"></t:displaySexes></td>

            <td data-value="${phenotype.lifeStageName}">${phenotype.lifeStageName}</td>

            <td data-value="${phenotype.phenotypeTerm.name}">${phenotype.phenotypeTerm.name}</td>

            <td data-value="${phenotype.parameter.name}">${phenotype.parameter.name}<br /><span class="small font-italic">${phenotype.procedure.name}</td>

            <td data-value="${phenotype.phenotypingCenter}">${phenotype.phenotypingCenter}<br /><span class="small font-italic">${phenotype.dataSourceName}</span></td>

            <td data-value="${phenotype.prValueAsString}"><t:formatScientific>${phenotype.prValueAsString}</t:formatScientific></td>

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