<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">${pageTitle} landing page | IMPC Phenotype Information</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/landing">Landing
        Pages</a> &nbsp;&raquo; ${pageTitle}</jsp:attribute>

    <jsp:attribute name="header">

	<!-- CSS Local Imports -->
	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>

	</jsp:attribute>


    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="addToFooter">



	</jsp:attribute>
    <jsp:body>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">${pageTitle} </h1>

                        <div class="section">
                            <div class="inner">
                                <p> We have undertaken a deafness screen in the IMPC cohort of mouse knockout strains. We
                                    detected known deafness genes and the vast majority of loci were novel. </p>
                                <br/> <br/>

                                <c:if test="${genePercentage.getDisplay()}">

                                    <div class="half">
                                        <div id="pieChart">
                                            <script type="text/javascript">${genePercentage.getPieChartCode()}</script>
                                        </div>

                                        <c:if test="${genePercentage.getTotalGenesTested() > 0}">
                                            <p><span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of the
                                                tested genes with null mutations on a B6N genetic background have related phenotype
                                                associations
                                                (${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()})
                                            </p>
                                        </c:if>
                                        <c:if test="${genePercentage.getFemaleGenesTested() > 0}">
                                            <p>
                                                        <span class="padleft"><span class="bigger">
                                                            ${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </span>
                                            </p>
                                        </c:if>
                                        <c:if test="${genePercentage.getMaleGenesTested() > 0}">
                                            <p>
                                                    <span class="padleft"><span
                                                            class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</span>
                                            </p>
                                        </c:if>
                                    </div>


                                    <div class="half">

                                        <table>
                                            <thead>
                                            <tr> <th class="headerSort"> Phenotype </th> <th> # Associations </th> </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="row" items="${phenotypes}">
                                            <tr>
                                                <td><h4 class="capitalize">${row.category}</h4></td>
                                                <c:if test="${row.mpId != null}">
                                                    <td><h4><a href="${baseUrl}/phenotypes/${row.mpId}">${row.count}</a></h4></td>
                                                </c:if>
                                                <c:if test="${row.mpId == null}">
                                                    <td><h4>${row.count}</h4></td>
                                                </c:if>
                                            </tr>
                                            </c:forEach>
                                            <tr>
                                                <td><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/viabilityReport.csv" style="text-decoration:none;" download> <i class="fa fa-download" alt="Download"> Download</i></a></td>
                                                <td></td>
                                            </tr>
                                        </tbody>
                                        </table>

                                    </div>

                                    <div class="clear both"></div>
                                </c:if>
                            </div>
                        </div>

                        <div class="section">
                            <div class="inner">
                                <h1>Approach</h1>
                                <p>In order to identify genes required for hearing function, the consortium uses an
                                    auditory brainstem response (ABR) test in the adult pipeline at week 14 that
                                    assesses hearing at five frequencies – 6kHz, 12kHz, 18kHz, 24kHz and 30kHz – as well
                                    as a broadband click stimulus. The consortium aimed to analyse a minimum of 4 mutant
                                    mice for each gene and, in most cases, mutant males and females were analysed.</p>
                                <p>For the statistical analysis of the IMPC ABR dataset, we used a reference range
                                    approach with the aim of eliminating false positives (see Methods). Briefly, we used
                                    the total set of matched baseline control data from wild-type C57BL/6N mice that is
                                    generated at each IMPC centre to establish a reference range. For each mutant a
                                    contingency table is employed for both appropriate wild-type control mice and
                                    mutants, and a Fisher’s exact test performed to identify if mutants deviate
                                    significantly from the wild-type distribution. We determined a suitable reference
                                    range and critical p value by the examination of known deafness genes, and selected
                                    a stringent 98% reference range and p value of 0.01 for the initial selection of
                                    putative deafness loci. </p>
                                <p>Details of the experimental design of <a
                                        href="https://www.mousephenotype.org/impress/protocol/176/7"> acoustic startle
                                    and Pre-pulse Inhibition </a> and <a
                                        href="https://www.mousephenotype.org/impress/protocol/149/7">Auditory Brain Stem
                                    Response</a> are available on IMPRESS.</p>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>


