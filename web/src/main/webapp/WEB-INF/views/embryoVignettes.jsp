<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage-landing>

    <jsp:attribute name="title">IMPC Embryo Landing Page</jsp:attribute>
    <jsp:attribute name="pagename">Embryo Vignettes</jsp:attribute>
    <jsp:attribute name="breadcrumb">Embryo Vignettes</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="header">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/slider.js?v=${version}'></script>
        <link rel="stylesheet" href='${baseUrl}/css/slider.css?v=${version}'/>
    </jsp:attribute>

    <jsp:body>
        <div class="container">
            <div class="row mb-3">
                <div class="col-12">
                    <h2>IMPC Embryo Vignettes</h2>
                    <p>The vignettes showcase the IMPC embryo pipeline. They highlight the different phenotyping
                        procedures centres employ to phenotype embryonic lethal or subviable nulls. For more information
                        on the pipeline refer to the: <a href="${baseUrl}/embryo"
                                                         target="_blank"> IMPC Embryo Pipeline Introduction</a>, or read
                        more in our paper <a href=https://europepmc.org/articles/PMC5295821 target="_blank">High-throughput
                            discovery of novel developmental phenotypes, Nature 2016</a>. For a comprehensive list of
                        lines with 3D image data refer to: <a href="${baseUrl}/embryo_heatmap" target="_blank">IMPC 3D Embryo Data</a>.</p>
                </div>
            </div>
            <hr>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1913761"></c:set>
                    <h2 id="${geneId}"><t:formatAllele>Chtop<tm1a(EUCOMM)Wtsi></t:formatAllele></h2>
                    <div id="1" class="row">
                        <div class="col-8">
                            <p>
                                Chtop has been shown to recruit the histone-methylating methylosome to genomic regions
                                containing 5-Hydroxymethylcytosine, thus affecting gene expression.
                                <br/>
                                Chtop mutants showed complete preweaning lethality with no homozygous pups observed.
                                High resolution episcopic microscopy (HREM) imaging at E14.5 revealed multiple
                                phenotypes including edema, abnormal forebrain morphology and decreased number of
                                vertebrae and ribs.
                            </p>
                            <p>Phenotype data links</p>
                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Complete preweaning lethality</a>
                                </li>
                                <li>3-D imaging: <a href="http://dmdd.org.uk/mutants/Chtop" target="_blank">Images</a>
                                </li>
                                <li>Placental Histopathology: <a href="http://dmdd.org.uk/mutants/Chtop"
                                                                 target="_blank">Images</a></li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>
                        </div>
                        <div class="col-4">
                            <a href="${baseUrl}/images?gene_id=MGI:1913761&amp;fq=expName:Embryo%20Dysmorphology"><img
                                    class="w-100" alt="Embryo Dysmorphology Image"
                                    src="${baseUrl}/img/vignettes/chtopPink.jpg">Chtop
                                null embryo</a>
                        </div>
                    </div>
                </div>
            </div>

            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1916804"></c:set>
                    <h2 class="title" id="${geneId}"><t:formatAllele>Klhdc2<tm1b(EUCOMM)Hmgu></t:formatAllele></h2>
                    <div id="2" class="row">
                        <div class="col-8">
                            <p>Kelch domain-containing protein 2 functions as a transcriptional corepressor through its
                                inhibitory interaction with LZIP.</p>
                            <p>Klhdc2 mutants showed complete preweaning lethality with no homozygous pups observed,
                                but remain viable up to E18.5.
                                Micro-computed tomography (microCT) imaging revealed mutants display posterior
                                polydactyly and edema. In addition to this,
                                sections of microCT showed a smaller tongue, ventral septum defect (VSD), abnormal
                                intestines and displaced kidneys.
                            </p>
                            <p>The Kldhc2 gene is located within a locus linked to an automsomal dominant disease that
                                leads
                                to fibro-fatty replacement of right ventricle myocardium leading to arrythmias (ARVD3;
                                OMIM).<br>
                                The gene is expressed in
                                <a href="https://www.ebi.ac.uk/gxa/experiments/E-MTAB-3358?accessKey=&amp;serializedFilterFactors=DEVELOPMENTAL_STAGE:adult&amp;queryFactorType=ORGANISM_PART&amp;rootContext=&amp;heatmapMatrixSize=50&amp;displayLevels=false&amp;displayGeneDistribution=false&amp;geneQuery=KLHDC2&amp;exactMatch=true&amp;_exactMatch=on&amp;_queryFactorValues=1&amp;specific=true&amp;_specific=on&amp;cutoff=0.5">heart</a>
                                (expression atlas link) and has been implicated in <a
                                        href="http://www.ncbi.nlm.nih.gov/pubmed/16008511" target="_blank">endothelial
                                    differentation</a> and
                                <a href="http://www.ncbi.nlm.nih.gov/pubmed/16860314" target="_blank">myoblast
                                    differentation</a>.
                                Heterozygote null mice have abnormal heart rhythms while
                                the lethal embryos may have a heart defect.
                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVM_001_001"
                                       target="_blank">E12.5 Homozygous - Viable</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>3-D Imaging:
                                    <!--
                                    <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}&pid=203&h=382&s=on&c=off&a=off&o=vertical&zoom=4&sb=600&wn=klf7-tm1b-ic/16.5b_5553715&wx=69&wy=117&wz=177&wl=0&wu=200&mn=klhdc2-tm1b-ic/19.3c_5577193&mx=89&my=120&mz=176&ml=0&mu=213&wov=none&mov=none" target="_blank">Adrenal
                                    gland</a>, 
                                    <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}&pid=203&h=489&s=on&c=off&a=off&o=vertical&zoom=0&sb=600&wn=klf7-tm1b-ic/16.5b_5553715&wx=80&wy=117&wz=177&wl=0&wu=150&mn=klhdc2-tm1b-ic/21.1f_5578050&mx=80&my=106&mz=154&ml=0&mu=180" target="_blank">Intestines</a>, 
                                    <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}&pid=203&h=372&s=on&c=on&a=on&o=horizontal&zoom=0&sb=600&wn=klf7-tm1b-ic/16.5b_5553715&wx=91&wy=116&wz=163&wl=0&wu=255&mn=klhdc2-tm1b-ic/21.1f_5578050&mx=82&my=117&mz=144&ml=0&mu=255&wov=none&mov=none" target="_blank">VSD</a>, 
-->
                                    <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>

                        </div>
                        <div class="col-4">
                            <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank"><img class="w-100"
                                                                                                     alt="E18.5  Klhdc2 null embryo"
                                                                                                     src="${baseUrl}/img/vignettes/Kldhc2.png">E18.5
                                Klhdc2 null embryo</a>
                        </div>
                    </div>
                </div>
            </div>

            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:102806"></c:set>
                    <h2 class="title" id="${geneId}"><t:formatAllele>Acvr2a<tm1.1(KOMP)Vlcg></t:formatAllele></h2>

                    <div id="3" class="row">
                        <div class="col-8">
                            <p>
                                Activin receptor IIA is a receptor for activins, which are members of the TGF-beta
                                superfamily involved in diverse biological processes.<br>
                                Acvr2a mutants are subviable with most pups dying before postnatal day 7. Micro-CT
                                analysis
                                at E15.5 revealed variable penetrance of eye and craniofacial abnormalities. Eye
                                phenotypes
                                varied from normal (Embryo 1- (E1)), to underdeveloped (E2), to cyclopic (E3), to absent
                                (E4). Craniofacial phenotypes varied from normal (E1) to narrow snout (E2), to an
                                elongated
                                snout missing the mandible and tongue (E3, 4) and low set ears (E2, 3, 4).
                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Subviable</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVO_001_001"
                                       target="_blank">E15.5 Homozygous - Viable</a>
                                </li>
                                <li>
                                    Viability at P3/P7: <a id="acvr2aP3Trigger" class="various"
                                                           href="#acvr2aP3">Lethal</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    Gross Morphology: <a
                                        href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEO_050_001&acc=${geneId}"
                                        target="_blank">E15.5 Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                                <li>
                                    Embryo Histopathology: <a id="acvr2aHistTrigger" class="various" href="#acvr2aHist">Image</a>
                                </li>

                            </ul>

                        </div>
                        <div class="col-4"><img class="w-100" alt="Micro-CT of E15.5 Acvr2a"
                                                src="${baseUrl}/img/vignettes/Acvr2aMicroCT.png">
                        </div>


                        <div id="acvr2aHist" name="acvr2aHist" style="display:none">
                            <div class="row">
                                <img class="w-100" src="${baseUrl}/img/vignettes/Acvr2aHist.png"/>
                            </div>

                        </div>

                        <div id="acvr2aP3" style="display:none">
                            <div class="row">
                                <img class="w-100" src="${baseUrl}/img/vignettes/acvr2aP3.png"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1195985"></c:set>
                    <h2 class="title" id="${geneId}"><t:formatAllele>Cbx4<tm1.1(KOMP)Vlcg></t:formatAllele></h2>

                    <div class="row">
                        <div class="col-8">
                            <p>
                                Chromobox 4 is in the polycomb protein family that are key regulators of transcription
                                and is reported to be upregulated in lung bud formation and required for thymus
                                development.<br>
                                Cbx4 mutants showed complete preweaning lethality but were viable at E12.5 and E15.5
                                with no obvious gross morphological change.
                                Micro-CT analysis at E15.5 confirmed that
                                <t:formatAllele>Cbx4<tm1.1/tm1.1></t:formatAllele> mutants had statistically smaller
                                thymus and also revealed smaller adrenal glands and trigeminal ganglia compared to
                                <t:formatAllele>Cbx4<+/+></t:formatAllele> wildtype embryos.

                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVM_001_001"
                                       target="_blank">E12.5 Homozygous - Viable</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVO_001_001"
                                       target="_blank">E15.5 Homozygous - Viable</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                                <li>
                                    3D Volumetric Analysis: <a href="#cbx4Graphs" id="cbx4GraphsTrigger"
                                                               class="various">Graph</a>
                                </li>
                            </ul>

                        </div>
                        <div class="col-4"><img class="w-100" alt="Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1"
                                                src="${baseUrl}/img/vignettes/cbx4.png">Automated MRI analysis of E15.5
                            <t:formatAllele>Cbx4<tm1.1/tm1.1></t:formatAllele> mutants viewed in coronal section
                            revealed that mutant embryos had bilateral smaller trigeminal ganglia, thymus, and adrenal
                            glands compared to <t:formatAllele>Cbx4<+/+></t:formatAllele> wildtype embryos as indicated
                            by blue colour and highlighted by pink arrows (False Discovery Rate (FDR) threshold of 5%).
                        </div>

                        <div id="cbx4Graphs" class="row" style="display:none">
                            <div class="col-4"><img class="w-100" alt="Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1"
                                                    src="${baseUrl}/img/vignettes/thymus_black.png"
                                                    style="height: 600px; width:600px">
                            </div>
                            <div><img class="col-4" alt="Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1"
                                      src="${baseUrl}/img/vignettes/adrenal_black.png"
                                      style="height: 600px;  width:600px">
                            </div>
                            <p>Whole structural volume differences calculated as a percentage of whole body volume for
                                the left and right thymic rudiment (left) and left and right adrenal (right) of
                                <t:formatAllele>Cbx4<tm1.1/tm1.1></t:formatAllele> mutant embryos compared to
                                <t:formatAllele>Cbx4<+/+></t:formatAllele> wildtype embryos. Both organs are
                                significantly smaller in the Cbx4 mutant embryos at an FDR threshold of 5% where the
                                error bars represent 95% confidence intervals.
                            </p>
                        </div>
                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1915138"></c:set>
                    <h2 class="title" id="${geneId}"><t:formatAllele>Tmem100<tm1e.1(KOMP)Wtsi></t:formatAllele></h2>

                    <div class="row">
                        <div class="col-8">
                            <p>
                                Transmembrane Protein 100 functions downstream of the BMP/ALK1 signaling pathway.<br>
                                Tmem100 mutants showed complete preweaning lethality and were also lethal at E12.5.
                                LacZ staining in E12.5 Het embryos was found predominantly in arterial endothelial cells
                                and the heart (arrow) .
                                OPT analysis at E9.5 revealed that Tmem100 mutant embryos have a large pericardial
                                effusion with cardiac dysmorphology and enlargement (arrow).

                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVL_001_001"
                                       target="_blank">E9.5 Homozygous - Viable</a>
                                </li>

                                <li>
                                    Gross Morphology: <a
                                        href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEL_044_001&acc=${geneId}"
                                        target="_blank">E9.5 Images</a>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>


                        </div>


                        <div class="col-4"><img class="w-100" alt="Tmem100 wildtype embryo compared to a mutant"
                                                src="${baseUrl}/img/vignettes/tmem100.png">OPT analysis of E9.5 Tmem100
                            wildtype embryo compared to a <t:formatAllele>Tmem100<tm1e.1/tm1e.1></t:formatAllele> mutant
                            embryo and lacZ staining in an E12.5 <t:formatAllele>Tmem100<+/tm1e.1></t:formatAllele>
                            embryo.
                        </div>
                        <div id="tmem100EmbGross" style="display:none">
                            <img class="w-100" alt="Automated MRI analysis of E15.5 Cbx4tm1.1/tm1.1"
                                 src="${baseUrl}/img/vignettes/tmem100GM.png">
                            <p>
                                Gross morphology at E9.5 revealed that
                                <t:formatAllele>Tmem100<tm1e.1/tm1e.1></t:formatAllele> mutant embryos have a large
                                pericardial effusion with cardiac dysmorphology and enlargement (arrow).
                            </p>
                        </div>

                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1337104"></c:set>
                    <h2 class="title" id="${geneId}"><t:formatAllele>Eya4<tm1b(KOMP)Wtsi></t:formatAllele></h2>

                    <div class="row">
                        <div class="col-8">
                            <p>
                                Eyes absent transcriptional coactivator and phosphatase 4 is associated with a variety
                                of developmental defects including hearing loss.<br>
                                Eya4 mutants showed complete preweaning lethality with no homozygous pups observed.
                                Micro-CT analysis at E15.5 revealed <t:formatAllele>Eya<tm1b/tm1b></t:formatAllele>
                                mutant embryos had bi-lateral smaller cochlear volumes as well as a smaller thyroid
                                gland, Meckel's cartilage, trachea (opening), cricoid cartilage, and arytenoid
                                cartilage.

                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVM_001_001"
                                       target="_blank">E12.5 Homozygous - Viable</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVO_001_001"
                                       target="_blank">E15.5 Homozygous - Viable</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a id="eyaEmbLacZTrigger" href="#eyaEmbLacZ"
                                                               class="various">Images</a>
                                </li>
                                <li>
                                    Embryo Histopathology: <a id="eyaEmbHisTrigger" href="#eyaEmbHis" class="various">Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>

                            </ul>

                        </div>
                        <div class="col-4"><img class="w-100"
                                                alt="Automated MRI analysis of E15.5 Eya4<tm1b/tm1b> mutants"
                                                src="${baseUrl}/img/vignettes/eye4.png">
                            Automated MRI analysis of E15.5 Eya4tm1b/tm1b mutants showed that mutant embryos had a
                            statistically smaller
                            volumes of the cochlea and other tissues compared to
                            <t:formatAllele>Eya4<+/+></t:formatAllele> wildtype
                            embryos as highlighted in blue in transverse, coronal, and sagittal sections (false
                            discovery rate (FDR) threshold of 5%).
                        </div>


                        <div id="eyaEmbLacZ" style="display:none">

                            <div>
                                <img class="w-100" alt="Lac Z staining at E12.5"
                                     src="${baseUrl}/img/vignettes/eye4Lac.png">
                                <p>Lac Z staining at E12.5 showed that Eya4 expression is primarily in the craniofacial
                                    mesenchyme, cochleae and outer ear, dermamyotome, and limb.
                                </p>

                            </div>

                        </div>

                        <div id="eyaEmbHis" style="display: none">
                            <img class="w-100" src="${baseUrl}/img/vignettes/eya4LacSlides.png"/>
                            <p> H&E stained sagittal section through the right cochlea of an
                                <t:formatAllele>Eya4<+/+></t:formatAllele>
                                wildtype embryo compared to an <t:formatAllele>Eya4<tm1b/tm1b></t:formatAllele> mutant
                                embryo indicated that the mutant
                                embryo had a hypoplastic cochlea. Higher magnification of the region (indicated by the
                                white box) showed abnormal
                                perilymphatic (periotic) mesenchyme (PM) in the mutant embryo compared to the wildtype
                                embryo. In the wildtype embryo
                                the perilymphatic mesenchyme (PM) was rarefied and had multifocal vacuolation (arrow)
                                suggesting normal perilymph
                                development. In the mutant embryo the perilymphatic mesenchyme (PM) did not show
                                rarefaction and had reduced vacuolation
                                (arrow) suggesting the cochlear hypoplasia was due to delayed perilymph development.
                                BL-Bony Labyrinth (cartilage at E15.5), PM-Perilymphatic (periotic) mesenchyme,
                                ML-Membranous Labyrinth, EN-Endolymph
                            </p>

                        </div>
                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:3039593"></c:set>
                    <h2 id="${geneId}"><t:formatAllele>Tox3<tm1b(KOMP)Mbp></t:formatAllele></h2>
                    <div class="row">
                        <div class="col-8">
                            <p> Tox High Mobility Group Box Family Member 3 is a member of the HMG-box family involved
                                in bending and unwinding DNA.
                                Tox3 mutants have partial preweaning lethality with 1/3 of the pups dying before P7.
                                Whole brain MRI at P7 revealed that <t:formatAllele>Tox3<tm1b/tm1b></t:formatAllele>
                                mutants had a much smaller cerebellum (blue) compared to the
                                <t:formatAllele>Tox3<+/+></t:formatAllele>
                                wildtype mice (as seen in coronal, sagittal, and axial section) and a relatively larger
                                amygdala, thalamus, pons (red)
                            </p>

                            Phenotype data links
                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Subviable</a>
                                </li>
                                <li>
                                    Viability at P3/P7: <a href="#tox3Viable" id="tox3ViableTrigger" class="various">Viable</a>
                                </li>
                                <li>
                                    Embryo Histopathology: <a href="#tox3His" id="tox3HisTrigger"
                                                              class="various">Images</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>
                        </div>
                        <div class="col-4"><img class="w-100" alt="Tox3 MRI" src="${baseUrl}/img/vignettes/tox3MRI.png">
                            Caudal to rostral coronal sections of whole brain MRI with automated volume analysis
                            revealed P7
                            <t:formatAllele>Tox3<tm1b/tm1b></t:formatAllele> mutant mice had smaller (blue) and larger
                            (red) tissues compared
                            to the <t:formatAllele>Tox3<+/+></t:formatAllele> wildtype average.
                        </div>


                        <div id="tox3Viable" style="display: none">
                            <h3>P3/P7 viability test Tox3</h3>

                            <img class="w-100" src="${baseUrl}/img/vignettes/Tox3Table.png">
                        </div>

                        <div id="tox3His" style="display: none">
                            <img class="w-100" src="${baseUrl}/img/vignettes/Tox3HIST.png">
                            <p>
                                H&E stained coronal section through the brain of a
                                <t:formatAllele>Tox3<+/+></t:formatAllele> wildtype embryo
                                compared to a <t:formatAllele>Tox3<tm1b/tm1b></t:formatAllele> mutant embryo indicated
                                that the mutant embryo had a
                                hypoplastic and dysplastic cerebellum (CE) with markedly reduced fissure formation.
                                Higher magnification revealed
                                that the transient external granular layer was absent in the
                                <t:formatAllele>Tox3<tm1b/tm1b></t:formatAllele> mutant
                                mice and the subjacent molecular layer was hypotrophic and irregular in thickness
                                (arrow).
                            </p>
                        </div>
                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1922814"></c:set>
                    <h2 id="${geneId}"><t:formatAllele>Rsph9<tm1.1(KOMP)Vlcg></t:formatAllele></h2>
                    <div class="row">
                        <div class="col-8">
                            <p>
                                Radial spoke head protein 9 is a component of the radial spoke head in motile cilia
                                and flagella.
                                Rsph9 mutants showed partial pre-weaning lethality but viable to P7.
                                Whole brain MRI and H&E staining of coronal sections of the P7 brain revealed severe
                                hydrocephaly of the left and right lateral ventricles of the Rsph9 mutant.
                                Coronal section through the nasal region showed that the sinuses of the Rsph9
                                mutants were filled with pus (asterisks).
                                Both hydrocephaly and nasal blockage are phenotypes associated with Primary Ciliary
                                Dyskinesia in humans.
                            </p>

                            <p>Phenotype data links</p>
                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Subviable</a>
                                </li>
                                <li>
                                    Viability at P3/P7: <a href="#rsph9Viable" class="various"
                                                           id="rsph9ViableTrigger">Viable</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a
                                        href="${baseUrl}/genes/${geneId}#phenotypesTab" target="_blank">Table</a>
                                </li>
                                <li>
                                    Whole Brain MRI: <a href="#rsph9Mri" class="various"
                                                        id="rsph9MriTrigger">Images</a>
                                </li>
                            </ul>
                        </div>

                        <div class="col-4">
                            <img class="w-100" alt="H&E stained Rsph9" src="${baseUrl}/img/vignettes/Rsph9Slides.png">
                            H&E stained coronal sections of P7 mice revealed enlarged ventricles and blocked sinuses
                            in the
                            <t:formatAllele>Rsph9<tm1.1/tm1.1></t:formatAllele> mutant mice.
                        </div>

                        <div class="clear"></div>


                        <div id="rsph9Viable" style="display: none">
                            <h3>P3/P7 viability test Rsph9</h3>
                            <img class="w-100" src="${baseUrl}/img/vignettes/Rsph9Table.png">
                        </div>

                        <div id="rsph9Mri" style="display:none">
                            <img class="w-100" src="${baseUrl}/img/vignettes/Rsph9MRI.png">
                            <p> Coronal and axial sections of whole brain MRI showed enlarged ventricles in P7
                                <t:formatAllele>Rsph9<tm1.1/tm1.1></t:formatAllele>
                                mutant mice. P7 <t:formatAllele>Rsph9<tm1.1/tm1.1></t:formatAllele> mice brains had
                                enlarged left and right lateral ventricles (arrows)
                                when compared to the <t:formatAllele>Rsph9<+/+></t:formatAllele> WT average.
                            </p>
                        </div>


                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:97491"></c:set>
                    <h2 class="title" id="${geneId}"><t:formatAllele>Pax7<tm1.1(KOMP)Vlcg></t:formatAllele></h2>

                    <div class="row">
                        <div class="col-8">
                            <p>
                                Pax 7 is a nuclear transcription factor with DNA-binding activity via its paired
                                domain.<br>
                                It is involved in specification of the neural crest and is an upstream regulator of
                                myogenesis during post-natal growth and muscle regeneration in the adult.
                                Pax7 mutants showed complete preweaning lethality.
                                Micro-CT analysis at E15.5 revealed voxel-wise local volume differences with a
                                larger nasal septum, cavity and capsule (False Discovery Rate <5%) in the E15.5
                                <t:formatAllele>Pax7<tm1.1/tm1.1></t:formatAllele> mutant embryos compared the
                                wildtype embryos.
                                LacZ staining at E12.5 showed very strong staining in the medial region of the
                                frontonasal prominence (arrows) where structural changes were found.
                                LacZ staining was also seen in the midbrain, hindbrain, spinal cord, vertebrae, ribs
                                and neural crest.
                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Subviable</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>

                        </div>
                        <div class="col-4">
                            <img class="w-100" alt="MicroCT Pax7 embryos and LacZ Staining"
                                 src="${baseUrl}/img/vignettes/Pax7.png">Micro-CT analysis of E15.5 Pax7 embryos and
                            lacZ staining of E12.5 embryos indicating volume changes and staining in the nasal area.
                        </div>


                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1928849"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Svep1<tm1b (EUCOMM)Hmgu/J></t:formatAllele>
                    </h2>

                    <div class="row">
                        <div class="col-8">
                            <p>
                                Svep1 codes for an uncharacterized protein named after the multiple domains
                                identified in the
                                sequence: Sushi, a domain common in adhesion and complement proteins; von
                                Willebrand factor
                                type A, occurring in extra-cellular matrix and integrin proteins; Epidermal
                                Growth Factor, extra-cellular
                                cysteine-rich repeats promoting protein-protein interactions; pentraxin domain
                                containing 1, reactive
                                with the complement system. No prior targeted mutations for this gene have been
                                reported.
                                Homozygous mutants show complete preweaning lethality, with embryonic lethality
                                occurring after
                                E18.5. Hemorrhaging is seen in surviving E18.5 mutants, as is severe edema and
                                small embryo size
                                (Fig 1, left). Among other defects, microCT analysis reveals brain defects, lung
                                hypoplasia and absent
                                renal pelvis in the kidney (Fig 1, middle, right). Phenotypes of heterozygotes
                                include abnormal body composition
                                and abnormal blood chemistry.
                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVM_001_001"
                                       target="_blank">E12.5 Homozygous - Subviable</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    Gross Morphology:
                                    <a href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEP_064_001&acc=${geneId}"
                                       target="_blank">E18.5 Images</a>,
                                    <a href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEO_050_001&acc=${geneId}"
                                       target="_blank">E15.5 Images</a>

                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>

                        </div>
                        <div class="col-4">
                            <img class="w-100" alt="E18.5  Svep1 null embryo" src="${baseUrl}/img/vignettes/Svep1.jpg">
                        </div>
                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:2151064"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Strn3<tm1b (KOMP)Wtsi/J></t:formatAllele>
                    </h2>

                    <div class="row">
                        <div class="col-8">
                            <p>
                                Striatins act as both calcium-dependent signaling proteins and scaffolding
                                proteins, linking calcium-sensing signaling events with cellular action [1].
                                Strn3
                                homozygous mutants show complete preweaning lethality, with embryonic
                                lethality occurring around E15.5. Surviving embryos are smaller in size and
                                display both hemorrhaging and severe edema (Fig 1, left panels). MicroCT
                                analyses reveal small, but consistent septal defects in heart (Fig 1, right
                                panels). Multiple phenotypes are observed in heterozygous adult animals,
                                including
                                abnormal blood chemistry and hematology, impaired glucose tolerance and
                                abnormal behavior, among others. A Genome-wide association study linking
                                Strn3 with the canine disease Arrhythmogenic Right Ventricular Cardiomyopathy
                                (ARVC) supports a role in cardiac function [2].
                            </p>
                            <p>
                                <i>[1] The striatin family: a new signaling platform in dendritic spines,
                                    Benoist, M, et al, (2006) J. Physiol. Paris; [2] Identification of Striatin
                                    deletion in canine ARVC. (2010) Meurs, K. M., et al, Hum. Genet.</i>
                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVM_001_001"
                                       target="_blank">E12.5 Homozygous - Viable</a>,
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    Gross Morphology:
                                    <a href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEO_050_001&acc=${geneId}"
                                       target="_blank">E15.5 Images</a>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a
                                        href="${baseUrl}/genes/${geneId}#phenotypesTab" target="_blank">Table</a>
                                </li>
                            </ul>

                        </div>
                        <div class="col-4">
                            <img class="w-100" alt="E18.5  Strn3 null embryo" src="${baseUrl}/img/vignettes/Strn3.jpg">
                        </div>

                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:104606"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Rab34<tm1b (EUCOMM)Hmgu/J></t:formatAllele>
                    </h2>

                    <div class="row">
                        <div class="col-8">
                            <p>
                                Rab34 is a member of the RAS oncogene family, which are small GTPases
                                involved in intracellular vesicle transport. Rab34 is known to be Golgi-bound,
                                involved in lysosomal positioning.
                                Rab34 is a potential target of Gli1 and a possible
                                component of hedgehog signaling. The Rab34 knockout is the first reported null
                                allele for this gene,
                                resulting in complete preweaning lethality. Phenotypes
                                include patterning defects, such as polydactyly and facial clefting, as well as
                                abnormal eye
                                development and severe lung hypoplasia (Fig 1, lu = lung). The mutants are
                                subviable at E18.5, lethality presumably occurring perinatally.
                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVO_001_001"
                                       target="_blank">E15.5 Homozygous - Viable</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    Gross Morphology:
                                    <a href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEP_064_001&acc=${geneId}"
                                       target="_blank">E18.5 Images</a>,
                                    <a href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEO_050_001&acc=${geneId}"
                                       target="_blank">E15.5 Images</a>,
                                    <a href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEM_049_001&acc=${geneId}"
                                       target="_blank">E12.5 Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>
                        </div>
                        <div class="col-4">
                            <img class="w-100" alt="Rab34 null embryo" title="Fig. 1"
                                 src="${baseUrl}/img/vignettes/Rab34.jpg">
                        </div>

                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:103226"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Cox7c
                        <tm1b (KOMP)Mbp></t:formatAllele>
                    </h2>

                    <div class="row">
                        <div class="col-8">
                            <p>
                                Cytochrome c oxidase subunit VIIc (Cox7c) is a nuclear-encoded regulatory
                                component of cytochrome c oxidase. Homozygous mutants show complete
                                preweaning lethality, with embryonic lethality occurring after E15.5. Surviving
                                mutants are smaller (Figure 1, top panels), and have an abnormally small
                                placenta (Figure 1, bottom panels). Although significant development progresses
                                after E15.5, by E18.5 homozygous embryos die and begin to resorb.
                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    Gross Morphology:
                                    <a
                                            href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEP_064_001&acc=${geneId}"
                                            target="_blank">E18.5 Images</a>,
                                    <a
                                            href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEO_050_001&acc=${geneId}"
                                            target="_blank">E15.5 Images</a>,
                                    <a
                                            href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEM_049_001&acc=${geneId}"
                                            target="_blank">E12.5 Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>

                        </div>
                        <div class="col-4">
                            <img class="w-100" alt="E15.5 Cox7c embryo" src="${baseUrl}/img/vignettes/Cox7c.jpg">
                        </div>

                    </div>
                </div>
            </div>

            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1920939"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Bloc1s2<tm1.1(KOMP)Mbp></t:formatAllele>
                    </h2>

                    <div class="row">
                        <div class="col-8">
                            <p>
                                Biogenesis of lysosomal organelles complex 1, subunit 2 is a component of the
                                BLOC-1 complex, which functions in the formation of lysosome-related
                                organelles, is implicated in synapse function, and is associated with gamma-
                                tubulin and the centrosome [1]. Homozygous mutants show complete
                                preweaning lethality, with embryonic lethality occurring around E15.5. Surviving
                                mutants at E15.5 show edema, hemorrhage, and abnormal cardiovascular
                                development (Fig 1). MicroCT datasets of E15.5 embryos also reveal lung
                                hypoplasia, enlarged right atrium, and compromised right ventricle of the heart
                                (Fig.1, arrow). Adult heterozygotes show abnormal immunophenotypes.
                            </p>
                            <p><i>[1] Falcon-Perez et al., BLOC-1, a novel complex containing the pallidin and
                                muted proteins involved in the biogenesis of melanosomes and platelet-dense
                                granules, J Biol Chem 2002</i></p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: No expression
                                </li>
                                <li>
                                    Gross Morphology:
                                    <a href="${baseUrl}/imageComparator?&parameter_stable_id=IMPC_GEM_049_001&acc=${geneId}"
                                       target="_blank">E12.5 Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>

                        </div>
                        <div class="col-4">
                            <img class="w-100" alt="E15.5 Bloc1s2 embryo" src="${baseUrl}/img/vignettes/Bloc1s2.jpg">
                        </div>

                    </div>
                </div>
            </div>


            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:95698"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Gfpt1<tm1b(EUCOMM)Wtsi>
                        </t:formatAllele>
                    </h2>

                    <div class="row">
                        <div class="col-8">
                            <p> Gfpt1 encodes glutamine:fructose-6-phosphate amidotransferase 1, which catalyzes
                                the transfer of an amino group from
                                glutamine onto fructose-6-phosphate. This is the first and rate limiting enzyme
                                of the hexosamine biosynthetic pathway.
                            </p>
                            <p>
                                Gfpt1 mutants showed complete lethality by E12.5 with no homozygous embryos
                                observed. Optical projection tomography (OPT)
                                at E9.5 illustrated developmental delay, craniofacial abnormalities, abnormal
                                allantois development, failure to complete turning and abnormal heart looping.
                            </p>
                            <p>Phenotype data links</p>
                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVM_001_001"
                                       target="_blank">E12.5 Homozygous - Lethal</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>
                        </div>
                        <div class="col-4">
                            <img class="w-100" alt="Developmental delay and failure to turn in E9.5 Gfpt1-null mutants."
                                 src="${baseUrl}/img/vignettes/gfpt1.png"> Developmental delay and failure to
                            turn in E9.5 Gfpt1-null mutants. Morphology captured by OPT.
                        </div>
                    </div>
                </div>
            </div>
            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1915091"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Atg3<tm1b(EUCOMM)Hmgu>
                        </t:formatAllele>
                    </h2>
                    <div class="row">
                        <div class="col-8">
                            <p> Atg3 is an E2-like protein-conjugating enzyme involved in autophagy broadly
                                expressed during development and in the adult.
                            </p>
                            <p>
                                Atg3 mutants show complete preweaning lethality with no homozygous pups
                                observed, but they are viable at least until E14.5.
                                Micro-computed tomography (microCT) imaging at E14.5 revealed homozygous mutant
                                fetuses had cardiovascular abnormalities such
                                as ventral septum defects (VSD), thick atrio-ventricular valves and a thin
                                myocardium, as well as an enlarged umbilical vein.
                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVM_001_001"
                                       target="_blank">E12.5 Homozygous - Viable</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVO_001_001"
                                       target="_blank">E14.5 Homozygous - Viable</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>

                        </div>
                        <div class="col-4">
                            <img class="w-100" alt="VSD seen in transverse section in Atg3-null mutants."
                                 src="${baseUrl}/img/vignettes/Atg3.png"> VSD seen in transverse section in
                            Atg3-null mutants.
                        </div>

                    </div>
                </div>
            </div>

            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1924285"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Kdm8<tm1b(EUCOMM)Wtsi>
                        </t:formatAllele>
                    </h2>

                    <div class="row">
                        <div class="col-8">
                            <p>Kdm8 encodes for lysine (K)-specific demethylase 8, which is predicted to have
                                dual functions as a histone demethylase and as a protein hydroxylase.
                                The gene is formerly known as Jmjd5.
                            </p>
                            <p>
                                <t:formatAllele>Kdm8
                                <tm1b></t:formatAllele> homozygous mutants showed complete lethality by E12.5.
                                    Optical projection tomography (OPT) showed that at E9.5 mutant embryos
                                    appear small in size, remain unturned and that they are developmentally
                                    delayed by this stage of gestation. Interestingly.
                                    <t:formatAllele>Kdm8
                                    <tm1a></t:formatAllele> homozygous mutants can live up to the end of
                                        gestation, suggesting that the targeted trap is a hypomorphic allele.
                            </p>

                            <p>Phenotype data links</p>

                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a
                                        href="${baseUrl}/genes/${geneId}#phenotypesTab" target="_blank">Table</a>
                                </li>
                            </ul>

                        </div>
                        <div class="col-4">
                            <img class="w-100" alt="Developmental delay at E9.5 in Kdm8tm1b mutants."
                                 src="${baseUrl}/img/vignettes/Kdm8.png"> Developmental delay at E9.5 in
                            <t:formatAllele>Kdm8<tm1b></tm1b></t:formatAllele> mutants.
                        </div>

                    </div>
                </div>
            </div>

            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1914797"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Slc39a8<tm1b(EUCOMM)Wtsi></t:formatAllele>
                    </h2>
                    <div class="row">
                        <div class="col-8">
                            <p>Solute carrier family 39 (metal ion transporter), member 8 encodes a protein that
                                functions as a transporter for several divalent cations.
                                Mutants show complete preweaning lethality with no homozygous pups observed, but
                                are viable at least until E14.5.
                                Micro-computed tomography (microCT) imaging at E14.5 revealed mutants were
                                smaller and had cardiovascular abnormalities, such as ventral septum defects.
                                It also revealed mutants lacked a sternum and had a small chest cavity and
                                liver.
                            </p>
                            <p>Phenotype data links</p>
                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVM_001_001"
                                       target="_blank">E12.5 Homozygous - Viable</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>
                        </div>
                        <div class="col-4">
                            <img class="w-100"
                                 alt="Slc39a8-null mutants are significantly smaller than WT littermates and have smaller livers."
                                 src="${baseUrl}/img/vignettes/Slc39a8.png"> <br/> Slc39a8-null mutants are
                            significantly smaller than WT littermates and have smaller livers.
                        </div>
                    </div>
                </div>
            </div>
            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:1351614"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Gyg<tm1b(KOMP)Wtsi></t:formatAllele>
                    </h2>
                    <div class="row">
                        <div class="col-8">
                            <p>Glycogenin is an enzyme that converts glucose to glycogen. Glycogenin catalyzes
                                UDP-alpha-D-glucose + glycogenin &rlhar; UDP + alpha-D-glucosylglycogenin. The
                                enzyme is a homodimer of 37 kDa subunits.
                                Mutations in human GYG1 are associated with Glyocgen Storage Disease XV and
                                Polyglucosan Body Myopathy 2 (<a
                                        href="http://www.omim.org/entry/603942">OMIM</a>). Homozygous null Gyg
                                mice die between birth and weaning but were found in normal proportions at
                                E18.5. Mutants were indistinguishable from littermates at E12.5, E15.5 or E18.5
                                but analysis of microCT images revealed obvious cardiac abnormalities, enlarged
                                thymus and abnormal nervous system morphology. This is the first reported Gyg
                                mouse mutant.
                            </p>
                            <p>Phenotype data links</p>
                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVP_001_001"
                                       target="_blank">E18.5 Homozygous - Viable</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                            </ul>
                        </div>
                        <div class="col-4">
                            <img class="w-100"
                                 alt="Single images from E18.5 microCT volumes showing spinal cord abnormalities (arrow), enlarged thymus (asterisk) and thickened myocardium (arrowhead) in homozygous null embryos compared to wild-type littermates."
                                 src="${baseUrl}/img/vignettes/Gygmutants.png"> <br/> Single images from E18.5
                            microCT volumes showing spinal cord abnormalities (arrow), enlarged thymus
                            (asterisk) and thickened myocardium (arrowhead) in homozygous null embryos compared
                            to wild-type littermates.
                        </div>
                    </div>
                </div>
            </div>
            <hr/>
            <div class="row mb-5">
                <div class="col-12">
                    <c:set var="geneId" value="MGI:2147810"></c:set>
                    <h2 class="title" id="${geneId}">
                        <t:formatAllele>Tmem132a<tm1b(KOMP)Wtsi></t:formatAllele>
                    </h2>
                    <div class="row">
                        <div class="col-8">
                            <p>Transmembrane protein132a is transmembrane protein of unknown function.
                                Homozygous null mutants were viable at normal proportions at E15.5 and E18.5 but
                                showed obvious and severe defects that were readibly visible by eye. Embryos had
                                abnormal limb morphology with syndactyly, spina bifida, heart abnormalities.
                                Some mutants were smaller than littermates.
                            </p>
                            <p>Phenotype data links</p>
                            <ul>
                                <li>
                                    Viability:
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_VIA_001_001"
                                       target="_blank">Adult Homozygous - Lethal</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVM_001_001"
                                       target="_blank">E12.5 Homozygous - Viable</a>,
                                    <a href="${baseUrl}/charts?accession=${geneId}&parameter_stable_id=IMPC_EVP_001_001"
                                       target="_blank">E18.5 Homozygous - Viable</a>
                                </li>
                                <li>
                                    Embryo LacZ Expression: <a
                                        href="${baseUrl}/imageComparator?acc=${geneId}&anatomy_term=%22TS20%20embryo%20or%20Unassigned%22&parameter_stable_id=IMPC_ELZ_064_001"
                                        target="_blank">Images</a>
                                </li>
                                <li>
                                    3-D Imaging: <a href="${cmsBaseUrl}/embryoviewer/?mgi=${geneId}" target="_blank">3D
                                    Viewer</a>
                                </li>
                                <li>
                                    All adult and embryo phenotypes: <a href="${baseUrl}/genes/${geneId}#phenotypesTab"
                                                                        target="_blank">Table</a>
                                </li>
                                <li>
                                    Sagittal images from microCT: <a href="#tmem132aMri" class="various"
                                                                     id="tmem132aTrigger">Centre provided image</a>
                                </li>
                                <li>
                                    Axial images from microCT <a href="#tmem132amCT" class="various"
                                                                 id="tmem132amCTTrigger">Centre provided image</a>
                                </li>
                            </ul>
                        </div>

                        <div class="col-4">
                            <img class="w-100"
                                 alt="Surface renderings of  microCT volumes of Tmem132a mutants compared to wildtype littermates"
                                 src="${baseUrl}/img/vignettes/Tmem132a surface recon.png"> <br/> Surface
                            renderings of microCT volumes of Tmem132a mutants compared to wildtype littermates
                        </div>

                        <div id="tmem132aMri" style="display:none">
                            <img class="w-100" src="${baseUrl}/img/vignettes/Tmem132aE15.5 sag.png">
                            <p>
                                Sagittal images from microCT volumes of wildtype (WT) and mutant
                                (<t:formatAllele>Tmem132a<tm1b/tm1b></t:formatAllele>) showing areas of defects
                                (red arrows) indicating reduced neural tissue, heart defects and kidney
                                abnormalities (extra lobe).
                            </p>
                        </div>

                        <div id="tmem132amCT" style="display:none">
                            <img class="w-100" src="${baseUrl}/img/vignettes/Tmem132a axial.png">
                            <p>
                                Axial images from microCT volumes of wildtype (WT) and mutant
                                (<t:formatAllele>Tmem132a<tm1b/tm1b></t:formatAllele>) showing kidney
                                abnormalities (extra lobe).
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
        </div>


        <script type="text/javascript">
            $("#acvr2aHistTrigger").fancybox();
            $("#acvr2aP3Trigger").fancybox();
            $("#tmem100EmbGrossTrigger").fancybox();
            $("#eyaEmbLacZTrigger").fancybox();
            $("#eyaEmbHisTrigger").fancybox();
            $("#cbx4GraphsTrigger").fancybox();
            $("#tox3ViableTrigger").fancybox();
            $("#tox3HisTrigger").fancybox();
            $("#rsph9ViableTrigger").fancybox();
            $("#rsph9MriTrigger").fancybox();
            $("#tmem132aTrigger").fancybox();
            $("#tmem132amCTTrigger").fancybox();
        </script>


    </jsp:body>

</t:genericpage-landing>