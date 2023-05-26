<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>



			<table id="anatomy" class="table tableSorter">
					    <thead>
					    <tr>
					        <th class="headerSort">Gene/Allele</th>
					        <th class="headerSort">Expression</th>
					        <th class="headerSort">Anatomy</th>					        
					        <th class="headerSort">Zygosity</th>
					        <th class="headerSort">Sex</th>
					        <th class="headerSort">Parameter</th>
					        <th class="headerSort">Phenotyping Center</th>
					        <th class="headerSort"># LacZ images</th>
					        <th class="headerSort">Images</th>
					    </tr>
					    </thead>
					    <tbody>
					    <c:forEach var="row" items="${anatomyTable}" varStatus="status">
					        <c:set var="europhenome_gender" value="Both-Split"/>
					        <tr>
					            <td>
					            	<c:if test="${row.gene.accessionId != null}">
					            	 	<a href="${baseUrl}/genes/${row.gene.accessionId}">${row.gene.symbol} </a><br/> <span class="smallerAlleleFont"><t:formatAllele>${row.allele.symbol}</t:formatAllele></span>
					             	</c:if>
					             	<c:if test="${row.gene.accessionId == null}">
					            	 	&nbsp;control
					             	</c:if>
					            </td>
					            <td>
					            	<%-- <c:if test="${row.expression eq 'expression'}">
					            		detected
					            	</c:if>
					            	<c:if test="${row.expression eq 'no expression'}">
					            		non detected
					            	</c:if> --%>
					            	${row.expression}
					            </td>
					           	<td>${row.anatomyLinks}</td>
					            <td>${row.zygosity.getShortName()}</td>
					            <td>
					            <t:displaySexes sexes="${row.sexes}"></t:displaySexes>
					               <%--  assuming we don't need this for europhenome data check anymore - checked with JM and he thinks data is treated the same in the cores now
					               <c:set var="count" value="0" scope="page"/>
					                <c:forEach var="sex" items="${row.sexes}"><c:set var="count" value="${count + 1}" scope="page"/>
					                    <c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/>
					                        <img alt="Female" src="${baseUrl}/img/female.jpg"/>
					                    </c:if>
					                    <c:if test="${sex == 'male'}">
					                        <c:if test="${count != 2}"><img data-placement="top" src="${baseUrl}/img/empty.jpg"/></c:if>
					                        <c:set var="europhenome_gender" value="Male"/><img alt="Male" src="${baseUrl}/img/male.jpg"/>
					                    </c:if>
					                </c:forEach> --%>
					            </td>				
					            <td>${row.parameter.name}</td>
					            <td>${row.phenotypingCenter} </td>
					            <td>${row.numberOfImages} </td>
					            <td>
					            	<c:if test="${row.numberOfImages > 0}">
					            		<a href='${row.getEvidenceLink().getUrl()}'><i class="fa fa-image" alt="${row.getEvidenceLink().getAlt()}"></i></a>
					            	</c:if>
					            </td>	
					        </tr>
					    </c:forEach>
					    </tbody>
					</table>
					<br/>
					 <div id="export">
                  <p class="textright">
                      Download data as:
					<a id="tsvDownload" href="${baseUrl}/anatomy/export/${anatomy.anatomyId}?fileType=tsv" target="_blank" class="button fa fa-download download-data">TSV</a>
					<a id="xlsDownload" href="${baseUrl}/anatomy/export/${anatomy.anatomyId}?fileType=xls" target="_blank" class="button fa fa-download download-data">XLS</a>
					</p>
					</div>