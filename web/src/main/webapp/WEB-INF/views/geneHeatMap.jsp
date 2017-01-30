<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


	<link type='text/css' rel='stylesheet' href='${baseUrl}/css/geneHeatmapStyling.css'  />

	<script>
 		$(function() {
	    var header_height = 0;
	    $('table th span').each(function() {
	        if ($(this).outerWidth() > header_height) header_height = $(this).outerWidth();
	        $(this).width($(this).height()* 0.05);
	    });
	    $('table th').height(header_height);
		});
 	</script>
  	
  	<div id="export">
										<p class="textright">
											Download data as:
											<a id="tsvDownload" href="${baseUrl}/secondaryproject/export/idg?fileType=tsv&fileName=idgData" target="_blank" class="button fa fa-download">TSV</a>
											<a id="xlsDownload" href="${baseUrl}/secondaryproject/export/idg?fileType=xls&fileName=idgData" target="_blank" class="button fa fa-download">XLS</a>
										</p>
									</div>     
 	<table id="secondaryGeneHeatmap">
 	
   <thead>
     <tr> 
        <th class="gene-heatmap-header"><span>Gene</span></th>
        <th><span>Family</span></th>
        <th><span>Availability</span></th>
        <c:forEach var="xAxisBean" items="${xAxisBeans}" >
            <th title="${xAxisBean.name}"><span class="vertical"><a href="${baseUrl}/phenotypes/${xAxisBean.id}">${xAxisBean.name}</a></span></th>
        </c:forEach>
     </tr>
   </thead>
   
	 <c:forEach items="${geneRows}" var="row">
     	<tr>
            <td><a href="${baseUrl}/genes/${row.accession}">${row.symbol}</a><br/>${row.getHumanSymbol()}</td>
            <td>${row.groupLabel}</td>
      	    <td>${row.miceProduced}</td>
        <c:forEach var="xAxisBean" items="${xAxisBeans}" > 
          <td
           	<c:choose>

                <c:when test="${row.XAxisToCellMap[xAxisBean.name].status eq 'Deviance Significant'}">style="background-color:rgb(191, 75, 50)"</c:when>
                <c:when test="${row.XAxisToCellMap[xAxisBean.name].status eq 'Could not analyse'}">style="background-color: rgb(119, 119, 119)"</c:when>
                <c:when test="${row.XAxisToCellMap[xAxisBean.name].status eq 'Data analysed, no significant call'}">style="background-color: rgb(247, 157, 70)"</c:when>
                <c:when test="${row.XAxisToCellMap[xAxisBean.name].status eq 'No data' }">style="background-color: rgb(230, 242, 246)"</c:when>

            </c:choose>
            title="${xAxisBean.name}"></td>
        </c:forEach>
      </tr>
    </c:forEach>
    
    
    
  </table>
           
          