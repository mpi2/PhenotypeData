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
                            	
                            	<div  class="half">
				            		<p>
				            		Chtop has been shown to recruit the histone-methylating methylosome to genomic regions containing 5-Hydroxymethylcytosine, thus affecting gene expression.
				            		</p>
				            		<p>
				            		Chtop mutants showed complete preweaning lethality with no homozygous pups observed. High resolution episcopic microscopy (HREM) imaging, revealed decreased number of vertebrae, abnormal joint morphology and edema.
				            		</p>
				            		Phenotype data  links
				            		<ul>
				            			<li>
				            				Viability: <a href="http://www.mousephenotype.org/data/charts?accession=MGI:1913761&amp;allele_accession_id=MGI:4842477&amp;zygosity=homozygote&amp;parameter_stable_id=IMPC_VIA_001_001&amp;pipeline_stable_id=MGP_001&amp;phenotyping_center=WTSI" target="_blank">Complete preweaning lethality</a>
				            			</li>
				            			<li>
				            				Embryo Lacz Expression:  NA
				            			</li>
				            			<li>
				            				Embryo Gross Dysmorphology: <a href="http://dev.mousephenotype.org/data/images?gene_id=MGI:1913761&amp;fq=expName:Embryo%20Dysmorphology">Images</a>
				            			</li>
				            			<li>
				            				3-D imaging: NA (Consider this link: <a href="http://dmdd.org.uk/stacks/DMDD5009/xy">http://dmdd.org.uk/stacks/DMDD5009/xy</a>
				            			</li>
				            			<li>
				            				Adult het phenotype data: <a href="http://dev.mousephenotype.org/data/genes/MGI:1913761#section-associations">table</a>
				            			</li>
				            		</ul>
								</div>
                            	<div class="half" style="width: 300px; font-size:80%; text-align:center;"><a href="http://dev.mousephenotype.org/data/images?gene_id=MGI:1913761&amp;fq=expName:Embryo%20Dysmorphology"><img alt="Embryo Dysmorphology Image" src="https://wwwdev.ebi.ac.uk/mi/impc/dev/phenotype-archive/media/images/0/MFUD39.1g__download_tn_large.jpg" style="width: 300px; height: 276px;padding-bottom:0.5em;">Chtop null embryo</a></div>
				            		<div class="clear"></div>					
	                           
	                       
	                           
	                           
	                           
                            </div>
                        </div>

        				

						<div class="section">
							<h2 class="title">Kldhc2- heart defect linked to disease</h2>
                            <div class="inner">
                            	<div class="half">
                         			<p>
                         				The Kldhc2 gene is located within a locus linked to an automsomal dominant disease that leads to fibro-fatty replacement of right ventricle myocardium leading to arrythmias (ARVD3 ; OMIM) The gene is expressed in <a href="https://www.ebi.ac.uk/gxa/experiments/E-MTAB-3358?accessKey=&amp;serializedFilterFactors=DEVELOPMENTAL_STAGE:adult&amp;queryFactorType=ORGANISM_PART&amp;rootContext=&amp;heatmapMatrixSize=50&amp;displayLevels=false&amp;displayGeneDistribution=false&amp;geneQuery=KLHDC2&amp;exactMatch=true&amp;_exactMatch=on&amp;_queryFactorValues=1&amp;specific=true&amp;_specific=on&amp;cutoff=0.5">heart</a> (expression atlas link) and has been implicated in  <a href="http://www.ncbi.nlm.nih.gov/pubmed/16008511">endothelial differentation</a> and 
                         				<a href="http://www.ncbi.nlm.nih.gov/pubmed/16860314">myoblast differentation</a>. Heterozygote null mice have abnormal heart rhythms while the lethal​embryos may have a heart defect.
                        			</p>
                        			Phenotype data  links
                            		<ul>
                            			<li>
                            				Viability:<a href="http://dev.mousephenotype.org/data/charts?accession=MGI:1916804&amp;allele_accession_id=MGI:5548587&amp;zygosity=homozygote&amp;parameter_stable_id=IMPC_VIA_001_001&amp;pipeline_stable_id=HRWL_001&amp;phenotyping_center=MRC%20Harwell" target="_blank">Complete preweaning lethality</a>
                            			</li>
                            			<li>
                            				Embryo LacZ Expression: NA
                            			</li>
                            			<li>
                            				Embryo Gross Dysmorphology: NA
                            			</li>
                            			<li>
                            				3-D Imaging: <a href="http://dev.mousephenotype.org/EmbryoViewerWebApp/?mgi=MGI:1916804">Image</a>
                            			</li> 
                            			<li>
                            				Adult het phenotype data: <a href="http://dev.mousephenotype.org/data/genes/MGI:1916804">table</a>
                            			</li>
                            		</ul>
                            	</div>
                            	<div class="half" style="width: 245px; font-size:80%; text-align:center;"><a href="http://dev.mousephenotype.org/EmbryoViewerWebApp/?mgi=MGI:1916804"><img alt="E18.5  Klhdc2 null embryo" src="https://dev.mousephenotype.org/sites/dev.mousephenotype.org/files/mousephenotype_files/Screen%20Shot%202016-02-12%20at%2013.23.37.png" style="width: 245px; height: 276px;padding-bottom:0.5em;">E18.5 Klhdc2 null embryo</a></div>
								
                            	<div class="clear"></div>
                            </div>
                       	</div>
                       	
  
                        
                      
                    <!--end of node wrapper should be after all secions  -->
                </div>
            </div>
        </div>
        
        </div>


	

      </jsp:body>

</t:genericpage>

