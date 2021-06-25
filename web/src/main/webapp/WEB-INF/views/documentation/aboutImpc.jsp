<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">About IMPC</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; About IMPC</jsp:attribute>
  <jsp:attribute name="bodyTag"><bold id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
      <link href="${baseUrl}/css/impc-doc.css" rel="stylesheet" type="text/css" />
      <style>
        ul.about li {
          list-style-type: square;
          margin-left: 15px;
        }
        .secSep {
          margin-top: 40px;
        }
        div#threeCols {
          position: relative;
        }
        div.fcol {
          color: black;
          font-weight: bold;
          font-size: 16px;
          float: left;
          text-align: center;
          height: 650px;
          background-color: #F2F2F2;
          margin: 5px;
          padding: 10px 5px;
          border-radius: 5px;
        }
        div.fcolCap {
          font-size: 18px;
          height: 50px;
          border-bottom: 1px solid grey;
          width: 100%;
        }
        div.fcol li {
          text-align: left;
          margin-left: 30px;

        }
        div.fcol > ul {
          margin-top: 20px;
        }
        div#lcol {
          width: 23%;
        }
        div#mcol {
          width: 20%;
        }
        div#rcol {
          width: 47%;
        }
        img#imglcol {
          margin-top: 150px;
          margin-left: 50px;
        }
        img#imgmcol {
          margin-top: 280px;
          margin-left: 3px;
        }
        img#imgrcol {
          margin-top: 10px;
          margin-left: -100px;
        }
        span.work {
          cursor: pointer;
          color: #0978A1;
        }
        div.hideme {

          display: none;
          border: 1px solid grey;
          border-radius: 5px;
          padding: 20px;
          margin-bottom: 15px;
        }
        div.showme {
          display: block;
        }
        span#es, span#mouse, span#crispr {
          float: left;
          margin-right: 20px;
          padding: 20px 10px 10px 10px;
        }
        div#impress iframe {min-width: 960px; min-height: 600px;}
        ul#derived li {
            list-style-type: square;
            margin-left: 50px;
        }
        img#cmap {
            display: block;
            margin: 5px auto;
        }
      </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <a name="whatisimpc"></a>
    <h1>What is IMPC?</h1>

    <img src="${baseUrl}/img/IMPC_map6.png" /><br>
    <h3>IMPC Members</h3>
    <b>The IMPC is currently composed of 19 research institutions and 5 national funders from 11 countries.</b><p>

    <ul class="about">
      <li>Global infrastructure recognised by the G7</li>
      <li>Creating 20,000 knockout mouse strains on a single background strain</li>
      <li>Characterizing each through a standardized phenotyping protocol</li>
      <li>Integrating the data to existing mouse and human disease resources</li>
      <li>Providing Strains and phenotype data for use by the research community</li>
    </ul>

    <div class="secSep"></div>
    <a name="whatdoesimpcdo"></a>
    <h1>What does IMPC do?</h1>

    <div id="threeCols">
      <div id="lcol" class="fcol"><div class="fcolCap">Mouse production and phenotyping</div>
          <ul><li>Standardized allele production and phenotyping pipelines</li></ul>
        <img id="imglcol" src="${baseUrl}/img/mouseProdPheno.png" />
      </div>
      <div id="mcol" class="fcol"><div class="fcolCap">Data analysis</div>
        <ul class="about"><li>Quality control</li>
        <li>Statistical analysis</li>
        <li>Disease association</li></ul>
        <img id="imgmcol" src="${baseUrl}/img/dataAnalysis.png" />
        </div>
      <div id="rcol" class="fcol"><div class="fcolCap">Data distribution</div>
        <ul class="about"><li>Free data access and visualizationl</li>
          <li>Embryonic and adult data</li>
          <li>Human disease association</li>
        <li>ES cells and mouse ordering</li></ul>
        <img id="imgrcol" src="${baseUrl}/img/dataDistribution.png" />
      </div>
      <div style="clear: both"></div>

    </div>

    <div class="secSep"></div>
    <a name="howdoesimpcwork"></a>
    <h1>How does IMPC work?</h1>

    <div ><span class="work alleleDesign">Allele design</span>
      <div class="hideme">
          <div>The IMPC consortium is using different targeting strategies that have different and complementary properties to produce Knockout alleles.
            Below is a general outline of all the targeting strategies used by IMPC teams.
          </div>
          <div id="tabs">
              <ul>

                  <li><a href="#tabs-1">Knockout-first</a></li>
                  <li><a href="#tabs-2">Velocigene null allele design</a></li>
                  <li><a href="#tabs-3">Cas9 CRISPR allele</a></li>
              </ul>


              <div id="tabs-1">
                  <br>
                  <div>This strategy relies on the identification of a 'critical' exon common to all transcript variants that, when deleted, creates a frame-shift mutation.
                      The Knockout first allele is flexible and can produce reporter knockouts, conditional knockouts, and null alleles following exposure to site-specific recombinases Cre and Flp.
                      Promoterless and promoter-driven targeting cassettes are used for the generation of a 'Knockout-first allele' in C57BL/6N embryonic stem cells (<a href="http://europepmc.org/abstract/MED/19525957">Pettitt et al., 2009</a>).<br>
                      These two strategies have been shown to yield different targeting efficiencies (<a href="http://europepmc.org/abstract/MED/21677750">Skarnes et al., 2011</a>).</div>

                  <img src="${baseUrl}/img/promoterless.png" />
                  <img src="${baseUrl}/img/promoter-driven.png" />
                  <img src="${baseUrl}/img/targeted-non-conditional.png" />
                  <div>"Targeted, non-conditional " alleles (tm1e) are missing the downstream loxP site. The 3’ loxP site is often lost due to recombination events in the homology region between the targeting cassette and 3’ loxP site.
                      These mutations cannot be converted to conditional alleles following Flp treatment.</div><br>

                  <div>Standard deletion alleles with the promoter-driven targeting cassette:</div>
                  <img src="${baseUrl}/img/targeted-deletion.png" />
                  <div>
                      Number of available derivative alleles:
                      <ul id="derived">
                          <li>tm1a: KO first allele (reporter-tagged insertion allele): <span id="tm1a">0</span></li>
                          <li>tm1b: Reporter-tagged deletion allele (post-Cre): <span id="tm1b">0</span></li>
                          <li>tm1c: Conditional allele (post-Flp): <span id="tm1c">0</span></li>
                          <li>tm1d: Deletion allele (post-Flp and Cre with no reporter): <span id="tm1d">0</span></li>
                          <li>tm1e: targeted, non-conditional allele: <span id="tm1e">0</span></li>
                          <li>tm1: Reporter-tagged deletion allele (with selection cassette): <span id="tm1">0</span></li>
                          <li>tm1.1: Reporter-tagged deletion allele (post Cre, with no selection cassette): <span id="tm11">0</span></li>
                          <li>tm1.2: Reporter-tagged deletion allele (post Flp, with no reporter and selection cassette): <span id="tm12">0</span></li>
                      </ul>
                  </div>
              </div>
              <div id="tabs-2">

                  <img src="${baseUrl}/img/velocigene.jpg" />
                  <div>In most cases this design will result in complete null alleles that delete the entire protein coding sequence of the target gene.
                      This allele design can be applied to any gene transcribed by RNA polymerase II regardless of its size, intron-exon structure, RNA splicing pattern, or protein-coding capacity
                      (<a href="http://europepmc.org/abstract/MED/12730667">Valenzuela et al., 2003</a>).</div>
                  <br>
                  <div>VelociGene lines available: <span id="velocigene"></span></div>
              </div>
              <div id="tabs-3">
                    <br>
                <h3>IMPC alleles viable for high throughput pipieline</h3>
                <img src="${baseUrl}/img/crispr-viable.png" />
                <ul><li>(i) Small Deletions (single cut strategy)</li>
                    <li>(ii) Exon Deletion / large Deletions (2 cut strategy)</li>
                </ul>

                <h3>Alleles on request</h3>
                <img src="${baseUrl}/img/crispr-on-request.png" />
                <ul>
                    <li>(i) Loxp-flanked critical regions</li>
                    <li>(ii) Point Mutations</li><li>(iii) Conditional & lacZ reporter Allele - Insertion of dsVector</li>
                </ul>
                <br>
                <div>
                    CRISPR alleles available:
                    <ul>
                        <li class="crisprProduced">Total: <span id="crisprTotal"></span></li>
                        <li class="crisprProduced">NHEJ Alleles (small deletions): <span id="nhej"></span></li>
                        <li class="crisprProduced">Large Deletion/ Exon deletion: <span id="deletion"></span></li>
                    </ul>
                </div>
              </div>
          </div>
      </div>
    </div>
    <div ><span class="work">Coordinated production</span>
      <div class="hideme">
          <h3>Coordination</h3>

          <p>The IMPC is a confederation of international mouse phenotyping projects working towards the agreed goals of the consortium:
              To undertake the phenotyping of 20,000 mouse mutants over a ten year period, providing the first functional annotation of a mammalian genome.</p>

          <p>The IMPC Steering Committee provides the governance for the overall consortium. Participants are tasked with making key strategic decisions including
              selection of participating organizations, approving and coordinating key operational decisions such as phenotyping platforms and pipeline used,
              quality assurance and operating standards, and IT organization. Membership provides stakeholders with an opportunity to influence key activities as they develop.</p>
          <p></p>

          <img alt="" src="${baseUrl}/documentation/img/impc-coordination.png" usemap="#Map" id="cmap" />

          <map id="Map" name="Map"><area coords="269,17,418,151" href="/about-impc/impc-members" shape="rect"> <area coords="20,24,174,149" href="/about-impc/coordination/panel-scientific-consultants" shape="rect"> <area coords="20,242,186,341" href="/about-impc/coordination/phenotyping-steering-group" shape="rect"> <area coords="33,380,178,487" href="/about-impc/coordination/mta-and-line-exchange" shape="rect"> <area coords="396,208,578,279" href="/about-impc/coordination/production-steering-group" shape="rect"> <area coords="267,381,412,486" href="/about-impc/coordination/imits-steering-group" shape="rect"> <area coords="516,17,664,149" href="/about-impc/coordination/finance-committee" shape="rect"> <area coords="483,240,662,344" href="/about-impc/coordination/data-analysis-advisory-committee" shape="rect"> <area coords="499,380,646,488" href="/about-impc/coordination/statistics-technical-group" shape="rect"> <area coords="5,179,674,215" href="/about-impc/coordination/communications-working-group" shape="rect"></map>

      </div>
    </div>
    <div ><span class="work">Embryo and Adult Mouse phenotyping protocols</span>
      <div class="hideme" id="impress">
        <%--<iframe src="https://www.mousephenotype.org/impress"></iframe>--%>
        <%--<iframe scrolling="no" src="https://www.mousephenotype.org/impress" style="z-index:-10; height: 1500px; margin-top: -450px"></iframe>--%>

          <h3>The Adult and Embryonic Phenotype Pipeline</h3>

          <p>The IMPC (International Mouse Phenotyping Consortium) core pipeline describes the phenotype pipeline that has been agreed by the research institutions.
            The pipeline is currently in development. The protocols in the core IMPC Pipeline are currently being developed by the IMPC phenotyping working groups
            and the current versions on this site are still under final review. The phenotyping working groups are working closely with the data wranglers to
            complete an agreed first version. Updates on the progress of this will be available through IMPReSS.</p>

          You can click on the protocols below for more information.<p></p>


          <img src="https://www.mousephenotype.org/impress/images/pipeline_horizontal_vE8.gif" style="margin:10px 0; min-width:1020px !important" usemap="#meowmap" alt="The IMPC Pipeline" height="814" border="0" width="1020">
          <map name="meowmap" id="meowmap">
            <!-- #$-:Image map file created by GIMP Image Map plug-in -->
            <!-- #$-:GIMP Image Map plug-in by Maurits Rijk -->
            <!-- #$-:Please do not edit lines starting with "#$" -->
            <!-- #$VERSION:2.3 -->
            <!-- Fertility & Viability -->
            <area shape="rect" coords="075,065,210,090" href="https://www.mousephenotype.org/impress/protocol/105/7" alt="Fertility">
            <area shape="rect" coords="220,065,360,090" href="https://www.mousephenotype.org/impress/protocol/154/7" alt="Viability">
            <!-- E9.5 -->
            <area shape="rect" coords="640,95,825,120" href="https://www.mousephenotype.org/impress/protocol/221/7" alt="Histopathology Embryo">
            <area shape="rect" coords="831,94,1013,118" href="https://www.mousephenotype.org/impress/protocol/222/7" alt="Histopathology Placenta">
            <area shape="rect" coords="640,125,825,150" href="https://www.mousephenotype.org/impress/protocol/252/7" alt="Gross Morphology Embryo">
            <area shape="rect" coords="830,125,1010,150" href="https://www.mousephenotype.org/impress/protocol/190/7" alt="Gross Morphology Placenta">
            <area shape="rect" coords="830,155,905,180" href="https://www.mousephenotype.org/impress/protocol/177/7" alt="Viability">
            <!-- E12.5 -->
            <area shape="rect" coords="055,280,230,300" href="https://www.mousephenotype.org/impress/protocol/172/7" alt="Embryo LacZ">
            <area shape="rect" coords="335,280,425,300" href="https://www.mousephenotype.org/impress/protocol/172/7" alt="Embryo LacZ">
            <area shape="rect" coords="335,220,515,245" href="https://www.mousephenotype.org/impress/protocol/261/7" alt="Gross Morphology Embryo">
            <area shape="rect" coords="335,250,515,270" href="https://www.mousephenotype.org/impress/protocol/194/7" alt="Gross Morphology Placenta">
            <area shape="rect" coords="435,280,515,300" href="https://www.mousephenotype.org/impress/protocol/178/7" alt="Viability">
            <!-- E14.5-E15.5 -->
            <area shape="rect" coords="640,250,825,275" href="https://www.mousephenotype.org/impress/protocol/262/7" alt="Gross Morphology Embryo">
            <area shape="rect" coords="830,250,1010,275" href="https://www.mousephenotype.org/impress/protocol/195/7" alt="Gross Morphology Placenta">
            <area shape="rect" coords="830,280,905,300" href="https://www.mousephenotype.org/impress/protocol/179/7" alt="Viability">
            <!-- E18.5 -->
            <area shape="rect" coords="640,345,825,365" href="https://www.mousephenotype.org/impress/protocol/263/7" alt="Gross Morphology Embryo">
            <area shape="rect" coords="830,345,1010,365" href="https://www.mousephenotype.org/impress/protocol/196/7" alt="Gross Morphology Placenta">
            <area shape="rect" coords="830,375,905,395" href="https://www.mousephenotype.org/impress/protocol/180/7" alt="Viability">
            <!-- Weight Curve -->
            <area shape="rect" coords="055,480,1013,500" href="https://www.mousephenotype.org/impress/protocol/103/7" alt="Body Weight">
            <!-- Week 9 -->
            <area shape="rect" coords="060,545,145,570" href="https://www.mousephenotype.org/impress/protocol/81/7" alt="Open Field">
            <area shape="rect" coords="060,575,105,595" href="https://www.mousephenotype.org/impress/protocol/186/7" alt="CSD">
            <area shape="rect" coords="060,605,155,625" href="https://www.mousephenotype.org/impress/protocol/83/7" alt="Grip Strength">
            <!-- Week 10 -->
            <area shape="rect" coords="181,605,318,625" href="https://www.mousephenotype.org/impress/protocol/176/7" alt="Acoustic Startle">
            <!-- Week 11 -->
            <area shape="rect" coords="330,605,414,625" href="https://www.mousephenotype.org/impress/protocol/240/7" alt="Calorimetry">
            <!-- Week 12 -->
            <area shape="rect" coords="426,575,497,595" href="https://www.mousephenotype.org/impress/protocol/109/7" alt="Echo">
            <area shape="rect" coords="426,605,497,625" href="https://www.mousephenotype.org/impress/protocol/108/7" alt="ECG">
            <!-- Week 13 -->
            <area shape="rect" coords="511,540,666,580" href="https://www.mousephenotype.org/impress/protocol/88/7" alt="Challenge">
            <area shape="rect" coords="511,590,666,625" href="https://www.mousephenotype.org/impress/protocol/87/7" alt="IPGTT">
            <!-- Week 14 -->
            <area shape="rect" coords="683,545,734,570" href="https://www.mousephenotype.org/impress/protocol/91/7" alt="X-ray">
            <area shape="rect" coords="684,575,888,595" href="https://www.mousephenotype.org/impress/protocol/149/7" alt="ABR">
            <area shape="rect" coords="685,605,875,625" href="https://www.mousephenotype.org/impress/protocol/90/7" alt="Body Composition">
            <!-- Week 15 -->
            <area shape="rect" coords="899,585,1012,625" href="https://www.mousephenotype.org/impress/protocol/94/7" alt="Eye Morphology">
            <!-- Week 16 -->
            <area shape="rect" coords="055,710,150,730" href="https://www.mousephenotype.org/impress/protocol/150/7" alt="Hematology">
            <area shape="rect" coords="055,740,150,760" href="https://www.mousephenotype.org/impress/protocol/107/7" alt="Adult LacZ">
            <area shape="rect" coords="162,710,260,750" href="https://www.mousephenotype.org/impress/protocol/182/7" alt="Clinical Chemistry">
            <area shape="rect" coords="270,710,367,750" href="https://www.mousephenotype.org/impress/protocol/183/7" alt="Insulin Blood Level">
            <area shape="rect" coords="380,710,490,750" href="https://www.mousephenotype.org/impress/protocol/174/7" alt="Immunophenotyping">
            <area shape="rect" coords="505,710,600,740" href="https://www.mousephenotype.org/impress/protocol/100/7" alt="Heart Weight">
            <area shape="rect" coords="612,710,762,750" href="https://www.mousephenotype.org/impress/protocol/181/7" alt="Gross Pathology">
            <area shape="rect" coords="775,710,877,770" href="https://www.mousephenotype.org/impress/protocol/101/7" alt="Tissue Embedding">
            <area shape="rect" coords="885,710,1010,770" href="https://www.mousephenotype.org/impress/protocol/102/7" alt="Histopathology">
            <area alt="OPT E9.5" title="" href="https://www.mousephenotype.org/impress/protocol/202/7" shape="poly" coords="730,157,766,157,765,176,730,178">
            <area alt="MicroCT E14.5-E15.5" title="" href="https://www.mousephenotype.org/impress/protocol/203/7" shape="poly" coords="729,280,765,283,767,302,730,301">
            <area style="cursor: pointer;" alt="MicroCT E18.5" title="" href="https://www.mousephenotype.org/impress/protocol/204/7" shape="poly" coords="787,376,821,376,821,394,787,396">
          </map>

      </div>
    </div>
    <div><span class="work">Statistics to Phenotype</span>
      <div class="hideme"><h3>Statistics to Phenotype</h3>

          The selection of the statistical method is an important step in the process of phenotype data analysis and is dependent on the experimental implementation, and the variable characteristics (e.g. continuous or categorical).

          <p>The statistical analysis was done using an R package developed for IMPC called <a href="http://bioconductor.org/packages/release/bioc/html/PhenStat.html">PhenStat</a>.<br>
          PhenStat is a statistical analysis tool suite developed based on known variation in experimental workflow and design of phenotyping pipelines (<a href="http://europepmc.org/abstract/MED/26147094">Kurbatova N et al, 2015)</a>.
          <p></p>
          <div>• <b>Categorical data analysis</b> was performed using a Fisher’s Exact test. Concerning continuous data, it has been shown that gender, weight, and environmental effects (batch) are a significant source of variation. See
              <ul>
                  <li><a href="http://europepmc.org/abstract/MED/22829707">Karp et al., 2012</a></li>
                  <li><a href="http://europepmc.org/abstract/MED/25343444">Karp et al., 2014</a></li>
              </ul>
          </div>
          <p></p><p></p>
          <div>•	<b>Continuous data analysis</b> was performed with the PhenStat Mixed Model (MM) framework which uses linear mixed models where batch is treated as a random effect. Details of the implementation, including decision tree and models descriptions, are available in the <a href="http://goo.gl/tfbA5k">PhenStat package user's guide</a>.</div>  

          <div>•	For <b>viability and fertility data</b>, the center conducting the experiment use a statistical method appropriate for the breeding scheme used (exact details are available on the IMPC data portal) and supplied the analysis results to the IMPC.</div>
          <p></p><p></p>

          Following statistical assessment, if the mutant genotype effect represents a significant change from the control group, then the IMPC pipeline will attempt to associate a Mammalian Phenotype (MP) term to the data.
          The particular MP term(s) defined for a parameter are maintained in IMPReSS. Frequently, the term indicates an increase or decrease of the parameter measured.
          more details about how IMPC uses statistics and call for phenotypes can be found in the
          <a href="http://dev.mousephenotype.org/data/documentation/doc-method">documentation section</a>.

      </div>
    </div>
    <div ><span class="work">Human disease association</span>
      <div class="hideme"><%@ include file="disease-help.jsp" %></div>
    </div>


    <script>
      $(document).ready(function(){

        $('span.work').click(function(){

          var sib = $(this).siblings('div.hideme');
          if ( sib.hasClass("showme") ){
            sib.removeClass('showme');
          }
          else {
            sib.addClass('showme');
            if ( $(this).hasClass('alleleDesign') ){
                $( "#tabs" ).tabs({ active: 0 });

                _fetchVelocigenes();
                _fetchKoFirst();
                _fetchCrispr();

//                $.ajax({
//                'url' : 'http://wp-np2-e1:8090/mi/impc/dev/solr/allele2/select',
//                'data' : 'q=type:Allele&facet=on&facet.field=es_cell_available&facet.field=mutation_type&facet.mincount=1&facet.limit=-1&rows=0&wt=json',
//                'dataType' : 'jsonp',
//                'jsonp' : 'json.wrf',
//                timeout : 5000,
//                success: function(json) {
//                  var esCells = json.facet_counts.facet_fields.es_cell_available;
//                  var mutationTypes = json.facet_counts.facet_fields.mutation_type;
//
//                  for ( var i=0; i<esCells.length; i++){
//                    if (esCells[i] == "true"){
//                      $('span#es').text("ES cell vailable: " + esCells[i+1]);
//                      break;
//                    }
//                  }
//
//                  for (var j=0; j<mutationTypes.length; j++){
//                    if (mutationTypes[j] == "Endonuclease-mediated"){
//                      $('span#crispr').text("KO CRISPR lines available: " + mutationTypes[j+1]);
//                    }
//                    else if (mutationTypes[j] == "Targeted"){
//                      $('span#mouse').text("Mouse lines available: " + mutationTypes[j+1]);
//                    }
//                  }
//                },
//                error: function(){
//                  console.log("ajax error")
//                }
//              });
            }
          }
        });

        function _fetchVelocigenes(){
            $.ajax({
                'url': 'http://wp-np2-e1:8090/mi/impc/dev/solr/allele2/select',
                'data': 'q=type:Allele AND pipeline:"KOMP-Regeneron"&rows=0&wt=json',
                'dataType': 'jsonp',
                'jsonp': 'json.wrf',
                timeout: 5000,
                success: function (json) {
                    $('span#velocigene').text(json.response.numFound);
                },
                error: function(){
                    console.log("AJAX error fetching number of VelociGene alleles...")
                }
            });
        }
        function _fetchKoFirst(){
            console.log("doing ko")
            $.ajax({
              'url': 'http://wp-np2-e1:8090/mi/impc/dev/solr/allele2/select',
              'data': 'q=type:Allele AND !feature_type:"CpG island" AND mutation_type:Targeted AND (mouse_available:true OR es_cell_available:true)&facet=on&facet.field=allele_type&facet.mincount=1&facet.limit=-1&rows=0&wt=json',
              'dataType': 'jsonp',
              'jsonp': 'json.wrf',
              timeout: 5000,
              success: function (json) {
                var alleleTypes = json.facet_counts.facet_fields.allele_type;
                //var names = ["a", "b", "c", "d", "e", "", ".1", ".2"];
                for ( var i=0; i<alleleTypes.length; i=i+2){
                    var type = "tm1" + alleleTypes[i].replace(".",""); // trouble with "."
                    var count = alleleTypes[i+1];
                    $('span#'+type).text(count);
                }
              },
              error: function(){
                  console.log("AJAX error fetching number of Knock-out-first alleles...")
              }
            });
          }
        function _fetchCrispr(){
            $.ajax({
             'url': 'http://wp-np2-e1:8090/mi/impc/dev/solr/allele2/select',
             'data': 'q=type:Allele AND mutation_type:Endonuclease-mediated&facet=on&facet.field=allele_type&facet.limit=-1&facet.mincount=1&rows=0&wt=json',
             'dataType': 'jsonp',
             'jsonp': 'json.wrf',
             timeout: 5000,
             success: function (json) {
                var mutationTypes = json.facet_counts.facet_fields.allele_type;
                var total = null;
                for ( var i=0; i<mutationTypes.length; i++){
                    if (mutationTypes[i] == "Deletion"){
                        var num = mutationTypes[i+1];
                        $('span#deletion').text(num);
                        total += num;
                    }
                    if (mutationTypes[i] == "NHEJ"){
                        var num = mutationTypes[i+1];
                        $('span#nhej').text(num);
                        total += num;
                    }
                }
                $('span#crisprTotal').text(total);
             },
             error: function(){
                 console.log("AJAX error fetching number of CRISPR alleles...")
             }
            });
        }
      });

    </script>


  </jsp:body>



</t:genericpage>