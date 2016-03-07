<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Embryo Landing Page</jsp:attribute>
	<jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">
	</jsp:attribute>          



	<jsp:attribute name="header">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/slider.js?v=${version}'></script> 
        <link rel="stylesheet" href='${baseUrl}/css/slider.css?v=${version}'/>        
    </jsp:attribute>

    <jsp:body>
        <div class="region region-content">
            <div class="block">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">Embryo Vignettes</h1>

                        <div class="section">
                            <div class="inner">
                            	<h2>Chtop</h2>
                            	<c:set var="geneId" value="MGI:1913761"></c:set>
                            	
                            	<div  class="twothird">
				            		<p>
				            		Chtop has been shown to recruit the histone-methylating methylosome to genomic regions containing 5-Hydroxymethylcytosine, thus affecting gene expression.
				            		</p>
				            		<p>
				            		Chtop mutants showed complete preweaning lethality with no homozygous pups observed. High resolution episcopic microscopy (HREM) imaging, revealed decreased number of vertebrae, abnormal joint morphology and edema.
				            		</p>
				            		Phenotype data  links
				            		<ul>
				            			<li>
				            				Viability: <a href="${baseUrl}/charts?accession=MGI:1913761&amp;allele_accession_id=MGI:4842477&amp;zygosity=homozygote&amp;parameter_stable_id=IMPC_VIA_001_001&amp;pipeline_stable_id=MGP_001&amp;phenotyping_center=WTSI" target="_blank">Complete preweaning lethality</a>
				            			</li>
				            			<li>
				            				Embryo Lacz Expression:  NA
				            			</li>
				            			<li>
				            				Embryo Gross Dysmorphology: <a href="${baseUrl}/images?gene_id=MGI:1913761&amp;fq=expName:Embryo%20Dysmorphology">Images</a>
				            			</li>
				            			<li>
				            				3-D imaging: NA (Consider this link: <a href="http://dmdd.org.uk/stacks/DMDD5009/xy">http://dmdd.org.uk/stacks/DMDD5009/xy</a>
				            			</li>
				            			<li>
				            				Adult het phenotype data: <a href="${baseUrl}/genes/MGI:1913761#section-associations">table</a>
				            			</li>
				            		</ul>
								</div>
                            	<div class="onethird"><a href="${baseUrl}/images?gene_id=MGI:1913761&amp;fq=expName:Embryo%20Dysmorphology"><img alt="Embryo Dysmorphology Image" src="${baseUrl}/img/vignettes/chtopPink.jpg" >Chtop null embryo</a></div>
				            		<div class="clear"></div>					
	                           
	                       
	                           
	                           
	                           
                            </div>
                        </div>

        				

						<div class="section">
							<h2 class="title">Kldhc2- heart defect linked to disease</h2>
                            <div class="inner">
                            	<div class="twothird">
                         			<p>
                         				The Kldhc2 gene is located within a locus linked to an automsomal dominant disease that leads to fibro-fatty replacement of right ventricle myocardium leading to arrythmias (ARVD3 ; OMIM) The gene is expressed in <a href="https://www.ebi.ac.uk/gxa/experiments/E-MTAB-3358?accessKey=&amp;serializedFilterFactors=DEVELOPMENTAL_STAGE:adult&amp;queryFactorType=ORGANISM_PART&amp;rootContext=&amp;heatmapMatrixSize=50&amp;displayLevels=false&amp;displayGeneDistribution=false&amp;geneQuery=KLHDC2&amp;exactMatch=true&amp;_exactMatch=on&amp;_queryFactorValues=1&amp;specific=true&amp;_specific=on&amp;cutoff=0.5">heart</a> (expression atlas link) and has been implicated in  <a href="http://www.ncbi.nlm.nih.gov/pubmed/16008511">endothelial differentation</a> and 
                         				<a href="http://www.ncbi.nlm.nih.gov/pubmed/16860314">myoblast differentation</a>. Heterozygote null mice have abnormal heart rhythms while the lethal​embryos may have a heart defect.
                        			</p>
                        			<p>
                        			Phenotype data  links
                        			</p>
                            		<ul>
                            			<li>
                            				Viability:<a href="${baseUrl}/charts?accession=MGI:1916804&amp;allele_accession_id=MGI:5548587&amp;zygosity=homozygote&amp;parameter_stable_id=IMPC_VIA_001_001&amp;pipeline_stable_id=HRWL_001&amp;phenotyping_center=MRC%20Harwell" target="_blank">Complete preweaning lethality</a>
                            			</li>
                            			<li>
                            				Embryo LacZ Expression: NA
                            			</li>
                            			<li>
                            				Embryo Gross Dysmorphology: NA
                            			</li>
                            			<li>
                            				3-D Imaging: <a href="${drupalBaseUrl}/EmbryoViewerWebApp/?mgi=MGI:1916804">Image</a>
                            			</li> 
                            			<li>
                            				Adult het phenotype data: <a href="${baseUrl}/genes/MGI:1916804">table</a>
                            			</li>
                            		</ul>
                            		
                            	</div>
                            	<div class="onethird"><a href="${baseUrl}/EmbryoViewerWebApp/?mgi=MGI:1916804"><img alt="E18.5  Klhdc2 null embryo" src="${baseUrl}/img/vignettes/Kldhc2.png" >E18.5 Klhdc2 null embryo</a></div>
								
                            	<div class="clear"></div>
                            </div>
                       	</div>
                       	
                       	
                       	<div class="section">
							<h2 class="title"><t:formatAllele>Acvr2a<tm1.1(KOMP)Vlcg></t:formatAllele></h2>
							<c:set var="geneId" value="MGI:102806"></c:set>
                            <div class="inner">
                            	<div class="half">
                         			<p>
                         				Activin receptor IIA is a receptor for activins, which are members of the TGF-beta superfamily involved in diverse biological processes.
										Acvr2a mutants are subviable with most pups dying before postnatal day 7. Micro-CT analysis at E15.5 revealed variable penetrance of eye and craniofacial abnormalities. Eye phenotypes varied from normal (Embryo 1- (E1)), to underdeveloped (E2), to cyclopic (E3), to absent (E4). Craniofacial phenotypes varied from normal (E1) to narrow snout (E2), to an elongated  snout missing the mandible and tongue (E3, 4) and low set ears (E2, 3, 4).
<a href="http://www.ncbi.nlm.nih.gov/pubmed/16860314">myoblast differentation</a>. Heterozygote null mice have abnormal heart rhythms while the lethal​embryos may have a heart defect.
                        			</p>
                        			<p>
                        			Phenotype data  links
                        			</p>
                            		<ul>
                            			<li>
                            				Viability:<a href="${baseUrl}/charts?accession=${geneId}&allele_accession_id=MGI:5548333&zygosity=homozygote&parameter_stable_id=IMPC_VIA_001_001&pipeline_stable_id=TCP_001&phenotyping_center=TCP" target="_blank">Partial preweaning lethality</a>
                            			</li>
                            			<li>
                            				Embryo LacZ Expression: <a href="${baseUrl}/genes/${geneId}#section-expression">Images</a>
                            			</li>
                            			<li>
                            				Embryo Gross Dysmorphology: NA
                            			</li>
                            			<li>
                            				3-D Imaging: <a href="${drupalBaseUrl}/EmbryoViewerWebApp/?mgi=${geneId}">Image</a>
                            			</li> 
                            			<li>
                            				Adult het phenotype data: <a href="${baseUrl}/genes/${geneId}#section-associations">table</a>
                            			</li>
                            		</ul>
                            		
                            	</div>
                            	<div class="half"><img alt="Micro-CT of E15.5 Acvr2a" src="${baseUrl}/img/vignettes/Acvr2a.png" >Micro-CT analysis of E15.5 Acvr2a wildtype embryo compared to four  mutant embryos with variable penetrance of  eye and craniofacial abnormalities.
								</div>
								
                            	<div class="clear"></div>
                            	
                            	<div >
                            	<img src="${baseUrl}/img/vignettes/Acvr2aHist.png"/>
                            	H&E stained sagittal section of an E15.5 Acvr2a+/+ wildtype embryo compared to an E15.5 Acvr2atm1.1/tm1.1 mutant embryo showing that the tongue and mandible are missing in the mutant embryo (arrow).
                            	
                            	</div>
                            </div>
                       	</div>
                       	
                       	
                       	
                       		<div class="section">
							<h2 class="title"><t:formatAllele>Cbx4<tm1.1(KOMP)Vlcg></t:formatAllele></h2>
							<c:set var="geneId" value="MGI:1195985"></c:set>
                            <div class="inner">
                            	<div class="half">
                         			<p>
                         				Chromobox 4 is in the polycomb protein family that are key regulators of transcription and is reported to be upregulated in lung bud formation and required for thymus development.
Cbx4 mutants showed complete preweaning lethality but were viable at E12.5 and E15.5 with no obvious gross morphological change.
Micro-CT analysis at E15.5 confirmed that Cbx4tm1.1/tm1.1 mutants  had statistically smaller thymus and also revealed smaller adrenal glands and trigeminal ganglia compared to Cbx4+/+ wildtype embryos.

                        			</p>
                        			<p>
                        			Phenotype data  links
                        			</p>
                            		<ul>
                            			<li>
                            				Viability:<a href="${baseUrl}/charts?accession=${geneId}&allele_accession_id=MGI:5548407&zygosity=homozygote&parameter_stable_id=IMPC_VIA_001_001&pipeline_stable_id=TCP_001&phenotyping_center=TCP" target="_blank">Complete preweaning lethality</a>
                            			</li>
                            			<li>
                            				Embryo LacZ Expression: <a href="${baseUrl}/genes/${geneId}#section-expression">Images</a>
                            			</li>
                            			<li>
                            				Embryo Gross Dysmorphology: NA
                            			</li>
                            			<li>
                            				3-D Imaging: <a href="${drupalBaseUrl}/EmbryoViewerWebApp/?mgi=${geneId}">Image</a>
                            			</li> 
                            			<li>
                            				Adult het phenotype data: <a href="${baseUrl}/genes/${geneId}#section-associations">table</a>
                            			</li>
                            		</ul>
                            		
                            	</div>
                            	<div class="half"><img alt="Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1" src="${baseUrl}/img/vignettes/cbx4.png" >Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1 mutants viewed in coronal section revealed that mutant embryos had bilateral smaller trigeminal ganglia, thymus, and adrenal glands compared to Cbx4+/+ wildtype embryos as indicated by blue colour and highlighted by pink arrows (False Discovery Rate (FDR) threshold of 5%).
</div>
								
                            	<div class="clear"></div>
                            	<div class="inner">
                       					<div class="half"><img alt="Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1" src="${baseUrl}/img/vignettes/cbxVol1.png"  >
                       					</div>
                       					<div><img class="half" alt="Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1" src="${baseUrl}/img/vignettes/cbxVol2.png">
                       					</div>
           								<div class="clear"></div>
                       					<p>Whole structural volume differences calculated as a percentage of whole body	volume for the left and right thymic rudiment (left) and left and right adrenal (right) of Cbx4tm1.1/tm1.1 mutant embryos compared to Cbx4+/+ wildtype embryos.  Both organs are significantly smaller in the Cbx4 mutant embryos at an FDR threshold of 5% where the error bars represent 95% confidence intervals.
                       					</p>
                       			</div>	
								</div>
                            </div>
                            
                            
                            
                            <div class="section">
							<h2 class="title"><t:formatAllele>Tmem100<tm1e.1(komp)Wtsi></t:formatAllele></h2>
							<c:set var="geneId" value="MGI:1915138"></c:set>
                            <div class="inner">
                            	<div class="half">
                         			<p>
                         				Transmembrane Protein 100 functions downstream of the BMP/ALK1 signaling pathway.
Tmem100 mutants showed complete preweaning lethality and were also lethal at E12.5.
LacZ staining in E12.5 Het embryos was found predominantly in arterial endothelial cells and the heart (arrow) .
OPT analysis at E9.5 revealed that Tmem100 mutant embryos have a large pericardial effusion with cardiac dysmorphology and enlargement (arrow).

                        			</p>
                        			<p>
                        			Phenotype data  links
                        			</p>
                            		<ul>
                            			<li>
                            				Viability:<a href="${baseUrl}/charts?accession=${geneId}&allele_accession_id=NULL-73D315493&zygosity=homozygote&parameter_stable_id=IMPC_VIA_001_001&pipeline_stable_id=TCP_001&phenotyping_center=TCP" target="_blank">Complete preweaning lethality</a>
                            			</li>
                            			<li>
                            				Embryo LacZ Expression: <a href="${baseUrl}/genes/${geneId}#section-expression">Images</a>
                            			</li>
                            			<li>
                            				Embryo Gross Dysmorphology: NA
                            			</li>
                            			<li>
                            				3-D Imaging: <a href="${drupalBaseUrl}/EmbryoViewerWebApp/?mgi=${geneId}">Image</a>
                            			</li> 
                            			<li>
                            				Adult het phenotype data: <a href="${baseUrl}/genes/${geneId}#section-associations">table</a>
                            			</li>
                            		</ul>
                            		
                            	</div>
                            	<div class="half"><img alt="Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1" src="${baseUrl}/img/vignettes/tmem100.png" >OPT analysis of E9.5 Tmem100 wildtype embryo to a Tmem100tm1e.1/tm1e.1  mutant embryo and lacZ staining in an E12.5 Tmem100+/tm1e.1 embryo.
								</div>
								<div><img alt="Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1" src="${baseUrl}/img/vignettes/tmem100GM.png" >Gross morphology at E9.5 revealed that Tmem100tm1e.1/tm1e.1 mutant embryos have a large pericardial effusion with cardiac dysmorphology and enlargement (arrow).
								</div>
								
                            	<div class="clear"></div>
                            	
								</div>
                            </div>
                            
                            
                            
                       	</div>
                       	
                          
                      
                    <!--end of node wrapper should be after all secions  -->
                </div>
            </div>
        </div>
        
        



	

      </jsp:body>

</t:genericpage>

