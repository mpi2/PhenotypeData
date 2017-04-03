<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
	<jsp:attribute name="title">IMPC dataset batch query</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/batchQuery">&nbsp;Batch search</a></jsp:attribute>
	<jsp:attribute name="header">
    
        <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
        
        <style type="text/css">

			div.region-content {
				margin-top: 53px;
			}
			div#tableTool {
				position: relative;
				top: -70px;
				right: 0px;
			}
			table#dataInput td:first-child {
				width: 90px;
				max-width: 90px;
			}
			table.dataTable span.highlight {
				background-color: yellow;
				font-weight: bold;
				color: black;
			}
			.hideMe {
				display: none;
			}
			.showMe {
				display: block;
			}
			.alleleToggle {
				cursor: pointer;
				font-size: 11px;
				font-weight: bold;
			}
			div.srchBox {
				display: none;
			}
			div.srchBox input {
				height: 25px !important;
				padding: 0 0 0 25px;
				width: 200px;
			}
			div.srchBox {position: relative;}
			div.srchBox i.fa-search {position: absolute; top: 10px; left: 10px;}
			div.srchBox i.fa-times {
				position: absolute;
				top: 10px;
				left: 210px;
				cursor: pointer;
			}
			span.sugListPheno {
				font-size: 10px;
			}
			.ui-menu-item:hover {
				background-color: unset !important;
				background-image: none;
				border: none;
				outline-color: unset;
			}
			div#saveTable {
				top: 34px;
				left: -25px;
			}
			div#toolBox {
				top: -58px;
				right: 35px;
			}
			#pasteList {
				font-size: 12px;
			}
			.notes {
				font-size: 10px;
			}
			#accordion {
				font-size: 11px;
				margin-bottom: 20px;
				background-color: white;
			}
			#pastedList, #srcfile {
				padding-left: 0;
			}
			#srcfile  {
				border: 0;
			}
			.lbl {
				font-size: 12px;
				font-weight: bold;
			}
			div#query {
				font-size: 11px !important;
			}
			td.idnote {
				font-size: 12px;
				padding-left: 20px;
				height: 40px;
			}
			.cat {
				font-weight: bold;
				font-size:14px;
				color: black;
				padding: 5px;
			}
			.inner {
				height: auto;
				margin-bottom: 15px;

			}
			div#sec2 {
				display: none;
			}
			.fl2 {
				width: 56%;
				float: right;
				padding: 0 0 20px 0;
			}
			.fl1 {
				float: none; /* not needed, just for clarification */
				/* the next props are meant to keep this block independent from the other floated one */
				width: 40%;
				padding: 0 15px 0 5px;
				border-right: 1px solid #C1C1C1;
			}
			h6.bq {
				color: gray;
				padding: 0;
				margin: 0;
			}
			div#errBlock {
				margin-bottom: 10px;
				display: none;
				color: #8B0A50;
			}
			hr {
				color: #F2F2F2;
				margin: 2px 0 !important;
			}
			button#chkfields {
				margin-top: 10px;
				display: block;
			}
			table.dataTable {
				overflow-x:scroll;
				width:100%;
				display:block;
			}
			input[type=checkbox] {
				height: 25px;
				vertical-align: middle;
			}
			div#tableTool {
				float: right;
				margin-top: 40px;
				clear: right;
			}
			#srchBlock {
				background-color: white;
				padding: 10px 10px 30px 10px;
			}
			div#infoBlock {
				margin-bottom: 10px;
			}
			form#dnld {
				margin: 0;
				padding: 0;
				border: none;
			}
			span#resubmit {
				font-size: 12px;
				padding-left: 20px;
				color: #8B0A50;
			}
			form#dnld {
				padding: 5px;
				border-radius: 5px;
				background: #F2F2F2;
			}
			form#dnld button {
				text-decoration: none;
			}
			table#dataInput {
				border-collapse: collapse;
			}
			tr#humantr {
				background-color: #F2F2F2;
				border-radius: 4px !important;
			}
			table#dataInput td {
				text-align: left;
				vertical-align: middle;
			}
			table#dataInput td.idnote {
				padding-left: 8px;
			}
			div.textright {
				padding: 5px 0;
			}
			div.qtipimpc {
				padding: 30px;
				background-color: white;
				border: 1px solid gray;
				/*height: 250px;
                overflow-x: hidden;*/
			}
			div.qtipimpc p {
				font-size: 12px;
				line-height: 20px;
			}
			div.tipSec {
				font-weight: bold;
				font-size: 14px;
				padding: 5px;
				background-color: #F2F2F2;
			}
			div#docTabs {
				border: none;
			}
			ul.ui-tabs-nav {
				border: none;
				border-bottom: 1px solid #666;
				background: none;
			}
			ul.ui-tabs-nav li:nth-child(1) {
				font-size: 14px;
			}
			ul.ui-tabs-nav li a {
				margin-bottom: -1px;
				border: 1px solid #666;
				font-size: 12px;
			}
			ul.ui-tabs-nav li a:hover {
				color: white;
				background-color: gray;
			}
			a#ftp {
				color: #0978a1 !important;
				text-decoration: none;
			}
			span#sample {
				color: black;
			}
			fieldset {
				padding: 3px 10px;
				border: 1px solid lightgrey;
				font-size: 12px;
				margin: 15px 0;
			}
			fieldset.human {
				background-color: #F2F2F2;
			}
			legend {
				padding: 0 15px;
				border: 1px solid lightgrey;
				background-color: #F2F2F2;
			}
			legend.human {
				margin-left: 50%;
				background-color: white;
			}
			legend i {
				border: none !important;
				margin: 3px 0 0 5px;
			}
			fieldset i {
				float: right;
				right: 10px;
				cursor: pointer;
				border: 1px solid lightgray;
				padding: 1px 2px;
			}
			#fieldList {
				margin-top: 15px;
			}

			/*chr range slider */
			div#rangeBox {
				display: none;
				margin-left: 15px;
			}
			div#chrSlider {
				width: 200px;
				height: 2px;
			}
			.ui-slider .ui-slider-handle {
				height: 5px;
				width: 0px;
				padding-left: 9px; /*add this*/
				margin-top: 2px;
			}
			.ui-slider-horizontal .ui-slider-handle{
				background: lightgray;
			}
			.ui-slider-range.ui-widget-header {
				background: orange;
			}
			input#chrRange {
				border: 0;
				color: gray;
				font-size: 12px;
			}
			span#range {
				font-size: 12px;
			}
			#chrSel {
				display: inline;
			}
			div#rangeBox input {padding: 0 3px;}
			input#rstart {width: 55px;}
			input#rend {width: 55px;}

		</style>

        <script type='text/javascript'>

            $(document).ready(function () {
                'use strict';
                // test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';

                var baseUrl = "${baseUrl}";
                var solrUrl = "${internalSolrUrl}";

			});

		</script>

        <script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.form.js"></script>
        <script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>
        <script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>
        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>

    </jsp:attribute>

	<jsp:attribute name="addToFooter">
        <div class="region region-pinned">

		</div>

    </jsp:attribute>

	<jsp:body>
		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">IMPC Dataset Batch Query<a id="bqdoc" class=""><i class="fa fa-question-circle pull-right"></i></a></h1>

						<div class="section">

							<!--  <h2 id="section-gostats" class="title ">IMPC Dataset Batch Query</h2>-->
							<div class='inner' id='srchBlock'>graph here
							</div>

						</div><!-- end of section -->

						<div class="section" id="sec2">
							<h2 id="section-gotable" class="title ">Batch Query Result</h2>

							<div class="inner">
								<div id='infoBlock'></div>
								<div id='errBlock'></div>
								<div id='bqResult'></div>

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

	</jsp:body>
</t:genericpage>

