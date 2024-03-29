/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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
 * toggle: used for the image dropdown.
 *
 */
jQuery(document).ready(	function() {

        function getURLParameter(name) {
          return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null
        }

//        try {
//            $.ajax({
//                    url: baseUrl + '/genesAllele2/' + gene_id,
//
//                    timeout: 5000,
//                    success: function (response) {
//
//                        $('#allele2').html(response);
//                    }
//                    ,error: function(x, t, m) {
//                        var errorMsg='<td class="gene-data" id="allele_links"><font color="red"><font color="red">Error trying to retrieve allele product infomation</font></td>';
//                        $('#allele2').html(errorMsg);
//                    }
//            });
//        }
//        catch(err) {
//            console.log("#### Exception: " + err);
//        }
        
//        try {
//            $.ajax({
//                    url: baseUrl + '/orderSection?acc=' + gene_id,
//
//                    timeout: 5000,
//                    success: function (response) {
//
//                        $('#order_product').html(response);
//                    }
//                    ,error: function(x, t, m) {
//                        var errorMsg='<td class="gene-data" id="allele_links"><font color="red"><font color="red">Error trying to retrieve allele product infomation</font></td>';
//                        $('#order_product').html(errorMsg);
//                    }
//            });
//        }
//        catch(err) {
//            console.log("#### Exception: " + err);
//        }

        $('.qcData').each(function(){
            var type = $(this).data("type");
            var name = $(this).data("name");
            var alleleType = $(this).data("alleletype");

            var bare_str = "";
            if (bare === "true"){ bare_str = "&bare=" + bare;}
            var url = baseUrl + '/alleles/qc_data/' + type + '/' + name + "?simple=true" + bare_str; 

            if( ! type || ! name) {

                $(this).html('<p>Not found!</p>');
                return;
            }

        	$.ajax({
		    url: url,
		    timeout: 2000,
                    context: this,
		    success: function (response) {

                        try {
                            $(this).html(response);
                        }
                        catch(err) {
                            console.log("#### Exception: " + err);
                        }

		    }
		    ,error: function(x, t, m) {
			var errorMsg='<td>QC Data Link:</td><td class="gene-data" id="allele_links"><font color="red"><font color="red">Error trying to retrieve QC Data infomation</font></td>';
	    	        $(this).html(errorMsg);
	            }
	        });
        });

        $(".hasTooltip").each(function(){
            $(this).qtip({
                content: {text: $(this).next('div')},
                position: {
			my: 'top middle',
			at: 'bottom middle'
			},
                style: { classes: 'ui-tooltip-wideimage'
                     }
            });
        });

    function toggleTable(id) {
        var target = "#" + id + "_toggle";
        $(target).on({'click':function(event){
        event.preventDefault();
        $("#" + id + " .rest").toggle("fast");

        if($(target).hasClass("toggle_closed")) {
            $(target).removeClass("toggle_closed");
            $(target).addClass("toggle_open");
            var type = $(target).data( "type" );
            $(target).text("Hide " + type);
        }
        else {
            $(target).removeClass("toggle_open");
            $(target).addClass("toggle_closed");
            var type = $(target).data( "type" );
            var count = $(target).data( "count" );
            $(target).text("Show all " + count + " " + type);
        }
        }});
    }

    toggleTable("mouse_table");
    toggleTable("es_cell_table");
    toggleTable("targeting_vector_table");

    $("#mice_order_contact_button").on({'click':function(event){
        if($("#mouse_table_toggle").hasClass("toggle_closed")) {
            $( "#mouse_table_toggle" ).trigger( "click" );
        }
    }});

    $("#es_cell_order_contact_button").on({'click':function(event){
        if($("#es_cell_table_toggle").hasClass("toggle_closed")) {
            $( "#es_cell_table_toggle" ).trigger( "click" );
        }
    }});

});