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

<table id="phenotypes"  class="table tableSorter">

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
        <th>Data</th>
    </tr>
    </thead>

    <tbody>
    <c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
        <c:set var="europhenome_gender" value="Both-Split"/>
        <tr>

            <td><a href="${baseUrl}/genes/${phenotype.gene.accessionId}">${phenotype.gene.symbol}</a><br/>
                <span class="smallerAlleleFont"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></span>
            </td>

            <td>${phenotype.zygosity.getShortName()}</td>

            <td style="font-family:Verdana;font-weight:bold;">
           
                
  <t:displaySexes sexes="${phenotype.sexes}"></t:displaySexes>
                  
                
            </td>

            <td>${phenotype.lifeStageName}</td>

            <td>
                <a href="${baseUrl}/phenotypes/${phenotype.phenotypeTerm.id}">${phenotype.phenotypeTerm.name}</a>
            </td>

            <td>${phenotype.procedure.name} | ${phenotype.parameter.name}</td>

            <td>${phenotype.phenotypingCenter} | ${phenotype.dataSourceName}</td>

            <td>${phenotype.prValueAsString}</td>

            
           
            
               	<td  class="postQcLink">
           
            <c:if test="${phenotype.getEvidenceLink().getDisplay()}">
                <c:if test='${phenotype.getEvidenceLink().getIconType().name().equalsIgnoreCase("IMAGE")}'>
                    <a href="${phenotype.getEvidenceLink().getUrl() }"><i class="fa fa-image" alt="${phenotype.getEvidenceLink().getAlt()}"></i>
                    </a>
                </c:if>
                <c:if test='${phenotype.getEvidenceLink().getIconType().name().equalsIgnoreCase("GRAPH")}'>
                    <a href="${phenotype.getEvidenceLink().getUrl() }&pageTitle=Phenotype: ${phenotype.phenotypeTerm.name}"><i class="fa fa-bar-chart-o" alt="${phenotype.getEvidenceLink().getAlt()}"></i>
                    </a>
                </c:if>
                 <c:if test='${phenotype.getEvidenceLink().getIconType().name().equalsIgnoreCase("TABLE")}'>
                    <a href="${phenotype.getEvidenceLink().getUrl() }"><i class="fa fa-table" alt="${phenotype.getEvidenceLink().getAlt()}"></i>
                    </a>
                </c:if>
            </c:if>

            <c:if test="${!phenotype.getEvidenceLink().getDisplay()}">
                <c:if test='${phenotype.getEvidenceLink().getIconType().name().equalsIgnoreCase("IMAGE")}'>
                    <i class="fa fa-image" title="No images available."></i>
                </c:if>
                <c:if test='${phenotype.getEvidenceLink().getIconType().name().equalsIgnoreCase("GRAPH")}'>
                    <i class="fa fa-bar-chart-o" title="No supporting data supplied."></i>
                </c:if>
            </c:if>

            
        </td> <!-- This is closing the td from the 2 ifs above -->
            
            
        </tr>
    </c:forEach>
    </tbody>

</table>
