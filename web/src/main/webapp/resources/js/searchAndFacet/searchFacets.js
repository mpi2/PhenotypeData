/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * sideBarFacetWidget: based on the results retrieved by the autocompleteWidget
 * and displays the facet results on the left bar.
 * 
 */
(function ($) {
    'use strict';

    /**
     * This function is called from search.jsp. Relevant for disease faceting.
     * 
     * @param {type} fkey
     * @returns {searchFacetsL#20.$.fn.labelMap.labels|labels}
     */
    $.fn.labelMap = function (fkey) {
        var labels = {
            // labels for faceting from disease core
            'human_curated': 'From human data (OMIM, Orphanet)',
            'impc_predicted_known_gene': 'From human data with IMPC prediction',
            'mgi_predicted_known_gene': 'From human data with MGI prediction',
            'mouse_curated': 'From mouse data (MGI)',
            'impc_predicted': 'From IMPC data',
            'impc_novel_predicted_in_locus': 'Novel IMPC prediction in linkage locus',
            'mgi_predicted': 'From MGI data',
            'mgi_novel_predicted_in_locus': 'Novel MGI prediction in linkage locus',
            // labels for faceting from phenodigm core            
            'human_curated_gene': 'From human data (OMIM, Orphanet)',
            'impc_model_with_curated_gene': 'From human data with IMPC model',
            'mgi_model_with_curated_gene': 'From human data with MGI model',
            'impc_model_with_computed_association': 'From IMPC data',
            'mgi_model_with_computed_association': 'From MGI data'            
        };
        return labels[fkey];
    };
    

    $.fn.displayFacets = function (type, json) {
        if (type === "gene") {
            displayGeneFacet(json);
        } else if (type === "mp") {
            displayMpFacet(json);
        } else if (type === "disease1") {
            displayDisease1Facet(json);
        } else if (type === "disease") {
            displayDiseaseFacet(json);
        } else if (type === "anatomy") {
            displayAnatomyFacet(json);
        } else if (type === "impc_images") {
            displayImpc_imagesFacet(json);
        } else if (type === "images") {
            displayImagesFacet(json);
        } else if (type === "allele2") {
            displayProductFacet(json);
        }
        $('div.facetSrchMsg').html("");
    };


    function displayGeneFacet(json) {

        var self = this;
        var numFound = json.iTotalRecords;

        /*-------------------------------------------------------*/
        /* ------ displaying sidebar and update dataTable ------ */
        /*-------------------------------------------------------*/
        var foundMatch = {'phenotyping': 0, 'production': 0, 'latest_production_centre': 0, 'latest_phenotyping_centre': 0, 'marker_type': 0, 'embryo_viewer': 0};

        if (numFound > 0) {

            var phenoCompleteLabel = "Approved";

            // subfacet: IMPC mouse phenotyping status
            var phenoStatusSect = $("<li class='fcatsection phenotyping' + ></li>");
            phenoStatusSect.append($('<span></span>').attr({'class': 'flabel'}).text('IMPC Phenotyping Status'));

            var pheno_count = {};

            var aImitsPhenos = {'Phenotyping Complete': phenoCompleteLabel,
                'Phenotyping Started': 'Started',
                'Phenotype Attempt Registered': 'Attempt Registered'};

            var phenoStatusFacetField = 'latest_phenotype_status';
            var phenoCount = 0;

            //var phenoFieldList = json.facet_counts['facet_fields'][phenoStatusFacetField];
            var phenoFieldList = json.facet_fields[phenoStatusFacetField];

            if (typeof phenoFieldList != 'undefined' && phenoFieldList.length != 0) {
                phenoCount = 1;
                foundMatch.phenotyping++;
                for (var j = 0; j < phenoFieldList.length; j += 2) {
                    // only want these statuses

                    var fieldName = phenoFieldList[j];
                    if (fieldName == 'Phenotype Attempt Registered' ||
                            fieldName == 'Phenotyping Started' ||
                            fieldName == 'Phenotyping Complete') {

                        //fieldName == 'Legacy' ){

                        pheno_count[aImitsPhenos[fieldName]] = phenoFieldList[j + 1];
                    }
                }
            }

            //pheno_count['Legacy'] = json.facet_fields['legacy_phenotype_status'][1];

            var phenoUlContainer = $("<ul></ul>");

            var phenotypingStatuses = {
                'Approved': {'fq': phenoStatusFacetField, 'val': 'Phenotyping Complete'},
                'Started': {'fq': phenoStatusFacetField, 'val': 'Phenotyping Started'},
                'Attempt Registered': {'fq': phenoStatusFacetField, 'val': 'Phenotype Attempt Registered'},
                //'Legacy':{'fq':'legacy_phenotype_status', 'val':'1'}
            };

            //var aPhenos = [phenoCompleteLabel, 'Started', 'Attempt Registered', 'Legacy'];
            var aPhenos = [phenoCompleteLabel, 'Started', 'Attempt Registered'];

            for (var i = 0; i < aPhenos.length; i++) {
                var phenotypingStatusFq = phenotypingStatuses[aPhenos[i]].fq;
                var phenotypingStatusVal = phenotypingStatuses[aPhenos[i]].val;
                var count = pheno_count[aPhenos[i]];
                var isGrayout = count == 0 ? 'grayout' : '';

                if (count !== undefined) {
                    //alert(phenotypingStatusFq + ' --- '+ phenotypingStatusVal + ' --- '+ count);
                    var liContainer = $("<li></li>").attr({'class': 'fcat phenotyping'});

                    var coreField = 'gene|' + phenotypingStatusFq + '|';
                    var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + phenotypingStatusVal + '|' + count + '|phenotyping'});
                    liContainer.removeClass('grayout').addClass(isGrayout);
                    var flabel = $('<span></span>').attr({'class': 'flabel'}).text(aPhenos[i]);
                    var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);

                    liContainer.append(chkbox, flabel, fcount);
                    phenoUlContainer.append(liContainer);
                }
            }
            phenoStatusSect.append(phenoUlContainer);
            $('div.flist li#gene > ul').append(phenoStatusSect);

            // subfacet: IMPC mouse production status
            var prodStatusSect = $("<li class='fcatsection production'></li>");
            prodStatusSect.append($('<span></span>').attr({'class': 'flabel'}).text('IMPC Mouse Production Status'));

            var status_facets = json.facet_fields['status'];
            if (typeof status_facets != 'undefined') {
                foundMatch.production = status_facets.length;
                var status_count = {};

                for (var i = 0; i < status_facets.length; i += 2) {
                    var type = status_facets[i];
                    var count = status_facets[i + 1];
                    status_count[type] = count;
                }

                var prodUlContainer = $("<ul></ul>");

                // status ordered in hierarchy
                for (var i = 0; i < MPI2.searchAndFacetConfig.geneStatuses.length; i++) {
                    var status = MPI2.searchAndFacetConfig.geneStatuses[i];
                    var count = status_count[MPI2.searchAndFacetConfig.geneStatuses[i]];
                    var isGrayout = count == 0 ? 'grayout' : '';

                    if (count !== undefined) {
                        var liContainer = $("<li></li>").attr({'class': 'fcat production'});

                        var coreField = 'gene|status|';
                        var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + status + '|' + count + '|production'});
                        liContainer.removeClass('grayout').addClass(isGrayout);

                        liContainer.append(chkbox);
                        liContainer.append($('<span class="flabel">' + status + '</span>'));
                        liContainer.append($('<span class="fcount">' + count + '</span>'));
                        prodUlContainer.append(liContainer);

                    }
                }
                prodStatusSect.append(prodUlContainer);

                $('div.flist li#gene > ul').append(prodStatusSect);
            }

            // subfacet: IMPC mouse production/phenotyping centers
            var centers = {
                'productionCenter': {'facet': 'latest_production_centre', 'label': 'IMPC Mouse Production Center'},
                'phenotypingCenter': {'facet': 'latest_phenotyping_centre', 'label': 'IMPC Mouse Phenotype Center'}
            };
            for (var c in centers) {

                var centerSect = $("<li class='fcatsection " + centers[c].facet + "'></li>");
                centerSect.append($('<span></span>').attr({'class': 'flabel'}).text(centers[c].label));

                var center_facets = json.facet_fields[centers[c].facet];


                foundMatch[centers[c].facet] = center_facets.length;

                var centerUlContainer = $("<ul></ul>");

                for (var i = 0; i < center_facets.length; i += 2) {
                    var center = center_facets[i];
                    var count = center_facets[i + 1];

                    if (center != '') { // skip solr field which value is an empty string
                        var liContainer = $("<li></li>").attr({'class': 'fcat ' + centers[c].facet});
                        var coreField = 'gene|' + centers[c].facet + '|';
                        var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + center + '|' + count + '|' + centers[c].facet});
                        var isGrayout = count == 0 ? 'grayout' : '';
                        liContainer.removeClass('grayout').addClass(isGrayout);

                        liContainer.append(chkbox);
                        liContainer.append($('<span class="flabel">' + center + '</span>'));
                        liContainer.append($('<span class="fcount">' + count + '</span>'));
                        centerUlContainer.append(liContainer);
                    }
                }

                centerSect.append(centerUlContainer);
                $('div.flist li#gene > ul').append(centerSect);
            }

            // subfacet: IMPC gene subtype
            var subTypeSect = $("<li class='fcatsection marker_type'></li>");
            subTypeSect.append($('<span></span>').attr({'class': 'flabel'}).text('Subtype'));

            var mkr_facets = json.facet_fields['marker_type'];
            foundMatch.marker_type = mkr_facets.length;
            var unclassified = [];
            var subTypeUlContainer = $("<ul></ul>");

            for (var i = 0; i < mkr_facets.length; i += 2) {
                var liContainer = $("<li></li>").attr({'class': 'fcat marker_type'});
                var type = mkr_facets[i];

                if (type == 'null') {
                    continue;
                }
                var count = mkr_facets[i + 1];
                var coreField = 'gene|marker_type|';
                var isGrayout = count == 0 ? 'grayout' : '';
                liContainer.removeClass('grayout').addClass(isGrayout);

                var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + type + '|' + count + '|marker_type'});
                var flabel = $('<span></span>').attr({'class': 'flabel'}).text(type);
                var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);

                //if ( type == 'protein coding gene' ){
                //	var displayCount = json.useProteinCodingGeneCount == true ? count : numFound;
                //	$('li#gene > span.fcount').text(displayCount);
                //}
                $('li#gene > span.fcount').text(numFound);

                if (type != 'unclassified gene' && type != 'unclassified non-coding RNA gene') {
                    liContainer.append(chkbox, flabel, fcount);
                } else {
                    unclassified.push(liContainer.append(chkbox, flabel, fcount));
                }
                subTypeUlContainer.append(liContainer);
            }

            if (unclassified.length > 0) {
                for (var i = 0; i < unclassified.length; i++) {
                    subTypeUlContainer.append(unclassified[i]);
                }
            }
            subTypeSect.append(subTypeUlContainer);
            $('div.flist li#gene > ul').append(subTypeSect);

            // subfacet: Embryo image viewer
            var embryoViewerSect = $("<li class='fcatsection embryo_viewer'></li>");
            embryoViewerSect.append($('<span></span>').attr({'class': 'flabel'}).text('Embryo 3D Imaging'));

            var embview_facets = json.facet_fields['embryo_data_available'];
            foundMatch.embryo_viewer = embview_facets.length;

            var embview_modalities_facets = json.facet_fields['embryo_modalities'];

            var embryo_analysis_view_name_facets = json.facet_fields['embryo_analysis_view_name'];

            var viewerUlContainer = $("<ul></ul>");

            // no need to show this as this is now split into modalities
            //for ( var i=0; i<embview_facets.length; i+=2 ){
            //	var type = embview_facets[i];
            //	if ( type == 'true' ){
            //		var liContainer = $("<li></li>").attr({'class':'fcat embryo_data_available'});
            //
            //		var type_label = 'available';
            //		var count = embview_facets[i+1];
            //		var coreField = 'gene|embryo_data_available|';
            //		var isGrayout = count == 0 ? 'grayout' : '';
            //		liContainer.removeClass('grayout').addClass(isGrayout);
            //
            //		var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + type + '|' + count + '|embryo_data_available'});
            //		var flabel = $('<span></span>').attr({'class':'flabel'}).text(type_label);
            //		var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
            //
            //		liContainer.append(chkbox, flabel, fcount);
            //
            //		viewerUlContainer.append(liContainer);
            //	}
            //}

            for (var i = 0; i < embview_modalities_facets.length; i += 2) {
                var modality = embview_modalities_facets[i];

                var liContainer = $("<li></li>").attr({'class': 'fcat embryo_modalities'});

                var type_label = modality;
                var count = embview_modalities_facets[i + 1];
                var coreField = 'gene|embryo_modalities|';
                var isGrayout = count == 0 ? 'grayout' : '';
                liContainer.removeClass('grayout').addClass(isGrayout);

                var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + modality + '|' + count + '|embryo_modalities'});
                var flabel = $('<span></span>').attr({'class': 'flabel'}).text(type_label);
                var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);

                liContainer.append(chkbox, flabel, fcount);

                viewerUlContainer.append(liContainer);

            }

            for (var i = 0; i < embryo_analysis_view_name_facets.length; i += 2) {
                var volumetry = embryo_analysis_view_name_facets[i];

                var liContainer = $("<li></li>").attr({'class': 'fcat embryo_analysis_view_name'});

                var type_label = volumetry;
                var count = embryo_analysis_view_name_facets[i + 1];
                var coreField = 'gene|embryo_analysis_view_name|';
                var isGrayout = count == 0 ? 'grayout' : '';
                liContainer.removeClass('grayout').addClass(isGrayout);

                var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + volumetry + '|' + count + '|embryo_analysis_view_name'});
                var flabel = $('<span></span>').attr({'class': 'flabel'}).text(type_label);
                var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);

                liContainer.append(chkbox, flabel, fcount);

                viewerUlContainer.append(liContainer);

            }

            embryoViewerSect.append(viewerUlContainer);
            $('div.flist li#gene > ul').append(embryoViewerSect);


            $.fn.initFacetToggles('gene');

            var selectorBase = "div.flist li#gene";

            // subfacet opening/closing behavior
            // collapse all subfacet first, then open the first one that has matches
            $(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');
            $.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);

            // change cursor for grayout filter
            $.fn.cursorUpdate('gene', 'not-allowed');

            // as the facet is open by default, we need to reset the value for widgetOpen
            //MPI2.searchAndFacetConfig.update.widgetOpen = false;

            $('li#gene li.fcat input').click(function () {

                // highlight the item in facet
                updateCheckedFilter($(this));
            });
        }
    }

    function displayMpFacet(json) {

        var facetField = "top_level_mp_term_inclusive";
        var aTopLevelCount = json.facet_fields[facetField];
        //var mpUlContainer = $("<ul></ul>");
        var mpUlContainer = $("li#mp ul");
        var liContainer_viable = null;
        var liContainer_fertile = null;

        $('li#mp > span.fcount').text(json.iTotalRecords);

        // top level MP terms
        for (var i = 0; i < aTopLevelCount.length; i += 2) {
            var topLevelName = aTopLevelCount[i];
            if (topLevelName == 'mammalian phenotype') {
                continue;
            }

            var count = aTopLevelCount[i + 1];
            var isGrayout = count == 0 ? 'grayout' : '';

            var liContainer = $("<li></li>").attr({'class': 'fcat'});
            liContainer.removeClass('grayout').addClass(isGrayout);

            //var coreField = 'mp|annotatedHigherLevelMpTermName|' + topLevelName + '|' + count;
            var coreField = 'mp|' + facetField + '|' + topLevelName + '|' + count;
            var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});

            var flabel = $('<span></span>').attr({'class': 'flabel'}).text(topLevelName.replace(' phenotype', ''));
            var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);

            liContainer.append(chkbox, flabel, fcount);

            if (topLevelName == 'reproductive system phenotype') {
                flabel.addClass('fertility');
                liContainer_fertile = liContainer;
            } else if (topLevelName == 'mortality/aging') {
                flabel.addClass('viability');
                liContainer_viable = liContainer;
            } else {
                mpUlContainer.append(liContainer);
            }
        }

        // move these 2 top level MPs to top of facet list
        mpUlContainer.prepend(liContainer_fertile);
        mpUlContainer.prepend(liContainer_viable);

        // update all subfacet counts of this facet
        $('div.flist li#mp > ul').append(mpUlContainer);

        $.fn.cursorUpdate('mp', 'not-allowed');

        $.fn.initFacetToggles('mp');

        $('li#mp li.fcat input').click(function () {

            // highlight the item in facet
            updateCheckedFilter($(this));
        });

    }

    /**
     * Original function to display facets for disease page based on old disease core
     * 
     * @param {type} json
     * @returns {undefined}
     */
    function displayDisease1Facet(json) {
        
        var numFound = json.iTotalRecords;
        var foundMatch = {'curated': 0, 'predicted': 0, 'disease_source': 0, 'disease_classes': 0};

        $('li#disease > span.fcount').text(numFound);

        /*-------------------------------------------------------*/
        /* ------ displaying sidebar and update dataTable ------ */
        /*-------------------------------------------------------*/

        if (numFound > 0) {

            var oSubFacets2 = {'curated': {'label': 'With Curated Gene Associations',
                    'subfacets': {'human_curated': 'From human data (OMIM, Orphanet)',
                        'impc_predicted_known_gene': 'From human data with IMPC prediction',
                        'mgi_predicted_known_gene': 'From human data with MGI prediction',
                        'mouse_curated': 'From mouse data (MGI)'}
                },
                'predicted': {'label': 'With Predicted Gene Associations by Phenotype',
                    'subfacets': {'impc_predicted': 'From IMPC data',
                        'impc_novel_predicted_in_locus': 'Novel IMPC prediction in linkage locus',
                        'mgi_predicted': 'From MGI data',
                        'mgi_novel_predicted_in_locus': 'Novel MGI prediction in linkage locus'}
                }
            };


            for (var assoc in oSubFacets2) {
                var label = oSubFacets2[assoc].label;
                var thisFacetSect = $("<li class='fcatsection " + assoc + "'></li>");

                thisFacetSect.append($('<span></span>').attr({'class': 'flabel'}).text(label));

                var thisUlContainer = $("<ul></ul>");

                var subfacets = oSubFacets2[assoc].subfacets;
                for (var fq in subfacets) {
                    if (subfacets.hasOwnProperty(fq)) {

                        var thisSubfacet = subfacets[fq];
                        var oData = json.facet_fields[fq];

                        for (var i = 0; i < oData.length; i = i + 2) {
                            var liContainer = $("<li></li>").attr({'class': 'fcat ' + fq});
                            var dPositive = oData[i];

                            if (dPositive === 'true') {
                                var count = oData[i + 1];
                                var isGrayout = count === 0 ? 'grayout' : '';

                                liContainer.removeClass('grayout').addClass(isGrayout);

                                foundMatch[assoc]++;

                                var diseaseFq = fq;
                                var coreField = 'disease|' + diseaseFq + '|';
                                var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + 'true' + '|' + count + '|' + assoc});
                                var flabel = $('<span></span>').attr({'class': 'flabel'}).text(thisSubfacet);
                                var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);
                                liContainer.append(chkbox, flabel, fcount);
                                thisUlContainer.append(liContainer);
                            }
                        }
                        thisFacetSect.append(thisUlContainer);
                        $('div.flist li#disease > ul').append(thisFacetSect);

                    }
                }
            }

            // Subfacets: disease classifications/sources
            var oSubFacets1 = {'disease_source': 'Sources', 'disease_classes': 'Classifications'};
            for (var fq in oSubFacets1) {
                var label = oSubFacets1[fq];
                var aData = json.facet_fields[fq];

                //table.append($('<tr></tr>').attr({'class':'facetSubCat '+ trCap + ' ' + fq}).append($('<td></td>').attr({'colspan':3}).text(label)));
                var thisFacetSect = $("<li class='fcatsection " + fq + "'></li>");
                thisFacetSect.append($('<span></span>').attr({'class': 'flabel'}).text(label));

                var unclassified;
                var thisUlContainer = $("<ul></ul>");

                for (var i = 0; i < aData.length; i = i + 2) {
                    var liContainer = $("<li></li>").attr({'class': 'fcat ' + fq});

                    var subFacetName = aData[i];

                    var count = aData[i + 1];
                    foundMatch[fq]++;

                    var diseaseFq = fq;
                    var coreField = 'disease|' + diseaseFq + '|';
                    var trClass = fq + 'Tr';
                    var isGrayout = count === 0 ? 'grayout' : '';
                    liContainer.removeClass('grayout').addClass(isGrayout);

                    var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + subFacetName + '|' + count + '|' + fq});
                    var flabel = $('<span></span>').attr({'class': 'flabel'}).text(subFacetName);
                    var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);


                    if (subFacetName !== 'unclassified') {
                        liContainer.append(chkbox, flabel, fcount);
                    } else {
                        unclassified = liContainer.append(chkbox, flabel, fcount);
                    }
                    thisUlContainer.append(liContainer);
                }

                if (fq === 'disease_classes' && unclassified) {
                    thisUlContainer.append(unclassified);
                }
                thisFacetSect.append(thisUlContainer);
                $('div.flist li#disease > ul').append(thisFacetSect);
            }


            // no actions allowed when facet count is zero
            $.fn.cursorUpdate('disease', 'not-allowed');

            // disease_source is open and rest of disease subfacets are collapsed by default
            $('div.flist li#disease > ul li:nth-child(1)').addClass('open');

            var selectorBase = "div.flist li#disease";
            // collapse all subfacet first, then open the first one that has matches
            $(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');
            $.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);

            // change cursor for grayout filter
            $.fn.cursorUpdate('disease', 'not-allowed');

            $.fn.initFacetToggles('disease');

            $('li#disease li.fcat input').click(function () {
                // highlight the item in facet
                updateCheckedFilter($(this));
            });

        }
    }

    /**
     * Handling of facets for the disease page drive by the phenodigm core
     * 
     * TK: Requires cleanup
     * 
     * @param {type} json
     * @returns {undefined}
     */
    function displayDiseaseFacet(json) {
        //console.log(json);
        
        var numFound = json.iTotalRecords;
        var foundMatch = {'curated': 0, 'predicted': 0, 'disease_source': 0, 'disease_classes': 0};

        $('li#disease > span.fcount').text(numFound);

        /*-------------------------------------------------------*/
        /* ------ displaying sidebar and update dataTable ------ */
        /*-------------------------------------------------------*/
        
        if (numFound > 0) {
            
            var oSubFacets2 = {
                'curated': {
                    'label': 'With Curated Gene Associations',
                    'subfacets': {
                        'human_curated_gene': $.fn.labelMap('human_curated_gene'),
                        'impc_model_with_curated_gene': $.fn.labelMap('impc_model_with_curated_gene'),
                        'mgi_model_with_curated_gene': $.fn.labelMap('mgi_model_with_curated_gene')
                    }
                },
                'predicted': {
                    'label': 'With Computed Associations by Phenotype',
                    'subfacets': {
                        'impc_model_with_computed_association': $.fn.labelMap('impc_model_with_computed_association'),
                        'mgi_model_with_computed_association': $.fn.labelMap('mgi_model_with_computed_association')
                    }
                }
            };            
            
            for (var assoc in oSubFacets2) {               
                var label = oSubFacets2[assoc].label;
                var thisFacetSect = $("<li class='fcatsection " + assoc + "'></li>");

                thisFacetSect.append($('<span></span>').attr({'class': 'flabel'}).text(label));

                var thisUlContainer = $("<ul></ul>");

                var subfacets = oSubFacets2[assoc].subfacets;
                for (var fq in subfacets) {                                        
                    if (subfacets.hasOwnProperty(fq)) {

                        var thisSubfacet = subfacets[fq];
                        var oData = json.facet_fields[fq];

                        for (var i = 0; i < oData.length; i = i + 2) {
                            var liContainer = $("<li></li>").attr({'class': 'fcat ' + fq});
                            var dPositive = oData[i];

                            if (dPositive == 'true') {
                                var count = oData[i + 1];
                                var isGrayout = count == 0 ? 'grayout' : '';

                                liContainer.removeClass('grayout').addClass(isGrayout);

                                foundMatch[assoc]++;

                                var diseaseFq = fq;
                                var coreField = 'disease|' + diseaseFq + '|';
                                var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + 'true' + '|' + count + '|' + assoc});
                                var flabel = $('<span></span>').attr({'class': 'flabel'}).text(thisSubfacet);
                                var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);
                                liContainer.append(chkbox, flabel, fcount);
                                thisUlContainer.append(liContainer);
                            }
                        }
                        thisFacetSect.append(thisUlContainer);
                        $('div.flist li#disease > ul').append(thisFacetSect);

                    }
                }
            }            

            // Subfacets: disease classifications/sources
            var oSubFacets1 = {'disease_source': 'Sources', 'disease_classes': 'Classifications'};
            for (var fq in oSubFacets1) {
                var label = oSubFacets1[fq];
                var aData = json.facet_fields[fq];

                //table.append($('<tr></tr>').attr({'class':'facetSubCat '+ trCap + ' ' + fq}).append($('<td></td>').attr({'colspan':3}).text(label)));
                var thisFacetSect = $("<li class='fcatsection " + fq + "'></li>");
                thisFacetSect.append($('<span></span>').attr({'class': 'flabel'}).text(label));

                var unclassified;
                var thisUlContainer = $("<ul></ul>");

                for (var i = 0; i < aData.length; i = i + 2) {
                    var liContainer = $("<li></li>").attr({'class': 'fcat ' + fq});

                    var subFacetName = aData[i];

                    var count = aData[i + 1];
                    foundMatch[fq]++;

                    var diseaseFq = fq;
                    var coreField = 'disease|' + diseaseFq + '|';
                    var trClass = fq + 'Tr';
                    var isGrayout = count == 0 ? 'grayout' : '';
                    liContainer.removeClass('grayout').addClass(isGrayout);

                    var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + subFacetName + '|' + count + '|' + fq});
                    var flabel = $('<span></span>').attr({'class': 'flabel'}).text(subFacetName);
                    var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);


                    if (subFacetName != 'unclassified') {
                        liContainer.append(chkbox, flabel, fcount);
                    } else {
                        unclassified = liContainer.append(chkbox, flabel, fcount);
                    }
                    thisUlContainer.append(liContainer);
                }

                if (fq == 'disease_classes' && unclassified) {
                    thisUlContainer.append(unclassified);
                }
                thisFacetSect.append(thisUlContainer);
                $('div.flist li#disease > ul').append(thisFacetSect);
            }            

            // no actions allowed when facet count is zero
            $.fn.cursorUpdate('disease', 'not-allowed');

            // disease_source is open and rest of disease subfacets are collapsed by default
            $('div.flist li#disease > ul li:nth-child(1)').addClass('open');
            
            var selectorBase = "div.flist li#disease";
            // collapse all subfacet first, then open the first one that has matches
            $(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');
            $.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);

            // change cursor for grayout filter
            $.fn.cursorUpdate('disease', 'not-allowed');
            
            $.fn.initFacetToggles('disease');

            $('li#disease li.fcat input').click(function () {

                // highlight the item in facet
                updateCheckedFilter($(this));
            });            
        }
    }

    function displayAnatomyFacet(json) {

        var core = "anatomy";
        var topLevelField = "selected_top_level_anatomy_term";
        var aTopLevelCount = json.facet_fields[topLevelField];

        var stageField = "stage";
        var aStageCount = json.facet_fields[stageField];

        var maUlContainer = $("li#" + core + " ul");

        $('li#' + core + ' > span.fcount').text(json.iTotalRecords);

        // stages
        for (var i = 0; i < aStageCount.length; i += 2) {

            var liContainer = $("<li></li>").attr({'class': 'fcat'});

            var count = aStageCount[i + 1];
            var coreField = core + '|' + stageField + '|' + aStageCount[i] + '|' + count;
            var isGrayout = count == 0 ? 'grayout' : '';
            liContainer.removeClass('grayout').addClass(isGrayout);

            var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
            var flabel = $('<span></span>').attr({'class': 'flabel'}).text(aStageCount[i]);
            var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);
            liContainer.append(chkbox, flabel, fcount);
            maUlContainer.append(liContainer);
        }

        // filter separator
        if (json.iTotalRecords > 0) {
            maUlContainer.append("<li><div id='anaSep'>&nbsp;</div></li>");
        }

        // selected top level MA terms
        for (var i = 0; i < aTopLevelCount.length; i += 2) {

            var liContainer = $("<li></li>").attr({'class': 'fcat'});

            var count = aTopLevelCount[i + 1];
            var coreField = core + '|' + topLevelField + '|' + aTopLevelCount[i] + '|' + count;
            var isGrayout = count == 0 ? 'grayout' : '';
            liContainer.removeClass('grayout').addClass(isGrayout);

            var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
            var flabel = $('<span></span>').attr({'class': 'flabel'}).text(aTopLevelCount[i]);
            var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);
            liContainer.append(chkbox, flabel, fcount);
            maUlContainer.append(liContainer);
        }

        // update all subfacet counts of this facet
        $('div.flist li#' + core + ' > ul').append(maUlContainer);

        // change cursor for grayout filter
        $.fn.cursorUpdate(core, 'not-allowed');

        $.fn.initFacetToggles(core);

        $('li#' + core + ' li.fcat input').click(function () {

            // highlight the item in facet
            updateCheckedFilter($(this));
        });

    }

    function displayImpc_imagesFacet(json) {

        $('li#impc_images > span.fcount').text(json.imgCount);

        //var foundMatch = {'Phenotype':0, 'Anatomy':0, 'Procedure':0, 'Gene':0};
        var foundMatch = {'Procedure': 0, 'Anatomy': 0};

        var aFacetFields = json.facet_fields; // eg. expName, symbol..

        // appearance order of subfacets
        //var aSubFacetNames = ['top_level_mp_term','selected_top_level_ma_term','procedure_name','marker_type'];
        var aSubFacetNames = ['procedure_name', 'selected_top_level_anatomy_term'];

        var displayLabel = {
            /*annotated_or_inferred_higherLevelMaTermName: 'Anatomy',
             expName : 'Procedure',
             annotatedHigherLevelMpTermName: 'Phenotype',
             subtype: 'Gene'
             */
            //top_level_mp_term: 'Phenotype',
            procedure_name: 'Procedure',
            selected_top_level_anatomy_term: 'Anatomy'
        };

        for (var n = 0; n < aSubFacetNames.length; n++) {
            var facetName = aSubFacetNames[n];

            var label = displayLabel[facetName];

            var thisFacetSect = $("<li class='fcatsection " + label + "'></li>");
            thisFacetSect.append($('<span></span>').attr({'class': 'flabel'}).text(label));

            var thisUlContainer = $("<ul></ul>");

            // add stage for Anatomy
            if (facetName == 'selected_top_level_anatomy_term') {

                var aStgFields = ["adult", "embryo"];

                for (var s = 0; s < aStgFields.length; s++) {

                    var liContainer = $("<li></li>").attr({'class': 'fcat ' + "stage"});
                    if (s == 1) {
                        s += 1;
                    }
                    var fieldName = aFacetFields["stage"][s];
                    var count = aFacetFields["stage"][s + 1];

                    var isGrayout = count == 0 ? 'grayout' : '';
                    liContainer.removeClass('grayout').addClass(isGrayout);

                    var coreField = 'impc_images|' + "stage" + '|' + fieldName + '|' + count + '|' + facetName;
                    var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
                    var flabel = $('<span></span>').attr({'class': 'flabel'}).text(fieldName);
                    var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);

                    thisUlContainer.append(liContainer.append(chkbox, flabel, fcount));
                }

                // filter separator
                if (json.iTotalRecords > 0) {
                    thisUlContainer.append("<li><div id='anaSep'>&nbsp;</div></li>");
                }
            }

            for (var i = 0; i < aFacetFields[facetName].length; i += 2) {
                //console.log("field name: " + aFacetFields[facetName][i]);
                //console.log(typeof aFacetFields[facetName][i]);

                if (typeof aFacetFields[facetName][i] == 'string') {
                    var liContainer = $("<li></li>").attr({'class': 'fcat ' + facetName});

                    var fieldName = aFacetFields[facetName][i];

                    var count = aFacetFields[facetName][i + 1];
                    var label = displayLabel[facetName];
                    foundMatch[label]++;



                    var isGrayout = count == 0 ? 'grayout' : '';
                    liContainer.removeClass('grayout').addClass(isGrayout);

                    var coreField = 'impc_images|' + facetName + '|' + fieldName + '|' + count + '|' + label;
                    var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
                    var flabel = $('<span></span>').attr({'class': 'flabel'}).text(fieldName.replace(' phenotype', ''));
                    var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);
                    thisUlContainer.append(liContainer.append(chkbox, flabel, fcount));
                }
            }

            thisFacetSect.append(thisUlContainer);
            $('div.flist li#impc_images > ul').append(thisFacetSect);
        }

        var selectorBase = "div.flist li#impc_images";
        // collapse all subfacet first, then open the first one that has matches
        $(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');
        $.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);

        // change cursor for grayout filter
        $.fn.cursorUpdate('impc_images', 'not-allowed');

        $.fn.initFacetToggles('impc_images');

        $('li#impc_images li.fcat input').click(function () {

            // highlight the item in facet
            updateCheckedFilter($(this));
        });

    }

    function displayImagesFacet(json) {

        $('li#images > span.fcount').text(json.imgCount);

        var foundMatch = {'Phenotype': 0, 'Anatomy': 0, 'Procedure': 0, 'Gene': 0};
        var aFacetFields = json.facet_fields; // eg. expName, symbol..

        // appearance order of subfacets
        //var aSubFacetNames = ['top_level_mp_term','selected_top_level_ma_term','procedure_name','marker_type'];
        var aSubFacetNames = ['selected_top_level_ma_term', 'procedure_name', 'marker_type'];

        var displayLabel = {
            /*annotated_or_inferred_higherLevelMaTermName: 'Anatomy',
             expName : 'Procedure',
             annotatedHigherLevelMpTermName: 'Phenotype',
             subtype: 'Gene'
             */
            //top_level_mp_term: 'Phenotype',
            procedure_name: 'Procedure',
            selected_top_level_ma_term: 'Anatomy',
            marker_type: 'Gene'
        };


        for (var n = 0; n < aSubFacetNames.length; n++) {
            var facetName = aSubFacetNames[n];
            var label = displayLabel[facetName];

            var thisFacetSect = $("<li class='fcatsection " + label + "'></li>");
            thisFacetSect.append($('<span></span>').attr({'class': 'flabel'}).text(label));

            var thisUlContainer = $("<ul></ul>");

            for (var i = 0; i < aFacetFields[facetName].length; i += 2) {

                var liContainer = $("<li></li>").attr({'class': 'fcat ' + facetName});

                var fieldName = aFacetFields[facetName][i];
                var count = aFacetFields[facetName][i + 1];
                var label = displayLabel[facetName];
                foundMatch[label]++;

                var isGrayout = count == 0 ? 'grayout' : '';
                liContainer.removeClass('grayout').addClass(isGrayout);

                var coreField = 'images|' + facetName + '|' + fieldName + '|' + count + '|' + label;
                var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
                var flabel = $('<span></span>').attr({'class': 'flabel'}).text(fieldName.replace(' phenotype', ''));
                var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);
                thisUlContainer.append(liContainer.append(chkbox, flabel, fcount));
            }

            thisFacetSect.append(thisUlContainer);
            $('div.flist li#images > ul').append(thisFacetSect);
        }

        var selectorBase = "div.flist li#images";
        // collapse all subfacet first, then open the first one that has matches
        $(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');
        $.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);

        // change cursor for grayout filter
        $.fn.cursorUpdate('images', 'not-allowed');

        $.fn.initFacetToggles('images');

        $('li#images li.fcat input').click(function () {

            // highlight the item in facet
            updateCheckedFilter($(this));
        });


    }

    function displayProductFacet(json) {
        var self = this;
        var numFound = json.iTotalRecords;
        var foundMatch = {'mutation_type_str': 0, 'allele_category_str': 0, 'es_cell_available': 0, 'mouse_available': 0, 'targeting_vector_available': 0};

        var coreName = "allele2";
        $('li#' + coreName + ' > span.fcount').text(numFound);

        /*-------------------------------------------------------*/
        /* ------ displaying sidebar and update dataTable ------ */
        /*-------------------------------------------------------*/

        if (numFound > 0) {

            // Subfacets: availability for es cells, mouse, targeting vector
            //var thisUlContainer = $("<ul></ul>");

            var oSubFacets = {'targeting_vector_available': 'Targeting Vector Available', 'es_cell_available': 'ES Cell Available', 'mouse_available': 'Mouse Available'};
            for (var fq in oSubFacets) {

                var aData = json.facet_fields[fq];
                for (var i = 0; i < aData.length; i = i + 2) {

                    var subFacetName = aData[i];
                    if (subFacetName == "true") { // ie, available
                        var displayedSubFacetName = oSubFacets[fq];
                        var liContainer = $("<li></li>").attr({'class': 'fcat ' + fq});
                        var count = aData[i + 1];
                        foundMatch[fq]++;

                        var coreField = coreName + '|' + fq + '|';
                        var trClass = fq + 'Tr';
                        var isGrayout = count == 0 ? 'grayout' : '';
                        liContainer.removeClass('grayout').addClass(isGrayout);

                        var chkbox = $('<input></input>').attr({
                            'type': 'checkbox',
                            'rel': coreField + subFacetName + '|' + count + '|' + fq
                        });
                        var flabel = $('<span></span>').attr({'class': 'flabel'}).text(displayedSubFacetName);
                        var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);

                        liContainer.append(chkbox, flabel, fcount);
                        //thisUlContainer.append(liContainer);
                        $('div.flist li#' + coreName + ' > ul').append(liContainer);
                    }
                }
            }

            var defaultLen = 25;

            // Subfacets: mutation_tyoe / allele category / allele feature
            var oSubFacets1 = {'mutation_type_str': 'Mutation Type', 'allele_category_str': 'Allele Category', 'allele_features_str': 'Allele Feature'};
            for (var fq in oSubFacets1) {
                var label = oSubFacets1[fq];
                var aData = json.facet_fields[fq];

                //table.append($('<tr></tr>').attr({'class':'facetSubCat '+ trCap + ' ' + fq}).append($('<td></td>').attr({'colspan':3}).text(label)));
                var thisFacetSect = $("<li class='fcatsection " + fq + "'></li>");
                thisFacetSect.append($('<span></span>').attr({'class': 'flabel'}).text(label));

                var unclassified;
                var thisUlContainer = $("<ul></ul>");

                for (var i = 0; i < aData.length; i = i + 2) {
                    var liContainer = $("<li></li>").attr({'class': 'fcat ' + fq});

                    var subFacetName = aData[i];

                    var count = aData[i + 1];
                    foundMatch[fq]++;

                    var coreField = coreName + '|' + fq + '|';
                    var trClass = fq + 'Tr';
                    var isGrayout = count == 0 ? 'grayout' : '';
                    liContainer.removeClass('grayout').addClass(isGrayout);

                    var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + subFacetName + '|' + count + '|' + fq});
                    var flabel = $('<span></span>').attr({'class': 'flabel'}).text(subFacetName);
                    var fcount = $('<span></span>').attr({'class': 'fcount'}).text(count);

                    liContainer.append(chkbox, flabel, fcount);
                    thisUlContainer.append(liContainer);
                }

                thisFacetSect.append(thisUlContainer);
                $('div.flist li#' + coreName + ' > ul').append(thisFacetSect);
            }


            // no actions allowed when facet count is zero
            $.fn.cursorUpdate(coreName, 'not-allowed');

            // mutation_type is open and rest of product subfacets are collapsed by default
            $('div.flist li#' + coreName + '> ul li:nth-child(1)').addClass('open');

            var selectorBase = 'div.flist li#' + coreName;
            // collapse all subfacet first, then open the first one that has matches
            $(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');
            $.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);

            // change cursor for grayout filter
            $.fn.cursorUpdate(coreName, 'not-allowed');

            $.fn.initFacetToggles(coreName);

            $('li#' + coreName + ' li.fcat input').click(function () {

                // highlight the item in facet
                updateCheckedFilter($(this));
            });

        }

    }

    function updateCheckedFilter(thisObj) {

        if (!thisObj.siblings('span.flabel').hasClass('filterCheck')) {
            thisObj.siblings('span.flabel').addClass('filterCheck');
        } else {
            thisObj.siblings('span.flabel').removeClass('filterCheck');
        }
    }

}(jQuery));




