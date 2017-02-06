<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">IDG | IMPC Project Information</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/IDG">IDG</a> &raquo; IDG</jsp:attribute>
    <jsp:attribute name="bodyTag">
		<body class="chartpage no-sidebars small-header">
	</jsp:attribute>
    <jsp:attribute name="header">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
    </jsp:attribute>

    <jsp:attribute name="addToFooter">

		<div class="region region-pinned">
            <div id="flyingnavi" class="block smoothScroll">
                <a href="#top"><i class="fa fa-chevron-up"
                                  title="scroll to top"></i></a>
                <ul>
                    <li><a href="#top">IDG</a></li>
                </ul>
                <div class="clear"></div>
            </div>
        </div>
		
    </jsp:attribute>


    <jsp:body>
        <!-- Assign this as a variable for other components -->
        <script type="text/javascript">
            var base_url = '${baseUrl}';
        </script>
				
						<div class="section">
							<div class=inner>
								
									<h3>IDG Human-Mouse Orthology Mapping</h3>
									<p>
Orthologous genes between human and mouse were mapped using <a href="https://www.ncbi.nlm.nih.gov/homologene">HomoloGene</a>. 89% of human IDG genes had mouse orthologs. 11% of human IDG genes did not have a mouse ortholog.
								</p>
									
									<div  class="half">
										<div id="idgHumanOrthologPie">
			            					<script type="text/javascript">
													${idgHumanOrthologPie}
											</script> 
										</div>       	
									</div>
									
									<div class="half">
									<table>
									<tr><th>Human Genes With No Mouse Ortholog</th></tr>
										<tr><td>ADCK4</td></tr><tr><td>ADGRD2</td></tr><tr><td>ADGRE2</td></tr><tr><td>ADGRE3</td></tr><tr><td>BEST4</td></tr><tr><td>CLCA3P</td></tr><tr><td>CLIC2</td></tr><tr><td>FXYD6P3</td></tr><tr><td>GNRHR2</td></tr><tr><td>GPR32</td></tr><tr><td>GPR32P1</td></tr><tr><td>GPR42</td></tr><tr><td>GPR78</td></tr><tr><td>HTR1E</td></tr><tr><td>HTR3C</td></tr><tr><td>HTR3D</td></tr><tr><td>HTR3E</td></tr><tr><td>KCNJ18</td></tr><tr><td>MAS1L</td></tr><tr><td>NPBWR2</td></tr><tr><td>NPY6R</td></tr><tr><td>OPN1MW2</td></tr><tr><td>OXER1</td></tr><tr><td>P2RY11</td></tr><tr><td>PAK7</td></tr><tr><td>PRKACG</td></tr><tr><td>PRKY</td></tr><tr><td>PSKH2</td></tr><tr><td>SCNN1D</td></tr><tr><td>SGK223</td></tr><tr><td>STK17A</td></tr><tr><td>TAS2R19</td></tr><tr><td>TAS2R20</td></tr><tr><td>TAS2R30</td></tr><tr><td>TAS2R43</td></tr><tr><td>TAS2R5</td></tr><tr><td>TAS2R50</td></tr><tr><td>TAS2R8</td></tr><tr><td>TAS2R9</td></tr><tr><td>VN1R1</td></tr><tr><td>VN1R17P</td></tr><tr><td>VN1R3</td></tr><tr><td>VN1R5</tr>
									</table>
									</div>
									 <div class="clear"></div>   
									<%-- <table>
									
										<thead>
											<tr >
												<th style="border-bottom: none"></th>
												<th style="border-bottom: none"></th>
												<th colspan="2">human-mouse ortholog relationship</th>
												<th style="border-bottom: none"></th>
											</tr>
											<tr>
												<th>Family</th>
												<th>Total Number</th>
												<th>One to One</th>
												<th>One to Many</th>
												<th>Human Genes with No Orthologs</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td>GPCRs</td>
												<td>143</td>
												<td>113</td>
												<td>3</td>
												<td>27</td>
											</tr>
											<tr>
												<td>Kinases</td>
												<td>134</td>
												<td>127</td>
												<td>0</td>
												<td>7</td>
											</tr>
											<tr>
												<td>Ion Channels</td>
												<td>118</td>
												<td>107</td>
												<td>2</td>
												<td>9</td>
											</tr>
										</tbody>
									
									</table> --%>
							</div>
						</div>
				
 
    </jsp:body>


</t:genericpage>
