<link href="/data/css/vendor/jquery.ui/jquery.ui.core.css" rel="stylesheet" />
    <link href="/data/css/vendor/jquery.ui/jquery.ui.theme.css" rel="stylesheet" /><!--<link href="/data/css/searchPage.css" rel="stylesheet" /> -->
    <form action="/data/search" method="GET"><!--      <i id='sicon' class='fa fa-search'></i>
    <p><input id="q" name="q" placeholder="Search" type="text" /></p>
    <i id='clearIcon' class='fa fa-times'></i> -->
    <p><input id="q" name="q" placeholder="Search" type="text" /></p>
    <style type="text/css">span.sugList {
    font-size: 12px !important;
    text-decoration: none;
}
span.category {
    color: #EF7B0B !important;
}
b.sugTerm {
    color: rgb(9, 120, 161);
    font-weight: normal;
}
input#q {
    padding-right: 10px;
    margin-top: 10px;
    color: gray;
}
div#block-block-12 p {
    margin: 15px 0 -14px 0;
}
/* autocomplete drop down ul */
ul.ui-autocomplete {
    background-color: white;
    border: 1px solid #0978A1;
    padding: 10px 15px;
    z-index: 999;
    -webkit-box-shadow: 0 5px 10px rgba(0, 0, 0, 0.8);
    -moz-box-shadow: 0 5px 10px rgba(0, 0, 0, 0.8);
    box-shadow: 0 5px 10px rgba(0, 0, 0, 0.8);
}
ul.ui-autocomplete li a {
    display: block;
    text-decoration: none;
    padding: 0 0 0 5px !important;
}
ul.ui-autocomplete li {
    font-size: 14px !important;
    border: none !important;
}
ul.ui-autocomplete li a:hover {
    background-color: #D4D4D4 !important;
    border-radius: 4px !important;
    border: none !important;
}
ul.ui-autocomplete a {
    text-decoration: none;
}
ul.ui-autocomplete hr {
    border:none;
    border-top:1px dotted gray;
    height:1px;
    width:100%;
}
a {
    text-decoration: none;
}
</style>
<p>Examples: <a href="/data/search/gene?kw=Ap4e1" style="color: white">Ap4e1</a>, <a href="/data/search/mp?kw='abnormal heart rate'&amp;fq=top_level_mp_term:*" style="color: white">Abnormal Heart Rate</a>, <a href="/data/search/disease?kw=&quot;Bernard-Soulier Syndrome&quot;" style="color: white">Bernard-Soulier Syndrome</a></p>
</form>
<!--<a href="data/search/gene?kw=&quot;chrx\%3A1234567\-4567890&quot;" style="color: white">chrx:1234567-4567890</a>--><script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script><script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script><script type='text/javascript'>

var hostname = window.location.hostname;
var baseUrl = '//' + hostname + '/data';
var solrUrl = '//www.ebi.ac.uk/mi/impc/solr';
if (hostname.indexOf("dev")!=-1) {
    solrUrl = "//wwwdev.ebi.ac.uk/mi/impc/dev/solr";
} else if (hostname.indexOf("beta")!=-1) {
    solrUrl = "//wwwdev.ebi.ac.uk/mi/impc/beta/solr";
}

var $ = jQuery;
$(document).ready(function(){
    var matchedFacet = false;
    var facet2Fq = {
        'gene' : '*:*',
        'mp'   : 'top_level_mp_term:*',
        'disease' : '*:*',
        'anatomy' : 'selected_top_level_anatomy_term:*',
        //'pipeline' : 'pipeline_stable_id:*',
        'images' : '*:*',
        'allele2' : 'type:Allele'
    }

    var facet2Label = {
        'gene'        : 'Genes',
        'mp'          : 'Phenotypes',
        'disease'     : 'Diseases',
        'anatomy'     : 'Anatomy',
        'impc_images' : 'Images',
        'allele2'     : 'Products'
    };

    // if users do not hit ENTER too quickly and wait until drop down list
    // appears, then we know about which facet to display by default
    // else if will be gene facet
    $("form").keypress(function (e) {
        if (e.keyCode == 13 ){
            matchedFacet = matchedFacet ? matchedFacet : 'gene';
            var facet = "gene"; // default
            var input = $('input#q').val().trim();
            input = /^\*\**?\*??$/.test(input) ? '' : input;  // lazy matching
            var re = new RegExp("^'(.*)'$");
            input = input.replace(re, "\"$1\""); // only use double quotes for
            // phrase query
            input = encodeURIComponent(input);

            input = input.replace("%5B", "\\[");
            input = input.replace("%5D", "\\]");
            input = input.replace("%7B", "\\{");
            input = input.replace("%7D", "\\}");
            input = input.replace("%7C", "\\|");
            input = input.replace("%5C", "\\\\");
            input = input.replace("%3C", "\\<");
            input = input.replace("%3E", "\\>");
            input = input.replace("."  , "\\.");
            input = input.replace("("  , "\\(");
            input = input.replace(")"  , "\\)");
            input = input.replace("%2F", "\\/");
            input = input.replace("%60", "\\`");
            input = input.replace("~"  , "\\~");
            input = input.replace("%"  , "\\%");
            input = input.replace("!"  , "\\!");
            input = input.replace("%21", "\\!");
            input = input.replace("-", "\\%2D");
            input = input.replace("-", "\\-");
            if ( /^\\%22.+%22$/.test(input) ){
                input = input.replace(/\\/g, ''); // remove starting \
                // before double
                // quotes
            }

            // no need to escape space - looks cleaner to the users
            // and it is not essential to escape space
            input = input.replace(/\\?%20/g, ' ');

            // default to search by double quotes in SOLR
            if (input != '') {
                if (input.indexOf("*") != -1 && input.indexOf(" ") == -1) {
                    input = input;
                }
                else {
                    input = '"' + input + '"';  // single quotes do NOT work in SOLR query
                }
            }

            if (input == ''){
                //document.location.href = baseUrl + '/search/' + facet + '?kw=*'; // default
                document.location.href = baseUrl + '/search' + '?kw=*'; //  default
            }
            else if (input.match(/HP\\\%3A\d+/i)){
                // work out the mapped mp_id and fire off the query
                _convertHp2MpAndSearch(input, "mp");
            }
            else if ( input.match(/MP%3A\d+ - (.+)/i) ){
                // hover over hp mp mapping but not selecting
                // eg. Cholesteatoma %C2%BB MP%3A0002102 - abnormal ear morpholog
                var matched = input.match(/MP%3A\d+ - (.+)/i);
                var mpTerm = '"' + matched[1] + '"';
                var fqStr = "top_level_mp_term:*";

                //document.location.href = baseUrl + '/search/mp?kw=' + mpTerm + '&fq=' + fqStr;
                document.location.href = baseUrl + '/search?kw=' + mpTerm + '&fq=' + fqStr;
            }
            else {
                // need to figure out the default datatype tab
                $.ajax({
                    url: baseUrl + '/fetchDefaultCore?q=' + input,
                    type: 'get',
                    success: function (defaultCore) {
                        // default to search by quotes
                        document.location.href = baseUrl + '/search/' + defaultCore + '?kw=' + input;
                    }
                });
            }

            e.preventDefault();
            return false;
        }
    });

    // generic search input autocomplete javascript
    var solrBq = "&bq=marker_symbol:*^100 hp_term:*^95 hp_term_synonym:*^95 mp_term:*^90 mp_term_synonym:*^80 mp_narrow_synonym:*^75 disease_term:*^70 anatomy_term:*^60 anatomy_term_synonym:*^50";
    $(function() {
        $( "input#q" ).autocomplete({
            source: function( request, response ) {
                var qfStr = request.term.indexOf("*") != -1 ? "auto_suggest" : "string auto_suggest";
                var facetStr = "&facet=on&facet.field=docType&facet.mincount=1&facet.limit=-1";
                var sortStr = "&sort=score desc";
                $.ajax({
                    url: solrUrl + "/autosuggest/select?rows=5&fq=!docType:gwas&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq + facetStr + sortStr,
                    dataType: "jsonp",
                    'jsonp': 'json.wrf',
                    data: {
                        q: '"'+request.term+'"'
                    },
                    success: function( data ) {
                        var docs = data.response.docs;
                        var suggests = [];
                        var seenTerm = {};

                        for ( var i=0; i<docs.length; i++ ){
                            var facet = null;
                            for ( var key in docs[i] ){
                                // console.log('key: '+key);
                                if ( facet == 'hp' && (key == 'hpmp_id' || key == 'hpmp_term') ){
                                    continue;
                                }

                                if ( key == 'docType' ){
                                    facet = docs[i][key].toString();
                                }
                                else {
                                    var term = docs[i][key].toString();
                                    var termHl = term;

                                    // highlight multiple matches
                                    // (partial matches) while users
                                    // typing in search keyword(s)
                                    // let jquery autocomplet UI handles
                                    // the wildcard
                                    // var termStr =
                                    // $('input#q').val().trim('
                                    // ').split('
                                    // ').join('|').replace(/\*|"|'/g,
                                    // '');
                                    var termStr = $('input#q').val().trim(' ').split(' ').join('|').replace(/\*|"|'/g, '').replace(/\(/g,'\\(').replace(/\)/g,'\\)');

                                    var re = new RegExp("(" + termStr + ")", "gi") ;
                                    var termHl = termHl.replace(re,"<b class='sugTerm'>$1</b>");

                                    // add only once with the top score
                                    var lowerCaseTerm = term.toLowerCase();
                                    if ( seenTerm[lowerCaseTerm] == undefined){
                                        seenTerm[lowerCaseTerm]++;
                                        suggests.push("<span class='" + facet + " sugList'>" + termHl + "</span>");
                                    }
                                }
                            }
                        }
                        var dataTypeVal = [];
                        for( var corename in facet2Label ) {
                            dataTypeVal.push(_getDropdownList(corename, facet2Label, request.term));
                        }
                        if ( suggests.length > 0 ) {
                            dataTypeVal.push("<hr>");
                            for (var i = 0; i < suggests.length; i++) {
                                dataTypeVal.push(suggests[i]);
                            }
                        }
                        response( dataTypeVal );
                    }
                });
            },
            focus: function (event, ui) {
                var thisInput = $(ui.item.label).text().replace(/<\/?span>|^\w* : /g,'');
                //this.value = '"' + thisInput.trim() + '"';  // double quote value when mouseover or KB UP.DOWN a dropdown list

                // assign value to input box
                this.value = thisInput.trim();
                event.preventDefault(); // Prevent the default focus behavior.
            },
            minLength: 3,
            select: function( event, ui ) {
                // select by mouse / KB
                // console.log(this.value + ' vs ' + ui.item.label);
                // var oriText = $(ui.item.label).text();

                var facet = $(ui.item.label).attr('class').replace(' sugList', '') == 'hp' ? 'mp' : $(ui.item.label).attr('class').replace(' sugList', '');

                var q;
                var qVal = this.value;
                var qRe = new RegExp(" in (Genes|Phenotypes|Diseases|Anatomy|Images|Products)$");
                q = qVal.replace(qRe, "");

                q = encodeURIComponent(q).replace("%3A", "\\%3A");

                // default to send query to Solr in quotes !!!
                if (q.indexOf("*") != -1 && q.indexOf(" ") == -1) {
                    q = q;
                }
                else {
                    q = '"' + q + '"'; // single quotes do NOT work in SOLR query
                }
                var href = baseUrl + '/search/' + facet  + '?' + 'kw=' + q;
                if (q.match(/(MGI:|MP:|MA:|EMAP:|EMAPA:|HP:|OMIM:|ORPHANET:|DECIPHER:)\d+/i)) {
                    href += "&fq=" + facet2Fq[facet];
                }
                document.location.href = href;
                // prevents escaped html tag displayed in input box
                event.preventDefault(); return false;
            },
            open: function(event, ui) {
                // fix jQuery UIs autocomplete width
                $(this).autocomplete("widget").css({
                    "width": ($(this).width() + "px")
                });

                $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
            },
            close: function() {
                $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
            }
        }).data("ui-autocomplete")._renderItem = function( ul, item) { // prevents
            // HTML
            // tags
            // being
            // escaped
            return $( "<li></li>" )
                .data( "item.autocomplete", item )
                .append( $( "<a></a>" ).html( item.label ) )
                .appendTo( ul );
        };
    });
    function sortJson(o) {
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

    function _getDropdownList(corename, facet2Label, input) {
        var catLabel = "<span class='category'>" + facet2Label[corename] + "</span>";
        return "<span class='" + corename + " sugList'>" + input + " in " + catLabel + "</span>"; // so that we know it is category search
    }
    function _convertHp2MpAndSearch(input, facet){
        input = input.toUpperCase();
        $.ajax({
            url: solrUrl + "/autosuggest/select?wt=json&fl=hpmp_id&rows=1&q=hp_id:\""+input+"\"",
            dataType: "jsonp",
            jsonp: 'json.wrf',
            type: 'post',
            async: false,
            success: function( json ) {
                var mpid = json.response.docs[0].hpmp_id;
                document.location.href = baseUrl + '/search/' + facet + '?kw=' + mpid + '&fq=top_level_mp_term:*';
            }
        });
    }

    function _convertInputForSearch(input){
        $.ajax({
            url: solrUrl + "/autosuggest/select?wt=json&rows=1&qf=auto_suggest&defType=edismax&q=\""+input+"\"",
            dataType: "jsonp",
            jsonp: 'json.wrf',
            type: 'post',
            async: false,
            success: function( json ) {
                var doc = json.response.docs[0];
                var facet, q;

                for( var field in doc ) {
                    if ( field != 'docType' ){
                        q = doc[field];
                    }
                    else {
                        facet = doc[field];
                    }
                }

                document.location.href = baseUrl + '/search/' + facet + '?kw=' + q;
            }
        });
    }
});
</script>