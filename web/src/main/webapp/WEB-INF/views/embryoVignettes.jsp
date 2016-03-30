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
                            <div id="1" class="inner">
                            <c:set var="geneId" value="MGI:1913761"></c:set>
                            	<%-- <a href="${baseUrl}/genes/${geneId}"> --%><h2 id="${geneId}"><t:formatAllele>Chtop<tm1a(EUCOMM)Wtsi></t:formatAllele></h2><!-- </a> -->
                            	
                            	
                            	<div  class="twothird">
				            		<p>
				            		Chtop has been shown to recruit the histone-methylating methylosome to genomic regions containing 5-Hydroxymethylcytosine, thus affecting gene expression.<br>
				            		
				            		
				            		Chtop mutants showed complete preweaning lethality with no homozygous pups observed. High resolution episcopic microscopy (HREM) imaging at E14.5 revealed multiple phenotypes including edema, abnormal forebrain morphology and decreased number of vertebrae and ribs.<br>
				            		</p>
				            		<p>Phenotype data links</p>
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
				            				3-D imaging: NA (Consider this link: <a href="http://dmdd.org.uk/mutants/Chtop">Images</a>
				            			</li>
				            			<li>
				            				Adult het phenotype data: <a href="${baseUrl}/genes/MGI:1913761#section-associations">Table</a>
				            			</li>
				            			<li>
				            				Placental histopath: <a href="http://dmdd.org.uk/mutants/Chtop">Images</a>
				            			</li>
				            		</ul>
								</div>
                            	<div class="onethird"><a href="${baseUrl}/images?gene_id=MGI:1913761&amp;fq=expName:Embryo%20Dysmorphology"><img alt="Embryo Dysmorphology Image" src="${baseUrl}/img/vignettes/chtopPink.jpg" >Chtop null embryo</a></div>
				            		<div class="clear"></div>					
	                           
	                       
	                           
	                           
	                           
                            </div>
                        </div>

        				

						<div class="section">
							<h2 class="title" id="${geneId}"><t:formatAllele>Klhdc2<tm1b(EUCOMM)Hmgu></t:formatAllele></h2>
                            <div id="2" class="inner">
                            	<div class="twothird">
                         			<p>
                         				The Kldhc2 gene is located within a locus linked to an automsomal dominant disease that leads to fibro-fatty replacement of right ventricle myocardium leading to arrythmias (ARVD3 ; OMIM).<br>
                         				The gene is expressed in <a href="https://www.ebi.ac.uk/gxa/experiments/E-MTAB-3358?accessKey=&amp;serializedFilterFactors=DEVELOPMENTAL_STAGE:adult&amp;queryFactorType=ORGANISM_PART&amp;rootContext=&amp;heatmapMatrixSize=50&amp;displayLevels=false&amp;displayGeneDistribution=false&amp;geneQuery=KLHDC2&amp;exactMatch=true&amp;_exactMatch=on&amp;_queryFactorValues=1&amp;specific=true&amp;_specific=on&amp;cutoff=0.5">heart</a> (expression atlas link) and has been implicated in  <a href="http://www.ncbi.nlm.nih.gov/pubmed/16008511">endothelial differentation</a> and 
                         				<a href="http://www.ncbi.nlm.nih.gov/pubmed/16860314">myoblast differentation</a>. Heterozygote null mice have abnormal heart rhythms while the lethal ​embryos may have a heart defect.
                        			</p>
                        			
                        			<p>Phenotype data links</p>
                        			
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
                            				3-D Imaging: <a href="${drupalBaseUrl}/EmbryoViewerWebApp/?mgi=MGI:1916804">Images</a>
                            			</li> 
                            			<li>
                            				Adult het phenotype data: <a href="${baseUrl}/genes/MGI:1916804">Table</a>
                            			</li>
                            		</ul>
                            		
                            	</div>
                            	<div class="onethird"><a href="${baseUrl}/EmbryoViewerWebApp/?mgi=MGI:1916804"><img alt="E18.5  Klhdc2 null embryo" src="${baseUrl}/img/vignettes/Kldhc2.png" >E18.5 Klhdc2 null embryo</a></div>
								
                            	<div class="clear"></div>
                            </div>
                       	</div>
                       	
                       	
                       	<div class="section">
                       	<c:set var="geneId" value="MGI:102806"></c:set>
							<h2 class="title" id="${geneId}"><t:formatAllele>Acvr2a<tm1.1(KOMP)Vlcg></t:formatAllele></h2>
							
                            <div id="3" class="inner">
                            	<div>
                         			<p>
                         				Activin receptor IIA is a receptor for activins, which are members of the TGF-beta superfamily involved in diverse biological processes.<br>
										Acvr2a mutants are subviable with most pups dying before postnatal day 7. Micro-CT analysis at E15.5 revealed variable penetrance of eye and craniofacial abnormalities. Eye phenotypes varied from normal (Embryo 1- (E1)), to underdeveloped (E2), to cyclopic (E3), to absent (E4). Craniofacial phenotypes varied from normal (E1) to narrow snout (E2), to an elongated  snout missing the mandible and tongue (E3, 4) and low set ears (E2, 3, 4).
                        			</p>
                        			
                        			<p>Phenotype data links</p>
                        			
                            		<ul>
                            			<li>
                            				Viability:<a href="${baseUrl}/charts?accession=${geneId}&allele_accession_id=MGI:5548333&zygosity=homozygote&parameter_stable_id=IMPC_VIA_001_001&pipeline_stable_id=TCP_001&phenotyping_center=TCP" target="_blank">Partial preweaning lethality</a>
                            			</li>
                            			<li>
                            				Embryo LacZ Expression: <a href="${baseUrl}/genes/${geneId}#section-expression">Images</a>
                            			</li>
                            			<li>
                            				Embryo Gross Dysmorphology: <a href="${baseUrl}/imagePicker/MGI:102806/IMPC_GEO_050_001">Images</a>
                            			</li>
                            			<li>
                            				3-D Imaging: <a href="${drupalBaseUrl}/EmbryoViewerWebApp/?mgi=${geneId}">Image</a>
                            			</li> 
                            			<li>
                            				Adult het phenotype data: <a href="${baseUrl}/genes/${geneId}#section-associations">table</a>
                            			</li>
                            			<li>
                            				Embryo Histopathology: <a id="acvr2aHistTrigger" class="various" href="#acvr2aHist">Image</a>
                            			</li>
                            			<li>
                            				Viability at P3/P7: <a id="acvr2aP3Trigger" class="various" href="#acvr2aP3">Lethal</a>
                            			</li>
                            		</ul>
                            		
                            	</div>
                            	<div ><img alt="Micro-CT of E15.5 Acvr2a" src="${baseUrl}/img/vignettes/Acvr2aMicroCT.png" >
								</div>
								
                            	<div class="clear"></div>
                            	
                            	<div id="acvr2aHist" name="acvr2aHist" style="display:none" >
                            		<div class="inner">
                            			<img src="${baseUrl}/img/vignettes/Acvr2aHist.png"/>
                            		</div>
                            		
                            	</div>
                            	
                            	<div id="acvr2aP3" style="display:none" >
                            		<div class="inner">
                            			<img src="${baseUrl}/img/vignettes/acvr2aP3.png"/>
                            		</div>
                            		
                            	</div>
                            </div>
                       	</div>
                       	
                       	
                       	
                       		<div class="section">
                       		<c:set var="geneId" value="MGI:1195985"></c:set>
							<h2 class="title" id="${geneId}"><t:formatAllele>Cbx4<tm1.1(KOMP)Vlcg></t:formatAllele></h2>
							
                            <div class="inner">
                            	<div class="half">
                         			<p>
                         				Chromobox 4 is in the polycomb protein family that are key regulators of transcription and is reported to be upregulated in lung bud formation and required for thymus development.<br>
										Cbx4 mutants showed complete preweaning lethality but were viable at E12.5 and E15.5 with no obvious gross morphological change.
										Micro-CT analysis at E15.5 confirmed that Cbx4tm1.1/tm1.1 mutants  had statistically smaller thymus and also revealed smaller adrenal glands and trigeminal ganglia compared to Cbx4+/+ wildtype embryos.

                        			</p>
                        			
                        			<p>Phenotype data links</p>
                        			
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
                            <c:set var="geneId" value="MGI:1915138"></c:set>
							<h2 class="title" id="${geneId}"><t:formatAllele>Tmem100<tm1e.1(komp)Wtsi></t:formatAllele></h2>
							
                            <div class="inner">
                            	<div class="half">
                         			<p>
                         				Transmembrane Protein 100 functions downstream of the BMP/ALK1 signaling pathway.<br>
										Tmem100 mutants showed complete preweaning lethality and were also lethal at E12.5.
										LacZ staining in E12.5 Het embryos was found predominantly in arterial endothelial cells and the heart (arrow) .
										OPT analysis at E9.5 revealed that Tmem100 mutant embryos have a large pericardial effusion with cardiac dysmorphology and enlargement (arrow).

                        			</p>
                        			
                        			<p>Phenotype data links</p>
                        			
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
                            
                            
                            <div class="section">
                            <c:set var="geneId" value="MGI:1915138"></c:set>
							<h2 class="title" id="${geneId}"><t:formatAllele>Eya4
<tm1b(KOMP)Wtsi></t:formatAllele></h2>
							
                            <div class="inner">
                            	<div class="half">
                         			<p>
                         				Eyes absent transcriptional coactivator and phosphatase 4 is associated with a variety of developmental defects including hearing loss.<br>
										Eya4 mutants showed complete preweaning lethality with no homozygous pups observed.
										Micro-CT analysis at E15.5 revealed Eya tm1b/tm1b mutant embryos had bi-lateral smaller cochlear volumes as well as a smaller thyroid gland, Meckel’s cartilage, trachea (opening), cricoid cartilage, and arytenoid cartilage.

                        			</p>
                        			
                        			<p>Phenotype data links</p>
                        			
                            		<ul>
                            			<li>
                            				Viability:<a href="${baseUrl}/charts?accession=${geneId}&allele_accession_id=MGI:5548437&zygosity=homozygote&parameter_stable_id=IMPC_VIA_001_001&pipeline_stable_id=TCP_001&phenotyping_center=TCP" target="_blank">Complete preweaning lethality</a>
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
                            	<div class="half"><img alt=Automated MRI analysis of E15.5 Eya4tm1b/tm1b mutants" src="${baseUrl}/img/vignettes/eye4.png" >Automated MRI analysis of E15.5 Eya4tm1b/tm1b mutants showed that mutant embryos had a statistically smaller volumes of the cochlea and other tissues compared to Eya4+/+ wildtype embryos as highlighted in blue in transverse, coronal, and sagittal sections (false discovery rate (FDR) threshold of 5%).

								</div>
								
                            	<div class="clear"></div>
                            	
                            	
                            	<div class="inner">
								<div><img alt="Lac Z staining at E12.5" src="${baseUrl}/img/vignettes/eye4Lac.png" >
								<p>Lac Z staining at E12.5 showed that Eya4 expression is primarily in the craniofacial mesenchyme, cochleae and outer ear, dermamyotome, and limb.
								</p>

								</div>
								</div>
								<div class="inner">
                            	<img src="${baseUrl}/img/vignettes/eya4LacSlides.png"/>
                            	<p>
                            	H&E stained sagittal section through the right cochlea of an Eya4+/+ wildtype embryo compared to an Eya4tm1b/tm1b mutant embryo indicated that the mutant embryo had a hypoplastic cochlea. Higher magnification of the region (indicated by the white box) showed abnormal perilymphatic (periotic) mesenchyme (PM) in the mutant embryo compared to the wildtype embryo. In the wildtype embryo the perilymphatic mesenchyme (PM) was rarefied and had multifocal vacuolation (arrow) suggesting normal perilymph development. In the mutant embryo the perilymphatic mesenchyme (PM) did not show rarefaction and had reduced vacuolation (arrow) suggesting the cochlear hypoplasia was due to delayed perilymph development. 
BL-Bony Labyrinth (cartilage at E15.5), PM-Perilymphatic (periotic) mesenchyme, ML-Membranous Labyrinth, EN-Endolymph
                            	</p>
                            	
                            	</div>
                            	
                            	
								</div>
								
								
                            </div>
                            
                            
                            <div class="section">
                            <c:set var="geneId" value="MGI:3039593"></c:set>
                            <div class="inner">
                            	<h2 id="${geneId}"><t:formatAllele>Tox3
<tm1b(KOMP)Mbp></t:formatAllele></h2></h2>
                            	
                            	
                            	<div  class="half">
				            		<p>
				            		Tox High Mobility Group Box Family Member 3 is a member of the HMG-box family involved in bending and unwinding DNA.
Tox3 mutants have partial preweaning lethality with 1/3 of the pups dying before P7.
Whole brain MRI at P7 revealed that Tox3tm1b/tm1b mutants had a much smaller cerebellum and pontine nucleus (blue) compared to the Tox3+/+ wildtype mice and larger amygdala, thalamus, pons, respectively (red).
</p>
				            		
				            		Phenotype data links
				            		<ul>
				            			<li>
				            				Viability: <a href="${baseUrl}/charts?accession=${geneId}&amp;allele_accession_id=MGI:5548886&amp;zygosity=homozygote&amp;parameter_stable_id=IMPC_VIA_001_001&amp;pipeline_stable_id=MGP_001&amp;phenotyping_center=WTSI" target="_blank">Partial preweaning lethality</a>
				            			</li>
				            			<li>
				            				Viability at P3/P7: Subviable
				            			</li>
				            			<li>
				            				Embryo Lacz Expression:  NA
				            			</li>
				            			<li>
				            				Embryo Gross Dysmorphology: NA
				            			</li>
				            			<li>
				            				3-D imaging: NA
				            			</li>
				            			<li>
				            				Adult het phenotype data: <a href="${baseUrl}/genes/${geneId}#section-associations">table</a>
				            			</li>
				            		</ul>
								</div>
                            	<div class="half"><img alt="Tox3 MRI" src="${baseUrl}/img/vignettes/tox3MRI.png" >Caudal to rostral coronal sections of whole brain MRI with automated volume analysis revealed P7 Tox3tm1b/tm1b mutant mice had smaller (blue) and larger (red) tissues compared to the Tox3+/+ wildtype average.
</div>
				            		<div class="clear"></div>		
				            		
				            		
				            	<div class="inner">
				            		<h3>P3/P7 viability test Tox3</h3>
				    
				            		<img src="${baseUrl}/img/vignettes/Tox3Table.png">		
	                           	</div>
	                           	
	                       <div class="inner">
	                       <img src="${baseUrl}/img/vignettes/Tox3Sections.png">
	                       <p>
	                       H&E stained coronal section through the brain of a Tox3+/+ wildtype embryo compared to a Tox3tm1b/tm1b mutant embryo indicated that the mutant embryo had a hypoplastic and dysplastic cerebellum (CE) with markedly reduced fissure formation. Higher magnification revealed that the transient external granular layer was absent in the Tox3tm1b/tm1b mutant  mice and the subjacent molecular layer was hypotrophic and irregular in thickness (arrow).
	                       </p>
	                       </div>
	                           
	                           
	                           
                            </div>
                        </div>
                        
                        
                        
                        
                        
                            <div class="section">
                            <c:set var="geneId" value="MGI:1922814"></c:set>
                            <div class="inner">
                            	<h2 id="${geneId}"><t:formatAllele>Rsph9
<tm1.1(KOMP)Vlcg></t:formatAllele></h2></h2>
                            	
                            	
                            	<div  class="half">
				            		<p>
				            		Radial spoke head protein 9 is a component of the radial spoke head in motile cilia and flagella.
Rsph9 mutants showed partial pre-weaning lethality but viable to P7.
Whole brain MRI and H&E staining of coronal sections of the P7 brain revealed severe hydrocephaly of the left and right lateral ventricles of the Rsph9 mutant.
Coronal section through the nasal region showed that the sinuses of the Rsph9 mutants were filled with pus (asterisks).
Both hydrocephaly and nasal blockage are phenotypes associated with Primary Ciliary Dyskinesia in humans.
</p>
				            		
				            		<p>Phenotype data links</p>
				            		<ul>
				            			<li>
				            				Viability: <a href="${baseUrl}/charts?accession=${geneId}&amp;allele_accession_id=MGI:5695930&amp;zygosity=homozygote&amp;parameter_stable_id=IMPC_VIA_001_001&amp;pipeline_stable_id=MGP_001&amp;phenotyping_center=WTSI" target="_blank">Partial preweaning lethality</a>
				            			</li>
				            			<li>
				            				Viability at P3/P7: Viable
				            			</li>
				            			<li>
				            				Embryo Lacz Expression:  NA
				            			</li>
				            			<li>
				            				Embryo Gross Dysmorphology: <a href="${baseUrl}/images?gene_id=${geneId}&amp;fq=expName:Embryo%20Dysmorphology">Images</a>
				            			</li>
				            			<li>
				            				3-D imaging: <a href="${drupalBaseUrl}/EmbryoViewerWebApp/?mgi=${geneId}">Image</a>
				            			</li>
				            			<li>
				            				Histopathology: <a href="">Images</a><!-- We have no histopath images for this gene yet?? -->
				            			</li>
				            			<li>
				            				Adult het phenotype data: <a href="${baseUrl}/genes/${geneId}#section-associations">table</a>
				            			</li>
				            		</ul>
								</div>
                            	<div class="half"><img alt="H&E stained Rsph9" src="${baseUrl}/img/vignettes/Rsph9Slides.png" >H&E stained coronal sections of P7 mice revealed enlarged ventricles and blocked sinuses in the Rsph9tm1.1/tm1.1 mutant mice. 
</div>
				            		<div class="clear"></div>		
				            		
				            		
				            	<div class="inner">
				            		<h3>P3/P7 viability test Rsph9</h3>
				    
				            		<img src="${baseUrl}/img/vignettes/Rsph9Table.png">		
	                           	</div>
	                           	
	                       <div class="inner">
	                       <img src="${baseUrl}/img/vignettes/Rsph9MRI.png">
	                       <p>
	                       Coronal sections of whole brain MRI showed enlarged ventricles in P7 Rsph9tm1.1/tm1.1 mutant mice.  P7 Rsph9 tm1.1/tm1.1 mice brains had enlarged left and right lateral ventricles (arrows) when sectioned virtually from rostral to caudal and compared to the Rsph9+/+ wildtype average.
	                       </p>
	                       </div>
	                           
	                           
	                           
                            </div>
                        </div>
                        
                        
                        
                        
                        <div class="section">
                        <c:set var="geneId" value="MGI:97491"></c:set>
							<h2 class="title" id="${geneId}"><t:formatAllele>Pax7<tm1.1(KOMP)Vlcg></t:formatAllele></h2>
							
                            <div class="inner">
                            	<div class="half">
                         			<p>
                         				Pax 7 is a nuclear transcription factor with DNA-binding activity via its paired domain.<br>
                         				It is involved in specification of the neural crest and is an upstream regulator of myogenesis during post-natal growth and muscle regeneration in the adult.
										Pax7 mutants showed complete preweaning lethality.
										Micro-CT analysis at E15.5 revealed voxel-wise local volume differences with a larger nasal septum, cavity and capsule (False Discovery Rate <5%) in the E15.5 Pax7tm1.1/tm1.1 mutant embryos compared the wildtype embryos.
										LacZ staining at E12.5 showed very strong staining in the medial region of the frontonasal prominence (arrows) where structural changes  were found. 
										LacZ staining was also seen in the midbrain, hindbrain, spinal cord, vertebrae, ribs and neural crest. 
									</p>
                        			
                        			<p>Phenotype data links</p>
                        			
                            		<ul>
                            			<li>
                            				Viability:<a href="${baseUrl}/charts?accession=${geneId}&allele_accession_id=allele_accession_id=MGI:5505636&zygosity=homozygote&parameter_stable_id=IMPC_VIA_001_001&pipeline_stable_id=TCP_001&phenotyping_center=TCP" target="_blank">Complete preweaning lethality</a>
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
                            	<div class="half"><img alt="MicroCT Pax7 embryos and LacZ Staining" src="${baseUrl}/img/vignettes/Pax7.png" >Micro-CT analysis of E15.5 Pax7 embryos and lacZ staining of E12.5 embryos indicating volume changes and staining in the nasal area.

								</div>
								
                            	<div class="clear"></div>
                            	
								</div>
                            </div>
                        
                        
                        
                            
                            
                            
                       	</div>
                       	
                          
                      
                    <!--end of node wrapper should be after all secions  -->
                </div>
            </div>
        </div>
        
        



	
	<script type="text/javascript">
    $("#acvr2aHistTrigger").fancybox();
    
    $("#acvr2aP3Trigger").fancybox();
</script>


      </jsp:body>

</t:genericpage>

