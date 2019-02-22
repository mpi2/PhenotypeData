<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<script>
    function filterAllData() {
        /*if($('#' + rdId).is(':checked')) {
            console.log('hi');
        } else {
            $('#alldata').html("     <div class=\"pre-content\">\n" +
                "                        <div class=\"row no-gutters\">\n" +
                "                            <div class=\"col-12 my-5\">\n" +
                "                                <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                    </div>");
            $.ajax({
                url : '/data/experimentsFrag?geneAccession=' + geneAccession + '&' + mpTermId,
                type: 'GET',
                success: function(data){
                    $('#alldata').html(data);
                    $('#alldata-tab').trigger('click');
                    $('#phenotypesTab').scrollTop();
                }
            });
        }*/

        var val = [];
        $('#phIconGrid').find('input:checked').each(function() {
            val.push($(this).data('value'));
        });
        $('#alldata').html("     <div class=\"pre-content\">\n" +
            "                        <div class=\"row no-gutters\">\n" +
            "                            <div class=\"col-12 my-5\">\n" +
            "                                <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
            "                            </div>\n" +
            "                        </div>\n" +
            "                    </div>");
        $.ajax({
            url : '/data/experimentsFrag?geneAccession=' + '${gene.mgiAccessionId}' + '&' + val.join('&'),
            type: 'GET',
            success: function(data){
                $('#alldata').html(data);
                $('#alldata-tab').trigger('click');
                $('#phenotypesTab').scrollTop();
            }
        });
    }
</script>

<div class="container" style="min-width: 300px">
    <div class="row no-gutters justify-content-center">View data by physiological system</div>
    <div class="row no-gutters">

        <div class="container text-center text-muted"  id="phIconGrid">
            <c:forEach var="i" begin="0" end="3">
                <div class="row no-gutters btn-group-toggle" data-toggle="buttons">
                    <c:forEach var="j" begin="0" end="4">
                        <c:choose>
                            <c:when test="${not empty significantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}">
                                <label class="col-sm btn btn-outline-primary btn-icon significant m-1" href="#phenotypesTab" >
                                    <input type="checkbox"  autocomplete="off" data-value="${significantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}" onchange="filterAllData()">
                                    <i class="${phenotypeGroupIcons[i*5 + j]}" title="${gene.markerSymbol} ${phenotypeGroups[i*5 + j]} measurements" data-toggle="tooltip" data-placement="top"></i>
                                </label>
                            </c:when>
                            <c:when test="${not empty notsignificantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}">
                                <label class="col-sm btn btn-outline-info btn-icon m-1" href="#phenotypesTab">
                                    <input type="checkbox" autocomplete="off" data-value="${notsignificantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}" onchange="filterAllData()">
                                    <i class="${phenotypeGroupIcons[i*5 + j]}" title="${gene.markerSymbol} ${phenotypeGroups[i*5 + j]} measurements" data-toggle="tooltip" data-placement="top"></i>
                                </label>
                            </c:when>
                            <c:otherwise>
                                <a class="col-sm btn btn-outline-light btn-icon non-tested disabled m-1">
                                    <i class="${phenotypeGroupIcons[i*5 + j]}" title="${gene.markerSymbol} ${phenotypeGroups[i*5 + j]} measurements" data-toggle="tooltip" data-placement="top"></i>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>
    </div>
    <div class="row no-gutters font-weight-light text-center" style="font-size: 0.8em;">
        <div class="col text-primary">
            Significant
        </div>
        <div class="col text-info">
            Not Significant
        </div>
        <div class="col text-muted">
            Not tested
        </div>
    </div>
</div>

