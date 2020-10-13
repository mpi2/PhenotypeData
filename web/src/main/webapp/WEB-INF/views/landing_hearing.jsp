<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage-landing>

    <jsp:attribute name="title">${pageTitle}</jsp:attribute>
    <jsp:attribute name="pagename">${pageTitle}</jsp:attribute>
    <jsp:attribute name="breadcrumb">${systemName}</jsp:attribute>

    <jsp:attribute name="header">

        <!-- CSS Local Imports -->
		<link href="${baseUrl}/css/alleleref.css" rel="stylesheet"/>
        <link href="${baseUrl}/css/biological_system/style.css" rel="stylesheet"/>

        <%--<!-- JS Imports -->--%>
        <script type='text/javascript'
                src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript'
                src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript'
                src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>


        <style>
            /* Override allele ref style for datatable */
            table.dataTable thead tr {
                display: table-row;
            }


        </style>

	</jsp:attribute>


    <jsp:attribute name="bodyTag">
		<body class="phenotype-node no-sidebars small-header">
	</jsp:attribute>

    <jsp:attribute name="addToFooter">
		<script>
            $(document).ready(function () {
                $('#hearing-genes').DataTable({
                    "bDestroy": true,
                    "searching": false,
                    "bLengthChange": false,
                    "bPaginate": true,
                    "aaSorting": [[0, "asc"]], // 0-based index

                });

                $('#hearing-genes').attr({'style': 'width: 100%'});
            });
        </script>

		<script>
            //ajax chart caller code
            $(document).ready(function () {
                $('.chart').each(function (i, obj) {
                    var graphUrl = $(this).attr('graphUrl');
                    var id = $(this).attr('id');
                    var chartUrl = graphUrl + '&experimentNumber=' + id;
                    console.log(chartUrl);
                    $.ajax({
                        url: chartUrl,
                        cache: false
                    }).done(function (html) {
                        $('#' + id).append(html);
                        $('#spinner_' + id).html('');
                    });
                });
            });
        </script>

	</jsp:attribute>

    <jsp:body>

        <div class="container">
            <div class="row">
                <div class="col-12">
                    <p>The IMPC is hunting unknown genes responsible for hearing loss by screening knockout mice.
                        Worldwide, 360 million people live with mild to profound hearing loss. Notably, 70% hearing loss
                        occurs
                        as an isolated condition (non-syndromic) and 30% with additional
                        phenotypes (syndromic). The vast majority of genes responsible for hearing loss are unknown.
                    </p>
                    <ul>

                        <li> Press releases:
                            <a href="https://www.ebi.ac.uk/about/news/press-releases/hearing-loss-genes/">EMBL-EBI</a>&nbsp;|&nbsp;
                            <a href="https://www.mrc.ac.uk/news/browse/genes-critical-for-hearing-identified/">MRC</a>&nbsp;|&nbsp;
                            <a href="${cmsBaseUrl}/blog/2018/04/06/novel-hearing-loss-genes-identified-in-large-study-by-scientists-across-the-world/">IMPC</a>
                        </li>
                        <li><a href="http://bit.ly/IMPCDeafness">Nature Communications (released 12/10/2017)</a></li>
                        <li>
                            <a href="https://static-content.springer.com/esm/art%3A10.1038%2Fs41467-017-00595-4/MediaObjects/41467_2017_595_MOESM1_ESM.pdf">Supplementary
                                Material</a></li>
                    </ul>
                </div>
            </div>
                <%-- <c:import url="landing_overview_frag.jsp"/> removed as requested by Mike author of hearing paper --%>

            <div class="row">
                <div class="col-12">
                    <h2 id="approach">Approach</h2>

                    <p>In order to identify the function of genes, the consortium uses a series of
                        response (ABR)</a> test conducted
                        at 14 weeks of age. Hearing is assessed at five frequencies – 6kHz, 12kHz, 18kHz, 24kHz and
                        30kHz –
                        as well as a broadband click stimulus. Increased thresholds are indicative of abnormal hearing.
                        Abnormalities in adult ear morphology are
                        recorded as part of the <a
                                href="http://www.mousephenotype.org/impress/protocol/186">Combined SHIRPA and
                            Dysmorphology (CSD)</a> protocol, which
                        includes a response to a click box test (absence is indicative of a strong hearing deficit) and
                        visual inspection for behavioural signs that may indicate vestibular dysfunction e.g. head
                        bobbing or circling.
                    </p>
                    <h4>Procedures that can lead to relevant phenotype associations</h4>
                    <c:import url="landing_procedures_frag.jsp"/>
                </div>
            </div>


            <div class="row">
                <div class="col-12">
                    <h2 class="title">IMPC Deafness Publication</h2>

                    <h3>Hearing loss investigated in 3,006 knockout mouse lines</h3>
                    <p>
                        <a href="http://bit.ly/IMPCDeafness">A large scale hearing loss screen reveals an extensive
                            unexplored genetic landscape for auditory dysfunction.</a>
                    </p>

                    <ul>
                        <li>67 genes identified as candidate hearing loss genes</li>
                        <li>52 genes are not previously associated with hearing loss and encompass a wide
                            range of functions from structural proteins to transcription factors
                        </li>
                        <li>Among the novel candidate genes, <i>Atp2b1</i> is expressed in the inner ear and
                            <i>Sema3f</i> plays a role in sensory hair cell innervation in the cochlea
                        </li>
                        <li>The IMPC will continue screening for hearing loss mutants in its second 5 year
                            phase
                        </li>
                    </ul>

                    <h3>Methods</h3>
                    <p>Response data from the <a href="http://www.mousephenotype.org/impress/protocol/149/7">Auditory
                        Brain Stem response (ABR)</a> test was used – hearing at five frequencies, 6kHz, 12kHz, 18kHz,
                        24kHz and
                        30kHz was measured.</p>
                    <ul>
                        <li>Control wildtype mice from each phenotypic centre included, matched for gender, age,
                            phenotypic pipeline and metadata (e.g. instrument)
                        </li>
                        <li>Our production statistical approach that automatically detects mutants with abnormal hearing
                            was manually curated to yield 67 genes with profound hearing loss
                        </li>
                    </ul>

                    <h3>Gene table</h3>
                    <p>Sixty-seven deafness genes were identified:</p>

                    <table id="hearing-genes" class="table tableSorter">
                        <thead>
                        <tr>
                            <th class="headerSort ">Gene symbol</th>
                            <th class="headerSort">Zygosity</th>
                            <th class="headerSort">Status</th>
                            <th class="headerSort">Hearing loss</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2442934">A730017C20Rik</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1098687">Aak1</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1354713">Acsl4</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:102806">Acvr2a</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1933736">Adgrb1</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1274784">Adgrv1</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:107189">Ahsg</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1924337">Ankrd11</a></td>
                            <td>Het</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1929214">Ap3m2</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1337062">Ap3s1</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:104653">Atp2b1</a></td>
                            <td>Het</td>
                            <td>Novel</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:3588238">B020004J07Rik</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2652819">Baiap2l2</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1915589">Ccdc88c</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:106485">Ccdc92</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1929293">Cib2</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2388124">Clrn1</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:88466">Col9a2</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2444415">Cyb5r2</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:103157">Dnase1</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1914061">Duoxa2</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:3583900">Elmod1</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:95321">Emb</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1914675">Eps8l1</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:99960">Ewsr1</a></td>
                            <td>Het</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:95662">Gata2</a></td>
                            <td>Het</td>
                            <td>Known</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2146207">Gga1</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2387006">Gipc3</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2685519">Gpr152</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1333877">Gpr50</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1914393">Ikzf5</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:96546">Il1r2</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2146574">Ildr1</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:107953">Klc2</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2143315">Klhl18</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2446166">Marveld2</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1914249">Med28</a></td>
                            <td>Het</td>
                            <td>Novel</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1343489">Mpdz</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1339711">Myh1</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:104510">Myo7a</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1933754">Nedd4l</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:103296">Nfatc3</a></td>
                            <td>Het</td>
                            <td>Novel</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:105108">Nin</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1928323">Nisch</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:108077">Nptn</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:97401">Ocm</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2686003">Odf3l2</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2149209">Otoa</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1918248">Phf6</a></td>
                            <td>Het</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:99878">Ppm1a</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1096347">Sema3f</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2150150">Slc4a10</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2149330">Slc5a5</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2384936">Spns2</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1916205">Srrm4</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2442082">Tmem30b</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1921050">Tmtc4</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2181659">Tox</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2139535">Tprn</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1924817">Tram2</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:102944">Ube2b</a></td>
                            <td>Het</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1914378">Ube2g1</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1919338">Ush1c</a></td>
                            <td>Hom</td>
                            <td>Known</td>
                            <td alt="d">Severe</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:1855699">Vti1a</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="b">Mild</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2685541">Wdtc1</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="c">High</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2159407">Zcchc14</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="a">Low</td>
                        </tr>
                        <tr>
                            <td><a href="http://www.mousephenotype.org/data/genes/MGI:2444708">Zfp719</a></td>
                            <td>Hom</td>
                            <td>Novel</td>
                            <td alt="d">Severe</td>
                        </tr>
                        </tbody>
                    </table>

                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <h2>Vignettes</h2>
                    <div class="row">
                        <div class="col-6" style="text-align: center">
                            <h3>Novel, mild hearing loss</h3>
                            <a href="${baseUrl}/genes/MGI:1933736">Adgrb1<sup>tm2a(EUCOMM)Wtsi</sup></a>
                            <div class="chart" id="Adgrb1"
                                 graphUrl="${baseUrl}/chart?accession=MGI:1933736&allele_accession_id=MGI:5006776&zygosity=homozygote&parameter_stable_id=IMPC_ABR_004_001&pipeline_stable_id=MGP_001&phenotyping_center=WTSI&chart_only=true&chart_type=UNIDIMENSIONAL_ABR_PLOT">
                                <div id="spinner_tram2">
                                    <i class="fa fa-refresh fa-spin"></i>
                                </div>
                            </div>
                        </div>

                        <div class="col-6" style="text-align: center">
                            <h3>Known, severe hearing loss</h3>
                            <a href="${baseUrl}/genes/MGI:3583900">Elmod1<sup>tm1b(EUCOMM)Hmgu</sup></a>
                            <div class="chart" id="ush1c"
                                 graphUrl="${baseUrl}/chart?accession=MGI:3583900&allele_accession_id=MGI:5548895&zygosity=homozygote&parameter_stable_id=IMPC_ABR_004_001&pipeline_stable_id=HRWL_001&phenotyping_center=MRC%20Harwell&chart_only=true&chart_type=UNIDIMENSIONAL_ABR_PLOT">
                                <div id="spinner_ush1c">
                                    <i class="fa fa-refresh fa-spin"></i>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-6" style="text-align: center">
                            <h3>Novel, high-frequency hearing loss</h3>
                            <a href="${baseUrl}/genes/MGI:1915589">Ccdc88c<sup>tm1b(KOMP)Mbp</sup></a>
                            <div class="chart" id="wdtc1"
                                 graphUrl="${baseUrl}/chart?accession=MGI:1915589&parameter_stable_id=IMPC_ABR_012_001&chart_type=UNIDIMENSIONAL_ABR_PLOT&pipeline_stable_id=UCD_001&zygosity=homozygote&phenotyping_center=UC%20Davis&strain_accession_id=MGI:2683688&allele_accession_id=NULL-387FB49D6&chart_only=true">
                                <div id="spinner_wdtc1">
                                    <i class="fa fa-refresh fa-spin"></i>
                                </div>
                            </div>
                        </div>

                        <div class="col-6" style="text-align: center">
                            <h3>Novel, severe hearing loss</h3>
                            <a href="${baseUrl}/genes/MGI:2444708">Zfp719<sup>tm1b(EUCOMM)Wtsi</sup></a>
                            <div class="chart" id="zfp719"
                                 graphUrl="${baseUrl}/chart?accession=MGI:2444708&parameter_stable_id=IMPC_ABR_012_001&chart_type=UNIDIMENSIONAL_ABR_PLOT&pipeline_stable_id=MGP_001&zygosity=homozygote&phenotyping_center=WTSI&strain_accession_id=MGI:2159965&allele_accession_id=MGI:5548829&chart_only=true">
                                <div id="spinner_zfp719">
                                    <i class="fa fa-refresh fa-spin"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <h2 class="title">Phenotypes distribution</h2>
                    <div id="phenotypeChart">
                        <script type="text/javascript"> $(function () {  ${phenotypeChart}
                        }); </script>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <div class="section" id="paper">
                        <jsp:include page="paper_frag.jsp"></jsp:include>
                    </div>

                </div>
            </div>

        </div>
    </jsp:body>

</t:genericpage-landing>


