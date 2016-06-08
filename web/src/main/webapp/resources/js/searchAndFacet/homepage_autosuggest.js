<link href="/data/css/vendor/jquery.ui/jquery.ui.core.css" rel="stylesheet" />
    <link href="/data/css/vendor/jquery.ui/jquery.ui.theme.css" rel="stylesheet" /><!--<link href="/data/css/searchPage.css" rel="stylesheet" /> -->
    <form action="/data/search" method="GET">
    <p><input id="q" name="q" placeholder="Search" type="text" /></p>
    <style type="text/css">span.sugList {
    font-size: 12px !important;
}
</style>
<p>Examples: <a href="/data/search/gene?kw=Ap4e1" style="color: white">Ap4e1</a>, <a href="/data/search/mp?kw='abnormal heart rate'&amp;fq=top_level_mp_term:*" style="color: white">Abnormal Heart Rate</a>, <a href="/data/search/disease?kw=&quot;Bernard-Soulier Syndrome&quot;" style="color: white">Bernard-Soulier Syndrome</a></p>
</form>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script><script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script><script type='text/javascript'>

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
        'pipeline' : 'pipeline_stable_id:*',
        'images' : '*:*'
    }

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

            if (input == ''){
                document.location.href = baseUrl + '/search/' + facet + '?kw=*'; // default
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

                document.location.href = baseUrl + '/search/mp?kw=' + mpTerm + '&fq=' + fqStr;
            }
            else {
                document.location.href = baseUrl + '/search/' + facet + '?kw=' + input;
            }

            e.preventDefault();
            return false;
        }
    });

    var solrBq = "&bq=marker_symbol:*^100 hp_term:*^95 hp_term_synonym:*^95 mp_term:*^90 mp_term_synonym:*^80 disease_term:*^70 anatomy_term:*^60 anatomy_term_synonym:*^50" ;

    $(function() {
        $( "input#q" ).autocomplete({
            source: function( request, response ) {
                var qfStr = request.term.indexOf("*") != -1 ? "auto_suggest" : "string auto_suggest";
                $.ajax({
                    url: solrUrl + "/autosuggest/select?fq=!docType:gwas&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq,
                    dataType: "jsonp",
                    'jsonp': 'json.wrf',
                    data: {
                        q: '"'+request.term+'"'
                    },
                    success: function( data ) {

                        matchedFacet = false; // reset
                        var docs = data.response.docs;
                        // console.log(docs);

                        var aKVtmp = {};
                        for ( var i=0; i<docs.length; i++ ){
                            var facet;
                            for ( var key in docs[i] ){
                                // console.log('key: '+key);
                                if ( facet == 'hp' && (key == 'hpmp_id' || key == 'hpmp_term') ){
                                    continue;
                                }

                                if ( key == 'docType' ){
                                    facet = docs[i][key].toString();
                                    if ( ! aKVtmp.hasOwnProperty(facet) ) {
                                        aKVtmp[facet] = [];
                                    }
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

                                    if ( facet == 'hp' ){
                                        termHl += " &raquo; <span class='hp2mp'>" + docs[i]['hpmp_id'].toString() + ' - ' + docs[i]['hpmp_term'].toString() + "</span>";
                                    }

                                    aKVtmp[facet].push("<span class='" + facet + " sugList'>" + "<span class='dtype'>"+ facet + ' : </span>' + termHl + "</span>");

                                    if (i == 0){
                                        // take the first found in
                                        // autosuggest and open that
                                        // facet
                                        matchedFacet = facet;
                                    }

                                }
                            }
                        }
                        var dataTypeVal = [];
                        var aKVtmpSorted = sortJson(aKVtmp);
                        for ( var k in aKVtmpSorted ){
                            for ( var v in aKVtmpSorted[k] ) {
                                dataTypeVal.push(aKVtmpSorted[k][v]);
                            }
                        }

                        response( dataTypeVal );
                    }
                });
            },
            focus: function (event, ui) {
                var thisInput = $(ui.item.label).text().replace(/<\/?span>|^\w* : /g,'');
                this.value = '"' + thisInput.trim() + '"';  // double quote value when mouseover or KB UP.DOWN a dropdown list
                event.preventDefault(); // Prevent the default focus behavior.
            },
            minLength: 3,
            select: function( event, ui ) {
                // select by mouse / KB
                // console.log(this.value + ' vs ' + ui.item.label);
                // var oriText = $(ui.item.label).text();

                var facet = $(ui.item.label).attr('class').replace(' sugList', '') == 'hp' ? 'mp' : $(ui.item.label).attr('class').replace(' sugList', '');

                var q;
                var matched = decodeURIComponent(this.value).match(/.+(MP:\d+) - .+/);

                if ( matched ){
                    q = matched[1];
                }
                else {
                    q = this.value;
                }
                q = encodeURIComponent(q).replace("%3A", "\\%3A");

                // we are choosing value from drop-down list so need to double quote the value for SOLR query
                //document.location.href = baseUrl + '/search/' + facet  + '?' + "kw=\"" + q + "\"&fq=" + fqStr;

                var href = baseUrl + '/search/' + facet  + '?' + "kw=" + q;
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