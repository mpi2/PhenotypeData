/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * tools.js: various tools used across the web application. Use closure to
 * safely write jQuery as $ as the closure creates a function with $ as
 * parameter and is run immediately with the value jQuery which gets mapped to $
 * 
 * Author: Chao-Kung Chen
 */
(function($) {


	$.fn.setSearchMode = function(oCounts) {

		// priority order of facet to be opened based on search result
		if (oCounts.gene != 0) {
			return 'gene';
		} else if (oCounts.mp != 0) {
			return 'mp';
		} else if (oCounts.disease != 0) {
			return 'disease';
		} else if (oCounts.anatomy != 0) {
			return 'anatomy';
		}
		/*
		 * else if (oCounts.pipeline != 0) { return 'pipeline'; }
		 */
		else if (oCounts.impc_images != 0) {
			return 'impc_images';
		} else if (oCounts.images != 0) {
			return 'images';
		}

		else {
			return false; // nothing found
		}
	};

	$.fn.initFacetToggles = function(facet) {
		// console.log('init toggle facet');
		// toggle Main Categories
		/*
		 * $('div.flist li#' + facet + ' > .flabel').click(function() {
		 * alert('1'); if ($(this).parent('.fmcat').hasClass('open')) {
		 * $(this).parent('.fmcat').removeClass('open'); } else {
		 * $('.fmcat').removeClass('open');
		 * $(this).parent('.fmcat').addClass('open'); } });
		 */
		$('div.flist >ul li#' + facet).click(function() {

			if ($(this).find('span.fcount').text() == 0) {
				return false; // for facet having no matches, a click does
								// nothing
			} else if ($(this).hasClass('open')) {
				$(this).removeClass('open');
			} else {
				$(this).addClass('open');
			}
		});

		// kick start itself (when initialized as above) if not yet
		if (!$('div.flist li#' + facet).hasClass('open')) {
			$('div.flist li#' + facet + ' > .flabel').click();
		}

		$('div.flist ul li#' + facet)
				.find('li.fcatsection')
				.click(
						function(e) {

							// when subfacet opens, tick checkbox facet filter
							// if there is matching summary facet filter
							// (created from url on page load)
							if ($('ul#facetFilter li.' + facet + ' li.ftag')
									.size() != 0) {
								$('ul#facetFilter li.ftag a').each(
										function() {
											var aVals = $(this).attr('rel')
													.split('|');
											var ffacet = aVals[0];
											var kv = aVals[1] + '|' + aVals[2];


											// tick only filters in opening
											// facet
											if (ffacet == facet) {
												$('div.flist li.fcat').find(
														'input[rel*="' + kv
																+ '"]').prop(
														'checked', true)
														.siblings('.flabel')
														.addClass('highlight');
											}
										});
							}

							e.stopPropagation();

							if ($(this).parent().parent().find('span.fcount')
									.text() == 0) {
								return false; // for facet having no matches,
												// a click does nothing
							} else if (MPI2.searchAndFacetConfig.update.filterChange) {
								MPI2.searchAndFacetConfig.update.filterChange = false; // reset,
																						// as
																						// this
																						// is
																						// used
																						// as a
																						// checkpoint
																						// for
																						// opening/closing
																						// a
																						// subfacet
							} else {
								$(this).toggleClass('open');
							}

						});

		// make filter li clickable
		$('div.flist li#' + facet).find('li.fcat .flabel').click(
				function(event) {
					if ($(this).next('span.fcount').text() == 0) {
						return false;
					} else {
						$(this).prev('input').trigger('click');
						event.stopPropagation();
					}
				});

		// stop facet count from bubbling up
		$('div.flist li#' + facet).find('li.fcat .fcount, li.fcat input')
				.click(function(e) {
					e.stopPropagation();
				});

	};

	function _facetRefresh(json, selectorBase) {

		// refresh main facet sum count
		var fcount = json.response.numFound;
		$(selectorBase + ' > span.fcount').text(fcount);

		var freezeMode = fcount == 0 ? true : false;
		$.fn.freezeFacet($(selectorBase), freezeMode);

		// set all subfacet counts to zero first and then update only those
		// matching facets
		$(selectorBase).find('li.fcat span.fcount').each(function() {
			$(this).text('0');
		});

	}

	$.fn.freezeFacet = function(obj, freezeMode) {

		if (freezeMode) {
			obj.css('cursor', 'not-allowed');
			obj.find('li.fcatsection').css('cursor', 'not-allowed');
			obj.find('span').addClass('grayout');
		} else {
			obj.css('cursor', 'pointer');
			obj.find('li.fcatsection').css('cursor', 'pointer');
			obj.find('span').removeClass('grayout');
		}
	};

	$.fn.addFacetOpenCollapseLogic = function(foundMatch, selectorBase) {
		var firstMatch = 0;

		for ( var sub in foundMatch) {
			if (foundMatch[sub] != 0) {
				firstMatch++;
				if (firstMatch == 1) {
					// open first subfacet w/ match
					$(selectorBase + ' li.fcatsection.' + sub).addClass('open');
				}

				// remove grayout for other subfacet(s) with match
				$(selectorBase + ' li.fcatsection.' + sub).removeClass(
						'grayout');
			}
		}
	};

	$.fn.fetchFecetFieldsStr = function(aFacetFields) {
		var facetFieldsStr = '';
		for (var i = 0; i < aFacetFields.length; i++) {
			facetFieldsStr += '&facet.field=' + aFacetFields[i];
		}
		return facetFieldsStr + "&facet=on&facet.limit=-1&rows=0";
	};

	$.fn.fetchFecetFieldsObj = function(aFacetFields, oParams) {
		var facetFields = [];
		for (var i = 0; i < aFacetFields.length; i++) {
			facetFields.push(aFacetFields[i]);
		}
		oParams.facet = 'on';
		oParams['facet.limit'] = -1;
		// oParams['facet.mincount']=1; // also want zero ones
		oParams['facet.field'] = facetFields.join(',');
		return oParams;
	};

	$.fn.cursorUpdate = function(core, mode) {

		// console.log('core: '+ core + ' mode: ' + mode);
		var sClass = mode == 'pointer' ? ' li.fcat' : ' li.fcat.grayout';
		// console.log('selector: '+ ' li#' + core + sClass + ' input')
		$('div.flist li#' + core + sClass).css('cursor', mode);

		var state = mode == 'pointer' ? false : true;
		$('div.flist li#' + core + sClass + ' input').prop('disabled', state)
				.css('cursor', mode);

	};

	function _composeFacetUpdateParamStr(q, facet, fqStr, aFacetFields) {
		var fecetFieldsStr = $.fn.fetchFecetFieldsStr(aFacetFields);
		var paramStr = 'qf=auto_suggest&defType=edismax&wt=json' + '&fq='
				+ fqStr + fecetFieldsStr + '&q=' + q;

		return paramStr;
	}

	$.fn.dquote = function(str) {
		return '"' + str + '"';
	};

	$.fn.resetUrlFqStr = function(fqStr, facet) {
		var oUrlParams = $.fn
				.parseHashString(window.location.hash.substring(1));

		window.location.search = 'q=' + $.fn.fetchQueryStr();

		// ISSUE: hit ENTER for a search keyword which hits nothing, there will
		// be no facet in the url
		// If in addition, there are filters, facet in the argument would be
		// from unchecking the summary facet filter
		// but when click on 'Remove all facet filters' button where there is
		// not result on all facets,
		// facet in the argument is undefined
		if (typeof oUrlParams.facetName == 'undefined') {
			facet = typeof facet == 'undefined' ? 'gene' : facet;
		}

		if (typeof fqStr == 'undefined') {
			// replace fq with facet default
			var fq = MPI2.searchAndFacetConfig.facetParams[facet + 'Facet'].fq;
			window.location.hash = 'fq=' + fq + '&facet=' + facet;
		} else {
			// remove current filter in summary facet filters
			var oldFqs = oUrlParams.fq.split(' AND ');

			MPI2.searchAndFacetConfig.update.filterChange = true;
			MPI2.searchAndFacetConfig.update.notFound = true;

			if (oldFqs.length == 1) {

				MPI2.searchAndFacetConfig.update.lastFilterNotFound = true;
				$.fn.removeAllFilters();
				var fq = MPI2.searchAndFacetConfig.facetParams[facet + 'Facet'].fq;
				window.location.hash = 'fq=' + fq + '&facet=' + facet;
			} else {
				var newFqs = [];
				for (var i = 0; i < oldFqs.length; i++) {
					var str = oldFqs[i].replace(/\(|\)/g, '');
					if (str != fqStr) {
						newFqs.push('(' + str + ')');
					}
				}
				// compose new fqstr
				var newFqStr = newFqs.join(' AND ');
				window.location.hash = 'fq=' + newFqStr + '&facet=' + facet;
			}
		}
	};

	$.fn.showNotFoundMsg = function(urlFacetNotFound) {
		MPI2.searchAndFacetConfig.update.notFound = true;

		var q = decodeURIComponent($.fn.fetchQueryStr());
		q = q.replace(/\\/g, '');

		var filter = '';
		if ($('ul#facetFilter li.ftag').size() > 0) {
			filter += " AND the selected filter(s)";
		}

		var NoResultFacetMsg = '';
		if (typeof urlFacetNotFound != 'undefined') {
			var label = MPI2.searchAndFacetConfig.facetParams[urlFacetNotFound
					+ 'Facet'].name;
			NoResultFacetMsg = typeof urlFacetNotFound != 'undefined' ? ' for '
					+ '<b>' + label + '</b>' : '';
		}
		$('div#mpi2-search').html(
				'INFO: Search keyword: ' + q + filter + ' returned no entry '
						+ NoResultFacetMsg + ' in the database');

	};

	$.fn.fetchQueryStr = function() {
		// make sure # is encoded by encodeURIComponent()
		return window.location.search.replace("?q=", "");
	};


	$.fn.qTip = function(oConf) {
		// pageName: gene | mp | ma

		// .documentation is applied to h2 and p
		$('.documentation a')
				.each(
						function() {
							// now use id instead of class for better css logic
							var key = $(this).attr('id');

							$(this).attr('href',
									MDOC[oConf.pageName][key + 'DocUrl']);
							$(this)
									.qtip(
											{
												content : {
													text : MDOC[oConf.pageName][key]
												},
												style : {
													classes : 'qtipimpc',
													tip : {
														corner : typeof oConf.tip != undefined ? oConf.tip
																: 'top right'
													}
												},
												position : {
													my : typeof oConf.corner != undefined ? oConf.corner
															: 'right top'
												}
											});
						});
	}

	$.fn.setHashUrl = function(q, core) {
		var hashParams = {};
		hashParams.q = q;
		hashParams.core = core;
		hashParams.fq = MPI2.searchAndFacetConfig.facetParams[core + 'Facet'].fq;
		window.location.hash = $.fn.stringifyJsonAsUrlParams(hashParams);
	}

	$.fn.updateBreadCrumb = function(coreName) {
		var hashParams = $.fn
				.parseHashString(window.location.hash.substring(1));

		var breadcrumbBox = $('p.ikmcbreadcrumb');
		var baseLinks = "<a href=" + drupalBaseUrl
				+ ">Home</a> &raquo; <a href=" + baseUrl
				+ "/search>Search</a> &raquo; ";

		if (coreName && !hashParams.coreName) {
			hashParams.coreName = coreName;
			hashParams.fq = 'undefined';
		} else if (!coreName && !hashParams.q) {
			hashParams.q = "*:*";
			hashParams.coreName = 'gene';
			hashParams.fq = 'undefined';
		}
		baseLinks += fetchFacetLink(hashParams);
		breadcrumbBox.html(baseLinks);
	}

	function fetchFacetLink(hashParams) {
		var coreName = hashParams.coreName;
		var fq = MPI2.searchAndFacetConfig.facetParams[coreName + 'Facet'].fq; // default
																				// for
																				// whole
																				// dataset
																				// of a
																				// facet
		var breadCrumbLabel = MPI2.searchAndFacetConfig.facetParams[coreName
				+ 'Facet'].breadCrumbLabel;
		var url = encodeURI(baseUrl + "/search#q=*:*" + "&core="
				+ hashParams.coreName + "&fq=" + fq);
		return "<a href=" + url + ">" + breadCrumbLabel + "</a>";
	}

	$.fn.openFacet = function(core) {

		$('div.facetCatList').hide();
		$('div.facetCat').removeClass('facetCatUp');

		// priority order of facet to be opened based on search result
		if (core == 'gene') {
			$('div#geneFacet div.facetCatList').show();
			$('div#geneFacet div.facetCat').addClass('facetCatUp');
		} else if (core == 'mp') {
			$('div#mpFacet div.facetCatList').show();
			$('div#mpFacet div.facetCat').addClass('facetCatUp');
		} else if (core == 'anatomy') {
			$('div#maFacet div.facetCatList').show();
			$('div#maFacet div.facetCat').addClass('facetCatUp');
		} else if (core == 'pipeline') {
			$('div#pipelineFacet div.facetCatList').show();
			$('div#pipelineFacet div.facetCat').addClass('facetCatUp');
		} else if (core == 'impc_images') {
			$('div#impc_imagesFacet div.facetCatList').show();
			$('div#impc_imagesFacet div.facetCat').addClass('facetCatUp');
		} else if (core == 'images') {
			$('div#imagesFacet div.facetCatList').show();
			$('div#imagesFacet div.facetCat').addClass('facetCatUp');
		} else if (core == 'disease') {
			$('div#diseaseFacet div.facetCatList').show();
			$('div#diseaseFacet div.facetCat').addClass('facetCatUp');
		}
	}

	$.fn.ieCheck = function() {

		/*
		 * if ( $.browser.msie && $.browser.version < 8.0 ){ var msg = "<div
		 * id='noSupport'>Dear user:<p><p>It appears that you are using
		 * Internet Explorer 7 or earlier version.<p>To ensure that IMPC is
		 * supporting the best browsing features, functionalities and
		 * experiences, " + "and considering the security issues of older IEs,
		 * we decided not to support IE7 and earlier versions.<p>We are sorry
		 * if this has caused your inconvenience.<p>Here is a list of
		 * supported browsers: " + "<a href='http://www.mozilla.org'>Firefox</a>,
		 * <a href='http://www.google.com/chrome'>Google chrome</a>, <a
		 * href='http://support.apple.com/downloads/#internet'>Apple safari</a>.<p>" +
		 * "IMPC team.</div>";
		 *
		 * $('div.navbar').siblings('div.container').html(msg); return false; }
		 */

		var ver = getInternetExplorerVersion();

		if (ver < 8.0) {
			var msg = "<div id='noSupport'>Dear user:<p><p>It appears that you are using Internet Explorer 7 or earlier version.<p>To ensure that IMPC is supporting the best browsing features, functionalities and experiences, "
					+ "and considering the security issues of older IEs, we decided not to support IE7 and earlier versions.<p>We are sorry if this has caused your inconvenience.<p>Here is a list of supported browsers: "
					+ "<a href='http://www.mozilla.org'>Firefox</a>, <a href='http://www.google.com/chrome'>Google chrome</a>, <a href='http://support.apple.com/downloads/#internet'>Apple safari</a>.<p>"
					+ "IMPC team.</div>";

			$('div.navbar').siblings('div.container').html(msg);
			return false;
		}
	}
	function getInternetExplorerVersion() {

		// Returns the version of IE or -1

		var rv = -1; // default
		if (navigator.appName == 'Microsoft Internet Explorer') {
			var ua = navigator.userAgent;
			var re = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
			if (re.exec(ua) != null)
				rv = parseFloat(RegExp.$1);
		}
		return rv;
	}

	// inverse simple JSON: eg, {a: 'one', b: 'two}
	// cannot do complicated nested associated array
	$.fn.inverseSimpleJSON = function(json) {
		var newJson = {};
		for ( var i in json) {
			newJson[json[i]] = i;
		}
		return newJson;
	};

	$.fn.endsWith = function(str, suffix) {
		return str.indexOf(suffix, str.length - suffix.length) !== -1;
	};

	$.fn.composeSelectUI = function(aFormats, selName) {
		var oSelect = $('<select></select>').attr({
			'name' : selName
		});

		for (var i = 0; i < aFormats.length; i++) {
			oSelect.append("<option>" + aFormats[i]);
		}
		return oSelect;
	};

	$.fn.loadFileExporterUI = function(conf) {
		var oFormatSelector = conf.formatSelector;
		var label = conf.label;
		var textPos = conf.textPos;
		var iconDiv = $('<p></p>').attr({
			'class' : textPos
		}).html(label + " &nbsp;");
		var it = 0;
		for ( var f in oFormatSelector) {
			if (it++ > 0)
				$(iconDiv).append("&nbsp;or&nbsp;");
			// var btn = $('<a href="#"></a>').attr({'class': oFormatSelector[f]
			// + ' ' + conf['class']}).html("<i class=\"fa fa-download\"></i> "
			// + f);
			// changed to use button instead of <a> as this will follow the link
			// and the download won't work when clicked - have tried return
			// false,
			// but due to a couple of ajax down the road, I could not get it to
			// work.
			// The button is styled as the new design
			var btn = $('<button></button>').attr(
					{
						'class' : oFormatSelector[f]
								+ ' fa fa-download gridDump ' + conf['class']
					}).html(f);

			$(iconDiv).append(btn);
		}
		return iconDiv;
	};

	$.fn.stringifyJsonAsUrlParams = function(json) {

		var aStr = [];
		for ( var i in json) {
			aStr.push(i + '=' + json[i]);
		}
		return aStr.join("&");
	};

	$.fn.setSolrQfStr = function(facet) {
		return MPI2.searchAndFacetConfig.coreQf[facet];
	};

	$.fn.processCurrentFqFromUrl = function(facet) {
		// return $.fn.getCurrentFq(facet).replace(/img_/g, '')
		return $.fn.getCurrentFq(facet).replace(/img_|impcImg_/g, '');
	};

	$.fn.fetchUrlParams = function(key) {

		var params = {};
		var paramStr = window.location.search.replace("?","");

		var kw = paramStr.split("&");
		for ( var i=0; i<kw.length; i++ ) {
			var pairs = kw[i].split("=");
			var k = pairs[0];
			var v = pairs[1];
			params[k] = v;
		}
		return params[key];
	};

	$.fn.getCurrentFq = function(facet) {

		var hashStr = $(location).attr('hash');
		if (hashStr != '' && hashStr.indexOf('fq=') != -1) {
			var fqStr;

			if (hashStr.indexOf('&facet=') == -1) {
				fqStr = hashStr.replace(/#fq=/, '');
			} else {
				fqStr = hashStr.match(/fq=.+\&/)[0].replace(/fq=|\&/g, '');
			}

			if (/.*:\*/.test(fqStr)) { // default
				// not all mega cores are the same, eg. pipeline and ma is
				// different
				return MPI2.searchAndFacetConfig.facetParams[facet + 'Facet'].fq;
			}
			return fqStr; // do not replace img_ if there is one, as this will
							// replace the url fq
		} else if (hashStr.indexOf('fq=') == -1) {
			return MPI2.searchAndFacetConfig.facetParams[facet + 'Facet'].fq;
		}
		return '*:*';
	};

	$.fn.setCurrentFq = function() {
		var hashStr = $(location).attr('hash');
		if (hashStr != '' && hashStr.indexOf('fq=') != -1) {
			MPI2.searchAndFacetConfig.currentFq = hashStr.match(/fq=.+\&?/)[0]
					.replace(/fq=|\&/g, '');
		} else {
			MPI2.searchAndFacetConfig.currentFq = false;
		}
	};

	$.fn.getCurrentFacet = function() {
		if ($(location).attr('hash') != '') {
			var hashStr = $(location).attr('hash');
			return hashStr.match(/facet=.+\&?/)[0].replace(/facet=|\&/g, '');
		}
		return 'gene';
	};

	$.fn.process_q = function(q) {
		// console.log('PREprocessed q: '+q)
		q = q.replace(/\\%20/g, ' ');

		// eg. \%22bl*%20ce*%22
		// if ( /^\\%22.*%22$/.test(q) && /\w+?\**\w*\*+/.test(q) ){
		if (/^\\%22.*%22$/.test(q) && /%20/.test(q)) {
			q = q.replace(/^\\%22|%22$/g, '');
			q = _setSolrComplexPhraseQuery(q);
		}
		return q;
	};

	$.fn.process_q2 = function(q) {
		// console.log('PREprocessed q: '+q)
		// Escaping Special Characters for SOLR
		// Lucene supports escaping special characters that are part of the
		// query syntax.
		// These cannot be encoded so need to be escaped
		// The current list special characters are

		// var re = /([-|||!(){}[]^~])/g;
		// var re = /([-!(){}^~])/g;
		// var q = q.replace(re,"\\" + "$1");

		if (/^%22\*+.+%22$/.test(q)) {
			return q;
		}
		// apply SOLR complexphrase search for * in quotes, eg. "blo* cel*"
		// if ( /^[%22"].*[%22"]$/.test(q) && /\*/.test(q) ){
		else if (/^%22.*%22$/.test(q) && /\w+?\**\w*\*+/.test(q)) {
			q = _setSolrComplexPhraseQuery(q);
		}

		// console.log('processed q: '+q)
		return q;
	};

	function _setSolrComplexPhraseQuery(q) {

		if (typeof q == 'undefined') {
			q = '*:*';
		} else {
			q = q.replace(/\//g, '\\/');
		}
		// console.log(q)
		// catches user typing ' instead of " for phrase search
		if (/^%27.+%27$/.test(q)) {
			q = q.replace(/^%27|%27$/g, '%22');
		}

		// need to remove leading wildcard as solr4.8 does NOT support this for
		// complexphrase search
		q = q.replace(/^[\*\?]/, '');

		// try a slop of 10 for now to look for matching string 10 words apart
		// w/0 slop the query result maybe strange
		q = '{!complexphrase}auto_suggest:"' + q + '"~10';

		return q;
	}

	$.fn.parseHashString = function(sHash) {

		var hashParams = {};

		var aKV = decodeURI(sHash).split("&");
		if (aKV[0] != '') {
			for (var i = 0; i < aKV.length; i++) {
				var aList = aKV[i].split('=');

				var key = aList[0] == 'facet' ? 'facetName' : aList[0];
				var val = aList[1];

				hashParams[key] = val;
				if (key == 'fq') {

					// catches fq renders to false due to no value - due to
					// hitting ENTER too fast
					if (val == 'false') {
						window.location.hash = 'fq=*:*&facet='
								+ $.fn.getCurrentFacet();
					} else {
						hashParams.oriFq = val;
					}
				}
			}
		}

		return hashParams;
	};

	$.fn.fetchEmptyTable = function(theadStr, colNum, id, pageReload) {

		var table = $('<table></table>').attr({
			'id' : id,
			'class' : 'table tableSorter'
		});
		var thead = theadStr;
		var tds = '';
		for (var i = 0; i < colNum; i++) {
			tds += "<td></td>";
		}
		var tbody = $('<tbody><tr>' + tds + '</tr></tbody>');
		table.append(thead, tbody);
		return table;
	};

	function _fetchProcedureNameById(sid) {
		$.ajax({
			'url' : solrUrl + '/pipeline/select',
			'data' : 'q=procedure_stable_id:"' + sid
					+ '"&fl=procedure_name&rows=1',
			'dataType' : 'jsonp',
			'async' : false,
			'jsonp' : 'json.wrf',
			'success' : function(json) {
				$('span#hiddenBox').html(json);
				return procName = json.response.numFound;
			}
		});
	}

	$.fn.concatFilters = function(operator) {
		var aFilters = [];
		$('ul#facetFilter span.hidden').each(function() {
			aFilters.push('(' + $(this).text() + ')');
		});
		return aFilters.join(' ' + operator + ' ');
	};

	function _prepare_resultMsg_and_dTableSkeleton(oUrlParams) {

		var q = oUrlParams.q;
		var facetDivId = oUrlParams.widgetName;

		var filterStr = $.fn.concatFilters('AND');
		var oVal = MPI2.searchAndFacetConfig.facetParams[facetDivId];
		var dTable = $.fn.fetchEmptyTable(oVal.tableHeader, oVal.tableCols,
				oVal.gridName);

		var imgViewSwitcher = '';
		if (facetDivId == 'imagesFacet' || facetDivId == 'impc_imagesFacet') {
			imgViewSwitcher = _load_imgViewSwitcher(dTable, oVal);
			$("div#resultMsg").prepend(imgViewSwitcher);
		}
		var searchKw = " AND search keyword: ";
		searchKw += q == '*:*' ? '""' : '"' + q + '"';

		var dataCount = "Found <span id='resultCount'><span id='annotCount'></span><a></a></span>";
		var resultMsg = $("<div id='resultMsg'></div>").append(imgViewSwitcher,
				dataCount);

		$('div#mpi2-search').html('');
		$('div#mpi2-search').append(resultMsg, dTable);

		// hidden by default; displays only when dataTable is loaded
		$('div#mpi2-search').hide();
	}

	$.fn.relabelFilterForUsers = function(fqStr, facetDivId) {

		var oldStr = fqStr;
		for ( var i in MPI2.searchAndFacetConfig.facetFilterLabel) {
			var regex = new RegExp('\\b' + i + '\\b', "gi");
			fqStr = fqStr.replace(regex,
					MPI2.searchAndFacetConfig.facetFilterLabel[i]);
		}

		// fqStr = fqStr.replace(/\"1\"/g, '"Started"');
		fqStr = fqStr.replace(/\"1\"/g, function() {
			return facetDivId == 'diseaseFacet' ? 'yes' : 'Started';
		});

		return fqStr;
	};

	function _load_imgViewSwitcher(oDTable) {
		// toggles two types of views for images: annotation view, image view
		var viewLabel, imgViewSwitcherDisplay, viewMode;

		oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;

		if (oConf.showImgView) {
			oDTable.find('th:nth-child(2)').text("Image");
		} else {
			oDTable.find('th:nth-child(2)').text("Example Images");
		}

		var imgViewSwitcher = $('<div></div>').attr({
			'id' : 'imgView',
			'rel' : oConf.viewMode
		}).html(
				"<span id='imgViewSubTitle'>" + oConf.viewLabel + "</span>"
						+ "<span id='imgViewSwitcher'>"
						+ oConf.imgViewSwitcherDisplay + "</span>");

		return imgViewSwitcher;
	}

	$.fn.setDefaultImgSwitcherConf_ori = function() {
		var oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;
		oConf.imgViewSwitcherDisplay = 'Show Annotation View';
		oConf.viewLabel = 'Image View: lists annotations to an image';
		oConf.viewMode = 'imageView';
		oConf.showImgView = true;
	};

	$.fn.setDefaultImgSwitcherConf = function() {
		var oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet; // use
																		// for
																		// both
																		// Sanger
																		// and
																		// IMPC
																		// images
		oConf.imgViewSwitcherDisplay = 'Show Image View';
		oConf.viewLabel = 'Annotation View: groups images by annotation';
		oConf.viewMode = 'annotView';
		oConf.showImgView = false;
	};

	function setImageFacetSumCount(oInfos) {
		var q = oInfos.q;
		var fqStr = oInfos.fq;
		var paramStr = 'q=' + $.fn.process_q(q)
				+ '&wt=json&defType=edismax&qf=auto_suggest';
		paramStr += '&fq=' + fqStr;
		var thisSolrUrl = solrUrl + '/images/select';

		$.ajax({
			'url' : thisSolrUrl,
			'data' : paramStr,
			'dataType' : 'jsonp',
			'jsonp' : 'json.wrf',
			'success' : function(json) {
				$('span#resultCount a')
						.text(json.response.numFound + ' images');
			}
		});
	}

	function setImpcImageFacetSumCount(oInfos) {
		var q = oInfos.q;
		var fqStr = oInfos.fq;
		var paramStr = 'q=' + $.fn.process_q(q);
		paramStr += '&fq=' + fqStr;
		var thisSolrUrl = solrUrl + '/impc_images/select';

		$.ajax({
			'url' : thisSolrUrl,
			'data' : paramStr,
			'dataType' : 'jsonp',
			'jsonp' : 'json.wrf',
			'success' : function(json) {
				$('span#resultCount a')
						.text(json.response.numFound + ' images');
			}
		});
	}

	$.fn.initDataTableDumpControl = function(oInfos) {
		// function initDataTableDumpControl(oInfos) {

		$('div#saveTable').remove();
		$('div#toolBox').remove();

		// var saveTool = $("<div id='saveTable'></div>").html("Download table
		// <img src='"+baseUrl+"/img/floppy.png' />");//.corner("4px");
		var saveTool = $("<div id='saveTable'></div>")
				.html(
						"<span class='fa fa-download'>&nbsp;<span id='dnld'>Download</span></span>");// .corner("4px");

		var toolBox = fetchSaveTableGui();

		$('div.dataTables_processing').siblings('div#tableTool').append(
				saveTool, toolBox);

		$('div#saveTable')
				.click(
						function() {

							if ($('div#toolBox').is(":visible")) {
								$('div#toolBox').hide();
							} else {
								$('div#toolBox').show();

								// browser-specific position fix
								if (parseInt(getInternetExplorerVersion()) === 8) {
									// if ($.browser.msie &&
									// parseInt($.browser.version, 10) === 8) {
									$('div#toolBox').css({
										'top' : '-30px',
										'left' : '65px'
									});
								}

								var solrCoreName;
								if (oInfos.hasOwnProperty('widgetName')) {
									solrCoreName = oInfos.widgetName.replace(
											'Facet', '');
								}

								// work out solr query start and row length
								// dynamically
								var iActivePage = $(
										'div.dataTables_paginate li.active a')
										.text();
								var oCurrDt = $('table.dataTable').dataTable(); // find
																				// the
																				// dataTable
																				// object
								var oSettings = oCurrDt.fnSettings();
								var iLength = oSettings._iDisplayLength;
								var iRowStart = iActivePage == 1 ? 0
										: iActivePage * iLength - iLength;

								var showImgView = $('div#resultMsg div#imgView')
										.attr('rel') == 'imgView' ? true
										: false;

								$('button.gridDump').unbind('click');

								var conf = {
									legacyOnly : oInfos.legacyOnly,
									externalDbId : 5,
									rowStart : iRowStart,
									length : iLength,
									solrCoreName : solrCoreName,
									params : oInfos.params,
									showImgView : showImgView,
									// gridFields:
									// MPI2.searchAndFacetConfig.facetParams[oInfos.widgetName].gridFields,
									gridFields : oInfos.gridFields,
									dogoterm : oInfos
											.hasOwnProperty('dogoterm') ? oInfos.dogoterm
											: false,
									fileName : typeof oInfos.fileName == 'undefined' ? solrCoreName
											+ '_table_dump'
											: oInfos.fileName,
									filterStr : oInfos
											.hasOwnProperty('filterStr') ? oInfos.filterStr
											: false,
									doAlleleRef : oInfos
											.hasOwnProperty('doAlleleRef') ? oInfos.doAlleleRef
											: false,
								};

								var exportObjPageTsv = buildExportUrl(conf,
										'tsv', 'page');
								var exportObjPageXls = buildExportUrl(conf,
										'xls', 'page');
								var exportObjAllTsv = buildExportUrl(conf,
										'tsv', 'all');
								var exportObjAllXls = buildExportUrl(conf,
										'xls', 'all');
								$('button.gridDump')
										.each(
												function(index, obj) {
													if ($(this).hasClass(
															'tsv_grid')) {
														$(this)
																.attr(
																		'data-exporturl',
																		exportObjPageTsv.exportUrl);
													} else if ($(this)
															.hasClass(
																	'xls_grid')) {
														$(this)
																.attr(
																		'data-exporturl',
																		exportObjPageXls.exportUrl);
													} else if ($(this)
															.hasClass('tsv_all')) {
														$(this)
																.attr(
																		'data-exporturl',
																		exportObjAllTsv.exportUrl);
													} else if ($(this)
															.hasClass('xls_all')) {
														$(this)
																.attr(
																		'data-exporturl',
																		exportObjAllXls.exportUrl);
													}
												});

								$('button.gridDump').click(function() {
									initGridExporter($(this), conf);
								});
							}
						});
	}

	/**
	 * if count &gt; DOWNLOAD_WARNING_THRESHOLD, presents a confirm() box to the
	 * user warning them that the download may take a long time and giving them
	 * the ability to opt out.
	 * 
	 * count - value above which the confirm() box is run. Returns false if
	 * <code>count</count> &gt; DOWNLOAD_WARNING_THRESHOLD and user has canceled
	 * the operation; true otherwise.
	 */
	var DOWNLOAD_WARNING_THRESHOLD = 60000; // count value for alleleref page
											// was 32104 as of 07-Apr-2015 and
											// the download is instantaneous
											// (mrelac).
	function confirmDownloadIfExceedsThreshold(count) {
		var retVal = true;
		if (count > DOWNLOAD_WARNING_THRESHOLD) {
			retVal = confirm("Download big dataset would take a while, would you like to proceed?");
		}

		return retVal;
	}

	function initGridExporter(thisButt, conf) {
		var fileType = thisButt.text();
		var dumpMode = thisButt.attr('class').indexOf('all') != -1 ? 'all'
				: 'page';

		var exportObj = buildExportUrl(conf, fileType, dumpMode);
		var form = exportObj.form;

		if (dumpMode == 'all') {

			if (typeof conf.solrCoreName != 'undefined') {
				var paramStr = conf['params'] + "&start=" + conf['rowStart']
						+ "&rows=0";
				var url1;

				url1 = solrUrl + '/' + conf['solrCoreName'] + "/select?";
				paramStr += "&wt=json";

				$
						.ajax({
							url : url1,
							data : paramStr,
							dataType : 'jsonp',
							jsonp : 'json.wrf',
							timeout : 5000,
							success : function(json) {
								// prewarn users if dataset is big
								if (confirmDownloadIfExceedsThreshold(json.response.numFound)) {
									$(form).appendTo('body').submit().remove();
								}
							},
							error : function(jqXHR, textStatus, errorThrown) {
								$('div#facetBrowser').html(
										'Error fetching data ...');
							}
						});
			} else if (conf.hasOwnProperty('doAlleleRef')) {
				dump_all_allele_ref(conf, form);
			}
		} else {
			// NOTE that IE8 prevents from download if over https.
			// see http://support.microsoft.com/kb/2549423
			$(form).appendTo('body').submit().remove();
		}

		$('div#toolBox').hide();

	}

	function dump_all_allele_ref(conf, form) {

		var url1 = baseUrl + '/dataTableAlleleRefCount';

		$.ajax({
			url : url1,
			data : {
				filterStr : conf.hasOwnProperty('filterStr') ? conf.filterStr
						: ''
			},
			// dataType: 'jsonp',
			jsonp : 'json.wrf',
			timeout : 5000,
			success : function(count) {
				// prewarn users if dataset is big
				if (confirmDownloadIfExceedsThreshold(count)) {
					$(form).appendTo('body').submit().remove();
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				$('div#facetBrowser').html('Error fetching data ...');
			}
		});
	}

	function buildExportUrl(conf, fileType, dumpMode) {
		if (fileType === undefined)
			fileType = '';

		var url = baseUrl + '/export';
		var sInputs = '';
		var aParams = [];
		for ( var k in conf) {
			aParams.push(k + "=" + conf[k]);
			sInputs += "<input type='text' name='" + k + "' value='" + conf[k]
					+ "'>";
		}
		sInputs += "<input type='text' name='fileType' value='"
				+ fileType.toLowerCase() + "'>";
		sInputs += "<input type='text' name='dumpMode' value='" + dumpMode
				+ "'>";

		var form = "<form action='" + url + "' method=get>" + sInputs
				+ "</form>";
		var exportUrl = url + "?" + $(form).serialize();

		var retVal = new Object();
		retVal.url = url;
		retVal.form = form;
		retVal.exportUrl = exportUrl;
		return retVal;
	}

	function fetchSaveTableGui() {

		var div = $("<div id='toolBox'></div>");// .corner("4px");
		div.append($("<div class='dataName'></div>").html(
				"Current paginated entries in table"));
		div.append($.fn.loadFileExporterUI({
			label : 'Export as:',
			formatSelector : {
				TSV : 'tsv_grid',
				XLS : 'xls_grid'
			},
			'class' : 'gridDump'
		}));
		div.append($("<div class='dataName'></div>").html(
				"All entries in table"));
		div.append($.fn.loadFileExporterUI({
			label : 'Export as:',
			formatSelector : {
				TSV : 'tsv_all',
				XLS : 'xls_all'
			},
			'class' : 'gridDump'
		}));
		return div;
	}

	$.fn.initDataTable = function(jqObj, customConfig) {

		var params = {
			// "sDom":
			// "<'row-fluid'<'#foundEntries'><'span6'f>r>t<'row-fluid'<'#tableShowAllLess'><'span6'p>>",
			// "bPaginate":true,
			"bLengthChange" : false,
			"bSort" : true,
			"bInfo" : false,
			"bAutoWidth" : false,
			"iDisplayLength" : 10000, // 10 rows as default
			"bRetrieve" : true,
			/* "bDestroy": true, */
			"bFilter" : false,
			"sPaginationType" : "bootstrap",
		};
		var oTbl = jqObj.dataTable($.extend({}, params, customConfig))
				.fnSearchHighlighting();
		return oTbl;
	};

	$.fn.naturalSort = function(a, b) {
		// setup temp-scope variables for comparison evauluation
		var x = a.toString().toLowerCase() || '', y = b.toString()
				.toLowerCase()
				|| '', nC = String.fromCharCode(0), xN = x.replace(
				/([-]{0,1}[0-9.]{1,})/g, nC + '$1' + nC).split(nC), yN = y
				.replace(/([-]{0,1}[0-9.]{1,})/g, nC + '$1' + nC).split(nC), xD = (new Date(
				x)).getTime(), yD = (new Date(y)).getTime();
		// natural sorting of dates
		if (xD && yD && xD < yD)
			return -1;
		else if (xD && yD && xD > yD)
			return 1;
		// natural sorting through split numeric strings and default strings
		for (var cLoc = 0, numS = Math.max(xN.length, yN.length); cLoc < numS; cLoc++)
			if ((parseFloat(xN[cLoc]) || xN[cLoc]) < (parseFloat(yN[cLoc]) || yN[cLoc]))
				return -1;
			else if ((parseFloat(xN[cLoc]) || xN[cLoc]) > (parseFloat(yN[cLoc]) || yN[cLoc]))
				return 1;
		return 0;
	}

	// toggle showing first 10 / all rows in a table
	$.fn.toggleTableRows = function(oTable) {
		var rowNum = $(oTable).find('tbody tr').length;

		var rowToggler;
		if (rowNum > 10) {
			$(oTable).find("tbody tr:gt(9):lt(" + rowNum + ")").hide();
			var txtShow10 = 'Show all ' + rowNum + ' records';
			rowToggler = $('<span></span>').attr({
				'class' : 'rowToggler'
			}).text(txtShow10).toggle(function() {
				$(oTable).find("tbody tr:gt(9):lt(" + rowNum + ")").show();
				$(this).text('Show first 10 records');
			}, function() {
				$(oTable).find("tbody tr:gt(9):lt(" + rowNum + ")").hide();
				$(this).text(txtShow10);
			});
		}

		return rowToggler;
	}

	$.fn.inArray = function(item, list) {
		var length = list.length;
		for (var i = 0; i < length; i++) {
			if (list[i] == item) {
				return true;
			}
		}

		return false;
	}

	// get unique element from array
	$.fn.getUnique = function(list) {
		var u = {}, a = [];
		for (var i = 0, l = list.length; i < l; ++i) {
			if (list[i] in u) {
				continue;
			}
			a.push(list[i]);
			u[list[i]] = 1;
		}
		return a;
	}

	// tooltip
	$.fn.komp2_tooltip = function(options) {
		var defaults = {
			title : '',
			color : 'black',
			bgcolor : '#F4F4F4',
			mozBr : '4px', // -moz-border-radius
			webkitBr : '4px', // -webkit-border-radius
			khtmlBr : '4px', // -khtml-border-radius
			borderRadius : '4px' // border-radius
		}
		var o = $.extend(defaults, options);

		return this.each(function() {
			var oC = $(this);
			var sTitle = oC.attr('title');
			if (sTitle) {
				oC.removeAttr('title');
			} else if (o.title != '') {
				sTitle = o.title;
			} else if (o.url != '') {
				// do ajax call
				$.ajax({
					url : o.url,
					success : function(data) {
						sTitle = data;
					}
				});
			}

			oC.hover(function(event) {
				$('<div id="tooltip" />').appendTo('body').text(sTitle).css({
					'max-width' : '150px',
					'font-size' : '10px',
					border : '1px solid gray',
					padding : '3px 5px',
					color : o.color,
					'background-color' : o.bgcolor,
					'z-index' : 999,
					position : 'absolute',
					'-moz-border-radius' : o.mozBr,
					'-webkit-border-radius' : o.webkitBr,
					'-khtml-border-radius' : o.khtmlBr,
					'border-radius' : o.borderRadius
				}).komp2_updatePosition(event);
			}, function(event) {
				$('div#tooltip').remove();
			});
		});
	}

	$.fn.komp2_updatePosition = function(event) {
		return this.each(function() {
			$('div#tooltip').css({
				left : event.pageX + 10,
				top : event.pageY + 15
			})
		});
	}
	// end of tooltip

	$.fn.upperCaseFirstLetter = function(str) {
		return str.replace(/\w\S*/g, function(txt) {
			return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
		});
	}

	$.fn.sortJson = function(o) {
		var sorted = {}, key, a = [];

		for (key in o) {
			if (o.hasOwnProperty(key)) {
				a.push(key);
			}
		}

		a.sort();

		for (key = 0; key < a.length; key++) {
			sorted[a[key]] = o[a[key]];
		}
		return sorted;
	}


	$.fn.startsWith = function(str, prefix, offset){
		// offset sets the position (0-based index) where the startsWith should start
		return str.slice(offset, prefix.length + offset) == prefix;
	}
//})(jQuery);

// HIGHLIGHT FCT
$.fn.dataTableExt.oApi.fnSearchHighlighting = function(oSettings) {
	// Initialize regex cache
	if (oSettings == null) {
		// console.log('oSettings is null or undefined');
		// was failing if null so added this - but presumably this is needed on
		// the search pages still?
	} else {

		oSettings.oPreviousSearch.oSearchCaches = {};

		oSettings.oApi._fnCallbackReg(oSettings, 'aoRowCallback', function(
				nRow, aData, iDisplayIndex, iDisplayIndexFull) {
			// Initialize search string array
			var searchStrings = [];
			var oApi = this.oApi;
			var cache = oSettings.oPreviousSearch.oSearchCaches;
			// Global search string
			// If there is a global search string, add it to the search string
			// array
			if (oSettings.oPreviousSearch.sSearch) {
				searchStrings.push(oSettings.oPreviousSearch.sSearch);
			}
			// Individual column search option object
			// If there are individual column search strings, add them to the
			// search string array
			if ((oSettings.aoPreSearchCols)
					&& (oSettings.aoPreSearchCols.length > 0)) {
				for ( var i in oSettings.aoPreSearchCols) {
					if (oSettings.aoPreSearchCols[i].sSearch) {
						searchStrings
								.push(oSettings.aoPreSearchCols[i].sSearch);
					}
				}
			}
			// Create the regex built from one or more search string and cache
			// as necessary
			if (searchStrings.length > 0) {
				var sSregex = searchStrings.join("|");
				if (!cache[sSregex]) {
					// This regex will avoid in HTML matches
					cache[sSregex] = new RegExp("(" + sSregex
							+ ")(?!([^<]+)?>)", 'i');
				}
				var regex = cache[sSregex];
			}
			// Loop through the rows/fields for matches
			$('td', nRow).each(
					function(i) {

						// Take into account that ColVis may be in use
						var j = oApi._fnVisibleToColumnIndex(oSettings, i);
						// Only try to highlight if the cell is not empty or
						// null
						if (aData[j]) {
							// If there is a search string try to match
							if ((typeof sSregex !== 'undefined') && (sSregex)) {
								this.innerHTML = aData[j].replace(regex,
										function(matched) {
											return "<span class='hit'>"
													+ matched + "</span>";
										});
							}
							// Otherwise reset to a clean string
							else {
								this.innerHTML = aData[j];
							}
						}
					});
			return nRow;
		}, 'row-highlight');
		return this;

	}
};

/* customized jquery tabs css and behavior */
$.fn.customJqTabs = function() {

	$('ul.ui-tabs-nav li a').click(function() {
		$('ul.ui-tabs-nav li a').css({
			'border-bottom' : 'none',
			'background-color' : '#F4F4F4',
			'border' : 'none'
		});
		$(this).css({
			'border' : '1px solid #666',
			'border-bottom' : '1px solid white',
			'background-color' : 'white',
			'color' : '#666'
		});
		$('ul.ui-tabs-nav li').mouseover(function() {
			$(this).find('a').css('color', 'black');
		}).mouseout(function() {
			$(this).find('a').css('color', '#666');
		})
	});

	$('ul.ui-tabs-nav li a').css({
		'border-bottom' : 'none',
		'background-color' : '#F4F4F4',
		'border' : 'none'
	});

	if ($('ul li.trtName').size() == 1) {
		$('ul.ui-tabs-nav li:nth-child(2) a').css({
			'border' : '1px solid #666',
			'border-bottom' : '1px solid white',
			'background-color' : 'white',
			'color' : '#666'
		});
	}

	$('ul.ui-tabs-nav li').mouseover(function() {
		$(this).find('a').css('color', 'black');
	}).mouseout(function() {
		$(this).find('a').css('color', '#666');
	});
}

/* API method to get paging information for style bootstrap */
$.fn.dataTableExt.oApi.fnPagingInfo = function(oSettings) {
	return {
		"iStart" : oSettings._iDisplayStart,
		"iEnd" : oSettings.fnDisplayEnd(),
		"iLength" : oSettings._iDisplayLength,
		"iTotal" : oSettings.fnRecordsTotal(),
		"iFilteredTotal" : oSettings.fnRecordsDisplay(),
		"iPage" : Math.ceil(oSettings._iDisplayStart
				/ oSettings._iDisplayLength),
		"iTotalPages" : Math.ceil(oSettings.fnRecordsDisplay()
				/ oSettings._iDisplayLength)
	};
}
/* API method to get paging information */
$.fn.dataTableExt.oApi.fnPagingInfo = function(oSettings) {
	return {
		"iStart" : oSettings._iDisplayStart,
		"iEnd" : oSettings.fnDisplayEnd(),
		"iLength" : oSettings._iDisplayLength,
		"iTotal" : oSettings.fnRecordsTotal(),
		"iFilteredTotal" : oSettings.fnRecordsDisplay(),
		"iPage" : Math.ceil(oSettings._iDisplayStart
				/ oSettings._iDisplayLength),
		"iTotalPages" : Math.ceil(oSettings.fnRecordsDisplay()
				/ oSettings._iDisplayLength)
	};
}
/* Bootstrap style pagination control */
$
		.extend(
				$.fn.dataTableExt.oPagination,
				{
					"bootstrap" : {
						"firstCount" : 0,
						"fnInit" : function(oSettings, nPaging, fnDraw) {
							var oLang = oSettings.oLanguage.oPaginate;

							var fnClickHandler = function(e) {
								e.preventDefault();
								if (oSettings.oApi._fnPageChange(oSettings,
										e.data.action)) {
									fnDraw(oSettings);
								}
							};

							$(nPaging)
									.addClass('pagination')
									.append(
											'<ul>'
													+ '<li class="prev disabled"><a href="#">&larr; '
													+ oLang.sFirst
													+ '</a></li>'
													+ '<li class="prev disabled"><a href="#">&larr; '
													+ oLang.sPrevious
													+ '</a></li>'
													+ '<li class="next disabled"><a href="#">'
													+ oLang.sNext
													+ ' &rarr; </a></li>'
													+ '<li class="next disabled"><a href="#">'
													+ oLang.sLast
													+ ' &rarr; </a></li>'
													+ '</ul>');
							var els = $('a', nPaging);
							$(els[0]).bind('click.DT', {
								action : "first"
							}, fnClickHandler);
							$(els[1]).bind('click.DT', {
								action : "previous"
							}, fnClickHandler);
							$(els[2]).bind('click.DT', {
								action : "next"
							}, fnClickHandler);
							$(els[3]).bind('click.DT', {
								action : "last"
							}, fnClickHandler);
						},
						"fnUpdate" : function(oSettings, fnDraw) {

							var iListLength = 5;
							var oPaging = oSettings.oInstance.fnPagingInfo();
							var an = oSettings.aanFeatures.p;
							var i, j, sClass, iStart, iEnd, iHalf = Math
									.floor(iListLength / 2);

							if (oPaging.iTotalPages < iListLength) {
								iStart = 1;
								iEnd = oPaging.iTotalPages;
							} else if (oPaging.iPage <= iHalf) {
								iStart = 1;
								iEnd = iListLength;
							} else if (oPaging.iPage >= (oPaging.iTotalPages - iHalf)) {
								iStart = oPaging.iTotalPages - iListLength + 1;
								iEnd = oPaging.iTotalPages;
							} else {
								iStart = oPaging.iPage - iHalf + 1;
								iEnd = iStart + iListLength - 1;
							}

							for (i = 0, iLen = an.length; i < iLen; i++) {

								// Remove the middle elements
								$('li:gt(0)', an[i]).filter(':not(:last)')
										.remove();

								// Add the new list items and their event
								// handlers

								// modified for IMPC to show last page with
								// '...' in front of it
								// but omit '...' when last page is within last
								// five pages
								var count = 0;
								for (j = iStart; j <= iEnd; j++) {

									count++;
									sClass = (j == oPaging.iPage + 1) ? 'class="active"'
											: '';

									if (j != oPaging.iTotalPages) {

										$(
												'<li ' + sClass
														+ '><a href="#">' + j
														+ '</a></li>')
												.insertBefore(
														$('li:last', an[i])[0])
												.bind(
														'click',
														function(e) {
															e.preventDefault();
															oSettings._iDisplayStart = (parseInt(
																	$('a', this)
																			.text(),
																	10) - 1)
																	* oPaging.iLength;
															fnDraw(oSettings);
														});

										if (count == iListLength) {

											$(
													"<li><span class='ellipse'>...</span></li>")
													.insertBefore(
															$('li:last', an[i])[0]);

											$(
													'<li><a href="#">'
															+ oPaging.iTotalPages
															+ '</a></li>')
													.insertBefore(
															$('li:last', an[i])[0])
													.bind(
															'click',
															function(e) {
																e
																		.preventDefault();
																oSettings._iDisplayStart = (parseInt(
																		$('a',
																				this)
																				.text(),
																		10) - 1)
																		* oPaging.iLength;
																fnDraw(oSettings);
															});
										}
									}

									if (count <= iListLength
											&& j == oPaging.iTotalPages) {
										$(
												'<li ' + sClass
														+ '><a href="#">'
														+ oPaging.iTotalPages
														+ '</a></li>')
												.insertBefore(
														$('li:last', an[i])[0])
												.bind(
														'click',
														function(e) {
															e.preventDefault();
															oSettings._iDisplayStart = (parseInt(
																	$('a', this)
																			.text(),
																	10) - 1)
																	* oPaging.iLength;
															fnDraw(oSettings);
														});
									}
								}

								// Add / remove disabled classes from the static
								// elements
								if (oPaging.iPage === 0) {
									$('li:first', an[i]).addClass('disabled');
								} else {
									$('li:first', an[i])
											.removeClass('disabled');
								}

								if (oPaging.iPage === oPaging.iTotalPages - 1
										|| oPaging.iTotalPages === 0) {
									$('li:last', an[i]).addClass('disabled');
								} else {
									$('li:last', an[i]).removeClass('disabled');
								}
							}
						}
					}
				});

// Set the classes that TableTools uses to something suitable for Bootstrap
/*
 * $.extend( true, $.fn.DataTable.TableTools.classes, { "container":
 * "btn-group", "buttons": { "normal": "btn", "disabled": "btn disabled" },
 * "collection": { "container": "DTTT_dropdown dropdown-menu", "buttons": {
 * "normal": "", "disabled": "disabled" } } } );
 */
// Have the tableTools collection use a bootstrap compatible dropdown
$.extend(true, $.fn.DataTable.TableTools.DEFAULTS.oTags, {
	"collection" : {
		"container" : "ul",
		"button" : "li",
		"liner" : "a"
	}
});

$.fn.dataTableExt.sErrMode = 'throw'; // override default alert

$.extend($.fn.dataTableExt.oStdClasses, {
	"sWrapper" : "dataTables_wrapper form-inline"
});

// Sort image columns based on the content of the title tag
$.extend($.fn.dataTableExt.oSort, {
	"alt-string-pre" : function(a) {
		return a.match(/alt="(.*?)"/)[1].toLowerCase();
	},
	"alt-string-asc" : function(a, b) {
		console.log("SOORTING");
		return ((a < b) ? -1 : ((a > b) ? 1 : 0));

	},
	"alt-string-desc" : function(a, b) {
		return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	}
});
$.fn.dataTableExt.oApi.fnStandingRedraw = function(oSettings) {
	if (oSettings.oFeatures.bServerSide === false) {
		var before = oSettings._iDisplayStart;

		oSettings.oApi._fnReDraw(oSettings);

		// iDisplayStart has been reset to zero - so lets change it back
		oSettings._iDisplayStart = before;
		oSettings.oApi._fnCalculateEnd(oSettings);
	}

	// draw the 'current' page
	oSettings.oApi._fnDraw(oSettings);
};

// fix jQuery UIs autocomplete width
$.extend($.ui.autocomplete.prototype.options, {
	open : function(event, ui) {
		$(this).autocomplete("widget").css({
			"width" : ($(this).width() + "px")
		});
	}
});


})(jQuery);