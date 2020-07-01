<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage-landing>

    <jsp:attribute name="title">IMPC Embryo Landing Page</jsp:attribute>
    <jsp:attribute name="pagename">IMPC Embryo Data</jsp:attribute>
    <jsp:attribute name="breadcrumb">Embryo</jsp:attribute>

    <jsp:attribute name="bodyTag"><body class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="header">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/slider.js?v=${version}'></script>
        <link rel="stylesheet" href='${baseUrl}/css/slider.css?v=${version}'/>
		<link href="${baseUrl}/css/alleleref.css" rel="stylesheet"/>
    </jsp:attribute>

    <jsp:body>

        <div class="container">
            <h2>Introduction to IMPC Embryo Data</h2>
            <div class="row mb-5">
                <div class="col-12">
                    <div class="row mb-3">
                        <p>
                            Up to one third of homozygous knockout lines are lethal, which means no homozygous mice or
                            less than expected are observed past the weaning stage (IMPC <a
                                href="${cmsBaseUrl}/impress/ProcedureInfo?action=list&procID=703&pipeID=7">Viability
                            Primary Screen procedure</a>). Early death may occur during embryonic development or soon
                            after birth, during the pre-weaning stage. For this reason, the IMPC established a <a
                                href="${cmsBaseUrl}/impress">systematic embryonic phenotyping pipeline</a> to
                            morphologically evaluate mutant embryos to ascertain the primary perturbations that cause
                            early death and thus gain insight into gene function.

                        </p>
                        <p>
                            As determined in IMPReSS (see interactive diagram <a href="${cmsBaseUrl}/impress">here</a>), all embryonic lethal lines undergo
                            gross morphology assessment at E12.5 (embryonic day 12.5) to determine whether defects occur
                            earlier or later during embryonic development. A comprehensive imaging platform is then used
                            to assess dysmorphology. Embryo gross morphology, as well as 2D and 3D imaging are actively
                            being implemented by the IMPC for lethal lines.
                        </p>
                        <p>
                            Read more in our paper on <a href="https://europepmc.org/article/PMC/5295821">High-throughput
                            discovery of novel developmental phenotypes, Nature 2016.</a>
                        </p>
                    </div>

                    <h2>Accessing Embryo Phenotype Data</h2>
                    <div class="row mb-3">
                        <p>
                            Embryo phenotype data can be accessed through:
                        </p>
                            <ul>
                        <li><a href="${baseUrl}/embryo_heatmap">Embryo
                            Viewer</a></li>
                        <li>FTP site or REST API (see the documentation <a
                                href="${cmsBaseUrl}/help/data-access/">here</a>)</li>
                        <li><a
                                href="${baseUrl}/embryo/vignettes">Embryo Vignettes page</a></li>
                    </ul>
                    </div>
                    <h2>Determining Lethal Lines</h2>
                    <div class="row mb-3">
                        <p>
                            The IMPC assesses each gene knockout line for viability (Viability Primary Screen
                            <a href="${cmsBaseUrl}/impress/ProcedureInfo?action=list&procID=703&pipeID=7">IMPC_VIA_001</a>).
                            In this procedure, the proportion of homozygous pups is determined soon after
                            birth, during the preweaning stage, in litters produced from mating heterozygous animals. A
                            line is declared lethal if no homozygous pups for the null allele are detected at weaning
                            age, and subviable if pups homozygous for the null allele constitute less than 12.5% of the
                            litter.
                        </p>
                        <p>
                            Lethal strains are further phenotyped in the <a href="${cmsBaseUrl}/impress">embryonic
                            phenotyping pipeline</a>. For embryonic
                            lethal and subviable strains, heterozygotes are phenotyped in the IMPC <a
                                href="${cmsBaseUrl}/impress">adult phenotyping
                            pipeline</a>.
                        </p>
                    </div>
                    <div class="row mb-5">
                        <div id="viabilityChart" class="col-6 h-100">
                            <script type="text/javascript">${viabilityChart}</script>
                        </div>
                        <div id="viabilityChart" class="col-6 align-items-center">
                            <table class="table table-striped thead-light w-100">
                                <thead>
                                <tr>
                                    <th class="headerSort">Category</th>
                                    <th># Genes</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="row" items="${viabilityTable}">
                                    <tr>
                                        <td><span class="capitalize">${row.category}</span></td>
                                        <c:if test="${row.mpId != null}">
                                            <td><a href="${baseUrl}/phenotypes/${row.mpId}">${row.count}</a></td>
                                        </c:if>
                                        <c:if test="${row.mpId == null}">
                                            <td>${row.count}</td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td></td>
                                    <td>
                                        <a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest/reports/viabilityReport.csv"
                                           style="text-decoration:none;" download> <i class="fa fa-download"
                                                                                      alt="Download"></i> Download</a>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row mb-5">
                <div class="col-12">
                    <h2>IMPC Embryo Phenotyping - Goals and Procedures</h2>

                    <p>With up to one third of knockout strains being embryonic lethal, a systematic unbaised
                        phenotyping pipeline was established to perform morphologic and imaging evaluation of mutant
                        embryos to define the primary perturbations that cause their death. From this important insights
                        are gained into gene function.</p>
                    <p>IMPC centers funded by the NIH Common fund mechanism are delivering the following for <b>All
                        Lines</b>:</p>
                    <ul>
                        <li>Viability</li>
                        <li>Heterozygote E12.5 Embryonic LacZ staining ( 2 mutant animals, wt reference images)</li>
                    </ul>

                    <p>For <b>All Embryonic Lethal Lines</b>, gross morphology is assessed at E12.5 to determine if
                        defects occur earlier or later in development. A comprehensive imaging platform is then used to
                        assess dysmorphology at the <b>most</b> appropriate stage:</p>

                    <table class="table table-striped thead-light w-100">
                        <tr>
                            <th>Procedure</th>
                            <th>Number</th>
                            <th>Note
                        </tr>
                        <tr>
                            <td>E9.5 Gross morphology</td>
                            <td>at least 2 homs,2 wt</td>
                            <td>images optional</td>
                        </tr>
                        <tr>
                            <td>E9.5 OPT screening</td>
                            <td>at least 2 homs</td>
                            <td>reconstructions available</td>
                        </tr>
                        <tr>
                            <td>E14.5-E15.5 Gross morphology</td>
                            <td>at least 2 homs, 2 wt</td>
                            <td>images optional</td>
                        </tr>
                        <tr>
                            <td>E14.5-E15.5 microCT screening</td>
                            <td>at least 2 homs</td>
                            <td>reconstructions available</td>
                        </tr>
                        <tr>
                            <td>E14.5 HREM</td>
                            <td>at least 3 homs, 1wt</td>
                            <td> reconstructions available</td>
                        </tr>
                        <tr>
                            <td>E18.5 Gross morphology</td>
                            <td>at least 2 homs</td>
                            <td>images optional</td>
                        </tr>
                        <tr>
                            <td>E18.5 microCT</td>
                            <td>at least 2 homs, 2 wt</td>
                            <td>reconstructions available</td>
                        </tr>
                    </table>

                    <p>In addition, the NIH is supporting in-depth phenotyping of embryonic lethal lines with three
                        current awardees.</p>
                    <p>
                        <a href="http://www.ucdenver.edu/academics/colleges/medicalschool/programs/Molbio/faculty/WilliamsT/Pages/WilliamsT.aspx">Trevor
                            William, University of Colorado School of Medicine</a></p>
                    <p><a href="https://www.vasci.umass.edu/research-faculty/jesse-mager">Jesse Mager, University of
                        Massachusetts Amherst</a></p>
                    <p><a href="https://www.mskcc.org/research-areas/labs/elizabeth-lacy">Elizabeth Lacy, Memorial Sloan
                        Kettering Cancer Center</a></p>
                    <p>
                        <a href="https://projectreporter.nih.gov/project_info_description.cfm?aid=9206831&icde=32235262&ddparam=&ddvalue=&ddsub=&cr=1&csb=default&cs=ASC">Dr.
                            Jeremy Reiter, University of California San Francisco</a></p>
                </div>
            </div>

            <div class="row mb-5">
                <div class="col-12">
                    <h2>2D Imaging </h2>
                    <div class="row mb-5">
                        <div class="col-6">
                            <h3>Embryo LacZ</h3>
                            <img src="${baseUrl}/img/Tmem100_het.jpeg" height="200"/>
                            <p>
                                <a href="${baseUrl}/imageComparator?acc=MGI:1915138&anatomy_term=TS20%20embryo%20or%20Unassigned&parameter_stable_id=IMPC_ELZ_064_001">Tmem100</a>
                            </p>
                            <p> The majority of IMPC knockout strains replace a critical protein coding exon with a LacZ
                                gene expression reporter element. Heterozygote E12.5 embryos from IMPC strains are
                                treated to determine in situ expression of the targeted gene.</p>
                            <p>See all genes with <a
                                    href='${cmsBaseUrl}/understand/impc-image-search/?procedure=Embryo LacZ'>embryo
                                LacZ images</a>.</p>
                        </div>
                        <div class="col-6">
                            <h3>Embryo Gross Morphology</h3>
                            <img src="${baseUrl}/img/Acvr2a_hom.jpeg" height="200" style="width:auto"/>
                            <p>WT / <a
                                    href="${baseUrl}/imageComparator?acc=MGI:102806&parameter_stable_id=IMPC_GEO_050_001">Acvr2a</a>
                            </p>
                            <p>Gross morphology of embryos from lethal and subviable strains highlights which biological
                                systems are impacted when the function of a gene is turned off. The developmental stage
                                selected is determined by an initial assessment.</p>
                            <p>See embryo gross morphology images for
                                <a href='${cmsBaseUrl}/understand/impc-image-search/?procedure=Gross Morphology Embryo E12.5'>E12.5</a>,
                                <a href='${cmsBaseUrl}/understand/impc-image-search/?procedure=Gross Morphology Embryo E14.5-E15.5'>E14.5-E15.5</a>,
                                <a href='${cmsBaseUrl}/understand/impc-image-search/?procedure=Gross Morphology Embryo E18.5'>E18.5</a>.
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row mb-5">
                <div class="col-12">
                    <h2>3D Imaging </h2>
                    <img class="w-100" alt="IEV" src="${baseUrl}/img/IEV.png"/>
                    <p> The embryonic and perinatal lethal pipeline comprises several 3D imaging modalities to quantify
                        aberrant morphology that could not be determined by gross inspection. Images acquired by
                        micro-CT and OPT are available via our Interactive Embryo Viewer (IEV). </p>
                    <div>
                        <a class="btn btn-primary"
                           href="${cmsBaseUrl}/embryoviewer/?mgi=MGI:2147810&pid=203&h=undefined&s=on&c=off&a=off&o=vertical&zoom=0&sb=600&wn=146521&wx=54&wy=66&wz=68&wl=0&wu=254&mn=129313&mx=52&my=68&mz=108&ml=0&mu=205">Tmem132a</a>
                        <a class="btn btn-primary"
                           href="${cmsBaseUrl}/embryoviewer/?mgi=MGI:1916804&mod=203&h=624&wt=klf7-tm1b-ic/16.5b_5553715&mut=klhdc2-tm1b-ic/21.1f_5578050&s=on&c=off&a=off&wx=64&wy=117&wz=178&mx=44&my=107&mz=154&wl=0&wu=255&ml=0&mu=255&o=vertical&zoom=0">Klhdc2</a>
                        <a class="btn btn-primary"
                           href="${cmsBaseUrl}/embryoviewer/?mgi=MGI:1195985&mod=203&h=561&wt=Population%20average&mut=AAPN_K1026-1-e15.5&s=off&c=off&a=on&wx=94&wy=64&wz=177&mx=94&my=70&mz=137&wl=0&wu=255&ml=0&mu=254&o=vertical&zoom=0&wto=jacobian&muto=none">Cbx4</a>
                        <a class="btn btn-primary"
                           href="${cmsBaseUrl}/embryoviewer/?mgi=MGI:102806&pid=203&h=569&s=on&c=off&a=off&o=vertical&zoom=0&sb=600&wn=ABIF_K1339-10-e15.5&wx=79&wy=107&wz=141&wl=0&wu=255&mn=ABIF_K1267-19-e15.5&mx=79&my=107&mz=142&ml=0&mu=255">Acvr2a</a>
                        <a class="btn btn-primary" href="${cmsBaseUrl}/understand/advanced-tools/embryo-viewer/"> See
                            all </a>
                    </div>
                </div>
            </div>

            <div class="row mb-5">
                <div class="col-12">
                    <h2 class="title"> Vignettes </h2>

                    <div id="sliderDiv">
                        <div id="slider">
                            <div id="sliderHighlight" class="slider" imgUrl="${baseUrl}/embryo/vignettes"></div>
                            <div>
                                <span class="control_next half left">></span>
                                <span class="control_prev half right"><</span>
                            </div>
                        </div>
                        <div id="sliderControl" class="sliderControl">
                            <ul>
                                <li id="item0"><img src="${baseUrl}/img/vignettes/chtopPink.jpg"/>
                                    <p class="embryo-caption"> Chtop has been shown to recruit the histone-methylating
                                        methylosome to genomic regions containing
                                        5-Hydroxymethylcytosine, thus affecting gene expression. Chtop mutants showed
                                        complete preweaning lethality with
                                        no homozygous pups observed. High resolution episcopic microscopy (HREM)
                                        imaging, revealed decreased number of
                                        vertebrae, abnormal joint morphology and edema. <t:vignetteLink
                                                geneId="MGI:1913761"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Chtop<tm1a(EUCOMM)Wtsi></t:formatAllele></p>
                                </li>
                                <li id="item1"><img src="${baseUrl}/img/vignettes/Kldhc2.png"/>
                                    <p class="embryo-caption"> The Kldhc2 gene is located within a locus linked to an
                                        automsomal dominant disease that leads to fibro-fatty replacement of right
                                        ventricle myocardium leading to arrythmias (ARVD3 ; OMIM) <t:vignetteLink
                                                geneId="MGI:1916804"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Klhdc2<tm1b(EUCOMM)Hmgu></t:formatAllele></p>
                                </li>
                                <li id="item2"><img src="${baseUrl}/img/vignettes/Acvr2aMicroCT.png"/>
                                    <p class="embryo-caption">Activin receptor IIA is a receptor for activins, which are
                                        members of the TGF-beta superfamily involved in diverse biological processes.
                                        Acvr2a mutants are subviable with most pups dying before postnatal day 7.
                                        <t:vignetteLink geneId="MGI:102806"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Acvr2a<tm1.1(KOMP)Vlcg></t:formatAllele></p>
                                </li>
                                <li id="item3"><img src="${baseUrl}/img/vignettes/cbx4.png"/>
                                    <p class="embryo-caption">Chromobox 4 is in the polycomb protein family that are key
                                        regulators of transcription and is reported to be upregulated in lung bud
                                        formation and required for thymus development <t:vignetteLink
                                                geneId="MGI:1195985"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Cbx4<tm1.1(KOMP)Vlcg></t:formatAllele></p>
                                </li>
                                <li id="item4"><img src="${baseUrl}/img/vignettes/tmem100.png"/>
                                    <p class="embryo-caption">Transmembrane Protein 100 functions downstream of the
                                        BMP/ALK1 signaling pathway. Tmem100 mutants showed complete preweaning lethality
                                        and were also lethal at E12.5. <t:vignetteLink
                                                geneId="MGI:1915138"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Tmem100<tm1e.1(KOMP)Wtsi></t:formatAllele>
                                    </p>
                                </li>
                                <li id="item5"><img src="${baseUrl}/img/vignettes/eye4.png"/>
                                    <p class="embryo-caption"> Eyes absent transcriptional coactivator and phosphatase 4
                                        is associated with a variety of developmental defects including hearing loss.
                                        Eya4 mutants showed complete preweaning lethality with no homozygous pups
                                        observed. <t:vignetteLink geneId="MGI:1337104"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Eya4<tm1b(KOMP)Wtsi></t:formatAllele></p>
                                </li>
                                <li id="item6"><img src="${baseUrl}/img/vignettes/tox3MRI.png"/>
                                    <p class="embryo-caption">Tox High Mobility Group Box Family Member 3 is a member of
                                        the HMG-box family involved in bending and unwinding DNA. Tox3 mutants have
                                        partial preweaning lethality with 1/3 of the pups dying before P7.
                                        <t:vignetteLink geneId="MGI:3039593"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Tox3<tm1b(KOMP)Mbp></t:formatAllele></p>
                                </li>
                                <li id="item7"><img src="${baseUrl}/img/vignettes/Rsph9Slides.png"/>
                                    <p class="embryo-caption">Radial spoke head protein 9 is a component of the radial
                                        spoke head in motile cilia and flagella. Rsph9 mutants showed partial
                                        pre-weaning lethality but viable to P7. <t:vignetteLink
                                                geneId="MGI:1922814"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Rsph9<tm1.1(KOMP)Vlcg></t:formatAllele></p>
                                </li>

                                <li id="item8"><img src="${baseUrl}/img/vignettes/Pax7.png"/>
                                    <p class="embryo-caption">Pax 7 is a nuclear transcription factor with DNA-binding
                                        activity via its paired domain. It is involved in specification of the neural
                                        crest and is an upstream regulator of myogenesis during post-natal growth and
                                        muscle regeneration in the adult. <t:vignetteLink
                                                geneId="MGI:97491"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Pax7<tm1.1(KOMP)Vlcg></t:formatAllele></p>
                                </li>

                                <li id="item9"><img src="${baseUrl}/img/vignettes/Svep1.jpg"/>
                                    <p class="embryo-caption">Svep1 codes for an uncharacterized protein named after the
                                        multiple, extra-cellular domains identified in the sequence. Homozygotes fail
                                        between E18.5 and birth. <t:vignetteLink
                                                geneId="MGI:1928849"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Svep1<tm1b(EUCOMM)Hmgu/J></t:formatAllele>
                                    </p>
                                </li>

                                <li id="item10"><img src="${baseUrl}/img/vignettes/Strn3.jpg"/>
                                    <p class="embryo-caption">Striatins act as both calcium-dependent signaling proteins
                                        and scaffolding proteins, linking calcium-sensing signaling events with cellular
                                        action. Lethality in Strn3 homozygotes occurs around E15.5. <t:vignetteLink
                                                geneId="MGI:2151064"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Strn3<tm1b(KOMP)Wtsi/J></t:formatAllele></p>
                                </li>

                                <li id="item11"><img src="${baseUrl}/img/vignettes/Rab34.jpg"/>
                                    <p class="embryo-caption">Rab34 is a member of the RAS oncogene family, involved in
                                        intracellular vesicle transport. Rab34 homozygotes are subviable at E18.5.
                                        <t:vignetteLink geneId="MGI:104606"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Rab34<tm1b(EUCOMM)Hmgu/J></t:formatAllele>
                                    </p>
                                </li>

                                <li id="item12"><img src="${baseUrl}/img/vignettes/Cox7c.jpg"/>
                                    <p class="embryo-caption">Cytochrome c oxidase subunit VIIc (Cox7c) is a
                                        nuclear-encoded regulatory component of cytochrome c oxidase. Homozygous mutants
                                        do not survive between E15.5 and E18.5. <t:vignetteLink
                                                geneId="MGI:103226"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Cox7c<tm1b(KOMP)Mbp></t:formatAllele></p>
                                </li>

                                <li id="item13"><img src="${baseUrl}/img/vignettes/Bloc1s2.jpg"/>
                                    <p class="embryo-caption">Bloc1s2 functions in the formation of lysosome-related
                                        organelles through the BLOC-1 complex, with lethality occurring in knockouts
                                        around E15.5.<t:vignetteLink geneId="MGI:1920939"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Bloc1s2<tm1.1(KOMP)Mbp></t:formatAllele></p>
                                </li>

                                <li id="item14"><img src="${baseUrl}/img/vignettes/Slc39a8.png"/>
                                    <p class="embryo-caption">Solute carrier (metal ion transporter) family 39 member 8
                                        (Slc39a8) mutants are small at E14.5 and show heart defects.<t:vignetteLink
                                                geneId="MGI:1914797"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Slc39a8<tm1b(EUCOMM)Wtsi></t:formatAllele>
                                    </p>
                                </li>

                                <li id="item15"><img src="${baseUrl}/img/vignettes/Kdm8.png"/>
                                    <p class="embryo-caption">Lysine (K)-specific demethylase 8 (Kdm8) is predicted to
                                        have dual functions as a histone demethylase and as a protein hydroxylase and is
                                        also known as Jmjd5. Tm1b mutants show developmental delay and fail to turn at
                                        E9.5.<t:vignetteLink geneId="MGI:19224285"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Kdm8<tm1b(EUCOMM)Wtsi></t:formatAllele></p>
                                </li>

                                <li id="item16"><img src="${baseUrl}/img/vignettes/Atg3.png"/>
                                    <p class="embryo-caption">Autophagy related 3 (Atg3) mutants show cardio-vascular
                                        defects at E14.5.<t:vignetteLink geneId="MGI:1915091"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Atg3<tm1b(EUCOMM)Hmgu></t:formatAllele></p>
                                </li>

                                <li id="item17"><img src="${baseUrl}/img/vignettes/gfpt1.png"/>
                                    <p class="embryo-caption">Glutamine:fructose-6-phosphate amidotransferase 1 (Gfpt1)
                                        mutants show developmental delay and fail to turn at E9.5.<t:vignetteLink
                                                geneId="MGI:95698"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Gfpt1<tm1b(EUCOMM)Wtsi></t:formatAllele></p>
                                </li>
                                <li id="item18"><img src="${baseUrl}/img/vignettes/Gygmutants.png"/>
                                    <p class="embryo-caption">Glycogenin is an enzyme that converts glucose to glycogen.
                                        Glycogenin catalyzes UDP-alpha-D-glucose + glycogenin &rlhar; UDP +
                                        alpha-D-glucosylglycogenin. The enzyme is a homodimer of 37 kDa subunits.
                                        Mutations in human GYG1 are associated with Glyocgen Storage Disease XV and
                                        Polyglucosan Body Myopathy 2 (<a
                                                href="http://www.omim.org/entry/603942">OMIM</a>). Homozygous null Gyg
                                        mice die between birth and weaning but were found in normal proportions at
                                        E18.5. Mutants were indistinguishable from littermates at E12.5, E15.5 or E18.5
                                        but analysis of microCT images revealed obvious cardiac abnormalities, enlarged
                                        thymus and abnormal nervous system morphology. This is the first reported Gyg
                                        mouse mutant.
                                        <t:vignetteLink geneId="MGI:1351614"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Gyg<tm1b(KOMP)Wtsi></t:formatAllele></p>
                                </li>
                                <li id="item19"><img src="${baseUrl}/img/vignettes/Tmem132aE15.5 sag.png"/>
                                    <p class="embryo-caption">Transmembrane protein132a is transmembrane protein of
                                        unknown function.
                                        Homozygous null mutants were viable at normal proportions at E15.5 and E18.5 but
                                        showed obvious and severe defects that were readibly visible by eye. Embryos had
                                        abnormal limb morphology with syndactyly, spina bifida, heart abnormalities.
                                        Some mutants were smaller than littermates.
                                        <t:vignetteLink geneId="MGI:2147810"></t:vignetteLink></p>
                                    <p class="sliderTitle"><t:formatAllele>Tmem132a<tm1b(KOMP)Wtsi></t:formatAllele></p>
                                </li>

                            </ul>
                        </div>
                    </div>

                    <p> These vignettes highlight the utility of embryo phenotyping pipeline and demonstrate how gross
                        morphology, embryonic
                        lacz expression, and high resolution 3D imaging provide insights into developmental biology.
                        Clicking on an image will provide
                        more information. </p>
                </div>
            </div>


            <div class="row mb-5">
                <div class="col-12">
                    <h2 class="title ">IMPC Embryonic Pipeline</h2>
                    <div><a href="${cmsBaseUrl}/impress"><img src="${baseUrl}/img/embryo_impress.png"/> </a></div>
                </div>
            </div>

            <div class="row mb-5">
                <div class="col-12">
                    <jsp:include page="paper_frag.jsp"></jsp:include>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage-landing>

