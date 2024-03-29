<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<!-- just for testing with styles remove -->
<%-- <head>
<link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
</head> --%>
<t:genericpage>

    <jsp:attribute name="title">${allele.markerSymbol}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${allele.markerSymbol}</jsp:attribute>
    <jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">

	</jsp:attribute>
    <jsp:attribute name="addToFooter">
          
	</jsp:attribute>


    <jsp:attribute name="header">

	</jsp:attribute>

    <jsp:body>
        <!-- <div class="region region-content">
        <div class="block">
        <div class="content">
        <div class="node node-gene"> -->
        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2>${type.name}:<t:formatAllele>${allele.markerSymbol}<${allele.alleleName}></t:formatAllele></h2>
                </div>
            </div>
        </div>

        <!-- <div class="section">

        <div class="inner"> -->
        <div class="container single single--no-side">
            <div class="row">
                <div class="col-12 white-bg">
                    <div class="page-content pt-5 pb-5">

                        <c:if test="${fn:length(storeToProductsMap)>0}">

                            <c:if test="${creLine}">
                                <c:set var="creLineParam" value="&creLine=true"/>
                            </c:if>

                            <h4>This product is available from the following repositories:</h4>
                            <table>
                                <thead>
                                </thead>
                                <tbody>
                                <c:forEach var="store" items="${storeToProductsMap}">

                                    <tr><%-- ${store} --%>
                                        <td><%-- store=${store} --%> <img
                                                src="img/rep_icons/${fn:toLowerCase(store.key)}.jpg"
                                                alt="${store.key}" height="30px"
                                                onerror="this.style.display='none';this.parentElement.innerHTML='${store.key}';"/>
                                        </td>
                                        <td>
                                            <!-- just to print out the order link in case there are multiple order names and links that aren't this one -->
                                            <c:forEach var="orderName" items="${ store.value[0].orderNames}"
                                                       varStatus="repoIndex">
                                                <%-- ${orderName} store!!!!= ${store} --%>
                                                <c:if test="${orderName eq store.key}">
                                                    <a class="btn"
                                                       href="${ store.value[0].orderLinks[repoIndex.index] }"
                                                       target="_blank"><i class="fa fa-shopping-cart"></i>&nbsp;&nbsp;&nbsp;&nbsp;Go
                                                        to ${store.key} site for ordering</a>
                                                </c:if>
                                            </c:forEach>
                                        </td>
                                        <td></td>
                                    </tr>
                                    <!-- loop through the products so we get their name and qc links -->
                                    <c:forEach var="prod" items="${store.value}">
                                        <!--qc data links like this http://localhost:8080/phenotype-archive/alleles/qc_data/es_cell/EPD0386_3_A05/ -->
                                        <tr>
                                            <td></td>
                                            <td>${prod.name} </td>
                                            <td>
                                                <c:if test="${fn:length(prod.qcData)>0}">
                                                    <a class="btn"
                                                       href="${baseUrl}/qcData?type=${type}&productName=${prod.name}&alleleName=${prod.alleleName}&${creLineParam}&bare=true"><i
                                                            class="fa fa-info"></i>QC Data</a>
                                                </c:if>


                                                <c:if test="${fn:length(prod.qcData)==0}">
                                                    No QC Data Available
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                </c:forEach>
                                </tbody>
                            </table>

                        </c:if>
                        <c:if test="${fn:length(storeToProductsMap)==0}">
                            There are no products of type ${type} for this allele this link shouldn't have been available if no products.
                        </c:if>
                        <img alt="image not found!" src="${allele.alleleSimpleImage}">

                        <c:if test="${fn:length(storeToProductsMap)>0}">
                            <p>
                                Mouse strains produced by the IMPC are made available to researchers by depositing them with the KOMP
                                mouse repositiory at the University of California-Davis and/or the European Mutant Mouse Archive
                                maintained by INFRAFRONTIER, Gmbh. IMPC centers may also provide breeders on a colloborative basis based
                                on availability.
                            </p>
                            <p>
                                We provide links and contact information based on tracking data supplied by the IMPC centers, but are
                                not responsible for the ordering process.
                            </p>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>






    </jsp:body>
</t:genericpage>
	
				
    
    
