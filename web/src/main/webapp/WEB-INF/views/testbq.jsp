<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">Publications with IMPC alleles</jsp:attribute>
    <jsp:attribute name="header">

        <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
        <link href="${baseUrl}/css/default.css" rel="stylesheet" />

        <style type="text/css">


        </style>

        <script type='text/javascript'>

            $(document).ready(function () {
                'use strict';

                // test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';

                var baseUrl = "${baseUrl}";
                var solrUrl = "${internalSolrUrl};"





            });

        </script>

        <%--<script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>--%>
        <%--<script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>--%>
        <%--<script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>--%>

    </jsp:attribute>

    <jsp:attribute name="addToFooter">
        <div class="region region-pinned">

        </div>

    </jsp:attribute>
    <jsp:body>
        <a href="http://genome.ucsc.edu/cgi-bin/hgTracks?org=Mouse&amp;db=mm9&amp;position=chrX&amp;hgt.customText=ftp://ftp.ebi.ac.uk/pub/databases/impc/trackhubs/track1" target="blank">view track 1</a><br>

        <a href="http://genome.ucsc.edu/cgi-bin/hgTracks?org=Mouse&amp;db=mm9&amp;position=chrX&amp;hgt.customText=ftp://ftp.ebi.ac.uk/pub/databases/impc/trackhubs/track2" target="blank">view track 2</a><br>
        <a href="http://genome.ucsc.edu/cgi-bin/hgTracks?org=Mouse&amp;db=mm9&amp;position=chrX&amp;hgt.customText=ftp://ftp.ebi.ac.uk/pub/databases/impc/trackhubs/track3" target="blank">view track 3</a><br>
        <a href="http://genome.ucsc.edu/cgi-bin/hgTracks?org=Mouse&amp;db=mm9&amp;position=chrX&amp;hgt.customText=ftp://ftp.ebi.ac.uk/pub/databases/impc/trackhubs/chr7" target="blank">view track chr7</a><br>

        <a href="http://genome.ucsc.edu/cgi-bin/hgTracks?org=Mouse&amp;db=mm9&amp;position=chrX&amp;hgt.customText=ftp://ftp.ebi.ac.uk/pub/databases/impc/trackhubs/chr19" target="blank">view chr19</a><br>

        <a href="http://genome.ucsc.edu/cgi-bin/hgTracks?org=Mouse&amp;db=mm9&amp;position=chrX&amp;hgt.customText=ftp://ftp.ebi.ac.uk/pub/databases/impc/trackhubs/IMPC-allele-chr7.bb" target="blank">view bigBed Chr7</a><br>
        <a href="http://genome.ucsc.edu/cgi-bin/hgTracks?org=Mouse&amp;db=mm9&amp;position=chrX&amp;hgt.customText=ftp://ftp.ebi.ac.uk/pub/databases/impc/trackhubs/impc-track-ucsc" target="blank">view IMPC track on UCSC</a><br>

        <a href="http://www.ensembl.org/Mus_musculus/Location/View?g=ENSMUSG00000030822;contigviewbottom=url:ftp://ftp.ebi.ac.uk/pub/databases/impc/trackhubs/impc-track-ensembl" target="blank">view IMPC track on Ensembl</a>


        <%--<form action="${baseUrl}/batchQuery" method="">--%>
            <%--<input type="hidden" name="idlist" value="MGI:106209" />--%>
            <%--<input type="hidden" name="fllist" value="marker_symbol,mgi_accession_id" />--%>
            <%--<input type="hidden" name="corename" value="gene" />--%>
            <%--<button>submit</button>--%>
        <%--</form>--%>
    </jsp:body>

</t:genericpage>

