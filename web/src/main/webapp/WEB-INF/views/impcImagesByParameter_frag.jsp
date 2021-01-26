<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 19:53
  To change this template use File | Settings | File Templates.
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<div class="row">

        <c:forEach var="group" items="${impcImageGroups}" varStatus="status">

<div class="col-lg-2 col-4">
            <c:forEach var="doc" items="${group.result}">
            
	
                <div class="card mt-3 pt-2" >

                    <c:set var="label" value="${doc.procedure_name}: ${doc.parameter_name}"/>
                    <c:if test="${doc.parameter_name eq 'Images'}">
                        <c:set var="label" value="${doc.procedure_name}"/>
                    </c:if>

                    <c:if test="${doc.omero_id == '0'}"><!-- these are secondary project images so compara image view won't work on them -->

                    <c:if test="${acc!=null}">
                        <c:set var="query" value='q=gene_accession_id:"${acc}"'/>
                    </c:if>
                    <c:if test="${phenotype.getMpId()!=null}">
                        <c:set var="query"
                               value='q=mp_id:"${phenotype.getMpId()}" OR+intermediate_mp_id:"${phenotype.getMpId()}" OR intermediate_mp_term:"${phenotype.getMpId()}" OR top_level_mp_term:"${phenotype.getMpId()}"&fq=parameter_stable_id:${doc.parameter_stable_id}'/>
                    </c:if>


                    <!-- http://localhost:8080/phenotype-archive/impcImages/images?q=*:*%20AND%20observation_type:image_record&qf=imgQf&defType=edismax&fq=procedure_name:%22Brain%20Histopathology%22 -->
                    <c:set var="href" scope="page"
                           value='${baseUrl}/impcImages/images?${query}&fq=parameter_stable_id:${doc.parameter_stable_id}'></c:set>
                    </c:if>
                        <%-- <c:when test="${doc.omero_id == '0' && phenotype.getMpId() != null}"><!-- these are secondary project images so compara image view won't work on them -->
                            <!-- http://localhost:8080/phenotype-archive/impcImages/images?q=*:*%20AND%20observation_type:image_record&qf=imgQf&defType=edismax&fq=procedure_name:%22Brain%20Histopathology%22 -->
                            <c:set var="href" scope="page"
                                   value='${baseUrl}/impcImages/images?q=mp_id:"${phenotype.getMpId()}" OR+intermediate_mp_id:"${phenotype.getMpId()}" OR intermediate_mp_term:"${phenotype.getMpId()}" OR top_level_mp_term:"${phenotype.getMpId()}"&fq=parameter_stable_id:${doc.parameter_stable_id}'></c:set>&fq=parameter_stable_id:\"${doc.parameter_stable_id}"&fq=mp_id:"MP:0000807" OR+intermediate_mp_id:"MP:0000807" OR intermediate_mp_term:"MP:0000807" OR top_level_mp_term:"MP:0000807"&group=true&group.field=parameter_stable_id&group

                        </c:when> --%>
                    <c:if test="${doc.omero_id != '-1'}"> <!-- omero images and gene or mp ids for comparator -->
                        <c:set var="query" value='parameter_stable_id=${doc.parameter_stable_id}'/>
                        <c:if test="${acc!=null}">
                            <c:set var="query" scope="page"
                                   value='&${query}&acc=${acc}'></c:set>
                        </c:if>
                        <c:if test="${phenotype.getMpId()!=null}">
                            <c:set var="query" scope="page"
                                   value='&${query}&mp_id=${phenotype.getMpId()}'></c:set>
                        </c:if>


                        <c:set var="href" scope="page"
                               value='${baseUrl}/imageComparator?${query}'></c:set>
                    </c:if>

                    <%--img class="card-img-top img-fluid" src="${doc.thumbnail_url}" alt="Card image cap"--%>


                    <t:impcimgdisplaycard
                            img="${doc}"
                            impcMediaBaseUrl="${impcMediaBaseUrl}"
                            pdfThumbnailUrl="${pdfThumbnailUrl}"
                            href="${fn:escapeXml(href)}"
                            count="${paramToNumber[doc.parameter_stable_id]}"
                            parameterName="${label}"></t:impcimgdisplaycard>


                        <%--  <div class="clear"></div>
                            <c:if test="${entry.count>5}">
                                <p class="textright"><a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}"><i class="fa fa-caret-right"></i> show all ${entry.count} images</a></p>
                            </c:if> --%>

                </div>
                
                
                
            </c:forEach>

</div>
        </c:forEach>
        
</div>




