<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<%-- <p class="resultCount">Total number of results: ${rows}</p> --%>

<!--div class="row justify-content-end mt-3 mr-2">
    <div class="btn-group btn-group-toggle" data-toggle="buttons">
        <label class="btn btn-outline-primary btn-sm active">
            <input type="radio" name="optionsPh" id="phChartToggle" autocomplete="off" value="phChart"> Chart
        </label>
        <label class="btn btn-outline-primary btn-sm">
            <input type="radio" name="optionsPh" id="phChartTableToggle" value="phTable" autocomplete="off" checked>
            Table
        </label>
    </div>
</div-->

<div id="phChart">
    <c:if test="${chart != null}">
        <!--p>Hints: Use the dropdown filters to filter both the chart and the table. Hover over points in chart to see the parameter name and information. Click on a point to view the chart for that data point. Click and drag on the chart to zoom to that area.
        </p-->
        <!-- chart here -->
        <p class="mt-3">This section allows you to view data points that have been statistically analyzed to establish significance phenotypes.   You can also view a table of  All results (520)</p>
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <div>
                <ul>
                    <li>Mouseover the data points for more information</li>
                    <li>Click and drag to zoom the chart</li>
                    <li>Click on the legends to disable/enable data</li>
                </ul>
            </div>
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <button id="checkAll" class="btn btn-sm btn-outline-success"><i class="fa fa-check" aria-hidden="true"></i> Select all</button>
        <button id="uncheckAll" class="btn btn-sm btn-outline-danger"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</button>
        <div id="chartDiv"></div>
        <div class="clear both"></div>

        <script type="text/javascript" async>${chart}</script>
    </c:if>
</div>
<!-- Associations table -->


<c:set var="count" value="0" scope="page"/>

<script>
    var resTemp = document.getElementsByClassName("resultCount");
    if (resTemp.length > 1) {
        resTemp[0].remove();
    }
</script>

