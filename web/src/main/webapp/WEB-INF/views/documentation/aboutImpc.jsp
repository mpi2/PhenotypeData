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
        span#crispr {float: right;}
        div#impress iframe {min-width: 960px; min-height: 600px;}
       
      </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>

    <h1 name="whatisimpc">What is IMPC?</h1>
    <img src="${baseUrl}/img/IMPC_map6.jpg" /><br>
    <h3>IMPC Members</h3>
    <b>The IMPC is currently composed of 17 research institutions and 5 national funders.</b><p>

    <ul class="about">
      <li>Global infrastructure recognised by the G7</li>
      <li>Creating 20,000 knockout mouse strains on a single background strain</li>
      <li>Characterizing each through a standardized phenotyping protocol</li>
      <li>Integrating the data to existing mouse and human disease resources</li>
      <li>Providing Strains and phenotype data for use by the research community</li>
    </ul>

    <div class="secSep"></div>

    <h1 name="whatdoesimpcdo">What does IMPC do?</h1>
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
    <h1 name="howdoesimpcwork">How does IMPC work?</h1>

    <div ><span class="work">Allele design</span>
      <div class="hideme"> <img src="${baseUrl}/img/alleleDesign.png" />
        <p></p>
        <span id="es">X number KO ES cell lines</span>
        <span id="crispr">X number of KO CRISPR cell lines</span>
      </div>
    </div>
    <div ><span class="work">Coordinated production</span>
      <div class="hideme"></div>
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

        <p>If the mutant genotype effect represents a significant change from the control group, then the IMPC pipeline will
          attempt to associate a <a href="http://www.informatics.jax.org/searches/MP_form.shtml">Mammalian Phenotype (MP) term</a> to the data.</p>

        <p>The particular MP term(s) defined for a parameter are maintained in <a href="https://www.mousephenotype.org/impress">IMPReSS</a>.
          Frequently, the term indicates an <strong>increase</strong> or <strong>decrease</strong> of the parameter measured.</p>

        <p>When a statistical result is determined as significant, the following diagram is used for associating MP terms:</p>
        <img src="img/stats-mpterms.png" />

        <h4>Significance</h4>

        <p>When a mutant genotype effect P value is less than 1.0E-4 (i.e. 0.0001), it is considered significant.</p>
      </div>
    </div>
    <div ><span class="work">Human disease association</span>
      <div class="hideme"><%@ include file="disease-help.jsp" %></div>
    </div>


    <script>
      $(document).ready(function(){
        $('span.work').click(function(){
          console.log('hideme');
          var sib = $(this).siblings('div.hideme');
          if ( sib.hasClass("showme") ){
            sib.removeClass('showme');
          }
          else {
            sib.addClass('showme');
          }
        });

        //alert(baseUrl + "/data/release.json");
        $.ajax({
          url : baseUrl + "/data/release.json",
          //url : "http://dev.mousephenotype.org/data/release.json",
          dataType : 'jsonp',
          //jsonp : 'json.wrf',
          timeout : 5000,
          success: function(response) {

            //var stats = JSON.parse(response);
            console.log(response);
            $('span#es').html("test");
          },
          error: function(){
          }
        });





    });

    </script>


  </jsp:body>



</t:genericpage>